package devarea.fr.discord.workers;

import devarea.fr.db.DBManager;
import devarea.fr.discord.DevArea;
import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entity.ActionEvent;
import devarea.fr.discord.entity.events_filler.MemberJoinEventFiller;
import devarea.fr.discord.entity.events_filler.MemberLeaveEventFiller;
import devarea.fr.discord.entity.events_filler.ReadyEventFiller;
import devarea.fr.utils.Logger;
import discord4j.common.util.Snowflake;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Iterator;

public class ReadyWorker implements Worker {
    @Override
    public void onStart() {
        Logger.logMessage("ReadyWorker Created !");
    }

    @Override
    public ActionEvent<?> setupEvent() {
        return (ActionEvent<ReadyEventFiller>) event -> {

            final Iterator<Document> iterator = DBManager.getUSERDATA();
            final ArrayList<String> ids = new ArrayList<>();

            while (iterator.hasNext()) {

                final String id = (String) iterator.next().get("_id");
                if (!MemberCache.cache().containsKey(id))
                    DevArea.executeGlobal(new MemberLeaveEventFiller(Snowflake.of(id)));
                else
                    ids.add(id);

            }

            for (String id : MemberCache.cache().keySet()) {

                if (!ids.contains(id))
                    DevArea.executeGlobal(new MemberJoinEventFiller(MemberCache.get(id)));

            }

        };
    }

    @Override
    public void onStop() {

    }
}
