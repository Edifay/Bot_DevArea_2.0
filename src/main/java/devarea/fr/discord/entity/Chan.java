package devarea.fr.discord.entity;

import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.discordjson.json.ChannelData;


public class Chan<T extends GuildChannel> extends ObjectListener<T> {


    public Chan(T entity) {
        super(entity);
    }

    public static Chan of(final GuildChannel channel) {
        return new Chan(channel);
    }

    public ChannelData getData(){
        return this.entity.getData();
    }

}