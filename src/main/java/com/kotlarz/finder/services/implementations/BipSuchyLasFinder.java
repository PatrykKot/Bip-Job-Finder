package com.kotlarz.finder.services.implementations;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.kotlarz.domain.AbstractJobOffer;
import com.kotlarz.domain.BipSuchyLasOffer;
import com.kotlarz.finder.services.SiteFinder;
import com.kotlarz.finder.types.FinderTypes;
import com.kotlarz.translator.Translator;
import com.vaadin.server.Page;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.ItemClick;
import com.vaadin.ui.components.grid.ItemClickListener;

@Service
public class BipSuchyLasFinder implements SiteFinder {

	private static String URL = "http://bip.suchylas.pl/ogloszenia/32/oferty-pracy/";

	private Document getDocument(String url) throws IOException {
		return Jsoup.connect(url).get();
	}

	private List<BipSuchyLasOffer> find(Document document) {
		Elements artContent = document.getElementById("article-content").children();
		List<BipSuchyLasOffer> offerList = new LinkedList<>();

		for (Element element : artContent) {
			BipSuchyLasOffer offer = new BipSuchyLasOffer();

			offer.setName(element.select("h1").get(0).text());
			offer.setLink("http://bip.suchylas.pl" + element.select("a").get(0).attr("href"));

			offerList.add(offer);
		}

		return offerList;
	}

	@SuppressWarnings("serial")
	@Override
	public Grid<?> generateGrid() throws Exception {
		List<BipSuchyLasOffer> jobList = find(getDocument(URL));

		Grid<BipSuchyLasOffer> grid = new Grid<BipSuchyLasOffer>();
		grid.setSizeFull();
		grid.setItems(jobList);

		grid.addColumn(BipSuchyLasOffer::getName).setCaption(Translator.getMessage("name"));

		grid.addItemClickListener(new ItemClickListener<BipSuchyLasOffer>() {

			@Override
			public void itemClick(ItemClick<BipSuchyLasOffer> event) {
				Page.getCurrent().setLocation(event.getItem().getLink());
			}
		});

		return grid;
	}

	@Override
	public List<? extends AbstractJobOffer> getOffers() throws Exception {
		return find(getDocument(URL));
	}

	@Override
	public String getOrganizationName() {
		return FinderTypes.SUCHY_LAS.toString();
	}

}
