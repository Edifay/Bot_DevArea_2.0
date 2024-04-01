package devarea.fr.discord.badges.role_badge;

import devarea.fr.discord.Core;

import static devarea.fr.discord.statics.DefaultData.DOMAIN_NAME;

public class Partner_Badge extends RolesBadges {
    public Partner_Badge() {
        super("Partenaire", DOMAIN_NAME + "assets/images/badges/70x70/roles_badges/partner_badge.png",
                "Ce membre est partenaire avec le serveur !", Core.badgesImages.get("partner_badge"));
    }
}
