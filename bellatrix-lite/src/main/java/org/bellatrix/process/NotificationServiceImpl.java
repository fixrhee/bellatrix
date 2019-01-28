package org.bellatrix.process;

import java.util.LinkedList;
import java.util.List;
import javax.xml.ws.Holder;
import org.bellatrix.data.Notifications;
import org.bellatrix.data.Status;
import org.bellatrix.data.StatusBuilder;
import org.bellatrix.data.TransactionException;
import org.bellatrix.services.Header;
import org.bellatrix.services.NotificationRequest;
import org.bellatrix.services.NotificationResponse;
import org.bellatrix.services.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationServiceImpl implements Notification {

	@Autowired
	private WebserviceValidation webserviceValidation;
	@Autowired
	private BaseRepository baseRepository;

	@Override
	public NotificationResponse loadNotifications(Holder<Header> headerParam, NotificationRequest req) {
		NotificationResponse notificationResponse = new NotificationResponse();
		try {
			webserviceValidation.validateWebservice(headerParam.value.getToken());
			if (req.getId() != null) {
				List<Notifications> lnotif = new LinkedList<Notifications>();
				Notifications notif = baseRepository.getNotificationsRepository().loadNotificationByID(req.getId());
				lnotif.add(notif);
				notificationResponse.setNotification(lnotif);
			} else {
				List<Notifications> lnotif = baseRepository.getNotificationsRepository()
						.loadAllNotification(req.getCurrentPage(), req.getPageSize());
				notificationResponse.setNotification(lnotif);
			}

			notificationResponse.setStatus(StatusBuilder.getStatus(Status.PROCESSED));
			return notificationResponse;
		} catch (TransactionException e) {
			e.printStackTrace();
			notificationResponse.setStatus(StatusBuilder.getStatus(e.getMessage()));
			return notificationResponse;
		}
	}

	@Override
	public void createNotifications(Holder<Header> headerParam, NotificationRequest req) throws TransactionException {
		webserviceValidation.validateWebservice(headerParam.value.getToken());
		baseRepository.getNotificationsRepository().createNotification(req);
	}

	@Override
	public void editNotifications(Holder<Header> headerParam, NotificationRequest req) throws TransactionException {
		webserviceValidation.validateWebservice(headerParam.value.getToken());
		baseRepository.getNotificationsRepository().editNotification(req);
	}

	@Override
	public void deleteNotifications(Holder<Header> headerParam, NotificationRequest req) throws TransactionException {
		webserviceValidation.validateWebservice(headerParam.value.getToken());
		baseRepository.getNotificationsRepository().deleteNotification(req);
	}

}
