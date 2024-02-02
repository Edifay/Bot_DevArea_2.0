package devarea.fr;

import devarea.fr.utils.Logger;
import devarea.fr.web.SpringBackend;
import devarea.fr.db.DBManager;
import devarea.fr.discord.Core;
import devarea.fr.web.challenges.ChallengesHandler;
import org.springframework.boot.SpringApplication;

import java.io.FileNotFoundException;


public class Main {

    public static final boolean developing = false;

    public static void main(String[] args) throws Exception {
        Logger.preInit();

        SpringApplication.run(SpringBackend.class);

        Logger.initLogger();

        ChallengesHandler.init();

        DBManager.initDB();

        try {
            Core.initCoreDiscordClient();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Core.client.onDisconnect().block();

    }

}