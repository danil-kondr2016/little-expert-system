package ru.danilakondr.les.knowbase;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

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

    @JsonCreator
    public KnowledgeBase(
            @JsonProperty("comment") String comment,
            @JsonProperty("questions") List<String> questions,
            @JsonProperty("hypotheses") List<Hypothesis> hypotheses)
    {
        this.comment = comment;
        this.questions = questions;
        this.hypotheses = hypotheses;
    }

    /**
     * Получить комментарий.
     */
    @JsonGetter("comment")
    public String getComment() {
        return comment;
    }

    /**
     * Установить комментарий.
     */
    @JsonSetter("comment")
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Получить список вопросов.
     */
    @JsonGetter("questions")
    public List<String> getQuestions() {
        return questions;
    }

    /**
     * Получить список гипотез.
     */
    @JsonGetter("hypotheses")
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
