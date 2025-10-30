package com.necom.service.promotion;

import com.necom.constant.FieldName;
import com.necom.constant.ResourceName;
import com.necom.constant.SearchFields;
import com.necom.dto.ListResponse;
import com.necom.dto.promotion.PromotionRequest;
import com.necom.dto.promotion.PromotionResponse;
import com.necom.entity.product.Product;
import com.necom.entity.promotion.Promotion;
import com.necom.exception.ResourceNotFoundException;
import com.necom.mapper.promotion.PromotionMapper;
import com.necom.repository.promotion.PromotionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private PromotionRepository promotionRepository;
    private PromotionMapper promotionMapper;

    @Override
    public ListResponse<PromotionResponse> findAll(int page, int size, String sort, String filter, String search, boolean all) {
        return defaultFindAll(page, size, sort, filter, search, all, SearchFields.PROMOTION, promotionRepository, promotionMapper);
    }

    @Override
    public PromotionResponse findById(Long id) {
        return defaultFindById(id, promotionRepository, promotionMapper, ResourceName.PROMOTION);
    }

    @Override
    public PromotionResponse save(PromotionRequest request) {
        Promotion promotion = promotionMapper.requestToEntity(request);

        if (promotion.getProducts().size() == 0) {
            throw new RuntimeException("Product list of promotion is empty");
        }

        for (Product product : promotion.getProducts()) {
            List<Promotion> promotions = promotionRepository
                    .findByProductId(product.getId(), promotion.getStartDate(), promotion.getEndDate());
            if (promotions.size() > 0) {
                throw new RuntimeException("Overlap promotion with product id: " + product.getId());
            }
        }

        return promotionMapper.entityToResponse(promotionRepository.save(promotion));
    }

    @Override
    public PromotionResponse save(Long id, PromotionRequest request) {
        Promotion promotion = promotionRepository.findById(id)
                .map(existingEntity -> promotionMapper.partialUpdate(existingEntity, request))
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.PROMOTION, FieldName.ID, id));

        if (promotion.getProducts().size() == 0) {
            throw new RuntimeException("Product list of promotion is empty");
        }

        return promotionMapper.entityToResponse(promotionRepository.save(promotion));
    }

    @Override
    public void delete(Long id) {
        promotionRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        promotionRepository.deleteAllById(ids);
    }

    @Override
    public boolean checkCanCreatePromotionForProduct(Long productId, Instant startDate, Instant endDate) {
        List<Promotion> promotions = promotionRepository.findByProductId(productId, startDate, endDate);
        return promotions.size() == 0;
    }

}
