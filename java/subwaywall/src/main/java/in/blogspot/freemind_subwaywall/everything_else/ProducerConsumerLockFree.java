package in.blogspot.freemind_subwaywall.everything_else;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by soubhik on 14-08-2016.
 */
public class ProducerConsumerLockFree {
    public static class LockFreeCircularFifo<T> {
        // thread unsafe BufferedItem implementation. at most one thread can have access to a BufferedItem.
        // use of the atomic variables in this implementation is to implement a memory barrier:
        // after a thread has updated a buffered item, a different thread should be able to see the updated value.
        private static class BufferedItem <T> {
            final AtomicReference<T> item;
            final AtomicBoolean isEmpty;
            final AtomicLong accessCount; //TODO: sbh: is this needed? why?

            BufferedItem() {
                item = new AtomicReference<>();
                isEmpty = new AtomicBoolean(true);
                accessCount = new AtomicLong(0);
            }

            void add(T item) {
                this.item.set(item);
                accessCount.getAndIncrement();
                isEmpty.set(false);
            }

            T remove() {
                if (isEmpty.compareAndSet(false, true) == false) {
                    return null;
                }
                accessCount.getAndIncrement();
                return item.get();
            }
        }

        private final AtomicReference<T>[] buffer;
        // atomic reference to immutable objects seems to be the only sane way to use the Atomic* types in Java.
        // since the object being referred to is immutable, a reference check is sufficient to determine
        // if it has changed. ABA problem can still happen if two different objects end up with same reference (at
        // different times). however, this is going to be rare.
        private final AtomicReference<Integer> head;
        private final AtomicReference<Integer> tail;

        public LockFreeCircularFifo(int size) {
            assert (size > 0);

            this.buffer = new AtomicReference[size];
            this.head = new AtomicReference<>(new Integer(0));
            this.tail = new AtomicReference<>(new Integer(0)); //TODO: sbh: make it AtomicInteger: no CAS on tail, hence no ABA problem
        }

        public boolean insert(T item) {
            int currentTail = tail.get();
            int nextTail = (currentTail + 1) % buffer.length;
            // if the following CAS fails, it does not necessarily mean the buffer is full.
            // it's possible that another thread has won the race and inserted an item in the slot buffer[currentTail].
            if (buffer[currentTail].compareAndSet(null, item)) {
                // 1. if a producer thread preempts the current thread at this point, it's going to find that
                // buffer[currentTail] is not empty and the above CAS will fail for it, even though
                // the buffer is not full. we can fixing this and simplify the body of this function by ensuring that
                // at most one thread can be executing the body of this function. this can be achieved by spinlocking
                // on an atomic boolean at the beginning of this function. this, does not prevent concurrent
                // execution of the insert() and remove() though.
                // 2. if a consumer thread preempts the current thread at this point, and happens to consume the item
                // from buffer[currentTail], then gets preempted by a different producer thread, the producer will
                // find that the buffer[currentTail] is empty, will insert an item at this position. it can then
                // insert more items and keep advancing the tail. eventually, when the control returns to the current
                // thread, it will set the tail to a stale value, an index where an item has already been inserted by
                // the 2nd producer. doing a CAS rather than set() will avoid this problem.
                // see http://codereview.stackexchange.com/a/138746/115067
                tail.set(nextTail);
                return true;
            }

            return false;
        }

        public T remove() {
            int currentHead = head.get();
            int nextHead = (currentHead + 1) % buffer.length;
            T item = buffer[currentHead].get();
            if (buffer[currentHead].compareAndSet(item, null)) {
                if (item != null) {
                    head.set(nextHead);
                    return item;
                }
            }

            return null;
        }
    }

    private static void testAtomicReferenceToMutableObject() {
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
    }
}
