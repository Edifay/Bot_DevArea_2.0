package devarea.fr.discord.workers;

import devarea.fr.discord.entity.OneEvent;

public interface Worker {

    void onStart();

    OneEvent<?> setupEvent();

    void onStop();

}
