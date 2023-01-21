package devarea.fr.db.data;

import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

public class DBMission implements DBItem {

    protected String _id;
    protected String title;
    protected String description;
    protected String budget;
    protected String deadLine;
    protected String language;
    protected String support;
    protected String difficulty;

    protected DBMessage message;
    protected long createdAt;
    protected String createdById;
    protected long lastUpdate;
    protected DBMessage messageUpdate;


    public DBMission(final Document document) {
        this._id = (String) document.get("_id");
        this.title = (String) document.get("title");
        this.description = (String) document.get("description");
        this.budget = (String) document.get("budget");
        this.deadLine = (String) document.get("deadline");
        this.language = (String) document.get("language");
        this.support = (String) document.get("support");
        this.difficulty = (String) document.get("difficulty");

        this.message = new DBMessage((Document) document.get("message"));
        this.createdAt = (long) document.get("createdAt");
        this.createdById = (String) document.get("createdById");
        this.lastUpdate = (long) document.get("lastUpdate");
        if (document.get("messageUpdate") != null)
            this.messageUpdate = new DBMessage((Document) document.get("messageUpdate"));
    }


    public String get_id() {
        return _id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getBudget() {
        return budget;
    }

    public String getDeadLine() {
        return deadLine;
    }

    public String getLanguage() {
        return language;
    }

    public String getSupport() {
        return support;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public DBMessage getMessage() {
        return message;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getCreatedById() {
        return createdById;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public DBMessage getMessageUpdate() {
        return messageUpdate;
    }


    public void setMessageUpdate(DBMessage messageUpdate) {
        this.messageUpdate = messageUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public Document toDocument() {
        return new Document()
                .append("_id", this._id)
                .append("title", this.title)
                .append("description", this.description)
                .append("budget", this.budget)
                .append("deadLine", this.deadLine)
                .append("language", this.language)
                .append("support", this.support)
                .append("difficulty", this.difficulty)
                .append("message", this.message.toDocument())
                .append("createdAt", this.createdAt)
                .append("createdById", this.createdById)
                .append("lastUpdate", this.lastUpdate)
                .append("messageUpdate", this.messageUpdate.toDocument());
    }

    @Override
    public Bson toUpdates() {
        return Updates.combine(
                Updates.set("title", this.title),
                Updates.set("description", this.description),
                Updates.set("budget", this.budget),
                Updates.set("deadLine", this.deadLine),
                Updates.set("language", this.language),
                Updates.set("support", this.support),
                Updates.set("difficulty", this.difficulty),
                Updates.set("message", this.message.toDocument()),
                Updates.set("createdAt", this.createdAt),
                Updates.set("createdById", this.createdById),
                Updates.set("lastUpdate", this.lastUpdate),
                this.messageUpdate == null ? Updates.unset("messageUpdate") :
                        Updates.set("messageUpdate", this.messageUpdate.toDocument())
        );
    }

    @Override
    public String toString() {
        return "DBMission{" +
                "_id='" + _id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", budget='" + budget + '\'' +
                ", deadLine='" + deadLine + '\'' +
                ", language='" + language + '\'' +
                ", support='" + support + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", message=" + message +
                ", createdAt=" + createdAt +
                ", createdById='" + createdById + '\'' +
                ", lastUpdate=" + lastUpdate +
                ", messageUpdate=" + messageUpdate +
                '}';
    }
}
