/* =====================================================
 I18N MESSAGES (from data-* bridge)
 ===================================================== */
const msgContainer = document.getElementById("familyMessages");

const msgBrotherName = msgContainer.dataset.brotherName;
const msgSisterName = msgContainer.dataset.sisterName;
const msgStatusSelect = msgContainer.dataset.statusSelect;
const msgMarried = msgContainer.dataset.married;
const msgUnmarried = msgContainer.dataset.unmarried;
const msgBrotherLabel = msgContainer.dataset.brotherLabel;
const msgSisterLabel = msgContainer.dataset.sisterLabel;

document.getElementById("submitBtn").addEventListener("click", function () {

    updateFamily("brothers", msgBrotherLabel, "brotherInputs");
    updateFamily("sisters", msgSisterLabel, "sisterInputs");

    if (!runValidation()) {
        return;
    }
    document.getElementById("biodataForm").submit();
});

function runValidation() {

    const requiredFields = [
        {name: "name", labelKey: "validation.required.name"},
        {name: "birthDate", labelKey: "validation.required.birth"},
        {name: "religion", labelKey: "validation.required.religion"},
        {name: "caste", labelKey: "validation.required.caste"},
        {name: "height", labelKey: "validation.required.height"},
        {name: "education", labelKey: "validation.required.education"},
        {name: "mobile", labelKey: "validation.required.mobile"},
        {name: "address", labelKey: "validation.required.address"}
    ];

    const missing = [];

    requiredFields.forEach(field => {
        const input = document.querySelector(`[name="${field.name}"]`);
        if (!input || !input.value.trim()) {
            missing.push(field.labelKey);
        }
    });

    if (missing.length > 0) {
        const box = document.getElementById("validationBox");
        const list = document.getElementById("validationList");

        list.innerHTML = "";
        missing.forEach(key => {
            const li = document.createElement("li");
            li.innerText = getMessage(key);
            list.appendChild(li);
        });

        box.classList.remove("d-none");
        box.scrollIntoView({behavior: "smooth"});
        return false;
    }
    return true;
}


function getMessage(key) {
    const el = document.querySelector(`[data-msg-key="${key}"]`);
    return el ? el.innerText : key;
}

function updateBirthDate() {
    const d = document.getElementById("birthDay").value;
    const m = document.getElementById("birthMonth").value;
    const y = document.getElementById("birthYear").value;

    if (d && m && y) {
        document.getElementById("birthDate").value = `${d}/${m}/${y}`;
    }
}

["birthDay", "birthMonth", "birthYear"]
        .forEach(id => document.getElementById(id)
                    .addEventListener("change", updateBirthDate));


function updateBirthTime() {
    const h = document.getElementById("birthHour").value;
    const m = document.getElementById("birthMinute").value;

    if (h !== "" && m !== "") {
        document.getElementById("birthTime").value = `${h}:${m}`;
    }
}

["birthHour", "birthMinute"]
        .forEach(id => document.getElementById(id)
                    .addEventListener("change", updateBirthTime));


function renderPersonInputs(count, containerId, isBrother) {

    const container = document.getElementById(containerId);
    container.innerHTML = "";

    for (let i = 1; i <= count; i++) {
        container.insertAdjacentHTML("beforeend", `
            <div class="row g-2 mb-2">
                <div class="col-7">
                    <input class="form-control ime-marathi"
                           data-name
                           placeholder="${isBrother ? msgBrotherName : msgSisterName}">
                </div>
                <div class="col-5">
                    <select class="form-select" data-status>
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
        const status = input.closest(".row")
                .querySelector("[data-status]").value || msgUnmarried;

        if (name) {
            values.push(`${name}(${status})`);
        }
    });

    document.getElementById(hiddenFieldId).value =
            values.length ? values.join(", ") : "";

}


/* Brothers */
document.getElementById("brotherCount").addEventListener("change", e => {
    renderPersonInputs(e.target.value, "brotherInputs", true);
    updateFamily("brothers", msgBrotherLabel, "brotherInputs");
});

/* Sisters */
document.getElementById("sisterCount").addEventListener("change", e => {
    renderPersonInputs(e.target.value, "sisterInputs", false);
    updateFamily("sisters", msgSisterLabel, "sisterInputs");
});

/* Update family text on input change */
document.addEventListener("input", () => {
    updateFamily("brothers", msgBrotherLabel, "brotherInputs");
    updateFamily("sisters", msgSisterLabel, "sisterInputs");
});

function selectGod(god) {
    document.querySelector("input[name='godImage']").value = god;
    document.querySelector(".god-preview img").src =
            "/images/gods/" + god + ".png";

    bootstrap.Modal.getInstance(
            document.getElementById('godModal')
            ).hide();
}

const profileInput = document.getElementById("profileImage");
const profilePreview = document.getElementById("profilePreview");

if (profileInput) {
    profileInput.addEventListener("change", function () {
        if (this.files && this.files[0]) {
            const reader = new FileReader();
            reader.onload = e => {
                profilePreview.src = e.target.result;
                profilePreview.style.display = "block";
            };
            reader.readAsDataURL(this.files[0]);
        }
    });
}

function removeProfilePhoto() {
    if (profileInput) {
        profileInput.value = "";
    }
    if (profilePreview) {
        profilePreview.src = "";
        profilePreview.style.display = "none";
    }
}

function adjustGodPreview(hasProfile) {
    const godImg = document.getElementById("godPreview");
    if (!godImg) return;

    godImg.style.maxHeight = hasProfile ? "80px" : "120px";
}

function toggleGuide(btn) {
    const content = document.getElementById("biodataGuideContent");
    const isHidden = content.style.display === "none";

    content.style.display = isHidden ? "block" : "none";

    const hideText = btn.getAttribute("data-hide");
    const showText = btn.getAttribute("data-show");

    btn.querySelector("span").innerText = isHidden ? hideText : showText;
}