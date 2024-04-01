package devarea.fr.discord.commands.slash;

import devarea.fr.db.data.DBMission;
import devarea.fr.discord.commands.SlashCommand;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.events_filler.ButtonInteractionEventFiller;
import devarea.fr.discord.entities.events_filler.SelectMenuInteractionEventFiller;
import devarea.fr.discord.entities.events_filler.SlashCommandFiller;
import devarea.fr.discord.statics.ColorsUsed;
import devarea.fr.discord.workers.linked.MissionWorker;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.InteractionReplyEditSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;

import java.util.ArrayList;
import java.util.List;

import static devarea.fr.discord.statics.DefaultData.DOMAIN_NAME;

public class Mission extends SlashCommand {
    @Override
    public ApplicationCommandRequest definition() {
        return ApplicationCommandRequest.builder()
                .name("mission")
                .description("Permet de contrôler et gérer les missions possédées !")
                .build();
    }

    @Override
    public void play(SlashCommandFiller filler) {
        if (MissionWorker.getMissionsOf(filler.mem.getSId()).size() != 0) {
            filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                    .addEmbed(EmbedCreateSpec.builder()
                            .title("Mission")
                            .description("Choisissez l'action ci-dessous.")
                            .color(ColorsUsed.same)
                            .build())
                    .ephemeral(true)
                    .addComponent(ActionRow.of(Button.danger("delete", "Delete"),
                            Button.link(DOMAIN_NAME + "options?open=0", "devarea.fr")))
                    .build()).subscribe();

            filler.mem.listenDuring((ActionEvent<ButtonInteractionEventFiller>) fillerButton -> listMission(fillerButton),
                    false, SPOILED_TIME, filler.context());
        } else {
            filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                    .addEmbed(EmbedCreateSpec.builder()
                            .title("Mission")
                            .description("Vous n'avez pas de mission. Utilisez le lien ci-dessous pour en créer une.")
                            .color(ColorsUsed.same)
                            .build())
                    .ephemeral(true)
                    .addComponent(ActionRow.of(Button.link(DOMAIN_NAME + "missions", "devarea.fr")))
                    .build()).subscribe();
        }

    }

    public static void listMission(final ButtonInteractionEventFiller filler) {
        ArrayList<SelectMenu.Option> optionsList = new ArrayList<>();

        int index = 1;
        for (DBMission mission : MissionWorker.getMissionsOf(filler.mem.getSId())) {
            String title = mission.getTitle().length() > 35 ? mission.getTitle().substring(0, 30) + "..." : mission.getTitle();
            optionsList.add(SelectMenu.Option.of(index + " - " + title, "mission_del " + mission.get_id()));
        }

        filler.event.reply(
                InteractionApplicationCommandCallbackSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Mission")
                                .description("Choisissez la mission à supprimer.")
                                .color(ColorsUsed.same)
                                .build())
                        .addComponent(ActionRow.of(SelectMenu.of("mission_delete", optionsList)))
                        .ephemeral(true)
                        .build()
        ).subscribe();

        filler.mem.listenDuring((ActionEvent<SelectMenuInteractionEventFiller>) fillerMenu -> {
            String _id = fillerMenu.event.getValues().get(0).split(" ")[1];
            DBMission mission = MissionWorker.getMissionBy_Id(_id);
            MissionWorker.deleteMission(_id);

            filler.event.editReply(InteractionReplyEditSpec.builder()
                    .addEmbed(EmbedCreateSpec.builder()
                            .title("Mission")
                            .description("Votre mission " + (mission.getTitle().length() > 50 ? mission.getTitle().substring(0, 50) + "..." : mission.getTitle()))
                            .color(ColorsUsed.same)
                            .build())
                    .components(List.of())
                    .build()).subscribe();
        }, false, SPOILED_TIME);
    }
}
