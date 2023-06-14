package devarea.fr.discord.workers.linked;

import devarea.fr.db.data.DBMessage;
import devarea.fr.discord.Core;
import devarea.fr.discord.cache.ChannelCache;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.Chan;
import devarea.fr.discord.entities.Mem;
import devarea.fr.discord.statics.ColorsUsed;
import devarea.fr.discord.workers.Worker;
import devarea.fr.utils.Logger;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageEditSpec;

import static devarea.fr.discord.statics.TextMessage.presentationBottomMessage;
import static devarea.fr.utils.ThreadHandler.startAway;

public class DescriptionWorker implements Worker {

    private static Message bottomMessage;

    private static Chan<GuildMessageChannel> presentationChannel;

    @Override
    public void onStart() {
        Worker.super.onStart();

        presentationChannel = ChannelCache.watch(Core.data.presentation_channel.asString());
        setupBottomMessage();
    }

    @Override
    public ActionEvent<?> setupEvent() {
        return null;
    }


    public static void updateDescription(final Mem mem) {
        if (mem.db().getDescription() == null || mem.db().getDescription().equals("")) {
            try {
                if (mem.db().getDescriptionMessage() != null)
                    mem.db().getDescriptionMessage().getMessage().delete().block();
            } catch (Exception e) {
            } finally {
                mem.db().setDescriptionMessage(null);
            }
            return;
        }

        if (mem.db().getDescriptionMessage() != null) {
            try {
                mem.db().getDescriptionMessage().getMessage().edit(MessageEditSpec.builder()
                        .addEmbed(getEmbedDescriptionOf(mem))
                        .build()).block();
            } catch (Exception e) {
                mem.db().setDescriptionMessage(null);
                Logger.logError("Couldn't retrieve description message of " + mem.entity.getTag() + " ! A new message will be sent !");
            }
        }

        if (mem.db().getDescriptionMessage() == null) {
            Message msg = presentationChannel.entity.createMessage(getEmbedDescriptionOf(mem)).block();
            mem.db().setDescriptionMessage(new DBMessage(msg));
            updateBottomMessage();
        }
    }

    private static EmbedCreateSpec getEmbedDescriptionOf(final Mem mem) {
        return EmbedCreateSpec.builder()
                .author(mem.entity.getUsername(), "https://devarea.fr/member-profile?member_id=" + mem.getSId(), mem.entity.getAvatarUrl())
                .description(mem.db().getDescription())
                .color(ColorsUsed.same)
                .build();
    }


    /**
     * Check if the mission embed is already at the bottom of the channel.
     * If not it send a new one.
     */
    private static void setupBottomMessage() {
        bottomMessage = presentationChannel.entity.getLastMessage().block();
        if (bottomMessage == null || bottomMessage.getEmbeds().size() == 0 || bottomMessage.getEmbeds().get(0).getTitle().isEmpty() || !bottomMessage.getEmbeds().get(0).getTitle().get().equals("Créer une présentation ?"))
            sendLastMessage();
    }

    /**
     * Send the bottom message.
     */
    private static void sendLastMessage() {
        startAway(() -> bottomMessage = presentationChannel.entity.createMessage(presentationBottomMessage).block());
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

    @Override
    public void onStop() {

    }

}
