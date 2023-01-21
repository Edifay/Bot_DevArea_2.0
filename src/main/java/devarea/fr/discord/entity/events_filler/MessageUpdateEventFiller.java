package devarea.fr.discord.entity.events_filler;

import devarea.fr.discord.commands.Context;
import discord4j.core.event.domain.message.MessageUpdateEvent;

public class MessageUpdateEventFiller extends Filler<MessageUpdateEvent> {
    public MessageUpdateEventFiller(MessageUpdateEvent event) {
        super(event);
    }

    @Override
    public Context context() {
        return Context.builder()
                .messageId(event.getMessageId().asString())
                .channelId(event.getChannelId().asString())
                .build();
    }
}
