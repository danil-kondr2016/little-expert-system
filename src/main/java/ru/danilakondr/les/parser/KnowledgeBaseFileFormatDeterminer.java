package ru.danilakondr.les.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class KnowledgeBaseFileFormatDeterminer {
    public static KnowledgeBaseFileFormat determineFormat(InputStream in) throws IOException {
        byte[] buffer = new byte[8];

        int nRead = in.read(buffer, 0, 2);
        if (nRead < 2)
            return KnowledgeBaseFileFormat.MKB_INVALID;

        if (buffer[0] == 0x0D && buffer[1] == 0x0A)
            return KnowledgeBaseFileFormat.MKB_CP1251;

        if (buffer[0] >= 0x00 && buffer[0] <= 0x09)
            return KnowledgeBaseFileFormat.MKB_BUKHNIN_ENCODED;

        if (buffer[0] == '{')
            return KnowledgeBaseFileFormat.MKB_JSON;

        if (buffer[0] == (byte)0xEF && buffer[1] == (byte)0xBB) {
            nRead = in.read(buffer, 2, 1);
            if (nRead == 0)
                return KnowledgeBaseFileFormat.MKB_INVALID;
            if (buffer[2] != (byte)0xBF)
                return KnowledgeBaseFileFormat.MKB_CP1251;

            nRead = in.read(buffer, 3, 1);
            if (nRead == 0)
                return KnowledgeBaseFileFormat.MKB_INVALID;
            if (buffer[3] == '{')
                return KnowledgeBaseFileFormat.MKB_JSON;

            return KnowledgeBaseFileFormat.MKB_UTF8;
        }

        return KnowledgeBaseFileFormat.MKB_INVALID;
    }
}
