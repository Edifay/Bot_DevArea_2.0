package devarea.fr.discord.commands.slash;

import devarea.fr.discord.cache.ChannelCache;
import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.commands.Permissions;
import devarea.fr.discord.commands.SlashCommand;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.Chan;
import devarea.fr.discord.entities.EventOwner;
import devarea.fr.discord.entities.Mem;
import devarea.fr.discord.entities.events_filler.*;
import devarea.fr.discord.statics.ColorsUsed;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.*;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.component.TextInput;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.discordjson.possible.Possible;
import discord4j.rest.util.Color;
import discord4j.rest.util.Permission;

import java.util.List;
import java.util.Optional;

public class UseBot extends SlashCommand {

    protected boolean newMessage = false;
    protected SlashCommandFiller filler;
    protected Message message;

    @Override
    public ApplicationCommandRequest definition() {
        return ApplicationCommandRequest.builder()
                .name("usebot")
                .description("Permet d'utiliser le bot pour envoyer et editer des messages.")
                .build();
    }

    /**
     *
     * La commande UseBot permet d'utiliser le bot pour envoyer ou éditer des messages.
     *
     * Ici la première étape est de demander au membre si il veut éditer ou envoyer un message.
     *
     * @param filler
     */
    @Override
    public void play(SlashCommandFiller filler) {
        this.filler = filler;

        filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                .addEmbed(EmbedCreateSpec.builder()
                        .title("UseBot")
                        .description("Choisissez ce que vous voulez effectuer avec le bot.")
                        .color(ColorsUsed.same)
                        .build())
                .addComponent(ActionRow.of(SelectMenu.of("usebot", List.of(SelectMenu.Option.of("Send", "send"),
                        SelectMenu.Option.of("Edit", "edit")))))
                .ephemeral(true)
                .build()).subscribe();

