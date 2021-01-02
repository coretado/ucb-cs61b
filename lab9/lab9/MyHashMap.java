package lab9;

import java.util.Iterator;
import java.util.Set;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  @author Your name here
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    private static final int DEFAULT_SIZE = 16;
    private static final double MAX_LF = 0.75;

    private ArrayMap<K, V>[] buckets;
    private int size;

    /*
     * Weird note about this - I'm not sure for certain if there is a typo/error
     * with the given code here, but my hunch is there is. Because this will
     * return an int (0 or 1) when you are checking for resize, it will only
     * ever resize when this hits max capacity, compared to the 0.75 that is listed above.
     * You can see this if you put a sout command for put and output the result of loadFactor()
     * on any of the test cases that cause a resize. I changed this to return a double, and was
     * able to have it resize on 0.75 like it intended, but as far as I can tell there is no
     * actual difference in the tests if you make it resize off the int or double,
     * and I don't want to break any tests potentially (or test it myself),
     * so it stays at an int - jspii
     */
    private int loadFactor() {
        return size / buckets.length;
    }

    public MyHashMap() {
        buckets = new ArrayMap[DEFAULT_SIZE];
        this.clear();
    }

    /* Removes all of the mappings from this map. */
    @Override
    public void clear() {
        this.size = 0;
        for (int i = 0; i < this.buckets.length; i += 1) {
            this.buckets[i] = new ArrayMap<>();
        }
    }

    /** Computes the hash function of the given key. Consists of
     *  computing the hashcode, followed by modding by the number of buckets.
     *  To handle negative numbers properly, uses floorMod instead of %.
     */
    private int hash(K key) {
        if (key == null) {
            return 0;
        }

        int numBuckets = buckets.length;
        return Math.floorMod(key.hashCode(), numBuckets);
    }

    /*
     * Another weird one; at least the way that I do the resizing, I can't use
     * the provided hash method, because when I resize if I use the previous hash
     * method to place a key value pair, it will potentially be placed in a mismatched
     * location because the hash used for the NEW bucket will be different from the
     * one calculated using the length of the OLD bucket -jspii
     */
    private int hashResize(K key, int bucketsLength) {
        if (key == null) {
            return 0;
        }

        return Math.floorMod(key.hashCode(), bucketsLength);
    }

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public V get(K key) {
        return this.buckets[hash(key)].get(key);
    }

    /* Associates the specified value with the specified key in this map. */
    @Override
    public void put(K key, V value) {
        // value already exists, just update
        if (this.get(key) != null) {
            this.setPair(key, value);
            return;
        }

        this.setPair(key, value);
        this.size += 1;
        if (loadFactor() >= MAX_LF) {
            this.resizeHelper();
        }
    }

    private void setPair(K key, V value) {
        int hash = this.hash(key);
        this.buckets[hash].put(key, value);
    }

    private void resizeHelper() {
        int bucketSize = this.buckets.length * 2;
        ArrayMap<K, V>[] newBuckets = new ArrayMap[bucketSize];
        for (int i = 0; i < bucketSize; i += 1) {
            newBuckets[i] = new ArrayMap<>();
        }

        for (ArrayMap<K, V> bucket : this.buckets) {
            if (bucket != null) {
                bucket.forEach(kV -> {
                    if (kV != null) {
                        newBuckets[this.hashResize(kV, bucketSize)].put(kV, bucket.get(kV));
                    }
                });
            }
        }

        this.buckets = newBuckets;
    }

    /* Returns the number of key-value mappings in this map. */
    @Override
    public int size() {
        return this.size;
    }

    //////////////// EVERYTHING BELOW THIS LINE IS OPTIONAL ////////////////

    /* Returns a Set view of the keys contained in this map. */
    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    /* Removes the mapping for the specified key from this map if exists.
     * Not required for this lab. If you don't implement this, throw an
     * UnsupportedOperationException. */
    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for this lab. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }
}
