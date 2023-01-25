package devarea.fr.web.backend.entities.userInfos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import devarea.fr.discord.badges.Badges;
import devarea.fr.discord.entities.Mem;
import devarea.fr.discord.workers.self.XPWorker;
import devarea.fr.web.backend.entities.WebFreelance;
import devarea.fr.web.backend.entities.WebMission;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class WebUserInfos {

    @JsonProperty
    protected String id;
    @JsonProperty
    protected String urlAvatar;
    @JsonProperty
    protected String name;
    @JsonProperty
    protected String tag;
    @JsonProperty
    protected String memberDescription;

    @JsonProperty
    protected int xp;
    @JsonProperty
    protected int rank;
    @JsonProperty
    protected int level;
    @JsonProperty
    protected int previous_xp_level;
    @JsonProperty
    protected int next_xp_level;

    @JsonProperty
    protected WebMission.WebMissionPreview[] missions_list;

    @JsonProperty
    protected WebFreelance freelance;

    @JsonProperty
    Badges[] badges;

    public WebUserInfos(Mem mem) {
        this.id = mem.getSId();
        this.urlAvatar = mem.entity.getAvatarUrl();
        this.name = mem.entity.getDisplayName();
        this.tag = mem.entity.getTag();
        this.memberDescription = mem.db().getDescription();

        this.xp = mem.db().getXP();
        this.rank = XPWorker.getRankOfMember(mem.getSId());
        this.level = XPWorker.getLevelForXp(this.xp);
        this.previous_xp_level = XPWorker.getAmountForLevel(this.level);
        this.next_xp_level = XPWorker.getAmountForLevel(this.level + 1);

        this.missions_list = WebMission.getWebMissionsPreview(mem.db().getMissions());

        this.freelance = WebFreelance.of(mem);

        this.badges = mem.getBadges();
    }

}
