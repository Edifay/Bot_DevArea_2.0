package devarea.fr.discord.workers.core;

import devarea.fr.discord.cache.ChannelCache;
import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.events_filler.MemberJoinVoiceEventFiller;
import devarea.fr.discord.entities.events_filler.MemberLeaveVoiceEventFiller;
import devarea.fr.discord.entities.events_filler.VoiceStateUpdateEventFiller;
import devarea.fr.discord.workers.Worker;
import devarea.fr.utils.Logger;

public class VoiceChannelDispatcherWorker implements Worker {
    @Override
    public ActionEvent<?> setupEvent() {
        return (ActionEvent<VoiceStateUpdateEventFiller>) filler -> {
            if (filler.event.isJoinEvent() || filler.event.isMoveEvent())
                ChannelCache.get(filler.event.getCurrent().getChannelId().get().asString()).execute(new MemberJoinVoiceEventFiller(filler.event.getCurrent()));
            if (filler.event.isLeaveEvent() || filler.event.isMoveEvent())
                ChannelCache.get(filler.event.getOld().get().getChannelId().get().asString()).execute(new MemberLeaveVoiceEventFiller(filler.event.getOld().get()));
        };
    }

    @Override
    public void onStop() {

    }
}
