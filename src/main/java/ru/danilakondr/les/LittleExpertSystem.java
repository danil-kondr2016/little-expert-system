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
    private float yesLevel = 5.0f, noLevel = -5.0f, dunno = 0.0f;
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

    /**
     * Установить минимальный и максимальный уровни уверенности. Уровень "да" и
     * уровень "нет" устанавливаются пользователем. Уровень "не знаю"
     * устанавливается автоматически как их среднее арифметическое.
     * @param yes уровень "да"
     * @param no уровень "нет"
     * @throws IllegalArgumentException, если уровень "да" меньше уровня "нет".
     */
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

    /**
     * Получить номер текущего вопроса.
     */
    public int getCurrentQuestion() {
        return currentQuestion;
    }

    /**
     * Запустить экспертную систему.
     */
    public void start() {
        if (kb == null)
            throw new NullPointerException("Knowledge base has not been loaded");

        if (!nextQuestion())
            return;

        running = true;
    }

    /**
     * Принять ответ на вопрос.
     * @param confidence уровень уверенности
     */
    public void answer(float confidence) {
        if (kb == null)
            throw new NullPointerException("Knowledge base has not been loaded");
        if (!running)
            return;

        float normConfidence = (confidence - dunno) / (yesLevel - noLevel) + 0.5f;
        recalculate(normConfidence);

        if (!nextQuestion())
            running = false;
    }

    /**
     * Остановить экспертную систему.
     */
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

    /**
     * Получить состояние работы экспертной системы.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Перейти к следующему вопросу.
     */
    private boolean nextQuestion() {
        used.set(currentQuestion);
        currentQuestion = selectQuestion();
        if (currentQuestion == -1)
            return false;

        return true;
    }

    /**
     * Выбрать вопрос.
     * @return номер следующего вопроса, -1 - если вопрос не найден
     */
    private int selectQuestion() {
        int firstFree = 0;
        final int nQuestions = kb.getQuestions().size();
        final int nHypotheses = kb.getHypotheses().size();

        float[] ruleValue = new float[nQuestions];
        for (int hIndex = 0; hIndex < nHypotheses; hIndex++) {
            Hypothesis h = kb.getHypotheses().get(hIndex);

            for (int qIndex = 1; qIndex < nQuestions; qIndex++) {
                if (used.get(qIndex))
                    continue;

                ProbabilityPair p = h.getAnswerPair(qIndex);
                if (p == null)
                    continue;

                float py = posteriorYes(values[hIndex], p.yes(), p.no());
                float pn = posteriorNo(values[hIndex], p.yes(), p.no());
                ruleValue[qIndex] += Math.abs(py - pn);
            }
        }

        int maxIndex = -1;
        float max = 0;
        for (int i = 0; i < ruleValue.length; i++) {
            if (max < ruleValue[i]) {
                max = ruleValue[i];
                maxIndex = i;
            }
        }

        if (maxIndex == -1 || ruleValue[maxIndex] == 0)
            return -1;

        return maxIndex;
    }

    /**
     * Пересчитать вероятности.
     * @param normConfidence нормированный уровень уверенности в [-1.0; 1.0]
     */
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
                values[i] = values[i] + (values[i] - pNotE) * (-normConfidence);
        }
    }

    private float posteriorYes(float pPrior, float pY, float pN) {
        return (pY * pPrior) / (pY * pPrior + pN * (1 - pPrior));
    }

    private float posteriorNo(float pPrior, float pY, float pN) {
        return posteriorYes(pPrior, 1 - pY, 1 - pN);
    }
}
