package devarea.fr.discord;

import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.EventOwner;
import devarea.fr.discord.entities.events_filler.Filler;
import devarea.fr.discord.setup.InitialData;
import devarea.fr.discord.workers.Worker;
import devarea.fr.utils.Logger;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import discord4j.core.event.domain.guild.MemberUpdateEvent;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.*;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.gateway.intent.IntentSet;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.Buffer;
import java.util.*;

import static devarea.fr.utils.ThreadHandler.startAway;

public class Core {

    public static final HashMap<String, BufferedImage> assetsImages = new HashMap<>();
    public static HashMap<String, BufferedImage> badgesImages = new HashMap<>();
    public static final ArrayList<String> mots = new ArrayList<>();

    public static GatewayDiscordClient client;
    public static InitialData data;
    public static Guild devarea;

    public static boolean initStatus = false;
    protected static final ArrayList<EventOwner<?>> globalListeners = new ArrayList<>();

    public static void initCoreDiscordClient() throws FileNotFoundException {
        long startEpoch = System.currentTimeMillis();

        data = InitialData.loadInitialData();

        Logger.logTitle("Loading assets.");
        assetsLoader();
        Logger.logMessage("Assets load success.");

        Logger.logTitle("Connecting to discord.");
        final String token = new Scanner(new FileInputStream("./token.token")).nextLine();
        client = discord4j.core.DiscordClient.create(token)
            .gateway()
            .setEnabledIntents(IntentSet.all())
            .login()
            .block();
        Logger.logMessage("Connection to discord success.");

        Logger.logTitle("Fetching Dev'Area Guild.");
        devarea = client.getGuildById(data.devarea).block();
        Logger.logMessage("Dev'Area Guild fetched.");

        Logger.logTitle("Loading Members.");
        MemberCache.use(devarea.getMembers().buffer().blockLast().toArray(new Member[0]));
        Logger.logMessage(MemberCache.cacheSize() + " members loaded.");

        Logger.logTitle("Setup Workers");

        final int workersCount =
            + setupWorkers("devarea.fr.discord.workers");
//            + setupWorkers("devarea.fr.discord.workers.linked")
//            + setupWorkers("devarea.fr.discord.workers.self");

        Logger.logTitle(workersCount + " workers loaded.");

        Logger.logTitle("Setup Events");
        setupDiscordEvent();
        Logger.logMessage("Events setup success.");

        initStatus = true;

        Logger.logTitle("Initialization end. Took " + ((System.currentTimeMillis() - startEpoch) / 1000f) + "s.");
    }

    private static void assetsLoader() {
        try {
            long ms = System.currentTimeMillis();

            for (Map.Entry<String, String> entry : data.assetsImages.entrySet())
                assetsImages.put(entry.getKey(), loadImageInPot(entry.getValue()));

            for (Map.Entry<String, String> entry : data.badgesImages.entrySet())
                badgesImages.put(entry.getKey(), loadImageInPot(entry.getValue()));

            new BufferedReader(new InputStreamReader(Core.class.getResource(data.pathsMots).openStream()))
                .lines().forEach(mots::add);

            Logger.logMessage("Assets took : " + (System.currentTimeMillis() - ms) + "ms to load !");
        } catch (Exception e) {
            e.printStackTrace();
            Logger.logError("Les fichiers assets n'ont pas pu être chargé !!");
        }
    }

    public static BufferedImage loadImageInPot(String path) throws IOException {
        return ImageIO.read(Core.class.getResource(path).openStream());
    }

    public static void setupDiscordEvent() {
        client.getEventDispatcher().on(ReadyEvent.class).subscribe(event -> startAway(() -> Dispatcher.onReadyEvent(event)));
        client.getEventDispatcher().on(ButtonInteractionEvent.class).subscribe(event -> startAway(() -> Dispatcher.onButtonInteractionEvent(event)));
        client.getEventDispatcher().on(MemberJoinEvent.class).subscribe(event -> startAway(() -> Dispatcher.onMemberJoinEvent(event)));
        client.getEventDispatcher().on(MemberLeaveEvent.class).subscribe(event -> startAway(() -> Dispatcher.onMemberLeaveEvent(event)));
        client.getEventDispatcher().on(MessageCreateEvent.class).subscribe(event -> startAway(() -> Dispatcher.onMessageCreateEvent(event)));
        client.getEventDispatcher().on(MessageDeleteEvent.class).subscribe(event -> startAway(() -> Dispatcher.onMessageDeleteEvent(event)));
        client.getEventDispatcher().on(MessageUpdateEvent.class).subscribe(event -> startAway(() -> Dispatcher.onMessageUpdateEvent(event)));
        client.getEventDispatcher().on(ReactionAddEvent.class).subscribe(event -> startAway(() -> Dispatcher.onReactionAddEvent(event)));
        client.getEventDispatcher().on(ReactionRemoveEvent.class).subscribe(event -> startAway(() -> Dispatcher.onReactionRemoveEvent(event)));
        client.getEventDispatcher().on(SelectMenuInteractionEvent.class).subscribe(event -> startAway(() -> Dispatcher.onSelectMenuInteractionEvent(event)));
        client.getEventDispatcher().on(VoiceStateUpdateEvent.class).subscribe(event -> startAway(() -> Dispatcher.onVoiceStateUpdateEvent(event)));
        client.getEventDispatcher().on(ChatInputInteractionEvent.class).subscribe(event -> startAway(() -> Dispatcher.onChatInputInteractionEvent(event)));
        client.getEventDispatcher().on(ModalSubmitInteractionEvent.class).subscribe(event -> startAway(() -> Dispatcher.onModalSubmitInteractionEvent(event)));
        client.getEventDispatcher().on(MemberUpdateEvent.class).subscribe(event -> startAway(() -> Dispatcher.onMemberUpdateEvent(event)));
    }

    public static int setupWorkers(final String path) {
        int count = 0;


        Reflections reflections = new Reflections(new ConfigurationBuilder().forPackages(path));
        Set<Class<? extends Worker>> classes = reflections.getSubTypesOf(Worker.class);

        for (Class<? extends Worker> workerClass : classes) {
            try {
                Worker worker = workerClass.getConstructor().newInstance();
                Logger.separate();
                worker.onStart();
                ActionEvent<?> event;
                if ((event = worker.setupEvent()) != null)
                    globalListeners.add(new EventOwner<>(event, true));

                count++;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        return count;
    }

    public static void executeGlobal(Filler<?> event) {
        for (EventOwner<?> eventOwner : globalListeners) {
            boolean started = eventOwner.persistent(true).execute(event);
            if (eventOwner.isTerminalEvent() && started)
                break;
        }
    }

    public static void listen(final ActionEvent<?> event) {
        listen(event, true);
    }

    public static void listen(final ActionEvent<?> event, final boolean persistent) {
        globalListeners.add(new EventOwner<>(event, persistent));
    }

    public static void listenFirst(final ActionEvent<?> event, final boolean persistent) {
        listenFirst(event, persistent, true);
    }

    public static void listenFirst(final ActionEvent<?> event, final boolean persistent, final boolean terminator) {
        globalListeners.add(0, new EventOwner<>(event, persistent, terminator));
    }


}
