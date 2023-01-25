package devarea.fr.discord.workers.linked;

import devarea.fr.db.DBManager;
import devarea.fr.db.data.DBMessage;
import devarea.fr.db.data.DBMission;
import devarea.fr.db.data.DBMissionFollow;
import devarea.fr.discord.Core;
import devarea.fr.discord.cache.ChannelCache;
import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.Mem;
import devarea.fr.discord.entities.events_filler.ButtonInteractionEventFiller;
import devarea.fr.discord.statics.ColorsUsed;
import devarea.fr.discord.workers.Worker;
import discord4j.common.util.Snowflake;
import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import discord4j.core.spec.TextChannelCreateSpec;
import discord4j.core.spec.TextChannelEditSpec;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static devarea.fr.discord.statics.TextMessage.*;
import static devarea.fr.utils.ThreadHandler.startAway;
import static devarea.fr.utils.ThreadHandler.startAwayIn;

public class MissionFollowWorker implements Worker {

    @Override
    public ActionEvent<?> setupEvent() {
        return (ActionEvent<ButtonInteractionEventFiller>) filler -> {
            interact(filler);
        };
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
        closeFollowedMission(filler.event.getInteraction().getMember().get().getId().asString(),
                getMissionFollowByMessageID(filler.event.getMessageId().asString()));
    }

    /**
     * Get the mission follow from a message.
     *
     * @param messageID the message Id
     * @return the missionFollow who own the message.
     */
    public static DBMissionFollow getMissionFollowByMessageID(final String messageID) {
        return DBManager.getMissionFollowFromMessage(messageID);
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
                return false;
            }
            if (alreadyHaveAChannel(mission.getCreatedById(), member_react_id.asString())) {
                filler.event.reply(alreadyFollowingThisMission).subscribe();
                return false;
            }
            followThisMission(mission, member_react_id);
            return true;
        }
        return false;
    }

    public static String webTookMission(final DBMission mission, final Mem mem) {
        if (mission.getCreatedById().equals(mem.getSId()))
            return "Vous ne pouvez pas prendre votre propre mission !";
        if (alreadyHaveAChannel(mission.getCreatedById(), mem.getSId()))
            return "Vous avez déjà pris cette mission !";
        followThisMission(mission, mem.getId());
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
    private static void followThisMission(DBMission mission, Snowflake member_react_id) {
        // Create a channel
        Set<PermissionOverwrite> set = getPermissionsOverrideCreatePrivateChannel(mission, member_react_id);
        DBManager.incrementMissionFollowCount();
        GuildMessageChannel channel = Core.devarea.createTextChannel(TextChannelCreateSpec.builder()
                .parentId(Core.data.mission_follow_category)
                .name("Suivis de mission n°" + DBManager.currentMissionFollowCount())
                .permissionOverwrites(set)
                .build()).block();


        // Send basics information
        channel.createMessage(missionFollowMissionPreview(mission)).subscribe();

        Message message =
                channel.createMessage(missionFollowedCreateMessageExplication(member_react_id, mission)).block();

        DBManager.createMissionFollow(new DBMissionFollow(DBManager.currentMissionFollowCount(), new DBMessage(message),
                mission.getCreatedById(), member_react_id.asString()));

    }

    /**
     * Return a the set of permissions for a new missionFollow.
     *
     * @param mission         the mission to follow
     * @param member_react_id the member who's following
     * @return the set of Permission to set to a channel.
     */
    private static Set<PermissionOverwrite> getPermissionsOverrideCreatePrivateChannel(DBMission mission,
                                                                                       Snowflake member_react_id) {
        Set<PermissionOverwrite> set = new HashSet<>();
        set.add(PermissionOverwrite.forRole(Core.data.rulesAccepted_role, PermissionSet.of(),
                PermissionSet.of(Permission.VIEW_CHANNEL)));
        set.add(PermissionOverwrite.forRole(Core.devarea.getEveryoneRole().block().getId(), PermissionSet.of(),
                PermissionSet.of(Permission.VIEW_CHANNEL)));
        set.add(PermissionOverwrite.forMember(member_react_id, PermissionSet.of(Permission.VIEW_CHANNEL,
                Permission.READ_MESSAGE_HISTORY, Permission.SEND_MESSAGES), PermissionSet.of()));
        set.add(PermissionOverwrite.forMember(Snowflake.of(mission.getCreatedById()),
                PermissionSet.of(Permission.VIEW_CHANNEL, Permission.READ_MESSAGE_HISTORY,
                        Permission.SEND_MESSAGES), PermissionSet.of()));
        set.add(PermissionOverwrite.forRole(Snowflake.of("777782222920744990"),
                PermissionSet.of(Permission.VIEW_CHANNEL), PermissionSet.of()));
        set.add(PermissionOverwrite.forRole(Snowflake.of("768383784571240509"),
                PermissionSet.of(Permission.VIEW_CHANNEL), PermissionSet.of()));
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

            startAwayIn(() -> {

                DBManager.deleteMissionFollow(missionFollow);

                Set<PermissionOverwrite> set = getPermissionOverwritesToHideChannel(missionFollow);

                // Rename channel and set Perms
                ((TextChannel) ChannelCache.watch(missionFollow.getMessage().getChannelID()).entity).edit(TextChannelEditSpec.builder()
                        .name("Closed n°" + missionFollow.getN())
                        .permissionOverwrites(set)
                        .build()).subscribe();

                // Create the MP Embed
                EmbedCreateSpec embed = EmbedCreateSpec.builder()
                        .title("Clôture du suivi n°" + missionFollow.getN() + " !")
                        .description("Le suivi de mission n°" + missionFollow.getN() + " a été clôturé à la " +
                                "demande de <@" + memberRequest + ">.")
                        .color(ColorsUsed.just)
                        .build();

                startAway(() -> MemberCache.get(missionFollow.getClientId()).entity.getPrivateChannel().block().createMessage(embed).subscribe());
                startAway(() -> MemberCache.get(missionFollow.getDevId()).entity.getPrivateChannel().block().createMessage(embed).subscribe());

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
        set.add(PermissionOverwrite.forRole(Core.data.rulesAccepted_role, PermissionSet.of(),
                PermissionSet.of(Permission.VIEW_CHANNEL)));
        set.add(PermissionOverwrite.forRole(Core.devarea.getEveryoneRole().block().getId(), PermissionSet.of(),
                PermissionSet.of(Permission.VIEW_CHANNEL)));
        set.add(PermissionOverwrite.forMember(Snowflake.of(missionFollow.getClientId()), PermissionSet.of(),
                PermissionSet.of(Permission.VIEW_CHANNEL)));
        set.add(PermissionOverwrite.forMember(Snowflake.of(missionFollow.getDevId()), PermissionSet.of(),
                PermissionSet.of(Permission.VIEW_CHANNEL)));
        set.add(PermissionOverwrite.forRole(Snowflake.of("777782222920744990"),
                PermissionSet.of(Permission.VIEW_CHANNEL), PermissionSet.of()));
        set.add(PermissionOverwrite.forRole(Snowflake.of("768383784571240509"),
                PermissionSet.of(Permission.VIEW_CHANNEL), PermissionSet.of()));
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
}
