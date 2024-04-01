package devarea.fr.web.challenges.created.tools;

import java.util.ArrayList;

public class TextStream {

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
