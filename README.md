# Dev'Area v2

Dev'Area est un serveur discord basé autour du développement. Tous les niveaux de développeurs sont les bien venus.

Ce serveur fourni des fonctionnalités supplémentaires grâce au bot qui lui est dédié. Le repo github contient le code du
bot de Dev'Area.

> Lien du discord : https://discord.gg/gpZemxGmrD

Le code ci-dessus est la 2ème version du bot, qui est la refonte complète de la [version 1](https://github.com/DevAreaServer/Bot_DevArea).

Cette version 2 a pour but de rendre le code plus portable pour les nouveautés discord. Tels que les intéractions plus complèxes `Menu`, `Boutons`, `Modals`.

Mais aussi pour rendre son code plus accessible. En effet, le code du bot a pour but d'être amélioré et maintenu par la communauté. C'est pour quoi la simplicité et la facilité de créer de nouvelles commandes a été un des objectifs principal dans la conception du bot. Et cela doit rester un des objectifs pour les modifications futures apportés au code.

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