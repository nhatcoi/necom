import {API, Utils, Alerts} from "../../util/utils.js";

(function ($) {

    const PROD_IN_PAGE = 6;
    const PAGINATION_TO_SHOW = 3;
    const INITIAL_PAGE = 0;

    $(document).ready(init);

    function init() {
        filterByPrice();
        sortProducts();
        loadCategories();
        handleCategoryClick();

        $(document).on('click', '.add-to-cart', handleAddToCart);

        handleSearchOnPageLoad();
        handleSearchFormSubmit();
        handlePaginationClick();
        // handleFilterCategory();
    }


    // Tách xử lý search khi tải trang
    function handleSearchOnPageLoad() {
        const currentPath = window.location.pathname;
        const searchParams = new URLSearchParams(window.location.search);
        console.log('Search params:', searchParams);

        const search = searchParams.get('search');
        const categoryId = searchParams.get('category');

        if (search) {
            $('.search-results-title').text(`Search results for: "${search}"`);
            fetchProductsBySearch(search);
        } else if (categoryId) {
            if (categoryId) {
                console.log(`Loading products for category ID: ${categoryId}`);
                getProductsByCategory(categoryId);
            } else {
                console.log('No category selected, loading all products');
                getProductsByCategory(null);
            }
        } else {
            $("#filterButton").trigger("click");
        }


    }

    // Tách xử lý form submit cho search
    function handleSearchFormSubmit() {
        $('.hero__search__form form').on('submit', function (event) {
            const searchQuery = $(this).find('input[type="text"]').val();

            if (window.location.pathname === '/shop-grid') {
                event.preventDefault();
                console.log('Search:', searchQuery);

                const newUrl = `${window.location.pathname}?search=${encodeURIComponent(searchQuery)}`;
                window.history.pushState(null, '', newUrl);

                fetchProductsBySearch(searchQuery);
            }
        });
    }

    // Tách xử lý sự kiện click phân trang
    function handlePaginationClick() {
        $(document).on('click', '.pagination-link', function (event) {
            event.preventDefault();
            const page = $(this).data('page');
            const url = $(this).data('url');
            loadProducts(url, page);
        });
    }

    function fetchProductsBySearch(search) {
        console.log(`${API.urls.products}/search?search=${encodeURIComponent(search)}`);
        const url = `${API.urls.products}/search?search=${encodeURIComponent(search)}`;
        loadProducts(url, INITIAL_PAGE);
    }

    function handleAddToCart(event) {
        event.preventDefault();
        const productId = $(this).data('id');
        Utils.addToCartHandler(productId);
    }

    function renderProducts(products) {
        console.log('Products render:', products);
        const productsContainer = $("#filterProducts");
        productsContainer.empty();

        products.forEach(product => {
            productsContainer.append(`
                <div class="col-lg-4 col-md-6 col-sm-6">
                    <div class="product__item">
                        <div class="product__item__pic set-bg" style="background-image: url('${product.thumbnail}');">
                            <ul class="product__item__pic__hover">
                                <li><a href="#"><i class="fa fa-heart"></i></a></li>
                                <li><a href="#"><i class="fa fa-retweet"></i></a></li>
                                
<!--                                add to cart-->
                                <li><a href="#" class="add-to-cart" data-id="${product.id}"><i class="fa fa-shopping-cart"></i></a></li>
                            </ul>
                        </div>
                        <div class="product__item__text">
                            <h6><a href="/product-details/${product.id}">${product.name}</a></h6>
                            <h5>${product.price} $</h5>
                        </div>
                    </div>
                </div>
            `);
        });
    }

    function getProductsByCategory(cateId) {
        const url = `${API.urls.productsByCategory}/${cateId}`;
        $.ajax({
            url: url,
            type: "GET",
            success: (response) => {
                $("#products-found").text(response.data.length);
                renderProducts(response.data);
                document.getElementById('slip-cate').scrollIntoView({ behavior: 'smooth' });
            },
            error: () => Alerts.handleError("Error fetching category products"),
        });
    }

    function filterByPrice() {
        const min = 1, max = 120000;
        const priceRange = $(".price-range");
        const minAmount = $("#minamount");
        const maxAmount = $("#maxamount");

        priceRange.slider({
            range: true,
            min,
            max,
            values: [min, max],
            slide: (event, ui) => {
                minAmount.val(ui.values[0]);
                maxAmount.val(ui.values[1]);
            },
        });

        minAmount.val(priceRange.slider("values", 0));
        maxAmount.val(priceRange.slider("values", 1));

        $("#filterButton").click(() => {
            const url = `${API.urls.products}/filter-by-price?minamount=${minAmount.val()}&maxamount=${maxAmount.val()}`;
            loadProducts(url, INITIAL_PAGE);
        });
    }

    function loadProducts(urlBase, page) {
        $.ajax({
            url: urlBase + `&page=${page}&size=${PROD_IN_PAGE}`,
            type: "GET",
            success: (response) => {
                console.log('response:', response);
                const products = response.data;
                $("#products-found").text(response.pagination.totalItems);
                renderProducts(products);
                renderPagination(response.pagination, urlBase);
                document.getElementById('slip-cate').scrollIntoView({ behavior: 'smooth' });
            },
            error: () => Alerts.handleError("Error loading products"),
        });
    }

    function renderPagination({ currentPage, totalPages }, url) {
        const paginationDiv = document.querySelector('.product__pagination.blog__pagination');
        paginationDiv.innerHTML = '';

        const startPage = Math.floor(currentPage / PAGINATION_TO_SHOW) * PAGINATION_TO_SHOW;
        const endPage = Math.min(startPage + PAGINATION_TO_SHOW, totalPages);

        if (startPage > 0) paginationDiv.insertAdjacentHTML('beforeend', createPageLink(url, startPage - 1, '&laquo;'));
        for (let i = startPage; i < endPage; i++) {
            paginationDiv.insertAdjacentHTML('beforeend', createPageLink(url, i, i + 1, i === currentPage ? 'active' : ''));
        }
        if (endPage < totalPages) paginationDiv.insertAdjacentHTML('beforeend', createPageLink(url, endPage, '&raquo;'));
    }

    function createPageLink(url, page, text, active = "") {
        return `<a href="#" class="pagination-link ${active}" data-page="${page}" data-url="${url}">${text}</a>`;
    }


    function sortProducts() {
        $('#sortOptions').niceSelect('destroy').change(function () {
            const sortBy = $(this).val();
            const products = $('#filterProducts .col-lg-4').toArray();

            products.sort((a, b) => {
                const priceA = parseFloat($(a).find('.product__item__text h5').text().replace('$', '').trim());
                const priceB = parseFloat($(b).find('.product__item__text h5').text().replace('$', '').trim());

                return sortBy === 'priceHighToLow' ? priceB - priceA : priceA - priceB;
            });

            $('#filterProducts').empty().append(products);
        });
    }

    function loadCategories() {
        $.ajax({
            url: API.urls.categories,
            type: 'GET',
            success: (response) => {
                const categoriesContainer = $('.hero__categories ul');
                categoriesContainer.empty();

                response.data.forEach(category => {
                    const categoryItem = `
                        <li>
                            <a data-category-id="${category.id}" href="#">${category.name}</a>
                        </li>
                    `;
                    categoriesContainer.append(categoryItem);
                    $(".cate-filter").append(categoryItem);
                });
            },
            error: (error) =>
                console.error('Error loading categories:', error),
        });
    }

    function handleCategoryClick() {
        $(".hero__categories ul, .cate-filter").on("click", "li", function (event) {
            event.preventDefault();
            $(this).addClass("active").siblings().removeClass("active");

            const cateId = $(this).find('a').data('category-id');
            getProductsByCategory(cateId);
        });
    }

})(jQuery);

