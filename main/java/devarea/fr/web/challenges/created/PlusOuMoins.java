package devarea.fr.web.challenges.created;

import devarea.fr.web.challenges.Challenge;
import devarea.fr.web.challenges.Session;
import devarea.fr.web.challenges.SimplePacket;

import java.util.Random;

@Challenge.ChallengeDefinition(name = "PlusOuMoins", requiredChallenge = {"tutoriel"})
public class PlusOuMoins extends Challenge {

    protected int atTrouver;
    protected long start;
    protected long nbEssaies = 0;


    public PlusOuMoins(final String name, Session session) {
        super(name, session);
    }

    @Override
    public SimplePacket onLoad() {
        return new SimplePacket("", "Tu dois rendre visite à un amis à 17h.\n\nArrivé en bas de l'immeuble de 200 étages, tu n'as malheureusement pas pensé à lui demander au quel il se trouve.\n\nMais tu as une idée de génie !\nVous avez tout les deux installé une application qui te permet de savoir si tu es plus haut ou plus bas que lui.\n\nTu vas donc méthodiquement mettre en place un algorithme, permettant de trouver l'étage de ton amis.\n\nTu te situes dans l'ascenseur de l'immeuble, envois au serveur l'étage au quel tu veux aller, une fois arrivé, le serveur te retourneras si tu es au dessus ('+') ou en dessous ('-') de ton amis .\n\nUtilise l'action 'start' pour commencer.");
    }

    @Controller(name = "start", freeToUse = true)
    public SimplePacket start(SimplePacket packet) {
        setState("essaie");

        Random random = new Random();
        atTrouver = random.nextInt(0, 200);

        this.start = System.currentTimeMillis();

        return new SimplePacket("", "L'étage de ton amis à été déterminé (aléatoirement). A toi de le retrouver !");
    }

    @Controller(name = "essaie", freeToUse = false)
    public SimplePacket essaie(SimplePacket packet) {
        int value;
        try {
            value = Integer.parseInt(packet.getData());
        } catch (NumberFormatException e) {
            return new SimplePacket("", "Valeur mal formatée !");
        }

        this.nbEssaies++;

        if (value == atTrouver) {
            if (System.currentTimeMillis() - start > 2000) {
                this.fail();
                return new SimplePacket("", "Tu as trouvé le bon étage mais... tu as été trop lent ! (>2s)");
            }
            if (this.nbEssaies >= 10) {
                this.fail();
                return new SimplePacket("", "Tu as trouvé le bon étage mais... tu as fait trop de faute ! (>10)");
            }
            this.validate();
            return new SimplePacket("", "Bravo tu as trouvé le bon étage, en temps et en heure !!\n\nChallenge validé !");
        } else if (value < atTrouver) {
            return new SimplePacket("-", "-");
        } else {
            return new SimplePacket("+", "+");
        }
    }

}
