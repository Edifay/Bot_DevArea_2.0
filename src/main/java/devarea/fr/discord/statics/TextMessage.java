package devarea.fr.discord.statics;

import devarea.fr.db.data.DBMission;
import devarea.fr.discord.Core;
import discord4j.common.util.Snowflake;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.MessageCreateSpec;

import java.time.Instant;
import java.util.Calendar;
import java.util.TimeZone;

import static devarea.fr.discord.statics.DefaultData.DOMAIN_NAME;

public class TextMessage {

    /*
        Ce message est envoyé aux personnes qui envoient des messages au bot en privé.
     */
    public static final String messageDisableInPrivate = "`Les commandes ne sont pas activées dans les messages " +
            "privés.`";
    /*
        Ce message est envoyé dans un embed quand la commande n'existe pas
     */
    public static final String commandNotFound = "La commande que vous avez demandée n'existe pas !";
    /*
        Message quand l'utilisateur n'a pas la permission de faire la commande qu'il a demandé
     */
    public static final String haventPermission = "Vous n'avez pas la permission d'exécuter cette commande !";
    /*
        Quand le bot s'arrête !
     */
    public static final String stopCommand = "Le bot a été arrêté ! :o:";
    /*
        Quand l'utilisateur n'a pas mis d'argument alors que la commande en demandait
     */
    public static final String errorNeedArguments = "Vous devez mettre du texte après la commande !";
    /*
        Le premier message lors du questionnaire de bienvenue
     */
    public static final String firstText = "Pour que tu puisses bien t'intégrer au serveur, je vais te donner " +
            "quelques informations, et t'en demander quelques-unes pour que je puisse bien te diriger !\n\nTu en as " +
            "pour maximum **2min**, mais attention, tu seras kick après 10min sans avoir complété le questionnaire " +
            "!\n\nPour passer à la suite il te faut réagir <:ayy:" + Core.data.yes.asString() + "> ! Bonne " +
            "chance !";
    /*
        Les règles pour que la communication lors du code soit correcte
     */
    public static final String rulesForSpeakCode = "Pour pouvoir discuter efficacement avec la communauté voici " +
            "quelques règles de base :\n\n- Essaye de parler avec un bon français.\n\n" + "- Le code ne doit pas être" +
            " envoyé en brut. Tu peux utiliser \\`\\`\\`code\\`\\`\\` => ```code``` pour les petits codes (moins de " +
            "2000 caractères). Pour les codes plus grands tu peux utiliser des sites externes comme hastbin/pastebin" +
            ".\n\n" + "- Pose ta question directement, pas de \"**Quelqu'un peut m'aider ?**\" ou autres questions " +
            "sans intérêt. Pose directement ta question dans le channel adapté, avec du code et les recherches que tu" +
            " as effectuées (avant de poser une question, va regarder rapidement sur google pour être sûr que la " +
            "réponse n'y est pas déjà).\n\n" + "- Fais attention où tu parles ! **ATTENTION** : le serveur est très " +
            "structuré, pour la bonne compréhension essaye de respecter les channels.\n\nSi tu as bien lu ces règles," +
            " réagis avec <:ayy:" + Core.data.yes.asString() + ">";
    /*
        Les règles pour demander des missions
     */
    public static final String rulesForAskCode = "Si tu es sur ce serveur c'est donc que tu as besoin de développeurs" +
            ". \nLes channels : <#" + Core.data.freelance_channel.asString() + "> et <#" + Core.data.paidMissions_channel.asString() + "> te permettent de proposer des missions/projets. \n\nCliques sur : <:ayy:" + Core.data.yes.asString() + "> pour accéder à la suite.";
    /*
        Le message de règles
     */
    public static final String rules = "Tu dois maintenant accepter les règles :" + "\n\nLes règles classiques " +
            "s'appliquent sur ce serveur, la sanction pour toute forme de violation de ces règles de vie, sera pour " +
            "tout le monde le bannissement." + "\n\nNous n'aiderons en aucun cas, à la production de logiciel " +
            "malveillant, programme lié au darknet, recherche de failles de sécurité ou autres mauvaises intentions." + "\n\nLe serveur ne prend pas en charge la sûreté des missions payantes, en effet le serveur ne s'implique en aucun cas de la fiabilité du client ou du développeur." + "\n\nN'hésitez pas à venir dire Salut dans le channel général. D'autres personnes seront certainement là pour vous expliquer le fonctionnement du discord." + "\n\nCliques sur : <:ayy:" + Core.data.yes.asString() + "> pour accéder à la suite.";
    /*
        Le message mettant en avant le channel présentation
     */
    public static final String presentation = "Avant d'aller parler dans les channels et de rencontrer les membres de" +
            " la communauté, essaye de faire une présentation de toi qui permettra d'entamer la discussion, et d'en " +
            "savoir un peu plus sur toi ! <#" + Core.data.presentation_channel.asString() + ">" + "\n\nClique sur" +
            " : <:ayy:" + Core.data.yes.asString() + "> pour accéder à la suite.";
    /*
        Le message mettant en avant le channel des rôles
     */
    public static final String roles = "Tu as maintenant accès au <#" + Core.data.roles_channel.asString() + ">, " +
            "tu dois choisir tes rôles avec précision, **attention cela est la base du serveur**.\n\nJe te donne accès au " +
            "serveur dans 30 secondes (ne t'inquiète pas si tu prends plus que 30 secondes tu as tout le temps qu'il " +
            "te faut) tu as donc le temps de prendre tes <#" + Core.data.roles_channel.asString() + ">.\n" +
            "\nBienvenue !";
    /*
        le message lors de la commande help
     */
    public static final EmbedCreateSpec helpEmbed = EmbedCreateSpec.builder()
            .title("Voici la liste des commandes :")
            .description("Voici la liste de toutes les commandes :")
            .addField("Les commandes globales :", "`/help` -> donne cette liste.\n`/ping` -> donne le temps de " +
                            "latence du bot.\n`/start` -> envoie un message qui permet de bien commencer dans un langage.\n"
                    , false)
            .addField("Communication :", "`/devhelp` -> mentionne les membres ayant pris le rôle Dev'Helper" +
                    ".\n`/ask` -> donne les informations pour bien poser une question.\n`/meetup` -> permet de " +
                    "créer un meetup autour d'un sujet.\n\nLes channels d'aides vocaux sont créés automatiquement par" +
                    " le bot lors de la connexion au channel vocal : \"Votre channel d'aide\".", false)
            .addField("Développement:", "`//run` -> exécute du code directement depuis Discord.", false)
            .addField("XP:", "`/rank` -> donne l'xp et le rang de la personne (mentionable).\n`/leaderboard` -> " +
                    "permet de voir le classement des membres du serveur en xp.\n`/askreward [mention de la personne" +
                    " que vous avez aidée]` -> Si vous ou plusieurs personnes avez aidé quelqu'un à résoudre son " +
                    "problème, vous pouvez lui demander une récompense (en xp) avec cette commande.\n`/givereward` " +
                    "-> Si une ou plusieurs personnes vous ont aidé à résoudre votre problème et que vous désirez lui" +
                    " donner une récompense (en xp), vous pouvez le faire avec cette commande. Les chiffres : pour " +
                    "10xp donnés 50xp reçus, ici le nombre d'xp est défini et inchangeable.", false)
            .addField("Développeurs <-> Clients :", "`/mission` -> permet de gérer les missions créées" +
                    ".\n`/freelance` permet de gérer le message freelance.\n\n`creationMissions` & " +
                    "`creationFreeLance` -> ne se lancent pas comme des commandes classiques, une réaction dans le " +
                    "channel : <#" + Core.data.paidMissions_channel.asString() + "> & <#" + Core.data.freelance_channel.asString() + ">" +
                    " permet de commencer la commande.\n\n", false)
            .addField("Le site", "Le serveur Dev'Area possède aussi un site internet qui permet de gérer certaines " +
                    "fonctionnalités du serveur.\n\n   -> https://devarea.fr/", false)
            .color(ColorsUsed.same)
            .timestamp(Instant.now()).build();
    /*
        Le message lors de la commande help pour les admins
     */
    public static final EmbedCreateSpec helpEmbedAdmin = EmbedCreateSpec.builder()
            .title("Voici la liste des commandes admin:")
            .description("`/send` -> permet de faire envoyer des messages aux bots.\n`/stop` -> arrête le script du" +
                    " bot.")
            .color(ColorsUsed.same)
            .timestamp(Instant.now()).build();
    /*
        Le message lors de la commande start pour le java
     */
    public static final EmbedCreateSpec startJava = EmbedCreateSpec.builder()
            .title("Java")
            .url("https://fr.wikipedia.org/wiki/Java_(langage)")
            .description("Java est un langage de programmation orienté objet créé par James Gosling et Patrick " +
                    "Naughton, employés de Sun Microsystems, avec le soutien de Bill Joy, présenté officiellement le " +
                    "23 mai 1995 au SunWorld. La société Sun a été ensuite rachetée en 2009 par la société Oracle qui" +
                    " détient et maintient désormais Java")
            .color(ColorsUsed.same)
            .addField("Installer Java", "Java a besoin d'une JVM (Java Virtual Machine), un programme qui va " +
                    "interpréter le code pour le faire exécuter par la machine. Il existe de nombreuse JVM qui ont " +
                    "chacune des avantages et des inconvénients.\n\nLa JVM \"officielle\" par Oracle: https://www" +
                    ".java.com/fr/\nUne JVM mise à jour régulièrement: https://adoptopenjdk" +
                    ".net/?variant=openjdk11&jvmVariant=openj9", false)
            .addField("Les bases", "Beaucoup de personnes veulent apprendre le java pour différentes raisons. Par " +
                    "exemple minecraft, backend site, par l'envie d'apprendre.... Mais pour toutes les raisons la " +
                    "base est le langage Java. Il faut impérativement passer par les bases pour ensuite partir dans " +
                    "une branche du java.\n\nJava est un langage populaire et connu, il y a donc de nombreuses " +
                    "ressources pour apprendre ce langage.", false)
            .addField("Les cours écrits.", "OpenClassroom: https://openclassrooms" +
                    ".com/fr/courses/26832-apprenez-a-programmer-en-java\nDeveloppez: https://java.developpez" +
                    ".com/livres-collaboratifs/javaenfants/\nZeste de savoir: https://zestedesavoir" +
                    ".com/tutoriels/646/apprenez-a-programmer-en-java/", false)
            .addField("Ou des cours vidéos...", "Les Teachers Du Net: https://www.youtube" +
                    ".com/watch?v=fmJsqBWkXm4&list=PLlxQJeQRaKDRnvgIvfHTV6ZY8M2eurH95\nEt plein d'autres je vous " +
                    "laisse chercher si cette chaîne ne vous convient pas :/", false)
            .addField("IDE (logiciels simplifiant le développement)", "Les IDE sont des logiciels très puissant, qui " +
                    "rassemblent tous les outils permettant le développement. Je vais vous en proposer 2, qui sont " +
                    "les plus connus dans le langage java.\n\nJetBrain IntelliJ: https://www.jetbrains" +
                    ".com/fr-fr/idea/download/#section=windows\nEclipse: https://www.eclipse" +
                    ".org/downloads/\n\nChoisissez celui qui vous fait le plus envie :)", false)
            .addField("Bonne Chance !", "Maintenant vous pouvez naviguer dans les tutos, cours, et vidéos pour " +
                    "apprendre le java. Le serveur est là si vous rencontrez certains problèmes.", false)
            .author(Core.client.getSelf().block().getUsername(), null, Core.client.getSelf().block().getAvatarUrl())
            .timestamp(Instant.now()).build();

