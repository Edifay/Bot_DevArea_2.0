package devarea.fr.discord.badges.role_badge;

import devarea.fr.discord.Core;

import static devarea.fr.discord.statics.DefaultData.DOMAIN_NAME;

public class Graphist_Badge extends RolesBadges {
    public Graphist_Badge() {
        super("Graphiste", DOMAIN_NAME + "assets/images/badges/70x70/roles_badges/graphist_badge.png",
                "Ce membre est un Graphiste du serveur !", Core.badgesImages.get("graphist_badge"));
    }
}
