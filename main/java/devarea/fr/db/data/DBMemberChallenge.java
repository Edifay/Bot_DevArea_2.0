package devarea.fr.db.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.client.model.Updates;
import devarea.fr.db.DBManager;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public class DBMemberChallenge implements DBItem {

    protected String key;
    protected String _id;
    protected List<DBValidatedChallenge> challengesAccomplished;

    public DBMemberChallenge(final String _id, final String key) {
        this._id = _id;
        this.key = key;
        this.challengesAccomplished = new ArrayList<>();
    }


    public DBMemberChallenge(Document document) {
        this.key = (String) document.get("key");
        this._id = (String) document.get("_id");

        this.challengesAccomplished = new ArrayList<>(document.getList("challenges", Document.class)
            .stream()
            .map(doc -> new DBValidatedChallenge((String) doc.get("name"), (Long) doc.get("date")))
            .toList());
    }

    public String getId() {
        return _id;
    }

    public String getKey() {
        return key;
    }

    public List<String> getChallengesAccomplished() {
        return challengesAccomplished.stream().map(dbValidatedChallenge -> dbValidatedChallenge.name).toList();
    }

    public List<DBValidatedChallenge> getFullChallengesAccomplished() {
        return this.challengesAccomplished;
    }

    /**
     * @param challenge
     * @return true if the challenge was added, false if the challenge was already done by the member.
     */
    public boolean addAccomplishedChallenge(final String challenge) {
        if (this.getChallengesAccomplished().contains(challenge))
            return false;
        this.challengesAccomplished.add(new DBValidatedChallenge(challenge, System.currentTimeMillis()));

        DBManager.updateChallenge(this);
        return true;
    }


    @Override
    public Document toDocument() {
        return new Document()
            .append("key", this.key)
            .append("_id", this._id)
            .append("challenges", this.challengesAccomplished.stream().map(DBValidatedChallenge::toDocument).toList());
    }

    @Override
    public Bson toUpdates() {
        return Updates.combine(
            Updates.set("key", this.key),
            Updates.set("challenges", this.challengesAccomplished.stream().map(DBValidatedChallenge::toDocument).toList())
        );
    }

    public static class DBValidatedChallenge implements DBItem {
        @JsonProperty
        protected final String name;
        @JsonProperty
        protected final long date;

        public DBValidatedChallenge(final String name, final long date) {
            this.name = name;
            this.date = date;
        }

        public long getDate() {
            return date;
        }

        public String getName() {
            return name;
        }

        @Override
        public Document toDocument() {
            return new Document()
                .append("name", this.name)
                .append("date", this.date);
        }

        @Override
        public Bson toUpdates() {
            return Updates.combine(
                Updates.set("date", this.date),
                Updates.set("name", this.name)
            );
        }

    }

}
