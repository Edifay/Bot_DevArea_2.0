package devarea.fr.discord.entity.events_filler;

import devarea.fr.discord.commands.Context;
import discord4j.core.event.domain.message.ReactionRemoveEvent;

public class ReactionRemoveEventFiller extends Filler<ReactionRemoveEvent> {
    public ReactionRemoveEventFiller(ReactionRemoveEvent event) {
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
