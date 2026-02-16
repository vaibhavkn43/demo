// ================= DOM READY =================
document.addEventListener("DOMContentLoaded", function () {
    initMarathiIME();
});

// ================= MARATHI IME =================
function initMarathiIME() {
    function tryInit() {
        if (!window.google || !google.inputtools) {
            setTimeout(tryInit, 200);
            return;
        }

        const ime = new google.inputtools.InputTools();
        ime.addLanguage("mr-t-i0-transliteration");

        document.querySelectorAll(".ime-marathi")
            .forEach(el => ime.makeTransliteratable(el));

        window._marathiIME = ime;

        console.log("✅ Marathi IME initialized");
    }

    tryInit();
}

// ================= LANGUAGE SWITCH =================
function changeLanguage(lang) {
    const url = new URL(window.location.href);
    url.searchParams.set('lang', lang);
    window.location.href = url.toString();
}

// ================= SUGGESTION BOX =================
function closeSuggestions() {
    const box = document.getElementById("suggestionBox");
    if (box) {
        box.classList.add("hidden");   // Tailwind instead of d-none
    }
}

// ================= GENERIC UTILITIES =================

// show element
function show(id) {
    const el = document.getElementById(id);
    if (el) el.classList.remove("hidden");
}

// hide element
function hide(id) {
    const el = document.getElementById(id);
    if (el) el.classList.add("hidden");
}

// toggle
function toggle(id) {
    const el = document.getElementById(id);
    if (el) el.classList.toggle("hidden");
}

// scroll top
function scrollTopSmooth() {
    window.scrollTo({ top: 0, behavior: "smooth" });
}

// toast message
function showToast(msg) {
    const toast = document.createElement("div");
    toast.innerText = msg;
    toast.className =
        "fixed bottom-5 right-5 bg-black text-white px-4 py-2 rounded shadow z-50";

    document.body.appendChild(toast);

    setTimeout(() => {
        toast.remove();
    }, 2500);
}
