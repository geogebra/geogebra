/* global self, caches*/

/**
* Designed by Gabor, made by GWT
*/
var urlsToCache = {"urls_to_cache":[%URLS% "https:\/\/ajax.googleapis.com\/ajax\/libs\/jquery\/1.11.1\/jquery.min.js",
                                    "https:\/\/ajax.googleapis.com\/ajax\/libs\/jqueryui\/1.11.1\/jquery-ui.min.js",
                                    "\/scripts\/jlatexmath\/jlatexmath.js?v=1472544409",
                                    "\/scripts\/deployggb.js?v=1472544409"],"unique_id":"#%ID%"};

self.addEventListener('install', function(event) {
    "use strict";
    event.waitUntil(
        caches
            .open(urlsToCache.unique_id)
            .then(function(cache) {
                console.log('[install] Caches opened, adding GeoGebraWeb js files to cache' +
                    'to cache');
                return cache.addAll(urlsToCache.urls_to_cache);
            })
            .then(function() {
                console.log('[install] All required resources have been cached');
                return self.skipWaiting();
            })
    );
});

self.addEventListener('fetch', function(event) {
    "use strict";
    event.respondWith(
        caches.match(event.request)
            .then(function(response) {
                // Cache hit - return response
                if (response) {
                    console.log("from cache: " + response.url);
                    return response;
                }

                // IMPORTANT: Clone the request. A request is a stream and
                // can only be consumed once. Since we are consuming this
                // once by cache and once by the browser for fetch, we need
                // to clone the response
                    var fetchRequest = event.request.clone();

                    return fetch(fetchRequest).then(
                        function(response) {
                            // Check if we received a valid response
                            if(!response || response.status !== 200 || response.type !== 'basic') {
                                return response;
                            }

                            // IMPORTANT: Clone the response. A response is a stream
                            // and because we want the browser to consume the response
                            // as well as the cache consuming the response, we need
                            // to clone it so we have 2 stream.
                            if (urlsToCache.urls_to_cache.indexOf(response.url) > -1) {
                                var responseToCache = response.clone();

                                caches.open(urlsToCache.unique_id)
                                    .then(function (cache) {
                                        cache.put(event.request, responseToCache);
                                        console.log("put to cache: " + responseToCache.url);
                                    });
                            }

                            return response;
                        }
                ).catch(function(reason) {
                        console.log(reason);
                        return response;
                    });
            })
    );
});

self.addEventListener('activate', function(event) {
    "use strict";

    console.log("activate");

    event.waitUntil(
        caches.keys()
            .then(function(cacheNames) {
                return Promise.all(
                    cacheNames.map(function(cacheName) {
                        if (urlsToCache.unique_id !== cacheName) {
                            console.log("deleting from cache " + cacheName);
                            return caches.delete(cacheName);
                        }
                    })
                );
            })
            .then(self.clients.claim())
    );
});