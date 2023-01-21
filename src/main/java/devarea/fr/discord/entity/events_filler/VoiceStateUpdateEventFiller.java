package devarea.fr.discord.entity.events_filler;

import devarea.fr.discord.commands.Context;
import discord4j.core.event.domain.VoiceStateUpdateEvent;

public class VoiceStateUpdateEventFiller extends Filler<VoiceStateUpdateEvent> {
    public VoiceStateUpdateEventFiller(VoiceStateUpdateEvent event) {
        super(event);
    }

    @Override
    public Context context() {
        return Context.builder().build();
    }
}
