package com.aubay.formations.nr.entities;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Country entity
 *
 * @author jbureau@aubay.com
 */
@Entity
@Table(name = "countries")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class CountryEntity implements Serializable {

	private static final long serialVersionUID = -9060115814034824006L;

	@Id
	private String code;

	private String labelFr;

	public CountryEntity() {
	}

	public CountryEntity(final String code, final String labelFr) {
		this.code = code;
		this.labelFr = labelFr;
	}

	public String getCode() {
		return code;
	}

	public String getLabelFr() {
		return labelFr;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final var other = (CountryEntity) obj;
		return Objects.equals(code, other.code);
	}

	@Override
	public int hashCode() {
		return Objects.hash(code);
	}
}