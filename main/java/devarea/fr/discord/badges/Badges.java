package devarea.fr.discord.badges;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import devarea.fr.discord.badges.role_badge.RolesBadges;
import devarea.fr.discord.badges.success_badge.SuccessBadge;
import devarea.fr.discord.badges.time_badge.TimeOnServerBadge;
import devarea.fr.discord.badges.xp_badge.XPBadges;
import devarea.fr.discord.entities.Mem;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public abstract class Badges {

    @JsonProperty
    protected final String name;
    @JsonProperty
    protected final String url_icon;
    @JsonProperty
    protected final String description;
    @JsonIgnore
    protected final BufferedImage local_icon;

    public Badges(final String name, final String url_icon, final String description, final BufferedImage local_icon) {
        this.name = name;
        this.url_icon = url_icon;
        this.description = description;
        this.local_icon = local_icon;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl_icon() {
        return url_icon;
    }

    public BufferedImage getLocal_icon() {
        return local_icon;
    }

    public static ArrayList<Badges> getBadgesOf(final Mem mem) {
        ArrayList<Badges> badges = new ArrayList<>(RolesBadges.getRolesBadges(mem));
        Badges xpBadge;
        if ((xpBadge = XPBadges.getXPBadgesOf(mem)) != null)
            badges.add(xpBadge);
        badges.addAll(SuccessBadge.getSuccessBadges(mem));
        badges.add(TimeOnServerBadge.getTimeBadgeOf(mem));
        return badges;
    }

}
