package ru.danilakondr.les.parser;

public class MKBSyntaxError extends IllegalArgumentException {
    private final int row;

    public MKBSyntaxError(int row) {
        this.row = row;
    }

    // FIXME добавить локализацию
    @Override
    public String getLocalizedMessage() {
        return getMessage();
    }

    public String getMessage() {
        return String.format("Syntax error at line #%d", row)
                + (this.getCause() != null ? ": " + this.getCause() : "");
    }
}
