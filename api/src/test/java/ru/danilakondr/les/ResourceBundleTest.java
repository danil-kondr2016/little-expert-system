package ru.danilakondr.les;

import org.junit.jupiter.api.Test;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class ResourceBundleTest {
    @Test
    public void resourceBundle() {
        String x = MessageFormat.format(ResourceBundle
                                .getBundle("LesStrings")
                                .getString("parser.invalidQuestionReference"),
                        1, 2);
        System.out.println(x);
    }
}
