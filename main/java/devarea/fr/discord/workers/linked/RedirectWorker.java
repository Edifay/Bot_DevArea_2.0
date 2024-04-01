
package devarea.fr.discord.workers.linked;

import devarea.fr.db.DBManager;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.events_filler.ButtonInteractionEventFiller;
import devarea.fr.discord.statics.ColorsUsed;
import devarea.fr.discord.statics.DefaultData;
import devarea.fr.discord.workers.Worker;
import devarea.fr.discord.workers.self.AuthWorker;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;

public class RedirectWorker implements Worker {
    @Override
    public ActionEvent<?> setupEvent() {
        return (ActionEvent<ButtonInteractionEventFiller>) filler -> {
            String link;
            if ((link = DBManager.getRedirect(filler.event.getCustomId())) != null) {
                filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                        .ephemeral(true)
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Lien personnalisé")
                                .color(ColorsUsed.same)
                                .build())
                        .addComponent(ActionRow.of(
                                Button.link(
                                        DefaultData.DOMAIN_NAME + "?code=" + AuthWorker.getCodeForMember(filler.mem.getSId())
                                                + "&redirect=" + link
                                        , "devarea.fr"
                                )
                        ))
                        .build()).subscribe();
            }
        };
    }

    @Override
    public void onStop() {

    }

}
