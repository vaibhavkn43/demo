let customIndex = 0;

document.addEventListener("DOMContentLoaded", function () {

    const msgContainer = document.getElementById("familyMessages");
    if (!msgContainer)
        return;

    window.msgBrotherName = msgContainer.dataset.brotherName;
    window.msgSisterName = msgContainer.dataset.sisterName;
    window.msgStatusSelect = msgContainer.dataset.statusSelect;
    window.msgMarried = msgContainer.dataset.married;
    window.msgUnmarried = msgContainer.dataset.unmarried;
    window.msgBrotherLabel = msgContainer.dataset.brotherLabel;
    window.msgSisterLabel = msgContainer.dataset.sisterLabel;

    const prefixBox = document.getElementById("prefixMessages");
    window.prefSelect = prefixBox.dataset.select;
    window.prefMr = prefixBox.dataset.mr;
    window.prefChi = prefixBox.dataset.chi;
    window.prefMiss = prefixBox.dataset.miss;
    window.prefMrs = prefixBox.dataset.mrs;
    window.prefShreemati = prefixBox.dataset.shreemati;

    // ---------- DEFAULT GOD ----------
    const godField = document.getElementById("godImageHidden");
    if (godField && !godField.value) {
        godField.value = "ganesh";
    }

    // ---------- DEFAULT MANTRA ----------
    const mantraField = document.getElementById("mantraHidden");
    if (mantraField && !mantraField.value) {
        mantraField.value = "|| श्री गणेशाय नमः ||";
    }

    /* ================= SUBMIT ================= */
    document.getElementById("submitBtn")?.addEventListener("click", function () {

        // 🔴 run validation first
        if (!runValidation()) {
            e.preventDefault();

            // scroll to first error
            const firstError = document.querySelector(".border-red-500");
            firstError?.scrollIntoView({behavior: "smooth", block: "center"});

            return;
        }

        mergePrefixWithNames();

        updateFamily("brothers", msgBrotherLabel, "brotherInputs");
        updateFamily("sisters", msgSisterLabel, "sisterInputs");



        const mamaCombined = collectMultiValues("mamaName", "mamaExtra");
        console.log(mamaCombined, "mamaCombined")
        document.querySelector('[name="mama"]').value = mamaCombined;

        const kakaCombined = collectMultiValues("kakaName", "kakaExtra");
        console.log(kakaCombined, "kakaCombined")
        document.querySelector('[name="kaka"]').value = kakaCombined;



        document.getElementById("biodataForm").submit();
    });

    /* ================= DATE ================= */
    ["birthDay", "birthMonth", "birthYear"].forEach(id => {
        document.getElementById(id)?.addEventListener("change", updateBirthDate);
    });

    ["birthHour", "birthMinute"].forEach(id => {
        document.getElementById(id)?.addEventListener("change", updateBirthTime);
    });

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

["name","birthDate","birthPlace","religion","caste","complexion","height",
 "education","fatherName","motherName","relatives","mobile","address"]
.forEach(id => {
    document.getElementById(id)?.addEventListener("blur", () => {
        validateField(id, "error-" + id);
    });
});

function validateField(inputId, errorId) {
    const input = document.getElementById(inputId);
    const error = document.getElementById(errorId);

    if (!input) return true;

    const value = input.value ? input.value.trim() : "";

    if (!value) {
        error?.classList.remove("hidden");
        input.classList.add("border-red-500");
        return false;
    } else {
        error?.classList.add("hidden");
        input.classList.remove("border-red-500");
        return true;
    }
}


function runValidation() {
    let valid = true;

    valid &= validateField("name", "error-name");
    valid &= validateField("birthDate", "error-birthDate");
    valid &= validateField("birthPlace", "error-birthPlace");
    valid &= validateField("religion", "error-religion");
    valid &= validateField("caste", "error-caste");
    valid &= validateField("complexion", "error-complexion");
    valid &= validateField("height", "error-height");
    valid &= validateField("education", "error-education");
    valid &= validateField("fatherName", "error-fatherName");
    valid &= validateField("motherName", "error-motherName");
    valid &= validateField("relatives", "error-relatives");
    valid &= validateField("mobile", "error-mobile");
    valid &= validateField("address", "error-address");

    return !!valid;
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

function renderPersonInputs(count, containerId, isBrother) {

    const container = document.getElementById(containerId);
    if (!container)
        return;

    container.innerHTML = "";

    for (let i = 1; i <= count; i++) {
        container.insertAdjacentHTML("beforeend", `
            <div class="grid grid-cols-1 md:grid-cols-12 gap-3 mb-3">

                <!-- PREFIX -->
                <div class="col-span-1 md:col-span-2">
                    <select class="prefix-select w-full border p-2 rounded bg-white">
                        <option value="">${prefSelect}</option>

                        ${isBrother ? `
                            <option value="${prefMr}">${prefMr}</option>
                            <option value="${prefChi}">${prefChi}</option>
                        ` : `
                            <option value="${prefMiss}">${prefMiss}</option>
                            <option value="${prefMrs}">${prefMrs}</option>
                            <option value="${prefShreemati}">${prefShreemati}</option>
                        `}
                    </select>
                </div>

                <!-- NAME INPUT -->
                <div class="col-span-1 md:col-span-5 ime-wrapper relative overflow-visible">
                    <input class="w-full border p-2 ime-marathi rounded"
                           data-name
                           placeholder="${isBrother ? msgBrotherName : msgSisterName}">

                    <div class="suggestion-dropdown hidden absolute left-0 right-0 top-full mt-1
                        bg-white border border-gray-200 rounded-lg shadow-lg z-[9999] max-h-48 overflow-auto">
                    </div>
                </div>

                <!-- STATUS -->
                <div class="col-span-1 md:col-span-5">
                    <select class="w-full border p-2 rounded" data-status>
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

        const row = input.closest(".grid"); // correct parent row
        const statusSelect = row ? row.querySelector("[data-status]") : null;

        const status = statusSelect ? statusSelect.options[statusSelect.selectedIndex].text : msgUnmarried;

        if (name) {
            values.push(`${name}(${status})`);
        }

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


function toggleSection(sectionId, iconId) {
    const section = document.getElementById(sectionId);
    const icon = document.getElementById(iconId);

    section.classList.toggle("hidden");

    if (section.classList.contains("hidden")) {
        icon.textContent = "+";
    } else {
        icon.textContent = "−";
    }
}



function toggleGuide() {
    const box = document.getElementById("guideContent");
    const text = document.getElementById("guideToggleText");
    const icon = document.getElementById("guideIcon");

    const isHidden = box.classList.toggle("hidden");

    const showText = text.dataset.show;
    const hideText = text.dataset.hide;

    text.innerText = isHidden ? showText : hideText;

    // change icon
    icon.innerText = isHidden ? "👁" : "🙈";

    localStorage.setItem("guideHidden", isHidden);
}

window.addEventListener("DOMContentLoaded", () => {
    const hidden = localStorage.getItem("guideHidden") === "true";

    if (hidden) {
        document.getElementById("guideContent")?.classList.add("hidden");
        document.getElementById("guideToggleText").innerText =
                document.getElementById("guideToggleText").dataset.show;
    }
});


// ===== GOD MODAL =====
function openGodModal() {
    document.getElementById("godModal").classList.remove("hidden");

    const selected = document.getElementById("godImageHidden")?.value || "";

    document.querySelectorAll("#godModal img").forEach(img => {
        if (img.getAttribute("data-value") === selected) {
            img.classList.add("ring-2", "ring-red-500");
        }
    });
}

function closeGodModal() {
    document.getElementById("godModal").classList.add("hidden");
}

// click select image
document.querySelectorAll("#godModal img").forEach(img => {
    img.addEventListener("click", function () {

        const value = this.getAttribute("data-value");

        document.getElementById("godImageHidden").value = value;
        document.getElementById("previewGodImage").src = "/images/gods/" + value + ".png";

        // remove previous selection
        document.querySelectorAll("#godModal img").forEach(i => {
            i.classList.remove("ring-2", "ring-red-500");
        });

        // highlight selected
        this.classList.add("ring-2", "ring-red-500");

        closeGodModal();
    });
});



// ===== MANTRA MODAL =====
function openMantraModal() {
    document.getElementById("mantraModal").classList.remove("hidden");
}
function closeMantraModal() {
    document.getElementById("mantraModal").classList.add("hidden");
}

function selectMantra(text) {

    document.getElementById("previewMantra").innerText = text;
    document.getElementById("mantraHidden").value = text;

    document.querySelectorAll(".mantra-option").forEach(el => {
        el.classList.remove("bg-gray-200");
    });

    event.target.classList.add("bg-gray-200");

    closeMantraModal();
}


function applyCustomMantra() {
    const val = document.getElementById("customMantraInput").value;
    if (!val)
        return;
    selectMantra(val);
}

document.addEventListener("click", function (e) {
    const btn = e.target.closest("[data-mantra]");
    if (!btn)
        return;
    const value = btn.getAttribute("data-mantra");
    selectMantra(value);
});


// ================= PHOTO TOGGLE =================

document.getElementById("photoYes")?.addEventListener("change", function () {
    document.getElementById("photoUploadSection").classList.remove("hidden");
});

document.getElementById("photoNo")?.addEventListener("change", function () {
    document.getElementById("photoUploadSection").classList.add("hidden");

    // clear preview if user selects No
    const preview = document.getElementById("photoPreview");
    const placeholder = document.getElementById("photoPlaceholder");

    if (preview)
        preview.src = "";
    if (placeholder)
        placeholder.classList.remove("hidden");
});

document.getElementById("photoInput")?.addEventListener("change", function (e) {

    const file = e.target.files[0];
    if (!file)
        return;

    const reader = new FileReader();

    reader.onload = function (evt) {
        const img = document.getElementById("photoPreview");
        const placeholder = document.getElementById("photoPlaceholder");

        img.src = evt.target.result;
        img.classList.remove("hidden");
        placeholder.classList.add("hidden");
    };

    reader.readAsDataURL(file);
});



// ================= MAMA ADD MORE =================
(function setupMamaDynamic() {

    const container = document.getElementById("mama-container");
    const addBtn = document.getElementById("add-mama");

    if (!container || !addBtn)
        return;

    addBtn.addEventListener("click", () => {

        const rows = container.querySelectorAll(".mama-row");

        if (rows.length >= 5) {
            alert("You can add maximum 5 Mama");
            return;
        }

        const firstRow = container.querySelector(".mama-row");
        const newRow = firstRow.cloneNode(true);

        newRow.querySelectorAll("input").forEach(i => i.value = "");

        const removeBtn = newRow.querySelector(".remove-row");
        removeBtn.classList.remove("hidden");

        removeBtn.onclick = () => newRow.remove();

        container.insertBefore(newRow, addBtn);
    });

})();

// ================= KAKA ADD MORE =================
(function setupKakaDynamic() {

    const container = document.getElementById("kaka-container");
    const addBtn = document.getElementById("add-kaka");

    if (!container || !addBtn)
        return;

    addBtn.addEventListener("click", () => {

        const rows = container.querySelectorAll(".kaka-row");

        // optional max limit
        if (rows.length >= 5) {
            alert("You can add maximum 5 Kaka");
            return;
        }

        const firstRow = container.querySelector(".kaka-row");
        const newRow = firstRow.cloneNode(true);

        newRow.querySelectorAll("input").forEach(i => i.value = "");

        const removeBtn = newRow.querySelector(".remove-row");
        removeBtn.classList.remove("hidden");

        removeBtn.onclick = () => newRow.remove();

        container.insertBefore(newRow, addBtn);
    });

})();

function collectMultiValues(nameField, extraField) {

    const names = document.querySelectorAll(`[name="${nameField}"]`);
    const extras = document.querySelectorAll(`[name="${extraField}"]`);

    let result = [];

    for (let i = 0; i < names.length; i++) {

        const name = names[i].value.trim();
        const extra = extras[i].value.trim();

        // skip empty rows
        if (!name)
            continue;

        // append with brackets if extra present
        if (extra) {
            result.push(`${name} (${extra})`);
        } else {
            result.push(name);
        }
    }

    // join with comma
    return result.join(", ");
}

function mergePrefixWithNames() {

    document.querySelectorAll(".prefix-select").forEach(select => {

        // go to parent flex container (more stable than ime-wrapper)
        const container = select.parentElement;

        if (!container)
            return;

        // find the first text input inside same container
        const input = container.querySelector("input[type='text'], input.ime-marathi");

        if (!input)
            return;

        const prefix = select.value ? select.value.trim() : "";
        const name = input.value ? input.value.trim() : "";

        if (!name)
            return;

        // prevent duplicate prefix
        if (prefix && !name.startsWith(prefix)) {
            input.value = prefix + " " + name;
        }
    });
}

// storing form details in local storage

// ===== CONFIG =====
const form = document.getElementById("biodataForm");
const templateId = document.querySelector("input[name='templateId']").value;
const STORAGE_KEY = `biodata_draft_${templateId}`;

// ===== SERIALIZE FORM =====
function getFormData(form) {
    const formData = new FormData(form);
    const data = {};

    formData.forEach((value, key) => {
        if (data[key]) {
            if (!Array.isArray(data[key])) {
                data[key] = [data[key]];
            }
            data[key].push(value);
        } else {
            data[key] = value;
        }
    });

    return data;
}

// ===== RESTORE FORM =====
function restoreFormData(form, data) {
    Object.keys(data).forEach(name => {
        const field = form.elements[name];
        if (!field)
            return;

        if (field instanceof RadioNodeList) {
            [...field].forEach(el => {
                if (el.value == data[name])
                    el.checked = true;
            });
        } else if (field.type === "checkbox") {
            field.checked = true;
        } else {
            field.value = data[name];
        }
    });
}

// ===== AUTO SAVE (debounced) =====
let timeout;

form.addEventListener("input", () => {
    clearTimeout(timeout);

    timeout = setTimeout(() => {
        const data = getFormData(form);
        localStorage.setItem(STORAGE_KEY, JSON.stringify(data));
        console.log("Draft auto-saved");
    }, 400);
});

// ===== RESTORE ON LOAD =====
window.addEventListener("DOMContentLoaded", () => {
    const saved = localStorage.getItem(STORAGE_KEY);

    if (saved) {
        const data = JSON.parse(saved);

        const shouldRestore = confirm("Resume your previous biodata draft?");
        if (shouldRestore) {
            restoreFormData(form, data);
        }
    }
});

// ===== CLEAR AFTER FINAL SUBMIT =====
form.addEventListener("submit", () => {
    localStorage.removeItem(STORAGE_KEY);
});

const urlParams = new URLSearchParams(window.location.search);
const isResume = urlParams.get("resume");

if (isResume === "true") {
    const saved = localStorage.getItem(STORAGE_KEY);
    if (saved) {
        restoreFormData(form, JSON.parse(saved));
    }
}


