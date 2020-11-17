package pl.greenmc.tob.game.util;


import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.google.common.io.ByteStreams;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static pl.greenmc.tob.game.util.Logger.debug;

/**
 * Utilities
 */
@SuppressWarnings("unused")
public class Utilities {
    public static final String LATIN_EXTENDED = FreeTypeFontGenerator.DEFAULT_CHARS + "\u0080\u0081\u0082\u0083\u0084\u0085\u0086\u0087\u0088\u0089\u008a\u008b\u008c\u008d\u008e\u008f\u0090\u0091\u0092\u0093\u0094\u0095\u0096\u0097\u0098\u0099\u009a\u009b\u009c\u009d\u009e\u009f\u00a0\u00a1\u00a2\u00a3\u00a4\u00a5\u00a6\u00a7\u00a8\u00a9\u00aa\u00ab\u00ac\u00ad\u00ae\u00af\u00b0\u00b1\u00b2\u00b3\u00b4\u00b5\u00b6\u00b7\u00b8\u00b9\u00ba\u00bb\u00bc\u00bd\u00be\u00bf\u00c0\u00c1\u00c2\u00c3\u00c4\u00c5\u00c6\u00c7\u00c8\u00c9\u00ca\u00cb\u00cc\u00cd\u00ce\u00cf\u00d0\u00d1\u00d2\u00d3\u00d4\u00d5\u00d6\u00d7\u00d8\u00d9\u00da\u00db\u00dc\u00dd\u00de\u00df\u00e0\u00e1\u00e2\u00e3\u00e4\u00e5\u00e6\u00e7\u00e8\u00e9\u00ea\u00eb\u00ec\u00ed\u00ee\u00ef\u00f0\u00f1\u00f2\u00f3\u00f4\u00f5\u00f6\u00f7\u00f8\u00f9\u00fa\u00fb\u00fc\u00fd\u00fe\u00ff\u0100\u0101\u0102\u0103\u0104\u0105\u0106\u0107\u0108\u0109\u010a\u010b\u010c\u010d\u010e\u010f\u0110\u0111\u0112\u0113\u0114\u0115\u0116\u0117\u0118\u0119\u011a\u011b\u011c\u011d\u011e\u011f\u0120\u0121\u0122\u0123\u0124\u0125\u0126\u0127\u0128\u0129\u012a\u012b\u012c\u012d\u012e\u012f\u0130\u0131\u0132\u0133\u0134\u0135\u0136\u0137\u0138\u0139\u013a\u013b\u013c\u013d\u013e\u013f\u0140\u0141\u0142\u0143\u0144\u0145\u0146\u0147\u0148\u0149\u014a\u014b\u014c\u014d\u014e\u014f\u0150\u0151\u0152\u0153\u0154\u0155\u0156\u0157\u0158\u0159\u015a\u015b\u015c\u015d\u015e\u015f\u0160\u0161\u0162\u0163\u0164\u0165\u0166\u0167\u0168\u0169\u016a\u016b\u016c\u016d\u016e\u016f\u0170\u0171\u0172\u0173\u0174\u0175\u0176\u0177\u0178\u0179\u017a\u017b\u017c\u017d\u017e\u017f\u0180\u0181\u0182\u0183\u0184\u0185\u0186\u0187\u0188\u0189\u018a\u018b\u018c\u018d\u018e\u018f\u0190\u0191\u0192\u0193\u0194\u0195\u0196\u0197\u0198\u0199\u019a\u019b\u019c\u019d\u019e\u019f\u01a0\u01a1\u01a2\u01a3\u01a4\u01a5\u01a6\u01a7\u01a8\u01a9\u01aa\u01ab\u01ac\u01ad\u01ae\u01af\u01b0\u01b1\u01b2\u01b3\u01b4\u01b5\u01b6\u01b7\u01b8\u01b9\u01ba\u01bb\u01bc\u01bd\u01be\u01bf\u01c0\u01c1\u01c2\u01c3\u01c4\u01c5\u01c6\u01c7\u01c8\u01c9\u01ca\u01cb\u01cc\u01cd\u01ce\u01cf\u01d0\u01d1\u01d2\u01d3\u01d4\u01d5\u01d6\u01d7\u01d8\u01d9\u01da\u01db\u01dc\u01dd\u01de\u01df\u01e0\u01e1\u01e2\u01e3\u01e4\u01e5\u01e6\u01e7\u01e8\u01e9\u01ea\u01eb\u01ec\u01ed\u01ee\u01ef\u01f0\u01f1\u01f2\u01f3\u01f4\u01f5\u01f6\u01f7\u01f8\u01f9\u01fa\u01fb\u01fc\u01fd\u01fe\u01ff\u0200\u0201\u0202\u0203\u0204\u0205\u0206\u0207\u0208\u0209\u020a\u020b\u020c\u020d\u020e\u020f\u0210\u0211\u0212\u0213\u0214\u0215\u0216\u0217\u0218\u0219\u021a\u021b\u021c\u021d\u021e\u021f\u0220\u0221\u0222\u0223\u0224\u0225\u0226\u0227\u0228\u0229\u022a\u022b\u022c\u022d\u022e\u022f\u0230\u0231\u0232\u0233\u0234\u0235\u0236\u0237\u0238\u0239\u023a\u023b\u023c\u023d\u023e\u023f\u0240\u0241\u0242\u0243\u0244\u0245\u0246\u0247\u0248\u0249\u024a\u024b\u024c\u024d\u024e\u024f";

