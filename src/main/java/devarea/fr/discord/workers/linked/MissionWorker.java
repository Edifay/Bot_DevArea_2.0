package devarea.fr.discord.workers.linked;

import devarea.fr.Main;
import devarea.fr.db.DBManager;
import devarea.fr.db.data.DBMessage;
import devarea.fr.db.data.DBMission;
import devarea.fr.discord.Core;
import devarea.fr.discord.cache.ChannelCache;
import devarea.fr.discord.cache.GuildEmojiCache;
import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.Mem;
import devarea.fr.discord.entities.events_filler.ButtonInteractionEventFiller;
import devarea.fr.discord.entities.events_filler.ReadyEventFiller;
import devarea.fr.discord.statics.ColorsUsed;
import devarea.fr.discord.workers.Worker;
import devarea.fr.utils.Logger;
import discord4j.common.util.Snowflake;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.*;
import discord4j.discordjson.possible.Possible;
import discord4j.rest.util.AllowedMentions;

import java.lang.reflect.Executable;
import java.time.Instant;
import java.util.*;

import static devarea.fr.discord.statics.DefaultData.DOMAIN_NAME;
import static devarea.fr.discord.statics.TextMessage.*;
import static devarea.fr.utils.ThreadHandler.*;

public class MissionWorker implements Worker {

    /**
     * A static channel to do rapid action on it. He is not fetched again.
     */
    private static GuildMessageChannel missionChannel;

    private static Message bottomMessage;

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
        Core.listen((ActionEvent<ButtonInteractionEventFiller>) filler -> {
            interact(filler);
        });
        return (ActionEvent<ReadyEventFiller>) event -> {

            missionChannel = (GuildMessageChannel) ChannelCache.fetch(Core.data.paidMissions_channel.asString()).entity;

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
        try {
            bottomMessage = missionChannel.getLastMessage().block();
        } catch (Exception ignored) {
        }
        if (bottomMessage == null || bottomMessage.getEmbeds().size() == 0 || bottomMessage.getEmbeds().get(0).getTitle().isPresent() && !bottomMessage.getEmbeds().get(0).getTitle().get().equals("Créer une mission."))
            sendLastMessage();
    }

    /**
     * Send the bottom message.
     */
    private static void sendLastMessage() {
        startAway(() -> bottomMessage = missionChannel.createMessage(missionBottomMessage).block());
    }

