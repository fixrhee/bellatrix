package org.prime.batch.process;

import java.math.BigDecimal;
import org.apache.log4j.Logger;
import org.prime.batch.data.Accounts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Phase04Processor {

	@Autowired
	private BatchRepository batchRepository;
	private Logger logger = Logger.getLogger(Phase04Processor.class);

	public Accounts doProcess(Accounts account) {
		Integer lastTransferID = batchRepository.getLastTransactionID(account.getMember().getId(), account.getId());
		if (lastTransferID != 0) {
			BigDecimal balance = batchRepository.loadBalanceInquiry(account.getMember().getUsername(), account.getId(),
					lastTransferID);
			if (balance != null) {
				account.setBalance(balance);
				account.setLastTransferID(lastTransferID);
				account.setStatus("PROCESSED");
			} else {
				account.setStatus("FAILED");
			}
		} else {
			account.setStatus("NO_TRANSACTION");
		}
		return account;
	}
}
