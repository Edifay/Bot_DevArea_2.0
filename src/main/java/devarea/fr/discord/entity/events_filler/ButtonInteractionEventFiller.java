package devarea.fr.discord.entity.events_filler;

import devarea.fr.discord.commands.Context;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;

public class ButtonInteractionEventFiller extends Filler<ButtonInteractionEvent> {
    public ButtonInteractionEventFiller(ButtonInteractionEvent event) {
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
