package edu.yu.cs.com1320.project.professorsTests.stage4.impl;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URI;

import edu.yu.cs.com1320.project.stage5.DocumentStore.DocumentFormat;
import edu.yu.cs.com1320.project.stage5.impl.DocumentStoreImpl;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DocumentStoreAPITest {

    
    //STAGE 4 TESTS
    @Test
    public void stage4SetMaxDocumentCountExists(){
        new DocumentStoreImpl().setMaxDocumentCount(1);
    }
    @Test
    public void stage4SetMaxDocumentBytesExists(){
        new DocumentStoreImpl().setMaxDocumentBytes(1);
    }
    //STAGE 3 TESTS

    @Test
    public void stage3SearchExists(){
            new DocumentStoreImpl().search("test");
    }
    @Test
    public void stage3DeleteAllExists(){
        new DocumentStoreImpl().deleteAll("test");
    }
    @Test
    public void stage3SearchByPrefixExists(){
        new DocumentStoreImpl().searchByPrefix("test");
    }
    @Test
    public void stage3DeleteAllWithPrefixExists(){
        new DocumentStoreImpl().deleteAllWithPrefix("test");
    }

    //stage 2 tests
    @Test
    public void stage2UndoExists(){
        try {
            new DocumentStoreImpl().undo();
        } catch (Exception e) {}
    }

    @Test
    public void stage2UndoByURIExists(){
        try {
            new DocumentStoreImpl().undo(new URI("hi"));
        } catch (Exception e) {}
    }


    //stage 1 tests
        @Test
    public void fieldCount() {
        Field[] fields = DocumentStoreImpl.class.getFields();
        int publicFieldCount = 0;
        for (Field field : fields) {
            if (Modifier.isPublic(field.getModifiers())) {
                publicFieldCount++;
            }
        }
        assertTrue(publicFieldCount == 0);
    }

    @Test
    public void subClassCount() {
        @SuppressWarnings("rawtypes")
        Class[] classes = DocumentStoreImpl.class.getClasses();
        assertTrue(classes.length == 0);
    }

    @Test
    public void constructorExists() {
        try {
            new DocumentStoreImpl();
        } catch (Exception e) {}
    }

    @Test
    public void putDocumentExists() throws URISyntaxException{
        try {
            new DocumentStoreImpl().putDocument(null, new URI("hi"), DocumentFormat.BINARY);
        } catch (Exception e) {}
    }

    @Test
    public void getDocumentExists() throws URISyntaxException{
        try {
            new DocumentStoreImpl().getDocument(new URI("hi"));
        } catch (Exception e) {}
    }

    @Test
    public void deleteDocumentExists() throws URISyntaxException {
        try {
            new DocumentStoreImpl().deleteDocument(new URI("hi"));
        } catch (Exception e) {}
    }

}