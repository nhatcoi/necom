import { API } from "../util/utils";

const menuItems = document.querySelectorAll('.header__cart.user ul a');
// Lấy phần chứa nội dung
const contentContainer = document.querySelector('.user-content');

// Nội dung mẫu tương ứng với từng icon
const contentMap = {
    cart: window.location.href = API.urls.cart.base,
    shipping: '<p>Shipping details go here...</p>',
    favorites: '<p>Here are your favorite items...</p>',
    settings: '<p>Adjust your settings here...</p>',
};

// Hàm tạo nội dung DOM
function renderContent(contentId) {
    // Xóa nội dung cũ
    contentContainer.innerHTML = '';
    // Tạo nội dung mới
    if (contentMap[contentId]) {
        const newContent = document.createElement('div');
        newContent.innerHTML = contentMap[contentId];
        contentContainer.appendChild(newContent);
    }
}

// Thêm sự kiện click vào từng mục menu
menuItems.forEach(menuItem => {
    menuItem.addEventListener('click', event => {
        event.preventDefault(); // Ngăn điều hướng mặc định
        const contentId = menuItem.getAttribute('data-content');
        renderContent(contentId);
    });
});

function redirectToCart() {
    window.location.href = "/cart";
}
