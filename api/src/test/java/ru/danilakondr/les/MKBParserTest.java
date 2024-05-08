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
                        "H1, 1,0,1, 2,1,0\r\n" +
                        "H2, 1,1,0, 2,0,1";

        Scanner sc = new Scanner(MKB_TEST);
        MKBParser parser = new MKBParser(sc);
        KnowledgeBase kb = parser.parse();

        assertEquals("DUMMY_COMMENT", kb.getComment());
    }

    @Test
    public void emptyCommnent() {
        final String MKB_TEST =
                        "\r\n" +
                        "Questions:\r\n" +
                        "Question 1\r\n" +
                        "Question 2\r\n" +
                        "\r\n" +
                        "H1, 1,0,1, 2,1,0\r\n" +
                        "H2, 1,1,0, 2,0,1";

        Scanner sc = new Scanner(MKB_TEST);
        MKBParser parser = new MKBParser(sc);
        KnowledgeBase kb = parser.parse();

        assertEquals("", kb.getComment());
    }
}
