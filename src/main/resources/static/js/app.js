// App.js - Main JavaScript file for QR Patrol System

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    console.log('QR Patrol System - App initialized');

    // Initialize Preline UI components
    if (window.HSStaticMethods) {
        window.HSStaticMethods.autoInit();
        console.log('Preline UI components initialized');
    }

    // Initialize custom components
    initializeCustomComponents();
    initializeFormValidation();
    initializeToasts();
    initializeTheme();
});

// Custom component initialization
function initializeCustomComponents() {
    // Sidebar toggle for mobile
    const sidebarToggle = document.getElementById('sidebar-toggle');
    const sidebar = document.getElementById('sidebar');

    if (sidebarToggle && sidebar) {
        sidebarToggle.addEventListener('click', function() {
            sidebar.classList.toggle('-translate-x-full');
        });
    }

    // Auto-hide alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert:not(.alert-persistent)');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s ease-out';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 500);
        }, 5000);
    });

    // Confirm delete buttons
    const deleteButtons = document.querySelectorAll('[data-confirm-delete]');
    deleteButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            const message = this.getAttribute('data-confirm-delete') || 'Bạn có chắc chắn muốn xóa?';
            if (!confirm(message)) {
                e.preventDefault();
                return false;
            }
        });
    });

    // Loading states for forms
    const forms = document.querySelectorAll('form[data-loading]');
    forms.forEach(form => {
        form.addEventListener('submit', function() {
            const submitButton = this.querySelector('button[type="submit"]');
            if (submitButton) {
                const originalText = submitButton.innerHTML;
                submitButton.innerHTML = '<span class="loading-spinner mr-2"></span>Đang xử lý...';
                submitButton.disabled = true;

                // Re-enable after 10 seconds as fallback
                setTimeout(() => {
                    submitButton.innerHTML = originalText;
                    submitButton.disabled = false;
                }, 10000);
            }
        });
    });
}

// Form validation
function initializeFormValidation() {
    const forms = document.querySelectorAll('form[data-validate]');

    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            let isValid = true;

            // Required fields validation
            const requiredFields = this.querySelectorAll('[required]');
            requiredFields.forEach(field => {
                if (!field.value.trim()) {
                    showFieldError(field, 'Trường này là bắt buộc');
                    isValid = false;
                } else {
                    clearFieldError(field);
                }
            });

            // Email validation
            const emailFields = this.querySelectorAll('input[type="email"]');
            emailFields.forEach(field => {
                if (field.value && !isValidEmail(field.value)) {
                    showFieldError(field, 'Email không hợp lệ');
                    isValid = false;
                }
            });

            if (!isValid) {
                e.preventDefault();
                return false;
            }
        });

        // Real-time validation
        const inputs = form.querySelectorAll('input, textarea, select');
        inputs.forEach(input => {
            input.addEventListener('blur', function() {
                validateField(this);
            });

            input.addEventListener('input', function() {
                clearFieldError(this);
            });
        });
    });
}

// Field validation helpers
function validateField(field) {
    if (field.hasAttribute('required') && !field.value.trim()) {
        showFieldError(field, 'Trường này là bắt buộc');
        return false;
    }

    if (field.type === 'email' && field.value && !isValidEmail(field.value)) {
        showFieldError(field, 'Email không hợp lệ');
        return false;
    }

    clearFieldError(field);
    return true;
}

function showFieldError(field, message) {
    clearFieldError(field);

    field.classList.add('border-red-500', 'focus:border-red-500', 'focus:ring-red-500');

    const errorElement = document.createElement('p');
    errorElement.className = 'form-error field-error';
    errorElement.textContent = message;

    field.parentNode.appendChild(errorElement);
}

function clearFieldError(field) {
    field.classList.remove('border-red-500', 'focus:border-red-500', 'focus:ring-red-500');

    const existingError = field.parentNode.querySelector('.field-error');
    if (existingError) {
        existingError.remove();
    }
}

function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

// Toast notifications
function initializeToasts() {
    // Create toast container if it doesn't exist
    if (!document.getElementById('toast-container')) {
        const container = document.createElement('div');
        container.id = 'toast-container';
        container.className = 'fixed top-4 right-4 z-50 space-y-2';
        document.body.appendChild(container);
    }
}

function showToast(message, type = 'success', duration = 5000) {
    const container = document.getElementById('toast-container');

    const toast = document.createElement('div');
    toast.className = `max-w-xs bg-white border rounded-xl shadow-lg dark:bg-gray-800 dark:border-gray-700 transform transition-all duration-300 translate-x-full opacity-0`;

    const iconClasses = {
        success: 'text-green-500 fas fa-check-circle',
        error: 'text-red-500 fas fa-exclamation-circle',
        warning: 'text-yellow-500 fas fa-exclamation-triangle',
        info: 'text-blue-500 fas fa-info-circle'
    };

    const borderClasses = {
        success: 'border-green-200',
        error: 'border-red-200',
        warning: 'border-yellow-200',
        info: 'border-blue-200'
    };

    toast.classList.add(borderClasses[type] || borderClasses.info);

    toast.innerHTML = `
        <div class="flex p-4">
            <div class="flex-shrink-0">
                <i class="${iconClasses[type] || iconClasses.info}"></i>
            </div>
            <div class="ml-3">
                <p class="text-sm text-gray-700 dark:text-gray-400">${message}</p>
            </div>
            <div class="ml-auto">
                <button type="button" class="text-gray-400 hover:text-gray-600" onclick="this.closest('[role=\\'alert\\']').remove()">
                    <i class="fas fa-times"></i>
                </button>
            </div>
        </div>
    `;

    toast.setAttribute('role', 'alert');
    container.appendChild(toast);

    // Animate in
    setTimeout(() => {
        toast.classList.remove('translate-x-full', 'opacity-0');
    }, 100);

    // Auto remove
    setTimeout(() => {
        toast.classList.add('translate-x-full', 'opacity-0');
        setTimeout(() => {
            if (toast.parentNode) {
                toast.remove();
            }
        }, 300);
    }, duration);

    return toast;
}

// Theme management
function initializeTheme() {
    const themeToggle = document.getElementById('theme-toggle');
    const html = document.documentElement;

    // Load saved theme
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark' || (!savedTheme && window.matchMedia('(prefers-color-scheme: dark)').matches)) {
        html.classList.add('dark');
    }

    // Theme toggle functionality
    if (themeToggle) {
        themeToggle.addEventListener('click', function() {
            html.classList.toggle('dark');
            localStorage.setItem('theme', html.classList.contains('dark') ? 'dark' : 'light');
        });
    }
}

// Utility functions
function debounce(func, wait, immediate) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            timeout = null;
            if (!immediate) func(...args);
        };
        const callNow = immediate && !timeout;
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
        if (callNow) func(...args);
    };
}

function formatDate(date, format = 'dd/MM/yyyy') {
    const d = new Date(date);
    const day = String(d.getDate()).padStart(2, '0');
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const year = d.getFullYear();
    const hours = String(d.getHours()).padStart(2, '0');
    const minutes = String(d.getMinutes()).padStart(2, '0');

    return format
        .replace('dd', day)
        .replace('MM', month)
        .replace('yyyy', year)
        .replace('HH', hours)
        .replace('mm', minutes);
}

// Export functions for global use
window.showToast = showToast;
window.validateField = validateField;
window.formatDate = formatDate;