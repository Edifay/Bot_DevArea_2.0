package devarea.fr;

import devarea.fr.db.DBManager;
import devarea.fr.discord.DevArea;

import java.io.FileNotFoundException;


public class Main {

    public static final boolean developing = false;

    public static void main(String[] args) {
        DBManager.initDB();

        try {
            DevArea.initDevArea();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        DevArea.client.onDisconnect().block();

    }

        /*
        Reflections reflections = new Reflections("devarea.fr");
        Set<Class<? extends CommandDefinition>> classes = reflections.getSubTypesOf(CommandDefinition.class);
        System.out.println(classes);
        */

}