package devarea.fr.discord.entity.events_filler;

import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.commands.Context;
import devarea.fr.discord.entity.Mem;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;

public class SlashCommandFiller extends Filler<ChatInputInteractionEvent> {

    public final Mem mem;

    public SlashCommandFiller(final ChatInputInteractionEvent event) {
        super(event);
        this.mem = MemberCache.get(event.getInteraction().getMember().get().getId().asString());
    }

    @Override
    public Context context() {
        return Context.builder()
                .messageId(event.getInteraction().getMessageId().isPresent() ? event.getInteraction().getMessageId().get().asString() : null)
                .channelId(event.getInteraction().getChannelId().asString())
                .build();
    }

}
