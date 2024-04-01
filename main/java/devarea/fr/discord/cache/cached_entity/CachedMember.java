package devarea.fr.discord.cache.cached_entity;


import devarea.fr.discord.Core;
import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entities.Mem;
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
            Member member = Core.devarea.getMemberById(Snowflake.of(this.object_id)).block();

            if (this.object_cached != null)
                this.object_cached.update(member);
            else
                this.object_cached = Mem.of(member);

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

    @Override
    public void use(Mem object_cached) throws Exception {
        if (this.object_id.equals(object_cached.getId().asString())) {
            this.object_cached.update(object_cached.entity);
            this.last_fetch = System.currentTimeMillis();
        } else
            throw new Exception("Wrong member usage !");
    }
}
