package org.bellatrix.process;

import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.bellatrix.data.Accounts;
import org.bellatrix.data.BrokeringResult;
import org.bellatrix.data.Currencies;
import org.bellatrix.data.FeeResult;
import org.bellatrix.data.Fees;
import org.bellatrix.data.Members;
import org.bellatrix.data.PaymentCustomFields;
import org.bellatrix.data.PaymentDetails;
import org.bellatrix.data.PaymentFields;
import org.bellatrix.data.Status;
import org.bellatrix.data.TransactionException;
import org.bellatrix.data.TransferTypeFields;
import org.bellatrix.data.TransferTypes;
import org.bellatrix.data.Transfers;
import org.bellatrix.data.WebServices;
import org.bellatrix.services.GeneratePaymentTicketRequest;
import org.bellatrix.services.InquiryRequest;
import org.bellatrix.services.InquiryResponse;
import org.bellatrix.services.PaymentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

@Component
public class TransferValidation {

	@Autowired
	private BaseRepository baseRepository;
	@Autowired
	private FeeProcessor feeProcessor;
	@Autowired
	private BrokerProcessor brokerProcessor;
	@Autowired
	private CredentialValidation credentialValidation;
	@Autowired
	private TransferTypeValidation transferTypeValidation;
	@Autowired
	private WebserviceValidation webserviceValidation;
	@Autowired
	private GroupValidation groupValidation;
	@Autowired
	private MemberValidation memberValidation;
	@Autowired
	private AccountValidation accountValidation;
	@Autowired
	private HazelcastInstance instance;
	@Autowired
	private TraceNumberValidation traceNumberValidation;

	private Logger logger = Logger.getLogger(TransferValidation.class);

	public InquiryResponse validateInquiry(InquiryRequest req, String token)
			throws SocketTimeoutException, TransactionException {

		/*
		 * Validate Webservice Access
		 */
		WebServices ws = webserviceValidation.validateWebservice(token);
		Integer wsID = ws.getId();

		/*
		 * Validate FromMember
		 */
		Members fromMember = memberValidation.validateMember(req.getFromMember(), true);

		/*
		 * Validate FromMember Group Permission to Webservice
		 */
		groupValidation.validateGroupPermission(instance, wsID, fromMember.getGroupID());

		/*
		 * Validate ToMember
		 */
		Members toMember = memberValidation.validateMember(req.getToMember(), false);

		/*
		 * Validate TransferType
		 */
		TransferTypes transferType = transferTypeValidation.validateTransferType(req.getTransferTypeID(),
				fromMember.getGroupID(), req.getAmount(), fromMember.getId());

		/*
		 * Validate FromAccount
		 */
		Accounts fromAccount = accountValidation.validateAccount(transferType, fromMember, true);
		Accounts toAccount = null;
		// if (transferType.getFromAccounts() != transferType.getToAccounts()) {

		/*
		 * Validate ToAccount
		 */
		toAccount = accountValidation.validateAccount(transferType, toMember, false);
		// } else {
		// toAccount = fromAccount;
		// }

		/*
		 * Validate Currency
		 */

		accountValidation.validateAccountCurrency(fromAccount, toAccount);

		FeeResult feeResult = null;
		/*
		 * PRIORITY Fees Processing (if Priority Fee == true then skip the Regular Fee
		 * Processing)
		 */

		FeeResult priorityFeeResult = feeProcessor.CalculatePriorityFee(transferType, fromMember, toMember, fromAccount,
				toAccount, req.getAmount());

		/*
		 * Regular Fees Processing (Skip this if Priority Fee == true)
		 */

		if (priorityFeeResult.getListTotalFees().size() == 0) {
			logger.info("[Executing REGULAR Fee]");
			FeeResult regularFeeResult = feeProcessor.CalculateFee(transferType, fromMember, toMember, fromAccount,
					toAccount, req.getAmount());
			feeResult = regularFeeResult;
		} else {
			logger.info("[Executing PRIORITY Fee]");
			feeResult = priorityFeeResult;
		}

		BigDecimal otpThreshold = transferType.getOtpThreshold();
		logger.info("[Transaction Amount : " + feeResult.getFinalAmount() + "/ OTP Threshold : " + otpThreshold + "]");

		Currencies currency = baseRepository.getCurrenciesRepository().loadCurrencyByAccountID(fromAccount.getId());

		InquiryResponse ir = new InquiryResponse();
		TransferTypeFields tfield = new TransferTypeFields();
		tfield.setFromAccounts(fromAccount.getId());
		tfield.setToAccounts(toAccount.getId());
		tfield.setName(transferType.getName());
		tfield.setId(transferType.getId());

		ir.setFinalAmount(feeResult.getFinalAmount());
		ir.setFormattedFinalAmount(Utils.formatAmount(feeResult.getFinalAmount(), currency.getGrouping(),
				currency.getDecimal(), currency.getFormat(), currency.getPrefix(), currency.getTrailer()));

		ir.setTotalFees(feeResult.getTotalFees());
		ir.setFormattedTotalFees(Utils.formatAmount(feeResult.getTotalFees(), currency.getGrouping(),
				currency.getDecimal(), currency.getFormat(), currency.getPrefix(), currency.getTrailer()));

		ir.setTransactionAmount(feeResult.getTransactionAmount());
		ir.setFormattedTransactionAmount(Utils.formatAmount(feeResult.getTransactionAmount(), currency.getGrouping(),
				currency.getDecimal(), currency.getFormat(), currency.getPrefix(), currency.getTrailer()));

		ir.setFromMember(fromMember);
		ir.setToMember(toMember);
		ir.setTransferType(tfield);

		return ir;

	}

