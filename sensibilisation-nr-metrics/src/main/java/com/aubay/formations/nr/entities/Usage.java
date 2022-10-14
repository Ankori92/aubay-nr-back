package com.aubay.formations.nr.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Metric entity<br />
 * Represent usage of one application functionality<br />
 *
 * Interpretation : "This API was called this day, triggered X SQL queries and Y
 * ko of data was returned after Z milliseconds."
 *
 * @author jbureau@aubay.com
 */
@Entity
@Table(name = "usages")
public class Usage implements Serializable {

	private static final long serialVersionUID = -929041270800337913L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "metric_generator")
	@SequenceGenerator(name = "metric_generator", sequenceName = "metric_seq")
	private long id;

	private String uri;

	@Column(name = "date", nullable = false, columnDefinition = "DATE DEFAULT CURRENT_TIMESTAMP")
	private Date date;

	private int duration;

	private int queries;

	private int weight;

	public Usage() {
	}

	public Usage(final String uri, final int duration, final int queries, final int weight) {
		this.uri = uri;
		this.duration = duration;
		this.queries = queries;
		this.weight = weight;
		date = new Date();
	}

	public long getId() {
		return id;
	}

	public String getUri() {
		return uri;
	}

	public Date getDate() {
		return date;
	}

	public int getDuration() {
		return duration;
	}

	public int getQueries() {
		return queries;
	}

	public int getWeight() {
		return weight;
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
		final Usage other = (Usage) obj;
		return id == other.id;
	}
}