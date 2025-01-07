const SELECTORS = {
    userTableBody: 'user-table-body',
    paginationDiv: '.user__pagination.blog__pagination'
};

const PAGINATION_TO_SHOW = 5;

export function renderPagination({ currentPage, totalPages, hasPreviousPage, hasNextPage }, url) {
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

