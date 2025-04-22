package com.example.ecommerceweb.service.services_impl;

import com.example.ecommerceweb.dto.cart.CartRequest;
import com.example.ecommerceweb.dto.cart.CartResponse;
import com.example.ecommerceweb.dto.product.ProductDetailResponse;
import com.example.ecommerceweb.entity.Cart;
import com.example.ecommerceweb.entity.CartItem;
import com.example.ecommerceweb.entity.CartItemAttribute;
import com.example.ecommerceweb.entity.User;
import com.example.ecommerceweb.entity.product.Product;
import com.example.ecommerceweb.entity.product.ProductAttribute;
import com.example.ecommerceweb.exception.ErrorCode;
import com.example.ecommerceweb.exception.ResourceException;
import com.example.ecommerceweb.mapper.ProductMapper;
import com.example.ecommerceweb.repository.*;
import com.example.ecommerceweb.security.SecurityUtils;
import com.example.ecommerceweb.service.CartService;
import com.example.ecommerceweb.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final ProductMapper productMapper;
    private final ProductAttributeRepository productAttributeRepository;
    private final CartItemAttributeRepository cartItemAttributeRepository;
    private final UserService userService;

    @Override
    public List<CartResponse> getCartItems() {
        User user = securityUtils.getCurrentUser();
        List<CartItem> cartItems = cartItemRepository.findCartItemsByUserId(user.getId());

        return cartItems.stream().map(this::convertToCartResponse).collect(Collectors.toList());
    }

    private CartResponse convertToCartResponse(CartItem cartItem) {
        CartResponse response = new CartResponse();
        response.setId(cartItem.getId());
        response.setQuantity(cartItem.getQuantity());
        response.setPrice(cartItem.getPrice());

        // set product details
        ProductDetailResponse productDetail = productMapper.productToProductDetailResponse(cartItem.getProduct());
        response.setProduct(productDetail);

        // set selected attributes
        List<CartResponse.SelectedAttributeDTO> selectedAttributes = cartItem.getAttributes().stream().map(attr -> {
            CartResponse.SelectedAttributeDTO dto = new CartResponse.SelectedAttributeDTO();
            dto.setId(attr.getProductAttribute().getId());
            dto.setAttributeName(attr.getProductAttribute().getAttributeValue().getAttribute().getName());
            dto.setAttributeValue(attr.getProductAttribute().getAttributeValue().getValue());
            dto.setPrice(attr.getProductAttribute().getPrice());
            dto.setQuantity(attr.getProductAttribute().getStockQuantity());
            return dto;
        }).collect(Collectors.toList());

        response.setSelectedAttributes(selectedAttributes);



        response.setTotalPrice(cartItem.getPrice());

        return response;
    }


    @Override
    @Transactional
    public CartResponse createCartItem(CartRequest cartRequest) {
        User user = securityUtils.getCurrentUser();

        // Lấy Cart của user (hoặc tạo mới nếu chưa có)
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        // Lấy sản phẩm từ DB
        Product product = productRepository.findById(cartRequest.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Lấy danh sách ProductAttributes
        List<ProductAttribute> attributes = productAttributeRepository.findAllById(cartRequest.getSelectedAttributeId());


        // Tính giá dựa trên sản phẩm + thuộc tính
        float totalPrice = product.getPrice();
        for (ProductAttribute attr : attributes) {
            totalPrice += attr.getPrice();
        }


        // Kiểm tra sản phẩm đã có trong giỏ hàng chưa
        List<CartItem> existingCartItems = cartItemRepository.findByCartAndProduct(cart.getId(), product.getId());

        boolean check = false;
        if (existingCartItems != null && !existingCartItems.isEmpty()) {
            for (CartItem cartItem : existingCartItems) {
                List<Long> cartItemAttributeIds = cartItem.getAttributes()
                        .stream()
                        .map(a -> a.getProductAttribute().getId())
                        .toList();

                List<Long> requestAttributeIds = attributes
                        .stream()
                        .map(ProductAttribute::getId)
                        .toList();

                // Kiểm tra nếu danh sách thuộc tính của CartItem và request giống hệt nhau
                if (new HashSet<>(cartItemAttributeIds).containsAll(requestAttributeIds) && new HashSet<>(requestAttributeIds).containsAll(cartItemAttributeIds)) {
                    check = true;
                    cartItem.setQuantity(cartItem.getQuantity() + cartRequest.getQuantity());
                    cartItem.setPrice(totalPrice);
                    cartItemRepository.save(cartItem);
                    return convertToCartResponse(cartItem);
                }
            }
        }

//        // Nếu sản phẩm đã tồn tại trong giỏ hàng với thuộc tính khác, cập nhật số lượng và giá
//        assert existingCartItems != null;
//        if (!existingCartItems.isEmpty() && check) {
//            for (CartItem cartItem : existingCartItems) {
//
//            }
//            return convertToCartResponse(existingCartItems.getFirst()); // Trả về item đầu tiên (hoặc cần logic xử lý phù hợp)
//        }

        // Tạo mới CartItem
        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(cartRequest.getQuantity())
                .price(totalPrice)
                .build();

        // Lưu CartItem trước để có ID
        cartItem = cartItemRepository.save(cartItem);

        // Thêm thuộc tính vào CartItem
        CartItem finalCartItem = cartItem;
        List<CartItemAttribute> cartItemAttributes = attributes.stream()
                .map(attr -> {
                    CartItemAttribute cartItemAttribute = new CartItemAttribute();
                    cartItemAttribute.setCartItem(finalCartItem);
                    cartItemAttribute.setProductAttribute(attr);
                    return cartItemAttribute;
                })
                .collect(Collectors.toList());

        cartItemAttributeRepository.saveAll(cartItemAttributes);

        // Cập nhật danh sách thuộc tính vào CartItem
        cartItem.setAttributes(cartItemAttributes);

        return convertToCartResponse(cartItem);
    }

    @Override
    public void removeItem(Long id) {
        User user = securityUtils.getCurrentUser();

        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new ResourceException(ErrorCode.RESOURCE_NOT_FOUND, "Cart item not found"));

        if (user.getId().equals(cartItem.getCart().getUser().getId())) {
            cartItemRepository.delete(cartItem);
        } else {
            throw new ResourceException(ErrorCode.UNAUTHORIZED, "You are not authorized to remove this item");
        }
    }


    @Override
    public Integer getTotalInCart() {
        return 0;
    }



    @Override
    public void updateCartItem(Long id, Integer quantity) {

    }

    @Override
    public void removeItems(String username, List<Long> productIds) {
        User user = userService.getUserByUsername(username);

        // Kiểm tra nếu danh sách productIds rỗng
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("Product IDs cannot be empty");
        }

        // Tìm giỏ hàng của người dùng
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceException(ErrorCode.RESOURCE_NOT_FOUND, "Cart not found"));

        // Lọc các sản phẩm cần xóa
        List<CartItem> itemsToRemove = cart.getItems().stream()
                .filter(item -> productIds.contains(item.getProduct().getId()))
                .toList();

        if (itemsToRemove.isEmpty()) {
            throw new ResourceException(ErrorCode.RESOURCE_NOT_FOUND, "No items found to remove");
        }

        // Xóa sản phẩm khỏi giỏ hàng
        cart.getItems().removeAll(itemsToRemove);
        cartRepository.save(cart);
    }

    @Override
    public void clearCart() {
        User user = securityUtils.getCurrentUser();
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceException(ErrorCode.RESOURCE_NOT_FOUND, "Cart not found"));
        if (user.getId().equals(cart.getUser().getId())) {
            cartRepository.deleteById(cart.getId());
        } else {
            throw new ResourceException(ErrorCode.UNAUTHORIZED, "You are not authorized to clear this cart");
        }
    }


