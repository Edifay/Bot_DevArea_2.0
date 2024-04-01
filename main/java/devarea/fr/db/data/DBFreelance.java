package devarea.fr.db.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.client.model.Updates;
import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entities.Mem;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.stream.Collectors;

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

    public DBFreelance(final String _id) {
        this._id = _id;
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

    public Mem getMember() {
        return MemberCache.get(this._id);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFields(ArrayList<DBField> fields) {
        this.fields = fields;
    }

    public void setLastBump(long lastBump) {
        this.lastBump = lastBump;
    }

    public void setMessage(DBMessage message) {
        this.message = message;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static class DBField implements DBItem {

        @JsonProperty
        protected String title;
        @JsonProperty
        protected String description;
        @JsonProperty
        protected String prix;
        @JsonProperty
        protected String temps;
        @JsonProperty
        protected boolean inline;

        public DBField(final Document document) {
            this((String) document.get("title"), (String) document.get("description"), (String) document.get("prix"), (String) document.get("temps"), (boolean) document.get("inline"));
        }

        public DBField(final String title, final String description, final String prix, final String temps, final boolean inline) {
            this.title = title;
            this.description = description;
            this.prix = prix;
            this.temps = temps;
            this.inline = inline;
        }

        public String getTitle() {
            return title;
        }

        public boolean getInLine() {
            return this.inline;
        }

        public String getDescription() {
            return description;
        }

        public String getPrix() {
            return prix;
        }

        public String getTemps() {
            return temps;
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
                .append("fields", this.fields.stream().map(DBField::toDocument).collect(Collectors.toList()));
    }

    @Override
    public Bson toUpdates() {
        return Updates.combine(
                Updates.set("name", this.name),
                Updates.set("description", this.description),
                Updates.set("lastBump", this.lastBump),
                Updates.set("message", this.message.toDocument()),
                Updates.set("fields", this.fields.stream().map(DBField::toDocument).collect(Collectors.toList()))
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
