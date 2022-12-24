package devarea.fr.discord.cache;

import devarea.fr.discord.cache.cached_entity.CachedChannel;
import devarea.fr.discord.entity.Chan;
import discord4j.core.object.entity.channel.GuildChannel;
import reactor.util.annotation.NonNull;

import java.util.HashMap;

public class ChannelCache {


    private static final HashMap<String, CachedChannel> channels = new HashMap<>();

    public static Chan get(@NonNull final String channelID) {
        CachedChannel cachedChannel = getCachedChannel(channelID);
        if (cachedChannel == null) {
            if (!working(channelID)) return null;
            cachedChannel = new CachedChannel(channelID);
            channels.put(channelID, cachedChannel);
        }

        return cachedChannel.get();
    }

    public static Chan fetch(@NonNull final String channelID) {
        CachedChannel cachedChannel = getCachedChannel(channelID);
        if (cachedChannel == null) {
            if (!working(channelID)) return null;
            cachedChannel = new CachedChannel(channelID);
            channels.put(channelID, cachedChannel);
        }

        return cachedChannel.fetch();
    }

    public static Chan watch(@NonNull final String channelID) {
        if (!working(channelID)) return null;
        CachedChannel cachedChannel = getCachedChannel(channelID);
        if (cachedChannel == null) {
            if (!working(channelID)) return null;
            cachedChannel = new CachedChannel(channelID);
            channels.put(channelID, cachedChannel);
            cachedChannel.get();
        }
        if (getCachedChannel(channelID) == null)
            return null;
        return cachedChannel.watch();
    }

    public static void use(@NonNull GuildChannel... channelsAtAdd) {
        for (GuildChannel channel : channelsAtAdd) {
            if (channel != null) {
                CachedChannel cachedChannel = getCachedChannel(channel.getId().asString());
                if (cachedChannel == null)
                    channels.put(channel.getId().asString(), new CachedChannel(channel, System.currentTimeMillis()));
                else {
                    try {
                        cachedChannel.use(Chan.of(channel));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void reset(@NonNull final String channelID) {
        CachedChannel cachedChannel = getCachedChannel(channelID);
        if (cachedChannel == null) {
            if (!working(channelID)) return;
            cachedChannel = new CachedChannel(channelID);
            channels.put(channelID, cachedChannel);
        }

        cachedChannel.reset();
    }

    public static void slash(final String channelID) {
        channels.remove(channelID);
    }

    private static CachedChannel getCachedChannel(final String channelID) {
        return channels.get(channelID);
    }

    public static HashMap<String, CachedChannel> cache() {
        return channels;
    }

    public static int cacheSize() {
        return channels.size();
    }

    public static boolean contain(final String channelID) {
        return channels.containsKey(channelID);
    }

    private static boolean working(final String channelID) {
        boolean working = true;
        if (channelID == null)
            working = false;
        return working;
    }

}
