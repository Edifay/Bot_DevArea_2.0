package devarea.fr.discord.badges.time_badge;

import devarea.fr.discord.Core;

import static devarea.fr.discord.statics.DefaultData.DOMAIN_NAME;

public class Junior_Badge extends TimeOnServerBadge {
    public Junior_Badge(final String on_server_time) {
        super("Junior", DOMAIN_NAME + "assets/images/badges/70x70/time_badges/junior_badge.png", on_server_time,
                Core.badgesImages.get("junior_badge"));
    }
}
