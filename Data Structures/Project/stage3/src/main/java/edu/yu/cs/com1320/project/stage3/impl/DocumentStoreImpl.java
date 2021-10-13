package edu.yu.cs.com1320.project.stage3.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.yu.cs.com1320.project.*;
import edu.yu.cs.com1320.project.impl.*;
import edu.yu.cs.com1320.project.stage3.Document;
import edu.yu.cs.com1320.project.stage3.DocumentStore;

public class DocumentStoreImpl implements DocumentStore {

    private HashTableImpl<URI, Document> store = new HashTableImpl<>();
    private Stack<Undoable> commandStack = new StackImpl<>();
    private Trie<Document> documentTrie = new TrieImpl<>();

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
        if (input == null) { // delete if passed a null input
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
        Document previousDoc = putDocument(doc, uri);
        commandStack.push(new GenericCommand<URI>(uri, x -> {
            this.putDocument(previousDoc, x);
            return true;
        }));
        return previousDoc == null ? 0 : previousDoc.hashCode();
    }

    /**
     * 
     * @param doc the document to add
     * @return the document that used to be stored at that uri
     */
    private Document putDocument(Document doc, URI uri) {
        Document previousDoc = store.put(uri, doc);
        if (previousDoc != null) {
            removeFromTrie(previousDoc);
        }
        if (doc != null) {
            addToTrie(doc);
        }
        return previousDoc;
    }

    /**
     * Adds every word from the given documment to the document trie
     * 
     * @param doc the document to add
     */
    private void addToTrie(Document doc) {
        for (String word : doc.getWords()) {
            documentTrie.put(word, doc);
        }
    }

    private void removeFromTrie(Document doc) {
        for (String word : doc.getWords()) {
            documentTrie.delete(word, doc);
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
        commandStack.push(new GenericCommand<URI>(uri, x -> {
            this.putDocument(oldValue, uri);
            return true;
        }));
        if (oldValue != null) {
            removeFromTrie(oldValue);
        }
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
        Undoable c = commandStack.pop();
        c.undo();
    }

    /**
     * undo the last put or delete that was done with the given URI as its key
     * 
     * @param uri
     * @throws IllegalStateException if there are no actions on the command stack
     *                               for the given URI
     */
    @SuppressWarnings("unchecked")
    public void undo(URI uri) throws IllegalStateException {
        Stack<Undoable> helperStack = new StackImpl<>();
        while ((commandStack.size() > 0) && !(matchingTarget(commandStack.peek(), uri))) {
            helperStack.push(commandStack.pop());
        }
        if (commandStack.size() == 0) {
            while (helperStack.size() > 0) {
                commandStack.push(helperStack.pop());
            }
            throw new IllegalStateException();
        }
        Undoable c = commandStack.pop();
        if (c instanceof CommandSet) {
            ((CommandSet<URI>) c).undo(uri);
            if (((CommandSet<URI>) c).size() != 0) {
                commandStack.push(c);
            }
        } else {
            c.undo();
        }
        
        while (helperStack.size() > 0) {
            commandStack.push(helperStack.pop());
        }
    }

    /**
     * 
     * @param x
     * @param uri
     * @return true if x contains the target uri
     */
    @SuppressWarnings("unchecked")
    private boolean matchingTarget(Undoable x, URI uri) {
        if (x instanceof CommandSet) {
            return ((CommandSet<URI>) x).containsTarget(uri);
        } else if (x instanceof GenericCommand) {
            return ((GenericCommand<URI>) x).getTarget().equals(uri);
        } else {
            return false;
        }
    }

    /**
     * Retrieve all documents whose text contains the given keyword. Documents are
     * returned in sorted, descending order, sorted by the number of times the
     * keyword appears in the document. Search is CASE INSENSITIVE.
     * 
     * @param keyword
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    public List<Document> search(String keyword) {
        String key = keyword.toLowerCase();
        return documentTrie.getAllSorted(key, (doc1, doc2) -> {
            if (doc1.wordCount(key) < doc2.wordCount(key)) {
                return 1;
            } else if (doc1.wordCount(key) > doc2.wordCount(key)) {
                return -1;
            } else {
                return 0;
            }
        });
    }

    /**
     * Retrieve all documents whose text starts with the given prefix Documents are
     * returned in sorted, descending order, sorted by the number of times the
     * prefix appears in the document. Search is CASE INSENSITIVE.
     * 
     * @param keywordPrefix
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    public List<Document> searchByPrefix(String keywordPrefix) {
        String key = keywordPrefix.toLowerCase();
        return documentTrie.getAllWithPrefixSorted(key, (doc1, doc2) -> {
            Integer doc1Count = 0;
            Integer doc2Count = 0;

            for (String word : doc1.getWords()) {
                if (word.startsWith(key)) {
                    doc1Count += doc1.wordCount(word);
                }
            }
            for (String word : doc2.getWords()) {
                if (word.startsWith(key)) {
                    doc2Count += doc2.wordCount(word);
                }
            }

            return doc2Count.compareTo(doc1Count);
        });
    }

    /**
     * Completely remove any trace of any document which contains the given keyword
     * 
     * @param keyword
     * @return a Set of URIs of the documents that were deleted.
     */
    public Set<URI> deleteAll(String keyword) {
        CommandSet<URI> commandSet = new CommandSet<>();
        Set<URI> r = new HashSet<>();
        for (Document document : search(keyword)) {
            URI uri = document.getKey();
            Document oldValue = store.put(uri, null);
            removeFromTrie(oldValue);
            commandSet.addCommand(new GenericCommand<URI>(uri, x -> {
                this.putDocument(oldValue, x);
                return true;
            }));
            r.add(uri);
        }
        commandStack.push(commandSet);
        return r;
    }

    /**
     * Completely remove any trace of any document which contains a word that has
     * the given prefix Search is CASE INSENSITIVE.
     * 
     * @param keywordPrefix
     * @return a Set of URIs of the documents that were deleted.
     */
    public Set<URI> deleteAllWithPrefix(String keywordPrefix) {
        CommandSet<URI> commandSet = new CommandSet<>();
        Set<URI> r = new HashSet<>();
        for (Document document : searchByPrefix(keywordPrefix)) {
            URI uri = document.getKey();
            Document oldValue = store.put(uri, null);
            removeFromTrie(oldValue);
            commandSet.addCommand(new GenericCommand<URI>(uri, x -> {
                this.putDocument(oldValue, x);
                return true;
            }));
            r.add(uri);
        }
        commandStack.push(commandSet);
        return r;
    }
}