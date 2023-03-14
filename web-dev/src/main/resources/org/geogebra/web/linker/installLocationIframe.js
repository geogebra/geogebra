var frameDoc;

function getInstallLocationDoc() {
  setupInstallLocation();
  return frameDoc;
}

// This function is left for compatibility
// and may be used by custom linkers
function getInstallLocation() {
  return getInstallLocationDoc().body;
}

function setupInstallLocation() {
  if (frameDoc) { return; }
  // Create the script frame, making sure it's invisible, but not
  // "display:none", which keeps some browsers from running code in it.
  var scriptFrame = $doc.createElement('iframe');
  scriptFrame.id = '__MODULE_NAME__';
  scriptFrame.style.cssText = 'position:absolute; width:0; height:0; border:none; left: -1000px;'
    + ' top: -1000px;';
  scriptFrame.tabIndex = -1;
  $doc.body.appendChild(scriptFrame);

  frameDoc = scriptFrame.contentWindow.document;

  // The following code is needed for proper operation in Firefox, Safari, and
  // Internet Explorer.
  //
  // In Firefox, this prevents the frame from re-loading asynchronously and
  // throwing away the current document.
  //
  // In IE, it ensures that the <body> element is immediately available.
  if (navigator.userAgent.indexOf("Chrome") == -1) {
    frameDoc.open();
    var doctype = (document.compatMode == 'CSS1Compat') ? '<!doctype html>' : '';
    frameDoc.write(doctype + '<html><head></head><body></body></html>');
    frameDoc.close();
  }
}