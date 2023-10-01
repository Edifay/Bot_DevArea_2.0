package devarea.fr.discord.commands.slash;

import devarea.fr.discord.cache.ChannelCache;
import devarea.fr.discord.commands.SlashCommand;
import devarea.fr.discord.entities.Chan;
import devarea.fr.discord.entities.events_filler.SlashCommandFiller;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;

public class Structure extends SlashCommand {
    @Override
    public void play(SlashCommandFiller filler) {
        String id;
        if (filler.event.getOption("mention").isPresent()) {
            id = filler.event.getOption("mention").get().getValue().get().asSnowflake().asString();
        } else {
            Chan<GuildMessageChannel> channel = ChannelCache.get(filler.event.getInteraction().getChannelId().asString());
            id = channel.entity.getLastMessage().block().getAuthor().get().getId().asString();
        }
        filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
            .content("Bonjour <@" + id + ">, dans l'optique de t'aider au mieux et dans les meilleurs conditions, nous t'invitons à suivre ces consignes simples :\n" +
                     "\n" +
                     "- Ne demande pas si tu peux demander, juste demande.\n\n" +
                     "- Pose ta question dans le bon salon. Pour obtenir ces derniers rends toi ici : <id:customize>.\n\n" +
                     "- Si tu prends le rôle intermédiaire ou pro, on sera moins sympa si tu poses une question évidente <:ayy:814189080232722492>.\n\n" +
                     "- Pense à nous présenter proprement ton code formaté sur discord, tu peux insérer 3 \"back tics\" (alt-gr + 7)  au début et à la fin de ton code, cela nous permet de le tester facilement.\n\n" +
                     "- De ce fait, évite les capture d'écrans, c'est compliqué à copier/coller et à debugger.\n\n" +
                     "- Pense à nous montrer ton/tes messages d'erreurs, pense aussi à les lire toi même.\n\n" +
                     "- N'oublie pas de te référer à la documentation ou de poser tes questions à Google avant de la poser ici.\n\n" +
                     "- Une fonction `/devhelp` existe, utilise là après avoir posé ta question si tu n'as pas de réponse au bout d'un certains temps.\n\n" +
                     "- N'hésites pas à aller demander de l'aide en vocal.\n\n" +
                     "\n" +
                     "Nous avons tous débuté ;) !")
            .build()).subscribe();
    }


    @Override
    public ApplicationCommandRequest definition() {
        return ApplicationCommandRequest.builder()
            .name("structure")
            .description("Envois un message pour expliquer le fonctionnement du serveur.")
            .addOption(ApplicationCommandOptionData.builder()
                .name("mention")
                .description("Le membre à ping. Ping l'auteur du message précédent par défaut.")
                .required(false)
                .type(ApplicationCommandOption.Type.MENTIONABLE.getValue())
                .build())
            .build();
    }

}
