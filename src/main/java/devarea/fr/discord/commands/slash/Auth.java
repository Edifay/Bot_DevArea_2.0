package devarea.fr.discord.commands.slash;

import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.commands.SlashCommand;
import devarea.fr.discord.entity.Mem;
import devarea.fr.discord.entity.events_filler.SlashCommandFiller;
import devarea.fr.discord.statics.ColorsUsed;
import devarea.fr.discord.statics.DefaultData;
import devarea.fr.discord.workers.AuthWorker;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.InteractionReplyEditSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;

import java.util.ArrayList;
import java.util.Collections;

import static devarea.fr.utils.ThreadHandler.startAway;

public class Auth extends SlashCommand {

    private static final long TIMER_AFTER_SPOIL_MESSAGE = 300000;

    @Override
    public ApplicationCommandRequest definition() {
        return ApplicationCommandRequest.builder()
                .name("auth")
                .description("Obtenez votre lien d'authentification au site de Dev'Area.")
                .build();
    }

    @Override
    public void play(SlashCommandFiller filler) {
        final Mem reelMember =
                MemberCache.get(filler.event.getInteraction().getMember().get().getId().asString());

        final String code = AuthWorker.getCodeForMember(reelMember.getId().asString());

        filler.event.reply(InteractionApplicationCommandCallbackSpec.builder() // TODO extract text
                .ephemeral(true)
                .addEmbed(EmbedCreateSpec.builder()
                        .title("Authentification au site de Dev'area !")
                        .description("Vous venez de vous authentifier sur le site de dev'area !\n\nPour vous " +
                                "connecter utilisez ce lien :\n\n" + DefaultData.DOMAIN_NAME + "?code=" + code + "\n\nCe " +
                                "message sera supprimé d'ici **5 minutes** pour sécuriser l'accès. Si vous avez " +
                                "besoin de le retrouver exécutez de nouveau la commande !")
                        .color(ColorsUsed.just)
                        .build())
                .build()).subscribe();

        final EmbedCreateSpec finalEmbed = EmbedCreateSpec.builder()
                .title("Authentification au site de Dev'area !")
                .description("Si vous voulez retrouver le lien d'authentification vous pouvez exécuter la commande " +
                        "`/auth` à nouveau !")
                .color(ColorsUsed.same)
                .build();

        startAway(() -> {
            try {
                Thread.sleep(TIMER_AFTER_SPOIL_MESSAGE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                filler.event.editReply(InteractionReplyEditSpec.builder()
                        .embeds(Collections.singleton(finalEmbed))
                        .build()).subscribe();
            }
        });
    }
}
