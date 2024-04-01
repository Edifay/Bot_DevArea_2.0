package devarea.fr.web.challenges.created;

import devarea.fr.utils.PasswordGenerator;
import devarea.fr.web.challenges.Challenge;
import devarea.fr.web.challenges.Session;
import devarea.fr.web.challenges.SimplePacket;

import java.util.Random;
import java.util.function.IntFunction;

@Challenge.ChallengeDefinition(name = "Cesar", requiredChallenge = {"PlusOuMoins"})
public class Cesar extends Challenge {

    private final static PasswordGenerator keyGen = PasswordGenerator.passwordGenerator;

    private String key;

    private static final String explicationMessage = """
        Le codage césar.
                
        Le codage César est une méthode de chiffrement de texte.
                
        Le principe est simple, remplacer chaque lettre de l'alphabet d'un message par la n-ème lettre suivant.
                
        Exemple avec un décalage de 3:
            Avant -> Après
            A     -> D
            B     -> E
            C     -> F
            ABC   -> DEF
            SALUT -> VDOXW
            Z     -> C
                
        Utilisez 'start' pour passer à la suite.
                
        """;


    private static final String explicationStart = """
        La valeur de retour de 'start' contient les consignes pour résoudre ce challenge.
                
        Mais attention ces consignes sont chiffrées à l'aide du codage césar.
                
        Vous devez donc déchiffrer les consignes pour passer à la suite !
                
        !! IMPORTANT !! : Le décalage des consignes est de 8. Uniquement les caractères de la table ascii seront utilisés.
        """;

    private static final String dataStart = """
        Bravo !!
                
        Vous avez reussit a dechiffrer la consigne.
                
        Vous allez pouvoir maintenant passer a la suite du challenge.
                
        Nous avons une clef permettant de dechiffrer la base de donnee de Dev'Area, Edifay a chiffrer cette clef a l'aide du codage Cesar. 
                
        Malheureusement il a oublie quel decalage a ete utilise pour la chiffrer :/.
                
        Nous allons avoir besoin de vous pour nous envoyer tout les decalages possibles.
                
        Envoyer 'clef' (submit) au serveur, la valeur retourner sera la clef chiffre par Edifay.
        """;

    private static final String explicationGuess = """
    Soumettez tout les décalages possibles de la clef reçue, tant que la valeur de retour du serveur n'est pas 'OK'.
    """;

    public Cesar(String name, Session session) {
        super(name, session);
    }

    @Override
    public SimplePacket onLoad() {
        return new SimplePacket("", explicationMessage);
    }

    @Challenge.Controller(name = "start", freeToUse = true)
    public SimplePacket start(SimplePacket packet) {
        setState("clef");
        return new SimplePacket(slideText(dataStart, 8), explicationStart);
    }

    @Challenge.Controller(name = "clef", freeToUse = false)
    public SimplePacket clef(SimplePacket packet) {
        if (!packet.getData().equalsIgnoreCase("clef"))
            return new SimplePacket("", "Mauvaise valeur reçue.");

        key = keyGen.generate(60);
        Random rand = new Random();

        setState("guess");

        return new SimplePacket(slideText(key, rand.nextInt(26)), explicationGuess);
    }

    @Challenge.Controller(name = "guess", freeToUse = false)
    public SimplePacket guess(SimplePacket packet) {
        if(packet.getData().equals(key)) {
            this.validate();
            return new SimplePacket("OK", "Bravo vous avez trouvé la bonne clef ! Nous allons pouvoir récupérer notre base de donnée !");
        }
        return new SimplePacket("", "Cela n'as pas l'air d'être la bonne clef...");
    }


    public static String slideText(final String message, int move) {
        StringBuilder builder = new StringBuilder();

        message.chars().mapToObj((IntFunction<? extends Character>) operand -> {
            if (operand >= 'a' && operand <= 'z') {
                return (char) (((operand - 'a' + move + 26) % 26) + 'a');
            } else if (operand >= 'A' && operand <= 'Z') {
                return (char) (((operand - 'A' + move + 26) % 26) + 'A');
            }
            return (char) operand;
        }).forEach(builder::append);

        return builder.toString();
    }
}
