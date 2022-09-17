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
			hibernateRecursiveInitialization(e);
			if (filterResigned) {
				filterResignedEmployees(e);
			}
		}
		return employees;
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
