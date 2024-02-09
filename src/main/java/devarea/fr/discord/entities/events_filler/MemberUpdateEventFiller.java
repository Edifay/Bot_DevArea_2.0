package devarea.fr.discord.entities.events_filler;

import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entities.Context;
import devarea.fr.discord.entities.Mem;
import discord4j.core.event.domain.guild.MemberUpdateEvent;

public class MemberUpdateEventFiller extends Filler<MemberUpdateEvent> {

    final public Mem mem;

    public MemberUpdateEventFiller(MemberUpdateEvent event) {
        super(event);
        this.mem = MemberCache.get(event.getMemberId().asString());
    }

    @Override
    public Context context() {
        return Context.builder().build();
    }
}
