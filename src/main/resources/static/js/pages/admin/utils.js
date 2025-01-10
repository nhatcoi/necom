import { API, Utils, Alerts } from "../util/utils.js";

export async function verifyAdminAccess() {
    const url = API.urls.auth.info;

    try {
        const response = await fetch(url, {
            method: 'GET',
            headers: Utils.getAuthHeaders(),
        });

        if (!response.ok) throw new Error(`Access denied! Status: ${response.status}`);

        const { data: { roleNames } } = await response.json();

        if (roleNames.includes('ADMIN')) {
            Alerts.handleSuccessTimeCenter('You are authorized as ADMIN.');
        } else {
            Alerts.handleErrorTimeCenter('Access Denied - You are not authorized as ADMIN.');
            setTimeout(() => window.history.back(), 2500);
            throw new Error('Not authorized');
        }
    } catch (error) {
        Alerts.handleError('Access Denied', error.message || 'Unauthorized');
    }
}

const PAGINATION_TO_SHOW = 4;

export function renderPagination({ currentPage, totalPages }, url, paginationClass, paginationLinkClass) {
    const paginationDiv= document.querySelector(paginationClass);
    paginationDiv.innerHTML = "";

    const startPage = Math.floor(currentPage / PAGINATION_TO_SHOW) * PAGINATION_TO_SHOW;
    const endPage = Math.min(startPage + PAGINATION_TO_SHOW, totalPages);

    if (startPage > 0) paginationDiv.insertAdjacentHTML('beforeend', createPageLink(url, startPage - 1, '&laquo;', paginationLinkClass));
    for (let i = startPage; i < endPage; i++) {
        paginationDiv.insertAdjacentHTML('beforeend', createPageLink(url, i, i + 1, i === currentPage ? 'active' : '', paginationLinkClass));
    }
    if (endPage < totalPages) paginationDiv.insertAdjacentHTML('beforeend', createPageLink(url, endPage, '&raquo;', paginationLinkClass));
}

function createPageLink(url, page, text, active = "", paginationLinkClass) {
    return `<a href="#" class="${paginationLinkClass} ${active}" data-page="${page}" data-url="${url}">${text}</a>`;
}
