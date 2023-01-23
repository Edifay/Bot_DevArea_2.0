package devarea.fr.discord.workers.core;

import devarea.fr.discord.Core;
import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.workers.Worker;
import devarea.fr.utils.Logger;
import discord4j.core.object.entity.Member;

import static devarea.fr.utils.ThreadHandler.repeatEachMillis;
import static devarea.fr.utils.ThreadHandler.startAwayIn;

public class MemberCacheWorker implements Worker {

    @Override
    public void onStart() {
        Worker.super.onStart();
        startAwayIn(() -> repeatEachMillis(() -> {
            Logger.logTitle(this.getClass().getSimpleName() + " : Loading Members.");
            MemberCache.use(Core.devarea.getMembers().buffer().blockLast().toArray(new Member[0]));
            Logger.logMessage(MemberCache.cacheSize() + " members loaded.");
        }, 86400000), 86400000);
    }

    @Override
    public ActionEvent<?> setupEvent() {
        return null;
    }

    @Override
    public void onStop() {

    }
}
