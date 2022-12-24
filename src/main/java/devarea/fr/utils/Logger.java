package devarea.fr.utils;

import devarea.fr.discord.statics.DefaultData;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import static devarea.fr.Main.developing;

public class Logger {

    static {
        initLogger();
    }

    private static final String separator = "----------------------------------------";
    private static final String enter = "\n";

    private static final DateFormat logDateFormat = new SimpleDateFormat("[HH:mm:ss-dd/MM/yy] ");

    public static void initLogger() {
        if (!developing) {
            DateFormat format = new SimpleDateFormat("MM-dd-yy_HH-mm");

            File file = new File(DefaultData.LOG_FOLDER + format.format(Date.from(Instant.now())) + ".log");
            if (!file.exists()) {
                try {
                    file.getParentFile().mkdirs();

                    PrintStream out = new PrintStream(file);
                    System.setOut(out);
                    System.setErr(out);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String now() {
        return logDateFormat.format(Date.from(Instant.now()));
    }

    public static void logTitle(final Object title) {
        String nowText = now();
        System.out.println(nowText + separator + enter + nowText + title);
    }

    public static void logMessage(final Object message) {
        System.out.println(now() + message);
    }

    public static void separate() {
        System.out.println(now() + separator);
    }

    public static void logError(final Object error) {
        System.out.println(now() + "[ERROR]" + separator + enter + "   -> " + error);
    }
}
