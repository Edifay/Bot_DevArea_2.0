package devarea.fr.discord.workers.core;

import devarea.fr.discord.cache.ChannelCache;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.events_filler.MessageCreateEventFiller;
import devarea.fr.discord.entities.events_filler.MessageCreateInChannelFiller;
import devarea.fr.discord.workers.Worker;

/**
 * ChannelDispatcherWorker is linked to {@link MessageCreateEventFiller} event and dispatch a {@link MessageCreateInChannelFiller}
 * event to {@link devarea.fr.discord.entities.Chan} events.
 * <p>
 * The {@link MessageCreateInChannelFiller} listen on new message in a channel.
 * <p>
 * To listen this event, use {@link devarea.fr.discord.entities.Chan#listen(ActionEvent)};
 */
public class MessageChannelDispatcherWorker implements Worker {

    @Override
    public ActionEvent<?> setupEvent() {
        return (ActionEvent<MessageCreateEventFiller>) filler -> {
            if (ChannelCache.contain(filler.event.getMessage().getChannelId().asString())) {
                ChannelCache.get(filler.event.getMessage().getChannelId().asString()).execute(new MessageCreateInChannelFiller(filler.event));
            }
        };
    }

    @Override
    public void onStop() {

    }
}
