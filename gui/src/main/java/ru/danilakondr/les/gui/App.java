package ru.danilakondr.les.gui;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import ru.danilakondr.les.knowbase.KnowledgeBase;
import ru.danilakondr.les.knowbase.KnowledgeBaseReader;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

import static javax.swing.JOptionPane.ERROR_MESSAGE;

public class App {
    private KnowledgeBase kb;

    public void parseCommandLine(String[] args) throws IOException, ParseException {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(null, args);
        String input = cmd.getArgs()[0];

        KnowledgeBaseReader reader = new KnowledgeBaseReader(new File(input));
        this.kb = reader.read();
    }

    public static void main(String[] args) {
        setSystemLookAndFeel();
        App app = new App();

        try {
            app.parseCommandLine(args);
        }
        catch (IOException e) {
            guiDie("Ошибка при открытии файла: " + e.getLocalizedMessage());
        }
        catch (ParseException e) {
            guiDie("Ошибка при обработке командной строки: " + e.getLocalizedMessage());
        }
        catch (IllegalArgumentException e) {
            guiDie("Неверный аргумент: " + e.getLocalizedMessage());
        }

    }

    private static void guiDie(String message) {
        JOptionPane.showMessageDialog(null, message, "Ошибка", ERROR_MESSAGE);
        System.exit(-1);
    }

    private static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
