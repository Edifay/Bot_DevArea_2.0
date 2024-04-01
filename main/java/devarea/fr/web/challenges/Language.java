package devarea.fr.web.challenges;

public enum Language {
    JAVA,
    PYTHON,
    C;


    public static String asString(final Language language) {
        return switch (language) {
            case JAVA -> "JAVA";
            case PYTHON -> "PYTHON";
            case C -> "C";
        };
    }

    public static Language parse(final String language) {
        return switch (language) {
            case "JAVA" -> JAVA;
            case "PYTHON" -> PYTHON;
            case "C" -> C;
            default -> null;
        };
    }

}
