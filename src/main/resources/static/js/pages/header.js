'use strict';

(function ($) {
    $(document).ready(function() {
        const PREFIX = '/api/v1';

        handleUserAuthentication();
        updateCartCount();

        function handleUserAuthentication() {
            const userId = localStorage.getItem('userId');
            const fullName = localStorage.getItem('fullName');

            if (fullName && userId) {
                $('.welcome-display').text(`Welcome, ${fullName}`);
                $('.nameSection').show();
                $('.authSection').html('<a href="#" class="logout"><i class="fa fa-sign-out"></i> Logout</a>');

                $('.logout').on('click', function(event) {
                    event.preventDefault();
                    localStorage.removeItem('userId');
                    localStorage.removeItem('fullName');
                    window.location.href = '/';
                });
            } else {
                $('#authSection').html(`<a href="${PREFIX}/api/login-form"><i class="fa fa-user"></i> Login</a>`);
                $('#nameSection').hide();
            }
        }

        function updateCartCount() {
            let cartId = localStorage.getItem('userId');
            if (!cartId) {
                return;
            }
            const CART_URL = `${PREFIX}/shopping-cart/count/${cartId}`;
            $.ajax({
                url: CART_URL,
                method: 'GET',
                success: function (data) {
                    $('.amount-cart span').text(data);
                },
                error: function (xhr) {
                    console.error("Failed to fetch cart count:", xhr.responseText);
                }
            });
        }
    });


})(jQuery);
