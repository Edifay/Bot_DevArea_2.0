package devarea.fr.web.challenges.created;

import devarea.fr.discord.Core;
import devarea.fr.web.challenges.Challenge;
import devarea.fr.web.challenges.ChallengeErrorException;
import devarea.fr.web.challenges.Session;
import devarea.fr.web.challenges.SimplePacket;
import devarea.fr.web.challenges.created.tools.parsers.ParserArrayString;

import java.nio.charset.MalformedInputException;
import java.util.Random;

@Challenge.ChallengeDefinition(name = "ListeDeTexte", requiredChallenge = {"ListeDeNombres"})
public class ListeDeTexte extends Challenge {
    public ListeDeTexte(String name, Session session) {
        super(name, session);
    }

    private static final String explicationLoad = """
        Le but de cette branche de challenge est toujours de créer les outils nous permettant de dialoguer avec le serveur.
                
        Dans le challenge précédent nous avons créé un parser pour les listes de nombres. Ici nous allons créer un parser pour les chaines de caractères.
                
        Mais cela va être un tout petit peu plus complexe. Pour des raisons de praticité, une liste de string sera représenté par une liste de liste de nombre.
                
        Par exemple :
                
        L'objet informatique contenant la liste :
          - ["oui", "non", "yes", "no"].
        Sera représenté par ce texte :
          - 4 [3 [111, 117, 105], 3 [110, 111, 110], 3 [121, 101, 115], 2 [110, 111]]
           
        3 [111, 117, 105] -> "oui"
        3 [110, 111, 110] -> "non"
        3 [121, 101, 115] -> "yes"
        2 [110, 111] -> "no"
                
        Le challenge du codage 'Cesar' peut aider à comprendre le principe. Ici chaque lettre est représenté par sa valeur dans la table ascii.
                
        Le but est donc d'obtenir l'objet informatique :
           - [[111, 117, 105], [110, 111, 110], [121, 101, 115], [110, 111]]
        Puis de le convertir pour obtenir l'objet informatique voulu :
           - ["oui", "non", "yes", "no"]
           
        Indice: Il est conseiller de réutiliser les fonctions utilisé dans le challenge précédent. Le format des listes de nombres est le même que précédemment.
                
        Utilisez 'start' pour commencer le challenge !
        """;

    private static final String explicationStart = """
        Vous allez recevoir 20 listes, vous devez parser la liste (essayez de ne pas utiliser des fonctions déjà faites je pense pour python ;) ).
                
        Ce que vous devez renvoyé au serveur est le mot de taille n, correspondant à la taille de la liste reçu, contenant au n-ème caractère la n-ème lettre du n-ème mot.
                
        Prenons la liste : ["oui", "non", "yes", "no"]
                    index:   0      1      2      3
                
        Le mot à renvoyer au serveur sera:
           - lettre index 0 du mot index 0 : 'o'.
           - lettre index 1 du mot index 1 : 'o'.
           - lettre index 2 du mot index 2 : 's'.
           - lettre index 3 du mot index 3 : 'o'.
           
        !!! ATTENTION !!! : L'index de la lettre reviens à 0 lorsqu'elle dépasse la taille du mot.
                
         >> 4 ["oui", "non", "yes", "no"]
         << ooso
                
        Le résultat de 'start' contient la première liste.
        """;


    private static final String explicationCheckReader = """
        Bravo ! Vous avez passé la première étape !
                
        La deuxième étape consiste en l'action inverse du parser. Le but est de pouvoir créer une fonction qui à partir d'une liste de string retourne une chaine de caractère pouvant être envoyé au serveur.
                
        Vous allez recevoir 20 chaines de caractères contenant chacune une phrase.
                
        Le but ici est de renvoyer la liste des mots séparés par des espaces.
                
        Par exemple :
                
        "bonjour devarea !" -> ["bonjour", "devarea", "!"]
                
        ["bonjour", "devarea", "!"] est l'objet informatique. Je pense que vous savez sous quel format envoyer celle-ci.
                
        La première phrase est :
                
        "oui non yes no"
        """;

    private int currentAsked;
    private String[] currentSent;
    private static final String[] firstList = {"oui", "non", "yes", "no"};

    @Override
    public SimplePacket onLoad() {
        return new SimplePacket("", explicationLoad);
    }

    @Controller(name = "start", freeToUse = true)
    public SimplePacket start(final SimplePacket packet) throws Exception {
        this.setState("checkParser");
        this.currentSent = firstList;
        this.currentAsked = 0;
        return new SimplePacket(ParserArrayString.encodeStringList(firstList), explicationStart);
    }

