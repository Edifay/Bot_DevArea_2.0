package devarea.fr.discord.workers.self;

import devarea.fr.discord.Core;
import devarea.fr.discord.badges.Badges;
import devarea.fr.discord.cache.ChannelCache;
import devarea.fr.discord.cache.MemberCache;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.Chan;
import devarea.fr.discord.entities.Mem;
import devarea.fr.discord.entities.events_filler.MessageCreateEventFiller;
import devarea.fr.discord.workers.Worker;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static devarea.fr.discord.statics.DefaultData.DOMAIN_NAME;
import static devarea.fr.utils.ThreadHandler.startAway;

public class ProfileLinkWorker implements Worker {

    private static Font font;
    private final static String style = "https://devarea.fr/member-profile?member_id=";

    @Override
    public void onStart() {
        try {
            font = Font.createFont(Font.TRUETYPE_FONT,
                Core.class.getResource("/assets/fonts/font.otf").openStream());
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
        Worker.super.onStart();
    }

    @Override
    public ActionEvent<?> setupEvent() {
        return (ActionEvent<MessageCreateEventFiller>) filler -> {
            // Transform message to Members
            String content = filler.event.getMessage().getContent();
            if (!content.contains(style))
                return;

            String[] occurs = content.split("https://devarea.fr/member-profile?");

            boolean other = false;

            ArrayList<Mem> members = new ArrayList<>();
            for (String occur : occurs)
                if (occur.startsWith("?member_id=") && occur.length() >= 29) {
                    Mem member;
                    if ((member = MemberCache.get(occur.substring(11, 29))) != null && !members.contains(member))
                        members.add(member);
                } else if (!occur.isBlank())
                    other = true;

            // Delete message if it contains only links
            if (!other)
                filler.event.getMessage().delete().subscribe(unused -> {
                }, throwable -> {
                });

            if (members.size() == 0)
                return;

            generateLinkEmbed((Chan<GuildMessageChannel>) ChannelCache.watch(filler.event.getMessage().getChannelId().asString()), members);
        };
    }

    @Override
    public void onStop() {

    }


    public static void generateLinkEmbed(Chan<GuildMessageChannel> chan, ArrayList<Mem> members) {
        for (Mem mem : members)
            startAway(() -> {
                try {
                    // Creating a new message Builder
                    MessageCreateSpec.Builder msgBuilder = MessageCreateSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                            .title("Profil de : " + mem.entity.getDisplayName())
                            .image("attachment://profile.png")
                            .color(discord4j.rest.util.Color.of(255, 87, 51)).build()
                        );

                    // Creating components to draw !
                    ByteArrayInputStream image_stream = generateImageStreamForMember(mem);

                    // Finalize builder
                    msgBuilder
                        .addFile("profile.png", image_stream)
                        .addComponent(ActionRow.of(Button.link(DOMAIN_NAME + "member-profile?member_id=" + mem.getId().asString(), "devarea.fr")));

                    chan.entity.createMessage(msgBuilder.build()).subscribe();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
    }


    public static ByteArrayInputStream generateImageStreamForMember(Mem mem) throws IOException {
        BufferedImage img = new BufferedImage(1200, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) img.getGraphics();

        // Setup Graphics
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw background
        g.drawImage(Core.assetsImages.get("profile_background"), 0, 0, 1200, 600, null);

        // Draw Member Icon !
        g.drawImage(makeRoundedCorner(ImageIO.read(new URL(mem.entity.getAvatarUrl())), 10000), 25, 20, 210,
            210,
            null);

        // Draw UserName
        g.setFont(font);
        drawCentredAndCentred(g, mem.entity.getDisplayName(), 730, 90, getFontSize(mem.entity.getDisplayName(), g));

        // Draw Xp Part
        int xp = mem.db().getXP();
        int level = XPWorker.getLevelForXp(xp);

        float percentage =
            (float) (xp - XPWorker.getAmountForLevel(level)) / (XPWorker.getAmountForLevel(level + 1) - XPWorker.getAmountForLevel(level));

        // Xp bar
        g.setColor(Color.black);
        g.setStroke(new BasicStroke(38.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, null,
            0.0f));
        g.draw(new Line2D.Float(249, 300, 1151, 300));

        g.setColor(Color.white);
        g.setStroke(new BasicStroke(35.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, null,
            0.0f));
        g.draw(new Line2D.Float(250, 300, 1150, 300));
        g.setColor(new Color(248, 176, 86));
        g.draw(new Line2D.Float(250, 300, 250 + (percentage * 900), 300));

        // Xp text
        Font xp_font = font.deriveFont(60f);
        g.setFont(xp_font);
        g.setColor(Color.white);

        g.drawString(level + "", 240, 272);
        drawLeft(g, (level + 1) + "", 1155, 272, g.getFont());
        drawCenteredString(g, "XP-" + xp, 700, 272, g.getFont());

        // Badges part
        Badges[] badges = mem.getBadges();

        Font badges_font = font.deriveFont(30f);
        g.setFont(badges_font);

        int inset_x = 50;
        int inset_y = 380;

        // Draw max of 4 badges inline
        for (int i = 0; i < badges.length && i < 4; i++) {
            g.drawImage(badges[i].getLocal_icon(), inset_x + 68 + (i * 250),
                inset_y + 10, 107, 107, null);
            drawCenteredString(g, badges[i].getName(), inset_x + 120 + (i * 250), inset_y + 150,
                badges_font);
        }

        // Transform image to output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(img, "png", outputStream);
        ByteArrayInputStream image_stream = new ByteArrayInputStream(outputStream.toByteArray());
        return image_stream;
    }

    private static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {
        BufferedImage output = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = output.createGraphics();

        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, output.getWidth(), output.getHeight(), cornerRadius, cornerRadius));

        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, output.getWidth(), output.getHeight(), null);

        g2.dispose();

        return output;
    }

    private static void drawCenteredString(Graphics g, String text, int x, int y, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        int posX = x - (metrics.stringWidth(text) / 2);
        g.setFont(font);
        g.drawString(text, posX, y);
    }

    private static void drawCentredAndCentred(Graphics g, String text, int x, int y, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        int posX = x - (metrics.stringWidth(text) / 2);
        int poxY = y + (metrics.getHeight() / 2);
        g.setFont(font);
        g.drawString(text, posX, poxY);
    }

    private static Font getFontSize(String text, Graphics2D g) {
        Font font = g.getFont().deriveFont(140f);
        FontMetrics metrics = g.getFontMetrics(font);
        while (950 < metrics.stringWidth(text)) {
            font = font.deriveFont((float) (font.getSize() - 1));
            metrics = g.getFontMetrics(font);
        }
        return font;
    }

    private static void drawLeft(Graphics g, String text, int x, int y, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        int posX = x - metrics.stringWidth(text);
        g.setFont(font);
        g.drawString(text, posX, y);
    }
}
