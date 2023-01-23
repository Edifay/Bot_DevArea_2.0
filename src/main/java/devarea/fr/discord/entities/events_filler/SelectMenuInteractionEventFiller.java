package devarea.fr.discord.entities.events_filler;

import devarea.fr.discord.entities.Context;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;

public class SelectMenuInteractionEventFiller extends Filler<SelectMenuInteractionEvent> {
    public SelectMenuInteractionEventFiller(SelectMenuInteractionEvent event) {
        super(event);
    }

    @Override
    public Context context() {
        return Context.builder()
                .messageId(event.getMessageId().asString())
                .channelId(event.getInteraction().getChannelId().asString())
                .build();
    }
}
