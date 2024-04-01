package devarea.fr.discord.commands.slash;

import devarea.fr.discord.commands.Permissions;
import devarea.fr.discord.commands.SlashCommand;
import devarea.fr.discord.entities.events_filler.SlashCommandFiller;
import devarea.fr.discord.statics.ColorsUsed;
import devarea.fr.discord.workers.self.StatsWorker;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Permission;

public class Update extends SlashCommand {
    @Override
    public ApplicationCommandRequest definition() {
        return ApplicationCommandRequest.builder()
                .name("update")
                .description("Force la mise à jour des channels Statistiques.")
                .build();
    }

    @Override
    public void play(SlashCommandFiller filler) {
        final long ms = System.currentTimeMillis();
        StatsWorker.update();
        filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()

                .addEmbed(EmbedCreateSpec.builder()
                        .title("Update !")
                        .description("Les stats ont été update en " + (System.currentTimeMillis() - ms) + "ms.")
                        .color(ColorsUsed.just).build())
                .ephemeral(true)
                .build()).subscribe();
    }

    @Override
    public Permissions permissions() {
        return Permissions.of(Permission.ADMINISTRATOR);
    }
}
