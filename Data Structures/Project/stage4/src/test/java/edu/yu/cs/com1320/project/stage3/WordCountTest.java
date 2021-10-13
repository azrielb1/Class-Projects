package edu.yu.cs.com1320.project.stage3;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import org.junit.Test;

import edu.yu.cs.com1320.project.stage4.Document;
import edu.yu.cs.com1320.project.stage4.impl.DocumentImpl;

public class WordCountTest {

    @Test
    public void TestWordCount() throws URISyntaxException {
        URI uri = new URI("hello");
        Document d = new DocumentImpl(uri, "This string has some words in this string");
        assertEquals(2, d.wordCount("this"));
        assertEquals(2, d.wordCount("STRING"));
        assertEquals(1, d.wordCount("sOme"));
        assertEquals(0, d.wordCount("nothere"));

        d = new DocumentImpl(uri, "word-word!word-word");
        assertEquals(0, d.wordCount("word"));
        assertEquals(1, d.wordCount("wordwordwordword"));
        assertEquals(0, d.wordCount("word"));

    }

    @Test
    public void testLongDocument() throws URISyntaxException {
        String s = "";
        for (int i = 0; i < 700; i++) {
            s += "word ";
        }
        URI uri = new URI("hello");
        Document d = new DocumentImpl(uri, s);
        assertEquals(700, d.wordCount("word"));
    }

    /**
     * from Ephraim Crystal
     */
    @Test
    public void wordCountAndGetWordsTest() throws URISyntaxException {
        DocumentImpl txtDoc = new DocumentImpl(new URI("placeholder"),
                " The!se ARE? sOme   W@o%$rds with^ s**ymbols (m)ixed [in]. Hope    this test test passes!");
        assertEquals(0, txtDoc.wordCount("bundle"));
        assertEquals(1, txtDoc.wordCount("these"));
        assertEquals(1, txtDoc.wordCount("WORDS"));
        assertEquals(1, txtDoc.wordCount("S-Y-M-B-O-??-LS"));
        assertEquals(1, txtDoc.wordCount("p@A$$sse$s"));
        assertEquals(2, txtDoc.wordCount("tEst"));
        Set<String> words = txtDoc.getWords();
        assertEquals(12, words.size());
        assertTrue(words.contains("some"));

        DocumentImpl binaryDoc = new DocumentImpl(new URI("0110"), new byte[] { 0, 1, 1, 0 });
        assertEquals(0, binaryDoc.wordCount("anythingYouPutHereShouldBeZero"));
        Set<String> words2 = binaryDoc.getWords();
        assertEquals(0, words2.size());
    }
}
