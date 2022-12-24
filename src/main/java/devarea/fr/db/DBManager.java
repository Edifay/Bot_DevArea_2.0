package devarea.fr.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import devarea.fr.db.data.MemberAdapter;
import devarea.fr.utils.Logger;
import org.bson.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DBManager {

    private static final String MONGOURL_PATH = "db.url";

    private static final String mongoURL;

    static {
        String mongoURLTemp;
        try {
            mongoURLTemp = new Scanner(new File(MONGOURL_PATH)).nextLine();
        } catch (FileNotFoundException e) {
            Logger.logError("db.url not found !");
            mongoURLTemp = "";
            e.printStackTrace();
            System.exit(0);
        }
        mongoURL = mongoURLTemp;
    }

    private static MongoClient mongoClient;

    private static MongoDatabase DEVAREA_DB;

    private static MongoCollection<Document> USERDATA;

    public static void initDB() {

        Logger.logTitle("Connection to DEVAREA database.");

        try {
            mongoClient = MongoClients.create(mongoURL);
            DEVAREA_DB = mongoClient.getDatabase("DEVAREA");
            USERDATA = DEVAREA_DB.getCollection("USERDATA");

            incrementXP("5145880440918448921", 20);
            final int xp = getXP("5145880440918448921");
            Logger.logMessage("XP of 5145880440918448921 : " + xp);

            Logger.logMessage("Connection to DEVAREA database success.");
        } catch (Exception e) {
            e.printStackTrace();
            Logger.logError("Connection to DEVAREA database failed.");
        }

    }

    public static int getXP(final String id) {
        return (int) USERDATA.find(MemberAdapter.memberToDocument(id)).projection(Projections.include("xp")).first().get("xp");
    }

    public static void setXP(final String id, final int xp) {
        USERDATA.updateOne(new Document("_id", id), Updates.set("xp", xp));
    }

    public static void incrementXP(final String id, final int incrementation) {
        USERDATA.updateOne(new Document("_id", id), Updates.inc("xp", incrementation));
    }

}
