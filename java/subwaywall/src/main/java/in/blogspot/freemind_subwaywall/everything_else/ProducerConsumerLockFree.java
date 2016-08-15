package in.blogspot.freemind_subwaywall.everything_else;

import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * Created by soubhik on 14-08-2016.
 */
public class ProducerConsumerLockFree {
    public static class LockFreeCircularFifo<T> {
        private final AtomicReferenceArray<T> buffer;
        private final AtomicReferenceArray<Boolean> watches;
        private final int size;
        // atomic reference to immutable objects seems to be the only sane way to use the Atomic* types in Java.
        // since the object being referred to is immutable, a reference check is sufficient to determine
        // if it has changed. ABA problem can still happen if two different objects end up with same reference (at
        // different times). however, this is going to be rare.
        private final AtomicReference<Integer> head;
        private final AtomicReference<Integer> tail;

        public LockFreeCircularFifo(int size) {
            assert (size > 0);

            this.buffer = new AtomicReferenceArray<T>(size);
            this.watches = new AtomicReferenceArray<Boolean>(size);
            for (int i = 0; i < size; i++) {
                watches.set(i, Boolean.FALSE);
            }
            this.size = size;
            this.head = new AtomicReference<>(new Integer(0));
            this.tail = new AtomicReference<>(new Integer(0)); //TODO: sbh: make it AtomicInteger: no CAS on tail, hence no ABA problem
        }

        public void insert(T item) throws InterruptedException {
            assert (item != null);

            Integer currentTail = tail.get();
            int nextTail = (currentTail + 1) % size;
            while (!tail.compareAndSet(currentTail, nextTail)) {
                currentTail = tail.get();
                nextTail = (currentTail + 1) % size;
            }

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
        }

        public T remove() throws InterruptedException {
            Integer currentHead = head.get();
            int nextHead = (currentHead + 1) % size;
            while (!head.compareAndSet(currentHead, nextHead)) {
                currentHead = head.get();
                nextHead = (currentHead + 1) % size;
            }

            if (watches.compareAndSet(currentHead, Boolean.FALSE, Boolean.TRUE)) {
                T item = buffer.get(currentHead);
                if (item != null) {
                    buffer.set(currentHead, null);
                    watches.set(currentHead, Boolean.FALSE);
                    return item;
                } else {
                    Boolean watch = watches.get(currentHead);
                    synchronized (watch) {
                        while ((item = buffer.get(currentHead)) == null) {
                            watch.wait();
                        }
                    }
                    watches.set(currentHead, Boolean.FALSE);
                    return item;
                }
            } else {
                T item;
                while ((item = buffer.get(currentHead)) == null);
                Boolean watch = watches.get(currentHead);
                synchronized (watch) {
                    watch.notify();
                }

                return item;
            }
        }
    }

    private static class IntegerProducer extends Thread {
        private final int start;
        private final int end;
        private final int maxSleepTime;
        private final Random random;
        private final LockFreeCircularFifo<Integer> fifo;
        private AtomicBoolean stopNow;
        private int next;

        IntegerProducer(int start, int end, int maxSleepTime, LockFreeCircularFifo<Integer> fifo) {
            this.start = start;
            this.end = end;
            this.next = start;
            this.maxSleepTime = maxSleepTime;
            this.random = new Random();
            this.fifo = fifo;
            this.stopNow = new AtomicBoolean(false);
        }

        @Override
        public void run() {
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
                next++;
                if (next >= end) {
                    break;
                }
            }
            System.out.println("produced integers from " + start + " to " + (next-1));
        }

        public void lazyStop() {
            this.stopNow.set(true);
        }
    }

    private static class IntegerConsumer extends Thread implements Iterable<Integer> {
        private final int maxSleepTime;
        private final Random random;
        private final LockFreeCircularFifo<Integer> fifo;
        private final List<Integer> numbers;
        private AtomicBoolean stopNow;

        IntegerConsumer(int maxSleepTime, LockFreeCircularFifo<Integer> fifo) {
            this.maxSleepTime = maxSleepTime;
            this.random = new Random();
            this.fifo = fifo;
            this.numbers = new ArrayList<>();
            this.stopNow = new AtomicBoolean(false);
        }

        @Override
        public void run() {
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

                System.out.println("consumed n: " + n);
            }
        }

        public void lazyStop() {
            this.stopNow.set(true);
        }

        @Override
        public Iterator<Integer> iterator() {
            return numbers.iterator();
        }
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

        IntegerProducer producer = new IntegerProducer(2, 7, 10, fifo);
        IntegerConsumer consumer = new IntegerConsumer(0, fifo);
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
        for (int i = 2; i < 7; i++) {
            int n = results.next();
            assert (i == n);
        }
        assert (results.hasNext() == false);
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
