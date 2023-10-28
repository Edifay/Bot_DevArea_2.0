package devarea.fr.web.backend.rest;

import devarea.fr.db.DBManager;
import devarea.fr.discord.entities.Mem;
import devarea.fr.discord.workers.self.AuthWorker;
import devarea.fr.utils.Logger;
import devarea.fr.web.backend.entities.userInfos.WebPrivateUserInfos;
import org.springframework.web.bind.annotation.*;

import static devarea.fr.web.SpringBackend.checkStatus;

@CrossOrigin()
@RestController
@RequestMapping("auth")
public class ControllerAuth {

    @GetMapping("get")
    public static WebPrivateUserInfos getUserInfo(@RequestParam(value = "code") final String code) {
        checkStatus();
        Mem mem = AuthWorker.getMemberOfCode(code);
        Logger.logMessage(mem.entity.getTag() + " retrieved his datas. (Site input)");
        return new WebPrivateUserInfos(mem);
    }

    @GetMapping("/delete-account")
    public static boolean removeBinding(@RequestParam(value = "code") final String code) {
        checkStatus();
        DBManager.deleteAuthCodeOf(DBManager.getMemberOfCode(code));
        return true;
    }
}
