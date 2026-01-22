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
    profileInput.value = "";
    profilePreview.style.display = "none";
    document.getElementById("removePhotoBtn")?.classList.add("d-none");
    updatePhotoButtons(false);

}

function updatePhotoButtons(hasPhoto) {
    const uploadBtn = document.getElementById("uploadPhotoBtn");
    if (!uploadBtn) return;

    uploadBtn.innerText = hasPhoto ? "Replace Photo" : "Upload Photo";
}

function adjustGodPreview(hasProfile) {
    const godImg = document.getElementById("godPreview");
    if (!godImg)
        return;

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

let cropper;
let cropImageEl = document.getElementById("cropperImage");
let cropModalEl = document.getElementById("photoCropModal");
let originalFile = null;
function openCropper(event) {
    const file = event.target.files[0];
    if (!file)
        return;
    originalFile = file;
    // Set image source
    cropImageEl.src = URL.createObjectURL(file);

    // Show modal
    const modal = new bootstrap.Modal(cropModalEl);
    modal.show();
}

// 🔑 INIT CROPper AFTER modal is visible
cropModalEl.addEventListener('shown.bs.modal', function () {

    if (cropper) {
        cropper.destroy();
        cropper = null;
    }

    cropper = new Cropper(cropImageEl, {
        aspectRatio: 1,
        viewMode: 1,

        dragMode: 'move',
        cropBoxMovable: false,
        cropBoxResizable: false,

        movable: true,
        zoomable: true,
        rotatable: false,

        background: false,
        responsive: true,
        autoCropArea: 1,

        zoomOnTouch: true,
        zoomOnWheel: true,
        wheelZoomRatio: 0.08,

        toggleDragModeOnDblclick: false
    });
});

// Cleanup on close
cropModalEl.addEventListener('hidden.bs.modal', function () {
    if (cropper) {
        cropper.destroy();
        cropper = null;
    }
});


function applyCroppedPhoto() {

    if (!cropper || !originalFile)
        return;

    const canvas = cropper.getCroppedCanvas({
        width: 400,
        height: 400,
        imageSmoothingQuality: "high"
    });

    canvas.toBlob(blob => {

        const croppedFile = new File(
                [blob],
                originalFile.name || "profile.png",
                {type: "image/png"}
        );

        const dataTransfer = new DataTransfer();
        dataTransfer.items.add(croppedFile);
        document.getElementById("profileImage").files = dataTransfer.files;

        const preview = document.getElementById("profilePreview");
        preview.src = URL.createObjectURL(croppedFile);
        preview.style.display = "inline-block";

        cropper.destroy();
        cropper = null;

        bootstrap.Modal
                .getInstance(document.getElementById("photoCropModal"))
                .hide();
    });
    document.getElementById("removePhotoBtn")?.classList.remove("d-none");
    updatePhotoButtons(true);

}
function toggleGuideFromContainer(event) {
    // Prevent double trigger if user clicks button itself
    if (event.target.closest("button")) return;

    const btn = document.querySelector(".biodata-guide button");
    if (btn) {
        toggleGuide(btn);
    }
}
