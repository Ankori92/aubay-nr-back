package com.aubay.formations.nr.controllers;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.aubay.formations.nr.entities.Usage;
import com.aubay.formations.nr.repositories.UsageRepository;

/**
 * Statistics controller
 *
 * @author jbureau@aubay.com
 */
@RestController
public class StatisticsController {

	@Autowired
	private UsageRepository usageRepository;

	/**
	 * Get usage statistics of application functionalities
	 *
	 * @return
	 */
	@GetMapping("/stats")
	public List<Usage> getUsageStatistics() {
		return usageRepository.findAll();
	}

	/**
	 * Clear all statistics about functionalities usage
	 *
	 * @return
	 */
	@Transactional
	@DeleteMapping("/stats")
	public void clearUsageStatistics(@RequestBody final List<String> uris) {
		for (final String uri : uris) {
			usageRepository.deleteByUri(uri);
		}
	}
}
