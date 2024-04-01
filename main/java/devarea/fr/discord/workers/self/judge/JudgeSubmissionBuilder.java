package devarea.fr.discord.workers.self.judge;

public class JudgeSubmissionBuilder {

    private String code;
    private String language;
    private String args;
    private String stdin;
    private boolean isAlias;

    public JudgeSubmissionBuilder() {
    }

    public JudgeSubmissionBuilder code(String code) {
        this.code = code;
        return this;
    }

    public JudgeSubmissionBuilder languageAlias(String alias) {
        language = alias;
        isAlias = true;
        return this;
    }

    public JudgeSubmissionBuilder languageId(String id) {
        language = id;
        isAlias = false;
        return this;
    }

    public JudgeSubmissionBuilder args(String args) {
        this.args = args;
        return this;
    }

    public JudgeSubmissionBuilder stdin(String stdin) {
        this.stdin = stdin;
        return this;
    }

    public JudgeSubmission build() {
        if (language == null || code == null) {
            throw new IllegalArgumentException();
        }
        return new JudgeSubmission(code, language, isAlias, args, stdin);
    }
}
