package devarea.fr.discord.entities.events_filler;

import devarea.fr.discord.entities.Context;
import devarea.fr.discord.entities.Mem;
import discord4j.core.event.domain.guild.MemberJoinEvent;

public class MemberJoinEventFiller extends Filler<MemberJoinEvent> {

    public final Mem mem;

    public MemberJoinEventFiller(final Mem mem) {
        super(null);
        this.mem = mem;
    }

    @Override
    public Context context() {
        return Context.builder().build();
    }
}
