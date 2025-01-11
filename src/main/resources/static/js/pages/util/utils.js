

export const API = {
    PREFIX: '',
    urls: {
        admin: {
            base: '/admin',
            users: '/users',
            products: '/products',
            categories: '/admin/categories',
            blogs: '/admin/blogs',
            reports: '/admin/reports',
        },
        auth: {
            logIn: '/auth/log-in',
            register: '/users/create-user',
            info: '/users/my-info'
        },
        myInfo: '/users/my-info',
        cartCount: '/shopping-cart/count',
        loginForm: '/login-form',
        categories: '/categories',
        products: '/products',
        productsByCategory: '/products/category',
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

export const REGEX_VALIDATORS = {
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
            console.log(data.data);

            Alerts.handleSuccess('Good job!', data.message);

            const amountSpan = document.querySelector('.quantity-in-cart');
            if (amountSpan) {
                amountSpan.textContent = data.data;
            } else {
                console.error("Element '.amount-cart span' not found!");
            }

        } catch (error) {
            const errorMessage = error.message || 'Not Authenticated, Please Login!';
            Alerts.handleError('Oops', errorMessage);
        }
    },

    getAuthHeaders() {
        const token = localStorage.getItem('token');
        return token ? { Authorization: `Bearer ${token}` } : {};
    },

    formatName: (name) => (name && name.length > 10 ? name.substring(0, 8) + '...' : name),
    redirectToLogin: () => (window.location.href = API.urls.loginForm),
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

    handleSuccessTimeCenter(text) {
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

    handleErrorTimeCenter(text) {
        Swal.fire({
            position: 'center',
            icon: 'error',
            title: text,
            showConfirmButton: false,
            timer: 2500,
        });
    },
};
