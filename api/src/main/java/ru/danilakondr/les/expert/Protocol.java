package ru.danilakondr.les.expert;

import ru.danilakondr.les.knowbase.Hypothesis;
import ru.danilakondr.les.knowbase.KnowledgeBase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Protocol {
    static class Entry {
        public final String question;
        public final float confidence;

        public Entry(String question, float confidence) {
            this.question = question;
            this.confidence = confidence;
        }
    }
    static class HypothesisResult {
        public final String text;
        public final float probability;

        HypothesisResult(String text, float probability) {
            this.text = text;
            this.probability = probability;
        }
    }

    private final List<Entry> entries;
    private final List<HypothesisResult> results;
    private float noLevel, yesLevel;
    private LocalDateTime start, end;
    private String name, comment;
    private KnowledgeBase currentKb;
    private boolean completed;

    public Protocol() {
        entries = new ArrayList<>();
        results = new ArrayList<>();
    }

    public float getNoLevel() {
        return noLevel;
    }

    public float getYesLevel() {
        return yesLevel;
    }

    public void setNoLevel(float noLevel) {
        this.noLevel = noLevel;
    }

    public void setYesLevel(float yesLevel) {
        this.yesLevel = yesLevel;
    }

    public void begin(KnowledgeBase kb) {
        this.currentKb = kb;
        this.comment = kb.getComment();
        this.start = LocalDateTime.now();
        completed = false;
    }

    public void transferResults(float[] values) {
        if (values.length != currentKb.getHypotheses().size())
            throw new IllegalArgumentException();

        results.clear();

        List<Hypothesis> hypotheses = currentKb.getHypotheses();
        for (int i = 0; i < values.length; i++) {
            results.add(new HypothesisResult(hypotheses.get(i).name(), values[i]));
        }
    }

    public void end() {
        this.currentKb = null;
        this.end = LocalDateTime.now();
        completed = true;
    }

    public void addRecord(int question, float confidence) {
        entries.add(new Entry(currentKb.getQuestions().get(question), confidence));
    }

    public String toPlainText() {
        if (!completed)
            throw new IllegalStateException();

        StringBuilder proto = new StringBuilder();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        proto.append("Протокол консультаций с Малой экспертной системой\r\n");

        proto.append("Начало: ").append(format.format(start)).append("\r\n");
        proto.append("Окончание: ").append(format.format(end)).append("\r\n");
        proto.append(String.format("Диапазон коэффициента уверенности: [%f;%f]\r\n", yesLevel, noLevel));

        proto.append("\r\n");
        proto.append("-----------------------\r\n");
        proto.append(comment).append("\r\n");
        proto.append("-----------------------\r\n");

        proto.append("Обработанные свидетельства:\r\n");
        int i = 0;
        for (Entry e: entries) {
            proto.append(String.format("  %d) %s (%.2f)\r\n", i+1, e.question, e.confidence));
        }

        proto.append("\r\n");
        proto.append("-----------------------\r\n");
        proto.append("Результат консультации:\r\n");

        for (HypothesisResult r: results) {
            proto.append(String.format("- (%.5f) %s\r\n", r.probability, r.text));
        }

        return proto.toString();
    }
}
