package in.blogspot.freemind_subwaywall.everything_else;

public class LruCache implements Cache<K, V> {
    private static class Entry {
       final K key;
       final  V value;
       final long expiryTime;
       Entry next;
       Entry previous;

       Entry(K k, V v, long expTime) {
           key = k;
           value = v;
           expiryTime = expTime;
       }
    }

    private static class EntryList {
        Entry head;
        Entry tail;

        void addAtHead(Entry e) {
            Entry h = head;
            head = e;
            e.next = h;
            e.previous = null;
            if (h != null) {
                h.previous = e;
            } else {
                tail = e;
            }
        }

        void remove(Entry e) {
            Entry p = e.previous;
            Entry n = e.next;

            if (p != null) {
                p.next = n;
            }
            if (n != null) {
                n.previous = p;
            }

            if (tail == e) {
                tail = p;
            }

            e.previous = e.next = null;
        }

        Entry getFromTail() {
            return (tail);
        }
    }

    private static class TimeoutCommand implements Runnable {
        void run() {
            while (!lock.compareAndSet(false, true));
            long now = System.nanoTime();
            while (true) {
                Entry tail = timeoutList.getFromTail();
                if (tail == null) {
                    break;
                }
                long expiryTime = tail.expiryTime;
                if (expiryTime > now) {
                    timeoutList.remove(tail);
                    accessList.remove(tail);
                    cache.remove(e.key);
                } else {
                    break;
                }
            }
            lock.set(false);
        }
    }

    private final AtomicBoolean lock;
    private final Map<K, Entry> cache;
    private int capacity;
    private final long timeout;
    private final EntryList accessList;
    private final EntryList timeoutList;
    private final ScheduledExecutorService timeoutHandler;
    private final timeoutCommand;

    public LruCache(int capacity, long timeout) {
        this.capacity = capacity;
        this.timeout = timeout;

        lock = new AtomocBoolean(false);
        cache = new HashMap<>();
        accessList = new EntryList();
        timeoutList = new EntryList();
        timeoutHandler = Executors.newSingleThreadScheduledExecutor();
        timeoutCommand = new TimeoutCommand();
    }

    public V get(K key) {
        while (!lock.compareAndSet(false, true));

        if (!cache.containsKey(key)) {
            lock.set(false);
            return (null);
        }
        Entry e = cache.get(key);
        V value = e.value;

        accessList.remove(e);
        accessList.addAtHead(e);

        lock.set(false);

        return (value);
    }

    public void put(K key, V value, long timeout) {
        while (!lock.compareAndSet(false, true));

        if (cache.containsKey(key)) {
            Entry e = cache.get(key);
            accessList.remove(e);
            timeoutList.remove(e);
        }

        long expiryTime = System.nanoTime() + timeout;
        Entry e = new Entry(key, value, expiryTime);
        cache.put(e);
        accessList.addAtHead(e);
        timeoutList.addAtHead(e);

        timeoutHandler.schedule(timeoutCommand, timeout, TimeUnit.NANOSECONDS);

        lock.set(false);
    }
}
