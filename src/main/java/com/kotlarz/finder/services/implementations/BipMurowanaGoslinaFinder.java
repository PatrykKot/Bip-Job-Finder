package com.kotlarz.finder.services.implementations;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.kotlarz.domain.AbstractJobOffer;
import com.kotlarz.domain.BipMurowanaGoslinaOffer;
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
public class BipMurowanaGoslinaFinder implements SiteFinder {
	private static String URL = "http://bip.murowana-goslina.pl/wiadomosci/9470/xml";

	private String getXml(String url) throws ClientProtocolException, IOException {
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(url);
		HttpResponse response = client.execute(get);

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		return result.toString();
	}

	public List<BipMurowanaGoslinaOffer> generateOffers(String xmlFile)
			throws ParserConfigurationException, SAXException, IOException {
		List<BipMurowanaGoslinaOffer> jobList = new LinkedList<>();
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputStream stream = new ByteArrayInputStream(xmlFile.getBytes(StandardCharsets.UTF_8));

		Document document = builder.parse(stream);
		NodeList wiadElements = document.getElementsByTagName("wiadomosc");

		for (int i = 1; i < wiadElements.getLength(); i++) {
			BipMurowanaGoslinaOffer offer = new BipMurowanaGoslinaOffer();

			Element element = (Element) wiadElements.item(i);
			offer.setName(element.getElementsByTagName("title").item(0).getTextContent());
			offer.setLink(element.getElementsByTagName("url").item(0).getTextContent());

			jobList.add(offer);
		}

		return jobList;
	}

	@SuppressWarnings("serial")
	@Override
	public Component generateGrid() throws Exception {
		List<BipMurowanaGoslinaOffer> jobList = generateOffers(getXml(URL));

		Grid<BipMurowanaGoslinaOffer> grid = new Grid<BipMurowanaGoslinaOffer>();
		grid.setSizeFull();
		grid.setItems(jobList);

		grid.addColumn(BipMurowanaGoslinaOffer::getName).setCaption(Translator.getMessage("name"));

		grid.addItemClickListener(new ItemClickListener<BipMurowanaGoslinaOffer>() {

			@Override
			public void itemClick(ItemClick<BipMurowanaGoslinaOffer> event) {
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
		return generateOffers(getXml(URL));
	}

	@Override
	public String getOrganizationName() {
		return FinderTypes.MUROWANA_GOSLINA.toString();
	}

}
