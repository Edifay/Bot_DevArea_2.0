package devarea.fr.web.challenges.created.tools.parsers;

import devarea.fr.web.challenges.ChallengeErrorException;
import devarea.fr.web.challenges.created.tools.TextStream;

public class ParserIntArray {


    public static String intListToString(int[] numbers) {
        StringBuilder builder = new StringBuilder(20 + 4 * numbers.length);
        builder.append(numbers.length)
            .append(" [");

        if (numbers.length == 0) {
            return builder.append("]").toString();
        }

        for (int n : numbers) {
            builder.append(n).append(", ");
        }
        builder.setCharAt(builder.length() - 2, ']');
        builder.setLength(builder.length() - 1);
        return builder.toString();
    }

    /**
     * We will admit that the format of the list is well-formed.
     * <p>
     * This parser is a really simple parser, and don't handle all the https://www.json.org/json-en.html automaton for array.
     * <p>
     * It's the simple automaton for int array.
     *
     * @param text the text at parse.
     * @return the result of parsing.
     */
    public static int[] intListParser(final String text) throws ChallengeErrorException {
        try {
            TextStream stream = new TextStream(text)
                .appendDelimiter(" ")
                .appendDelimiter(",")
                .appendDelimiter("[")
                .appendDelimiter("]");


            int size = Integer.parseInt(stream.nextWord());

            if (size > 10000) {
                throw new ChallengeErrorException("Unable to parse a list of size > 10000.");
            }

            int[] list = new int[size];
            fillIntListUsingStream(stream, list);
            return list;
        } catch (ChallengeErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new ChallengeErrorException("Le serveur n'as pas pu parser votre liste.");
        }
    }


    public static void fillIntListUsingStream(final TextStream stream, final int[] list) {
        int currentIndex = 0;

        String nextWord;
        while (currentIndex < list.length && !(nextWord = stream.nextWord()).isEmpty()) {
            list[currentIndex++] = Integer.parseInt(nextWord);
        }
    }


    public static int[][] parse2DIntArray(final String text) throws ChallengeErrorException {
        try {

            TextStream stream = new TextStream(text)
                .appendDelimiter(" ")
                .appendDelimiter(",")
                .appendDelimiter("[")
                .appendDelimiter("]");

            int size = Integer.parseInt(stream.nextWord());

            if (size > 10000) {
                throw new ChallengeErrorException("Unable to parse a list of size > 10000.");
            }

            int[][] list = new int[size][];

            int currentIndex = 0;

            String nextWord;
            while (currentIndex < size && !(nextWord = stream.nextWord()).isEmpty()) {
                int insideSize = Integer.parseInt(nextWord);

                if (insideSize > 10000) {
                    throw new ChallengeErrorException("Unable to parse a list of size > 10000.");
                }

                list[currentIndex] = new int[insideSize];

                fillIntListUsingStream(stream, list[currentIndex]);
                currentIndex++;
            }

            if (currentIndex != size)
                throw new ChallengeErrorException("La liste de contient pas le nombre d'élément attendu !");

            return list;

        } catch (ChallengeErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new ChallengeErrorException("Le serveur n'as pas pu parser la liste");
        }
    }

}
