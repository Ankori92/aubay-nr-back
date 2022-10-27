### TP NR 102

## Gestion des branches du dépot
Branches principales :<br />
- La branche "**tp**" contient le code qui sera remis aux participants à la formation, c'est cette version de l'application qui contient les mauvaises pratiques à détecter et corriger.<br />
- La branche "**master**" se base sur la version "tp" et contient plusieurs commits de correction, cette version pourra être présentée partiellement à la fin du TP pour présenter des problèmes et quelques solutions associées. Cette version devra être fournie en intégralité aux participants après leur formation pour qu'ils puisse étudier plus en détail le fonctionnement.<br />

# Getting Started
Branches secondaires :<br />
- La branche "**multimodule**" contient une version "master" découpée en multi-modules maven. A voir si ce développement est intéressant ou pas.<br />
- La branche "**codefactory**" contient une version "master" servant de base pour le développement du socle pour CODE FACTORY. Elle intègre aussi des développements OpenAPI CodeGen et pour une connexion MongoDB.

### Reference Documentation
For further reference, please consider the following sections:
## Généralités
Durée totale du TP : 1 jour



* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.0.0-SNAPSHOT/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.0.0-SNAPSHOT/maven-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.0.0-SNAPSHOT/reference/htmlsingle/#web)
* [Spring Security](https://docs.spring.io/spring-boot/docs/3.0.0-SNAPSHOT/reference/htmlsingle/#web.security)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/3.0.0-SNAPSHOT/reference/htmlsingle/#data.sql.jpa-and-spring-data)
Matériel nécessaire :
-	1 PC par participant<br />
-	1 PC formateur, avec une base de données H2 accessible par les PC des participants<br />
-	Réseau LAN entre participants et formateur<br />
-	SSD externe contenant les documents, le code source et les applications nécessaires

### Guides
The following guides illustrate how to use some features concretely:
## Préparation de la formation (à faire par le formateur) :
-	Supprimer toutes traces d’anciennes formations sur les PC des participants (C:\formation-nr\)<br />
-	Déposer le dossier formation-nr sur chacun des PC des participants (C:\formation-nr\)<br />
-	Configurer le chemin d’accès à la base de données depuis chaque PC (application.properties)<br />
-	Installer H2 sur le PC du formateur et charger les données (Le chargement de la hiérarchie doit prendre 5~10 secondes avec DEBUG désactivé, depuis les PC des participants, réactiver le mode DEBUG après test. Changer la volumétrie de donner à charger dans ApplicationController via DataGenerator.generateEmployees(15, 6). 15 est le nombre maximum d’employés par équipe, et 6 la profondeur maximale de la hierarchie).

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
## Prérequis
Ce TP est une application développée en Java 17, Angular 14, Spring Boot 2 et Hibernate 5.


Il est impératif d’avoir des compétences en Java et Angular (Toutes versions), et très fortement recommandé d’avoir une première expérience avec Spring Boot et Hibernate.

## Objectif du TP
Ce TP est une mise en situation d’un développeur devant une application déjà fonctionnelle, mais présentant quelques défauts de conception, de développement et de configuration.

Suite à la sensibilisation qu’il a reçue, le développeur devra être capable d’identifier le maximum de mauvaises pratiques, imaginer des solutions et éventuellement mettre en place une correction.

Pour chacun des problèmes, la détection, les solutions théoriques et les corrections apportées rapportent un certain nombre de points en fonction de leur impact NR, et sont présentés dans la partie « Problèmes à identifier » de ce document.

L’objectif final est donc d’obtenir le maximum de points. Aucun minimum n’est imposé, il s’agit d’une manière un peu ludique de présenter le TP et de « féliciter » les développeurs les plus performants.

## Enoncé
Vous êtes développeur de l’application « Aubay - NR », c’est une application récente en Java 17 et Angular 14, basée sur Spring Boot. Le client ne semble pas satisfait des performances de l’application, cela semble cacher des problèmes structurels, des mauvaises pratiques de développement, et des configurations douteuses. 
Suite à la sensibilisation que vous venez de recevoir, vous devez prendre quelques heures pour identifier des problèmes, imaginer des solutions et pourquoi pas tenter de les corriger, vous avez carte blanche tant qu’il n’y a pas d’impact fonctionnel.
Chacun des problèmes identifié et éventuellement résolu vous rapportera un certain nombre de points (Une solution théorique rapporte moins de points qu’une solution fonctionnelle). Pensez à noter vos idées, une idée qui n’est pas écrite avant la fin du TP ne rapporte pas de point !


## Problèmes à identifier

### Le modèle relationnel
Chacun des employés (entité Employee) est lié à son manager (Un manager est aussi un Employee). Un manager peut avoir plusieurs employés sous sa responsabilité, éventuellement eux-mêmes manager.<br />
Il y a donc une relation de type 1-n entre Employee et elle-même, matérialisée avec la relation @ManyToOne au niveau de l’entité Java.<br />
Cela signifie que si nous demandons à Hibernate de récupérer un employé, nous récupérons aussi son manager, et le manager de celui-ci, et le manager de celui-ci, etc. Cette structure semble donc assez dangereuse puisque récursive. Cependant en réalité, les entreprises n’ont pas une profondeur hiérarchique suffisante pour réellement poser des gros problèmes de performance, si on ne récupère qu’un seul employé (Ce qui ne veut pas dire qu’il n’y a pas de gain à s’occuper aussi de ce problème, particulièrement si un ou plusieurs services récupèrent plusieurs employés ! Chaque employé présentant toute sa hiérarchie, il y aurait énormément de doublons et de données inutiles).<br />
En revanche, la liaison inverse est particulièrement dangereuse, car récupérer uniquement le PDG revient à récupérer l’intégralité des employés de la base de données. Cette relation est matérialisée via @OneToMany au niveau de l’entité Java.<br />
La récupération de données non-nécessaire allonge considérablement les temps de traitement, mais aussi très probablement les volumes de données qui seront transmis au frontend.<br />
De plus, la récupération des employés sous la responsabilité d’un manager se fait par défaut via des requêtes séparées, on rencontre donc le problème « N+1 requêtes », à savoir que l’on a une première requête SQL pour récupérer le manager, puis autant de requêtes SQL que d’employés liés à récupérer. Le temps de récupération du manager explose.<br />
Les solutions ne manquent pas, à commencer par l’utilisation du « lazy-loading » pour éviter de récupérer systématiquement les entités liées (Ajout de fetch = FetchType.LAZY aux annotations @OneToMany et/ou @ManyToOne).<br />
Pour se débarrasser du problème N+1, il y a aussi plusieurs solutions, comme l’utilisation de JOIN FETCH dans une requête HQL pour qu’Hibernate réalise une jointure et récupère automatiquement toutes les données en une seule requête. Il est aussi possible de complètement revoir le système de récupération de ces données, par exemple en récupérant l’ensemble des employés via une requête récursive (avec « WITH ») puis en reconstruisant la hiérarchie manuellement.<br />
Dans le cas de l’écran présentant l’intégralité de l’arbre hiérarchique, il est possible (et même souhaitable) de ne pas remonter l’intégralité des employés au chargement de la page. Nous ignorons quelle partie de l’arbre intéresse l’utilisateur, il est donc préférable de le laisser dérouler manager par manager l’arbre, en ne remontant que l’équipe directe du manager « ouvert ». Le nombre de requêtes aux API va ainsi augmenter, mais les requêtes deviendront quasi-instantanées et l’infrastructure entière sera beaucoup moins sollicitée. Mettre en place un système de lazy-loading au niveau de l’IHM serait ici le plus grand gain que le développeur puisse mettre en place.<br />

<table border="1">
	<tr>
		<th>Problème</th>
		<th>Identifié</th>
		<th>Solution théorique</th>
		<th>Correction en place</th>
	</tr>
	<tr>
		<td>Le manager est récupéré systématiquement et récursivement</td>
		<td>2</td>
		<td>4</td>
		<td>6</td>
	</tr>
	<tr>
		<td>Les employés d’un manager sont récupérés systématiquement et récursivement</td>
		<td>2</td>
		<td>4</td>
		<td>8</td>
	</tr>
	<tr>
		<td>Problème N+1 requêtes</td>
		<td>3</td>
		<td>5</td>
		<td>10</td>
	</tr>
	<tr>
		<td>Récupération de l’intégralité de la table « Employee » au niveau de l’écran « Ressources Humaines »</td>
		<td>4</td>
		<td>6</td>
		<td>10</td>
	</tr>
</table>


### Limitation des résultats des API
Plusieurs API exposent des données sans limitations. C’est par exemple le cas de /employees qui récupère l’intégralité des employés de la base de données, sans possibilité de limiter les résultats (Que ce soit via des filtres, ou via un système de pagination).

<table border="1">
	<tr>
		<th>Problème</th>
		<th>Identifié</th>
		<th>Solution théorique</th>
		<th>Correction en place</th>
	</tr>
	<tr>
		<td>Les API ne proposent aucun système de limitation des résultats</td>
		<td>1</td>
		<td>3</td>
		<td>5</td>
	</tr>
</table>


### Attention aux nouveautés
L’application est récente et utilise les dernières technologies disponibles. En l’occurrence, l’utilisation de Java 17 impose l’entreprise de disposer de JRE 17 installés sur leurs serveurs. Cette contrainte risque de forcer les équipes DevOps à fournir plus de travail pour installer et entretenir les environnements, alors que l’utilisation de Java 17 est très moyennement justifiée : La seule fonctionnalité de Java 17 exploitée dans l’application est présente dans RequestStatisticsInterceptor, en Java 17 :<br />

```java
if (response instanceof ContentCachingResponseWrapper cachedResponse) {
       length = cachedResponse.getContentSize();
}
```
Au lieu de (Java < 16) :

```java
if (response instanceof ContentCachingResponseWrapper) {
       ContentCachingResponseWrapper cachedResponse = (ContentCachingResponseWrapper) response;
       length = cachedResponse.getContentSize();
}
```
Oui, c’est plus court, mais est-ce que cela vaut la peine d’imposer cette contrainte à une entreprise qui n’a pas encore sauté le pas vers Java 17 ? Évidemment, si l’entreprise a déjà installé le JRE 17 sur ses serveurs, ce problème est sans objet.

<table border="1">
	<tr>
		<th>Problème</th>
		<th>Identifié</th>
		<th>Solution théorique</th>
		<th>Correction en place</th>
	</tr>
	<tr>
		<td>La version 17 de Java n’est pas justifiée</td>
		<td>1</td>
		<td>2</td>
		<td>4</td>
	</tr>
</table>


### Mutualiser les développements
L’application utilise son propre système d’authentification. Outre les éventuelles vulnérabilités de sécurité qu’il pourrait y avoir, ce système d’authentification a demandé du temps pour sa conception, sa réalisation et demandera encore des efforts pour sa maintenance (Par exemple à cause de nouvelles fonctionnalités liées aux droits, ou la prise en compte des mots de passes expirés, ou de comptes bloqués, etc).<br />
La mutualisation du système d’authentification, soit en réutilisant un système déjà développé, soit en utilisant un SSO (Single-Sign-On), permet de limiter ces impacts.
Aussi, un découpage de l’application en différents modules (Par exemple, un module Maven dédié aux ressources humaines) et un outil de description des API (Comme Swagger / OpenAPI) permettraient de faciliter la réutilisation des modules développés au sein de Aubay-NR.

<table border="1">
	<tr>
		<th>Problème</th>
		<th>Identifié</th>
		<th>Solution théorique</th>
		<th>Correction en place</th>
	</tr>
	<tr>
		<td>Le système d’authentification est « fait maison »</td>
		<td>1</td>
		<td>2</td>
		<td>N/A</td>
	</tr>
	<tr>
		<td>L’application est très « monolithe » et gagnerait en réutilisabilité avec un découpage par module</td>
		<td>1</td>
		<td>2</td>
		<td>3</td>
	</tr>
	<tr>
		<td>Les API mises à disposition ne sont pas documentées (Swagger / OpenAPI)</td>
		<td>1</td>
		<td>2</td>
		<td>3</td>
	</tr>
</table>


### Soigner la qualité du code
Une anomalie, c’est tout un travail pour cibler le problème, comprendre les cas où il survient, décrire ce qui a été compris à l’équipe de développement, pour être ré-analysé techniquement par le développeur, qualifié (effectivement une anomalie car non-conforme à la demande, ou demande d’évolution car non précisé), corrigé, re-testé… Sans parler de toute la gestion de projet autours des équipes, des indicateurs négatifs qui viennent détruire les relations et le moral des troupes…<br />
Tout ce temps a été investi inutilement, c’est donc un impact NR qui aurait pu être évité.<br />
S’il est effectivement contre-productif de chercher le zéro-fautes à tout prix dans un contexte où les anomalies sont permises, certaines bonnes pratiques permettent de réduire fortement le risque d’anomalies, sans trop d’efforts initiaux.<br />
Les tests unitaires permettent de s’assurer qu’à tout moment, chacune des fonctions de l’application a bien le comportement attendu. Ils sont exécutés à chaque construction de l’application et permettent de détecter toute régression avant même que l’application ne soit livrée. Pour cela, il faut que la couverture en tests unitaires soit suffisamment importante pour couvrir la quasi-totalité des classes importantes (Couche de service, par exemple) et environ 70-80% du total de l’application.<br />
Pour la réalisation des tests unitaires, les développeurs devraient parfaitement maitriser le Framework de test utilisé dans leur application. Par exemple, Mockito est un excellent framework et se conjugue parfaitement avec jUnit.<br />
Pour mesurer le taux de couverture global, par classe, par méthode et avoir le détail du code couvert, instruction par instruction, il est possible d’utiliser l’outil intégré d’Eclipse (Run Coverage) pour une utilisation en local, et JaCoCo ou Corbertura sur Jenkins/Sonar.

<table border="1">
	<tr>
		<th>Problème</th>
		<th>Identifié</th>
		<th>Solution théorique</th>
		<th>Correction en place</th>
	</tr>
	<tr>
		<td>Absence de tests unitaires Java</td>
		<td>1</td>
		<td>3</td>
		<td>5</td>
	</tr>
	<tr>
		<td>Absence de tests unitaires Angular</td>
		<td>1</td>
		<td>3</td>
		<td>5</td>
	</tr>
</table>


### Maitriser la configuration de l’application
L’application est configurée par défaut pour afficher un niveau de logs extrêmement élevé : DEBUG.<br />
Ce niveau de logs est particulièrement intéressant pour comprendre le fonctionnement interne de certains composants, mais ne devrait jamais être laissé en DEBUG par les développeurs en raison de la très forte dégradation des performances de l’application. Le niveau maximal acceptable pour de la production est « INFO ».<br />
Aussi, la configuration actuelle des logs indique une rotation journalière avec un historique de 30 jours, sans limitation de volume. Une utilisation intensive de l’application pourrait donc générer des fichiers de logs extrêmement imposants, au point de faire crasher l’application. L’espace de stockage devra donc être démesuré (peut-être plusieurs To), juste pour stocker des fichiers de logs. <br />
La solution consiste à ajouter une politique de rotation des logs supplémentaire, par exemple en découpant les fichiers sitôt qu’ils atteignent 10Mo. En ne conservant que 30 fichiers maximum, cela représente 300Mo maximum de logs, quelque soit la charge de l’application.

<table border="1">
	<tr>
		<th>Problème</th>
		<th>Identifié</th>
		<th>Solution théorique</th>
		<th>Correction en place</th>
	</tr>
	<tr>
		<td>Le niveau de logs est DEBUG</td>
		<td>1</td>
		<td>2</td>
		<td>4</td>
	</tr>
	<tr>
		<td>La rotation des logs n’est pas maitrisée en volume</td>
		<td>2</td>
		<td>3</td>
		<td>4</td>
	</tr>
</table>


### Choisir les technologies les plus pertinentes
De nombreuses technologies intégrées à l’application n’ont que peu d’intérêt et viennent alourdir l’application, alors que les fonctionnalités souhaitées ne nécessitent pas forcément l’ajout d’une bibliothèque.<br />
Par exemple, Lombok permet d’éviter de devoir écrire du code « simple », comme les getters, setters, et constructeurs des entités (Choses déjà réalisées nativement par les IDE, via la génération automatique de code). Son intérêt est donc particulièrement limité, mais impose aux développeurs de l’application de disposer d’un eclipse modifié spécialement pour comprendre le sens des annotations utilisées, et ajoute un jar de 2Mo à l’application. L’idée est bonne, mais cela fait un peu cher le getter. Lombok peut aujourd’hui être remplacé par la fonctionnalité « Record » de Java 16. Il n’est effectivement plus nécessaire de renseigner les accesseurs des Records, on gagne en écriture sans même ajouter de bibliothèque ni triturer le compilateur d’Eclipse.<br />
Aussi, coté frontend, la simple utilisation d’un framework comme Angular peut être remis en question. A-t-on besoin d’une usine à gaz d’un méga-octet complet (Une fois uglyfié et minifié avec les réglages de production) pour faire tourner une application de 2 pages et demi ?<br />
Au niveau des dépendances, plusieurs bibliothèques ont été ajoutées tout en réalisant assez peu d’économies de développement, comme Material (card, input, button, snackbar, progressbar…).
La typo Roboto est une Font custom que devra télécharger chacun des clients (150ko chacun, juste pour la police de caractères custom). Pour la défense de google, toutes les applications Angular utilisant cette typo se fournissent automatiquement sur fonts.gstatic.com et bénéficie donc d’un cache partagé entre toutes les applications Angular de l’utilisateur, via le cache du navigateur web. Les typo custom restent une mauvaise pratique NR, cela représente du volume facilement soustrayable sans efforts ni impacts fonctionnels.<br />
Certaines dépendances techniques ne sont même pas utilisées, comme jackson-datatype-hibernate5 (et ses dépendances), poi-ooxml (et ses dépendances), et plein de modules dans angular.

<table border="1">
	<tr>
		<th>Problème</th>
		<th>Identifié</th>
		<th>Solution théorique</th>
		<th>Correction en place</th>
	</tr>
	<tr>
		<td>Lombok n’apporte que très peu de valeur / gain de temps</td>
		<td>1</td>
		<td>2</td>
		<td>3</td>
	</tr>
	<tr>
		<td>Angular est particulièrement lourd, sa justification est donc difficile</td>
		<td>3</td>
		<td>5</td>
		<td>8</td>
	</tr>
	<tr>
		<td>Material permet de faire quelques gains de temps niveau développement mais se rend vite contre-productif quand il y a de la personnalisation à apporter, et alourdi inutilement l’application</td>
		<td>1</td>
		<td>2</td>
		<td>4</td>
	</tr>
	<tr>
		<td>Les typo custom sont à bannir</td>
		<td>2</td>
		<td>3</td>
		<td>4</td>
	</tr>
	<tr>
		<td>jackson-datatype-hibernate5 (+ dépendances) n’est pas utilisé</td>
		<td>1</td>
		<td>2</td>
		<td>3</td>
	</tr>
	<tr>
		<td>poi-ooxml (+ dépendances) n’est pas utilisé</td>
		<td>1</td>
		<td>2</td>
		<td>3</td>
	</tr>
	<tr>
		<td>Plein de modules dans Angular ne sont pas utilisés</td>
		<td>1</td>
		<td>2</td>
		<td>3</td>
	</tr>
</table>


### Les API ne remontent que les informations nécessaires
Chaque employé contient un flag indiquant qu’il a démissionné (resigned), cette donnée n’est pas utilisée par le frontend (Elle vaut toujours « false », sinon le serveur l’aurait filtré). Il est souhaitable de retirer ce champ de l'entité (Et modifier les requêtes en conséquence), ou de créer un DTO ne contenant pas ce champ, ou d'ajouter @JsonIgnore sur les champs qui ne sont pas destinés à être envoyés par l'API (@JsonIgnore indique au sérialiseur JSON -jackson- de ne pas sérialiser ce champ).<br />
De même, les User contiennent le flag "enabled","accountNonExpired", "accountNonLocked" et "credentialsNonExpired" qui ne sont pas utile au front. Rajouter @JsonIgnore permet de facilement retirer ces données des API.<br />
Concernant la consultation des statistiques, l’ensemble des données conservées sont envoyées en détail à Angular, pour que celui-ci les agrège, analyse et présente quelques valeurs. Cela représente une quantité de données très, très importante une fois en production (chaque action de chaque utilisateur ajoute une centaine d’octets aux statistiques, soit 20Mo pour quelques 100 utilisateurs qui feraient 100 requêtes par jour pendant un mois, 240Mo si les statistiques n’ont pas été nettoyées de l’année, etc). Il serait préférable de laisser l’analyse des données au serveur et à la base de données pour limiter les gros flux de données. Pour aller plus loin, il serait intéressant de consolider les statistiques via un batch, pour transformer quelques milliers de lignes en une seule consolidée, et ainsi ne plus avoir de problème de volumétrie liée aux statistiques.<br />
Aussi, la simple navigation entre les pages de l’application rafraichi systématiquement les données des pages. Or il est peu probable que certaines données aient été modifiées, ou leur modification peut être considérée négligeable (comme les statistiques). Il serait donc préférable de conserver en cache certaines données, et proposer un bouton si l’utilisateur souhaite rafraîchir volontairement les données.

<table border="1">
	<tr>
		<th>Problème</th>
		<th>Identifié</th>
		<th>Solution théorique</th>
		<th>Correction en place</th>
	</tr>
	<tr>
		<td>Le champ resigned de Employee n’est jamais utilisé par angular</td>
		<td>1</td>
		<td>2</td>
		<td>3</td>
	</tr>
	<tr>
		<td>Les champs "enabled", "accountNonExpired", "accountNonLocked" et "credentialsNonExpired" de User ne sont jamais utilisés par Angular</td>
		<td>1</td>
		<td>2</td>
		<td>3</td>
	</tr>
	<tr>
		<td>L’intégralité des données des usages sont envoyées à Angular pour calculer les statistiques</td>
		<td>3</td>
		<td>5</td>
		<td>8</td>
	</tr>
	<tr>
		<td>La navigation d’une page à l’autre rafraichi systématiquement le contenu des pages</td>
		<td>2</td>
		<td>3</td>
		<td>4</td>
	</tr>
</table>


### Utiliser le batching pour réaliser des modifications en masse dans l’application
La réalisation d’INSERT, d’UPDATE ou de DELETE via Hibernate déclenche systématiquement (par défaut) autant de requêtes SQL que d’entité à modifier. Il est bien heureusement possible de remédier à ça en utilisant le batching. Il est ainsi possible de remplacer repository.deleteAll() par repository.deleteAllInBatch(), qui permettra de passer de multiples requêtes du type :

```sql
DELETE FROM entity WHERE id = ? ;
```
À une seule et unique requête :

```sql
DELETE FROM entity ;
```
Aussi, il est possible d’activer le batching directement dans hibernate via les propriétés :

```properties
hibernate.jdbc.batch_size=50
hibernate.order_inserts=true
hibernate.order_updates=true
```
Cette modification permettra de passer de multiples requêtes du type :

```sql
INSERT INTO entity (id, param1, param2) VALUES (?, ?, ?) ;
```
A une seule et unique requête (s’il y a moins d’entités que la taille du batch) :

```sql
INSERT INTO entity (id, param1, param2) VALUES (?, ?, ?), (?, ?, ?), (?, ?, ?), (?, ?, ?) ;
```
Attention, en cas de batching, pensez à flush() et clear() la session hibernate pour éviter de conserver énormément d’entités en mémoire et déclencher un OutOfMemoryError.<br />
Le système de batching peut être adapté pour l’enregistrement des statistiques. Chacun des usages peut être mesuré et ajouté à une liste en cache, puis enregistrés automatiquement quand la taille du cache dépasse par exemple 1000 éléments à enregistrer. Pour éviter d’avoir une requête « lente » toutes les 1000 actions, il suffirait d’exécuter cet enregistrement dans un Thread asynchrone.

<table border="1">
	<tr>
		<th>Problème</th>
		<th>Identifié</th>
		<th>Solution théorique</th>
		<th>Correction en place</th>
	</tr>
	<tr>
		<td>La modification de multiples entités déclenche autant de requêtes SQL que d’entités à modifier</td>
		<td>3</td>
		<td>5</td>
		<td>7</td>
	</tr>
	<tr>
		<td>Les statistiques sont enregistrées au fil de l’eau, impliquant une perte de performance globale sur toutes les actions</td>
		<td>2</td>
		<td>5</td>
		<td>7</td>
	</tr>
</table>


### Eviter les exécutions multiples d’une même fonction
Une fonction prend généralement du temps à s’exécuter, si le résultat de cette fonction est utilisé plusieurs fois, il est préférable de stocker ce résultat dans une variable locale tant que ce résultat est utile.<br />
On retrouve dans l’application des appels multiples à des fonctions au même endroit, par exemple dans le template Angular team.component.ts :

```html
<span *ngIf="nbEmployeesBelow(team) > 1">(Manager de {{nbEmployeesBelow(team)-1}} employé{{nbEmployeesBelow(team)>2?'s':''}})</span>
```
Le simple affichage de l’instruction « (Manager de X employés) » déclenche ainsi le calcul du nombre d’employés 3 fois de suite, à chaque utilisation de ce template.<br />
D’une règle générale, il est préférable de limiter le plus possible l’appel de fonctions depuis les templates, et privilégier le binding de variable déjà alimentées.

<table border="1">
	<tr>
		<th>Problème</th>
		<th>Identifié</th>
		<th>Solution théorique</th>
		<th>Correction en place</th>
	</tr>
	<tr>
		<td>Des fonctions sont appelées de multiples fois alors que le résultat de cette fonction aurait pu être stocké dans une variable locale puis réutilisé</td>
		<td>1</td>
		<td>2</td>
		<td>4</td>
	</tr>
</table>


### Ne pas négliger l’accessibilité
Dans une grande entreprise, de nombreux employés sont en situation de handicap. Il est donc nécessaire de leurs rendre accessibles les applications qu’ils utilisent. <br />
Une application doit être facilement utilisable pour tous et apporter le plus de valeur possible.<br />
Aubay-NR est particulièrement inaccessible, le composant principal de l’application (L’arbre hiérarchique) ne dispose d’aucune instruction pour permettre à un lecteur d’écran d’interpréter les éléments.<br />
Aussi, certains boutons (comme « Refresh » dans les statistiques) ne sont que des images avec « onclick », sans description, sans sélection via le clavier. Aucune chance qu’un non-voyant l’utilise.<br />
Enfin, l’arbre hiérarchique est très peu ergonomique, y compris pour les « valides ». Il impose de connaitre l’ensemble des managers responsables de l’employé qui intéresse l’utilisateur, ou de perdre énormément de temps à chercher dans l’arborescence manuellement. Un moteur de recherche est indispensable sur cette application ! (Et pourtant absent, mais que fait le Product Owner ?). Cette modification ayant un impact fonctionnel, le rôle du développeur s’arrête à la suggestion de la fonctionnalité au PO.

<table border="1">
	<tr>
		<th>Problème</th>
		<th>Identifié</th>
		<th>Solution théorique</th>
		<th>Correction en place</th>
	</tr>
	<tr>
		<td>L’application n’est pas accessible</td>
		<td>3</td>
		<td>3</td>
		<td>9</td>
	</tr>
	<tr>
		<td>Il n’y a aucun moteur de recherche</td>
		<td>2</td>
		<td>3</td>
		<td>N/A</td>
	</tr>
</table>

### Pas de code mort
Un code source doit être utile, tout code mort doit évidemment être retiré. Dans l’application, de nombreux services ne sont même pas utilisés, comme tout ce qui touche à l’internationalisation (La langue préférée de l’utilisateur par exemple).<br />
Cela facilite la maintenance (application moins volumineuse donc plus facile à modifier) et évite d’embarquer du code et ses dépendances dans les livrables. De plus, moins de code c’est aussi moins de surface d’attaque pour un éventuel attaquant.

<table border="1">
	<tr>
		<th>Problème</th>
		<th>Identifié</th>
		<th>Solution théorique</th>
		<th>Correction en place</th>
	</tr>
	<tr>
		<td>Certains services et composants sont inutilisés</td>
		<td>1</td>
		<td>2</td>
		<td>3</td>
	</tr>
</table>

### Eviter les accès à la base de données en boucle 
Les accès à la base de données ont un coût NR très important :<br />
-	Chacune des requêtes transitent par le réseau, il faut donc mettre sous tension de grandes longueurs de câble, alimenter les routeurs, pare-feu, cartes réseaux, etc.<br />
-	Chacune des requêtes respecte plusieurs protocoles encapsulés (7 couches du modèle OSI), il faut donc préparer les données pour l’envoi sur le réseau puis les désencapsuler en réalisant l’inverse pour chacun des protocoles.<br />
-	Chaque requête SQL devra passer par une phase d’analyse par la base de données, qui dans le meilleur des cas réutilisera un plan d’exécution précédemment construit, mais dans le reste des cas demandera beaucoup de travail à la base de données pour trouver le plan d’exécution le plus efficace.<br />
Le constat est alors évident : Mieux vaut réaliser 1 requête qui récupère 1000 éléments, que de faire 1000 requêtes qui récupèrent un élément chacune (Et on ne parle ici que de 1000 éléments…). La volumétrie est identique, mais le coût NR est sans commune mesure.<br />
Par ailleurs, tout accès réseau, quel qu’il soit, est un gros goulot d’étranglement pour les performances, y compris avec la fibre ou sur un réseau LAN. Il est possible de réaliser des millions d’itérations dans une boucle locale en quelques millisecondes, alors que la moindre requête envoyée sur le réseau prendra plusieurs millisecondes à elle seule, dans le meilleurs des cas.<br />
Il convient donc d’identifier les données nécessaires en amont, puis réaliser le traitement souhaité. Il est préconisé d’indexer les données récupérées (Ex : List<Object> => Map<String, Object>, où String est la clé primaire et Object l’entité récupérée).

<table border="1">
	<tr>
		<th>Problème</th>
		<th>Identifié</th>
		<th>Solution théorique</th>
		<th>Correction en place</th>
	</tr>
	<tr>
		<td>Accès en boucle à la BDD dans clearUsageStatistics</td>
		<td>4</td>
		<td>5</td>
		<td>6</td>
	</tr>
</table>

### Eviter les dark patterns
Les dark patterns sont des interfaces utilisateur spécialement conçues pour manipuler l’utilisateur, par exemple pour obtenir son consentement (case à cocher pré-cochée), orienter ses décisions (un bouton mis en évidence, l’autre beaucoup plus discret) ou rendre plus difficile son départ (Résiliation difficile, addiction), etc…<br />
Sur la page d’authentification, la case à cocher « J’accepte » les conditions d’utilisation est pré-coché et légèrement transparente. On obtient donc l’accord de l’utilisateur sans même qu’il ait eu à donner quoique ce soit. Si cet accord est effectivement nécessaire, la case doit être décochée par défaut, et mis en évidence pour que celui-ci ne cherche pas 5 minutes pourquoi le bouton « Se connecter » ne fonctionne pas.

<table border="1">
	<tr>
		<th>Problème</th>
		<th>Identifié</th>
		<th>Solution théorique</th>
		<th>Correction en place</th>
	</tr>
	<tr>
		<td>« J’accepte les conditions d’utilisation » pré-cochée</td>
		<td>3</td>
		<td>4</td>
		<td>5</td>
	</tr>
</table>

### Utiliser un système de cache quand c’est possible
Dans le cas où une donnée n’évolue pas ou peu, mais est consulté fréquemment, il est possible de configurer des caches qui permettront de court-circuiter tout ou partie d’une chaine de traitement.<br />
Certains caches sont automatiques (mais peuvent être configurés plus finement), comme le cache du navigateur, le cache de niveau 1 d’Hibernate, ou encore les plans d’exécutions des requêtes SQL (voir leur résultat) au niveau de la base de données. D’autres caches sont à implémenter manuellement en fonction de l’utilisation, comme le cache de Spring ou le cache de niveau 2 d’hibernate.<br />
Le cache de Spring est particulièrement intéressant puisqu’il permet via une annotation d’indiquer que le résultat d’une méthode (un controleur, par exemple) doit être mis en cache. Spring se chargera automatiquement de renvoyer le résultat mis en cache si la même requête survient une seconde fois.<br />
Exemple :

```java
@Cacheable("countries")
@GetMapping("/countries")
public List<Country> getCountries() {
	return employeeService.getCountries();
}
```
Ici, ce contrôleur va automatiquement mettre dans un cache nommé « countries » la liste des pays qu’il aura obtenu lors du premier appel, et ne plus faire appel à employeeService pour les appels suivants.<br />
Evidemment, sur des applications plus complètes, il est nécessaire de gérer aussi la mise à jour de ce cache, via l’utilisation des annotations @CacheEvict pour retirer un/des élément(s) du cache ou @CachePut pour mettre à jour certains éléments.<br />
Attention, ne pas oublier @EnableCaching sur le @SpringBootApplication.

<table border="1">
	<tr>
		<th>Problème</th>
		<th>Identifié</th>
		<th>Solution théorique</th>
		<th>Correction en place</th>
	</tr>
	<tr>
		<td>Mettre en cache la récupération de la liste des pays et employés</td>
		<td>4</td>
		<td>5</td>
		<td>6</td>
	</tr>
</table>
