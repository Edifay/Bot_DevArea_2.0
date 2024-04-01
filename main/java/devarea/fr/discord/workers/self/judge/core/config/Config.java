package devarea.fr.discord.workers.self.judge.core.config;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import devarea.fr.discord.workers.self.judge.Language;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@JsonDeserialize(using = ConfigDeserializer.class)
public class Config {

    private final String url;
    private final boolean rapidapi;
    private final boolean encode;
    private final Map<String, Language> languages;

    public Config(String url, boolean rapidapi, boolean encode, Map<String, Language> languages) {
        this.url = url;
        this.rapidapi = rapidapi;
        this.encode = encode;
        this.languages = languages;
    }

    public String getUrl() {
        return url;
    }

    public boolean isRapidapi() {
        return rapidapi;
    }

    public boolean isEncode() {
        return encode;
    }

    public Language aliasToLanguage(String alias) {
        return languages.values()
                .stream()
                .filter(lang -> lang.aliasExists(alias))
                .findFirst()
                .orElse(null);
    }

    public Language idToLanguage(String id) {
        return languages.get(id);
    }

    public Map<String, List<String>> languages() {
        Map<String, List<String>> allLanguages = new TreeMap<>();

        for (Language language : languages.values()) {
            List<String> aliases = language.getAliases();
            if (!aliases.isEmpty()) {
                allLanguages.put(language.getName(), aliases);
            }
        }

        return allLanguages;
    }
}