    public static final EmbedCreateSpec startCSharp = EmbedCreateSpec.builder()
            .title("C#, CSharp")
            .description("C# (a prononcé C-Sharp) est un langage de programmation centré sur le paradigme Orienté " +
                    "Objet offrant des fonctionnalités d'autres paradigmes.\nDévelopper par Microsoft au début des " +
                    "années 2000 pour construire des applications Windows sans avoir à utiliser Java, et inspiré par " +
                    "le C/C++ et Java, le C# est un langage compilé qui vise le .NET, un Framework qui aide à " +
                    "construire des applications de toutes sortes et ne se limitant pas qu'au C#. (F#, VB, des " +
                    "variantes de Python, C++, etc)")
            .url("https://en.wikipedia.org/wiki/C_Sharp_(programming_language)")
            .addField("C# Pour faire quoi ?", "Le C# est un langage qui se veut simple d'utilisation, fortement typé " +
                    "et robuste.\nIl peut vous aider à créer tout types d'applications ; du site web monolithique " +
                    "utilisant Razor aux APIs performantes et facile d'implémentation, il sert aussi a créer des jeux" +
                    " grâce au moteur de jeu Unity, le CryEngine de CryTek ou encore le framework de jeu MonoGame " +
                    "basé sur le XNA de Microsoft. \nPensé avant tout pour développer des applications Windows, il " +
                    "peut aussi vous permettre de créer des Applications Mobile multi plateforme grâce à .NET MAUI" +
                    ".\n\nSupporté en premier lieu par Microsoft, depuis plusieurs années maintenant le C# tout " +
                    "autant que le .NET sont open source et gérer par des fondations externe a Microsoft.", false)
            .addField("Installer C#", "Il vous suffit de télécharger l'un des packages de développement .NET sur le " +
                    "site de Microsoft. \n.NET Core est multi plateforme et peut être utilisé sur Windows tout autant" +
                    " que Linux & MacOS.\n.NET Framework est une implémentation qui tend à être remplacé par Core, et" +
                    " est exclusif à Windows.\nVous pouvez aussi installer Visual studio, il vous permettra d'avoir " +
                    ".NET sur votre machine et un IDE puissant en plus.\n\n.NET Core & Framework => https://dotnet" +
                    ".microsoft.com/download\nVisual studio => https://visualstudio.microsoft.com/fr/", false)
            .addField("Ou apprendre le C# ?", "Le C# en trois parties (FR/EN) :\nhttps://docs.microsoft" +
                    ".com/fr-fr/learn/paths/csharp-first-steps/\nhttps://docs.microsoft" +
                    ".com/fr-fr/learn/paths/csharp-data/\nhttps://docs.microsoft" +
                    ".com/fr-fr/learn/paths/csharp-logic/\n\nLe .Net et Xamarin aka MAUI :\nhttps://docs.microsoft" +
                    ".com/fr-fr/learn/paths/build-dotnet-applications-csharp/\nhttps://docs.microsoft" +
                    ".com/fr-fr/learn/paths/build-mobile-apps-with-xamarin-forms/\n\nSources externes à Microsoft " +
                    ":\nPar Mike dane => https://www.mikedane.com/programming-languages/csharp/\n\nPar Brackeyz (8 " +
                    "vidéos courtes, c'est la première) => \nhttps://www.youtube.com/watch?v=N775KsWQVkw&t=1s\n\nSur " +
                    "OCR => https://openclassrooms.com/fr/courses/218202-apprenez-a-programmer-en-c-sur-net\n\nSur " +
                    "LearnCS => https://www.learncs.org/", false)
            .addField("Les outils", "Visual studio est l'outil de développement C# par excellence, il supporte et " +
                    "sublime la stack de technologie Microsoft au possible. JetBrains Rider est aussi un excellent " +
                    "outil de développement et totalement à niveau de VS.\nVisual studio code est plus proche d'un " +
                    "éditeur de texte qu'un IDE tel que VS ou Rider mais il offre des options de développement digne " +
                    "d'un IDE et a le mérite d'être léger pour le développement.\n\nVS et VSCode => " +
                    "https://visualstudio.microsoft.com/fr/\nRider => https://www.jetbrains.com/fr-fr/rider/", false)
            .addField("Bonne Chance !", "Maintenant vous pouvez naviguer dans les tutos, cours, et vidéos pour " +
                    "apprendre le C#. Le serveur est là si vous rencontrez certains problèmes.", false)
            .author(Core.client.getSelf().block().getUsername(), null, Core.client.getSelf().block().getAvatarUrl())
            .timestamp(Instant.now())
            .color(ColorsUsed.same).build();

