package devarea.fr.web.challenges.created;

import devarea.fr.web.challenges.Challenge;
import devarea.fr.web.challenges.Session;
import devarea.fr.web.challenges.SimplePacket;
import devarea.fr.web.challenges.created.tools.parsers.ParserIntArray;

import java.util.Random;

@Challenge.ChallengeDefinition(name = "ListeDeNombres")
public class ListeDeNombres extends Challenge {

    public ListeDeNombres(String name, Session session) {
        super(name, session);
    }

    private static final String onLoadExplication = """
                
        Cette branche des challenges rentre rapidement dans le vif du sujet.
        Si vous êtes débutant je vous conseille de finir complètement la branche 'tutoriel'. Pour les plus avertit je vous invite à réaliser au moins le challenge 'tutoriel' pour comprendre le fonctionnement du client.
                
        Cette branche va vous permettre de créer certains outils qui vous seront utiles dans la suite des challenges.
                
        Je vous conseille donc de prendre le temps de faire un code bien propre et modulaire pour qu'il puisse être réutiliser facilement dans d'autres parties du code.
                
        Comme le nom peut le laisser deviner le premier outil à créer sera un parser de
        liste/tableau.
                
        Le but sera de pouvoir récupérer une liste sous forme de texte et de pouvoir la transformer en objet informatique utilisable.
                
        Le format utilisé par le serveur sera :
                
          n [e1, e2, ..... , e(n-1), en]
                
          n -> étant le nombre d'élément de la liste.
          [e1, ... , en] -> la liste elle même avec chaque élément séparé par une virgule.
                
        Un exemple concrêt :
                
        6 [0, 1, 2, 3, 4, 5] -> est un format valide.
                
        !!! ATTENTION !!!
        6 [0,1,2,3,4,5] -> est aussi un format valide et représente la même liste.
                
        Utilisez 'start' pour commencer le challenge.
                
        """;

    private static final String startExplication = """
        Les listes de nombres.
                
        Vous allez recevoir un total de 20 listes, contenant uniquement des nombres. Il faudra renvoyer la somme des nombres de la liste.
                
        Par exemple :
                
        << 6 [0, 1, 2, 3, 4, 5]
        >> 15
                
        0 + 1 + 2 + 3 + 4 + 5 = 15
                
        La première liste reçu est :
                
        6 [0, 1, 2, 3, 4, 5]""";

    private static final String parserCheckValidated = """
        Bravo ! Vous avez réussi la première partie de ce challenge.
                
        Vous êtes désormais capable de parser une liste de nombre.
                
        Il faudrait maintenant que vous soyez capable de transformer un tableau de nombre en texte pour pouvoir l'envoyer au serveur !
                
        Cette opération est normalement plus facile que la précédente ! C'est pour quoi je vais rajouter une (toute petite) complexité algorithmique.
                
        Le serveur va vous envoyer 20 listes de nombre que vous allez devoir parser.
                
        Ensuite les opérations a effectuer sur ces listes seront les suivantes pour chaque nombre :
                
            - ajouter le précédent au nombre courant. (Le premier nombre ajoute le dernier.)
            - multiplier tout par 2 après l'opération précédente.
                
        Exemple :
                
         >> 6 [0, 1, 2, 3, 4, 5]
              -> Etape intermédiaires :
                  - [0 + 5, 0 + 1, 1 + 2, 2 + 3, 3 + 4, 4 + 5] x 2
                  - [5, 1, 3, 5, 7, 9] x 2
         << 6 [10, 2, 6, 10, 14, 18]
         
         
        Une fois les opérations appliquées renvoyez la liste au serveur en format texte identique à celui envoyé par le serveur.
                
        Le résultat de ce 'submit' contient déjà la première liste sur la quelle appliquer ces opérations.
                
        6 [0, 1, 2, 3, 4, 5]
        """;

    private final int[] firstList = {0, 1, 2, 3, 4, 5};
    private int result;
    private int currentAsked = 1;
    private int[] currentSent;

    @Override
    public SimplePacket onLoad() {
        return new SimplePacket("", onLoadExplication);
    }

    @Controller(name = "start", freeToUse = true)
    public SimplePacket start(final SimplePacket packet) {
        setState("parserCheck");
        result = 15;
        return new SimplePacket(ParserIntArray.intListToString(firstList), startExplication);
    }

