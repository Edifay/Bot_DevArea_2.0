package devarea.fr.web.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import devarea.fr.discord.entities.Mem;

public class WebValidatedChallengeCard {

    @JsonProperty
    protected WebValidatedChallenge challenge;

    @JsonProperty
    protected String name;
    @JsonProperty
    protected String memberId;
    @JsonProperty
    protected String avatarUrl;


    public WebValidatedChallengeCard(final Mem mem, final WebValidatedChallenge challenge) {
        this.challenge = challenge;

        this.name = mem.entity.getDisplayName();
        this.memberId = mem.getSId();
        this.avatarUrl = mem.entity.getAvatarUrl();
    }

    @JsonIgnore
    public long getDate() {
        return this.challenge.getDate();
    }
}
