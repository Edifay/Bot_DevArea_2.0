package devarea.fr.web.challenges.created;

import devarea.fr.web.challenges.Challenge;
import devarea.fr.web.challenges.Session;
import devarea.fr.web.challenges.SimplePacket;

import java.util.Random;

// Le nom du challenge
@Challenge.ChallengeDefinition(name = "PlusOuMoins")
public class PlusOuMoins extends Challenge {

    public int atTrouver;

    public PlusOuMoins(final String name, Session session) {
        super(name, session);
    }

    @Override
    public SimplePacket onLoad() {
        // indique vers la prochaine methode à lancer lors du prochain packet.
        return new SimplePacket("", "Vous rendez visite à un amis à 17h.\n\nArrivé en bas de l'immeuble de 200 étages, vous n'avez malheureusement pas pensé à lui demandé au quel il se trouve.\n\nMais votre petit cerveau de génie a une super idée ! Vous avez installé tout les 2 une application vous permettant de savoir si vous êtes plus haut ou plus bas que lui.\n\nVous allez donc méthodiquement mettre en place un algorithme, permettant de trouver l'étage de votre amis.\n\nVous vous situez dans un ascenseur, indiquez l'étage au quel vous voulez vous rendre et le serveur vous retourneras en donné si vous êtes '+' plus haut que votre amis ou en dessous de votre amis '-'.\n\nLancez l'action 'start' pour commencer.");
    }

    // Accessible de n'importe quand dans le challenge. (freeToUse = true)
    @Controller(name = "help", freeToUse = true)
    public SimplePacket help(SimplePacket packet) {
        return new SimplePacket("", "Vous avez demandé des astuces pour résoudre ce challenge !");
    }

    @Controller(name = "start", freeToUse = true)
    public SimplePacket start(SimplePacket packet) {
        // Indique que cette étape et validée et la nouvelle étape et l'étape validé
        setState("essaie");

        Random random = new Random();
        atTrouver = random.nextInt(0, 200);
        // Ici par exemple on peut envoyer un jeu de test généré aléatoirement.
        return new SimplePacket("", "Un etage aleatoire a été choisis ! A vos tests.");
    }

    @Controller(name = "essaie", freeToUse = false)
    public SimplePacket essaie(SimplePacket packet) {
        // Ici on récupère le résultat attendu du jeu de test donné précédement
        int value;
        try {
            value = Integer.parseInt(packet.getData());
        }catch (NumberFormatException e)
        {
            return new SimplePacket("", "Mauvaise valeur !");
        }

        if (value == atTrouver) {
            this.validate();
            return new SimplePacket("", "Bravo vous avez trouvé le bon étage !");
        } else if (value < atTrouver) {
            return new SimplePacket("-", "-");
        } else {
            return new SimplePacket("+", "+");
        }
    }

}
