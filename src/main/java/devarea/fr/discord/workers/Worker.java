package devarea.fr.discord.workers;

import devarea.fr.discord.entity.ActionEvent;

public interface Worker {

    void onStart();

    ActionEvent<?> setupEvent();

    void onStop();

}
