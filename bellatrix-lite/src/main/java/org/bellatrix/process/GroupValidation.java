package org.bellatrix.process;

import java.util.List;

import org.bellatrix.data.Groups;
import org.bellatrix.data.Status;
import org.bellatrix.data.TransactionException;
import org.bellatrix.services.CreateGroupsRequest;
import org.bellatrix.services.LoadGroupsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

@Component
public class GroupValidation {

	@Value("${global.cache.config.enabled}")
	private boolean useCache;
	@Autowired
	private BaseRepository baseRepository;

	public Groups loadGroupsByID(Integer id) {
		return baseRepository.getGroupsRepository().loadGroupsByID(id);
	}

	public List<Groups> loadAllGroups(LoadGroupsRequest req) {
		return baseRepository.getGroupsRepository().loadAllGroups(req.getCurrentPage(), req.getPageSize());
	}

	public Integer getTotalRecords() {
		return baseRepository.getGroupsRepository().countTotalGroups();
	}

	public void validateCreateGroup(CreateGroupsRequest req) {
		baseRepository.getGroupsRepository().createGroups(req.getGroups());
	}

	public void validateGroupPermission(HazelcastInstance instance, Integer wsID, Integer groupID) throws TransactionException {
		boolean authenticate = false;

		if (useCache) {
			IMap<String, Boolean> gpMap = instance.getMap("GroupPermissionMap");
			String gpID = String.valueOf(wsID) + ":" + String.valueOf(groupID);
			authenticate = gpMap.get(gpID);
		} else {
			authenticate = baseRepository.getWebServicesRepository().validateGroupAccessToWebService(wsID, groupID);
		}

		if (!authenticate) {
			throw new TransactionException(String.valueOf(Status.SERVICE_NOT_ALLOWED));
		}
	}
}
