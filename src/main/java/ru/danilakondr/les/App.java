package ru.danilakondr.les;

import ru.danilakondr.les.knowbase.KnowledgeBase;
import ru.danilakondr.les.knowbase.ProbabilityPair;
import ru.danilakondr.les.parser.MKBParser;

import java.util.Locale;
import java.util.Scanner;

public class App 
{
    private static final String MKB_TEST =
            "The internal test file. Created at 2024-04-25\r\n" +
                    "\r\n" +
                    "Questions:\r\n" +
                    "Question 1\r\n" +
                    "Question 2\r\n" +
                    "\r\n" +
                    "Hypothesis 1, 0.5, 1,1,0, 2,0,1\r\n" +
                    "Hypothesis 2, 0.5, 1,0,1, 2,1,0\r\n";

    public static void main( String[] args )
    {
        Scanner sc = new Scanner(MKB_TEST);
        MKBParser parser = new MKBParser(sc);
        KnowledgeBase kbTest = parser.parse();

        System.out.printf("Comment: %s%n", kbTest.getComment());
        System.out.println("Questions: ");
        kbTest.getQuestions().forEach((x) -> System.out.println("  " + x));
        System.out.println("Hypotheses: ");
        kbTest.getHypotheses().forEach((h) -> {
            System.out.println("  Name: " + h.name());
            System.out.println("  Prior probability: " + h.pPrior());
            System.out.println("  Question references: ");
            for (int i = 0; i < kbTest.getQuestions().size(); i++) {
                ProbabilityPair p = h.getAnswer(i);
                if (p == null)
                    continue;
                System.out.printf(Locale.US, "    %d: y=%f; n=%f%n", i, p.yes(), p.no());
            }
        });
    }
}
