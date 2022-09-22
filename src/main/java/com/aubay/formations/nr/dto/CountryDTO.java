package com.aubay.formations.nr.dto;

import java.io.Serializable;

import com.aubay.formations.nr.entities.Country;

/**
 * Country entity
 *
 * @author jbureau@aubay.com
 */
public class CountryDTO implements Serializable {

	private static final long serialVersionUID = -7468361382278959302L;

	private String code;

	private String labelFr;

	public CountryDTO() {

	}

	public CountryDTO(final String code, final String labelFr) {
		this.code = code;
		this.labelFr = labelFr;
	}

	/**
	 * Mapper to Entity
	 * @formatter:off
	 * @return
	 */
	public Country toEntity() {
		return new Country(code, labelFr);
	}

	public static CountryDTO fromEntity(final Country c) {
		return new CountryDTO(c.getCode(), c.getLabelFr());
	}

	public String getCode() {
		return code;
	}

	public String getLabelFr() {
		return labelFr;
	}

}