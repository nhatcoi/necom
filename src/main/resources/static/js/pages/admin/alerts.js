export const Alerts = {
    handleSuccess: (title, message) => Swal.fire({ icon: 'success', title, text: message }),
    handleError: (title, message) => Swal.fire({ icon: 'error', title, text: message }),
    handleSuccessTimeCenter: (message) => Swal.fire({ position: 'center', icon: 'success', title: message, timer: 2000, showConfirmButton: false }),
    handleErrorTimeCenter: (message) => Swal.fire({ position: 'center', icon: 'error', title: message, timer: 2000, showConfirmButton: false }),
};
