package com.aubay.formations.nr.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
	@PostMapping("/employees")
	public void updateEmployee(@RequestBody final EmployeeDTO employeeDto) {
		employeeService.saveEmployee(employeeDto.toEntity());
	}

	/**
	 * Get all employees
	 *
	 * @return
	 */
	@GetMapping("/employees/top")
	public List<EmployeeDTO> getTopManagers(
			@RequestParam(value = "filterResigned", defaultValue = "false") final boolean filterResigned) {
		return EmployeeDTO.fromEntities(employeeService.getTopManagers(filterResigned));
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
