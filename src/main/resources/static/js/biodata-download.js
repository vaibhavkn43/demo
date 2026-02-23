async function downloadImage() {
    try {
        disableButtons(true);
        showStatus("Generating image...");

        const node = document.getElementById("biodata-preview");

        if (!node) {
            alert("Preview not ready");
            return;
        }

        const dataUrl = await htmlToImage.toPng(node, {
            cacheBust: true,
            pixelRatio: 2,
            width: node.offsetWidth,
            height: node.offsetHeight,
            style: {
                margin: "0",
                transform: "scale(1)",
                transformOrigin: "top left"
            }
        });

        const link = document.createElement('a');
        link.download = 'biodata.png';
        link.href = dataUrl;
        link.click();

        showStatus("Image downloaded successfully ✅");

    } catch (e) {
        console.error(e);
        showStatus("Download failed ❌");
    } finally {
        setTimeout(() => {
            hideStatus();
            disableButtons(false);
        }, 1500);
    }
}
async function downloadPDF() {
    try {
        disableButtons(true);
        showStatus("Generating PDF...");

        const element = document.getElementById("biodata-preview");

        if (!element) {
            alert("Preview not ready");
            return;
        }

        const originalShadow = element.style.boxShadow;
        element.style.boxShadow = "none";

        await html2pdf().set({
            margin: 0,
            filename: 'biodata.pdf',
            image: { type: 'jpeg', quality: 1 },
            html2canvas: { scale: 2, useCORS: true, scrollY: 0 },
            jsPDF: { unit: 'mm', format: 'a4', orientation: 'portrait' },
            pagebreak: { mode: ['avoid-all', 'css', 'legacy'] }
        }).from(element).save();

        element.style.boxShadow = originalShadow;

        showStatus("PDF downloaded successfully ✅");

    } catch (e) {
        console.error(e);
        showStatus("PDF failed ❌");
    } finally {
        setTimeout(() => {
            hideStatus();
            disableButtons(false);
        }, 1500);
    }
}

async function shareOnWhatsApp() {
    try {
        disableButtons(true);
        showStatus("Preparing file for sharing...");

        const element = document.getElementById("biodata-preview");

        const dataUrl = await htmlToImage.toPng(element, {
            quality: 1,
            pixelRatio: 3
        });

        const blob = await (await fetch(dataUrl)).blob();
        const file = new File([blob], "biodata.png", { type: "image/png" });

        if (navigator.canShare && navigator.canShare({ files: [file] })) {
            showStatus("Opening share options...");
            await navigator.share({
                files: [file],
                title: "माझा बायोडाटा",
                text: "हा माझा बायोडाटा आहे"
            });
            showStatus("Shared successfully ✅");
        } else {
            showStatus("Sharing not supported ❌");
        }

    } catch (e) {
        console.error(e);
        showStatus("Sharing failed ❌");
    } finally {
        setTimeout(() => {
            hideStatus();
            disableButtons(false);
        }, 1500);
    }
}


function adjustPreviewScale() {
    const preview = document.getElementById("biodata-preview");
    const scaleWrapper = document.querySelector(".preview-scale");

    if (!preview || !scaleWrapper)
        return;

    const screenWidth = window.innerWidth;
    const originalWidth = 794;

    let scale;

    if (screenWidth < 768) {
        scale = (screenWidth - 20) / originalWidth;
    } else {
        scale = 0.75;
    }

    scaleWrapper.style.transform = `scale(${scale})`;
    scaleWrapper.style.transformOrigin = "top center";
}

window.addEventListener("load", adjustPreviewScale);
window.addEventListener("resize", adjustPreviewScale);



function showStatus(msg) {
    const el = document.getElementById("action-status");
    el.innerHTML = "⏳ " + msg;
    el.style.display = "block";
}

function hideStatus() {
    const el = document.getElementById("action-status");
    el.style.display = "none";
}

function disableButtons(disable = true) {
    document.querySelectorAll(".action-buttons button")
        .forEach(btn => {
            btn.disabled = disable;
            btn.style.opacity = disable ? "0.6" : "1";
        });
}