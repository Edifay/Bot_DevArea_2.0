package devarea.fr.discord.badges.xp_badge;

import devarea.fr.discord.badges.Badges;
import devarea.fr.discord.entities.Mem;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;

public abstract class XPBadges extends Badges {
    public XPBadges(String name, String url_icon, String description, BufferedImage local_icon) {
        super(name, url_icon, "Ce membre est un membre " + description + " sur le serveur !", local_icon);
    }

    public static XPBadges getXPBadgesOf(final Mem mem) {
        int xpLastWeekCount = getXpCountOnLastWeek(mem);
        if (xpLastWeekCount > 100)
            return new ActiveMember_Badge();
        else if (xpLastWeekCount > 50)
            return new RegularMember_Badge();
        else if (xpLastWeekCount > 25)
            return new OccasionedMember_Badge();

        return null;
    }


    private static int getXpCountOnLastWeek(final Mem mem) {
        int count = 0;
        final HashMap<String, Integer> xpHistory = mem.db().getXPHistory();
        LocalDateTime dateTime = LocalDateTime.now();

        for (int i = 0; i < 7; i++) {
            final String date = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH).format(dateTime);
            if (xpHistory.containsKey(date))
                count += xpHistory.get(date);
            dateTime = dateTime.minusDays(1L);
        }

        return count;
    }

}
