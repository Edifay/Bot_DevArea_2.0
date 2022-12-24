package devarea.fr.discord.workers;

import devarea.fr.discord.entity.OneEvent;
import devarea.fr.discord.entity.events_filler.MemberLeaveEventFiller;
import devarea.fr.utils.Logger;

public class MemberLeaveWorker implements Worker {

    @Override
    public void onStart() {
        Logger.logMessage("MemberLeaveWorker Created !");
    }

    @Override
    public OneEvent<?> setupEvent() {
        return (OneEvent<MemberLeaveEventFiller>) event -> {
        };
    }

    @Override
    public void onStop() {

    }
}
