package edu.yu.cs.com1320.project;

import static org.junit.Assert.*;
import org.junit.Test;

import edu.yu.cs.com1320.project.impl.BTreeImpl;

public class TestBTreeImpl {

    @Test
    public void hashTableImplSimplePutAndGet() {
        BTree<Integer, Integer> hashTable = new BTreeImpl<>();
        hashTable.put(1, 2);
        hashTable.put(3, 6);
        hashTable.put(7, 14);
        int x = hashTable.get(1);
        int y = hashTable.get(3);
        int z = hashTable.get(7);
        assertEquals(2, x);
        assertEquals(6, y);
        assertEquals(14, z);
    }

    @Test
    public void testPutAndGet() {
        BTree<String, Integer> ht = new BTreeImpl<>();
        ht.put("num 1", 1);
        ht.put("num 2", 2);
        ht.put("num 6", 6);
        Integer a = ht.get("num 6");
        assertEquals((Integer) 6, a);
        Integer b = ht.put("num 1", null);
        assertEquals((Integer) 1, b);
        Integer c = ht.get("num 1");
        assertNull(c);
        Integer d = ht.get("num 2");
        assertEquals((Integer) 2, d);
    }

    @Test
    public void testPutALot() {
        BTree<String, Integer> b = new BTreeImpl<>();
        for (Integer i = 0; i < 1000; i++) {
            b.put(i.toString(), i);
        }
        for (Integer i = 0; i < 1000; i++) {
            assertEquals(i, b.get(i.toString()));
        }
    }

    @Test
    public void hashTableImplALotOfInfoTest() {
        BTree<Integer, Integer> hashTable = new BTreeImpl<>();
        for (int i = 0; i < 1000; i++) {
            hashTable.put(i, 2 * i);
        }

        Integer aa = hashTable.get(450);
        Integer bb = Integer.valueOf(900);
        assertEquals(bb, aa);
    }

    @Test
    public void hashTableImplCollisionTest() {
        BTree<Integer, Integer> hashTable = new BTreeImpl<>();
        hashTable.put(1, 9);
        hashTable.put(6, 12);
        hashTable.put(11, 22);
        int a = hashTable.get(1);
        int b = hashTable.get(6);
        int c = hashTable.get(11);
        assertEquals(9, a);
        assertEquals(12, b);
        assertEquals(22, c);
    }

    @Test
    public void hashTableImplReplacementTest() {
        BTree<Integer, Integer> hashTable = new BTreeImpl<>();
        hashTable.put(1, 2);
        int a = hashTable.put(1, 3);
        assertEquals(2, a);
        int b = hashTable.put(1, 4);
        assertEquals(3, b);
        int c = hashTable.put(1, 9);
        assertEquals(4, c);
    }

    @Test
    public void hashTableDelNullPut() {
        BTree<String, Integer> hashTable = new BTreeImpl<>();

        hashTable.put("Defied", (Integer) 22345);
        Integer test1a = hashTable.get("Defied");
        assertEquals(test1a, (Integer) 22345);
        hashTable.put("Defied", null);
        Integer test1b = hashTable.get("Defied");
        assertEquals(test1b, null);
        hashTable.put("Oakland", 87123);

        Integer test2a = hashTable.get("Oakland");
        assertEquals(test2a, (Integer) 87123);
        hashTable.put("Oakland", null);
        hashTable.get("Oakland");
        Integer test2b = hashTable.get("Oakland");
        assertEquals(test2b, null);

        hashTable.put("Sanguine", (Integer) 4682);
        Integer test3a = hashTable.get("Sanguine");
        assertEquals(test3a, (Integer) 4682);
        hashTable.put("Sanguine", null);
        hashTable.get("Sanguine");
        Integer test3b = hashTable.get("Sanguine");
        assertEquals(test3b, null);
    }

    @Test
    public void HashEqualButNotEqual() {
        BTree<String, Integer> hashTable = new BTreeImpl<>();

        hashTable.put("tensada", 3521);
        hashTable.put("friabili", 1253);
        Integer test1a = hashTable.get("tensada");
        assertEquals(test1a, (Integer) 3521);
        Integer test1b = hashTable.get("friabili");
        assertEquals(test1b, (Integer) 1253);

        hashTable.put("abyz", 8948);
        hashTable.put("abzj", 84980);
        Integer test2a = hashTable.get("abyz");
        assertEquals(test2a, (Integer) 8948);
        Integer test2b = hashTable.get("abzj");
        assertEquals(test2b, (Integer) 84980);

        hashTable.put("Siblings", 27128);
        hashTable.put("Teheran", 82172);
        Integer test3a = hashTable.get("Siblings");
        assertEquals(test3a, (Integer) 27128);
        Integer test3b = hashTable.get("Teheran");
        assertEquals(test3b, (Integer) 82172);
    }
}