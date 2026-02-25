async function downloadImage() {
    try {
        disableButtons(true);
        showStatus("Generating HD Image...");

        const node = document.getElementById("biodata-preview");

        if (!node) {
            alert("Preview not ready");
            return;
        }

        const dataUrl = await htmlToImage.toPng(node, {
            cacheBust: true,
            // 🚀 QUALITY BOOST: Makes the image 2x sharper (Perfect for WhatsApp sharing)
            pixelRatio: 2, 
            width: 1033,
            height: 1540,
            style: {
                transform: "scale(1)",
                transformOrigin: "top left",
                margin: "0",
                padding: "0",
                left: "0",
                top: "0"
            }
        });

        const link = document.createElement('a');
        link.download = 'wedding-biodata.png';
        link.href = dataUrl;
        link.click();

        showStatus("HD Image downloaded ✅");

    } catch (e) {
        console.error("Image Export Error:", e);
        showStatus("Download failed ❌");
    } finally {
        setTimeout(() => {
            hideStatus();
            disableButtons(false);
        }, 1500);
    }
}
async function downloadPDF() {
    const element = document.getElementById("biodata-preview");
    
    const options = {
        margin: 0,
        filename: 'wedding-biodata.pdf',
        image: { type: 'jpeg', quality: 1.0 },
        html2canvas: { 
            scale: 2, 
            useCORS: true, 
            width: 1033, 
            height: 1540,
            scrollY: 0
        },
        jsPDF: { 
            unit: 'mm', 
            format: 'a4', 
            orientation: 'portrait' 
        }
    };

    // 🚀 NEW LOGIC: This forces the PDF to stay at 1 page
    html2pdf().set(options).from(element).toPdf().get('pdf').then(function (pdf) {
        const pageCount = pdf.internal.getNumberOfPages();
        if (pageCount > 1) {
            // Remove every page except the first one
            for (let i = pageCount; i > 1; i--) {
                pdf.deletePage(i);
            }
        }
    }).save();
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
        const file = new File([blob], "biodata.png", {type: "image/png"});

        if (navigator.canShare && navigator.canShare({files: [file]})) {
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
    const originalWidth = 1033; // Updated from 794

    let scale;
    if (screenWidth < 1033) { // Scale if screen is smaller than template
        scale = (screenWidth - 40) / originalWidth;
    } else {
        scale = 0.8; // Standard desktop preview size
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