package devarea.fr.web.backend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import devarea.fr.db.data.DBFreelance;
import devarea.fr.discord.entities.Mem;

import java.util.ArrayList;

public class WebFreelance {

    public String member_id;
    public String name;
    public String avatar_url;
    public String description;
    public ArrayList<DBFreelance.DBField> fields;

    public WebFreelance() {
    }

    public WebFreelance(Mem mem) {
        DBFreelance freelance = mem.db().getFreelance();

        this.member_id = freelance.get_id();
        this.name = freelance.getName();
        this.avatar_url = mem.entity.getAvatarUrl();

        this.description = freelance.getDescription();
        this.fields = freelance.getFields();
    }

    public static WebFreelance of(Mem mem) {
        if (mem.db().hasFreelance())
            return new WebFreelance(mem);
        return null;
    }

    public static class WebFreelancePreview {

        @JsonProperty
        public String member_id;
        @JsonProperty
        public String name;
        @JsonProperty
        public String avatar_url;
        @JsonProperty
        public String description;
        @JsonProperty
        public String[] abilities;

        private WebFreelancePreview(final Mem mem) {
            DBFreelance freelance = mem.db().getFreelance();

            this.member_id = freelance.get_id();
            this.name = freelance.getName();
            this.avatar_url = mem.entity.getAvatarUrl();

            this.description = freelance.getDescription().length() > 150 ? freelance.getDescription().substring(0,
                    150) :
                    freelance.getDescription();

            ArrayList<String> abilities = new ArrayList<>();
            for (DBFreelance.DBField field : freelance.getFields())
                if (!field.getTitle().equals("Contact") && !field.getTitle().equals("Liens"))
                    abilities.add(field.getTitle());
            this.abilities = abilities.toArray(String[]::new);
        }

        public static WebFreelancePreview of(Mem mem) {
            if (mem.db().hasFreelance())
                return new WebFreelancePreview(mem);
            return null;
        }
    }
}
