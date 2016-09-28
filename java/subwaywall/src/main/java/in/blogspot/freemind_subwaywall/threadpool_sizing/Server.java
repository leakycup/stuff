import java.lang.Thread;
import java.lang.ThreadGroup;
import java.lang.InterruptedException;
import java.lang.Runtime;

import java.lang.reflect.Array;

import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Date;
import java.util.concurrent.Executors;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import java.util.concurrent.atomic.AtomicInteger;

import java.io.IOException;

import java.net.InetSocketAddress;
import java.net.URI;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Headers;

public class Server {
    private static class WorkItem {
        public int input;
        public long sleepiness;
        public long result;
        public long intime;
        public long outtime;
        public long finishtime;
        HttpExchange exchange;
        Headers responseHeaders;
        public WorkItem(int n, long s) {
            input = n;
            sleepiness = s;
            result = 0;
        }
    }
    private static BlockingQueue<WorkItem> workQueue = 
                                            new LinkedBlockingQueue<WorkItem>();
    private static BlockingQueue<WorkItem> outQueue = 
                                            new LinkedBlockingQueue<WorkItem>();

    private static class ThreadPool {
        private ThreadGroup group = new ThreadGroup("ThreadPoolSizingTest");
        private AtomicInteger busyCounter = new AtomicInteger(0);

        private class ThreadPoolItem extends Thread {
            private volatile boolean deleted;
            private long sleepTime;
            private long result;

            public ThreadPoolItem(long s) {
                super(group, "worker");
                deleted = false;
                sleepTime = s;
                result = 0;
            }

            public void run() {
                while(!deleted) {
                    WorkItem item;
                    try {
                        item = workQueue.take();
                    } catch (InterruptedException e) {
                        continue; //continue to check if I'm deleted
                    }
                    //item.outtime = new Date().getTime();
                    item.outtime = System.nanoTime();
                    busyCounter.incrementAndGet();
                    int number = item.input;
                    long sleepDenominator = (item.sleepiness == 0) ?
                                                number : number / item.sleepiness;
                    long sum = number;
                    while (number > 0) {
                        number--;
                        sum += number;
                        if ((sleepDenominator != 0) &&
                            (number % sleepDenominator) == 0) {
                            try {
                                sleep(sleepTime);
                            } catch (InterruptedException e) {
                                //ignore the interupt and finish the work
                            }
                        }
                    }
                    item.result = sum;
                    //item.finishtime = new Date().getTime();
                    item.finishtime = System.nanoTime();
                    outQueue.offer(item);
                    busyCounter.decrementAndGet();
                }
            }

            public void markDeleted() {
                deleted = true;
            }
        }
        private LinkedList<ThreadPoolItem> threads =
                                            new LinkedList<ThreadPoolItem>();

        private long sleepTime;
        private static long defaultSleepTime = 100;
        private int maxCapacity;

        public void grow(int number, int currentCapacity) {
            int threadsInPool = threads.size();
            int aboutToDie = currentCapacity - threadsInPool;
            number += aboutToDie;
            //System.err.println("ThreadPool: grow(): current capacity: " +
            //                   currentCapacity +
            //                   " number: " + number +
            //                   " max capacity: " + maxCapacity);
            if ((number + threadsInPool) > maxCapacity) {
                number = maxCapacity - threadsInPool;
            }
            long now = new Date().getTime();
            for (int idx = 0; idx < number; idx++) {
                ThreadPoolItem item = new ThreadPoolItem(sleepTime);
                threads.add(item);
                item.start();
            }
            long delta = (new Date().getTime()) - now;
            System.out.println("ThreadPool: added " + number + " threads in " +
                               delta + "ms");
            //System.err.println("ThreadPool: theads size: " + threads.size());
        }

        public void shrink(int number, int currentCapacity) {
            int aboutToDie = currentCapacity - threads.size();
            number -= aboutToDie;
            long now = new Date().getTime();
            for (int idx = 0; idx < number; idx++) {
                ThreadPoolItem item;
                try {
                    item = threads.remove();
                } catch (java.lang.IndexOutOfBoundsException e) {
                    System.err.println("ThreadPool: number: " + number +
                                       " index: " + idx +
                                       " size: " + threads.size());
                    throw(e);
                } catch (java.util.NoSuchElementException e) {
                    System.err.println("ThreadPool: number: " + number +
                                       " index: " + idx +
                                       " about to die: " + aboutToDie +
                                       " size: " + threads.size());
                    throw(e);
                }
                item.markDeleted();
                item.interrupt();
            }
            long delta = (new Date().getTime()) - now;
            System.out.println("ThreadPool: deleted " + number + " threads in " +
                               delta + "ms");
        }

