package edu.yu.cs.com1320.project.stage4.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import edu.yu.cs.com1320.project.*;
import edu.yu.cs.com1320.project.impl.*;
import edu.yu.cs.com1320.project.stage4.Document;
import edu.yu.cs.com1320.project.stage4.DocumentStore;

public class DocumentStoreImpl implements DocumentStore {

    private HashTableImpl<URI, Document> store = new HashTableImpl<>();
    private Stack<Undoable> commandStack = new StackImpl<>();
    private Trie<Document> documentTrie = new TrieImpl<>();
    /**
     * queues documents by usage time
     */
    private MinHeap<Document> minHeap = new MinHeapImpl<>();

    /**
     * number of documents in the doument store
     */
    private int documentCount = 0;
    private int bytesStored = 0;

    /**
     * maximum number of documents that may be stored.
     * -1 if no limit is set
     */
    private int maxDocumentCount = -1;

    /**
     *  maximum number of bytes of memory that may be used by all the documents in memory combined.
     *  -1 if no limit is set
     */
    private int maxDocumentBytes = -1;

    private class NanoTimeStore {
        long t;
    }
    private NanoTimeStore nanoTimeStore = new NanoTimeStore();
    
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
     * @param doc the document to add (or null to delete)
     * @return the document that used to be stored at that uri
     */
    private Document putDocument(Document doc, URI uri) {
        if (doc != null) {
            while (maxDocumentCount >= 0 && documentCount + 1 > maxDocumentCount) {
                Document docToDelete = minHeap.remove();
                putDocument(null, docToDelete.getKey());
                removeFromUndoCommands(docToDelete.getKey());
            } 
            while (maxDocumentBytes >= 0 && bytesStored + (doc.getDocumentTxt() == null ? doc.getDocumentBinaryData().length : doc.getDocumentTxt().getBytes().length) > maxDocumentBytes) {
                Document docToDelete = minHeap.remove();
                putDocument(null, docToDelete.getKey());
                removeFromUndoCommands(docToDelete.getKey());
            }
        }
        Document previousDoc = store.put(uri, doc);
        if (previousDoc != null) {
            removeFromTrie(previousDoc);
            try {
                removeFromMinHeap(previousDoc);
            } catch (NoSuchElementException e) {}
            documentCount--;
            bytesStored -= previousDoc.getDocumentTxt() == null ? previousDoc.getDocumentBinaryData().length : previousDoc.getDocumentTxt().getBytes().length;
        }
        if (doc != null) {
            addToTrie(doc);
            doc.setLastUseTime(System.nanoTime());
            minHeap.insert(doc);
            documentCount++;
            bytesStored += doc.getDocumentTxt() == null ? doc.getDocumentBinaryData().length : doc.getDocumentTxt().getBytes().length;
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

    /**
     *  
     * @param doc the document to remove
     */
    private void removeFromTrie(Document doc) {
        for (String word : doc.getWords()) {
            documentTrie.delete(word, doc);
        }
    }

    /**
     * Remove the given document from the minHeap
     * 
     * @param doc the document to remove from the heap
     * @return true if everything worked
     */
    private boolean removeFromMinHeap(Document doc) {
        doc.setLastUseTime(Long.MIN_VALUE);
        minHeap.reHeapify(doc);
        return doc == minHeap.remove();
    }

    /**
     * @param uri the unique identifier of the document to get
     * @return the given document
     */
    public Document getDocument(URI uri) {
        Document doc = store.get(uri);

        if (doc != null) {
            doc.setLastUseTime(System.nanoTime());
            minHeap.reHeapify(doc);
        }
        return doc;
    }

    /**
     * @param uri the unique identifier of the document to delete
     * @return true if the document is deleted, false if no document exists with
     *         that URI
     */
    public boolean deleteDocument(URI uri) {
        Document oldValue = putDocument(null, uri);
        commandStack.push(new GenericCommand<URI>(uri, x -> {
            this.putDocument(oldValue, uri);
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
        nanoTimeStore.t = System.nanoTime();
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
        if (uri  == null) {
            throw new IllegalArgumentException();
        }
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
        nanoTimeStore.t = System.nanoTime();
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
     * @return true if Undoable x contains the target uri
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
        List<Document> d = documentTrie.getAllSorted(key, (doc1, doc2) -> {
            if (doc1.wordCount(key) < doc2.wordCount(key)) {
                return 1;
            } else if (doc1.wordCount(key) > doc2.wordCount(key)) {
                return -1;
            } else {
                return 0;
            }
        });

        long nanoTime = System.nanoTime();

        for (Document document : d) {
            document.setLastUseTime(nanoTime);
            minHeap.reHeapify(document);
        }

        return d;
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
        List<Document> d = documentTrie.getAllWithPrefixSorted(key, (doc1, doc2) -> {
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

        long nanoTime = System.nanoTime();

        for (Document document : d) {
            document.setLastUseTime(nanoTime);
            minHeap.reHeapify(document);
        }

        return d;
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
            Document oldValue = putDocument(null, uri);
            commandSet.addCommand(new GenericCommand<URI>(uri, x -> {
                this.putDocument(oldValue, x);
                oldValue.setLastUseTime(nanoTimeStore.t);
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
            Document oldValue = putDocument(null, uri);
            commandSet.addCommand(new GenericCommand<URI>(uri, x -> {
                this.putDocument(oldValue, x);
                oldValue.setLastUseTime(nanoTimeStore.t);
                return true;
            }));
            r.add(uri);
        }
        commandStack.push(commandSet);
        return r;
    }

    /**
     * set maximum number of documents that may be stored
     * @param limit
     */
    public void setMaxDocumentCount(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException();
        }
        maxDocumentCount = limit;
        
        while (documentCount > maxDocumentCount) {
            Document docToDelete = minHeap.remove();
            putDocument(null, docToDelete.getKey());
            removeFromUndoCommands(docToDelete.getKey());
        }
    }

    /**
     * set maximum number of bytes of memory that may be used by all the documents in memory combined
     * @param limit
     */
    public void setMaxDocumentBytes(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException();
        }
        maxDocumentBytes = limit;
        
        while (bytesStored > maxDocumentBytes) {
            Document docToDelete = minHeap.remove();
            putDocument(null, docToDelete.getKey());
            removeFromUndoCommands(docToDelete.getKey());
        }
    }

    @SuppressWarnings("unchecked")
    private void removeFromUndoCommands(URI uri) {
        Stack<Undoable> helperStack = new StackImpl<>();
        while (commandStack.size() > 0) {
            helperStack.push(commandStack.pop());
        }

        while (helperStack.size() > 0) {
            Undoable c = helperStack.pop();

            if (c instanceof CommandSet && ((CommandSet<URI>) c).containsTarget(uri)) {
                Iterator<GenericCommand<URI>> i = ((CommandSet<URI>) c).iterator();
                while (i.hasNext()) {
                    GenericCommand<URI> gc = i.next();
                    if (gc.getTarget().equals(uri)) {
                        i.remove();
                    }
                }
                if (((CommandSet<URI>) c).isEmpty()) {
                    continue;
                }
            } else if (c instanceof GenericCommand && ((GenericCommand<URI>) c).getTarget().equals(uri)) {
                continue;
            }

            commandStack.push(c);
        }
    }
}