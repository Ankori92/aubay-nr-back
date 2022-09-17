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

	private String labelEn;

	/**
	 * Mapper to Entity
	 * @formatter:off
	 * @return
	 */
	public Country toEntity() {
		return Country.builder()
				.code(code)
				.labelFr(labelFr)
				.labelEn(labelEn)
				.build();
	}

}