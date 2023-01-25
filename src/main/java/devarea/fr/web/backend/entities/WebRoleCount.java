package devarea.fr.web.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WebRoleCount {

    @JsonIgnore
    protected String roleId;
    @JsonProperty
    protected String name;
    @JsonProperty
    protected int countMember;
    @JsonProperty
    protected String color;

    public WebRoleCount() {
    }

    public WebRoleCount(final int countMember, final String roleId, final String name) {
        this();
        this.roleId = roleId;
        this.countMember = countMember;
        this.name = name;
    }

    public WebRoleCount(final String roleId) {
        this.roleId = roleId;
    }

    @JsonIgnore
    public int getCountMember() {
        return countMember;
    }

    @JsonIgnore
    public String getRoleId() {
        return roleId;
    }

    @JsonIgnore
    public String getName() {
        return name;
    }

    @JsonIgnore
    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @JsonIgnore
    public void setCountMember(int countMember) {
        this.countMember = countMember;
    }

    @JsonIgnore
    public void setColor(String color) {
        this.color = color;
    }

    @JsonIgnore
    public String getColor() {
        return color;
    }
}
