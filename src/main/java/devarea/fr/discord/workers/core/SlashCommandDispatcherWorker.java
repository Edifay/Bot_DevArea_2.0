package devarea.fr.discord.workers.core;

import devarea.fr.discord.Core;
import devarea.fr.discord.commands.SlashCommand;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.events_filler.SlashCommandFiller;
import devarea.fr.discord.workers.Worker;
import devarea.fr.utils.Logger;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


/**
 * SlashCommandDispatcherWorker handle {@link SlashCommandFiller}, and dispatch to the classes who inherit {@link SlashCommand}.
 * <p></p>
 * To listen this event, create a new class extending {@link SlashCommand}, and complete the {@link SlashCommand#definition()},
 * the discord slashCommand will be automatically linked with the {@link SlashCommandFiller} event.<p></p>
 * {@link SlashCommand#definition()} is called one time in the {@link SlashCommandDispatcherWorker#onStart()}.
 * <p></p>
 * When the command is executed by a member, a new instance of the linked {@link SlashCommand} is created. {@link SlashCommand#permissions()} is called to
 * check if the member have permission to play this command.
 * <p></p> If the member do not have the permission an error message is send. Else if the member have the permission required.
 * {@link SlashCommand#play(SlashCommandFiller)} is called and the command can start.
 */
public class SlashCommandDispatcherWorker implements Worker {

    public static HashMap<String, Constructor<SlashCommand>> slashCommands = new HashMap<>();

    /**
     * The init find all classes who inherit {@link SlashCommand} and check their {@link SlashCommand#definition()}, using {@link Reflections}.
     * <br>
     * After loading saving the constructor of each SlashCommand link to their command name in {@link #slashCommands}.
     * <br>
     * And update commands definition to the discord Client.
     */
    @Override
    public void onStart() {

        Logger.logMessage("SlashCommandDispatcherWorker starting.");
        Logger.logMessage("Loading SlashCommand.");

        Reflections reflections = new Reflections(new ConfigurationBuilder().forPackages("devarea.fr.discord.commands.slash"));
        Set<Class<? extends SlashCommand>> classes = reflections.getSubTypesOf(SlashCommand.class);
        Logger.logMessage(classes.size() + " SlashCommand found.");

        Logger.logMessage("Extracting data from SlashCommands.");

        ArrayList<ApplicationCommandRequest> applicationCommandRequests = new ArrayList<>();

        for (Class<? extends SlashCommand> commandClass : classes) {
            try {
                @SuppressWarnings("unchecked")
                Constructor<SlashCommand> constructor = (Constructor<SlashCommand>) commandClass.getConstructor();
                SlashCommand command = constructor.newInstance();

                applicationCommandRequests.add(command.definition());
                slashCommands.put(command.definition().name(), constructor);

            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                Logger.logError("The command " + commandClass.getSimpleName() + " don't have empty constructor. She couldn't be created.");
            }
        }

        Logger.logMessage(applicationCommandRequests.size() + "/" + classes.size() + " data extracted.");

        Logger.logMessage("Updating SlashCommand to discord api.");
        Core.client.getRestClient().getApplicationService()
                .bulkOverwriteGlobalApplicationCommand(Core.client.getRestClient().getApplicationId().block(),
                        applicationCommandRequests).subscribe();
        Logger.logMessage("SlashCommandDispatcherWorker started with out error.");

    }

    /**
     * Create the handle on {@link SlashCommandFiller}. Get the constructor of the commandName called. And instantiate a new command, check the {@link SlashCommand#permissions()}.
     * And call the {@link SlashCommand#play(SlashCommandFiller)} if the member have the permissions required.
     *
     * @return the handling event
     */
    @Override
    public ActionEvent<?> setupEvent() {
        return (ActionEvent<SlashCommandFiller>) filler -> {
            if (filler.event.getInteraction().getMember().isEmpty())
                return;

            Logger.logTitle("Member " + filler.mem.entity.getTag() + " ask to execute " + filler.event.getCommandName() + ".");

            try {

                SlashCommand command = slashCommands.get(filler.event.getCommandName()).newInstance();

                if (filler.event.getInteraction().getMember().isPresent())
                    if (command.permissions() == null || command.permissions().isMemberOwningPermissions(filler.event.getInteraction().getMember().get())) {
                        command.play(filler);
                        Logger.logMessage("Command " + filler.event.getCommandName() + " was executed by " + filler.mem.entity.getTag() + ".");
                    } else {
                        notPermissionToExecute(filler.event);
                        Logger.logMessage("Permission not granted for " + filler.mem.entity.getTag() + " to execute " + filler.event.getCommandName() + ".");
                    }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    public void onStop() {

    }

    private static void notPermissionToExecute(final ChatInputInteractionEvent event) {
        event.reply(InteractionApplicationCommandCallbackSpec.builder()
                .ephemeral(true)
                .content("Vous n'avez pas la permission d'utiliser cette commande !")
                .build()).subscribe();
    }

}
