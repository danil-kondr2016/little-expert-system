package ru.danilakondr.les;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import ru.danilakondr.les.knowbase.KnowledgeBase;
import ru.danilakondr.les.parser.MKBParser;

import java.util.Scanner;

public class MKBParserTest
{
    @Test
    public void dummyFullTest() {
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

    @Test
    public void emptyCommnent() {
        final String MKB_TEST =
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

        assertEquals("", kb.getComment());
        assertEquals("Questions:", kb.getQuestions().get(0));
        assertEquals("Question 1", kb.getQuestions().get(1));
        assertEquals("Question 2", kb.getQuestions().get(2));
    }
}
