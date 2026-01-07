document.addEventListener("DOMContentLoaded", function () {
    function initIME() {
        if (!window.google || !google.inputtools) {
            // wait until Google Input Tools is ready
            setTimeout(initIME, 200);
            return;
        }
        const ime = new google.inputtools.InputTools();
        ime.addLanguage("mr-t-i0-transliteration"); // Marathi

        document.querySelectorAll(".ime-marathi")
                .forEach(el => ime.makeTransliteratable(el));
        window._marathiIME = ime;
        console.log("Marathi IME initialized");
    }
    initIME();
});

function changeLanguage(lang) {
    const url = new URL(window.location.href);
    url.searchParams.set('lang', lang);
    window.location.href = url.toString();
}
