package me.soubhik;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.ScanResult;

import java.io.Closeable;
import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Created by soubhik on 6/17/16.
 */
public class RedisClient {
    public static class RedisSet extends AbstractSet<String> implements Closeable {
        private static final JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
        private final Jedis jedis;
        private final String identity;

        public RedisSet() {
            this.jedis = pool.getResource();
            this.identity = this.getClass().getSimpleName() + ":" + Integer.toString(System.identityHashCode(this));
        }

        @Override
        public void close() {
            jedis.close();
            Set<String> keys = jedis.keys(identity + ":*");
            jedis.del(keys.toArray(new String[1]));
        }

        @Override
        public int size() {
            return (int)jedis.dbSize().intValue();
        }

        @Override
        public boolean add(String item) {
            String key = "setElement:" + identity + ":" + item;
            boolean ret = !jedis.exists(key);
            jedis.set(key, item);
            return ret;
        }

        @Override
        public Iterator<String> iterator() {
            return new RedisSetIterator(jedis, identity);
        }
    }

    public static class RedisSetIterator implements Iterator<String>, Closeable {
        private final Jedis jedis;
        private final String identity;
        private final String parentIdentity;
        private String cursor;
        private Iterator<String> items;
        private String last;

        public RedisSetIterator(Jedis jedis, String identity) {
            this.jedis = jedis;
            this.parentIdentity = identity;

            this.cursor = "0";
            do {
                ScanResult<String> scanResult = jedis.scan(this.cursor);
                this.cursor = scanResult.getStringCursor();
                Set<String> itemSet = new HashSet(scanResult.getResult());
                Iterator<String> itemIterator = itemSet.iterator();
                while (itemIterator.hasNext()) {
                    String i = itemIterator.next();
                    if (!i.startsWith("setElement:" + parentIdentity)) {
                        itemIterator.remove();
                    }
                }
                this.items = itemSet.iterator();
            } while (!items.hasNext() && !"0".equals(this.cursor));

            this.identity = identity + ":" + Integer.toString(System.identityHashCode(this));
        }

        @Override
        public boolean hasNext() {
            return (items.hasNext());
        }

        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            String item = items.next();
            jedis.set(identity + ":" + item, "");
            last = item;

            while (!items.hasNext() && !"0".equals(cursor)) {
                ScanResult<String> scanResult = jedis.scan(cursor);
                cursor = scanResult.getStringCursor();
                Set<String> itemSet = new HashSet(scanResult.getResult());
                Iterator<String> itemIterator = itemSet.iterator();
                while (itemIterator.hasNext()) {
                    String i = itemIterator.next();
                    if (!i.startsWith("setElement:" + parentIdentity)) {
                        itemIterator.remove();
                    }
                    if (jedis.exists(identity + ":" + i)) {
                        itemIterator.remove();
                    }
                }
                items = itemSet.iterator();
            }

            if (!hasNext()) {
                close();
            }

            return jedis.get(item);
        }

        @Override
        public void remove() {
            if (last != null) {
                jedis.del(last);
            } else {
                throw new NoSuchElementException();
            }
        }

        @Override
        public void close() {
            Set<String> keys = jedis.keys(identity + ":*");
            jedis.del(keys.toArray(new String[1]));
        }
    }

    public static void main(String[] args) {
        RedisSet mangoes = new RedisSet();
        mangoes.add("mallika");
        mangoes.add("imam pasand");
        mangoes.add("him sagar");
        mangoes.add("mallika");
        for (String mango: mangoes) {
            System.out.println("mango: " + mango);
        }

        RedisSet oranges = new RedisSet();
        for (String orange: oranges) {
            System.out.println("orange: " + orange);
        }

        JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            //jedis.set("foo", "bar");
            //System.out.println("redis: key: foo, value: " + jedis.get("foo"));
            String cursor = "0";
            do {
                ScanResult<String> scanResult = jedis.scan(cursor);
                cursor = scanResult.getStringCursor();
                List<String> items = scanResult.getResult();
                for (String item: items) {
                    System.out.println("item: " + item);
                }
            } while (!"0".equals(cursor));
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        pool.destroy();
    }
}
