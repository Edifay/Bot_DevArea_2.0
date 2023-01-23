package devarea.fr.discord.badges.role_badge;

import devarea.fr.discord.badges.Badges;
import devarea.fr.discord.entities.Mem;
import discord4j.common.util.Snowflake;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Set;

public abstract class RolesBadges extends Badges {
    // Staff
    public static final Snowflake fonda = Snowflake.of("768372172552667176");
    public static final Snowflake admin = Snowflake.of("768383784571240509");
    public static final Snowflake modo = Snowflake.of("777782222920744990");
    public static final Snowflake helper = Snowflake.of("777816365641760788");
    public static final Snowflake graphiste = Snowflake.of("840540882376851466");
    public static final Snowflake booster = Snowflake.of("775025210620903456");
    public static final Snowflake partner = Snowflake.of("771347908813520896");

    // Roles
    public static final Snowflake contest_winner = Snowflake.of("986924934959865876");

    public RolesBadges(String name, String url_icon, String description, BufferedImage local_icon) {
        super(name, url_icon, description, local_icon);
    }

    public static ArrayList<RolesBadges> getRolesBadges(final Mem mem) {
        ArrayList<RolesBadges> badges = new ArrayList<>();

        Set<Snowflake> roles = mem.entity.getRoleIds();

        // Staff
        if (roles.contains(fonda))
            badges.add(new Fonda_Badge());
        if (roles.contains(admin))
            badges.add(new Admin_Badge());
        if (roles.contains(modo))
            badges.add(new Modo_Badge());
        if (roles.contains(helper))
            badges.add(new Helper_Badge());
        if (roles.contains(graphiste))
            badges.add(new Graphist_Badge());

        // Roles
        if (roles.contains(contest_winner))
            badges.add(new Winner_Badge());
        if (roles.contains(booster))
            badges.add(new Booster_Badge());
        if (roles.contains(partner))
            badges.add(new Partner_Badge());


        return badges;
    }
}
