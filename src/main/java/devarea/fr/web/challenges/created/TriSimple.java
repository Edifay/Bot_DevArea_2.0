package devarea.fr.web.challenges.created;

import devarea.fr.web.challenges.Challenge;
import devarea.fr.web.challenges.Session;
import devarea.fr.web.challenges.SimplePacket;

@Challenge.ChallengeDefinition(name = "TriSimple", requiredChallenge = {"ListeDeTexte"})
public class TriSimple extends Challenge {

    public TriSimple(String name, Session session) {
        super(name, session);
    }

    private static final String explicationOnLoad = """
                Work in progress...
        """;

    @Override
    public SimplePacket onLoad() {
        return new SimplePacket("", explicationOnLoad);
    }
}
