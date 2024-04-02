package devarea.fr.discord.workers.self;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Optional;

import devarea.fr.discord.Core;
import devarea.fr.discord.cache.ChannelCache;
import devarea.fr.discord.commands.Permissions;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.events_filler.MessageCreateEventFiller;
import devarea.fr.discord.utils.MemberBehavior;
import devarea.fr.discord.workers.Worker;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.Category;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.TextChannelCreateSpec;
import discord4j.rest.util.Color;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import discord4j.common.util.Snowflake;
import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Member;


public class AntiSpamWorker implements Worker {

    private static HashMap<String, MemberBehavior> membersBehavior = new HashMap<>();
    private static Permissions adminPermission = Permissions.of(Permission.ADMINISTRATOR);

    // TODO : adjust these parameters
    private int suspectThreshold = 30;
    private int dangerousThreshold = 60;

    private GuildMessageChannel staffChannel;
    private Category sanctionDiscussCategory;
    private PermissionSet basicPermissions = PermissionSet.of(Permission.VIEW_CHANNEL, Permission.SEND_MESSAGES);

    @Override
    public ActionEvent<?> setupEvent() {

        staffChannel = (GuildMessageChannel) ChannelCache.watch(Core.data.staff_channel.asString()).entity;
        sanctionDiscussCategory = (Category) ChannelCache.watch(Core.data.sanction_discuss_category.asString()).entity;

        return (ActionEvent<MessageCreateEventFiller>) filler -> {

            Optional<Member> member = filler.event.getMember();
            
            if (!member.isPresent()) {
                return;
            }
            if (adminPermission.isMemberHasPermissions(member.get())) {
                return;
            }

            System.out.println(member.get().getUsername());
            Message message = filler.event.getMessage();

            String memberId = member.get().getId().asString();
            
            MemberBehavior behavior = membersBehavior.get(memberId);

            if (behavior == null) {
                behavior = new MemberBehavior();
                membersBehavior.put(memberId, behavior);
            }

            behavior.recordMessage(message);

            int behaviorScore = behavior.getBehaviorScore();

            System.out.println(behaviorScore);

            if (behaviorScore > dangerousThreshold) {
                takeAction(member.get(), behavior);
            } else if (behaviorScore > suspectThreshold) {
                // Not implemented
            }
        };
    }

    @Override
    public void onStop() {

    }

    private void takeAction(Member member, MemberBehavior behavior) {
        System.out.println("ACTION TAKEN");
        
        Optional<Instant> joinTime = member.getJoinTime();

        String joinTimeText;
        if (joinTime.isPresent()) {
            joinTimeText = String.format("<t:%d:d>", Math.round(joinTime.get().toEpochMilli()/1000));
        } else {
            joinTimeText = "*inconnu*";
        }

        // removes all members's roles
        for (Snowflake roleId : member.getRoleIds()) {
            member.removeRole(roleId).subscribe();
        }

        // delete all recent messages
        for (Message message : behavior.lastMessages) {
            message.delete("Possible spam détecté").subscribe();
        }

        // create sanction discuss channel
        TextChannel textChannel = Core.devarea.createTextChannel(TextChannelCreateSpec.builder()
            .name("⚠️｜Anti-Spam｜" + member.getDisplayName())
            .parentId(sanctionDiscussCategory.getId())
            .addPermissionOverwrite(PermissionOverwrite.forMember(member.getId(), basicPermissions, PermissionSet.none()))
            .build()).block(Duration.ofSeconds(10));


        String memberIdString = member.getId().asString();
        String channelIdString = textChannel.getId().asString();

        staffChannel.createMessage(MessageCreateSpec.builder()
            .addEmbed(
                EmbedCreateSpec.builder()
                .color(Color.RED).title("Une tentative de spam a été détectée !")
                .author(member.getDisplayName(), null, member.getAvatarUrl())
                .description(String.format("L'utilisateur %s, membre du server depuis %s\n\nIl a été confiné dans le salon %s et ses derniers messages ont été supprimés", member.getMention(), joinTimeText, textChannel.getMention()))
                .footer("En cas d'erreur, vous pouvez libérer l'utilisateur`", null)
                .addField(
                    String.format("Comportement sur les %d dernières secondes", Math.round(behavior.getRecordDuration()/1000)), 
                    String.format("- %d message.s envoyé.s\n- %d salon.s différent.s\n- %d lien.s d'invitation\n- score de mots suspects: %d\n\n**> Score total: %d**", behavior.messageStreak, behavior.channelsId.size(), behavior.invitesCount, behavior.suspectWordsScore, behavior.getBehaviorScore()),
                     false).build()
            )
            .addComponent(
                ActionRow.of(
                    Button.danger("antiSpam_ban_" + memberIdString + "_" + channelIdString, ReactionEmoji.unicode("⛔"), "Bannir"),
                    Button.danger("antiSpam_muteWeek_" + memberIdString + "_" + channelIdString, ReactionEmoji.unicode("🔇"), "Mute 1 semaine"),
                    Button.primary("antiSpam_muteDay_" + memberIdString + "_" + channelIdString, ReactionEmoji.unicode("🔉"), "Mute 1 jour"),
                    Button.success("antiSpam_free_" + memberIdString + "_" + channelIdString, ReactionEmoji.unicode("✅"), "Libérer")
                )
            )
            .build()
        ).subscribe();

        String lastMessagesString = "";
        
        for (Message message : behavior.lastMessages) {
            lastMessagesString += "```" + message.getContent().replace("```", "'''") + "```\n";
        }

        textChannel.createMessage(EmbedCreateSpec.builder()
            .color(Color.RED).title("Discussion de sanction")
            .author(member.getDisplayName(), null, member.getAvatarUrl())
            .description(String.format("%s, tu as été détecté par l'**anti spam**.\n\nTu peux **discuter** de la sanction avec le staff, mais sache que les messages envoyés ont été **enregistrés**.", member.getMention()))
            .footer("En cas d'erreur, vous pouvez libérer l'utilisateur.", null)
            .build()).subscribe();

        textChannel.createMessage(EmbedCreateSpec.builder()
            .color(Color.ORANGE).title("Aperçu des messages envoyés")
            .author(member.getDisplayName(), null, member.getAvatarUrl())
            .description(lastMessagesString)
            .footer("Ces messages ont été envoyés par " + member.getUsername(), null)
            .build()).subscribe();

        behavior.forgetBehavior();
    }
}