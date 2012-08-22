package vinna.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MultivaluedHashMap<K, V> extends HashMap<K, List<V>> {

    public void add(K key, V value) {
        List<V> list = this.get(key);
        list.add(value);
        this.put(key, list);
    }

    public V getFirst(K key) {
        List<V> list = this.get(key);
        return list.size() > 0 ? list.get(0) : null;
    }

    public void putSingle(K key, V value) {
        List<V> list = new ArrayList<>();
        list.add(value);
        this.put(key, list);
    }

    public boolean remove(K key, V value) {
        return this.get(key).remove(value);
    }

    @Override
    public List<V> get(Object key) {
        List<V> list = super.get(key);
        return list != null ? list : new ArrayList<V>();
    }

    @Override
    public boolean containsKey(Object key) {
        return !this.get(key).isEmpty();
    }
}
