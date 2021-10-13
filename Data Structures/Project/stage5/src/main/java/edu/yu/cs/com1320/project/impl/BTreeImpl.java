package edu.yu.cs.com1320.project.impl;

import java.io.IOException;

import edu.yu.cs.com1320.project.BTree;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;

public class BTreeImpl<Key extends Comparable<Key>, Value> implements BTree<Key, Value> {

    private static final int MAX = 4; // max children per B-tree node = MAX-1 (must be an even number and greater than 2)
    private Node root; // root of the B-tree
    private int height; // height of the B-tree

    private static final class Node {
        private int entryCount; // number of entries
        private Entry[] entries = new Entry[MAX]; // the array of children

        // create a node with k entries
        private Node(int k) {
            this.entryCount = k;
        }
    }

    @SuppressWarnings("rawtypes")
    public static class Entry {
        private Comparable key;
        private Object val;
        private Node child;

        public Entry(Comparable key, Object val, Node child) {
            this.key = key;
            this.val = val;
            this.child = child;
        }
    }

    public BTreeImpl() {
        this.root = new Node(0);
    }

    @Override
    public Value put(Key k, Value v) {
        if (k == null) {
            throw new IllegalArgumentException("key is null");
        }

        // if the key already exists in the b-tree, simply replace the value
        Entry alreadyThere = this.get(this.root, k, this.height);
        if (alreadyThere != null) {
            @SuppressWarnings("unchecked")
            Value oldValue = (Value) alreadyThere.val;
            alreadyThere.val = v;
            if (oldValue == null) {
                try {
                    return persistenceManager.deserialize(k);
                } catch (Exception e) {
                    return oldValue;
                }
            }
            return oldValue;
        }

        Node newNode = this.put(this.root, k, v, this.height);

        if (newNode == null) {
            return null;
        }

        // split the root:
        Node newRoot = new Node(2);
        newRoot.entries[0] = new Entry(this.root.entries[0].key, null, this.root);
        newRoot.entries[1] = new Entry(newNode.entries[0].key, null, newNode);
        this.root = newRoot;
        this.height++;
        return null;
    }

    private Node put(Node currentNode, Key key, Value val, int height) {
        // Have to set j to the index in currentNode.entries[] where the new entry goes.
        int j;
        Entry newEntry = new Entry(key, val, null);

        // if it is an external node
        if (height == 0) {
            // Set j to the index of the first entry in the current node whose key > the new
            // key
            for (j = 0; j < currentNode.entryCount; j++) {
                if (less(key, currentNode.entries[j].key)) {
                    break;
                }
            }
        }

        // internal node
        else {
            for (j = 0; j < currentNode.entryCount; j++) {
                if ((j + 1 == currentNode.entryCount) || less(key, currentNode.entries[j + 1].key)) {
                    Node newNode = this.put(currentNode.entries[j++].child, key, val, height - 1);
                    if (newNode == null) {
                        return null;
                    }
                    newEntry.key = newNode.entries[0].key;
                    newEntry.val = null;
                    newEntry.child = newNode;
                    break;
                }
            }
        }

        // shift greater entries over one place to make room for new entry
        for (int i = currentNode.entryCount; i > j; i--) {
            currentNode.entries[i] = currentNode.entries[i - 1];
        }
        // insert the new entry into slot j in the current node
        currentNode.entries[j] = newEntry;
        currentNode.entryCount++;
        if (currentNode.entryCount < BTreeImpl.MAX) {
            // No structural changes needed in the tree
            return null;
        } else {
            // We have to split this node and create a new entry in the parent
            // We return the new node which is created by the split
            return this.split(currentNode);
        }
    }

    private Node split(Node currentNode) {
        Node newNode = new Node(BTreeImpl.MAX / 2);
        // copy top half of currentNode, which has been chopped off into newNode
        for (int j = 0; j < BTreeImpl.MAX / 2; j++) {
            newNode.entries[j] = currentNode.entries[BTreeImpl.MAX / 2 + j];
            currentNode.entries[BTreeImpl.MAX / 2 + j] = null;
        }
        currentNode.entryCount = BTreeImpl.MAX / 2;
        return newNode;
    }

    @Override
    public Value get(Key k) {
        Value v = getFromMemory(k);
        if (v != null) {
            return v;
        }
        if (persistenceManager != null) {
            try {
                v = persistenceManager.deserialize(k);
                put(k, v);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return v;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Value getFromMemory(Key k) {
        if (k == null) {
            throw new IllegalArgumentException("key is null");
        }
        Entry entry = this.get(this.root, k, this.height);
        if (entry != null) {
            return (Value) entry.val;
        }
        return null;
    }

    private Entry get(Node currentNode, Key key, int height) {
        Entry[] entries = currentNode.entries;

        if (height == 0) {
            for (int j = 0; j < currentNode.entryCount; j++) {
                if (isEqual(key, entries[j].key)) {
                    // found desired key. Return entry
                    return entries[j];
                }
            }
            return null; // didn't find the key
        }
        // current node is internal (height > 0)
        else {
            for (int j = 0; j < currentNode.entryCount; j++) {
                if (j + 1 == currentNode.entryCount || less(key, entries[j + 1].key)) {
                    return this.get(entries[j].child, key, height - 1);
                }
            }
            // didn't find the key
            return null;
        }
    }

    @SuppressWarnings("all")
    private boolean less(Comparable k1, Comparable k2) {
        return k1.compareTo(k2) < 0;
    }
    
    @SuppressWarnings("all")
    private static boolean isEqual(Comparable k1, Comparable k2) {
        return k1.compareTo(k2) == 0;
    }

    @Override
    public void moveToDisk(Key k) throws Exception {
        if (persistenceManager == null) {
            throw new IllegalStateException("persistence manager not set");
        }
        Value toMove = getFromMemory(k);
        if (toMove == null) {
            throw new IllegalStateException("key not in memory");
        }
        persistenceManager.serialize(k, toMove);
        put(k, null);
    }

    private PersistenceManager<Key, Value> persistenceManager;

    @Override
    public void setPersistenceManager(PersistenceManager<Key, Value> pm) {
        this.persistenceManager = pm;
    }
}