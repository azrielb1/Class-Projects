package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.HashTable;

public class HashTableImpl<Key, Value> implements HashTable<Key, Value> {

    @SuppressWarnings("hiding")
    class Entry<Key, Value> {
        Key key;
        Value value;
        Entry<Key, Value> next;

        Entry(Key k, Value v, Entry<Key, Value> n) {
            if (k == null) {
                throw new IllegalArgumentException();
            }
            key = k;
            value = v;
            next = n;
        }
    }

    private Entry<Key, Value>[] table; // the hashtable's underlying array
    private int numElements; // Number of entries stored in the hashtable

    @SuppressWarnings("unchecked")
    public HashTableImpl() {
        this.table = new HashTableImpl.Entry[4];
        this.numElements = 0;
    }

    /**
     * @param k the key whose value should be returned
     * @return the value that is stored in the HashTable for k, or null if there is
     *         no such key in the table
     */
    public Value get(Key k) {
        if (k == null) {
            throw new IllegalArgumentException();
        }
        int idx = hashFunction(k);
        if (table[idx] == null) {
            return null;
        }
        // go through list and find the key
        Entry<Key, Value> current = table[idx];
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
        if (k == null) {
            throw new IllegalArgumentException();
        }
        int idx = hashFunction(k);
        Value old = delete(k);
        if (v == null) {
            return old;
        }
        Entry<Key, Value> putEntry = new Entry<>(k, v, table[idx]);
        this.table[idx] = putEntry;
        numElements++;

        if ((numElements/4) > this.table.length) {
            rehash();
        }
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
        Entry<Key, Value> current = table[idx];
        Entry<Key, Value> old;
        if (current == null) {
            return null;
        }
        if (current.key == k) {
            old = current;
            table[idx] = current.next;
            numElements--;
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
        numElements--;
        return old.value;
    }

    private int hashFunction(Key key) {
        return (key.hashCode() & 0x7fffffff) % this.table.length;
    }

    /**
     * Doubles the size of the array and rehashes all entries
     */
    @SuppressWarnings("unchecked")
    private void rehash() {
        Entry<Key, Value>[] oldTable = this.table;
        this.table = new HashTableImpl.Entry[oldTable.length * 2];
        this.numElements = 0;
        
        for (Entry<Key, Value> head : oldTable) { // for each list in the array
            Entry<Key, Value> current = head;
            while (current != null) { // while we have not reached the end of the list
                put(current.key, current.value);
                current = current.next; // go to the next element in the list
            }
        }
    }
}