        filler.mem.listenDuring(new ActionEvent<SelectMenuInteractionEventFiller>() {
            @Override
            public void run(SelectMenuInteractionEventFiller fillerMenu) {
                if (fillerMenu.event.getCustomId().equals("usebot")) {
                    filler.mem.removeListener(new EventOwner<>(this));
                    getMessageData(fillerMenu, fillerMenu.event.getValues().get(0).equals("send"));
                }
            }
        }, true, SPOILED_TIME, filler.context());
    }

    @Override
    public Permissions permissions() {
        return Permissions.of(Permission.MANAGE_MESSAGES);
    }

    /**
     *
     * Cette étape ne fait une action que dans le cas où c'est un message à éditer.
     * Elle se charge de récupérer le message à éditer.
     *
     * @param filler
     * @param newMessage si l'action est un envois ou une édition.
     */
    private void getMessageData(final SelectMenuInteractionEventFiller filler, final boolean newMessage) {
        this.newMessage = newMessage;

        Mem mem = MemberCache.watch(filler.event.getInteraction().getMember().get().getId().asString());

        if (!newMessage) {
            filler.event.edit(InteractionApplicationCommandCallbackSpec.builder()
                    .addEmbed(EmbedCreateSpec.builder()
                            .title("UseBot")
                            .description("Donnez moi l'id du message a éditer. Il doit être dans ce channel !")
                            .color(ColorsUsed.same)
                            .build())
                    .components(List.of(ActionRow.of(Button.primary("usebot_open_id", "Modal"))))
                    .build()).subscribe();

            ActionEvent<ButtonInteractionEventFiller> buttonAction = fillerButton -> {
                if (fillerButton.event.getCustomId().equals("usebot_open_id"))
                    fillerButton.event.presentModal("Donnez moi l'id du message !", "usebot_modal_id", List.of(ActionRow.of(TextInput.small("usebot_id", "Message id : ", 19, 19)))).subscribe();
            };
            mem.listenDuring(buttonAction, true, SPOILED_TIME, filler.context());

            mem.listenDuring(new ActionEvent<ModalSubmitInteractionEventFiller>() {
                @Override
                public void run(ModalSubmitInteractionEventFiller fillerModal) {

                    if (fillerModal.event.getCustomId().equals("usebot_modal_id")) {

                        final String id = fillerModal.event.getComponents().get(0).getData().components().get().get(0).value().get();


                        Chan<GuildMessageChannel> chan = ChannelCache.get(fillerModal.event.getInteraction().getChannelId().asString());

                        try {
                            message = chan.entity.getMessageById(Snowflake.of(id)).block();

                            mem.removeListener(new EventOwner<>(buttonAction));
                            mem.removeListener(new EventOwner<>(this));
                            actionType(fillerModal, mem);
                        } catch (Exception e) {
                            fillerModal.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                                    .content("Le message n'as pas été trouvé !")
                                    .ephemeral(true)
                                    .build()).subscribe();
                        }
                    }
                }
            }, true, SPOILED_TIME, filler.context());

        } else
            actionType(filler, mem);


    }


    /**
     *
     * Cette étape consiste à savoir le type de message voulu. Si c'est un embed ou un message classique.
     *
     * @param filler
     * @param mem
     */
    private void actionType(final Filler<? extends ComponentInteractionEvent> filler, final Mem mem) {

        filler.event.edit(InteractionApplicationCommandCallbackSpec.builder()
                .addEmbed(EmbedCreateSpec.builder()
                        .title("UseBot")
                        .description("Quel est le type de votre message ?")
                        .color(ColorsUsed.same)
                        .build())
                .addComponent(ActionRow.of(SelectMenu.of("usebot_message_type",
                        List.of(SelectMenu.Option.of("Embed", "embed"),
                                SelectMenu.Option.of("Message", "message")))))
                .build()).subscribe();

        mem.listenDuring(new ActionEvent<SelectMenuInteractionEventFiller>() {
            @Override
            public void run(SelectMenuInteractionEventFiller fillerMenu) {

                if (fillerMenu.event.getCustomId().equals("usebot_message_type")) {
                    mem.removeListener(new EventOwner<>(this));

                    if (fillerMenu.event.getValues().get(0).equals("embed"))
                        actionEmbed(fillerMenu);
                    else
                        actionMessage(fillerMenu);
                }
            }
        }, false, SPOILED_TIME, filler.context());
    }

    /**
     *
     * Cette action demande le contenu du message et fait l'action voulu et termine la commande.
     *
     * @param filler
     */
    private void actionMessage(final SelectMenuInteractionEventFiller filler) {
        Mem mem = MemberCache.watch(filler.event.getInteraction().getMember().get().getId().asString());
        filler.event.presentModal("Complétez votre message !", "usebot_modal_message_content", List.of(ActionRow.of(TextInput.paragraph("usebot_content_message", "Message : ", 1, 2000)))).subscribe();

        mem.listenDuring((ActionEvent<ModalSubmitInteractionEventFiller>) fillerModal -> {

            final String content = fillerModal.event.getComponents().get(0).getData().components().get().get(0).value().get();

            if (newMessage) {
                Chan<GuildMessageChannel> chan = ChannelCache.watch(this.filler.event.getInteraction().getChannelId().asString());
                chan.entity.createMessage(MessageCreateSpec.builder()
                        .content(content)
                        .build()).subscribe();
            } else {
                message.edit(MessageEditSpec.builder()
                        .content(Possible.of(Optional.of(content)))
                        .build()).subscribe();
            }


            fillerModal.event.edit(InteractionApplicationCommandCallbackSpec.builder()
                    .addEmbed(EmbedCreateSpec.builder()
                            .title("UseBot")
                            .description("Votre action a été prise en compte !")
                            .color(ColorsUsed.same)
                            .build())
                    .components(List.of())
                    .build()).subscribe();

        }, false, SPOILED_TIME, filler.context());
    }


    /**
     * Cette action demande le contenu de l'embed et fait l'action voulu et termine la commande.
     *
     * @param filler
     */
    private void actionEmbed(final SelectMenuInteractionEventFiller filler) {
        Mem mem = MemberCache.watch(filler.event.getInteraction().getMember().get().getId().asString());
        filler.event.presentModal(
                "Complétez votre embed !",
                "usebot_modal_embed_content",
                List.of(
                        ActionRow.of(TextInput.small("usebot_title_embed", "Title : ", 1, 250))
                        , ActionRow.of(TextInput.paragraph("usebot_description_embed", "Description : ", 1, 2000))
                        , ActionRow.of(TextInput.small("usebot_color_embed", "Color (same | just | wrong) : ", 4, 5))
                )
        ).subscribe();

        mem.listenDuring((ActionEvent<ModalSubmitInteractionEventFiller>) fillerModal -> {

            final String title = fillerModal.event.getComponents().get(0).getData().components().get().get(0).value().get();
            final String content = fillerModal.event.getComponents().get(1).getData().components().get().get(0).value().get();
            final String color = fillerModal.event.getComponents().get(2).getData().components().get().get(0).value().get();

            Color colorObj = ColorsUsed.same;
            if (color.equals("wrong"))
                colorObj = ColorsUsed.wrong;
            else if (color.equals("just"))
                colorObj = ColorsUsed.just;

            EmbedCreateSpec embed = EmbedCreateSpec.builder()
                    .title(title)
                    .description(content)
                    .color(colorObj)
                    .build();
            if (newMessage) {
                Chan<GuildMessageChannel> chan = ChannelCache.watch(this.filler.event.getInteraction().getChannelId().asString());
                chan.entity.createMessage(MessageCreateSpec.builder()
                        .addEmbed(embed)
                        .build()).subscribe();
            } else {
                message.edit(MessageEditSpec.builder()
                        .addEmbed(embed)
                        .build()).subscribe();
            }


            fillerModal.event.edit(InteractionApplicationCommandCallbackSpec.builder()
                    .addEmbed(EmbedCreateSpec.builder()
                            .title("UseBot")
                            .description("Votre action a été prise en compte !")
                            .color(ColorsUsed.same)
                            .build())
                    .components(List.of())
                    .build()).subscribe();

        }, false, SPOILED_TIME, filler.context());
    }

}
