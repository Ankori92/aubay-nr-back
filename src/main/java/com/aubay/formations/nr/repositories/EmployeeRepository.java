package com.aubay.formations.nr.repositories;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.aubay.formations.nr.entities.Employee;

/**
 * Employee JPA repository
 *
 * @author jbureau@aubay.com
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

	List<Employee> findByManagerNullAndResignedFalse();

	@Query(value = "WITH emp(id, firstname, lastname, salary, entry_date, resigned, country_id, manager_id) AS (SELECT id, firstname, lastname, salary, entry_date, resigned, country_id, manager_id FROM employees WHERE id = :managerId UNION ALL SELECT child.id, child.firstname, child.lastname, child.salary, child.entry_date, child.resigned, child.country_id, child.manager_id FROM employees child JOIN emp parent ON child.manager_id = parent.id) SELECT id, firstname, lastname, salary, entry_date, resigned, country_id, manager_id FROM emp", nativeQuery = true)
	List<Map<String, Object>> findEmployeesInTree(long managerId);
}
