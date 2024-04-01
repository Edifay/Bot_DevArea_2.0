package devarea.fr.discord.workers.self;

import devarea.fr.Main;
import devarea.fr.discord.Core;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.Mem;
import devarea.fr.discord.entities.events_filler.MemberUpdateEventFiller;
import devarea.fr.discord.statics.ColorsUsed;
import devarea.fr.discord.workers.Worker;
import discord4j.common.util.Snowflake;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ChallengeNotificationWorker implements Worker {
    @Override
    public ActionEvent<?> setupEvent() {
        return (ActionEvent<MemberUpdateEventFiller>) (filler) -> {
            if(Main.developing)
                return;


            Set<Snowflake> oldRoles = new HashSet<>();

            if (filler.event.getOld().isPresent()) {
                oldRoles = filler.event.getOld().get().getRoleIds();
            }

            Set<Snowflake> currentRoles = filler.event.getCurrentRoleIds();
            Set<Snowflake> addedRoles = new HashSet<>();

            for (Snowflake snow : currentRoles) {
                if (!oldRoles.contains(snow)) {
                    addedRoles.add(snow);
                }
            }

            Mem mem = filler.mem;

            if (addedRoles.contains(Core.data.debutant_role)) {
                try {
                    Objects.requireNonNull(mem.entity.getPrivateChannel().block())
                        .createMessage(MessageCreateSpec.builder()
                            .addEmbed(EmbedCreateSpec.builder()
                                .title("Challenges")
                                .color(ColorsUsed.same)
                                .description("""
                                    Je vois que tu as pris le rôle débutant !

                                    Dev'Area propose des petits challenges algorithmiques, qui peuvent t'aider à progresser.
                                    Je t'invite à regarder sur le site de Dev'Area la partie dédié aux [challenges](https://devarea.fr/challenges). Tu peux aussi te rendre sur le channel <#1172915593737941073>.
                                    
                                    Cela peut être un bon moyen d'apprentissage ;) !
                                    """)
                                .build())
                            .build()).block();
                } catch (Exception ignored) {
                }
            }

        };
    }

    @Override
    public void onStop() {

    }
}
