import { API, Utils, Alerts } from "../util/utils.js";
import { verifyAdminAccess } from "./utils.js";
import { initializeManageUsers } from "./user.js";
import { initializeManageProducts } from "./product.js";

const SELECTORS = {
    adminMenu: ".admin-menu .list-group-item",
    functionContent: "function-content", // id
};

document.addEventListener("DOMContentLoaded", () => {
    verifyAdminAccess();
    initializeAdminFunctions();
});

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
                    initializeManageProducts(contentArea);
                    break;
                default:
                    contentArea.innerHTML = "<p>Function not implemented.</p>";
                    break;
            }
        });
    });
}
