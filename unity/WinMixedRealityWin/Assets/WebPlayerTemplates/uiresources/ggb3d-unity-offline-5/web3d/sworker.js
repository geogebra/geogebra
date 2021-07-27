/* global self, caches*/
 
var silent = true, // disable output to console
  urlsToCache = {
    "unique_id": "#5.0.452.0:1523610710374",
    "urls_to_cache": [
      "https://download.geogebra.org/web/5.0/5.0.452.0/web3d/00D6B4AFC66EE0AC801D46BC7C72CD31.cache.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/68999323A9AE008195A76987E995D389.cache.png",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/clear.cache.gif",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/deferredjs/00D6B4AFC66EE0AC801D46BC7C72CD31/1.cache.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/deferredjs/00D6B4AFC66EE0AC801D46BC7C72CD31/2.cache.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/deferredjs/00D6B4AFC66EE0AC801D46BC7C72CD31/3.cache.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/deferredjs/00D6B4AFC66EE0AC801D46BC7C72CD31/4.cache.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/deferredjs/00D6B4AFC66EE0AC801D46BC7C72CD31/5.cache.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/deferredjs/00D6B4AFC66EE0AC801D46BC7C72CD31/6.cache.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/cyrillic/fonts/jlm_wnbx10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/cyrillic/fonts/jlm_wnbxti10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/cyrillic/fonts/jlm_wnr10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/cyrillic/fonts/jlm_wnss10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/cyrillic/fonts/jlm_wnssbx10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/cyrillic/fonts/jlm_wnssi10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/cyrillic/fonts/jlm_wnti10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/cyrillic/fonts/jlm_wntt10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/fonts/base/jlm_cmmi10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/fonts/base/jlm_cmmib10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/fonts/euler/jlm_eufb10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/fonts/euler/jlm_eufm10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/fonts/latin/jlm_cmr10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/fonts/latin/jlm_jlmbi10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/fonts/latin/jlm_jlmbx10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/fonts/latin/jlm_jlmi10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/fonts/latin/jlm_jlmr10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/fonts/latin/jlm_jlmsb10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/fonts/latin/jlm_jlmsbi10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/fonts/latin/jlm_jlmsi10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/fonts/latin/jlm_jlmss10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/fonts/latin/jlm_jlmtt10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/fonts/latin/optional/jlm_cmbx10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/fonts/latin/optional/jlm_cmbxti10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/fonts/latin/optional/jlm_cmssbx10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/fonts/latin/optional/jlm_cmssi10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/fonts/latin/optional/jlm_cmti10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/fonts/latin/optional/jlm_cmtt10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/fonts/maths/jlm_cmbsy10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/fonts/maths/jlm_msam10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/fonts/maths/jlm_msbm10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/fonts/maths/jlm_rsfs10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/fonts/maths/jlm_special.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/fonts/maths/jlm_stmary10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/fonts/maths/optional/jlm_dsrom10.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/greek/fonts/jlm_fcmbipg.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/greek/fonts/jlm_fcmbpg.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/greek/fonts/jlm_fcmripg.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/greek/fonts/jlm_fcmrpg.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/greek/fonts/jlm_fcsbpg.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/greek/fonts/jlm_fcsropg.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/greek/fonts/jlm_fcsrpg.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/font/greek/fonts/jlm_fctrpg.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/html/ggtcallback.html",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/html/opener.html",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/images/cursor_eraser.png",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/images/cursor_pen.png",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/js/gl-matrix-min.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/js/webfont.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/js/workercheck.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/js/zSpace.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/js/zipjs/deflate.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/js/zipjs/inflate.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/js/zipjs/z-worker.js",
"https://download.geogebra.org/web/5.0/5.0.452.0/web3d/web3d.nocache.js",
"https://cdn.geogebra.org/apps/deployggb.js?v=5.0.452.0",
"https://www.geogebra.org/graphing",
"https://www.geogebra.org/cas",
"https://www.geogebra.org/3d",
"https://www.geogebra.org/spreadsheet",
"https://www.geogebra.org/probability",
"https://www.geogebra.org/geometry",
"https://www.geogebra.org/exam",
      "https://cdn.geogebra.org/files/lib/jquery/1.11.1/jquery.min.js",
      "https://cdn.geogebra.org/files/lib/jqueryui/1.11.1/jquery-ui.min.js",
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
  // Use the service worker only for uls from the urls_to_cache array
  var modUrl = event.request.url.replace(/cdn.geogebra.org\/apps/, 'download.geogebra.org/web/5.0');
  if (urlsToCache.urls_to_cache.indexOf(event.request.url) !== -1 || urlsToCache.urls_to_cache.indexOf(modUrl) !== -1) {
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
            if (urlsToCache.unique_id !== cacheName) {
              silent || console.log("deleting from cache " + cacheName);
              return caches.delete(cacheName);
            }
          })
        );
      })
  );
});