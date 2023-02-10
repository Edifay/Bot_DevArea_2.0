package devarea.fr.discord.commands.slash;

import devarea.fr.db.DBManager;
import devarea.fr.db.data.DBMember;
import devarea.fr.db.data.DBMission;
import devarea.fr.discord.cache.ChannelCache;
import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.commands.Permissions;
import devarea.fr.discord.commands.SlashCommand;
import devarea.fr.discord.entities.*;
import devarea.fr.discord.entities.events_filler.*;
import devarea.fr.discord.statics.ColorsUsed;
import devarea.fr.discord.workers.linked.FreelanceWorker;
import devarea.fr.discord.workers.linked.MissionWorker;
import discord4j.common.util.Snowflake;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.component.TextInput;
import discord4j.core.object.entity.channel.GuildMessageChannel;
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
        options.add(SelectMenu.Option.of("Freelance Manager", "freelance_manager"));
        options.add(SelectMenu.Option.of("Option 3", "option_3"));
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

            switch (fillerMenu.event.getValues().get(0)) {
                case "mission_manager":
                    selectMissionToManage(fillerMenu);
                    break;
                case "freelance_manager":
                    selectFreelanceToManage(fillerMenu);
                    break;
                case "option_3":
                    fillerMenu.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                            .ephemeral(true)
                            .content("Option 3")
                            .build()).subscribe();
                    break;
            }
        }, false, SPOILED_TIME);
    }

    @Override
    public Permissions permissions() {
        return Permissions.of(Permission.MANAGE_MESSAGES);
    }

    public void selectFreelanceToManage(final SelectMenuInteractionEventFiller filler) {
        filler.event.edit(InteractionApplicationCommandCallbackSpec.builder()
                .ephemeral(true)
                .addEmbed(EmbedCreateSpec.builder()
                        .title("Freelance Manager")
                        .description("Veuillez donner l'id du membre sur le quel vous voulez agir.")
                        .color(ColorsUsed.same)
                        .build())
                .components(ActionRow.of(Button.secondary("admin_button_freelance_id", "Member ID")))
                .build()).subscribe();


        Context contextFromButtonReply = Context.builder()
                .messageId(filler.event.getReply().block().getId().asString())
                .channelId(filler.context().channelId())
                .build();

        ActionEvent<?> buttonOpenModal = (ActionEvent<ButtonInteractionEventFiller>) fillerButtonModal -> {
            if (fillerButtonModal.event.getCustomId().equals("admin_button_freelance_id"))
                fillerButtonModal.event.presentModal("Donnez moi l'id du membre : ", "admin_modal_freelance_id", List.of(ActionRow.of(TextInput.small("modal_id", "Member id : ", 15, 21)))).subscribe();
        };
        filler.mem.listenDuring(buttonOpenModal, true, SPOILED_TIME, contextFromButtonReply);


        filler.mem.listenDuring(new ActionEvent<ModalSubmitInteractionEventFiller>() {
            @Override
            public void run(ModalSubmitInteractionEventFiller fillerModal) {

                if (fillerModal.event.getCustomId().equals("admin_modal_freelance_id")) {

                    final String id = fillerModal.event.getComponents().get(0).getData().components().get().get(0).value().get();


                    Mem mem = MemberCache.get(id);
                    if (mem != null) {
                        filler.mem.removeListener(new EventOwner<>(buttonOpenModal));
                        filler.mem.removeListener(new EventOwner<>(this));


                        actionOnFreelance(fillerModal, mem);

                    } else
                        fillerModal.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                                .ephemeral(true)
                                .content("Le membre n'as pas été trouvé !")
                                .build()).subscribe();
                }
            }
        }, true, SPOILED_TIME, contextFromButtonReply);


    }

    public void actionOnFreelance(final ModalSubmitInteractionEventFiller filler, final Mem target) {

        if (target.db().hasFreelance()) {
            filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                    .ephemeral(true)
                    .addEmbed(EmbedCreateSpec.builder()
                            .title("Freelance Manager")
                            .description("Voici les actions possible pour la page freelance de <@" + target.getSId() + ">.")
                            .color(ColorsUsed.same)
                            .author(target.entity.getDisplayName(), "", target.entity.getAvatarUrl())
                            .build())
                    .components(ActionRow.of(Button.danger("admin_delete_freelance", "Delete")))
                    .build()).subscribe();

            Context context = Context.builder()
                    .messageId(filler.event.getReply().block().getId().asString())
                    .build();

            filler.mem.listenDuring((ActionEvent<ButtonInteractionEventFiller>) fillerButtonDelete -> {
                FreelanceWorker.deleteFreelanceOf(target.getSId());
                fillerButtonDelete.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                        .ephemeral(true)
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Freelance Manager")
                                .description("Vous venez de supprimer la page freelance de <@" + target.getSId() + ">.")
                                .color(ColorsUsed.same)
                                .build())
                        .build()).subscribe();
            }, false, SPOILED_TIME, context);

        } else {
            filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                    .ephemeral(true)
                    .addEmbed(EmbedCreateSpec.builder()
                            .title("Freelance Manager")
                            .description("Ce membre n'as pas de freelance. Vous n'avez donc aucune action sur ce membre.")
                            .color(ColorsUsed.same)
                            .build())
                    .build()).subscribe();
        }

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
