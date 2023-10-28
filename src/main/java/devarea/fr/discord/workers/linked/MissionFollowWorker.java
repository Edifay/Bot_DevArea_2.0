package devarea.fr.discord.workers.linked;

import devarea.fr.Main;
import devarea.fr.db.DBManager;
import devarea.fr.db.data.*;
import devarea.fr.discord.Core;
import devarea.fr.discord.cache.ChannelCache;
import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.Mem;
import devarea.fr.discord.entities.events_filler.ButtonInteractionEventFiller;
import devarea.fr.discord.statics.ColorsUsed;
import devarea.fr.discord.workers.Worker;
import devarea.fr.utils.Logger;
import discord4j.common.util.Snowflake;
import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.*;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static devarea.fr.discord.statics.DefaultData.DOMAIN_NAME;
import static devarea.fr.discord.statics.TextMessage.*;
import static devarea.fr.utils.ThreadHandler.*;

public class MissionFollowWorker implements Worker {

    @Override
    public ActionEvent<?> setupEvent() {
        if (Main.developing)
            return null;

        repeatEachMillis(() -> {
            ArrayList<DBMissionFollow> missionFollows = DBManager.getMissionFollow();
            missionFollows.forEach(dbMissionFollow -> {
                try {
                    GuildMessageChannel channel = dbMissionFollow.getMessage().getChannel();
                    Message lastMessage = channel.getLastMessage().block();
                    if (lastMessage.getTimestamp().isBefore(Instant.now().minus(2, ChronoUnit.DAYS)) && lastMessage.getAuthor().get().getId().equals(Core.client.getSelfId())) {
                        Logger.logMessage("Auto-closing suivis-n°" + dbMissionFollow.getN() + ".");
                        closeFollowedMission(Core.client.getSelfId().asString(), dbMissionFollow);
                    } else if (lastMessage.getTimestamp().isBefore(Instant.now().minus(3, ChronoUnit.DAYS))) {
                        Logger.logMessage("Ask to close suivis-n°" + dbMissionFollow.getN() + ".");
                        channel.createMessage(MessageCreateSpec.builder()
                            .content("<@" + dbMissionFollow.getClientId() + ">, <@" + dbMissionFollow.getDevId() + ">.")
                            .addEmbed(EmbedCreateSpec.builder()
                                .title("Fermeture du suivis ?")
                                .description("Cela fait plus de 3 jours que ce suivis est inactif ! Il serait peut être temps de le fermer ?\n\nSi oui il vous suffit de cliquer sur le bouton ci-dessous.")
                                .footer("Ceci est un message automatique.", null)
                                .color(ColorsUsed.same)
                                .build())
                            .addComponent(ActionRow.of(Button.secondary("followMission_close", "Cloturer le channel")))
                            .build()).subscribe();
                    }
                } catch (Exception e) {
                    Logger.logError("Error when trying to access to suivis-n°" + dbMissionFollow.getN() + " :\n    -> " + e.getMessage());
                }
            });
        }, 86400000 /*1 day*/);

        return (ActionEvent<ButtonInteractionEventFiller>) MissionFollowWorker::interact;
    }

    @Override
    public void onStop() {

    }

    /**
     * Dispatch the response from ButtonInteractionEvent
     *
     * @param filler the filler event
     */
    public static void interact(final ButtonInteractionEventFiller filler) {
        if (filler.event.getCustomId().equals("took_mission"))
            actionToTookMission(filler);
        else if (filler.event.getCustomId().equals("followMission_close"))
            actionToCloseFollowedMission(filler);
    }

    /**
     * Clear all missionFollow of a member, by renaming and change perm of the channel.
     *
     * @param id the member Id
     */
    public static void clearThisMember(final String id) {
        ArrayList<DBMissionFollow> missionFollows = DBManager.getMissionFollowOf(id);
        for (DBMissionFollow missionFollow : missionFollows) {
            closeFollowedMission(id, missionFollow);
        }
    }

