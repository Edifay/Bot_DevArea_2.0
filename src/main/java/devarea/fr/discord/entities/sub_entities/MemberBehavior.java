package devarea.fr.discord.entities.sub_entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import devarea.fr.discord.Core;
import discord4j.core.object.entity.Message;
import discord4j.discordjson.json.MessageData;

public class MemberBehavior {
    /* Represents the behavior a member, used in AntiSpamWorker */

    /**
     * Time in millis.
     */
    private static final long forgetTime = 10000;
    private static final Map<String, Integer> suspectWords = Map.of(
        "nude", 30,
        "porn", 30,
        "@everyone", 20
    );

    public int messageStreak = 0;
    public ArrayList<Message> lastMessages = new ArrayList<>();
    public Set<String> channelsId = new HashSet<>(); // IDs of recents channels user sent messages to
    public int invitesCount = 0;
    public int suspectWordsScore = 0;
    public boolean flagged = false;


    private long firstRecordTimestamp = 0;
    private long lastRecordTimestamp = 0;

    public MemberBehavior() {
    }

    public void forgetBehavior() {
        messageStreak = 0;
        lastMessages.clear();
        channelsId.clear();
        invitesCount = 0;
        suspectWordsScore = 0;
        flagged = false;
    }

    public void recordMessage(final Message message) {
        if (canBeForgiven()) {
            forgetBehavior();
            firstRecordTimestamp = System.currentTimeMillis();
        }

        String messageContent = message.getContent().toLowerCase().replace("0", "o").replace("3", "e");

        lastRecordTimestamp = System.currentTimeMillis();

        messageStreak += 1;
        lastMessages.add(message);
        channelsId.add(message.getChannelId().asString());

        /* Invite link */
        if (messageContent.contains("discord.gg/")) {
            invitesCount += 1;
        }

        for (String suspectWord : suspectWords.keySet()) {
            if (messageContent.contains(suspectWord)) {
                suspectWordsScore += suspectWords.get(suspectWord);
            }
        }
    }

    // TODO : adjust these parameters
    /* > 60 means dangerous, > 30 means suspect, < 30 is ok */
    public int getBehaviorScore() {
        return messageStreak * 4 + channelsId.size() * 10 + invitesCount * 20 + suspectWordsScore;
    }

    public long getRecordDuration() {
        return System.currentTimeMillis() - firstRecordTimestamp;
    }

    public boolean canBeForgiven() {
        return System.currentTimeMillis() - lastRecordTimestamp > forgetTime;
    }
}
