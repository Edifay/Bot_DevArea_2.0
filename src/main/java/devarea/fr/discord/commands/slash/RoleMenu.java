package devarea.fr.discord.commands.slash;

import devarea.fr.discord.cache.ChannelCache;
import devarea.fr.discord.cache.RoleCache;
import devarea.fr.discord.entities.Context;
import devarea.fr.discord.commands.Permissions;
import devarea.fr.discord.commands.SlashCommand;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.Chan;
import devarea.fr.discord.entities.EventOwner;
import devarea.fr.discord.entities.events_filler.*;
import devarea.fr.discord.statics.ColorsUsed;
import discord4j.common.util.Snowflake;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.component.TextInput;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.spec.*;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.discordjson.json.SelectOptionData;
import discord4j.discordjson.possible.Possible;
import discord4j.rest.util.Permission;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoleMenu extends SlashCommand {

    public static final String SELECTOR = "switchRole:";

    protected Message atModif;
    protected List<Snowflake> roleIDS;

    @Override
    public ApplicationCommandRequest definition() {
        return ApplicationCommandRequest.builder()
                .name("rolemenu")
                .description("Gérer les RoleMenu.")
                .build();
    }

    @Override
    public void play(SlashCommandFiller filler) {

        filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                .addEmbed(EmbedCreateSpec.builder()
                        .title("RoleMenu")
                        .description("Choisissez l'action voulue ci-dessous.")
                        .color(ColorsUsed.same)
                        .build())
                .addComponent(ActionRow.of(Button.primary("create", "Create")))
                .ephemeral(true)
                .build()).subscribe();

        filler.mem.listenDuring((ActionEvent<ButtonInteractionEventFiller>) fillerButton -> {

            fillerButton.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                    .addEmbed(EmbedCreateSpec.builder()
                            .title("RoleMenu")
                            .description("Donnez l'id du message sur le quel ajouter un role menu. (Il doit être dans ce channel)")
                            .color(ColorsUsed.same)
                            .build())
                    .ephemeral(true)
                    .addComponent(ActionRow.of(Button.primary("role_menu_id", "ID")))
                    .build()).subscribe();

            Context contextFromButtonReply = Context.builder()
                    .messageId(fillerButton.event.getReply().block().getId().asString())
                    .channelId(fillerButton.context().channelId())
                    .build();

            ActionEvent<?> buttonOpenModal = (ActionEvent<ButtonInteractionEventFiller>) fillerButtonModal -> {
                if (fillerButtonModal.event.getCustomId().equals("role_menu_id"))
                    fillerButtonModal.event.presentModal("Donnez moi l'id du message !", "rolemenu_modal_id", List.of(ActionRow.of(TextInput.small("modal_id", "Message id : ", 19, 19)))).subscribe();
            };
            fillerButton.mem.listenDuring(buttonOpenModal, true, SPOILED_TIME, contextFromButtonReply);


            filler.mem.listenDuring(new ActionEvent<ModalSubmitInteractionEventFiller>() {
                @Override
                public void run(ModalSubmitInteractionEventFiller fillerModal) {

                    if (fillerModal.event.getCustomId().equals("rolemenu_modal_id")) {

                        final String id = fillerModal.event.getComponents().get(0).getData().components().get().get(0).value().get();


                        Chan<GuildMessageChannel> chan = ChannelCache.get(fillerModal.event.getInteraction().getChannelId().asString());

                        try {
                            atModif = chan.entity.getMessageById(Snowflake.of(id)).block();

                            filler.mem.removeListener(new EventOwner<>(buttonOpenModal));
                            filler.mem.removeListener(new EventOwner<>(this));

                            getRolesIdsForModal(fillerModal);
                        } catch (Exception e) {
                            fillerModal.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                                    .content("Le message n'as pas été trouvé !")
                                    .ephemeral(true)
                                    .build()).subscribe();
                        }
                    }
                }
            }, true, SPOILED_TIME, contextFromButtonReply);

        }, false, SPOILED_TIME, filler.context());
    }

    public void getRolesIdsForModal(final ModalSubmitInteractionEventFiller filler) {
        filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                .addEmbed(EmbedCreateSpec.builder()
                        .title("RoleMenu")
                        .description("Mentionnez tout les rôles à ajouter à ce role menu.")
                        .color(ColorsUsed.same)
                        .build())
                .ephemeral(true)
                .components(List.of())
                .build()).subscribe();

        filler.mem.listenDuring((ActionEvent<MessageCreateEventFiller>) fillerMessage -> {
            roleIDS = fillerMessage.event.getMessage().getRoleMentionIds();

            filler.event.createFollowup(InteractionFollowupCreateSpec.builder()
                    .ephemeral(true)
                    .addEmbed(EmbedCreateSpec.builder()
                            .title("Création du RoleMenu réussie !")
                            .color(ColorsUsed.just)
                            .description("Vous avez bien créé un lien entre le(s) rôle(s) et le menu du message !")
                            .build())
                    .build()).subscribe();

            SelectMenu menu;

            List<SelectMenu.Option> options = new ArrayList<>();
            System.out.println("AtModif components number : " + atModif.getComponents().size());
            if (atModif.getComponents().size() != 0)
                for (SelectOptionData option :
                        atModif.getComponents().get(0).getChildren().get(0).getData().options().get()) {
                    options.add(SelectMenu.Option.of(option.label(), option.value()));
                }

            for (Snowflake role : roleIDS)
                options.add(SelectMenu.Option.of(RoleCache.get(role.asString()).getName(),
                        SELECTOR + role.asString()));

            menu = SelectMenu.of("roleMenu", options).withMinValues(0).withMaxValues(options.size());

            atModif.edit(MessageEditSpec.builder()
                    .components(Possible.of(Optional.of(List.of(ActionRow.of(menu)))))
                    .build()).subscribe();
        }, false, SPOILED_TIME, Context.builder().channelId(filler.context().channelId()).build());

    }

    @Override
    public Permissions permissions() {
        return Permissions.of(Permission.ADMINISTRATOR);
    }
}
