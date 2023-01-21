package devarea.fr.discord.commands;


import devarea.fr.discord.entity.events_filler.SlashCommandFiller;
import discord4j.discordjson.json.ApplicationCommandRequest;

public abstract class SlashCommand {
    /**
     * 600000ms = 10 min
     */
    protected static long SPOILED_TIME = 600000;

    public abstract ApplicationCommandRequest definition();

    public abstract void play(final SlashCommandFiller filler);

    public Permissions permissions() {
        return null;
    }


}
