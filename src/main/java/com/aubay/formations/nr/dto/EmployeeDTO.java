package com.aubay.formations.nr.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aubay.formations.nr.entities.Employee;

/**
 * Employee entity
 *
 * @author jbureau@aubay.com
 */
public class EmployeeDTO implements Serializable {

	private static final long serialVersionUID = 723051368789385748L;

	private long id;

	private String firstname;

	private String lastname;

	private Date entryDate;

	private Integer salary;

	private CountryDTO country;

	private List<EmployeeDTO> employees;

	private Integer teamSize;

	public EmployeeDTO() {
	}

	/**
	 * Mapper to Entity
	 * @formatter:off
	 * @return
	 */
	public Employee toEntity() {
		return Employee.builder(id)
				.firstname(firstname)
				.lastname(lastname)
				.entryDate(entryDate)
				.salary(salary)
				.resigned(false)
				.country(country.toEntity())
				.employees(employees == null ? new ArrayList<>() : employees.stream().map(EmployeeDTO::toEntity).toList())
				.build();
	}

	/**
	 * Mapper from Entity
	 * @return
	 */
	public static EmployeeDTO fromEntity(final Employee e){
		return EmployeeDTO.builder(e.getId())
				.firstname(e.getFirstname())
				.lastname(e.getLastname())
				.entryDate(e.getEntryDate())
				.salary(e.getSalary())
				.country(CountryDTO.fromEntity(e.getCountry()))
				.employees(e.getEmployees() == null ? new ArrayList<>() : e.getEmployees().stream().map(EmployeeDTO::fromEntity).toList())
				.teamSize(e.getTeamSize())
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

	public static Builder builder(final long id) {
		return new EmployeeDTO.Builder().builder(id);
	}

	public static class Builder {
		private long id;
		private String firstname;
		private String lastname;
		private Date entryDate;
		private Integer salary;
		private CountryDTO country;
		private List<EmployeeDTO> employees;
		private transient int teamSize;

		public Builder builder(final long id) {
			this.id = id;
			return this;
		}

		public Builder firstname(final String firstname) {
			this.firstname = firstname;
			return this;
		}

		public Builder lastname(final String lastname) {
			this.lastname = lastname;
			return this;
		}

		public Builder entryDate(final Date entryDate) {
			this.entryDate = entryDate;
			return this;
		}

		public Builder salary(final Integer salary) {
			this.salary = salary;
			return this;
		}

		public Builder country(final CountryDTO country) {
			this.country = country;
			return this;
		}

		public Builder employees(final List<EmployeeDTO> employees) {
			this.employees = employees;
			return this;
		}

		public Builder teamSize(final int teamSize) {
			this.teamSize = teamSize;
			return this;
		}

		public EmployeeDTO build() {
			final var e = new EmployeeDTO();
			e.id = id;
			e.firstname = firstname;
			e.lastname = lastname;
			e.entryDate = entryDate;
			e.salary = salary;
			e.country = country;
			e.employees = employees;
			e.teamSize = teamSize;
			return e;
		}
	}

	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(final String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(final String lastname) {
		this.lastname = lastname;
	}

	public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(final Date entryDate) {
		this.entryDate = entryDate;
	}

	public Integer getSalary() {
		return salary;
	}

	public void setSalary(final Integer salary) {
		this.salary = salary;
	}

	public CountryDTO getCountry() {
		return country;
	}

	public void setCountry(final CountryDTO country) {
		this.country = country;
	}

	public List<EmployeeDTO> getEmployees() {
		return employees;
	}

	public void setEmployees(final List<EmployeeDTO> employees) {
		this.employees = employees;
	}

	public Integer getTeamSize() {
		return teamSize;
	}

	public void setTeamSize(final Integer teamSize) {
		this.teamSize = teamSize;
	}
}