package ru.danilakondr.les.knowbase;

import java.util.ArrayList;
import java.util.List;

/**
 * База знаний. Модель соответствует базам знаний Малой экспертной системы 2.0.
 */
public class KnowledgeBase {
    private String comment;
    private final List<String> questions;
    private final List<Hypothesis> hypotheses;

    public KnowledgeBase() {
        comment = "";
        questions = new ArrayList<>();
        hypotheses = new ArrayList<>();
    }

    /**
     * Получить комментарий.
     */
    public String getComment() {
        return comment;
    }

    /**
     * Установить комментарий.
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Получить список вопросов.
     */
    public List<String> getQuestions() {
        return questions;
    }

    /**
     * Получить список гипотез.
     */
    public List<Hypothesis> getHypotheses() {
        return hypotheses;
    }

    /**
     * Добавить вопрос в список.
     * @param question строка с вопросом
     */
    public void addQuestion(String question) {
        questions.add(question);
    }

    /**
     * Добавить гипотезу в список.
     * @param hypothesis гипотеза
     */
    public void addHypothesis(Hypothesis hypothesis) {
        hypotheses.add(hypothesis);
    }

    /**
     * Очистить базу знаний.
     */
    public void clear() {
        comment = "";
        questions.clear();
        hypotheses.clear();
    }
}
