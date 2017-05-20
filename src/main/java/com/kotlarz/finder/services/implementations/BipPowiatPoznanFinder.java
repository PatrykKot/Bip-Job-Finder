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
import com.kotlarz.domain.BipPowiatPoznanOffer;
import com.kotlarz.finder.services.SiteFinder;
import com.kotlarz.finder.types.FinderTypes;
import com.kotlarz.translator.Translator;
import com.vaadin.server.Page;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Grid.ItemClick;
import com.vaadin.ui.components.grid.ItemClickListener;

@Service
public class BipPowiatPoznanFinder implements SiteFinder {

	private static String URL = "http://www.bip.powiat.poznan.pl/1652,nabor-na-stanowiska-urzednicze";

	private Document getDocument(String url) throws IOException {
		return Jsoup.connect(url).get();
	}

	@Override
	public String getOrganizationName() {
		return FinderTypes.POWIAT_POZNAN.toString();
	}

	@SuppressWarnings("serial")
	@Override
	public Component generateGrid() throws Exception {
		List<BipPowiatPoznanOffer> jobList = find(URL);

		Grid<BipPowiatPoznanOffer> grid = new Grid<BipPowiatPoznanOffer>();
		grid.setSizeFull();
		grid.setItems(jobList);

		grid.addColumn(BipPowiatPoznanOffer::getName).setCaption(Translator.getMessage("name"));

		grid.addItemClickListener(new ItemClickListener<BipPowiatPoznanOffer>() {

			@Override
			public void itemClick(ItemClick<BipPowiatPoznanOffer> event) {
				Page.getCurrent().setLocation(event.getItem().getLink());
			}
		});

		Layout layout = new VerticalLayout();
		Label label = new Label(Translator.getMessage(getOrganizationName()));

		layout.addComponents(label, grid);
		return layout;
	}

	@Override
	public List<? extends AbstractJobOffer> getOffers() throws Exception {
		return find(URL);
	}

	private List<BipPowiatPoznanOffer> find(String url) throws IOException {
		Document document = getDocument(url);
		//Elements hrefs = document.select(".dataTable tbody td:first-child a");
		Elements hrefs = document.select("tbody td:first-child a");
		List<BipPowiatPoznanOffer> offers = new LinkedList<>();

		for (Element element : hrefs) {
			Elements strongsElements = element.select("strong");
			BipPowiatPoznanOffer offer = new BipPowiatPoznanOffer();
			offer.setLink(element.attr("href"));
			offer.setName(strongsElements.get(0).text());
			offer.setOrganizationName(strongsElements.get(1).text());

			offers.add(offer);
		}

		return offers;
	}

}
