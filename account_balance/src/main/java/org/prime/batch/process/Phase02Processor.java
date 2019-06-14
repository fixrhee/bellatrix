package org.prime.batch.process;

import java.util.List;
import org.apache.log4j.Logger;
import org.prime.batch.data.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Phase02Processor {

	@Autowired
	private BatchRepository batchRepository;
	private Logger logger = Logger.getLogger(Phase02Processor.class);

	public List<Members> doProcess(String pages) {
		Integer offset = Integer.valueOf(pages.split(",")[0]);
		Integer limit = Integer.valueOf(pages.split(",")[1]);
		List<Members> members = batchRepository.findMembers(offset, limit);
		return members;
	}
}
