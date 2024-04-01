package devarea.fr.discord.badges.role_badge;

import devarea.fr.discord.Core;

import static devarea.fr.discord.statics.DefaultData.DOMAIN_NAME;

public class Fonda_Badge extends RolesBadges {
    public Fonda_Badge() {
        super("Fondateur", DOMAIN_NAME + "assets/images/badges/70x70/roles_badges/fonda_badge.png",
                "Ce membre est le fondateur de Dev'Area !", Core.badgesImages.get("fonda_badge"));
    }
}
