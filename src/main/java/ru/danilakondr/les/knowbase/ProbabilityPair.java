package ru.danilakondr.les.knowbase;

import java.util.Objects;

/**
 * Пара вероятностей: вероятность P<sub>y</sub> = P(E/Y) и P<sub>n</sub> =
 * P(E/!Y).
 */
public class ProbabilityPair {
    private final float yes, no;

    /**
     * Конструктор пары вероятностей.
     * @param yes вероятность "да" (P(E/Y))
     * @param no вероятность "нет" (P(E/!Y))
     */
    public ProbabilityPair(float yes, float no) {
        this.yes = yes;
        this.no = no;
    }

    /**
     * Получить вероятность "да".
     */
    public float yes() {
        return yes;
    }

    /**
     * Получить вероятность "нет".
     */
    public float no() {
        return no;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;

        ProbabilityPair p = (ProbabilityPair) obj;
        return yes == p.yes && no == p.no;
    }

    @Override
    public String toString() {
        return String.format("[ProbabilityPair: %f, %f]", yes, no);
    }

    @Override
    public int hashCode() {
        return Objects.hash(yes, no);
    }
}
