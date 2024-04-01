package devarea.fr.discord.badges.role_badge;


import devarea.fr.discord.Core;

import static devarea.fr.discord.statics.DefaultData.DOMAIN_NAME;

public class Winner_Badge extends RolesBadges {
    public Winner_Badge() {
        super("Contest Winner", DOMAIN_NAME + "assets/images/badges/70x70/roles_badges/winner_badge.png",
                "Ce membre a fini sur le podium du dernier Contest organisé par le serveur !", Core.badgesImages.get("winner_badge"));
    }
}
