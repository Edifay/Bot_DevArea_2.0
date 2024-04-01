package devarea.fr.web.challenges;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;

public abstract class Challenge {

    protected final Session session;

    protected final String name;

    protected String currentState = "default";

    public Challenge(final String name, final Session session) {
        this.name = name;
        this.session = session;
    }


    abstract public SimplePacket onLoad();

    protected void validate() {
        this.session.validate();
    }

    protected void fail(){
        this.session.fail();
    }

    public void setState(final String state) {
        this.currentState = state;
    }

    public String getState() {
        return currentState;
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ChallengeDefinition {
        String name();

        String[] requiredChallenge() default {};

    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface Controller {
        String name();

        boolean freeToUse();
    }


    public static ChallengeSkull getSkull(final Class<? extends Challenge> challengeClass) throws Exception {

        ChallengeDefinition challengeDefinition = challengeClass.getAnnotation(ChallengeDefinition.class);
        ChallengeSkull skull = new ChallengeSkull();
        skull.challengeNeeded = challengeDefinition.requiredChallenge();

        try {
            skull.constructor = challengeClass.getConstructor(String.class, Session.class);
        } catch (NoSuchMethodException e) {
            throw new Exception("ERROR ! The challenge " + challengeClass.getName() + " need the default super constructor : Challenge(String name, Session session).");
        }

        Controller controller;
        for (Method method : challengeClass.getMethods()) {
            controller = null;
            try {
                controller = method.getAnnotation(Controller.class);
            } catch (NullPointerException ignored) {
                // isn't an input method.
            }

            if (controller == null)
                continue;

            if (method.getReturnType() != SimplePacket.class)
                throw new Exception("Wrong return type for method \"" + method.getName() + "\" in \"" + challengeClass.getName() + "\". Need the SimplePacket return type.");
            if (method.getParameterCount() != 1 || method.getParameters()[0].getType() != SimplePacket.class)
                throw new Exception("Wrong args for \"" + method.getName() + "\" in \"" + challengeClass.getName() + "\". Need " + method.getName() + "(SimplePacket packet).");


            skull.methods.put(controller.name(), new ChallengeSkull.MethodSkull(method, controller.freeToUse()));
        }

        return skull;
    }

    public static class ChallengeSkull {
        protected Constructor<? extends Challenge> constructor = null;

        protected String[] challengeNeeded;
        protected HashMap<String, MethodSkull> methods = new HashMap<>();

        public Constructor<? extends Challenge> getConstructor() {
            return constructor;
        }

        public MethodSkull getMethod(final String name) {
            return methods.get(name);
        }

        public HashMap<String, MethodSkull> getMethods() {
            return methods;
        }

        public static class MethodSkull {
            protected Method method;
            protected boolean freeToUse;

            public MethodSkull(final Method method, final boolean freeToUse) {
                this.method = method;
                this.freeToUse = freeToUse;
            }

            public Method getMethod() {
                return method;
            }

            public boolean isFreeToUse() {
                return freeToUse;
            }
        }
    }
}
