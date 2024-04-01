package devarea.fr.discord.entities.events_filler;

import devarea.fr.discord.entities.Context;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.guild.MemberLeaveEvent;

public class MemberLeaveEventFiller extends Filler<MemberLeaveEvent> {
    public final Snowflake memberId;

    public MemberLeaveEventFiller(final Snowflake memberId) {
        super(null);
        this.memberId = memberId;
    }

    @Override
    public Context context() {
        return Context.builder().build();
    }
}
