package devarea.fr.utils;

public class ThreadHandler {

    public static void startAway(final Runnable runnable) {
        new Thread(runnable).start();
    }

    public static void repeatEachMillis(final Runnable runnable, final long millis) {
        startAway(()->{
            try {
                while (true) {
                    runnable.run();
                    Thread.sleep(millis);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void startAwayIn(final Runnable runnable, final long millis) {
        startAway(()->{
            try {
                Thread.sleep(millis);
            } catch (Exception ignored) {
            } finally {
                runnable.run();
            }
        });
    }
}
