/* ============================================================
 Marathi Transliteration + Inline Suggestions (Optimized)
 ============================================================ */

let marathiEnabled = true;
let activeInput = null;
let activeWordRange = null;
let activeSuggestionIndex = -1;

// 🔥 Cache to reduce API calls
const suggestionCache = new Map();

/* ============================================================
 INIT
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

document.addEventListener("click", function (e) {
    if (!e.target.closest(".ime-wrapper")) {
        hideSuggestions();
    }
});

/* ============================================================
 KEYUP → Word suggestions
 ============================================================ */

document.addEventListener("keyup", async function (e) {

    const input = e.target;
    if (!input.classList.contains("ime-marathi"))
        return;
    if (!marathiEnabled)
        return;
    if (["ArrowDown", "ArrowUp", "Enter", " "].includes(e.key))
        return;

    const cursor = input.selectionStart;
    const text = input.value || "";
    const left = text.slice(0, cursor);

    const match = left.match(/([a-zA-Z]+)(?=[^a-zA-Z]*$)/);

    if (!match) {
        hideSuggestions();
        return;
    }

    const word = match[1];
    const start = left.lastIndexOf(word);
    const end = start + word.length;

    activeInput = input;
    activeWordRange = {start, end};
    activeSuggestionIndex = -1;

    const normalized = normalizeSpecialCases(word);
    const suggestions = await fetchSuggestions(normalized);

    showInlineSuggestions(input, suggestions);
});

/* ============================================================
 KEYDOWN → Navigation
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

    if (e.key === "ArrowDown") {
        e.preventDefault();
        activeSuggestionIndex =
                (activeSuggestionIndex + 1) % items.length;
        updateHighlight(items);
    } else if (e.key === "ArrowUp") {
        e.preventDefault();
        activeSuggestionIndex =
                (activeSuggestionIndex - 1 + items.length) % items.length;
        updateHighlight(items);
    } else if (e.key === "Enter" || e.key === " ") {
        if (activeSuggestionIndex >= 0) {
            e.preventDefault();
            items[activeSuggestionIndex].click();

            const pos = activeInput.selectionStart;
            activeInput.value =
                    activeInput.value.slice(0, pos) + " " +
                    activeInput.value.slice(pos);

            activeInput.setSelectionRange(pos + 1, pos + 1);
            hideSuggestions();
        }
    }
});

/* ============================================================
 INPUT EVENT → Paste + Mobile Autofill Support
 ============================================================ */

document.addEventListener("input", async function (e) {

    const input = e.target;
    if (!input.classList.contains("ime-marathi"))
        return;
    if (!marathiEnabled)
        return;

    const text = input.value.trim();

    // Convert only if full English sentence detected
    if (/^[a-zA-Z\s]+$/.test(text) && text.includes(" ")) {

        // Avoid infinite loop (store flag)
        if (input.dataset.converted === "true") {
            input.dataset.converted = "false";
            return;
        }

        const words = text.split(/\s+/);
        let converted = [];

        for (let w of words) {
            const normalized = normalizeSpecialCases(w);
            const suggestions = await fetchSuggestions(normalized);
            converted.push(suggestions[0] || w);
        }

        input.dataset.converted = "true";
        input.value = converted.join(" ");
        hideSuggestions();
    }
});

/* ============================================================
 FETCH SUGGESTIONS (With Cache)
 ============================================================ */

async function fetchSuggestions(word) {

    if (suggestionCache.has(word)) {
        return suggestionCache.get(word);
    }

    const currentLang = document.documentElement.lang || "mr";
    if (currentLang === "en")
        return [];

    const itcCode = currentLang === "hi"
            ? "hi-t-i0-und"
            : "mr-t-i0-und";

    const url =
            `https://inputtools.google.com/request?text=${encodeURIComponent(word)}&itc=${itcCode}&num=6`;

    try {
        const res = await fetch(url);
        const data = await res.json();
        const result = data?.[1]?.[0]?.[1] || [];

        suggestionCache.set(word, result);
        return result;

    } catch (err) {
        console.error("Suggestion fetch error:", err);
        return [];
    }
}

/* ============================================================
 SHOW INLINE SUGGESTIONS
 ============================================================ */

function showInlineSuggestions(input, suggestions) {

    const wrapper = input.closest(".ime-wrapper");
    const dropdown = wrapper.querySelector(".suggestion-dropdown");
    dropdown.innerHTML = "";

    if (!suggestions || suggestions.length === 0) {
        dropdown.classList.add("hidden");
        return;
    }

    suggestions.slice(0, 6).forEach((word, index) => {

        const div = document.createElement("div");
        div.className = "px-3 py-2 cursor-pointer hover:bg-gray-100";
        div.innerText = word;

        div.onclick = () => {

            activeSuggestionIndex = index;

            const {start, end} = activeWordRange;
            const text = input.value;

            input.value =
                    text.slice(0, start) + word + " " + text.slice(end);

            const pos = start + word.length + 1; // +1 for space
            input.setSelectionRange(pos, pos);
            input.focus();

            dropdown.classList.add("hidden");
            activeSuggestionIndex = -1;
        };

        dropdown.appendChild(div);
    });

    dropdown.classList.remove("hidden");
    activeSuggestionIndex = 0;
    updateHighlight(dropdown.querySelectorAll("div"));
}

/* ============================================================
 HELPERS
 ============================================================ */

function hideSuggestions() {
    document.querySelectorAll(".suggestion-dropdown")
            .forEach(d => d.classList.add("hidden"));
    activeSuggestionIndex = -1;
}

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
            .replace(/x/g, "ksh")
            .replace(/Ksh/g, "Ksh")
            .replace(/kSh/g, "ksh");
}