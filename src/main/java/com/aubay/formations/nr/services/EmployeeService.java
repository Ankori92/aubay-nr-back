package com.aubay.formations.nr.services;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aubay.formations.nr.entities.Country;
import com.aubay.formations.nr.entities.Employee;
import com.aubay.formations.nr.repositories.CountryRepository;
import com.aubay.formations.nr.repositories.EmployeeRepository;
import com.aubay.formations.nr.utils.Chrono;

/**
 * Employees and Teams services
 *
 * @author jbureau@aubay.com
 */
@Service
@Transactional
public class EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private CountryRepository countryRepository;

	/**
	 * Create or update employee
	 *
	 * @param employee
	 * @return saved employee
	 */
	public void saveEmployee(final Employee employee) {
		final var original = employeeRepository.getReferenceById(employee.getId());
		employee.setManager(original.getManager());
		employeeRepository.save(employee);
	}

	/**
	 * Get TOP managers (= employees without manager, and not resigned)
	 *
	 * @param filterResigned true if resigned employees must be removed from result,
	 *                       false else
	 * @return Manager list
	 */
	public List<Employee> getTopManagers(final boolean filterResigned) {
		final var employees = employeeRepository.findByManagerNullAndResignedFalse();
		for (final Employee e : employees) {
			initializeEmployee(e);
		}
		return employees;
	}

	/**
	 * Build the manager's team with only 2 queries (Employees, and countries)<br />
	 * May be optimized with JOIN, but first query is already complex.<br />
	 * A team is the manager (Employee object) and employees under the
	 * responsibility of the manager (List<Employee> employees)<br />
	 * Warning : A sub-employee may be also manager of his proper team
	 *
	 * @param manager not initialized
	 */
	public void initializeEmployee(final Employee manager) {
		Chrono.start();
		// Get all concerned Employees (ONE SHOT QUERY)
		final var employeesInManagerTeam = employeeRepository.findEmployeesInTree(manager.getId());
		Chrono.trace("Employees loaded", employeesInManagerTeam.size());

		// Get all concerned Countries (ONE SHOT QUERY)
		final var countriesId = employeesInManagerTeam.stream().map(e -> (String) e.get("country_id")).toList();
		final var countries = countryRepository.findAllById(countriesId).stream()
				.collect(Collectors.toMap(Country::getCode, Function.identity()));
		Chrono.trace("Countries loaded", countries.size());

		// NO QUERIES AFTER THIS POINT
		// Rebuild all teams
		final Map<Long, Set<Long>> map = new HashMap<>();
		for (final Map<String, Object> emp : employeesInManagerTeam) {
			if (emp.get("manager_id") == null) {
				continue;
			}
			final var managerId = ((BigInteger) emp.get("manager_id")).longValue();
			var team = map.get(managerId);
			if (team == null) {
				team = new HashSet<>();
			}
			team.add(((BigInteger) emp.get("id")).longValue());
			map.put(managerId, team);
		}
		Chrono.trace("Analyzing employees relations");

		// Organize all teams and set final team
		manager.setEmployees(rebuildTeam(map.get(manager.getId()), employeesInManagerTeam, map, countries));
		Chrono.trace("Rebuilt employees relations");
		Chrono.stop();
	}

	/**
	 * Rebuilt manager's team, recursively
	 * @formatter:off
	 * @param employeesInThisTeam
	 * @param allEmployees
	 * @param teamByManager
	 * @param allCountries
	 * @return
	 */
	private List<Employee> rebuildTeam(final Set<Long> employeesInThisTeam, final List<Map<String, Object>> allEmployees,
			final Map<Long, Set<Long>> teamByManager, final Map<String, Country> allCountries) {
		final var result = new ArrayList<Employee>();
		if (employeesInThisTeam == null) {
			return result;
		}
		for (final Long employeeId : employeesInThisTeam) {
			final var empData = getEmployeeInList(allEmployees, employeeId);
			if((boolean) empData.get("resigned")) {
				continue;
			}
			final var team = rebuildTeam(teamByManager.get(((BigInteger) empData.get("id")).longValue()), allEmployees, teamByManager, allCountries);
			final var emp = Employee.builder()
					.id(((BigInteger) empData.get("id")).longValue())
					.firstname((String) empData.get("firstname"))
					.lastname((String) empData.get("lastname"))
					.entryDate((Date) empData.get("entry_date"))
					.resigned((boolean) empData.get("resigned"))
					.salary((int) empData.get("salary"))
					.country(allCountries.get(empData.get("country_id")))
					.employees(team)
					.build();
			emp.setEmployees(team);
			result.add(emp);
		}
		return result;
	}

	/**
	 * @formatter:on
	 */
	public Map<String, Object> getEmployeeInList(final List<Map<String, Object>> allEmps, final long id) {
		for (final Map<String, Object> emp : allEmps) {
			if (id == ((BigInteger) emp.get("id")).longValue()) {
				return emp;
			}
		}
		return null;
	}

	/**
	 * Get all countries in database
	 *
	 * @return all countries
	 */
	public List<Country> getCountries() {
		return countryRepository.findAll();
	}

	/**
	 * Filter resigned employees
	 *
	 * @param employees
	 */
	public void filterResignedEmployees(final Employee employee) {
		final var result = new ArrayList<Employee>();
		for (final Employee e : employee.getEmployees()) {
			if (!e.isResigned()) {
				filterResignedEmployees(e);
				result.add(e);
			}
		}
		employee.setEmployees(result);
	}
}
