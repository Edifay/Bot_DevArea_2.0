package devarea.fr.web.challenges.created;

import devarea.fr.discord.Core;
import devarea.fr.web.challenges.Challenge;
import devarea.fr.web.challenges.Session;
import devarea.fr.web.challenges.SimplePacket;
import devarea.fr.web.challenges.created.tools.parsers.ParserArrayString;

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
            return new SimplePacket("", "");
        } else if (this.currentAsked == 19) {
            this.currentSent = new String[0];
            return new SimplePacket(ParserArrayString.encodeStringList(this.currentSent), ParserArrayString.encodeStringList(this.currentSent));
        } else if (this.currentAsked > 15) {
            String[] tab = pickNRandomWord(rand.nextInt(2000, 3000));
            this.currentSent = tab;
            return new SimplePacket(ParserArrayString.encodeStringList(tab), "Le contenu du message est trop long pour être affiché.");
        } else if (this.currentAsked > 10) {
            String[] tab = pickNRandomWord(rand.nextInt(1000, 2000));
            this.currentSent = tab;
            return new SimplePacket(ParserArrayString.encodeStringList(tab), "Le contenu du message est trop long pour être affiché.");
        } else {
            String[] tab = pickNRandomWord(rand.nextInt(4, 10));
            this.currentSent = tab;
            return new SimplePacket(ParserArrayString.encodeStringList(tab), ParserArrayString.encodeStringList(tab));
        }
    }

    @Controller(name = "checkReader", freeToUse = false)
    public SimplePacket checkReader(final SimplePacket packet) {

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


}
