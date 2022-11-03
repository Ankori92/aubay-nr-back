package com.aubay.formations.nr.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Data for home page
 *
 * @author jbureau@aubay.com
 */
public class HomeInfoDTO implements Serializable {

	private static final long serialVersionUID = -6451712232351010265L;

	private final List<CountryDTO> countries;

	private final List<EmployeeDTO> employees;

	public HomeInfoDTO(final List<CountryDTO> countries, final List<EmployeeDTO> employees) {
		this.countries = countries;
		this.employees = employees;
	}

	public List<CountryDTO> getCountries() {
		return countries;
	}

	public List<EmployeeDTO> getEmployees() {
		return employees;
	}
}