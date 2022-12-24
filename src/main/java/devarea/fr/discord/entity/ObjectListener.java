package devarea.fr.discord.entity;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Entity;

import java.util.ArrayList;

public class ObjectListener<T extends Entity> implements Entity {


    public T entity;
    protected final ArrayList<OneEvent<?>> listeners;

    protected ObjectListener(final T entity) {
        this.entity = entity;
        this.listeners = new ArrayList<>();
    }

    public void listen(final OneEvent<?> listener) {
        this.listeners.add(0, listener);
    }

    public void removeListener(final OneEvent<?> listener) {
        this.listeners.remove(listener);
    }

    public void execute(final Object event) {
        ArrayList<OneEvent<?>> atRemove = new ArrayList<>();
        for (OneEvent<?> oneEvent : listeners) {
            oneEvent.execute(event);
            if (!oneEvent.isPersistent())
                atRemove.add(oneEvent);
            if (oneEvent.isTerminalEvent())
                break;
        }
        atRemove.forEach(listeners::remove);
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
