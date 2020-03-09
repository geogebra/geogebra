var system = require('system');
var args = system.args;

if (args.length === 1) {
  console.log('Usage: phantomjs ./testurl.js <URL> [TIMEOUT]');
  // phantomjs ./testurl.js file:///`pwd`/FILENAME.html
  phantom.exit(1);
}

var url = args[1];

var timeout = 20000;
if (args.length === 3) {
    timeout = args[2] * 1000;
    }

var output = "tmp/testurl.png";
var repeatcheck = 1000;

/**
 * This is a PhantomJS code template to get console log output.
 * Waits until the test condition (exitstring match) is true or a timeout occurs.
 * Results in 2 (timeout) or 3 (exitstring match).
 * Based on "waitfor.js" in the PhantomJS documentation.
 */
function waitFor() {
    var maxtimeOutMillis = timeout,
        start = new Date().getTime(),
        condition = false,
        interval = setInterval(function() {
	    if ( (new Date().getTime() - start >= maxtimeOutMillis) ) {
		console.log("phantomjs: Timeout after " + timeout);
                page.render(output);
		phantom.exit(2);
		} else {
		// console.log("...tick...");
		}
	    }, repeatcheck);
};

var page = require('webpage').create();
page.viewportSize = { width: 1920, height: 1080 };

var casloaded = 0;

// Exitstring match condition.
page.onConsoleMessage = function(msg) {
        console.log(msg);
        if (msg.search("all CAS up") >= 0) {
            casloaded = 1;
            console.log("CAS loaded");
            page.evaluate(function() { return document.ggbApplet.reset(); });
        }
        if ((casloaded == 1) && msg.search("COMPARISON RESULT IS ") >= 0) {
            console.log("Data found");
            page.render(output);
            phantom.exit(3);
        }
}

page.onError = function (msg, trace) {
    console.log(msg);
    trace.forEach(function(item) {
        console.log('->', item.file, ':', item.line);
    })
};

page.open(url, function (status) {
    // Check for page load success
    if (status !== "success") {
        console.log("phantomjs: Unable to access network");
    } else {
        // Wait for an event...
        waitFor();
    }
});

