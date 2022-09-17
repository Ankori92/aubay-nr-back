package com.aubay.formations.nr.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Usage entity<br />
 * Represent usage of one application functionality<br />
 *
 * Interpretation : "This API was called this day, triggered X SQL queries and Y
 * ko of data was returned after Z milliseconds."
 *
 * @author jbureau@aubay.com
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usages")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Usage implements Serializable {

	private static final long serialVersionUID = -929041270800337913L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usage_generator")
	@SequenceGenerator(name = "usage_generator", sequenceName = "usage_seq")
	@EqualsAndHashCode.Include
	private long id;

	private String uri;

	@Column(name = "date", nullable = false, columnDefinition = "DATE DEFAULT CURRENT_TIMESTAMP")
	private Date date;

	private int duration;

	private int queries;

	private int weight;

}