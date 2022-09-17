package com.aubay.formations.nr.services;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aubay.formations.nr.entities.Country;
import com.aubay.formations.nr.entities.Employee;
import com.aubay.formations.nr.repositories.CountryRepository;
import com.aubay.formations.nr.repositories.EmployeeRepository;
import com.aubay.formations.nr.utils.AuthentHelper;

/**
 * Employees and Teams services
 *
 * @author jbureau@aubay.com
 */
@Service
@Transactional
public class EmployeeService {

	@Autowired
	private AuthentHelper authentHelper;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private CountryRepository countryRepository;

	/**
	 * Find an Employee by his ID
	 *
	 * @param id
	 * @param filterResigned true if resigned employees must be removed from result,
	 *                       false else
	 * @return
	 */
	public Employee findEmployee(final long id, final boolean filterResigned) {
		final var employee = employeeRepository.getReferenceById(id);
		if (filterResigned) {
			filterResignedEmployees(employee);
		}
		hibernateRecursiveInitialization(employee);
		return employee;
	}

	/**
	 * Create or update employee collection
	 *
	 * @param employees collection
	 * @return saved employees
	 */
	public List<Employee> saveEmployees(final List<Employee> employees) {
		final List<Employee> savedEmployees = employeeRepository.saveAll(employees);
		for (final Employee e : savedEmployees) {
			hibernateRecursiveInitialization(e);
		}
		return savedEmployees;
	}

	/**
	 * Create or update employee
	 *
	 * @param employee
	 * @return saved employee
	 */
	public Employee saveEmployee(final Employee employee) {
		final var original = findEmployee(employee.getId(), false);
		employee.setManager(original.getManager());
		final var e = employeeRepository.save(employee);
		hibernateRecursiveInitialization(e);
		return e;
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
			hibernateRecursiveInitialization(e);
			if (filterResigned) {
				filterResignedEmployees(e);
			}
		}
		return employees;
	}

	/**
	 * Get ALL employees stored in database
	 *
	 * @param filterResigned true if resigned employees must be removed from result,
	 *                       false else
	 * @return all Employee objects
	 */
	public List<Employee> getAllEmployees(final boolean filterResigned) {
		final var employees = employeeRepository.findAll();
		for (final Employee e : employees) {
			hibernateRecursiveInitialization(e);
			if (filterResigned) {
				filterResignedEmployees(e);
			}
		}
		return employees;
	}

	/**
	 * Delete all employees from database, except current user
	 */
	public void removeAllEmployeesExceptMe() {
		employeeRepository.deleteAllExcept(authentHelper.getUser().getEmployee());
	}

	/**
	 * Save countries list in database
	 *
	 * @param countries
	 */
	public void saveCountries(final List<Country> countries) {
		countryRepository.saveAll(countries);
	}

	/**
	 * Get country entity by countryCode
	 *
	 * @param countryCode
	 * @return Country
	 */
	public Country getCountry(final String countryCode) {
		return countryRepository.getReferenceById(countryCode);
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

	/**
	 * Initialize employees (Hibernate LAZY entities links)
	 *
	 * @param employees
	 */
	public void hibernateRecursiveInitialization(final Employee employee) {
		for (final Employee e : employee.getEmployees()) {
			hibernateRecursiveInitialization(e);
			Hibernate.initialize(e);
		}
	}
}
