package com.kotlarz.domain;

import com.kotlarz.finder.types.FinderTypes;

public class BipPowiatPoznanOffer extends AbstractJobOffer {
	@Override
	public String getOrganizationName() {
		return FinderTypes.POWIAT_POZNAN.toString();
	}
}
