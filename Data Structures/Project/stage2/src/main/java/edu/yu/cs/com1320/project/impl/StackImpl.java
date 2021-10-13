package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Stack;

public class StackImpl<T> implements Stack<T> {

    @SuppressWarnings("hiding")
    class Entry<T> {
        T content;
        Entry<T> next;

        Entry(T c, Entry<T> n) {
            content = c;
            next = n;
        }
    }
    
    private Entry<T> head;
    private int stackSize;

    public StackImpl() {
        this.head = null;
        this.stackSize = 0;
    }

    /**
     * @param element object to add to the Stack
     */
    public void push(T element) {
        if (element == null) {
            throw new IllegalArgumentException();
        }
        Entry<T> e = new Entry<>(element, head);
        head = e;
        stackSize++;
    }

    /**
     * removes and returns element at the top of the stack
     * @return element at the top of the stack, null if the stack is empty
     */
    public T pop() {
        if (stackSize == 0) {
            return null;
        }
        T returnValue = head.content;
        head = head.next;
        stackSize--;
        return returnValue;
    }

    /**
     *
     * @return the element at the top of the stack without removing it
     */
    public T peek() {
        if (stackSize == 0) {
            return null;
        }
        return head.content;
    }

    /**
     *
     * @return how many elements are currently in the stack
     */
    public int size() {
        return stackSize;
    }
}