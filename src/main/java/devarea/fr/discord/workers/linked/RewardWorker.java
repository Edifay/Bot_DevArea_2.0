package devarea.fr.discord.workers.linked;

import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.workers.Worker;

import java.util.HashMap;

public class RewardWorker implements Worker {

    private static HashMap<String, String> cooldown = new HashMap<>();

    @Override
    public ActionEvent<?> setupEvent() {
        return null;
    }

    @Override
    public void onStop() {

    }
}
