package com.kotlarz.domain.database;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.kotlarz.domain.AbstractJobOffer;

@Entity
@Table(name = "jobOffers")
public class JobOfferEntity {
	@Id
	@GeneratedValue
	private Long id;

	private String name;

	private String organizationName;

	private String link;

	private Boolean sent;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public Boolean getSent() {
		return sent;
	}

	public void setSent(Boolean sent) {
		this.sent = sent;
	}

	public static JobOfferEntity create(AbstractJobOffer offer) {
		JobOfferEntity entity = new JobOfferEntity();

		entity.setName(offer.getName());
		entity.setOrganizationName(offer.getOrganizationName());
		entity.setLink(offer.getLink());
		entity.setSent(false);

		return entity;
	}

}
