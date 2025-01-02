import {API, Utils, Alerts} from "../components/utils.js";

const USERS_IN_PAGE = 10;
const PAGINATION_TO_SHOW = 5;
const INITIAL_PAGE = 0;
const SELECTORS = {
    adminMenu: '.admin-menu .list-group-item',
    functionContent: 'function-content', // id
    userTableBody: 'user-table-body', // id
    paginationDiv: '.user__pagination.blog__pagination'
};

document.addEventListener('DOMContentLoaded', () => {
    verifyAdminAccess();
    initializeAdminFunctions();
});

async function verifyAdminAccess() {
    const url = API.urls.auth.info;

    try {
        const response = await fetch(url, {
            method: 'GET',
            headers: Utils.getAuthHeaders(),
        });

        if (!response.ok) throw new Error(`Access denied! Status: ${response.status}`);

        const { data: { roleNames } } = await response.json();

        if (roleNames.includes('ADMIN')) {
            Alerts.handleSuccessTimeCenter('You are authorized as ADMIN.');
        } else {
            Alerts.handleErrorTimeCenter('Access Denied - You are not authorized as ADMIN.');
            setTimeout(() => window.history.back(), 2500);
            throw new Error('Not authorized');
        }
    } catch (error) {
        Alerts.handleError('Access Denied', error.message || 'Unauthorized');
    }
}

function initializeAdminFunctions() {
    const menuItems = document.querySelectorAll(SELECTORS.adminMenu);
    const contentArea = document.getElementById(SELECTORS.functionContent);

    // Map of function keys to their content
    const functionContents = {
        "manage-products": `<h5>Manage Products</h5><p>Add, edit, or delete products.</p>`,
        "manage-users": `<h5>Manage Users</h5><p>View, update, or delete user accounts.</p>
            <table class="table table-bordered table-hover">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Full Name</th>
                        <th>Phone Number</th>
                        <th>Is Active</th>
                    </tr>
                </thead>
                <tbody id="user-table-body"></tbody>
            </table>
            <div class="col-lg-12">
                <div class="user__pagination blog__pagination d-flex justify-content-center"></div>
            </div>`,
        "view-reports": `<h5>View Reports</h5><p>View system reports here.</p>`,
        "settings": `<h5>Settings</h5><p>Configure application settings.</p>`
    };


    // Add event listeners to menu items
    menuItems.forEach((item) => {
        item.addEventListener("click", () => {
            const functionKey = item.dataset.function;
            contentArea.innerHTML = functionContents[functionKey] || "<p>Function not implemented.</p>";

            if (functionKey === "manage-users") {
                populateUserTable(API.urls.admin.users, INITIAL_PAGE);
            }
        });
    });
}

async function populateUserTable(url, page) {
    const urlWithParams = `${url}?page=${page}&size=${USERS_IN_PAGE}`;

    try {
        const response = await fetch(urlWithParams, {
            method: 'GET',
            headers: Utils.getAuthHeaders(),
        });

        if (!response.ok) throw new Error('Failed to fetch users');

        const { data: users, pagination } = await response.json();

        renderUsers(users);
        renderPagination(pagination, url);
    } catch (error) {
        Alerts.handleError('Error loading users', error.message);
    }
}

document.addEventListener('click', function (event) {
    if (event.target.classList.contains('pagination-link')) {
        event.preventDefault();

        const page = event.target.dataset.page;
        console.log('click page: ' + page);
        const url = event.target.dataset.url;
        console.log('click url: ' + url);
        populateUserTable(url, page).then(r => console.log(r)).catch(error => handleError('Error loading products.'));
    }
});


function renderUsers(users) {
    const userTableBody = document.getElementById(SELECTORS.userTableBody);
    console.log('userTableBody: ' + userTableBody);
    userTableBody.innerHTML = '';

    users.forEach((user) => {
        const row = document.createElement('tr');
        row.dataset.userId = user.id;
        row.innerHTML = `
            <td>${user.id}</td>
            <td>${user.fullName}</td>
            <td>${user.phoneNumber}</td>
            <td>${user.isActive ? 'Active' : 'Inactive'}</td>`;
        userTableBody.appendChild(row);
    });

    userTableBody.addEventListener('click', handleUserRowClick);
}

function handleUserRowClick(event) {
    const row = event.target.closest('tr');
    if (!row) return;

    const userId = row.dataset.userId;
    displayUserDetail(userId);
}

function renderPagination({ currentPage, totalPages, hasPreviousPage, hasNextPage }, url) {
    const paginationDiv = document.querySelector(SELECTORS.paginationDiv);
    paginationDiv.innerHTML = '';

    if (hasPreviousPage) paginationDiv.innerHTML += createPageLink(url, currentPage - 1, '&laquo;');

    const startPage = Math.max(0, currentPage - Math.floor(PAGINATION_TO_SHOW / 2));
    const endPage = Math.min(totalPages, startPage + PAGINATION_TO_SHOW);

    for (let i = startPage; i < endPage; i++) {
        const isActive = i === currentPage ? 'active' : '';
        paginationDiv.innerHTML += createPageLink(url, i, i + 1, isActive);
    }

    if (hasNextPage) paginationDiv.innerHTML += createPageLink(url, currentPage + 1, '&raquo;');
}

function createPageLink(url, page, text, active = '') {
    return `<a href="#" class="pagination-link ${active}" data-page="${page}" data-url="${url}">${text}</a>`;
}


// Function to display user details
function displayUserDetail(userId) {
    // Example modal with SweetAlert
    Swal.fire({
        title: 'User Details',
        html: `
            <p><strong>ID:</strong> ${userId}</p>
            <!-- Add more user details here -->`,
        icon: 'info',
        showCancelButton: true,
        confirmButtonText: 'Delete',
        cancelButtonText: 'Close',
    }).then((result) => {
        if (result.isConfirmed) deleteUser(userId);
    });
}

function deleteUser(userId) {
    console.log(`User with ID ${userId} deleted.`);
    const rowToDelete = document.querySelector(`tr[data-user-id="${userId}"]`);
    if (rowToDelete) rowToDelete.remove();
}
