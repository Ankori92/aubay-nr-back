package com.aubay.formations.nr.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aubay.formations.nr.entities.CountryEntity;

/**
 * Country JPA repository
 *
 * @author jbureau@aubay.com
 */
@Repository
public interface CountryRepository extends JpaRepository<CountryEntity, String> {

}
