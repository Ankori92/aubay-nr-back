package com.aubay.formations.nr.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsWrapperDTO {

	private final List<StatisticsDTO> global;
	private final Map<Long, List<StatisticsDTO>> byPeriod;

	public StatisticsWrapperDTO(final List<StatisticsDTO> global, final List<StatisticsDTO> byPeriod) {
		this.global = global;
		final Map<Long, List<StatisticsDTO>> map = new HashMap<Long, List<StatisticsDTO>>();
		for (final StatisticsDTO stat : byPeriod) {
			final long key = stat.getDate().getTime();
			final List<StatisticsDTO> value = map.getOrDefault(key, new ArrayList<StatisticsDTO>());
			value.add(stat);
			map.put(key, value);
		}
		this.byPeriod = map;
	}

	public List<StatisticsDTO> getGlobal() {
		return global;
	}

	public Map<Long, List<StatisticsDTO>> getByPeriod() {
		return byPeriod;
	}
}
