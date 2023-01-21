package devarea.fr.discord.workers;

import devarea.fr.Main;
import devarea.fr.db.DBManager;
import devarea.fr.db.data.DBMessage;
import devarea.fr.db.data.DBMission;
import devarea.fr.discord.DevArea;
import devarea.fr.discord.cache.ChannelCache;
import devarea.fr.discord.cache.GuildEmojiCache;
import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entity.ActionEvent;
import devarea.fr.discord.entity.Mem;
import devarea.fr.discord.entity.events_filler.ButtonInteractionEventFiller;
import devarea.fr.discord.entity.events_filler.ReadyEventFiller;
import devarea.fr.discord.statics.ColorsUsed;
import devarea.fr.utils.Logger;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.*;
import discord4j.discordjson.possible.Possible;

import java.util.*;

import static devarea.fr.discord.statics.TextMessage.*;
import static devarea.fr.utils.ThreadHandler.*;

public class MissionWorker implements Worker {

    /**
     * A static channel to do rapid action on it. He is not fetched again.
     */
    private static GuildMessageChannel missionChannel;

    @Override
    public void onStart() {
        Logger.logMessage("MissionsWorker Created !");
    }

    /**
     * Add 2 listeners.
     * <p>
     * ButtonInteractionEvent -> handle the interaction coming from buttons.
     * ReadyEventFiller -> start checking missions status, and setup the bottomMessage.
     *
     * @return
     */
    @Override
    public ActionEvent<?> setupEvent() {
        DevArea.listen((ActionEvent<ButtonInteractionEventFiller>) filler -> {
            interact(filler);
        });
        return (ActionEvent<ReadyEventFiller>) event -> {

            setupBottomMessage();

            repeatEachMillis(() -> {
                if (!Main.developing)
                    checkForUpdate();
            }, 3600000);

        };
    }

    @Override
    public void onStop() {

    }

    /**
     * Check if the mission embed is already at the bottom of the channel.
     * If not it send a new one.
     */
    private static void setupBottomMessage() {
        missionChannel = (GuildMessageChannel) ChannelCache.fetch(DevArea.data.paidMissions_channel.asString()).entity;
        Message currentAtBottom = missionChannel.getLastMessage().block();
        if (currentAtBottom.getEmbeds().size() == 0 || currentAtBottom.getEmbeds().get(0).getTitle().equals("Créer " +
                "une mission."))
            sendLastMessage();
    }

    /**
     * Send the bottom message.
     */
    private static void sendLastMessage() {
        missionChannel.createMessage(missionBottomMessage).subscribe();
    }

    /**
     * Dispatch the interaction event
     *
     * @param filler the current event
     */
    public static void interact(final ButtonInteractionEventFiller filler) {
        if (filler.event.getCustomId().startsWith("mission"))
            actionToUpdateMission(filler);
    }

    /**
     * When a member leave delete of the message of the mission don't touch to the data base.
     *
     * @param id the id of the member
     */
    public static void clearThisMember(final String id) {
        for (DBMission mission : DBManager.getMissionOf(id)) {
            clearThisMission(mission);
        }
    }

    /**
     * Clear a mission. When she is deleted or when a member leave. Don't touch to data base.
     *
     * @param mission the mission at clear.
     */
    public static void clearThisMission(DBMission mission) {
        startAway(() -> mission.getMessage().getMessage().delete().subscribe());
    }

    /**
     * Get all missions of a member.
     *
     * @param id the member Id
     * @return the list of the DBMissions.
     */
    public static ArrayList<DBMission> getMissionsOf(final String id) {
        return DBManager.getMissionOf(id);
    }

    /**
     * Check the status of a mission.<p>
     * If the mission need to be updated, she send a DM to the member. And set the lastUpdate to let 3 more days to respond.<p>
     * If the member didn't react during the 3days, the message is updated, and the mission is deleted.
     */
    public static void checkForUpdate() {
        ArrayList<DBMission> spoiled_missions = new ArrayList<>();

        for (DBMission mission : DBManager.getMissions())
            if (mission.getMessageUpdate() == null && System.currentTimeMillis() - mission.getLastUpdate() > 604800000)
                askValidate(mission);
            else if (mission.getMessageUpdate() != null && System.currentTimeMillis() - mission.getLastUpdate() > 864000000)
                spoiled_missions.add(mission);


        for (DBMission mission : spoiled_missions)
            validateSpoilAction(mission);

    }

