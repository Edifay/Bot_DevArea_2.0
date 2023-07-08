package devarea.fr.web.backend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import devarea.fr.db.data.DBAvis;
import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entities.Mem;

public class WebAvis {
    @JsonProperty
    protected String id;
    @JsonProperty
    protected String avatarUrl;
    @JsonProperty
    protected String name;
    @JsonProperty
    protected int grade;
    @JsonProperty
    protected String comment;
    @JsonProperty
    protected String status;

    public WebAvis(final DBAvis avis) {
        this.id = avis.getId();
        this.grade = avis.getGrade();
        this.comment = avis.getComment();
        this.status = avis.getStatus().value;
        Mem mem = MemberCache.get(id);
        if (mem != null) {
            this.avatarUrl = mem.entity.getAvatarUrl();
            this.name = mem.entity.getUsername();
        } else {
            this.avatarUrl = "/assets/images/reseaux/discord.png";
            this.name = "Left account.";
        }
    }


}
