document.addEventListener("DOMContentLoaded", () => {
    const videos = document.querySelectorAll(".js-video-toggle");

    const muteAndPauseAllExcept = (activeVideo) => {
        videos.forEach(video => {
            if (video !== activeVideo) {
                video.pause();
                video.muted = true;

                const btn = video.closest(".video-wrapper")
                                 .querySelector(".js-video-sound-toggle");
                if (btn) btn.textContent = "🔇";
            }
        });
    };

    videos.forEach(video => {
        const wrapper = video.closest(".video-wrapper");
        const toggleBtn = wrapper.querySelector(".js-video-sound-toggle");

        toggleBtn.addEventListener("click", (e) => {
            e.stopPropagation(); // don’t double-trigger video click

            const wasMuted = video.muted;

            muteAndPauseAllExcept(video);

            video.muted = !wasMuted;
            video.play();

            toggleBtn.textContent = video.muted ? "🔇" : "🔊";
        });
    });
});
