package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.HashTable;

public class HashTableImpl<Key, Value> implements HashTable<Key, Value> {

    class Entry {
        Key key;
        Value value;
        Entry next;

        Entry(Key k, Value v, Entry n) {
            if (k == null) {
                throw new IllegalArgumentException();
            }
            key = k;
            value = v;
            next = n;
        }
    }

    private Entry[] table = new HashTableImpl.Entry[5];

    /**
     * @param k the key whose value should be returned
     * @return the value that is stored in the HashTable for k, or null if there is
     *         no such key in the table
     */
    public Value get(Key k) {
        int idx = hashFunction(k);
        if (table[idx] == null) {
            return null;
        }
        // go through list and find the key
        Entry current = table[idx];
        while (current != null && !current.key.equals(k)) {
            current = current.next;
        }
        if (current != null) {
            return current.value;
        } else {
            return null;
        }
    }

    /**
     * @param k the key at which to store the value
     * @param v the value to store
     * @return if the key was already present in the HashTable, return the previous
     *         value stored for the key. If the key was not already present, return
     *         null.
     */
    public Value put(Key k, Value v) {
        int idx = hashFunction(k);
        Value old = delete(k);
        if (v == null) {
            return old;
        }
        Entry putEntry = new Entry(k, v, table[idx]);
        this.table[idx] = putEntry;
        return old;
    }

    /**
     * deletes a key from the hash table
     * 
     * @param k the key to delete
     * @return the value of the deleted key or null if key isn't present.
     */
    private Value delete(Key k) {
        int idx = hashFunction(k);
        Entry current = table[idx];
        Entry old;
        if (current == null) {
            return null;
        }
        if (current.key == k) {
            old = current;
            table[idx] = current.next;
            return old.value;
        }
        while (current.next != null && !current.next.key.equals(k)) {
            current = current.next;
        }
        if (current.next == null) {
            return null;
        }
        old = current.next;
        current.next = current.next.next;
        return old.value;
    }

    private int hashFunction(Key key) {
        return (key.hashCode() & 0x7fffffff) % this.table.length;
    }
}