        public int getCapacity() {
            return (group.activeCount());
        }

        public int getBusy() {
            return (busyCounter.get());
        }

        public ThreadPool(long s, int number, int max) {
            sleepTime = s;
            maxCapacity = max;
            grow(number, 0);
        }

        public ThreadPool(int number, int max) {
            this(defaultSleepTime, number, max);
        }
    }

    private static AtomicInteger requestCounter = new AtomicInteger(0);
    private static AtomicInteger responseCounter = new AtomicInteger(0);
    private static int HTTP_OK = 200;
    private static int HTTP_BAD_REQUEST = 400;
    private static int HTTP_BAD_SERVER = 500;
    private static String HTTP_GET = "GET";
    private static String INPUT = "input";
    private static String SLEEP = "sleep";
    private static String resultHeader = "X-Result";
    private static String idleTimeHeader = "X-idle-time";
    private static String workTimeHeader = "X-work-time";
    private static String busyHeader = "X-busy";
    private static String capacityHeader = "X-capacity";

    private static HttpServer createHttpServer(String host) {
        InetSocketAddress address = new InetSocketAddress(host, 0);
        HttpServer server = null;
        try {
            server = HttpServer.create(address, 0);
        } catch (IOException e) {
            e.printStackTrace();
            Runtime.getRuntime().exit(1);
        }

        return (server);
    }

    static private int globalBusy;
    static private int globalCapacity;
    static private int loadBalanceFactor;

    private static class ManagementThread implements HttpHandler {
        private static String contextPath = "/threadpooltest/management/";
        private static String GETLOAD = "getload";
        private static String BUSY = "busy";
        private static String CAPACITY = "capacity";
        private static String LOAD_BALANCE_FACTOR = "factor";

        ThreadPool pool;

        public ManagementThread(HttpServer server, ThreadPool pool) {
            this.pool = pool;
            server.createContext(contextPath, this);
            globalBusy = this.pool.getBusy();
            globalCapacity = this.pool.getCapacity();
            loadBalanceFactor = 100;
        }

        public void handle(HttpExchange exchange) throws IOException {
            Headers requestHeaders = exchange.getRequestHeaders();
            Headers responseHeaders = exchange.getResponseHeaders();
            if (!HTTP_GET.equals(exchange.getRequestMethod())) {
                String [] result = {"Bad Request Method (only GET is supported)"};
                responseHeaders.put(resultHeader, Arrays.asList(result));
                exchange.sendResponseHeaders(HTTP_BAD_REQUEST, -1);
                exchange.close();
                return;
            }
            URI uri = exchange.getRequestURI();
            //System.err.println("Management thread: request URI: " + uri +
                               //" query: " + uri.getQuery());

            String [] queries = uri.getQuery().split(",");
            for (String query: queries) {
                //System.err.println("Management thread: query: " + query);
                try {
                    if (query.startsWith(GETLOAD)) {
                        String [] busy = {Integer.toString(pool.getBusy())};
                        String [] capacity = {Integer.toString(pool.getCapacity())};
                        responseHeaders.put(busyHeader, Arrays.asList(busy));
                        responseHeaders.put(capacityHeader,
                                            Arrays.asList(capacity));
                    } else if (query.startsWith(BUSY)) {
                        globalBusy = Integer.parseInt(query.split("=")[1]);
                        System.out.println("Management thread:" +
                                           " global busy: " + globalBusy);
                    } else if (query.startsWith(CAPACITY)) {
                        globalCapacity = Integer.parseInt(query.split("=")[1]);
                        System.out.println("Management thread:" +
                                           " global capacity: " + globalCapacity);
                    } else if (query.startsWith(LOAD_BALANCE_FACTOR)) {
                        loadBalanceFactor = Integer.parseInt(query.split("=")[1]);
                        System.out.println("Management thread:" +
                                           " load balance factor: " +
                                           loadBalanceFactor);
                    }
                } catch (NumberFormatException e) {
                    String [] result = {"Bad Management Request: " + e.toString()};
                    responseHeaders.put(resultHeader, Arrays.asList(result));
                    exchange.sendResponseHeaders(HTTP_BAD_REQUEST, -1);
                    exchange.close();
                    return;
                }
            }
            exchange.sendResponseHeaders(HTTP_OK, -1);
            exchange.close();
        }
    }

