package devarea.fr.discord.entities;

import devarea.fr.db.data.DBMember;
import devarea.fr.discord.badges.Badges;
import devarea.fr.discord.entities.sub_entities.MemberBehavior;
import discord4j.core.object.entity.Member;

public class Mem extends ObjectListener<Member> {

    protected MemberBehavior behavior = null;

    public Mem(Member object) {
        super(object);
    }

    public static Mem of(final Member member) {
        return new Mem(member);
    }

    public DBMember db() {
        return new DBMember(this.getSId());
    }

    public Badges[] getBadges() {
        return Badges.getBadgesOf(this).toArray(Badges[]::new);
    }


    public MemberBehavior getBehavior() {
        return this.behavior;
    }

    public void initBehavior() {
        if (this.behavior == null) {
            this.behavior = new MemberBehavior();
        }
    }

    public void removeBehavior() {
        this.behavior = null;
        // Garbage collector work.
    }

}