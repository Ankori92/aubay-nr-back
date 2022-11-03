package com.aubay.formations.nr.controllers;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aubay.formations.nr.repositories.EmployeeRepository;

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

	@GetMapping("/np1")
	public void hibernateNp1Problem() {
		final var employees = employeeRepository.findByManagerNullAndResignedFalse(); // La première requête récupère une liste de manager
		for(final var employee : employees) {
			LOG.info("Employee : " + employee.getCountry().getLabelFr()); // Chacun des pays n'ayant pas été récupérés lors de la première requête, N requêtes sont exécutées ici
		}
	}
}
