package devarea.fr.discord.workers.self.judge.core.io;

import devarea.fr.discord.workers.self.judge.JudgeException;
import devarea.fr.discord.workers.self.judge.core.ResponseBuilder;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Properties;

public class Client {

    private final String host;
    private final URI submissionUrl;
    private final String parameter;
    private final boolean rapidapi;
    private String apiKey;

    public Client(String url, boolean rapidapi, boolean encode) throws IOException {
        this.rapidapi = rapidapi;
        if (rapidapi) {
            Properties properties = new Properties();
            properties.load(new FileInputStream("judge.properties"));

            apiKey = properties.getProperty("judge.rapidapi.key");
        }
        host = URI.create(url).getHost();
        submissionUrl = URI.create(url).resolve("/submissions/");
        parameter = "?base64_encoded=" + encode;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (rapidapi) {
            headers.set("X-RapidAPI-Key", apiKey);
            headers.set("X-RapidAPI-Host", host);
        }
        return headers;
    }

    public String createSubmission(Map<String, String> body) throws JudgeException {
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<Token> result = restTemplate.exchange(
                    submissionUrl.resolve(parameter),
                    HttpMethod.POST,
                    new HttpEntity<>(body, createHeaders()),
                    Token.class);

            if (result.getBody() == null || result.getBody().getToken() == null) {
                throw new JudgeException("Erreur interne du serveur.");
            }
            return result.getBody().getToken();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new JudgeException(e.getRawStatusCode() + " " + e.getStatusText());
        } catch (RestClientException e) {
            throw new JudgeException(e.getCause().getMessage());
        }
    }

    public ResponseBuilder getSubmission(String token) throws JudgeException {
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<ResponseBuilder> result = restTemplate.exchange(
                    submissionUrl.resolve(token + parameter),
                    HttpMethod.GET,
                    new HttpEntity<>(createHeaders()),
                    ResponseBuilder.class);

            return result.getBody();
        } catch (HttpClientErrorException e) {
            throw new JudgeException(e.getRawStatusCode() + " " + e.getStatusText());
        }
    }
}
