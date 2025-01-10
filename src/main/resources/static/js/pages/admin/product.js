import { API, Utils } from "../util/utils.js";
import { Alerts } from "./alerts.js";
import { renderPagination } from "./utils.js";

const SELECTORS = {
    userTableBody: 'product-table-body',
    paginationDiv: '.product__pagination.blog__pagination',
    paginationLinkClass: 'product-pagination-link',
    productTableBodyId: 'product-table-body',
    PRODS_PER_PAGE: 10,
    editBtn: '.edit-btn',
};

export function initializeManageProducts(contentArea) {
    contentArea.innerHTML = generateProductPageTemplate();
    populateProductsTable(API.urls.admin.products, 0);
}

function generateProductPageTemplate() {
    return `
        <h5 class="mb-4">Manage Products</h5>
        ${generateSearchBox()}
        ${generateProductTable()}
        ${generatePagination()}
    `;
}

function generateSearchBox() {
    return `
        <div class="input-group mb-4">
            <input type="text" class="form-control" id="search-box" placeholder="Search users..." 
                   aria-label="Search users" aria-describedby="button-search">
            <button class="btn btn-primary" type="button" id="button-search">Search</button>
        </div>
    `;
}

function generateProductTable() {
    return `
        <table class="table table-bordered table-hover mb-4">
            <thead>
                <tr class="table-primary">
                    <th>ID</th>
                    <th>Name</th>
                    <th>Price</th>
                    <th>Quantity In Stock</th>
                    <th>Thumbnail</th>
                    <th></th>
                </tr>
            </thead>
            <tbody id="product-table-body"></tbody>
        </table>
    `;
}

function generatePagination() {
    return `
        <div class="col-lg-12">
            <div class="product__pagination blog__pagination d-flex justify-content-center"></div>
        </div>
    `;
}

document.addEventListener('click', function (event) {
    if (event.target.classList.contains(SELECTORS.paginationLinkClass)) {
        event.preventDefault();
        const page = event.target.dataset.page;
        const url = event.target.dataset.url;
        console.log('click page: ' + page);
        console.log('click url: ' + url);
        populateProductsTable(url, page).catch(error => handleError('Error loading products.'));
    }
});

async function populateProductsTable(url, page) {
    const urlWithParams = `${url}?page=${page}&size=${SELECTORS.PRODS_PER_PAGE}`;
    console.log("url with param: " + urlWithParams);

    try {
        const productPagination = await fetch(urlWithParams, {
            method: "GET",
            headers: Utils.getAuthHeaders(),
        });

        if (!productPagination.ok) throw new Error("Failed to fetch products");

        const { data: products, pagination } = await productPagination.json();
        renderProducts(products);
        renderPagination(pagination, url, SELECTORS.paginationDiv, SELECTORS.paginationLinkClass);
    } catch (error) {
        Alerts.handleError("Error loading products", error.message);
    }
}

function renderProducts(products) {
    const productTableBody = document.getElementById(SELECTORS.productTableBodyId);
    productTableBody.innerHTML = products.map(product => generateProductRow(product)).join('');
    addProductRowEventListeners();
}

function generateProductRow(product) {
    return `
        <tr data-product-id="${product.id}">
            <td>${product.id}</td>
            <td>${product.name}</td>
            <td>${product.price}</td>
            <td>${product.quantity_in_stock}</td>
            <td><img src="${product.thumbnail}" alt="Thumbnail" width="20" height="20"></td>
            <td>
                <button class="edit-btn btn btn-sm btn-danger">
                    <i class="fa fa-trash"></i>
                </button>
            </td>
        </tr>
    `;
}

function addProductRowEventListeners() {
    const productTableBody = document.getElementById(SELECTORS.productTableBodyId);
    productTableBody.addEventListener("click", handleUserRowClick);

    const editButtons = document.querySelectorAll(SELECTORS.editBtn);
    editButtons.forEach(button => {
        button.addEventListener("click", handleEditButtonClick);
    });
}

function handleUserRowClick(event) {
    const row = event.target.closest("tr");
    if (!row) return;

    const productId = row.dataset.productId;
    displayProductDetail(productId);
}

function handleEditButtonClick(event) {
    event.stopPropagation();
    // Logic to handle editing a product can go here
}

async function displayProductDetail(productId) {
    try {
        const response = await fetch(`${API.urls.admin.products}/${productId}`, {
            method: "GET",
            headers: Utils.getAuthHeaders(),
        });

        if (!response.ok) throw new Error("Failed to fetch product details");

        const { data } = await response.json();
        const imageListHtml = generateImageList(data.productImages, data.id);
        showProductDetailModal(data, imageListHtml);
    } catch (error) {
        console.error("Error fetching product details:", error.message);
        showErrorAlert();
    }
}

function generateImageList(images, productId) {
    return images.map(imageUrl => generateImageItem(imageUrl, productId)).join('');
}

function generateImageItem(imageUrl, productId) {
    return `
        <li style="list-style: none; margin: 5px;">
            <img src="${imageUrl}" alt="Product Image" 
                 style="width: 100px; height: 100px; object-fit: cover; border: 1px solid #ddd; border-radius: 5px;" 
                 onclick="showImage('${imageUrl}', '${productId}')"/>
        </li>
    `;
}

function showProductDetailModal(data, imageListHtml) {
    Swal.fire({
        title: "Product Details",
        html: `
            <div style="text-align: left;">
                <p><strong>ID:</strong> ${data.id}</p>
                <p><strong>Name:</strong> ${data.name}</p>
                <p><strong>Price:</strong> $${data.price.toFixed(2)}</p>
                <p><strong>Description:</strong> ${data.description}</p>
                <p><strong>Category:</strong> ${data.category_id}</p>
                <p><strong>Created At:</strong> ${new Date(data.createdAt).toLocaleString()}</p>
                <p><strong>Updated At:</strong> ${new Date(data.updatedAt).toLocaleString()}</p>
                <p><strong>Average Rating:</strong> ${data.avgRating.toFixed(1)}</p>
                <p><strong>Thumbnail:</strong></p>
                <img src="${data.thumbnail}" alt="Product Thumbnail" 
                     style="width: 200px; height: auto; cursor: pointer; border: 1px solid #ddd; border-radius: 5px;" 
                     onclick="showImage('${data.thumbnail}', '${data.id}')">
                <p><strong>Product Images:</strong></p>
                <ul style="display: flex; flex-wrap: wrap; padding: 0; margin: 0;">
                    ${imageListHtml}
                </ul>
            </div>
        `,
        icon: "info",
        showCancelButton: true,
        confirmButtonText: "Edit",
        cancelButtonText: "Close",
    }).then(result => {
        if (result.isConfirmed) {
            console.log("Edit product feature can be added here");
        }
    });
}

function showErrorAlert() {
    Swal.fire({
        icon: "error",
        title: "Error",
        text: "Failed to fetch product details. Please try again later.",
    });
}
window.showImage = function (imageUrl, productId) {
    console.log("Show image:", productId);

    Swal.fire({
        html: `
            <img src="${imageUrl}" alt="Large Image" 
                 style="width: 100%; max-width: 600px; height: auto; border-radius: 10px;"/>
        `,
        showCloseButton: true,
        showConfirmButton: false,
        allowOutsideClick: true,
        allowEscapeKey: true,
        willClose: () => {
            console.log("Swal closed, calling displayProductDetail");
            displayProductDetail(productId);
        },
    });
};
