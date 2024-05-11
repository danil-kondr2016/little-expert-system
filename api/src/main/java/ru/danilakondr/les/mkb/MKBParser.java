package ru.danilakondr.les.mkb;

import ru.danilakondr.les.knowbase.Hypothesis;
import ru.danilakondr.les.knowbase.KnowledgeBase;
import ru.danilakondr.les.knowbase.InvalidQuestionReferenceException;

import java.util.Scanner;

/**
 * Парсер файлов в формате Малой экспертной системы 2.0. Способен принимать
 * файлы в любой кодировке.
 *
 * <h2> Описание формата </h2>
 *
 * <p><i>Оригинальное описание формата файлов было взято с сайта
 * <a href="http://bourabai.ru/alg/mes2.htm">http://bourabai.ru/alg/mes2.htm</a>,
 * которое, в свою очередь, взято из оригинальной справки к Малой экспертной
 * системе. Автором оригинальной справки является Алексей Бухнин.</i>
 *
 * <p>База знаний представляет собой текстовый файл (который в дальнейшем может
 * быть зашифрован), включающий три секции со следующей структурой:</p>
 *
 * <p><b>1.</b></p>
 *
 * <p>Описание базы знаний, имя автора, комментарий и т.п.</p>
 *
 * <p>(можно в несколько строк, общая длина которых не должна превышать 10000
 * символов; данная секция заканчивается после первой пустой строки).</p>
 *
 * <p><b>2.</b></p>
 *
 * <p>Свидетельство № 0 (любой текст (не более 1000 символов),
 * заканчивающийся переносом строки)</p>
 *
 * <p>Свидетельство № 1</p>
 *
 * <p>Свидетельство № 2</p>
 *
 * <p>...</p>
 *
 * <p>Свидетельство № N (после последнего свидетельства следует
 * одна пустая строка, и вторая секция заканчивается).</p>
 *
 * <p><b>3.</b></p>
 *
 * <p>Исход № 0, P [ , i, Py, Pn ]</p>
 *
 * <p>Исход № 1, P [ , i, Py, Pn ]</p>
 *
 * <p>Исход № 2, P [ , i, Py, Pn ]</p>
 *
 * <p>...</p>
 *
 * <p>Исход</span><span> № M, P [ , i, Py, Pn ]</p>
 *
 * <p>Смысл первых двух секций вполне понятен из приведённой схемы. Последняя
 * секция требует более подробного рассмотрения. В ней перечисляются правила
 * вывода: каждое задаётся в отдельной строке; перечисление заканчивается с
 * концом файла.</p>
 *
 * <p>В начале описания правила вывода задаётся исход, вероятность которого
 * меняется в соответствии с данным правилом. Это текст, включающий любые
 * символы, кроме запятых. После запятой указывается априорная вероятность
 * данного исхода (P), т.е. вероятность исхода в случае отсутствия
 * дополнительной информации. После этого через запятую идёт ряд повторяющихся
 * полей из трёх элементов. Первый элемент (i) – это номер соответствующего
 * вопроса (симптома, свидетельства). Следующие два элемента ( Py = P(E / H)
 * и Pn = P(E / неH) ) – соответственно вероятности получения ответа «Да» на
 * этот вопрос, если возможный исход верен и неверен. Эти данные указываются для
 * каждого вопроса, связанного с данным исходом.</p>
 *
 * <h2> Принцип работы парсера </h2>
 *
 * Формат файла очень прост, поэтому файлы можно разобрать путём
 * последовательного взятия комментария, вопросов и гипотез. Обработка идёт
 * построчно.
 */
public class MKBParser {
    private final Scanner scanner;
    private int lineIndex;

    public MKBParser(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Парсит входной поток данных. Для этого используется класс
     * Scanner.
     * @return база знаний
     */
    public KnowledgeBase parse() {
        KnowledgeBase kb = new KnowledgeBase();

        lineIndex = 1;
        kb.setComment(parseComment());
        parseQuestions(kb);
        parseHypotheses(kb);

        return kb;
    }

    private String parseComment() {
        StringBuilder comment = new StringBuilder();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.isEmpty())
                break;

            comment.append(line);
            comment.append('\n');
            this.lineIndex++;
        }

        return comment.toString().replaceAll("\n$", "");
    }

    private void parseQuestions(KnowledgeBase kb) {
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.isEmpty())
                break;

            kb.addQuestion(line);
            this.lineIndex++;
        }
    }

    private void parseHypotheses(KnowledgeBase kb) {
        Hypothesis h = null;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] tokens = line.split("\\s*,\\s*");

            String name = tokens[0];
            float pApriori = Float.parseFloat(tokens[1]);
            h = new Hypothesis(name, pApriori);

            for (int i = 2; i < tokens.length; i += 3) {
                int question;
                float yes, no;

                try {
                    question = Integer.parseInt(tokens[i]);
                    yes = Float.parseFloat(tokens[i+1]);
                    no = Float.parseFloat(tokens[i+2]);
                }
                catch (NumberFormatException e) {
                    throw new MKBSyntaxError(lineIndex);
                }

                if (question <= 0 && question >= kb.getQuestions().size()) {
                    throw new InvalidQuestionReferenceException(question, lineIndex);
                }

                h.putAnswerPair(question, yes, no);
            }
            this.lineIndex++;
            kb.addHypothesis(h);
        }
    }

}
