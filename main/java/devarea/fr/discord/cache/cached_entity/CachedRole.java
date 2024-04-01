package devarea.fr.discord.cache.cached_entity;

import devarea.fr.discord.Core;
import devarea.fr.discord.cache.MemberCache;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Role;

import java.util.Objects;

public class CachedRole extends CachedObject<Role> {

    public CachedRole(final Role role, final long last_fetch) {
        super(role, last_fetch);
    }

    public CachedRole(final String roleID) {
        super(roleID);
    }


    @Override
    public Role fetch() {
        this.object_cached = Core.devarea.getRoleById(Snowflake.of(this.object_id)).block();
        return this.object_cached;
    }

    @Override
    public int hashCode() {
        return Objects.hash(object_id, object_cached, last_fetch);
    }

    public static int getRoleMemberCount(final String roleID) {
        int count = 0;

        for (CachedMember member : MemberCache.cache().values())
            if (member.watch() == null)
                System.err.println("ERROR: Member is null !");
            else if (member.watch().entity.getRoleIds().contains(Snowflake.of(roleID)))
                count++;

        return count;
    }
}
