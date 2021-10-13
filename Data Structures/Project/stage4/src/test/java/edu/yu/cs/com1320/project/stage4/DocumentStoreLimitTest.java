package edu.yu.cs.com1320.project.stage4;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.yu.cs.com1320.project.stage4.DocumentStore.DocumentFormat;
import edu.yu.cs.com1320.project.stage4.impl.DocumentStoreImpl;

public class DocumentStoreLimitTest {
    URI[] uriArray = new URI[21];
    Document[] docArray = new Document[21];
    String[] stringArray = { "The blue parrot drove by the hitchhiking mongoose.",
            "She thought there'd be sufficient time if she hid her watch.",
            "Choosing to do nothing is still a choice, after all.",
            "He found the chocolate covered roaches quite tasty.",
            "The efficiency we have at removing trash has made creating trash more acceptable.",
            "Peanuts don't grow on trees, but cashews do.",
            "A song can make or ruin a person’s day if they let it get to them.",
            "You bite up because of your lower jaw.",
            "He realized there had been several deaths on this road, but his concern rose when he saw the exact number.",
            "So long and thanks for the fish.", "Three years later, the coffin was still full of Jello.",
            "Weather is not trivial - it's especially important when you're standing in it.",
            "He walked into the basement with the horror movie from the night before playing in his head.",
            "He wondered if it could be called a beach if there was no sand.",
            "Jeanne wished she has chosen the red button.",
            "It's much more difficult to play tennis with a bowling ball than it is to bowl with a tennis ball.",
            "Pat ordered a ghost pepper pie on top.",
            "Everyone says they love nature until they realize how dangerous she can be.",
            "The memory we used to share is no longer coherent.",
            "My harvest will come Tiny valorous straw Among the millions Facing to the sun",
            "A dreamy-eyed child staring into night On a journey to storyteller's mind Whispers a wish speaks with the stars the words are silent in him" };

    @BeforeEach
    void init() {
        for (int i = 0; i < 21; i++) {
            uriArray[i] = URI.create("www.uri" + i + ".com");
        }
    }

    @Test
    void setDocLimitTest() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();
        for (int i = 0; i < 21; i++) {
            documentStore.putDocument(new ByteArrayInputStream(stringArray[i].getBytes()), uriArray[i], DocumentFormat.TXT);
        }

        documentStore.setMaxDocumentCount(20);
        assertEquals(19, documentStore.searchByPrefix("t").size());
        assertNull(documentStore.getDocument(uriArray[0]));
        
        assertEquals(1, documentStore.search("bite").size());
        assertEquals(19, documentStore.searchByPrefix("t").size());
        documentStore.setMaxDocumentCount(19);
        assertEquals(0, documentStore.search("bite").size());
        assertEquals(19, documentStore.searchByPrefix("t").size());
        documentStore.setMaxDocumentCount(1);
        assertEquals(1, documentStore.searchByPrefix("t").size());
        documentStore.setMaxDocumentCount(0);
        assertEquals(0, documentStore.searchByPrefix("t").size());
    }

    @Test
    void docLimitTest() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();
        
        documentStore.setMaxDocumentCount(2);
        documentStore.putDocument(new ByteArrayInputStream(stringArray[0].getBytes()), uriArray[0], DocumentFormat.TXT);
        assertEquals(stringArray[0], documentStore.getDocument(uriArray[0]).getDocumentTxt());
        
        documentStore.putDocument(new ByteArrayInputStream(stringArray[1].getBytes()), uriArray[1], DocumentFormat.TXT);
        assertEquals(stringArray[0], documentStore.getDocument(uriArray[0]).getDocumentTxt());
        assertEquals(stringArray[1], documentStore.getDocument(uriArray[1]).getDocumentTxt());
        
        documentStore.putDocument(new ByteArrayInputStream(stringArray[2].getBytes()), uriArray[2], DocumentFormat.TXT);
        assertNull(documentStore.getDocument(uriArray[0]));
        assertEquals(stringArray[1], documentStore.getDocument(uriArray[1]).getDocumentTxt());
        assertEquals(stringArray[2], documentStore.getDocument(uriArray[2]).getDocumentTxt());

        documentStore.putDocument(new ByteArrayInputStream(stringArray[3].getBytes()), uriArray[3], DocumentFormat.TXT);
        assertNull(documentStore.getDocument(uriArray[0]));
        assertNull(documentStore.getDocument(uriArray[1]));
        assertEquals(stringArray[2], documentStore.getDocument(uriArray[2]).getDocumentTxt());
        assertEquals(stringArray[3], documentStore.getDocument(uriArray[3]).getDocumentTxt());
    }
}
