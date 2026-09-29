/**
 * VYAAPAAR E-COMMERCE CLIENT SCRIPT (Vanilla JavaScript)
 * Handles API integration, dynamic rendering, user session, and cart synchronization.
 */

const API_BASE = '/api';

// ==========================================================================
// SESSION MANAGEMENT & USER AUTH
// ==========================================================================
function getCurrentUser() {
    try {
        const userStr = localStorage.getItem('vyaapaar_user');
        return userStr ? JSON.parse(userStr) : null;
    } catch (e) {
        return null;
    }
}

function setCurrentUser(user) {
    if (user) {
        localStorage.setItem('vyaapaar_user', JSON.stringify(user));
    } else {
        localStorage.removeItem('vyaapaar_user');
    }
    updateNavState();
    updateCartBadge();
}

function logout() {
    setCurrentUser(null);
    showToast('You have been logged out successfully.', 'info');
    setTimeout(() => {
        window.location.href = '/index.html';
    }, 600);
}

function requireAuth(redirectUrl = window.location.pathname) {
    const user = getCurrentUser();
    if (!user) {
        showToast('Please log in to continue.', 'error');
        sessionStorage.setItem('auth_redirect', redirectUrl);
        setTimeout(() => {
            window.location.href = '/login.html';
        }, 500);
        return false;
    }
    return true;
}

// ==========================================================================
// UI & NAVBAR UPDATER
// ==========================================================================
function updateNavState() {
    const user = getCurrentUser();
    const authLinks = document.getElementById('nav-auth-links');
    if (!authLinks) return;

    if (user) {
        authLinks.innerHTML = `
            <a href="/orders.html" class="nav-link">📦 My Orders</a>
            <span class="user-pill">👤 ${escapeHtml(user.fullName.split(' ')[0])}</span>
            <button onclick="logout()" class="btn btn-outline btn-sm">Logout</button>
        `;
    } else {
        authLinks.innerHTML = `
            <a href="/login.html" class="nav-link">Login</a>
            <a href="/register.html" class="btn btn-primary btn-sm">Register</a>
        `;
    }
}

async function updateCartBadge() {
    const badge = document.getElementById('cart-badge');
    if (!badge) return;

    const user = getCurrentUser();
    if (!user) {
        badge.innerText = '0';
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/cart?userId=${user.userId}`);
        const data = await res.json();
        if (data && data.items) {
            const count = data.items.reduce((sum, item) => sum + item.quantity, 0);
            badge.innerText = count.toString();
        } else {
            badge.innerText = '0';
        }
    } catch (e) {
        badge.innerText = '0';
    }
}

function setupGlobalSearch() {
    const searchForm = document.getElementById('nav-search-form');
    const searchInput = document.getElementById('nav-search-input');
    if (searchForm && searchInput) {
        searchForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const query = searchInput.value.trim();
            if (query) {
                window.location.href = `/products.html?q=${encodeURIComponent(query)}`;
            } else {
                window.location.href = `/products.html`;
            }
        });
    }
}

// ==========================================================================
// TOAST NOTIFICATIONS
// ==========================================================================
function showToast(message, type = 'success') {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        container.className = 'toast-container';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    const icon = type === 'success' ? '✅' : (type === 'error' ? '⚠️' : 'ℹ️');
    toast.innerHTML = `<span>${icon}</span> <span>${escapeHtml(message)}</span>`;
    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(100%)';
        setTimeout(() => toast.remove(), 300);
    }, 3500);
}

// ==========================================================================
// FORMATTING HELPERS
// ==========================================================================
function formatCurrency(amount) {
    return '₹' + Number(amount || 0).toLocaleString('en-IN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
}

function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.innerText = text;
    return div.innerHTML;
}

function getProductIcon(categoryId) {
    switch (Number(categoryId)) {
        case 1: return '🎧'; // Electronics
        case 2: return '👕'; // Clothing & Fashion
        case 3: return '🍳'; // Home & Kitchen
        case 4: return '📚'; // Books & Stationery
        default: return '📦';
    }
}

// Global initialization on DOM ready
document.addEventListener('DOMContentLoaded', () => {
    updateNavState();
    updateCartBadge();
    setupGlobalSearch();
});
