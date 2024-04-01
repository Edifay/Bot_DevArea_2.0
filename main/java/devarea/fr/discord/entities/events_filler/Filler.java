package devarea.fr.discord.entities.events_filler;

import devarea.fr.discord.entities.Context;

public abstract class Filler<T> {

    /**
     * The current event Object.
     */
    public T event;

    public Filler(final T event) {
        this.event = event;
    }

    /**
     * Transform the owned event to a context.
     *
     * @return the context of this event.
     */
    abstract public Context context();

}
