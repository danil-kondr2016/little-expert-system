package ru.danilakondr.les;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class LocalizedMessages {
    private static final ResourceBundle bundle = ResourceBundle.getBundle("LesStrings");

    public static String mkbObfuscatedNotice() {
        return bundle.getString("mkb.obfuscated");
    }

    public static String invalidQuestionReference(int question, int lineIndex) {
        return MessageFormat.format(
                bundle.getString("parser.invalidQuestionReference"),
                question, lineIndex);
    }

    public static String syntaxError(int row) {
        return MessageFormat.format(
                bundle.getString("parser.syntaxError"),
                row
        );
    }

    public static String knowledgeBaseHasNotBeenLoaded() {
        return bundle.getString("les.knowledgeBaseHasNotBeenLoaded");
    }

    public static String yesSmallerThanNo() {
        return bundle.getString("les.yesSmallerThanNo");
    }
}
