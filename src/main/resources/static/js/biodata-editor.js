document.getElementById("biodataForm").addEventListener("submit", function (e) {

    const requiredFields = [
        { name: "name", labelKey: "validation.required.name" },
        { name: "birthDate", labelKey: "validation.required.birth" },
        { name: "birthPlace", labelKey: "validation.required.birthPlace" },
        { name: "religion", labelKey: "validation.required.religion" },
        { name: "caste", labelKey: "validation.required.caste" },
        { name: "height", labelKey: "validation.required.height" },
        { name: "education", labelKey: "validation.required.education" },
        { name: "profession", labelKey: "validation.required.profession" },
        { name: "mobile", labelKey: "validation.required.mobile" },
        { name: "address", labelKey: "validation.required.address" }
    ];

    let missing = [];

    requiredFields.forEach(field => {
        const input = document.querySelector(`[name="${field.name}"]`);
        if (!input || !input.value.trim()) {
            missing.push(field.labelKey);
        }
    });

    if (missing.length > 0) {
        e.preventDefault(); // 🚫 BLOCK SUBMIT

        const box = document.getElementById("validationBox");
        const list = document.getElementById("validationList");

        list.innerHTML = "";

        missing.forEach(key => {
            const li = document.createElement("li");
            li.innerText = getMessage(key);
            list.appendChild(li);
        });

        box.classList.remove("d-none");
        box.scrollIntoView({ behavior: "smooth" });
    }
});

/* Helper: resolve i18n text from DOM */
function getMessage(key) {
    const el = document.querySelector(`[data-msg-key="${key}"]`);
    return el ? el.innerText : key;
}

