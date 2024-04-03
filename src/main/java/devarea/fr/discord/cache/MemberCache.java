package devarea.fr.discord.cache;

import devarea.fr.db.DBManager;
import devarea.fr.discord.cache.cached_entity.CachedMember;
import devarea.fr.discord.entities.Mem;
import discord4j.core.object.entity.Member;
import reactor.util.annotation.NonNull;

import java.util.HashMap;

public class MemberCache {

    private static final HashMap<String, CachedMember> members = new HashMap<>();

    public static Mem get(@NonNull final String memberID) {
        CachedMember cachedMember = getCachedMember(memberID);
        if (cachedMember == null) {
            if (nullID(memberID))
                throw new NullPointerException();
            cachedMember = new CachedMember(memberID);
            members.put(memberID, cachedMember);
        }

        return cachedMember.get();
    }

    public static Mem fetch(@NonNull final String memberID) {
        CachedMember cachedMember = getCachedMember(memberID);
        if (cachedMember == null) {
            if (nullID(memberID))
                throw new NullPointerException();
            cachedMember = new CachedMember(memberID);
            members.put(memberID, cachedMember);
        }

        return cachedMember.fetch();
    }

    public static Mem watch(@NonNull final String memberID) {
        if (nullID(memberID))
            throw new NullPointerException();
        CachedMember cachedMember = getCachedMember(memberID);
        if (cachedMember == null) {
            cachedMember = new CachedMember(memberID);
            members.put(memberID, cachedMember);
            return cachedMember.get();
        }
        return cachedMember.watch();
    }

    public static void reset(@NonNull final String memberID) {
        CachedMember cachedMember = getCachedMember(memberID);
        if (cachedMember == null) {
            if (nullID(memberID))
                throw new NullPointerException();
            cachedMember = new CachedMember(memberID);
            members.put(memberID, cachedMember);
        }

        cachedMember.reset();
    }

    public static void use(@NonNull Member... membersAtAdd) {
        for (Member member : membersAtAdd) {
            if (member != null) {
                CachedMember cachedMember = getCachedMember(member.getId().asString());
                if (cachedMember == null)
                    members.put(member.getId().asString(), new CachedMember(member, System.currentTimeMillis()));
                else {
                    try {
                        cachedMember.use(Mem.of(member));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void slash(final String memberID) {
        members.remove(memberID);
        DBManager.memberLeft(memberID);
    }

    private static CachedMember getCachedMember(final String memberID) {
        return members.get(memberID);
    }

    public static HashMap<String, CachedMember> cache() {
        return members;
    }

    public static int cacheSize() {
        return members.size();
    }

    public static boolean contain(final String memberID) {
        return members.containsKey(memberID);
    }

    private static boolean nullID(final String memberID) {
        return memberID == null;
    }

}