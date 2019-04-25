package org.bellatrix.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BaseRepository {

	@Autowired
	private WebserviceRepository webServicesRepository;
	@Autowired
	private GroupRepository groupsRepository;
	@Autowired
	private MemberRepository membersRepository;
	@Autowired
	private AccessRepository accessesRepository;
	@Autowired
	private AccountRepository accountsRepository;
	@Autowired
	private CurrencyRepository currenciesRepository;
	@Autowired
	private TransferRepository transfersRepository;
	@Autowired
	private TransferTypeRepository transferTypesRepository;
	@Autowired
	private NotificationRepository notificationsRepository;
	@Autowired
	private GlobalConfigRepository globalConfigsRepository;
	@Autowired
	private MenuRepository menusRepository;
	@Autowired
	private MessageRepository messageRepository;

	public WebserviceRepository getWebServicesRepository() {
		return webServicesRepository;
	}

	public void setWebServicesRepository(WebserviceRepository webServicesRepository) {
		this.webServicesRepository = webServicesRepository;
	}

	public GroupRepository getGroupsRepository() {
		return groupsRepository;
	}

	public void setGroupsRepository(GroupRepository groupsRepository) {
		this.groupsRepository = groupsRepository;
	}

	public MemberRepository getMembersRepository() {
		return membersRepository;
	}

	public void setMembersRepository(MemberRepository membersRepository) {
		this.membersRepository = membersRepository;
	}

	public AccountRepository getAccountsRepository() {
		return accountsRepository;
	}

	public void setAccountsRepository(AccountRepository accountsRepository) {
		this.accountsRepository = accountsRepository;
	}

	public AccessRepository getAccessesRepository() {
		return accessesRepository;
	}

	public void setAccessesRepository(AccessRepository accessesRepository) {
		this.accessesRepository = accessesRepository;
	}

	public NotificationRepository getNotificationsRepository() {
		return notificationsRepository;
	}

	public void setNotificationsRepository(NotificationRepository notificationsRepository) {
		this.notificationsRepository = notificationsRepository;
	}

	public TransferRepository getTransfersRepository() {
		return transfersRepository;
	}

	public void setTransfersRepository(TransferRepository transfersRepository) {
		this.transfersRepository = transfersRepository;
	}

	public CurrencyRepository getCurrenciesRepository() {
		return currenciesRepository;
	}

	public void setCurrenciesRepository(CurrencyRepository currenciesRepository) {
		this.currenciesRepository = currenciesRepository;
	}

	public TransferTypeRepository getTransferTypesRepository() {
		return transferTypesRepository;
	}

	public void setTransferTypesRepository(TransferTypeRepository transferTypesRepository) {
		this.transferTypesRepository = transferTypesRepository;
	}

	public GlobalConfigRepository getGlobalConfigsRepository() {
		return globalConfigsRepository;
	}

	public void setGlobalConfigsRepository(GlobalConfigRepository globalConfigsRepository) {
		this.globalConfigsRepository = globalConfigsRepository;
	}

	public MenuRepository getMenusRepository() {
		return menusRepository;
	}

	public void setMenusRepository(MenuRepository menusRepository) {
		this.menusRepository = menusRepository;
	}

	public MessageRepository getMessageRepository() {
		return messageRepository;
	}

	public void setMessageRepository(MessageRepository messageRepository) {
		this.messageRepository = messageRepository;
	}

}
