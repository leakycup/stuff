package in.blogspot.freemind_subwaywall.everything_else;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * Created by soubhik on 14-08-2016.
 */
public class ProducerConsumerLockFree {
    /** lock free circular FIFO.
     * 1. multiple producers and multiple consumers (MPMC) supported.
     * 2. implementation is not wait free. producers wait if the buffer is full, consumers wait if buffer is empty.
     * 3. fifo can not store NULL. NULL is used to test if a slot in buffer is empty.
     * 4. circular FIFO is implemented over a fixed sized buffer. no memory allocation during insert/remove.
     */
    public static class LockFreeCircularFifo<T> {
        private static final int maxTries = 10;

        private final AtomicReferenceArray<T> buffer;
        private final int size;
        private final AtomicInteger head;
        private final AtomicInteger tail;

        private static class DebugInfo {
            static final int DEBUG_INFO_LENGTH = 15;
            int index = 0;
            String[] debugList = new String[DEBUG_INFO_LENGTH];

            void add(String s) {
                debugList[index] = s;
                index = (index + 1) % DEBUG_INFO_LENGTH;
            }

            void print() {
                long tid = Thread.currentThread().getId();
                for (int i = index; i < (index + DEBUG_INFO_LENGTH); i++) {
                    String s = debugList[i%DEBUG_INFO_LENGTH];
                    if (s != null) {
                        System.err.println(tid + ": " + s);
                    }
                }
            }
        }
        //this exists so that the FIFO can be debugged with minimal effect on the timing or sequence of events.
        //if a println() is added or a debugger is attached, the timing/sequencing gets impacted.
        private final ThreadLocal<DebugInfo> debugInfo;

        public LockFreeCircularFifo(int size) {
            assert (size > 0);

            this.buffer = new AtomicReferenceArray<T>(size);
            this.size = size;
            this.head = new AtomicInteger(0);
            this.tail = new AtomicInteger(0);
            this.debugInfo = new ThreadLocal<DebugInfo>() {
                @Override
                protected DebugInfo initialValue() {
                    return new DebugInfo();
                }
            };
        }

        public boolean insert(T item) throws InterruptedException {
            assert (item != null);

            //reserve a slot in buffer[currentTail]. by advancing the tail index, we hope to ensure that
            //no other producer thread tries to insert into buffer[currentTail].
            Integer currentTail = tail.getAndIncrement();

            for (int i = 0; i < maxTries; i++) {
                if (buffer.compareAndSet(currentTail%size, null, item)) {
                    debugInfo.get().add("insert(): currentTail: " + currentTail + " item: " + item + " SUCCESS");
                    return true;
                }
            }
            tail.getAndDecrement();
            debugInfo.get().add("insert(): currentTail: " + currentTail + " item: " + item + " FAILED");

            return false;
        }

        public T remove() throws InterruptedException {
            Integer currentHead = head.getAndIncrement();

            for (int i = 0; i < maxTries; i++) {
                T item = buffer.get(currentHead%size);
                if ((item != null) && buffer.compareAndSet(currentHead%size, item, null)) {
                    debugInfo.get().add("remove(): currentHead: " + currentHead + " item: " + item);
                    return item;
                }
            }
            head.getAndDecrement();
            debugInfo.get().add("remove(): currentHead: " + currentHead + " item: " + null);

            return null;
        }

        public void printDebug() {
            debugInfo.get().print();
        }
    }

    private static long cpuTime(long id) {
        return (ManagementFactory.getThreadMXBean().getThreadCpuTime(id));
    }

    private static ThreadInfo threadInfo(long id) {
        return (ManagementFactory.getThreadMXBean().getThreadInfo(id));
    }

    private static class IntegerProducer extends Thread {
        private static AtomicInteger idMaker = new AtomicInteger(0);

        private final int start;
        private final int end;
        private final int maxSleepTime;
        private final Random random;
        private final LockFreeCircularFifo<Integer> fifo;
        private final int id;
        private AtomicBoolean stopNow;
        private int next;

        IntegerProducer(int start, int end, int maxSleepTime, LockFreeCircularFifo<Integer> fifo) {
            this.start = start;
            this.end = end;
            this.next = start;
            this.maxSleepTime = maxSleepTime;
            this.random = new Random();
            this.fifo = fifo;
            this.id = idMaker.getAndIncrement();
            this.stopNow = new AtomicBoolean(false);
        }

