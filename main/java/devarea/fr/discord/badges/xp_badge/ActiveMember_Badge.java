package devarea.fr.discord.badges.xp_badge;


import devarea.fr.discord.Core;

import static devarea.fr.discord.statics.DefaultData.DOMAIN_NAME;

public class ActiveMember_Badge extends XPBadges {
    public ActiveMember_Badge() {
        super("Membre Actif", DOMAIN_NAME + "assets/images/badges/70x70/xp_badges/activemember_badge.png", "actif",
                Core.badgesImages.get("activeMember_badge"));
    }
}
