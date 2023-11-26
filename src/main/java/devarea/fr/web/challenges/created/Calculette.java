package devarea.fr.web.challenges.created;

import devarea.fr.web.challenges.Challenge;
import devarea.fr.web.challenges.Session;
import devarea.fr.web.challenges.SimplePacket;

import java.util.Random;

@Challenge.ChallengeDefinition(name = "Calculette", requiredChallenge = {"PlusOuMoins"})
public class Calculette extends Challenge {


    private static final String onLoadExplication = """
        Bienvenue dans le challenge 'Calculette' !
                
        Le but de ce challenge est de créer un petit programme pouvant résoudre des problèmes mathématiques simple.
                
        Exemple:
         - '3 + 5' -> 8
         - '3 * 5' -> 15
         - '3 - 5' -> 2
         - '12 * 3' -> 36
         
        Les opérations à implémenter dans votre calculette seront : '+', '-', '*'. A noter que le calculs contiendra des nombres entiers (composé d'un nombre de chiffres différents).
                
        Utilisez 'start' pour passer à la suite !
        """;

    public Calculette(String name, Session session) {
        super(name, session);
    }

    @Override
    public SimplePacket onLoad() {
        return new SimplePacket("", onLoadExplication);
    }

    private final static String startExplication = """
        Un total de 20 calculs vous sera demandé. Le premier test n'est pas généré aléatoirement.
                
        La valeur retournée par 'start' est '3 + 5'.
                
        INDICE: Vous devez envoyer '8' pour passer au calcul suivant, la valeur de retour de 'submit' sera le prochain calcul.
        """;

    private int i = 1;

    private String currentCalculusResult;
    private String currentCalculus;

    @Controller(name = "start", freeToUse = true)
    public SimplePacket start(final SimplePacket packet) {
        setState("result");
        this.currentCalculus = "3 + 5";
        this.currentCalculusResult = "8";
        return new SimplePacket(this.currentCalculus, "");
    }


    @Controller(name = "result", freeToUse = false)
    public SimplePacket result(final SimplePacket packet) {
        if (packet.getData().trim().equalsIgnoreCase(this.currentCalculusResult)) {
            this.fail();
            return new SimplePacket("", "Vous avez raté le challenge ! Ce n'est pas la réponses attendue à '" + this.currentCalculus + "'.");
        }
        if (this.i == 20) {
            this.validate();
            return new SimplePacket("", "Bravo ! Vous avez validé le challenge. Vous êtes un petit génie des maths !");
        }
        this.i++;
        this.genNewCalcul();
        return new SimplePacket(this.currentCalculus, this.currentCalculus);
    }


    private void genNewCalcul() {
        Random random = new Random();
        int a;
        int b;

        if (this.i < 10) {
            a = random.nextInt(50);
            b = random.nextInt(50);
        } else {
            a = random.nextInt(500);
            b = random.nextInt(500);
        }

        char operator;

        int operatorIndex = random.nextInt(3);
        switch (operatorIndex) {
            case 1:
                operator = '-';
                this.currentCalculusResult = String.valueOf(a - b);
                break;
            case 2:
                operator = '*';
                this.currentCalculusResult = String.valueOf(a * b);
                break;
            default:
                operator = '+';
                this.currentCalculusResult = String.valueOf(a + b);
                break;
        }

        this.currentCalculus = a + " " + operator + " " + b;
    }
}
