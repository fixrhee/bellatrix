package org.bellatrix.process;

import java.util.List;

import org.bellatrix.auth.JWTAuthCodecFilter;
import org.bellatrix.data.Status;
import org.bellatrix.data.TransactionException;
import org.bellatrix.data.WebServices;
import org.bellatrix.services.CreateWebserviceRequest;
import org.bellatrix.services.LoadWebserviceRequest;
import org.bellatrix.services.WebservicePermissionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

@Component
public class WebserviceValidation {
	@Autowired
	private HazelcastInstance instance;
	@Value("${global.cache.config.enabled}")
	private boolean useCache;
	@Autowired
	private JWTAuthCodecFilter authFilter;
	@Autowired
	private BaseRepository baseRepository;

	public WebServices validateWebserviceByID(Integer id) throws TransactionException {
		return baseRepository.getWebServicesRepository().validateWebService(id);
	}

	public WebServices validateWebservice(String username, String password) throws TransactionException {
		return baseRepository.getWebServicesRepository().validateWebService(username, password);
	}

	public WebServices validateWebservice(String token) throws TransactionException {
		WebServices ws = null;
		if (useCache) {
			IMap<String, WebServices> wsMap = instance.getMap("WebServiceMap");
			ws = wsMap.get(token);
		} else {
			ws = authFilter.Authenticate(token);
		}
		if (ws == null) {
			throw new TransactionException(String.valueOf(Status.UNAUTHORIZED_ACCESS));
		}
		return ws;
	}

	public List<WebServices> validateLoadWS(String token, LoadWebserviceRequest req) throws TransactionException {
		this.validateWebservice(token);
		if (req.getId() != null) {
			return baseRepository.getWebServicesRepository().loadWebservicesByID(req.getId());
		} else {
			return baseRepository.getWebServicesRepository().loadWebservices(req.getCurrentPage(), req.getPageSize());
		}
	}

	public List<WebServices> validateLoadWSPermission(String token, LoadWebserviceRequest req)
			throws TransactionException {
		this.validateWebservice(token);
		return baseRepository.getWebServicesRepository().loadWebservicesPermission(req.getId());
	}

	public List<WebServices> validateWSByGroup(String token, LoadWebserviceRequest req) throws TransactionException {
		this.validateWebservice(token);
		return baseRepository.getWebServicesRepository().loadWebservicesByGroup(req.getGroupID(), req.getCurrentPage(),
				req.getPageSize());
	}

	public Integer countTotalWebservices() {
		Integer records = baseRepository.getWebServicesRepository().countWebservices();
		return records;
	}

	public void validateCreateWS(CreateWebserviceRequest req) {
		baseRepository.getWebServicesRepository().insertWebservice(req.getUsername(), req.getPassword(), req.getName(),
				req.getHash(), req.isActive(), req.isSecureTransaction());
	}

	public void validateUpdateWS(CreateWebserviceRequest req) {
		baseRepository.getWebServicesRepository().updateWebservice(req.getId(), req.getUsername(), req.getPassword(),
				req.getName(), req.getHash(), req.isActive(), req.isSecureTransaction());
	}

	public void validateDeleteWS(CreateWebserviceRequest req) {
		baseRepository.getWebServicesRepository().deleteWebservice(req.getId());
	}

	public void validateAddWSPermission(WebservicePermissionRequest req) throws TransactionException {
		if (baseRepository.getWebServicesRepository().validateGroupAccessToWebService(req.getWebserviceID(),
				req.getGroupID()) == false) {
			baseRepository.getWebServicesRepository().addWebservicePermission(req.getWebserviceID(), req.getGroupID());
		} else {
			throw new TransactionException(String.valueOf(Status.PERMISSION_ALREADY_GRANTED));
		}
	}

	public void validateDeleteWSPermission(WebservicePermissionRequest req) throws TransactionException {
		if (req.getId() != null) {
			baseRepository.getWebServicesRepository().deleteWebservicePermission(req.getId());
		} else {
			if (baseRepository.getWebServicesRepository().validateGroupAccessToWebService(req.getWebserviceID(),
					req.getGroupID()) == true) {
				baseRepository.getWebServicesRepository().deleteWebservicePermission(req.getWebserviceID(),
						req.getGroupID());
			} else {
				throw new TransactionException(String.valueOf(Status.INVALID_PARAMETER));
			}
		}
	}
}
