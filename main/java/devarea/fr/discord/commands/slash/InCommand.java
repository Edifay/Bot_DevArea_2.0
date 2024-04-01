package devarea.fr.discord.commands.slash;

import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.cache.cached_entity.CachedMember;
import devarea.fr.discord.commands.SlashCommand;
import devarea.fr.discord.entities.EventOwner;
import devarea.fr.discord.entities.events_filler.SlashCommandFiller;
import devarea.fr.discord.statics.ColorsUsed;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;

import java.util.ArrayList;

public class InCommand extends SlashCommand {
    @Override
    public ApplicationCommandRequest definition() {
        return ApplicationCommandRequest.builder()
                .name("incommand")
                .description("Permet de savoir si des membres sont actuellement en LongCommand.")
                .build();
    }

    @Override
    public void play(SlashCommandFiller filler) {
        StringBuilder builder = new StringBuilder("List : \n\n");

        for (CachedMember mem : MemberCache.cache().values()) {
            ArrayList<EventOwner<?>> events;
            if ((events = mem.get().getListeners()).size() != 0) {
                builder.append(mem.get().entity.getDisplayName() + " : \n");
                events.forEach(eventOwner -> builder.append("  - " + eventOwner.getType().getClass().getName() + "\n"));
                builder.append("\n");
            }
        }

        filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                .addEmbed(EmbedCreateSpec.builder()
                        .title("In Command !")
                        .description(builder.toString())
                        .color(ColorsUsed.same)
                        .build())
                .build()).subscribe();

    }
}
