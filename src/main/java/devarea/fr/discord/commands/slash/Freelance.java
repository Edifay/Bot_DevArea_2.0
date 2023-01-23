package devarea.fr.discord.commands.slash;

import devarea.fr.discord.commands.SlashCommand;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.events_filler.ButtonInteractionEventFiller;
import devarea.fr.discord.entities.events_filler.SlashCommandFiller;
import devarea.fr.discord.statics.ColorsUsed;
import devarea.fr.discord.workers.linked.FreelanceWorker;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;


import static devarea.fr.discord.statics.DefaultData.DOMAIN_NAME;

public class Freelance extends SlashCommand {
    @Override
    public ApplicationCommandRequest definition() {
        return ApplicationCommandRequest.builder()
                .name("freelance")
                .description("Permet de gérer votre page freelance.")
                .build();
    }

    @Override
    public void play(SlashCommandFiller filler) {
        if (FreelanceWorker.hasFreelance(filler.mem.getSId())) {
            filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                    .addEmbed(EmbedCreateSpec.builder()
                            .title("Freelance")
                            .description("Veuillez choisir votre option.")
                            .color(ColorsUsed.same)
                            .build())
                    .ephemeral(true)
                    .addComponent(
                            ActionRow.of(
                                    Button.primary("bump", "Bump"),
                                    Button.danger("delete", "Supprimer")
                            )
                    )
                    .build()).subscribe();

            filler.mem.listenDuring((ActionEvent<ButtonInteractionEventFiller>) fillerButton -> {
                if (fillerButton.event.getCustomId().equals("bump"))
                    bumpFreelance(fillerButton);
                else if (fillerButton.event.getCustomId().equals("delete"))
                    deleteFreelance(fillerButton);
            }, false, SPOILED_TIME, filler.context());

        } else {
            filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                    .addEmbed(EmbedCreateSpec.builder()
                            .title("Freelance")
                            .description("Vous n'avez pas de freelance. Vous pouvez en créer une en cliquant sur le boutton ci-dessous.")
                            .color(ColorsUsed.same)
                            .build())
                    .ephemeral(true)
                    .addComponent(ActionRow.of(Button.link(DOMAIN_NAME + "freelances", "devarea.fr")))
                    .build()).subscribe();
        }
    }


    public void bumpFreelance(final ButtonInteractionEventFiller filler) {
        if (FreelanceWorker.hasFreelance(filler.mem.getSId())) {
            if (FreelanceWorker.bumpFreeLance(filler.mem.getSId())) {
                filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Freelance")
                                .description("Votre page freelance a été bump. Vous devez attendre 24h avant de faire cette action à nouveau.")
                                .color(ColorsUsed.same)
                                .build())
                        .ephemeral(true)
                        .build()).subscribe();
            } else {
                filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Freelance")
                                .description("Vous devez attendre 24h après avoir fait cette action.")
                                .color(ColorsUsed.wrong)
                                .build())
                        .ephemeral(true)
                        .build()).subscribe();
            }
        }
    }

    public void deleteFreelance(final ButtonInteractionEventFiller filler) {
        if (FreelanceWorker.hasFreelance(filler.mem.getSId())) {
            FreelanceWorker.deleteFreelanceOf(filler.mem.getSId());
            filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                    .addEmbed(EmbedCreateSpec.builder()
                            .title("Freelance")
                            .description("Votre freelance a bien été supprimée.")
                            .color(ColorsUsed.same)
                            .build())
                    .ephemeral(true)
                    .build()).subscribe();
        }
    }
}
