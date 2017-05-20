package com.kotlarz.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.kotlarz.domain.AbstractJobOffer;
import com.kotlarz.domain.database.JobOfferEntity;
import com.kotlarz.finder.services.SiteFinder;
import com.kotlarz.mail.service.MailSenderService;

@Service
public class JobOfferService {

	private static Logger log = Logger.getLogger(JobOfferService.class);

	@Autowired
	JobOfferDao jobOfferDao;

	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	List<SiteFinder> jobFinders;

	@Autowired
	MailSenderService mailSender;

	@Scheduled(fixedRate = 1000 * 60 * 5)
	public void scheduledFinder() throws FileNotFoundException, IOException {
		Map<String, List<? extends AbstractJobOffer>> offersMap = getOffers();
		Map<String, List<AbstractJobOffer>> newOffers = findNewOffers(offersMap);

		log.info("Znaleziono " + newOffers.size());

		if (!newOffers.isEmpty()) {
			String filePath = "mail/mail.properties";
			Properties emailProps = mailSender.loadProperties(filePath);

			List<String> emailsToSend = mailSender.getEmailsToSend(filePath);
			for (String destinationMail : emailsToSend) {
				try {
					mailSender.sendOffers(newOffers, destinationMail, emailProps);
				} catch (Exception ex) {
					log.error("Problem z wysłaniem", ex);
					return;
				}
			}

			log.info("Wysłano");

			for (Entry<String, List<AbstractJobOffer>> entryOffer : newOffers.entrySet()) {
				List<JobOfferEntity> offerEntities = createOfferEntities(entryOffer.getValue());
				save(offerEntities);
			}

			log.info("Zapisano");
		}
	}

	@Transactional
	public void save(List<JobOfferEntity> jobOffers) {
		jobOfferDao.save(jobOffers);
	}

	private List<JobOfferEntity> createOfferEntities(List<AbstractJobOffer> abstractOffers) {
		List<JobOfferEntity> entities = new LinkedList<>();

		for (AbstractJobOffer offer : abstractOffers) {
			entities.add(JobOfferEntity.create(offer));
		}

		return entities;
	}

	private Map<String, List<AbstractJobOffer>> findNewOffers(Map<String, List<? extends AbstractJobOffer>> offersMap) {
		Map<String, List<AbstractJobOffer>> newOffersMap = new HashMap<>();

		for (Entry<String, List<? extends AbstractJobOffer>> entryOffer : offersMap.entrySet()) {
			List<? extends AbstractJobOffer> offers = entryOffer.getValue();
			List<AbstractJobOffer> newOffersList = new LinkedList<>();

			for (AbstractJobOffer offer : offers) {
				List<JobOfferEntity> found = jobOfferDao.findByNameAndOrganizationName(offer.getName(),
						offer.getOrganizationName());
				if (found.isEmpty()) {
					newOffersList.add(offer);
				}
			}

			if (!newOffersList.isEmpty()) {
				newOffersMap.put(entryOffer.getKey(), newOffersList);
			}
		}

		return newOffersMap;
	}

	private Map<String, List<? extends AbstractJobOffer>> getOffers() {
		Map<String, List<? extends AbstractJobOffer>> foundJobOffers = new HashMap<>();

		for (SiteFinder finder : jobFinders) {
			try {
				List<? extends AbstractJobOffer> offers = finder.getOffers();
				foundJobOffers.put(finder.getOrganizationName(), offers);
			} catch (Exception e) {
				log.error("Nie można pobrać ofert dla " + finder, e);
				continue;
			}
		}

		return foundJobOffers;
	}
}
