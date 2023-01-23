package devarea.fr.discord.workers.self;

import devarea.fr.discord.Core;
import devarea.fr.discord.cache.ChannelCache;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.Chan;
import devarea.fr.discord.entities.events_filler.MessageCreateInChannelFiller;
import devarea.fr.discord.workers.Worker;
import devarea.fr.utils.Logger;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.spec.StartThreadSpec;

public class ThreadCreatorWorker implements Worker {

    @Override
    public ActionEvent<?> setupEvent() {

        for (Snowflake channelId : Core.data.channelsThreadCreator) {
            Chan<? extends GuildMessageChannel> chan = ChannelCache.watch(channelId.asString());
            Logger.logMessage("Adding : " + chan.entity.getName() + " thread creator !");
            chan.listen((ActionEvent<MessageCreateInChannelFiller>) filler -> {
                Logger.logMessage("In channel !");
                if (filler.event.getMember().isEmpty())
                    return;
                int characterNumber = Math.min(filler.event.getMessage().getContent().length(), 94 - filler.event.getMember().get().getDisplayName().length());

                filler.event.getMessage().startThread(
                        StartThreadSpec.builder()
                                .name(
                                        filler.event.getMember().get().getDisplayName() + " - " +
                                                filler.event.getMessage().getContent().substring(0, characterNumber) +

                                                (characterNumber == (94 - filler.event.getMember().get().getDisplayName().length()) ? "..." : "")
                                )
                                .build()
                ).subscribe();

            }, true);
        }


        return null;
    }

    @Override
    public void onStop() {

    }
}
