package devarea.fr.discord.entity.events_filler;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;

public class SlashCommandFiller {

    public final ChatInputInteractionEvent event;

    public SlashCommandFiller(final ChatInputInteractionEvent event) {
        this.event = event;
    }

}
