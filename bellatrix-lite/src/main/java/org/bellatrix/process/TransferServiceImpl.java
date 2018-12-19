package org.bellatrix.process;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.xml.ws.Holder;
import org.apache.log4j.Logger;
import org.bellatrix.data.ExternalMemberFields;
import org.bellatrix.data.MemberView;
import org.bellatrix.data.Notifications;
import org.bellatrix.data.PaymentFields;
import org.bellatrix.data.Status;
import org.bellatrix.data.StatusBuilder;
import org.bellatrix.data.TransactionException;
import org.bellatrix.data.TransferHistory;
import org.bellatrix.data.TransferTypeFields;
import org.bellatrix.data.Transfers;
import org.bellatrix.services.ConfirmPaymentRequest;
import org.bellatrix.services.ConfirmPaymentTicketRequest;
import org.bellatrix.services.CreatePaymentCustomFieldsRequest;
import org.bellatrix.services.GeneratePaymentTicketRequest;
import org.bellatrix.services.GeneratePaymentTicketResponse;
import org.bellatrix.services.Header;
import org.bellatrix.services.InquiryRequest;
import org.bellatrix.services.InquiryResponse;
import org.bellatrix.data.PaymentDetails;
import org.bellatrix.services.PaymentRequest;
import org.bellatrix.services.PaymentResponse;
import org.bellatrix.services.PendingPaymentRequest;
import org.bellatrix.services.PendingPaymentResponse;
import org.bellatrix.services.RequestPaymentConfirmationResponse;
import org.bellatrix.services.ReversalRequest;
import org.bellatrix.services.ReversalResponse;
import org.bellatrix.services.TransactionStatusRequest;
import org.bellatrix.services.TransactionStatusResponse;
import org.bellatrix.services.Transfer;
import org.bellatrix.services.ValidatePaymentTicketRequest;
import org.bellatrix.services.ValidatePaymentTicketResponse;
import org.mule.api.MuleException;
import org.mule.module.client.MuleClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

@Component
public class TransferServiceImpl implements Transfer {

	@Autowired
	private TransferValidation transfersValidation;
	@Autowired
	private BaseRepository baseRepository;
	@Autowired
	private HazelcastInstance instance;
	@Autowired
	private Configurator configurator;
	private Logger logger = Logger.getLogger(TransferServiceImpl.class);

	@Override
	@Transactional
	public PaymentResponse doPayment(Holder<Header> headerParam, PaymentRequest req) {
		PaymentResponse pr = new PaymentResponse();
		PaymentDetails pd = null;
		try {
			pd = transfersValidation.validatePayment(req, headerParam.value.getToken(), "PROCESSED");
			if (pd != null) {
				List<Notifications> ln = baseRepository.getNotificationsRepository()
						.loadNotificationByTransferType(pd.getTransferType().getId());
				pd.setNotification(ln);
				MuleClient client = new MuleClient(configurator.getMuleContext());
				Map<String, Object> header = new HashMap<String, Object>();
				client.dispatch("NotificationVM", pd, header);
			}

			pr.setId(pd.getTransferID());
			pr.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
			pr.setAmount(pd.getFees().getFinalAmount());
			pr.setDescription(pd.getRequest().getDescription());
			pr.setFromMember(pd.getFromMember());
			pr.setPaymentFields(pd.getRequest().getPaymentFields());
			TransferTypeFields typeField = new TransferTypeFields();
			typeField.setFromAccounts(pd.getFromAccount().getId());
			typeField.setId(pd.getTransferType().getId());
			typeField.setName(pd.getTransferType().getName());
			typeField.setToAccounts(pd.getToAccount().getId());
			pr.setTransferType(typeField);
			pr.setToMember(pd.getToMember());
			pr.setTraceNumber(pd.getRequest().getTraceNumber());
			pr.setTransactionNumber(pd.getTransactionNumber());
			return pr;

		} catch (TransactionException e) {
			pr.setStatus(StatusBuilder.getStatus(e.getMessage()));
			return pr;
		} catch (SocketTimeoutException ex) {
			pr.setStatus(StatusBuilder.getStatus(Status.REQUEST_TIMEOUT));
			return pr;
		} catch (MuleException e) {
			pr.setStatus(StatusBuilder.getStatus(Status.UNKNOWN_ERROR));
			return pr;
		}
	}

