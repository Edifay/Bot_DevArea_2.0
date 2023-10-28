package devarea.fr.web.challenges;

import devarea.fr.db.DBManager;
import devarea.fr.db.data.DBChallenge;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Session {

    protected String clientKey;
    protected long creationDate;
    protected Challenge currentChallenge;

    public Session(final String clientKey) {
        this.clientKey = clientKey;
        this.creationDate = System.currentTimeMillis();
        this.currentChallenge = null;
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
        DBChallenge challenge = DBManager.getChallengeForKey(clientKey);

        challenge.addAccomplishedChallenge(this.currentChallenge.name);

        this.currentChallenge = null;
    }


}
