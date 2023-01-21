package devarea.fr.discord.entity.events_filler;

import devarea.fr.discord.commands.Context;

public abstract class Filler<T> {

    public T event;

    public Filler(final T event) {
        this.event = event;
    }

    abstract public Context context();

}
