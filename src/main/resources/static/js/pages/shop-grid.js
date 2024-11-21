'use restrict';

(function ($) {
    // Set percent discount
    $(document).ready(function() {
        $('.product__discount__item__pic').each(function() {
            const salePrice = parseFloat($(this).attr('data-sale-price'));
            const productPrice = parseFloat($(this).attr('data-product-price'));
            const discountPercent = ((1 - salePrice / productPrice) * 100).toFixed(1);
            $(this).find('.product__discount__percent').text(`-${discountPercent}%`);
        });
    });

    // set price and sale price
    $(document).ready(function() {
        $('.product__item__price').each(function() {
            var originalPrice = parseFloat($(this).find('.original-price').data('original-price'));
            var salePrice = parseFloat($(this).find('.sale-price').data('sale-price'));

            // Định dạng giá gốc
            $(this).find('.original-price').text(originalPrice.toFixed(2) + ' $');

            // Định dạng giá sale
            $(this).find('.sale-price').text(salePrice.toFixed(2) + ' $');
        });
    });

    // Countdown timer
    $(document).ready(function() {
        var endTimeText = $("#flash-sale-end-time").text();
        if (endTimeText) {
            var endTime = new Date(endTimeText).getTime();
            var countdownInterval = setInterval(function() {
                var now = new Date().getTime();
                var timeRemaining = endTime - now;

                if (timeRemaining <= 0) {
                    $('#flash-sale-count-end-time').text('Flash sale has ended');
                    clearInterval(countdownInterval);
                } else {
                    var days = Math.floor(timeRemaining / (1000 * 60 * 60 * 24));
                    var hours = Math.floor((timeRemaining % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
                    var minutes = Math.floor((timeRemaining % (1000 * 60 * 60)) / (1000 * 60));
                    var seconds = Math.floor((timeRemaining % (1000 * 60)) / 1000);

                    $('#flash-sale-count-end-time').text(`Countdown: ${days}d ${hours}h ${minutes}m ${seconds}s`);
                }
            }, 1000);
        } else {
            $('#flash-sale-count-end-time').text('Flash sale has ended!');
        }
    });


    // Filter by price
    $(document).ready(function() {
        // Set default min and max price
        var min = 1;
        var max = 5000;

        $(".price-range").slider({
            range: true,
            min: min,
            max: max,
            values: [min, max],
            slide: function(event, ui) {
                $("#minamount").val(ui.values[0]);
                $("#maxamount").val(ui.values[1]);
            }
        });

        $("#minamount").val($(".price-range").slider("values", 0));
        $("#maxamount").val($(".price-range").slider("values", 1));

        // Filter and set products
        $("#filterButton").click(function() {
            $.ajax({
                url: "/shop-grid/filterByPrice",
                type: "GET",
                data: {
                    minamount: $("#minamount").val(),
                    maxamount: $("#maxamount").val()
                },
                success: function(data) {
                    $("#products-found").text(data.length);
                    $("#filterProducts").empty();

                    $.each(data, function(index, product) {
                        $("#filterProducts").append(`
                        <div class="col-lg-4 col-md-6 col-sm-6">
                            <div class="product__item">
                                <div class="product__item__pic set-bg" style="background-image: url('${product.thumbnail}');">
                                    <ul class="product__item__pic__hover">
                                        <li><a href="#"><i class="fa fa-heart"></i></a></li>
                                        <li><a href="#"><i class="fa fa-retweet"></i></a></li>
                                        <li><a href="#"><i class="fa fa-shopping-cart"></i></a></li>
                                    </ul>
                                </div>
                                <div class="product__item__text">
                                    <h6><a href="/products/${product.id}">${product.name}</a></h6>
                                    <h5>${product.price} $</h5>
                                </div>
                            </div>
                        </div>
                    `);
                    });
                },
                error: function() {
                    alert("Error");
                }
            });
        });
    });

    // delete nice select with
    $('#sortOptions').niceSelect('destroy');

    // Sort options products
    $('#sortOptions').change(function() {
        const sortBy = $(this).val();
        let products = $('#filterProducts .col-lg-4').toArray();

        products.sort(function(a, b) {
            // Get prices from the product elements
            const priceA = parseFloat($(a).find('.product__item__text h5').text().replace('$', '').trim());
            const priceB = parseFloat($(b).find('.product__item__text h5').text().replace('$', '').trim());

            if (sortBy === 'priceHighToLow') {
                return priceB - priceA; // Sort descending
            } else if (sortBy === 'priceLowToHigh') {
                return priceA - priceB; // Sort ascending
            }
            return 0; // No sorting
        });

        // Clear and re-append sorted products
        $('#filterProducts').empty().append(products);
    });
} (jQuery));