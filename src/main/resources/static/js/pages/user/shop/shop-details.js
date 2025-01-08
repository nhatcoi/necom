'use restrict';

(function ($) {
    const { API, Utils } = App;

    function init() {
        $("#filterButton").trigger("click");
        $(document).on('click', '.add-to-cart', handleAddToCart);
    }
    $(document).ready(init);

    function handleAddToCart(event) {
        event.preventDefault();
        const productId = $(this).data('id');
        Utils.addToCartHandler(productId);
    }

} (jQuery));