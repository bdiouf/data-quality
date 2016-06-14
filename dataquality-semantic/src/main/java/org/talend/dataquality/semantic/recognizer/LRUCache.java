package org.talend.dataquality.semantic.recognizer;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> extends LinkedHashMap<K, V> {

    private static final long serialVersionUID = 5505698421201224840L;

    private int cacheSize;

    public LRUCache(int cacheSize) {
        super(cacheSize);
        this.cacheSize = cacheSize;
    }

    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > cacheSize;
    }
}