package devarea.fr.web.backend.entities.userInfos;

import com.fasterxml.jackson.annotation.JsonInclude;
import devarea.fr.discord.entities.Mem;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebPrivateUserInfos extends WebUserInfos {

    public WebPrivateUserInfos(Mem mem) {
        super(mem);
    }
}
