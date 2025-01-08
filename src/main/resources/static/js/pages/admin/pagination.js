const SELECTORS = {
    userTableBody: 'user-table-body',
    paginationDiv: '.user__pagination.blog__pagination'
};

export const USERS_IN_PAGE = 10;
const PAGINATION_TO_SHOW = 5;

export function renderPagination({ currentPage, totalPages }, url) {
    const paginationDiv = document.querySelector(SELECTORS.paginationDiv);
    paginationDiv.innerHTML = "";

    const startPage = Math.floor(currentPage / PAGINATION_TO_SHOW) * PAGINATION_TO_SHOW;
    const endPage = Math.min(startPage + PAGINATION_TO_SHOW, totalPages);

    if (startPage > 0) paginationDiv.insertAdjacentHTML('beforeend', createPageLink(url, startPage - 1, '&laquo;'));
    for (let i = startPage; i < endPage; i++) {
        paginationDiv.insertAdjacentHTML('beforeend', createPageLink(url, i, i + 1, i === currentPage ? 'active' : ''));
    }
    if (endPage < totalPages) paginationDiv.insertAdjacentHTML('beforeend', createPageLink(url, endPage, '&raquo;'));
}

function createPageLink(url, page, text, active = "") {
    return `<a href="#" class="pagination-link ${active}" data-page="${page}" data-url="${url}">${text}</a>`;
}
