package devarea.fr.web.backend.rest;

import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entities.Mem;
import devarea.fr.discord.workers.self.AuthWorker;
import devarea.fr.web.backend.entities.userInfos.WebPublicUserInfos;
import org.springframework.web.bind.annotation.*;


@CrossOrigin()
@RestController
public class ControllerUserData {

    @GetMapping("user-data/member-profile")
    public static WebPublicUserInfos getMemberProfile(@RequestParam(value = "member_id", required = true) String id) {
        return new WebPublicUserInfos(MemberCache.get(id));
    }

    @PostMapping("user-data/update-description")
    public static boolean updateUserDescription(@RequestBody(required = false) String description,
                                                @RequestParam(value = "code") String code) {
        Mem mem;
        if ((mem = AuthWorker.getMemberOfCode(code)) == null)
            return false;

        if (description != null && description.length() > 300)
            description = description.substring(0, 300);

        mem.db().setDescription(description);
        return true;
    }
}
