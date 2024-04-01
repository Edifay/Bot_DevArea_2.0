package devarea.fr.discord.commands.slash;

import devarea.fr.discord.Core;
import devarea.fr.discord.commands.Permissions;
import devarea.fr.discord.commands.SlashCommand;
import devarea.fr.discord.entities.events_filler.SlashCommandFiller;
import devarea.fr.discord.statics.ColorsUsed;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Permission;

import java.util.Set;

import static devarea.fr.discord.statics.TextMessage.stopCommand;

public class Stop extends SlashCommand {
    @Override
    public ApplicationCommandRequest definition() {
        return ApplicationCommandRequest.builder()
                .name("stop")
                .description("Commande pour Edifay ;) !")
                .build();
    }

    @Override
    public void play(SlashCommandFiller filler) {
        filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                .ephemeral(true)
                .addEmbed(EmbedCreateSpec.builder()
                        .title(stopCommand)
                        .color(ColorsUsed.same).build())
                .build()).block();
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        for (Thread t : threadSet)
            if (!t.equals(Thread.currentThread()))
                t.interrupt();
        Core.client.logout().block();
        System.exit(0);
    }

    @Override
    public Permissions permissions() {
        return Permissions.of(Permission.ADMINISTRATOR);
    }
}
