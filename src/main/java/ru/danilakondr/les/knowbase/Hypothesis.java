package ru.danilakondr.les.knowbase;

import java.util.HashMap;
import java.util.Map;

public class Hypothesis {
    private final String name;
    private final float pApriori;

    private final Map<Integer, ProbabilityPair> answers;

    public Hypothesis(String name, float pApriori) {
        this.name = name;
        this.pApriori = pApriori;
        this.answers = new HashMap<>();
    }

    public String name() {
        return name;
    }

    public void putAnswer(int question, float yes, float no) {
        answers.put(question, new ProbabilityPair(yes, no));
    }

    public ProbabilityPair getAnswer(int question) {
        return answers.get(question);
    }
}
