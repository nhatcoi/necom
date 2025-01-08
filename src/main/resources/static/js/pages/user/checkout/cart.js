'use strict';

import {API, Utils, Alerts} from "../../util/utils.js";

// Cart API Module
const CartAPI = {
    BASE_URL: API.urls.cart.base,
    endpoints: API.urls.cart,
    getAuthHeaders: () => Utils.getAuthHeaders(),
    async fetchCartItems() {
        try {
            const response = await fetch(this.endpoints.items, {
                method: 'GET',
                headers: this.getAuthHeaders(),
            });
            if (!response.ok) throw new Error('Failed to fetch cart items');
            return await response.json();
        } catch (e) {
            Alerts.handleError('Error fetching cart items', e.message);
        }
    },

    async updateCartItemQuantity(itemId, quantity) {
        try {
            const response = await fetch(this.endpoints.update.replace('{id}', itemId), {
                method: 'PUT',
                headers: {
                    ...this.getAuthHeaders(),
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ quantity }),
            });
            if (!response.ok) throw new Error('Failed to update cart item');
            return await response.json();
        } catch (error) {
            Alerts.handleError('Error updating cart item', error.message);
        }
    },

    async removeCartItem(itemId) {
        try {
            const response = await fetch(this.endpoints.remove.replace('{id}', itemId), {
                method: 'DELETE',
                headers: this.getAuthHeaders(),
            });
            console.log(this.endpoints.remove.replace('{id}', itemId));
            if (!response.ok) throw new Error('Failed to remove cart item');
            return await response.json();
        } catch (error) {
            Alerts.handleError('Error removing cart item', error.message);
        }
    },
};

// UI Rendering
async function loadCart() {
    const cartItems = await CartAPI.fetchCartItems();
    if (!cartItems || !cartItems.data) return;
    renderCartItems(cartItems.data);
}

function renderCartItems(cartItems) {
    const cartTableBody = document.querySelector('#cart-items');
    cartTableBody.innerHTML = ''; // Clear existing items
    let subtotal = 0;

    cartItems.forEach((item) => {
        const itemTotal = item.product.price * item.quantity;
        subtotal += itemTotal;

        const row = document.createElement('tr');
        row.innerHTML = `
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
        `;
        cartTableBody.appendChild(row);
    });

    document.querySelector('#subtotal').textContent = `$${subtotal.toFixed(2)}`;
    document.querySelector('#total').textContent = `$${subtotal.toFixed(2)}`;

    attachEventListeners();
}

function attachEventListeners() {
    const cartTableBody = document.querySelector('#cart-items');

    // Kiểm tra nếu listener đã được gắn, tránh việc gắn nhiều lần
    cartTableBody.removeEventListener('change', onQuantityChange);
    cartTableBody.removeEventListener('click', onRemoveItemClick);

    cartTableBody.addEventListener('change', onQuantityChange);
    cartTableBody.addEventListener('click', onRemoveItemClick);
}

async function onQuantityChange(event) {
    const input = event.target;
    if (input.tagName === 'INPUT' && input.type === 'number') {
        const itemId = input.dataset.itemId;
        let newQuantity = parseInt(input.value, 10);

        if (newQuantity < 1 || newQuantity > 5) {
            newQuantity = Math.max(1, Math.min(newQuantity, 5));
            input.value = newQuantity;
        }

        await CartAPI.updateCartItemQuantity(itemId, newQuantity);
        await loadCart();
    }
}

async function onRemoveItemClick(event) {
    const closeIcon = event.target;
    if (closeIcon.classList.contains('icon_close')) {
        const itemId = closeIcon.dataset.itemId;
        const removeItem = await CartAPI.removeCartItem(itemId);
        await loadCart();
        Alerts.handleSuccessTimeCenter(removeItem.message);
    }
}


// Initialize
document.addEventListener('DOMContentLoaded', loadCart);
