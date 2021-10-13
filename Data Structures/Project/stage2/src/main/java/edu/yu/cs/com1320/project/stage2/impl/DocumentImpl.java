package edu.yu.cs.com1320.project.stage2.impl;

import java.net.URI;
import java.util.Arrays;

import edu.yu.cs.com1320.project.stage2.Document;

public class DocumentImpl implements Document {

    private URI identifier;
    private String txtContents;
    private byte[] binaryContents;

    public DocumentImpl(URI uri, String txt) {
        if (uri == null || txt == null || txt.isBlank() || uri.toString().isBlank()) {
            throw new IllegalArgumentException();
        }

        this.identifier = uri;
        txtContents = txt;
    }

    public DocumentImpl(URI uri, byte[] binaryData) {
        if (uri == null || binaryData == null || binaryData.length == 0 || uri.toString().isBlank()) {
            throw new IllegalArgumentException();
        }

        identifier = uri;
        binaryContents = binaryData;
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