        @Override
        public void run() {
            int items = 0;
            while (!stopNow.get()) {
                if (maxSleepTime > 0) {
                    long sleepTime = random.nextInt(maxSleepTime);
                    try {
                        sleep(sleepTime);
                    } catch (InterruptedException e) {
                        break;
                    }
                }

                boolean success;
                try {
                    success = fifo.insert(next);
                } catch (InterruptedException e) {
                    break;
                }
                if (!success) {
                    continue;
                }

                System.out.println("produced n: " + next);

                items++;
                next++;
                if (next >= end) {
                    break;
                }
            }
            printDebug(items);
        }

        public void lazyStop() {
            this.stopNow.set(true);
        }

        private void printDebug(int items) {
            long tid = getId();
            System.err.println("Producer: " + id + ", thread Id: " + tid +
                    " produced " + items + " integers from " + start + " to " + (next-1) +
                    " cpuTime: " + cpuTime(tid));
            fifo.printDebug();
            System.err.println(threadInfo(tid));
        }
    }

    private static class IntegerConsumer extends Thread implements Iterable<Integer> {
        private static AtomicInteger idMaker = new AtomicInteger(0);

        private final int maxSleepTime;
        private final Random random;
        private final LockFreeCircularFifo<Integer> fifo;
        private final List<Integer> numbers;
        private final int id;
        private AtomicBoolean stopNow;

        IntegerConsumer(int maxSleepTime, LockFreeCircularFifo<Integer> fifo) {
            this.maxSleepTime = maxSleepTime;
            this.random = new Random();
            this.fifo = fifo;
            this.id = idMaker.getAndIncrement();
            this.numbers = new ArrayList<>();
            this.stopNow = new AtomicBoolean(false);
        }

        @Override
        public void run() {
            int items = 0;
            while (!stopNow.get()) {
                if (maxSleepTime > 0) {
                    long sleepTime = random.nextInt(maxSleepTime);
                    try {
                        sleep(sleepTime);
                    } catch (InterruptedException e) {
                        break;
                    }
                }

                Integer n;
                try {
                    n = fifo.remove();
                } catch (InterruptedException e) {
                    break;
                }
                if (n == null) {
                    continue;
                }
                numbers.add(n);

                items++;
                System.out.println("consumed n: " + n);
            }
            printDebug(items);
        }

        public void lazyStop() {
            this.stopNow.set(true);
        }

        @Override
        public Iterator<Integer> iterator() {
            return numbers.iterator();
        }

        private void printDebug(int items) {
            long tid = getId();
            System.err.println("Consumer: " + id + ", thread Id: " + tid + " consumed " + items + " items" +
                                " cpuTime: " + cpuTime(tid));
            fifo.printDebug();
            System.err.println(threadInfo(tid));
        }
    }