	@Override
	public InquiryResponse doInquiry(Holder<Header> headerParam, InquiryRequest req) {
		InquiryResponse ir = new InquiryResponse();
		try {
			ir = transfersValidation.validateInquiry(req, headerParam.value.getToken());
			ir.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
			return ir;
		} catch (SocketTimeoutException e) {
			ir.setStatus(StatusBuilder.getStatus(Status.REQUEST_TIMEOUT));
			return ir;
		} catch (TransactionException ex) {
			ir.setStatus(StatusBuilder.getStatus(ex.getMessage()));
			return ir;
		}
	}

	@Override
	@Transactional
	public RequestPaymentConfirmationResponse requestPaymentConfirmation(Holder<Header> headerParam,
			PaymentRequest req) {

		RequestPaymentConfirmationResponse rp = new RequestPaymentConfirmationResponse();
		IMap<String, PaymentDetails> mapLrpcMap = instance.getMap("RequestPaymentMap");
		PaymentDetails pd = null;

		try {

			pd = transfersValidation.validatePayment(req, headerParam.value.getToken(), "PENDING");
			String reqID = UUID.randomUUID().toString();

			/*
			 * 
			 * OTP IMPLEMENTATION
			 * 
			 * 
			 * 
			 * BigDecimal otpThreshold = pd.getTransferType().getOtpThreshold();
			 * logger.info("[Transaction Amount : " + pd.getFees().getFinalAmount() +
			 * "/ OTP Threshold : " + otpThreshold + "]");
			 * 
			 * if (pd.getFees().getFinalAmount().compareTo(otpThreshold) == 1) {
			 * pd.setTwoFactorAuthentication(true); List<Notifications> ln = new
			 * LinkedList<Notifications>(); Notifications notif =
			 * baseRepository.getNotificationsRepository()
			 * .loadDefaultNotificationByGroupID(pd.getFromMember().getGroupID());
			 * notif.setNotificationType("requestPaymentConfirmation");
			 * 
			 * ln.add(notif); pd.setNotification(ln); String otp =
			 * Utils.GenerateRandomNumber(6); pd.setOtp(otp);
			 * rp.setTwoFactorAuthentication(true); MuleClient client; client = new
			 * MuleClient(configurator.getMuleContext()); Map<String, Object> header = new
			 * HashMap<String, Object>(); client.dispatch("NotificationVM", pd, header); }
			 */

			try {
				if (req.getRequestEviction().getRequestEvictionSeconds() != 0L) {
					mapLrpcMap.put(reqID, pd, req.getRequestEviction().getRequestEvictionSeconds(), TimeUnit.SECONDS);
				} else {
					mapLrpcMap.put(reqID, pd);
				}
			} catch (NullPointerException ex) {
				mapLrpcMap.put(reqID, pd);
			}

			TransferTypeFields tf = new TransferTypeFields();
			tf.setFromAccounts(pd.getFromAccount().getId());
			tf.setId(pd.getTransferType().getId());
			tf.setName(pd.getTransferType().getName());
			tf.setToAccounts(pd.getToAccount().getId());

			rp.setFromMember(pd.getFromMember());
			rp.setToMember(pd.getToMember());
			rp.setRequestID(reqID);
			rp.setFinalAmount(pd.getFees().getFinalAmount());
			rp.setTotalFees(pd.getFees().getTotalFees());
			rp.setTransactionAmount(req.getAmount());
			rp.setTransferType(tf);
			rp.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
			return rp;

		} catch (TransactionException e) {
			rp.setStatus(StatusBuilder.getStatus(e.getMessage()));
			return rp;
		} catch (SocketTimeoutException ex) {
			rp.setStatus(StatusBuilder.getStatus(Status.REQUEST_TIMEOUT));
			return rp;
		}
	}

