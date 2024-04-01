package devarea.fr.discord.badges.xp_badge;

import devarea.fr.discord.Core;

import static devarea.fr.discord.statics.DefaultData.DOMAIN_NAME;

public class OccasionedMember_Badge extends XPBadges {
    public OccasionedMember_Badge() {
        super("Membre Occasionnel", DOMAIN_NAME + "assets/images/badges/70x70/xp_badges/occasionedmember_badge.png",
                "occasionnel", Core.badgesImages.get("occasionedMember_badge"));
    }
}
