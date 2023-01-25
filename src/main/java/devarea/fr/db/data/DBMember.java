package devarea.fr.db.data;

import devarea.fr.db.DBManager;
import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entities.Mem;

import java.util.ArrayList;
import java.util.HashMap;

public class DBMember {

    protected final String id;

    public DBMember(final String id) {
        this.id = id;
    }

    public int getXP() {
        return DBManager.getXP(this.id);
    }

    public void setXP(final int xp) {
        DBManager.setXP(this.id, xp);
    }

    public void incrementXP(final int incrementation) {
        DBManager.incrementXP(this.id, incrementation);
    }

    public String getDescription() {
        return DBManager.getDescription(this.id);
    }

    public void setDescription(final String description) {
        DBManager.setDescription(this.id, description);
    }

    public HashMap<String, Integer> getXPHistory() {
        return DBManager.getXPHistory(this.id);
    }

    public ArrayList<DBMission> getMissions() {
        return DBManager.getMissionOf(this.id);
    }

    public DBFreelance getFreelance() {
        return DBManager.getFreelanceOf(this.id);
    }

    public boolean hasFreelance() {
        return DBManager.hasFreelanceOf(this.id);
    }

    public String getAuthCode() {
        return DBManager.getAuthCodeOf(this.id);
    }

    public Mem getMember() {
        return MemberCache.get(this.id);
    }

    public String getId() {
        return this.id;
    }

}
