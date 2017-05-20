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
import com.kotlarz.domain.BipRokietnicaOffer;
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
public class BipRokietnicaFinder implements SiteFinder {

	private static String URL = "http://bip.rokietnica.pl/public/?id=47021";

	private Document getDocument(String url) throws IOException {
		return Jsoup.connect(url).get();
	}

	private List<BipRokietnicaOffer> find(Document document) {
		Elements artContent = document.select(".nazwa_pliku");
		List<BipRokietnicaOffer> offerList = new LinkedList<>();

		for (int i = 2; i < artContent.size(); i++) {
			Element element = artContent.get(i);

			BipRokietnicaOffer offer = new BipRokietnicaOffer();
			offer.setName(element.text());
			offer.setLink("http://bip.rokietnica.pl/public/" + element.attr("href"));

			offerList.add(offer);
		}

		return offerList;
	}

	@Override
	public String getOrganizationName() {
		return FinderTypes.ROKIETNICA.toString();
	}

	@SuppressWarnings("serial")
	@Override
	public Component generateGrid() throws Exception {
		List<BipRokietnicaOffer> jobList = find(getDocument(URL));

		Grid<BipRokietnicaOffer> grid = new Grid<BipRokietnicaOffer>();
		grid.setSizeFull();
		grid.setItems(jobList);

		grid.addColumn(BipRokietnicaOffer::getName).setCaption(Translator.getMessage("name"));

		grid.addItemClickListener(new ItemClickListener<BipRokietnicaOffer>() {

			@Override
			public void itemClick(ItemClick<BipRokietnicaOffer> event) {
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
		return find(getDocument(URL));
	}

}
