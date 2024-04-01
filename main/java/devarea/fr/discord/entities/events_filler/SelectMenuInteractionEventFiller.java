package devarea.fr.discord.entities.events_filler;

import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entities.Context;
import devarea.fr.discord.entities.Mem;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;

public class SelectMenuInteractionEventFiller extends Filler<SelectMenuInteractionEvent> {

    public Mem mem;

    public SelectMenuInteractionEventFiller(SelectMenuInteractionEvent event) {
        super(event);
        if (event.getInteraction().getMember().isPresent())
            this.mem = MemberCache.get(event.getInteraction().getMember().get().getId().asString());
    }

    @Override
    public Context context() {
        return Context.builder()
                .messageId(event.getMessageId().asString())
                .channelId(event.getInteraction().getChannelId().asString())
                .build();
    }
}
