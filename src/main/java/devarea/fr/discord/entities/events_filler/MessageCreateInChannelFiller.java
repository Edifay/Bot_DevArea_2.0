package devarea.fr.discord.entities.events_filler;

import devarea.fr.discord.entities.Context;
import discord4j.core.event.domain.message.MessageCreateEvent;

public class MessageCreateInChannelFiller extends Filler<MessageCreateEvent> {

    public MessageCreateInChannelFiller(final MessageCreateEvent event) {
        super(event);
    }

    @Override
    public Context context() {
        return Context.builder()
                .messageId(event.getMessage().getId().asString())
                .channelId(event.getMessage().getChannelId().asString())
                .build();
    }

}
