package com.necom.service.review;

import com.necom.dto.review.ReviewRequest;
import com.necom.dto.review.ReviewResponse;
import com.necom.service.CrudService;

public interface ReviewService extends CrudService<Long, ReviewRequest, ReviewResponse> {}
