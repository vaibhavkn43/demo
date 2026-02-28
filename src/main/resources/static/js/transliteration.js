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

/* ============================================================
 GLOBAL STATE (shared across typing session)
 ============================================================ */

let marathiEnabled = true;            // toggle for enabling/disabling IME
let activeInput = null;               // currently focused input element
let activeWordRange = null;           // {start, end} of current word
let activeSuggestionIndex = -1;       // index of highlighted suggestion

/* ============================================================
 DOM READY → Initialize toggle listener
 ============================================================ */

document.addEventListener("DOMContentLoaded", () => {
    const toggle = document.getElementById("marathiToggle");
    if (toggle) {
        toggle.addEventListener("change", (e) => {
            marathiEnabled = e.target.checked;
            hideSuggestions();
        });
    }
});

/* ============================================================
 CLICK OUTSIDE → close suggestion dropdown
 ============================================================ */

document.addEventListener("click", function (e) {
    if (!e.target.closest(".ime-wrapper")) {
        hideSuggestions();
    }
});


/* ============================================================
 KEYUP EVENT
 Trigger: user types letters
 Purpose: detect English word and fetch Marathi suggestions
 ============================================================ */

document.addEventListener("keyup", async function (e) {

    const input = e.target;

    // allow only inputs with class .ime-marathi
    if (!input.classList.contains("ime-marathi"))
        return;

    // if toggle disabled, stop
    if (!marathiEnabled)
        return;

    // ignore navigation keys
    if (["ArrowDown", "ArrowUp", "Enter", " "].includes(e.key)) {
        return;
    }

    const cursor = input.selectionStart;
    const text = input.value || "";

    // get word before cursor
    const left = text.slice(0, cursor);

// 🔥 find last English word ignoring trailing commas/spaces
    const match = left.match(/([a-zA-Z]+)(?=[^a-zA-Z]*$)/);

    if (!match) {
        hideSuggestions();
        return;
    }

    const word = match[1];

// 🔥 get correct start index of that word
    const start = left.lastIndexOf(word);
    const end = start + word.length;

// 🔥 IMPORTANT: store actual word range only (not comma part)
    activeInput = input;
    activeWordRange = {start, end};
    activeSuggestionIndex = -1;

    // fetch suggestions from Google API
    const normalized = normalizeSpecialCases(word);
    const suggestions = await fetchSuggestions(normalized);

    // show dropdown suggestions
    showInlineSuggestions(input, suggestions);
});


/* ============================================================
 KEYDOWN EVENT
 Trigger: arrow keys / enter / space
 Purpose: navigate and select suggestions
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

            // simulate click on selected item
            items[activeSuggestionIndex].click();

            // add space after word
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

            // simulate click on selected item
            items[activeSuggestionIndex].click();

            const pos = activeInput.selectionStart;

            activeInput.value =
                    activeInput.value.slice(0, pos) + " " + activeInput.value.slice(pos);

            activeInput.setSelectionRange(pos + 1, pos + 1);

            hideSuggestions();
        }
    }

});


/* ============================================================
 FETCH SUGGESTIONS FROM GOOGLE INPUT TOOLS API
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

    // if no suggestions, hide dropdown
    if (!suggestions || suggestions.length === 0) {
        dropdown.classList.add("hidden");
        return;
    }

    // create suggestion items
    suggestions.slice(0, 6).forEach((word, index) => {

        const div = document.createElement("div");

        div.className = "px-3 py-2 cursor-pointer hover:bg-gray-100";
        div.innerText = word;

        // click handler (user selects suggestion)
        div.onclick = () => {

            activeSuggestionIndex = index;

            const {start, end} = activeWordRange;
            const text = input.value;

            const newText =
                    text.slice(0, start) + word + text.slice(end);

            input.value = newText;

            const pos = start + word.length;

            input.setSelectionRange(pos, pos);
            input.focus();

            dropdown.classList.add("hidden");

            // reset index to avoid override
            activeSuggestionIndex = -1;
        };

        dropdown.appendChild(div);
    });

    dropdown.classList.remove("hidden");

    // default highlight first suggestion
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
function normalizeSpecialCases(text) {
    return text
        .replace(/x/g, "ksh")   // convert x → ksh
        .replace(/Ksh/g, "Ksh")
        .replace(/kSh/g, "ksh");
}