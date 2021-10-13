package edu.yu.cs.com1320.project;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.yu.cs.com1320.project.impl.StackImpl;

public class StackImplTest {
    
    @Test
    public void siplePushAndPop() {
        Stack<String> s = new StackImpl<>();
        s.push("one");
        s.push("two");
        s.push("three");
        assertEquals(3, s.size());
        assertEquals("three", s.peek());
        assertEquals("three", s.pop());
        assertEquals("two", s.peek());
        assertEquals("two", s.peek());
        assertEquals("two", s.peek());
        assertEquals("two", s.peek());
        assertEquals("two", s.peek());
        assertEquals(2, s.size());
        assertEquals("two", s.pop());
        assertEquals("one", s.pop());
        assertEquals(0, s.size());
    }

    @Test
    public void aLotOfData() {
        Stack<Integer> s = new StackImpl<>();
        for (int i = 0; i < 1000; i++) {
            s.push(i);
            assertEquals((Integer)i, s.peek());
        }
        assertEquals(1000, s.size());
        assertEquals((Integer)999, s.peek());
        for (int i = 999; i >= 0; i--) {
            assertEquals((Integer)i, s.peek());
            assertEquals((Integer)i, s.pop());
        }
        assertEquals(0, s.size());
    }
}
