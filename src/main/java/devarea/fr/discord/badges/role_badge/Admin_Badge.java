package devarea.fr.discord.badges.role_badge;

import devarea.fr.discord.Core;

import static devarea.fr.discord.statics.DefaultData.DOMAIN_NAME;

public class Admin_Badge extends RolesBadges {
    public Admin_Badge() {
        super("Admin", DOMAIN_NAME + "assets/images/badges/70x70/roles_badges/admin_badge.png",
                "Ce membre est administrateur sur le serveur !", Core.badgesImages.get("admin_badge"));
    }
}
