package devarea.fr.utils;

import devarea.fr.discord.Core;
import devarea.fr.discord.cache.ChannelCache;
import devarea.fr.discord.statics.DefaultData;
import discord4j.core.object.entity.channel.GuildMessageChannel;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import static devarea.fr.Main.developing;
import static devarea.fr.utils.ThreadHandler.repeatEachMillis;

public class Logger {

    private static final String separator = "----------------------------------------";
    private static final String enter = "\n";

    private static final DateFormat logDateFormat = new SimpleDateFormat("[HH:mm:ss-dd/MM/yy] ");

    private static Queue<String> logs = new LinkedList<>();

    public static void preInit() {
        LogPrinter out = new LogPrinter(System.out);
        System.setOut(out);
        System.setErr(out);
    }

    public static void initLogger() {
        if (!developing) {
            DateFormat format = new SimpleDateFormat("MM-dd-yy_HH-mm");

            File file = new File(DefaultData.LOG_FOLDER + format.format(Date.from(Instant.now())) + ".log");
            if (!file.exists()) {
                try {
                    file.getParentFile().mkdirs();

                    LogPrinter out = new LogPrinter(file);
                    System.setOut(out);
                    System.setErr(out);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        repeatEachMillis(() -> {
            if (Core.devarea != null) {

                StringBuilder message = new StringBuilder("```");

                while (!logs.isEmpty()) {
                    String current = logs.poll();

                    if (current.length() > 1994)
                        current = current.substring(0, 1994);

                    message.append(current).append("\n");

                    if (logs.isEmpty() || message.length() + logs.peek().length() >= 1994) {
                        ((GuildMessageChannel) ChannelCache.watch(Core.data.log_channel.asString()).entity)
                                .createMessage(message.append("```").toString()).subscribe(msg -> {
                                }, throwable -> {
                                });
                        message = new StringBuilder("```");
                    }
                }
            }
        }, 5000);
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
        System.out.println(now() + "[ERROR]" + separator + enter + "       -> " + error);
    }

    public static class LogPrinter extends PrintStream {

        public LogPrinter(OutputStream outputStream) {
            super(outputStream);
        }

        public LogPrinter(File file) throws FileNotFoundException {
            super(file);
        }

        @Override
        public void println(String x) {
            super.println(x);
            logs.add(x);
        }


        @Override
        public void println(Object x) {
            super.println(x);
            logs.add(x.toString());
        }

    }
}
