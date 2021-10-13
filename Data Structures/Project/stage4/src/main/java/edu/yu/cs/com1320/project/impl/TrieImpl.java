package edu.yu.cs.com1320.project.impl;

import java.util.*;

import edu.yu.cs.com1320.project.Trie;

public class TrieImpl<Value> implements Trie<Value> {

    private Node<Value> root; // root of trie

    @SuppressWarnings("unchecked")
    static class Node<Value> {
        protected List<Value> values = new ArrayList<>();
        protected Node<Value>[] links = new Node[36];
    }

    public TrieImpl() {
        this.root = new Node<>();
    }

    /**
     * add the given value at the given key
     * 
     * @param key
     * @param val
     */
    public void put(String key, Value val) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (val == null || key.isEmpty()) {
            return;
        }

        Node<Value> current = root;
        for (char c : key.toLowerCase().toCharArray()) { // follow the trie letter by letter to the end of the word
            int idx = getIndex(c);
            if (current.links[idx] == null) { // if the node for the next letter doesn't exist
                current.links[idx] = new Node<>(); // construct it
            }
            current = current.links[idx]; // set current to the next letter
        }
        if (!current.values.contains(val)) {
            current.values.add(val);
        }
    }

    private Node<Value> get(String key) {
        Node<Value> current = root;
        if (key == null || key.isEmpty()) {
            return null;
        }
        for (char c : key.toLowerCase().toCharArray()) { // follow the trie letter by letter to the end of the word
            int idx = getIndex(c);
            if (current.links[idx] == null) { // if the next letter doesn't exist
                return null;
            }
            current = current.links[idx]; // set current to the next letter
        }
        return current;
    }

    /**
     * get all exact matches for the given key, sorted in descending order. Search
     * is CASE INSENSITIVE.
     * 
     * @param key
     * @param comparator used to sort values
     * @return a List of matching Values, in descending order
     */
    public List<Value> getAllSorted(String key, Comparator<Value> comparator) {
        if (key == null || comparator == null) {
            throw new IllegalArgumentException();
        }
        Node<Value> nodeAtKey = this.get(key);
        if (nodeAtKey == null) {
            return Collections.emptyList();
        }

        List<Value> returnList = new ArrayList<>(nodeAtKey.values);
        returnList.sort(comparator);
        return returnList;
    }

    /**
     * get all matches which contain a String with the given prefix, sorted in
     * descending order. For example, if the key is "Too", you would return any
     * value that contains "Tool", "Too", "Tooth", "Toodle", etc. Search is CASE
     * INSENSITIVE.
     * 
     * @param prefix
     * @param comparator used to sort values
     * @return a List of all matching Values containing the given prefix, in
     *         descending order
     */
    public List<Value> getAllWithPrefixSorted(String prefix, Comparator<Value> comparator) {
        if (prefix == null || comparator == null) {
            throw new IllegalArgumentException();
        }
        
        List<Value> returnList = new ArrayList<>();

        Node<Value> subtreeAtPrefix = this.get(prefix);
        if (subtreeAtPrefix == null) {
            return returnList;
        }
        traversalAddToList(subtreeAtPrefix, returnList);
        returnList.sort(comparator);
        return returnList;
    }

    private void traversalAddToList(Node<Value> n, Collection<Value> list) {
        if (n != null) {
            for (Value value : n.values) {
                if (!list.contains(value)) {
                    list.add(value);
                }
            }
        }
        for (Node<Value> c : n.links) {
            if (c != null) {
                traversalAddToList(c, list);
            }
        }
    }

    /**
     * Delete the subtree rooted at the last character of the prefix. Search is CASE
     * INSENSITIVE.
     * 
     * @param prefix
     * @return a Set of all Values that were deleted.
     */
    @SuppressWarnings("unchecked")
    public Set<Value> deleteAllWithPrefix(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException();
        }
        Set<Value> returnSet = new HashSet<>();
        Node<Value> subtreeAtPrefix = this.get(prefix);
        if (subtreeAtPrefix == null) {
            return returnSet;
        }
        traversalAddToList(subtreeAtPrefix, returnSet);
        subtreeAtPrefix.links = new Node[36];
        deleteAll(this.root, prefix.toLowerCase(), 0, new HashSet<>());
        return returnSet;
    }

    /**
     * Delete all values from the node of the given key (do not remove the values
     * from other nodes in the Trie)
     * 
     * @param key
     * @return a Set of all Values that were deleted.
     */
    public Set<Value> deleteAll(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        Set<Value> r = new HashSet<>();
        deleteAll(this.root, key.toLowerCase(), 0, r);
        return r;
    }

    private Node<Value> deleteAll(Node<Value> x, String key, int d, Set<Value> s) {
        if (x == null) {
            return null;
        }
        // we're at the node to del - clear the values
        if (d == key.length()) {
            s.addAll(x.values);
            x.values.clear();
        }
        // continue down the trie to the target node
        else {
            char c = key.charAt(d);
            int i = getIndex(c);
            x.links[i] = this.deleteAll(x.links[i], key, d + 1, s);
        }
        // this node has a val – do nothing, return the node
        if (!x.values.isEmpty()) {
            return x;
        }
        // remove subtrie rooted at x if it is completely empty
        for (int c = 0; c < 36; c++) {
            if (x.links[c] != null) {
                return x; // not empty
            }
        }
        // empty - set this link to null in the parent
        return null;
    }

    /**
     * Remove the given value from the node of the given key (do not remove the
     * value from other nodes in the Trie)
     * 
     * @param key
     * @param val
     * @return the value which was deleted. If the key did not contain the given
     *         value, return null.
     */
    public Value delete(String key, Value val) {
        if (val == null || key == null) {
            throw new IllegalArgumentException();
        }
        Node<Value> node = get(key);
        if (node == null) {
            return null;
        }
        int valIndex = node.values.indexOf(val);
        Value oldValue = valIndex != -1 ? node.values.get(valIndex) : null;
        node.values.remove(val);
        if (node.values.isEmpty()) {
            deleteAll(this.root, key.toLowerCase(), 0, new HashSet<>());
        }
        return oldValue;
    }

    /**
     * @param c a character from 0 - 9 and a - z
     * @return which index in the node links array this charachter is stored at
     *         (between 0 and 35)
     */
    private int getIndex(char c) {
        if (c >= 48 && c <= 57) { // if it is a number from 0 - 9
            return c - 48;
        } else if (c >= 97 && c <= 122) { // if it is a lower case letter from a - z
            return c - 87;
        } else {
            throw new IllegalArgumentException("Invalid character");
        }
    }
}
