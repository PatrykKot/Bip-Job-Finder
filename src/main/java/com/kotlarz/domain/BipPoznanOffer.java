package com.kotlarz.domain;

import org.json.JSONObject;

import com.kotlarz.finder.types.FinderTypes;
import com.kotlarz.services.types.SiteTypes;

public class BipPoznanOffer extends AbstractJobOffer {

	public BipPoznanOffer() {
		this.organizationName = FinderTypes.POZNAN.toString();
	}

	private String organization;

	private String publicDate;

	private String deadline;

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getPublicDate() {
		return publicDate;
	}

	public void setPublicDate(String publicDate) {
		this.publicDate = publicDate;
	}

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	public static BipPoznanOffer create(JSONObject object, SiteTypes type) throws Exception {
		switch (type) {
		case BIP_POZNAN: {
			return createFromBipPoznan(object);
		}
		default: {
			throw new IllegalArgumentException("Type " + type);
		}
		}
	}

	private static BipPoznanOffer createFromBipPoznan(JSONObject object) throws Exception {
		BipPoznanOffer offer = new BipPoznanOffer();
		offer.setLink(object.getString("link").replace("&api=json", ""));
		offer.setOrganization(object.getString("nazwa_organizacja"));
		offer.setName(object.getString("stanowisko"));
		offer.setPublicDate(object.getString("data_publikacji"));
		offer.setDeadline(object.getString("termin_skladania_ofert"));
		return offer;
	}

}
