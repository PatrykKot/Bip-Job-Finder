package com.kotlarz.finder.services;

import java.util.List;

import com.kotlarz.domain.AbstractJobOffer;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;

public interface SiteFinder {
	public String getOrganizationName();
	
	public Component generateGrid() throws Exception;
	
	public List<? extends AbstractJobOffer> getOffers() throws Exception;
}
