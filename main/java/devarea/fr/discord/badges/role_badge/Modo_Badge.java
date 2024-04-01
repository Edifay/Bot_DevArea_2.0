package devarea.fr.discord.badges.role_badge;

import devarea.fr.discord.Core;

import static devarea.fr.discord.statics.DefaultData.DOMAIN_NAME;

public class Modo_Badge extends RolesBadges {
    public Modo_Badge() {
        super("Modérateur", DOMAIN_NAME + "assets/images/badges/70x70/roles_badges/modo_badge.png",
                "Ce membre est modérateur sur le serveur !", Core.badgesImages.get("modo_badge"));
    }
}
