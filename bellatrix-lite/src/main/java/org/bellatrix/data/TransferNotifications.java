package org.bellatrix.data;

public class TransferNotifications {

	private Integer id;
	private Integer transferTypeID;
	private Integer notificationID;
	private String notificationName;
	private String notificationType;
	private String destinationModule;
	private boolean enabled;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getTransferTypeID() {
		return transferTypeID;
	}

	public void setTransferTypeID(Integer transferTypeID) {
		this.transferTypeID = transferTypeID;
	}

	public Integer getNotificationID() {
		return notificationID;
	}

	public void setNotificationID(Integer notificationID) {
		this.notificationID = notificationID;
	}

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getNotificationName() {
		return notificationName;
	}

	public void setNotificationName(String notificationName) {
		this.notificationName = notificationName;
	}

	public String getDestinationModule() {
		return destinationModule;
	}

	public void setDestinationModule(String destinationModule) {
		this.destinationModule = destinationModule;
	}

}
