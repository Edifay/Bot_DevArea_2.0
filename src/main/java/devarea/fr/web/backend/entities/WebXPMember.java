package devarea.fr.web.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import devarea.fr.discord.workers.self.XPWorker;

public class WebXPMember {

    @JsonProperty
    protected String id;
    @JsonProperty
    protected String name;
    @JsonProperty
    protected int xp;
    @JsonProperty
    protected int rank;
    @JsonProperty
    protected int level;
    @JsonProperty
    protected String urlAvatar;

    public WebXPMember(final String id, final int xp, final int rank) {
        this.id = id;
        this.xp = xp;
        this.rank = rank;
        this.level = XPWorker.getLevelForXp(xp);
    }

    @JsonIgnore
    public int getXp() {
        return xp;
    }

    @JsonIgnore
    public String getName() {
        return name;
    }

    @JsonIgnore
    public String getId() {
        return id;
    }

    @JsonIgnore
    public void setXp(int xp) {
        this.xp = xp;
    }

    @JsonIgnore
    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore
    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public void setRank(int rank) {
        this.rank = rank;
    }

    @JsonIgnore
    public void setUrlAvatar(String urlAvatar) {
        this.urlAvatar = urlAvatar;
    }

    @JsonIgnore
    public int getRank() {
        return rank;
    }

    @JsonIgnore
    public String getUrlAvatar() {
        return urlAvatar;
    }

    @JsonIgnore
    public int getLevel() {
        return level;
    }

    @JsonIgnore
    public void setLevel(int level) {
        this.level = level;
    }

}
