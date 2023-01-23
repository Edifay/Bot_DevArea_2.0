package devarea.fr.discord.entities.events_filler;

import devarea.fr.discord.entities.Context;
import discord4j.core.object.VoiceState;

public class MemberLeaveVoiceEventFiller extends Filler<VoiceState> {
    public MemberLeaveVoiceEventFiller(VoiceState event) {
        super(event);
    }

    @Override
    public Context context() {
        return null;
    }
}