//    private final CartRepository cartRepository;
//    private final UserRepository userRepository;
//    private final ProductRepository productRepository;
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    @Override
//    public List<Cart> getCartItems() {
//        String username = getAuthenticatedUsername();
//        return cartRepository.findByUserName(username);
//    }
//
//    @Override
//    public Integer getTotalInCart() {
//        String username = getAuthenticatedUsername();
//        return cartRepository.countByUsername(username) != null ? cartRepository.countByUsername(username) : 0;
//    }
//
//    @Override
//    public void removeItem(Long id) {
//        Cart cart = findCartById(id);
//        cartRepository.delete(cart);
//    }
//
//    @Override
//    public void updateCartItem(Long id, Integer quantity) {
//        Cart cart = findCartById(id);
//        cart.setQuantity(quantity);
//        cartRepository.save(cart);
//    }
//
//    @Override
//    public Integer createCartItem(Long productId) {
//        String username = getAuthenticatedUsername();
//        Cart cart = findCartByUsernameAndProductId(username, productId);
//
//        if (cart != null) {
//            updateCartItemQuantity(cart);
//        } else {
//            createNewCartItem(username, productId);
//        }
//
//        return cartRepository.countByUsername(username) != null ? cartRepository.countByUsername(username) : 0;
//    }
//
//    @Override
//    public void removeItems(String username, List<Long> productIds) {
//        Long userId = userRepository.findIdByUsername(username);
//        cartRepository.deleteByUserIdAndProductId(userId, productIds);
//    }
//
//    private String getAuthenticatedUsername() {
//        var authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated()) {
//            throw new ResourceException(ErrorCode.UNAUTHENTICATED);
//        }
//        return authentication.getName();
//    }
//
//    private Cart findCartById(Long id) {
//        return cartRepository.findById(id)
//                .orElseThrow(() -> new ResourceException(ErrorCode.CART_NOT_FOUND));
//    }
//
//    private Cart findCartByUsernameAndProductId(String username, Long productId) {
//        return cartRepository.findByUsernameAndProductId(username, productId)
//                .orElse(null);
//    }
//
//    private void updateCartItemQuantity(Cart cart) {
//        if (cart.getQuantity() < 5) {
//            cart.setQuantity(cart.getQuantity() + 1);
//            cartRepository.save(cart);
//        } else {
//            throw new ResourceException(ErrorCode.CART_QUANTITY_LIMIT);
//        }
//    }
//
//    private void createNewCartItem(String username, Long productId) {
//        var user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new ResourceException(ErrorCode.USER_NOT_EXISTED));
//
//        var product = productRepository.findById(productId)
//                .orElseThrow(() -> new ResourceException(ErrorCode.PRODUCT_NOT_EXISTED));
//
//        Cart cartItem = Cart.builder()
//                .user(user)
//                .product(product)
//                .quantity(1)
//                .build();
//
//        cartRepository.save(cartItem);
//    }
}
