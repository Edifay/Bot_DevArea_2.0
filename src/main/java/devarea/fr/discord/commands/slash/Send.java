package devarea.fr.discord.commands.slash;

import devarea.fr.discord.cache.ChannelCache;
import devarea.fr.discord.commands.Permissions;
import devarea.fr.discord.commands.SlashCommand;
import devarea.fr.discord.entities.events_filler.SlashCommandFiller;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Permission;

public class Send extends SlashCommand {

    @Override
    public ApplicationCommandRequest definition() {
        return ApplicationCommandRequest.builder()
                .name("send")
                .options(ApplicationCommandOptionData.builder()
                        .name("message")
                        .description("Donnez le message à faire envoyer par le bot.")
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .required(true)
                        .build())
                .description("Permet d'envoyer un message à l'aide du bot.")
                .build();
    }

    @Override
    public void play(SlashCommandFiller filler) {


        ((GuildMessageChannel) ChannelCache.get(filler.event.getInteraction().getChannelId().asString()).entity).createMessage(MessageCreateSpec.builder()
                .content(filler.event.getOption("message").get().getValue().get().asString())
                .build()).subscribe();

        filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                .content("Votre message a été envoyé.")
                .ephemeral(true)
                .build()).subscribe();

    }

    @Override
    public Permissions permissions() {
        return Permissions.of(Permission.MANAGE_MESSAGES);
    }

}