    /**
     * Resend BottomMessage to keep it at the bottom of the channel
     */
    public static void updateBottomMessage() {
        try {
            if (bottomMessage != null)
                startAway(() -> bottomMessage.delete().subscribe());
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendLastMessage();
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
        try {
            ArrayList<DBMission> spoiled_missions = new ArrayList<>();

            for (DBMission mission : DBManager.getMissions())
                if (mission.getMessageUpdate() == null && System.currentTimeMillis() - mission.getLastUpdate() > 604800000)
                    askValidate(mission);
                else if (mission.getMessageUpdate() != null && System.currentTimeMillis() - mission.getLastUpdate() > 864000000)
                    spoiled_missions.add(mission);


            for (DBMission mission : spoiled_missions)
                validateSpoilAction(mission);
        } catch (Exception e) {
            Logger.logError("Erreur dans le check de l'update.");
            e.printStackTrace();
        }

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
                Logger.logMessage(filler.mem.entity.getTag() + " revalidate his mission \"" + current_mission.getTitle() + "\".");
            } else if (filler.event.getCustomId().equals("mission_no")) {
                sendMissionDeleteSuccessful(filler, current_mission);
                DBManager.deleteMission(current_mission.get_id());
                clearThisMission(current_mission);
                Logger.logMessage(filler.mem.entity.getTag() + " spoiled his mission \"" + current_mission.getTitle() + "\".");
            }
            return true;
        }
        return false;
    }

    /**
     * Generate a message to comfirm the delete of a mission.
     *
     * @param filler          the event.
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
     * @param filler          response event
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
        try {
            Message message = mission_member.entity.getPrivateChannel().block().createMessage(MessageCreateSpec.builder()
                .addEmbed(EmbedCreateSpec.builder()
                    .title("Vérification de la validité d'une mission.")
                    .description("Vous avez une mission actuellement active !\n\nLe titre de cette mission est : " +
                                 "**" + mission.getTitle() + "**\n\nIl vous reste 3 jours pour nous confirmer ou non " +
                                 "si cette mission est toujours d'actualité.\n\nSi oui : <:ayy:" + Core.data.yes.asString() + "> si non : <:ayy:" + Core.data.no.asString() + ">.")
                    .color(ColorsUsed.same).build())
                .addComponent(ActionRow.of(Button.primary("mission_yes", ReactionEmoji.custom(GuildEmojiCache.watch(Core.data.yes.asString()))),
                    Button.primary("mission_no", ReactionEmoji.custom(GuildEmojiCache.watch(Core.data.no.asString())))))
                .build()).block();
            mission.setLastUpdate(System.currentTimeMillis() - 604800000);
            mission.setMessageUpdate(new DBMessage(message));
            DBManager.updateMission(mission);
            Logger.logMessage("The private channel with " + mission_member.entity.getTag() + " be opened, validation for the mission \"" + mission.getTitle() + "\" as been sended !");
        } catch (Exception e) {
            Logger.logMessage("The private channel with " + mission_member.entity.getTag() + " couldn't be opened, the mission \"" + mission.getTitle() + "\" will be deleted.");
            deleteMission(mission.get_id());
            e.printStackTrace();
        }

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
        Logger.logMessage("La mission de " + mission_member.entity.getTag() + " : \"" + mission.getTitle() + "\" a été transféré au spoil. Mission data " + mission);
    }

    /**
     * Get the mission from the _id. (id is an unique id, but it's not the id of the member)
     *
     * @param _id mission id
     * @return the DBMission
     */
    public static DBMission getMissionBy_Id(final String _id) {
        return DBManager.getMission(_id);
    }

    public static void deleteMission(final String _id) {
        clearThisMission(getMissionBy_Id(_id));
        DBManager.deleteMission(_id);
    }

    /**
     * Transform a {@link DBMission} to {@link EmbedCreateSpec} ready to send in a discord message.
     *
     * @param mission the DBMission to transform
     * @return the Embed ready to send.
     */
    public static EmbedCreateSpec getEmbedOf(final DBMission mission) {
        Member member = mission.getMember().entity;
        return EmbedCreateSpec.builder()
            .title(mission.getTitle())
            .description(mission.getDescription() + "\n\nPrix: " + mission.getBudget() + "\nDate de retour: " + mission.getDeadLine() +
                         "\nType de support: " + mission.getSupport() + "\nLangage: " + mission.getLanguage() + "\nNiveau estimé:" +
                         " " + mission.getDifficulty() + "\n\nCette mission est posté par : " + "<@" + mission.getCreatedById() + ">.")
            .color(ColorsUsed.same)
            .author(member.getDisplayName(), DOMAIN_NAME + "member-profile?member_id=" + member.getId().asString(), member.getAvatarUrl())
            .timestamp(Instant.now())
            .build();
    }

    /**
     * Couldown beetween mission create. Contain memberIds.
     */
    public static final ArrayList<String> cooldown_create_Mission = new ArrayList<>();

    /**
     * This function create a new mission owned by the {@link Mem}.
     * <p>
     * The contain of the mission is pass with {@link MissionMapper}.
     *
     * @param mapper the mission data
     * @param mem    the owner of the mission
     * @return true if the mission could be created, false if not.
     */
    public static boolean createMission(final MissionMapper mapper, final Mem mem) {
        if (cooldown_create_Mission.contains(mem.getSId()))
            return false;

        DBMission mission = new DBMission();
        mission.setCreatedById(mem.getSId());
        mission.setTitle(mapper.title);
        mission.setDescription(mapper.description);
        mission.setBudget(mapper.budget);
        mission.setDifficulty(mapper.difficulty);
        mission.setDeadLine(mapper.deadLine);
        mission.setSupport(mapper.support);
        mission.setLanguage(mapper.language);

        EmbedCreateSpec embed = getEmbedOf(mission);

        Message message = missionChannel.createMessage(MessageCreateSpec.builder()
            .content("**Mission proposée par <@" + mem.getSId() + "> :**")
            .allowedMentions(AllowedMentions.suppressAll())
            .addEmbed(embed)
            .addComponent(ActionRow.of(Button.link(DOMAIN_NAME + "mission?id=" + mission.get_id(),
                "devarea.fr"), Button.secondary("took_mission", "Prendre la mission")))
            .build()).block();

        mission.setMessage(new DBMessage(message));


        DBManager.createMission(mission);

        cooldown_create_Mission.add(mem.getSId());
        startAwayIn(() -> cooldown_create_Mission.remove(mem.getSId()), 5000);

        updateBottomMessage();

        Logger.logMessage(mem.entity.getTag() + " <-> " + mem.getSId() + " created a mission : \"" + mission.getTitle() + "\". \n Data : " + mission.toString());

        return true;
    }

    /**
     * Access to the {@link MissionMapper} like this remove the {@code new MissionMapper();} syntax.
     *
     * @return a MissionMapper ready to use.
     */
    public static MissionMapper missionMapper() {
        return new MissionMapper();
    }

    /**
     * To use this class call the {@link #missionMapper()} method.
     * <p>
     * This class is used in {@link #createMission(MissionMapper, Mem)} to pass the mission data.
     */
    public static class MissionMapper {

        protected String title,
            description,
            budget,
            deadLine,
            language,
            support,
            difficulty;


        private MissionMapper() {

        }

        public MissionMapper title(final String title) {
            this.title = title;
            return this;
        }

        public MissionMapper description(final String description) {
            this.description = description;
            return this;
        }

        public MissionMapper budget(final String prix) {
            this.budget = prix;
            return this;
        }

        public MissionMapper deadLine(final String dateRetour) {
            this.deadLine = dateRetour;
            return this;
        }

        public MissionMapper language(final String langage) {
            this.language = langage;
            return this;
        }

        public MissionMapper support(final String support) {
            this.support = support;
            return this;
        }

        public MissionMapper difficulty(final String niveau) {
            this.difficulty = niveau;
            return this;
        }


    }


}
