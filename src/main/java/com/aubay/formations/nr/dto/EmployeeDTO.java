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
				.employees(employees == null ? new ArrayList<>() : employees.stream().map(EmployeeDTO::toEntity).toList())
				.build();
	}

	/**
	 * Mapper from Entity
	 * @return
	 */
	public static EmployeeDTO fromEntity(final Employee e){
		return EmployeeDTO.builder()
				.id(e.getId())
				.firstname(e.getFirstname())
				.lastname(e.getLastname())
				.entryDate(e.getEntryDate())
				.salary(e.getSalary())
				.resigned(e.isResigned())
				.country(CountryDTO.fromEntity(e.getCountry()))
				.employees(e.getEmployees() == null ? new ArrayList<>() : e.getEmployees().stream().map(EmployeeDTO::fromEntity).toList())
				.build();
	}

	/**
	 * Mapper from entities
	 * @param topManagers
	 * @return
	 */
	public static List<EmployeeDTO> fromEntities(final List<Employee> topManagers) {
		return topManagers.stream().map(EmployeeDTO::fromEntity).toList();
	}
}