import { API, Utils, Alerts } from "../util/utils.js";
import { initializeManageUsers } from "./user.js";
// import { initializeManageProducts } from "./product.js";

const SELECTORS = {
    adminMenu: ".admin-menu .list-group-item",
    functionContent: "function-content", // id
};

document.addEventListener("DOMContentLoaded", () => {
    verifyAdminAccess();
    initializeAdminFunctions();
});

async function verifyAdminAccess() {
    const url = API.urls.auth.info;

    try {
        const response = await fetch(url, {
            method: "GET",
            headers: Utils.getAuthHeaders(),
        });

        if (!response.ok) throw new Error(`Access denied! Status: ${response.status}`);

        const { data: { roleNames } } = await response.json();

        if (roleNames.includes("ADMIN")) {
            Alerts.handleSuccessTimeCenter("You are authorized as ADMIN.");
        } else {
            Alerts.handleErrorTimeCenter("Access Denied - You are not authorized as ADMIN.");
            setTimeout(() => window.history.back(), 2500);
            throw new Error("Not authorized");
        }
    } catch (error) {
        Alerts.handleError("Access Denied", error.message || "Unauthorized");
    }
}

function initializeAdminFunctions() {
    const menuItems = document.querySelectorAll(SELECTORS.adminMenu);
    const contentArea = document.getElementById(SELECTORS.functionContent);

    menuItems.forEach((item) => {
        item.addEventListener("click", () => {
            const functionKey = item.dataset.function;
            contentArea.innerHTML = "";

            switch (functionKey) {
                case "manage-users":
                    initializeManageUsers(contentArea);
                    break;
                case "manage-products":
                    // initializeManageProducts(contentArea);
                    break;
                default:
                    contentArea.innerHTML = "<p>Function not implemented.</p>";
                    break;
            }
        });
    });
}
