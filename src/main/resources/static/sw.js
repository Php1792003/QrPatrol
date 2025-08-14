const CACHE_NAME = 'qr-patrol-cache-v7'; // Tăng phiên bản để kích hoạt lại
const STATIC_ASSETS = [
    '/',                      // Trang landing page
    '/login',                 // Trang đăng nhập
    '/manifest.json',
    '/img/lading.png',        // Ảnh của trang landing page
    '/vendor/fontawesome-free/css/all.min.css' // Ví dụ file css công khai
];

self.addEventListener('install', event => {
    console.log('[Service Worker] Install');
    event.waitUntil(
        caches.open(CACHE_NAME)
            .then(cache => {
                console.log('[Service Worker] Caching core assets');
                return cache.addAll(STATIC_ASSETS);
            })
            .then(() => self.skipWaiting())
    );
});

self.addEventListener('activate', event => {
    console.log('[Service Worker] Activate');
    event.waitUntil(
        caches.keys().then(cacheNames => {
            return Promise.all(
                cacheNames.map(cacheName => {
                    if (cacheName !== CACHE_NAME) {
                        console.log('[Service Worker] Clearing old cache:', cacheName);
                        return caches.delete(cacheName);
                    }
                })
            );
        }).then(() => self.clients.claim())
    );
});


self.addEventListener('fetch', event => {
    if (event.request.method !== 'GET' || !event.request.url.startsWith('http')) {
        return;
    }

    if (event.request.mode === 'navigate') {
        event.respondWith(
            fetch(event.request)
                .then(response => {
                    const responseToCache = response.clone();
                    caches.open(CACHE_NAME).then(cache => {
                        cache.put(event.request, responseToCache);
                    });
                    return response;
                })
                .catch(() => {
                    return caches.match(event.request);
                })
        );
        return;
    }

    event.respondWith(
        caches.match(event.request)
            .then(cachedResponse => {
                if (cachedResponse) {
                    return cachedResponse;
                }

                return fetch(event.request).then(networkResponse => {
                    return caches.open(CACHE_NAME).then(cache => {
                        // Lưu bản sao vào cache
                        cache.put(event.request, networkResponse.clone());
                        return networkResponse;
                    });
                });
            })
    );
});