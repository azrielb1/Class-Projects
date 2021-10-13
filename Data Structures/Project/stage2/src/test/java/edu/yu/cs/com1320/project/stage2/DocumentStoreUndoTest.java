package edu.yu.cs.com1320.project.stage2;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.junit.Test;

import edu.yu.cs.com1320.project.stage2.DocumentStore.DocumentFormat;
import edu.yu.cs.com1320.project.stage2.impl.DocumentStoreImpl;

public class DocumentStoreUndoTest {
    @Test
    public void undoTest() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();

        Boolean test = false;
        try {
            documentStore.undo();
        } catch (IllegalStateException e) {
            test = true;
        }
        assertEquals(true, test);

        String string1 = "It was a dark and stormy night";
        String string2 = "It was the best of times, it was the worst of times";
        String string3 = "It was a bright cold day in April, and the clocks were striking thirteen";
        InputStream inputStream1 = new ByteArrayInputStream(string1.getBytes());
        InputStream inputStream2 = new ByteArrayInputStream(string2.getBytes());
        InputStream inputStream3 = new ByteArrayInputStream(string3.getBytes());
        URI uri1 = URI.create("www.wrinkleintime.com");

        documentStore.putDocument(inputStream1, uri1, DocumentFormat.TXT);
        assertEquals(string1, documentStore.getDocument(uri1).getDocumentTxt());
        documentStore.putDocument(inputStream2, uri1, DocumentFormat.TXT);
        assertEquals(string2, documentStore.getDocument(uri1).getDocumentTxt());
        documentStore.undo();
        assertEquals(string1, documentStore.getDocument(uri1).getDocumentTxt());
        documentStore.undo();
        assertEquals(null, documentStore.getDocument(uri1));

        documentStore.putDocument(inputStream3, uri1, DocumentFormat.TXT);
        assertEquals(string3, documentStore.getDocument(uri1).getDocumentTxt());
        documentStore.deleteDocument(uri1);
        assertEquals(null, documentStore.getDocument(uri1));
        documentStore.undo();
        assertEquals(string3, documentStore.getDocument(uri1).getDocumentTxt());
    }

    @Test
    public void testUndoSpecificUri() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();

        String string1 = "It was a dark and stormy night";
        String string2 = "It was the best of times, it was the worst of times";
        String string3 = "It was a bright cold day in April, and the clocks were striking thirteen";
        String string4 = "I am free, no matter what rules surround me.";
        InputStream inputStream1 = new ByteArrayInputStream(string1.getBytes());
        InputStream inputStream2 = new ByteArrayInputStream(string2.getBytes());
        InputStream inputStream3 = new ByteArrayInputStream(string3.getBytes());
        InputStream inputStream4 = new ByteArrayInputStream(string4.getBytes());
        URI uri1 = URI.create("www.wrinkleintime.com");
        URI uri2 = URI.create("www.taleoftwocities.com");
        URI uri3 = URI.create("www.1984.com");

        documentStore.putDocument(inputStream1, uri1, DocumentFormat.TXT);
        assertEquals(string1, documentStore.getDocument(uri1).getDocumentTxt());
        documentStore.putDocument(inputStream2, uri2, DocumentFormat.TXT);
        assertEquals(string2, documentStore.getDocument(uri2).getDocumentTxt());
        documentStore.undo(uri1);
        assertEquals(null, documentStore.getDocument(uri1));
        assertEquals(string2, documentStore.getDocument(uri2).getDocumentTxt());
        documentStore.putDocument(inputStream3, uri1, DocumentFormat.TXT);
        assertEquals(string3, documentStore.getDocument(uri1).getDocumentTxt());
        documentStore.putDocument(inputStream4, uri1, DocumentFormat.TXT);
        assertEquals(string4, documentStore.getDocument(uri1).getDocumentTxt());
        documentStore.deleteDocument(uri1);
        assertEquals(null, documentStore.getDocument(uri1));
        documentStore.undo(uri2);
        assertEquals(null, documentStore.getDocument(uri2));
        documentStore.undo();
        assertEquals(string4, documentStore.getDocument(uri1).getDocumentTxt());
        documentStore.undo(uri1);
        assertEquals(string3, documentStore.getDocument(uri1).getDocumentTxt());

        Boolean test = false;
        try {
            documentStore.undo(uri3);
        } catch (IllegalStateException e) {
            test = true;
        }
        assertEquals(true, test);
    }
}
