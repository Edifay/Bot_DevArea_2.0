package devarea.fr.discord.entities.events_filler;

import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entities.Context;
import devarea.fr.discord.entities.Mem;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;

public class ButtonInteractionEventFiller extends Filler<ButtonInteractionEvent> {
    public Mem mem;

    public ButtonInteractionEventFiller(ButtonInteractionEvent event) {
        super(event);
        this.mem = MemberCache.get(event.getInteraction().getUser().getId().asString());
    }

    @Override
    public Context context() {
        return Context.builder()
                .messageId(event.getMessageId().asString())
                .channelId(event.getInteraction().getChannelId().asString())
                .build();
    }

}