/* FLash Sale */
// // discount
// function setDiscount() {
//     $('.product__discount__item__pic').each(function() {
//         const salePrice = parseFloat($(this).attr('data-sale-price'));
//         const productPrice = parseFloat($(this).attr('data-product-price'));
//         const discountPercent = ((1 - salePrice / productPrice) * 100).toFixed(1);
//         $(this).find('.product__discount__percent').text(`-${discountPercent}%`);
//     });
// }
//
// // set price
// function setPrice() {
//     $('.product__item__price').each(function() {
//         var originalPrice = parseFloat($(this).find('.original-price').data('original-price'));
//         var salePrice = parseFloat($(this).find('.sale-price').data('sale-price'));
//
//         $(this).find('.original-price').text(originalPrice.toFixed(2) + ' $');
//         $(this).find('.sale-price').text(salePrice.toFixed(2) + ' $');
//     });
// }
//
// // countdown timer
// function setCountdownTimer() {
//     var endTimeText = $("#flash-sale-end-time").text();
//     if (endTimeText) {
//         var endTime = new Date(endTimeText).getTime();
//         var countdownInterval = setInterval(function() {
//             var now = new Date().getTime();
//             var timeRemaining = endTime - now;
//
//             if (timeRemaining <= 0) {
//                 $('#flash-sale-count-end-time').text('Flash sale has ended');
//                 clearInterval(countdownInterval);
//             } else {
//                 var days = Math.floor(timeRemaining / (1000 * 60 * 60 * 24));
//                 var hours = Math.floor((timeRemaining % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
//                 var minutes = Math.floor((timeRemaining % (1000 * 60 * 60)) / (1000 * 60));
//                 var seconds = Math.floor((timeRemaining % (1000 * 60)) / 1000);
//
//                 $('#flash-sale-count-end-time').text(`Countdown: ${days}d ${hours}h ${minutes}m ${seconds}s`);
//             }
//         }, 1000);
//     } else {
//         $('#flash-sale-count-end-time').text('Flash sale has ended!');
//     }
// }
//
//
//
// function loadFlashSale() {
//     $.ajax({
//         url: "/shop-grid/flash-sale",
//         method: "GET",
//         success: function(response) {
//
//             console.log(response);
//
//             let flashSale = response.data; // Dữ liệu Flash Sale từ API
//             console.log(flashSale);
//             let flashSaleSlider = $('.flash-sale-slider'); // Container để chứa sản phẩm
//
//             // Xóa dữ liệu cũ nếu có
//             flashSaleSlider.empty();
//
//             // Duyệt qua tất cả các sản phẩm và thêm vào slider
//             flashSale.items.forEach(item => {
//                 const productItem = item.productSale;
//                 console.log(productItem);
//                 flashSaleSlider.append(`
//                                 <div class="col-lg-4">
//                                     <div class="product__discount__item">
//                                         <div class="product__discount__item__pic set-bg"
//                                              data-setbg="${productItem.thumbnail}"
//                                              data-sale-price="${item.salePrice}"
//                                              data-product-price="${productItem.price}">
//                                             <div class="product__discount__percent">
//                                                 -${(1 - item.salePrice / productItem.price) * 100}%
//                                             </div>
//                                             <ul class="product__item__pic__hover">
//                                                 <li><a href="#"><i class="fa fa-heart"></i></a></li>
//                                                 <li><a href="#"><i class="fa fa-retweet"></i></a></li>
//                                                 <li><a href="#"><i class="fa fa-shopping-cart"></i></a></li>
//                                             </ul>
//                                         </div>
//                                         <div class="product__discount__item__text">
//
//                                             <h5><a href="/product-details/${productItem.id}">${productItem.name}</a></h5>
//                                             <div class="product__item__price">
//                                                 <span class="original-price" data-original-price="${productItem.price}" style="text-decoration: line-through;"></span>
//                                                 <span class="sale-price" data-sale-price="${item.salePrice}" style="text-decoration: none; font-weight: bold;">${item.salePrice}</span>
//                                             </div>
//                                         </div>
//                                     </div>
//                                 </div>
//                             `);
//             });
//
//             if (response.data.endTime) {
//                 $('#flash-sale-end-time').text(response.data.endTime);
//             }
//
//         },
//         error: function(error) {
//             console.log("Có lỗi xảy ra khi tải dữ liệu flash sale:", error);
//         }
//     });
// }

