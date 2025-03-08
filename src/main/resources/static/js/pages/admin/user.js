import { API, Utils } from "../util/utils.js";
import { Alerts } from "./alerts.js";
import { renderPagination } from "./utils.js";

const SELECTORS = {
    userTableBodyId: 'user-table-body',
    paginationDiv: '.user__pagination.blog__pagination',
    paginationLinkClass: 'user-pagination-link',
    editBtn: '.edit-btn',
    USERS_PER_PAGE: 10
};


export function initializeManageUsers(contentArea) {
    contentArea.innerHTML = generateHtmlContent();
    attachSearchButtonListener();
    populateUserTable(API.urls.admin.users, 0);
}

document.addEventListener('click', function (event) {
    if (event.target.classList.contains(SELECTORS.paginationLinkClass)) {
        event.preventDefault();
        const { page, url } = event.target.dataset;
        populateUserTable(url, page);
    }
});

async function attachSearchButtonListener() {
    const searchButton = document.getElementById("button-search");
    if (searchButton) {
        searchButton.addEventListener("click", function () {
            const searchBox = document.getElementById("search-box");
            const searchValue = searchBox.value.trim();
            if (searchValue) searchUsers(searchValue)

            else populateUserTable(API.urls.admin.users, 0);
        });
    }
}

function searchUsers(searchValue) {
    const searchParams = new URLSearchParams({ search: searchValue });
    const urlSearchBase = `${API.urls.admin.users}/search?${searchParams.toString()}`;
    const urlSearch = `${urlSearchBase}&page=0&size=${SELECTORS.USERS_PER_PAGE}`;
    console.log(urlSearch);
    fetchUsers(urlSearch, urlSearchBase);
}

async function populateUserTable(urlBase, page) {
    let urlWithParams;
    // template  /users/search?search=user?page=1&size=3

    if(urlBase.includes("search?search=")) {
        urlWithParams = `${urlBase}&page=${page}&size=${SELECTORS.USERS_PER_PAGE}`;
    } else {
        urlWithParams = `${urlBase}?page=${page}&size=${SELECTORS.USERS_PER_PAGE}`;
    }

    await fetchUsers(urlWithParams, urlBase);
}

async function fetchUsers(urlWithParams, urlBase) {
    console.log("url with param: " + urlWithParams);
    try {
        const response = await fetch(urlWithParams, {
            method: "GET",
            headers: Utils.getAuthHeaders(),
        });

        if (!response.ok) throw new Error("Failed to fetch users");
        const { data: users, pagination } = await response.json();
        renderUsers(users);
        renderPagination(pagination, urlBase, SELECTORS.paginationDiv, SELECTORS.paginationLinkClass);
    } catch (error) {
        Alerts.handleError("Error loading users", error.message);
    }
}

function renderUsers(users) {
    const userTableBody = document.getElementById(SELECTORS.userTableBodyId);
    userTableBody.innerHTML = users.map(user => createUserRow(user)).join('');
    userTableBody.addEventListener("click", handleUserRowClick);
    document.querySelectorAll(SELECTORS.editBtn).forEach(button => {
        button.addEventListener("click", event => {
            event.stopPropagation();
            handleEditButtonClick(event);
        });
    });
}

function createUserRow(user) {
    return `
        <tr data-user-id="${user.id}">
            <td>${user.id}</td>
            <td>${user.fullName}</td>
            <td>${user.phoneNumber}</td>
            <td>${user.isActive}</td>
            <td>
                <button class="edit-btn btn btn-sm btn-primary">
                    <i class="fas fa-edit"></i>
                </button>
            </td>
        </tr>
    `;
}

function handleEditButtonClick(event) {
    const userId = event.target.closest("tr").dataset.userId;
    Swal.fire({
        title: `Update User ID: ${userId}`,
        html: generateModalHtml(),
        showCancelButton: true,
        confirmButtonText: "Update",
        preConfirm: () => getUpdatedUserData(),
    }).then(async (result) => {
        if (result.isConfirmed) {
            try {
                const updatedUser = result.value;
                const response = await updateUser(userId, updatedUser);
                updateRow(userId, await response.json());
                Swal.fire("Updated!", "User information has been updated.", "success");
            } catch (error) {
                Swal.fire("Error", error.message, "error");
            }
        }
    });
}