	public PaymentDetails validatePayment(PaymentRequest req, String token, String transactionState)
			throws TransactionException, SocketTimeoutException {
		IMap<String, BigDecimal> mapLock = instance.getMap("AccountLock");
		List<Fees> fees = new LinkedList<Fees>();
		Members sourceMember = new Members();
		Members targetMember = new Members();
		Accounts fromAccount = new Accounts();
		Accounts toAccount = new Accounts();
		try {

			/*
			 * Validate Webservice Access
			 */
			WebServices ws = webserviceValidation.validateWebservice(token);
			Integer wsID = ws.getId();

			/*
			 * Validate FromMember
			 */
			Members fromMember = memberValidation.validateMember(req.getFromMember(), true);
			sourceMember = fromMember;

			/*
			 * Validate FromMember Group Permission to Webservice
			 */
			groupValidation.validateGroupPermission(instance, wsID, fromMember.getGroupID());

			/*
			 * Validate FromMember Credential
			 */
			credentialValidation.validateCredential(ws, req.getAccessTypeID(), req.getCredential(), fromMember);

			/*
			 * Validate ToMember
			 */
			Members toMember = memberValidation.validateMember(req.getToMember(), false);
			targetMember = toMember;

			/*
			 * Validate Trace Number (Save to cache also ?)
			 */
			traceNumberValidation.validateTraceNumber(wsID, req.getTraceNumber());

			/*
			 * Validate TransferType
			 */
			TransferTypes transferType = transferTypeValidation.validateTransferType(req.getTransferTypeID(),
					fromMember.getGroupID(), req.getAmount(), fromMember.getId());

			if (req.getDescription() == null) {
				req.setDescription(transferType.getName());
			}

			/*
			 * Validate PaymentCustomField
			 */
			if (req.getPaymentFields() != null) {
				validatePaymentCustomField(transferType.getId(), req.getPaymentFields());
			}

			/*
			 * 
			 * Validate FromAccount + Lock Accounts
			 *
			 */
			fromAccount = accountValidation.validateAccount(transferType, fromMember, true);

			// if (transferType.getFromAccounts() != transferType.getToAccounts()) {

			/*
			 * Validate ToAccount
			 */
			toAccount = accountValidation.validateAccount(transferType, toMember, false);
			// } else {
			// toAccount = fromAccount;
			// }

			/*
			 * Validate Currency
			 */

			accountValidation.validateAccountCurrency(fromAccount, toAccount);

			/*
			 * Lock Member
			 */
			mapLock.put(fromMember.getUsername() + fromAccount.getId(), BigDecimal.ZERO);
			mapLock.lock(fromMember.getUsername() + fromAccount.getId(), 80000, TimeUnit.MILLISECONDS);
			logger.info("[LOCK Source Member/Account : " + req.getFromMember() + "/" + fromAccount.getId() + "]");

			mapLock.put(toMember.getUsername() + toAccount.getId(), BigDecimal.ZERO);
			mapLock.lock(toMember.getUsername() + toAccount.getId(), 80000, TimeUnit.MILLISECONDS);
			logger.info("[LOCK Destination Member/Account : " + req.getToMember() + "/" + toAccount.getId() + "]");

			FeeResult feeResult = null;
			/*
			 * PRIORITY Fees Processing (if Priority Fee == true then skip the Regular Fee
			 * Processing)
			 */

			FeeResult priorityFeeResult = feeProcessor.ProcessPriorityFee(transferType, fromMember, toMember,
					fromAccount, toAccount, req.getAmount());

			/*
			 * Regular Fees Processing (Skip this if Priority Fee == true)
			 */

			if (priorityFeeResult.getListTotalFees().size() == 0) {
				logger.info("[Executing REGULAR Fee]");
				FeeResult regularFeeResult = feeProcessor.ProcessFee(transferType, fromMember, toMember, fromAccount,
						toAccount, req.getAmount());
				feeResult = regularFeeResult;
			} else {
				logger.info("[Executing PRIORITY Fee]");
				feeResult = priorityFeeResult;
			}

			/*
			 * If Source Account is SystemAccount then skip this validation, otherwise check
			 * for Monthly Limit
			 */

			logger.info("[Source Account : " + fromAccount.getName() + "]");

			if (fromAccount.isSystemAccount() == false) {

				logger.info(
						"[Validating Source Account balance/Monthly limit validation : " + fromAccount.getName() + "]");

				/*
				 * Validate FromMember UpperCredit Limit --> Commented for validating incoming
				 * transaction ONLY (PBI Terbaru 2016)
				 */
				// accountValidation.validateMonthlyLimit(fromMember,
				// fromAccount, feeResult.getFinalAmount(), true);

				/*
				 * Get FromMember Balance Inquiry (Lock Account)
				 */
				accountValidation.validateAccountBalance(fromMember, fromAccount, feeResult.getFinalAmount(), true);
			}

			/*
			 * If Destination Account is SystemAccount then skip this validation, otherwise
			 * check for Monthly Limit
			 */

			logger.info("[Destination Account : " + toAccount.getName() + "]");

			if (toAccount.isSystemAccount() == false) {

				logger.info("[Validating Destination Account balance/Monthly limit validation : " + toAccount.getName()
						+ "]");

				/*
				 * Get ToMember UpperCredit Limit
				 */
				accountValidation.validateMonthlyLimit(toMember, toAccount, feeResult.getFinalAmount(), false);

				/*
				 * Get ToMember Balance Inquiry (Lock Account)
				 */
				accountValidation.validateAccountBalance(toMember, toAccount, feeResult.getFinalAmount(), false);
			}

			/*
			 * All Validation PASSED then generate Transaction Number
			 */
			String clusterid = System.getProperty("mule.clusterId") != null ? System.getProperty("mule.clusterId")
					: "00";
			String trxNo = Utils.GetDate("yyyyMMddkkmmssSSS") + clusterid + Utils.GenerateTransactionNumber();

			Integer transferID = baseRepository.getTransfersRepository().createTransfers(req,
					feeResult.getTransactionAmount(), transferType, fromAccount, toAccount, fromMember, toMember, trxNo,
					null, transactionState, wsID);

			/*
			 * Insert Fees & Brokers (If any)
			 */
			if (!feeResult.getListTotalFees().isEmpty()) {
				baseRepository.getTransfersRepository().insertFees(req, feeResult.getListTotalFees(), transferType,
						trxNo, transactionState, wsID);

				List<BrokeringResult> br = brokerProcessor.procesBrokering(feeResult.getListTotalFees(), trxNo);
				logger.info("[Total Brokering Fee : " + br.size() + "]");

				for (int i = 0; i < br.size(); i++) {
					baseRepository.getTransfersRepository().insertBrokers(br.get(i).getListBrokers(), transferType,
							transactionState, wsID);
				}
			}

			PaymentDetails pc = new PaymentDetails();
			pc.setTransferID(transferID);
			pc.setFees(feeResult);
			pc.setFromMember(fromMember);
			pc.setFromAccount(fromAccount);
			pc.setToMember(toMember);
			pc.setToAccount(toAccount);
			pc.setRequest(req);
			pc.setWebService(ws);
			pc.setTransactionNumber(trxNo);
			pc.setTransferType(transferType);
			pc.setTransactionDate(new Date());
			return pc;

		} finally {
			if (mapLock != null && sourceMember != null && targetMember != null && fromAccount != null
					&& toAccount != null) {
				if (mapLock.isLocked(req.getFromMember() + fromAccount.getId())) {
					logger.info(
							"[UNLOCK Source Member/Account : " + req.getFromMember() + "/" + fromAccount.getId() + "]");
					mapLock.unlock(req.getFromMember() + fromAccount.getId());
				}
				if (mapLock.isLocked(req.getToMember() + toAccount.getId())) {
					logger.info("[UNLOCK Destination Member/Account : " + req.getToMember() + "/" + toAccount.getId()
							+ "]");
					mapLock.unlock(req.getToMember() + toAccount.getId());
				}

				if (fees.size() > 0) {
					for (int i = 0; i < fees.size(); i++) {
						if (!fees.get(i).getFromMember().getUsername().equalsIgnoreCase(sourceMember.getUsername())
								&& mapLock.isLocked(
										fees.get(i).getFromMember().getUsername() + fees.get(i).getFromAccountID())) {
							logger.info(
									"[UNLOCK Fee Source Member/Account : " + fees.get(i).getFromMember().getUsername()
											+ "/" + fees.get(i).getFromAccountID() + "]");
							mapLock.unlock(fees.get(i).getFromMember().getUsername() + fees.get(i).getFromAccountID());
						}
						if (!fees.get(i).getToMember().getUsername().equalsIgnoreCase(targetMember.getUsername())
								&& mapLock.isLocked(
										fees.get(i).getToMember().getUsername() + fees.get(i).getToAccountID())) {
							logger.info("[UNLOCK Fee Destination Member/Account : "
									+ fees.get(i).getToMember().getUsername() + "/" + fees.get(i).getToAccountID()
									+ "]");
							mapLock.unlock(fees.get(i).getToMember().getUsername() + fees.get(i).getToAccountID());
						}
					}
				}

			}

		}
	}

