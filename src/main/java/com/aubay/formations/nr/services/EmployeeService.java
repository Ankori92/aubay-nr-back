package com.aubay.formations.nr.services;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aubay.formations.nr.entities.Country;
import com.aubay.formations.nr.entities.Employee;
import com.aubay.formations.nr.repositories.CountryRepository;
import com.aubay.formations.nr.repositories.EmployeeRepository;
import com.aubay.formations.nr.utils.Utils;

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
	 * Get employee by ID
	 *
	 * @param id
	 * @param filterResigned
	 * @param onlyDirectTeam
	 * @return
	 */
	public Employee getEmployee(final long id, final boolean filterResigned, final boolean onlyDirectTeam) {
		final var employee = employeeRepository.getReferenceById(id);
		initializeEmployee(employee, onlyDirectTeam);
		return employee;
	}

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
	 * Get TOP managers (= employees without manager)
	 *
	 * @return Manager list
	 */
	public List<Employee> getTopManagers() {
		final var employees = employeeRepository.findByManagerNullAndResignedFalse();
		final var employeesId = employees.stream().map(Employee::getId).toList();
		final var relations = employeeRepository.findEmployeesIdBelow(employeesId);
		final var allRelations = analyzeRelations(relations);
		final var countries = countryRepository
				.findAllById(employees.stream().map(e -> e.getCountry().getCode()).toList());
		final var countriesById = Utils.mapElementsById(countries, Country::getCode);
		employees.stream().forEach(e -> {
			e.setEmployees(new ArrayList<>());
			e.setCountry(countriesById.get(e.getCountry().getCode()));
			e.setTeamSize(computeTeamSize(e.getId(), allRelations));
		});
		return employees;
	}

	/**
	 * Build the manager's team with only 2 queries (Employees, and countries)<br />
	 * May be optimized with JOIN, but first query is already complex.<br />
	 * A team is the manager (Employee object) and employees under the
	 * responsibility of the manager (List<Employee> employees)<br />
	 * Warning : A sub-employee may be also manager of his proper team
	 *
	 * @param manager        not initialized
	 * @param onlyDirectTeam
	 */
	public void initializeEmployee(final Employee manager, final boolean onlyDirectTeam) {
		initializeEmployees(Arrays.asList(manager), onlyDirectTeam);
	}

	/**
	 * Build the manager's team with only 2 queries (Employees, and countries)<br />
	 * May be optimized with JOIN, but first query is already complex.<br />
	 * A team is the manager (Employee object) and employees under the
	 * responsibility of the manager (List<Employee> employees)<br />
	 * Warning : A sub-employee may be also manager of his proper team
	 *
	 * @param managers       not initialized
	 * @param onlyDirectTeam
	 */
	public void initializeEmployees(final List<Employee> managers, final boolean onlyDirectTeam) {
		// Get all concerned Employees (ONE SHOT QUERY)
		final var employeesId = managers.stream().map(Employee::getId).toList();
		List<Map<String, Object>> allEmpData = null;
		List<Map<String, Object>> allRelations = null;
		if (onlyDirectTeam) {
			allEmpData = employeeRepository.findDirectTeamOf(employeesId);
			allRelations = employeeRepository.findEmployeesIdBelow(employeesId);
		} else {
			allEmpData = employeeRepository.findEmployeesInTree(employeesId);
		}

		// Get all concerned Countries (ONE SHOT QUERY)
		final var allCountries = findCountries(managers, allEmpData);

		// NO QUERIES AFTER THIS POINT
		// Analyze all relations between employees
		final var relations = analyzeRelations(onlyDirectTeam ? allRelations : allEmpData);

		// Organize all teams and set final team
		for (final Employee manager : managers) {
			final var managerId = manager.getId();
			manager.setCountry(allCountries.get(manager.getCountry().getCode()));
			manager.setEmployees(buildTeam(managerId, allEmpData, relations, allCountries, onlyDirectTeam));
			manager.setTeamSize(computeTeamSize(managerId, relations));
		}
	}

	public Map<String, Country> findCountries(final List<Employee> managers,
			final List<Map<String, Object>> allEmpData) {
		// Countries in team
		final var countriesIdTeam = allEmpData.stream().map(this::getCountryId);
		// Countries in managers
		final var countriesIdEmp = managers.stream().map(e -> e.getCountry().getCode());
		// Find countries in DB
		final var countriesId = Stream.concat(countriesIdTeam, countriesIdEmp).distinct().toList();
		final var countries = countryRepository.findAllById(countriesId);
		return Utils.mapElementsById(countries, Country::getCode);
	}

	/**
	 * Analyze employees relations
	 *
	 * @param allEmpData
	 * @return Map<ID, Set<ID>> : Map<Manager, Set<Employee>>
	 */
	public Map<Long, Set<Long>> analyzeRelations(final List<Map<String, Object>> allEmpData) {
		final var relations = new HashMap<Long, Set<Long>>();
		for (final Map<String, Object> empData : allEmpData) {
			final var managerId = getManagerId(empData);
			if (managerId == null) {
				continue;
			}
			final var team = relations.getOrDefault(managerId, new HashSet<>());
			team.add(getId(empData));
			relations.put(managerId, team);
		}
		return relations;
	}

	/**
	 * Rebuilt manager's team, recursively
	 *
	 * @param employeesInThisTeam
	 * @param allEmpData
	 * @param relations
	 * @param allCountries
	 * @param onlyDirectTeam
	 * @return
	 */
	private ArrayList<Employee> buildTeam(final long id, final List<Map<String, Object>> allEmpData,
			final Map<Long, Set<Long>> relations, final Map<String, Country> allCountries,
			final boolean onlyDirectTeam) {
		final var employeesInThisTeam = relations.getOrDefault(id, new HashSet<Long>());
		final var result = new ArrayList<Employee>();
		for (final Long employeeId : employeesInThisTeam) {
			final var empData = getEmpData(allEmpData, employeeId);
			if (!isResigned(empData)) {
				result.add(buildEmployee(empData, allEmpData, relations, allCountries, onlyDirectTeam));
			}
		}
		return result;
	}

	/**
	 * Build Employee from empData
	 * @formatter:off
	 * @param empData
	 * @param allEmpData
	 * @param relations
	 * @param allCountries
	 * @param onlyDirectTeam
	 * @return built Employee
	 */
	private Employee buildEmployee(final Map<String, Object> empData, final List<Map<String, Object>> allEmpData,
			final Map<Long, Set<Long>> relations, final Map<String, Country> allCountries, final boolean onlyDirectTeam) {
		final var employeeId = getId(empData);
		return Employee.builder(employeeId)
				.firstname(getFirstname(empData))
				.lastname(getLastname(empData))
				.entryDate(getEntryDate(empData))
				.resigned(isResigned(empData))
				.salary(getSalary(empData))
				.country(allCountries.get(getCountryId(empData)))
				.teamSize(computeTeamSize(employeeId, relations))
				.employees(onlyDirectTeam ? new ArrayList<Employee>() : buildTeam(employeeId, allEmpData, relations, allCountries, onlyDirectTeam))
				.build();
	}

	/**
	 * Compute team size recursively
	 * @formatter:on
	 * @param managerId
	 * @param allEmpData
	 * @param relations
	 * @return
	 */
	private int computeTeamSize(final long managerId, final Map<Long, Set<Long>> relations) {
		final var team = relations.getOrDefault(managerId, new HashSet<>());
		return team.stream().mapToInt(id -> 1 + computeTeamSize(id, relations)).sum();
	}

	/**
	 * Find empData in allEmpData
	 */
	public Map<String, Object> getEmpData(final List<Map<String, Object>> allEmps, final long id) {
		for (final Map<String, Object> empData : allEmps) {
			if (getId(empData).equals(id)) {
				return empData;
			}
		}
		return null;
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

	/**
	 * Get all countries in database
	 *
	 * @return all countries
	 */
	public List<Country> getCountries() {
		return countryRepository.findAll();
	}

	/**
	 * Get Employee's id from empData
	 *
	 * @param empData
	 * @return Employee's id
	 */
	private Long getId(final Map<String, Object> empData) {
		final var bigInteger = (BigInteger) empData.get("id");
		return bigInteger == null ? null : bigInteger.longValue();
	}

	/**
	 * Get Employee's manager's id from empData
	 *
	 * @param empData
	 * @return Employee's manager's id
	 */
	private Long getManagerId(final Map<String, Object> empData) {
		final var bigInteger = (BigInteger) empData.get("manager_id");
		return bigInteger == null ? null : bigInteger.longValue();
	}

	/**
	 * Get Employee's salary from empData
	 *
	 * @param empData
	 * @return Employee's salary
	 */
	private int getSalary(final Map<String, Object> empData) {
		return (int) empData.get("salary");
	}

	/**
	 * Get Employee's country's id from empData
	 *
	 * @param empData
	 * @return Employee's country's id
	 */
	private String getCountryId(final Map<String, Object> empData) {
		return (String) empData.get("country_id");
	}

	/**
	 * Get Employee's firstname from empData
	 *
	 * @param empData
	 * @return Employee's firstname
	 */
	private String getFirstname(final Map<String, Object> empData) {
		return (String) empData.get("firstname");
	}

	/**
	 * Get Employee's lastname from empData
	 *
	 * @param empData
	 * @return Employee's lastname
	 */
	private String getLastname(final Map<String, Object> empData) {
		return (String) empData.get("lastname");
	}

	/**
	 * Get Employee's entry date from empData
	 *
	 * @param empData
	 * @return Employee's entry date
	 */
	private Date getEntryDate(final Map<String, Object> empData) {
		return (Date) empData.get("entry_date");
	}

	/**
	 * Get Employee's resigned flag from empData
	 *
	 * @param empData
	 * @return true if employee is resigned, false else
	 */
	private boolean isResigned(final Map<String, Object> empData) {
		return (boolean) empData.get("resigned");
	}
}
