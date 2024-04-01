package devarea.fr.discord.workers.core;

import devarea.fr.db.DBManager;
import devarea.fr.discord.Core;
import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.events_filler.MemberJoinEventFiller;
import devarea.fr.discord.entities.events_filler.MemberLeaveEventFiller;
import devarea.fr.discord.entities.events_filler.ReadyEventFiller;
import devarea.fr.discord.workers.Worker;
import devarea.fr.utils.Logger;
import discord4j.common.util.Snowflake;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Iterator;

public class DBWorker implements Worker {
    @Override
    public ActionEvent<?> setupEvent() {
        return (ActionEvent<ReadyEventFiller>) filler -> {

            final Iterator<Document> iterator = DBManager.getUSERDATA();
            final ArrayList<String> ids = new ArrayList<>();

            while (iterator.hasNext()) {

                final String id = (String) iterator.next().get("_id");
                if (!MemberCache.cache().containsKey(id)) {
                    Logger.logMessage("Emitting " + id + " left !");
                    Core.executeGlobal(new MemberLeaveEventFiller(Snowflake.of(id)));
                } else
                    ids.add(id);

            }

            for (String id : MemberCache.cache().keySet()) {

                if (!ids.contains(id)) {
                    Logger.logMessage("Emitting " + id + " joined !");
                    Core.executeGlobal(new MemberJoinEventFiller(MemberCache.get(id)));
                }

            }

        };
    }

    @Override
    public void onStop() {

    }
}