    public static final EmbedCreateSpec startPython = EmbedCreateSpec.builder()
            .title("Python")
            .url("https://fr.wikipedia.org/wiki/Python_(langage)")
            .description("Python est un langage de programmation interprété, multi-paradigme et multiplateforme. Il " +
                    "favorise la programmation impérative structurée, fonctionnelle et orientée objet.")
            .color(ColorsUsed.same)
            .addField("Installer Python", "Pour pouvoir programmer en python il faut d'abord télécharger la VM,  un " +
                    "programme qui va interpréter le code et pouvoir le faire fonctionner sur la machine.\n\n Il est " +
                    "simple d'installation, il suffit d'aller sur leur site officiel (https://www.python.org/) et de " +
                    "suivre les indications du téléchargement.", false)
            .addField("Les bases", "Il y a différents moyens pour apprendre le python, de nombreux sites permettent " +
                    "l'apprentissage et la compréhension des notions liées au python.", false)
            .addField("Il y a de nombreux cours écrits, cela permet de lire et d'aller à votre rythme.",
                    "OpenClassroom: https://openclassrooms.com/fr/courses/4262331-demarrez-votre-projet-avec-python" +
                            "\nDeveloppez: https://python.developpez" +
                            ".com/tutoriels/apprendre-programmation-python/les-bases/?page=le-langage-python\nZeste " +
                            "de Savoir: https://zestedesavoir.com/tutoriels/799/apprendre-a-programmer-avec-python-3" +
                            "/", false)
            .addField("Ou encore des cours vidéos...", "CodeAvecJonathan: https://www.youtube" +
                    ".com/watch?v=oUJolR5bX6g\nYvan Monka: https://www.youtube.com/watch?v=VmOPhT4HFNE", false)
            .addField("IDE (logiciels simplifiant le développement)", "Les IDE sont des logiciels très puissant, qui " +
                    "rassemblent tous les outils permettant le développement. Je vais vous en proposer 2, qui sont " +
                    "les plus connus dans le langage python.\n\nJetBrain PyCharm: https://www.jetbrains" +
                    ".com/fr-fr/pycharm/\nVisualStudioCode: https://code.visualstudio.com/\n\nChoisissez celui qui " +
                    "vous fait le plus envie :)", false)
            .addField("Bonne Chance !", "Maintenant vous pouvez naviguer dans les tutos, cours, et vidéos pour " +
                    "apprendre le python. Le serveur est là si vous rencontrez certains problèmes.", false)
            .author(Core.client.getSelf().block().getUsername(), null, Core.client.getSelf().block().getAvatarUrl())
            .timestamp(Instant.now()).build();

