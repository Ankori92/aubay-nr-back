package com.aubay.formations.nr.dto;

import com.aubay.formations.nr.entities.Usage;

public class StatisticsDTO {
	private final String uri;

	private int duration;

	private int queries;

	private int weight;

	private int calls;

	public StatisticsDTO(final String uri, final int duration, final int queries, final int weight, final int calls) {
		this.uri = uri;
		this.duration = duration;
		this.queries = queries;
		this.weight = weight;
		this.calls = calls;
	}

	public String getUri() {
		return uri;
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
	}
}
