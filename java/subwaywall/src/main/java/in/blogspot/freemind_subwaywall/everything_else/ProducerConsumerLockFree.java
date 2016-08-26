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
        private final AtomicReferenceArray<T> buffer;
        private final AtomicReferenceArray<Boolean> watches;
        private final int size;
        // atomic reference to immutable objects seems to be the only sane way to use the Atomic* types in Java.
        // since the object being referred to is immutable, a reference check is sufficient to determine
        // if it has changed. ABA problem can still happen if two different objects end up with same reference (at
        // different times). however, this is going to be rare. on the other hand, AtomicInteger can easily cause ABA
        // problem if the indexes wrap around the buffer.
        private final AtomicReference<Integer> head;
        private final AtomicReference<Integer> tail;

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
            this.watches = new AtomicReferenceArray<Boolean>(size);
            for (int i = 0; i < size; i++) {
                watches.set(i, Boolean.FALSE);
            }
            this.size = size;
            this.head = new AtomicReference<>(new Integer(0));
            this.tail = new AtomicReference<>(new Integer(0));
            this.debugInfo = new ThreadLocal<DebugInfo>() {
                @Override
                protected DebugInfo initialValue() {
                    return new DebugInfo();
                }
            };
        }

        public void insert(T item) throws InterruptedException {
            assert (item != null);

            //reserve a slot in buffer[currentTail]. by advancing the tail index, we ensure that
            //no other producer thread tries to insert into buffer[currentTail].
            //the chance of an ABA problem is same as that of two different Integer objects
            //having the same reference. this is much better than the chance of an AtomicInteger having same
            //value after wrapping over the buffer. hence we choose AtomicReference<Integer> rather than AtomicInteger
            //for tail.
            Integer currentTail = tail.get();
            int nextTail = (currentTail + 1) % size;
            while (!tail.compareAndSet(currentTail, nextTail)) {
                currentTail = tail.get();
                nextTail = (currentTail + 1) % size;
            }
            debugInfo.get().add("insert(): currentTail: " + currentTail + " nextTail: " + nextTail);

            //at this point, a race between two producers is not possible. however, it is possible that a
            //single producer and a single consumer thread attempts to access the same slot in buffer.
            //watch is used to synchronize between the single producer and single consumer threads so that
            //they both don't wait for each other. the thread that succeeds in CAS gets the right to wait() while
            //the other thread busy waits in a loop. e.g. if a producer succeeds in CAS and finds buffer[currentTail]
            //is full, it calls wait(), waiting for a consumer to consume the item and wake it up. on the other hand,
            //if it fails to CAS (implying, a consumer is already in action), it busy waits till buffer[currentTail]
            //is empty. the busy wait should be short since a consumer is already working on buffer[currentTail].
            //finally, the producer needs to wake up a consumer that is possibly waiting for an item to be available
            //in buffer[currentTail].
            //caveat: a producer can be interrupted or can otherwise die while waiting. however, since it has already
            //reserved the slot buffer[currentTail], no other producer can insert an item in this slot till the buffer
            //wraps around. so a consumer waiting for this slot to fill up may wait longer than expected.
            //
            // the current implementation works with the following 2 assumptions / claims:
            // 1. when we're here, a race between two producers is impossible. this is wrong. while one producer is
            // is in wait(), another producer can wrap the buffer around and come to this slot. in this case, it fails
            // to CAS the watch, falls in the else block and keeps trying to CAS in a tight loop. both the producers
            // (in fact, it may be more than 2 producers) can be in this state till a consumer visits this slot and
            // empties it.
            // 2. if a producer is waiting, the queue must be full and consumers are not waiting. this also turned out
            // to be wrong in multipleProducerMultipleConsumerTest(). the test gets stuck as producers and consumers
            // end up waiting on different slots (producers waiting for consumers to empty their slots, consumers
            // waiting for the producers to fill up their slots).
            if (watches.compareAndSet(currentTail, Boolean.FALSE, Boolean.TRUE)) {
                if (buffer.compareAndSet(currentTail, null, item)) {
                    watches.set(currentTail, Boolean.FALSE);
                    debugInfo.get().add("insert(): currentTail: " + currentTail + " inserted: " + item);
                } else {
                    debugInfo.get().add("insert(): currentTail: " + currentTail + " slot full.");
                    Boolean watch = watches.get(currentTail);
                    synchronized (watch) {
                        debugInfo.get().add("insert(): currentTail: " + currentTail + " locked watch.");
                        while (!buffer.compareAndSet(currentTail, null, item)) {
                            debugInfo.get().add("insert(): waiting: index: " + currentTail + " value: " + item);
                            watch.wait();
                            debugInfo.get().add("insert(): finished waiting: index: " + currentTail + " value: " + item);
                        }
                    }
                    watches.set(currentTail, Boolean.FALSE);
                }
            } else {
                debugInfo.get().add("insert(): currentTail: " + currentTail + " lost CAS to consumer.");
                while (!buffer.compareAndSet(currentTail, null, item));
                debugInfo.get().add("insert(): currentTail: " + currentTail + " inserted item after losing CAS.");
                Boolean watch = watches.get(currentTail);
                synchronized (watch) {
                    debugInfo.get().add("insert(): currentTail: " + currentTail + " locked watch for notify.");
                    watch.notify();
                }
            }
        }

        public T remove() throws InterruptedException {
            Integer currentHead = head.get();
            int nextHead = (currentHead + 1) % size;
            while (!head.compareAndSet(currentHead, nextHead)) {
                currentHead = head.get();
                nextHead = (currentHead + 1) % size;
            }
            debugInfo.get().add("remove(): currentHead: " + currentHead + " nextHead: " + nextHead);

            //caveat: a consumer can be interrupted or can otherwise die while waiting. however, since it has already
            //reserved the slot buffer[currentTail], no other consumer can remove an item from this slot till the buffer
            //wraps around. this means the item in this slot may be consumed late and out of insertion order.
            if (watches.compareAndSet(currentHead, Boolean.FALSE, Boolean.TRUE)) {
                T item = buffer.get(currentHead);
                if (item != null) {
                    buffer.set(currentHead, null);
                    watches.set(currentHead, Boolean.FALSE);
                    debugInfo.get().add("remove(): currentHead: " + currentHead + " removed item.");
                    return item;
                } else {
                    debugInfo.get().add("remove(): currentHead: " + currentHead + " slot empty.");
                    Boolean watch = watches.get(currentHead);
                    synchronized (watch) {
                        debugInfo.get().add("remove(): currentHead: " + currentHead + " locked watch.");
                        while ((item = buffer.get(currentHead)) == null) {
                            debugInfo.get().add("remove(): waiting: index: " + currentHead + " value: " + item);
                            watch.wait();
                            debugInfo.get().add("remove(): finished waiting: index: " + currentHead + " value: " + item);
                        }
                    }
                    buffer.set(currentHead, null);
                    watches.set(currentHead, Boolean.FALSE);
                    return item;
                }
            } else {
                T item;
                debugInfo.get().add("remove(): currentHead: " + currentHead + " lost CAS to producer.");
                while ((item = buffer.get(currentHead)) == null);
                debugInfo.get().add("remove(): currentHead: " + currentHead + " removed item after losing CAS.");
                Boolean watch = watches.get(currentHead);
                synchronized (watch) {
                    debugInfo.get().add("remove(): currentHead: " + currentHead + " locked watch for notify.");
                    watch.notify();
                }

                return item;
            }
        }

        public void printDebug() {
            debugInfo.get().print();
        }
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

                try {
                    fifo.insert(next);
                } catch (InterruptedException e) {
                    break;
                }
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
                    " produced " + items + " integers from " + start + " to " + (next-1));
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

                int n;
                try {
                    n = fifo.remove();
                } catch (InterruptedException e) {
                    break;
                }
                numbers.add(n);

                items++;
                System.err.println("consumed n: " + n);
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
            System.err.println("Consumer: " + id + ", thread Id: " + tid + " consumed " + items + " items");
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
