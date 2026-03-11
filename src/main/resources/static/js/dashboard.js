document.addEventListener("DOMContentLoaded", function () {

    const count = window.innerWidth < 768 ? 8 : 15;
    for (let i = 0; i < count; i++) {
        let petal = document.createElement("div");
        petal.className = "petal";

        petal.style.left = Math.random() * 100 + "vw";
        petal.style.animationDuration = (6 + Math.random() * 6) + "s";
        petal.style.opacity = Math.random();

        document.body.appendChild(petal);
    }

    document.querySelectorAll(".template-card").forEach(card => {

        card.addEventListener("click", function () {
            const id = this.dataset.id;
            const img = this.dataset.img;

            openTemplateModal(id, img);
        });

    });

});

function openTemplateModal(templateId, imageUrl) {
    const modal = document.getElementById("templateModal");
    const img = document.getElementById("templatePreviewImg");
    const btn = document.getElementById("useTemplateBtn");

    if (!modal || !img || !btn) {
        console.error("Modal elements not found");
        return;
    }

    img.src = imageUrl;
    btn.href = "/editor/" + templateId;

    modal.classList.remove("hidden");
}

function closeTemplateModal() {
    document.getElementById("templateModal").classList.add("hidden");
}


function toggleFaq(id) {
    const content = document.getElementById("faq-" + id);
    const icon = document.getElementById("icon-" + id);

    if (content.classList.contains("hidden")) {
        content.classList.remove("hidden");
        icon.innerHTML = "−";
    } else {
        content.classList.add("hidden");
        icon.innerHTML = "+";
    }
}
//Detect draft on Dashboard page
function getDraftTemplates() {
    const drafts = [];

    Object.keys(localStorage).forEach(key => {
        if (key.startsWith("biodata_draft_")) {
            const templateId = key.replace("biodata_draft_", "");
            drafts.push(templateId);
        }
    });

    return drafts;
}

const drafts = getDraftTemplates();
const container = document.getElementById("resumeContainer");

if (drafts.length > 0) {

    const templateId = drafts[0];   // just take first one

    const msgBox = document.getElementById("draftMessages");

    const title = msgBox.dataset.title;
    const subtitle = msgBox.dataset.subtitle;
    const resumeText = msgBox.dataset.resume;

    const card = document.createElement("div");

    card.className =
            "bg-white rounded-2xl shadow-md p-6 mb-8 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 border border-gray-100 hover:shadow-lg transition";

    const left = document.createElement("div");
    left.innerHTML = `
        <h4 class="font-semibold text-gray-800">📝 ${title}</h4>
        <p class="text-sm text-gray-500">${subtitle}</p>
    `;

    const right = document.createElement("button");
    right.innerText = resumeText;
    right.className =
            "bg-gradient-to-r from-red-600 to-pink-600 text-white px-5 py-2.5 rounded-lg text-sm font-medium hover:opacity-90 transition";

    right.onclick = () => {
        window.location.href = `/editor/${templateId}?resume=true`;
    };

    card.appendChild(left);
    card.appendChild(right);

    document.getElementById("resumeContainer").appendChild(card);
}