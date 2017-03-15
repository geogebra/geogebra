/* global self, caches*/

/**
* Designed by Gabor, made by GWT
*/
var urlsToCache = {"urls_to_cache":[%URLS%, "https:\/\/ajax.googleapis.com\/ajax\/libs\/jquery\/1.11.1\/jquery.min.js",
                                    "https:\/\/ajax.googleapis.com\/ajax\/libs\/jqueryui\/1.11.1\/jquery-ui.min.js",
                                    "\/scripts\/jlatexmath\/jlatexmath.js?v=1472544409",
                                    "\/scripts\/deployggb.js?v=1472544409"],"unique_id":"#%ID%"};

self.addEventListener('install', function(event) {
    "use strict";
    console.log("install");
    event.waitUntil(
        caches
            .open(urlsToCache.unique_id)
            .then(function(cache) {
                console.log('[install] Caches opened, adding GeoGebraWeb js files to cache');
                return cache.addAll(urlsToCache.urls_to_cache);
            })
            .then(function() {
                console.log('[install] All required resources have been cached');
            })
    );
});

self.addEventListener('fetch', function(event) {
    "use strict";
    event.respondWith(
        caches.match(event.request)
            .then(function(response) {
                if (response) {
                    return response;
                }
                return fetch(event.request);
            }).catch(function(reason) {
                    console.log(reason);
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
    );
});