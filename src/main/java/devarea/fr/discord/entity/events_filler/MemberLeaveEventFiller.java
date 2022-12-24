package devarea.fr.discord.entity.events_filler;

import discord4j.common.util.Snowflake;

public class MemberLeaveEventFiller {
    public final Snowflake memberId;

    public MemberLeaveEventFiller(final Snowflake memberId) {
        this.memberId = memberId;
    }
}
