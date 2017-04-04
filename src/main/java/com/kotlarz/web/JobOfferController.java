package com.kotlarz.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.kotlarz.domain.database.JobOfferEntity;
import com.kotlarz.services.JobOfferDao;

@Controller
public class JobOfferController {
	@Autowired
	JobOfferDao jobOfferDao;

	@ResponseBody
	@RequestMapping("offers")
	public List<JobOfferEntity> getJobOffers() {
		return Lists.newArrayList(jobOfferDao.findAll());
	}
}
