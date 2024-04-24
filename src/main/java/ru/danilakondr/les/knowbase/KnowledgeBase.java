package ru.danilakondr.les.knowbase;

import java.util.ArrayList;
import java.util.List;

public class KnowledgeBase {
    private String comment;
    private final List<String> questions;
    private final List<Hypothesis> hypotheses;

    public KnowledgeBase() {
        comment = "";
        questions = new ArrayList<>();
        hypotheses = new ArrayList<>();
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<String> getQuestions() {
        return questions;
    }

    public List<Hypothesis> getHypotheses() {
        return hypotheses;
    }

    public void addQuestion(String question) {
        questions.add(question);
    }

    public void addHypothesis(Hypothesis hypothesis) {
        hypotheses.add(hypothesis);
    }

    public void clear() {
        comment = "";
        questions.clear();
        hypotheses.clear();
    }
}