    private static class RequestThread implements HttpHandler {
        private static String contextPath = "/threadpooltest/";

        public RequestThread(HttpServer server) {
            server.createContext(contextPath, this);
        }

        public void handle(HttpExchange exchange) throws IOException {
            //System.err.println("Request: handle method called");
            Headers requestHeaders = exchange.getRequestHeaders();
            /*
            for (Map.Entry<String, List<String>> entry: requestHeaders.entrySet()) {
                String key = entry.getKey();
                List<String> values = entry.getValue();
                System.err.println("Request: (HTTP Header) " + key + ":" + values);
            }
            System.err.println("Request: (Request method) " +
                               exchange.getRequestMethod());
            */
            Headers responseHeaders = exchange.getResponseHeaders();
            if (!HTTP_GET.equals(exchange.getRequestMethod())) {
                String [] result = {"Bad Request Method (only GET is supported)"};
                responseHeaders.put(resultHeader, Arrays.asList(result));
                exchange.sendResponseHeaders(HTTP_BAD_REQUEST, -1);
                exchange.close();
                return;
            }
            URI uri = exchange.getRequestURI();
            //System.err.println("Request: (URI query) " + uri.getQuery());
            int input = -1;
            int sleepiness = -1;
            String [] queries = uri.getQuery().split(",");
            for (String query: queries) {
                try {
                    if (query.startsWith(INPUT)) {
                        input = Integer.parseInt(query.split("=")[1]);
                    } else if (query.startsWith(SLEEP)) {
                        sleepiness = Integer.parseInt(query.split("=")[1]);
                    }
                } catch (NumberFormatException e) {
                    String [] result = {"Bad Request: " + e.toString()};
                    responseHeaders.put(resultHeader, Arrays.asList(result));
                    exchange.sendResponseHeaders(HTTP_BAD_REQUEST, -1);
                    exchange.close();
                    return;
                }
            }

            if ((input < 0) || (sleepiness < 0)) {
                String [] result = {"Bad Request: input and sleep must be " +
                                    "supplied as \"input=<number>,sleep=<number>\" "
                                    + "in the query part of the URI and should be "
                                    + "non-negative integers"};
                responseHeaders.put(resultHeader, Arrays.asList(result));
                exchange.sendResponseHeaders(HTTP_BAD_REQUEST, -1);
                exchange.close();
                return;
            }

            try {
                WorkItem item = new WorkItem(input, sleepiness);
                item.exchange = exchange;
                item.responseHeaders = responseHeaders;
                //item.intime = new Date().getTime();
                item.intime = System.nanoTime();
                workQueue.put(item);
                requestCounter.getAndIncrement();
                return;
            } catch (InterruptedException e) {
                String [] result = {"Request interrupted: " + e.toString()};
                responseHeaders.put(resultHeader, Arrays.asList(result));
                exchange.sendResponseHeaders(HTTP_BAD_SERVER, -1);
                exchange.close();
                return;
            }

            /*
            try {
                while (true) {
                    int limit = 1000;
                    long now = new Date().getTime();
                    for (int i = 0; i < limit; i++) {
                        WorkItem item = new WorkItem(123456, 0);
                        item.intime = new Date().getTime();
                        workQueue.put(item);
                    }
                    long delta = (new Date().getTime()) - now;
                    System.out.println("Request generator: added " + limit +
                                       " work items in " + delta + "ms");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            */
        }
    }

    private static class ResponseThread extends Thread {
        public ResponseThread() {
            super("responder");
        }

