package com.wenruisong.basestationmap.basestation.Marker;

import com.wenruisong.basestationmap.basestation.Cell;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by wen on 2016/3/25.
 */
public class CellLruCache {

        public final LinkedHashMap<Cell, CellMarker> map;

        /** Size of this cache in units. Not necessarily the number of elements. */
        private int size;
        private int maxSize;

        private int putCount;
        private int createCount;
        private int evictionCount;
        private int hitCount;
        private int missCount;

        /**
         * @param maxSize for caches that do not override {@link #sizeOf}, this is
         *     the maximum number of entries in the cache. For all other caches,
         *     this is the maximum sum of the sizes of the entries in this cache.
         */
        public CellLruCache(int maxSize) {
            if (maxSize <= 0) {
                throw new IllegalArgumentException("maxSize <= 0");
            }
            this.maxSize = maxSize;
            this.map = new LinkedHashMap<Cell, CellMarker>(0, 0.75f, true);
        }

        /**
         * Returns the value for {@code key} if it exists in the cache or can be
         * created by {@code #create}. If a value was returned, it is moved to the
         * head of the queue. This returns null if a value is not cached and cannot
         * be created.
         */
        public final CellMarker get(Cell key) {
            if (key == null) {
                throw new NullPointerException("key == null");
            }

            CellMarker mapValue;
            synchronized (this) {
                mapValue = map.get(key);
                if (mapValue != null) {
                    hitCount++;
                    return mapValue;
                }
                missCount++;
            }

        /*
         * Attempt to create a value. This may take a long time, and the map
         * may be different when create() returns. If a conflicting value was
         * added to the map while create() was working, we leave that value in
         * the map and release the created value.
         */

            CellMarker createdValue = create(key);
            if (createdValue == null) {
                return null;
            }

            synchronized (this) {
                createCount++;
                mapValue = map.put(key, createdValue);

                if (mapValue != null) {
                    // There was a conflict so undo that last put
                    map.put(key, mapValue);
                } else {
                    size += safeSizeOf(key, createdValue);
                }
            }

            if (mapValue != null) {
                entryRemoved(false, key, createdValue, mapValue);
                return mapValue;
            } else {
                trimToSize(maxSize);
                return createdValue;
            }
        }

        /**
         * Caches {@code value} for {@code key}. The value is moved to the head of
         * the queue.
         *
         * @return the previous value mapped by {@code key}.
         */
        public final CellMarker put(Cell key, CellMarker value) {
            if (key == null || value == null) {
                throw new NullPointerException("key == null || value == null");
            }

            CellMarker previous;
            synchronized (this) {
                putCount++;
                size += safeSizeOf(key, value);
                previous = map.put(key, value);
                if (previous != null) {
                    size -= safeSizeOf(key, previous);
                }
            }

            if (previous != null) {
                entryRemoved(false, key, previous, value);
            }

            trimToSize(maxSize);
            return previous;
        }

        /**
         * @param maxSize the maximum size of the cache before returning. May be -1
         *     to evict even 0-sized elements.
         */
        public void trimToSize(int maxSize) {
            while (true) {
                Cell key;
                CellMarker value;
                synchronized (this) {
                    if (size < 0 || (map.isEmpty() && size != 0)) {
                        throw new IllegalStateException(getClass().getName()
                                + ".sizeOf() is reporting inconsistent results!");
                    }

                    if (size <= maxSize || map.isEmpty()) {
                        break;
                    }

                    Map.Entry<Cell, CellMarker> toEvict = map.entrySet().iterator().next();
                    key = toEvict.getKey();
                    value = toEvict.getValue();
                    key.isShow =false;
                    map.remove(key);
                    value.remove();
                    size -= safeSizeOf(key, value);
                    evictionCount++;
                }

                entryRemoved(true, key, value, null);
            }
        }

        /**
         * Removes the entry for {@code key} if it exists.
         *
         * @return the previous value mapped by {@code key}.
         */
        public final CellMarker remove(Cell key) {
            if (key == null) {
                throw new NullPointerException("key == null");
            }

            CellMarker previous;
            synchronized (this) {
                previous = map.remove(key);
                if (previous != null) {
                    size -= safeSizeOf(key, previous);
                }
            }

            if (previous != null) {
                entryRemoved(false, key, previous, null);
            }

            return previous;
        }

        /**
         * Called for entries that have been evicted or removed. This method is
         * invoked when a value is evicted to make space, removed by a call to
         * {@link #remove}, or replaced by a call to {@link #put}. The default
         * implementation does nothing.
         *
         * <p>The method is called without synchronization: other threads may
         * access the cache while this method is executing.
         *
         * @param evicted true if the entry is being removed to make space, false
         *     if the removal was caused by a {@link #put} or {@link #remove}.
         * @param newValue the new value for {@code key}, if it exists. If non-null,
         *     this removal was caused by a {@link #put}. Otherwise it was caused by
         *     an eviction or a {@link #remove}.
         */
        protected void entryRemoved(boolean evicted, Cell key, CellMarker oldValue, CellMarker newValue) {}

        /**
         * Called after a cache miss to compute a value for the corresponding key.
         * Returns the computed value or null if no value can be computed. The
         * default implementation returns null.
         *
         * <p>The method is called without synchronization: other threads may
         * access the cache while this method is executing.
         *
         * <p>If a value for {@code key} exists in the cache when this method
         * returns, the created value will be released with {@link #entryRemoved}
         * and discarded. This can occur when multiple threads request the same key
         * at the same time (causing multiple values to be created), or when one
         * thread calls {@link #put} while another is creating a value for the same
         * key.
         */
        protected CellMarker create(Cell key) {
            return null;
        }

        private int safeSizeOf(Cell key, CellMarker value) {
            int result = sizeOf(key, value);
            if (result < 0) {
                throw new IllegalStateException("Negative size: " + key + "=" + value);
            }
            return result;
        }

        /**
         * Returns the size of the entry for {@code key} and {@code value} in
         * user-defined units.  The default implementation returns 1 so that size
         * is the number of entries and max size is the maximum number of entries.
         *
         * <p>An entry's size must not change while it is in the cache.
         */
        protected int sizeOf(Cell key, CellMarker value) {
            return 1;
        }



        /**
         * For caches that do not override {@link #sizeOf}, this returns the number
         * of entries in the cache. For all other caches, this returns the sum of
         * the sizes of the entries in this cache.
         */
        public synchronized final int size() {
            return size;
        }

        /**
         * For caches that do not override {@link #sizeOf}, this returns the maximum
         * number of entries in the cache. For all other caches, this returns the
         * maximum sum of the sizes of the entries in this cache.
         */
        public synchronized final int maxSize() {
            return maxSize;
        }

        /**
         * Returns the number of times {@link #get} returned a value.
         */
        public synchronized final int hitCount() {
            return hitCount;
        }

        /**
         * Returns the number of times {@link #get} returned null or required a new
         * value to be created.
         */
        public synchronized final int missCount() {
            return missCount;
        }

        /**
         * Returns the number of times {@link #create(Object)} returned a value.
         */
        public synchronized final int createCount() {
            return createCount;
        }

        /**
         * Returns the number of times {@link #put} was called.
         */
        public synchronized final int putCount() {
            return putCount;
        }

        /**
         * Returns the number of values that have been evicted.
         */
        public synchronized final int evictionCount() {
            return evictionCount;
        }


    }