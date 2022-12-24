package devarea.fr.discord.cache.cached_entity;


import devarea.fr.discord.DevArea;
import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entity.Mem;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;

public class CachedMember extends CachedObject<Mem> {

    public CachedMember(Member member, long last_fetch) {
        super(Mem.of(member), last_fetch);
    }

    public CachedMember(Mem member, long last_fetch) {
        super(member, last_fetch);
    }

    public CachedMember(final String memberID) {
        super(memberID);
    }

    @Override
    public Mem fetch() {
        try {
            this.object_cached = Mem.of(DevArea.devarea.getMemberById(Snowflake.of(this.object_id)).block());
        } catch (Exception e) {
            System.err.println("ERROR: Member couldn't be fetched !");
            this.object_cached = null;
        }

        if (this.object_cached == null) {
            MemberCache.slash(this.object_id);
            return null;
        }

        this.last_fetch = System.currentTimeMillis();
        return this.object_cached;
    }
}
