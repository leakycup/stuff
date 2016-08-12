package in.blogspot.freemind_subwaywall.everything_else;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


/**
 * Created by soubhik on 12-08-2016.
 */
public class ProducerConsumerMinimalLocking {
    private static final int MAX_SLEEP = 100;
    private static final int RUNTIME = 10000;

    private static class Message {
        int[] itemList;
        int idx;
        int senderId;
        boolean replied;

        Message(int senderId) {
            this.senderId = senderId;
            this.replied = false;
        }
    }

    private AtomicBoolean keepRunning;
    private final List<Producer> producerList;
    private final List<Consumer> consumerList;
    private final int numProducers, numConsumers;

    private final AtomicReference<Message>[] consumerToProducerMessages;
    private final AtomicReference<Message>[] producerToConsumerMessages;
    private final Random random;

    private class Producer extends Thread {
        private final Random generator;
        private final int id;
        private final Message message;
        private int[] itemList; //this is actually a bounded stack (LIFO)
        private int idx;
        private int itemsProduced;

        Producer(int id, int itemListSize) {
            this.generator = new Random();
            this.id = id;
            this.itemList = new int[itemListSize];
            this.idx = 0;
            this.itemsProduced = 0;
            this.message = new Message(id);
        }

        public void run() {
            while (keepRunning.get()) {
                //sleep to control producer's rate
                long sleep = generator.nextInt(MAX_SLEEP);
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    System.err.println("Producer: " + id + ", sleep interrupted.");
                }

                //if itemList is full, find a random consumer and exchange itemList with it
                while (idx == itemList.length) {
                    int consumerIdx = random.nextInt(numConsumers);
                    Consumer consumer = consumerList.get(consumerIdx);
                    message.itemList = this.itemList;
                    message.idx = this.idx;
                    message.replied = false;
                    synchronized (consumer) {
                        producerToConsumerMessages[consumerIdx].set(message);
                        while (!message.replied) {
                            try {
                                consumer.wait();
                            } catch (InterruptedException e) {
                                //ignore
                            }
                        }
                        this.itemList = message.itemList;
                        this.idx = message.idx + 1;
                        System.out.println("Producer: " + id + ", buffer full. swapped with consumer: " + consumerIdx +
                                ". new buffer size: " + idx);
                    }
                }

                //produce an item and put it in itemList
                int item = generator.nextInt();
                itemList[idx] = item;
                idx++;
                itemsProduced++;
                System.out.println("Producer: " + id + ", index: " + idx + ", item: " + item +
                        ", total items produced: " + itemsProduced);

                //process a possible incoming message from a consumer
                Message incoming = consumerToProducerMessages[this.id].get();
                if (incoming != null) {
                    int[] incomingItemList = incoming.itemList;
                    int incomingIdx = incoming.idx;
                    incoming.itemList = this.itemList;
                    incoming.idx = this.idx;
                    this.itemList = incomingItemList;
                    this.idx = incomingIdx;
                    incoming.replied = true;
                    synchronized (this){
                        consumerToProducerMessages[this.id].set(null);
                        this.notify();
                    }
                }
            }
        }

        void report(long tid) {
            System.err.println("Producer: " + id + ", cpuTime (ns): " + cpuTime(tid) + ", itemsProduced: " + itemsProduced);
            System.err.println(threadInfo(tid));
        }
    }

    private class Consumer extends Thread {
        private final Random generator;
        private final int id;
        private final Message message;
        private int[] itemList; //this is actually a bounded stack (LIFO)
        private int idx;
        private int itemsConsumed;

        Consumer(int id, int itemListSize) {
            this.generator = new Random();
            this.id = id;
            this.itemList = new int[itemListSize];
            this.idx = -1;
            this.itemsConsumed = 0;
            this.message = new Message(id);
        }

        public void run() {
            while (keepRunning.get()) {
                //sleep to control consumer's rate
                long sleep = generator.nextInt(MAX_SLEEP);
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    System.err.println("Consumer: " + id + ", sleep interrupted.");
                }

                //if itemList is empty, pick a random producer and exchange itemList with it
                while (idx < 0) {
                    int producerIdx = random.nextInt(numProducers);
                    Producer producer = producerList.get(producerIdx);
                    message.itemList = this.itemList;
                    message.idx = this.idx;
                    message.replied = false;
                    synchronized (producer) {
                        consumerToProducerMessages[producerIdx].set(message);
                        while (!message.replied) {
                            try {
                                producer.wait();
                            } catch (InterruptedException e) {
                                //ignore
                            }
                        }
                    }
                    this.itemList = message.itemList;
                    this.idx = message.idx - 1;
                    System.out.println("Consumer: " + id + ", buffer empty. swapped with producer: " + producerIdx +
                            ". new buffer size: " + idx+1);
                }

                //consume an item
                int item = itemList[idx];
                idx--;
                itemsConsumed++;
                System.out.println("Consumer: " + id + ", index: " + idx + ", item: " + item +
                        ", total items cconsumed: " + itemsConsumed);

                //process an incoming message from a producer, if there's one
                Message incoming = producerToConsumerMessages[this.id].get();
                if (incoming != null) {
                    int[] incomingItemList = incoming.itemList;
                    int incomingIdx = incoming.idx;
                    incoming.itemList = this.itemList;
                    incoming.idx = this.idx;
                    incoming.replied = true;
                    synchronized (this) {
                        producerToConsumerMessages[this.id].set(null);
                        this.notify();
                    }
                }
            }
        }

        void report(long tid) {
            System.err.println("Consumer: " + id + ", cpuTime (ns): " + cpuTime(tid) + ", itemsConsumed: " + itemsConsumed);
            System.err.println(threadInfo(tid));
        }
    }

    public ProducerConsumerMinimalLocking(int maxItems, int numProducers, int numConsumers) {
        this.producerList = new ArrayList<Producer>(numProducers);
        for (int i = 0; i < numProducers; i++) {
            producerList.add(new Producer(i, maxItems));
        }
        this.numProducers = numProducers;

        this.consumerList = new ArrayList<Consumer>(numConsumers);
        for (int i = 0; i < numConsumers; i++) {
            consumerList.add(new Consumer(i, maxItems));
        }
        this.numConsumers = numConsumers;

        this.consumerToProducerMessages = new AtomicReference[numProducers];
        for (int i = 0; i < consumerToProducerMessages.length; i++) {
            consumerToProducerMessages[i] = new AtomicReference<Message>();
        }

        this.producerToConsumerMessages = new AtomicReference[numConsumers];
        for (int i = 0; i < producerToConsumerMessages.length; i++) {
            producerToConsumerMessages[i] = new AtomicReference<Message>();
        }

        this.random = new Random();

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

