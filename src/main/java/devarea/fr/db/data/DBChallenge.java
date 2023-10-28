package devarea.fr.db.data;

import com.mongodb.client.model.Updates;
import devarea.fr.db.DBManager;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public class DBChallenge implements DBItem {

    protected String key;
    protected String _id;
    protected List<String> challengesAccomplished;

    public DBChallenge(final String _id, final String key) {
        this._id = _id;
        this.key = key;
        this.challengesAccomplished = new ArrayList<>();
    }


    public DBChallenge(Document document) {
        this.key = (String) document.get("key");
        this._id = (String) document.get("_id");
        this.challengesAccomplished = document.getList("challenges", String.class);
    }

    public String getId() {
        return _id;
    }

    public String getKey() {
        return key;
    }

    public List<String> getChallengesAccomplished() {
        return challengesAccomplished;
    }

    public void addAccomplishedChallenge(final String challenge) {
        if (this.challengesAccomplished.contains(challenge))
            return;
        this.challengesAccomplished.add(challenge);

        DBManager.updateChallenge(this);
    }

    @Override
    public Document toDocument() {
        return new Document()
            .append("key", this.key)
            .append("_id", this._id)
            .append("challenges", this.challengesAccomplished);
    }

    @Override
    public Bson toUpdates() {
        return Updates.combine(
            Updates.set("key", this.key),
            Updates.set("challenges", this.challengesAccomplished)
        );
    }
}
