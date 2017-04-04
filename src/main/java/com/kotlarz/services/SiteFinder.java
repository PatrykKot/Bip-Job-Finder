package com.kotlarz.services;

import com.vaadin.ui.Grid;

public interface SiteFinder {
	public Grid<?> generateGrid() throws Exception;
}
