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
import com.kotlarz.domain.UrbanistykaInfoOffer;
import com.kotlarz.finder.services.SiteFinder;
import com.kotlarz.finder.types.FinderTypes;
import com.kotlarz.translator.Translator;
import com.vaadin.server.Page;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.Grid.ItemClick;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.ItemClickListener;

@Service
public class UrbanistykaInfoFinder implements SiteFinder {

	private static String URL = "http://www.urbanistyka.info/forum/45";

	private Document getDocument(String url) throws IOException {
		return Jsoup.connect(url).get();
	}

	private List<UrbanistykaInfoOffer> find(Document document) {
		Elements artContent = document.select(".title");
		List<UrbanistykaInfoOffer> offerList = new LinkedList<>();

		for (Element element : artContent) {
			UrbanistykaInfoOffer offer = new UrbanistykaInfoOffer();
			element = element.child(0);
			offer.setName(element.text());
			offer.setLink("http://www.urbanistyka.info" + element.attr("href"));

			offerList.add(offer);
		}

		return offerList;
	}

	@Override
	public String getOrganizationName() {
		return FinderTypes.URBANISTYKA_INFO.toString();
	}

	@SuppressWarnings("serial")
	@Override
	public Component generateGrid() throws Exception {
		List<UrbanistykaInfoOffer> jobList = find(getDocument(URL));

		Grid<UrbanistykaInfoOffer> grid = new Grid<UrbanistykaInfoOffer>();
		grid.setSizeFull();
		grid.setItems(jobList);

		grid.addColumn(UrbanistykaInfoOffer::getName).setCaption(Translator.getMessage("name"));

		grid.addItemClickListener(new ItemClickListener<UrbanistykaInfoOffer>() {

			@Override
			public void itemClick(ItemClick<UrbanistykaInfoOffer> event) {
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
