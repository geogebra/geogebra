// Taken from http://www.html5rocks.com/en/tutorials/pagevisibility/intro/
// Modifications by Zoltan Kovacs <zoltan@geogebra.org>

function getHiddenProp(){
    var prefixes = ['webkit','moz','ms','o'];
    
    // if 'hidden' is natively supported just return it
    if ('hidden' in document) return 'hidden';
    
    // otherwise loop over all the known prefixes until we find one
    for (var i = 0; i < prefixes.length; i++){
        if ((prefixes[i] + 'Hidden') in document) 
            return prefixes[i] + 'Hidden';
    }

    // otherwise it's not supported
    return null;
}

// use the property name to generate the prefixed event name
function visibilityEventMain() {
	var visProp = getHiddenProp();
	if (visProp) {
		var evtname = visProp.replace(/[H|h]idden/,'') + 'visibilitychange';
		document.addEventListener(evtname, visChange);
	}
}

// Style when the student puts GeoGebraExam into offline mode ("cheating")
function visChange() {
	var timer = document.getElementById("timer"); 
	if (timer.started) {
		timer.style.backgroundColor = "red";
		timer.style.color = "white";
	}
}
