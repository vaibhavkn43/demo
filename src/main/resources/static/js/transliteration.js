let marathiEnabled = true;
let activeInput = null;
let activeWordRange = null;
let activeSuggestion = null;
let activeSuggestionIndex = -1;
let suggestionList = [];

document.getElementById("marathiToggle").addEventListener("change", e => {
    marathiEnabled = e.target.checked;
    hideSuggestions();
});

/* =====================================================
 MARATHI IME — EVENT DELEGATION (STATIC + DYNAMIC)
 ===================================================== */

document.addEventListener("keyup", async function (e) {

    const input = e.target;
    if (!input.classList.contains("ime-marathi"))
        return;

    if (!marathiEnabled)
        return;

    // ignore space / enter (handled in keydown)
    if (e.key === " " || e.key === "Enter")
        return;

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


document.addEventListener("keydown", function (e) {

    const input = e.target;
    if (!input.classList.contains("ime-marathi"))
        return;

    if (!marathiEnabled || !activeSuggestion)
        return;

    if (e.key === " " || e.key === "Enter") {

        if (activeSuggestionIndex < 0 || !suggestionList.length)
            return;

        e.preventDefault();

        const selected = suggestionList[activeSuggestionIndex];
        replaceWord(selected + " ");

        activeSuggestion = null;
        activeSuggestionIndex = -1;
    }
});
function moveSelection(dir) {

    const items = document.querySelectorAll(".suggestion-item");
    if (!items.length)
        return;

    items[activeSuggestionIndex]?.classList.remove("active");

    activeSuggestionIndex =
            (activeSuggestionIndex + dir + items.length) % items.length;

    items[activeSuggestionIndex].classList.add("active");
    activeSuggestion = items[activeSuggestionIndex].textContent;
}


async function fetchSuggestions(word) {
    const url =
            `https://inputtools.google.com/request?text=${encodeURIComponent(word)}&itc=mr-t-i0-und&num=6&cp=0&cs=1&ie=utf-8&oe=utf-8`;

    try {
        const res = await fetch(url);
        const data = await res.json();
        return data?.[1]?.[0]?.[1] || []; // Optional Chaining (?.) used because if data[1][0][1] can fail if data is not proper return undefined then undefined || []
    } catch {
        return [];
    }
}

function showSuggestions(list) {

    const box = document.getElementById("suggestionBox");
    const container = document.getElementById("suggestionList");

    container.innerHTML = "";
    suggestionList = list.slice(0, 9);
    activeSuggestionIndex = 0;

    if (!suggestionList.length) {
        hideSuggestions();
        return;
    }

    suggestionList.forEach((word, index) => {

        const div = document.createElement("div");
        div.className = "suggestion-item";
        div.textContent = word;

        if (index === 0) {
            div.classList.add("active");
            activeSuggestion = word;
        }

        div.onclick = () => {
            activeSuggestionIndex = index;
            activeSuggestion = word;
            replaceWord(word + " ");
        };

        container.appendChild(div);
    });

    box.classList.remove("d-none");
}



function hideSuggestions() {
    document.getElementById("suggestionBox").classList.add("d-none");
}

function replaceWord(selected) {
    if (!activeInput || !activeWordRange)
        return;

    const {start, end} = activeWordRange;
    const text = activeInput.value;

    const newText =
            text.slice(0, start) + selected + text.slice(end);

    activeInput.value = newText;

    // move cursor to end of inserted word
    const cursorPos = start + selected.length;
    activeInput.setSelectionRange(cursorPos, cursorPos);

    activeInput.focus();
    hideSuggestions();
}



let customIndex = 0;

function addCustomField() {
    const container = document.getElementById("customFields");

    const row = document.createElement("div");
    row.className = "row g-2 mb-2 align-items-center";

    row.innerHTML = `
        <div class="col-5">
            <input type="text"
                   class="form-control ime-marathi"
                   name="customFields[${customIndex}].label"
                   placeholder="माहिती नाव"
                   autocomplete="off">
        </div>

        <div class="col-6">
            <input type="text"
                   class="form-control ime-marathi"
                   name="customFields[${customIndex}].value"
                   placeholder="माहिती"
                   autocomplete="off">
        </div>

        <div class="col-1 text-end">
            <button type="button"
                    class="btn btn-sm btn-outline-danger"
                    onclick="this.closest('.row').remove()">
                ✕
            </button>
        </div>
    `;

    container.appendChild(row);
    customIndex++;
}