	@Override
	@Transactional
	public PaymentResponse confirmPayment(Holder<Header> headerParam, ConfirmPaymentRequest req) {
		IMap<String, PaymentDetails> mapLrpcMap = instance.getMap("RequestPaymentMap");
		PaymentDetails pc = mapLrpcMap.get(req.getRequestID());
		PaymentResponse pr = new PaymentResponse();

		/*
		 * Validate Cache by requestID
		 */
		if (pc == null) {
			pr.setStatus(StatusBuilder.getStatus(Status.PAYMENT_CODE_NOT_FOUND));
			return pr;
		}

		/*
		 * OTP IMPLEMENTATION
		 * 
		 * if (pc.isTwoFactorAuthentication()) {
		 * 
		 * Validate OTP
		 * 
		 * if (!pc.getOtp().equalsIgnoreCase(req.getOtp())) {
		 * mapLrpcMap.remove(req.getRequestID());
		 * baseRepository.getTransfersRepository().deletePendingTransfers(pc.
		 * getTransactionNumber());
		 * pr.setStatus(StatusBuilder.getStatus(Status.OTP_VALIDATION_FAILED)); return
		 * pr; } }
		 */

		/*
		 * Evict Cache Now
		 */
		mapLrpcMap.remove(req.getRequestID());

		/*
		 * Update Transaction to PROCESSED
		 */
		baseRepository.getTransfersRepository().confirmPendingTransfers(pc.getTransactionNumber(),
				pc.getRequest().getTraceNumber(), pc.getWebService().getId());

		/*
		 * Update Fees to PROCESSED (if any)
		 */
		if (!pc.getFees().getListTotalFees().isEmpty()) {

			baseRepository.getTransferTypesRepository().updatePendingFees(pc.getTransactionNumber());
		}

		List<Notifications> ln = baseRepository.getNotificationsRepository()
				.loadNotificationByTransferType(pc.getTransferType().getId());
		try {

			if (ln.size() > 0) {
				pc.setNotification(ln);
				MuleClient client;
				client = new MuleClient(configurator.getMuleContext());
				Map<String, Object> header = new HashMap<String, Object>();
				client.dispatch("NotificationVM", pc, header);
			}

			pr.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
			pr.setAmount(pc.getFees().getFinalAmount());
			pr.setDescription(pc.getRequest().getDescription());
			pr.setFromMember(pc.getFromMember());
			pr.setPaymentFields(pc.getRequest().getPaymentFields());
			TransferTypeFields typeField = new TransferTypeFields();
			typeField.setFromAccounts(pc.getFromAccount().getId());
			typeField.setId(pc.getTransferType().getId());
			typeField.setName(pc.getTransferType().getName());
			typeField.setToAccounts(pc.getToAccount().getId());
			pr.setTransferType(typeField);
			pr.setToMember(pc.getToMember());
			pr.setTraceNumber(pc.getRequest().getTraceNumber().substring(1));
			pr.setTransactionNumber(pc.getTransactionNumber());
			return pr;
		} catch (MuleException e) {
			pr.setStatus(StatusBuilder.getStatus(Status.UNKNOWN_ERROR));
			return pr;
		}

	}

	@Override
	public GeneratePaymentTicketResponse generatePaymentTicket(Holder<Header> headerParam,
			GeneratePaymentTicketRequest req) {
		GeneratePaymentTicketResponse gtr = new GeneratePaymentTicketResponse();
		try {
			transfersValidation.validatePaymentRequest(headerParam.value.getToken(), req);
			String ticket = UUID.randomUUID().toString();
			IMap<String, GeneratePaymentTicketRequest> genMap = instance.getMap("GeneratePaymentMap");
			genMap.put(ticket, req);
			gtr.setTicket(ticket);
			gtr.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
			return gtr;
		} catch (TransactionException ex) {
			gtr.setStatus(StatusBuilder.getStatus(ex.getMessage()));
			return gtr;
		} catch (SocketTimeoutException ex) {
			gtr.setStatus(StatusBuilder.getStatus(Status.REQUEST_TIMEOUT));
			return gtr;
		}

	}

