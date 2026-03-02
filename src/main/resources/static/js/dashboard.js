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
  drafts.forEach(templateId => {
    const btn = document.createElement("button");

    btn.innerText = `Resume Biodata (${templateId})`;
    btn.className = "resume-btn";

    btn.onclick = () => {
      // redirect to form page with templateId
      window.location.href = `/form?templateId=${templateId}&resume=true`;
    };

    container.appendChild(btn);
  });
}