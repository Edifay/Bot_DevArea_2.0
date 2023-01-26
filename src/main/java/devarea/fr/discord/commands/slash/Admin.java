package devarea.fr.discord.commands.slash;

import devarea.fr.db.DBManager;
import devarea.fr.db.data.DBMember;
import devarea.fr.db.data.DBMission;
import devarea.fr.discord.commands.Permissions;
import devarea.fr.discord.commands.SlashCommand;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.events_filler.ButtonInteractionEventFiller;
import devarea.fr.discord.entities.events_filler.Filler;
import devarea.fr.discord.entities.events_filler.SelectMenuInteractionEventFiller;
import devarea.fr.discord.entities.events_filler.SlashCommandFiller;
import devarea.fr.discord.statics.ColorsUsed;
import devarea.fr.discord.workers.linked.MissionWorker;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.InteractionFollowupCreateSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Permission;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Admin extends SlashCommand {

    private static ArrayList<SelectMenu.Option> options = new ArrayList<>();

    static {
        options.add(SelectMenu.Option.of("Mission Manager", "mission_manager"));
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
            if (fillerMenu.event.getValues().get(0).equals("mission_manager")) {
                selectMissionToManage(fillerMenu);
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


    public void selectMissionToManage(final SelectMenuInteractionEventFiller filler) {

        filler.event.edit(InteractionApplicationCommandCallbackSpec.builder()
                .addEmbed(EmbedCreateSpec.builder()
                        .title("Mission Manager")
                        .description("La liste des missions vas s'afficher.")
                        .color(ColorsUsed.same)
                        .build())
                .components(List.of())
                .build()).subscribe();

        DBManager.getMissions().forEach(mission -> filler.event.createFollowup(InteractionFollowupCreateSpec.builder()
                .ephemeral(true)
                .addEmbed(EmbedCreateSpec.builder()
                        .title(mission.getTitle())
                        .color(ColorsUsed.same)
                        .author(EmbedCreateFields.Author.of(mission.getMember().entity.getDisplayName(), null, mission.getMember().entity.getAvatarUrl()))
                        .build())
                .addComponent(ActionRow.of(Button.danger("admin_delete_mission " + mission.get_id(), "Delete")))
                .build()).subscribe());

        if (!filler.mem.isListening(missionDeleteEvent))
            filler.mem.listenDuring(missionDeleteEvent, true, SPOILED_TIME);

    }

    private static final ActionEvent<ButtonInteractionEventFiller> missionDeleteEvent = filler -> {
        if (filler.event.getCustomId().startsWith("admin_delete_mission")) {
            String missionId = filler.event.getCustomId().split(" ")[1];
            MissionWorker.deleteMission(missionId);
            filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                    .ephemeral(true)
                    .addEmbed(EmbedCreateSpec.builder()
                            .title("Mission Manager")
                            .description("La mission a bien été supprimée !")
                            .color(ColorsUsed.same)
                            .build())
                    .build()).subscribe();
        }
    };
}
