/* global self, caches*/
 
var silent = true, // disable output to console
  cacheConfig = {
    "unique_id": "#%ID%",
    "urls_to_cache": [
      %URLS%
    ]
  };
 
function loadIntoCache(cache) {
  silent || console.info('[install] Caches opened, adding GeoGebraWeb js files to cache');
  // check if cdn.geogebra.org version already exists!
  cacheConfig.urls_to_cache.forEach(function (curUrl) {
    caches.match(curUrl).then(function (response) {
      if (response) {
        silent || console.log('[install] Resource already cached: ' + curUrl);
        return false;
      }
      silent || console.log('[install] Resource added to cache: ' + curUrl);
      return cache.add(curUrl);
    });
  });
}
        
self.addEventListener('install', function (event) {
  "use strict";
  silent || console.info('[install] Started');
  event.waitUntil(
    caches
      .open(cacheConfig.unique_id)
      .then(loadIntoCache)
  );
});
 
self.addEventListener('fetch', function (event) {
  "use strict";
  // Use the service worker only for uls from the urls_to_cache array
  if (cacheConfig.urls_to_cache.indexOf(event.request.url) !== -1) {
    event.respondWith(
      caches
        .match(event.request)
        .then(function (response) {
          // first attempt: find file in caches
          if (response) {
            silent || console.log("[fetch] Load resource with original url from cache: " + event.request.url);
            return response;
          }
          // second attempt: fetch un-cached file from other place
          silent || console.log("[fetch] No match in cached files: " + event.request.url);
          return fetch(event.request);
        }).catch(function (reason) {
        silent || console.error(reason);
      })
    );
  }
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
            if (cacheConfig.unique_id !== cacheName) {
              silent || console.log("deleting from cache " + cacheName);
              return caches.delete(cacheName);
            }
          })
        );
      })
  );
});