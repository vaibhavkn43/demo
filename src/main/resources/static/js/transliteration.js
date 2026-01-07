let marathiEnabled = true;
let activeInput = null;
let activeWordRange = null;

document.getElementById("marathiToggle").addEventListener("change", e => {
    marathiEnabled = e.target.checked;
    hideSuggestions();
});

document.querySelectorAll(".ime-marathi").forEach(input => {

    input.addEventListener("keyup", async e => {
        if (!marathiEnabled)
            return;

        const cursor = input.selectionStart;
        const text = input.value;

        // find current word
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
});

async function fetchSuggestions(word) {
    const url =
            `https://inputtools.google.com/request?text=${encodeURIComponent(word)}&itc=mr-t-i0-und&num=6&cp=0&cs=1&ie=utf-8&oe=utf-8`;

    try {
        const res = await fetch(url);
        const data = await res.json();
        return data?.[1]?.[0]?.[1] || [];
    } catch {
        return [];
    }
}

function showSuggestions(list) {
    const box = document.getElementById("suggestionBox");
    const container = document.getElementById("suggestionList");

    container.innerHTML = "";

    if (!list.length) {
        hideSuggestions();
        return;
    }

    list.forEach(word => {
        const div = document.createElement("div");
        div.className = "suggestion-item";
        div.textContent = word;
        div.onclick = () => replaceWord(word);
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

    activeInput.value =
            text.slice(0, start) + selected + text.slice(end);

    activeInput.focus();
    hideSuggestions();
}