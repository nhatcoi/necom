package com.example.ecommerceweb.controller;
import com.example.ecommerceweb.configuration.Translator;
import com.example.ecommerceweb.dto.wishlist.WishlistRequest;
import com.example.ecommerceweb.dto.response_data.ResponseData;
import com.example.ecommerceweb.service.services_impl.WishlistServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistServiceImpl wishlistService;

    private final Translator translator;

    @GetMapping("")
    public ResponseData<?> getWishlist() {
        return new ResponseData<>(
                HttpStatus.OK.value(),
                translator.toLocated("response.success"),
                wishlistService.getWishlistByUser()
        );
    }

    @PostMapping("/add")
    public ResponseData<?> addToWishlist(@RequestBody WishlistRequest request) {
        wishlistService.addToWishlist(request.getProductId());
        return new ResponseData<>(
                HttpStatus.OK.value(),
                translator.toLocated("response.success"),
                "Add product to wishlist successfully!"
        );
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseData<?>  removeFromWishlist(@PathVariable Long productId) {
        wishlistService.removeFromWishlist(productId);
        return new ResponseData<>(
                HttpStatus.OK.value(),
                translator.toLocated("response.success"),
                "Remove product from wishlist successfully!"
        );
    }
}

