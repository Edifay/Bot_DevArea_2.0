package devarea.fr.discord.workers;

import devarea.fr.discord.DevArea;
import devarea.fr.discord.cache.ChannelCache;
import devarea.fr.discord.entity.OneEvent;
import devarea.fr.discord.entity.events_filler.MessageCreateInChannelFiller;
import discord4j.core.spec.StartThreadSpec;

public class TestWorker implements Worker {
    @Override
    public void onStart() {

        ChannelCache.use(DevArea.devarea.getChannelById(DevArea.data.general_channel).block());

        ChannelCache.watch(DevArea.data.general_channel.asString())
                .listen((OneEvent<MessageCreateInChannelFiller>) event -> {

            if (event.event.getMember().isPresent() && !event.event.getMember().get().isBot()) {

                int characterNumber = Math.min(event.event.getMessage().getContent().length(),
                        94 - event.event.getMember().get().getDisplayName().length());

                event.event.getMessage().startThread(StartThreadSpec.builder()
                        .name(event.event.getMember().get().getDisplayName() + " - " + event.event.getMessage().getContent()
                                .substring(0, characterNumber) +
                                (characterNumber == (94 - event.event.getMember().get().getDisplayName().length()) ? "..." : "")
                        )
                        .build()).subscribe();
            }

        });

    }

    @Override
    public OneEvent<?> setupEvent() {
        return null;
    }

    @Override
    public void onStop() {

    }
}
