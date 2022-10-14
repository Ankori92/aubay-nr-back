package com.aubay.formations.nr.repositories;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.aubay.formations.nr.entities.Usage;

/**
 * Usage JPA repository
 *
 * @author jbureau@aubay.com
 */
@Repository
public interface UsageRepository extends JpaRepository<Usage, String> {

	@Query(value = "SELECT uri, AVG(queries) AS queries, AVG(duration) AS duration, AVG(weight) AS weight, count(*) AS calls, CAST(DATEADD('MINUTE', (:interval)*1,  CAST(:begin AS VARCHAR)) AS DATE) AS date FROM usages WHERE date >= DATEADD('MINUTE', (:interval)*0, CAST(:begin AS VARCHAR)) AND date < DATEADD('MINUTE', (:interval)*1, CAST(:begin AS VARCHAR)) GROUP BY uri"
			+ " UNION ALL SELECT uri, AVG(queries) AS queries, AVG(duration) AS duration, AVG(weight) AS weight, count(*) AS calls, CAST(DATEADD('MINUTE', (:interval)*2,  CAST(:begin AS VARCHAR)) AS DATE) AS date FROM usages WHERE date >= DATEADD('MINUTE', (:interval)*1, CAST(:begin AS VARCHAR)) AND date < DATEADD('MINUTE', (:interval)*2,  CAST(:begin AS VARCHAR)) GROUP BY uri"
			+ " UNION ALL SELECT uri, AVG(queries) AS queries, AVG(duration) AS duration, AVG(weight) AS weight, count(*) AS calls, CAST(DATEADD('MINUTE', (:interval)*3,  CAST(:begin AS VARCHAR)) AS DATE) AS date FROM usages WHERE date >= DATEADD('MINUTE', (:interval)*2, CAST(:begin AS VARCHAR)) AND date < DATEADD('MINUTE', (:interval)*3,  CAST(:begin AS VARCHAR)) GROUP BY uri"
			+ " UNION ALL SELECT uri, AVG(queries) AS queries, AVG(duration) AS duration, AVG(weight) AS weight, count(*) AS calls, CAST(DATEADD('MINUTE', (:interval)*4,  CAST(:begin AS VARCHAR)) AS DATE) AS date FROM usages WHERE date >= DATEADD('MINUTE', (:interval)*3, CAST(:begin AS VARCHAR)) AND date < DATEADD('MINUTE', (:interval)*4,  CAST(:begin AS VARCHAR)) GROUP BY uri"
			+ " UNION ALL SELECT uri, AVG(queries) AS queries, AVG(duration) AS duration, AVG(weight) AS weight, count(*) AS calls, CAST(DATEADD('MINUTE', (:interval)*5,  CAST(:begin AS VARCHAR)) AS DATE) AS date FROM usages WHERE date >= DATEADD('MINUTE', (:interval)*4, CAST(:begin AS VARCHAR)) AND date < DATEADD('MINUTE', (:interval)*5,  CAST(:begin AS VARCHAR)) GROUP BY uri"
			+ " UNION ALL SELECT uri, AVG(queries) AS queries, AVG(duration) AS duration, AVG(weight) AS weight, count(*) AS calls, CAST(DATEADD('MINUTE', (:interval)*6,  CAST(:begin AS VARCHAR)) AS DATE) AS date FROM usages WHERE date >= DATEADD('MINUTE', (:interval)*5, CAST(:begin AS VARCHAR)) AND date < DATEADD('MINUTE', (:interval)*6,  CAST(:begin AS VARCHAR)) GROUP BY uri"
			+ " UNION ALL SELECT uri, AVG(queries) AS queries, AVG(duration) AS duration, AVG(weight) AS weight, count(*) AS calls, CAST(DATEADD('MINUTE', (:interval)*7,  CAST(:begin AS VARCHAR)) AS DATE) AS date FROM usages WHERE date >= DATEADD('MINUTE', (:interval)*6, CAST(:begin AS VARCHAR)) AND date < DATEADD('MINUTE', (:interval)*7,  CAST(:begin AS VARCHAR)) GROUP BY uri"
			+ " UNION ALL SELECT uri, AVG(queries) AS queries, AVG(duration) AS duration, AVG(weight) AS weight, count(*) AS calls, CAST(DATEADD('MINUTE', (:interval)*8,  CAST(:begin AS VARCHAR)) AS DATE) AS date FROM usages WHERE date >= DATEADD('MINUTE', (:interval)*7, CAST(:begin AS VARCHAR)) AND date < DATEADD('MINUTE', (:interval)*8,  CAST(:begin AS VARCHAR)) GROUP BY uri"
			+ " UNION ALL SELECT uri, AVG(queries) AS queries, AVG(duration) AS duration, AVG(weight) AS weight, count(*) AS calls, CAST(DATEADD('MINUTE', (:interval)*9,  CAST(:begin AS VARCHAR)) AS DATE) AS date FROM usages WHERE date >= DATEADD('MINUTE', (:interval)*8, CAST(:begin AS VARCHAR)) AND date < DATEADD('MINUTE', (:interval)*9,  CAST(:begin AS VARCHAR)) GROUP BY uri"
			+ " UNION ALL SELECT uri, AVG(queries) AS queries, AVG(duration) AS duration, AVG(weight) AS weight, count(*) AS calls, CAST(DATEADD('MINUTE', (:interval)*10, CAST(:begin AS VARCHAR)) AS DATE) AS date FROM usages WHERE date >= DATEADD('MINUTE', (:interval)*9, CAST(:begin AS VARCHAR)) AND date < DATEADD('MINUTE', (:interval)*10, CAST(:begin AS VARCHAR)) GROUP BY uri", nativeQuery = true)
	List<Map<String, Object>> getStats(Date begin, double interval);

	@Query("SELECT MIN(date) FROM Usage")
	Date getOldest();

	@Query(value = "SELECT uri, AVG(queries) AS queries, AVG(duration) AS duration, AVG(weight) AS weight, count(*) AS calls FROM usages GROUP BY uri", nativeQuery = true)
	List<Map<String, Object>> getStats();
}
