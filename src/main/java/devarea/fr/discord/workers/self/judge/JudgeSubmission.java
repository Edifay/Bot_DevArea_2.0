package devarea.fr.discord.workers.self.judge;

public class JudgeSubmission {

    private final String code;
    private final String language;
    private final boolean isAlias;
    private final String args;
    private final String stdin;

    JudgeSubmission(String code, String language, boolean isAlias, String args, String stdin) {
        this.code = code;
        this.language = language;
        this.isAlias = isAlias;
        this.args = args;
        this.stdin = stdin;
    }

    public String getCode() {
        return code;
    }

    public String getLanguage() {
        return language;
    }

    public boolean isAlias() {
        return isAlias;
    }

    public String getArgs() {
        return args;
    }

    public String getStdin() {
        return stdin;
    }
}
