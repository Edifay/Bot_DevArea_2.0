package devarea.fr.discord.workers.self;

import java.util.ArrayList;
import java.util.Optional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;


import devarea.fr.discord.Core;
import devarea.fr.discord.cache.ChannelCache;
import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.commands.Permissions;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.Mem;
import devarea.fr.discord.entities.events_filler.ButtonInteractionEventFiller;
import devarea.fr.discord.statics.ColorsUsed;
import devarea.fr.discord.statics.TextMessage;
import devarea.fr.discord.workers.Worker;
import devarea.fr.utils.Logger;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.spec.BanQuerySpec;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.MessageEditSpec;
import discord4j.discordjson.possible.Possible;
import discord4j.rest.util.Permission;

public class AntiSpamButtonsWorker implements Worker {
    private static final Permissions managePunishment = Permissions.of(Permission.MANAGE_MESSAGES);

    @Override
    public ActionEvent<?> setupEvent() {
        return (ActionEvent<ButtonInteractionEventFiller>) filler -> {

            String eventId = filler.event.getCustomId();

            if (!eventId.startsWith("antiSpam_")) {
                return;
            }

            String[] idData = eventId.split("_");

            Mem memRisk = MemberCache.watch(idData[2]);
            GuildMessageChannel textChannel = (GuildMessageChannel) ChannelCache.watch(idData[3]).entity;
            Mem memStaff = filler.mem;

            if (!managePunishment.isMemberOwningPermissions(memStaff.entity)) {
                filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                    .ephemeral(true)
                    .addEmbed(EmbedCreateSpec.builder()
                        .title("Error !")
                        .description("Vous n'avez pas les permissions pour donner la punition adéquate.")
                        .color(ColorsUsed.wrong)
                        .build())
                    .build()).subscribe();
                return;
            }


            switch (idData[1]) {
                case "ban":
                    Logger.logMessage("Banning " + memRisk.getSId() + " by auto spam validated by " + memStaff.getSId() + ".");

                    memRisk.entity.ban(BanQuerySpec.builder()
                        .reason("Auto Spam detected, validated by " + memStaff.entity.getUsername())
                        .deleteMessageSeconds(100)
                        .build()).subscribe();

                    replyAndEnd(filler, TextMessage.userHasBeenBanned, textChannel, memStaff);
                    break;

                case "muteWeek":
                    Logger.logMessage("Muting or a week " + memRisk.getSId() + " flagged by auto spam by " + memStaff.getSId() + ".");


                    memRisk.entity.edit().withCommunicationDisabledUntil(
                        Possible.of(Optional.of(Instant.now().plus(7, ChronoUnit.DAYS)))
                    ).subscribe();

                    memRisk.entity.addRole(Core.data.rulesAccepted_role).subscribe();
                    memRisk.entity.removeRole(Core.data.spam_detected_role).subscribe();

                    replyAndEnd(filler, TextMessage.userHasBeenMutedWeek, textChannel, memStaff);
                    break;

                case "muteDay":
                    Logger.logMessage("Muting or a day " + memRisk.getSId() + " flagged by auto spam by " + memStaff.getSId() + ".");

                    memRisk.entity.edit().withCommunicationDisabledUntil(
                        Possible.of(Optional.of(Instant.now().plus(1, ChronoUnit.DAYS)))
                    ).subscribe();

                    memRisk.entity.addRole(Core.data.rulesAccepted_role).subscribe();
                    memRisk.entity.removeRole(Core.data.spam_detected_role).subscribe();

                    replyAndEnd(filler, TextMessage.userHasBeenMutedDay, textChannel, memStaff);
                    break;

                case "free":
                    Logger.logMessage("Releasing " + memRisk.getSId() + " flagged by auto spam by " + memStaff.getSId() + ".");

                    memRisk.entity.addRole(Core.data.rulesAccepted_role).subscribe();
                    memRisk.entity.removeRole(Core.data.spam_detected_role).subscribe();

                    replyAndEnd(filler, TextMessage.userHasBeenReleased, textChannel, memStaff);
                    break;

                default:
                    break;
            }
        };
    }

    @Override
    public void onStop() {

    }

    private static void replyAndEnd(final ButtonInteractionEventFiller filler, final EmbedCreateSpec embed, final GuildMessageChannel textChannel, final Mem staffMember) {
        Optional<Message> message = filler.event.getMessage();

        filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
            .addEmbed(embed.withAuthor(EmbedCreateFields.Author.of(staffMember.entity.getUsername(), null, staffMember.entity.getAvatarUrl())))
            .build()).subscribe();


        if (message.isPresent()) {
            message.get().edit(MessageEditSpec.builder().componentsOrNull(new ArrayList<>()).build()).subscribe();
        }

        if (textChannel != null) {
            textChannel.delete().subscribe();
        }
    }
}
