package devarea.fr.discord.commands.slash;

import devarea.fr.discord.commands.SlashCommand;
import devarea.fr.discord.entities.events_filler.SlashCommandFiller;
import devarea.fr.discord.statics.ColorsUsed;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;

public class Start extends SlashCommand {
    @Override
    public ApplicationCommandRequest definition() {
        return ApplicationCommandRequest.builder()
                .name("start")
                .description("Donne un petit texte explicatif pour bien commencer le langage souhaité !")
                .build();
    }

    @Override
    public void play(SlashCommandFiller filler) {
        filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Start")
                                .description("Cliquez sur les boutons ci-dessous pour des informations sur les languages.")
                                .color(ColorsUsed.same)
                                .build())
                        .addComponent(ActionRow.of(
                                Button.secondary("start_java", "Java"),
                                Button.secondary("start_python", "Python"),
                                Button.secondary("start_csharp", "C#"),
                                Button.secondary("start_html_css", "Html/Css")
                        ))
                .build()).subscribe();
    }
}
