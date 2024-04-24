package ru.danilakondr.les.knowbase;

import java.util.Objects;

public class ProbabilityPair {
    private final float yes, no;

    public ProbabilityPair(float yes, float no) {
        this.yes = yes;
        this.no = no;
    }

    public float yes() {
        return yes;
    }

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
