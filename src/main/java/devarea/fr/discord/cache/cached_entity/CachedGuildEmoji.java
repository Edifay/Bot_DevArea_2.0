package devarea.fr.discord.cache.cached_entity;

import devarea.fr.discord.DevArea;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.GuildEmoji;

public class CachedGuildEmoji extends CachedObject<GuildEmoji> {
    public CachedGuildEmoji(String object_id) {
        super(object_id);
    }

    @Override
    public GuildEmoji fetch() {
        this.object_cached = DevArea.devarea.getGuildEmojiById(Snowflake.of(this.object_id)).block();
        return this.object_cached;
    }
}
