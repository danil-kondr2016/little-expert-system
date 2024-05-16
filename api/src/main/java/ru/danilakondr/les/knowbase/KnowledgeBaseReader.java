package ru.danilakondr.les.knowbase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import ru.danilakondr.les.LocalizedMessages;
import ru.danilakondr.les.mkb.MKBParser;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Scanner;

/**
 * Класс-читатель баз знаний в различных форматах. Поддерживается три формата:
 * JSON, YAML, MKB Малой ЭС 2.0.
 * <p>
 * Формат &laquo;шифрованных&raquo; файлов Малой ЭС 2.0 не поддерживается:
 * выбрасывается исключение {@code IllegalArgumentException}.
 */
public class KnowledgeBaseReader {
    private File f;
    private byte[] buffer;
    private InputStream is;
    private Charset cs;
    private Reader reader;

    public KnowledgeBaseReader(File f) throws IOException {
        this.f = f;
        this.is = Files.newInputStream(f.toPath());
    }

    public KnowledgeBaseReader(byte[] b) {
        this.buffer = b;
        this.is = new ByteArrayInputStream(b);
    }

    public KnowledgeBaseReader(byte[] b, Charset cs) {
        this.buffer = b;
        this.is = new ByteArrayInputStream(b);
        this.cs = cs;
    }

    public KnowledgeBase read() throws IOException {
        KBFormat fmt = KBFormat.INVALID;
        if (f != null) {
            fmt = KBFormatAnalyzer.getFormat(f);
        }
        else if (buffer != null) {
            fmt = KBFormatAnalyzer.getFormat(buffer);
        }

        switch (fmt) {
            case LES_JSON:
                reader = new InputStreamReader(is,
                        cs == null ? StandardCharsets.UTF_8 : cs);
                return readJson();
            case LES_YAML:
                reader = new InputStreamReader(is,
                        cs == null ? StandardCharsets.UTF_8 : cs);
                return readYaml();
            case MES_2_0:
                reader = new InputStreamReader(is,
                        cs == null ? Charset.forName("cp1251") : cs);
                return readMkb();
            case MES_2_0_OBFUSCATED:
                throw new IllegalArgumentException(LocalizedMessages.mkbObfuscatedNotice());
        }

        return null;
    }

    private KnowledgeBase readYaml() throws IOException {
        YAMLMapper yamlMapper = new YAMLMapper();

        return yamlMapper.readValue(reader, KnowledgeBase.class);
    }

    private KnowledgeBase readJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(reader, KnowledgeBase.class);
    }

    private KnowledgeBase readMkb() throws IOException {
        MKBParser parser = new MKBParser(new Scanner(reader));
        return parser.parse();
    }
}
