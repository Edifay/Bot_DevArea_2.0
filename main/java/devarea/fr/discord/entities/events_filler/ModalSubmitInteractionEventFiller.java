package devarea.fr.discord.entities.events_filler;

import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entities.Context;
import devarea.fr.discord.entities.Mem;
import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent;

public class ModalSubmitInteractionEventFiller extends Filler<ModalSubmitInteractionEvent> {

    public Mem mem;

    public ModalSubmitInteractionEventFiller(ModalSubmitInteractionEvent event) {
        super(event);
        this.mem = event.getInteraction().getMember().isPresent() ? MemberCache.get(event.getInteraction().getMember().get().getId().asString()) : MemberCache.get(event.getInteraction().getUser().getId().asString());
    }

    @Override
    public Context context() {
        return Context.builder()
                .messageId(event.getMessageId().asString())
                .channelId(event.getInteraction().getChannelId().asString())
                .build();
    }
}
