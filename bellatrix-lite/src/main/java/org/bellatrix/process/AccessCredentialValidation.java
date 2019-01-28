package org.bellatrix.process;

import java.util.List;
import org.apache.log4j.Logger;
import org.bellatrix.data.AccessStatus;
import org.bellatrix.data.AccessType;
import org.bellatrix.data.Accesses;
import org.bellatrix.data.Groups;
import org.bellatrix.data.TransactionException;
import org.bellatrix.services.AccessTypeRequest;
import org.bellatrix.services.CreateCredentialRequest;
import org.bellatrix.services.CredentialResponse;
import org.bellatrix.services.CredentialStatusRequest;
import org.bellatrix.services.ValidateCredentialRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccessCredentialValidation {

	@Autowired
	private BaseRepository baseRepository;
	private Logger logger = Logger.getLogger(AccessCredentialValidation.class);

	public void blockAttemptValidation(Integer memberID, Integer accessTypeID) {
		Groups group = baseRepository.getGroupsRepository().loadGroupsByMemberID(memberID);
		Integer count = countTotalFailedAttempts(memberID, accessTypeID);
		if (count + 1 >= group.getMaxPinTries()) {
			baseRepository.getAccessesRepository().blockCredential(memberID, accessTypeID);
			this.clearAccessAttemptsRecord(memberID, accessTypeID);
		} else {
			baseRepository.getAccessesRepository().flagAccessAttempts(memberID, accessTypeID);
		}
	}

	public void clearAccessAttemptsRecord(Integer memberID, Integer accessTypeID) {
		baseRepository.getAccessesRepository().clearAccessAttemptsRecord(memberID, accessTypeID);

	}

	public Integer countTotalFailedAttempts(Integer memberID, Integer accessTypeID) {
		return baseRepository.getAccessesRepository().countFailedAccessAttempts(memberID, accessTypeID);
	}

	public void createCredentialValidation(CreateCredentialRequest req) {
		baseRepository.getAccessesRepository().createCredential(req);
	}

	public Accesses validateCredential(ValidateCredentialRequest req) {
		return baseRepository.getAccessesRepository().loadCredentialByUsername(req.getUsername(),
				req.getAccessTypeID());
	}

	public AccessStatus credentialStatus(CredentialStatusRequest req) {
		return baseRepository.getAccessesRepository().accessStatus(req.getUsername(), req.getAccessTypeID());
	}

	public void unblockCredential(Integer memberID, Integer accessID) {
		baseRepository.getAccessesRepository().unblockCredential(memberID, accessID);
	}

	public void changeCredential(Integer memberID, Integer accessID, String newCredential) {
		baseRepository.getAccessesRepository().changeCredential(memberID, accessID, newCredential);
	}

	public CredentialResponse getSecretFromMember(Integer memberID, Integer accessID) throws TransactionException {
		return baseRepository.getAccessesRepository().getSecretFromMemberID(memberID, accessID);
	}

	public List<AccessType> getAllCredentialType() {
		return baseRepository.getAccessesRepository().getAllCredentialType();
	}

	public List<AccessType> getCredentialTypeByID(AccessTypeRequest req) {
		return baseRepository.getAccessesRepository().getCredentialTypeByID(req.getId());
	}

	public void updateCredentialType(AccessTypeRequest req) {
		baseRepository.getAccessesRepository().updateCredentialType(req.getId(), req.getName(), req.getInternalName(),
				req.getDescription());
	}

	public void createCredentialType(AccessTypeRequest req) {
		baseRepository.getAccessesRepository().createCredentialType(req.getName(), req.getInternalName(),
				req.getDescription());
	}

}
