package org.bellatrix.process;

import java.util.List;
import javax.xml.ws.Holder;
import org.bellatrix.auth.JWTProcessor;
import org.bellatrix.data.Groups;
import org.bellatrix.data.Status;
import org.bellatrix.data.StatusBuilder;
import org.bellatrix.data.TransactionException;
import org.bellatrix.data.WebServices;
import org.bellatrix.services.AuthRequest;
import org.bellatrix.services.AuthResponse;
import org.bellatrix.services.CreateWebserviceRequest;
import org.bellatrix.services.Header;
import org.bellatrix.services.LoadWebserviceRequest;
import org.bellatrix.services.LoadWebserviceResponse;
import org.bellatrix.services.Webservice;
import org.bellatrix.services.WebservicePermissionRequest;
import org.springframework.beans.factory.annotation.Autowired;

public class WebserviceImpl implements Webservice {

	@Autowired
	private JWTProcessor jwt;
	@Autowired
	private WebserviceValidation webserviceValidation;
	@Autowired
	private GroupValidation groupValidation;

	@Override
	public AuthResponse getWebServicesToken(AuthRequest req) {
		AuthResponse ar = new AuthResponse();
		try {
			WebServices ws = webserviceValidation.validateWebservice(req.getUsername(), req.getPassword());
			if (ws != null) {
				String token = jwt.createJWTHMAC256(ws.getUsername(), ws.getHash());
				ar.setToken(token);
				ar.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
				return ar;
			} else {
				ar.setStatus(StatusBuilder.getStatus(Status.ACCESS_DENIED));
				return ar;
			}
		} catch (TransactionException ex) {
			ar.setStatus(StatusBuilder.getStatus(ex.getMessage()));
			return ar;
		}
	}

	@Override
	public LoadWebserviceResponse loadWebservices(Holder<Header> headerParam, LoadWebserviceRequest req) {
		LoadWebserviceResponse lwr = new LoadWebserviceResponse();
		try {
			List<WebServices> lw = webserviceValidation.validateLoadWS(headerParam.value.getToken(), req);
			if (req.getId() == null) {
				Integer records = webserviceValidation.countTotalWebservices();
				lwr.setTotalRecords(records);
			}
			lwr.setWebservice(lw);
			lwr.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
			return lwr;
		} catch (TransactionException ex) {
			lwr.setStatus(StatusBuilder.getStatus(ex.getMessage()));
			return lwr;
		}
	}

	@Override
	public void createWebservices(Holder<Header> headerParam, CreateWebserviceRequest req) throws TransactionException {
		webserviceValidation.validateWebservice(headerParam.value.getToken());
		webserviceValidation.validateCreateWS(req);
	}

	@Override
	public void updateWebservices(Holder<Header> headerParam, CreateWebserviceRequest req) throws TransactionException {
		webserviceValidation.validateWebservice(headerParam.value.getToken());
		webserviceValidation.validateUpdateWS(req);
	}

	@Override
	public void deleteWebservices(Holder<Header> headerParam, CreateWebserviceRequest req) throws TransactionException {
		webserviceValidation.validateWebservice(headerParam.value.getToken());
		webserviceValidation.validateDeleteWS(req);
	}

	@Override
	public LoadWebserviceResponse loadWebservicesByGroup(Holder<Header> headerParam, LoadWebserviceRequest req) {
		LoadWebserviceResponse lwr = new LoadWebserviceResponse();
		try {
			webserviceValidation.validateWebservice(headerParam.value.getToken());
			List<WebServices> lw = webserviceValidation.validateWSByGroup(headerParam.value.getToken(), req);
			lwr.setWebservice(lw);
			lwr.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
			return lwr;
		} catch (TransactionException ex) {
			lwr.setStatus(StatusBuilder.getStatus(ex.getMessage()));
			return lwr;
		}
	}

	@Override
	public void addWebservicePermission(Holder<Header> headerParam, WebservicePermissionRequest req)
			throws TransactionException {
		Groups groups = groupValidation.loadGroupsByID(req.getGroupID());
		if (groups != null) {
			WebServices ws = webserviceValidation.validateWebserviceByID(req.getWebserviceID());
			if (ws != null) {
				webserviceValidation.validateWebservice(headerParam.value.getToken());
				webserviceValidation.validateAddWSPermission(req);
			} else {
				throw new TransactionException(String.valueOf(Status.INVALID_PARAMETER));
			}
		} else {
			throw new TransactionException(String.valueOf(Status.INVALID_GROUP));
		}
	}

	@Override
	public void deleteWebservicePermission(Holder<Header> headerParam, WebservicePermissionRequest req)
			throws TransactionException {
		webserviceValidation.validateWebservice(headerParam.value.getToken());
		webserviceValidation.validateDeleteWSPermission(req);
	}

}
