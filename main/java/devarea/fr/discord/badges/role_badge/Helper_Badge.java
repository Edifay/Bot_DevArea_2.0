package devarea.fr.discord.badges.role_badge;

import devarea.fr.discord.Core;

import static devarea.fr.discord.statics.DefaultData.DOMAIN_NAME;

public class Helper_Badge extends RolesBadges {
    public Helper_Badge() {
        super("Helper", DOMAIN_NAME + "assets/images/badges/70x70/roles_badges/helper_badge.png",
                "Ce membre est Helper sur le serveur !", Core.badgesImages.get("helper_badge"));
    }
}
