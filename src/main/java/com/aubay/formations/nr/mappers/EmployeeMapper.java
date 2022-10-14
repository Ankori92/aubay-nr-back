package com.aubay.formations.nr.mappers;

import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.aubay.formations.nr.entities.CountryEntity;
import com.aubay.formations.nr.entities.EmployeeEntity;
import com.aubay.formations.nr.rest.model.Country;
import com.aubay.formations.nr.rest.model.Employee;

public class EmployeeMapper {
	public static EmployeeEntity toEntity(final Employee employee) {
		return EmployeeEntity.builder(employee.getId()).firstname(employee.getFirstname())
				.lastname(employee.getLastname()).country(toEntity(employee.getCountry())).salary(employee.getSalary())
				.teamSize(employee.getTeamSize())
				.entryDate(new Date(employee.getEntryDate().toInstant().toEpochMilli()))
				.employees(toEntity(employee.getEmployees())).build();
	}

	public static Employee toDto(final EmployeeEntity employee) {
		final var dto = new Employee();
		dto.setId(employee.getId());
		dto.setFirstname(employee.getFirstname());
		dto.setLastname(employee.getLastname());
		dto.setCountry(toDto(employee.getCountry()));
		dto.setTeamSize(employee.getTeamSize());
		dto.setEntryDate(employee.getEntryDate().toInstant().atOffset(ZoneOffset.UTC));
		dto.setSalary(employee.getSalary());
		dto.setEmployees(toDto(employee.getEmployees()));
		return dto;
	}

	public static List<EmployeeEntity> toEntity(final List<Employee> employees) {
		return employees.stream().map(EmployeeMapper::toEntity).collect(Collectors.toList());
	}

	public static List<Employee> toDto(final List<EmployeeEntity> employees) {
		return employees.stream().map(EmployeeMapper::toDto).collect(Collectors.toList());
	}

	public static CountryEntity toEntity(final Country country) {
		return new CountryEntity(country.getCode(), country.getLabelFr());
	}

	public static Country toDto(final CountryEntity country) {
		final var dto = new Country();
		dto.setCode(country.getCode());
		dto.setLabelFr(country.getLabelFr());
		return dto;
	}

	public static List<Country> toCountriesDto(final List<CountryEntity> countries) {
		return countries.stream().map(EmployeeMapper::toDto).collect(Collectors.toList());
	}
}
