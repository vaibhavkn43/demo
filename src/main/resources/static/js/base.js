function changeLanguage(lang) {
    const url = new URL(window.location.href);
    url.searchParams.set('lang', lang);
    window.location.href = url.toString();
}

function toggleLangDropdown() {
    document.getElementById("langMenu").classList.toggle("hidden");
}

// close dropdown when clicking outside
document.addEventListener("click", function (e) {
    const dropdown = document.getElementById("langDropdown");

    if (!dropdown.contains(e.target)) {
        document.getElementById("langMenu").classList.add("hidden");
        
    }
});


