package devarea.fr.db.data;

import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

public class DBMissionFollow implements DBItem {

    protected int n;
    protected DBMessage message;
    protected String client_id;
    protected String dev_id;

    public DBMissionFollow(final Document document) {
        this.n = (int) document.get("n");
        this.message = new DBMessage((Document) document.get("message"));
        this.client_id = (String) document.get("client_id");
        this.dev_id = (String) document.get("dev_id");
    }

    public DBMissionFollow(final int n, final DBMessage message, final String client_id, final String dev_id) {
        this.n = n;
        this.message = message;
        this.client_id = client_id;
        this.dev_id = dev_id;
    }

    public DBMessage getMessage() {
        return message;
    }

    public int getN() {
        return n;
    }

    public String getClientId() {
        return client_id;
    }

    public String getDevId() {
        return dev_id;
    }

    @Override
    public Document toDocument() {
        return new Document()
                .append("n", this.n)
                .append("message", this.message.toDocument())
                .append("client_id", this.client_id)
                .append("dev_id", this.dev_id);
    }

    @Override
    public Bson toUpdates() {
        return Updates.combine(
                Updates.set("n", this.n),
                Updates.set("message", this.message.toDocument()),
                Updates.set("client_id", this.client_id),
                Updates.set("dev_id", this.dev_id)
        );
    }
}
