package devarea.fr.discord.badges.success_badge;

import devarea.fr.discord.Core;

import static devarea.fr.discord.statics.DefaultData.DOMAIN_NAME;

public class Profile_Badge extends SuccessBadge {
    public Profile_Badge() {
        super("Profil complété", DOMAIN_NAME + "assets/images/badges/70x70/success_badges/profile_badge.png",
                "Ce membre a complété son profil !", Core.badgesImages.get("profile_badge"));
    }
}
