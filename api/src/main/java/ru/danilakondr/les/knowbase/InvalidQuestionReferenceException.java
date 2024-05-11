package ru.danilakondr.les.knowbase;

import ru.danilakondr.les.LocalizedMessages;
import ru.danilakondr.les.OriginalMessages;

public class InvalidQuestionReferenceException extends IllegalArgumentException {
    private final int question, lineNumber;

    public InvalidQuestionReferenceException(int question, int lineNumber) {
        this.question = question;
        this.lineNumber = lineNumber;
    }

    public InvalidQuestionReferenceException(int question) {
        this(question, -1);
    }

    @Override
    public String getMessage() {
        return OriginalMessages.invalidQuestionReference(question, lineNumber);
    }

    @Override
    public String getLocalizedMessage() {
        return LocalizedMessages.invalidQuestionReference(question, lineNumber);
    }
}
