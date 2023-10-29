package devarea.fr.web.challenges;

public enum Language {
    JAVA,
    PYTHON;


    public static String asString(final Language language) {
        return switch (language) {
            case JAVA -> "JAVA";
            case PYTHON -> "PYTHON";
        };
    }

    public static Language parse(final String language) {
        if (language.equals("JAVA")) {
            return JAVA;
        } else if (language.equals("PYTHON")) {
            return PYTHON;
        }
        return null;
    }
}