    private static void multipleProducersMultipleConsumersTest(int[] starts, int[] ends, int[] producerSleeps,
                                                               int[] consumerSleeps,
                                                               LockFreeCircularFifo<Integer> fifo) {
        assert (starts.length == ends.length);
        assert (starts.length == producerSleeps.length);

        final long MAX_RUN_TIME = 30000;

        IntegerProducer[] producers = new IntegerProducer[starts.length];
        for (int i = 0; i < starts.length; i++) {
            producers[i] = new IntegerProducer(starts[i], ends[i], producerSleeps[i], fifo);
        }

        IntegerConsumer[] consumers = new IntegerConsumer[consumerSleeps.length];
        for (int i = 0; i < consumerSleeps.length; i++) {
            consumers[i] = new IntegerConsumer(consumerSleeps[i], fifo);
        }

        for (IntegerConsumer consumer: consumers) {
            consumer.start();
        }
        for (IntegerProducer producer: producers) {
            producer.start();
        }

        try {
            for (IntegerProducer producer : producers) {
                producer.join(MAX_RUN_TIME);
            }
            for (IntegerConsumer consumer: consumers) {
                consumer.join(MAX_RUN_TIME);
            }
        } catch (InterruptedException e) {
            System.err.println("abrupt end of MPMC test!");
        }

        long[] deadlockedIds = ManagementFactory.getThreadMXBean().findDeadlockedThreads();
        if (deadlockedIds != null) {
            System.err.println("MPMC Test: Deadlocked threads found: " + deadlockedIds.length);
            for (long id: deadlockedIds) {
                System.err.println(ManagementFactory.getThreadMXBean().getThreadInfo(id));
            }
        } else {
            System.err.println("MPMC Test: No deadlocked threads found");
        }

        for (IntegerProducer producer : producers) {
            if (producer.isAlive()) {
                producer.interrupt();
            }
        }
        for (IntegerConsumer consumer: consumers) {
            if (consumer.isAlive()) {
                consumer.interrupt();
            }
        }

        List<Integer>[] items = new List[producers.length];
        for (int i = 0; i < producers.length; i++) {
            items[i] = new ArrayList<>();
        }

        for (IntegerConsumer consumer: consumers) {
            List<Integer>[] consumedItems = new List[producers.length];
            Iterator<Integer> results = consumer.iterator();
            while (results.hasNext()) {
                int r = results.next();
                int p = -1;
                for (int i = 0; i < producers.length; i++) {
                    if ((r >= starts[i]) && (r < ends[i])) {
                        p = i;
                        break;
                    }
                }
                assert (p != -1);
                if (consumedItems[p] == null) {
                    consumedItems[p] = new ArrayList<>();
                    consumedItems[p].add(r);
                    continue;
                }
                int last = consumedItems[p].get(consumedItems[p].size() - 1);
                assert (r > last);
            }
            for (int i = 0; i < producers.length; i++) {
                items[i].addAll(consumedItems[i]);
            }
        }

        for (int i = 0; i < producers.length; i++) {
            int start = starts[i];
            int end = ends[i];
            int numItems = end - start;
            assert (numItems == items[i].size());
            Collections.sort(items[i]);
            for (int j = 0; j < numItems; j++) {
                int item = items[i].get(j);
                assert (item == (start + j));
            }
        }
    }

    private static void singleProducerSingleConsumerTest(int start, int end, int producerSleep, int consumerSleep,
                                                         LockFreeCircularFifo<Integer> fifo) {
        final long MAX_RUN_TIME = 30000;

        IntegerProducer producer = new IntegerProducer(start, end, producerSleep, fifo);
        IntegerConsumer consumer = new IntegerConsumer(consumerSleep, fifo);
        consumer.start();
        producer.start();
        try {
            producer.join(MAX_RUN_TIME);
            Thread.sleep(20);
        } catch (InterruptedException e) {
            System.out.println("Test lock free circular fifo (multi-threaded) ended abruptly!!");
        }

        long[] deadlockedIds = ManagementFactory.getThreadMXBean().findDeadlockedThreads();
        if (deadlockedIds != null) {
            System.err.println("SPSC Test: Deadlocked threads found: " + deadlockedIds.length);
            for (long id: deadlockedIds) {
                System.err.println(ManagementFactory.getThreadMXBean().getThreadInfo(id));
            }
        } else {
            System.err.println("SPSC Test: No deadlocked threads found");
        }
        consumer.interrupt();
        Iterator<Integer> results = consumer.iterator();
        for (int i = start; i < end; i++) {
            int n = results.next();
            assert (i == n);
        }
        assert (results.hasNext() == false);
    }

    private static void testLockFreeCircularFifo() {
        System.out.println("Test lock free circular fifo");
        System.out.println("====================================");

        LockFreeCircularFifo<Integer> fifo = new LockFreeCircularFifo<>(5);

        try {
            fifo.insert(0);
            int zero = fifo.remove();
            assert (zero == 0);

            fifo.insert(1);
            fifo.insert(2);
            fifo.insert(3);
            int one = fifo.remove();
            int two = fifo.remove();
            int three = fifo.remove();
            assert (one == 1);
            assert (two == 2);
            assert (three == 3);

            fifo.insert(1);
            fifo.insert(2);
            one = fifo.remove();
            fifo.insert(3);
            fifo.insert(4);
            two = fifo.remove();
            three = fifo.remove();
            int four = fifo.remove();
            assert (one == 1);
            assert (two == 2);
            assert (three == 3);
            assert (four == 4);

            fifo.insert(2);
            fifo.insert(2);
            int two_1 = fifo.remove();
            int two_2 = fifo.remove();
            assert (two_1 == 2);
            assert (two_2 == 2);
        } catch (InterruptedException e) {
            System.out.println("Test lock free circular fifo (single threaded) ended abruptly!!");
        }

        System.out.println("singleProducerSingleConsumerTest: 2 to 7, 10 ms, 0 ms:");
        System.out.println("----------------------------------------------------------");
        singleProducerSingleConsumerTest(2, 7, 10, 0, fifo);
        System.out.println("singleProducerSingleConsumerTest: 2 to 7, 10 ms, 10 ms:");
        System.out.println("----------------------------------------------------------");
        fifo = new LockFreeCircularFifo<>(5);
        singleProducerSingleConsumerTest(2, 7, 10, 10, fifo);

        System.out.println("multipleProducerMultipleConsumerTest# 1");
        System.out.println("----------------------------------------------------------");
        fifo = new LockFreeCircularFifo<>(100);
        int[] starts = new int[] {0, 200000};
        int[] ends = new int[] {200000, 400000};
        int[] producerSleeps = new int[] {10, 10};
        int[] consumerSleeps = new int[] {5, 5, 5};
        /*
        int[] starts = new int[] {0, 200000, 400000, 600000, 800000};
        int[] ends = new int[] {200000, 400000, 600000, 800000, 1000000};
        int[] producerSleeps = new int[] {10, 10, 10, 10, 10};
        int[] consumerSleeps = new int[] {5, 5, 5};
        */
        multipleProducersMultipleConsumersTest(starts, ends, producerSleeps, consumerSleeps, fifo);
    }

