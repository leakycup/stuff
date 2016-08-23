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
            static final int DEBUG_INFO_LENGTH = 100;
            int index = 0;
            String[] debugList = new String[DEBUG_INFO_LENGTH];

            void add(String s) {
                debugList[index] = s;
                index = (index + 1) % DEBUG_INFO_LENGTH;
            }

            void print() {
                long tid = Thread.currentThread().getId();
                for (String s: debugList) {
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
            if (watches.compareAndSet(currentTail, Boolean.FALSE, Boolean.TRUE)) {
                if (buffer.compareAndSet(currentTail, null, item)) {
                    watches.set(currentTail, Boolean.FALSE);
                } else {
                    Boolean watch = watches.get(currentTail);
                    synchronized (watch) {
                        while (!buffer.compareAndSet(currentTail, null, item)) {
                            watch.wait();
                        }
                    }
                    watches.set(currentTail, Boolean.FALSE);
                }
            } else {
                while (!buffer.compareAndSet(currentTail, null, item));
                Boolean watch = watches.get(currentTail);
                synchronized (watch) {
                    watch.notify();
                }
            }

            debugInfo.get().add("insert(): index: " + currentTail + " value: " + item);
        }

        public T remove() throws InterruptedException {
            Integer currentHead = head.get();
            int nextHead = (currentHead + 1) % size;
            while (!head.compareAndSet(currentHead, nextHead)) {
                currentHead = head.get();
                nextHead = (currentHead + 1) % size;
            }

            //caveat: a consumer can be interrupted or can otherwise die while waiting. however, since it has already
            //reserved the slot buffer[currentTail], no other consumer can remove an item from this slot till the buffer
            //wraps around. this means the item in this slot may be consumed late and out of insertion order.
            if (watches.compareAndSet(currentHead, Boolean.FALSE, Boolean.TRUE)) {
                T item = buffer.get(currentHead);
                if (item != null) {
                    buffer.set(currentHead, null);
                    watches.set(currentHead, Boolean.FALSE);
                    debugInfo.get().add("remove()#1: index: " + currentHead + " value: " + item);
                    return item;
                } else {
                    Boolean watch = watches.get(currentHead);
                    synchronized (watch) {
                        while ((item = buffer.get(currentHead)) == null) {
                            watch.wait();
                        }
                    }
                    buffer.set(currentHead, null);
                    watches.set(currentHead, Boolean.FALSE);
                    debugInfo.get().add("remove()#2: index: " + currentHead + " value: " + item);
                    return item;
                }
            } else {
                T item;
                while ((item = buffer.get(currentHead)) == null);
                Boolean watch = watches.get(currentHead);
                synchronized (watch) {
                    watch.notify();
                }

                debugInfo.get().add("remove()#3: index: " + currentHead + " value: " + item);
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
            System.err.println("produced " + items + " integers from " + start + " to " + (next-1));
            printDebug();
        }

        public void lazyStop() {
            this.stopNow.set(true);
        }

        private void printDebug() {
            long tid = getId();
            System.err.println("Producer: " + id + ", thread Id: " + tid);
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
            System.err.println("consumed " + items + " items");
            printDebug();
        }

        public void lazyStop() {
            this.stopNow.set(true);
        }

        @Override
        public Iterator<Integer> iterator() {
            return numbers.iterator();
        }

        private void printDebug() {
            long tid = getId();
            System.err.println("Consumer: " + id + ", thread Id: " + tid);
            fifo.printDebug();
            System.err.println(threadInfo(tid));
        }
    }

    private static void singleProducerSingleConsumerTest(int start, int end, int producerSleep, int consumerSleep,
                                                         LockFreeCircularFifo<Integer> fifo) {
        IntegerProducer producer = new IntegerProducer(start, end, producerSleep, fifo);
        IntegerConsumer consumer = new IntegerConsumer(consumerSleep, fifo);
        consumer.start();
        producer.start();
        try {
            producer.join();
            Thread.sleep(20);
        } catch (InterruptedException e) {
            System.out.println("Test lock free circular fifo (multi-threaded) ended abruptly!!");
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
