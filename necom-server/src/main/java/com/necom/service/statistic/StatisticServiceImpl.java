package com.necom.service.statistic;

import com.necom.dto.statistic.StatisticResource;
import com.necom.dto.statistic.StatisticResponse;
import com.necom.repository.authentication.UserProjectionRepository;
import com.necom.repository.customer.CustomerRepository;
import com.necom.repository.order.OrderProjectionRepository;
import com.necom.repository.order.OrderRepository;
import com.necom.repository.product.BrandRepository;
import com.necom.repository.product.ProductRepository;
import com.necom.repository.product.SupplierRepository;
import com.necom.repository.promotion.PromotionRepository;
import com.necom.repository.review.ReviewProjectionRepository;
import com.necom.repository.review.ReviewRepository;
import com.necom.repository.waybill.WaybillProjectionRepository;
import com.necom.repository.waybill.WaybillRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    private CustomerRepository customerRepository;
    private ProductRepository productRepository;
    private OrderRepository orderRepository;
    private WaybillRepository waybillRepository;
    private PromotionRepository promotionRepository;
    private SupplierRepository supplierRepository;
    private BrandRepository brandRepository;
    private ReviewRepository reviewRepository;
    private UserProjectionRepository userProjectionRepository;
    private OrderProjectionRepository orderProjectionRepository;
    private WaybillProjectionRepository waybillProjectionRepository;
    private ReviewProjectionRepository reviewProjectionRepository;

    @Override
    public StatisticResponse getStatistic() {
        StatisticResponse statisticResponse = new StatisticResponse();

        // TODO: Nên dùng tên hàm `count` hợp lý hơn, như `countAll()`
        int totalCustomer = customerRepository.countByCustomerId();
        int totalProduct = productRepository.countByProductId();
        int totalOrder = orderRepository.countByOrderId();
        int totalWaybill = waybillRepository.countByWaybillId();
        int totalReview = reviewRepository.countByReviewId();
        int totalActivePromotion = promotionRepository.countByPromotionId();
        int totalSupplier = supplierRepository.countBySupplierId();
        int totalBrand = brandRepository.countByBrandId();

        List<StatisticResource> statisticRegistration = userProjectionRepository.getUserCountByCreateDate();
        List<StatisticResource> statisticOrder = orderProjectionRepository.getOrderCountByCreateDate();
        List<StatisticResource> statisticReview = reviewProjectionRepository.getReviewCountByCreateDate();
        List<StatisticResource> statisticWaybill = waybillProjectionRepository.getWaybillCountByCreateDate();

        statisticResponse.setTotalCustomer(totalCustomer);
        statisticResponse.setTotalProduct(totalProduct);
        statisticResponse.setTotalOrder(totalOrder);
        statisticResponse.setTotalWaybill(totalWaybill);
        statisticResponse.setTotalReview(totalReview);
        statisticResponse.setTotalActivePromotion(totalActivePromotion);
        statisticResponse.setTotalSupplier(totalSupplier);
        statisticResponse.setTotalBrand(totalBrand);
        statisticResponse.setStatisticRegistration(statisticRegistration);
        statisticResponse.setStatisticOrder(statisticOrder);
        statisticResponse.setStatisticReview(statisticReview);
        statisticResponse.setStatisticWaybill(statisticWaybill);

        return statisticResponse;
    }

}
