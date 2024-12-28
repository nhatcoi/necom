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
        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText);
            }

            return await response.json();
        } catch (error) {
            Swal.fire({
                icon: "error",
                title: "Oops...",
                text: "Account Invalid",
            });
            console.error(error);
            throw error;
        }
    }

    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', async (event) => {
            event.preventDefault();

            const userIdentifier = document.querySelector('input[name="userIdentifier"]').value;
            const password = document.querySelector('input[name="password"]').value;

            if (!userIdentifier || !password) {
                alert("Please fill in all required fields.");
                return;
            }

            try {
                const response = await submitForm(`${PREFIX}/auth/log-in`, { userIdentifier, password });
                localStorage.setItem('token', response.data.token);
                window.location.href = '/';
            } catch (error) {
                // Error is already handled in submitForm
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
