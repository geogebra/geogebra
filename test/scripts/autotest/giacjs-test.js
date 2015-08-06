// This PhantomJS script is a slightly modified version of webtest-template.js.
// Basically it loads the CasUnitTests.html within a timeout
// and exports its results to fatal.html and out.html.
// See giacjs-test.sh for more details. Note that a recent PhantomJS may be required.

// @author Zoltan Kovacs <zoltan@geogebra.org>

var system = require('system'),
    fs = require('fs');

if (system.args.length !== 4) {
    console.log('Usage: phantomjs-2.0.0 --ignore-ssl-errors=true giacjs-test.js begin end timeout_seconds');
    phantom.exit();
}

begin = system.args[1];
end = system.args[2];

starting_test = "Starting test ";

url = "https://autotest.geogebra.org/job/GeoGebra-autotest/lastSuccessfulBuild/artifact/web/war/CASUnitTests.html?_start=" +
    begin + "&_end=" + end + "&cb=jenkins"

// console.log("Running " + url);

timeout_seconds = system.args[3];
starting_test_len = starting_test.length;
var sem_exit = false;

function saveTables() {
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
}

function waitFor(testFx, onReady) {
    var maxtimeOutMillis = timeout_seconds * 1000,
        start = new Date().getTime(),
        condition = sem_exit,
        interval = setInterval(function() {
            if ( (new Date().getTime() - start < maxtimeOutMillis) && !condition ) {
                // If not time-out yet and condition not yet fulfilled
		condition = sem_exit;
            } else {
		console.log("Saving tables for " + begin + ".." + end);
		saveTables();
            }
        }, 1000); // repeat check every 1000ms
};

var page = require('webpage').create();

var curtest = 0;

page.onConsoleMessage = function(msg) {
// console.log(msg);
if (msg.search(starting_test) == 0) {
    curtest = msg.substring(starting_test_len);
    console.log("Test #" + curtest);
    return;
    }
if (msg.search("Cannot enlarge memory arrays.") == 0) {
    // end = curtest;
    // saveTables(); // output tables are not yet ready
    console.log("Ending " + curtest + " without output");
    if (curtest == end) {
	sem_exit = true;
	console.log("Assuming 'the end'");
	}
    }
if (msg.search("js giac output:") == 0) {
    // console.log("Ending " + curtest);
    if (curtest == end) {
	sem_exit = true;
	console.log("Assuming 'the end'");
	}
    }
}

page.onResourceRequested = function (request) {
};

page.onError = function (msg, trace) {
console.log("ERROR: " + msg);

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
