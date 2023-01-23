package devarea.fr.discord.badges.time_badge;

import devarea.fr.discord.badges.Badges;
import devarea.fr.discord.entities.Mem;

import java.awt.image.BufferedImage;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public abstract class TimeOnServerBadge extends Badges {

    public static final long millis_epoch_created = 1595894400000L;

    public TimeOnServerBadge(final String name, final String url, final String time_on_server,
                             final BufferedImage local_icon) {
        super(name, url, "Ce membre a rejoint le serveur le " + time_on_server + ".",
                local_icon);
    }


    public static TimeOnServerBadge getTimeBadgeOf(final Mem mem) {
        Instant instant = mem.entity.getJoinTime().get();
        if (instant.isBefore(Instant.ofEpochMilli(millis_epoch_created).plus(90, ChronoUnit.DAYS)))
            return new Precursor_Badge(instant.toString());
        else if (instant.isBefore(Instant.now().minus(365, ChronoUnit.DAYS)))
            return new Senior_Badge(instant.toString());
        else return new Junior_Badge(instant.toString());
    }

}
