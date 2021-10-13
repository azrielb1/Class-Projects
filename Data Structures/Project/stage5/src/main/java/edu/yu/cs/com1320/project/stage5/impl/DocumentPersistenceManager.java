package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.Scanner;

import com.google.gson.Gson;

/**
 * created by the document store and given to the BTree via a call to
 * BTree.setPersistenceManager
 */
public class DocumentPersistenceManager implements PersistenceManager<URI, Document> {

    private final File baseDir;

    public DocumentPersistenceManager(File baseDir) {
        if (baseDir != null) {
            this.baseDir = baseDir;
        } else {
            this.baseDir = new File(System.getProperty("user.dir"));
        }
    }

    @Override
    public void serialize(URI uri, Document val) throws IOException {
        if (uri == null || val == null) {
            throw new IllegalArgumentException();
        }
        String path = uri.toString().replaceFirst("^(http[s]?://)", "").replaceFirst("/+$", "");
        File file = new File(baseDir, path + ".json");
        file.getParentFile().mkdirs();
        FileWriter writer = new FileWriter(file);
        Gson gson = new Gson();
        writer.write(gson.toJson(val));
        writer.close();
    }

    @Override
    public Document deserialize(URI uri) throws IOException {
        String path = uri.toString().replaceFirst("^(http[s]?://)", "").replaceFirst("/+$", "");
        Scanner scanner;
        try {
            scanner = new Scanner(new File(baseDir, path + ".json"));
        } catch (FileNotFoundException e) {
            return null;
        }
        Gson gson = new Gson();
        if (!scanner.hasNextLine()) {
            return null;
        }
        Document doc = gson.fromJson(scanner.nextLine(), DocumentImpl.class);
        doc.setLastUseTime(System.nanoTime());
        delete(uri);
        return doc;
    }

    @Override
    public boolean delete(URI uri) throws IOException {
        String path = uri.toString().replaceFirst("^(http[s]?://)", "").replaceFirst("/+$", "");
        File file = new File(baseDir, path + ".json");
        return file.delete();
    }
}