    /**
     * The action from ButtonInteractionEvent, to close a missionFollow.
     *
     * @param filler The event
     */
    public static void actionToCloseFollowedMission(final ButtonInteractionEventFiller filler) {
        DBMissionFollow mission = getMissionFollowByChannelID(filler.event.getInteraction().getChannelId().asString());
        if (mission == null) {
            filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                .ephemeral(true)
                .addEmbed(EmbedCreateSpec.builder()
                    .title("Erreur")
                    .color(ColorsUsed.same)
                    .description("Le suivis de mission que vous cherchez n'existe plus !")
                    .build())
                .build()).subscribe();
            return;
        }
        startAway(() -> {
            filler.event.deferReply().block();
            filler.event.deleteReply().subscribe();
        });
        closeFollowedMission(filler.event.getInteraction().getMember().get().getId().asString(), mission);
    }

    /**
     * Get the mission follow from a message.
     *
     * @param channelID the channel Id
     * @return the missionFollow who own the message.
     */
    public static DBMissionFollow getMissionFollowByChannelID(final String channelID) {
        return DBManager.getMissionFollowFromChannel(channelID);
    }

    /**
     * Action from the ButtonInteractionEvent, to take a mission.
     * Create a new missionFollow.
     *
     * @param filler the event
     * @return true if missionFollow already exist or is created. And false if not.
     */
    public static boolean actionToTookMission(final ButtonInteractionEventFiller filler) {
        DBMission mission = DBManager.getMissionFromMessage(new DBMessage(filler.event.getMessageId().asString(), filler.event.getMessage().get().getChannelId().asString()));
        Snowflake member_react_id = filler.event.getInteraction().getMember().get().getId();
        if (mission != null) {
            if (mission.getCreatedById().equals(member_react_id.asString())) {
                filler.event.reply(cannotFollowYourOwnMission).subscribe();
                Logger.logMessage(filler.mem.entity.getTag() + " tried to follow his own mission ! (Discord input)");
                return false;
            }
            if (alreadyHaveAChannel(mission.getCreatedById(), member_react_id.asString())) {
                filler.event.reply(alreadyFollowingThisMission).subscribe();
                Logger.logMessage(filler.mem.entity.getTag() + " tried to follow an already followed mission : \"" + mission.getTitle() + "\". (Discord input)");
                return false;
            }
            Logger.logMessage(filler.mem.entity.getTag() + " followed the mission \"" + mission.getTitle() + "\". (Discord input)");
            Snowflake channelId = followThisMission(mission, member_react_id);
            filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                .ephemeral(true)
                .addEmbed(EmbedCreateSpec.builder()
                    .title("Suivis de mission")
                    .description("Un channel privé a été créé -> <#" + channelId.asString() + "> !")
                    .color(ColorsUsed.same)
                    .build())
                .build()).subscribe();
            return true;
        }
        return false;
    }

    public static String webTookMission(final DBMission mission, final Mem mem) {
        if (mission.getCreatedById().equals(mem.getSId())) {
            Logger.logMessage(mem.entity.getTag() + " tried to follow his own mission ! (Site input)");
            return "Vous ne pouvez pas prendre votre propre mission !";
        }
        if (alreadyHaveAChannel(mission.getCreatedById(), mem.getSId())) {
            Logger.logMessage(mem.entity.getTag() + " tried to follow an already followed mission : \"" + mission.getTitle() + "\". (Site input)");
            return "Vous avez déjà pris cette mission !";
        }
        followThisMission(mission, mem.getId());
        Logger.logMessage(mem.entity.getTag() + " followed the mission \"" + mission.getTitle() + "\". (Site input)");
        return "Vous venez de prendre cette mission !";
    }

    /**
     * Creator of a missionFollow. Create the channel changes permissions on it.
     * <p>
     * Add the missionFollow to DB.
     *
     * @param mission         the mission to follow
     * @param member_react_id the member who is following
     */
    private static Snowflake followThisMission(DBMission mission, Snowflake member_react_id) {
        // Create a channel
        Set<PermissionOverwrite> set = getPermissionsOverrideCreatePrivateChannel(mission, member_react_id);
        DBManager.incrementMissionFollowCount();
        GuildMessageChannel channel = Core.devarea.createTextChannel(TextChannelCreateSpec.builder()
            .parentId(Core.data.mission_follow_category)
            .name("Suivis n°" + DBManager.currentMissionFollowCount())
            .permissionOverwrites(set)
            .build()).block();


        // Send basics information
        channel.createMessage(missionFollowMissionPreview(mission)).subscribe();
        Message message =
            channel.createMessage(missionFollowedCreateMessageExplication(member_react_id, mission)).block();

        Mem client = mission.getMember();
        Mem dev = MemberCache.get(member_react_id.asString());
        channel.createMessage(MessageCreateSpec.builder()
            .addEmbed(EmbedCreateSpec.builder()
                .color(ColorsUsed.same)
                .title("Avis")
                .addField(client.entity.getDisplayName(), "Note " + moyeneNote(client.db()) + "/5.\n -> [Voir plus](" + DOMAIN_NAME + "/member-profile?member_id=" + client.getSId() + "&open=2)", true)
                .addField("", "", true)
                .addField(dev.entity.getDisplayName(), "Note " + moyeneNote(dev.db()) + "/5.\n -> [Voir plus](" + DOMAIN_NAME + "/member-profile?member_id=" + dev.getSId() + "&open=2)", true)
                .build())
            .build()).subscribe();

        DBManager.createMissionFollow(new DBMissionFollow(DBManager.currentMissionFollowCount(), new DBMessage(message),
            mission.getCreatedById(), member_react_id.asString()));

        return channel.getId();
    }

    /**
     * Return a the set of permissions for a new missionFollow.
     *
     * @param mission         the mission to follow
     * @param member_react_id the member who's following
     * @return the set of Permission to set to a channel.
     */
    private static Set<PermissionOverwrite> getPermissionsOverrideCreatePrivateChannel(DBMission mission, Snowflake member_react_id) {
        Set<PermissionOverwrite> set = new HashSet<>();
        set.add(PermissionOverwrite.forRole(Core.data.rulesAccepted_role, PermissionSet.of(), PermissionSet.of(Permission.VIEW_CHANNEL)));
        set.add(PermissionOverwrite.forRole(Core.devarea.getEveryoneRole().block().getId(), PermissionSet.of(), PermissionSet.of(Permission.VIEW_CHANNEL)));
        set.add(PermissionOverwrite.forMember(member_react_id, PermissionSet.of(Permission.VIEW_CHANNEL, Permission.READ_MESSAGE_HISTORY, Permission.SEND_MESSAGES), PermissionSet.of()));
        set.add(PermissionOverwrite.forMember(Snowflake.of(mission.getCreatedById()), PermissionSet.of(Permission.VIEW_CHANNEL, Permission.READ_MESSAGE_HISTORY, Permission.SEND_MESSAGES), PermissionSet.of()));
        set.add(PermissionOverwrite.forRole(Snowflake.of("777782222920744990"), PermissionSet.of(Permission.VIEW_CHANNEL), PermissionSet.of()));
        set.add(PermissionOverwrite.forRole(Snowflake.of("768383784571240509"), PermissionSet.of(Permission.VIEW_CHANNEL), PermissionSet.of()));
        return set;
    }

    /**
     * Close a followedMission. Changes perms, and rename the channel.
     *
     * @param memberRequest The member requesting the close.
     * @param missionFollow The mission Follow.
     */
    private static void closeFollowedMission(String memberRequest, DBMissionFollow missionFollow) {
        if (missionFollow != null) {
            ((GuildMessageChannel) ChannelCache.watch(missionFollow.getMessage().getChannelID()).entity)
                .createMessage(missionFollowedCloseIn1Hour(memberRequest)).subscribe();

            missionFollow.getMessage().getMessage().edit(MessageEditSpec.builder()
                .components(new ArrayList<>())
                .build()).subscribe();

            DBManager.deleteMissionFollow(missionFollow);

            startAwayIn(() -> {

                Set<PermissionOverwrite> set = getPermissionOverwritesToHideChannel(missionFollow);

                // Rename channel and set Perms
                ((TextChannel) ChannelCache.watch(missionFollow.getMessage().getChannelID()).entity).edit(TextChannelEditSpec.builder()
                    .name("Closed n°" + missionFollow.getN())
                    .permissionOverwrites(set)
                    .build()).subscribe();

                // Create the MP Embed
                startAway(() -> {
                    EmbedCreateSpec embed = EmbedCreateSpec.builder()
                        .title("Clôture du suivi n°" + missionFollow.getN() + " !")
                        .description("Le suivi de mission n°" + missionFollow.getN() + " a été clôturé à la " +
                                     "demande de <@" + memberRequest + ">.\n\nSi l'échange a mené à la réalisation de la mission n'hésitez pas à donner un avi sur <@" + missionFollow.getDevId() + "> !")
                        .color(ColorsUsed.same)
                        .build();

                    MemberCache.get(missionFollow.getClientId()).entity.getPrivateChannel().block().createMessage(MessageCreateSpec.builder()
                        .addEmbed(embed)
                        .addComponent(ActionRow.of(Button.primary("avis_" + missionFollow.getDevId() + "_C", ReactionEmoji.codepoints("U+1F4D5"), "Laisser un avis.")))
                        .build()).subscribe();
                });
                startAway(() -> {
                    EmbedCreateSpec embed = EmbedCreateSpec.builder()
                        .title("Clôture du suivi n°" + missionFollow.getN() + " !")
                        .description("Le suivi de mission n°" + missionFollow.getN() + " a été clôturé à la " +
                                     "demande de <@" + memberRequest + ">.\n\nSi l'échange a mené à la réalisation de la mission n'hésitez pas à donner un avi sur <@" + missionFollow.getClientId() + "> !")
                        .color(ColorsUsed.same)
                        .build();
                    MemberCache.get(missionFollow.getDevId()).entity.getPrivateChannel().block().createMessage(MessageCreateSpec.builder()
                        .addEmbed(embed)
                        .addComponent(ActionRow.of(Button.primary("avis_" + missionFollow.getClientId() + "_F", ReactionEmoji.codepoints("U+1F4D5"), "Laisser un avis.")))
                        .build()).subscribe();
                });

            }, 3600000L);
        }
    }


    /**
     * Return a the set of permissions for close a missionFollow.
     *
     * @param missionFollow the mission to follow
     * @return the set of Permission to set to a channel.
     */
    private static Set<PermissionOverwrite> getPermissionOverwritesToHideChannel(DBMissionFollow missionFollow) {
        Set<PermissionOverwrite> set = new HashSet<>();
        set.add(PermissionOverwrite.forRole(Core.data.rulesAccepted_role, PermissionSet.of(), PermissionSet.of(Permission.VIEW_CHANNEL)));
        set.add(PermissionOverwrite.forRole(Core.devarea.getEveryoneRole().block().getId(), PermissionSet.of(), PermissionSet.of(Permission.VIEW_CHANNEL)));
        set.add(PermissionOverwrite.forMember(Snowflake.of(missionFollow.getClientId()), PermissionSet.of(), PermissionSet.of(Permission.VIEW_CHANNEL)));
        set.add(PermissionOverwrite.forMember(Snowflake.of(missionFollow.getDevId()), PermissionSet.of(), PermissionSet.of(Permission.VIEW_CHANNEL)));
        set.add(PermissionOverwrite.forRole(Snowflake.of("777782222920744990"), PermissionSet.of(Permission.VIEW_CHANNEL), PermissionSet.of()));
        set.add(PermissionOverwrite.forRole(Snowflake.of("768383784571240509"), PermissionSet.of(Permission.VIEW_CHANNEL), PermissionSet.of()));
        return set;
    }

    /**
     * Check if a a mission follow already exist.
     *
     * @param clientID the client Id
     * @param devID    the dev Id
     * @return return a boolean at the question have already a missionFollow ? True or False
     */
    public static boolean alreadyHaveAChannel(final String clientID, final String devID) {
        return DBManager.getMissionFollowFromPerson(clientID, devID) != null;
    }

    public static String moyeneNote(final DBMember mem) {
        DBAvis[] avis = mem.getAvis();
        if (avis.length == 0)
            return "?";
        int moyenne = avis[0].getGrade();
        for (int i = 1; i < avis.length; i++)
            moyenne += avis[i].getGrade();
        return String.valueOf(moyenne / avis.length);
    }
}
