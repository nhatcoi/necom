package com.necom.service.inventory;

import com.necom.constant.FieldName;
import com.necom.constant.ResourceName;
import com.necom.constant.SearchFields;
import com.necom.dto.ListResponse;
import com.necom.dto.inventory.DocketRequest;
import com.necom.dto.inventory.DocketResponse;
import com.necom.entity.client.Preorder;
import com.necom.entity.general.Notification;
import com.necom.entity.general.NotificationType;
import com.necom.entity.inventory.Docket;
import com.necom.exception.ResourceNotFoundException;
import com.necom.mapper.general.NotificationMapper;
import com.necom.mapper.inventory.DocketMapper;
import com.necom.repository.client.PreorderRepository;
import com.necom.repository.general.NotificationRepository;
import com.necom.repository.inventory.DocketRepository;
import com.necom.service.general.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class DocketServiceImpl implements DocketService {

    private DocketRepository docketRepository;

    private DocketMapper docketMapper;

    private PreorderRepository preorderRepository;

    private NotificationRepository notificationRepository;

    private NotificationService notificationService;

    private NotificationMapper notificationMapper;

    @Override
    public ListResponse<DocketResponse> findAll(int page, int size, String sort, String filter, String search, boolean all) {
        return defaultFindAll(page, size, sort, filter, search, all, SearchFields.DOCKET, docketRepository, docketMapper);
    }

    @Override
    public DocketResponse findById(Long id) {
        return defaultFindById(id, docketRepository, docketMapper, ResourceName.DOCKET);
    }

    @Override
    public DocketResponse save(DocketRequest request) {
        Docket docket = docketRepository.save(docketMapper.requestToEntity(request));
        afterCreateOrUpdateCallback(docket);
        return docketMapper.entityToResponse(docket);
    }

    @Override
    public DocketResponse save(Long id, DocketRequest request) {
        Docket docket = docketRepository.findById(id)
                .map(existingEntity -> docketMapper.partialUpdate(existingEntity, request))
                .map(docketRepository::save)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.DOCKET, FieldName.ID, id));
        afterCreateOrUpdateCallback(docket);
        return docketMapper.entityToResponse(docket);
    }

    private void afterCreateOrUpdateCallback(Docket docket) {
        // Docket nhập (1) có trạng thái Hoàn thành (3)
        if (docket.getType().equals(1) && docket.getStatus().equals(3)) {
            List<Long> productIds = docket.getDocketVariants().stream()
                    .map(docketVariant -> docketVariant.getVariant().getProduct().getId())
                    .distinct()
                    .collect(Collectors.toList());

            List<Preorder> preorders = preorderRepository.findByProduct_IdInAndStatus(productIds, 1);

            List<Notification> notifications = preorders.stream()
                    .map(preorder -> new Notification()
                            .setUser(preorder.getUser())
                            .setType(NotificationType.PREORDER)
                            .setMessage(String.format("Sản phẩm %s đã có hàng. Vui lòng kiểm tra.", preorder.getProduct().getName()))
                            .setAnchor("/product/" + preorder.getProduct().getSlug())
                            .setStatus(1))
                    .collect(Collectors.toList());

            notificationRepository.saveAll(notifications);

            notifications.forEach(notification -> notificationService
                    .pushNotification(notification.getUser().getUsername(), notificationMapper.entityToResponse(notification)));

            preorders.forEach(preorder -> {
                preorder.setUpdatedAt(Instant.now());
                preorder.setStatus(2);
            });

            preorderRepository.saveAll(preorders);

            List<String> usernames = notifications.stream()
                    .map(notification -> notification.getUser().getUsername())
                    .collect(Collectors.toList());

            log.info("Push notifications for users: " + usernames);
        }
    }

    @Override
    public void delete(Long id) {
        docketRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        docketRepository.deleteAllById(ids);
    }

}
