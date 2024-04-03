package devarea.fr.discord.commands;

import discord4j.core.object.entity.Member;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

import java.util.concurrent.atomic.AtomicBoolean;

public class Permissions {

    /**
     * The permission list
     */
    final Permission[] permissions;


    private Permissions(final Permission... permissions) {
        this.permissions = permissions;
    }

    /**
     * Create a Permissions object from a permission list.
     *
     * @param permissions the permission list
     * @return the Permissions object
     */
    public static Permissions of(final Permission... permissions) {
        return new Permissions(permissions);
    }

    /**
     * @return the PermissionSet from discord4J objects
     */
    public PermissionSet getSet() {
        return PermissionSet.of(this.permissions);
    }

    /**
     * Method to check if a member have the permissions of the current object.
     *
     * @param member the member to check
     * @return true if the member contain the current object permissions, false if not.
     */
    public boolean isMemberOwningPermissions(Member member) {
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
