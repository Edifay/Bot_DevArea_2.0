package devarea.fr.db;

import com.mongodb.Block;
import com.mongodb.client.*;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import devarea.fr.db.data.*;
import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.workers.linked.FreelanceWorker;
import devarea.fr.discord.workers.self.StatsWorker;
import devarea.fr.utils.Logger;
import devarea.fr.web.backend.entities.WebReseau;
import devarea.fr.web.backend.entities.WebStaff;
import org.bson.Document;

import javax.print.Doc;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

import static com.mongodb.client.model.Filters.or;

public class DBManager {

    private static final String MONGO_URL_PATH = "db.url";

    private static final String mongoURL;

    static {
        String mongoURLTemp;
        try {
            mongoURLTemp = new Scanner(new File(MONGO_URL_PATH)).nextLine();
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
    private static MongoCollection<Document> MISSIONS;
    private static MongoCollection<Document> MISSIONS_FOLLOW;
    private static MongoCollection<Document> MISSIONS_FOLLOW_CONFIG;
    private static MongoCollection<Document> FREELANCES;
    private static MongoCollection<Document> AUTH;
    private static MongoCollection<Document> XP_LEFT;
    private static MongoCollection<Document> STATS_CONFIG;
    private static MongoCollection<Document> RESEAUX;
    private static MongoCollection<Document> STAFF;

    public static void initDB() {

        Logger.logTitle("Connection to DEVAREA database.");

        try {
            mongoClient = MongoClients.create(mongoURL);
            DEVAREA_DB = mongoClient.getDatabase("DEVAREA");

            USERDATA = DEVAREA_DB.getCollection("USERDATA");
            MISSIONS = DEVAREA_DB.getCollection("MISSIONS");
            MISSIONS_FOLLOW = DEVAREA_DB.getCollection("MISSIONS_FOLLOW");
            MISSIONS_FOLLOW_CONFIG = DEVAREA_DB.getCollection("MISSIONS_FOLLOW_CONFIG");
            FREELANCES = DEVAREA_DB.getCollection("FREELANCES");
            AUTH = DEVAREA_DB.getCollection("AUTH");
            XP_LEFT = DEVAREA_DB.getCollection("XP_LEFT");
            STATS_CONFIG = DEVAREA_DB.getCollection("STATS_CONFIG");
            RESEAUX = DEVAREA_DB.getCollection("RESEAUX");
            STAFF = DEVAREA_DB.getCollection("STAFF");


            Logger.logMessage("Connection to DEVAREA database success.");
        } catch (Exception e) {
            e.printStackTrace();
            Logger.logError("Connection to DEVAREA database failed.");
        }

    }

    public static void memberJoin(final String id) {
        if (USERDATA.find(MemberAdapter.memberToDocument(id)).first() == null)
            USERDATA.insertOne(MemberAdapter.memberToDocument(id));
        transferXPLeftToXP(id);
    }

    public static void memberLeft(final String id) {
        transferXPToXPLeft(id);
        USERDATA.deleteOne(MemberAdapter.memberToDocument(id));
        AUTH.deleteOne(MemberAdapter.memberToDocument(id));
        MISSIONS.deleteMany(new Document("createdById", id));
        //MISSIONS_FOLLOW.deleteMany(or(new Document("dev_id", id), new Document("client_id", id)));
        FreelanceWorker.deleteFreelanceOf(id);
    }

    public static String getDescription(final String id) {
        return (String) USERDATA.find(MemberAdapter.memberToDocument(id)).projection(Projections.include("description")).first().get("description");
    }

    public static void setDescription(final String id, final String description) {
        USERDATA.updateOne(MemberAdapter.memberToDocument(id), Updates.set("description", description));
    }

    public static DBMessage getMessageDescription(final String id) {
        Document member = USERDATA.find(MemberAdapter.memberToDocument(id)).projection(Projections.include("description_message")).first();
        Document msg = (Document) member.get("description_message");
        return msg == null ? null : new DBMessage(msg);
    }

    public static void setMessageDescription(final String id, final DBMessage message) {
        USERDATA.updateOne(MemberAdapter.memberToDocument(id), Updates.set("description_message", message == null ? null : message.toDocument()));
    }

    public static int getXP(final String id) {
        Document document = USERDATA.find(MemberAdapter.memberToDocument(id)).projection(Projections.include("xp")).first();
        if (document == null || document.get("xp") == null)
            return 0;
        return (int) document.get("xp");
    }

    public static void setXP(final String id, final int xp) {
        USERDATA.updateOne(MemberAdapter.memberToDocument(id), Updates.set("xp", xp));
    }

    public static void incrementXP(final String id, final int incrementation) {
        USERDATA.updateOne(MemberAdapter.memberToDocument(id), Updates.combine(
                Updates.inc("xp", incrementation),
                Updates.inc("xp_history." + new SimpleDateFormat("dd-MM-yyyy").format(Date.from(Instant.now())), incrementation)
        ));
    }

    public static void removeXP(final String id, final int amount) {
        USERDATA.updateOne(MemberAdapter.memberToDocument(id), Updates.inc("xp", -amount));
    }

    public static FindIterable<Document> listOfXP() {
        return USERDATA.find().projection(Projections.include("_id", "xp")).sort(Sorts.descending("xp"));
    }


    public static HashMap<String, Integer> getXPHistory(final String id) {
        Document document = (Document) USERDATA.find(MemberAdapter.memberToDocument(id)).projection(Projections.include("xp_history")).first().get("xp_history");
        HashMap<String, Integer> xp_history = new HashMap<>();
        if (document != null)
            for (Map.Entry<String, Object> entrys : document.entrySet()) {
                xp_history.put(entrys.getKey(), (Integer) entrys.getValue());
            }
        return xp_history;
    }

    public static ArrayList<DBMission> getMissions() {
        FindIterable<Document> documents = MISSIONS.find().sort(Sorts.descending("lastUpdate"));
        ArrayList<DBMission> missions = new ArrayList<>();

        for (Document document : documents)
            missions.add(new DBMission(document));

        return missions;
    }

    public static ArrayList<DBMission> getMissionOf(final String createdById) {
        FindIterable<Document> document = MISSIONS.find(new Document("createdById", createdById));
        ArrayList<DBMission> missions = new ArrayList<>();

        for (Document doc : document) {
            missions.add(new DBMission(doc));
        }

        return missions;
    }

    public static DBMission getMissionFromUpdateMessage(final DBMessage message) {
        return new DBMission(MISSIONS.find(new Document().append("messageUpdate", message.toDocument())).first());
    }

    public static void deleteMission(final String id) {
        MISSIONS.deleteOne(new Document("_id", id));
    }

    public static void updateMission(final DBMission mission) {
        MISSIONS.updateOne(new Document("_id", mission.get_id()), mission.toUpdates());
    }

    public static void createMission(final DBMission mission) {
        MISSIONS.insertOne(mission.toDocument());
    }

    public static DBMission getMissionFromMessage(final DBMessage message) {
        return new DBMission(MISSIONS.find(new Document("message", message.toDocument())).first());
    }

    public static int currentMissionFollowCount() {
        return (int) MISSIONS_FOLLOW_CONFIG.find(new Document("_id", 0)).first().get("count");
    }

    public static void incrementMissionFollowCount() {
        MISSIONS_FOLLOW_CONFIG.updateOne(new Document("_id", 0), Updates.inc("count", 1));
    }

    public static DBMission getMission(final String _id) {
        Document mission = MISSIONS.find(new Document("_id", _id)).first();
        if (mission == null)
            return null;
        return new DBMission(mission);
    }


    public static ArrayList<DBMissionFollow> getMissionFollowOf(final String id) {
        FindIterable<Document> documents = MISSIONS_FOLLOW.find(or(new Document("client_id", id), new Document("dev_id", id)));
        ArrayList<DBMissionFollow> missions = new ArrayList<>();
        documents.forEach((Block<? super Document>) document -> missions.add(new DBMissionFollow(document)));
        return missions;
    }

    public static void createMissionFollow(final DBMissionFollow missionFollow) {
        MISSIONS_FOLLOW.insertOne(missionFollow.toDocument());
    }

    public static void deleteMissionFollow(final DBMissionFollow missionFollow) {
        MISSIONS_FOLLOW.deleteOne(missionFollow.toDocument());
    }

    public static DBMissionFollow getMissionFollowFromMessage(final String id) {
        Document doc = MISSIONS_FOLLOW.find(new Document("message.idMessage", id)).first();
        return doc == null ? null : new DBMissionFollow(doc);
    }

    public static DBMissionFollow getMissionFollowFromPerson(final String client_id, final String dev_id) {
        Document document = MISSIONS_FOLLOW.find(new Document("client_id", client_id).append("dev_id", dev_id)).first();
        if (document == null)
            return null;
        return new DBMissionFollow(document);
    }


    public static DBFreelance getFreelanceOf(final String id) {
        Document freelance = FREELANCES.find(new Document("_id", id)).first();
        if (freelance == null)
            return null;
        return new DBFreelance(freelance);
    }

    public static boolean hasFreelanceOf(final String id) {
        return null != getFreelanceOf(id);
    }

    public static void updateFreelance(final DBFreelance freelance) {
        if (freelance.getMember().db().hasFreelance())
            FREELANCES.updateOne(new Document("_id", freelance.get_id()), freelance.toUpdates());
        else
            FREELANCES.insertOne(freelance.toDocument());
    }

    public static void deleteFreelanceOf(final String id) {
        FREELANCES.deleteOne(new Document("_id", id));
    }

    public static ArrayList<DBFreelance> getFreelancesList() {
        FindIterable<Document> freelances = FREELANCES.find().sort(Sorts.descending("lastBump"));
        ArrayList<DBFreelance> dbFreelances = new ArrayList<>();
        freelances.forEach((Block<? super Document>) document -> {
            dbFreelances.add(new DBFreelance(document));
        });
        return dbFreelances;
    }

    public static void bumpFreelanceOf(final String id, DBMessage message) {
        FREELANCES.updateOne(new Document("_id", id), Updates.combine(
                Updates.set("message", message.toDocument()),
                Updates.set("lastBump", System.currentTimeMillis())
        ));
    }

    public static String getAuthCodeOf(final String id) {
        Document document = AUTH.find(new Document().append("_id", id)).first();
        if (document == null)
            return null;
        return (String) document.get("key");
    }

    public static void setAuthCodeOf(final String id, final String code) {
        AUTH.insertOne(new Document().append("_id", id).append("key", code));
    }

    public static void deleteAuthCodeOf(final String id) {
        AUTH.deleteOne(new Document().append("_id", id));
    }

    public static String getMemberOfCode(final String authCode) {
        return (String) AUTH.find(new Document().append("key", authCode)).first().get("_id");
    }

    public static void setXPLeft(final String id, final int xp) {
        if (XP_LEFT.find(new Document("_id", id)).first() == null)
            XP_LEFT.insertOne(new Document()
                    .append("_id", id)
                    .append("xp", xp));
        else
            XP_LEFT.updateOne(new Document("_id", id), Updates.set("xp", xp));
    }

    public static int getXPLeft(final String id) {
        Document document = XP_LEFT.find(new Document("_id", id)).first();
        if (document == null)
            return 0;
        return (int) document.get("xp");
    }

    public static void deleteXPLeft(final String id) {
        XP_LEFT.deleteOne(new Document("_id", id));
    }

    public static void transferXPLeftToXP(final String id) {
        int xp = getXPLeft(id);
        setXP(id, xp);
        deleteXPLeft(id);
        Logger.logMessage("XP for " + MemberCache.get(id).entity.getTag() + " was setted to " + xp + " xp.");
    }

    public static void transferXPToXPLeft(final String id) {
        int xp;
        if ((xp = getXP(id)) != 0)
            setXPLeft(id, xp);
    }

    public static FindIterable<Document> getSortedXPList() {
        return USERDATA.find().sort(Sorts.descending("xp")).limit(10).projection(Projections.include("_id"));
    }


    public static Iterator<Document> getUSERDATA() {
        return USERDATA.find().iterator();
    }

    public static StatsWorker.StatsConfig getStats() {
        return new StatsWorker.StatsConfig(STATS_CONFIG.find(new Document("_id", 0)).first());
    }

    public static ArrayList<WebReseau> getReseaux() {
        ArrayList<WebReseau> webReseaux = new ArrayList<>();

        RESEAUX.find().forEach((Block<? super Document>) document -> webReseaux.add(new WebReseau(document)));

        return webReseaux;
    }

    public static ArrayList<WebStaff> getStaffs() {
        ArrayList<WebStaff> webStaffs = new ArrayList<>();

        STAFF.find().forEach((Block<? super Document>) document -> webStaffs.add(new WebStaff(document)));

        return webStaffs;
    }

}