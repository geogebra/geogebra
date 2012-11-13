/*
 * This is a PhantomJS code to get console log output.
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 * The following Ubuntu Linux packages are required to run it:
 *
 *   - phantomjs
 *   - xvfb-run
 */

var page = new WebPage();
page.onConsoleMessage = function(msg) {
        console.log("msg from webpage:"+msg);
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

console.log("processing: "+phantom.args[0]);

page.open("$TESTURL", function(status) {
        console.log("status: "+status);
        if (status !== "success") {
                console.log("Unable to load page");
        } else {
                page.render("$OUTPUTPNG");
                page.evaluate(function() {
                    console.log(document.title);
                });
        }
        phantom.exit();
});
