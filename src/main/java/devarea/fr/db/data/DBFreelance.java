package devarea.fr.db.data;

import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;

public class DBFreelance implements DBItem {

    protected String name;
    protected String description;
    protected String _id;
    protected long lastBump;
    protected DBMessage message;
    protected ArrayList<DBField> fields;

    public DBFreelance(final Document document) {
        this.name = (String) document.get("name");
        this.description = (String) document.get("description");
        this._id = (String) document.get("_id");
        this.lastBump = (long) document.get("lastBump");
        this.message = new DBMessage((Document) document.get("message"));
        this.fields = new ArrayList<>();
        for (Document doc : (ArrayList<Document>) document.get("fields"))
            this.fields.add(new DBField(doc));
    }

    public String getDescription() {
        return description;
    }

    public String get_id() {
        return _id;
    }

    public ArrayList<DBField> getFields() {
        return fields;
    }

    public DBMessage getMessage() {
        return message;
    }

    public long getLastBump() {
        return lastBump;
    }

    public String getName() {
        return name;
    }


    public static class DBField implements DBItem {

        protected String title;
        protected String description;
        protected String prix;
        protected String temps;
        protected boolean inline;

        public DBField(final Document document) {
            this.title = (String) document.get("title");
            this.description = (String) document.get("description");
            this.prix = (String) document.get("prix");
            this.temps = (String) document.get("temps");
            this.inline = (boolean) document.get("inline");
        }

        public String getTitle() {
            return title;
        }

        public boolean getInLine() {
            return this.inline;
        }

        public String getValue() {
            return this.description +
                    ((!this.prix.equalsIgnoreCase("empty") || !this.temps.equalsIgnoreCase("empty")) ? "\n" : "")
                    + (this.prix.equalsIgnoreCase("empty") ? "" : ("\nPrix: " + this.prix))
                    + (this.temps.equalsIgnoreCase("empty") ? "" : ("\nTemps de retour: " + this.temps));
        }


        @Override
        public Document toDocument() {
            return new Document()
                    .append("title", this.title)
                    .append("description", this.description)
                    .append("prix", this.prix)
                    .append("temps", this.temps)
                    .append("inline", this.inline);
        }

        @Override
        public Bson toUpdates() {
            return Updates.combine(
                    Updates.set("title", title),
                    Updates.set("description", this.description),
                    Updates.set("prix", this.prix),
                    Updates.set("temps", temps),
                    Updates.set("inline", this.inline)
            );
        }

        @Override
        public String toString() {
            return "DBField{" +
                    "title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", prix='" + prix + '\'' +
                    ", temps='" + temps + '\'' +
                    ", inline=" + inline +
                    '}';
        }
    }


    @Override
    public Document toDocument() {
        return new Document()
                .append("name", this.name)
                .append("description", this.description)
                .append("_id", this._id)
                .append("lastBump", this.lastBump)
                .append("message", this.message.toDocument())
                .append("fields", this.fields.stream().map(DBField::toDocument));
    }

    @Override
    public Bson toUpdates() {
        return Updates.combine(
                Updates.set("name", this.name),
                Updates.set("description", this.description),
                Updates.set("lastBump", this.lastBump),
                Updates.set("message", this.message.toDocument()),
                Updates.set("fields", this.fields.stream().map(DBField::toDocument))
        );
    }

    @Override
    public String toString() {
        return "DBFreelance{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", _id='" + _id + '\'' +
                ", lastBump=" + lastBump +
                ", message=" + message +
                ", fields=" + fields +
                '}';
    }
}
