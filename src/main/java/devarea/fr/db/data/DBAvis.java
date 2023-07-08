package devarea.fr.db.data;

import org.bson.Document;
import org.bson.conversions.Bson;

public class DBAvis implements DBItem {

    protected int grade;
    protected Status status;
    protected String id;
    protected String comment;

    public DBAvis(Document document) {
        this.grade = (int) document.get("grade");
        this.status = Status.getStatus((String) document.get("status"));
        this.id = (String) document.get("id");
        this.comment = (String) document.get("comment");
    }

    public DBAvis(final int grade, final Status status, final String id, final String comment) {
        this.grade = grade;
        this.status = status;
        this.id = id;
        this.comment = comment;
    }

    public int getGrade() {
        return grade;
    }

    public Status getStatus() {
        return status;
    }

    public String getComment() {
        return comment;
    }

    public String getId() {
        return id;
    }

    @Override
    public Document toDocument() {
        return new Document()
                .append("grade", grade)
                .append("status", status.value)
                .append("id", id)
                .append("comment", comment);
    }

    @Override
    public Bson toUpdates() {
        throw new RuntimeException("NOT IMPLEMENTED");
    }

    public enum Status {
        CLIENT("CLIENT"), FREELANCE("FREELANCE");

        public final String value;

        Status(final String value) {
            this.value = value;
        }

        public static Status getStatus(final String value) {
            return switch (value) {
                case "CLIENT", "C" -> CLIENT;
                case "FREELANCE", "F" -> FREELANCE;
                default -> throw new RuntimeException("PROBLEM CASTING STATUS");
            };
        }

    }
}