        public void run() {
            try {
                while (true) {
                    WorkItem item = outQueue.take();
                    Headers responseHeaders = item.responseHeaders;

                    String [] result = {Long.toString(item.result)};
                    responseHeaders.put(resultHeader, Arrays.asList(result));

                    String [] workTime = {Long.toString(item.finishtime -
                                                        item.outtime)};
                    responseHeaders.put(workTimeHeader, Arrays.asList(workTime));

                    String [] idleTime = {Long.toString(item.outtime -
                                                        item.intime)};
                    responseHeaders.put(idleTimeHeader, Arrays.asList(idleTime));

                    HttpExchange exchange = item.exchange;
                    exchange.sendResponseHeaders(HTTP_OK, -1);
                    exchange.close();
                    responseCounter.getAndIncrement();
                    /*
                    System.err.println("Response: input: " + item.input +
                                       " result: " + item.result +
                                       " idle time (ms): " +
                                       (item.outtime - item.intime) +
                                       " work time (ms): " +
                                       (item.finishtime - item.outtime));
                    */
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static int NUM_REQUEST_THREADS = 5;
    private static String USE_LOCAL_LOAD = "loc";
    /*
     * parameters of the pool sizing algorithm
     */
    private static int MAX_CAPACITY = 1000; //maximum number of threads in pool
    private static int MIN_CAPACITY = 5; //minimum number of threads in pool
    private static int INITIAL_CAPACITY = MIN_CAPACITY; //initial number of threads
                                                        // the in pool
    private static int LOAD_RATIO = 4; //ideal capacity/busy ratio
    private static int MANAGEMENT_PERIOD = 200; //time (in ms) between
                                                 //consecutive pool management
                                                 //activities
    private static int DAMPENING_TERM = 0; //used to reduce short
                                            //growth-shrink-growth oscillations

    public static void main(String[] args) {
        String host = args[0];
        boolean useLocalLoad = true;
        if (Array.getLength(args) > 1) {
            useLocalLoad = args[1].startsWith(USE_LOCAL_LOAD);
        }
        Thread mainThread = Thread.currentThread();
        HttpServer server = createHttpServer(host);
        server.setExecutor(Executors.newFixedThreadPool(NUM_REQUEST_THREADS));
        RequestThread requester = new RequestThread(server);
        ResponseThread responder = new ResponseThread();
        ThreadPool pool = new ThreadPool(INITIAL_CAPACITY, MAX_CAPACITY);
        ManagementThread manager = new ManagementThread(server, pool);
        responder.start();
        server.start();
        System.out.println("main: server IP address and port " +
                            server.getAddress().getAddress() + ":" +
                            server.getAddress().getPort());
        System.out.println("main: using " + (useLocalLoad ? "local" : "global") +
                           " load data");
        System.out.println("main: initial capacity of threadpool: " +
                           pool.getCapacity());
        try {
            mainThread.setPriority(Thread.MAX_PRIORITY);
            while (true) {
                Thread.sleep(MANAGEMENT_PERIOD);
                int localCapacity = pool.getCapacity();
                int localBusy = pool.getBusy();
                System.out.println("main: threadpool stat (busy/capacity): " +
                                   localBusy + "/" + localCapacity);
                int requests = requestCounter.getAndSet(0);
                int responses = responseCounter.getAndSet(0);
                System.out.println("main: requests/responses: " + requests + "/" +
                                   responses);
                int capacity = (useLocalLoad) ? localCapacity : globalCapacity;
                int busy = (useLocalLoad) ? localBusy : globalBusy;
                int fraction = (useLocalLoad) ? 100 : loadBalanceFactor;
                if ((busy == 0) && (localCapacity > MIN_CAPACITY)) {
                    pool.shrink((localCapacity - MIN_CAPACITY), localCapacity);
                    System.out.println("main: shrunk threadpool to " +
                                       MIN_CAPACITY);
                    continue;
                } else if (busy == 0) {
                    continue;
                }
                if (((capacity / busy) < LOAD_RATIO)) {
                    int growth = ((LOAD_RATIO*busy - capacity) * fraction) / 100;
                    if (growth < DAMPENING_TERM) {
                        growth = DAMPENING_TERM;
                    }
                    pool.grow(growth, localCapacity);
                    System.out.println("main: grown threadpool to " +
                                       (localCapacity + growth));
                } else if ((capacity > MIN_CAPACITY) &&
                           ((capacity / busy) > LOAD_RATIO) &&
                           ((capacity - LOAD_RATIO*busy) > DAMPENING_TERM)) {
                    int decay = ((capacity - LOAD_RATIO*busy) * fraction) / 100;
                    pool.shrink(decay, localCapacity);
                    System.out.println("main: shrunk threadpool to " +
                                       (localCapacity - decay));
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