    public static final EmbedCreateSpec startHtmlCss = EmbedCreateSpec.builder()
            .title("HTML / CSS")
            .description("Le HTML (HyperText Markup Language), est un langage de balisage conçu pour réaliser des " +
                    "pages web. HTML est une des trois inventions à la base du World Wide Web, avec le HyperText " +
                    "Transfer Protocol et les adresses web. HTML a été inventé pour permettre d'écrire des documents " +
                    "hypertextuels liant les différentes ressources d’Internet avec des hyperliens.")
            .color(ColorsUsed.same)
            .url("https://fr.wikipedia.org/wiki/HTML5")
            .addField("Les bases", "On apprend l'HTML et le CSS pour faire des sites web.\nCe langage est très connu " +
                    "pour le dev web.", false)
            .addField("Les cours écrits", "OpenClassroom : https://openclassrooms" +
                    ".com/fr/courses/1603881-apprenez-a-creer-votre-site-web-avec-html5-et-css3\nDeveloppez : " +
                    "https://www.developpez.com/actu/177723/Apprendre-la-programmation-Web-moins-HTML-CSS-a-travers" +
                    "-des-TD-un-tutoriel-de-Romain-Lebreton/\nZeste de savoir : https://zestedesavoir" +
                    ".com/tutoriels/599/creer-un-jeu-html5-avec-quintus/272_decouverte-de-la-librairie/1554_creer-une" +
                    "-page-html-basique/", false)
            .addField("Les cours vidéos", "Les Teachers Du Net: https://www.youtube.com/watch?v=YT7eJufmOQM\nEt plein" +
                    " d'autres je vous laisse chercher si cette chaîne ne vous convient pas :/", false)
            .addField("IDE (logiciels simplifiant le développement)", "Les IDE sont des logiciels très puissant, qui " +
                    "rassemblent tous les outils permettant le développement.\n\nVisual Studio Code : https://code" +
                    ".visualstudio.com/\nWebStorm : https://www.jetbrains.com/fr-fr/webstorm/\nSublime Text : " +
                    "https://www.sublimetext.com/\nAtom : https://atom.io/\nChoisissez celui qui vous fait le plus " +
                    "envie :)", false)
            .addField("Bonne Chance !", "Maintenant vous pouvez naviguer dans les tutos, cours, et vidéos pour " +
                    "apprendre l'HTML/CSS. Le serveur est là si vous rencontrez certains problèmes.", false)
            .author(Core.client.getSelf().block().getUsername(), null, Core.client.getSelf().block().getAvatarUrl())
            .timestamp(Instant.now()).build();


