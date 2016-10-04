package me.soubhik;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.CanonicalPathHandler;
import io.undertow.server.handlers.GracefulShutdownHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.RequestLimitingHandler;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.util.Headers;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by soubhik on 7/30/16.
 */
public class UndertowApp {
    private static final long DEFAULT_SHUTDOWN_TIMEOUT = 500; //in millisecond
    private static final int DEFAULT_REQUEST_RATE_LIMIT = 5; //5 requests per second
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 3110;

    private final Undertow server;
    private final GracefulShutdownHandler shutdownHandler;

    public UndertowApp(Map<String, HttpHandler> pathToHandler) {
        this(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_REQUEST_RATE_LIMIT, pathToHandler);
    }

    public UndertowApp(String host, int port, int requestRateLimit, Map<String, HttpHandler> pathToHandler) {
        PathHandler pathHandler = new PathHandler(ResponseCodeHandler.HANDLE_403);
        for (Map.Entry<String, HttpHandler> entry: pathToHandler.entrySet()) {
            String path = entry.getKey();
            HttpHandler handler = entry.getValue();
            pathHandler.addExactPath(path, handler);
        }

        LowerCasePathHandler lowerCasePathHandler = new LowerCasePathHandler(pathHandler);
        CanonicalPathHandler canonicalPathHandler = new CanonicalPathHandler(lowerCasePathHandler);
        LowerCaseQueryParamHandler lowerCasing = new LowerCaseQueryParamHandler(canonicalPathHandler);
        RequestLimitingHandler rateLimiter = new RequestLimitingHandler(requestRateLimit, lowerCasing);
        this.shutdownHandler = new GracefulShutdownHandler(rateLimiter);

        HttpHandler root = shutdownHandler;
        this.server = Undertow.builder().setServerOption(UndertowOptions.ENABLE_HTTP2, true).
                                        addHttpListener(port, host).
                                        setHandler(root).
                                        build();
    }

    public void start() {
        this.server.start();
    }

    public void stop(long timeoutInMillis) {
        if (timeoutInMillis <= 0) {
            timeoutInMillis = DEFAULT_SHUTDOWN_TIMEOUT;
        }

        shutdownHandler.shutdown();
        try {
            shutdownHandler.awaitShutdown(timeoutInMillis);
        } catch (InterruptedException e) {
            //do nothing
        }
        this.server.stop();
    }

    public static interface Helpful {
        public JSONObject help();
    }

    public static class LowerCasePathHandler implements HttpHandler {
        private final HttpHandler next;

        public LowerCasePathHandler() {
            this(null);
        }

        public LowerCasePathHandler(HttpHandler next) {
            this.next = next;
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) throws Exception {
            exchange.setRelativePath(exchange.getRelativePath().toLowerCase());

            if (next != null) {
                next.handleRequest(exchange);
            }
        }
    }

    public static class LowerCaseQueryParamHandler implements HttpHandler {
        private final HttpHandler next;

        public LowerCaseQueryParamHandler() {
            this(null);
        }

        public LowerCaseQueryParamHandler(HttpHandler next) {
            this.next = next;
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) throws Exception {
            Map<String, Deque<String>> queryParamaters = exchange.getQueryParameters();
            Map<String, Deque<String>> lowerCasedParameters = new HashMap<String, Deque<String>>();

            Iterator<Map.Entry<String, Deque<String>>> entryIterator = queryParamaters.entrySet().iterator();
            while (entryIterator.hasNext()) {
                Map.Entry<String, Deque<String>> entry = entryIterator.next();
                String key = entry.getKey();
                String lowerCasedKey = key.toLowerCase();
                if (queryParamaters.containsKey(lowerCasedKey)) {
                    continue;
                }
                Deque<String> value = entry.getValue();
                lowerCasedParameters.put(lowerCasedKey, value);
                entryIterator.remove();
            }

            for (Map.Entry<String, Deque<String>> entry: lowerCasedParameters.entrySet()) {
                String key = entry.getKey();
                Deque<String> value = entry.getValue();
                queryParamaters.put(key, value);
            }

            if (next != null) {
                next.handleRequest(exchange);
            }
        }
    }

