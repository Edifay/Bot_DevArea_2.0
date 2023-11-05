package devarea.fr.web.backend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import devarea.fr.db.data.DBMemberChallenge;

public class WebValidatedChallenge {

    @JsonProperty
    protected final String name;
    @JsonProperty
    protected final long date;

    public WebValidatedChallenge(final String name, final long date) {
        this.name = name;
        this.date = date;
    }

    public static WebValidatedChallenge of(final DBMemberChallenge.DBValidatedChallenge challenge) {
        return new WebValidatedChallenge(challenge.getName(), challenge.getDate());
    }

    public long getDate() {
        return date;
    }
}
