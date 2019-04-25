package org.bellatrix.process;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.xml.ws.Holder;

import org.bellatrix.data.CategoryMenuData;
import org.bellatrix.data.ChildMenu;
import org.bellatrix.data.Groups;
import org.bellatrix.data.MainMenu;
import org.bellatrix.data.ParentMenu;
import org.bellatrix.data.ParentMenuData;
import org.bellatrix.data.Status;
import org.bellatrix.data.StatusBuilder;
import org.bellatrix.data.TransactionException;
import org.bellatrix.data.WelcomeMenu;
import org.bellatrix.services.Header;
import org.bellatrix.services.LoadCategoryMenuRequest;
import org.bellatrix.services.LoadCategoryMenuResponse;
import org.bellatrix.services.LoadChildMenuRequest;
import org.bellatrix.services.LoadChildMenuResponse;
import org.bellatrix.services.LoadMenuByGroupsRequest;
import org.bellatrix.services.LoadMenuByGroupsResponse;
import org.bellatrix.services.LoadParentMenuRequest;
import org.bellatrix.services.LoadParentMenuResponse;
import org.bellatrix.services.LoadWelcomeMenuRequest;
import org.bellatrix.services.LoadWelcomeMenuResponse;
import org.bellatrix.services.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MenuServiceImpl implements Menu {

	@Autowired
	private WebserviceValidation webserviceValidation;
	@Autowired
	private BaseRepository baseRepository;

	@Override
	public LoadMenuByGroupsResponse loadMenuByGroups(Holder<Header> headerParam, LoadMenuByGroupsRequest req) {
		LoadMenuByGroupsResponse loadMenuByGroupsResponse = new LoadMenuByGroupsResponse();
		try {
			webserviceValidation.validateWebservice(headerParam.value.getToken());
			Groups groups = baseRepository.getGroupsRepository().loadGroupsByID(req.getGroupID());
			if (groups == null) {
				loadMenuByGroupsResponse.setRepsonse(StatusBuilder.getStatus(Status.INVALID_GROUP));
				return loadMenuByGroupsResponse;
			}

			List<ParentMenuData> parentList = baseRepository.getMenusRepository()
					.loadParentMenuByGroupID(req.getGroupID());

			List<Integer> parentIDs = new LinkedList<Integer>();
			for (ParentMenuData p : parentList) {
				parentIDs.add(p.getId());
			}

			if (parentIDs.size() != 0) {
				List<ChildMenu> childList = baseRepository.getMenusRepository().loadChildMenuByParentID(parentIDs);

				Map<Integer, List<ChildMenu>> childMap = childList.stream()
						.collect(Collectors.groupingBy(ChildMenu::getParentMenuID, Collectors.toList()));

				Map<String, List<ParentMenuData>> mainMenuMap = parentList.stream()
						.collect(Collectors.groupingBy(ParentMenuData::getMainMenuName, Collectors.toList()));

				Map<String, List<ParentMenuData>> mainMap = new TreeMap<String, List<ParentMenuData>>();
				mainMap.putAll(mainMenuMap);

				List<MainMenu> listMainMenu = new LinkedList<MainMenu>();
				for (Map.Entry<String, List<ParentMenuData>> entry : mainMap.entrySet()) {
					MainMenu mainMenu = new MainMenu();
					mainMenu.setMainMenuName(entry.getKey());
					List<ParentMenu> listParentMenu = new LinkedList<ParentMenu>();
					for (int i = 0; i < entry.getValue().size(); i++) {
						ParentMenu parentMenu = new ParentMenu();
						parentMenu.setIcon(entry.getValue().get(i).getIcon());
						parentMenu.setParentMenuName(entry.getValue().get(i).getParentMenuName());
						parentMenu.setSequenceNo(entry.getValue().get(i).getSequenceNo());
						parentMenu.setId(entry.getValue().get(i).getId());
						parentMenu.setChildMenu(childMap.get(entry.getValue().get(i).getId()));
						parentMenu.setMainMenuid(entry.getValue().get(i).getMainMenuID());
						listParentMenu.add(parentMenu);
					}
					mainMenu.setParentMenu(listParentMenu);
					listMainMenu.add(mainMenu);
				}

				loadMenuByGroupsResponse.setMainMenu(listMainMenu);
				loadMenuByGroupsResponse.setWelcomeMenu(
						baseRepository.getMenusRepository().loadWelcomeMenuLinkByGroup(groups.getId()).getLink());
				loadMenuByGroupsResponse.setRepsonse(StatusBuilder.getStatus(Status.PROCESSED));
			}

			loadMenuByGroupsResponse.setRepsonse(StatusBuilder.getStatus(Status.PROCESSED));

		} catch (TransactionException ex) {
			loadMenuByGroupsResponse.setRepsonse(StatusBuilder.getStatus(ex.getMessage()));
			return loadMenuByGroupsResponse;
		}
		return loadMenuByGroupsResponse;
	}

	@Override
	public LoadParentMenuResponse loadParentMenu(Holder<Header> headerParam, LoadParentMenuRequest req) {
		LoadParentMenuResponse lpmr = new LoadParentMenuResponse();
		try {
			webserviceValidation.validateWebservice(headerParam.value.getToken());
			if (req.getId() != null) {
				List<ParentMenuData> lpm = baseRepository.getMenusRepository().loadParentMenuByID(req.getId());
				lpmr.setParentMenu(lpm);
				lpmr.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
				return lpmr;
			}

			if (req.getGroupID() != null) {
				Groups groups = baseRepository.getGroupsRepository().loadGroupsByID(req.getGroupID());
				if (groups == null) {
					lpmr.setStatus(StatusBuilder.getStatus(Status.INVALID_GROUP));
					return lpmr;
				} else {
					List<ParentMenuData> lpm = baseRepository.getMenusRepository()
							.loadParentMenuByGroupID(req.getGroupID());
					lpmr.setParentMenu(lpm);
					lpmr.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
					return lpmr;
				}
			}

			if (req.getCategoryID() != null) {
				List<ParentMenuData> lpm = baseRepository.getMenusRepository()
						.loadParentMenuByCategoryID(req.getCategoryID());
				lpmr.setParentMenu(lpm);
				lpmr.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
				return lpmr;
			}

			if (req.getCategoryID() == null && req.getGroupID() == null) {
				List<ParentMenuData> lpm = baseRepository.getMenusRepository().loadParentMenu();
				lpmr.setParentMenu(lpm);
				lpmr.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
				return lpmr;
			}

			lpmr.setStatus(StatusBuilder.getStatus(Status.INVALID_PARAMETER));
			return lpmr;

		} catch (TransactionException ex) {
			lpmr.setStatus(StatusBuilder.getStatus(ex.getMessage()));
			return lpmr;
		}
	}

	@Override
	public LoadCategoryMenuResponse loadCategoryMenu(Holder<Header> headerParam, LoadCategoryMenuRequest req) {
		LoadCategoryMenuResponse lcmr = new LoadCategoryMenuResponse();
		try {
			webserviceValidation.validateWebservice(headerParam.value.getToken());
			if (req.getId() != null) {
				List<CategoryMenuData> cmd = baseRepository.getMenusRepository().loadCategoryMenuByID(req.getId());
				lcmr.setCategoryMenu(cmd);
				lcmr.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
				return lcmr;
			} else {
				List<CategoryMenuData> cmd = baseRepository.getMenusRepository().loadCategoryMenu();
				lcmr.setCategoryMenu(cmd);
				lcmr.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
				return lcmr;
			}
		} catch (TransactionException ex) {
			lcmr.setStatus(StatusBuilder.getStatus(ex.getMessage()));
			return lcmr;
		}
	}

	@Override
	public LoadChildMenuResponse loadChildMenu(Holder<Header> headerParam, LoadChildMenuRequest req) {
		LoadChildMenuResponse lcmr = new LoadChildMenuResponse();
		try {
			webserviceValidation.validateWebservice(headerParam.value.getToken());
			if (req.getId() != null) {
				List<ChildMenu> lcm = baseRepository.getMenusRepository().loadChildMenuByID(req.getId());
				lcmr.setChildMenu(lcm);
				lcmr.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
				return lcmr;
			} else if (req.getParentID() != null) {
				List<ChildMenu> lcm = baseRepository.getMenusRepository().loadChildMenuByParentID(req.getParentID());
				lcmr.setChildMenu(lcm);
				lcmr.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
				return lcmr;
			} else {
				List<ChildMenu> lcm = baseRepository.getMenusRepository().loadChildMenu();
				lcmr.setChildMenu(lcm);
				lcmr.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
				return lcmr;
			}
		} catch (TransactionException ex) {
			lcmr.setStatus(StatusBuilder.getStatus(ex.getMessage()));
			return lcmr;
		}
	}

	@Override
	public LoadWelcomeMenuResponse loadWelcomeMenu(Holder<Header> headerParam, LoadWelcomeMenuRequest req) {
		LoadWelcomeMenuResponse lwmr = new LoadWelcomeMenuResponse();
		try {
			webserviceValidation.validateWebservice(headerParam.value.getToken());
			if (req.getId() != null) {
				WelcomeMenu link = baseRepository.getMenusRepository().loadWelcomeMenuLinkByID(req.getId());
				List<WelcomeMenu> ls = new LinkedList<WelcomeMenu>();
				ls.add(link);
				lwmr.setWelcomeLink(ls);
				lwmr.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
				return lwmr;
			} else if (req.getGroupID() != null) {
				WelcomeMenu link = baseRepository.getMenusRepository().loadWelcomeMenuLinkByGroup(req.getGroupID());
				List<WelcomeMenu> ls = new LinkedList<WelcomeMenu>();
				ls.add(link);
				lwmr.setWelcomeLink(ls);
				lwmr.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
				return lwmr;
			} else {
				List<WelcomeMenu> link = baseRepository.getMenusRepository().loadWelcomeMenuLink();
				lwmr.setWelcomeLink(link);
				lwmr.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
				return lwmr;
			}
		} catch (TransactionException ex) {
			lwmr.setStatus(StatusBuilder.getStatus(ex.getMessage()));
			return lwmr;
		}
	}

}
