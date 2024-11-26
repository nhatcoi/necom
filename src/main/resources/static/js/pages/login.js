'use strict';

(function ($) {
    $(document).ready(function() {
        const PREFIX = '/api/v1';
        $('#loginForm').on('submit', function(event) {
            event.preventDefault();

            const phoneNumber = $('input[name="phoneNumber"]').val();
            const password = $('input[name="password"]').val();

            $.ajax({
                url: `${PREFIX}/login`,
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ phoneNumber: phoneNumber, password: password }),
                success: function(response) {
                    const userId = response.id;
                    localStorage.setItem('userId', userId);
                    localStorage.setItem('fullName', response.fullName);
                    window.location.href = '/';
                },
                error: function(xhr) {
                    console.error("Error:", xhr.responseText);
                    alert("Login failed: " + xhr.responseText);
                }
            });
        });
    });

} (jQuery));