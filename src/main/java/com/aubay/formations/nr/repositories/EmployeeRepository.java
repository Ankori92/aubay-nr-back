package com.aubay.formations.nr.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
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
}
