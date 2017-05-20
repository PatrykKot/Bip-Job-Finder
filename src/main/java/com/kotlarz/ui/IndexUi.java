package com.kotlarz.ui;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.kotlarz.finder.services.SiteFinder;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@SpringUI
public class IndexUi extends UI {

	private static Logger log = Logger.getLogger(IndexUi.class);

	@Autowired
	ApplicationContext context;

	public List<SiteFinder> getFinders() {
		Map<String, SiteFinder> beans = context.getBeansOfType(SiteFinder.class);
		List<SiteFinder> list = new LinkedList<SiteFinder>();

		for (Entry<String, SiteFinder> entry : beans.entrySet()) {
			list.add(entry.getValue());
		}

		return list;
	}

	@Override
	protected void init(VaadinRequest vaadinRequest) {
		Panel mainPanel = new Panel();
		VerticalLayout vertLayout = new VerticalLayout();
		vertLayout.setSizeFull();

		List<SiteFinder> finders = getFinders();

		for (SiteFinder finder : finders) {
			try {
				Component grid = finder.generateGrid();
				if (grid == null)
					continue;

				vertLayout.addComponent(grid);
			} catch (Exception e) {
				log.error("Nie można pobrać grida dla " + finder, e);
			}
		}

		mainPanel.setContent(vertLayout);
		setContent(mainPanel);
	}
}