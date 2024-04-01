package devarea.fr.discord;

import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entities.Mem;
import devarea.fr.discord.entities.events_filler.*;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import discord4j.core.event.domain.guild.MemberUpdateEvent;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.*;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;

public class Dispatcher {

    public static void onReadyEvent(final ReadyEvent event) {
        Core.executeGlobal(new ReadyEventFiller(event));
    }

    public static void onButtonInteractionEvent(final ButtonInteractionEvent event) {
        if (event.getInteraction().getMember().isPresent())
            MemberCache.use(event.getInteraction().getMember().get());
        Core.executeGlobal(new ButtonInteractionEventFiller(event));
        if (event.getInteraction().getMember().isPresent())
            MemberCache.get(event.getInteraction().getUser().getId().asString()).execute(new ButtonInteractionEventFiller(event));
    }

    public static void onMemberJoinEvent(final MemberJoinEvent event) {
        MemberCache.use(event.getMember());
        Core.executeGlobal(new MemberJoinEventFiller(Mem.of(event.getMember())));
    }

    public static void onMemberLeaveEvent(final MemberLeaveEvent event) {
        Core.executeGlobal(new MemberLeaveEventFiller(event.getUser().getId()));
        MemberCache.slash(event.getUser().getId().asString());
    }

    public static void onMessageCreateEvent(final MessageCreateEvent event) {
        if (event.getMember().isPresent())
            MemberCache.use(event.getMember().get());
        Core.executeGlobal(new MessageCreateEventFiller(event));
        if (event.getMember().isPresent())
            MemberCache.get(event.getMember().get().getId().asString()).execute(new MessageCreateEventFiller(event));
    }

    public static void onMessageDeleteEvent(final MessageDeleteEvent event) {
        Core.executeGlobal(new MessageDeleteEventFiller(event));
    }

    public static void onMessageUpdateEvent(final MessageUpdateEvent event) {
        if (event.getOld().isPresent() && (event.getOld().get().getAuthor().isEmpty() || event.getOld().get().getAuthor().get().isBot()))
            return;
        Core.executeGlobal(new MessageUpdateEventFiller(event));
    }

    public static void onReactionAddEvent(final ReactionAddEvent event) {
        if (event.getMember().isPresent())
            MemberCache.use(event.getMember().get());
        Core.executeGlobal(new ReactionAddEventFiller(event));
        MemberCache.get(event.getMember().get().getId().asString()).execute(new ReactionAddEventFiller(event));
    }

    public static void onReactionRemoveEvent(final ReactionRemoveEvent event) {
        Core.executeGlobal(new ReactionRemoveEventFiller(event));
        MemberCache.get(event.getUserId().asString()).execute(new ReactionRemoveEventFiller(event));
    }

    public static void onSelectMenuInteractionEvent(final SelectMenuInteractionEvent event) {
        if (event.getInteraction().getMember().isPresent())
            MemberCache.use(event.getInteraction().getMember().get());
        Core.executeGlobal(new SelectMenuInteractionEventFiller(event));
        if (event.getInteraction().getMember().isPresent())
            MemberCache.get(event.getInteraction().getMember().get().getId().asString()).execute(new SelectMenuInteractionEventFiller(event));
    }

    public static void onVoiceStateUpdateEvent(final VoiceStateUpdateEvent event) {
        Core.executeGlobal(new VoiceStateUpdateEventFiller(event));
        if (event.isLeaveEvent())
            MemberCache.get(event.getOld().get().getUserId().asString()).execute(new VoiceStateUpdateEventFiller(event));
        if (event.isJoinEvent() || event.isMoveEvent())
            MemberCache.get(event.getCurrent().getUserId().asString()).execute(new VoiceStateUpdateEventFiller(event));
    }

    public static void onChatInputInteractionEvent(final ChatInputInteractionEvent event) {
        if (event.getInteraction().getMember().isPresent())
            MemberCache.use(event.getInteraction().getMember().get());
        else {
            event.reply(InteractionApplicationCommandCallbackSpec.builder()
                .ephemeral(true)
                .content("Les commandes sont désactivées dans les messages privés.")
                .build()).subscribe();
            return;
        }
        Core.executeGlobal(new SlashCommandFiller(event));
        MemberCache.get(event.getInteraction().getUser().getId().asString()).execute(new SlashCommandFiller(event));
    }

    public static void onModalSubmitInteractionEvent(final ModalSubmitInteractionEvent event) {
        Core.executeGlobal(new ModalSubmitInteractionEventFiller(event));
        MemberCache.get(event.getInteraction().getUser().getId().asString()).execute(new ModalSubmitInteractionEventFiller(event));
    }

    public static void onMemberUpdateEvent(final MemberUpdateEvent event) {
        Core.executeGlobal(new MemberUpdateEventFiller(event));
        MemberCache.get(event.getMemberId().asString()).execute(new MemberUpdateEventFiller(event));
    }

}
