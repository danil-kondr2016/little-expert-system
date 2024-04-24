package ru.danilakondr.les.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class KnowledgeBaseFileFormatDeterminer {
    /*
     * Изначально строка закодирована в CP1251, но поскольку средствами
     * Java такую кодировку включить не получается, приходится сначала
     * перегнать строку в ISO 8859-1, а затем уже в байтовое проедставление.
     */
    private static final byte[] BUKHNIN_MES_20 =
            "\nÁàçà çíàíèé äëÿ ïðîãðàì ìû \"Ìàëàÿ ÝÑ\" âåðñèè 2.0\n"
                    .getBytes(StandardCharsets.ISO_8859_1);

    public static KnowledgeBaseFileFormat determineFormat(InputStream in) throws IOException {
        byte[] buffer = new byte[8];

        int nRead = in.read(buffer, 0, 2);
        if (nRead < 2)
            return KnowledgeBaseFileFormat.MKB_INVALID;

        if (buffer[0] == 0x0D && buffer[1] == 0x0A)
            return KnowledgeBaseFileFormat.MKB_CP1251;

        /*
         * У Бухнина в бинарниках (исходники он не хочет показывать, потому что
         * код неидеален с высоты его нынешнего опыта) определено, что если
         * первый байт
         */
        if (buffer[0] >= 0x00 && buffer[0] <= 0x09) {
            byte[] buf2 = new byte[BUKHNIN_MES_20.length];
            nRead = in.read(buf2);
            if (nRead < buf2.length)
                return KnowledgeBaseFileFormat.MKB_INVALID;
            for (int i = 0; i < buf2.length; i++)
                if (buf2[i] != BUKHNIN_MES_20[i])
                    return KnowledgeBaseFileFormat.MKB_INVALID;
            return KnowledgeBaseFileFormat.MKB_BUKHNIN_ENCODED;
        }

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