	@Override
	public ValidatePaymentTicketResponse validatePaymentTicket(Holder<Header> headerParam,
			ValidatePaymentTicketRequest req) {
		IMap<String, GeneratePaymentTicketRequest> genMap = instance.getMap("GeneratePaymentMap");
		ValidatePaymentTicketResponse vtr = new ValidatePaymentTicketResponse();
		GeneratePaymentTicketRequest gtr = genMap.get(req.getTicket());
		if (gtr == null) {
			vtr.setStatus(StatusBuilder.getStatus(Status.PAYMENT_NOT_FOUND));
			return vtr;
		}
		vtr.setAmount(gtr.getAmount());
		vtr.setDescription(gtr.getDescription());
		vtr.setToMember(gtr.getToMember());
		vtr.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
		return vtr;
	}

	@Override
	public PaymentResponse confirmPaymentTicket(Holder<Header> headerParam, ConfirmPaymentTicketRequest req) {
		IMap<String, GeneratePaymentTicketRequest> genMap = instance.getMap("GeneratePaymentMap");
		PaymentResponse pr = new PaymentResponse();
		try {

			GeneratePaymentTicketRequest gtr = genMap.get(req.getTicket());
			if (gtr == null) {
				pr.setStatus(StatusBuilder.getStatus(Status.PAYMENT_NOT_FOUND));
				return pr;
			}

			String fromMember;
			if (req.isExternalID()) {
				ExternalMemberFields ext = baseRepository.getMembersRepository()
						.loadExternalMemberFields(req.getFromMember());
				if (ext == null) {
					pr.setStatus(StatusBuilder.getStatus(Status.MEMBER_NOT_FOUND));
					return pr;
				} else {
					fromMember = ext.getUsername();
				}
			} else {
				fromMember = req.getFromMember();
			}

			PaymentRequest preq = new PaymentRequest();
			preq.setAccessTypeID(req.getAccessTypeID());
			preq.setAmount(gtr.getAmount());
			preq.setCredential(req.getCredential());
			preq.setDescription(gtr.getDescription());
			preq.setFromMember(fromMember);
			preq.setPaymentFields(gtr.getPaymentFields());
			preq.setToMember(gtr.getToMember());
			preq.setTraceNumber(req.getTraceNumber());
			preq.setTransferTypeID(gtr.getTransferTypeID());

			PaymentDetails pd;
			pd = transfersValidation.validatePayment(preq, headerParam.value.getToken(), "PROCESSED");

			pr.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
			pr.setAmount(pd.getFees().getFinalAmount());
			pr.setDescription(pd.getRequest().getDescription());
			pr.setFromMember(pd.getFromMember());
			pr.setPaymentFields(pd.getRequest().getPaymentFields());
			TransferTypeFields typeField = new TransferTypeFields();
			typeField.setFromAccounts(pd.getFromAccount().getId());
			typeField.setId(pd.getTransferType().getId());
			typeField.setName(pd.getTransferType().getName());
			typeField.setToAccounts(pd.getToAccount().getId());
			pr.setTransferType(typeField);
			pr.setToMember(pd.getToMember());
			pr.setTraceNumber(pd.getRequest().getTraceNumber().substring(1));
			pr.setTransactionNumber(pd.getTransactionNumber());
			return pr;

		} catch (TransactionException e) {
			pr.setStatus(StatusBuilder.getStatus(e.getMessage()));
			return pr;
		} catch (SocketTimeoutException ex) {
			pr.setStatus(StatusBuilder.getStatus(Status.REQUEST_TIMEOUT));
			return pr;
		} finally {
			genMap.remove(req.getTicket());
		}
	}

	@Override
	@Transactional
	public ReversalResponse reversePayment(Holder<Header> headerParam, ReversalRequest req) {
		ReversalResponse rr = new ReversalResponse();
		List<Transfers> listMainTransfers = new LinkedList<Transfers>();

		try {
			if (req.getTransactionNumber() == null) {
				listMainTransfers = transfersValidation.validateReversal(req.getTraceNumber(),
						headerParam.value.getToken());
				if (listMainTransfers == null) {
					rr.setStatus(StatusBuilder.getStatus(Status.PAYMENT_NOT_FOUND));
					return rr;
				}
			} else {
				listMainTransfers = transfersValidation.validateReversal(req.getTransactionNumber());
			}

			Transfers mainTransfers = listMainTransfers.get(0);
			baseRepository.getTransfersRepository().reverseTransaction(mainTransfers.getTransactionNumber());
			rr.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
			return rr;

		} catch (SocketTimeoutException e) {
			rr.setStatus(StatusBuilder.getStatus(Status.REQUEST_TIMEOUT));
			return rr;
		} catch (TransactionException e) {
			rr.setStatus(StatusBuilder.getStatus(e.getMessage()));
			return rr;
		}
	}

