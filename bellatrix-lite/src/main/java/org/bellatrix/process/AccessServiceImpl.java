package org.bellatrix.process;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.ws.Holder;
import org.bellatrix.data.AccessStatus;
import org.bellatrix.data.AccessType;
import org.bellatrix.data.Accesses;
import org.bellatrix.data.Groups;
import org.bellatrix.data.Members;
import org.bellatrix.data.Notifications;
import org.bellatrix.data.Status;
import org.bellatrix.data.StatusBuilder;
import org.bellatrix.data.TransactionException;
import org.bellatrix.services.Access;
import org.bellatrix.services.AccessTypeRequest;
import org.bellatrix.services.ChangeCredentialRequest;
import org.bellatrix.services.CreateCredentialRequest;
import org.bellatrix.services.CredentialStatusRequest;
import org.bellatrix.services.CredentialStatusResponse;
import org.bellatrix.services.Header;
import org.bellatrix.services.LoadAccessTypeResponse;
import org.bellatrix.services.ResetCredentialRequest;
import org.bellatrix.services.ResetCredentialResponse;
import org.bellatrix.services.CredentialRequest;
import org.bellatrix.services.CredentialResponse;
import org.bellatrix.services.UnblockCredentialRequest;
import org.bellatrix.services.ValidateCredentialRequest;
import org.bellatrix.services.ValidateCredentialResponse;
import org.mule.api.MuleException;
import org.mule.module.client.MuleClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AccessServiceImpl implements Access {

	@Autowired
	private BaseRepository baseRepository;
	@Autowired
	private AccessCredentialValidation accessValidation;
	@Autowired
	private WebserviceValidation webserviceValidation;
	@Autowired
	private GroupValidation groupValidation;
	@Autowired
	private MemberValidation memberValidation;
	@Autowired
	private Configurator configurator;

	@Override
	@Transactional
	public void createCredential(Holder<Header> headerParam, CreateCredentialRequest req) throws Exception {
		try {
			webserviceValidation.validateWebservice(headerParam.value.getToken());
			Members members = memberValidation.validateMember(req.getUsername(), true);
			req.setMemberID(members.getId());
			boolean status = baseRepository.getAccessesRepository().validateCreateCredential(req.getAccessTypeID(),
					members.getId());
			if (status == true) {
				accessValidation.createCredentialValidation(req);
			} else {
				throw new TransactionException(String.valueOf(Status.INVALID_PARAMETER));
			}
		} catch (Exception e) {
			throw new TransactionException(String.valueOf(Status.INVALID_PARAMETER));
		}
	}

	@Override
	public ValidateCredentialResponse validateCredential(Holder<Header> headerParam, ValidateCredentialRequest req) {
		ValidateCredentialResponse validateResponse = new ValidateCredentialResponse();
		try {
			webserviceValidation.validateWebservice(headerParam.value.getToken());
			Members members = memberValidation.validateMember(req.getUsername(), true);
			Accesses access = accessValidation.validateCredential(req);
			if (access == null) {
				validateResponse.setStatus(StatusBuilder.getStatus(Status.INVALID_PARAMETER));
				return validateResponse;
			}

			Groups group = groupValidation.loadGroupsByID(members.getGroupID());
			if (group.getName().equalsIgnoreCase("CLOSED")) {
				validateResponse.setStatus(StatusBuilder.getStatus(Status.BLOCKED));
				return validateResponse;
			}

			if (!access.getPin().equals(Utils.getMD5Hash(req.getCredential()))) {
				if (access.isBlocked()) {
					validateResponse.setStatus(StatusBuilder.getStatus(Status.BLOCKED));
					return validateResponse;
				}
				accessValidation.blockAttemptValidation(members.getId(), req.getAccessTypeID());
				validateResponse.setStatus(StatusBuilder.getStatus(Status.INVALID));
				return validateResponse;
			}

			if (accessValidation.countTotalFailedAttempts(members.getId(), access.getAccessTypeID()) == group
					.getMaxPinTries()) {
				accessValidation.clearAccessAttemptsRecord(members.getId(), access.getAccessTypeID());
				validateResponse.setStatus(StatusBuilder.getStatus(Status.VALID));
			}

			validateResponse.setStatus(StatusBuilder.getStatus(Status.VALID));
			return validateResponse;
		} catch (TransactionException e) {
			validateResponse.setStatus(StatusBuilder.getStatus(e.getMessage()));
			return validateResponse;
		}

	}

	@Override
	public CredentialStatusResponse credentialStatus(Holder<Header> headerParam, CredentialStatusRequest req) {
		CredentialStatusResponse crStatus = new CredentialStatusResponse();
		try {
			webserviceValidation.validateWebservice(headerParam.value.getToken());
			AccessStatus status = accessValidation.credentialStatus(req);
			if (status == null) {
				crStatus.setStatus(StatusBuilder.getStatus(Status.CREDENTIAL_INVALID));
				return crStatus;
			}
			crStatus.setAccessStatus(status);
			crStatus.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
			return crStatus;
		} catch (TransactionException e) {
			crStatus.setStatus(StatusBuilder.getStatus(e.getMessage()));
			return crStatus;
		}
	}

	@Override
	@Transactional
	public ResetCredentialResponse resetCredential(Holder<Header> headerParam, ResetCredentialRequest req)
			throws TransactionException {
		webserviceValidation.validateWebservice(headerParam.value.getToken());
		Members member = memberValidation.validateMember(req.getUsername(), true);
		ResetCredentialResponse resp = new ResetCredentialResponse();

		/* === VALIDATE EMAIL ====
		 * if (member.getEmail() != null) { if
		 * (!member.getEmail().equalsIgnoreCase(req.getEmail())) { throw new
		 * TransactionException(String.valueOf(Status.MEMBER_NOT_FOUND)); } } else {
		 * throw new TransactionException(String.valueOf(Status.INVALID_PARAMETER)); }
		 */
		
		Groups group = groupValidation.loadGroupsByID(member.getGroupID());
		String newCredential = Utils.GenerateRandomNumber(group.getPinLength());

		accessValidation.changeCredential(member.getId(), req.getAccessTypeID(), newCredential);
		accessValidation.unblockCredential(member.getId(), req.getAccessTypeID());
		accessValidation.clearAccessAttemptsRecord(member.getId(), req.getAccessTypeID());

		resp.setMember(member);
		resp.setCredential(newCredential);
		return resp;
	}

	@Override
	@Transactional
	public void unblockCredential(Holder<Header> headerParam, UnblockCredentialRequest req)
			throws TransactionException {
		webserviceValidation.validateWebservice(headerParam.value.getToken());
		Members member = memberValidation.validateMember(req.getUsername(), true);
		accessValidation.unblockCredential(member.getId(), req.getAccessTypeID());
		accessValidation.clearAccessAttemptsRecord(member.getId(), req.getAccessTypeID());
	}

	@Override
	public void changeCredential(Holder<Header> headerParam, ChangeCredentialRequest req) throws TransactionException {
		ValidateCredentialRequest validate = new ValidateCredentialRequest();
		webserviceValidation.validateWebservice(headerParam.value.getToken());
		Members member = memberValidation.validateMember(req.getUsername(), true);
		validate.setAccessTypeID(req.getAccessTypeID());
		validate.setCredential(req.getOldCredential());
		validate.setUsername(req.getUsername());
		Accesses access = accessValidation.validateCredential(validate);
		if (access != null) {
			if (!access.getPin().equalsIgnoreCase(Utils.getMD5Hash(req.getOldCredential()))) {
				throw new TransactionException(String.valueOf(Status.INVALID));
			} else {
				accessValidation.changeCredential(member.getId(), req.getAccessTypeID(), req.getNewCredential());
			}
		} else {
			throw new TransactionException(String.valueOf(Status.INVALID_PARAMETER));
		}
	}

	@Override
	public CredentialResponse getCredential(Holder<Header> headerParam, CredentialRequest req) throws Exception {
		CredentialResponse cr = new CredentialResponse();
		try {
			webserviceValidation.validateWebservice(headerParam.value.getToken());
			Members members = memberValidation.validateMember(req.getUsername(), true);
			cr = accessValidation.getSecretFromMember(members.getId(), req.getAccessTypeID());
			cr.setCredential(cr.getCredential());
			cr.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
			return cr;
		} catch (TransactionException ex) {
			cr.setStatus(StatusBuilder.getStatus(ex.getMessage()));
			return cr;
		}
	}

	@Override
	public LoadAccessTypeResponse loadAccessType(Holder<Header> headerParam, AccessTypeRequest req) throws Exception {
		LoadAccessTypeResponse latr = new LoadAccessTypeResponse();
		webserviceValidation.validateWebservice(headerParam.value.getToken());
		List<AccessType> accessType = null;
		if (req.getId() == null) {
			accessType = accessValidation.getAllCredentialType();
		} else {
			accessType = accessValidation.getCredentialTypeByID(req);
		}
		latr.setAccessType(accessType);
		latr.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
		return latr;
	}

	@Override
	public void updateAccessType(Holder<Header> headerParam, AccessTypeRequest req) throws TransactionException {
		try {
			webserviceValidation.validateWebservice(headerParam.value.getToken());
			accessValidation.updateCredentialType(req);
		} catch (TransactionException ex) {
			throw new TransactionException(ex.getMessage());
		}
	}

	@Override
	public void createAccessType(Holder<Header> headerParam, AccessTypeRequest req) throws TransactionException {
		try {
			webserviceValidation.validateWebservice(headerParam.value.getToken());
			accessValidation.createCredentialType(req);
		} catch (TransactionException ex) {
			throw new TransactionException(ex.getMessage());
		}
	}

}
