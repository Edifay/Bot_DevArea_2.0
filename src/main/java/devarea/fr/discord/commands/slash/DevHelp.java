package devarea.fr.discord.commands.slash;

import devarea.fr.discord.DevArea;
import devarea.fr.discord.cache.ChannelCache;
import devarea.fr.discord.commands.SlashCommand;
import devarea.fr.discord.entity.Chan;
import devarea.fr.discord.entity.events_filler.SlashCommandFiller;
import devarea.fr.utils.Logger;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.entity.channel.ThreadChannel;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.AllowedMentions;

import java.util.ArrayList;

import static devarea.fr.utils.ThreadHandler.startAway;
import static devarea.fr.utils.ThreadHandler.startAwayIn;

public class DevHelp extends SlashCommand {

    private static final ArrayList<Snowflake> timer = new ArrayList<>();

    @Override
    public ApplicationCommandRequest definition() {
        return ApplicationCommandRequest.builder()
                .name("devhelp")
                .description("Commande qui permet d'envoyer un ping au développeurs volontaires dans les channels " +
                        "entraides.")
                .build();
    }

    @Override
    public void play(SlashCommandFiller filler) {
        final Chan channel = ChannelCache.watch(filler.event.getInteraction().getChannelId().asString());
        Snowflake parentId = null;

        if (channel.entity instanceof ThreadChannel && ((ThreadChannel) channel.entity).getParentId().isPresent())
            parentId = Snowflake.of(ChannelCache.watch(((ThreadChannel) channel.entity).getParentId().get().asString()).getData().parentId().get().get());
        if (channel.entity instanceof TextChannel)
            parentId = ((TextChannel) channel.entity).getCategoryId().get();
        if (parentId == null)
            return;

        if (parentId.equals(DevArea.data.assistance_category)) {
            if (!timer.contains(channel.getId())) {

                startAway(() -> {
                    filler.event.deferReply().block();
                    filler.event.deleteReply().subscribe();
                });

                ((GuildMessageChannel) ChannelCache.watch(filler.event.getInteraction().getChannelId().asString()).entity).createMessage(
                        MessageCreateSpec.builder()
                                .allowedMentions(AllowedMentions.builder()
                                        .allowRole(DevArea.data.devHelper_role)
                                        .allowUser(filler.mem.getId())
                                        .build())
                                .content("<@" + filler.mem.getId().asString() + ">, a demandé de " +
                                        "l'aide ! <@&" + DevArea.data.devHelper_role.asString() + ">.").build()
                ).subscribe();

                timer.add(channel.getId());

                startAwayIn(() -> timer.remove(channel.getId()), 1800000);
            } else
                filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                        .ephemeral(true)
                        .content("La commande devhelp n'est disponible que toutes les 30 minutes.")
                        .build()).subscribe();
        } else
            filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                    .ephemeral(true)
                    .content("Uniquement les channels d'entraide acceptent cette commande.")
                    .build()).subscribe();
    }
}
