package devarea.fr.discord.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import discord4j.core.object.entity.Message;
import discord4j.discordjson.json.MessageData;

public class MemberBehavior {
    /* Represents the behavior a member, used in AntiSpamWorker */

    // TODO : modify these words
    private static final long forgetTime = 10000;
    private static final Map<String, Integer> suspectWords = Map.of(
        "nude", 30,
        "porn", 30,
        "@everyone", 20
    );

    public int messageStreak = 0;
    public ArrayList<Message> lastMessages = new ArrayList<>();
    public Set<Long> channelsId = new HashSet<>(); // IDs of recents channels user sent messages to
    public int invitesCount = 0;
    public int suspectWordsScore = 0;

    private long firstRecordTimestamp = 0;
    private long lastRecordTimestamp = 0;

    public MemberBehavior() {}

    public void forgetBehavior() {
        messageStreak = 0;
        lastMessages.clear();
        channelsId.clear();
        invitesCount = 0;
        suspectWordsScore = 0;
    }

    public void recordMessage(final Message message) {
        if (System.currentTimeMillis() - lastRecordTimestamp > forgetTime) {
            forgetBehavior();
            firstRecordTimestamp = System.currentTimeMillis();
        }

        MessageData messageData = message.getData();

        String messageContent = messageData.content().toLowerCase().replace("0", "o").replace("3", "e");

        lastRecordTimestamp = System.currentTimeMillis();

        messageStreak += 1;
        lastMessages.add(message);
        channelsId.add(messageData.channelId().asLong());

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
}
