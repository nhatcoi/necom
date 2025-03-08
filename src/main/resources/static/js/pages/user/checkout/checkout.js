
(function ($, App) {
    const { API, Utils , REGEX_VALIDATORS} = App;
    const { getAuthHeaders, handleError, redirectToLogin } = Utils;
    const {} = App;

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
        if (!getAuthHeaders().Authorization) {
            redirectToLogin();
            return;
        }
        loadBillingDetails();
        loadOrder();
    });

    function ajaxRequest({ url, type = 'GET', data = null, headers = {}, success, error }) {
        $.ajax({
            url: API.PREFIX + url,
            type,
            headers: { ...getAuthHeaders(), ...headers },
            contentType: headers['Content-Type'] || 'application/json',
            data: data ? JSON.stringify(data) : null,
            success,
            error: error || handleError('Error during request')
        });
    }

    function loadBillingDetails() {
        $('.guest').children().hide();

        $('#fill-info').on('change', function () {
            if ($(this).is(':checked')) {
                ajaxRequest({
                    url: API.urls.myInfo,
                    success: function (response) {
                        const user = response.data;
                        const address = user.addresses[0] || {};
                        const fields = {
                            '.full-name': user.fullName,
                            '.country': address.country,
                            '.street-address': address.addressLine,
                            '.city': address.city,
                            '.postcode': address.postcode,
                            '.phone': user.phoneNumber,
                            '.email': user.email
                        };

                        for (const [selector, value] of Object.entries(fields)) {
                            $(selector).val(value || '');
                        }
                    }
                });
            } else {
                $('#user-info-form input').val('');
            }
        });
    }

    function renderYourOrder(data) {
        const orderList = document.querySelector('.checkout__order ul');
        const subtotalElement = document.querySelector('.checkout__order__subtotal span');
        const totalElement = document.querySelector('.checkout__order__total span');

        const fragment = document.createDocumentFragment();
        let subtotal = 0;

        data.forEach(order => {
            const { product, quantity } = order;
            const totalPrice = quantity * product.price;
            subtotal += totalPrice;

            const listItem = document.createElement('li');
            listItem.innerHTML = `
                <p>${quantity} x ${Utils.formatName(product.name)}</p>
                <span>$${totalPrice.toFixed(2)}</span>
            `;
            fragment.appendChild(listItem);
        });

        orderList.innerHTML = '';
        orderList.appendChild(fragment);
        subtotalElement.textContent = `$${subtotal.toFixed(2)}`;
        totalElement.textContent = `$${subtotal.toFixed(2)}`;
    }

    function placeOrder(items) {
        $("#place-order").click(function (e) {
            e.preventDefault();

            const orderInfo = {
                fullName: $('.full-name').val(),
                country: $('.country').val(),
                addressLine: $('.street-address').val(),
                city: $('.city').val(),
                postcode: $('.postcode').val(),
                phoneNumber: $('.phone').val(),
                email: $('.email').val(),
                totalPrice: 0
            }

            let totalPrice = 0;
            items.forEach(item => {
                totalPrice += item.product.price * item.quantity;
            });
            orderInfo.totalPrice = totalPrice;

            if(!validateForm(orderInfo)){
                return;
            }


            const orderData = {
                products: items.map(item => ({
                    id: item.product.id,
                    name: item.product.name,
                    currency: 'USD',
                    amount: item.product.price,
                    quantity: item.quantity
                })),
                orderInfo: orderInfo
            };

            console.log(orderData)

            ajaxRequest({
                url: '/orders',
                type: 'POST',
                headers: { 'Content-Type': 'application/json' },
                data: orderData,
                success: function (response) {
                    orderData.orderMetadata = response.data;
                    delete orderData.orderInfo;

                    ajaxRequest({
                        url: API.urls.checkout,
                        type: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        data: orderData,
                        success: function (response) {
                            window.location.href = response.sessionUrl;
                        }
                    });
                }
            });




        });
    }

    function loadOrder() {
        ajaxRequest({
            url: API.urls.cart.items,
            success: function (response) {
                const items = response.data;
                renderYourOrder(items);
                placeOrder(items);
            }
        });
    }
})(jQuery, window.App);
