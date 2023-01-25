package devarea.fr;

import devarea.fr.utils.Logger;
import devarea.fr.web.SpringBackend;
import devarea.fr.db.DBManager;
import devarea.fr.discord.Core;
import org.springframework.boot.SpringApplication;

import java.io.FileNotFoundException;


public class Main {

    public static final boolean developing = false;

    public static void main(String[] args) {
        Logger.initLogger();

        SpringApplication.run(SpringBackend.class);

        DBManager.initDB();

        try {
            Core.initCoreDiscordClient();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

       Core.client.onDisconnect().block();

    }

}