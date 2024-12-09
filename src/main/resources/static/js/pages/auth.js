'use strict';

(function ($) {

    const {REGEX_VALIDATORS} = App;

    function validateField(fieldName, value) {
        const regex = REGEX_VALIDATORS[fieldName];
        return regex ? regex.test(value) : true;
    }

    function validateForm(fields) {
        let isValid = true;
        for (const [field, value] of Object.entries(fields)) {
            if (!validateField(field, value)) {
                isValid = false;
                console.error(`${field} is invalid: ${value}`);
                alert(`${field} is invalid. Please check your input.`);
                return isValid;
            }
        }
        return isValid;
    }

    $(document).ready(function () {
        const PREFIX = '';

        $('#loginForm').on('submit', function (event) {
            event.preventDefault();

            const userIdentifier = $('input[name="userIdentifier"]').val();
            const password = $('input[name="password"]').val();

            if (!userIdentifier || !password) {
                alert("Please fill in all required fields.");
                return;
            }

            $.ajax({
                url: `${PREFIX}/auth/log-in`,
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ userIdentifier, password }),
                success: function (response) {
                    const token = response.data.token;
                    localStorage.setItem('token', token);
                    window.location.href = '/';
                },
                error: function (xhr) {
                    console.error("Error:", xhr.responseText);
                    alert("Login failed: " + xhr.responseText);
                }
            });
        });

        $('#registerForm').on('submit', function (event) {
            event.preventDefault();

            const fields = {
                fullName: $('input[name="fullName"]').val(),
                addressLine: $('input[name="addressLine"]').val(),
                district: $('input[name="district"]').val(),
                city: $('input[name="city"]').val(),
                country: $('input[name="country"]').val(),
                postcode: $('input[name="postcode"]').val(),
                email: $('input[name="email"]').val(),
                username: $('input[name="username"]').val(),
                phoneNumber: $('input[name="phoneNumber"]').val(),
                password: $('input[name="password"]').val(),
                confirmPassword: $('input[name="confirmPassword"]').val(),
                dob: $('input[name="dob"]').val()
            };


            if (!validateForm(fields)) {
                return;
            }

            if (fields.password !== fields.confirmPassword) {
                alert("Passwords do not match.");
                return;
            }

            const addresses = [
                {
                    addressLine: fields.addressLine,
                    city: fields.city,
                    state: fields.district,
                    country: fields.country,
                    postcode: fields.postcode
                }
            ];

            const requestData = {
                username: fields.username,
                fullName: fields.fullName,
                password: fields.password,
                email: fields.email,
                phoneNumber: fields.phoneNumber,
                dateOfBirth: fields.dob,
                addresses: addresses
            };

            $.ajax({
                url: `${PREFIX}/users/create-user`,
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(requestData),
                success: function () {
                    alert("Registration successful. Please login to continue.");
                    window.location.href = `${PREFIX}/login-form`;
                },
                error: function (xhr) {
                    console.error("Error:", xhr.responseText);
                    alert("Registration failed: " + xhr.responseText);
                }
            });
        });
    });
})(jQuery);
