package com.aubay.formations.nr.controllers;

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

import com.aubay.formations.nr.dto.CountryDTO;
import com.aubay.formations.nr.dto.EmployeeDTO;
import com.aubay.formations.nr.dto.HomeInfoDTO;
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
	 * Get countries list
	 *
	 * @return
	 */
	@Cacheable("home")
	@GetMapping("/home")
	public HomeInfoDTO getHomeInfo() {
		final var countries = CountryDTO.fromEntities(employeeService.getCountries());
		final var employees = EmployeeDTO.fromEntities(employeeService.getTopManagers());
		return new HomeInfoDTO(countries, employees);
	}

	/**
	 * Clear Spring caches
	 */
	@CacheEvict(value = { "home", "employees" }, allEntries = true)
	@DeleteMapping("/cache")
	public void clearCache() {
	}
}
