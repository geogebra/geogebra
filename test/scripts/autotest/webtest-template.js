/**
 * This is a PhantomJS code template to get console log output.
 * Waits until the test condition is true or a timeout occurs.
 * Results 0 (condition match), 2 (timeout), 3 (exitstring match).
 * Based on "waitfor.js" in the PhantomJS documentation.
 * Warning: Take care of the $... variables which must be substituted
 * by parameter values.
 */
function waitFor(testFx, onReady, timeOutMillis) {
    var maxtimeOutMillis = timeOutMillis ? timeOutMillis : $TIMEOUT,
        start = new Date().getTime(),
        condition = false,
        interval = setInterval(function() {
            if ( (new Date().getTime() - start < maxtimeOutMillis) && !condition ) {
                // If not time-out yet and condition not yet fulfilled
                condition = (typeof(testFx) === "string" ? eval(testFx) : testFx()); //< defensive code
            } else {
                if(!condition) {
                    // If condition still not fulfilled (timeout but condition is 'false')
                    console.log("'waitFor()' timeout");
                    page.render("$OUTPUTPNG");
                    phantom.exit(2);
                } else {
                    // Condition fulfilled (timeout and/or condition is 'true')
                    console.log("'waitFor()' finished in " + (new Date().getTime() - start) + "ms.");
                    typeof(onReady) === "string" ? eval(onReady) : onReady(); //< Do what it's supposed to do once the condition is fulfilled
                    clearInterval(interval); //< Stop this interval
                }
            }
        }, $REPEAT); //< repeat check every 1000ms
};

var page = require('webpage').create();

var lastmsg = "";

page.onConsoleMessage = function(msg) {
        console.log("msg from webpage:"+msg);
        /* If there would be two messages twice, this could be a good exit condition.
        if (msg == lastmsg) {
                    page.render("$OUTPUTPNG");
                    phantom.exit(1);
        }
        lastmsg = msg;
        But unfortunately not, e.g. for CylinderInCone.ggb.
        */
        if (msg.search("$EXITSTRING") >= 0) {
                    page.render("$OUTPUTPNG");
                    phantom.exit(3);
        }

}

page.onResourceRequested = function (request) {
        console.log('Request ' + JSON.stringify(request, undefined, 4));
};

page.onError = function (msg, trace) {
    console.log(msg);
    trace.forEach(function(item) {
        console.log('  ', item.file, ':', item.line);
    })
};

page.open("$TESTURL", function (status) {
    // Check for page load success
    if (status !== "success") {
        console.log("Unable to access network");
    } else {
        // Wait for 'signin-dropdown' to be visible
        waitFor(function() {
            // Check in the page if a specific element is now visible
            return page.evaluate(function() {
                console.log(document.title);
                return $("#signin-dropdown").is(":visible");
            });
        }, function() {
           console.log("The sign-in dialog should be visible now.");
           phantom.exit(0);
        });
    }
});
