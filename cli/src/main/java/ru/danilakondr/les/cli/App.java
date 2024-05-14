package ru.danilakondr.les.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import ru.danilakondr.les.expert.LittleExpertSystem;
import ru.danilakondr.les.knowbase.KnowledgeBase;
import ru.danilakondr.les.knowbase.KnowledgeBaseReader;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class App
{
    private static void runKnowledgeBase(KnowledgeBase kb) {
        LittleExpertSystem les = new LittleExpertSystem();
        les.loadKnowledgeBase(kb);

        Scanner cin = new Scanner(System.in);
        les.start();
        while (les.isRunning()) {
            int q = les.getCurrentQuestion();
            String qString = kb.getQuestions().get(q);

            float[] values = les.getValues();
            for (int i = 0; i < values.length; i++)
                System.out.printf("%s: %f%n", kb.getHypotheses().get(i).name(), values[i]);

            System.out.printf("Q %d: %s%n", q, qString);
            float level = cin.nextFloat();
            les.answer(level);
        }

        float[] values = les.getValues();
        for (int i = 0; i < values.length; i++)
            System.out.printf("%s: %f%n", kb.getHypotheses().get(i).name(), values[i]);
    }

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(null, args);
            String input = cmd.getArgs()[0];

            KnowledgeBaseReader reader = new KnowledgeBaseReader(new File(input));
            KnowledgeBase kb = reader.read();

            runKnowledgeBase(kb);
        }
        catch (ParseException | IOException e) {
            e.printStackTrace(System.err);
        }
    }
}
