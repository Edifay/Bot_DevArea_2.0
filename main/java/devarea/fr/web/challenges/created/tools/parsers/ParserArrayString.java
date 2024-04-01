package devarea.fr.web.challenges.created.tools.parsers;

import devarea.fr.web.challenges.ChallengeErrorException;
import java.nio.charset.StandardCharsets;

import static devarea.fr.web.challenges.created.tools.parsers.ParserIntArray.*;

public class ParserArrayString {
    public static boolean isPureAscii(String v) {
        return StandardCharsets.US_ASCII.newEncoder().canEncode(v);
    }

    public static String encodeStringList(final String[] list) {
        for (String str : list) {
            if (!isPureAscii(str))
                throw new IllegalArgumentException("list need contain String composed by ascii char.");
        }

        int[][] tab = new int[list.length][];
        for (int i = 0; i < list.length; i++) {
            tab[i] = new int[list[i].length()];
            for (int j = 0; j < tab[i].length; j++) {
                tab[i][j] = list[i].charAt(j);
            }
        }

        StringBuilder builder = new StringBuilder()
            .append(tab.length)
            .append(" [");

        if (tab.length == 0) {
            return builder.append("]").toString();
        }


        for (int[] word : tab) {
            builder
                .append(intListToString(word))
                .append(", ");
        }

        builder.setCharAt(builder.length() - 2, ']');
        builder.setLength(builder.length() - 1);
        return builder.toString();
    }

    public static String[] convert2DArrayToStringArray(final int[][] array) {
        String[] tab = new String[array.length];

        for (int i = 0; i < tab.length; i++) {
            tab[i] = "";
            for (int j = 0; j < array[i].length; j++) {
                tab[i] += (char) array[i][j];
            }
        }

        return tab;
    }


    public static String[] parseStringList(final String text) throws ChallengeErrorException {
        int[][] _2DIntArray = parse2DIntArray(text);
        return convert2DArrayToStringArray(_2DIntArray);
    }
}
