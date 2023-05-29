package devarea.fr.discord.workers.core;

import devarea.fr.discord.Core;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.events_filler.ReadyEventFiller;
import devarea.fr.discord.workers.Worker;
import devarea.fr.utils.Logger;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import discord4j.core.object.presence.Status;

public class ReadyWorker implements Worker {

    @Override
    public ActionEvent<?> setupEvent() {
        return (ActionEvent<ReadyEventFiller>) filler -> {
            Logger.logMessage("Dev'Area bot is ready !");
            Core.client.updatePresence(ClientPresence.of(Status.ONLINE,
                    ClientActivity.playing("/help | Dev'Area Server !"))).subscribe();
        };
    }

    @Override
    public void onStop() {

    }
}
