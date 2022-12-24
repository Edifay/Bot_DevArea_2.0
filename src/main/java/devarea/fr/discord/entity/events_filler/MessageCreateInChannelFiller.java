package devarea.fr.discord.entity.events_filler;

import discord4j.core.event.domain.message.MessageCreateEvent;

public class MessageCreateInChannelFiller {

    public final MessageCreateEvent event;

    public MessageCreateInChannelFiller(final MessageCreateEvent event) {
        this.event = event;
    }

}
