package devarea.fr.discord.workers.self;

import devarea.fr.discord.Core;
import devarea.fr.discord.cache.ChannelCache;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.Chan;
import devarea.fr.discord.entities.events_filler.MessageCreateInChannelFiller;
import devarea.fr.discord.statics.ColorsUsed;
import devarea.fr.discord.workers.Worker;
import discord4j.common.util.Snowflake;
import discord4j.common.util.TimestampFormat;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.spec.EmbedCreateSpec;

import java.time.Duration;
import java.time.Instant;

public class BumpWorker implements Worker {

    private static Chan<? extends GuildMessageChannel> bumpChannel;

    /**
     * 7200000ms -> 2hours
     */
    private static final long BUMP_DELAY = 7200000;


    private static Thread thread;
    private static Message botMessage;

    @Override
    public void onStart() {
        bumpChannel = ChannelCache.watch(Core.data.bump_channel.asString());

        if (bumpChannel == null)
            return;

        botMessage = bumpChannel.entity.getMessagesBefore(Snowflake.of(Instant.now()))
                .skipUntil(message -> message.getAuthor().isPresent()
                        && message.getAuthor().get().getId().equals(Core.client.getSelfId()))
                .blockFirst();

        checkBumpAvailable();

        Worker.super.onStart();
    }

    @Override
    public ActionEvent<?> setupEvent() {
        bumpChannel.listen((ActionEvent<MessageCreateInChannelFiller>) filler -> {

            if (filler.event.getMessage().getAuthor().isPresent()
                    && filler.event.getMessage().getAuthor().get().getId().equals(Core.data.disboard_bot))
                checkBumpAvailable();
        });

        return null;
    }

    @Override
    public void onStop() {

    }


    private synchronized static void sendBumpMessage(Instant instant) {
        boolean available = instant.isBefore(Instant.now());

        EmbedCreateSpec spec = available ?
                EmbedCreateSpec.builder()
                        .color(ColorsUsed.same)
                        .description("Le bump est disponible avec la commande `/bump`.")
                        .build() :
                EmbedCreateSpec.builder()
                        .color(ColorsUsed.same)
                        .description("Le bump est à nouveau disponible " + TimestampFormat.RELATIVE_TIME.format(instant) + ".")
                        .build();

        if (botMessage != null) {
            botMessage.delete().subscribe();
        }
        botMessage = bumpChannel.entity.createMessage(spec).block();

        waitUntilBumpAvailable(instant);
    }

    public static void checkBumpAvailable() {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
        sendBumpMessage(nextBump());
    }

    private static boolean isDisboardBump(Message message) {
        return message.getAuthor().isPresent()
                && message.getAuthor().get().getId().equals(Core.data.disboard_bot)
                && message.getInteraction().isPresent()
                && message.getInteraction().get().getName().equals("bump")
                || message.getTimestamp().isBefore(Instant.now().minusMillis(BUMP_DELAY));
    }

    private static Instant nextBump() {
        Message disboardMessage = latestDisboardMessage();
        long latestBump = BUMP_DELAY - (disboardMessage != null ?
                Duration.between(disboardMessage.getTimestamp(), Instant.now()).toMillis() : 0);
        return Instant.now().plusMillis(latestBump);
    }

    private synchronized static Message latestDisboardMessage() {
        if (bumpChannel == null) {
            return null;
        }
        Snowflake now = Snowflake.of(Instant.now());
        return bumpChannel.entity.getMessagesBefore(now)
                .skipUntil(BumpWorker::isDisboardBump)
                .blockFirst();
    }

    private static void waitUntilBumpAvailable(Instant instant) {
        long millis = Duration.between(Instant.now(), instant).toMillis();
        if (millis >= 0) {
            thread = new Thread(() -> {
                try {
                    Thread.sleep(millis);
                    sendBumpMessage(nextBump());
                } catch (InterruptedException ignored) {

                }
            });
            thread.start();
        }
    }
}
