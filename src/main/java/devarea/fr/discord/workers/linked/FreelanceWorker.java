package devarea.fr.discord.workers.linked;

import devarea.fr.db.DBManager;
import devarea.fr.db.data.DBFreelance;
import devarea.fr.db.data.DBMessage;
import devarea.fr.discord.Core;
import devarea.fr.discord.cache.ChannelCache;
import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.Mem;
import devarea.fr.discord.statics.ColorsUsed;
import devarea.fr.discord.workers.Worker;
import devarea.fr.utils.Logger;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import discord4j.rest.util.AllowedMentions;

import java.util.ArrayList;

import static devarea.fr.discord.statics.DefaultData.DOMAIN_NAME;
import static devarea.fr.discord.statics.TextMessage.freelanceBottomMessage;
import static devarea.fr.utils.ThreadHandler.startAway;
import static devarea.fr.utils.ThreadHandler.startAwayIn;

public class FreelanceWorker implements Worker {


    private static final long TIME_BETWEEN_BUMP = 86400000L;

    private static GuildMessageChannel freelanceChannel;

    private static Message bottomMessage;

    /**
     * Cooldown for edit and send a new freelance !
     * Contain member Ids.
     */
    private static final ArrayList<String> coolDown = new ArrayList<>();


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
        try {
            bottomMessage = freelanceChannel.getLastMessage().block();
        } catch (Exception ignored) {
        }
        if (bottomMessage == null || bottomMessage.getEmbeds().size() == 0 || bottomMessage.getEmbeds().get(0).getTitle().isPresent() && !bottomMessage.getEmbeds().get(0).getTitle().get().equals("Proposez vos services !"))
            sendLastMessage();
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
     * Send a new BottomMessage
     */
    private static void sendLastMessage() {
        startAway(() -> bottomMessage = freelanceChannel.createMessage(freelanceBottomMessage).block());
    }

    /**
     * This function update, or create a freelance. Using the {@link FreelanceMapper} to pass the datas of the new freelance.
     * <p>
     * If the {@code mem} have already a freelance, it will update the message with modifications. And send update to the db.
     * If the {@code mem} don't have freelance, it will send a new message with the freelance embed, update the bottomMessage, and insert the freelance
     * for the member.
     *
     * @param mapper the mapper {@link FreelanceMapper} contains all the informations of an update, or a creation of a freelance.
     * @param mem    the member who's updating or creating the freelance.
     * @return if the update or send could be done. true if yes, false if not.
     */
    public static boolean setFreelance(FreelanceMapper mapper, Mem mem) {
        if (coolDown.contains(mem.getSId()))
            return false;

        DBFreelance freelance = mem.db().hasFreelance() ? mem.db().getFreelance() : new DBFreelance(mem.getSId());

        freelance.setName(mapper.name);
        freelance.setDescription(mapper.description);
        freelance.setLastBump(System.currentTimeMillis());

        ArrayList<DBFreelance.DBField> fields = new ArrayList<>();

        for (FreelanceMapper.FreelanceFieldMapper fieldMapper : mapper.fields)
            fields.add(new DBFreelance.DBField(fieldMapper.title, fieldMapper.description, fieldMapper.prix, fieldMapper.temps, fieldMapper.inline));
        freelance.setFields(fields);

        if (mem.db().hasFreelance()) {
            freelance.getMessage().getMessage().edit(MessageEditSpec.builder()
                    .addEmbed(getEmbedOf(freelance))
                    .build()).subscribe();
            Logger.logMessage(mem.entity.getTag() + " updated his freelance !");
        } else {
            Message message = sendFreelanceMessage(freelance);
            freelance.setMessage(new DBMessage(message));
            updateBottomMessage();
            Logger.logMessage(mem.entity.getTag() + " created his freelance !");
        }

        coolDown.add(mem.getSId());
        startAwayIn(() -> coolDown.remove(mem.getSId()), 5000);

        DBManager.updateFreelance(freelance);

        return true;
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

        freeLance.getMessage().getMessage().delete().subscribe(unused -> {
        }, throwable -> {
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
                        .addComponent(ActionRow.of(Button.link(DOMAIN_NAME + "member-profile?member_id=" + freeLance.get_id() +
                                        "&open=1",
                                "devarea.fr")))
                        .build()).block();
    }


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
     * @param id the freelance at delete
     */
    public static void deleteFreelanceOf(final String id) {
        try {
            DBManager.getFreelanceOf(id).getMessage().getMessage().delete().subscribe();
        } catch (Exception ignored) {
        }
        DBManager.deleteFreelanceOf(id);
    }

    public static FreelanceMapper freelanceMapper() {
        return new FreelanceMapper();
    }

    /**
     * An object who contain the data of a freelance. Only the data, {@link DBMessage} is not saved here for example.
     * To use this classes call {@link #freelanceMapper()}, and use dynamically the methods, like a builder.
     * <p></p>
     * This class contains the {@link FreelanceFieldMapper}, it can be used with calling {@link #fieldMapper()}.
     */
    public static class FreelanceMapper {

        protected String name;
        protected String description;
        protected ArrayList<FreelanceFieldMapper> fields = new ArrayList<>();

        private FreelanceMapper() {

        }

        public FreelanceMapper name(final String name) {
            this.name = name;
            return this;
        }

        public FreelanceMapper description(final String description) {
            this.description = description;
            return this;
        }

        public FreelanceMapper addField(final FreelanceFieldMapper mapper) {
            this.fields.add(mapper);
            return this;
        }

        public static FreelanceFieldMapper fieldMapper() {
            return new FreelanceFieldMapper();
        }

        /**
         * Refer at the {@link FreelanceMapper} documentation.
         */
        public static class FreelanceFieldMapper {
            protected String title, description, prix, temps;
            protected boolean inline;

            public FreelanceFieldMapper title(final String title) {
                this.title = title;
                return this;
            }

            public FreelanceFieldMapper description(final String description) {
                this.description = description;
                return this;
            }

            public FreelanceFieldMapper prix(final String prix) {
                this.prix = prix;
                return this;
            }

            public FreelanceFieldMapper temps(final String temps) {
                this.temps = temps;
                return this;
            }

            public FreelanceFieldMapper inline(final boolean inline) {
                this.inline = inline;
                return this;
            }
        }
    }
}
