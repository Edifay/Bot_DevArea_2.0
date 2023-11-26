package devarea.fr.web.challenges;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

public class SimplePacket {

    @JsonProperty("data")
    protected String data;
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    @JsonProperty("toShow")
    protected String toShow;


    public SimplePacket() {
        this("", "");
    }

    public SimplePacket(final String data) {
        this(data, "");
    }

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
