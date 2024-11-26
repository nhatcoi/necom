'use strict';

(function ($) {
    $(document).ready(function () {
        const PREFIX = '/api/v1';
        loadCategories();
        loadBlogs(`${PREFIX}/blog/blogs`, 0);
        loadRecentBlogs();
        let pagesToShow = 2;

        function loadCategories() {
            $.ajax({
                url: `${PREFIX}/categories`,
                type: 'GET',
                success: function (response) {
                    response.forEach(category => {
                        $("#categories").append(`
                            <li><a href="/api/v1/categories/${category.id}">${category.name}</a></li>
                        `);
                    });
                },
                error: handleError('Could not load categories.')
            });

            $.ajax({
                url: `${PREFIX}/blog/categories`,
                type: 'GET',
                success: function (response) {
                    response.forEach(category => {
                        const categoryItem = `
                            <li><a href="#" class="category-link" data-category-id="${category.id}">${category.name}</a></li>
                        `;
                        $("#blog-categories, #search-by").append(categoryItem);
                    });

                    // click event
                    $(".category-link").click(function (e) {
                        e.preventDefault();
                        let categoryId = $(this).data("category-id");
                        loadBlogs(`${PREFIX}/blog/category/${categoryId}`, 0);
                    });
                },
                error: handleError('Could not load blog categories.')
            });
        }

        function loadBlogs(url, page) {
            $.ajax({
                url: url,
                method: 'GET',
                data: { page: page, size: 4 },
                success: function (data) {
                    renderBlogs(data.content);
                    renderPagination(data.totalPages, data.currentPage, url);
                },
                error: handleError('Error fetching blog data.')
            });
        }

        function renderBlogs(blogs) {
            let blogContainer = $('#blog-container');
            blogContainer.empty();

            blogs.forEach(blog => {
                let date = new Date(blog.createdAt);
                let formattedDate = date.toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' });

                let blogItem = `
                    <div class="col-lg-6 col-md-6 col-sm-6">
                        <div class="blog__item">
                            <div class="blog__item__pic">
                                <img src="${blog.thumbnail}" alt="image blog">
                            </div>
                            <div class="blog__item__text">
                                <ul>
                                    <li><i class="fa fa-calendar-o"></i> ${formattedDate}</li>
                                </ul>
                                <h5><a href="${PREFIX}/blog-details/${blog.id}">${blog.title}</a></h5>
                                <p>${blog.content.substring(0, 80)}...</p>
                                <a href="${PREFIX}/blog-details.html?id=${blog.id}" class="blog__btn">READ MORE <span class="arrow_right"></span></a>
                            </div>
                        </div>
                    </div>
                `;
                blogContainer.append(blogItem);
            });
        }

        // Pagination
        function renderPagination(totalPages, currentPage, url) {
            let pagination = $('.product__pagination.blog__pagination');
            pagination.empty();

            let startPage = Math.floor(currentPage / pagesToShow) * pagesToShow;
            let endPage = Math.min(startPage + pagesToShow, totalPages);

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
            loadBlogs(url, page);
        });

        // Load recent blog posts
        function loadRecentBlogs() {
            $.ajax({
                url: `${PREFIX}/blog/recent-news`,
                method: 'GET',
                success: function (data) {
                    let recentBlogContainer = $('#recent-news');
                    recentBlogContainer.empty();

                    data.forEach(blog => {
                        let date = new Date(blog.createdAt);
                        let formattedDate = date.toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' });
                        let recentBlogItem = `
                            <a href="${PREFIX}/blog/${blog.id}" class="blog__sidebar__recent__item">
                                <div class="blog__sidebar__recent__item__pic"></div>
                                <div class="blog__sidebar__recent__item__text">
                                    <h6>${blog.title}</h6>
                                    <span>${formattedDate}</span>
                                </div>
                            </a>
                        `;
                        recentBlogContainer.append(recentBlogItem);
                    });
                },
                error: handleError('Error fetching recent blog data.')
            });
        }

        // General error handler for AJAX requests
        function handleError(message) {
            return function () {
                alert(message);
            };
        }
    });
})(jQuery);
