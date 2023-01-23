package devarea.fr.discord.entities.events_filler;

import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entities.Context;
import devarea.fr.discord.entities.Mem;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;

/**
 * The filler from the ChatInputInteractionEvent.
 */
public class SlashCommandFiller extends Filler<ChatInputInteractionEvent> {

    public final Mem mem;

    public SlashCommandFiller(final ChatInputInteractionEvent event) {
        super(event);
        this.mem = MemberCache.get(event.getInteraction().getMember().get().getId().asString());
    }

    @Override
    public Context context() {
        return Context.builder()
                .messageId(event.getInteraction().getMessageId().isPresent() ? event.getInteraction().getMessageId().get().asString() : event.getReply().block().getId().asString())
                .channelId(event.getInteraction().getChannelId().asString())
                .build();
    }

}
