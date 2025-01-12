import {API, Utils, Alerts} from "../util/utils.js";

const menuItems = document.querySelectorAll('.header__cart.user ul a');
// Lấy phần chứa nội dung
const contentContainer = document.querySelector('.user-content');

// Nội dung mẫu tương ứng với từng icon
const contentMap = {
    cart: '<p>Here are the details of your cart...</p>',
    shipping: '<p>Shipping details go here...</p>',
    favorites: '<p>Here are your favorite items...</p>',
    settings: '<p>Adjust your settings here...</p>',
};

document.querySelector('.header__cart.user ul').addEventListener('click', event => {
    const menuItem = event.target.closest('a[data-content]');
    if (!menuItem) return;

    event.preventDefault();
    const contentId = menuItem.getAttribute('data-content');
    renderContent(contentId);
});


function renderContent(contentId) {
    contentContainer.innerHTML = '';

    if (contentId === 'cart') {
        console.log('Redirecting to cart...');
        redirectToCart();
        return;
    }

    const newContent = contentMap[contentId] || '<p>Content not found.</p>';
    const contentElement = document.createElement('div');
    contentElement.innerHTML = newContent;
    contentContainer.appendChild(contentElement);
}


function redirectToCart() {
    window.location.href = API.urls.cart.base;
}

function getCountCart() {
    const url = `${API.PREFIX}${API.urls.cartCount}`;
    fetch(url, {
        method: 'GET',
        headers: Utils.getAuthHeaders(),
    })
        .then(response => {
            if (!response.ok) throw new Error('Cart count fetch failed');
            return response.json();
        })
        .then(response => {
            const quantityInCartElements = document.querySelectorAll('.quantity-in-cart');
            quantityInCartElements.forEach(element => {
                element.textContent = response.data || 0;
            });
        })
        .catch(error => {
            console.error(error);
            localStorage.clear();
            Utils.redirectToLogin();
        });
}

function getCountOrder() {
    const url = `${API.PREFIX}${API.urls.orderCount}`;
    fetch(url, {
        method: 'GET',
        headers: Utils.getAuthHeaders(),
    })
        .then(response => {
            if (!response.ok) throw new Error('Order count fetch failed');
            return response.json();
        })
        .then(response => {
            const quantityInOrderElements = document.querySelectorAll('.quantity-in-order');
            quantityInOrderElements.forEach(element => {
                element.textContent = response.data || 0;
            });
        })
        .catch(error => {
            console.error(error);
            localStorage.clear();
            Utils.redirectToLogin();
        });
}

document.addEventListener('DOMContentLoaded', () => {
    getCountCart();
});
