self.addEventListener("message", function(event) {
//	var msgobject = JSON.parse(msg);
//	if (typeof msgObject.loadCAS !== 'undefined') {
//	  msgObject.loadCAS();
//	}
    self.postMessage('TODO: load CAS msg from worker ' + event.data);
});