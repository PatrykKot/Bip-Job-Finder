package com.kotlarz.domain;

public abstract class AbstractJobOffer {
	protected String name;

	protected String organizationName;

	protected String link;

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	@Override
	public String toString() {
		return "AbstractJobOffer [name=" + name + ", organizationName=" + organizationName + "]";
	}

}
