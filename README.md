# Dev'Area v2

Dev'Area est un serveur discord basé autour du développement. Tous les niveaux de développeurs sont les bien venus.

Ce serveur fourni des fonctionnalités supplémentaires grâce au bot qui lui est dédié. Le repo github contient le code du
bot de Dev'Area.

> Lien du discord : https://discord.gg/gpZemxGmrD

Le code ci-dessus est la 2ème version du bot, qui est la refonte complète de la [version 1](https://github.com/DevAreaServer/Bot_DevArea).

Cette version 2 a pour but de rendre le code plus portable pour les nouveautés discord. Tels que les intéractions plus complèxes `Menu`, `Boutons`, `Modals`.

Mais aussi pour rendre son code plus accessible. En effet, le code du bot a pour but d'être amélioré et maintenu par la communauté. C'est pour quoi la simplicité et la facilité de créer de nouvelles commandes a été un des objectifs principal dans la conception du bot. Et cela doit rester un des objectifs pour les modifications futures apportés au code.

# Sommaire

- [Télécharger le projet](https://github.com/Edifay/Bot_DevArea_2.0#t%C3%A9l%C3%A9charger-le-projet-get-started)
- [L'Architecture](https://github.com/Edifay/Bot_DevArea_2.0#larchitecture)
- [Ajouter une fonctionnalité](https://github.com/Edifay/Bot_DevArea_2.0#tutoriel-sur-lajout-de-fonctionnalit%C3%A9s)
- [Les Challenges](https://devarea.fr)
- [Conclusion](https://github.com/Edifay/Bot_DevArea_2.0#merci-)
- [Licence](https://github.com/Edifay/Bot_DevArea_2.0#licence)


# Télécharger le projet (get started)

Pour mettre en place l'environnement pour développer sur le serveur, il faudra mettre en places quelques petites choses.

> Pour afficher les logs dans la console, mettre à ``true`` la variable ``developing`` dans ``main.Main``.

### Cloner le repo

Tout d'abord il faudra télécharger les fichiers du projet :

 - En utilisant git ``git clone https://github.com/Edifay/Bot_DevArea_2.0.git``.
 - Ou en téléchargeant les fichiers à partir du repo ci-dessus.

### Java & Gradle

Le projet utilise le JDK 17, et le projet doit être importé comme un projet Gradle.

Toutes les librairies nécessaires au fonctionnement du bot devraient être automatiquement installées par gradle à l'aide du [build.gradle](https://github.com/Edifay/Bot_DevArea_2.0/blob/main/build.gradle).

### MongoDB

Le bot a besoin de l'url de la base de donnée, il vous sera demandé de créer un fichier ``db.url`` dans le repertoire de lancement du programme.
(Généralement au même niveau que ``build.gradle`` si l'exécution est faites dans l'IDE).

Pour obtenir l'url de la base de donnée, il faut tout d'abord installer mongodb en suivant ces étapes :

 - [Installer et lancer mongodb](https://www.mongodb.com/docs/manual/administration/install-community/)
 - Puis créer et/ou compléter le fichier ``db.url`` avec l'url vers la db.

> L'url de la db sera sous la forme :
> 
> - ``mongodb://user:password@adress:port/DEVAREA``
> 
> Généralement si mongodb est installé sur la même machine l'url par défault sera :
> 
>  - ``mongodb://localhost:27017/DEVAREA``

À noter que le bot peut tourner avec une url non valide, cependant un bon nombre de fonctionnalités ne seront pas disponibles en plus des nombreuses erreurs provoquées.

### Configurer le bot

Tout d'abord il vous faudra avoir un Bot à disposition sur un serveur discord.

 - [Créer un bot discord](https://appmaster.io/fr/blog/bot-discord-comment-le-creer-et-lajouter-au-serveur)

Après avoir lancé le projet et après avoir créé le fichier ``db.url``, un fichier ``config.json`` devrait avoir été créé à côté de celui-ci (il était possiblement déjà existant, étant fourni dans le repo).

Il permet de donner toutes les informations nécessaires au bot, tel que les channels, les rôles et plus encore.

Il suffit d'éditer le fichier et de le modifier avec les valeurs voulues.

Exemple :

 - ``"paidMissions_channel":null`` -> ``"paidMissions_channel":"943817647060025354"``.

> Pensez bien à mettre les guillemets pour les ids, ils sont traités comme des chaines de caractère et non comme des nombres.

### Token

Il faudra créer, toujours au même endroit, un fichier ``token.token`` et y mettre le token sur la première ligne.

Le token du bot est trouvable sur la page de configuration du bot créé précédemment.

### Problèmes ?

Si vous recontrez un problème dans la mise en place de l'environnement de travail sur le bot, n'hésitez pas à venir envoyer un message sur le discord dans le channel support.

# L'Architecture

L'architecture du bot est primordiale à comprendre. Le code utilise plusieurs `lib` :

- `Spring` qui sert à pour le backend du site.
- `Mongo Java Driver` qui permet la communication à la base de Donnée MongoDB.
- `Discord4J` permet de se connecter à l'api discord.
- `Reflections` sert dans le noyau de l'architecture, en permettant d'extraire des classes spécifiques.

### Le système de log

Pour clarifier et reprendre plus clair les logs, le code est fourni d'une classe `Logger`, qui possède 3 méthodes :

- `Logger.logMessage(String text)` remplace `System.out.println()`.
- `Logger.logError(String text)` remplace `System.err.println()`.
- `Logger.logTitle(String text)` permet de créer une séparation avec les messages précédents.

### Le code est séparé en 3 parties :

- Une partie dédiée à mongoDB `db`. 
- Une partie dédiée à l'api discord `discord`.
- Une partie dédiée aux requêtes du site Dev'Area `web`.

Pour ajouter des fonctionnalités au bot, il faudra principalement se rendre dans la partie `discord`.

# Tutoriel sur l'ajout de fonctionnalités

###### Dans la suite, nous allons supposer pour le chemin des répertoires que nous nous trouvons dans le repertoire ``devarea.fr``.


### Créer une commande :


Les commandes sont stockées dans le répertoire ``discord.commands.slash``.

Pour créer une nouvelle commande il suffit de créer une nouvelle `class` héritant de `SlashCommand`, dans le répertoire donné ci-dessus.

Puis d'implémenter les commandes héritant de `SlashCommand` : `definition()`, `play()`, `permissions()`.

```java
package devarea.fr.discord.commands.slash;

import devarea.fr.discord.commands.Permissions;
import devarea.fr.discord.commands.SlashCommand;
import devarea.fr.discord.entities.events_filler.SlashCommandFiller;
import discord4j.discordjson.json.ApplicationCommandRequest;

public class MaCommande extends SlashCommand {

    @Override
    public ApplicationCommandRequest definition() {
        return null;
    }

    @Override
    public void play(SlashCommandFiller filler) {

    }

    @Override
    public Permissions permissions() {
        return super.permissions();
    }
}
```

La définition de la commande est chargée au démarrage du bot avec l'appel de `definition()`.

Cette définition contient les informations de la commande slash, tel que le nom, la description, les options...

`definition()` doit être implémenté et ne peut pas retourner `null`.

```java
public class MaCommande extends SlashCommand {
    @Override
    public ApplicationCommandRequest definition() {
        return ApplicationCommandRequest.builder()
                .name("commande")
                .description("Ma commande fait...")
                .build();
    }
}
```

Ensuite des permissions peuvent être demandées pour exécuter la commande, grâce à la méthode `permissions()`. Cette méthode peut retourner `null` ou ne pas être implémentée.
Sinon voici une implémentation classique.

```java
public class MaCommande extends SlashCommand {
    @Override
    public Permissions permissions() {
        return Permissions.of(Permission.MANAGE_CHANNELS, Permission.MANAGE_CHANNELS);
    }
}
```

Le corps de la commande se situe dans la méthode `play()`, qui reçoit en paramètre l'évènement `SlashCommandFiller`. Qui contient l'évènement `ChatInputInteractionEvent` et le `Mem`.

Voici un exemple de commande :
```java
public class MaCommande extends SlashCommand {

    @Override
    public ApplicationCommandRequest definition() {
        return ApplicationCommandRequest.builder()
                .name("commande")
                .description("Ma commande fait...")
                .build();
    }

    @Override
    public void play(SlashCommandFiller filler) {
        filler.event.reply(InteractionApplicationCommandCallbackSpec.builder()
                .ephemeral(true)
                .addEmbed(EmbedCreateSpec.builder()
                        .title("Ma commande")
                        .description("Vous avez exécuté la commande !")
                        .color(ColorsUsed.same)
                        .build())
                .build()).subscribe();
    }

    @Override
    public Permissions permissions() {
        return Permissions.of(Permission.MANAGE_CHANNELS, Permission.MANAGE_CHANNELS);
    }
    
}
```

### Créer un service ou système :


Les services ou systèmes sont des parties du code qui se lancent au démarrage du bot. Et peuvent effectuer des actions très variées. Dans la suite du tuto j'appellerais ces systèmes/services des `workers`

Comme les commandes slash, les workers sont situés au même endroit dans le code pour plus de clarté, dans le repertoire `discord.workers`.

Ils sont ensuite classés dans 3 répertoires différents :

- `core`: les `workers` principaux qui constituent le noyau du bot.
- `linked`: les `workers` qui sont liés aux commandes.
- `self`: les `workers` autonomes dont le fonctionnement reste interne et n'as pas de lien avec d'autres classes (autre que `db`).


Ce classement dans les 3 repertoires ci-dessus est uniquement pour un projet plus clair. Cela ne change en aucun cas le fonctionnement des workers.

Pour créer un worker il suffit de créer une `class` et de l'implémenter de l'interface `Worker`. Et de compléter les 3 méthodes de `Worker` : `onStart()`, `setupEvent()`, `onStop()`.

```java
package devarea.fr.discord.workers;

import devarea.fr.discord.entities.ActionEvent;

public class MonWorker implements Worker {

    @Override
    public void onStart() {
        Worker.super.onStart();
    }

    @Override
    public ActionEvent<?> setupEvent() {
        return null;
    }

    @Override
    public void onStop() {

    }
}
```

Tout d'abord, la méthode `onStart()`. Cette méthode est appelée une seule fois, lors de la création des workers. C'est-à-dire au début de l'exécution du bot.

Cette méthode peu ne pas être implémenté, l'interface `worker` possède une version par default de la méthode.

```java
public class MonWorker implements Worker {
    @Override
    public void onStart() {
        Logger.logMessage("MonWorker a été créé !");
    }
}
```
La méthode `onStop()` n'a pour l'instant pas encore de "caller". C'est-à-dire qu'elle n'est jamais appelée par le noyau du bot.


La méthode `setupEvent()` est appelé juste après `onStart()` elle permet d'ajouter rapidement un "listener" sur le noyau.

En effet, le noyau est capable de distribuer les évènements provenant de l'api discord. La méthode `setupEvent()` retourne un `ActionEvent<.?>`.
L'objet `ActionEvent<?>`  correspond à une action a effectuer lors de la reception d'un event. La valeur retournée peut être `null`.

Les évènements d'écoute disponibles sont tous des objets héritant de `Filler<T>`.
Tout les fillers existants se trouvent dans `discord.entities.events_filler`.

Par exemple un `worker` qui ajoute automatiquement une action au noyau se déclare comme ceci :

```java
public class MonWorker implements Worker {
    @Override
    public ActionEvent<?> setupEvent() {
        return new ActionEvent<MessageCreateEventFiller>() {
            @Override
            public void run(MessageCreateEventFiller filler) {
                Chan<GuildMessageChannel> chan = ChannelCache.watch(filler.event.getMessage().getChannelId().asString());
                chan.entity.createMessage(MessageCreateSpec.builder()
                        .content("Vous avez envoyé un message !")
                        .build()).subscribe();
            }
        };
    }
}
```

En utilisant une expression lambda :

```java
public class MonWorker implements Worker {
    @Override
    public ActionEvent<?> setupEvent() {
        return (ActionEvent<MessageCreateEventFiller>) filler -> {
            Chan<GuildMessageChannel> chan = ChannelCache.watch(filler.event.getMessage().getChannelId().asString());
            chan.entity.createMessage(MessageCreateSpec.builder()
                    .content("Vous avez envoyé un message !")
                    .build()).subscribe();
        };
    }
}
```

Dans cet exemple, le worker écoute sur la création de nouveaux messages et envois un message dans le channel de la création du message.

Exemple complet :
```java
package devarea.fr.discord.workers;

import devarea.fr.discord.cache.ChannelCache;
import devarea.fr.discord.entities.ActionEvent;
import devarea.fr.discord.entities.Chan;
import devarea.fr.discord.entities.events_filler.MessageCreateEventFiller;
import devarea.fr.utils.Logger;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.spec.MessageCreateSpec;

public class MonWorker implements Worker {

    @Override
    public void onStart() {
        Logger.logMessage("MonWorker a été créé !");
    }

    @Override
    public ActionEvent<?> setupEvent() {
        return (ActionEvent<MessageCreateEventFiller>) filler -> {
            Chan<GuildMessageChannel> chan = ChannelCache.watch(filler.event.getMessage().getChannelId().asString());
            chan.entity.createMessage(MessageCreateSpec.builder()
                    .content("Vous avez envoyé un message !")
                    .build()).subscribe();
        };
    }

    @Override
    public void onStop() {

    }
}
```


### Le cache et les ObjectListener

Dans la conception du code, une partie très importante est le cache. Il existe 3 type de cache :

- `MemberCache`: ce cache doit être la seule façon de récupérer un membre.
- `ChannelCache`: ce cache doit être la seule façon de récupérer un channel.
- `RolesCache`: ce cache doit être la seule façon de récupérer un role.

Ces 3 caches proposent 3 méthodes principales :

- `get()`: retourne l'objet en cache. Si les données enregistrées datent de plus de 10min alors des données plus récentes sont récupérées par le bot. C'est la méthode qui sera le plus efficace et à utiliser dans la plupart des cas.
- `fetch()`: retourne les données de l'objet venant d'être récupérés à travers le bot.
- `watch()`: retourne l'objet en cache. Ne cherche pas à savoir si les données sont trop vielles. Cette méthode est à utiliser dans les cas où on cherche à effectuer une action sur l'objet sans utiliser ses données. (Tel que la création d'un message dans un channel).


Les objets retournés par ces caches (`MemberCache` et `ChannelCache`) sont des objets héritant d'`ObjectListener`.
 
Ce sont des objets pouvant supporter l'écoute d'évènement.

```java
Mem mem = MemberCache.get(id);
mem.listen((ActionEvent<MessageCreateEventFiller>) filler -> {
    Logger.logMessage("Le membre " + mem.entity.getUsername() + " a envoyé un message !");
});
```

```java
Chan<GuildMessageChannel> chan = ChannelCache.watch(id);
chan.listen((ActionEvent<MessageCreateInChannelFiller>) filler -> {
    Logger.logMessage("Un message a été envoyé dans le channel " + chan.entity.getName());
});
```

Vous pouvez combiner ça avec un `Worker` ou une `SlashCommand`.


# Les Challenges

> Si vous rencontrez la moindre difficulté n'hésitez pas à venir voir Edifay sur le serveur discord.

Les challenges sont une partie de Dev'Area qui a pour but d'être intéractive.

Le principe est simple, des défis sont proposés, pour accéder à ces défis le serveur propose une API en passant par 
l'url suivante : `https://devarea.fr/data/challenge/{actions}`.

Le côté client de cette API est implémenté dans différents langages que vous pouvez retrouver sur le site de [devarea.fr](https://devarea.fr/challenges/download).

Les membres peuvent donc utiliser simplement ces clients pour accéder aux challenges.

Le code de résolution des challenges s'exécute directement sur la machine cliente et valide les challenges à travers l'échange de donnés de l'API.

### Créer un client

Si vous souhaitez implémenter l'API dans un langage qui n'est actuellement pas disponible, cela est assez simple.

Prenez le code d'un langage déjà implémenté, il suffit de faire la portabilité dans le nouveau langage de programmation.

### Créer un challenge

[//]: # ( TODO tutoriel sur la création d'un challenge )

Tout d'abord les challenges sont situé dans le répertoire `devarea.fr.web.challenges`. Vous pourrez
y retrouver l'implémentation du système.

Les implémentations des challenges "fonctionnels" se trouvent dans le repertoire `created`.

La première étape pour créer un nouveau challenge est de créer une nouvelle classe héritant de la classe `Challenge`.

Voici un "template" pour créer un nouveau challenge :

```java
package devarea.fr.web.challenges.created;

import devarea.fr.web.challenges.Challenge;
import devarea.fr.web.challenges.Session;
import devarea.fr.web.challenges.SimplePacket;

@Challenge.ChallengeDefinition(name = "MonChallenge", requiredChallenge = {"tutoriel"})
public class MonChallenge extends Challenge {

    public MonChallenge(String name, Session session) {
        super(name, session);
    }

    private static final String explicationOnLoad = """
                Mes explications...
        """;

    @Override
    public SimplePacket onLoad() {
        return new SimplePacket("", explicationOnLoad);
    }

}
```

Maintenant que le challenge existe, il faut pouvoir rajouter de la logique derrière.

Avant cela, il faut comprendre comment les informations vont être échangés entre le serveur et le client.

L'objet `SimplePacket` va avoir ce rôle, de passer de l'information simple. `SimplePacket` est composé de 2 valeurs, `data` et `toShow` : 

 - `toShow` -> contiendra le texte à afficher lors de la réception du packet.
 - `data` -> contiendra l'information en elle-même, celle qui sera fournie au client pour qu'il puisse la traîter.

> ```java 
> new SimplePacket(data, toShow);
> ```
> Les packets sont ensuite transformé en JSON puis envoyé à travers le protocole https, certains caractères ne pourront pas passer.
> 
> C'est pour quoi uniquement les caractères ascii sont conseillés (les accents peuvent être utilisé, certaines modifications chez les clients ont été effectués pour cela).


### Enfin place à la logique d'un challenge.

Chaque challenge possède un état. Cet état correspond à la méthode qui sera exécutée lors de la reception d'un nouveau packet.

Pour lier une méthode à un état il suffit de rajouter `@Controller(name = "monEtat", freeToUse = false)` au dessus de la méthode en question.

Cela donne :

```java
@Override
public SimplePacket onLoad() {
    this.setState("monEtat");
    return new SimplePacket("", explicationOnLoad);
}

@Controller(name = "monEtat", freeToUse = false)
public SimplePacket maMethode(final SimplePacket packet) {
    
    String donneeDuMessageRecu = packet.getData();

    if (donneeDuMessageRecu.equals("....")) {
        this.validate();
        return new SimplePacket("", "Gagné !");
    } else {
        this.fail();
        return new SimplePacket("", "Perdu !");
    }
}
```

Explications :

 - Toutes les méthodes ayant l'annotation `@Controller` doivent prendre comme paramètre un `SimplePacket`, et retourner un `SimplePacket`, sinon le code vous criera dessus ;).
 - Changer l'état actuel du challenge se fait à l'aide de la méthode `setState("monEtat")`.
 - Valider un challenge se fait à l'aide de la méthode `validate()`.
 - Louper un challenge se fait à l'aide de la méthode `fail()`.

> La valeur `freeToUse` de l'annotation `@Controller`, permet de définir si le client peut lui-même demander d'accéder à cette méthode.
> 
> C'est le cas dans la plupart des challenges pour la méthode `start()`.

Vous avez désormais les bases pour créer un challenge. Je vous laisse pour la suite prendre inspiration sur les challenges déjà existants !


# Merci !!

Voici la fin du tutoriel j'espère que vous serez nombreux mettre la main à la pâte pour ajouter quelques fonctionnalités.

N'oubliez pas pour les développeurs plus avertit que le bot est lié à un site web et à une base de donnés. Cela pourrait peut-être vous donner plus d'imagination.

Edifay ;)

# Licence

Tout le code est sous licence GPL v3.

```
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, version 3 of the License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
```