    /*
        Le message expliquant la commande start, et en disant quels sont les langages qui sont disponibles.
     */
    public static final EmbedCreateSpec startCommandExplain = EmbedCreateSpec.builder()
            .title("Start")
            .description("Cette commande permet aux débutants d'avoir les liens, et les premières indications sur un " +
                    "langage.")
            .addField("Les langages :", "Java, Python, C#, html/css, en développement....\n\nPour choisir le langage," +
                    " tapez simplement son nom et vous aurez toutes les informations.\n\nVous pouvez annuler la " +
                    "commande avec `annuler` ou `cancel`.", false)
            .color(ColorsUsed.just)
            .timestamp(Instant.now()).build();

    public static final EmbedCreateSpec runCommandExplain = EmbedCreateSpec.builder()
            .title("Run")
            .description("Cette commande permet d'exécuter du code directement depuis Discord.")
            .addField("Langages", "Plus de 30 langages sont supportés.\n`//run languages` -> voir la liste des " +
                    "langages.", false)
            .addField("Utilisation", "//run <arguments> (Optionnel)\n\\`\\`\\`<langage>\nVotre code\n" +
                    "\\`\\`\\`<entrée standard> (Optionnel)", false)
            .addField("Exemple", "//run\n\\`\\`\\`python\nprint(\"Hello World !\")\n\\`\\`\\`", false)
            .color(ColorsUsed.just)
            .build();

