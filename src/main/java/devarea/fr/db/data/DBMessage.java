package devarea.fr.db.data;

import com.mongodb.client.model.Updates;
import devarea.fr.discord.cache.ChannelCache;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import org.bson.Document;
import org.bson.conversions.Bson;

public class DBMessage implements DBItem {

    protected String idMessage;
    protected String idChannel;


    public DBMessage(final String idMessage, final String idChannel) {
        this.idMessage = idMessage;
        this.idChannel = idChannel;
    }

    public DBMessage(final Message message) {
        this.idMessage = message.getId().asString();
        this.idChannel = message.getChannelId().asString();
    }

    public DBMessage(final Document document) {
        this.idMessage = (String) document.get("idMessage");
        this.idChannel = (String) document.get("idChannel");
    }

    public Message getMessage() {
        return ((GuildMessageChannel) ChannelCache.get(idChannel).entity).getMessageById(Snowflake.of(this.idMessage)).block();
    }

    public GuildMessageChannel getChannel() {
        return (GuildMessageChannel) ChannelCache.get(idChannel).entity;
    }

    public String getMessageID() {
        return this.idMessage;
    }

    public String getChannelID() {
        return this.idChannel;
    }

    @Override
    public Document toDocument() {
        return new Document()
                .append("idMessage", this.idMessage)
                .append("idChannel", this.idChannel);
    }

    @Override
    public Bson toUpdates() {
        return Updates.combine(
                Updates.set("idMessage", this.idMessage),
                Updates.set("idChannel", this.idChannel)
        );
    }

    @Override
    public String toString() {
        return "DBMessage{" +
                "idMessage='" + idMessage + '\'' +
                ", idChannel='" + idChannel + '\'' +
                '}';
    }
}
