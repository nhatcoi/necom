package com.necom.service.review;

import com.necom.constant.FieldName;
import com.necom.constant.ResourceName;
import com.necom.constant.SearchFields;
import com.necom.dto.ListResponse;
import com.necom.dto.review.ReviewRequest;
import com.necom.dto.review.ReviewResponse;
import com.necom.entity.review.Review;
import com.necom.exception.ResourceNotFoundException;
import com.necom.mapper.review.ReviewMapper;
import com.necom.repository.review.ReviewRepository;
import com.necom.utils.RewardUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private ReviewRepository reviewRepository;
    private ReviewMapper reviewMapper;
    private RewardUtils rewardUtils;

    @Override
    public ListResponse<ReviewResponse> findAll(int page, int size, String sort, String filter, String search, boolean all) {
        return defaultFindAll(page, size, sort, filter, search, all, SearchFields.REVIEW, reviewRepository, reviewMapper);
    }

    @Override
    public ReviewResponse findById(Long id) {
        return defaultFindById(id, reviewRepository, reviewMapper, ResourceName.REVIEW);
    }

    @Override
    public ReviewResponse save(ReviewRequest request) {
        return defaultSave(request, reviewRepository, reviewMapper);
    }

    @Override
    public ReviewResponse save(Long id, ReviewRequest request) {
        Review review = reviewRepository.findById(id)
                .map(existingEntity -> reviewMapper.partialUpdate(existingEntity, request))
                .map(reviewRepository::save)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.DOCKET, FieldName.ID, id));

        rewardUtils.approveReviewHook(review);

        return reviewMapper.entityToResponse(review);
    }

    @Override
    public void delete(Long id) {
        reviewRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        reviewRepository.deleteAllById(ids);
    }

}
