package devarea.fr.discord.commands;

import devarea.fr.utils.Logger;
import discord4j.core.object.entity.Member;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

import java.util.concurrent.atomic.AtomicBoolean;

public class Permissions {

    final Permission[] permissions;


    private Permissions(final Permission... permissions) {
        this.permissions = permissions;
    }

    public static Permissions of(final Permission... permissions) {
        return new Permissions(permissions);
    }

    public PermissionSet getSet() {
        return PermissionSet.of(this.permissions);
    }

    public boolean isMemberHasPermissions(Member member) {
        PermissionSet perm = member.getBasePermissions().block();
        PermissionSet current = getSet();

        AtomicBoolean atReturn = new AtomicBoolean(true);
        current.stream().iterator().forEachRemaining(permission -> {
            AtomicBoolean haveFind = new AtomicBoolean(false);
            perm.stream().iterator().forEachRemaining(permission1 -> {
                if (permission.equals(permission1)) haveFind.set(true);
            });
            if (!haveFind.get()) atReturn.set(false);
        });

        return atReturn.get();
    }

}
