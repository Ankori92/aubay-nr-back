### TP NR 102

## Enoncé
Vous êtes développeur de l’application « **Aubay - NR** », c’est une application récente en Java 17 et Angular 14, basée sur Spring Boot. Le client ne semble pas satisfait des performances de l’application, cela semble cacher des problèmes structurels, des mauvaises pratiques de développement, et des configurations douteuses. <br />
Suite à la sensibilisation que vous venez de recevoir, vous devez prendre quelques heures pour **identifier des problèmes**, **imaginer des solutions** et pourquoi pas **tenter de les corriger**, vous avez **carte blanche** tant qu’il n’y a pas d’impact fonctionnel.<br />
Chacun des problèmes identifié et éventuellement résolu vous rapportera un certain nombre de points (Une solution théorique rapporte moins de points qu’une solution fonctionnelle). **Pensez à noter vos idées**, une idée qui n’est pas écrite avant la fin du TP ne rapporte pas de point !


## Comment procéder ?
En matière de numérique responsable, il n'y a pas de recette à suivre pour rendre une application plus NR. Il existe bien quelques outils qui vous ont été présentés et d'autres que vous devez déjà connaitre pour vous aider à détecter des problèmes, mais les mauvaises pratiques se cachent dans chacun des petits gestes, dans chaque petit bout de code ou de configuration.<br />
Vous devez donc faire un peu de retro-ingenierie pour comprendre le fonctionnement actuel de l'application et noter tous les élements qui vous semblent améliorable, par exemple en matière de performance ou de consommation de ressources.<br />
Il est conseillé de dédier un temps conséquent à l'identification du maximum de problèmes, puis dans un second temps de trouver des solutions théoriques à tous les problèmes, puis, s'il vous reste du temps ou que certains points sont plutôt simple, en corriger le plus possible.

## Outils à disposition

### Le navigateur web
Le premier outil du développeur web, c’est le navigateur web et les outils développeurs accessibles via F12. Ces outils inclus entre-autres :

#### L’onglet « Réseau »
Cet onglet présente toutes les requêtes réalisées par le navigateur avec des serveurs distants. Il permet de consulter les données brutes échangées, et analyse leur poids, les délais de réponses et de traitement, leur type, leur compression, s’ils proviennent du cache, le temps nécessaire à charger le DOM, les erreurs rencontrées, les cookies et headers échangés, etc.<br />

Cet onglet doit être parfaitement maitrisé par les développeurs, y compris les plus débutants !<br />

TIP : Pensez à cocher la case pour désactiver le cache, et celle pour conserver le journal d’une page à l’autre (Une redirection ou même F5 vide les requêtes analysées).
 

 
#### L’onglet « Performances »
Cet onglet permet de réaliser un profilage des performances JavaScript de l’application.<br />

Une fois la page prête à être analysée, un clic sur le bouton « Enregistrer » (ou CTRL+E) permet de démarrer la capture. Un second clic sur « Arrêter » (ou CTRL+E encore) permet d’arrêter la capture et d’obtenir le résultat d’analyse.<br />

Il convient ensuite de rechercher dans le graphique les tâches ayant retenue le plus de temps pour identifier les améliorations possibles. Essayez d’être le plus précis possible sur la capture (utilisez CTRL+E !) pour réduire la quantité d’informations à analyser.<br />

