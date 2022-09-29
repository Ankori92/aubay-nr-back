package com.aubay.formations.nr.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aubay.formations.nr.entities.Usage;

/**
 * Usage JPA repository
 *
 * @author jbureau@aubay.com
 */
@Repository
public interface UsageRepository extends JpaRepository<Usage, String> {

	void deleteByUri(String uri);

}
