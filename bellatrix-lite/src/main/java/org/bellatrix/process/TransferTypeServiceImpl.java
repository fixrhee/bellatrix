package org.bellatrix.process;

import java.util.List;
import javax.xml.ws.Holder;

import org.bellatrix.data.Accounts;
import org.bellatrix.data.Brokers;
import org.bellatrix.data.Fees;
import org.bellatrix.data.Members;
import org.bellatrix.data.Status;
import org.bellatrix.data.StatusBuilder;
import org.bellatrix.data.TransactionException;
import org.bellatrix.data.TransferTypes;
import org.bellatrix.services.LoadBrokerRequest;
import org.bellatrix.services.LoadBrokerResponse;
import org.bellatrix.services.BrokerRequest;
import org.bellatrix.services.FeeRequest;
import org.bellatrix.services.Header;
import org.bellatrix.services.LoadFeesByTransferTypeRequest;
import org.bellatrix.services.LoadFeesByTransferTypeResponse;
import org.bellatrix.services.LoadTransferTypesByAccountIDRequest;
import org.bellatrix.services.LoadTransferTypesByAccountIDResponse;
import org.bellatrix.services.LoadTransferTypesByIDRequest;
import org.bellatrix.services.LoadTransferTypesByIDResponse;
import org.bellatrix.services.LoadTransferTypesByUsernameRequest;
import org.bellatrix.services.LoadTransferTypesByUsernameResponse;
import org.bellatrix.services.TransferType;
import org.bellatrix.services.TransferTypePermissionRequest;
import org.bellatrix.services.TransferTypeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransferTypeServiceImpl implements TransferType {

	@Autowired
	private WebserviceValidation webserviceValidation;
	@Autowired
	private MemberValidation memberValidation;
	@Autowired
	private BaseRepository baseRepository;
	@Autowired
	private TransferTypeValidation transferTypeValidation;
	@Autowired
	private AccountValidation accountValidation;

	@Override
	public LoadTransferTypesByIDResponse loadTransferTypesByID(Holder<Header> headerParam,
			LoadTransferTypesByIDRequest req) {
		LoadTransferTypesByIDResponse tid = new LoadTransferTypesByIDResponse();
		try {
			webserviceValidation.validateWebservice(headerParam.value.getToken());
			TransferTypes t = transferTypeValidation.validateTransferType(req.getId());
			tid.setTransferTypes(t);
			tid.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
			return tid;
		} catch (Exception e) {
			tid.setStatus(StatusBuilder.getStatus(e.getMessage()));
			return tid;
		}
	}

	@Override
	public LoadTransferTypesByAccountIDResponse loadTransferTypesByAccountID(Holder<Header> headerParam,
			LoadTransferTypesByAccountIDRequest req) {
		LoadTransferTypesByAccountIDResponse taid = new LoadTransferTypesByAccountIDResponse();
		try {
			webserviceValidation.validateWebservice(headerParam.value.getToken());
			List<TransferTypes> lt = baseRepository.getTransferTypesRepository()
					.listTransferTypeByAccountID(req.getAccountID());
			taid.setTransferTypes(lt);
			taid.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
			return taid;
		} catch (Exception e) {
			taid.setStatus(StatusBuilder.getStatus(e.getMessage()));
			return taid;
		}
	}

	@Override
	public LoadTransferTypesByUsernameResponse loadTransferTypesByUsername(Holder<Header> headerParam,
			LoadTransferTypesByUsernameRequest req) {
		LoadTransferTypesByUsernameResponse tur = new LoadTransferTypesByUsernameResponse();
		try {
			webserviceValidation.validateWebservice(headerParam.value.getToken());
			Members member = memberValidation.validateMember(req.getUsername(), true);
			List<TransferTypes> ltv = baseRepository.getTransferTypesRepository()
					.listTransferTypeByGroupID(member.getGroupID());
			tur.setTransferTypes(ltv);
			tur.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
		} catch (Exception e) {
			tur.setStatus(StatusBuilder.getStatus(e.getMessage()));
			return tur;
		}
		return tur;
	}

	@Override
	public LoadFeesByTransferTypeResponse loadFeesByTransferType(Holder<Header> headerParam,
			LoadFeesByTransferTypeRequest req) {
		LoadFeesByTransferTypeResponse lfbt = new LoadFeesByTransferTypeResponse();
		try {
			webserviceValidation.validateWebservice(headerParam.value.getToken());
			transferTypeValidation.validateTransferType(req.getId());
			List<Fees> fees = baseRepository.getTransferTypesRepository().getFeeFromTransferTypeID(req.getId());
			lfbt.setFees(fees);
			lfbt.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
		} catch (Exception e) {
			lfbt.setStatus(StatusBuilder.getStatus(e.getMessage()));
			return lfbt;
		}
		return lfbt;
	}

	@Override
	public void createTransferTypes(Holder<Header> headerParam, TransferTypeRequest req) throws TransactionException {
		try {
			webserviceValidation.validateWebservice(headerParam.value.getToken());
			Accounts fromAcc = accountValidation.validateAccount(req.getFromAccountID());
			Accounts toAcc = accountValidation.validateAccount(req.getToAccountID());
			baseRepository.getTransferTypesRepository().createTransferType(fromAcc.getId(), toAcc.getId(),
					req.getName(), req.getDescription(), req.getMinAmount(), req.getMaxAmount(), req.getMaxCount(),
					req.getOtpThreshold());
		} catch (Exception e) {
			throw new TransactionException(e.getMessage());
		}
	}

	@Override
	public void updateTransferTypes(Holder<Header> headerParam, TransferTypeRequest req) throws TransactionException {
		try {
			webserviceValidation.validateWebservice(headerParam.value.getToken());
			transferTypeValidation.validateTransferType(req.getId());
			Accounts fromAcc = accountValidation.validateAccount(req.getFromAccountID());
			Accounts toAcc = accountValidation.validateAccount(req.getToAccountID());
			baseRepository.getTransferTypesRepository().updateTransferType(req.getId(), fromAcc.getId(), toAcc.getId(),
					req.getName(), req.getDescription(), req.getMinAmount(), req.getMaxAmount(), req.getMaxCount(),
					req.getOtpThreshold());
		} catch (Exception e) {
			throw new TransactionException(e.getMessage());
		}
	}

	@Override
	public void createTransferTypePermissions(Holder<Header> headerParam, TransferTypePermissionRequest req)
			throws TransactionException {
		try {
			webserviceValidation.validateWebservice(headerParam.value.getToken());
			transferTypeValidation.validateTransferType(req.getTransferTypeID());
			baseRepository.getTransferTypesRepository().createTransferTypePermission(req.getTransferTypeID(),
					req.getGroupID());
		} catch (Exception e) {
			throw new TransactionException(e.getMessage());
		}
	}

	@Override
	public void deleteTransferTypePermissions(Holder<Header> headerParam, TransferTypePermissionRequest req)
			throws TransactionException {
		try {
			webserviceValidation.validateWebservice(headerParam.value.getToken());
			transferTypeValidation.validateTransferType(req.getTransferTypeID());
			baseRepository.getTransferTypesRepository().deleteTransferTypePermission(req.getTransferTypeID(),
					req.getGroupID());
		} catch (Exception e) {
			throw new TransactionException(e.getMessage());
		}
	}

	@Override
	public void createFees(Holder<Header> headerParam, FeeRequest req) throws TransactionException {
		try {
			webserviceValidation.validateWebservice(headerParam.value.getToken());
			transferTypeValidation.validateTransferType(req.getTransferTypeID());
			baseRepository.getTransferTypesRepository().createFee(req);
		} catch (Exception e) {
			throw new TransactionException(e.getMessage());
		}
	}

	@Override
	public void updateFees(Holder<Header> headerParam, FeeRequest req) throws TransactionException {
		try {
			webserviceValidation.validateWebservice(headerParam.value.getToken());
			transferTypeValidation.validateTransferType(req.getTransferTypeID());
			baseRepository.getTransferTypesRepository().updateFee(req);
		} catch (Exception e) {
			throw new TransactionException(e.getMessage());
		}
	}

	@Override
	public void deleteFees(Holder<Header> headerParam, FeeRequest req) throws TransactionException {
		try {
			webserviceValidation.validateWebservice(headerParam.value.getToken());
			transferTypeValidation.validateTransferType(req.getTransferTypeID());
			baseRepository.getTransferTypesRepository().deleteFee(req.getTransferTypeID());
		} catch (Exception e) {
			throw new TransactionException(e.getMessage());
		}
	}

	@Override
	public LoadBrokerResponse loadBrokers(Holder<Header> headerParam, LoadBrokerRequest req) {
		LoadBrokerResponse br = new LoadBrokerResponse();
		try {
			webserviceValidation.validateWebservice(headerParam.value.getToken());
			if (req.getFeeID() != null && req.getFromMember() != null) {
				Members member = memberValidation.validateMember(req.getFromMember(), true);
				List<Brokers> lb = baseRepository.getTransferTypesRepository().getBrokersFromMember(req.getFeeID(),
						member.getId());
				br.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
				br.setBrokers(lb);
			} else if (req.getFeeID() != null && req.getToMember() != null) {
				Members member = memberValidation.validateMember(req.getToMember(), false);
				List<Brokers> lb = baseRepository.getTransferTypesRepository().getBrokersToMember(req.getFeeID(),
						member.getId());
				br.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
				br.setBrokers(lb);
			} else if (req.getFeeID() != null) {
				List<Brokers> lb = baseRepository.getTransferTypesRepository().getBrokersFromFee(req.getFeeID());
				br.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
				br.setBrokers(lb);
			} else {
				List<Brokers> lb = baseRepository.getTransferTypesRepository().loadAllBrokers();
				br.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
				br.setBrokers(lb);
			}
			return br;
		} catch (TransactionException ex) {
			br.setStatus(StatusBuilder.getStatus(ex.getMessage()));
			return br;
		}
	}

	@Override
	public void createBrokers(Holder<Header> headerParam, BrokerRequest req) throws TransactionException {
		webserviceValidation.validateWebservice(headerParam.value.getToken());
		Members toMember = memberValidation.validateMember(req.getToMember(), false);
		accountValidation.validateAccount(req.getToAccountID());
		if (req.getFromMember().equalsIgnoreCase("0") || req.getFromMember().equalsIgnoreCase("-1")) {
			baseRepository.getTransferTypesRepository().createBrokers(req, Integer.valueOf(req.getFromMember()),
					toMember.getId());
		} else {
			Members fromMember = memberValidation.validateMember(req.getFromMember(), true);
			accountValidation.validateAccount(req.getFromAccountID());
			baseRepository.getTransferTypesRepository().createBrokers(req, fromMember.getId(), toMember.getId());
		}
	}

	@Override
	public void editBrokers(Holder<Header> headerParam, BrokerRequest req) throws TransactionException {
		webserviceValidation.validateWebservice(headerParam.value.getToken());
		Members toMember = memberValidation.validateMember(req.getToMember(), false);
		accountValidation.validateAccount(req.getToAccountID());
		if (req.getFromMember().equalsIgnoreCase("0") || req.getFromMember().equalsIgnoreCase("-1")) {
			baseRepository.getTransferTypesRepository().updateBrokers(req, Integer.valueOf(req.getFromMember()),
					toMember.getId());
		} else {
			Members fromMember = memberValidation.validateMember(req.getFromMember(), true);
			accountValidation.validateAccount(req.getFromAccountID());
			baseRepository.getTransferTypesRepository().updateBrokers(req, fromMember.getId(), toMember.getId());
		}
	}

	@Override
	public void deleteBrokers(Holder<Header> headerParam, BrokerRequest req) throws TransactionException {
		webserviceValidation.validateWebservice(headerParam.value.getToken());
		baseRepository.getTransferTypesRepository().deleteBrokers(req);
	}
}
