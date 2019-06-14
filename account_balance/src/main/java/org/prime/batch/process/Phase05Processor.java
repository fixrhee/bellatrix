package org.prime.batch.process;

import org.apache.log4j.Logger;
import org.prime.batch.data.Accounts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Phase05Processor {

	@Autowired
	private BatchRepository batchRepository;
	private Logger logger = Logger.getLogger(Phase05Processor.class);

	public Accounts doProcess(Accounts account) {
		if (account.getStatus().equalsIgnoreCase("PROCESSED")) {
			batchRepository.insertBalance(account);
		}
		return null;
	}
}
