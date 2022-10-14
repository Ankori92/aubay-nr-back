package com.aubay.formations.nr.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aubay.formations.nr.dto.EmployeeDTO;
import com.aubay.formations.nr.entities.Country;
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
	 * Update employee
	 *
	 * @param Employee
	 * @return Updated employee
	 */
	@CacheEvict(value = "employees", allEntries = true)
	@PostMapping("/employees")
	public void updateEmployee(@RequestBody final EmployeeDTO employeeDto) {
		employeeService.saveEmployee(employeeDto.toEntity());
	}

	/**
	 * Get all employees
	 *
	 * @return
	 */
	@Cacheable("employees")
	@GetMapping("/employees/{id}")
	public EmployeeDTO getManager(
			@RequestParam(value = "filterResigned", defaultValue = "false") final boolean filterResigned,
			@RequestParam(value = "onlyDirectTeam", defaultValue = "true") final boolean onlyDirectTeam,
			@PathVariable("id") final long id) {
		return EmployeeDTO.fromEntity(employeeService.getEmployee(id, filterResigned, onlyDirectTeam));
	}

	/**
	 * Get TOP employees
	 *
	 * @return
	 */
	@Cacheable("topemployees")
	@GetMapping("/employees/top")
	public List<EmployeeDTO> getTopManagers() {
		return EmployeeDTO.fromEntities(employeeService.getTopManagers());
	}

	/**
	 * Get countries list
	 *
	 * @return
	 */
	@Cacheable("countries")
	@GetMapping("/countries")
	public List<Country> getCountries() {
		return employeeService.getCountries();
	}

	/**
	 * Clear Spring caches
	 */
	@CacheEvict(value = { "countries", "topemployees", "employees" }, allEntries = true)
	@DeleteMapping("/cache")
	public void clearCache() {
	}
}
