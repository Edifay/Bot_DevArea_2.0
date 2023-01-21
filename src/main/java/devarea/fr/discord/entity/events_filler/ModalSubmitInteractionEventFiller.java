package devarea.fr.discord.entity.events_filler;

import devarea.fr.discord.commands.Context;
import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent;

public class ModalSubmitInteractionEventFiller extends Filler<ModalSubmitInteractionEvent> {
    public ModalSubmitInteractionEventFiller(ModalSubmitInteractionEvent event) {
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
