package devarea.fr.discord.workers;

import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.utils.Logger;

public interface Worker {

    default void onStart() {
        Logger.logMessage(this.getClass().getSimpleName() + " created !");
    }

    ActionEvent<?> setupEvent();

    void onStop();

}
