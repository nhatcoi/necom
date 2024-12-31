import {API, Utils, Alerts} from "../components/utils.js";

const USERS_IN_PAGE = 10;
const PAGINATION_TO_SHOW = 5;
const INITIAL_PAGE = 0

const SELECTORS = {
    adminMenu: '.admin-menu .list-group-item',
    functionContent: '#function-content',
    userTableBody: '#user-table-body',
    paginationDiv: '.user__pagination.blog__pagination'
};

document.addEventListener("DOMContentLoaded", () => {
    verifyAdminAccess();
    initAdminFunctions();
});


async function verifyAdminAccess() {
    const url = API.urls.auth.info;
    try {
        const response = await fetch(url, {
            method: 'GET',
            headers: Utils.getAuthHeaders(),
        });

        if (!response.ok) throw new Error(`Access denied! Status: ${response.status}`);

        const data = await response.json();
        const roles = data.data.roleNames;
        console.log(roles);
        if (roles.includes('ADMIN')) {
            console.log('Admin verified');
            Alerts.handleSuccessTimeCenter('You are authorized as ADMIN.');
        } else {
            Alerts.handleErrorTimeCenter('Access Denied - You are not authorized as ADMIN.');
            setTimeout(() => {
                window.history.back();
            }, 2500);
            throw new Error('Not authorized');
        }
    } catch (error) {
        Alerts.handleError('Access Denied', error.message || 'Unauthorized');
    }
}

function initAdminFunctions() {
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
    const urlGetUsersByPage = url + `?page=${page}&size=${USERS_IN_PAGE}`;
    const responseUser = await fetch(urlGetUsersByPage, {
        method: 'GET',
        headers: Utils.getAuthHeaders(),
    });


    const dataUser = await responseUser.json();
    const users = dataUser.data;
    const pagination = dataUser.pagination;

    const userTableBody = document.getElementById(SELECTORS.userTableBody);
    userTableBody.innerHTML = '';

    renderUser(users, userTableBody);
    renderPagination(pagination, url);


    userTableBody.addEventListener('click', (event) => {
        const clickedRow = event.target.closest('tr');
        if (clickedRow) {
            const userId = clickedRow.dataset.userId;
            const user = users.find(u => u.id == userId);
            displayUserDetail(user);
        }
    }); 
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


function renderUser(users, tableDOM) {
    users.forEach(user => {
        const row = document.createElement('tr');
        row.dataset.userId = user.id.toString();
        row.innerHTML = `
            <td>${user.id}</td>
            <td>${user.fullName}</td>
            <td>${user.phoneNumber}</td>
            <td>${user.isActive ? 'Active' : 'Inactive'}</td>
        `;
        tableDOM.appendChild(row);
    });
}

function renderPagination(pagination, url) {
    const paginationDiv = document.querySelector(SELECTORS.paginationDiv);
    paginationDiv.html('');

    const { currentPage, totalPages, hasPreviousPage, hasNextPage } = pagination;

    if (hasPreviousPage) {
        paginationDiv.append(createPageLink(url, currentPage - 1, '&laquo;', ''));
    }

    const startPage = Math.max(0, currentPage - Math.floor(PAGINATION_TO_SHOW / 2));
    const endPage = Math.min(totalPages, startPage + PAGINATION_TO_SHOW);

    for (let i = startPage; i < endPage; i++) {
        const activeClass = i === currentPage ? 'active' : '';
        paginationDiv.append(createPageLink(url, i, i + 1, activeClass));
    }

    if (hasNextPage) {
        paginationDiv.append(createPageLink(url, currentPage + 1, '&raquo;', ''));
    }
}

function createPageLink(url, page, text, active = '') {
    return `<a href="#" class="pagination-link ${active}" data-page="${page}" data-url="${url}">${text}</a>`;
}


// Function to display user details
function displayUserDetail(user) {
    Swal.fire({
        title: 'User Details',
        html: `
            <p><strong>ID:</strong> ${user.id}</p>
            <p><strong>Full Name:</strong> ${user.fullName}</p>
            <p><strong>Phone Number:</strong> ${user.phoneNumber}</p>
            <p><strong>Status:</strong> ${user.isActive ? 'Active' : 'Inactive'}</p>
        `,
        icon: 'info',
        showCancelButton: true,
        cancelButtonText: 'OK',
        confirmButtonColor: "#cf0000",
        confirmButtonText: 'Delete',

    }).then((result) => {
        if (result.isConfirmed) {
            deleteUser(user.id);
            Swal.fire({
                title: "Deleted!",
                text: "Your file has been deleted.",
                icon: "success"
            });
        }
    });
}

// Function to delete a user (example)
function deleteUser(userId) {
    // TODO: delete from server

    // Here, you can make an API call to delete the user from the backend
    console.log(`User with ID ${userId} has been deleted.`);
    // You can also remove the user from the DOM
    const rowToDelete = document.querySelector(`tr[data-user-id="${userId}"]`);
    if (rowToDelete) {
        rowToDelete.remove();
    }
}