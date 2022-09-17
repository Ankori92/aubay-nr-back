package com.aubay.formations.nr.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aubay.formations.nr.entities.Employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Employee entity
 *
 * @author jbureau@aubay.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {

	private long id;

	private String firstname;

	private String lastname;

	private Date entryDate;

	private Integer salary;

	private boolean resigned;

	private CountryDTO country;

	private EmployeeDTO manager;

	private List<EmployeeDTO> employees;

	/**
	 * Mapper to Entity
	 * @formatter:off
	 * @return
	 */
	public Employee toEntity() {
		return Employee.builder()
				.id(id)
				.firstname(firstname)
				.lastname(lastname)
				.entryDate(entryDate)
				.salary(salary)
				.resigned(resigned)
				.country(country.toEntity())
				.manager(manager == null ? null : manager.toEntity())
				.employees(employees == null ? new ArrayList<>() : employees.stream().map(EmployeeDTO::toEntity).toList())
				.build();
	}
}