	public void validatePaymentRequest(String token, GeneratePaymentTicketRequest req)
			throws TransactionException, SocketTimeoutException {
		/*
		 * Validate Webservice Access
		 */
		webserviceValidation.validateWebservice(token);

		/*
		 * Validate ToMember
		 */
		Members toMember = memberValidation.validateMember(req.getToMember(), false);

		/*
		 * Validate TransferType
		 */
		TransferTypes transferType = transferTypeValidation.validateTransferType(req.getTransferTypeID(),
				req.getAmount());

		/*
		 * Validate PaymentCustomField
		 */
		if (req.getPaymentFields() != null) {
			validatePaymentCustomField(transferType.getId(), req.getPaymentFields());
		}

		/*
		 * Validate ToAccount
		 */
		accountValidation.validateAccount(transferType, toMember, false);

	}

	public List<Transfers> validateReversal(String traceNumber, String token)
			throws SocketTimeoutException, TransactionException {
		/*
		 * Validate Webservice Access
		 */

		WebServices ws = webserviceValidation.validateWebservice(token);

		List<Transfers> mainTransfers = baseRepository.getTransfersRepository().getTransfersFromField("trace_number",
				ws.getId() + traceNumber);
		if (mainTransfers.size() == 0) {
			return null;
		} else {
			return mainTransfers;
		}

	}

