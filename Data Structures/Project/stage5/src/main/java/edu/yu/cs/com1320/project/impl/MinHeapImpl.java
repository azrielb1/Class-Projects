package edu.yu.cs.com1320.project.impl;

import java.util.NoSuchElementException;

import edu.yu.cs.com1320.project.MinHeap;

public class MinHeapImpl<E extends Comparable<E>> extends MinHeap<E> {
   
    @SuppressWarnings("unchecked")
    public MinHeapImpl() {
        super();
        elements = (E[]) new Comparable[5];
    }

    @Override
    public void reHeapify(E element) {
        upHeap(getArrayIndex(element));
        downHeap(getArrayIndex(element));
    }

    @Override
    protected int getArrayIndex(E element) {
        for (int i = 0; i < elements.length; i++) {
            if (element.equals(elements[i])) {
                return i;
            }
        }
        //throw a NoSuchElementException if the element is not in the heap
        throw new NoSuchElementException();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doubleArraySize() {
        E[] temp = (E[]) new Comparable[elements.length * 2];
        for (int i = 0; i < elements.length; i++) {
            temp[i] = elements[i];
        }
        elements = temp;
    }
}