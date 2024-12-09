'use restrict';

(function ($) {
    const CartAPI = {
        BASE_URL: App.API.urls.cart.base,
        endpoints: App.API.urls.cart,
        getAuthHeaders: () => App.Utils.getAuthHeaders(),
    };

    $(document).ready(() => {
        loadCart();
    });

    function loadCart() {
        $.ajax({
            url: CartAPI.endpoints.items,
            type: 'GET',
            headers: CartAPI.getAuthHeaders(),
            success: (response) => renderCartItems(response.data),
            error: App.Utils.handleError('Error fetching cart items'),
        });
    }

    function renderCartItems(cartItems) {
        const cartTableBody = $('#cart-items').empty();
        let subtotal = 0;

        cartItems.forEach((item) => {
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
                            <input type="number" value="${item.quantity}" min="1" max="5" data-item-id="${item.id}" />
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

        $('#subtotal').text(`$${subtotal.toFixed(2)}`);
        $('#total').text(`$${subtotal.toFixed(2)}`);

        attachEventListeners();
    }

    function attachEventListeners() {
        const cartItems = $('#cart-items');
        cartItems.off('change', '.pro-qty input').on('change', '.pro-qty input', function () {
            const itemId = $(this).data('item-id');
            let newQuantity = parseInt($(this).val(), 10);

            if (newQuantity < 1 || newQuantity > 5) {
                newQuantity = Math.max(1, Math.min(newQuantity, 5));
                $(this).val(newQuantity);
            }

            updateCartItemQuantity(itemId, newQuantity);
        });

        cartItems.off('click', '.icon_close').on('click', '.icon_close', function () {
            const itemId = $(this).data('item-id');
            removeCartItem(itemId);
        });
    }

    function updateCartItemQuantity(itemId, quantity) {
        $.ajax({
            url: CartAPI.endpoints.update.replace('{id}', itemId),
            type: 'PUT',
            headers: CartAPI.getAuthHeaders(),
            contentType: 'application/json',
            data: JSON.stringify({ quantity }),
            success: (response) => {
                App.Utils.handleSuccess(response.message);
                loadCart();
            },
            error: App.Utils.handleError('Error updating cart item'),
        });
    }

    function removeCartItem(itemId) {
        $.ajax({
            url: CartAPI.endpoints.remove.replace('{id}', itemId),
            type: 'DELETE',
            headers: CartAPI.getAuthHeaders(),
            success: (response) => {
                App.Utils.handleSuccess(response.message);
                loadCart();
            },
            error: App.Utils.handleError('Error removing cart item'),
        });
    }

} (jQuery));
