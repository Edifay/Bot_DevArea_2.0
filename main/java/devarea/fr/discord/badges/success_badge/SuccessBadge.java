package devarea.fr.discord.badges.success_badge;

import devarea.fr.discord.badges.Badges;
import devarea.fr.discord.entities.Mem;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public abstract class SuccessBadge extends Badges {
    public SuccessBadge(String name, String url_icon, String description, BufferedImage local_icon) {
        super(name, url_icon, description, local_icon);
    }

    public static ArrayList<SuccessBadge> getSuccessBadges(final Mem mem) {
        ArrayList<SuccessBadge> badges = new ArrayList<>();
        if (mem.db().getDescription() != null)
            badges.add(new Profile_Badge());
        return badges;
    }
}