	@Override
	public TransactionStatusResponse transactionStatus(Holder<Header> headerParam, TransactionStatusRequest req) {
		TransactionStatusResponse status = new TransactionStatusResponse();
		try {
			List<Transfers> trf = transfersValidation.validateTransactionStatus(req.getTraceNumber(),
					headerParam.value.getToken());

			List<Integer> transferIDs = new LinkedList<Integer>();
			for (Transfers t : trf) {
				transferIDs.add(t.getId());
			}

			List<PaymentFields> pfield = baseRepository.getTransfersRepository()
					.loadMultiPaymentFieldValuesByTransferID(transferIDs);

			Map<Integer, List<PaymentFields>> result = pfield.stream()
					.collect(Collectors.groupingBy(PaymentFields::getTransferID, Collectors.toList()));

			List<TransferHistory> lth = new LinkedList<TransferHistory>();
			for (int i = 0; i < trf.size(); i++) {
				TransferHistory trfHistory = new TransferHistory();
				if (trf.get(i).isCustomField()) {
					List<PaymentFields> paymentFields = result.get(trf.get(i).getId());
					trfHistory.setCustomFields(paymentFields);
				}
				MemberView fromMemberTrf = new MemberView();
				fromMemberTrf.setId(trf.get(i).getFromMemberID());
				fromMemberTrf.setName(trf.get(i).getFromName());
				fromMemberTrf.setUsername(trf.get(i).getFromUsername());

				MemberView toMemberTrf = new MemberView();
				toMemberTrf.setId(trf.get(i).getToMemberID());
				toMemberTrf.setName(trf.get(i).getToName());
				toMemberTrf.setUsername(trf.get(i).getToUsername());

				if (trf.get(i).getParentID() == null) {
					TransferTypeFields typeField = new TransferTypeFields();
					typeField.setFromAccounts(trf.get(i).getFromAccountID());
					typeField.setToAccounts(trf.get(i).getToAccountID());
					typeField.setName(trf.get(i).getName());
					trfHistory.setTransferType(typeField);
				}

				trfHistory.setAmount(trf.get(i).getAmount());
				trfHistory.setChargedBack(trf.get(i).isChargedBack());
				trfHistory.setDescription(trf.get(i).getDescription());
				trfHistory.setFromMember(fromMemberTrf);
				trfHistory.setToMember(toMemberTrf);
				trfHistory.setId(trf.get(i).getId());
				trfHistory.setParentID(trf.get(i).getParentID());
				trfHistory.setTraceNumber(trf.get(i).getTraceNumber().substring(1));
				trfHistory.setTransactionState(trf.get(i).getTransactionState());
				trfHistory.setTransactionDate(trf.get(i).getTransactionDate());
				trfHistory.setTransactionNumber(trf.get(i).getTransactionNumber());
				lth.add(trfHistory);
			}

			status.setTransfers(lth);
			status.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
			return status;

		} catch (SocketTimeoutException e) {
			status.setStatus(StatusBuilder.getStatus(Status.REQUEST_TIMEOUT));
			return status;
		} catch (TransactionException e) {
			status.setStatus(StatusBuilder.getStatus(e.getMessage()));
			return status;
		}
	}

	@Override
	public void createPaymentCustomFields(Holder<Header> headerParam, CreatePaymentCustomFieldsRequest req)
			throws Exception {
		baseRepository.getTransfersRepository().createPaymentCustomField(req);
	}

	@Override
	public PendingPaymentResponse loadPendingTransaction(Holder<Header> headerParam, PendingPaymentRequest req) {
		// TODO Auto-generated method stub
		return null;
	}
}
