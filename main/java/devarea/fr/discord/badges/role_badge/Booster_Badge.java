package devarea.fr.discord.badges.role_badge;

import devarea.fr.discord.Core;

import static devarea.fr.discord.statics.DefaultData.DOMAIN_NAME;

public class Booster_Badge extends RolesBadges {

    public Booster_Badge() {
        super("Booster", DOMAIN_NAME + "assets/images/badges/70x70/roles_badges/booster_badge.png",
                "Ce membre booste gentiment le serveur !", Core.badgesImages.get("booster_badge"));
    }

}
