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



function toggleMobileMenu() {
    const menu = document.getElementById("mobileMenu");
    menu.classList.toggle("hidden");
}
window.addEventListener('scroll', function() {
    const actionBox = document.querySelector(".action-buttons");

    if (window.scrollY > 50) {
        // When header shrinks (80px -> 64px), move box up by 16px
        if(actionBox) actionBox.style.top = "84px"; 
    } else {
        // Back to original position
        if(actionBox) actionBox.style.top = "100px";
    }
});