    private static void testAtomicReferenceToMutableObject() {
        System.out.println("Test atomic reference to mutable object");
        System.out.println("====================================");

        StringBuilder b1 = new StringBuilder("builder1");
        StringBuilder b2 = new StringBuilder("builder2");

        AtomicReference<StringBuilder> atomicReference = new AtomicReference<>(b1);
        StringBuilder b = atomicReference.get();
        System.out.println("atomic reference to: " + b + " (" + System.identityHashCode(b) + ")");
        if (atomicReference.compareAndSet(b1, b2)) {
            b = atomicReference.get();
            System.out.println("cas(b1, b2) is success. atomic reference to: " +
                    b + " (" + System.identityHashCode(b) + ")");
        } else {
            b = atomicReference.get();
            System.out.println("cas(b1, b2) is failure. atomic reference to: " +
                    b + " (" + System.identityHashCode(b) + ")");
        }

        b2.append(" modified!");
        b = atomicReference.get();
        System.out.println("atomic reference to: " + b + " (" + System.identityHashCode(b) + ")");
        if (atomicReference.compareAndSet(b2, b1)) {
            //CAS succeeds even though the object being referred to has changed.
            b = atomicReference.get();
            System.out.println("cas(b2, b1) is success. atomic reference to: " +
                    b + " (" + System.identityHashCode(b) + ")");
        } else {
            b = atomicReference.get();
            System.out.println("cas(b2, b1) is failure. atomic reference to: " +
                    b + " (" + System.identityHashCode(b) + ")");
        }
    }

    private static void testAtomicBasicType() {
        System.out.println("Test atomic basic type");
        System.out.println("====================================");

        AtomicInteger atomicInteger = new AtomicInteger(5);

        //CAS1: 5 --> 6
        int beforeCAS1 = atomicInteger.get();
        if (atomicInteger.compareAndSet(5, 6)) {
            System.out.println("cas(5, 6) is success. new value: " + atomicInteger.get());
        } else {
            System.out.println("cas(5, 6) is failure. old value: " + atomicInteger.get());
        }

        //CAS2: 5 --> 4
        if (atomicInteger.compareAndSet(beforeCAS1, 4)) {
            System.out.println("cas(5, 4) is success. new value: " + atomicInteger.get());
        } else {
            System.out.println("cas(5, 4) is failure. old value: " + atomicInteger.get());
        }

        //CAS3: 6 --> 5
        if (atomicInteger.compareAndSet(6, 5)) {
            System.out.println("cas(6, 5) is success. new value: " + atomicInteger.get());
        } else {
            System.out.println("cas(6, 5) is failure. old value: " + atomicInteger.get());
        }

        //CAS4: 5 --> 4
        if (atomicInteger.compareAndSet(beforeCAS1, 4)) {
            //ABA
            System.out.println("cas(5, 4) is success. new value: " + atomicInteger.get());
        } else {
            System.out.println("cas(5, 4) is failure. old value: " + atomicInteger.get());
        }
    }

    public static void main(String[] args) {
        testAtomicReferenceToMutableObject();
        testAtomicBasicType();
        testLockFreeCircularFifo();
    }
}
