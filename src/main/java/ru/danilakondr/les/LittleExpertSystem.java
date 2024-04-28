package ru.danilakondr.les;

import ru.danilakondr.les.knowbase.Hypothesis;
import ru.danilakondr.les.knowbase.KnowledgeBase;
import ru.danilakondr.les.knowbase.ProbabilityPair;

import java.util.BitSet;

/**
 * Главный класс Малой экспертной системы.
 * <p>
 * Малая экспертная система в основе своей использует теорему Байеса для
 * подсчёта вероятностей исходов. После запуска система задаёт вопрос и получает
 * ответ в виде уровня уверенности - по умолчанию от -5 до 5.
 * <p>
 * После принятия ответа вероятности пересчитываются следующим образом.
 * Подсчитываются два значения вероятности:
 * <p>P(H/E) = P*Py / (P*Py + (1-P)*Pn)</p>
 * <p>P(H/!E) = P*(1-Py)/(P*(1-Py) + (1-P)*(1-Pn)</p>
 * <p>
 * Далее новое значение интерполируется в зависимости от уровня уверенности:
 * если уровень меньше среднего (уровень "не знаю"), то он интерполируется в
 * сторону P(H/!E), иначе - в сторону P(H/E).
 * <p>
 *     <strong>NB: </strong>
 * Выбор вопроса на данный момент осуществляется по порядку, однако в
 * оригинальной Малой экспертной системе 2.0 используется другой способ,
 * который каким-либо образом основан на значениях вероятностей в базе знаний.
 */
public class LittleExpertSystem {
    private KnowledgeBase kb = null;
    private float[] values = null;
    private BitSet used;
    private float yesLevel = -5.0f, noLevel = 5.0f, dunno = 0.0f;
    private int currentQuestion;

    private boolean running = false;

    public LittleExpertSystem() {
        this.used = new BitSet();
    }

    /**
     * Загрузить базу знаний.
     * @param kb база знаний
     */
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
        if (yes < no)
            throw new IllegalArgumentException("Yes level is smaller than no level");
        this.yesLevel = yes;
        this.noLevel = no;
        this.dunno = (yes + no) / 2.0f;
    }

    /**
     * Получить значения вероятностей гипотез.
     */
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

            if (normConfidence == 0)
                return;
            else if (normConfidence > 0)
                values[i] = values[i] + (pE - values[i]) * (normConfidence);
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
