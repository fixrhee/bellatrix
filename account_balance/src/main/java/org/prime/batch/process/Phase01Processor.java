package org.prime.batch.process;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Phase01Processor {

	@Autowired
	private BatchRepository phase01Repository;
	private Logger logger = Logger.getLogger(Phase01Processor.class);

	public List<String> doProcess(String command) {
		List<String> limitPages = new LinkedList<String>();
		Integer membersCount = phase01Repository.countMembers();
		//membersCount = 100;
		Integer limit = 50;
		logger.info("[MembersCount = " + membersCount + " / Limit = " + limit + "]");
		double pages = (double) membersCount / limit;
		double roundedPages = Math.ceil(pages);
		logger.info("[Pages = " + pages + " , Rounded = " + roundedPages + "]");

		Integer initialLimit = 0;
		for (int i = 0; i < (int) roundedPages; i++) {
			limitPages.add(initialLimit + "," + (initialLimit + limit));
			initialLimit = initialLimit + limit;
		}
		
		return limitPages;
	}
}
