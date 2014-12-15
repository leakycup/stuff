package in.blogspot.freemind_subwaywall.everything_else;

public interface Cache<K, V> {
    public V get(K key);
    public void put(K key, V value, long timeout);
}
