package com.aubay.formations.nr.dto;

import com.aubay.formations.nr.entities.Country;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Country entity
 *
 * @author jbureau@aubay.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryDTO {

	private String code;

	private String labelFr;

	/**
	 * Mapper to Entity
	 * @formatter:off
	 * @return
	 */
	public Country toEntity() {
		return Country.builder()
				.code(code)
				.labelFr(labelFr)
				.build();
	}

	public static CountryDTO fromEntity(final Country c) {
		return CountryDTO.builder()
				.code(c.getCode())
				.labelFr(c.getLabelFr())
				.build();
	}

}