package edu.yu.cs.com1320.project.stage2.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import edu.yu.cs.com1320.project.Command;
import edu.yu.cs.com1320.project.Stack;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.stage2.Document;
import edu.yu.cs.com1320.project.stage2.DocumentStore;

public class DocumentStoreImpl implements DocumentStore {

    private HashTableImpl<URI, Document> store = new HashTableImpl<>();
    private Stack<Command> commandStack = new StackImpl<>();

    /**
     * @param input  the document being put
     * @param uri    unique identifier for the document
     * @param format indicates which type of document format is being passed
     * @return if there is no previous doc at the given URI, return 0. If there is a
     *         previous doc, return the hashCode of the previous doc. If InputStream
     *         is null, this is a delete, and thus return either the hashCode of the
     *         deleted doc or 0 if there is no doc to delete.
     */
    public int putDocument(InputStream input, URI uri, DocumentFormat format) throws IOException {
        if (format == null) {
            throw new IllegalArgumentException();
        }
        if (input == null) {
            Document oldDoc = store.get(uri);
            boolean complete = deleteDocument(uri);
            return complete ? oldDoc.hashCode() : 0;
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
        commandStack.push(new Command(uri, x -> {
            store.put(x, previousDoc);
            return true;
        }));
        return previousDoc == null ? 0 : previousDoc.hashCode();
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
        commandStack.push(new Command(uri, x -> {
            store.put(x, oldValue);
            return true;
        }));
        return oldValue != null;
    }

    /**
     * undo the last put or delete command
     * 
     * @throws IllegalStateException if there are no actions to be undone, i.e. the
     *                               command stack is empty
     */
    public void undo() throws IllegalStateException {
        if (commandStack.size() == 0) {
            throw new IllegalStateException();
        }
        Command c = commandStack.pop();
        c.undo();
    }

    /**
     * undo the last put or delete that was done with the given URI as its key
     * 
     * @param uri
     * @throws IllegalStateException if there are no actions on the command stack
     *                               for the given URI
     */
    public void undo(URI uri) throws IllegalStateException {
        Stack<Command> helperStack = new StackImpl<>();
        while ((commandStack.size() > 0) && !(commandStack.peek().getUri().equals(uri))) {
            helperStack.push(commandStack.pop());
        }
        if (commandStack.size() == 0) {
            while (helperStack.size() > 0) {
                commandStack.push(helperStack.pop());
            }
            throw new IllegalStateException();
        }
        Command c = commandStack.pop();
        c.undo();
        while (helperStack.size() > 0) {
            commandStack.push(helperStack.pop());
        }
    }
}