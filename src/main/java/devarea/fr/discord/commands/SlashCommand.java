package devarea.fr.discord.commands;


import devarea.fr.discord.entities.events_filler.SlashCommandFiller;
import discord4j.discordjson.json.ApplicationCommandRequest;

public abstract class SlashCommand {
    /**
     * 600000ms = 10 min
     */
    protected static long SPOILED_TIME = 600000;

    /**
     * Method who's returning the SlashCommand Definition as <bold>ApplicationCommandRequest</bold>
     * Called at the start of the bot. To create all missions.
     *
     * @return the ApplicationCommandRequest
     */
    public abstract ApplicationCommandRequest definition();

    /**
     * The method called when a mission is executed. When a mission is executed by the SlashCommandWorker, he start
     * this method with passing SlashCommandFiller.
     *
     * @param filler the event who's start the command.
     */
    public abstract void play(final SlashCommandFiller filler);

    /**
     * Call before each command start. To check if the member have permission to execute the command.
     * A null return mean that this command no need permission.
     *
     * @return The permissions needed. null -> no permission needed.
     */
    public Permissions permissions() {
        return null;
    }


}
