package devarea.fr.discord.entity.events_filler;

import devarea.fr.discord.commands.Context;
import discord4j.core.event.domain.message.MessageDeleteEvent;

public class MessageDeleteEventFiller extends Filler<MessageDeleteEvent> {
    public MessageDeleteEventFiller(MessageDeleteEvent event) {
        super(event);
    }

    @Override
    public Context context() {
        return Context.builder()
                .channelId(event.getChannelId().asString())
                .build();
    }
}
