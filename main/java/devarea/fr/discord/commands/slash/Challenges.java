package devarea.fr.discord.commands.slash;

import devarea.fr.db.DBManager;
import devarea.fr.discord.commands.SlashCommand;
import devarea.fr.discord.entities.events_filler.SlashCommandFiller;
import devarea.fr.discord.statics.ColorsUsed;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;

public class Challenges extends SlashCommand {
    @Override
    public ApplicationCommandRequest definition() {
        return ApplicationCommandRequest.builder()
            .name("challenges")
            .description("Vous donne les explications concernant les challenges.")
            .build();
    }

    @Override
    public void play(SlashCommandFiller filler) {
        filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
            .addEmbed(EmbedCreateSpec.builder()
                .color(ColorsUsed.same)
                .title("Les Challenges !")
                .description("""
                    Les challenges, tout d'abord qu'est ce que c'est ?
                                            
                    Un challenge est un petit défi algorithmique et/ou de programmation intéractif proposé par Dev'Area.
                                            
                    Le fonctionnement est assez simple, vous avez simplement a télécharger le client dans votre language préféré pour commencer les défis !
                                            
                    Le client vous permettra de dialoguer avec le serveur pour recevoir les jeux de tests et validé vos réponses !
                                            
                    Vous pouvez voir votre progression dans la page Challenge de [devarea.fr](https://devarea.fr/challenges).
                    """)
                .build())
            .addComponent(
                ActionRow.of(Button.secondary("get_challenge_key", "Récupérer ma clef challenge"),
                    Button.secondary("download_client_redirect", "Récupérer un client")))
            .build()).subscribe();
    }
}
