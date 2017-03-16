/* global self, caches*/
 
var silent = true, // disable output to console
  urlsToCache = {
    "unique_id": "#%ID%",
    "urls_to_cache": [
      %URLS%,
      "https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js",
      "https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.1/jquery-ui.min.js",
      "/scripts/jlatexmath/jlatexmath.js?v=1472544409"
    ]
  };
 
self.addEventListener('install', function (event) {
  "use strict";
  silent || console.info('[install] Started');
  event.waitUntil(
    caches
      .open(urlsToCache.unique_id)
      .then(function (cache) {
        silent || console.info('[install] Caches opened, adding GeoGebraWeb js files to cache');
        // check if cdn.geogebra.org version already exists!
        urlsToCache.urls_to_cache.forEach(function (curUrl) {
          var modUrl = curUrl.replace(/download.geogebra.org\/web\/5.0/, 'cdn.geogebra.org/apps');
          caches.match(modUrl).then(function (response) {
            if (response) {
              silent || console.log('[install] Resource with modified url already cached: ' + modUrl);
              return false;
            }
            silent || console.log('[install] Resource added to cache: ' + curUrl);
            return cache.add(curUrl);
          });
        });
      })
  );
});
 
self.addEventListener('fetch', function (event) {
  "use strict";
  event.respondWith(
    caches
      .match(event.request)
      .then(function (response) {
        // first attempt: find file in caches
        if (response) {
          silent || console.log("[fetch] Load resource with original url from cache: " + event.request.url);
          return response;
        }
        // second attempt: find file with different url in caches
        var modUrl = event.request.url.replace(/cdn.geogebra.org\/apps/, 'download.geogebra.org/web/5.0');
        if (urlsToCache.urls_to_cache.indexOf(modUrl) !== -1) {
          return caches
            .match(modUrl)
            .then(function (response) {
              if (response) {
                silent || console.log("[fetch] Url in list, load resource with overridden url from cache: " + modUrl);
                return response;
              }
              silent || console.log("[fetch] Overridden url in list, but no match in cached files: " + event.request.url);
              return fetch(event.request);
            });
        }
        // third attempt: fetch un-cached file from other place
        silent || console.log("[fetch] No match in cached files: " + event.request.url);
        return fetch(event.request);
      }).catch(function (reason) {
      silent || console.error(reason);
    })
  );
});
 
self.addEventListener('activate', function (event) {
  "use strict";
  silent || console.info("[activate]");
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