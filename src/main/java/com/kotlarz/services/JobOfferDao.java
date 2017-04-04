package com.kotlarz.services;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.kotlarz.domain.database.JobOfferEntity;

public interface JobOfferDao extends CrudRepository<JobOfferEntity, Long> {
	public List<JobOfferEntity> findByNameAndOrganizationName(String name, String organizationName);
}
