document.addEventListener('DOMContentLoaded', () => {
    let deferredPrompt;

    const installBanner = document.getElementById('pwa-install-banner');
    const installButton = document.getElementById('pwa-install-button');
    const dismissButton = document.getElementById('pwa-dismiss-button');
    const closeButton = document.getElementById('pwa-close-button');

    function hideInstallBanner() {
        if (installBanner) {
            installBanner.classList.add('hidden');
        }
    }


    function showInstallBanner() {
        if (installBanner) {
            installBanner.classList.remove('hidden');
            // Thêm hiệu ứng xuất hiện (tùy chọn)
            installBanner.classList.add('animate-bounce-in');
        }
    }

    window.addEventListener('beforeinstallprompt', (e) => {
        e.preventDefault();
        deferredPrompt = e;
        console.log('Sự kiện `beforeinstallprompt` đã được lưu lại.');

        showInstallBanner();
    });

    if (installButton) {
        installButton.addEventListener('click', async () => {
            hideInstallBanner();
            if (deferredPrompt) {
                deferredPrompt.prompt();
                const { outcome } = await deferredPrompt.userChoice;
                console.log(`Phản hồi của người dùng: ${outcome}`);
                deferredPrompt = null;
            }
        });
    }

    function dismissPrompt() {
        hideInstallBanner();
        console.log('Người dùng đã chọn để sau.');
    }
    if (dismissButton) dismissButton.addEventListener('click', dismissPrompt);
    if (closeButton) closeButton.addEventListener('click', dismissPrompt);



    window.addEventListener('appinstalled', () => {
        hideInstallBanner();
        deferredPrompt = null;
        console.log('PWA đã được cài đặt thành công.');
    });
});