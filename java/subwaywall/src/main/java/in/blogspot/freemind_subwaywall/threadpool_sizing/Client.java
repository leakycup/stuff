package in.blogspot.freemind_subwaywall.threadpool_sizing;

import java.lang.Thread;

import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.Date;
import java.util.Random;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class Client {

    private static final String resultHeader = "X-Result";

    private static class ManagementThread extends Thread {

        private Collection<Callable<GetLoadInfo>> getLoadTasks;
        private Collection<SetLoadTask> setLoadTasks;
        private ExecutorService executors;

        public ManagementThread(List<HostPortPair> hostPortPairs) {
            final String protocol = "http";
            final String path = "/threadpooltest/management/";
            final String getLoadQuery = "?getload";
            final String busyHeader = "X-busy";
            final String capacityHeader = "X-capacity";

            int numberOfHosts = hostPortPairs.size();
            final int loadBalanceFactor = 100/numberOfHosts;
            executors = Executors.newFixedThreadPool(numberOfHosts);
            getLoadTasks = new ArrayList<Callable<GetLoadInfo>>(numberOfHosts);
            setLoadTasks = new ArrayList<SetLoadTask>(numberOfHosts);

            for (HostPortPair pair: hostPortPairs) {
                final String host = pair.first;
                final int port = Integer.parseInt(pair.second);

                Callable<GetLoadInfo> getLoadTask = new Callable
                                                            <GetLoadInfo>() {
                    public GetLoadInfo call() throws Exception {
                        try {
                            URL url = new URL(protocol, host, port,
                                              path + getLoadQuery);
                            HttpURLConnection connection = (HttpURLConnection)
                                                            url.openConnection();
                            connection.connect();
                            int code = connection.getResponseCode();
                            if (code == HttpURLConnection.HTTP_OK) {
                                String busy = connection.getHeaderField(busyHeader);
                                String capacity = connection.getHeaderField(
                                                                    capacityHeader);
                                GetLoadInfo info = new GetLoadInfo(busy, capacity,
                                                                host + ":" + port);
                                return (info);
                            } else {
                                String result = connection.
                                                    getHeaderField(resultHeader);
                                System.err.println("Management thread: HTTP error"
                                                   + ". code: " + code +
                                                   ((result == null) ?
                                                        "" : " (" + result + ")") +
                                                   ", url: " + url);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return (null);
                    }
                };
                getLoadTasks.add(getLoadTask);

                SetLoadTask setLoadTask = new SetLoadTask() {
                    public Void call() throws Exception {
                        try {
                            String query = "?busy=" + first + ",capacity=" + second
                                           + ",factor=" + loadBalanceFactor;
                            URL url = new URL(protocol, host, port, path + query);
                            HttpURLConnection connection = (HttpURLConnection)
                                                            url.openConnection();
                            connection.connect();
                            int code = connection.getResponseCode();
                            if (code != HttpURLConnection.HTTP_OK) {
                                String result = connection.
                                                    getHeaderField(resultHeader);
                                System.err.println("Management thread: HTTP error"
                                                   + ". code: " + code +
                                                   ((result == null) ?
                                                        "" : " (" + result + ")") +
                                                   ", url: " + url);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return (null);
                    }
                };
                setLoadTasks.add(setLoadTask);
            }
        }

        private static long MANAGEMENT_PERIOD = 100;
        private static long MIN_MANAGEMENT_PERIOD = 20;

        public void run() {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

            int prevBusy = 0;
            int prevCapacity = 0;
mainLoop:
            while (true) {
                long start = (new Date()).getTime();

                int busy = 0;
                int capacity = 0;

                try {
                    List<Future<GetLoadInfo>> getLoadInfoFutures =
                                                executors.invokeAll(getLoadTasks);
                    for (Future<GetLoadInfo> future: getLoadInfoFutures) {
                        GetLoadInfo info = future.get();
                        if (info == null) { // an exception in callable. start over.
                            Thread.sleep(MIN_MANAGEMENT_PERIOD);
                            continue mainLoop;
                        }
                        try {
                            int serverBusy = Integer.parseInt(info.first);
                            int serverCapacity = Integer.parseInt(info.second);
                            String server = info.server;
                            busy += serverBusy;
                            capacity += serverCapacity;
                            System.out.println("Management thread: get:" +
                                               " Server: " + server +
                                               " busy: " + serverBusy +
                                               " capacity: " + serverCapacity +
                                               " global busy: " + prevBusy +
                                               " global capacity: " + prevCapacity);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            Thread.sleep(MIN_MANAGEMENT_PERIOD);
                            continue mainLoop;
                        }
                    }
                    for (SetLoadTask task: setLoadTasks) {
                        task.setBusyAndCapacity(busy, capacity);
                    }
                    System.out.println("Management thread: set:" +
                                       " global busy: " + busy +
                                       " global capacity: " + capacity);
                    executors.invokeAll(setLoadTasks);
                    prevBusy = busy;
                    prevCapacity = capacity;

                    long end = (new Date()).getTime();
                    long napPeriod = MANAGEMENT_PERIOD - (end - start);
                    if (napPeriod < MIN_MANAGEMENT_PERIOD) {
                        napPeriod = MIN_MANAGEMENT_PERIOD;
                    }
                    //System.err.println("Management thread: sleeping for " +
                                       //napPeriod + "ms");
                    Thread.sleep(napPeriod);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    continue mainLoop;
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    continue mainLoop;
                }
            }
        }
    }

    private static class ResponseThread extends Thread {
        private static String idleTimeHeader = "X-idle-time";
        private static String workTimeHeader = "X-work-time";
        HttpURLConnection connection;
        int input;
        int sleep;

        public ResponseThread(HttpURLConnection c, int input, int sleep) {
            super("response " + c.getURL());
            connection = c;
            this.input = input;
            this.sleep = sleep;
        }

        public void run() {
            try {
                    // sbh: debug java.net.SocketException: Connection reset
                    if (false) {
                    try {
                        sleep(200); //sleep for 2 ms
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    }
                    // sbh: end: debug java.net.SocketException: Connection reset
                int code = connection.getResponseCode();
                String host = connection.getURL().getHost();
                int port = connection.getURL().getPort();
                String result = connection.getHeaderField(resultHeader);
                if (code == HttpURLConnection.HTTP_OK) {
                    String idleTime = connection.getHeaderField(idleTimeHeader);
                    String workTime = connection.getHeaderField(workTimeHeader);
                    System.out.println("Response: code: " + code +
                                       ",input: " + input +
                                       ",result: " + result +
                                       ",sleep: " + sleep +
                                       ",idle-time: " + idleTime +
                                       ",work-time: " + workTime +
                                       ",host: " + host +
                                       ",port: " + port);
                } else {
                    System.out.println("Response: code: " + code +
                                       ",input: " + input +
                                       ",result: " + result +
                                       ",sleep: " + sleep +
                                       ",idle-time: " + "0" +
                                       ",work-time: " + "0" +
                                       ",host: " + host +
                                       ",port: " + port);
                }
            } catch (IOException e) {
                System.err.println("Response: IOException: input: " +
                                   input + " sleep: " + sleep);
                e.printStackTrace();
            }
        }
    }

    private static class RequestStream extends Thread {
        private static String protocol = "http";
        private static String path = "/threadpooltest/";
        private static int MAXINPUT = 1000000;
        private static int MAXSLEEP = 1000;
        private CircularListIterator<HostPortPair> serverList;
        private Random randomInput;
        private Random randomSleep;
        private BufferedReader inputFile = null;

        public RequestStream(List<HostPortPair> hostPortPairs, String fileName) {
            super("requeststream");
            serverList = new CircularListIterator<HostPortPair>(hostPortPairs);
            if (fileName != null) {
                try {
                inputFile = new BufferedReader(new InputStreamReader(
                                                    new FileInputStream(fileName)));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
            randomInput = new Random((new Date()).getTime());
            try {
                sleep(randomInput.nextInt(2000)); //sleep for at most 2 sec
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            randomSleep = new Random((new Date()).getTime());
        }

        public void run() {
            HostPortPair pair = serverList.next();
            int cumulativeWait = 0;
            //final int MOVE_TO_NEXT = -1;
            final int MOVE_TO_NEXT = 250; //send traffic to a server for 250ms
            while (true) {
                try {
                    int input, sleep, wait;
                    if (inputFile == null) {
                        input = randomInput.nextInt(MAXINPUT);
                        sleep = randomSleep.nextInt(MAXSLEEP);
                        wait = randomInput.nextInt(10000); //sleep for at most 10sec
                    } else {
                        String inputLine = inputFile.readLine();
                        if (inputLine == null) {
                            break;
                        }
                        if (inputLine.startsWith("#")) {
                            continue;
                        }
                        String[] values = inputLine.split(",");
                        input = Integer.parseInt(values[0]);
                        sleep = Integer.parseInt(values[1]);
                        wait = Integer.parseInt(values[2]);
                    }
                    String query = "?" + "input=" + input + "," + "sleep=" + sleep;
                    String host = pair.first;
                    int port = Integer.parseInt(pair.second);
                    URL url = new URL(protocol, host, port, path + query);
                    HttpURLConnection connection = (HttpURLConnection)
                                                   url.openConnection();
                    //System.err.println("Request: opened connection to " + url);
                    // sbh: debug java.net.SocketException: Connection reset
                    if (true) {
                    try {
                        sleep(wait);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    }
                    // sbh: end: debug java.net.SocketException: Connection reset
                    connection.connect();
                    (new ResponseThread(connection, input, sleep)).start();
                    cumulativeWait += wait;
                    if (cumulativeWait > MOVE_TO_NEXT) {
                        cumulativeWait = 0;
                        pair = serverList.next();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    private static class Pair<F, S> {
        public F first;
        public S second;
        public Pair (F f, S s) {
            first = f;
            second = s;
        }
    }

    private static class HostPortPair extends Pair<String, String> {
        public HostPortPair(String host, String port) {
            super(host, port);
        }
    }

    private static class BusyCapacityPair extends Pair<String, String> {
        public BusyCapacityPair(String busy, String capacity) {
            super(busy, capacity);
        }
    }

    private static class GetLoadInfo extends BusyCapacityPair {
        private String server;

        public GetLoadInfo(String busy, String capacity, String server) {
            super(busy, capacity);
            this.server = server;
        }
    }

    private abstract static class SetLoadTask extends BusyCapacityPair
                                              implements Callable<Void> {
        public SetLoadTask() {
            super(null, null);
        }

        public void setBusyAndCapacity(int busy, int capacity) {
            first = Integer.toString(busy);
            second = Integer.toString(capacity);
        }
    }

    public static void main(String[] args) {
        List<HostPortPair> hostPortPairs = new LinkedList<HostPortPair>();
        String fileName = args[0];
        boolean ignoreFirstArg = true;
        for (String arg: args) {
            if (ignoreFirstArg) {
                ignoreFirstArg = false;
                continue;
            }
            String [] hostAndPort = arg.split(":");
            String host = hostAndPort[0];
            String port = hostAndPort[1];
            System.out.println("main thread: host:port " + host + ":" + port);
            hostPortPairs.add(new HostPortPair(host, port));
        }
        if (fileName.equals("-")) {
            fileName = null;
        }
        RequestStream requests = new RequestStream(hostPortPairs, fileName);
        ManagementThread manager = new ManagementThread(hostPortPairs);
        requests.start();
        manager.start();
    }
}

class CircularListIterator<E> implements Iterator<E> {
    private int idx = 0;
    List<E> list;

    public CircularListIterator(List<E> l) {
        list = l;
    }

    public boolean hasNext() {
        if (list.size() == 0) {
            return (false);
        }
        return (true);
    }

    public E next() {
        int listSize = list.size();
        if (listSize == 0) {
            throw (new NoSuchElementException());
        }

        if (idx == listSize) {
            idx = 0;
        }
        E current = list.get(idx);
        idx++;

        return (current);
    }

    public void remove() {
        throw (new UnsupportedOperationException());
    }
}
