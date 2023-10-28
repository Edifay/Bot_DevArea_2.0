package devarea.fr.web.backend.rest;

import devarea.fr.db.DBManager;
import devarea.fr.db.data.DBFreelance;
import devarea.fr.discord.entities.Mem;
import devarea.fr.discord.workers.linked.FreelanceWorker;
import devarea.fr.discord.workers.self.AuthWorker;
import devarea.fr.web.backend.entities.WebFreelance;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

import static devarea.fr.web.SpringBackend.checkStatus;

@CrossOrigin()
@RestController
@RequestMapping("freelances")
public class ControllerFreelances {

    @GetMapping("preview")
    public static WebFreelance.WebFreelancePreview[] preview(@RequestParam(value = "start", defaultValue = "0") int start,
                                                             @RequestParam(value = "end", defaultValue = "5") int end) {
        checkStatus();

        ArrayList<DBFreelance> freelances = DBManager.getFreelancesList();

        if (start > end) {
            int temp = start;
            start = end;
            end = temp;
        }

        if (start < 0)
            start = 0;
        if (end > freelances.size())
            end = freelances.size();

        return freelances.subList(start, end)
                .stream()
                .map(dbFreelance -> WebFreelance.WebFreelancePreview.of(dbFreelance.getMember()))
                .toArray(WebFreelance.WebFreelancePreview[]::new);
    }


    @PostMapping("set")
    public static boolean setFreelance(@RequestBody() WebFreelance freelance,
                                       @RequestParam(value = "code") String code) {
        checkStatus();

        Mem mem;
        if ((mem = AuthWorker.getMemberOfCode(code)) == null)
            return false;

        FreelanceWorker.FreelanceMapper mapper = FreelanceWorker.freelanceMapper();

        mapper
                .name(freelance.name)
                .description(freelance.description);

        for (DBFreelance.DBField field : freelance.fields)
            mapper.addField(FreelanceWorker.FreelanceMapper.fieldMapper()
                    .title(field.getTitle())
                    .inline(field.getInLine())
                    .prix(field.getPrix())
                    .temps(field.getTemps())
                    .description(field.getDescription())
            );

        return FreelanceWorker.setFreelance(mapper, mem);
    }

    @GetMapping("delete")
    public static boolean deleteFreelance(@RequestParam(value = "code") String code) {
        checkStatus();
        FreelanceWorker.deleteFreelanceOf(AuthWorker.getIdOfCode(code));
        return true;
    }

    @GetMapping("bump")
    public static boolean bumpFreelance(@RequestParam(value = "code") String code) {
        checkStatus();
        return FreelanceWorker.bumpFreeLance(AuthWorker.getIdOfCode(code));
    }


}
