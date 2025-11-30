package com.necom.controller.client;

import com.necom.constant.AppConstants;
import com.necom.constant.FieldName;
import com.necom.constant.ResourceName;
import com.necom.dto.ListResponse;
import com.necom.dto.general.EventInitiationResponse;
import com.necom.dto.general.NotificationRequest;
import com.necom.dto.general.NotificationResponse;
import com.necom.entity.general.Notification;
import com.necom.exception.ResourceNotFoundException;
import com.necom.mapper.general.NotificationMapper;
import com.necom.repository.general.NotificationRepository;
import com.necom.service.general.EmitterService;
import com.necom.service.general.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/client-api/notifications")
@AllArgsConstructor
@CrossOrigin(AppConstants.FRONTEND_HOST)
@Slf4j
public class ClientNotificationController {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final EmitterService emitterService;
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ListResponse<NotificationResponse>> getAllNotifications(
            Authentication authentication,
            @RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(name = "sort", defaultValue = AppConstants.DEFAULT_SORT) String sort,
            @RequestParam(name = "filter", required = false) @Nullable String filter
    ) {
        String username = authentication.getName();
        Page<Notification> notifications = notificationRepository.findAllByUsername(username, sort, filter, PageRequest.of(page - 1, size));
        List<NotificationResponse> notificationResponses = notifications.map(notificationMapper::entityToResponse).toList();
        return ResponseEntity.status(HttpStatus.OK).body(ListResponse.of(notificationResponses, notifications));
    }

    // Reference: https://stackoverflow.com/a/62749980
    @GetMapping("/init-events")
    public ResponseEntity<EventInitiationResponse> initNotificationEvents(Authentication authentication) {
        String username = authentication.getName();

        String eventSourceUuid;

        if (emitterService.isExistEmitterByUniqueKey(username)) {
            eventSourceUuid = emitterService.getEmitterUuidByUniqueKey(username);
        } else {
            eventSourceUuid = UUID.randomUUID().toString();
            emitterService.createEmitter(eventSourceUuid, username);
        }

        EventInitiationResponse eventInitiationResponse = new EventInitiationResponse(eventSourceUuid);
        return ResponseEntity.status(HttpStatus.OK).body(eventInitiationResponse);
    }

    @GetMapping("/events")
    public SseEmitter subscribeNotificationEvents(@RequestParam String eventSourceUuid) {
        return emitterService.getEmitterByUuid(eventSourceUuid);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationResponse> updateNotification(@PathVariable Long id,
                                                                   @RequestBody NotificationRequest request) {
        NotificationResponse notificationResponse = notificationRepository
                .findById(id)
                .map(existingEntity -> notificationMapper.partialUpdate(existingEntity, request))
                .map(notificationRepository::save)
                .map(notificationMapper::entityToResponse)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.NOTIFICATION, FieldName.ID, id));
        return ResponseEntity.status(HttpStatus.OK).body(notificationResponse);
    }

    @PostMapping("/push-events")
    public ResponseEntity<NotificationResponse> pushNotification(@RequestBody NotificationRequest request) {
        Notification notification = notificationRepository.save(notificationMapper.requestToEntity(request));
        NotificationResponse notificationResponse = notificationMapper.entityToResponse(notification);
        notificationService.pushNotification(notification.getUser().getUsername(), notificationResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationResponse);
    }

}
