package devarea.fr.discord.workers.linked;

import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.events_filler.SelectMenuInteractionEventFiller;
import devarea.fr.discord.workers.Worker;
import discord4j.common.util.Snowflake;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.discordjson.json.SelectOptionData;

import static devarea.fr.discord.commands.slash.RoleMenu.SELECTOR;

public class RoleMenuWorker implements Worker {

    @Override
    public ActionEvent<?> setupEvent() {
        return (ActionEvent<SelectMenuInteractionEventFiller>) filler -> {

            if (filler.event.getCustomId().equals("roleMenu")) {

                StringBuilder addedRoles = new StringBuilder();
                StringBuilder removeRoles = new StringBuilder();

                for (SelectOptionData options :
                        filler.event.getMessage().get().getComponents().get(0).getChildren().get(0).getData().options().get()) {
                    String id = options.value().substring(SELECTOR.length());
                    if (filler.event.getValues().contains(options.value())) {
                        if (filler.event.getInteraction().getMember().get().getRoleIds().contains(Snowflake.of(id)))
                            continue;
                        addedRoles.append("<@&" + id + ">");
                        filler.event.getInteraction().getMember().get().addRole(Snowflake.of(id)).subscribe();
                    } else if (filler.event.getInteraction().getMember().get().getRoleIds().contains(Snowflake.of(id))) {
                        removeRoles.append("<@&" + id + ">");
                        filler.event.getInteraction().getMember().get().removeRole(Snowflake.of(id)).subscribe();
                    }
                }

                if (addedRoles.isEmpty())
                    addedRoles.append("...");
                if (removeRoles.isEmpty())
                    removeRoles.append("...");


                filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                        .ephemeral(true)
                        .content("Vous avez ajouté " + addedRoles + " vous avez enlevé " + removeRoles + " !")
                        .build()).subscribe();
            }
        };
    }

    @Override
    public void onStop() {

    }
}
