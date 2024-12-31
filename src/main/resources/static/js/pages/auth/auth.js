'use strict';

document.addEventListener('DOMContentLoaded', () => {
    const regexValidators = {
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

    const PREFIX = '';

    function validateField(fieldName, value) {
        const regex = regexValidators[fieldName];
        return regex ? regex.test(value) : true;
    }

    function validateForm(fields) {
        for (const [field, value] of Object.entries(fields)) {
            if (!validateField(field, value)) {
                Swal.fire(`${field} is invalid. Please check your input.`);
                return false;
            }
        }
        return true;
    }

    async function submitForm(url, data) {
        return await makeRequest(url, 'POST', data);
    }

    async function fetchInfo(url, token) {
        return await makeRequest(url, 'GET', null, { Authorization: `Bearer ${token}` });
    }

    async function makeRequest(url, method, data = null, customHeaders = {}) {
        try {
            const response = await fetch(url, {
                method,
                headers: {
                    'Content-Type': 'application/json',
                    ...customHeaders,
                },
                body: data ? JSON.stringify(data) : null,
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || `HTTP error! Status: ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            handleError("An error occurred", error);
            throw error;
        }
    }

    function handleError(message, error) {
        Swal.fire({
            icon: "error",
            title: "Oops...",
            text: message,
        });
        console.error(error);
    }

    function redirectToPageBasedOnRole(roles) {
        const hasAdmin = roles.includes('ADMIN');
        const hasUser = roles.includes('USER');

        if (hasAdmin && hasUser) {
            Swal.fire({
                title: "ADMIN",
                text: "Shopping or Managing?",
                icon: "success",
                showCancelButton: true,
                confirmButtonColor: "#3085d6",
                confirmButtonText: "Shopping",
                cancelButtonColor: "#40a803",
                cancelButtonText: "Managing",
            }).then((result) => {
                if (result.isConfirmed) {
                    navigateTo('/');
                } else if (result.dismiss === Swal.DismissReason.cancel) {
                    navigateTo('/admin');
                }
            });
        } else if (hasAdmin) {
            showTemporaryAlert("ADMIN", '/admin');
        } else if (hasUser) {
            showTemporaryAlert("Welcome", '/');
        } else {
            Swal.fire({
                icon: "warning",
                title: "Access Denied",
                text: "You don't have permission to access this application.",
            });
        }
    }

    function showTemporaryAlert(title, redirectUrl) {
        Swal.fire({
            title,
            icon: "success",
            timer: 1500,
            draggable: true,
        }).then(() => navigateTo(redirectUrl));
    }

    function navigateTo(url) {
        window.location.href = url;
    }

    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', async (event) => {
            event.preventDefault();

            const userIdentifier = document.querySelector('input[name="userIdentifier"]').value.trim();
            const password = document.querySelector('input[name="password"]').value.trim();

            if (!userIdentifier || !password) {
                alert("Please fill in all required fields.");
                return;
            }

            try {
                const response = await submitForm(`/auth/log-in`, { userIdentifier, password });
                localStorage.setItem('token', response.data.token);

                const userInfo = await fetchInfo('/users/my-info', response.data.token);
                redirectToPageBasedOnRole(userInfo.data.roleNames);
            } catch (error) {
                // Errors are already handled in individual functions
            }
        });
    }



    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', async (event) => {
            event.preventDefault();

            const fields = {
                fullName: document.querySelector('input[name="fullName"]').value,
                addressLine: document.querySelector('input[name="addressLine"]').value,
                district: document.querySelector('input[name="district"]').value,
                city: document.querySelector('input[name="city"]').value,
                country: document.querySelector('input[name="country"]').value,
                postcode: document.querySelector('input[name="postcode"]').value,
                email: document.querySelector('input[name="email"]').value,
                username: document.querySelector('input[name="username"]').value,
                phoneNumber: document.querySelector('input[name="phoneNumber"]').value,
                password: document.querySelector('input[name="password"]').value,
                confirmPassword: document.querySelector('input[name="confirmPassword"]').value,
                dob: document.querySelector('input[name="dob"]').value
            };

            if (!validateForm(fields)) {
                return;
            }

            if (fields.password !== fields.confirmPassword) {
                alert("Passwords do not match.");
                return;
            }

            const requestData = {
                username: fields.username,
                fullName: fields.fullName,
                password: fields.password,
                email: fields.email,
                phoneNumber: fields.phoneNumber,
                dateOfBirth: fields.dob,
                addresses: [
                    {
                        addressLine: fields.addressLine,
                        city: fields.city,
                        state: fields.district,
                        country: fields.country,
                        postcode: fields.postcode
                    }
                ]
            };

            try {
                await submitForm(`${PREFIX}/users/create-user`, requestData);
                alert("Registration successful. Please login to continue.");
                window.location.href = `${PREFIX}/login-form`;
            } catch (error) {
                // Error is already handled in submitForm
            }
        });
    }
});