    /**
     * Handle the response to the Update DM.
     *
     * @param filler the response event.
     * @return true if the response could be handle. False if not.
     */
    public static boolean actionToUpdateMission(final ButtonInteractionEventFiller filler) {
        DBMission current_mission = DBManager.getMissionFromUpdateMessage(new DBMessage(filler.event.getMessageId().asString(), filler.event.getMessage().get().getChannelId().asString()));

        System.out.println("Mission : " + current_mission);

        if (current_mission != null) {
            if (filler.event.getCustomId().equals("mission_yes")) {
                sendMissionRevalidateSuccessful(filler, current_mission);
            } else if (filler.event.getCustomId().equals("mission_no")) {
                sendMissionDeleteSuccessful(filler, current_mission);
                DBManager.deleteMission(current_mission.get_id());
                clearThisMission(current_mission);
            }
            return true;
        }
        return false;
    }

    /**
     * Generate a message to comfirm the delete of a mission.
     *
     * @param filler           the event.
     * @param current_mission the mission at delete
     */
    private static void sendMissionDeleteSuccessful(ButtonInteractionEventFiller filler, DBMission current_mission) {
        // This action is private channel ChannelCache cannot be used !
        filler.event.getInteraction().getChannel().block().getMessageById(Snowflake.of(current_mission.getMessageUpdate().getMessageID())).block()
                .edit(MessageEditSpec.builder().addEmbed(EmbedCreateSpec.builder()
                                .title("Mission supprimée !")
                                .description("La mission : **" + current_mission.getTitle() + "**, a été " +
                                        "définitivement supprimée !")
                                .color(ColorsUsed.just).build())
                        .components(Possible.of(Optional.of(new ArrayList<>())))
                        .build()).subscribe();
    }

    /**
     * Update the mission, and give 7 more days.
     *
     * @param filler           response event
     * @param current_mission the mission to update
     */
    private static void sendMissionRevalidateSuccessful(ButtonInteractionEventFiller filler, DBMission current_mission) { // TODO Extract message
        // This action is private channel ChannelCache cannot be used !
        filler.event.getInteraction().getChannel().block().getMessageById(Snowflake.of(current_mission.getMessageUpdate().getMessageID())).block()
                .edit(MessageEditSpec.builder().addEmbed(EmbedCreateSpec.builder()
                                .title("Mission actualisée !")
                                .description("La mission : **" + current_mission.getTitle() + "**, a été définie comme" +
                                        " valide pour encore 7 jours.\n\nVous recevrez une nouvelle demande de " +
                                        "validation dans 7 jours.")
                                .color(ColorsUsed.just).build())
                        .components(Possible.of(Optional.of(new ArrayList<>())))
                        .build()).block();
        current_mission.setLastUpdate(System.currentTimeMillis());
        current_mission.setMessageUpdate(null);
        DBManager.updateMission(current_mission);
    }

    /**
     * Send the message to ask to validate or not.
     * With 2 component, yes, or not.
     * <p>
     * Component ids :
     * yes -> mission_yes
     * no -> mission_no
     *
     * @param mission the mission on who ask update.
     */
    public static void askValidate(DBMission mission) {
        Mem mission_member = MemberCache.get(mission.getCreatedById());// TODO Extract message
        Message message = mission_member.entity.getPrivateChannel().block().createMessage(MessageCreateSpec.builder()
                .addEmbed(EmbedCreateSpec.builder()
                        .title("Vérification de la validité d'une mission.")
                        .description("Vous avez une mission actuellement active !\n\nLe titre de cette mission est : " +
                                "**" + mission.getTitle() + "**\n\nIl vous reste 3 jours pour nous confirmer ou non " +
                                "si cette mission est toujours d'actualité.\n\nSi oui : <:ayy:" + DevArea.data.yes.asString() + "> si non : <:ayy:" + DevArea.data.no.asString() + ">.")
                        .color(ColorsUsed.same).build())
                .addComponent(ActionRow.of(Button.primary("mission_yes", ReactionEmoji.custom(GuildEmojiCache.watch(DevArea.data.yes.asString()))),
                        Button.primary("mission_no", ReactionEmoji.custom(GuildEmojiCache.watch(DevArea.data.no.asString())))))
                .build()).block();
        mission.setLastUpdate(System.currentTimeMillis() - 604800000);
        mission.setMessageUpdate(new DBMessage(message));
        DBManager.updateMission(mission);
    }

    /**
     * After 3 days, spoil the mission and update the DM message.
     * The mission is also deleted.
     *
     * @param mission the mission at spoil.
     */
    public static void validateSpoilAction(DBMission mission) {
        Mem mission_member = MemberCache.get(mission.getCreatedById());
        mission_member.entity.getPrivateChannel().block().getMessageById(Snowflake.of(mission.getMessageUpdate().getMessageID())).block()
                .edit(MessageEditSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Mission supprimée !")
                                .description("Le délai des 3 jours a expiré. La mission : **" + mission.getTitle() +
                                        "**, a été définitivement supprimée !")
                                .color(ColorsUsed.wrong).build())
                        .components(new ArrayList<>()).build())
                .subscribe();
        clearThisMission(mission);
        DBManager.deleteMission(mission.get_id());
    }


}
