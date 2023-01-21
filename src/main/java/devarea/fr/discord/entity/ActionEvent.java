package devarea.fr.discord.entity;

import devarea.fr.discord.entity.events_filler.Filler;

public interface ActionEvent<T extends Filler<?>> {
    void run(final T filler);

}
