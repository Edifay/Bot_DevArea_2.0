package devarea.fr.discord.workers;

import devarea.fr.discord.DevArea;
import devarea.fr.discord.commands.SlashCommand;
import devarea.fr.discord.entity.OneEvent;
import devarea.fr.discord.entity.events_filler.SlashCommandFiller;
import devarea.fr.utils.Logger;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class SlashCommandDispatcherWorker implements Worker {

    public static HashMap<String, Constructor<SlashCommand>> slashCommands = new HashMap<>();


    @Override
    public void onStart() {

        Logger.logMessage("SlashCommandDispatcherWorker starting.");
        Logger.logMessage("Loading SlashCommand.");

        Reflections reflections = new Reflections("devarea.fr.discord.commands.slash");
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
        DevArea.client.getRestClient().getApplicationService()
                .bulkOverwriteGlobalApplicationCommand(DevArea.client.getRestClient().getApplicationId().block(),
                        applicationCommandRequests).subscribe();
        Logger.logMessage("SlashCommandDispatcherWorker started with out error.");

    }

    @Override
    public OneEvent<?> setupEvent() {
        return (OneEvent<ChatInputInteractionEvent>) event -> {
            Logger.logTitle("Receive SlashCommand " + event.getCommandName());

            try {

                SlashCommand command = slashCommands.get(event.getCommandName()).newInstance();

                if (event.getInteraction().getMember().isPresent())
                    if (command.permissions() == null || command.permissions().isMemberHasPermissions(event.getInteraction().getMember().get()))
                        command.play(new SlashCommandFiller(event));
                    else
                        notPermissionToExecute(event);

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
