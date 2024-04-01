package devarea.fr.web.challenges;

import devarea.fr.db.DBManager;
import devarea.fr.db.data.DBMemberChallenge;
import devarea.fr.discord.workers.linked.ChallengeWorker;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Session {

    protected String clientKey;
    protected long creationDate;
    protected Challenge currentChallenge;

    protected Language lang;

    public Session(final String clientKey, final Language lang) {
        this.clientKey = clientKey;
        this.creationDate = System.currentTimeMillis();
        this.currentChallenge = null;
        this.lang = lang;
    }


    public SimplePacket startChallenge(final String name, Constructor<? extends Challenge> challengeConstructor) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        this.currentChallenge = challengeConstructor.newInstance(name, this);
        return this.currentChallenge.onLoad();
    }

    public boolean hasAChallenge() {
        return this.currentChallenge != null;
    }

    public String getChallengeState() {
        return this.currentChallenge.getState();
    }

    public SimplePacket executeAction(final Method method, final SimplePacket packet) throws InvocationTargetException, IllegalAccessException {
        return (SimplePacket) method.invoke(this.currentChallenge, packet);
    }

    protected void validate() {
        DBMemberChallenge challenge = DBManager.getChallengeForKey(clientKey);

        boolean added = challenge.addAccomplishedChallenge(this.currentChallenge.name);
        if (added)
            ChallengeWorker.sendMemberValidatedANewChallenge(challenge.getId(), this.currentChallenge.name);

        this.currentChallenge = null;
    }

    protected void fail() {
        this.currentChallenge = null;
    }

    public Language getLang() {
        return lang;
    }

    public long getCreationDate() {
        return creationDate;
    }
}
