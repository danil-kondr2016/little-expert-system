package ru.danilakondr.les.parser;

public class InvalidQuestionReferenceException extends IllegalArgumentException {
    private final int question, lineNumber;

    public InvalidQuestionReferenceException(int question, int lineNumber) {
        this.question = question;
        this.lineNumber = lineNumber;
    }

    @Override
    public String getMessage() {
        return String.format("Invalid reference to question #%d at line %d", question, lineNumber);
    }

    @Override
    public String getLocalizedMessage() {
        return getMessage();
    }
}