    @Controller(name = "checkParser", freeToUse = false)
    public SimplePacket checkParser(final SimplePacket packet) throws Exception {
        String oracle = applyOperations(this.currentSent);
        String result = packet.getData();

        int resultStatus;
        if ((resultStatus = compareStrings(result, oracle)) != -2) {
            this.fail();
            if (resultStatus == -1) {
                return new SimplePacket("", "Le résultat attendu n'est pas de la bonne taille ! Vous avez perdu");
            } else {
                return new SimplePacket("", "Le résultat attendu n'est pas le bon ! Reçu : " + subStringArroundOfLength(result, resultStatus, 10) + " attendu : " + subStringArroundOfLength(oracle, resultStatus, 10) + ". Vous avez perdu !");
            }
        }

        this.currentAsked++;

        Random rand = new Random();

        if (this.currentAsked == 20) {
            setState("checkReader");
            this.currentAsked = 1;
            this.currentSent = firstList;
            return new SimplePacket(joinList(this.currentSent), explicationCheckReader);
        } else if (this.currentAsked == 19) {
            this.currentSent = new String[0];
            return new SimplePacket(ParserArrayString.encodeStringList(this.currentSent), ParserArrayString.encodeStringList(this.currentSent));
        } else if (this.currentAsked > 15) {
            this.currentSent = pickNRandomWord(rand.nextInt(2000, 3000));
            return new SimplePacket(ParserArrayString.encodeStringList(this.currentSent), "Le contenu du message est trop long pour être affiché.");
        } else if (this.currentAsked > 10) {
            this.currentSent = pickNRandomWord(rand.nextInt(1000, 2000));
            return new SimplePacket(ParserArrayString.encodeStringList(this.currentSent), "Le contenu du message est trop long pour être affiché.");
        } else {
            this.currentSent = pickNRandomWord(rand.nextInt(4, 10));
            return new SimplePacket(ParserArrayString.encodeStringList(this.currentSent), ParserArrayString.encodeStringList(this.currentSent));
        }
    }

    @Controller(name = "checkReader", freeToUse = false)
    public SimplePacket checkReader(final SimplePacket packet) throws MalformedInputException {

        String[] result;
        try {
            result = ParserArrayString.parseStringList(packet.getData());
        } catch (ChallengeErrorException e) {
            this.fail();
            return new SimplePacket("", e.getMessage());
        }

        int resultStatus;
        if ((resultStatus = compareList(result, this.currentSent)) != -2) {
            this.fail();
            if (resultStatus == -1) {
                return new SimplePacket("", "Le résultat attendu n'est pas de la bonne taille ! Vous avez perdu");
            } else {
                return new SimplePacket("", "Le résultat attendu n'est pas le bon ! Index " + resultStatus + " reçu : " + subStringArroundOfLength(result[resultStatus], 0, 30) + " attendu : " + subStringArroundOfLength(this.currentSent[resultStatus], 0, 30) + ". Vous avez perdu !");
            }
        }

        this.currentAsked++;

        Random rand = new Random();

        if (this.currentAsked == 20) {
            this.validate();
            return new SimplePacket("", "Bravo vous avez réussi le challenge !");
        } else if (this.currentAsked == 19) {
            this.currentSent = new String[0];
            return new SimplePacket(joinList(this.currentSent), joinList(this.currentSent));
        } else if (this.currentAsked > 15) {
            this.currentSent = pickNRandomWord(rand.nextInt(2000, 3000));
            return new SimplePacket(joinList(this.currentSent), "Le contenu du message est trop long pour être affiché.");
        } else if (this.currentAsked > 10) {
            this.currentSent = pickNRandomWord(rand.nextInt(100, 200));
            return new SimplePacket(joinList(this.currentSent), "Le contenu du message est trop long pour être affiché.");
        } else {
            this.currentSent = pickNRandomWord(rand.nextInt(4, 10));
            return new SimplePacket(joinList(this.currentSent), joinList(this.currentSent));
        }

    }


    private static String applyOperations(final String[] list) {
        StringBuilder builder = new StringBuilder(list.length);

        for (int i = 0; i < list.length; i++) {
            builder.append(list[i].charAt(i % list[i].length()));
        }

        return builder.toString();
    }

    /**
     * @param first  first
     * @param second second
     * @return -1 if the length is not the same ; -2 equals ; val > 0 the first index fail.
     */
    private static int compareStrings(final String first, final String second) {
        if (first.length() != second.length()) {
            return -1;
        }

        for (int i = 0; i < first.length(); i++) {
            if (first.charAt(i) != second.charAt(i)) {
                return i;
            }
        }

        return -2;
    }

    private static String subStringArroundOfLength(final String text, final int offset, final int length) {
        int before = length / 2;
        int after = (int) Math.ceil(length / 2d);

        boolean beforeChanged = offset - before < 0;
        boolean afterChanged = offset + after > text.length();

        while (offset - before < 0) {
            before--;
        }

        while (offset + after > text.length()) {
            after--;
        }

        return (beforeChanged ? "" : "...") + text.substring(offset - before, offset + after) + (afterChanged ? "" : "...");
    }


    private static String[] pickNRandomWord(final int number) {
        String[] str = new String[number];
        Random rand = new Random();
        for (int i = 0; i < number; i++) {
            str[i] = Core.mots.get(rand.nextInt(0, Core.mots.size()));
        }
        return str;
    }

    private static String joinList(final String[] str) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < str.length; i++) {
            builder.append(str[i]);
            if (str.length - 1 != i) {
                builder.append(' ');
            }
        }


        return builder.toString();
    }


    private static int compareList(final String[] first, final String[] second) {
        if (first.length != second.length)
            return -1;

        for (int i = 0; i < first.length; i++) {
            if (!first[i].equals(second[i])) {
                return i;
            }
        }

        return -2;

    }


}
