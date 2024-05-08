package ru.danilakondr.les.cli;

import ru.danilakondr.les.LittleExpertSystem;
import ru.danilakondr.les.knowbase.KnowledgeBase;
import ru.danilakondr.les.knowbase.ProbabilityPair;
import ru.danilakondr.les.parser.MKBParser;

import java.util.Locale;
import java.util.Scanner;

public class App 
{
    /*
     * Для теста была имплантирована база знаний по грибам из стандартной поставки
     * Малой экспертной системы 2.0.
     */
    private static final String MKB_TEST =
            "Пример базы знаний о грибах.\n" +
            "Автор Роман Бухнин.\n" +
            "Данная база знаний является далеко не полноценной и служит исключительно для демонстрационных целей.\n" +
            "\n" +
            "Признаки:\n" +
            "Гриб пластинчатый?\n" +
            "Гриб полностью жёлтый?\n" +
            "У гриба прочная толстая ножка?\n" +
            "Гриб полностью ярко-коричневый?\n" +
            "Шляпка красная?\n" +
            "Есть ли венчик на ножке?\n" +
            "\n" +
            "Лисичка,0.05, 1,1,0.5, 2,0.9,0.1, 3,0.03,0.3, 4,0,0.1, 5,0,0.25, 6,0,0.35\n" +
            "Свинух,0.05, 1,1,0.5, 2,0.005,0.2, 4,1,0.01, 5,0,0.25, 6,0,0.35\n" +
            "Подберёзовик,0.05, 1,0,0.5, 2,0,0.2, 3,0.25,0.3, 4,0.1,0.1, 5,0,0.25, 6,0,0.35\n" +
            "Подосиновик,0.05, 1,0,0.5, 2,0,0.2, 3,0.98,0.15, 4,0,0.1, 5,0.8,0.22, 6,0,0.35\n" +
            "Белый Гриб,0.05, 1,0,0.5, 2,0,0.2, 3,1,0.15, 4,0.001,0.1, 5,0.05,0.5, 6,0,0.35\n" +
            "Опёнок,0.05, 1,1,0.5, 2,0.7,0.15, 3,0,0.3, 4,0.15,0.1, 5,0,0.25\n" +
            "Мухомор Красный,0.05, 1,1,0.5, 2,0,0.2, 3,0.12,0.3, 4,0,0.1, 5,1,0.2, 6,1,0.3\n" +
            "Сыроежка,0.1, 1,1,0.5, 2,0.02,0.2, 3,0.1,0.3, 4,0,0.1, 5,0.3,0.2, 6,0,0.35\n" +
            "Маслёнок,0.05, 1,0,0.5, 2,0.25,0.2, 3,0.02,0.3, 4,0.05,0.1, 5,0.001,0.25, 6,0.7,0.3";

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
                ProbabilityPair p = h.getAnswerPair(i);
                if (p == null)
                    continue;
                System.out.printf(Locale.US, "    %d: y=%f; n=%f%n", i, p.yes(), p.no());
            }
        });

        System.out.println("This KB will be executed.");
        LittleExpertSystem les = new LittleExpertSystem();
        les.loadKnowledgeBase(kbTest);

        Scanner cin = new Scanner(System.in);
        les.start();
        while (les.isRunning()) {
            int q = les.getCurrentQuestion();
            String qString = kbTest.getQuestions().get(q);

            float[] values = les.getValues();
            for (int i = 0; i < values.length; i++)
                System.out.printf("%s: %f%n", kbTest.getHypotheses().get(i).name(), values[i]);

            System.out.printf("Q %d: %s%n", q, qString);
            float level = cin.nextFloat();
            les.answer(level);
        }

        float[] values = les.getValues();
        for (int i = 0; i < values.length; i++)
            System.out.printf("%s: %f%n", kbTest.getHypotheses().get(i).name(), values[i]);
    }
}
