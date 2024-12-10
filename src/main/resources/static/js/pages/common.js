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
            shopGrid: {
                filterByPrice: '/shop-grid/filterByPrice',
            },
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
        addToCartHandler: function (productId) {
            if (!productId) return console.error('Product ID is missing!');

            $.ajax({
                url: `${App.API.PREFIX}${App.API.urls.addToCart}${productId}`,
                method: 'POST',
                headers: this.getAuthHeaders(),
                success: (response) => {
                    const totalInCart = response.data;
                    const amountCart = $('.amount-cart span');
                    amountCart.text(totalInCart);
                    Alerts.handleSuccess("Good job!", response.message)
                },
                error: (jqXHR) => {
                    try {
                        const errorResponse = JSON.parse(jqXHR.responseText);
                        if (errorResponse.message) {
                            Alerts.handleError("Oops",errorResponse.message);
                        } else {
                            Alerts.handleError("Oops",'An unknown error occurred!');
                        }
                    } catch (err) {
                        Alerts.handleError("Oops", "Not Authenticated, Please Login!")
                    }
                },
            });
        },

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


    const Alerts = {
        handleSuccess: (title, text, confirmButtonText) => {
            Swal.fire({
                title: title,
                text: text,
                icon: 'success',
                confirmButtonText: "oce"
            });
        },

        handleError: (title, text, confirmButtonText) => {
            Swal.fire({
                title: title,
                text: text,
                icon: "error",
                confirmButtonText: "oce"
            });
        },
    }



    window.App = { API, Utils, Alerts, REGEX_VALIDATORS };
})(jQuery);
