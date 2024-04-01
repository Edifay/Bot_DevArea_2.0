package devarea.fr.discord.commands.slash;

import devarea.fr.discord.Core;
import devarea.fr.discord.commands.SlashCommand;
import devarea.fr.discord.entities.events_filler.SlashCommandFiller;
import devarea.fr.discord.statics.TextMessage;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.InteractionFollowupCreateSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Permission;

public class Help extends SlashCommand {
    @Override
    public ApplicationCommandRequest definition() {
        return ApplicationCommandRequest.builder()
                .name("help")
                .description("Donne une description de toutes les commandes du serveur.")
                .build();
    }

    @Override
    public void play(SlashCommandFiller filler) {
        filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                .addEmbed(TextMessage.helpEmbed)
                .build()).subscribe();
        if (filler.mem.entity.getBasePermissions().block().contains(Permission.ADMINISTRATOR) || filler.mem.entity.getRoleIds().contains(Core.data.admin_role) || filler.mem.entity.getRoleIds().contains(Core.data.modo_role))
            filler.event.createFollowup(InteractionFollowupCreateSpec.builder()
                    .ephemeral(true)
                    .addEmbed(TextMessage.helpEmbedAdmin)
                    .build()).subscribe();

    }
}
