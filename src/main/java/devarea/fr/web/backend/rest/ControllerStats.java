package devarea.fr.web.backend.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devarea.fr.db.DBManager;
import devarea.fr.db.data.DBMember;
import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.cache.RoleCache;
import devarea.fr.discord.entities.Mem;
import devarea.fr.discord.workers.self.XPWorker;
import devarea.fr.web.backend.entities.WebRoleCount;
import devarea.fr.web.backend.entities.WebXPMember;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import org.bson.Document;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@CrossOrigin()
@RestController
public class ControllerStats {

    private final static ObjectMapper mapper = new ObjectMapper();

    @GetMapping(value = "stats/rolesCount_list", produces = MediaType.APPLICATION_JSON_VALUE)
    public static WebRoleCount[] rolesCounts_list(@RequestParam(value = "roles", defaultValue = "") String rolesString) {

        try {

            String[] rolesId = mapper.readValue("[" + rolesString + "]", new TypeReference<>() {
            });

            WebRoleCount[] roleCounts = new WebRoleCount[rolesId.length];

            for (int i = 0; i < rolesId.length; i++) {
                Role role = RoleCache.watch(rolesId[i]);
                if (role == null)
                    continue;

                roleCounts[i] = new WebRoleCount(RoleCache.count(rolesId[i]), role.getId().asString(), role.getName());
                roleCounts[i].setColor(String.format("#%06X", (0xFFFFFF & role.getColor().getRGB())));
            }

            return roleCounts;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    @GetMapping(value = "/stats/xp_list")
    public static WebXPMember[] xp_list(@RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "end", defaultValue = "50") int end) {

        if (start > end) {
            int temp = end;
            end = start;
            start = temp;
        }

        ArrayList<WebXPMember> xpList = new ArrayList<>();

        int current = 0;
        for (Document xp : DBManager.listOfXP()) {
            if (current >= start) {
                Mem mem = MemberCache.watch((String) xp.get("_id"));

                WebXPMember member = new WebXPMember(mem.getSId(), mem.db().getXP(), current);
                member.setName(mem.entity.getDisplayName());
                member.setUrlAvatar(mem.entity.getAvatarUrl());

                xpList.add(member);
            }
            current++;
            if (current >= end)
                break;
        }

        return xpList.toArray(WebXPMember[]::new);
    }

    @GetMapping(value = "/stats/member_count")
    public static int getMemberCount() {
        return MemberCache.cacheSize();
    }


}