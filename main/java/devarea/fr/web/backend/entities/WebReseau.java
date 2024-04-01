package devarea.fr.web.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.Document;


public class WebReseau {

    @JsonProperty
    protected String name;
    @JsonProperty
    protected String url;
    @JsonProperty
    protected String description;
    @JsonProperty
    protected int index;

    public WebReseau() {

    }

    public WebReseau(final Document document) {
        this.name = (String) document.get("name");
        this.url = (String) document.get("url");
        this.description = (String) document.get("description");
        this.index = (int) document.get("index");
    }

    public WebReseau(final String name, final String url, final String description, final int index) {
        this.name = name;
        this.url = url;
        this.description = description;
        this.index = index;
    }

    @JsonIgnore
    public String getName() {
        return this.name;
    }

    @JsonIgnore
    public String getUrl() {
        return this.url;
    }

    @JsonIgnore
    public String getDescription() {
        return this.description;
    }

    @JsonIgnore
    public int getIndex() {
        return this.index;
    }

}
