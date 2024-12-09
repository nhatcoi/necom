'use strict';

(function ($) {
    const API = {
        PREFIX: '',
        urls: {
            auth: {
                logIn: '/auth/log-in',
                register: '/users/create-user'
            },
            myInfo: '/users/my-info',
            cartCount: '/shopping-cart/count',
            loginForm: '/login-form',
            categories: '/categories',
            products: '/products',
            recentBlogs: '/blog/recent-news',
            addToCart: '/shopping-cart/add-to-cart/',
            cart: {
                base: '/shopping-cart',
                items: '/shopping-cart/items',
                update: '/shopping-cart/update/{id}',
                remove: '/shopping-cart/remove/{id}',
            },
            checkout: '/checkout/checkout-list'
        },
    };

    const REGEX_VALIDATORS = {
        fullName: /^[A-Za-z\s]{2,50}$/,
        addressLine: /^[A-Za-z0-9\s,.-]{5,100}$/,
        district: /^[A-Za-z\s]{2,50}$/,
        city: /^[A-Za-z\s]{2,50}$/,
        country: /^[A-Za-z\s]{2,50}$/,
        postcode: /^[A-Za-z0-9]{3,10}$/,
        email: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
        username: /^[A-Za-z0-9_-]{3,20}$/,
        phoneNumber: /^[0-9]{7,15}$/,
        password: /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/,
        dob: /^\d{4}-\d{2}-\d{2}$/
    };

    const Utils = {
        getAuthHeaders: () => {
            const token = localStorage.getItem('token');
            return token ? { 'Authorization': `Bearer ${token}` } : {};
        },
        handleError: (message) => (error) => {
            console.error(message, error);
            alert(message);
        },
        handleSuccess: (message) => {
            console.log(message);
            alert(message);
        },
        formatName: (name) => (name && name.length > 10 ? name.substring(0, 8) + '...' : name),
        redirectToLogin: () => (window.location.href = API.urls.loginForm),
    };

    window.App = { API, Utils, REGEX_VALIDATORS };
})(jQuery);
