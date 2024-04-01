package devarea.fr.discord.workers.self.judge;

import com.fasterxml.jackson.databind.ObjectMapper;
import devarea.fr.discord.workers.self.judge.core.ResponseBuilder;
import devarea.fr.discord.workers.self.judge.core.config.Config;
import devarea.fr.discord.workers.self.judge.core.io.Boilerplate;
import devarea.fr.discord.workers.self.judge.core.io.Client;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class JudgeManager {

    private static final String LANGUAGE_ID = "language_id";
    private static final String SOURCE_CODE = "source_code";
    private static final String ARGUMENTS = "command_line_arguments";
    private static final String STDIN = "stdin";
    private static final int GATEWAY_TIMEOUT = 15000;

    private static final JudgeManager instance = new JudgeManager();

    private Config config;
    private Client client;

    private JudgeManager() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            config = mapper.readValue(new File("judge.json"), Config.class);
            client = new Client(config.getUrl(), config.isRapidapi(), config.isEncode());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JudgeManager get() {
        return instance;
    }

    public Config getConfig() throws JudgeException {
        checkConfig();
        return config;
    }

    private void checkConfig() throws JudgeException {
        if (config == null) {
            throw new JudgeException("La configuration Judge est invalide.");
        }
    }

    private String encode(String value) {
        if (!config.isEncode() || value == null) {
            return value;
        }
        return Base64.getEncoder().encodeToString(value.getBytes());
    }

    public CompletableFuture<JudgeResponse> executeAsync(JudgeSubmission submission) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.execute(submission);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    public JudgeResponse execute(JudgeSubmission submission) throws JudgeException {
        checkConfig();

        Language language = submission.isAlias() ? config.aliasToLanguage(submission.getLanguage()) : config.idToLanguage(submission.getLanguage());
        if (language == null) {
            throw new JudgeException("Le langage " + submission.getLanguage() + " n'est pas supporté.");
        }

        String sourceCode = Boilerplate.addBoilerplate(language.getName(), submission.getCode());

        Map<String, String> entity = new HashMap<>();
        entity.put(LANGUAGE_ID, language.getId());
        entity.put(SOURCE_CODE, encode(sourceCode));
        entity.put(ARGUMENTS, submission.getArgs());
        entity.put(STDIN, encode(submission.getStdin()));

        String token = client.createSubmission(entity);

        long startTime = System.currentTimeMillis();
        ResponseBuilder response;
        do {
            response = client.getSubmission(token);
            if (System.currentTimeMillis() - startTime > GATEWAY_TIMEOUT) {
                throw new JudgeException("Le serveur n'a pas répondu à temps.");
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (response.status.id == 1 || response.status.id == 2);

        return response.build(language, config.isEncode());
    }
}
