package com.necom.mapper.client;

import com.necom.dto.client.ClientWishRequest;
import com.necom.dto.client.ClientWishResponse;
import com.necom.entity.client.Wish;
import com.necom.repository.authentication.UserRepository;
import com.necom.repository.product.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@AllArgsConstructor
public class ClientWishMapper {

    private UserRepository userRepository;
    private ProductRepository productRepository;
    private ClientProductMapper clientProductMapper;

    public Wish requestToEntity(ClientWishRequest request) {
        Wish entity = new Wish();
        entity.setUser(userRepository.getById(request.getUserId()));
        entity.setProduct(productRepository.getById(request.getProductId()));
        return entity;
    }

    public ClientWishResponse entityToResponse(Wish entity) {
        ClientWishResponse response = new ClientWishResponse();
        response.setWishId(entity.getId());
        response.setWishCreatedAt(entity.getCreatedAt());
        // TODO: Triển khai `saleable` cho productResponse ở đây
        response.setWishProduct(clientProductMapper.entityToListedResponse(entity.getProduct(), Collections.emptyList()));
        return response;
    }

}
