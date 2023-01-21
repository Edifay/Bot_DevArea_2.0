package devarea.fr.discord.entity.events_filler;

import devarea.fr.discord.commands.Context;
import discord4j.core.event.domain.message.MessageCreateEvent;

public class MessageCreateEventFiller extends Filler<MessageCreateEvent> {
    public MessageCreateEventFiller(MessageCreateEvent event) {
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
