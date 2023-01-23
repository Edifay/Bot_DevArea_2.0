package devarea.fr.discord.commands.slash;

import devarea.fr.discord.commands.Permissions;
import devarea.fr.discord.commands.SlashCommand;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.events_filler.SelectMenuInteractionEventFiller;
import devarea.fr.discord.entities.events_filler.SlashCommandFiller;
import devarea.fr.discord.statics.ColorsUsed;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Permission;

import java.util.ArrayList;

public class Admin extends SlashCommand {

    private static ArrayList<SelectMenu.Option> options = new ArrayList<>();

    static {
        options.add(SelectMenu.Option.of("Option 1", "option_1"));
        options.add(SelectMenu.Option.of("Option 2", "option_2"));
    }


    @Override
    public ApplicationCommandRequest definition() {
        return ApplicationCommandRequest.builder()
                .name("admin")
                .description("Commande pour les modérateurs.")
                .build();
    }

    @Override
    public void play(SlashCommandFiller filler) {

        filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                .ephemeral(true)
                .addEmbed(EmbedCreateSpec.builder()
                        .title("Admin")
                        .description("Vous pouvez choisir l'action que vous voulez effectuer.")
                        .color(ColorsUsed.same)
                        .build())
                .addComponent(ActionRow.of(SelectMenu.of("admin", options)))
                .build()).subscribe();

        filler.mem.listenDuring((ActionEvent<SelectMenuInteractionEventFiller>) fillerMenu -> {
            if (!fillerMenu.event.getCustomId().equals("admin"))
                return;
            if (fillerMenu.event.getValues().get(0).equals("option_1")) {
                fillerMenu.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                        .ephemeral(true)
                        .content("Option 1")
                        .build()).subscribe();
            } else if (fillerMenu.event.getValues().get(0).equals("option_2")) {
                fillerMenu.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                        .ephemeral(true)
                        .content("Option 2")
                        .build()).subscribe();
            }
        }, false, SPOILED_TIME);
    }

    @Override
    public Permissions permissions() {
        return Permissions.of(Permission.MANAGE_MESSAGES);
    }
}