    @Controller(name = "parserCheck", freeToUse = false)
    public SimplePacket parserCheck(final SimplePacket packet) {
        int n;
        try {
            n = Integer.parseInt(packet.getData());
        } catch (NumberFormatException e) {
            this.fail();
            return new SimplePacket("", "Le nombre n'as pas pu être lu !");
        }

        if (n != result) {
            this.fail();
            return new SimplePacket("", n + " n'était pas le résultat attendu ! Vous avez perdu ! (Résultat attendu : " + this.result + ").");
        }

        this.currentAsked++;

        if (this.currentAsked == 20) {
            setState("checkReader");
            this.currentAsked = 1;
            this.currentSent = firstList;
            return new SimplePacket(ParserIntArray.intListToString(firstList), parserCheckValidated);
        } else if (this.currentAsked == 19) {
            result = 0;
            return new SimplePacket(ParserIntArray.intListToString(new int[0]), ParserIntArray.intListToString(new int[0]));
        } else if (this.currentAsked > 15) {
            Random rand = new Random();
            int size = rand.nextInt(2000, 10000);
            int[] tab = new int[size];
            result = 0;
            for (int i = 0; i < size; i++) {
                tab[i] = rand.nextInt(-200, 200);
                result += tab[i];
            }
            return new SimplePacket(ParserIntArray.intListToString(tab), "Le contenu du message est trop long pour être affiché.");
        } else if (this.currentAsked > 10) {
            Random rand = new Random();
            int size = rand.nextInt(2000, 10000);
            int[] tab = new int[size];
            result = 0;
            for (int i = 0; i < size; i++) {
                tab[i] = rand.nextInt(0, 200);
                result += tab[i];
            }
            return new SimplePacket(ParserIntArray.intListToString(tab), "Le contenu du message est trop long pour être affiché.");
        } else {
            Random rand = new Random();
            int size = rand.nextInt(4, 30);
            int[] tab = new int[size];
            result = 0;
            for (int i = 0; i < size; i++) {
                tab[i] = rand.nextInt(0, 50);
                result += tab[i];
            }
            return new SimplePacket(ParserIntArray.intListToString(tab), ParserIntArray.intListToString(tab));
        }

    }

    @Controller(name = "checkReader", freeToUse = false)
    public SimplePacket checkReader(final SimplePacket packet) {
        int[] receivedList;
        try {
            receivedList = ParserIntArray.intListParser(packet.getData());
        } catch (NumberFormatException e) {
            this.fail();
            return new SimplePacket("", "La liste envoyé n'as pas pu être lu ! Revoyez le format d'envois ! Vous avez perdu.");
        }

        applyOperations(this.currentSent);

        int result;
        if ((result = compareList(receivedList, this.currentSent)) != -2) {
            this.fail();
            if (result == -1) {
                return new SimplePacket("", "La liste n'as pas la taille attendue. Vous avez perdu !");
            } else {
                return new SimplePacket("", "L'index " + result + " ne correspond pas avec la liste attendue. Recu : " + receivedList[result] + " attendu " + this.currentSent[result] + ". Vous avez perdu !");
            }
        }

        this.currentAsked++;

        if (this.currentAsked == 20) {
            this.validate();
            return new SimplePacket("", "Bravo vous avez passé ce challenge !");
        } else if (this.currentAsked == 19) {
            this.currentSent = new int[0];
            return new SimplePacket(ParserIntArray.intListToString(this.currentSent), ParserIntArray.intListToString(this.currentSent));
        } else if (this.currentAsked > 15) {
            Random rand = new Random();
            int size = rand.nextInt(2000, 10000);
            this.currentSent = new int[size];
            result = 0;
            for (int i = 0; i < size; i++) {
                this.currentSent[i] = rand.nextInt(-200, 200);
                result += this.currentSent[i];
            }
            return new SimplePacket(ParserIntArray.intListToString(this.currentSent), "Le contenu du message est trop long pour être affiché.");
        } else if (this.currentAsked > 10) {
            Random rand = new Random();
            int size = rand.nextInt(2000, 10000);
            this.currentSent = new int[size];
            result = 0;
            for (int i = 0; i < size; i++) {
                this.currentSent[i] = rand.nextInt(0, 200);
                result += this.currentSent[i];
            }
            return new SimplePacket(ParserIntArray.intListToString(this.currentSent), "Le contenu du message est trop long pour être affiché.");
        } else {
            Random rand = new Random();
            int size = rand.nextInt(4, 30);
            this.currentSent = new int[size];
            result = 0;
            for (int i = 0; i < size; i++) {
                this.currentSent[i] = rand.nextInt(0, 50);
                result += this.currentSent[i];
            }
            return new SimplePacket(ParserIntArray.intListToString(this.currentSent), ParserIntArray.intListToString(this.currentSent));
        }
    }


    /**
     * @param first  first
     * @param second second
     * @return -1 is not the same size ; -2 same lists ; or the index of the first difference.
     */
    private static int compareList(final int[] first, final int[] second) {
        if (first.length != second.length)
            return -1;

        for (int i = 0; i < first.length; i++) {
            if (first[i] != second[i])
                return i;
        }
        return -2;
    }

    private static void applyOperations(final int[] list) {
        if (list.length == 0)
            return;

        int last = list[list.length - 1];
        for (int i = list.length - 1; i >= 0; i--) {
            if (i == 0) {
                list[i] = (list[i] + last) * 2;
            } else {
                list[i] = (list[i] + list[i - 1]) * 2;
            }
        }
    }
}
