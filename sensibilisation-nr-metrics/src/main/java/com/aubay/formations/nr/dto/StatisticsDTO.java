package com.aubay.formations.nr.dto;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class StatisticsDTO {
	private final String uri;

	private final Date date;

	private final int duration;

	private final int queries;

	private final int weight;

	private final int calls;

	public StatisticsDTO(final String uri, final Date date, final int duration, final int queries, final int weight,
			final int calls) {
		this.uri = uri;
		this.date = date;
		this.duration = duration;
		this.queries = queries;
		this.weight = weight;
		this.calls = calls;
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

	public int getCalls() {
		return calls;
	}

	/**
	 * Build DTO from raw data
	 * @formatter:off
	 * @param data
	 * @param begin
	 * @param end
	 * @return
	 */
	public static List<StatisticsDTO> fromMultipleRawData(final List<Map<String, Object>> data) {
		final List<StatisticsDTO> result = new ArrayList<StatisticsDTO>();
		for(final Map<String, Object> d : data) {
			result.add(StatisticsDTO.fromRawData(d));
		}
		return result;
	}

	/**
	 * Build DTO from raw data
	 * @formatter:off
	 * @param data
	 * @param begin
	 * @param end
	 * @return
	 */
	public static StatisticsDTO fromRawData(final Map<String, Object> data) {
		return new StatisticsDTO(
				(String) data.get("uri"),
				(Date) data.get("date"),
				((Double) data.get("duration")).intValue(),
				((Double) data.get("queries")).intValue(),
				((Double) data.get("weight")).intValue(),
				((BigInteger) data.get("calls")).intValue());
	}
}
