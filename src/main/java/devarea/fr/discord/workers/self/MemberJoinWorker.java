package devarea.fr.discord.workers.self;

import devarea.fr.db.DBManager;
import devarea.fr.discord.Core;
import devarea.fr.discord.cache.ChannelCache;
import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.events_filler.MemberJoinEventFiller;
import devarea.fr.discord.statics.ColorsUsed;
import devarea.fr.discord.statics.TextMessage;
import devarea.fr.discord.workers.Worker;
import devarea.fr.utils.Logger;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.object.entity.channel.PrivateChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;

import java.util.ArrayList;

import static devarea.fr.discord.statics.DefaultData.DOMAIN_NAME;
import static devarea.fr.utils.ThreadHandler.startAway;

public class MemberJoinWorker implements Worker {

    @Override
    public ActionEvent<?> setupEvent() { // TODO refactor the old code.
        return (ActionEvent<MemberJoinEventFiller>) event -> {

            DBManager.memberJoin(event.mem.getId().asString());

            Member member = event.mem.entity;

            member.addRole(Core.data.rulesAccepted_role).subscribe();

            try {
                PrivateChannel privateChannel = member.getPrivateChannel().block();
                startAway(() -> {
                    privateChannel.createMessage(TextMessage.helpEmbed).block();

                    final String code = AuthWorker.getCodeForMember(member.getId().asString());

                    final Message message_at_edit = privateChannel.createMessage(MessageCreateSpec.builder()
                            .addEmbed(EmbedCreateSpec.builder()
                                    .title("Authentification au site de Dev'area !")
                                    .description("Vous venez de vous authentifier sur le site de dev'area" +
                                            " !\n\nPour vous connecter utilisez ce lien :\n\n" + DOMAIN_NAME + "?code" +
                                            "=" + code + "\n\nCe message sera supprimé d'ici **5 " +
                                            "minutes** pour sécuriser l'accès. Si vous avez besoin de le " +
                                            "retrouver exécutez de nouveau la commande !")
                                    .color(ColorsUsed.just)
                                    .build())
                            .build()).block();

                    final ArrayList<EmbedCreateSpec> embeds = new ArrayList<>();

                    embeds.add(EmbedCreateSpec.builder()
                            .title("Authentification au site de Dev'area !")
                            .description("Si vous voulez retrouver le lien d'authentification vous pouvez" +
                                    " exécuter la commande `/auth` à nouveau !")
                            .color(ColorsUsed.same)
                            .build());

                    startAway(() -> {
                        try {
                            Thread.sleep(300000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            message_at_edit.edit(MessageEditSpec.builder()
                                    .addAllEmbeds(embeds)
                                    .build()).subscribe();
                        }
                    });

                });

            } catch (Exception e) {
            }

            startAway(() -> ((GuildMessageChannel) ChannelCache.watch(Core.data.welcome_channel.asString())
                    .entity).createMessage(MessageCreateSpec.builder()
                    .addEmbed(EmbedCreateSpec.builder()
                            .title("Salut ! " + member.getTag() + ", bienvenue sur **Dev'Area**, amuse " +
                                    "toi bien !")
                            .description("Membre n°" + MemberCache.cacheSize())
                            .image(member.getAvatarUrl())
                            .color(ColorsUsed.same)
                            .build())
                    .build()).subscribe());

            ((GuildMessageChannel) ChannelCache.watch(Core.data.general_channel.asString()).entity)
                    .createMessage(msg -> msg
                            .setContent("<@" + member.getId().asString() +
                                    "> vient de rejoindre le serveur ! Pour en savoir plus sur le serveur <#1004324078531907594> \uD83D\uDE09 !"))
                    .subscribe();
        };
    }

    @Override
    public void onStart() {
        Logger.logMessage("MemberJoinWorker Created !");
    }

    @Override
    public void onStop() {

    }
}
