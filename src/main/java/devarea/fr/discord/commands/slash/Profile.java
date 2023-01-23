package devarea.fr.discord.commands.slash;

import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.commands.SlashCommand;
import devarea.fr.discord.entities.Mem;
import devarea.fr.discord.entities.events_filler.SlashCommandFiller;
import devarea.fr.discord.statics.ColorsUsed;
import devarea.fr.discord.workers.self.ProfileLinkWorker;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionReplyEditSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static devarea.fr.discord.statics.DefaultData.DOMAIN_NAME;

public class Profile extends SlashCommand {

    @Override
    public ApplicationCommandRequest definition() {
        return ApplicationCommandRequest.builder()
                .name("profile")
                .description("Affiche votre profil web. Contient les badges, les levels...")
                .addOption(ApplicationCommandOptionData.builder()
                        .name("mention")
                        .description("Vous pouvez afficher le profil d'un membre du serveur.")
                        .required(false)
                        .type(ApplicationCommandOption.Type.MENTIONABLE.getValue())
                        .build())
                .build();
    }

    @Override
    public void play(SlashCommandFiller filler) {
        Mem target = filler.mem;
        filler.event.deferReply().subscribe();
        if (filler.event.getOption("mention").isPresent() && filler.event.getOption("mention").get().getValue().isPresent()) {
            target =
                    MemberCache.get(filler.event.getOption("mention").get().getValue().get().asSnowflake().asString());
            if (target == null)
                target = filler.mem;
        }
        try {
            ByteArrayInputStream image_stream = ProfileLinkWorker.generateImageStreamForMember(target);
            filler.event.editReply(InteractionReplyEditSpec.builder()
                    .addEmbed(EmbedCreateSpec.builder()
                            .image("attachment://profile.png")
                            .color(ColorsUsed.same)
                            .build())
                    .addFile("profile.png", image_stream)
                    .addComponent(ActionRow.of(Button.link(DOMAIN_NAME + "member-profile?member_id=" + target.getId().asString(), "devarea.fr")))
                    .build()).subscribe();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
