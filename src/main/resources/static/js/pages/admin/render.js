const SELECTORS = {
    userTableBody: 'user-table-body',
    paginationDiv: '.user__pagination.blog__pagination'
};

export function renderUsers(users) {
    const userTableBody = document.getElementById(SELECTORS.userTableBody);
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
}

export function renderPagination({ currentPage, totalPages, hasPreviousPage, hasNextPage }, url) {
    const paginationDiv = document.querySelector(SELECTORS.paginationDiv);
    paginationDiv.innerHTML = '';

    if (hasPreviousPage) paginationDiv.innerHTML += createPageLink(url, currentPage - 1, '&laquo;');

    const startPage = Math.max(0, currentPage - 2);
    const endPage = Math.min(totalPages, startPage + 5);

    for (let i = startPage; i < endPage; i++) {
        const isActive = i === currentPage ? 'active' : '';
        paginationDiv.innerHTML += createPageLink(url, i, i + 1, isActive);
    }

    if (hasNextPage) paginationDiv.innerHTML += createPageLink(url, currentPage + 1, '&raquo;');
}

function createPageLink(url, page, text, active = '') {
    return `<a href="#" class="pagination-link ${active}" data-page="${page}" data-url="${url}">${text}</a>`;
}
