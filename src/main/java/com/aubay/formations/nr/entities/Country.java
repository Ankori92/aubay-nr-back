package com.aubay.formations.nr.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Country entity
 *
 * @author jbureau@aubay.com
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "countries")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Country implements Serializable {

	private static final long serialVersionUID = -9060115814034824006L;

	@Id
	@EqualsAndHashCode.Include
	private String code;

	private String labelFr;

}