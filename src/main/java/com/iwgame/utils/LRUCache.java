/**
 * 
 */
package com.iwgame.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An LRU cache, based on LinkedHashMap. This class is thread-safe. All methods
 * of this class are synchronized.
 */
public class LRUCache<K, V> {

	private static final float hashTableLoadFactor = 0.75f;

	private LinkedHashMap<K, V> map;

	private int cacheSize;

	public LRUCache(int cacheSize) {
		this.cacheSize = cacheSize;
		int hashTableCapacity = (int) Math
				.ceil(cacheSize / hashTableLoadFactor) + 1;
		map = new LinkedHashMap<K, V>(hashTableCapacity, hashTableLoadFactor,
				true) {
			private static final long serialVersionUID = 1;

			@Override
			protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
				boolean shouldRemove = size() > LRUCache.this.cacheSize;
				if (shouldRemove && recycler != null)
					recycler.recycle(eldest.getValue());
				return shouldRemove;
			}
		};
	}

	public synchronized void setRecycler(Recycler<V> recycler) {
		this.recycler = recycler;

	}

	private Recycler<V> recycler = null;

	public static interface Recycler<V> {
		void recycle(V object);
	}

	public synchronized V get(K key) {
		return map.get(key);
	}

	public synchronized void remove(K key) {
		V v = map.get(key);
		if (recycler != null && v != null) {
			recycler.recycle(v);
		}
		map.remove(key);
	}

	public synchronized boolean containsKey(K key) {
		return map.containsKey(key);

	}

	public synchronized void put(K key, V value) {
		if (recycler != null) {
			V old = map.get(key);
			if (old != null && old != value) {
				recycler.recycle(old);
			}
		}

		map.put(key, value);
	}

	public synchronized void clear() {
		if (recycler != null) {
			Collection<V> values = map.values();
			if (values != null) {
				for (V v : values) {
					recycler.recycle(v);
				}
			}
		}

		map.clear();
	}

	public synchronized int usedEntries() {
		return map.size();
	}

	public synchronized Collection<Map.Entry<K, V>> getAll() {
		return new ArrayList<Map.Entry<K, V>>(map.entrySet());
	}

}
