import { API, Utils } from './utils.js';

document.addEventListener('DOMContentLoaded', () => {

    const authUserIcon = document.querySelector('.header__top__right__social.auth');
    const authUserIconMobile = document.querySelector('.header__top__right__social.auth-mobile');
    const quantityInCart = document.querySelector('.quantity-in-cart');

    const token = localStorage.getItem('token');
    console.log(token);
    if (token) {
        handleUserAuthentication();
        console.log('getCountCart');
        getCountCart();
    } else {
        attachLogin();
    }


    loadCategories()
        .then(data => console.log(data))
        .catch(error => handleError('Error loading categories.'));



    function attachLogin() {
        authUserIcon.innerHTML = `<a href="${API.urls.loginForm}" class="login-icon"><i class="fa-solid fa-user"></i></a> `;
        authUserIconMobile.innerHTML = `<a href="${API.urls.loginForm}" class="login-icon"><i class="fa-solid fa-user"></i></a> `;
    }

    function handleUserAuthentication() {
        fetch(`${API.PREFIX}${API.urls.myInfo}`, {
            method: 'GET',
            headers: Utils.getAuthHeaders(),
        })
            .then(response => {
                if (!response.ok) throw new Error('Authentication failed');
                return response.json();
            })
            .then(userResponse => {
                const fullName = Utils.formatName(userResponse.data.fullName);
                if (fullName) {
                    const iconHtml = `
                        <a href="/user" class="user-info"><i class="fa-solid fa-user"></i></a> 
                        <a class="logout-icon"><i class="fa-solid fa-right-from-bracket"></i></a> 
                    `;
                    authUserIcon.innerHTML = iconHtml;
                    authUserIconMobile.innerHTML = iconHtml;
                    attachLogoutHandler();
                }
            })
            .catch(() => {
                attachLogin();
            });
    }

    function getCountCart() {
        console.log('getCountCart');
        fetch(`${API.PREFIX}${API.urls.cartCount}`, {
            method: 'GET',
            headers: Utils.getAuthHeaders(),
        })
            .then(response => {
                if (!response.ok) throw new Error('Cart count fetch failed');
                return response.json();
            })
            .then(response => {
                quantityInCart.textContent = response.data || 0;
            })
            .catch(() => {
                localStorage.clear();
                Utils.redirectToLogin();
            });
    }


    function attachLogoutHandler() {
        const logoutLink = document.querySelector('.header__top__right__social.auth .logout-icon');
        const logoutLinkMobile = document.querySelector('.header__top__right__social.auth-mobile .logout-icon');

        if (logoutLink) {
            logoutLink.addEventListener('click', event => {
                event.preventDefault();
                localStorage.removeItem('token');
                alert('You have been logged out.');
                window.location.href = '/';
            });
        }
        if (logoutLinkMobile) {
            logoutLinkMobile.addEventListener('click', event => {
                event.preventDefault();
                localStorage.removeItem('token');
                alert('You have been logged out.');
                window.location.href = '/';
            });
        }
    }

    async function loadCategories() {
        try {
            const response = await fetch(`${API.PREFIX}${API.urls.categories}`);
            if (!response.ok) throw new Error('Failed to load categories');
            const data = await response.json();
            renderCategories(data.data);
        } catch (error) {
            handleError('Error loading categories.');
        }
    }

    function renderCategories(categories) {
        const categoriesContainer = document.querySelector('.hero__categories ul');
        if (categoriesContainer) {
            categoriesContainer.innerHTML = '';
            categories.forEach(category => {
                const li = document.createElement('li');
                const a = document.createElement('a');

                // TODO - Set the correct href for each category
                a.href = `/shop-grid?category=${encodeURIComponent(category.id)}`;


                a.textContent = category.name;
                a.dataset.categoryId = category.id;
                li.appendChild(a);
                categoriesContainer.appendChild(li);
            });
        }
    }

    function handleError(message) {
        console.error(message);
    }
});
