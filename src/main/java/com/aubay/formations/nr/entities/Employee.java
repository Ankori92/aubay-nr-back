package com.aubay.formations.nr.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Employee entity
 *
 * @author jbureau@aubay.com
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employees")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "manager" /* Infinite loop with "employees" */ })
public class Employee implements Serializable {

	private static final long serialVersionUID = 8413415934172116852L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_generator")
	@SequenceGenerator(name = "employee_generator", sequenceName = "employee_seq")
	@EqualsAndHashCode.Include
	private long id;

	private String firstname;

	private String lastname;

	private Date entryDate;

	private Integer salary;

	private boolean resigned;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "country_id")
	private Country country;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "manager_id")
	private Employee manager;

	@OneToMany(mappedBy = "manager", fetch = FetchType.LAZY)
	private List<Employee> employees;

	private transient int teamSize;

	@Override
	public final String toString() {
		return firstname + " " + lastname;
	}
}