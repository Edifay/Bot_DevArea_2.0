package devarea.fr.discord.badges.time_badge;

import devarea.fr.discord.Core;

import static devarea.fr.discord.statics.DefaultData.DOMAIN_NAME;

public class Precursor_Badge extends TimeOnServerBadge {

    public Precursor_Badge(final String on_server_time) {
        super("Précurseur", DOMAIN_NAME + "assets/images/badges/70x70/time_badges/precursor_badge.png",
                on_server_time, Core.badgesImages.get("precursor_badge"));
    }
}
