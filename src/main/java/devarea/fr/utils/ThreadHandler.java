package devarea.fr.utils;

public class ThreadHandler {

    public static void startAway(final Runnable runnable) {
        new Thread(runnable).start();
    }
}
