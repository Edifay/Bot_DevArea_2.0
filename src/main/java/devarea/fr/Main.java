package devarea.fr;

import devarea.fr.db.data.DBAvis;
import devarea.fr.db.data.DBMember;
import devarea.fr.utils.Logger;
import devarea.fr.web.SpringBackend;
import devarea.fr.db.DBManager;
import devarea.fr.discord.Core;
import org.springframework.boot.SpringApplication;

import java.io.FileNotFoundException;


public class Main {

    public static final boolean developing = false;

    public static void main(String[] args) {
        Logger.preInit();

        SpringApplication.run(SpringBackend.class);

        Logger.initLogger();

        DBManager.initDB();

        try {
            Core.initCoreDiscordClient();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        DBMember member = new DBMember("321673326105985025");
        // member.addAvis(new DBAvis(3, DBAvis.Status.FREELANCE, "412625084654551050", "bien !!"));

        Core.client.onDisconnect().block();

    }

}