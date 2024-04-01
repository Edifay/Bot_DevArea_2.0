package devarea.fr.discord.entities;

import devarea.fr.discord.entities.events_filler.Filler;

public interface ActionEvent<T extends Filler<?>> {
    void run(final T filler);

}
