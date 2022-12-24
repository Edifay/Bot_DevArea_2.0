package devarea.fr.discord.workers;

import devarea.fr.discord.cache.ChannelCache;
import devarea.fr.discord.cache.cached_entity.CachedChannel;
import devarea.fr.discord.entity.OneEvent;
import devarea.fr.discord.entity.events_filler.MessageCreateInChannelFiller;
import discord4j.core.event.domain.message.MessageCreateEvent;

public class ChannelDispatcherWorker implements Worker {
    @Override
    public void onStart() {

    }

    @Override
    public OneEvent<?> setupEvent() {
        return (OneEvent<MessageCreateEvent>) event -> {
            if (ChannelCache.contain(event.getMessage().getChannelId().asString())) {
                ChannelCache.get(event.getMessage().getChannelId().asString()).execute(new MessageCreateInChannelFiller(event));
            }
        };
    }

    @Override
    public void onStop() {

    }
}
