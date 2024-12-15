'use strict';

import {API, Utils, Alerts} from "./utils.js";

const PROD_IN_PAGE = 8;
const PAGINATION_TO_SHOW = 3;
const INITIAL_PAGE = 0;

document.addEventListener('DOMContentLoaded', () => {
    console.log(API.urls.products);
    loadProducts(API.urls.products, INITIAL_PAGE);
    loadCategories(API.urls.categories);
    loadBlogs(API.urls.recentBlogs, 3);

    document.addEventListener('click', event => {
        if (event.target.classList.contains('add-to-cart')) {
            handleAddToCart(event);
        }
        if (event.target.classList.contains('pagination-link')) {
            event.preventDefault();
            let page = event.target.dataset.page;
            let url = event.target.dataset.url;
            loadProducts(url, page);
        }
    });
});

function handleAddToCart(event) {
    event.preventDefault();
    const productId = event.target.dataset.id;
    console.log(productId)
    Utils.addToCartHandler(productId);
}

async function loadBlogs(url) {
    try {
        const response = await fetch(url);
        const data = await response.json();
        renderBlogs(data.data);
    } catch (error) {
        handleError('Blog error');
    }
}

function renderBlogs(blogs) {
    const blogContainer = document.querySelector('.tech-blog');
    blogContainer.innerHTML = '';
    blogs.forEach(blog => {
        const content = blog.content.length > 50 ? blog.content.substring(0, 50) + '...' : blog.content;
        const date = new Date(blog.createdAt);
        const formattedDate = date.toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' });
        const recentBlogItem = `
            <div class="col-lg-4 col-md-4 col-sm-6">
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
        blogContainer.insertAdjacentHTML('beforeend', recentBlogItem);
    });
}

async function loadCategories() {
    try {
        const response = await fetch(`${API.PREFIX}${API.urls.categories}`);
        const data = await response.json();
        renderCategories(data.data);
    } catch (error) {
        handleError('Error loading categories.');
    }
}

function renderCategories(categories) {
    const categoriesContainer = document.querySelector('.hero__categories ul');
    const slider = document.querySelector('.categories-slider');
    categoriesContainer.innerHTML = '';
    slider.innerHTML = '';

    categories.forEach(category => {
        categoriesContainer.insertAdjacentHTML('beforeend', `<li><a href="${API.PREFIX}/categories/${category.id}">${category.name}</a></li>`);
        slider.insertAdjacentHTML('beforeend', `
            <div class="col-lg-3">
                <div class="categories__item set-bg" style="background-image: url('${category.imageUrl}');">
                    <h5><a href="${API.PREFIX}/categories/${category.id}">${category.name}</a></h5>
                </div>
            </div>
        `);
    });

    $(slider).owlCarousel('destroy').owlCarousel({
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

async function loadProducts(url, page) {
    try {
        const response = await fetch(url + `?page=${page}&size=${PROD_IN_PAGE}`);
        const data = await response.json();
        renderProducts(data.content);
        renderPagination(data.totalPages, data.currentPage, url);
    } catch (error) {
        handleError('Error loading products.');
    }
}

function renderProducts(products) {
    const productContainer = document.querySelector('.product-container');
    productContainer.innerHTML = '';
    products.forEach(product => {
        const productItem = `
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
        `;
        productContainer.insertAdjacentHTML('beforeend', productItem);
    });
}

function renderPagination(totalPages, currentPage, url) {
    const pagination = document.querySelector('.product__pagination.blog__pagination');
    pagination.innerHTML = '';
    const startPage = Math.floor(currentPage / PAGINATION_TO_SHOW) * PAGINATION_TO_SHOW;
    const endPage = Math.min(startPage + PAGINATION_TO_SHOW, totalPages);

    if (startPage > 0) pagination.insertAdjacentHTML('beforeend', createPageLink(url, startPage - 1, '&laquo;'));
    for (let i = startPage; i < endPage; i++) {
        pagination.insertAdjacentHTML('beforeend', createPageLink(url, i, i + 1, i === currentPage ? 'active' : ''));
    }
    if (endPage < totalPages) pagination.insertAdjacentHTML('beforeend', createPageLink(url, endPage, '&raquo;'));
}

function createPageLink(url, page, text, active = '') {
    return `<a href="#" class="pagination-link ${active}" data-page="${page}" data-url="${url}">${text}</a>`;
}

function handleError(message) {
    Alerts.handleError(message);
}
