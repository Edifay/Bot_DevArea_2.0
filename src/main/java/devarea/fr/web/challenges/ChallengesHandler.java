package devarea.fr.web.challenges;


import devarea.fr.db.DBManager;
import devarea.fr.db.data.DBChallenge;
import devarea.fr.utils.Logger;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static devarea.fr.utils.ThreadHandler.repeatEachMillis;

public class ChallengesHandler {

    protected static Random random = new Random();

    protected static final HashMap<String, Challenge.ChallengeSkull> challenges = new HashMap<>();
    protected static HashMap<Integer, Session> sessions = new HashMap<>();


    public static void init() throws Exception {
        initChallenges();

        repeatEachMillis(() -> {
            ArrayList<Integer> atRemove = new ArrayList<>();
            long currentTime = System.currentTimeMillis();

            for (Map.Entry<Integer, Session> entry : sessions.entrySet())
                if (currentTime - entry.getValue().getCreationDate() > 300000)
                    atRemove.add(entry.getKey());

            for (Integer key : atRemove)
                sessions.remove(key);

        }, 300000 /*5 min*/);
    }

    public static SimplePacket createNewSession(final String clientKey, final Language lang) {
        int sessionId = random.nextInt() % 100000;
        while (sessions.containsKey(sessionId))
            sessionId = random.nextInt() % 100000;

        sessions.put(sessionId, new Session(clientKey, lang));

        return new SimplePacket(String.valueOf(sessionId));
    }

    public static List<String> getAccomplishedBy(final String key) {
        DBChallenge challenge = DBManager.getChallengeForKey(key);
        return challenge.getChallengesAccomplished();
    }

    public static void checkKey(final String key) throws IllegalAccessException {
        if (DBManager.getChallengeForKey(key) == null)
            throw new IllegalAccessException("ERROR ! This key doesn't exist !");
    }

    public static void checkSessionId(final int sessionId) throws IllegalAccessException {
        if (sessions.get(sessionId) == null)
            throw new IllegalAccessException("ERROR ! This session Id doesn't exit !");
    }

    /**
     * We assume that the {@code Class<? extends Challenge>} is always annoted by Challenge.Definition. By the assert in {@link devarea.fr.Main}
     *
     * @param sessionId
     * @param challenge
     * @return
     */
    public static SimplePacket startChallengeOn(final int sessionId, final String challenge) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (sessions.get(sessionId).hasAChallenge())
            return new SimplePacket("", "You are already in a challenge !");

        Challenge.ChallengeSkull skull;
        if ((skull = challenges.get(challenge)) == null) {
            return new SimplePacket("", "Le challenge'" + challenge + "' n'existe pas !");
        }

        if (!new HashSet<>(DBManager.getChallengeForKey(sessions.get(sessionId).clientKey).getChallengesAccomplished()).containsAll(Arrays.stream(skull.challengeNeeded).toList()))
            return new SimplePacket("", "Le challenge '" + challenge + "' sera accessible quand les challenges : " + Arrays.toString(skull.challengeNeeded) + " auront été validé !");

        return sessions.get(sessionId).startChallenge(challenge, skull.getConstructor());
    }

    public static SimplePacket executeOnChallenge(final int sessionId, String action, final SimplePacket receivedPacket) {
        Session session = sessions.get(sessionId);

        if (!session.hasAChallenge())
            return new SimplePacket("", "Vous n'avez pas actuellement de challenge actif !");

        if (action == null)
            action = session.getChallengeState();

        Challenge.ChallengeSkull.MethodSkull method;
        if ((method = challenges.get(session.currentChallenge.name).methods.get(action)) == null)
            return new SimplePacket("", "L'action '" + action + "' n'existe pas !");

        if (!action.equals(session.getChallengeState()) && !method.isFreeToUse())
            return new SimplePacket("", "L'action " + action + " n'est pas accessible !");

        try {
            return session.executeAction(method.getMethod(), receivedPacket);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return new SimplePacket("", "Une interne est survenue veuillez nous contacter !");
        }
    }


    private static void initChallenges() throws Exception {
        Logger.logTitle("Starting load challenges.");

        Reflections reflections = new Reflections(new ConfigurationBuilder().forPackages("devarea.fr.web.challenges.created"));
        Set<Class<? extends Challenge>> classes = reflections.getSubTypesOf(Challenge.class);
        Challenge.ChallengeDefinition challengeDefinition;

        for (Class<? extends Challenge> challengeClass : classes) {
            try {
                challengeDefinition = challengeClass.getAnnotation(Challenge.ChallengeDefinition.class);
            } catch (NullPointerException e) {
                Logger.logError("ERROR ! The challenge ' + challengeClass.getName() + ' need the Challenge.Definition Annotation.");
                throw new Exception("ERROR ! The challenge " + challengeClass.getName() + " need the Challenge.Definition Annotation.");
            }

            challenges.put(challengeDefinition.name(), Challenge.getSkull(challengeClass));
            Logger.logMessage("Challenge '" + challengeDefinition.name() + "' was loaded.");
        }

        Logger.logMessage("Load challenge completed.");
    }
}
