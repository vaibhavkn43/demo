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

        updateFamily("brothers", msgBrotherLabel, "brotherInputs");
        updateFamily("sisters", msgSisterLabel, "sisterInputs");

//        if (!runValidation())
//            return;

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
function runValidation() {

    const required = ["name", "birthDate", "religion", "caste", "height", "education", "mobile", "address"];

    let missing = [];

    required.forEach(name => {
        const input = document.querySelector(`[name="${name}"]`);
        if (!input || !input.value.trim())
            missing.push(name);
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

function renderPersonInputs(count, containerId, isBrother) {

    const container = document.getElementById(containerId);
    if (!container)
        return;

    container.innerHTML = "";

    for (let i = 1; i <= count; i++) {
        container.insertAdjacentHTML("beforeend", `
            <div class="grid grid-cols-12 gap-2 mb-2">

                <div class="col-span-7 ime-wrapper relative">
                    <input class="w-full border p-2 ime-marathi"
                           data-name
                           placeholder="${isBrother ? msgBrotherName : msgSisterName}">

                    <!-- Suggestion dropdown -->
                    <div class="suggestion-dropdown hidden absolute z-50 bg-white border rounded shadow w-full mt-1 max-h-40 overflow-y-auto"></div>
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

    const isHidden = box.classList.toggle("hidden");

    const showText = text.dataset.show;
    const hideText = text.dataset.hide;

    text.innerText = isHidden ? showText : hideText;

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

    if (preview) preview.src = "";
    if (placeholder) placeholder.classList.remove("hidden");
});

document.getElementById("photoInput")?.addEventListener("change", function (e) {

    const file = e.target.files[0];
    if (!file) return;

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



