package devarea.fr.discord.workers;

import devarea.fr.discord.cache.ChannelCache;
import devarea.fr.discord.entity.ActionEvent;
import devarea.fr.discord.entity.events_filler.MessageCreateEventFiller;
import devarea.fr.discord.entity.events_filler.MessageCreateInChannelFiller;
import devarea.fr.utils.Logger;
import discord4j.core.event.domain.message.MessageCreateEvent;

public class ChannelDispatcherWorker implements Worker {
    @Override
    public void onStart() {
        Logger.logMessage("ChannelDispatcherWorker Created !");
    }

    @Override
    public ActionEvent<?> setupEvent() {
        return (ActionEvent<MessageCreateEventFiller>) filler -> {
            if (ChannelCache.contain(filler.event.getMessage().getChannelId().asString())) {
                //ChannelCache.get(event.getMessage().getChannelId().asString()).execute(new MessageCreateInChannelFiller(event));
            }
        };
    }

    @Override
    public void onStop() {

    }
}