	public List<Transfers> validateReversal(String transactionNumber)
			throws SocketTimeoutException, TransactionException {

		List<Transfers> mainTransfers = baseRepository.getTransfersRepository()
				.getTransfersFromField("transaction_number", transactionNumber);
		if (mainTransfers.size() == 0) {
			return null;
		} else {
			return mainTransfers;
		}
	}

	public List<Transfers> validateTransactionStatus(String traceNumber, String token)
			throws SocketTimeoutException, TransactionException {
		/*
		 * Validate Webservice Access
		 */

		WebServices ws = webserviceValidation.validateWebservice(token);

		List<Transfers> mainTransfers = baseRepository.getTransfersRepository().getTransfersFromField("trace_number",
				ws.getId() + traceNumber);
		if (mainTransfers.size() == 0) {
			throw new TransactionException(String.valueOf(Status.PAYMENT_NOT_FOUND));
		} else {
			return mainTransfers;
		}

	}

	public List<Transfers> validateReversalFromTrxRef(String trxRef, String token)
			throws SocketTimeoutException, TransactionException {
		/*
		 * Validate Webservice Access
		 */

		webserviceValidation.validateWebservice(token);

		List<Transfers> mainTransfers = baseRepository.getTransfersRepository()
				.getTransfersFromField("reference_number", trxRef);
		if (mainTransfers.size() == 0) {
			return null;
		} else {
			return mainTransfers;
		}
	}

	public void validatePaymentCustomField(Integer transferTypeID, List<PaymentFields> paymentField)
			throws TransactionException {
		List<PaymentCustomFields> fields = baseRepository.getTransfersRepository()
				.loadPaymentCustomFieldByTransferType(transferTypeID);

		List<Integer> cfRef = new LinkedList<Integer>();
		for (int i = 0; i < fields.size(); i++) {
			cfRef.add(fields.get(i).getId());
		}
		List<Integer> cfReq = new LinkedList<Integer>();
		for (int i = 0; i < paymentField.size(); i++) {
			cfReq.add(paymentField.get(i).getPaymentCustomFieldID());
		}
		cfReq.removeAll(cfRef);
		if (cfReq.size() != 0) {
			logger.info("[Invalid Payment Custom Field Count : " + cfReq.size() + " / From : " + fields.size() + "]");
			throw new TransactionException(String.valueOf(Status.INVALID_PARAMETER));
		}
	}
}
