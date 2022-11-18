package com.aubay.formations.nr.controllers;

import java.util.HashMap;

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

	@GetMapping("/np1")
	public void hibernateNp1Problem() {
		Chrono.start();
		final var employees = employeeRepository.findByManagerNullAndResignedFalse(); // La première requête récupère une liste de manager
		for(final var employee : employees) {
			LOG.info("Employee : " + employee.getCountry().getLabelFr()); // Chacun des pays n'ayant pas été récupérés lors de la première requête, N requêtes sont exécutées ici
		}
		Chrono.trace("Les pays des top-managers ont été affichés", employees.size());
	}

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
}
