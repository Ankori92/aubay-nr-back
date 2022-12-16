package com.aubay.formations.nr.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aubay.formations.nr.dto.StatisticsDTO;
import com.aubay.formations.nr.entities.Usage;
import com.aubay.formations.nr.repositories.EmployeeRepository;
import com.aubay.formations.nr.repositories.UsageRepository;
import com.aubay.formations.nr.utils.Chrono;

/**
 * Exercices controller
 *
 * @formatter:off
 * @author jbureau@aubay.com
 */
@RestController
@RequestMapping("/tp")
@Transactional
public class ExercicesController {

	private static final Logger LOG = LoggerFactory.getLogger(ExercicesController.class);

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private UsageRepository usageRepository;

	/**
	 * Exercice 1 (25 minutes)
	 *
	 * Cette méthode est conçue pour afficher dans les logs la liste des employés qui ont été consultés, par leur nom et prénom
	 * On considère qu'un employé a été consulté quand il apparait dans la liste "usages" sous la forme "/employees/{id}".
	 * Cet algorithme présente le défaut de réaliser un appel à la base de données dans une boucle, il réalise donc, en pratique, des millions de requêtes SQL à la base de données.
	 * Cet anti-pattern peut-être particulièrement difficile à détecter car les appels à la base de données peuvent être "cachées" dans des méthodes, et pas directement visibles au niveau de la boucle.
	 * Il se retrouve donc de manière très fréquente dans les applications, malgré son effet catastrophique sur les performances.
	 * Vous devez donc comprendre le problème et trouver un nouvel algorithme pour limiter le nombre d'appels à la base de données.
	 *
	 */
	@GetMapping("/loop")
	public void sqlLoop() {
		Chrono.start();
		final var regex = Pattern.compile("/employees/\\d+");
		final var usages = Arrays.asList( // En pratique, nous supposerons que cette liste contient des millions d'entrées
				"/employees/1",
				"/employees/2",
				"/employees/3",
				"/stats",
				"/employees/4",
				"/countries/8765",
				"/employees/5",
				"/employees/6",
				"/employees/7",
				"/employees/8",
				"/employees/9",
				"/employees/10"
				// [...]
				);
		for(final var usage : usages) {
			if(regex.matcher(usage).matches()) {
				final var employeeId = Long.valueOf(usage.substring(11));
				final var employee = employeeRepository.getReferenceById(employeeId); // Une requête SQL pour chacun des usages de la liste au bon format (Plusieurs millions d'éléments)
				LOG.info("L'employé {} a été consulté", employee.getFirstname() + " " + employee.getLastname());
			}
		}
		Chrono.trace("Les employés consultés ont été affichés");
	}

	/**
	 * Exercice 2 (20 minutes)
	 *
	 * Cette méthode est conçue pour produire puis afficher des statistiques : La moyenne de requêtes SQL exécutées par usage
	 * Malheureusement, cette algorithme récupère l'intégralité des usages (Des milliards !) pour ensuite les filtrer.
	 * Vous devez comprendre la problématique et concevoir un nouvel algorithme qui diminuera grandement le volume de données inutiles renvoyées par la base de données.
	 */
	@GetMapping("/sql")
	public void sqlQueryOptimization() {
		Chrono.start();
		// Récupération des usages
		final var usages = usageRepository.findAll();
		// Chaque usage est regroupé avec les autres usages de la même URI dans une HashMap
		final var stats = new HashMap<String, StatisticsDTO>();
		for(final Usage usage : usages) {
			var stat = stats.get(usage.getUri());
			if(stat == null) {
				stat = new StatisticsDTO(usage.getUri(), usage.getDuration(), usage.getQueries(), usage.getWeight(), 0);
				stats.put(usage.getUri(), stat);
			}
			stat.incrementValues(usage); // Ajoute les valeurs de l'usage en cours aux statistiques de l'URI
		}
		// Calcul la moyenne des valeurs pour chacune des statistiques
		stats.forEach((uri, stat) -> stat.computeAverage());
		// Affiche les statistiques
		for(final StatisticsDTO stat : stats.values()) {
			LOG.info(stat.getUri() + " : " + stat.getQueries() + " requête(s) par appel");
		}
		Chrono.trace("Les statistiques ont été affichées", usages.size());
	}

	/**
	 * Exercice 3 (10 minutes)
	 *
	 * Cette méthode est conçue pour convertir des chaines de caractères en nombre, et afficher les cas en erreur dans les logs.
	 * Malheureusement, cet algorithme utilise les exceptions pour traiter le cas d'erreur, ce qui implique la création de la stacktrace et théoriquement son affichage dans les logs.
	 * La gestion est actuellement faite avec un LOG.error qui trace la stacktrace, tel que cela doit toujours être fait, mais d'autres manières habituelles ont été proposées en commentaires, pour tester.
	 * Vous devez donc ici prendre conscience de l'impact de la création des exceptions, et proposer un nouvel algorithme beaucoup plus rapide que ceux proposés.
	 */
	@GetMapping("/catch")
	public void avoidCatching() {
		Chrono.start();
		var errors = 0;
		final var myNumbers = new ArrayList<Double>();
		for(var i = 0 ; i < 10000 ; i++) {
			final var number = i >= 20 ? "a" : "" + i; // 20 cas OK, le reste KO
			try {
				myNumbers.add(Double.valueOf(number));
			} catch(final NumberFormatException e) {
				LOG.error("Format des données incorrect", e); // LOG au niveau ERROR, car toutes les exceptions doivent être tracées
				errors++;
				////////////////////////////////////////////////////////////////
				///////// Autres manières de gérer (ou pas) l'exception ////////
				////////////////////////////////////////////////////////////////
				// LOG.debug("Format des données incorrect", e); // LOG au niveau DEBUG, ce cas est fréquemment attendu sans que ça ne pose de problème /!\
				// LOG.debug("Format des données incorrect, sans affichage de la stacktrace"); /!\ /!\
				// Aucun log, l'exception est totalement passée sous silence /!\ /!\ /!\
				////////////////////////////////////////////////////////////////
			}
		}
		Chrono.trace("Tous les nombres ont été convertis en double, cas en erreur : " + errors);
		myNumbers.stream().forEach(number -> LOG.info("Nombre : " + number));
	}

	/**
	 * Exercice 4 (10 minutes)
	 *
	 * Cette méthode est conçue pour afficher dans les logs le pays de chacun des top-managers.
	 * Elle commence donc par récupérer la liste des top-managers (employees), puis boucle sur chacun d'entre-eux pour afficher le LabelFr de leur pays respectif.
	 * Malheureusement, cet algorithme réalise une très grande quantité de requêtes SQL pour récupérer les pays de chaque top-manager.
	 * Vous devez donc comprendre le problème et proposer une solution pour éviter de réaliser N+1 requêtes SQL.
	 */
	@GetMapping("/np1")
	public void hibernateNp1Problem() {
		Chrono.start();
		final var employees = employeeRepository.findByManagerNullAndResignedFalse(); // La première requête récupère une liste de manager
		for(final var employee : employees) {
			LOG.info("Pays de l'employé " + employee.getId() + " : " + employee.getCountry().getLabelFr()); // Chacun des pays n'ayant pas été récupérés lors de la première requête, N requêtes sont exécutées ici
		}
		Chrono.trace("Les pays des top-managers ont été affichés", employees.size());
	}
}
