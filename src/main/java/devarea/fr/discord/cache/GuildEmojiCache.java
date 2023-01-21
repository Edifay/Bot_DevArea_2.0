package devarea.fr.discord.cache;

import devarea.fr.discord.cache.cached_entity.CachedGuildEmoji;
import devarea.fr.discord.cache.cached_entity.CachedRole;
import discord4j.core.object.entity.GuildEmoji;

import java.util.HashMap;

public class GuildEmojiCache {


    public static final HashMap<String, CachedGuildEmoji> emojis = new HashMap<>();

    public static GuildEmoji get(final String emojiID) {
        CachedGuildEmoji cachedGuildEmoji = getCachedGuildEmoji(emojiID);
        if (cachedGuildEmoji == null) {
            cachedGuildEmoji = new CachedGuildEmoji(emojiID);
            emojis.put(emojiID, cachedGuildEmoji);
        }

        return cachedGuildEmoji.get();
    }

    public static GuildEmoji fetch(final String emojiID) {
        CachedGuildEmoji cachedGuildEmoji = getCachedGuildEmoji(emojiID);
        if (cachedGuildEmoji == null) {
            cachedGuildEmoji = new CachedGuildEmoji(emojiID);
            emojis.put(emojiID, cachedGuildEmoji);
        }

        return cachedGuildEmoji.fetch();
    }

    public static GuildEmoji watch(final String emojiID) {
        CachedGuildEmoji cachedGuildEmoji = getCachedGuildEmoji(emojiID);
        if (cachedGuildEmoji == null) {
            cachedGuildEmoji = new CachedGuildEmoji(emojiID);
            cachedGuildEmoji.fetch();
            emojis.put(emojiID, cachedGuildEmoji);
        }

        return cachedGuildEmoji.watch();
    }

    private static CachedGuildEmoji getCachedGuildEmoji(final String emojiID) {
        return emojis.get(emojiID);
    }
}
