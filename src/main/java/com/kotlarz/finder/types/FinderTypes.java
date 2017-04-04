package com.kotlarz.finder.types;

public enum FinderTypes {
	POZNAN("poznan"), MUROWANA_GOSLINA("murowanaGoslina"), SUCHY_LAS("suchyLas");

	private String organizationName;

	private FinderTypes(String organizationName) {
		this.organizationName = organizationName;
	}

	public String toString() {
		return organizationName;
	}

}
