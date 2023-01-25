[# Bot_DevArea_2.0
Ici la version 2 du bot de Dev'Area.

Version 1 ici -> https://github.com/DevAreaServer/Bot_DevArea

Un tutoriel plus complet va arriver bientôt.

Pour l'instant ce qu'il faut savoir :

Fonctionnement principal.

Dès que vous voulez faire quelques choses. (Qui n'est pas une slashCommand) Vous pouvez simplement créer une classe, le mieux est de la créer dans devarea.fr.discord.workers.
Et de la classé dans core, linked, ou self.
 core -> Les workers principaux qui complète l'architecture du système.
 linked -> Les workers liés aux commandes ou autres...
 self -> Les workers qui s'auto gère et ne sont lié à aucune commande.
 
Le classement dans ces dossiers ne change rien.

Cette classe que vous avez créer vous devez l'implémenté de Worker.

Worker contient 3 méthodes :
 - onStart()
 - setupEvent()
 - onStop()
 
A partir de ces methodes vous pouvez effectué ce que vous voulez.

Le système d'event est comme ceci : 
```
new OneEvent<'L'event que vous écoutez'>(){
 public run(l'event filler){
  // ce que vous voulez.
 }

}
```



cette classe OneEvent, peut être utilisé pour écouter des events sur le Core (c'est à dire sur le serveur Dev'Area)

Mais il peut être aussi utilisé pour écouter sur les membres.


L'accessibilité des membres et des channels se fait à travers : 
 - MemberCache
 - ChannelCache
 
 
Ce cache retourne une entité de stockage qui hérite de ObjectListener. C'est à dire un objet qui permet d'ajouter des listeners dessus.

 - Mem -> pour le membre.
 - Chan -> Pour les channels.

Un exemple :
```
Mem.listen(new OneEvent<MessageCreateFiller>(){
  public run(MessageCreateFiller filler){
      Chan<? extends GuildMessageChannel> chan = ChannelCache.watch(filler.event.getChannelId.asString());
      chan.createMessage("Vous avez envoyé " + filler.event.getMessage().getContent()).subscribe();
  }
}
```



Ici l'évènement écoute sur le membre.

Ceci est pour créer un worker.

Si vous voulez créer une SlashCommand.

Il suffit de créer une classe qui hérite de `SlashCommand` dans `devarea.fr.discord.commands.slash`.

Et de compléter les méthodes héritées.


Tout le système est assez simple, une partie du code est commenté et surement de meilleur qualité que ce README.


Un "tuto" plus clair et fait proprement sera ajouté rapidement.

Merci d'avance !
(Si vous avez besoin de me contacter n'hésitez sur le discord !)




](https://github.com/Edifay/Bot_DevArea_2.0)
