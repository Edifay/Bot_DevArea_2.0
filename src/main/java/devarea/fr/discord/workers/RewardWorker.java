package devarea.fr.discord.workers;

import devarea.fr.discord.entity.ActionEvent;
import devarea.fr.utils.Logger;

import java.util.HashMap;

public class RewardWorker implements Worker {

    private static HashMap<String, String> cooldown = new HashMap<>();

    @Override
    public void onStart() {
        Logger.logMessage("RewardWorker Created !");
    }

    @Override
    public ActionEvent<?> setupEvent() {
        return null;
    }

    @Override
    public void onStop() {

    }
}
