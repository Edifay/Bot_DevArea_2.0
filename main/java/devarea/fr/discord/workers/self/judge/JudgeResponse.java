package devarea.fr.discord.workers.self.judge;

public class JudgeResponse {

    private final Language language;
    private final String stdout;
    private final String time;
    private final int memory;
    private final String stderr;
    private final String token;
    private final String compileOutput;
    private final String message;
    private final int statusId;
    private final String statusDescription;

    public JudgeResponse(
            Language language,
            String stdout,
            String time,
            int memory,
            String stderr,
            String token,
            String compileOutput,
            String message,
            int statusId,
            String statusDescription) {
        this.language = language;
        this.stdout = stdout;
        this.time = time;
        this.memory = memory;
        this.stderr = stderr;
        this.token = token;
        this.compileOutput = compileOutput;
        this.message = message;
        this.statusId = statusId;
        this.statusDescription = statusDescription;
    }

    public Language getLanguage() {
        return language;
    }

    public String getStdout() {
        return stdout;
    }

    public String getTime() {
        return time;
    }

    public int getMemory() {
        return memory;
    }

    public String getStderr() {
        return stderr;
    }

    public String getToken() {
        return token;
    }

    public String getCompileOutput() {
        return compileOutput;
    }

    public String getMessage() {
        return message;
    }

    public int getStatusId() {
        return statusId;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    @Override
    public String toString() {
        return String.format("JudgeSubmission{stdout='%s', time='%s', memory='%s', stderr='%s', token='%s', " +
                        "compileOutput='%s', message='%s', statusId='%s', statusDescription='%s'}",
                stdout, time, memory, stderr, token, compileOutput, message, statusId, statusDescription);
    }
}
