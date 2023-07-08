package devarea.fr.discord.workers.self;

import devarea.fr.discord.Core;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.events_filler.MessageCreateEventFiller;
import devarea.fr.discord.workers.Worker;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.reaction.ReactionEmoji;

import java.util.Objects;

public class MessageReactorWorker implements Worker {
    @Override
    public ActionEvent<?> setupEvent() {
        return (ActionEvent<MessageCreateEventFiller>) filler -> {
            MessageCreateEvent event = filler.event;

            if (filler.event.getMember().isEmpty())
                return;

            if (filler.event.getMessage().getChannelId().equals(Core.data.log_channel))
                return;

            if (messageContain(event, "devarea") || messageContain(event, "dev'area") || messageContain(event,
                    "dev area")) {
                event.getMessage().addReaction(ReactionEmoji.custom(Objects.requireNonNull(Core.devarea.getGuildEmojiById(Snowflake.of(
                        "983423296341176331")).block()))).subscribe();
            }
            if (messageContain(event, "salut") || messageContain(event, "coucou") || messageContain(event,
                    "hey") || messageContain(event, "bonjour") || messageContain(event, "hello")) {
                event.getMessage().addReaction(ReactionEmoji.unicode("👋")).subscribe();
            }
            if (messageContain(event, "pour quoi") || messageContain(event, "pourquoi") || messageContain(event, "comment "
            )) {
                event.getMessage().addReaction(ReactionEmoji.unicode("🤔")).subscribe();
            }
            if (messageContain(event, "merci")) {
                event.getMessage().addReaction(ReactionEmoji.unicode("🙏")).subscribe();
            }
        };
    }

    @Override
    public void onStop() {

    }

    private static boolean messageContain(MessageCreateEvent event, String word) {
        return event.getMessage().getContent().toLowerCase().contains(word);
    }
}