    public static class HelpHandler implements HttpHandler, Helpful {
        private final JSONObject helpObject;
        Map<String, HttpHandler> pathToHandler;

        public HelpHandler(Map<String, HttpHandler> pathToHandler) {
            this.helpObject = new JSONObject();
            helpObject.put("description", "a handler that provides helps about other handlers/apps");
            JSONObject options = new JSONObject();
            options.put("handler", "name of the handler on which help is needed");
            helpObject.put("parameters", options);
            ArrayList<String> supportedHandlers = new ArrayList<String>(pathToHandler.keySet());
            supportedHandlers.add("help");
            helpObject.put("supported handlers", supportedHandlers);

            this.pathToHandler = pathToHandler;
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");

            JSONObject response = new JSONObject();
            Map<String, Deque<String>> queryParameters = exchange.getQueryParameters();
            if (!queryParameters.containsKey("handler")) {
                response.put("help", help());
                exchange.getResponseSender().send(response.toJSONString());
                return;
            }

            Deque<String> handlers = queryParameters.get("handler");
            for (String handlerName: handlers) {
                if (handlerName.equals("help")) {
                    response.put(handlerName, help());
                    continue;
                }
                if (!pathToHandler.containsKey(handlerName)) {
                    response.put(handlerName, "unsupported handler. try help.");
                    response.put("help", help());
                    continue;
                }
                HttpHandler handler = pathToHandler.get(handlerName);
                if (handler instanceof Helpful) {
                    JSONObject help = ((Helpful) handler).help();
                    response.put(handlerName, help);
                } else {
                    response.put(handlerName, "no help available");
                }
            }

            if (response.isEmpty()) {
                response.put("help", help());
            }

            exchange.getResponseSender().send(response.toJSONString());
        }

        @Override
        public JSONObject help() {
            return helpObject;
        }
    }

    public static class EditDistanceHandler implements HttpHandler, Helpful {
        private final EditDistance.UniformCostFunction costFunction;
        private final JSONObject helpObject;

        public EditDistanceHandler() {
            JSONObject options = new JSONObject();
            options.put("string1", "1st string");
            options.put("string2", "2nd string");

            helpObject = new JSONObject();
            helpObject.put("parameters", options);
            helpObject.put("description", "returns edit distance and minimum edits between the given strings");

            this.costFunction = new EditDistance.UniformCostFunction();
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");

            Map<String, Deque<String>> queryParameters = exchange.getQueryParameters();
            String string1 = getSingleValuedParam(queryParameters, "string1");
            String string2 = getSingleValuedParam(queryParameters, "string2");

            if ((string1 == null) || (string2 == null)) {
                exchange.setStatusCode(400);
                exchange.getResponseSender().send(help().toJSONString());
                return;
            }

            ImmutablePair<Integer, String> pair = EditDistance.editDistance(string1, string2, costFunction);
            JSONObject response = new JSONObject();
            response.put("edit-distance", pair.left);
            response.put("edits", pair.right);
            response.put("string1", string1);
            response.put("string2", string2);
            exchange.getResponseSender().send(response.toJSONString());
        }

        @Override
        public JSONObject help() {
            return helpObject;
        }
    }

    public static String getSingleValuedParam(Map<String, Deque<String>> parameters, String key) {
        String value = null;

        if (parameters.containsKey(key)) {
            Deque<String> queue = parameters.get(key);
            if (queue.size() == 1) {
                value = queue.getFirst();
            }
        }

        return value;
    }

    public static void main(String[] args) {
        Map<String, HttpHandler> pathToHandler = new HashMap<String, HttpHandler>();
        EditDistanceHandler editDistanceHandler = new EditDistanceHandler();
        pathToHandler.put("edit-distance", editDistanceHandler);
        HelpHandler helpHandler = new HelpHandler(pathToHandler);
        pathToHandler.put("/help", helpHandler);
        UndertowApp myApp = new UndertowApp(pathToHandler);
        myApp.start();
        try {
            while (true) {
                Thread.sleep(3600000);
            }
        } catch (InterruptedException e) {
            myApp.stop(500);
        }
    }
}
