package devarea.fr.discord.commands.slash;

import devarea.fr.discord.Core;
import devarea.fr.discord.commands.Permissions;
import devarea.fr.discord.commands.SlashCommand;
import devarea.fr.discord.entities.events_filler.SlashCommandFiller;
import devarea.fr.discord.statics.ColorsUsed;
import discord4j.core.object.entity.channel.Category;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Permission;

public class ClearFollowedMission extends SlashCommand {

    @Override
    public ApplicationCommandRequest definition() {
        return ApplicationCommandRequest.builder()
                .name("clearfollowedmission")
                .description("Supprime tous les suivis de missions fermé !")
                .build();
    }

    @Override
    public void play(SlashCommandFiller filler) {
        filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                .ephemeral(true)
                .addEmbed(EmbedCreateSpec.builder()
                        .title("Followed Mission Cleared.")
                        .color(ColorsUsed.same)
                        .description("Les suivis de missions fermé vont petit à petit être supprimé !")
                        .build())
                .build()).subscribe();

        ((Category) Core.devarea.getChannelById(Core.data.mission_follow_category).block()).getChannels().toIterable().forEach(categorizableChannel -> {
            if (categorizableChannel.getName().startsWith("closed")) {
                categorizableChannel.delete().block();
            }
        });
    }

    @Override
    public Permissions permissions() {
        return Permissions.of(Permission.MANAGE_CHANNELS);
    }
}
