package devarea.fr.discord.commands;


import devarea.fr.discord.entity.events_filler.SlashCommandFiller;
import discord4j.discordjson.json.ApplicationCommandRequest;

public abstract class SlashCommand {

    public abstract ApplicationCommandRequest definition();

    public abstract void play(final SlashCommandFiller filler);

    public Permissions permissions() {
        return null;
    }


}
