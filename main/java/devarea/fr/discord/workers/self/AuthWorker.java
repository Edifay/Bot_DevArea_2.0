package devarea.fr.discord.workers.self;

import devarea.fr.db.DBManager;
import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.Mem;
import devarea.fr.discord.entities.events_filler.MemberLeaveEventFiller;
import devarea.fr.discord.workers.Worker;

import static devarea.fr.utils.PasswordGenerator.passwordGenerator;

public class AuthWorker implements Worker {

    public static String getCodeForMember(String id) {
        String code = DBManager.getAuthCodeOf(id);
        if (code != null)
            return code;
        return addNewAuthForUser(id);
    }

    public static Mem getMemberOfCode(final String code) {
        final String id = DBManager.getMemberOfCode(code);
        if (id == null)
            return null;
        return MemberCache.get(id);
    }

    public static String getIdOfCode(final String code) {
        return DBManager.getMemberOfCode(code);
    }

    private static String addNewAuthForUser(final String memberID) {
        final String code = passwordGenerator.generate(30);
        DBManager.setAuthCodeOf(memberID, code);
        return code;
    }

    @Override
    public ActionEvent<?> setupEvent() {
        return (ActionEvent<MemberLeaveEventFiller>) event -> {
            DBManager.deleteAuthCodeOf(event.memberId.asString());
        };
    }

    @Override
    public void onStop() {

    }
}
