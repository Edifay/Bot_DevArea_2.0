package devarea.fr.discord.workers;

import devarea.fr.db.DBManager;
import devarea.fr.discord.entity.ActionEvent;
import devarea.fr.discord.entity.events_filler.MemberLeaveEventFiller;
import devarea.fr.utils.Logger;

public class MemberLeaveWorker implements Worker {

    @Override
    public void onStart() {
        Logger.logMessage("MemberLeaveWorker Created !");
    }

    @Override
    public ActionEvent<?> setupEvent() {
        return (ActionEvent<MemberLeaveEventFiller>) event -> {
            DBManager.memberLeft(event.memberId.asString());
            MissionWorker.clearThisMember(event.memberId.asString());
            MissionFollowWorker.clearThisMember(event.memberId.asString());
        };
    }

    @Override
    public void onStop() {

    }
}