    /**
     * Compresses file to file.zip
     *
     * @param file File to be compressed
     * @throws IOException On IO error
     */
    public static void compressFile(@NotNull File file) throws IOException {
        String zipFileName = file.getPath().concat(".zip");

        FileOutputStream fos = new FileOutputStream(zipFileName);
        ZipOutputStream zos = new ZipOutputStream(fos);

        zos.putNextEntry(new ZipEntry(file.getName()));

        FileInputStream from = new FileInputStream(file);
        ByteStreams.copy(from, zos);
        from.close();
        zos.closeEntry();
        zos.close();
    }

    /**
     * @param s1 String 1
     * @param s2 String 2
     * @return Whether s1 equals s2 after removing whitespaces
     */
    public static boolean equalsIgnoreWhitespaces(String s1, String s2) {
        s1 = s1.replaceAll("\\s", "");
        s2 = s2.replaceAll("\\s", "");
        return s1.equals(s2);
    }


    /**
     * Generates formatted caller signature
     *
     * @param depth 0 returns caller of this function, 1 caller of caller of this function etc.
     * @return Formatted caller signature
     */
    @NotNull
    public static String getMethodIdentifier(int depth) {
        StackTraceElement[] stackTrace = (new Exception()).getStackTrace();
        if (stackTrace.length < depth + 2) return "<Invalid stack>";
        StackTraceElement element = stackTrace[depth + 1];
        return (element.getClassName() + "#" + element.getMethodName() + ":" + element.getLineNumber());
    }


    /**
     * @param filename Filename to be escaped
     * @return Escaped filename
     */
    @NotNull
    public static String escapeFilename(@NotNull String filename) {
        filename = filename.replace("<", "_")
                .replace("*", "_")
                .replace(".", "_")
                .replace("#", "_")
                .replace("$", "_")
                .replace(">", "_")
                .replace(":", "_")
                .replace("\"", "_")
                .replace("/", "_")
                .replace("\\", "_")
                .replace("|", "_")
                .replace("?", "_");
        while (filename.endsWith(" ") || filename.endsWith("\t") || filename.endsWith("\0"))
            filename = filename.substring(0, filename.length() - 1);
        return filename;
    }


    /**
     * Puts exception stacktrace in String
     *
     * @param e Exception to be converted
     * @return String with stacktrace
     */
    @NotNull
    public static String exceptionToString(@NotNull Throwable e) {
        StringBuilder stackTrace = new StringBuilder();
        OutputStream os = new OutputStream() {
            @Override
            public void write(int b) {
                stackTrace.append((char) b);
            }
        };
        PrintStream ps = new PrintStream(os);
        e.printStackTrace(ps);
        return stackTrace.toString();
    }


    /**
     * Parses data from InputStream to byte array and String
     *
     * @param input InputStream with data
     * @return Processed data
     * @throws IOException Thrown on IO error
     */
    @NotNull
    @Contract("_ -> new")
    public static InputStreamData processInputStream(@NotNull InputStream input) throws IOException {
        debug("Processing input stream...");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[65536];
        while ((nRead = input.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        byte[] bytes = buffer.toByteArray();
        return new InputStreamData(bytes);
    }


    /**
     * @param tClass Type class
     * @param array  Array to append to
     * @param object Object to be appended
     * @param <T>    Type
     * @return Appended array
     */
    @NotNull
    @Contract("_,_,_ -> new")
    public static <T> T[] appendToArray(Class<T> tClass, @NotNull T[] array, T object) {
        @SuppressWarnings("unchecked") T[] out = (T[]) Array.newInstance(tClass, array.length + 1);
        System.arraycopy(array, 0, out, 0, array.length);
        out[array.length] = object;
        return out;
    }

    /**
     * @param url URL to split
     * @return Map with parameters
     */
    @NotNull
    public static Map<String, String> splitQuery(@NotNull URI url) {
        Map<String, String> query_pairs = new LinkedHashMap<>();
        String query = url.getQuery();
        if (query == null) return query_pairs;
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8), URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8));
        }
        return query_pairs;
    }
}

