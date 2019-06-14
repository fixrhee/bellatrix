package org.bellatrix.process;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.bellatrix.data.AccountBalance;
import org.bellatrix.data.Accounts;
import org.bellatrix.data.BrokeringResult;
import org.bellatrix.data.Brokers;
import org.bellatrix.data.Fees;
import org.bellatrix.data.Members;
import org.bellatrix.data.Status;
import org.bellatrix.data.TransactionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

@Component
public class BrokerProcessor {

	@Autowired
	private BaseRepository baseRepository;
	@Autowired
	private HazelcastInstance instance;
	@Autowired
	private AccountValidation accountValidation;
	private Logger logger = Logger.getLogger(BrokerProcessor.class);

	public List<BrokeringResult> procesBrokering(List<Fees> fees, String transactionNumber)
			throws TransactionException {

		List<BrokeringResult> lbr = new ArrayList<BrokeringResult>();
		IMap<String, BigDecimal> mapLock = instance.getMap("AccountLock");

		for (int i = 0; i < fees.size(); i++) {

			BrokeringResult br = new BrokeringResult();
			BigDecimal feeAmount = fees.get(i).getFeeAmount();
			List<BigDecimal> brokeringAmount = new LinkedList<BigDecimal>();
			boolean processBrokering = false;
			AccountBalance fromLastBalance = new AccountBalance();
			AccountBalance toLastBalance = new AccountBalance();
			List<Brokers> lb = baseRepository.getTransferTypesRepository().getBrokersFromFee(fees.get(i).getId());

			for (int j = 0; j < lb.size(); j++) {

				/*
				 * Brokering calculation
				 */

				BigDecimal fixedBrokering = BigDecimal.ZERO;
				BigDecimal percentageBrokering = BigDecimal.ZERO;
				BigDecimal result = BigDecimal.ZERO;

				if (lb.get(j).isDeductAllFee()) {
					result = feeAmount;
					lb.get(j).setFeeAmount(feeAmount);
					logger.info("[Brokering From All Fee : " + result + "]");
				} else {
					if (lb.get(j).getFixedAmount().compareTo(BigDecimal.ZERO) == 1) {
						fixedBrokering = lb.get(j).getFixedAmount();
					}

					if (lb.get(j).getPercentageValue().compareTo(BigDecimal.ZERO) == 1) {
						percentageBrokering = feeAmount.multiply(lb.get(j).getPercentageValue())
								.divide(new BigDecimal(100));
					}

					if (fixedBrokering.compareTo(percentageBrokering) == 0
							|| fixedBrokering.compareTo(percentageBrokering) == 1) {
						brokeringAmount.add(fixedBrokering);
						lb.get(j).setFeeAmount(fixedBrokering);
						logger.info("[Brokering by Amount : " + fixedBrokering + "]");
					} else {
						brokeringAmount.add(percentageBrokering);
						lb.get(j).setFeeAmount(percentageBrokering);
						logger.info("[Brokering by Percentage : " + percentageBrokering + "]");
					}

					result = brokeringAmount.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
				}

				if (feeAmount.compareTo(result) == 0 || feeAmount.compareTo(result) == 1) {

					/*
					 * VALIDATE BROKERING MEMBER & ACCOUNT : If value = 0 -> fill with Source
					 * memberID/accountID, If value = -1 -> fill with destination
					 */

					if (lb.get(j).getFromMemberID() == 0 || lb.get(j).getFromAccountID() == 0) {
						lb.get(j).setFromMemberID(fees.get(i).getFromMemberID());
						lb.get(j).setFromAccountID(fees.get(i).getFromAccountID());

						fromLastBalance = lb.get(j).getFromLastBalance();
						toLastBalance = lb.get(j).getToLastBalance();

						logger.info("[SOURCE Brokering]");
						logger.info("[BROKER Source Member ID : " + lb.get(j).getFromMemberID() + "]");
						logger.info("[BROKER Source Account ID : " + lb.get(j).getFromAccountID() + "]");

						logger.info("[BROKER Destination Member ID : " + lb.get(j).getToMemberID() + "]");
						logger.info("[BROKER Destination Account ID : " + lb.get(j).getToAccountID() + "]");

					} else if (lb.get(j).getFromMemberID() == -1 || lb.get(j).getFromAccountID() == -1) {
						lb.get(j).setFromMemberID(fees.get(i).getToMemberID());
						lb.get(j).setFromAccountID(fees.get(i).getToAccountID());

						fromLastBalance = lb.get(j).getFromLastBalance();
						toLastBalance = lb.get(j).getToLastBalance();

						logger.info("[DESTINATION Brokering]");
						logger.info("[BROKER Source Member ID : " + lb.get(j).getFromMemberID() + "]");
						logger.info("[BROKER Source Account ID : " + lb.get(j).getFromAccountID() + "]");

						logger.info("[BROKER Destination Member ID : " + lb.get(j).getToMemberID() + "]");
						logger.info("[BROKER Destination Account ID : " + lb.get(j).getToAccountID() + "]");

					} else {

						logger.info("[BROKER Source Member ID : " + lb.get(j).getFromMemberID() + "]");
						logger.info("[BROKER Source Account ID : " + lb.get(j).getFromAccountID() + "]");

						logger.info("[BROKER Destination Member ID : " + lb.get(j).getToMemberID() + "]");
						logger.info("[BROKER Destination Account ID : " + lb.get(j).getToAccountID() + "]");

						/*
						 * From Brokering Member Validation
						 */
						Members fromBrokerMember = baseRepository.getMembersRepository().findOneMembers("id",
								lb.get(j).getFromMemberID());

						if (fromBrokerMember == null) {
							logger.info("[Source Brokering MemberID Not Found [" + lb.get(j).getFromMemberID() + "]]");
							throw new TransactionException(String.valueOf(Status.MEMBER_NOT_FOUND));
						}

						/*
						 * From Brokering Account Validation
						 */
						Accounts fromBrokerAccount = baseRepository.getAccountsRepository()
								.loadAccountsByID(lb.get(j).getFromAccountID(), fromBrokerMember.getGroupID());
						if (fromBrokerAccount == null) {
							logger.info("[Invalid Source Brokering AccountID [" + lb.get(j).getFromAccountID() + "]]");
							throw new TransactionException(String.valueOf(Status.INVALID_ACCOUNT));
						}

						/*
						 * From Brokering BalanceInquiry
						 */
						logger.info("[Trying to Lock Source Brokering . . . ]");
						if (mapLock.isLocked(fromBrokerMember.getUsername() + fromBrokerAccount.getId()) == false) {
							logger.info("[LOCK Source Brokering : " + fromBrokerMember.getUsername() + "/"
									+ fromBrokerAccount.getId() + "]");
							mapLock.lock(fromBrokerMember.getUsername() + fromBrokerAccount.getId(), 80000,
									TimeUnit.MILLISECONDS);
						}

						accountValidation.validateMonthlyLimit(fromBrokerMember, fromBrokerAccount, feeAmount, true);
						fromLastBalance = accountValidation.validateAccountBalanceObj(fromBrokerMember,
								fromBrokerAccount, feeAmount, true);
					}

					/*
					 * To Brokering Member Validation
					 */
					Members toBrokerMember = baseRepository.getMembersRepository().findOneMembers("id",
							lb.get(j).getToMemberID());

					if (toBrokerMember == null) {
						logger.info("[Destination Brokering MemberID Not Found [" + lb.get(j).getToMemberID() + "]]");
						throw new TransactionException(String.valueOf(Status.MEMBER_NOT_FOUND));
					}

					/*
					 * To Brokering Account Validation
					 */
					Accounts toBrokerAccount = baseRepository.getAccountsRepository()
							.loadAccountsByID(lb.get(j).getToAccountID(), toBrokerMember.getGroupID());
					if (toBrokerAccount == null) {
						logger.info("[Invalid Destination Brokering AccountID [" + lb.get(j).getToAccountID() + "]]");
						throw new TransactionException(String.valueOf(Status.INVALID_ACCOUNT));
					}

					/*
					 * Destination Brokering BalanceInquiry
					 */
					logger.info("[Trying to Lock Destination Brokering . . . ]");
					if (mapLock.isLocked(toBrokerMember.getUsername() + toBrokerAccount.getId()) == false) {
						logger.info("[LOCK Destination Brokering : " + toBrokerMember.getUsername() + "/"
								+ toBrokerAccount.getId() + "]");
						mapLock.lock(toBrokerMember.getUsername() + toBrokerAccount.getId(), 80000,
								TimeUnit.MILLISECONDS);
					}

					accountValidation.validateMonthlyLimit(toBrokerMember, toBrokerAccount, feeAmount, true);
					toLastBalance = accountValidation.validateAccountBalanceObj(toBrokerMember, toBrokerAccount,
							feeAmount, true);

					String clusterid = System.getProperty("mule.clusterId") != null
							? System.getProperty("mule.clusterId")
							: "00";
					String brokeringTrxNo = Utils.GetDate("yyyyMMddkkmmssSSS") + clusterid + lb.get(j).getFeeID()
							+ Utils.GenerateTransactionNumber();
					lb.get(j).setTransactionNumber(brokeringTrxNo);
					lb.get(j).setFeeTransactionNumber(fees.get(i).getTransactionNumber());
					lb.get(j).setRequestTransactionAmount(transactionNumber);
					lb.get(j).setFromLastBalance(fromLastBalance);
					lb.get(j).setToLastBalance(toLastBalance);

					br.setTotalBrokeringAmount(result);
					br.setFeeAmount(feeAmount);
					br.setListBrokers(lb);
					logger.info("[Brokering " + (j + 1) + " of " + lb.size() + " TotalAmount(s) : " + result
							+ " From Fee id [" + fees.get(i).getId() + "] and Fee Amount : " + feeAmount + "]");
					processBrokering = true;
				} else {
					logger.info("[Brokering : " + result + " is bigger than Fee : " + feeAmount + "]");
					processBrokering = false;
				}
				if (processBrokering == false) {
					continue;
				}
			}
			if (processBrokering) {
				lbr.add(br);
			}
		}
		return lbr;
	}
}
