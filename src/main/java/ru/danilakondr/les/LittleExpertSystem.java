package ru.danilakondr.les;

import ru.danilakondr.les.knowbase.Hypothesis;
import ru.danilakondr.les.knowbase.KnowledgeBase;
import ru.danilakondr.les.knowbase.ProbabilityPair;

import java.util.BitSet;

public class LittleExpertSystem {
    private KnowledgeBase kb = null;
    private float[] values = null;
    private BitSet used;
    private float yesLevel = -1.0f, noLevel = 1.0f, dunno = 0.0f;
    private int currentQuestion;

    private boolean running = false;

    public LittleExpertSystem() {
        this.used = new BitSet();
    }

    public void loadKnowledgeBase(KnowledgeBase kb) {
        this.kb = kb;
        this.used.clear();

        this.values = new float[kb.getHypotheses().size()];
        for (int i = 0; i < values.length; i++) {
            Hypothesis h = kb.getHypotheses().get(i);
            this.values[i] = h.pPrior();
        }

        this.currentQuestion = 0;
    }

    public void setLevel(float yes, float no) {
        this.yesLevel = yes;
        this.noLevel = no;
        this.dunno = no + (yes - no) / 2.0f;
    }

    public float[] getValues() {
        return values.clone();
    }

    public int getCurrentQuestion() {
        return currentQuestion;
    }

    public void start() {
        if (kb == null)
            throw new NullPointerException("Knowledge base has not been loaded");

        if (!nextQuestion())
            return;

        running = true;
    }

    public void answer(float confidence) {
        if (kb == null)
            throw new NullPointerException("Knowledge base has not been loaded");
        if (!running)
            return;

        float normConfidence = (confidence - noLevel) / (yesLevel - noLevel) - 1.0f;
        recalculate(normConfidence);

        if (!nextQuestion())
            running = false;
    }

    public void stop() {
        if (kb == null)
            throw new NullPointerException("Knowledge base has not been loaded");

        running = false;
    }

    public boolean hasAnyQuestions() {
        if (kb == null)
            throw new NullPointerException("Knowledge base has not been loaded");

        return currentQuestion != -1;
    }

    public boolean isRunning() {
        return running;
    }

    private boolean nextQuestion() {
        used.set(currentQuestion);
        currentQuestion = selectQuestion();
        if (currentQuestion == -1)
            return false;

        return true;
    }

    private int selectQuestion() {
        int firstFree = 0;

        for (; firstFree < kb.getQuestions().size(); firstFree++) {
            if (!used.get(firstFree))
                break;
        }

        if (firstFree >= kb.getQuestions().size())
            firstFree = -1;

        return firstFree;
    }

    private void recalculate(float normConfidence) {
        for (int i = 0; i < kb.getHypotheses().size(); i++) {
            Hypothesis h = kb.getHypotheses().get(i);
            ProbabilityPair p = h.getAnswerPair(currentQuestion);
            if (p == null)
                continue;

            float pE = posteriorYes(values[i], p.yes(), p.no());
            float pNotE = posteriorNo(values[i], p.yes(), p.no());

            if (normConfidence >= dunno)
                values[i] = values[i] + (pE - values[i]) * (normConfidence - dunno);
            else
                values[i] = values[i] + (values[i] - pNotE) * (dunno - normConfidence);
        }
    }

    private float posteriorYes(float pPrior, float pY, float pN) {
        return (pY * pPrior) / (pY * pPrior + pN * (1 - pPrior));
    }

    private float posteriorNo(float pPrior, float pY, float pN) {
        return posteriorYes(pPrior, 1 - pY, 1 - pN);
    }
}
