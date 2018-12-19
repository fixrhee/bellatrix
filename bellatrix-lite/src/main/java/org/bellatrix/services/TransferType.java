package org.bellatrix.services;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.Holder;
import org.bellatrix.data.TransactionException;

@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.BARE)
@WebService
public interface TransferType {

	@WebMethod(action = "loadTransferTypesByID")
	public LoadTransferTypesByIDResponse loadTransferTypesByID(
			@WebParam(header = true, name = "headerAuth") Holder<Header> headerParam,
			@WebParam LoadTransferTypesByIDRequest req);

	@WebMethod(action = "loadTransferTypesByAccountID")
	public LoadTransferTypesByAccountIDResponse loadTransferTypesByAccountID(
			@WebParam(header = true, name = "headerAuth") Holder<Header> headerParam,
			@WebParam LoadTransferTypesByAccountIDRequest req);

	@WebMethod(action = "loadTransferTypesByUsername")
	public LoadTransferTypesByUsernameResponse loadTransferTypesByUsername(
			@WebParam(header = true, name = "headerAuth") Holder<Header> headerParam,
			@WebParam LoadTransferTypesByUsernameRequest req);

	@WebMethod(action = "loadFeesByTransferType")
	public LoadFeesByTransferTypeResponse loadFeesByTransferType(
			@WebParam(header = true, name = "headerAuth") Holder<Header> headerParam,
			@WebParam LoadFeesByTransferTypeRequest req);

	@WebMethod(action = "createTransferTypes")
	public void createTransferTypes(@WebParam(header = true, name = "headerAuth") Holder<Header> headerParam,
			@WebParam TransferTypeRequest req) throws TransactionException;

	@WebMethod(action = "updateTransferTypes")
	public void updateTransferTypes(@WebParam(header = true, name = "headerAuth") Holder<Header> headerParam,
			@WebParam TransferTypeRequest req) throws TransactionException;

	@WebMethod(action = "createTransferTypePermissions")
	public void createTransferTypePermissions(@WebParam(header = true, name = "headerAuth") Holder<Header> headerParam,
			@WebParam TransferTypePermissionRequest req) throws TransactionException;

	@WebMethod(action = "deleteTransferTypePermissions")
	public void deleteTransferTypePermissions(@WebParam(header = true, name = "headerAuth") Holder<Header> headerParam,
			@WebParam TransferTypePermissionRequest req) throws TransactionException;

	@WebMethod(action = "createFees")
	public void createFees(@WebParam(header = true, name = "headerAuth") Holder<Header> headerParam,
			@WebParam FeeRequest req) throws TransactionException;

	@WebMethod(action = "updateFees")
	public void updateFees(@WebParam(header = true, name = "headerAuth") Holder<Header> headerParam,
			@WebParam FeeRequest req) throws TransactionException;

	@WebMethod(action = "deleteFees")
	public void deleteFees(@WebParam(header = true, name = "headerAuth") Holder<Header> headerParam,
			@WebParam FeeRequest req) throws TransactionException;

	@WebMethod(action = "loadBrokers")
	public LoadBrokerResponse loadBrokers(@WebParam(header = true, name = "headerAuth") Holder<Header> headerParam,
			@WebParam LoadBrokerRequest req);

	@WebMethod(action = "createBrokers")
	public void createBrokers(@WebParam(header = true, name = "headerAuth") Holder<Header> headerParam,
			@WebParam BrokerRequest req) throws TransactionException;

	@WebMethod(action = "editBrokers")
	public void editBrokers(@WebParam(header = true, name = "headerAuth") Holder<Header> headerParam,
			@WebParam BrokerRequest req) throws TransactionException;

	@WebMethod(action = "deleteBrokers")
	public void deleteBrokers(@WebParam(header = true, name = "headerAuth") Holder<Header> headerParam,
			@WebParam BrokerRequest req) throws TransactionException;

}