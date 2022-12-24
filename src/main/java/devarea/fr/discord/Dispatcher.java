package devarea.fr.discord;

import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entity.Mem;
import devarea.fr.discord.entity.events_filler.MemberJoinEventFiller;
import devarea.fr.discord.entity.events_filler.MemberLeaveEventFiller;
import devarea.fr.discord.entity.events_filler.ReadyEventFiller;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.event.domain.message.*;

public class Dispatcher {

    public static void onReadyEvent() {
        DevArea.executeGlobal(new ReadyEventFiller());
    }

    public static void onButtonInteractionEvent(final ButtonInteractionEvent event) {
        if (event.getInteraction().getMember().isPresent())
            MemberCache.use(event.getInteraction().getMember().get());
        DevArea.executeGlobal(event);
        MemberCache.get(event.getInteraction().getMember().get().getId().asString()).execute(event);
    }

    public static void onMemberJoinEvent(final MemberJoinEvent event) {
        MemberCache.use(event.getMember());
        DevArea.executeGlobal(new MemberJoinEventFiller(Mem.of(event.getMember())));
    }

    public static void onMemberLeaveEvent(final MemberLeaveEvent event) {
        MemberCache.slash(event.getUser().getId().asString());
        DevArea.executeGlobal(new MemberLeaveEventFiller(event.getUser().getId()));
    }

    public static void onMessageCreateEvent(final MessageCreateEvent event) {
        if (event.getMember().isPresent())
            MemberCache.use(event.getMember().get());
        DevArea.executeGlobal(event);
        if (event.getMember().isPresent())
            MemberCache.get(event.getMember().get().getId().asString()).execute(event);
    }

    public static void onMessageDeleteEvent(final MessageDeleteEvent event) {
        DevArea.executeGlobal(event);
        MemberCache.get(event.getMessage().get().getAuthor().get().getId().asString()).execute(event);
    }

    public static void onMessageUpdateEvent(final MessageUpdateEvent event) {
        DevArea.executeGlobal(event);
        MemberCache.get(event.getOld().get().getAuthor().get().getId().asString()).execute(event);
    }

    public static void onReactionAddEvent(final ReactionAddEvent event) {
        if (event.getMember().isPresent())
            MemberCache.use(event.getMember().get());
        DevArea.executeGlobal(event);
        MemberCache.get(event.getMember().get().getId().asString()).execute(event);
    }

    public static void onReactionRemoveEvent(final ReactionRemoveEvent event) {
        DevArea.executeGlobal(event);
        MemberCache.get(event.getUserId().asString()).execute(event);
    }

    public static void onSelectMenuInteractionEvent(final SelectMenuInteractionEvent event) {
        if (event.getInteraction().getMember().isPresent())
            MemberCache.use(event.getInteraction().getMember().get());
        DevArea.executeGlobal(event);
        MemberCache.get(event.getInteraction().getMember().get().getId().asString()).execute(event);
    }

    public static void onVoiceStateUpdateEvent(final VoiceStateUpdateEvent event) {
        DevArea.executeGlobal(event);
        MemberCache.get(event.getOld().get().getUserId().asString()).execute(event);
    }

    public static void onChatInputInteractionEvent(final ChatInputInteractionEvent event) {
        if (event.getInteraction().getMember().isPresent())
            MemberCache.use(event.getInteraction().getMember().get());
        DevArea.executeGlobal(event);
        MemberCache.get(event.getInteraction().getUser().getId().asString()).execute(event);
    }

}
