package devarea.fr.web.backend.entities;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.Document;

public class WebStaff {

    @JsonProperty
    private String name;
    @JsonProperty
    private String bio;
    @JsonProperty
    private String urlAvatar;
    @JsonProperty
    private String idCss;
    @JsonProperty
    private String id;

    public WebStaff() {

    }

    public WebStaff(final Document document) {
        this.bio = (String) document.get("bio");
        this.id = (String) document.get("id");
    }

    public WebStaff(final String name, final String bio, final String id) {
        this.name = name;
        this.bio = bio;
        this.id = id;
    }

    public WebStaff(final String name, final String bio, final String id, final String url) {
        this.name = name;
        this.bio = bio;
        this.id = id;
        this.urlAvatar = url;
    }

    public String getId() {
        return id;
    }

    public void setUrlAvatar(String urlAvatar) {
        this.urlAvatar = urlAvatar;
    }

    public void setIdCss(String idCss) {
        this.idCss = idCss;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "WebStaff{" +
                "name='" + name + '\'' +
                ", bio='" + bio + '\'' +
                ", id='" + id + '\'' +
                ", urlAvatar='" + urlAvatar + '\'' +
                ", idCss='" + idCss + '\'' +
                '}';
    }

    @Override
    public WebStaff clone() {
        WebStaff cloned = new WebStaff();
        cloned.name = name;
        cloned.bio = bio;
        cloned.id = id;
        cloned.urlAvatar = urlAvatar;
        cloned.idCss = idCss;

        return cloned;
    }

}
