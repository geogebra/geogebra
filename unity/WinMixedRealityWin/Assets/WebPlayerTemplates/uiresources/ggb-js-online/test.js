// log
function log(v) {
    document.getElementById('log').innerHTML += "<br>"+v;
}

// buttons
function onClick(){
    log("button clicked");
    ggbApplet.evalCommand("A=(1,1)");
    ggbApplet.evalCommand("B=(2,1)");
    ggbApplet.evalCommand("f=Line(A,B)");
    log(ggbApplet.getValueString("f"));
}
