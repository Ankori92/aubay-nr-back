package com.aubay.formations.nr.rest;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;

import com.aubay.formations.nr.mappers.EmployeeMapper;
import com.aubay.formations.nr.rest.model.Country;
import com.aubay.formations.nr.rest.model.Employee;
import com.aubay.formations.nr.services.EmployeeService;

@Service
public class EmployeesApiDelegateImpl implements EmployeesApiDelegate, CountriesApiDelegate, CacheApiDelegate {

	@Autowired
	private EmployeeService employeeService;

	@Override
	public Optional<NativeWebRequest> getRequest() {
		return Optional.empty();
	}

	@Override
	@CacheEvict(value = "employees", allEntries = true)
	public ResponseEntity<Void> createEmployees(final Employee employee) {
		employeeService.saveEmployee(EmployeeMapper.toEntity(employee));
		return ResponseEntity.ok().build();
	}

	@Override
	@Cacheable("employees")
	public ResponseEntity<Employee> getEmployee(final Long employeeId, final Boolean onlyDirectTeam) {
		return ResponseEntity.ok().body(EmployeeMapper.toDto(employeeService.findEmployee(employeeId, onlyDirectTeam)));
	}

	@Override
	@Cacheable("topemployees")
	public ResponseEntity<List<Employee>> getTopManagers() {
		return ResponseEntity.ok().body(EmployeeMapper.toDto(employeeService.getTopManagers()));
	}

	// TODO: separate delegate and tag ? (or repath employees/countries)
	@Override
	@Cacheable("countries")
	public ResponseEntity<List<Country>> getCountries() {
		return ResponseEntity.ok().body(EmployeeMapper.toCountriesDto(employeeService.getCountries()));
	}

	/**
	 * Clear Spring caches
	 *
	 * @return
	 */
	@Override
	@CacheEvict(value = { "countries", "topemployees", "employees" }, allEntries = true)
	public ResponseEntity<Void> clearCache() {
		return ResponseEntity.ok().build();
	}
}
