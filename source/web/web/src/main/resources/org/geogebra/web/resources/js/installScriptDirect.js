// Installs the script directly, by simply appending a script tag with the
// src set to the correct location to the install location.
function installScript(filename) {
  // Provides the setupWaitForBodyLoad()function
  __WAIT_FOR_BODY_LOADED__

  function installCode(code) {
    var doc = getInstallLocationDoc();
    var docbody = doc.body;
    var script = doc.createElement('script');
    var debug = new URLSearchParams(location.search).get('GeoGebraDebug');
    var sourceMaps = debug != null;
    if (location.host && location.host.match(/localhost|apps-builds\./)) {
      sourceMaps = debug != 'false';
    }
    if (location.protocol.startsWith("http") && !sourceMaps) {
      // online strategy: fetch
      fetch(code, { cache: "force-cache" })
          .then(function(response) { return response.text(); })
          .then(function(text) {
            script.innerText = text;
            docbody.appendChild(script);
          });
    } else {
      // offline strategy: append to body
      script.src = code;
      docbody.appendChild(script);
    }

    sendStats('moduleStartup', 'scriptTagAdded');
  }

  // Start measuring from the time the caller asked for this file,
  // for consistency with installScriptEarlyDownload.js.
  // The elapsed time will include waiting for the body.
  sendStats('moduleStartup', 'moduleRequested');

  // Just pass along the filename so that a script tag can be installed in the
  // iframe to download it.  Since we will be adding the iframe to the body,
  // we still need to wait for the body to load before going forward.
  setupWaitForBodyLoad(function() {
    installCode(filename);
  });
}
