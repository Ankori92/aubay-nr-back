package com.aubay.formations.nr.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aubay.formations.nr.dto.EmployeeDTO;
import com.aubay.formations.nr.entities.Country;
import com.aubay.formations.nr.entities.Employee;
import com.aubay.formations.nr.services.EmployeeService;

/**
 * Employee controller
 *
 * @author jbureau@aubay.com
 */
@RestController
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	/**
	 * Get one Employee
	 *
	 * @param id             of employee
	 * @param filterResigned true if resigned employees must be removed from result,
	 *                       false else
	 * @return this Employee
	 */
	@GetMapping("/employees/{id}")
	public Employee getEmployee(@PathVariable("id") final Long id,
			@RequestParam(value = "filterResigned", defaultValue = "false") final boolean filterResigned) {
		return employeeService.findEmployee(id, filterResigned);
	}

	/**
	 * Update employee
	 *
	 * @param Employee
	 * @return Updated employee
	 */
	@PostMapping("/employees")
	public Employee updateEmployee(@RequestBody final EmployeeDTO employeeDto) {
		return employeeService.saveEmployee(employeeDto.toEntity());
	}

	/**
	 * Get all employees
	 *
	 * @return
	 */
	@GetMapping("/employees")
	public List<Employee> getEmployees(
			@RequestParam(value = "filterResigned", defaultValue = "false") final boolean filterResigned) {
		return employeeService.getAllEmployees(filterResigned);
	}

	/**
	 * Get all employees
	 *
	 * @return
	 */
	@GetMapping("/employees/top")
	public List<Employee> getTopManagers(
			@RequestParam(value = "filterResigned", defaultValue = "false") final boolean filterResigned) {
		return employeeService.getTopManagers(filterResigned);
	}

	/**
	 * Remove all employees except authenticated user... May be dangerous.
	 */
	@DeleteMapping("/employees")
	public void removeAllEmployeesExceptMe() {
		employeeService.removeAllEmployeesExceptMe();
	}

	/**
	 * Get countries list
	 *
	 * @return
	 */
	@GetMapping("/countries")
	public List<Country> getCountries() {
		return employeeService.getCountries();
	}
}
