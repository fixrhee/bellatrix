package org.bellatrix.services;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.Holder;

import org.bellatrix.data.TransactionException;

@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.BARE)
@WebService
public interface Notification {

	@WebMethod(action = "loadNotifications")
	public NotificationResponse loadNotifications(
			@WebParam(header = true, name = "headerAuth") Holder<Header> headerParam,
			@WebParam NotificationRequest req);

	@WebMethod(action = "createNotifications")
	public void createNotifications(@WebParam(header = true, name = "headerAuth") Holder<Header> headerParam,
			@WebParam NotificationRequest req) throws TransactionException;

	@WebMethod(action = "editNotifications")
	public void editNotifications(@WebParam(header = true, name = "headerAuth") Holder<Header> headerParam,
			@WebParam NotificationRequest req) throws TransactionException;

	@WebMethod(action = "deleteNotifications")
	public void deleteNotifications(@WebParam(header = true, name = "headerAuth") Holder<Header> headerParam,
			@WebParam NotificationRequest req) throws TransactionException;

}
