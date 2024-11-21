'use strict';

(function ($) {

    // $(document).ready(function() {
    //     const userId = localStorage.getItem('userId');
    //     if (userId) {
    //         console.log("User ID:", userId);
    //     } else {
    //         window.location.href = '/api/login-form';
    //     }
    // });

    $(document).ready(function () {
        const PRODUCTS_URL = '/products';
        const INITIAL_PAGE = 0;
        const PAGE_SIZE = 8;
        const PAGES_TO_SHOW = 2;

        // loadCategories();
        loadProducts(PRODUCTS_URL, INITIAL_PAGE);


        function loadProducts(url, page) {
            $.ajax({
                url: url,
                method: 'GET',
                data: { page: page, size: PAGE_SIZE },
                success: function (data) {
                    renderProducts(data.content);
                    renderPagination(data.totalPages, data.currentPage, url);
                },
            });
        }

        function renderProducts(products) {
            let productContainer = $('#product-container');
            productContainer.empty();

            products.forEach(product => {
                let productItem = `
                    <div class="col-lg-3 col-md-4 col-sm-6 mix">
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
                `;
                  productContainer.append(productItem);
            });
        }

        function renderPagination(totalPages, currentPage, url) {
            let pagination = $('.product__pagination.blog__pagination');
            pagination.empty();

            let startPage = Math.floor(currentPage / PAGES_TO_SHOW) * PAGES_TO_SHOW;
            let endPage = Math.min(startPage + PAGES_TO_SHOW, totalPages);

            let prevArrow = startPage > 0 ? `<a href="#" class="pagination-link fa fa-long-arrow-left" data-page="${startPage - 1}" data-url="${url}">&laquo;</a>` : '';
            let nextArrow = endPage < totalPages ? `<a href="#" class="pagination-link fa fa-long-arrow-right" data-page="${endPage}" data-url="${url}">&raquo;</a>` : '';

            pagination.append(prevArrow);

            for (let i = startPage; i < endPage; i++) {
                let activeClass = i === currentPage ? 'active' : '';
                pagination.append(`
                    <a href="#" class="pagination-link ${activeClass}" data-page="${i}" data-url="${url}">${i + 1}</a>
                `);
            }

            pagination.append(nextArrow);
        }

        // click page
        $(document).on('click', '.pagination-link', function (e) {
            e.preventDefault();
            let page = $(this).data('page');
            let url = $(this).data('url');
            loadProducts(url, page);
        });


        function handleError(message) {
            return function () {
                alert(message);
            };
        }
    });




})(jQuery);
