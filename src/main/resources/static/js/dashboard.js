document.addEventListener("DOMContentLoaded", function () {

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