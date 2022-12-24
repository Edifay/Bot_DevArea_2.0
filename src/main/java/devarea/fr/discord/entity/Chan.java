package devarea.fr.discord.entity;

import discord4j.core.object.entity.channel.GuildChannel;


public class Chan extends ObjectListener<GuildChannel> {


    public Chan(GuildChannel entity) {
        super(entity);
    }

    public static Chan of(final GuildChannel channel) {
        return new Chan(channel);
    }
}