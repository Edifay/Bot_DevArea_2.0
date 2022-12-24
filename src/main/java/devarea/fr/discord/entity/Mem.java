package devarea.fr.discord.entity;

import discord4j.core.object.entity.Member;

public class Mem extends ObjectListener<Member> {

    public Mem(Member object) {
        super(object);
    }

    public static Mem of(final Member member) {
        return new Mem(member);
    }

}