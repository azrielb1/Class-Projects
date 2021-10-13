package edu.yu.cs.com1320.project.stage1.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.stage1.Document;
import edu.yu.cs.com1320.project.stage1.DocumentStore;

public class DocumentStoreImpl implements DocumentStore {

    HashTableImpl<URI, Document> store = new HashTableImpl<>();

    /**
     * @param input  the document being put
     * @param uri    unique identifier for the document
     * @param format indicates which type of document format is being passed
     * @return if there is no previous doc at the given URI, return 0. If there is a
     *         previous doc, return the hashCode of the previous doc. 
     *         If InputStream is null, this is a delete, and thus
     *         return either the hashCode of the deleted doc or 0 if there is no doc
     *         to delete.
     */
    public int putDocument(InputStream input, URI uri, DocumentFormat format) throws IOException {
        if (input == null) {
            Document oldDoc = store.get(uri);
            boolean complete = deleteDocument(uri);
            if (complete) {
                return oldDoc.hashCode();
            } else {
                return 0;
            }
        }
        
        byte[] binaryContents = new byte[input.available()];
        input.read(binaryContents); // reads the input into the byte[]

        DocumentImpl doc;

        if (format == DocumentFormat.TXT) {
            String txtContents = new String(binaryContents); // converts the byte[] into a String
            doc = new DocumentImpl(uri, txtContents);
        } else {
            doc = new DocumentImpl(uri, binaryContents);
        }

        Document previousDoc = store.put(uri, doc);
        if (previousDoc == null) {
            return 0;
        } else {
            return previousDoc.hashCode();
        }
    }

    /**
     * @param uri the unique identifier of the document to get
     * @return the given document
     */
    public Document getDocument(URI uri) {
        return store.get(uri);
    }

    /**
     * @param uri the unique identifier of the document to delete
     * @return true if the document is deleted, false if no document exists with
     *         that URI
     */
    public boolean deleteDocument(URI uri) {
        Document oldValue = store.put(uri, null);
        return oldValue != null;
    }
}