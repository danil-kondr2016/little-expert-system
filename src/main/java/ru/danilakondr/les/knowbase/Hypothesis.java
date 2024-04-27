package ru.danilakondr.les.knowbase;

import java.util.HashMap;
import java.util.Map;

/**
 * Гипотеза. Модель соответствует Малой экспертной системе 2.0.
 *
 * <p> Для каждой гипотезы известно название, отображаемое пользователю,
 * её априорная вероятность P и пары вероятностей для всех указанных вопросов.
 *
 * @see ProbabilityPair
 */
public class Hypothesis {
    private final String name;
    private final float pPrior;

    private final Map<Integer, ProbabilityPair> answers;

    /**
     * Конструктор гипотезы.
     * @param name имя
     * @param pPrior априорная вероятность
     */
    public Hypothesis(String name, float pPrior) {
        this.name = name;
        this.pPrior = pPrior;
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
    public float pPrior() {
        return pPrior;
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
    public ProbabilityPair getAnswerPair(int question) {
        return answers.get(question);
    }
}
