package com.aubay.formations.nr.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Employee entity
 *
 * @author jbureau@aubay.com
 */
@Entity
@Table(name = "employees")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "manager" /* Infinite loop with "employees" */ })
public class EmployeeEntity implements Serializable {

	private static final long serialVersionUID = 8413415934172116852L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_generator")
	@SequenceGenerator(name = "employee_generator", sequenceName = "employee_seq")
	private long id;

	private String firstname;

	private String lastname;

	private Date entryDate;

	private Integer salary;

	private boolean resigned;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "country_id")
	private CountryEntity country;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "manager_id")
	private EmployeeEntity manager;

	@OneToMany(mappedBy = "manager", fetch = FetchType.LAZY)
	private List<EmployeeEntity> employees;

	private transient int teamSize;

	public static Builder builder(final long id) {
		return new EmployeeEntity.Builder().builder(id);
	}

	public static class Builder {
		private long id;
		private String firstname;
		private String lastname;
		private Date entryDate;
		private Integer salary;
		private boolean resigned;
		private CountryEntity country;
		private EmployeeEntity manager;
		private List<EmployeeEntity> employees;
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

		public Builder resigned(final boolean resigned) {
			this.resigned = resigned;
			return this;
		}

		public Builder country(final CountryEntity country) {
			this.country = country;
			return this;
		}

		public Builder manager(final EmployeeEntity manager) {
			this.manager = manager;
			return this;
		}

		public Builder employees(final List<EmployeeEntity> employees) {
			this.employees = employees;
			return this;
		}

		public Builder teamSize(final int teamSize) {
			this.teamSize = teamSize;
			return this;
		}

		public EmployeeEntity build() {
			final var e = new EmployeeEntity();
			e.id = id;
			e.firstname = firstname;
			e.lastname = lastname;
			e.entryDate = entryDate;
			e.salary = salary;
			e.resigned = resigned;
			e.country = country;
			e.manager = manager;
			e.employees = employees;
			e.teamSize = teamSize;
			return e;
		}
	}

	public long getId() {
		return id;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public Date getEntryDate() {
		return entryDate;
	}

	public Integer getSalary() {
		return salary;
	}

	public boolean isResigned() {
		return resigned;
	}

	public CountryEntity getCountry() {
		return country;
	}

	public EmployeeEntity getManager() {
		return manager;
	}

	public List<EmployeeEntity> getEmployees() {
		return employees;
	}

	public int getTeamSize() {
		return teamSize;
	}

	public void setManager(final EmployeeEntity manager) {
		this.manager = manager;
	}

	public void setEmployees(final List<EmployeeEntity> employees) {
		this.employees = employees;
	}

	public void setCountry(final CountryEntity country) {
		this.country = country;
	}

	public void setTeamSize(final int teamSize) {
		this.teamSize = teamSize;
	}

	@Override
	public final String toString() {
		return firstname + " " + lastname;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final var other = (EmployeeEntity) obj;
		return id == other.id;
	}
}