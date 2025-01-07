'use strict';

function handleError(errorLoadingCategories) {
    return undefined;
}

(function ($) {
    const { API, Utils } = App;

    $(document).ready(function () {
        const authSection = $('.authSection');
        const nameSection = $('.nameSection');
        const welcomeDisplay = $('.welcome-display');
        const amountCart = $('.amount-cart span');
        const token = localStorage.getItem('token');
        loadCategories(API.urls.categories).then(r => console.log(r)).catch(error => handleError('Error loading categories.'));

        if (token) {
            handleUserAuthentication();
            getCountCart();
        } else {
            displayLoginLink();
        }

        function displayLoginLink() {
            authSection.html('<div><i class="fa fa-user"></i> Login</div>');
            nameSection.hide();
        }

        function handleUserAuthentication() {
            $.ajax({
                url: `${API.PREFIX}${API.urls.myInfo}`,
                method: 'GET',
                headers: Utils.getAuthHeaders(),
                success: function (userResponse) {
                    const fullName = Utils.formatName(userResponse.data.fullName);
                    if (fullName) {
                        welcomeDisplay.text(`Hi, ${fullName}`);
                        nameSection.show();
                        authSection.html('<a href="#" class="logout"><i class="fa fa-sign-out"></i></a>');
                        attachLogoutHandler();
                    }
                },
                error: function () {
                    displayLoginLink();
                },
            });
        }

        function getCountCart() {
            $.ajax({
                url: `${API.PREFIX}${API.urls.cartCount}`,
                method: 'GET',
                headers: Utils.getAuthHeaders(),
                success: function (response) {
                    amountCart.text(response.data || 0);
                },
                error: function () {
                    localStorage.clear();
                    Utils.redirectToLogin();
                },
            });
        }


        function attachLogoutHandler() {
            $('.logout').on('click', function (event) {
                event.preventDefault();
                localStorage.removeItem('token');
                alert('You have been logged out.');
                window.location.href = '/';
            });
        }
    });


    async function loadCategories() {
        try {
            const response = await fetch(`${API.PREFIX}${API.urls.categories}`);
            const data = await response.json();
            renderCategories(data.data);
        } catch (error) {
            handleError('Error loading categories.');
        }
    }

    function renderCategories(categories) {
        const categoriesContainer = document.querySelector('.hero__categories ul');
        categoriesContainer.innerHTML = '';

        categories.forEach(category => {
            categoriesContainer.insertAdjacentHTML('beforeend', `<li><a data-category-id="${category.id}" href="/shop-grid">${category.name}</a></li>`);
        });
    }



})(jQuery);
