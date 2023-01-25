package devarea.fr.web.backend.rest;

import devarea.fr.db.DBManager;
import devarea.fr.db.data.DBMission;
import devarea.fr.discord.entities.Mem;
import devarea.fr.discord.workers.linked.MissionFollowWorker;
import devarea.fr.discord.workers.linked.MissionWorker;
import devarea.fr.discord.workers.self.AuthWorker;
import devarea.fr.web.backend.entities.WebMission;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@CrossOrigin()
@RestController
public class ControllerMissions {


    @GetMapping("missions/preview")
    public static WebMission.WebMissionPreview[] preview(@RequestParam(value = "start", defaultValue = "0") int start,
                                                         @RequestParam(value = "end", defaultValue =
                                                                 Integer.MAX_VALUE + "") int end) {
        final ArrayList<DBMission> missions = DBManager.getMissions();
        final int size = missions.size();

        if (start > end) end = start;
        if (end > size) end = size;
        if (start > size) start = size;

        return WebMission.getWebMissionsPreview(new ArrayList<>(missions.subList(start, end)));
    }

    @GetMapping("missions/get")
    public static WebMission getMission(@RequestParam(value = "id", required = true) String id) {
        DBMission mission;
        if ((mission = MissionWorker.getMissionBy_Id(id)) == null)
            return null;
        return new WebMission(mission);
    }

    @GetMapping("missions/took")
    public static String[] tookMission(@RequestParam(value = "missionID", required = true) String missionID,
                                       @RequestParam(value = "code", required = true) String code) {
        Mem mem;
        if ((mem = AuthWorker.getMemberOfCode(code)) == null)
            return new String[]{"wrong_code"};
        DBMission mission;
        if ((mission = MissionWorker.getMissionBy_Id(missionID)) == null)
            return new String[]{"Wrong mission id !"};

        return new String[]{MissionFollowWorker.webTookMission(mission, mem)};
    }

    @GetMapping("missions/delete")
    public static String[] delete(@RequestParam(value = "missionID") String id,
                                  @RequestParam(value = "code") String code) {

        Mem mem;
        if ((mem = AuthWorker.getMemberOfCode(code)) == null)
            return new String[]{"wrong_code"};
        DBMission mission;
        if ((mission = MissionWorker.getMissionBy_Id(id)) == null)
            return new String[]{"mission_not_found"};
        if (!mem.getSId().equals(mission.getCreatedById()))
            return new String[]{"You are not the mission owner !"};

        MissionWorker.deleteMission(mission.get_id());
        return new String[]{"deleted"};
    }

    @PostMapping("missions/create")
    public static boolean postMission(@RequestBody WebMission.ReceiveMission mission, @RequestParam(value = "code") String code) {

        Mem mem;
        if ((mem = AuthWorker.getMemberOfCode(code)) == null)
            return false;

        MissionWorker.MissionMapper mapper = MissionWorker.missionMapper();
        mapper
                .title(mission.title)
                .description(mission.description)
                .deadLine(mission.dateRetour)
                .language(mission.langage)
                .difficulty(mission.niveau)
                .budget(mission.budget)
                .support(mission.support);

        return MissionWorker.createMission(mapper, mem);
    }

}
