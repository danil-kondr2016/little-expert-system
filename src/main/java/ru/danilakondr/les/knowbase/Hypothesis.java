package ru.danilakondr.les.knowbase;

import java.util.HashMap;
import java.util.Map;

/**
 * Гипотеза.
 */
public class Hypothesis {
    private final String name;
    private final float pApriori;

    private final Map<Integer, ProbabilityPair> answers;

    /**
     * Конструктор гипотезы.
     * @param name имя
     * @param pApriori априорная вероятность
     */
    public Hypothesis(String name, float pApriori) {
        this.name = name;
        this.pApriori = pApriori;
        this.answers = new HashMap<>();
    }

    /**
     * Получить название.
     */
    public String name() {
        return name;
    }

    /**
     * Получить априорную вероятность.
     */
    public float pApriori() {
        return pApriori;
    }

    /**
     * Добавить пару вероятностей для заданного вопроса.
     * @param question номер вопроса
     * @param yes вероятность "да" (P(E/Y))
     * @param no вероятность "нет" (P(E/!Y))
     */
    public void putAnswer(int question, float yes, float no) {
        answers.put(question, new ProbabilityPair(yes, no));
    }

    /**
     * Получить пару вероятностей по заданному вопросу.
     * @param question номер вопроса
     */
    public ProbabilityPair getAnswer(int question) {
        return answers.get(question);
    }
}
