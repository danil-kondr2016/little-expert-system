package ru.danilakondr.les.mkb;

import ru.danilakondr.les.LocalizedMessages;
import ru.danilakondr.les.OriginalMessages;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class MKBSyntaxError extends IllegalArgumentException {
    private final int row;

    public MKBSyntaxError(int row) {
        this.row = row;
    }

    @Override
    public String getLocalizedMessage() {
        return LocalizedMessages.syntaxError(row);
    }

    public String getMessage() {
        return OriginalMessages.syntaxError(row);
    }


}
