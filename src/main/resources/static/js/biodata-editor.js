/* =========================================================
 GLOBAL STATE
========================================================= */
let customIndex = 0;

/* =========================================================
 SAFE INIT
========================================================= */
document.addEventListener("DOMContentLoaded", function () {

    const msgContainer = document.getElementById("familyMessages");
    if (!msgContainer) return;

    window.msgBrotherName = msgContainer.dataset.brotherName;
    window.msgSisterName = msgContainer.dataset.sisterName;
    window.msgStatusSelect = msgContainer.dataset.statusSelect;
    window.msgMarried = msgContainer.dataset.married;
    window.msgUnmarried = msgContainer.dataset.unmarried;
    window.msgBrotherLabel = msgContainer.dataset.brotherLabel;
    window.msgSisterLabel = msgContainer.dataset.sisterLabel;

    /* ================= SUBMIT ================= */
    document.getElementById("submitBtn")?.addEventListener("click", function () {

        updateFamily("brothers", msgBrotherLabel, "brotherInputs");
        updateFamily("sisters", msgSisterLabel, "sisterInputs");

        if (!runValidation()) return;

        document.getElementById("biodataForm").submit();
    });

    /* ================= DATE ================= */
    ["birthDay", "birthMonth", "birthYear"].forEach(id => {
        document.getElementById(id)?.addEventListener("change", updateBirthDate);
    });

    ["birthHour", "birthMinute"].forEach(id => {
        document.getElementById(id)?.addEventListener("change", updateBirthTime);
    });

    /* ================= FAMILY ================= */
    document.getElementById("brotherCount")?.addEventListener("change", e => {
        renderPersonInputs(e.target.value, "brotherInputs", true);
    });

    document.getElementById("sisterCount")?.addEventListener("change", e => {
        renderPersonInputs(e.target.value, "sisterInputs", false);
    });

});

/* =========================================================
 VALIDATION
========================================================= */
function runValidation() {

    const required = ["name", "birthDate", "religion", "caste", "height", "education", "mobile", "address"];

    let missing = [];

    required.forEach(name => {
        const input = document.querySelector(`[name="${name}"]`);
        if (!input || !input.value.trim()) missing.push(name);
    });

    if (missing.length) {
        alert("Please fill required fields: " + missing.join(", "));
        return false;
    }
    return true;
}

/* =========================================================
 DATE / TIME
========================================================= */
function updateBirthDate() {
    const d = document.getElementById("birthDay")?.value;
    const m = document.getElementById("birthMonth")?.value;
    const y = document.getElementById("birthYear")?.value;

    if (d && m && y) {
        document.getElementById("birthDate").value = `${d}/${m}/${y}`;
    }
}

function updateBirthTime() {
    const h = document.getElementById("birthHour")?.value;
    const m = document.getElementById("birthMinute")?.value;

    if (h !== "" && m !== "") {
        document.getElementById("birthTime").value = `${h}:${m}`;
    }
}

/* =========================================================
 FAMILY
========================================================= */
function renderPersonInputs(count, containerId, isBrother) {

    const container = document.getElementById(containerId);
    if (!container) return;

    container.innerHTML = "";

    for (let i = 1; i <= count; i++) {
        container.insertAdjacentHTML("beforeend", `
            <div class="grid grid-cols-12 gap-2 mb-2">
                <div class="col-span-7">
                    <input class="w-full border p-2 ime-marathi"
                           data-name
                           placeholder="${isBrother ? msgBrotherName : msgSisterName}">
                </div>
                <div class="col-span-5">
                    <select class="w-full border p-2" data-status>
                        <option value="">${msgStatusSelect}</option>
                        <option value="Married">${msgMarried}</option>
                        <option value="Unmarried">${msgUnmarried}</option>
                    </select>
                </div>
            </div>
        `);
    }
}

function updateFamily(hiddenFieldId, labelText, containerId) {

    const rows = document.querySelectorAll(`#${containerId} [data-name]`);
    const values = [];

    rows.forEach(input => {
        const name = input.value.trim();
        const status = input.closest("div").querySelector("[data-status]").value || msgUnmarried;

        if (name) values.push(`${name}(${status})`);
    });

    document.getElementById(hiddenFieldId).value = values.join(", ");
}

/* =========================================================
 CUSTOM FIELD
========================================================= */
function addCustomField() {

    const container = document.getElementById("customFields");

    const row = document.createElement("div");
    row.className = "grid grid-cols-12 gap-2 mb-2";

    row.innerHTML = `
        <div class="col-span-5">
            <input class="w-full border p-2 ime-marathi"
                   name="customFields[${customIndex}].label"
                   placeholder="Label">
        </div>
        <div class="col-span-6">
            <input class="w-full border p-2 ime-marathi"
                   name="customFields[${customIndex}].value"
                   placeholder="Value">
        </div>
        <div class="col-span-1 text-right">
            <button type="button"
                    onclick="this.closest('.grid').remove()"
                    class="text-red-500">✕</button>
        </div>
    `;

    container.appendChild(row);
    customIndex++;
}

/* =========================================================
 PROFILE PREVIEW
========================================================= */
document.getElementById("profileImage")?.addEventListener("change", function () {
    const preview = document.getElementById("profilePreview");
    const file = this.files[0];

    if (file) {
        const reader = new FileReader();
        reader.onload = e => {
            preview.src = e.target.result;
            preview.style.display = "block";
        };
        reader.readAsDataURL(file);
    }
});
