package devarea.fr.web.challenges.created;

import java.util.ArrayList;

public class Tools {

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
        return builder.toString();
    }


    private static class TextStream {
        private final String text;
        private int cursor;
        private final ArrayList<String> delimiterStrings = new ArrayList<>();

        public TextStream(final String text) {
            this.text = text;
            this.cursor = 0;
        }

        public String getText() {
            return this.text;
        }

        public char nextChar() {
            return this.text.charAt(this.cursor++);
        }

        private char shadowChar() {
            return this.text.charAt(this.cursor);
        }

        public String nextWord() {
            int start = this.cursor;
            boolean entredInWord = false;
            for (; this.cursor < this.text.length(); this.cursor++) {
                if (nextEqualContained(this.delimiterStrings)) {
                    if (!entredInWord) {
                        start = this.cursor + 1;
                    } else {
                        break;
                    }
                } else {
                    entredInWord = true;
                }
            }
            return this.text.substring(start, this.cursor);
        }

        public TextStream appendDelimiter(final char escapeChar) {
            this.delimiterStrings.add(escapeChar + "");
            return this;
        }

        public TextStream appendDelimiter(final String escapeSequence) {
            this.delimiterStrings.add(escapeSequence);
            return this;
        }

        private boolean nextEqualContained(final ArrayList<String> comp) {
            for (String str : comp) {
                if (nextEqual(str)) {
                    return true;
                }
            }
            return false;
        }

        public boolean nextEqual(final String comp) {
            return equalFromCurrentIndex(this.text, comp, this.cursor);
        }

        private static boolean equalFromCurrentIndex(final String first, final String second, final int index_first) {
            if ((first.length() - index_first) - second.length() < 0) {
                return false;
            }

            for (int i = 0; i < second.length(); i++) {
                if (first.charAt(index_first + i) != second.charAt(i)) {
                    return false;
                }
            }

            return true;
        }
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
    public static int[] intListParser(final String text) {
        TextStream stream = new TextStream(text)
            .appendDelimiter(" ")
            .appendDelimiter(",")
            .appendDelimiter("[")
            .appendDelimiter("]");

        int size = Integer.parseInt(stream.nextWord());

        if (size > 10000) {
            throw new IllegalArgumentException("Unable to parse a list of size > 10000.");
        }

        int[] list = new int[size];

        int currentIndex = 0;

        String nextWord;
        while (!(nextWord = stream.nextWord()).isEmpty()) {
            list[currentIndex++] = Integer.parseInt(nextWord);
        }

        return list;
    }

}
