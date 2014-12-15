package in.blogspot.freemind_subwaywall.everything_else;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.management.ThreadInfo;

//NOTE: replacing notifyAll() with notify() causes deadlock on a 
// MacBook Pro (retina) with 2.7 GHz Intel Core i7, run with 50 maxItems,
// 1 producer, 2 consumers. this is how it happens:
// at some point, both the consumers find the itemList empty and waits.
// the producer produces an item, wakes up one consumer using notify().
// producer continues to produce items faster than the lone consumer
// can consume them. thus, the itemList is full at some point and producer
// waits too. the lone consumer consumes one item from the full itemList
// and sends a notify. as a matter of chance, the notify() wakes up the
// other consumer, leaving the producer in wait state. both consumers
// together empty the itemList eventually and start waiting. so, all
// 3 threads are now waiting and the wait is going to last forever.
// the deadlock can be avoided if a consumer keeps calling notify() after
// consuming each item (even if the itemList was not full before consuming the
// item). this leads to unnecessary calls to notify(). this can still be optimized
// if producer increments a counter before it starts waiting, so that consumer calls a notify()
// after consuming an item only if this counter is non-zero.
// however, the other problem with notify() is it is less efficient. if producer is faster
// than the consumer then it makes sense to have more consumers run and consume items from
// the itemList. however, notify() only wakes up one. once again, a consumer can increment a counter
// before going on a wait and producer can send a notify if this counter is non-zero.
public class ProducerConsumer {
    private static final int SLEEP = 0;
    private static final int RUNTIME = 10000;

    private int[] itemList; //this is actually a bounded stack (LIFO)
    private int noItems;
    List<Producer> producerList;
    List<Consumer> consumerList;
    AtomicBoolean keepRunning;

    private class Producer extends Thread {
        private Random generator;
        private int id;
        private int sleep;
        private int itemsProduced;

        Producer(int id, int sleep) {
            generator = new Random();
            this.id = id;
            this.sleep = sleep; // in ms
            this.itemsProduced = 0;
        }

        public void run() {
            while (keepRunning.get()) {
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    System.err.println("Producer: " + id + ", sleep interrupted.");
                }
                int item = generator.nextInt();
                synchronized(itemList) {
                    while (noItems == itemList.length) {
                        try {
                            itemList.wait();
                        } catch (InterruptedException e) {
                            System.err.println("Producer: " + id + ", wait interrupted.");
                        }
                    }
                    itemList[noItems] = item;
                    System.out.println("Producer: " + id + ", index: " + noItems + ", item: " + item);
                    if (noItems == 0) {
                        itemList.notifyAll();
                    }
                    noItems++;
                    itemsProduced++;
                }
            }
        }

        void report(long tid) {
            System.err.println("Producer: " + id + ", cpuTime (ns): " + cpuTime(tid) + ", itemsProduced: " + itemsProduced);
            System.err.println(threadInfo(tid));
        }
    }

    private class Consumer extends Thread {
        private int id;
        private int itemsConsumed;

        Consumer(int id) {
            this.id = id;
            this.itemsConsumed = 0;
        }

        public void run() {
            while (keepRunning.get()) {
                synchronized(itemList) {
                    while (noItems == 0) {
                        try {
                            itemList.wait();
                        } catch (InterruptedException e) {
                            System.err.println("Consumer: " + id + ", wait interrupted.");
                        }
                    }
                    noItems--;
                    int item = itemList[noItems];
                    System.out.println("Consumer: " + id + ", index: " + noItems + ", item: " + item);
                    if (noItems == (itemList.length - 1)) {
                        itemList.notifyAll();
                    }
                    itemsConsumed++;
                }
            }
        }

        void report(long tid) {
            System.err.println("Consumer: " + id + ", cpuTime (ns): " + cpuTime(tid) + ", itemsConsumed: " + itemsConsumed);
            System.err.println(threadInfo(tid));
        }
    }

    public ProducerConsumer(int maxItems, int noProducers, int noConsumers) {
        this.itemList = new int[maxItems];
        this.noItems = 0;

        producerList = new ArrayList<>(noProducers);
        for (int i = 0; i < noProducers; i++) {
            producerList.add(new Producer(i, SLEEP));
        }

        consumerList = new ArrayList<>(noConsumers);
        for (int i = 0; i < noConsumers; i++) {
            consumerList.add(new Consumer(i));
        }

        this.keepRunning = new AtomicBoolean(true);

    }

    public void start() {
        for (Producer p: producerList) {
            p.start();
        }

        for (Consumer c: consumerList) {
            c.start();
        }
    }

    public void stop() {
        keepRunning.set(false);

        for (Producer p: producerList) {
            p.interrupt();
        }

        for (Consumer c: consumerList) {
            c.interrupt();
        }
    }

    public void report() {
        for (Producer p: producerList) {
            p.report(p.getId());
        }

        for (Consumer c: consumerList) {
            c.report(c.getId());
        }

        long[] deadlockedIds = ManagementFactory.getThreadMXBean().findDeadlockedThreads();
        if (deadlockedIds != null) {
            System.out.println("Deadlocked threads found: " + deadlockedIds.length);
            for (long id: deadlockedIds) {
                System.err.println(ManagementFactory.getThreadMXBean().getThreadInfo(id));
            }
        } else {
            System.err.println("No deadlocked threads found");
        }
    }

    private static long cpuTime(long id) {
        return (ManagementFactory.getThreadMXBean().getThreadCpuTime(id));
    }

    private static ThreadInfo threadInfo(long id) {
        return (ManagementFactory.getThreadMXBean().getThreadInfo(id));
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            throw new RuntimeException("Invalid no. of arguments: pass <maxItems> <noProducers> <noConsumers>");
        }
        int maxItems = Integer.parseInt(args[0]);
        int noProducers = Integer.parseInt(args[1]);
        int noConsumers = Integer.parseInt(args[2]);

        ProducerConsumer producerConsumer = new ProducerConsumer(maxItems, noProducers, noConsumers);
        producerConsumer.start();
        try {
            Thread.sleep(RUNTIME);
        } catch (InterruptedException e) {
            System.err.println("Interrupted....");
        }

        producerConsumer.report();
        producerConsumer.stop();
        System.exit(0);
    }
}
