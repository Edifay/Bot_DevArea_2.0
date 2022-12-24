package devarea.fr.discord;

import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entity.OneEvent;
import devarea.fr.discord.setup.InitialData;
import devarea.fr.discord.workers.Worker;
import devarea.fr.utils.Logger;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.*;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.gateway.intent.IntentSet;
import org.reflections.Reflections;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;

import static devarea.fr.utils.ThreadHandler.startAway;

public class DevArea {

    public static GatewayDiscordClient client;
    public static InitialData data;
    public static Guild devarea;
    public static ArrayList<OneEvent<?>> globalListeners = new ArrayList<>();

    public static void initDevArea() throws FileNotFoundException {
        data = InitialData.loadInitialData();

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
        final int workersCount = setupWorkers();
        Logger.logTitle(workersCount + " workers loaded.");

        Logger.logTitle("Setup Events");
        setupDiscordEvent();
        Logger.logMessage("Events setup success.");

    }

    public static void setupDiscordEvent() {
        client.getEventDispatcher().on(ReadyEvent.class).subscribe(event -> startAway(Dispatcher::onReadyEvent));
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
    }

    public static int setupWorkers() {
        int count = 0;

        Reflections reflections = new Reflections("devarea.fr");
        Set<Class<? extends Worker>> classes = reflections.getSubTypesOf(Worker.class);

        for (Class<? extends Worker> workerClass : classes) {
            try {
                Worker worker = workerClass.getConstructor().newInstance();
                Logger.separate();
                worker.onStart();
                OneEvent<?> event;
                if ((event = worker.setupEvent()) != null)
                    globalListeners.add(event);

                count += 1;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        return count;
    }

    public static void executeGlobal(Object event) {
        for (OneEvent<?> oneEvent : globalListeners) {
            boolean started = oneEvent.persistent(true).execute(event);
            if (oneEvent.isTerminalEvent() && started)
                break;
        }
    }

}
