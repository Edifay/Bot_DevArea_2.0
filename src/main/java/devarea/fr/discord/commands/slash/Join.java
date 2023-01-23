package devarea.fr.discord.commands.slash;

import devarea.fr.discord.Core;
import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.commands.Permissions;
import devarea.fr.discord.commands.SlashCommand;
import devarea.fr.discord.entities.Mem;
import devarea.fr.discord.entities.events_filler.MemberJoinEventFiller;
import devarea.fr.discord.entities.events_filler.SlashCommandFiller;
import devarea.fr.discord.statics.ColorsUsed;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Permission;

public class Join extends SlashCommand {
    @Override
    public ApplicationCommandRequest definition() {
        return ApplicationCommandRequest.builder()
                .name("join")
                .description("Commande qui permet de faire join un membre. Il refait donc le questionnaire.")
                .addOption(ApplicationCommandOptionData.builder()
                        .name("mention")
                        .description("Mention de la personne à faire join.")
                        .required(true)
                        .type(ApplicationCommandOption.Type.MENTIONABLE.getValue())
                        .build())
                .build();
    }

    @Override
    public void play(SlashCommandFiller filler) {
        Mem target =
                MemberCache.get(filler.event.getOption("mention").get().getValue().get().asSnowflake().asString());
        Core.executeGlobal(new MemberJoinEventFiller(target));

        filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                .ephemeral(true)
                .addEmbed(EmbedCreateSpec.builder()
                        .title("Join")
                        .description("Vous avez fait join : " + target.entity.getUsername())
                        .color(ColorsUsed.same)
                        .build())
                .build()).subscribe();
    }

    @Override
    public Permissions permissions() {
        return Permissions.of(Permission.ADMINISTRATOR);
    }
}
