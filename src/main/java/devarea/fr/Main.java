package devarea.fr;

import devarea.fr.db.DBManager;
import devarea.fr.discord.Core;

import java.io.FileNotFoundException;


public class Main {

    public static final boolean developing = false;

    public static void main(String[] args) {
        DBManager.initDB();

        try {
            Core.initCoreDiscordClient();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Core.client.onDisconnect().block();

    }

}