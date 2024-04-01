package devarea.fr.discord.commands.slash;

import devarea.fr.discord.commands.SlashCommand;
import devarea.fr.discord.entities.events_filler.SlashCommandFiller;
import devarea.fr.discord.statics.ColorsUsed;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;

public class Ask extends SlashCommand {
    @Override
    public ApplicationCommandRequest definition() {
        return ApplicationCommandRequest.builder()
                .name("ask")
                .description("Donne des astuces pour bien poser une question.")
                .build();
    }

    @Override
    public void play(SlashCommandFiller filler) {

        // TODO extract message

        filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                .addEmbed(EmbedCreateSpec.builder()
                        .title("Ne demande pas pour demander, demande !")
                        .description("https://dontasktoask.com/")
                        .image("https://devarea.fr/assets/images/image_ask.png")
                        .color(ColorsUsed.same).build()
                ).build()).subscribe();

    }
}
