package edu.yu.cs.com1320.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import edu.yu.cs.com1320.project.impl.MinHeapImpl;

public class MinHeapImplTest {
    
    @Test
    void testMinHeapInsertAndRemove() {
        MinHeap<Integer> mh = new MinHeapImpl<>();
        assertEquals(true, mh.isEmpty());
        
        mh.insert(13);
        mh.insert(9);
        mh.insert(4);
        mh.insert(33);
        mh.insert(61);
        mh.insert(-4);
        mh.insert(8);
        
        assertEquals(false, mh.isEmpty());
    
        try {
            mh.getArrayIndex(11);
            fail();
        } catch (NoSuchElementException e) {}

        assertEquals((Integer) (-4), mh.remove());
        assertEquals((Integer) 4, mh.remove());
        assertEquals((Integer) 8, mh.remove());
        assertEquals((Integer) 9, mh.remove());
        assertEquals((Integer) 13, mh.remove());
        assertEquals((Integer) 33, mh.remove());
        assertEquals((Integer) 61, mh.remove());


        assertEquals(true, mh.isEmpty());
    }
}
