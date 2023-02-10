package devarea.fr.discord.workers.self;

import devarea.fr.db.DBManager;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.events_filler.MemberLeaveEventFiller;
import devarea.fr.discord.workers.linked.FreelanceWorker;
import devarea.fr.discord.workers.linked.MissionFollowWorker;
import devarea.fr.discord.workers.linked.MissionWorker;
import devarea.fr.discord.workers.Worker;

public class MemberLeaveWorker implements Worker {

    @Override
    public ActionEvent<?> setupEvent() {
        return (ActionEvent<MemberLeaveEventFiller>) event -> {
            MissionWorker.clearThisMember(event.memberId.asString());
            MissionFollowWorker.clearThisMember(event.memberId.asString());
            DBManager.memberLeft(event.memberId.asString());
        };
    }

    @Override
    public void onStop() {

    }
}
