package devarea.fr;

import devarea.fr.db.DBManager;
import devarea.fr.discord.DevArea;

import java.io.FileNotFoundException;


public class Main {

    public static final boolean developing = true;

    public static void main(String[] args) {
        /*try {
            DevArea.initDevArea();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/

        DBManager.initDB();

        //DevArea.client.onDisconnect().block();

        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

        /*
        Reflections reflections = new Reflections("devarea.fr");
        Set<Class<? extends CommandDefinition>> classes = reflections.getSubTypesOf(CommandDefinition.class);
        System.out.println(classes);
        */

}