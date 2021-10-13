package edu.yu.cs.com1320.project.stage4.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.yu.cs.com1320.project.stage5.DocumentStore;
import edu.yu.cs.com1320.project.stage5.impl.DocumentStoreImpl;

public class TestDocumentStoreImpl {

    private URI uri1;
    private String txt1;

    private URI uri2;
    private String txt2;

    private URI uri3;
    private String txt3;

    private URI uri4;
    private String txt4;
    
    @BeforeEach
    public void init() throws Exception {
        //init possible values for doc1
        uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
        txt1 = "This doc1 plain text string Computer Headphones";

        //init possible values for doc2
        uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
        txt2 = "Text doc2 plain String";

        //init possible values for doc3
        uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
        txt3 = "This is the text of doc3";

        //init possible values for doc4
        uri4 = new URI("http://edu.yu.cs/com1320/project/doc4");
        txt4 = "This is the text of doc4";

        new File(System.getProperty("user.dir"), (uri1.toString().replaceFirst("^(http[s]?://)", "")) + ".json").delete();
        new File(System.getProperty("user.dir"), (uri2.toString().replaceFirst("^(http[s]?://)", "")) + ".json").delete();
        new File(System.getProperty("user.dir"), (uri3.toString().replaceFirst("^(http[s]?://)", "")) + ".json").delete();
        new File(System.getProperty("user.dir"), (uri4.toString().replaceFirst("^(http[s]?://)", "")) + ".json").delete();
    }

    @Test
    void testMaxDocCount() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT);

        File doc1File = new File(System.getProperty("user.dir"), (uri1.toString().replaceFirst("^(http[s]?://)", "")) + ".json");
        File doc2File = new File(System.getProperty("user.dir"), (uri2.toString().replaceFirst("^(http[s]?://)", "")) + ".json");
        File doc3File = new File(System.getProperty("user.dir"), (uri3.toString().replaceFirst("^(http[s]?://)", "")) + ".json");
        File doc4File = new File(System.getProperty("user.dir"), (uri4.toString().replaceFirst("^(http[s]?://)", "")) + ".json");
        
        assertTrue(doc1File.exists());
        assertFalse(doc2File.exists());
        assertFalse(doc3File.exists());
        assertFalse(doc4File.exists());
        
        assertEquals(txt1, store.getDocument(uri1).getDocumentTxt());
        
        assertFalse(doc1File.exists());
        assertTrue(doc2File.exists());
        assertFalse(doc3File.exists());
        assertFalse(doc4File.exists());
        
        assertEquals(txt2, store.search("doc2").get(0).getDocumentTxt());
        
        assertFalse(doc1File.exists());
        assertFalse(doc2File.exists());
        assertTrue(doc3File.exists());
        assertFalse(doc4File.exists());
        
        store.setMaxDocumentCount(0);
        
        assertTrue(doc1File.exists());
        assertTrue(doc2File.exists());
        assertTrue(doc3File.exists());
        assertTrue(doc4File.exists());
        
        assertEquals(3, store.search("this").size());
        
        assertTrue(doc1File.exists());
        assertTrue(doc2File.exists());
        assertTrue(doc3File.exists());
        assertTrue(doc4File.exists());
        
        assertTrue(store.deleteDocument(uri3));
        assertTrue(doc1File.exists());
        assertTrue(doc2File.exists());
        assertFalse(doc3File.exists());
        assertTrue(doc4File.exists());
        
        store.undo();
        assertTrue(doc1File.exists());
        assertTrue(doc2File.exists());
        assertTrue(doc3File.exists());
        assertTrue(doc4File.exists());
        
        store.undo();
        assertTrue(doc1File.exists());
        assertTrue(doc2File.exists());
        assertTrue(doc3File.exists());
        assertFalse(doc4File.exists());
        assertNull(store.getDocument(uri4));
        
        store.undo(uri1);
        assertFalse(doc1File.exists());
        assertTrue(doc2File.exists());
        assertTrue(doc3File.exists());
        assertFalse(doc4File.exists());
        assertNull(store.getDocument(uri1));
        assertNull(store.getDocument(uri4));
        assertEquals(txt2, store.getDocument(uri2).getDocumentTxt());
    
        store.deleteAllWithPrefix("t");
    }
}
