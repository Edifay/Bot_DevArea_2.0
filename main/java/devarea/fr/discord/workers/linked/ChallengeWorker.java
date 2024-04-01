package devarea.fr.discord.workers.linked;

import devarea.fr.db.DBManager;
import devarea.fr.discord.Core;
import devarea.fr.discord.cache.ChannelCache;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.Chan;
import devarea.fr.discord.entities.events_filler.ButtonInteractionEventFiller;
import devarea.fr.discord.statics.ColorsUsed;
import devarea.fr.discord.workers.Worker;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.MessageCreateSpec;

import java.time.Instant;

public class ChallengeWorker implements Worker {
    @Override
    public ActionEvent<?> setupEvent() {
        return (ActionEvent<ButtonInteractionEventFiller>) filler -> {
            if (filler.event.getCustomId().equals("get_challenge_key")) {
                filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                    .ephemeral(true)
                    .addEmbed(EmbedCreateSpec.builder()
                        .color(ColorsUsed.same)
                        .title("Votre clef challenge")
                        .description("`" + DBManager.getChallengeForId(filler.mem.getSId()).getKey() + "`")
                        .build())
                    .build()).subscribe();
            }
        };
    }

    @Override
    public void onStop() {
    }

    public static void sendMemberValidatedANewChallenge(final String id, final String challenge) {
        Chan<GuildMessageChannel> chan = ChannelCache.get(Core.data.command_channel.asString());

        chan.entity.createMessage(MessageCreateSpec.builder()
            .content("<@" + id + ">.")
            .addEmbed(EmbedCreateSpec.builder()
                .color(ColorsUsed.same)
                .title("Challenge validé !")
                .description("Le membre <@" + id + "> a validé le challenge **_" + challenge + "_** !")
                .timestamp(Instant.now())
                .build())
            .build()).block();
    }
}
