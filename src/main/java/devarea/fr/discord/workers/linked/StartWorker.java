package devarea.fr.discord.workers.linked;

import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.events_filler.ButtonInteractionEventFiller;
import devarea.fr.discord.statics.TextMessage;
import devarea.fr.discord.workers.Worker;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;

public class StartWorker implements Worker {

    @Override
    public ActionEvent<?> setupEvent() {
        return (ActionEvent<ButtonInteractionEventFiller>) filler -> {
            if (filler.event.getCustomId().startsWith("start_")) {
                switch (filler.event.getCustomId().substring(6)) {
                    case "java":
                        replyToFillerWithEmbed(filler, TextMessage.startJava);
                        break;
                    case "python":
                        replyToFillerWithEmbed(filler, TextMessage.startPython);
                        break;
                    case "csharp":
                        replyToFillerWithEmbed(filler, TextMessage.startCSharp);
                        break;
                    case "html_css":
                        replyToFillerWithEmbed(filler, TextMessage.startHtmlCss);
                        break;
                    default:
                        replyToFillerWithEmbed(filler, TextMessage.startCommandExplain);
                }
            }
        };
    }

    @Override
    public void onStop() {

    }

    private static void replyToFillerWithEmbed(final ButtonInteractionEventFiller filler, final EmbedCreateSpec embed) {
        filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                .ephemeral(true)
                .addEmbed(embed)
                .build()).subscribe();

    }
}
