package devarea.fr.discord.workers.self;

import devarea.fr.discord.Core;
import devarea.fr.discord.cache.ChannelCache;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.Chan;
import devarea.fr.discord.entities.events_filler.MessageCreateEventFiller;
import devarea.fr.discord.entities.events_filler.MessageDeleteEventFiller;
import devarea.fr.discord.entities.events_filler.MessageUpdateEventFiller;
import devarea.fr.discord.statics.ColorsUsed;
import devarea.fr.discord.statics.TextMessage;
import devarea.fr.discord.workers.Worker;
import devarea.fr.discord.workers.self.judge.*;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import discord4j.rest.util.AllowedMentions;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RunCommandWorker implements Worker {

    final static int MAX_MESSAGES = 400;

    private static final Map<Snowflake, Snowflake> latestMessages = new LinkedHashMap<>();

    final static int MAX_LINES = 20;
    final static int MAX_CHARS = 900;
    // Regex to extract the different parts of the message.
    final static Pattern PATTERN = Pattern.compile("^(.*)\\n```(.+)\\n(?s)(.*\\S.*)```\\n?(.*)$");
    final static String ZERO_WIDTH_SPACE = "\u200b";
    final static int ID_ACCEPTED = 3;

    @Override
    public void onStart() {
        Worker.super.onStart();

        Core.listen((ActionEvent<MessageUpdateEventFiller>) filler -> {
            Message messageHandler = filler.event.getMessage().block();

            if (!latestMessages.containsKey(messageHandler.getId())
                    || !messageHandler.getContent().startsWith(Core.data.prefix + "run")
                    || messageHandler.getAuthor().isEmpty()) {
                return;
            }

            Chan<GuildMessageChannel> channelHandler = ChannelCache.get(messageHandler.getChannelId().asString());

            if (channelHandler == null)
                return;

            runCommand(messageHandler);

        });

        Core.listen((ActionEvent<MessageDeleteEventFiller>) filler -> {
            Snowflake replyId = latestMessages.remove(filler.event.getMessage().get().getId());

            if (replyId == null)
                return;


            GuildMessageChannel channel = (GuildMessageChannel) ChannelCache.get(filler.event.getMessage().get().getChannelId().asString());

            channel.getMessageById(replyId).block().delete().subscribe(unused -> {
            }, throwable -> {
            });


        });
    }


    @Override
    public ActionEvent<?> setupEvent() {
        return (ActionEvent<MessageCreateEventFiller>) filler -> {
            if (filler.event.getMessage().getContent().startsWith(Core.data.prefix + "run"))
                runCommand(filler.event.getMessage());
        };
    }

    @Override
    public void onStop() {

    }

    public void runCommand(final Message message) {
        String content = message.getContent().substring((Core.data.prefix + "run").length());

        if (content.isBlank()) {
            sendResponse(message, TextMessage.runCommandExplain, false);
            return;
        }

        if (content.strip().equals("languages")) {
            try {
                sendResponse(message, embedListLanguages(), false);
            } catch (JudgeException e) {
                Chan<GuildMessageChannel> channel = ChannelCache.get(message.getChannelId().asString());
                channel.entity.createMessage(MessageCreateSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .color(ColorsUsed.wrong)
                                .title("Error")
                                .description(e.getMessage())
                                .build())
                        .build()).subscribe();
            }
            return;
        }

        Matcher matcher = PATTERN.matcher(content);

        if (matcher.find()) {
            message.addReaction(ReactionEmoji.custom(Core.devarea.getGuildEmojiById(Core.data.loading).block())).subscribe();

            JudgeSubmission submission = new JudgeSubmissionBuilder()
                    .args(matcher.group(1))
                    .languageAlias(matcher.group(2))
                    .code(matcher.group(3))
                    .stdin(matcher.group(4))
                    .build();

            JudgeManager.get().executeAsync(submission).thenAccept(response -> {
                sendResponse(message, embedResponse(response, message.getAuthor().get()), true);
            }).whenComplete((res, e) -> {
                message.removeSelfReaction(ReactionEmoji.custom(Core.devarea.getGuildEmojiById(Core.data.loading).block())).subscribe();
                if (e != null) {
                    if (e.getCause() instanceof JudgeException) {
                        Chan<GuildMessageChannel> channel = ChannelCache.get(message.getChannelId().asString());
                        channel.entity.createMessage(MessageCreateSpec.builder()
                                .addEmbed(EmbedCreateSpec.builder()
                                        .color(ColorsUsed.wrong)
                                        .title("Error")
                                        .description(e.getCause().getMessage())
                                        .build())
                                .build()).subscribe();
                    } else {
                        e.getCause().printStackTrace();
                    }
                }
            });
        } else {
            Chan<GuildMessageChannel> channel = ChannelCache.get(message.getChannelId().asString());
            channel.entity.createMessage(MessageCreateSpec.builder()
                    .addEmbed(EmbedCreateSpec.builder()
                            .color(ColorsUsed.wrong)
                            .title("Error")
                            .description("La commande n'est pas correctement formatée ! `" + Core.data.prefix +
                                    "run` pour voir comment utiliser cette commande.")
                            .build())
                    .build()).subscribe();


        }
    }


    /*
        Message binder, bind a message to a response if possible.
     */
    public static void sendResponse(Message message, EmbedCreateSpec spec, boolean edit) {
        Snowflake replyId = latestMessages.get(message.getId());
        Chan<GuildMessageChannel> channel = ChannelCache.get(message.getChannelId().asString());
        Message reply;

        if (channel == null) {
            return;
        }

        if (edit && replyId != null && (reply = channel.entity.getMessageById(replyId).block()) != null) {
            reply.edit(MessageEditSpec.builder().addEmbed(spec).build()).subscribe();
        } else if (replyId == null) {
            reply = channel.entity.createMessage(MessageCreateSpec.builder()
                    .addEmbed(spec)
                    .messageReference(message.getId())
                    .allowedMentions(AllowedMentions.suppressAll())
                    .build()).block();
            if (edit && reply != null) {
                addMessage(message.getId(), reply.getId());
            }
        }
    }


    private EmbedCreateSpec embedListLanguages() throws JudgeException {
        Map<String, List<String>> languages = JudgeManager.get().getConfig().languages();

        StringBuilder field = new StringBuilder("```\n");
        for (Map.Entry<String, List<String>> entry : languages.entrySet()) {
            field.append(String.format("> %s --> %s\n", entry.getKey(), String.join(", ", entry.getValue())));
        }
        field.append("```");

        EmbedCreateSpec.Builder embed = EmbedCreateSpec.builder()
                .addField("Langages supportés", field.toString(), false)
                .color(ColorsUsed.same);

        return embed.build();
    }

    private void embedCodeOutput(EmbedCreateSpec.Builder embed, String judgeMessage, String... values) {
        StringBuilder rawOutput = new StringBuilder();
        for (String s : values) {
            if (s != null) {
                rawOutput.append(s);
            }
        }

        String[] lines = rawOutput.toString().split("\n");
        String output = Arrays.stream(lines)
                .limit(MAX_LINES)
                .collect(Collectors.joining("\n"))
                .replace("`", "`" + ZERO_WIDTH_SPACE);

        if (output.length() > MAX_CHARS || lines.length > MAX_LINES) {
            embed.footer("La sortie a été réduite", null);
        }

        if (output.length() > MAX_CHARS) {
            output = output.substring(0, MAX_CHARS);
        } else if (output.isEmpty()) {
            output = " ";
        }

        StringBuilder formattedOutput = new StringBuilder("```\n")
                .append(output)
                .append("```");

        if (judgeMessage != null) {
            formattedOutput.append("\n").append(judgeMessage);
        }

        embed.addField("Résultat", formattedOutput.toString(), false);
    }

    public static void addMessage(Snowflake message, Snowflake reply) {
        latestMessages.put(message, reply);
        if (latestMessages.size() > MAX_MESSAGES) {
            latestMessages.remove(latestMessages.keySet().iterator().next());
        }
    }

    private EmbedCreateSpec embedResponse(JudgeResponse response, final User user) {
        String author = String.format("Code de %s | %s %s",
                user.getUsername(),
                response.getLanguage().getName(),
                response.getLanguage().getVersion());

        EmbedCreateSpec.Builder embed = EmbedCreateSpec.builder()
                .author(author, null, user.getAvatarUrl())
                .color(response.getStatusId() == ID_ACCEPTED ? ColorsUsed.same : ColorsUsed.wrong);

        embedCodeOutput(embed, response.getMessage(), response.getStdout(), response.getStderr(), response.getCompileOutput());

        if (response.getTime() != null) {
            embed.addField("Temps", response.getTime() + " s", true);
        }
        if (response.getMemory() != 0) {
            embed.addField("Mémoire", Math.round(response.getMemory() / 10D) / 100D + " MB", true);
        }
        if (response.getStatusDescription() != null) {
            embed.addField("Status", response.getStatusDescription(), true);
        }

        return embed.build();
    }
}
