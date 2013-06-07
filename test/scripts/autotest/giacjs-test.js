// This PhantomJS script is a slightly modified version of webtest-template.js.
// Basically it loads the CasUnitTests.html (needed to pass as 1st argument)
// within a timeout (2nd argument) and exports its results to fatal.html and out.html.
// See giacjs-test.sh for more details. Note that a recent PhantomJS may be required.

// @author Zoltan Kovacs <zoltan@geogebra.org>

var system = require('system'),
    fs = require('fs');

if (system.args.length !== 3) {
    console.log('Usage: giactest.js url timeout_seconds');
    phantom.exit();
}

url = system.args[1];
timeout_seconds = system.args[2];

function waitFor(testFx, onReady) {
    var maxtimeOutMillis = timeout_seconds * 1000,
        start = new Date().getTime(),
        condition = false,
        interval = setInterval(function() {
            if ( (new Date().getTime() - start < maxtimeOutMillis) && !condition ) {
                // If not time-out yet and condition not yet fulfilled
                condition = (typeof(testFx) === "string" ? eval(testFx) : testFx()); //< defensive code
            } else {
                if(!condition) {
                    var fatal = page.evaluate(function () {
                        // Mike returns fatal errors in this DIV:
                        return document.getElementById('table').innerHTML;
                    });
                    var out = page.evaluate(function () {
                        // Mike returns normal errors in this DIV:
                        return document.getElementById('table2').innerHTML;
                    });
                    f = fs.open("fatal.html", "w");
                    f.write(fatal);
                    f.close();

                    f = fs.open("out.html", "w");
                    f.write(out);
                    f.close();

                    phantom.exit(2);
                } else {
                    // Condition fulfilled (timeout and/or condition is 'true')
                    typeof(onReady) === "string" ? eval(onReady) : onReady(); //< Do what it's supposed to do once the condition is fulfilled
                    clearInterval(interval); //< Stop this interval
                }
            }
        }, 1000); //< repeat check every 1000ms
};

var page = require('webpage').create();

var lastmsg = "";

page.onConsoleMessage = function(msg) {
}

page.onResourceRequested = function (request) {
};

page.onError = function (msg, trace) {
};

page.open(url, function (status) {
    // Check for page load success
    if (status !== "success") {
    } else {
        waitFor(function() {
            return page.evaluate(function() {
            });
        }, function() {
           phantom.exit(0);
        });
    }
});