    public static EmbedCreateSpec meetupCommandExplain = EmbedCreateSpec.builder()
            .author("Les meetups sont des rencontres vocales/écrites sur un sujet créé par la communauté.", null, null)
            .title("Commandes")
            .description("`create` => Permet de créer un nouveau meetup.\n`delete` => Permet de supprimer un meetup " +
                    "que vous avez créé.\n`channel` => renvoie vers le channel des meetups.\n\nVous pouvez annuler la" +
                    " commande à tout moment avec `cancel` ou `annuler`.")
            .timestamp(Instant.now())
            .color(ColorsUsed.just).build();

    public static final EmbedCreateSpec meetupCreateGetDescription = EmbedCreateSpec.builder()
            .title("Sujet")
            .description("Quel est le sujet de votre meetup, le but que vous voulez accomplir lors de ce regroupement" +
                    " ? Décrivez brièvement, mais avec toutes les informations nécessaires, vous ne pourrez pas " +
                    "ajouter de description par la suite !")
            .footer("Vous pouvez annuler | cancel", null)
            .color(ColorsUsed.just).build();

    public static final EmbedCreateSpec meetupCreateGetDate = EmbedCreateSpec.builder()
            .title("Date")
            .description("Quand voulez-vous organiser ce meetup. Donnez la date et l'heure sous la forme **dd/MM/yyyy" +
                    " hh:mm**. Par exemple `" + Calendar.getInstance(TimeZone.getTimeZone("GMT+2:00")).get(Calendar.DAY_OF_MONTH) + "/" + (Calendar.getInstance(TimeZone.getTimeZone("GMT+2:00")).get(Calendar.MONTH) + 1) + "/" + Calendar.getInstance(TimeZone.getTimeZone("GMT+2:00")).get(Calendar.YEAR) + " " + Calendar.getInstance(TimeZone.getTimeZone("GMT+2:00")).get(Calendar.HOUR_OF_DAY) + ":" + Calendar.getInstance(TimeZone.getTimeZone("GMT+2:00")).get(Calendar.MINUTE) + "`.")
            .footer("cancel | annuler pour quitter.", null)
            .color(ColorsUsed.just).build();

