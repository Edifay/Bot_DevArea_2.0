package devarea.fr.web.backend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import devarea.fr.db.data.DBMission;
import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entities.Mem;

import java.util.ArrayList;


public class WebMission {

    @JsonProperty
    protected String title;
    @JsonProperty
    protected String description;
    @JsonProperty
    protected String budget;
    @JsonProperty
    protected String deadLine;
    @JsonProperty
    protected String language;
    @JsonProperty
    protected String support;
    @JsonProperty
    protected String level;
    @JsonProperty
    protected String memberName;
    @JsonProperty
    protected String avatarURL;
    @JsonProperty
    protected String memberTag;
    @JsonProperty
    protected String lastUpdate;
    @JsonProperty
    protected String id;
    @JsonProperty
    protected String memberID;
    @JsonProperty
    protected long createdAt;

    public WebMission(DBMission mission) {
        this.title = mission.getTitle();
        this.description = mission.getDescription();
        this.budget = mission.getBudget();
        this.deadLine = mission.getDeadLine();
        this.language = mission.getLanguage();
        this.support = mission.getSupport();
        this.level = mission.getDifficulty();
        this.memberID = mission.getCreatedById();
        this.id = mission.get_id();
        this.createdAt = mission.getCreatedAt();

        Mem member = MemberCache.get(mission.getCreatedById());

        this.memberName = member.entity.getDisplayName();
        this.avatarURL = member.entity.getAvatarUrl();
        this.memberTag = member.entity.getTag();

        this.lastUpdate = "" + ((System.currentTimeMillis() - mission.getLastUpdate()) / 86400000);
        if (this.lastUpdate.equals("0"))
            this.lastUpdate = "1";

    }

    public WebMissionPreview toPreview() {
        return new WebMissionPreview(this);
    }

    public static class WebMissionPreview {
        @JsonProperty
        protected String title, id, lastUpdate, description, avatarURL, budget;

        public WebMissionPreview() {
        }

        public WebMissionPreview(final WebMission mission) {
            this.title = mission.title;
            this.id = mission.id;
            this.lastUpdate = mission.lastUpdate;
            this.description = mission.description.length() > 91 ? mission.description.substring(0, 91) :
                    mission.description;
            this.avatarURL = mission.avatarURL;
            this.budget = mission.budget;
        }
    }

    public static WebMission[] getWebMissions(final ArrayList<DBMission> dbMissions) {
        return dbMissions.stream().map(WebMission::new).toArray(WebMission[]::new);
    }

    public static WebMissionPreview[] getWebMissionsPreview(final ArrayList<DBMission> dbMissions) {
        return dbMissions.stream().map(mission -> new WebMission(mission).toPreview()).toArray(WebMissionPreview[]::new);
    }

    public static class ReceiveMission {

        @JsonProperty
        public String title;
        @JsonProperty
        public String description;
        @JsonProperty
        public String dateRetour;
        @JsonProperty
        public String langage;
        @JsonProperty
        public String support;
        @JsonProperty
        public String niveau;
        @JsonProperty
        public String budget;

    }

}
