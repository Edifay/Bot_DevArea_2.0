package devarea.fr.discord.entity;

import devarea.fr.discord.commands.Context;
import devarea.fr.discord.entity.events_filler.Filler;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Entity;

import java.util.ArrayList;

import static devarea.fr.utils.ThreadHandler.startAway;

public class ObjectListener<T extends Entity> implements Entity {


    public T entity;
    protected final ArrayList<EventOwner<?>> listeners;
    protected Thread concurrentHandler = null;

    protected ObjectListener(final T entity) {
        this.entity = entity;
        this.listeners = new ArrayList<>();
    }

    public void listen(final ActionEvent<?> listener) {
        touchListenersSafely(() -> this.listeners.add(0, new EventOwner<>(listener)));
    }

    public void listen(final ActionEvent<?> listener, final boolean persistent) {
        touchListenersSafely(() -> this.listeners.add(0, new EventOwner<>(listener, persistent)));
    }

    public void listen(final ActionEvent<?> listener, final boolean persistent, final Context context) {
        touchListenersSafely(() -> this.listeners.add(0, new EventOwner<>(listener, persistent, false, context)));
    }

    public void listenDuring(final ActionEvent<?> listener, final boolean persistent, final long ms) {
        listenDuring(listener, persistent, ms, null);
    }

    public void listenDuring(final ActionEvent<?> listener, final boolean persistent, final long ms, final Context context) {
        touchListenersSafely(() -> {
            EventOwner<?> owner = new EventOwner<>(listener, persistent, false, context);
            this.listeners.add(0, owner);
            startAway(() -> {
                try {
                    Thread.sleep(ms);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    removeListener(owner);
                }
            });
        });
    }

    public void removeListener(final EventOwner<?> listener) {
        touchListenersSafely(() -> this.listeners.remove(listener));
    }

    public void execute(final Filler<?> event) {
        try {
            concurrentHandler = new Thread(() -> {

                ArrayList<EventOwner<?>> atRemove = new ArrayList<>();
                for (EventOwner<?> eventOwner : listeners)
                    if (eventOwner.execute(event)) {

                        if (!eventOwner.isPersistent())
                            atRemove.add(eventOwner);

                        if (eventOwner.isTerminalEvent())
                            break;
                    }

                atRemove.forEach(listeners::remove);

            });

            concurrentHandler.start();
            concurrentHandler.join();
            concurrentHandler = null;

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void touchListenersSafely(Runnable runnable) {
        startAway(() -> {
            try {
                if (concurrentHandler != null && concurrentHandler.isAlive())
                    concurrentHandler.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                runnable.run();
            }
        });
    }


    public void update(final T entity) {
        this.entity = entity;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Entity ent)
            return ent.getId().equals(this.entity.getId());
        return super.equals(obj);
    }


    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":{'entity':" + entity.getId().asString() + "}";
    }

    @Override
    public Snowflake getId() {
        return this.entity.getId();
    }

    @Override
    public GatewayDiscordClient getClient() {
        return this.entity.getClient();
    }
}