    public static final EmbedCreateSpec meetupCreateGetImage = EmbedCreateSpec.builder()
            .title("Image")
            .description("Vous avez la possibilité d'ajouter une image de présentation au meeetup !\nSi vous ne " +
                    "voulez pas ajouter d'image répondez `non`, sinon envoyez votre image.")
            .footer("cancel | annuler pour quitter.", null)
            .color(ColorsUsed.just).build();


    public static final EmbedCreateSpec meetupCreateAsk = EmbedCreateSpec.builder()
            .title("Votre demande a été transmise !")
            .description("Le staff va analyser votre demande, vous recevrez un message pour vous tenir au courant de " +
                    "son avancement !")
            .color(ColorsUsed.just)
            .timestamp(Instant.now()).build();

    public static final EmbedCreateSpec missionTitle = EmbedCreateSpec.builder()
            .title("Creation d'une mission")
            .description("Donnez un titre à votre mission !")
            .footer("cancel | annuler pour quitter.", null)
            .color(ColorsUsed.just).build();

    public static final EmbedCreateSpec missionDescription = EmbedCreateSpec.builder()
            .title("Creation d'une mission")
            .description("Donnez une description complète de votre mission, les aspects difficiles, et le contexte.")
            .footer("cancel | annuler pour quitter.", null)
            .color(ColorsUsed.just).build();

    public static final EmbedCreateSpec missionSupport = EmbedCreateSpec.builder()
            .title("Creation d'une mission")
            .description("Donnez le support sur lequel vous voulez que votre programme fonctionne. Web, linux, " +
                    "windows, discord.js....")
            .footer("cancel | annuler pour quitter.", null)
            .color(ColorsUsed.just).build();

    public static final EmbedCreateSpec missionLangage = EmbedCreateSpec.builder()
            .title("Creation d'une mission")
            .description("Donnez le langage de programmation que vous voulez (si vous n'en avez pas vous pouvez " +
                    "marquer `aucune préférence`).")
            .footer("cancel | annuler pour quitter.", null)
            .color(ColorsUsed.just).build();

    public static final EmbedCreateSpec missionPrix = EmbedCreateSpec.builder()
            .title("Creation d'une mission")
            .description("Donnez le prix/budget que vous pouvez mettre dans cette mission ! (Pensez à mettre la " +
                    "devise)")
            .footer("cancel | annuler pour quitter.", null)
            .color(ColorsUsed.just).build();

    public static final EmbedCreateSpec missionDate = EmbedCreateSpec.builder()
            .title("Creation d'une mission")
            .description("Donnez la date de retour de la mission. Elle peut être `non définie`.")
            .footer("cancel | annuler pour quitter.", null)
            .color(ColorsUsed.just).build();

    public static final EmbedCreateSpec missionNiveau = EmbedCreateSpec.builder()
            .title("Creation d'une mission")
            .description("Donnez le niveau de difficulté de la mission (estimation).")
            .footer("cancel | annuler pour quitter.", null)
            .color(ColorsUsed.just).build();

    public static final MessageCreateSpec freelanceBottomMessage = MessageCreateSpec.builder()
            .addEmbed(EmbedCreateSpec.builder()
                    .title("Proposez vos services !")
                    .description("Cliquez sur le bouton ci-dessous pour créer une page freelance " +
                            "!\n\nVisionner les freelances sur le web -> " + DOMAIN_NAME + "freelances")
                    .color(ColorsUsed.same)
                    .build())
            .addComponent(ActionRow.of(Button.secondary("create_freelance_redirect", "Créer une page freelance.")))
            .build();

