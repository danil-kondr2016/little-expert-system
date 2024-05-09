package ru.danilakondr.les.parser;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class KBFormatAnalyzer {
    public static KBFormat getFormat(InputStream is) throws IOException {
        if (tryJSON(is))
            return KBFormat.LES_JSON;
        if (tryYAML(is))
            return KBFormat.LES_YAML;

        KBFormat lesFmt = getFormat(is);
        is.close();
        return lesFmt;
    }

    private static boolean tryJSON(InputStream is) throws IOException {
        try {
            JsonParser parser = JsonFactory.builder()
                    .configure(StreamReadFeature.AUTO_CLOSE_SOURCE, true)
                    .build().createParser(is);
            JsonToken token;

            token = parser.nextToken();
            if (token != JsonToken.START_OBJECT)
                return false;

            while (parser.nextToken() != null);
            parser.close();
            return true;
        } catch (JsonParseException e) {
            return false;
        }
    }

    private static boolean tryYAML(InputStream is) throws IOException {
        try {
            YAMLParser parser = YAMLFactory.builder()
                    .configure(StreamReadFeature.AUTO_CLOSE_SOURCE, true)
                    .build().createParser(is);
            JsonToken token;

            token = parser.nextToken();
            if (token != JsonToken.START_OBJECT)
                return false;

            while (parser.nextToken() != null);
            parser.close();
            return true;
        } catch (JsonParseException e) {
            return false;
        }
    }

    private static final byte[] VALID_HEADER =
            "\nÁàçà çíàíèé äëÿ ïðîãðàì ìû \"Ìàëàÿ ÝÑ\" âåðñèè 2.0\n"
                    .getBytes(StandardCharsets.ISO_8859_1);

    private static KBFormat tryLES(InputStream is) throws IOException {
        int inc;

        inc = is.read();
        if (inc == -1)
            return KBFormat.INVALID;
        if (inc >= 0x20 || inc == 0x0D)
            return KBFormat.MES_2_0;

        if (inc <= 0x09) {
            byte[] header = new byte[49];
            int n_read = is.read(header);
            if (n_read < header.length)
                return KBFormat.INVALID;
            
            if (!Arrays.equals(header, VALID_HEADER))
                return KBFormat.INVALID;

            return KBFormat.MES_2_0_OBFUSCATED;
        }

        return KBFormat.INVALID;
    }
}
