package devarea.fr.discord.commands.slash;

import devarea.fr.db.DBManager;
import devarea.fr.discord.commands.SlashCommand;
import devarea.fr.discord.entities.events_filler.SlashCommandFiller;
import devarea.fr.discord.statics.ColorsUsed;
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
            .ephemeral(true)
            .addEmbed(EmbedCreateSpec.builder()
                .color(ColorsUsed.same)
                .title("Les Challenges !")
                .description("Les challenges sont une partie de Dev'Area qui ont pour but de pouvoir proposer des petits défis algorithmqiue de façon ludique de tout niveaux. Et de pouvoir partager avec les autres de leurs résolutions et de leurs difficultées.")
                .build())
            .addEmbed(EmbedCreateSpec.builder()
                .color(ColorsUsed.same)
                .title("Votre clef challenge :")
                .description("`" + DBManager.getChallengeForId(filler.mem.getSId()).getKey() + "`")
                .build())
            .build()).subscribe();
    }
}
