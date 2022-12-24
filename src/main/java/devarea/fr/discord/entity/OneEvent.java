package devarea.fr.discord.entity;

import java.util.concurrent.atomic.AtomicBoolean;

public interface OneEvent<T> {

    AtomicBoolean _persistent = new AtomicBoolean(false);
    AtomicBoolean used = new AtomicBoolean(false);
    AtomicBoolean terminalEvent = new AtomicBoolean(false);

    // default = false
    default OneEvent<T> persistent(final boolean persistent) {
        this._persistent.set(persistent);
        return this;
    }

    default boolean isPersistent() {
        return this._persistent.get();
    }

    default OneEvent<T> terminal(final boolean terminalEvent) {
        this.terminalEvent.set(terminalEvent);
        return this;
    }

    default boolean isTerminalEvent() {
        return this.terminalEvent.get();
    }

    default boolean execute(final Object event) {
        try {
            if (!this.used.get()) {
                this.used.set(!isPersistent());
                this.run((T) event);
                return true;
            }
        } catch (ClassCastException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    void run(final T event);

}
