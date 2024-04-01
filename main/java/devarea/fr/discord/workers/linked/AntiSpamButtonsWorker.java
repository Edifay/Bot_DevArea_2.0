package devarea.fr.discord.workers.linked;

import java.util.ArrayList;
import java.util.Optional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;


import devarea.fr.discord.Core;
import devarea.fr.discord.cache.ChannelCache;
import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.events_filler.ButtonInteractionEventFiller;
import devarea.fr.discord.statics.TextMessage;
import devarea.fr.discord.workers.Worker;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.spec.BanQuerySpec;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.MessageEditSpec;
import discord4j.discordjson.possible.Possible;

public class AntiSpamButtonsWorker implements Worker {

    @Override
    public ActionEvent<?> setupEvent() {
        return (ActionEvent<ButtonInteractionEventFiller>) filler -> {

            String eventId = filler.event.getCustomId();

            System.out.println("A");

            if (!eventId.startsWith("antiSpam_")) {
                return;
            }
            
            System.out.println("B");

            String[] idData = eventId.split("_");

            System.out.println(String.join("|", idData));

            Member member = (Member) MemberCache.watch(idData[2]).entity;
            GuildMessageChannel textChannel = (GuildMessageChannel) ChannelCache.watch(idData[3]).entity;
            Member staffMember = filler.mem.entity;

            switch (idData[1]) {
                case "ban":
                    System.out.println("banning user ");

                    member.ban(BanQuerySpec.builder()
                        .reason("Auto Spam detected, validated by " + staffMember.getUsername())
                        .deleteMessageSeconds(100)
                        .build()).subscribe();

                    replyAndEnd(filler, TextMessage.userHasBeenBanned, textChannel, staffMember);
                    break;
                                
                case "muteWeek":
                    System.out.println("muting user for a week");

                    member.edit().withCommunicationDisabledUntil(
                        Possible.of(Optional.of(Instant.now().plus(7, ChronoUnit.DAYS)))
                    ).subscribe();

                    // TODO : change this role
                    member.addRole(Core.data.rulesAccepted_role).subscribe();

                    replyAndEnd(filler, TextMessage.userHasBeenMutedWeek, textChannel, staffMember);
                    break;
                
                case "muteDay":
                    System.out.println("muting user for a day");

                    member.edit().withCommunicationDisabledUntil(
                        Possible.of(Optional.of(Instant.now().plus(1, ChronoUnit.DAYS)))
                    ).subscribe();

                    // TODO : change this role
                    member.addRole(Core.data.rulesAccepted_role).subscribe();

                    replyAndEnd(filler, TextMessage.userHasBeenMutedDay, textChannel, staffMember);
                    break;
                
                case "free":
                    System.out.println("releasing user");

                    member.addRole(Core.data.rulesAccepted_role).subscribe();

                    replyAndEnd(filler, TextMessage.userHasBeenReleased, textChannel, staffMember);
                    break;
            
                default:
                    break;
            }
        };
    }

    @Override
    public void onStop() {

    }

    private static void replyAndEnd(final ButtonInteractionEventFiller filler, final EmbedCreateSpec embed, final GuildMessageChannel textChannel, final Member staffMember) {
        Optional<Message> message = filler.event.getMessage();
        
        filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                .addEmbed(embed.withAuthor(EmbedCreateFields.Author.of(staffMember.getUsername(), null, staffMember.getAvatarUrl())))
                .build()).subscribe();
        

        if (message.isPresent()) {
            message.get().edit(MessageEditSpec.builder().componentsOrNull(new ArrayList<>()).build()).subscribe();
            System.out.println("MESSAGE EDITED !");
        }
        
        if (textChannel != null) {
            textChannel.delete().subscribe();
        }
    }
}
