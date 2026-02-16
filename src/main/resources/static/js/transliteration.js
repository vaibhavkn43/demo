let marathiEnabled = true;
let activeInput = null;
let activeWordRange = null;
let activeSuggestionIndex = -1;
let suggestionList = [];

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("marathiToggle")?.addEventListener("change", e => {
        marathiEnabled = e.target.checked;
        hideSuggestions();
    });
});

/* ================= KEYUP ================= */
document.addEventListener("keyup", async function (e) {

    const input = e.target;
    if (!input.classList.contains("ime-marathi")) return;
    if (!marathiEnabled) return;

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

    const suggestions = await fetchSuggestions(word);
    showSuggestions(suggestions);
});

/* ================= KEYDOWN ================= */
document.addEventListener("keydown", function (e) {

    const input = e.target;
    if (!input.classList.contains("ime-marathi")) return;
    if (!marathiEnabled || !suggestionList.length) return;

    if (e.key === "ArrowDown") {
        e.preventDefault();
        moveSelection(1);
    }

    if (e.key === "ArrowUp") {
        e.preventDefault();
        moveSelection(-1);
    }

    if (e.key === "Enter" || e.key === " ") {
        e.preventDefault();
        const selected = suggestionList[activeSuggestionIndex] || suggestionList[0];
        replaceWord(selected + " ");
    }
});

/* ================= FETCH ================= */
async function fetchSuggestions(word) {
    const url =
        `https://inputtools.google.com/request?text=${encodeURIComponent(word)}&itc=mr-t-i0-und&num=6`;

    try {
        const res = await fetch(url);
        const data = await res.json();
        return data?.[1]?.[0]?.[1] || [];
    } catch {
        return [];
    }
}

/* ================= SHOW ================= */
function showSuggestions(list) {

    const box = document.getElementById("suggestionBox");
    const container = document.getElementById("suggestionList");

    if (!box || !container) return;

    container.innerHTML = "";
    suggestionList = list.slice(0, 8);
    activeSuggestionIndex = 0;

    suggestionList.forEach((word, index) => {

        const div = document.createElement("div");
        div.className = "cursor-pointer px-2 py-1 rounded hover:bg-gray-100";
        div.textContent = word;

        if (index === 0) div.classList.add("bg-gray-200");

        div.onclick = () => {
            activeSuggestionIndex = index;
            replaceWord(word + " ");
        };

        container.appendChild(div);
    });

    box.classList.remove("hidden");
}

/* ================= NAVIGATION ================= */
function moveSelection(dir) {

    const items = document.querySelectorAll("#suggestionList div");
    if (!items.length) return;

    items[activeSuggestionIndex]?.classList.remove("bg-gray-200");

    activeSuggestionIndex =
        (activeSuggestionIndex + dir + items.length) % items.length;

    items[activeSuggestionIndex].classList.add("bg-gray-200");
}

/* ================= REPLACE ================= */
function replaceWord(selected) {

    if (!activeInput || !activeWordRange) return;

    const {start, end} = activeWordRange;
    const text = activeInput.value;

    const newText = text.slice(0, start) + selected + text.slice(end);
    activeInput.value = newText;

    const pos = start + selected.length;
    activeInput.setSelectionRange(pos, pos);
    activeInput.focus();

    hideSuggestions();
}

/* ================= HIDE ================= */
function hideSuggestions() {
    document.getElementById("suggestionBox")?.classList.add("hidden");
}
