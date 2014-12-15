package in.blogspot.freemind_subwaywall.everything_else;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import java.lang.management.ManagementFactory;

//reader/writer locks using wait()/notify() and synchronized blocks
public class ReaderWriterLock {
    private int numReaders;
    private int numWriters;
    private ContentionPolicy policy;
    private int readsPending;
    private int writesPending;

    public static interface ContentionPolicy {
        public boolean shouldReaderWait(ReaderWriterLock lock);
        public boolean shouldWriterWait(ReaderWriterLock lock);
    }

    public static class BasicPolicy implements ContentionPolicy {
        public boolean shouldReaderWait(ReaderWriterLock lock) {
            return (lock.numWriters > 0);
        }

        public boolean shouldWriterWait(ReaderWriterLock lock) {
            return ((lock.numReaders > 0) || (lock.numWriters > 0));
        }
    }

    public static class WriteOverReadPolicy implements ContentionPolicy {
        public boolean shouldReaderWait(ReaderWriterLock lock) {
            return ((lock.numWriters > 0) || (lock.writesPending > 0));
        }

        public boolean shouldWriterWait(ReaderWriterLock lock) {
            return ((lock.numReaders > 0) || (lock.numWriters > 0));
        }
    }

    public ReaderWriterLock(ContentionPolicy policy) {
        this.numReaders = 0;
        this.numWriters = 0;
        this.policy = policy;
        this.readsPending = 0;
        this.writesPending = 0;
    }

    public void readLock() {
        synchronized(this) {
            readsPending++;
            while (policy.shouldReaderWait(this)) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    System.err.println("readLock(): wait interrupted");
                }
            }
            readsPending--;
            numReaders++;
        }
    }

    public void readUnlock() {
        synchronized(this) {
            numReaders--;
            if ((readsPending > 0) || (writesPending > 0)) {
                this.notifyAll();
            }
        }
    }

    public void writeLock() {
        synchronized(this) {
            writesPending++;
            while (policy.shouldWriterWait(this)) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    System.err.println("writeLock(): wait interrupted");
                }
            }
            writesPending--;
            numWriters++;
        }
    }

    public void writeUnlock() {
        synchronized(this) {
            numWriters--;
            if ((readsPending > 0) || (writesPending > 0)) {
                this.notifyAll();
            }
        }
    }

    private static class ReaderWriterLockTest {
        private static final int MAX_SLEEP_TIME = 1000; // in ms

        private List<LockThread> readerList;
        private List<LockThread> writerList;

        private static class LockThread extends Thread {
            private Random generator;
            private ReaderWriterLock lock;
            private AtomicBoolean testRunning;
            private boolean isReader;

            LockThread(ReaderWriterLock lock, boolean isReader) {
                super();
                this.lock = lock;
                this.isReader = isReader;
                generator = new Random();
                testRunning = new AtomicBoolean(true);
            }

            public void run() {
                String type = (isReader) ? "reader" : "writer";
                while (testRunning.get()) {
                    randomSleep(generator);

                    System.out.println("tid: " + getId() + ", " + type + ", before lock, lock.numReaders: " + lock.numReaders + ", lock.numWriters: " + lock.numWriters + ", lock.writesPending: " + lock.writesPending + ", lock.readsPending: " + lock.readsPending);
                    if (isReader) {
                        lock.readLock();
                    } else {
                        lock.writeLock();
                    }
                    System.out.println("tid: " + getId() + ", " + type + ", after lock, lock.numReaders: " + lock.numReaders + ", lock.numWriters: " + lock.numWriters + ", lock.writesPending: " + lock.writesPending + ", lock.readsPending: " + lock.readsPending);

                    randomSleep(generator);

                    System.out.println("tid: " + getId() + ", " + type + ", before unlock, lock.numReaders: " + lock.numReaders + ", lock.numWriters: " + lock.numWriters + ", lock.writesPending: " + lock.writesPending + ", lock.readsPending: " + lock.readsPending);
                    if (isReader) {
                        lock.readUnlock();
                    } else {
                        lock.writeUnlock();
                    }
                    System.out.println("tid: " + getId() + ", " + type + ", after unlock, lock.numReaders: " + lock.numReaders + ", lock.numWriters: " + lock.numWriters + ", lock.writesPending: " + lock.writesPending + ", lock.readsPending: " + lock.readsPending);
                }
            }

            public void kill() {
                testRunning.set(false);
            }
        }

        public ReaderWriterLockTest(int readers, int writers, ContentionPolicy policy) {
            ReaderWriterLock lock = new ReaderWriterLock(policy);

            readerList = new ArrayList<>(readers);
            for (int i = 0; i < readers; i++) {
                readerList.add(new LockThread(lock, true));
            }

            writerList = new ArrayList<>(writers);
            for (int i = 0; i < writers; i++) {
                writerList.add(new LockThread(lock, false));
            }
        }

        public void start() {
            for (LockThread t: readerList) {
                t.start();
            }

            for (LockThread t: writerList) {
                t.start();
            }
        }

        public void stop() {
            for (LockThread t: readerList) {
                t.kill();
            }

            for (LockThread t: writerList) {
                t.kill();
            }
        }

        public void report() {
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

        private static void randomSleep(Random generator) {
            int sleepTime = generator.nextInt();
            if (sleepTime < 0) {
                sleepTime *= -1;
            }
            sleepTime = (sleepTime % MAX_SLEEP_TIME) + 1;
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                System.err.println("random sleep interrupted");
            }
        }
    }

    public static void main(String[] args) {
        final int TEST_DURATION = 5000; //in ms

        if (args.length < 2) {
            throw new RuntimeException("Please pass arguments: <numReaders> <numWriters>");
        }

        int numReaders = Integer.parseInt(args[0]);
        int numWriters = Integer.parseInt(args[1]);

        System.out.println("Test with basic contention policy");
        ReaderWriterLockTest test = new ReaderWriterLockTest(numReaders, numWriters, new BasicPolicy());
        test.start();
        try {
            Thread.sleep(TEST_DURATION);
        } catch (InterruptedException e) {
            System.err.println("test interrupted...");
        }
        test.report();
        test.stop();

        System.out.println("Test with write-over-read contention policy");
        test = new ReaderWriterLockTest(numReaders, numWriters, new WriteOverReadPolicy());
        test.start();
        try {
            Thread.sleep(TEST_DURATION);
        } catch (InterruptedException e) {
            System.err.println("test interrupted...");
        }
        test.report();
        test.stop();
    }
}
