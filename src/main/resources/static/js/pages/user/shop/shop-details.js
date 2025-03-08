import {API, Utils, Alerts} from "../../util/utils.js";

document.addEventListener('DOMContentLoaded', init);

function init() {
    const filterButton = document.getElementById('filterButton');
    if (filterButton) {
        filterButton.click();
    }
    document.addEventListener('click', (event) => {
        if (event.target.classList.contains('add-to-cart')) {
            handleAddToCart(event);
        }
    });
}

function handleAddToCart(event) {
    event.preventDefault();
    const productId = event.target.dataset.id;
    Utils.addToCartHandler(productId);
}
