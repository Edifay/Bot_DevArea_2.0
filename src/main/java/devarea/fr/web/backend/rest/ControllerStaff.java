package devarea.fr.web.backend.rest;

import devarea.fr.db.DBManager;
import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entities.Mem;
import devarea.fr.web.backend.entities.WebStaff;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;


@CrossOrigin()
@RestController
public class ControllerStaff {

    @GetMapping(value = "staff/staff_list", produces = MediaType.APPLICATION_JSON_VALUE)
    public static WebStaff[] getStaffList() {
        WebStaff[] staffs = DBManager.getStaffs().toArray(WebStaff[]::new);

        for (WebStaff staff : staffs) {
            Mem mem = MemberCache.get(staff.getId());
            if (mem == null)
                continue;
            staff.setName(mem.entity.getDisplayName());
            staff.setUrlAvatar(mem.entity.getAvatarUrl());
        }

        for (int i = 0; i < staffs.length; i++)
            staffs[i].setIdCss(i % 2f != 0f ? "pair" : "impair");

        return staffs;
    }

}
