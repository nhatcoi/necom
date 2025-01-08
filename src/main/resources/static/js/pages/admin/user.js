import { API, Utils } from "../util/utils.js";
import { Alerts } from "./alerts.js";
import { USERS_IN_PAGE, renderPagination } from "./pagination.js";

export function initializeManageUsers(contentArea) {
    contentArea.innerHTML = `
        <h5>Manage Users</h5>
        <p>View, update, or delete user accounts.</p>
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
    const urlWithParams = `${url}?page=${page}&size=${USERS_IN_PAGE}`;

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
        `;
        userTableBody.appendChild(row);
    });

    userTableBody.addEventListener("click", handleUserRowClick);
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
