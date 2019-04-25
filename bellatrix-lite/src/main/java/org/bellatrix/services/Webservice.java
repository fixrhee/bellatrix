package org.bellatrix.services;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.Holder;

import org.bellatrix.data.TransactionException;

@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.BARE)

@WebService
public interface Webservice {

	@WebMethod(action = "getWebServicesToken")
	public AuthResponse getWebServicesToken(@WebParam AuthRequest req);

	@WebMethod(action = "loadWebservices")
	public LoadWebserviceResponse loadWebservices(
			@WebParam(header = true, name = "headerAuth") Holder<Header> headerParam,
			@WebParam LoadWebserviceRequest req);

	@WebMethod(action = "loadWebservicesByGroup")
	public LoadWebserviceResponse loadWebservicesByGroup(
			@WebParam(header = true, name = "headerAuth") Holder<Header> headerParam,
			@WebParam LoadWebserviceRequest req);

	@WebMethod(action = "loadWebservicesPermission")
	public LoadWebserviceResponse loadWebservicesPermission(
			@WebParam(header = true, name = "headerAuth") Holder<Header> headerParam,
			@WebParam LoadWebserviceRequest req);

	@WebMethod(action = "createWebservices")
	public void createWebservices(@WebParam(header = true, name = "headerAuth") Holder<Header> headerParam,
			@WebParam CreateWebserviceRequest req) throws TransactionException;

	@WebMethod(action = "updateWebservices")
	public void updateWebservices(@WebParam(header = true, name = "headerAuth") Holder<Header> headerParam,
			@WebParam CreateWebserviceRequest req) throws TransactionException;

	@WebMethod(action = "deleteWebservices")
	public void deleteWebservices(@WebParam(header = true, name = "headerAuth") Holder<Header> headerParam,
			@WebParam CreateWebserviceRequest req) throws TransactionException;

	@WebMethod(action = "addWebservicePermission")
	public void addWebservicePermission(@WebParam(header = true, name = "headerAuth") Holder<Header> headerParam,
			@WebParam WebservicePermissionRequest req) throws TransactionException;

	@WebMethod(action = "deleteWebservicePermission")
	public void deleteWebservicePermission(@WebParam(header = true, name = "headerAuth") Holder<Header> headerParam,
			@WebParam WebservicePermissionRequest req) throws TransactionException;

}
