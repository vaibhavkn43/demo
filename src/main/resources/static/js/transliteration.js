/* ============================================================
 Marathi Transliteration + Inline Suggestions
 ------------------------------------------------------------
 Features:
 ✔ English → Marathi transliteration
 ✔ Inline dropdown suggestions under input
 ✔ Arrow key navigation (↑ ↓)
 ✔ Enter / Space selection
 ✔ Auto hide on outside click
 ✔ Works with multiple inputs (.ime-marathi)
 ============================================================ */


/* ================= GLOBAL STATE ================= */

let marathiEnabled = true;            // toggle for enabling/disabling IME
let activeInput = null;               // currently focused input
let activeWordRange = null;           // start & end index of active word
let activeSuggestionIndex = -1;       // keyboard navigation index


/* ================= TOGGLE INIT ================= */

document.addEventListener("DOMContentLoaded", () => {

    const toggle = document.getElementById("marathiToggle");

    if (toggle) {
        toggle.addEventListener("change", (e) => {
            marathiEnabled = e.target.checked;
            hideSuggestions();
        });
    }

});


/* ================= CLICK OUTSIDE ================= */

document.addEventListener("click", function (e) {
    if (!e.target.closest(".ime-wrapper")) {
        hideSuggestions();
    }
});


/* ============================================================
 KEYUP EVENT  (Fetch suggestions while typing)
 ============================================================ */

document.addEventListener("keyup", async function (e) {

    const input = e.target;

    if (!input.classList.contains("ime-marathi"))
        return;
    if (!marathiEnabled)
        return;

    // 🚀 FIX: ignore navigation keys
    if (["ArrowDown", "ArrowUp", "Enter", " "].includes(e.key)) {
        return;
    }

    const cursor = input.selectionStart;
    const text = input.value || "";

    const left = text.slice(0, cursor);
    const match = left.match(/([a-zA-Z]+)$/);

    if (!match) {
        hideSuggestions();
        return;
    }

    const word = match[1];
    const start = cursor - word.length;
    const end = cursor;

    activeInput = input;
    activeWordRange = {start, end};

    activeSuggestionIndex = -1;

    const suggestions = await fetchSuggestions(word);
    showInlineSuggestions(input, suggestions);
});



/* ============================================================
 KEYDOWN EVENT  (Arrow navigation + select)
 ============================================================ */

document.addEventListener("keydown", function (e) {

    if (!activeInput)
        return;

    const wrapper = activeInput.closest(".ime-wrapper");
    const dropdown = wrapper.querySelector(".suggestion-dropdown");

    if (!dropdown)
        return;

    const items = dropdown.querySelectorAll("div");

    if (!items.length)
        return;


    /* ---------- ARROW DOWN ---------- */
    if (e.key === "ArrowDown") {
        e.preventDefault();

        activeSuggestionIndex =
                (activeSuggestionIndex + 1) % items.length;

        updateHighlight(items);
    }


    /* ---------- ARROW UP ---------- */
    else if (e.key === "ArrowUp") {
        e.preventDefault();

        activeSuggestionIndex =
                (activeSuggestionIndex - 1 + items.length) % items.length;

        updateHighlight(items);
    }


    /* ---------- ENTER ---------- */
    else if (e.key === "Enter") {
        if (activeSuggestionIndex >= 0) {
            e.preventDefault();

            items[activeSuggestionIndex].click();

// add space at cursor position
            const pos = activeInput.selectionStart;
            activeInput.value =
                    activeInput.value.slice(0, pos) + " " + activeInput.value.slice(pos);

            activeInput.setSelectionRange(pos + 1, pos + 1);

            hideSuggestions();
        }
    }


    /* ---------- SPACE ---------- */
    else if (e.key === " ") {
        if (activeSuggestionIndex >= 0) {
            e.preventDefault();

            items[activeSuggestionIndex].click();

// add space at cursor position
            const pos = activeInput.selectionStart;
            activeInput.value =
                    activeInput.value.slice(0, pos) + " " + activeInput.value.slice(pos);

            activeInput.setSelectionRange(pos + 1, pos + 1);

            activeInput.value += " ";

            hideSuggestions();
        }
    }
});


/* ============================================================
 FETCH SUGGESTIONS (Google Input Tools API)
 ============================================================ */

async function fetchSuggestions(word) {

    const url =
            `https://inputtools.google.com/request?text=${encodeURIComponent(word)}&itc=mr-t-i0-und&num=6`;

    try {
        const res = await fetch(url);
        const data = await res.json();

        return data?.[1]?.[0]?.[1] || [];

    } catch (err) {
        console.error("Suggestion fetch error:", err);
        return [];
    }
}


/* ============================================================
 SHOW INLINE DROPDOWN UNDER INPUT
 ============================================================ */

function showInlineSuggestions(input, suggestions) {

    const wrapper = input.closest(".ime-wrapper");
    const dropdown = wrapper.querySelector(".suggestion-dropdown");

    dropdown.innerHTML = "";

    // no suggestions → hide
    if (!suggestions || suggestions.length === 0) {
        dropdown.classList.add("hidden");
        return;
    }

    // build dropdown items
    suggestions.slice(0, 6).forEach((word, index) => {

        const div = document.createElement("div");

        div.className =
                "px-3 py-2 cursor-pointer hover:bg-gray-100";

        div.innerText = word;

        // click handler
        div.onclick = () => {

            const {start, end} = activeWordRange;
            const text = input.value;

            const newText =
                    text.slice(0, start) + word + text.slice(end);

            input.value = newText;

            // set cursor position after inserted word
            const pos = start + word.length;

            input.setSelectionRange(pos, pos);
            input.focus();

            dropdown.classList.add("hidden");
        };

        dropdown.appendChild(div);
    });
    dropdown.classList.remove("hidden");
    activeSuggestionIndex = 0;
    updateHighlight(dropdown.querySelectorAll("div"));
}


/* ============================================================
 HIDE ALL DROPDOWNS
 ============================================================ */

function hideSuggestions() {

    document.querySelectorAll(".suggestion-dropdown")
            .forEach(d => d.classList.add("hidden"));

    activeSuggestionIndex = -1;
}


/* ============================================================
 UPDATE HIGHLIGHT DURING KEY NAVIGATION
 ============================================================ */

function updateHighlight(items) {

    items.forEach((el, i) => {
        el.classList.remove("bg-gray-200");

        if (i === activeSuggestionIndex) {
            el.classList.add("bg-gray-200");
        }
    });

}
