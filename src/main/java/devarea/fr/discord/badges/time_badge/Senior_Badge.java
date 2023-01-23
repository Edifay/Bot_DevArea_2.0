package devarea.fr.discord.badges.time_badge;

import devarea.fr.discord.Core;

import static devarea.fr.discord.statics.DefaultData.DOMAIN_NAME;

public class Senior_Badge extends TimeOnServerBadge {

    public Senior_Badge(final String on_server_time) {
        super("Senior", DOMAIN_NAME + "assets/images/badges/70x70/time_badges/senior_badge.png", on_server_time,
                Core.badgesImages.get("senior_badge"));
    }

}
