package devarea.fr.discord.workers.linked;

import devarea.fr.db.DBManager;
import devarea.fr.db.data.DBFreelance;
import devarea.fr.db.data.DBMessage;
import devarea.fr.discord.Core;
import devarea.fr.discord.cache.ChannelCache;
import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.statics.ColorsUsed;
import devarea.fr.discord.statics.DefaultData;
import devarea.fr.discord.workers.Worker;
import devarea.fr.utils.Logger;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.AllowedMentions;

import static devarea.fr.discord.statics.TextMessage.freelanceBottomMessage;
import static devarea.fr.utils.ThreadHandler.startAway;

public class FreelanceWorker implements Worker {


    private static final long TIME_BETWEEN_BUMP = 86400000L;

    private static GuildMessageChannel freelanceChannel;


    @Override
    public void onStart() {
        freelanceChannel = (GuildMessageChannel) ChannelCache.fetch(Core.data.freelance_channel.asString()).entity;

        if (freelanceChannel == null) {
            Logger.logError("Le channel freelance n'a pas pu être trouvé !");
            return;
        }

        setupLastMessage();

        Logger.logMessage("FreelanceWorker Created !");
    }

    @Override
    public ActionEvent<?> setupEvent() {
        return null;
    }

    @Override
    public void onStop() {

    }


    /**
     * Setup the bottomMessage
     */
    private static void setupLastMessage() {
        Message msg = freelanceChannel.getLastMessage().block();
        if (msg == null || msg.getEmbeds().size() == 0 || msg.getEmbeds().get(0).getTitle().isPresent() && !msg.getEmbeds().get(0).getTitle().get().equals("Proposez vos services !"))
            sendLastMessage();
    }

    /**
     * Resend BottomMessage to keep it at the bottom of the channel
     */
    public static void updateBottomMessage() {
        Message msg = freelanceChannel.getLastMessage().block();
        if (msg != null && msg.getEmbeds().size() == 1 && msg.getEmbeds().get(0).getTitle().get().equals("Proposez vos services !"))
            startAway(() -> msg.delete());
        sendLastMessage();
    }

    /**
     * Send a new BottomMessage
     */
    private static void sendLastMessage() {
        freelanceChannel.createMessage(freelanceBottomMessage).subscribe();
    }

    /**
     * Delete previous message of a freelance and send a new one. To push the message to the bot of the channel.
     *
     * @param id the id of the member to own the freelance
     * @return if the freelance could be bump or not.
     */
    public static boolean bumpFreeLance(String id) {

        if (!hasFreelance(id))
            return false;

        DBFreelance freeLance = FreelanceWorker.getFreelanceOf(id);

        if (canBumpFreelanceOf(id))
            return false;

        startAway(() -> {
            try {
                freeLance.getMessage().getMessage().delete();
            } catch (Exception ignored) {
            }
        });

        DBMessage newMessage = new DBMessage(sendFreelanceMessage(freeLance));
        DBManager.bumpFreelanceOf(id, newMessage);

        updateBottomMessage();

        return true;
    }

    /**
     * Send the message of the freelance
     *
     * @param freeLance the freelance to send the message
     */
    public static Message sendFreelanceMessage(final DBFreelance freeLance) {
        return ((GuildMessageChannel) ChannelCache.watch(Core.data.freelance_channel.asString()).entity)
                .createMessage(MessageCreateSpec.builder()
                        .content("**Freelance de <@" + freeLance.get_id() + "> :**")
                        .allowedMentions(AllowedMentions.suppressAll())
                        .addEmbed(getEmbedOf(freeLance))
                        .addComponent(ActionRow.of(Button.link(DefaultData.DOMAIN_NAME + "member-profile?member_id=" + freeLance.get_id() +
                                        "&open=1",
                                "devarea.fr")))
                        .build()).block();
    }


    // ------------------- UTILS -------------------

    /**
     * @param id the id of the member
     * @return true if the member have a freelance, false if not
     */
    public static boolean hasFreelance(String id) {
        return DBManager.getFreelanceOf(id) != null;
    }

    /**
     * @param id the id of the member
     * @return a DBFreelance from the id, or null if no freelance is bind
     */
    public static DBFreelance getFreelanceOf(String id) {
        return DBManager.getFreelanceOf(id);
    }

    /**
     * @param id the id of the member
     * @return true if the member can bump his freelance, false if not.
     */
    public static boolean canBumpFreelanceOf(final String id) {
        return System.currentTimeMillis() - DBManager.getFreelanceOf(id).getLastBump() < TIME_BETWEEN_BUMP;
    }

    /**
     * Create the EmbedCreatorSpec for the freelance give as param.
     *
     * @param freelance the DBFreelance to transform
     * @return the EmbedCreateSpec from the DBFreelance
     */
    public static EmbedCreateSpec getEmbedOf(final DBFreelance freelance) {

        EmbedCreateSpec.Builder builder = EmbedCreateSpec.builder();

        builder
                .author(freelance.getName(), null, MemberCache.get(freelance.get_id()).entity.getAvatarUrl())
                .description(freelance.getDescription())
                .color(ColorsUsed.same);

        for (int i = 0; i < freelance.getFields().size(); i++)
            builder.addField(freelance.getFields().get(i).getTitle(), freelance.getFields().get(i).getValue(), freelance.getFields().get(i).getInLine());

        builder.addField("Contact", "Pour contacter le freelancer voici son tag : " + MemberCache.get(freelance.get_id()).entity.getTag() + ", utilisez " +
                "directement sa mention : <@" + freelance.get_id() + ">", false);


        return builder.build();
    }

    /**
     * delete the freelance
     *
     * @param freelance the freelance at delete
     */
    public static void deleteFreelanceOf(final String id) {
        try {
            DBManager.getFreelanceOf(id).getMessage().getMessage().delete().subscribe();
        } catch (Exception ignored) {
        }
        DBManager.deleteFreelanceOf(id);
    }

}
