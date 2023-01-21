package devarea.fr.discord.workers;

import devarea.fr.db.DBManager;
import devarea.fr.discord.cache.ChannelCache;
import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.cache.RoleCache;
import devarea.fr.discord.cache.cached_entity.CachedRole;
import devarea.fr.discord.entity.ActionEvent;
import devarea.fr.discord.entity.events_filler.ReadyEventFiller;
import devarea.fr.utils.Logger;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.core.spec.VoiceChannelEditSpec;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;

import static devarea.fr.utils.ThreadHandler.repeatEachMillis;
import static devarea.fr.utils.ThreadHandler.startAwayIn;

public class StatsWorker implements Worker {

    public static void update() {
        StatsConfig config = DBManager.getStats();

        ((VoiceChannel) ChannelCache.watch(config.idMemberChannel).entity).edit(VoiceChannelEditSpec.builder().name("Members: " + MemberCache.cacheSize())
                .build()).subscribe();

        config.rolesToChannels.forEach((role, channel) ->
                ((VoiceChannel) ChannelCache.watch(channel).entity).edit(VoiceChannelEditSpec.builder().name(RoleCache.watch(role).getName() + ": " +
                        CachedRole.getRoleMemberCount(role)).build()).subscribe());
    }

    @Override
    public void onStart() {
        Logger.logMessage("StatsWorker Created !");
    }

    @Override
    public ActionEvent<?> setupEvent() {
        return (ActionEvent<ReadyEventFiller>) event -> {
            startAwayIn(() -> repeatEachMillis(StatsWorker::update, 600000), 600000);
        };
    }

    @Override
    public void onStop() {

    }


    public static class StatsConfig {
        protected String idMemberChannel;
        protected HashMap<String, String> rolesToChannels;

        public StatsConfig(final Document document) {
            this.idMemberChannel = document.getString("idMemberChannel");
            this.rolesToChannels = new HashMap<>();
            if (document.get("rolesToChannels") != null)
                for (Document doc : (ArrayList<Document>) document.get("rolesToChannels"))
                    this.rolesToChannels.put((String) doc.get("role"), (String) doc.get("channel"));
        }
    }

}
