package ru.danilakondr.les;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import ru.danilakondr.les.knowbase.KnowledgeBase;
import ru.danilakondr.les.mkb.MKBParser;

import java.util.Scanner;

public class JSONKBTest {
    @Test
    public void jsonKb() throws JsonProcessingException {
        final String MKB_TEST =
                "DUMMY_COMMENT\r\n" +
                "\r\n" +
                "Questions:\r\n" +
                "Question 1\r\n" +
                "Question 2\r\n" +
                "\r\n" +
                "H1, 0.5, 1,0,1, 2,1,0\r\n" +
                "H2, 0.5, 1,1,0, 2,0,1";

        Scanner sc = new Scanner(MKB_TEST);
        MKBParser parser = new MKBParser(sc);
        KnowledgeBase kb = parser.parse();

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(kb);

        System.out.println(json);
        assertEquals("{" +
                "\"comment\":\"DUMMY_COMMENT\"," +
                "\"questions\":[" +
                    "\"Questions:\"," +
                    "\"Question 1\"," +
                    "\"Question 2\"" +
                "],\"hypotheses\":[" +
                    "{\"name\":\"H1\",\"pPrior\":0.5,\"answers\":{" +
                    "\"1\":{\"yes\":0.0,\"no\":1.0}," +
                    "\"2\":{\"yes\":1.0,\"no\":0.0}" +
                    "}}," +
                    "{\"name\":\"H2\",\"pPrior\":0.5,\"answers\":{" +
                    "\"1\":{\"yes\":1.0,\"no\":0.0}," +
                    "\"2\":{\"yes\":0.0,\"no\":1.0}}" +
                "}]}", json);
    }

    @Test
    public void deJsonKb() throws JsonProcessingException {
        final String JSON = "{\"comment\":\"DUMMY_COMMENT\",\"questions\":[\"Questions:\",\"Question 1\",\"Question 2\"],\"hypotheses\":[{\"name\":\"H1\",\"pPrior\":0.5,\"answers\":{\"1\":{\"yes\":0.0,\"no\":1.0},\"2\":{\"yes\":1.0,\"no\":0.0}}},{\"name\":\"H2\",\"pPrior\":0.5,\"answers\":{\"1\":{\"yes\":1.0,\"no\":0.0},\"2\":{\"yes\":0.0,\"no\":1.0}}}]}";
        ObjectMapper mapper = new ObjectMapper();

        KnowledgeBase kb = mapper.readValue(JSON, KnowledgeBase.class);
        assertEquals("DUMMY_COMMENT", kb.getComment());
        assertEquals("Questions:", kb.getQuestions().get(0));
        assertEquals("Question 1", kb.getQuestions().get(1));
        assertEquals("Question 2", kb.getQuestions().get(2));
        assertEquals("H1", kb.getHypotheses().get(0).name());
        assertEquals(0.5, kb.getHypotheses().get(0).pPrior(), 0.01);
        assertEquals(0, kb.getHypotheses().get(0).getAnswerPair(1).yes(), 0.01);
        assertEquals(1, kb.getHypotheses().get(0).getAnswerPair(1).no(), 0.01);
        assertEquals(1, kb.getHypotheses().get(0).getAnswerPair(2).yes(), 0.01);
        assertEquals(0, kb.getHypotheses().get(0).getAnswerPair(2).no(), 0.01);
        assertEquals("H2", kb.getHypotheses().get(1).name());
        assertEquals(0.5, kb.getHypotheses().get(1).pPrior(), 0.01);
        assertEquals(1, kb.getHypotheses().get(1).getAnswerPair(1).yes(), 0.01);
        assertEquals(0, kb.getHypotheses().get(1).getAnswerPair(1).no(), 0.01);
        assertEquals(0, kb.getHypotheses().get(1).getAnswerPair(2).yes(), 0.01);
        assertEquals(1, kb.getHypotheses().get(1).getAnswerPair(2).no(), 0.01);
    }
}
