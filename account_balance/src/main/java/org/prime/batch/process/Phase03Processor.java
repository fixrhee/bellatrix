package org.prime.batch.process;

import java.util.List;
import org.apache.log4j.Logger;
import org.prime.batch.data.Accounts;
import org.prime.batch.data.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Phase03Processor {

	@Autowired
	private BatchRepository batchRepository;
	private Logger logger = Logger.getLogger(Phase03Processor.class);

	public List<Accounts> doProcess(Members members) {
		List<Accounts> accounts = batchRepository.loadAccountsByGroups(members);
		return accounts;
	}
}
