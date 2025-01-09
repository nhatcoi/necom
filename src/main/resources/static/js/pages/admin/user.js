import { API, Utils } from "../util/utils.js";
import { Alerts } from "./alerts.js";
import { NUM_PER_PAGE, renderPagination } from "./pagination.js";

export function initializeManageUsers(contentArea) {
    contentArea.innerHTML = `
        <h5 class="mb-4">Manage Users</h5>
        
        <!-- Search box -->
        <div class="input-group mb-4">
            <input type="text" class="form-control" id="search-box" placeholder="Search users..." 
                   aria-label="Search users" aria-describedby="button-search">
            <button class="btn btn-primary" type="button" id="button-search">Search</button>
        </div>
        
        <!-- User table -->
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
        
        <!-- Pagination -->
        <div class="col-lg-12">
            <div class="user__pagination blog__pagination d-flex justify-content-center"></div>
        </div>
    `;

    populateUserTable(API.urls.admin.users, 0);
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

async function populateUserTable(url, page) {
    const urlWithParams = `${url}?page=${page}&size=${NUM_PER_PAGE}`;

    try {
        const userPagination = await fetch(urlWithParams, {
            method: "GET",
            headers: Utils.getAuthHeaders(),
        });

        if (!userPagination.ok) throw new Error("Failed to fetch users");

        const { data: users, pagination } = await userPagination.json();

        renderUsers(users);
        renderPagination(pagination, url);
    } catch (error) {
        Alerts.handleError("Error loading users", error.message);
    }
}

function renderUsers(users) {
    const userTableBody = document.getElementById("user-table-body");
    userTableBody.innerHTML = "";

    users.forEach((user) => {
        const row = document.createElement("tr");
        row.dataset.userId = user.id;
        row.innerHTML = `
            <td>${user.id}</td>
            <td>${user.fullName}</td>
            <td>${user.phoneNumber}</td>
            <td>${user.isActive}</td>
            <td>
                <button class="edit-btn btn btn-sm btn-primary">
                    <i class="fas fa-edit"></i>
                </button>
            </td>
        `;
        userTableBody.appendChild(row);
    });

    // Lắng nghe sự kiện click trên bảng
    userTableBody.addEventListener("click", handleUserRowClick);

    // Lắng nghe sự kiện click trên nút chỉnh sửa
    const editButtons = document.querySelectorAll(".edit-btn");
    editButtons.forEach((button) => {
        button.addEventListener("click", function(event) {
            event.stopPropagation();
            handleEditButtonClick(event);
        });
    });
}

function handleEditButtonClick(event) {
    const button = event.target.closest("button");
    const userId = button.closest("tr").dataset.userId;

    Swal.fire({
        title: `Update User ID: ${userId}`,
        html: generateModalHtml(),
        showCancelButton: true,
        confirmButtonText: "Update",
        preConfirm: () => getUpdatedUserData(),
    }).then(async (result) => {
        if (result.isConfirmed) {
            const updatedUser = result.value;
            try {
                const response = await fetch(`${API.urls.admin.users}/${userId}`, {
                    method: "PUT",
                    headers: {
                        ...Utils.getAuthHeaders(),
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(updatedUser),
                });

                if (!response.ok) {
                    const errorData = await response.json();
                    console.error("Error response:", errorData);
                    throw new Error("Failed to update user");
                }

                updateRow(userId, await response.json());
                Swal.fire("Updated!", "User information has been updated.", "success");
            } catch (error) {
                console.error("Error during update:", error);
                Swal.fire("Error", error.message, "error");
            }
        }
    });
}

function generateModalHtml() {
    return `
        <div style="display: flex; flex-direction: column; gap: 10px;">
            ${generateInputField("password", "Password", "password")}
            ${generateInputField("phoneNumber", "Phone Number", "text")}
            ${generateInputField("email", "Email", "email")}
            ${generateInputField("fullName", "Full Name", "text")}
            ${generateInputField("dateOfBirth", "Date of Birth", "date")}
            <div>
                <label for="isActive" style="display: block; font-weight: bold;">Is Active:</label>
                <select id="isActive" class="swal2-input">
                    <option value="true">Yes</option>
                    <option value="false">No</option>
                </select>
            </div>
        </div>
    `;
}

function generateInputField(id, label, type) {
    return `
        <div>
            <label for="${id}" style="display: block; font-weight: bold;">${label}:</label>
            <input id="${id}" type="${type}" class="swal2-input" placeholder="Enter ${label.toLowerCase()}">
        </div>
    `;
}

function getUpdatedUserData() {
    const getValueOrNull = (id) => {
        const value = document.getElementById(id).value.trim();
        return value === "" ? null : value;
    };

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

    const fullNameCell = row.querySelector("td:nth-child(2)");
    const phoneNumberCell = row.querySelector("td:nth-child(3)");
    const isActiveCell = row.querySelector("td:nth-child(4)")

    if (fullNameCell.textContent !== user.fullName) {
        fullNameCell.textContent = user.fullName;
    }

    if (phoneNumberCell.textContent !== user.phoneNumber) {
        phoneNumberCell.textContent = user.phoneNumber;
    }

    if (isActiveCell.textContent !== user.isActive.toString()) {
        isActiveCell.textContent = user.isActive.toString();
    }
}

function handleUserRowClick(event) {
    const row = event.target.closest("tr");
    if (!row) return;

    const userId = row.dataset.userId;
    displayUserDetail(userId);
}

async function displayUserDetail(userId) {
    console.log(`${API.urls.admin.users}/${userId}`);
    const response = await fetch(`${API.urls.admin.users}/${userId}`, {
        method: "GET",
        headers: Utils.getAuthHeaders(),
    });
    if (!response.ok) throw new Error("Failed to fetch user details");

    const responseUser = await response.json();
    const  data = responseUser.data;
    console.log(responseUser);
    Swal.fire({
        title: "User Details",
        html: `
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
        `,
        icon: "info",
        showCancelButton: true,
        confirmButtonText: "Delete",
        cancelButtonText: "Close",
    }).then((result) => {
        if (result.isConfirmed) deleteUser(userId);
    });
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
