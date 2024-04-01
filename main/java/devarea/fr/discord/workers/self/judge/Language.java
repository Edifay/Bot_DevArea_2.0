package devarea.fr.discord.workers.self.judge;

import java.util.Collections;
import java.util.List;

public class Language {

    private final String id;
    private final String name;
    private final String version;
    private final List<String> aliases;

    public Language(String id, String name, String version, List<String> aliases) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.aliases = aliases;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public List<String> getAliases() {
        return Collections.unmodifiableList(aliases);
    }

    public boolean aliasExists(String alias) {
        return aliases.contains(alias);
    }
}
