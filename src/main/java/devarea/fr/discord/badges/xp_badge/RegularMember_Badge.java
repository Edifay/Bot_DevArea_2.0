package devarea.fr.discord.badges.xp_badge;

import devarea.fr.discord.Core;

import static devarea.fr.discord.statics.DefaultData.DOMAIN_NAME;

public class RegularMember_Badge extends XPBadges {
    public RegularMember_Badge() {
        super("Membre Régulier", DOMAIN_NAME + "assets/images/badges/70x70/xp_badges/regularmember_badge.png",
                "régulier", Core.badgesImages.get("regularMember_badge"));
    }
}
