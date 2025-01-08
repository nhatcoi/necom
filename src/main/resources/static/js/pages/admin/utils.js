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
