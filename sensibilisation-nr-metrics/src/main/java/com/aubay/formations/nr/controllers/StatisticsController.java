package com.aubay.formations.nr.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.aubay.formations.nr.dto.StatisticsDTO;
import com.aubay.formations.nr.dto.StatisticsWrapperDTO;
import com.aubay.formations.nr.repositories.UsageRepository;

/**
 * Statistics controller
 *
 * @author jbureau@aubay.com
 */
@RestController
public class StatisticsController {

	private static final long nbSteps = 10;

	@Autowired
	private UsageRepository usageRepository;

	/**
	 * Get usage statistics of application functionalities
	 *
	 * @return
	 */
	@GetMapping("/stats")
	public StatisticsWrapperDTO getStatistics() {
		final Date oldest = usageRepository.getOldest();
		if (oldest == null) { // No statistics
			return new StatisticsWrapperDTO(new ArrayList<StatisticsDTO>(), new ArrayList<StatisticsDTO>());
		}
		final double stepDuration = Double.valueOf(new Date().getTime() - oldest.getTime()) / nbSteps;
		final List<Map<String, Object>> statsByPeriod = usageRepository.getStats(oldest, stepDuration / 1000 / 60);
		final List<Map<String, Object>> statsGlobal = usageRepository.getStats();
		final List<StatisticsDTO> byPeriod = StatisticsDTO.fromMultipleRawData(statsByPeriod);
		final List<StatisticsDTO> global = StatisticsDTO.fromMultipleRawData(statsGlobal);
		return new StatisticsWrapperDTO(global, byPeriod);
	}

	/**
	 * Clear all statistics about functionalities usage
	 *
	 * @return
	 */
	@DeleteMapping("/stats")
	public void clearUsageStatistics() {
		usageRepository.deleteAllInBatch();
	}
}
