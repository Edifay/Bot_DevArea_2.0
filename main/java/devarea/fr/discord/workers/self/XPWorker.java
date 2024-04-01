package devarea.fr.discord.workers.self;

import devarea.fr.db.DBManager;
import devarea.fr.db.data.DBMember;
import devarea.fr.discord.Core;
import devarea.fr.discord.cache.ChannelCache;
import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.events_filler.MessageCreateEventFiller;
import devarea.fr.discord.statics.ColorsUsed;
import devarea.fr.discord.workers.Worker;
import discord4j.common.util.Snowflake;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import org.bson.Document;

import java.time.Instant;
import java.util.*;

import static devarea.fr.utils.ThreadHandler.*;

public class XPWorker implements Worker {

    private static final ArrayList<String> memberInCouldDown = new ArrayList<>();
    private static final HashMap<Snowflake, Integer> currentXpEarnVoiceStatus = new HashMap<>();

    @Override
    public void onStart() {
        setupVoiceXpEarn();
        Worker.super.onStart();
    }

    @Override
    public ActionEvent<?> setupEvent() {
        return (ActionEvent<MessageCreateEventFiller>) filler -> {
            if (filler.event.getMember().isPresent()) {
                addXpToMember(filler.event.getMember().get().getId().asString(), true, 1);
            }
        };
    }

    @Override
    public void onStop() {

    }

    /**
     * A thread who check people in voice, and add 1 xp every min, maxed to 90xp per 24 hours slippery.
     */
    private static void setupVoiceXpEarn() {
        repeatEachMillis(() -> {// TODO refactor old code
            List<VoiceState> states = Core.devarea.getVoiceStates().buffer().blockLast();
            if (states != null)

                for (VoiceState voice : states) {
                    Member member = MemberCache.get(voice.getUserId().asString()).entity;
                    boolean memberEarnXpVoiceStatus = currentXpEarnVoiceStatus.containsKey(member.getId());

                    if (!memberEarnXpVoiceStatus || currentXpEarnVoiceStatus.get(member.getId()) <= 90) {
                        // Adding xp and apply cooldown and maxXpEarnInvVoice count
                        addXpToMember(member.getId().asString(), false);
                        currentXpEarnVoiceStatus.put(member.getId(), memberEarnXpVoiceStatus ?
                                currentXpEarnVoiceStatus.get(member.getId()) + 1 : 1);

                        // Removing 1xp earn after 24h
                        startAwayIn(() -> currentXpEarnVoiceStatus.put(member.getId(),
                                currentXpEarnVoiceStatus.get(member.getId()) - 1), 86400000);
                    }
                }

        }, 60000);
    }

    /**
     * Add a default of one xp to a member
     *
     * @param id        the id of the member
     * @param withTimer is this gain of xp with a coulddown
     */
    public synchronized static void addXpToMember(final String id, final boolean withTimer) {
        addXpToMember(id, withTimer, 1);
    }

    /**
     * Add xp to a member, a member with timer can win only xp every 6 secondes.
     *
     * @param id        the memberId
     * @param withTimer timer to win xp again (true or false)
     * @param value     the amount of xp to add
     */
    public synchronized static void addXpToMember(final String id, boolean withTimer, Integer value) {
        if (!withTimer || !memberInCouldDown.contains(id)) {

            verifyIfNextLevelReach(id, value);
            DBManager.incrementXP(id, value);

            if (withTimer) {
                memberInCouldDown.add(id);
                startAwayIn(() -> removeSafely(id), 6000);
            }
        }
    }

    /**
     * Check if the member win a level, and send a message to prevent it.
     *
     * @param id    the memberID
     * @param value the amount of XP won.
     */
    private static void verifyIfNextLevelReach(final String id, Integer value) { // TODO refactor old code, and extract MessageCreateSpec
        int currentXp = DBManager.getXP(id);
        if (XPWorker.getLevelForXp(currentXp) < XPWorker.getLevelForXp(currentXp + value))
            startAway(() -> {
                ((GuildMessageChannel) ChannelCache.watch(Core.data.command_channel.asString()).entity)
                        .createMessage(MessageCreateSpec.builder()
                                .content("<@" + id + ">")
                                .addEmbed(EmbedCreateSpec.builder()
                                        .description("Bien joué <@" + id + ">, tu es passé niveau " + XPWorker.getLevelForXp((currentXp + value)) + " !")
                                        .timestamp(Instant.now())
                                        .color(ColorsUsed.same)
                                        .build())
                                .build()).subscribe();
            });
    }

    /**
     * Remove xp to a member
     *
     * @param id    the memberId
     * @param value the amount of xp
     */
    public synchronized static void removeXpToMember(final String id, final Integer value) {
        DBManager.removeXP(id, value);
    }

    /**
     * Get the xp of a member
     *
     * @param id the memberId
     * @return the amount of xp owned by the member
     */
    public synchronized static Integer getXpOfMember(final String id) {
        return DBManager.getXP(id);
    }

    /**
     * Get the rank of a member. His rank in the server.
     *
     * @param id the memberId
     * @return the rank of the member
     */
    public synchronized static Integer getRankOfMember(final String id) {
        Iterator<Document> sorted = DBManager.listOfXP().iterator();
        int current = 1;
        while (sorted.hasNext()) {
            if (sorted.next().get("_id").equals(id))
                return current;
            current += 1;
        }
        return -1;
    }

    /**
     * Transform an amount of xp to a level.
     *
     * @param xp the amount of xp
     * @return the level link to the xp
     */
    public synchronized static int getLevelForXp(int xp) {
        int level = 0;
        while (xp >= getAmountForLevel(level)) level++;
        return --level;
    }

    /**
     * Get the amount of xp needed to reach a level.
     *
     * @param level the level to reach
     * @return the amount of xp
     */
    public static int getAmountForLevel(int level) {
        return (int) (3 * (Math.pow(level, 2)));
    }

    /**
     * Remove a member from the coulddown list.
     *
     * @param id the memberId
     */
    public static synchronized void removeSafely(final String id) {
        memberInCouldDown.remove(id);
    }

    /**
     * Use for /leaderboard.
     *
     * @return the arrayList of the top 5 of xp list
     */
    public static ArrayList<DBMember> getLeaderBoard() {
        ArrayList<DBMember> list = new ArrayList<>();
        for (Document document : DBManager.getSortedXPList())
            list.add(new DBMember((String) document.get("_id")));
        return list;
    }

}
