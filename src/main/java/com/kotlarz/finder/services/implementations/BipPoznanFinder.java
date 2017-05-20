package com.kotlarz.finder.services.implementations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.kotlarz.domain.AbstractJobOffer;
import com.kotlarz.domain.BipPoznanOffer;
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
public class BipPoznanFinder implements SiteFinder {

	private static String URL = "http://bip.poznan.pl/api-json/bip/oferty-pracy/";

	private JSONObject getJson(String url) throws ClientProtocolException, IOException, JSONException {
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(url);
		HttpResponse response = client.execute(get);

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		return new JSONObject(result.toString());
	}

	private List<BipPoznanOffer> find() throws Exception {
		JSONObject jsonData = getJson(URL);
		JSONObject jobsData = (JSONObject) jsonData.getJSONObject("bip.poznan.pl").getJSONArray("data").get(0);
		jobsData = jobsData.getJSONObject("oferty_pracy");
		jobsData = (JSONObject) jobsData.getJSONArray("items").get(0);
		JSONArray jobsArray = (JSONArray) jobsData.getJSONArray("oferta");

		List<BipPoznanOffer> jobList = new LinkedList<>();
		for (int i = 0; i < jobsArray.length(); i++) {
			JSONObject obj = (JSONObject) jobsArray.get(i);
			jobList.add(BipPoznanOffer.create(obj));
		}

		return jobList;
	}

	@SuppressWarnings("serial")
	@Override
	public Component generateGrid() throws Exception {
		List<BipPoznanOffer> jobList = find();

		Grid<BipPoznanOffer> grid = new Grid<BipPoznanOffer>();
		grid.setSizeFull();
		grid.setItems(jobList);

		grid.addColumn(BipPoznanOffer::getName).setCaption(Translator.getMessage("name")).setWidth(400);
		grid.addColumn(BipPoznanOffer::getOrganization).setCaption(Translator.getMessage("organization")).setWidth(300);
		grid.addColumn(BipPoznanOffer::getPublicDate).setCaption(Translator.getMessage("publicDate"));
		grid.addColumn(BipPoznanOffer::getDeadline).setCaption(Translator.getMessage("deadline"));

		grid.addItemClickListener(new ItemClickListener<BipPoznanOffer>() {

			@Override
			public void itemClick(ItemClick<BipPoznanOffer> event) {
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
		return find();
	}

	@Override
	public String getOrganizationName() {
		return FinderTypes.POZNAN.toString();
	}

}
