package devarea.fr.discord.workers.self;

import devarea.fr.db.data.DBAvis;
import devarea.fr.discord.Core;
import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.Mem;
import devarea.fr.discord.entities.events_filler.ButtonInteractionEventFiller;
import devarea.fr.discord.entities.events_filler.ModalSubmitInteractionEventFiller;
import devarea.fr.discord.statics.ColorsUsed;
import devarea.fr.discord.workers.Worker;
import discord4j.common.util.Snowflake;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.TextInput;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.MessageEditSpec;
import discord4j.discordjson.possible.Possible;

import java.util.List;
import java.util.Optional;

public class AvisWorker implements Worker {
    @Override
    public ActionEvent<?> setupEvent() {
        Core.listen((ActionEvent<ModalSubmitInteractionEventFiller>) filler -> {
            if (filler.event.getCustomId().startsWith("avismodal"))
                retrieveModal(filler);

        });
        return (ActionEvent<ButtonInteractionEventFiller>) filler -> {
            if (filler.event.getCustomId().startsWith("avis"))
                askAvis(filler);
        };
    }

    public static void retrieveModal(final ModalSubmitInteractionEventFiller filler) {
        String[] splitted = filler.event.getCustomId().split("_");

        int grade;
        try {
            grade = Integer.parseInt(filler.event.getComponents().get(0).getData().components().get().get(0).value().get());
        } catch (NumberFormatException e) {
            filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                    .ephemeral(true)
                    .addEmbed(EmbedCreateSpec.builder()
                            .title("Erreur !")
                            .description("La note n'est pas valide !")
                            .color(ColorsUsed.wrong)
                            .build())
                    .build()).subscribe();
            return;
        }
        if (grade < 0 || grade > 5) {
            filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                    .ephemeral(true)
                    .addEmbed(EmbedCreateSpec.builder()
                            .title("Erreur !")
                            .description("La note dois êtes comprise entre 0 et 5 !")
                            .color(ColorsUsed.wrong)
                            .build())
                    .build()).subscribe();
            return;
        }

        MemberCache.get(splitted[1]).db().addAvis(new DBAvis(
                grade,
                DBAvis.Status.getStatus(splitted[2]),
                filler.mem.getSId(),
                filler.event.getComponents().get(1).getData().components().get().get(0).value().get()
        ));

        filler.event.getInteraction().getMessage().get().getChannel().block().getMessageById(Snowflake.of(splitted[3])).block().edit(MessageEditSpec.builder()
                .components(Possible.of(Optional.of(List.of())))
                .build()).subscribe();

        filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                .ephemeral(true)
                .content("Merci pour votre avis, celui-ci a bien été pris en compte !")
                .build()).subscribe();
    }

    public static void askAvis(final ButtonInteractionEventFiller filler) {
        String[] splitted = filler.event.getCustomId().split("_");
        Mem mem = MemberCache.get(splitted[1]);

        filler.event.presentModal("Laissez un avis pour " + (mem == null ? splitted[1] : mem.entity.getDisplayName()),
                "avismodal_" + splitted[1] + "_" + splitted[2] + "_" + filler.event.getInteraction().getMessage().get().getId().asString(),
                List.of(
                        ActionRow.of(TextInput.small("grade", "Note ?/5", 1, 1)),
                        ActionRow.of(TextInput.paragraph("comment", "Points positif/négatif de votre échange.", 70, 300))
                )).subscribe();

    }

    @Override
    public void onStop() {

    }
}
