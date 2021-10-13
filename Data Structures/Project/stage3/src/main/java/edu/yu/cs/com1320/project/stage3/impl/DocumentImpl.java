package edu.yu.cs.com1320.project.stage3.impl;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.yu.cs.com1320.project.stage3.Document;

public class DocumentImpl implements Document {

    private URI identifier;
    private String txtContents;
    private byte[] binaryContents;
    private HashMap<String, Integer> wordCountMap;

    public DocumentImpl(URI uri, String txt) {
        if (uri == null || txt == null || txt.isBlank() || uri.toString().isBlank()) {
            throw new IllegalArgumentException();
        }

        this.identifier = uri;
        txtContents = txt;
        wordCountMap = new HashMap<>();

        // count and store how many times each word appears
        String s = txtContents.toLowerCase().replaceAll("[^a-zA-Z0-9 ]", "");
        String[] words = s.split("\\s+");
        for (String word : words) {
            if (!word.isBlank()) {
                wordCountMap.put(word, wordCountMap.getOrDefault(word, 0)+1);
            }
        }
    }

    public DocumentImpl(URI uri, byte[] binaryData) {
        if (uri == null || binaryData == null || binaryData.length == 0 || uri.toString().isBlank()) {
            throw new IllegalArgumentException();
        }

        identifier = uri;
        binaryContents = binaryData;
        wordCountMap = new HashMap<>();
    }

    /**
     * @return content of text document
     */
    public String getDocumentTxt() {
        return txtContents;
    }

    /**
     * @return content of binary data document
     */
    public byte[] getDocumentBinaryData() {
        return binaryContents;
    }

    /**
     * @return URI which uniquely identifies this document
     */
    public URI getKey() {
        return identifier;
    }

    /**
     * how many times does the given word appear in the document?
     * @param word
     * @return the number of times the given words appears in the document. If it's a binary document, return 0.
     */
    public int wordCount(String word) {
        return this.wordCountMap.getOrDefault(word.toLowerCase().replaceAll("[^a-zA-Z0-9 ]", ""), 0);
    }

    /**
     * @return all the words that appear in the document
     */
    public Set<String> getWords() {
        return new HashSet<>(wordCountMap.keySet());
    }

    @Override
    public int hashCode() {
        int result = identifier.hashCode();
        result = 31 * result + (txtContents != null ? txtContents.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(binaryContents);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode();
    }
}
