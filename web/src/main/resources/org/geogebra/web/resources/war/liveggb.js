(function() {
		var api1;
		var eventCallback;
		var clientId = btoa(Math.random()).substring(0, 8);
		function sendEvent() {
			var type = arguments[0];
			var content = arguments[1];
			var event = {
				"type": type,
				"content": content,
				"client": clientId
			}
			if (arguments[2]) {
				event.label = arguments[2];
			}
			eventCallback(event);
		}
		function evalCommand(api, command) {
            unregisterListeners();
            api.evalCommand(command);
            registerListeners();
        }

        function evalXML(api, xml) {
            unregisterListeners();
            api.evalXML(xml);
            api.evalCommand("UpdateConstruction()");
            registerListeners()
        }

        function setXML(api, xml) {
            unregisterListeners();
            api.setXML(xml);
            api.evalCommand("UpdateConstruction()");
            registerListeners();
        }

        // *** UPDATE LISTENERS ***
        function updateListener(label) {
            if (updatingOn) {
                console.log(label + " is updated");
            } else {
                console.log("no update for " + label + ", waiting for 'movedGeos' event");
                return;
            }
            if (api1.isIndependent(label) || api1.isMoveable(label)) {
                var xml = api1.getXML(label);
                //console.log(xml);
                sendEvent("evalXML", xml);
            } else {
                console.log("not sending update for " + label + ", isIndependent()="
                + api1.isIndependent(label) + ", is movable()=" + api1.isMoveable(label));
            }

        }

        // *** ADD LISTENERS ***
        function addListener(label) {
            console.log(label + " is added");

            var xml = api1.getXML(label);
            //console.log(xml);

            var definition = api1.getCommandString(label);
            console.log(definition);
            if (definition) {
                console.log("full "+api1.getAlgorithmXML(label) );
                sendEvent("evalXML", api1.getAlgorithmXML(label) );
            } else {
                sendEvent("evalXML", xml);
            }
        }

        // *** REMOVE LISTENERS ***
        function removeListener(label) {
            console.log(label + " is removed");
            sendEvent("deleteObject", label);
        }

        // *** CLIENT LISTENERS ***
        function clientListener(event) {
            switch (event[0]) {

                case "updateStyle":
                    var label = event[1];
                    console.log(label + " has changed style");

                    var xml = api1.getXML(label);
                    //console.log(xml);

                    sendEvent("evalXML", xml);
                    break;

                case "setMode":
                    console.log("setMode(" + event[2] + ")");
                    break;

                case "editorKeyTyped":
                    var state = api1.getEditorState();
                    console.log(state);
                    sendEvent("setEditorState", state, event[1]);
                    break;
                case "editorStop":
                    sendEvent("setEditorState", "{content:\"\"}");
                    break;

                case "deselect":
                    console.log("deselect", event);
                    sendEvent("evalCommand", "SelectObjects[]");
                    break;
                case "select":
                    console.log("select", event);
                    sendEvent("evalCommand", "SelectObjects[" + event[1] + "]");
                    break;

                case "addPolygon":
                    console.log("addPolygon", event);
                    break;

                case "movingGeos":
                    console.log("movingGeos", event);
                    // do nothing until movedGeos event is received
                    updatingOn = false;
                    break;

                case "movedGeos":
                    console.log("movedGeos", event);

                    var xml = "";
                    for (var i = 1; i < event.length; i++) {
                        xml += api1.getXML(event[i]);
                    }

                    //console.log("batch send", xml);
                    sendEvent("evalXML", xml);

                    // reenable update events
                    updatingOn = true;
                    break;


                case "undo":
                case "redo":
                case "addPolygonComplete":
                    console.log(event[0], "sending whole xml");

                    var xml = api1.getXML();
                    //console.log(xml);
                    sendEvent("setXML", xml);
                    break;
                default:
                    console.log("unhandled event ", event[0], event);

            }

        }

        function registerListeners() {
            api1.registerUpdateListener(updateListener);
            api1.registerRemoveListener(removeListener);
            api1.registerAddListener(addListener);
            api1.registerClientListener(clientListener);
        }

        function unregisterListeners() {
            api1.unregisterUpdateListener(updateListener);
            api1.unregisterRemoveListener(removeListener);
            api1.unregisterAddListener(addListener);
            api1.unregisterClientListener(clientListener);
        }


window.GeoGebraLive = {
     dispatch: function(last) {
		if (last && last.client != clientId) {
			if (last.type == "evalXML") {
				evalXML(api1, last.content);
			} else if (last.type == "setXML") {
				setXML(api1, last.content);
			} else if (last.type == "evalCommand") {
				evalCommand(api1, last.content);
			} else if (last.type == "deleteObject") {
				api1.deleteObject(last.content);
			} else if (last.type == "setEditorState") {
				unregisterListeners();
				api1.setEditorState(last.content, last.label);
				registerListeners();
			}
		}
   },
   start: function(api, callback) {
       api1 = api;
       eventCallback = callback;
       registerListeners();
   }
}

})();