async function updateUser(userId, updatedUser) {
    const response = await fetch(`${API.urls.admin.users}/${userId}`, {
        method: "PUT",
        headers: {
            ...Utils.getAuthHeaders(),
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(updatedUser),
    });
    if (!response.ok) throw new Error("Failed to update user");
    return response;
}

function generateModalHtml() {
    return `
        <div style="display: flex; flex-direction: column; gap: 10px;">
            ${['password', 'phoneNumber', 'email', 'fullName', 'dateOfBirth'].map(field => generateInputField(field)).join('')}
            ${generateSelectField("isActive", "Is Active", ["true", "false"])}
        </div>
    `;
}

function generateInputField(id) {
    const labels = {
        password: "Password",
        phoneNumber: "Phone Number",
        email: "Email",
        fullName: "Full Name",
        dateOfBirth: "Date of Birth"
    };
    return `
        <div>
            <label for="${id}" style="font-weight: bold;">${labels[id]}:</label>
            <input id="${id}" type="${id === 'password' ? 'password' : 'text'}" class="swal2-input" placeholder="Enter ${labels[id].toLowerCase()}">
        </div>
    `;
}

function generateSelectField(id, label, options) {
    return `
        <div>
            <label for="${id}" style="font-weight: bold;">${label}:</label>
            <select id="${id}" class="swal2-input">
                ${options.map(option => `<option value="${option}">${option === 'true' ? 'Yes' : 'No'}</option>`).join('')}
            </select>
        </div>
    `;
}

function getUpdatedUserData() {
    const getValueOrNull = id => document.getElementById(id).value.trim() || null;
    return {
        password: getValueOrNull("password"),
        phoneNumber: getValueOrNull("phoneNumber"),
        email: getValueOrNull("email"),
        fullName: getValueOrNull("fullName"),
        dateOfBirth: getValueOrNull("dateOfBirth"),
        isActive: document.getElementById("isActive").value === "true",
    };
}

function updateRow(userId, data) {
    const row = document.querySelector(`tr[data-user-id='${userId}']`);
    const user = data.data;

    row.querySelector("td:nth-child(2)").textContent = user.fullName;
    row.querySelector("td:nth-child(3)").textContent = user.phoneNumber;
    row.querySelector("td:nth-child(4)").textContent = user.isActive.toString();
}

function handleUserRowClick(event) {
    const userId = event.target.closest("tr")?.dataset.userId;
    if (userId) displayUserDetail(userId);
}

async function displayUserDetail(userId) {
    try {
        const response = await fetch(`${API.urls.admin.users}/${userId}`, {
            method: "GET",
            headers: Utils.getAuthHeaders(),
        });
        if (!response.ok) throw new Error("Failed to fetch user details");

        const { data } = await response.json();
        showUserDetailModal(data, userId);
    } catch (error) {
        Alerts.handleError("Failed to fetch user details", error.message);
    }
}

function showUserDetailModal(data, userId) {
    Swal.fire({
        title: "User Details",
        html: generateUserDetailHtml(data),
        icon: "info",
        showCancelButton: true,
        confirmButtonText: "Delete",
        cancelButtonText: "Close",
    }).then(result => {
        if (result.isConfirmed) deleteUser(userId);
    });
}

function generateUserDetailHtml(data) {
    return `
        <p><strong>ID:</strong> ${data.id}</p>
        <p><strong>Full Name:</strong> ${data.fullName}</p>
        <p><strong>Username:</strong> ${data.username}</p>
        <p><strong>Phone Number:</strong> ${data.phoneNumber}</p>
        <p><strong>Email:</strong> ${data.email}</p>
        <p><strong>Date of Birth:</strong> ${data.dateOfBirth}</p>
        <ul>
            ${data.addresses.map(address => `
                <li>
                    <strong>Address Line:</strong> ${address.addressLine}<br>
                    <strong>City:</strong> ${address.city}<br>
                    <strong>District:</strong> ${address.district}<br>
                    <strong>Postcode:</strong> ${address.postcode}<br>
                    <strong>Country:</strong> ${address.country}
                </li>
            `).join('')}
        </ul>
        <p><strong>Roles:</strong> ${data.roleNames.join(', ')}</p>
    `;
}

async function deleteUser(userId) {
    const url = `${API.urls.admin.users}/${userId}`;
    try {
        const response = await fetch(url, {
            method: "DELETE",
            headers: Utils.getAuthHeaders(),
        });
        if (!response.ok) throw new Error("User deletion failed");

        Alerts.handleSuccess("User Deleted", "User successfully deleted.");
        document.querySelector(`tr[data-user-id="${userId}"]`).remove();
    } catch (error) {
        Alerts.handleError("Failed to delete user", error.message);
    }
}



function generateHtmlContent() {
    return `
        <h5 class="mb-4">Manage Users</h5>
        <div class="input-group mb-4">
            <input type="text" class="form-control" id="search-box" placeholder="Search users..." aria-label="Search users" aria-describedby="button-search">
            <button class="btn btn-primary" type="button" id="button-search">Search</button>
        </div>
        
        <table class="table table-bordered table-hover mb-4">
            <thead>
                <tr class="table-primary">
                    <th>ID</th>
                    <th>Full Name</th>
                    <th>Phone Number</th>
                    <th>Is Active</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody id="user-table-body"></tbody>
        </table>
        
        <div class="col-lg-12">
            <div class="user__pagination blog__pagination d-flex justify-content-center"></div>
        </div>
    `;
}
