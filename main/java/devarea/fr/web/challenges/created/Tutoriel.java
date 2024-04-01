package devarea.fr.web.challenges.created;

import devarea.fr.web.challenges.Challenge;
import devarea.fr.web.challenges.Session;
import devarea.fr.web.challenges.SimplePacket;

@Challenge.ChallengeDefinition(name = "tutoriel")
public class Tutoriel extends Challenge {

    private static final String javaTutoriel =
        """
            Tutoriel en Java :

            Tu utilises actuellement le programme de tutoriel déjà complété, n'hésites pas à le lire pour comprendre son fonctionnement.

            Voici les bases nécessaires pour réaliser les challenges suivants.

            Le Client :
             - Client client = new Client(<key>);

            C'est avec ça que tu communiqueras avec le serveur.
             - La <key> peut être envoyé à d'autres développeurs sans risque. Pas besoin de la cacher dans ces codes !

            Comment utiliser le client :
             - client.loadChallenge(<nom_du_challenge>); -> permet de charger un challenge.
             - String result = client.submit(<valeur>); -> permet d'envoyer une donnée au serveur. 'result' contient la valeur de retour du serveur.
             - client.start(); -> l'utilisation de cette methode vous sera explicitement demandé par d'inquiétude !
                        
            Voilà pour le petit tutoriel.
            Pensez bien à lire ce que vous renvois le serveur !""";

    private static final String pythonTutoriel =
        """
            Tutoriel en Python :

            Tu utilises actuellement le programme de tutoriel déjà complété, n'hésites pas à le lire pour comprendre son fonctionnement.

            Voici les bases nécessaires pour réaliser les challenges suivants.

            Le Client :
             - client = Client(<key>)

            C'est avec ça que tu communiqueras avec le serveur.
             - La <key> peut être envoyé à d'autres développeurs sans risque. Pas besoin de la cacher dans ces codes !

            Comment utiliser le client :
             - client.loadChallenge(<nom_du_challenge>) -> permet de charger un challenge.
             - result = client.submit(<valeur>) -> permet d'envoyer une donnée au serveur. 'result' contient la valeur de retour du serveur.
             - client.start() -> l'utilisation de cette methode vous sera explicitement demandé par d'inquiétude !
                        
            Voilà pour le petit tutoriel.
            Pensez bien à lire ce que vous renvois le serveur !""";

    private static final String cTutoriel =
        """
            Tutoriel en C :

            Tu utilises actuellement le programme de tutoriel déjà complété, n'hésites pas à le lire pour comprendre son fonctionnement.

            Voici les bases nécessaires pour réaliser les challenges suivants.

            Le Client est statique tu peux le trouver dans 'client/client.c' :
             - initDefaultClient(<key>); -> initialize le client et ouvre une session avec le serveur.

            C'est avec ça que tu communiqueras avec le serveur.
             - La <key> peut être envoyé à d'autres développeurs sans risque. Pas besoin de la cacher dans ces codes !

            Comment utiliser le client :
             - loadChallenge(<nom_du_challenge>); -> permet de charger un challenge.
             - char *result = submit(<valeur>); -> permet d'envoyer un tableau de char au serveur.
             - char *result = submitInt(<valeur>); -> permet d'envoyer un int au serveur.
             - start(); -> l'utilisation de cette methode vous sera explicitement demandé par d'inquiétude !
            
            !! ATTENTION !! : ne pas utiliser 'free(result);'.
            Le tableau retourner est un tableau de char géré par le client.
            Vous pouvez bien entendu le modifier à votre guise, mais vous n'avez pas à faire attention à libérer sa mémoire.
            
            
            Voilà pour le petit tutoriel.
            Pensez bien à lire ce que vous renvois le serveur !""";

    public Tutoriel(String name, Session session) {
        super(name, session);
    }

    @Override
    public SimplePacket onLoad() {
        String tutoriel =
            switch (this.session.getLang()) {
                case JAVA -> javaTutoriel;
                case PYTHON -> pythonTutoriel;
                case C -> cTutoriel;
            };
        return new SimplePacket("", "Bienvenue, à toi !\n\nCe challenge est un tutoriel sur le fonctionnement des challenges de Dev'Area ! Voici le petit tutoriel pour ton langage : \n\n " + tutoriel + "\n\nUtilisez 'start' pour passer à la suite.");
    }

    @Controller(name = "start", freeToUse = true)
    public SimplePacket start(final SimplePacket packet) {
        setState("OK");
        return new SimplePacket("", "Pour passer à la suite, envois 'OK' au serveur !");
    }

    @Controller(name = "OK", freeToUse = false)
    public SimplePacket OK(final SimplePacket packet) {
        if (!packet.getData().equals("OK"))
            return new SimplePacket("", "Ce n'est pas la valeur attendue !");
        this.validate();
        return new SimplePacket("", "Bravo vous avez validé le tutoriel !\n\nLe prochain challenge est le challenge 'PlusOuMoins'.");
    }

}
