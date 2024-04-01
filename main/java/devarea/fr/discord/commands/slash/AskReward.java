package devarea.fr.discord.commands.slash;

import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.commands.SlashCommand;
import devarea.fr.discord.entities.Mem;
import devarea.fr.discord.entities.events_filler.SlashCommandFiller;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;

public class AskReward extends SlashCommand {

    @Override
    public ApplicationCommandRequest definition() {
        return ApplicationCommandRequest.builder()
                .name("askreward")
                .description("Permet de faire une demande de reward à un membre du serveur !")
                .addOption(ApplicationCommandOptionData.builder()
                        .name("mention")
                        .description("Mentionnez la personne que vous avez aidée.")
                        .type(ApplicationCommandOption.Type.MENTIONABLE.getValue())
                        .required(true)
                        .build())
                .build();
    }

    @Override
    public void play(SlashCommandFiller filler) {

        Mem target = MemberCache.get(filler.event.getOption("mention").get().getValue().get().asSnowflake().asString());
        filler.event.reply("La commande n'as pas encore été implémentée.").subscribe();
    }

}
