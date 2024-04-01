package devarea.fr.discord.workers.self.judge.core;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import devarea.fr.discord.workers.self.judge.JudgeResponse;
import devarea.fr.discord.workers.self.judge.Language;

import java.util.Base64;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResponseBuilder {

    public String stdout;
    public String time;
    public int memory;
    public String stderr;
    public String token;
    public String compileOutput;
    public String message;
    public Status status;

    private String decode(String value) {
        if (value == null) {
            return null;
        }
        return new String(Base64.getMimeDecoder().decode(value));
    }

    public JudgeResponse build(Language language, boolean encode) {
        if (encode) {
            stdout = decode(stdout);
            stderr = decode(stderr);
            compileOutput = decode(compileOutput);
            message = decode(message);
        }
        return new JudgeResponse(
                language,
                this.stdout,
                this.time,
                this.memory,
                this.stderr,
                this.token,
                this.compileOutput,
                this.message,
                this.status.id,
                this.status.description);
    }

    public static class Status {

        public int id;
        public String description;
    }
}
