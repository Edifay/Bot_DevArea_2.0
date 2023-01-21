package devarea.fr.discord.entity;


import devarea.fr.discord.commands.Context;
import devarea.fr.discord.entity.events_filler.Filler;

public class EventOwner<T extends Filler<?>> {

    boolean persistent;
    boolean terminalEvent;
    boolean used = false;
    ActionEvent<T> event;
    protected Context context;

    public EventOwner(final ActionEvent<T> event) {
        this(event, false);
    }

    public EventOwner(final ActionEvent<T> event, final boolean persistent) {
        this(event, persistent, false);
    }

    public EventOwner(final ActionEvent<T> event, final boolean persistent, final boolean terminalEvent) {
        this(event, persistent, terminalEvent, null);
    }

    public EventOwner(final ActionEvent<T> event, final boolean persistent, final boolean terminalEvent, final Context context) {
        this.event = event;
        this.persistent = persistent;
        this.terminalEvent = terminalEvent;
        this.context = context;
    }

    // default = false
    public EventOwner<T> persistent(final boolean persistent) {
        this.persistent = persistent;
        return this;
    }

    public boolean isPersistent() {
        return this.persistent;
    }

    public EventOwner<T> terminal(final boolean terminalEvent) {
        this.terminalEvent = terminalEvent;
        return this;
    }

    public boolean isTerminalEvent() {
        return this.terminalEvent;
    }

    public boolean execute(final Filler<?> event) {
        try {
            if (!this.used && (context == null || context.match(event.context()))) {
                this.event.run((T) event);
                this.used = !this.persistent;
                if (this.terminalEvent)
                    stopAfterThis();
                return true;
            }
        } catch (ClassCastException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void stopAfterThis() {
        Thread.currentThread().stop();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EventOwner<?> ob)
            return ob.event == event;

        return super.equals(obj);
    }
}