À tout moment, l’ensemble de la stack est présentée à l’écran. On peut par exemple constater dans la capture ci-dessous qu’à 1151ms après le début de l’analyse, Angular est entré dans la fonction « nbEmployeeTotal » pendant 4.5ms et qu’à l’intérieur de cette fonction, c’est 4 niveaux plus profonds que la fonction « nbEmployees » a pris le plus de temps (3.7ms à elle seule, sans compter le contenu de sa propre stack, ayant pris le reste du temps). Le reste du temps d’exécution (celui de la fonction « nbEmployeesTotal » entre-autres, est négligeable. Il serait donc nécessaire d’améliorer les performances de la fonction « nbEmployees » si on souhaite améliorer les performances de la fonction « nbEmployeesTotal ».<br />
 
Cet onglet permet aussi d’analyser l’état de la mémoire au cours du temps et des actions JS.

 
#### Le plugin « GreenIT »
GreenIT-Analysis est une extension pour navigateur qui vous permet de quantifier les impacts environnementaux d'un parcours utilisateur complet, même derrière un firewall et / ou une authentification applicative. L'outil vérifie également l'utilisation de bonnes pratiques visant à diminuer ces impacts. <br />
Cette extension s'inspire fortement des fonctionnalités de EcoIndex et EcoMeter.<br />
Elle est aussi supportée sur Firefox avec quelques bonnes pratiques non supportées (du fait de limitations des API Firefox). A noter que bien que fonctionnant sur la version ESR de Firefox, le plugin a des comportements particuliers, voir point Firefox ESR ci-après. <br />
 
Fonctionnalités :<br />
-	Estimation des GES en gCO2eq <br />
-	Estimation de l’eau consommée <br />
-	Mesure du poids total de la page en ko <br />
-	Mesure du nombre de requêtes <br />
-	Mesure de la taille du DOM (à priori en lignes) <br />
-	Calcul d’un EcoIndex (meilleure valeur : 100) <br />
-	Vérification de 21 bonnes pratiques <br />
-	Historique des analyses pour constater une amélioration/détérioration <br />

Installer l'extension :<br />
Depuis Chrome, Chromium ou Edge : <br />
https://chrome.google.com/webstore/dtail/greenit-analysis/mofbfhffeklkbebfclfaiifefjflcpad <br />
Depuis Firefox ou Firefox ESR : <br />
https://addons.mozilla.org/fr/firefox/addon/greenit-analysis/ <br />
 
Utiliser l'extension :<br />
1.	Ouvrir les outils de développement du navigateur (F12). <br />
2.	Aller dans l'onglet GreenIT. <br />
3.	Dans le navigateur, aller sur la page à analyser. <br />
4.	Dans l'onglet GreenIT des outils de développement, cliquer sur le bouton "Lancer l'analyse". <br />
5.	Les résultats s'affichent. <br />
6.	Vous pouvez sauvegarder ce résultat dans un historique (seuls les indicateurs sont enregistrés) via le bouton "Sauver l'analyse" <br />
7.	L'historique des résultats sauvegardés est disponible via le bouton "Historique" <br />
8.	Pour avoir une analyse des bonnes pratiques, il faut cocher la case "Activer l'analyse des bonnes pratiques". <br />
 
Quelques points de vigilance :<br />
-	Si le nombre de requêtes est à zéro, c'est probablement parce que vous n'avez pas charger la page avec les outils de développement démarrés. Il faut donc penser à faire un rechargement de la page. <br />
-	Pour avoir des mesures correctes, il faut préalablement vider le cache du navigateur (Dans le cas contraire, le volume transféré va être réduit si vous avez déjà consulté le site mesuré). Pour vous éviter d'aller dans les menus du navigateur, un bouton est prévu à cet effet dans l'extension. <br />
-	L'utilisation d'un bloqueur de publicité ou autre filtre a une influence sur le résultat. 
 
 

 
#### L’onglet « Lighthouse » de Chrome
Cet onglet permet de faire un audit d’une application web en 1 clique et d’analyser aussi bien les performances, l’accessibilité, les bonnes pratiques (Sécurité, demande de droits…) et les SEO (conseils pour le référencement naturel).<br />
De très nombreux cas sont testés et tous les conseils sont bons à suivre (Sauf ceux de SEO et PWA quand ils ne sont pas applicables à l’application).

 

 
### La page « statistiques »
La page « Statistiques » de l’application est elle-même une mine d’or pour identifier les potentiels défauts de l’application, particulièrement sur le long terme grâce à l’historique.<br />
Cette page permet d’afficher des statistiques sur les temps de traitements de chacune des API, le nombre de requêtes SQL moyen à chaque appel de ces API, le poids moyen des réponses et le nombre d’appel à chacune des API.<br />
De quoi facilement détecter les services qui ne sont pas utilisés, ceux qui présentent des problèmes de type N+1 (Requêtes SQL), ceux qui ont une durée de traitement qui augmentent au fil du temps, etc.<br />
Toutes les API sont automatiquement scannées, y compris de potentielles nouvelles et présentées dans le tableau, avec ses caractéristiques mesurées.<br />
Les valeurs apparaissent en couleur pour mettre en avant les valeurs les plus problématiques.
 
 
### SonarQube
Sonar est capable nativement de détecter de nombreuses mauvaises pratiques en réalisant une analyse statique du code source. Beaucoup de ces pratiques ont un effet lié au numérique responsable, mais malheureusement il y a encore beaucoup d’autres règles à suivre qui ne sont pas encore pris en compte par Sonar.<br />
De nombreuses entreprises, dont Aubay, se sont alliées pour développer le projet OpenSource EcoCode, un plugin Sonar, permettant d’ajouter à Sonar les règles aujourd’hui identifiées dans le cadre du numérique responsable. <br />
Ce plugin est téléchargeable sur le github : https://github.com/cnumr/ecoCode

### L’utilitaire « Chrono.java »
Enfin, le petit utilitaire Chrono permet de facilement chronométrer une durée d’exécution d’un script.<br />
Appelez Chrono.start(), puis Chrono.trace(String) pour tracer dans les logs le nombre de millisecondes s’étant écoulées depuis l’appel à la méthode start(), ou depuis le dernier appel à la méthode trace(String).<br />
Si le Chrono est envoyé sur un serveur non-local, pensez à appeler Chrono.stop() pour prévenir des fuites de mémoire.
Par exemple :<br />

```java
Chrono.start();
employee.fired();
Chrono.trace("Employee fired");
employeeRepository.save(employee);
Chrono.trace("Employee saved");
Chrono.stop();
```
Dans les logs :

```txt
[CHRONO] Employee fired (67ms)
[CHRONO] Employee saved (24ms)
```
Le Chrono est Thread-safe et peut-être appelé y compris dans des sous-méthodes, sans avoir à transmettre d’information (Comme la date de début du Chrono).

### Le code coverage
La couverture en tests unitaire peut être mesurée directement dans Eclipse (Via un Run Coverage) ou via des plugins à ajouter à la plateforme d’intégration continue, comme JaCoCo ou Corbertura.<br />
Ces outils permettent de surligner en vert les instructions faisant l’objet de tests unitaires (Au moins un TU exécute cette instruction), en jaune les instructions testées, mais avec un ou plusieurs scénarios alternatifs non testés (Par exemple, les TU ne testent le code que si la condition est vraie, mais jamais si elle est fausse : La ligne passe jaune). Et en rouge, toutes les instructions non testées par les tests unitaires.<br />
Un taux de couverture élevé est un bon indicateur pour éviter les régressions dans le code, suite à des évolutions / corrections. Ces taux de couvertures sont calculés au global, ou par classe, ou par méthode, il est fortement recommandé d’avoir une couverture de plus de 80% du total du code.

### Les logs et le mode debug
Hibernate propose une propriété show_sql (Avec Spring boot : spring.jpa.show-sql=true) pour afficher les requêtes SQL qui seront exécutées au niveau de la base de données. Cela fonctionne aussi bien avec JPA, JPA repositories, les criteria, etc.<br />
Il est aussi possible de demander à hibernate de formater avec un peu d’indentation ces requêtes dans les logs : spring.jpa.properties.hibernate.format_sql=true.<br />
Les bibliothèques et frameworks ont parfois des comportements étranges pour les développeurs. Pour les aider à comprendre, de très nombreux logs sont prêts à être activés.

### Can I use ?
Les navigateurs web présentent chacun des limites aux fonctionnalités qu’ils proposent, et malheureusement tous ne sont pas égaux. Si les fonctionnalités de base en HTML 5, ECMAScript et CSS 3 sont assez bien maitrisées aujourd’hui par les principaux navigateurs, le web continue d’évoluer et les navigateurs proposent encore des fonctionnalités expérimentales pas toujours bien standardisées.<br />
Attention donc à ne pas exclure certains utilisateurs en utilisant certaines fonctionnalités implémentées que par un nombre réduit de navigateur, ou présentant des comportements différents d’un navigateur à l’autre.<br />
En cas de doutes, http://caniuse.com permet de vérifier la compatibilité des fonctions JavaScript ou CSS avec les différents navitateurs.
