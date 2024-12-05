'use restrict';

(function ($) {
    const API_BASE_URL = '/shopping-cart';
    const CART_ITEMS_URL = `${API_BASE_URL}/items`;
    const UPDATE_ITEM_URL = `${API_BASE_URL}/update/{id}`;
    const REMOVE_ITEM_URL = `${API_BASE_URL}/remove/{id}`;

    $(document).ready(function() {
        loadCart();
    });

    function loadCart() {
        const token = localStorage.getItem('token');
        $.ajax({
            url: CART_ITEMS_URL,
            type: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            success: function(response) {
                renderCartItems(response.data);
            },
            error: function(xhr, status, error) {
                console.log("Error fetching cart items: ", error);
            }
        });
    }

    function renderCartItems(cartItems) {
        const cartTableBody = $('#cart-items');
        cartTableBody.empty();

        let subtotal = 0;

        cartItems.forEach(function(item) {
            const itemTotal = item.product.price * item.quantity;
            subtotal += itemTotal;

            const row = `
            <tr>
                <td class="shopping__cart__item">
                    <img src="${item.product.thumbnail}" alt="">
                    <h5>${item.product.name}</h5>
                </td>
                <td class="shopping__cart__price">$${item.product.price.toFixed(2)}</td>
                <td class="shopping__cart__quantity">
                    <div class="quantity">
                        <div class="pro-qty">
                            <input type="number" value="${item.quantity}" min="1" data-item-id="${item.id}" />
                        </div>
                    </div>
                </td>
                <td class="shopping__cart__total">$${itemTotal.toFixed(2)}</td>
                <td class="shopping__cart__item__close">
                    <span class="icon_close" data-item-id="${item.id}"></span>
                </td>
            </tr>
            `;
            cartTableBody.append(row);
        });

        const total = subtotal;
        $('#subtotal').text(`$${subtotal.toFixed(2)}`);
        $('#total').text(`$${total.toFixed(2)}`);

        attachEventListeners();
    }

    function attachEventListeners() {
        $(document).off('change', '.pro-qty input').on('change', '.pro-qty input', function() {
            const itemId = $(this).data('item-id');
            let newQuantity = parseInt($(this).val(), 10);
            if (newQuantity > 5 || newQuantity < 1) {
                newQuantity = 5;
                $(this).val(newQuantity);
                return;
            }

            updateCartItemQuantity(itemId, newQuantity);
        });

        $(document).off('click', '.icon_close').on('click', '.icon_close', function() {
            const itemId = $(this).data('item-id');
            removeCartItem(itemId);
        });
    }

    function updateCartItemQuantity(itemId, quantity) {


        const token = localStorage.getItem('token');

        $.ajax({
            url: UPDATE_ITEM_URL.replace('{id}', itemId),
            type: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            data: JSON.stringify({
                quantity: quantity
            }),
            contentType: 'application/json',
            success: function(response) {
                console.log(response.message);
                loadCart();
            },
            error: function(xhr, status, error) {
                console.log("Error updating cart item: ", error);
            }
        });
    }

    function removeCartItem(itemId) {
        const token = localStorage.getItem('token');
        $.ajax({
            url: REMOVE_ITEM_URL.replace('{id}', itemId),
            type: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            success: function(response) {
                console.log(response.message);
                loadCart();
            },
            error: function(xhr, status, error) {
                console.log("Error removing cart item: ", error);
            }
        });
    }
} (jQuery));