    public static final MessageCreateSpec missionBottomMessage = MessageCreateSpec.builder()
            .addEmbed(EmbedCreateSpec.builder().color(ColorsUsed.same)
                    .title("Créer une mission.")
                    .description("Cliquez sur le bouton ci-dessous pour créer une mission !" +
                            "!\n\nVisionner les missions sur web -> " + DOMAIN_NAME + "missions")
                    .build())
            .addComponent(ActionRow.of(Button.secondary("create_mission_redirect", "Créer une mission."))).build();

    public static final MessageCreateSpec presentationBottomMessage = MessageCreateSpec.builder()
            .addEmbed(EmbedCreateSpec.builder().color(ColorsUsed.same)
                    .title("Créer une présentation ?")
                    .description("Cliquez sur le bouton ci-dessous, plus complétez la case ** *Plus sur moi...* **.")
                    .build())
            .addComponent(ActionRow.of(Button.secondary("presentation_redirect", "Créer sa présentation."))).build();

    public static MessageCreateSpec missionFollowedCloseIn1Hour(String memberID) {
        return MessageCreateSpec.builder()
                .addEmbed(EmbedCreateSpec.builder()
                        .title("Clôture du Suivi de mission.")
                        .description("La clôture du suivi a été exécutée par : <@" + memberID + ">. " +
                                "Le suivi fermera dans 1 heure.")
                        .color(ColorsUsed.same)
                        .timestamp(Instant.now())
                        .build())
                .build();
    }

    public static MessageCreateSpec missionFollowedCreateMessageExplication(Snowflake member_react_id, DBMission mission) {
        return MessageCreateSpec.builder()
                .content("<@" + member_react_id.asString() + "> -> <@" + mission.getCreatedById() + ">")
                .addEmbed(EmbedCreateSpec.builder()
                        .title("Suivi de Mission !")
                        .description("Bienvenue dans ce channel !\n\n" +
                                "Ce channel a été créé car <@" + member_react_id.asString() + "> est intéressé " +
                                "par la mission de <@" + mission.getCreatedById() + ">." +
                                "\n\nCe channel est dédié pour vous, ainsi qu'à la mise en place de la mission et" +
                                " nous vous demandons de passer exclusivement par ce channel pour toute " +
                                "discussion à propos de celle-ci." +
                                "\n\nCeci a pour but d'augmenter la fiabilité des clients et des développeurs " +
                                "pour qu'une mission puisse se passer de la meilleure des manières" +
                                ".\nRèglementation des missions : <#768435208906735656>." +
                                "\n\nVous pouvez clôturer ce channel à tout moment !")
                        .color(ColorsUsed.same)
                        .build())
                .addComponent(ActionRow.of(Button.secondary("followMission_close", "Cloturer le channel")))
                .build();
    }

    public static MessageCreateSpec missionFollowMissionPreview(DBMission mission) {
        return MessageCreateSpec.builder()
                .addEmbed(EmbedCreateSpec.builder()
                        .title(mission.getTitle())
                        .description(mission.getDescription() + "\n\nPrix: " + mission.getBudget() + "\nDate " +
                                "de retour: " + mission.getDeadLine() + "\nType de support: " + mission.getSupport()
                                + "\nLangage: " + mission.getLanguage() + "\nNiveau estimé: " + mission.getDifficulty())
                        .color(ColorsUsed.just)
                        .build())
                .build();
    }

    public static InteractionApplicationCommandCallbackSpec cannotFollowYourOwnMission = InteractionApplicationCommandCallbackSpec.builder()
            .ephemeral(true)
            .addEmbed(EmbedCreateSpec.builder()
                    .title("Erreur !")
                    .description("Vous ne pouvez pas prendre votre propre mission !")
                    .color(ColorsUsed.wrong)
                    .build())
            .build();

    public static InteractionApplicationCommandCallbackSpec alreadyFollowingThisMission = InteractionApplicationCommandCallbackSpec.builder()
            .ephemeral(true)
            .addEmbed(EmbedCreateSpec.builder()
                    .title("Erreur !")
                    .description("Vous suivez déjà cette mission !")
                    .color(ColorsUsed.wrong)
                    .build())
            .build();
}
