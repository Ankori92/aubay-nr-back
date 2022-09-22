package com.aubay.formations.nr.dto;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.aubay.formations.nr.entities.Usage;

public class StatisticsDTO {
	private final String uri;

	private final Date date;

	private int duration;

	private int queries;

	private int weight;

	private int calls;

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
		return data.stream().map(StatisticsDTO::fromRawData).toList();
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

	public void incrementCalls() {
		calls++;
	}

	public void incrementValues(final Usage usage) {
		calls++;
		duration += usage.getDuration();
		queries += usage.getQueries();
		weight += usage.getWeight();
	}

	public void computeAverage() {
		duration /= calls;
		queries /= calls;
		weight /= calls;
	}}
