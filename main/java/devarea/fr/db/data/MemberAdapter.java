package devarea.fr.db.data;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import org.bson.Document;

public class MemberAdapter extends Document {

    public static Document memberToDocument(final Member member) {
        return memberToDocument(member.getId().asString());
    }

    public static Document memberToDocument(final Snowflake snowflake) {
        return memberToDocument(snowflake.asString());
    }

    public static Document memberToDocument(final String id) {
        return new Document("_id", id);
    }

}
