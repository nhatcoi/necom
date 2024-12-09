'use strict';


(function ($) {

    const { API, Utils } = App;
    const PROD_IN_PAGE = 8;
    const PAGINATION_TO_SHOW = 3;
    const INITIAL_PAGE = 0;

    $(document).ready(function () {

        console.log(API.urls.products);
        loadProducts(API.urls.products, INITIAL_PAGE);
        loadCategories(API.urls.categories);
        loadBlogs(API.urls.recentBlogs, 3);


        $(document).on('click', '.add-to-cart', handleAddToCart);
        $(document).on('click', '.pagination-link', function (e) {
            e.preventDefault();
            let page = $(this).data('page');
            let url = $(this).data('url');
            loadProducts(url, page);
        });
    });

    function handleAddToCart(event) {
        event.preventDefault();
        const productId = $(this).data('id');
        if (!productId) return console.error('Product ID is missing!');

        $.ajax({
            url: `${API.PREFIX}${API.urls.addToCart}${productId}`,
            method: 'POST',
            headers: Utils.getAuthHeaders(),
            success: (response) => {
                const totalInCart = response.data;
                const amountCart = $('.amount-cart span');
                amountCart.text(totalInCart);
                Utils.handleSuccess('Add to Cart Successful')
            },
            error: Utils.handleError('Failed to add product to cart. Please Login!'),
        });
    }

    function loadBlogs(url) {
        $.ajax({
            url: url,
            method: 'GET',
            success: function (response) {
                let blogContainer = $('.tech-blog');
                blogContainer.empty();
                response.data.forEach(blog => {
                    let content = blog.content.length > 50 ? blog.content.substring(0, 50) + '...' : blog.content;
                    let date = new Date(blog.createdAt);
                    let formattedDate = date.toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' });
                    let recentBlogItem = `<div class="col-lg-4 col-md-4 col-sm-6">
                                                    <div class="blog__item">
                                                        <div class="blog__item__pic">
                                                            <img src="${blog.thumbnail}" alt="image">
                                                        </div>
                                                        <div class="blog__item__text">
                                                            <ul>
                                                                <li><i class="fa fa-calendar-o"></i>${formattedDate}</li>
                                                                <li><i class="fa fa-comment-o"></i></li>
                                                            </ul>
                                                            <h5><a href="#">${blog.title}</a></h5>
                                                            <p>${content}</p>
                                                        </div>
                                                    </div>
                                                </div>
                                                `;
                    blogContainer.append(recentBlogItem);
                });
            },
            error: handleError('Error fetching recent blog data.')
        });
    }

    function loadCategories() {
        $.ajax({
            url: `${API.PREFIX}${API.urls.categories}`,
            method: 'GET',
            success: (response) => renderCategories(response.data),
            error: Utils.handleError('Error loading categories.'),
        });
    }

    function renderCategories(categories) {
        const categoriesContainer = $('.hero__categories ul').empty();
        const slider = $('.categories-slider').empty();

        categories.forEach((category) => {
            categoriesContainer.append(`<li><a href="${API.PREFIX}/categories/${category.id}">${category.name}</a></li>`);
            slider.append(`
                <div class="col-lg-3">
                    <div class="categories__item set-bg" style="background-image: url('${category.imageUrl}');">
                        <h5><a href="${API.PREFIX}/categories/${category.id}">${category.name}</a></h5>
                    </div>
                </div>
            `);
        });

        slider.owlCarousel('destroy').owlCarousel({
            loop: true,
            margin: 10,
            nav: true,
            dots: true,
            autoplay: true,
            autoplayTimeout: 3000,
            autoplayHoverPause: true,
            smartSpeed: 1000,
            responsive: { 0: { items: 1 }, 600: { items: 2 }, 1000: { items: 4 } },
        });
    }



    function loadProducts(url, page) {
        $.ajax({
            url: url,
            method: 'GET',
            data: { page, size: PROD_IN_PAGE },
            success: (data) => {
                renderProducts(data.content);
                renderPagination(data.totalPages, data.currentPage, url);
            },
            error: Utils.handleError('Error loading products.'),
        });
    }

    function renderProducts(products) {
        const productContainer = $('.product-container').empty();
        products.forEach((product) => {
            productContainer.append(`
                <div class="col-lg-3 col-md-4 col-sm-6 mix">
                    <div class="product__item">
                        <div class="product__item__pic set-bg" style="background-image: url('${product.thumbnail}');">
                            <ul class="product__item__pic__hover">
                                <li><a href="#"><i class="fa fa-heart"></i></a></li>
                                <li><a href="#"><i class="fa fa-retweet"></i></a></li>
                                <li><a href="#" class="add-to-cart" data-id="${product.id}"><i class="fa fa-shopping-cart"></i></a></li>
                            </ul>
                        </div>
                        <div class="product__item__text">
                            <h6><a href="${API.PREFIX}/product-details/${product.id}">${product.name}</a></h6>
                            <h5>${product.price} $</h5>
                        </div>
                    </div>
                </div>
            `);
        });
    }

    function renderPagination(totalPages, currentPage, url) {
        const pagination = $('.product__pagination.blog__pagination').empty();
        const startPage = Math.floor(currentPage / PAGINATION_TO_SHOW) * PAGINATION_TO_SHOW;
        const endPage = Math.min(startPage + PAGINATION_TO_SHOW, totalPages);

        if (startPage > 0) pagination.append(createPageLink(url, startPage - 1, '&laquo;'));
        for (let i = startPage; i < endPage; i++) {
            pagination.append(createPageLink(url, i, i + 1, i === currentPage ? 'active' : ''));
        }
        if (endPage < totalPages) pagination.append(createPageLink(url, endPage, '&raquo;'));
    }

    function createPageLink(url, page, text, active = '') {
        return `<a href="#" class="pagination-link ${active}" data-page="${page}" data-url="${url}">${text}</a>`;
    }


    function handleError(message) {
        return function () {
            alert(message);
        };
    }





})(jQuery);
