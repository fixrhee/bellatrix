package org.bellatrix.services;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.Holder;

@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.BARE)
@WebService
public interface Menu {

	@WebMethod(action = "loadMenuByGroups")
	public LoadMenuByGroupsResponse loadMenuByGroups(
			@WebParam(header = true, name = "headerAuth") Holder<Header> headerParam,
			@WebParam LoadMenuByGroupsRequest req);

	@WebMethod(action = "loadParentMenu")
	public LoadParentMenuResponse loadParentMenu(
			@WebParam(header = true, name = "headerAuth") Holder<Header> headerParam,
			@WebParam LoadParentMenuRequest req);

	@WebMethod(action = "loadCategoryMenu")
	public LoadCategoryMenuResponse loadCategoryMenu(
			@WebParam(header = true, name = "headerAuth") Holder<Header> headerParam,
			@WebParam LoadCategoryMenuRequest req);

	@WebMethod(action = "loadChildMenu")
	public LoadChildMenuResponse loadChildMenu(@WebParam(header = true, name = "headerAuth") Holder<Header> headerParam,
			@WebParam LoadChildMenuRequest req);

	@WebMethod(action = "loadWelcomeMenu")
	public LoadWelcomeMenuResponse loadWelcomeMenu(
			@WebParam(header = true, name = "headerAuth") Holder<Header> headerParam,
			@WebParam LoadWelcomeMenuRequest req);

}