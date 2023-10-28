package devarea.fr.web.challenges;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SimplePacket {

    @JsonProperty("data")
    protected String data;
    @JsonProperty("toShow")
    protected String toShow;

    public SimplePacket(final String data, final String toShow) {
        this.data = data;
        this.toShow = toShow;
    }

    public String getData() {
        return data;
    }

    public String getToShow() {
        return toShow;
    }
}
