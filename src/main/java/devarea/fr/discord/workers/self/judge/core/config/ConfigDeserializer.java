package devarea.fr.discord.workers.self.judge.core.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import devarea.fr.discord.workers.self.judge.Language;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigDeserializer extends JsonDeserializer<Config> {

    @Override
    public Config deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        Root root = jp.readValueAs(Root.class);

        String url = root.url;
        boolean rapidapi = root.rapidapi;
        boolean encode = root.encode;
        Map<String, Language> languages = new HashMap<>();

        for (Lang lang : root.languages) {
            languages.put(lang.id, new Language(lang.id, lang.name, lang.version, lang.aliases));
        }

        return new Config(url, rapidapi, encode, languages);
    }

    private static class Root {

        public String url;
        public boolean rapidapi;
        public boolean encode;
        public List<Lang> languages;
    }

    private static class Lang {

        public String id;
        public String name;
        public String version;
        public List<String> aliases;
    }
}
