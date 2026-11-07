
document.addEventListener("DOMContentLoaded", () => {

    const hiddenPages = [
        "/",
        "/index.html",
        "/login.html",
        "/register.html"
    ];

    // Don't show navbar on login and registration pages
    if (hiddenPages.includes(window.location.pathname)) {
        return;
    }

    // Create navbar
    const navbar = document.createElement("nav");
    navbar.className = "main-navbar";

    // Add navbar content
    navbar.innerHTML = `
        <a href="/dashboard.html" class="nav-logo">PrepMate</a>

        <div class="nav-links">
            <a href="/dashboard.html">Home</a>
            <a href="/subjects.html">Subjects</a>
            <a href="/history">Quiz History</a>
            <a href="/performance">Performance</a>
            <a href="/logout" class="logout-link">Logout</a>
        </div>
    `;

    // Add navbar to the page
    document.body.prepend(navbar);

});
