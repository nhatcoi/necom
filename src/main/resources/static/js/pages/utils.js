'use strict';


// API Configuration

export const API = {
    PREFIX: '',
    urls: {
        auth: {
            logIn: '/auth/log-in',
            register: '/users/create-user',
        },
        myInfo: '/users/my-info',
        cartCount: '/shopping-cart/count',
        loginForm: '/login-form',
        categories: '/categories',
        products: '/products',
        recentBlogs: '/blog/recent-news',
        addToCart: '/shopping-cart/add-to-cart/',
        shopGrid: {
            filterByPrice: '/shop-grid/filterByPrice',
        },
        cart: {
            base: '/shopping-cart',
            items: '/shopping-cart/items',
            update: '/shopping-cart/update/{id}',
            remove: '/shopping-cart/remove/{id}',
        },
        checkout: '/checkout/checkout-list',
    },
};

// Utility Functions
export const Utils = {
    async addToCartHandler(productId) {
        if (!productId) {
            console.error('Product ID is missing!');
            return;
        }

        try {
            const response = await fetch(`${API.PREFIX}${API.urls.addToCart}${productId}`, {
                method: 'POST',
                headers: this.getAuthHeaders(),
            });

            if (!response.ok) throw new Error('Failed to add to cart');

            const data = await response.json();
            document.querySelector('.amount-cart span').textContent = data.data;
            Alerts.handleSuccess('Good job!', data.message);
        } catch (error) {
            const errorMessage = error.message || 'Not Authenticated, Please Login!';
            Alerts.handleError('Oops', errorMessage);
        }
    },

    getAuthHeaders() {
        const token = localStorage.getItem('token');
        return token ? { Authorization: `Bearer ${token}` } : {};
    },
};

// Alert Utilities
export const Alerts = {
    handleSuccess(title, text) {
        Swal.fire({
            title,
            text,
            icon: 'success',
            confirmButtonText: 'Okay',
        });
    },

    handleSuccessTop(text) {
        Swal.fire({
            position: 'center',
            icon: 'success',
            title: text,
            showConfirmButton: false,
            timer: 2500,
        });
    },

    handleDelete(text) {
        Swal.fire({
            title: 'Are you sure?',
            text: 'You won’t be able to revert this!',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Yes, delete it!',
            cancelButtonText: 'No, cancel!',
        }).then((result) => {
            if (result.isConfirmed) {
                Swal.fire('Deleted!', text, 'success');
            } else if (result.dismiss === Swal.DismissReason.cancel) {
                Swal.fire('Cancelled', 'Your imaginary file is safe :)', 'error');
            }
        });
    },

    handleError(title, text) {
        Swal.fire({
            title,
            text,
            icon: 'error',
            confirmButtonText: 'Okay',
        });
    },
};
