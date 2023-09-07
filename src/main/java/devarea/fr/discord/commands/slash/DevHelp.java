package devarea.fr.discord.commands.slash;

import devarea.fr.discord.Core;
import devarea.fr.discord.cache.ChannelCache;
import devarea.fr.discord.commands.SlashCommand;
import devarea.fr.discord.entities.Chan;
import devarea.fr.discord.entities.events_filler.SlashCommandFiller;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.entity.channel.ThreadChannel;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.AllowedMentions;

import javax.sound.midi.Soundbank;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

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

        if (!parentId.equals(Core.data.assistance_category)) { // Not in an help channel.
            filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                .ephemeral(true)
                .content("Uniquement les channels d'entraide acceptent cette commande.")
                .build()).subscribe();
            return;
        }

        if (timer.contains(channel.getId())) { // In cooldown
            filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                .ephemeral(true)
                .content("La commande `/devhelp` nécessite un temps de recharge de 1h30.")
                .build()).subscribe();
            return;
        }


        // Verification that the member is owning a message in the last 5 of the channel.
        GuildMessageChannel chan = ((GuildMessageChannel) channel.entity);

        final AtomicBoolean explicationMessageFound = new AtomicBoolean(chan.getLastMessage().block().getAuthor().get().getId().equals(filler.mem.getId()));
        if (!explicationMessageFound.get())
            chan.getMessagesBefore(chan.getLastMessageId().get()).take(4).doOnEach(messageSignal -> {
                if (messageSignal.hasValue() && messageSignal.get().getAuthor().get().getId().equals(filler.mem.getId()))
                    explicationMessageFound.set(true);
            }).blockLast();

        if (!explicationMessageFound.get()) {
            filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                .ephemeral(true)
                .content("Vous devez expliquer votre problème avant d'exécuter cette commande !")
                .build()).subscribe();
            return;
        }

        startAway(() -> {
            filler.event.deferReply().block();
            filler.event.deleteReply().subscribe();
        });

        ((GuildMessageChannel) ChannelCache.watch(filler.event.getInteraction().getChannelId().asString()).entity).createMessage(
            MessageCreateSpec.builder()
                .allowedMentions(AllowedMentions.builder()
                    .allowRole(Core.data.devHelper_role)
                    .allowUser(filler.mem.getId())
                    .build())
                .content("<@" + filler.mem.getId().asString() + ">, a demandé de " +
                         "l'aide ! <@&" + Core.data.devHelper_role.asString() + ">.").build()
        ).subscribe();

        timer.add(channel.getId());

        startAwayIn(() -> timer.remove(channel.getId()), 2700000);
    }
}
