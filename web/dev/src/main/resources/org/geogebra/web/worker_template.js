/* global self, caches*/
 
/**
 * Designed by Gabor, made by GWT
 */
var silent = true, // disable output to console
  urlsToCache = {
    "unique_id": "#%ID%",
    "urls_to_cache": [
      %URLS%
      "https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js",
      "https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.1/jquery-ui.min.js",
      "/scripts/jlatexmath/jlatexmath.js?v=1472544409"
    ]
  };
 
self.addEventListener('install', function (event) {
  "use strict";
  silent || console.log("[install]");
  event.waitUntil(
    caches
      .open(urlsToCache.unique_id)
      .then(function (cache) {
        silent || console.log('[install] Caches opened, adding GeoGebraWeb js files to cache');
        return cache.addAll(urlsToCache.urls_to_cache);
      })
      .then(function () {
        silent || console.log('[install] All required resources have been cached');
      })
  );
});
 
self.addEventListener('fetch', function (event) {
  "use strict";
  event.respondWith(
    caches
      .match(event.request)
      .then(function (response) {
        // first attempt to find file in caches
        if (response) {
          silent || console.log("[fetch] Match original url: " + event.request.url);
          return response;
        }
        // second attempt to find file in caches
        var url = event.request.url.replace(/cdn.geogebra.org\/apps/, 'download.geogebra.org/web/5.0');
        return caches
          .match(url)
          .then(function (response) {
            if (response) {
              return response;
            }
            silent || console.log("[fetch] Match overridden url: " + url);
            return fetch(url);
          });
      }).catch(function (reason) {
      silent || console.error(reason);
    })
  );
});
 
self.addEventListener('activate', function (event) {
  "use strict";
  silent || console.log("[activate]");
  event.waitUntil(
    caches
      .keys()
      .then(function (cacheNames) {
        return Promise.all(
          cacheNames.map(function (cacheName) {
            if (urlsToCache.unique_id !== cacheName) {
              silent || console.log("deleting from cache " + cacheName);
              return caches.delete(cacheName);
            }
          })
        );
      })
  );
});
