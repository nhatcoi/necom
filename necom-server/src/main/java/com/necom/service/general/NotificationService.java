package com.necom.service.general;

import com.necom.dto.general.NotificationResponse;

public interface NotificationService {

    void pushNotification(String uniqueKey, NotificationResponse notification);

}
