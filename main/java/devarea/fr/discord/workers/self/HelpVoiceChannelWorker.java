package devarea.fr.discord.workers.self;

import devarea.fr.discord.Core;
import devarea.fr.discord.cache.ChannelCache;
import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.Chan;
import devarea.fr.discord.entities.events_filler.MemberJoinVoiceEventFiller;
import devarea.fr.discord.entities.events_filler.MemberLeaveVoiceEventFiller;
import devarea.fr.discord.workers.Worker;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.core.spec.GuildMemberEditSpec;
import discord4j.core.spec.VoiceChannelCreateSpec;
import discord4j.discordjson.possible.Possible;

import java.util.ArrayList;
import java.util.Optional;

public class HelpVoiceChannelWorker implements Worker {

    private static ArrayList<Integer> numbers = new ArrayList<>();

    @Override
    public void onStart() {
        Worker.super.onStart();
    }

    @Override
    public ActionEvent<?> setupEvent() {

        ChannelCache.get(Core.data.help_voiceChannel.asString()).listen((ActionEvent<MemberJoinVoiceEventFiller>) filler -> {

            final int channelNumber = getCurrentHelpNumber() + 1;
            numbers.add(channelNumber);

            VoiceChannel channel = Core.devarea.createVoiceChannel(VoiceChannelCreateSpec.builder()
                    .name("Aide #" + channelNumber)
                    .parentId(Core.data.general_category)
                    .userLimit(5)
                    .build()).block();

            MemberCache.get(filler.event.getUserId().asString()).entity.edit(GuildMemberEditSpec.builder()
                            .newVoiceChannel(Possible.of(Optional.of(channel.getId())))
                            .build())
                    .subscribe();

            ChannelCache.use(channel);

            Chan<VoiceChannel> chan = ChannelCache.watch(channel.getId().asString());
            chan.listen((ActionEvent<MemberLeaveVoiceEventFiller>) fillerLeave -> {
                if (channel.getVoiceStates().blockLast() == null) {
                    numbers.remove((Integer) channelNumber);
                    ChannelCache.slash(channel.getId().asString());
                    channel.delete().subscribe();
                }
            }, true);

        }, true);
        return null;
    }

    @Override
    public void onStop() {

    }

    private static int getCurrentHelpNumber() {
        int current = 0;
        for (int n : numbers)
            if (n > current)
                current = n;
        return current;
    }
}
