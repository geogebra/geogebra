//just a dummy code to call, to see web workers supported or not.
self.addEventListener('message', function(e) {
  self.postMessage(e.data); // echo
});