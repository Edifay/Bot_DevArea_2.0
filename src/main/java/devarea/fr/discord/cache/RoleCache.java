package devarea.fr.discord.cache;

import devarea.fr.discord.cache.cached_entity.CachedRole;
import discord4j.core.object.entity.Role;

import java.util.HashMap;

public class RoleCache {

    public static final HashMap<String, CachedRole> roles = new HashMap<>();

    public static Role get(final String roleID) {
        CachedRole cachedRole = getCachedRole(roleID);
        if (cachedRole == null) {
            cachedRole = new CachedRole(roleID);
            roles.put(roleID, cachedRole);
        }

        return cachedRole.get();
    }

    public static Role fetch(final String roleID) {
        CachedRole cachedRole = getCachedRole(roleID);
        if (cachedRole == null) {
            cachedRole = new CachedRole(roleID);
            roles.put(roleID, cachedRole);
        }

        return cachedRole.fetch();
    }

    public static Role watch(final String roleID) {
        CachedRole cachedRole = getCachedRole(roleID);
        if (cachedRole == null) {
            cachedRole = new CachedRole(roleID);
            cachedRole.fetch();
            roles.put(roleID, cachedRole);
        }

        return cachedRole.watch();
    }

    public static int count(final String roleID) {
        return CachedRole.getRoleMemberCount(roleID);
    }

    private static CachedRole getCachedRole(final String roleID) {
        return roles.get(roleID);
    }

}
