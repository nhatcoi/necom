package com.necom.controller.client;

import com.necom.constant.AppConstants;
import com.necom.dto.CollectionWrapper;
import com.necom.dto.cashbook.ClientPaymentMethodResponse;
import com.necom.mapper.cashbook.PaymentMethodMapper;
import com.necom.repository.cashbook.PaymentMethodRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/client-api/payment-methods")
@AllArgsConstructor
@CrossOrigin(AppConstants.FRONTEND_HOST)
public class ClientPaymentMethodController {

    private PaymentMethodRepository paymentMethodRepository;
    private PaymentMethodMapper paymentMethodMapper;

    @GetMapping
    public ResponseEntity<CollectionWrapper<ClientPaymentMethodResponse>> getAllPaymentMethods() {
        List<ClientPaymentMethodResponse> clientPaymentMethodResponses = paymentMethodMapper
                .entityToClientResponse(paymentMethodRepository.findAllByStatus(1));
        return ResponseEntity.status(HttpStatus.OK).body(CollectionWrapper.of(clientPaymentMethodResponses));
    }

}
