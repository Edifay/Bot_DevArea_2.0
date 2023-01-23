package devarea.fr.discord.commands.slash;

import devarea.fr.db.data.DBMember;
import devarea.fr.discord.commands.SlashCommand;
import devarea.fr.discord.entities.events_filler.SlashCommandFiller;
import devarea.fr.discord.statics.ColorsUsed;
import devarea.fr.discord.workers.self.XPWorker;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;

import java.util.ArrayList;

public class LeaderBoard extends SlashCommand {
    @Override
    public ApplicationCommandRequest definition() {
        return ApplicationCommandRequest.builder()
                .name("leaderboard")
                .description("Affiche le tableau du top 5 du classement d'XP du serveur.")
                .build();
    }

    @Override
    public void play(SlashCommandFiller filler) {
        String text = "";
        ArrayList<DBMember> array = XPWorker.getLeaderBoard();
        for (int i = 0; i < array.size(); i++) {
            text += "`#" + (i + 1) + ":` <@" + array.get(i).getId() + ">: " + array.get(i).getXP() + "xp" +
                    ".\n";
        }
        text += "\n---------------------------------------------------------------\n\n";
        text += "`#" + XPWorker.getRankOfMember(filler.mem.getSId()) + ":` <@" + filler.mem.getSId() +
                ">: " + XPWorker.getXpOfMember(filler.mem.getSId()) + "xp.";
        text += "\n\nVous pouvez retrouver le leaderboard en entier sur le site web : https://devarea.fr/stats.";

        filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                .addEmbed(EmbedCreateSpec.builder()
                        .title("LeaderBoard !")
                        .color(ColorsUsed.same)
                        .description(text).build())
                .build()).subscribe();
    }
}
