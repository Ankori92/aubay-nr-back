package com.aubay.formations.nr.repositories;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.aubay.formations.nr.entities.EmployeeEntity;

/**
 * Employee JPA repository
 *
 * @author jbureau@aubay.com
 */
@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {

	List<EmployeeEntity> findByManagerNullAndResignedFalse();

	List<EmployeeEntity> findByManagerAndResignedFalse(EmployeeEntity manager);

	@Query(value = "SELECT m.id, m.firstname, m.lastname, m.salary, m.entry_date, m.resigned, m.country_id, m.manager_id, count(e.*) AS team_size FROM  employees m RIGHT JOIN employees e ON e.manager_id = m.id WHERE e.manager_id IN (:managersId) AND e.resigned = false GROUP BY m.id UNION ALL SELECT e.id, e.firstname, e.lastname, e.salary, e.entry_date, e.resigned, e.country_id, e.manager_id, 0 AS team_size FROM  employees e WHERE e.manager_id IN (:managersId) AND e.resigned = false", nativeQuery = true)
	List<Map<String, Object>> findDirectTeamOf(List<Long> managersId);

	@Modifying
	@Query("delete from EmployeeEntity b where b <> :employee")
	void deleteAllExcept(EmployeeEntity employee);

	@Query(value = "WITH emp(id, firstname, lastname, salary, entry_date, resigned, country_id, manager_id) AS (SELECT id, firstname, lastname, salary, entry_date, resigned, country_id, manager_id FROM employees WHERE id IN (:managersId) UNION ALL SELECT child.id, child.firstname, child.lastname, child.salary, child.entry_date, child.resigned, child.country_id, child.manager_id FROM employees child JOIN emp parent ON child.manager_id = parent.id WHERE child.resigned = false) SELECT id, firstname, lastname, salary, entry_date, resigned, country_id, manager_id FROM emp", nativeQuery = true)
	List<Map<String, Object>> findEmployeesInTree(List<Long> managersId);

	@Query(value = "WITH emp(id, manager_id) AS (SELECT id, manager_id FROM employees WHERE id IN (:managersId)	UNION ALL SELECT child.id, child.manager_id FROM employees child JOIN emp parent ON child.manager_id = parent.id WHERE child.resigned = false) SELECT id, manager_id FROM emp", nativeQuery = true)
	List<Map<String, Object>> findEmployeesIdBelow(List<Long> managersId);
}
