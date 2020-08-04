(function() {
    function LiveApp(parentClientId, embedLabel) {
        this.api = null;
        this.eventCallback;
        this.clientId = parentClientId || btoa(Math.random()).substring(0, 8);
        this.embeds = {};
        this.sendEvent = function() {
            var type = arguments[0];
            var content = arguments[1];
            var event = {
                "type": type,
                "content": content,
                "embedLabel": embedLabel,
                "client": this.clientId
            }
            if (arguments[2]) {
                event.label = arguments[2];
            }
            this.eventCallback(event);
        };
        this.evalCommand = function(command) {
            this.unregisterListeners();
            this.api.evalCommand(command);
            this.registerListeners();
        };

        this.evalXML = function(xml) {
            this.unregisterListeners();
            this.api.evalXML(xml);
            this.api.evalCommand("UpdateConstruction()");
            this.registerListeners();
            var that = this;
            setTimeout(function() {
                that.api.getAllObjectNames("embed").forEach(that.initEmbed.bind(that));
            }, 500);//TODO no timeout
        };

        this.setXML = function(xml) {
            this.unregisterListeners();
            this.api.setXML(xml);
            this.api.evalCommand("UpdateConstruction()");
            this.registerListeners();
        };

        this.initEmbed = function(label) {
            if (this.embeds[label]) {
                return;
            }
            var calc = (this.api.getEmbeddedCalculators() || {})[label];
            if (calc) {
                var calcLive = new LiveApp(this.clientId, label);
                calcLive.api = calc;
                calcLive.eventCallback = this.eventCallback;
                calcLive.registerListeners();
                this.embeds[label] = calcLive;
            }
        }
        // *** UPDATE LISTENERS ***
        var updateListener = (function(label) {
            if (updatingOn) {
                console.log(label + " is updated");
            } else {
                console.log("no update for " + label + ", waiting for 'movedGeos' event");
                return;
            }
            if (this.api.isIndependent(label) || this.api.isMoveable(label)) {
                var xml = this.api.getXML(label);
                //console.log(xml);
                this.sendEvent("evalXML", xml);
            } else {
                console.log("not sending update for " + label + ", isIndependent()="
                + this.api.isIndependent(label) + ", is movable()=" + this.api.isMoveable(label));
            }

        }).bind(this);

        // *** ADD LISTENERS ***
        var addListener = (function(label) {
            console.log(label + " is added");
            var image = this.api.getImageFileName(label);
            var that = this;
            if (image) {
                var json = this.api.getFileJSON();
                json.archive.forEach(function(item) {
                    if (item.fileName == image) {
                        that.sendEvent("addImage", JSON.stringify(item));
                    }
                })
            }
            var xml = this.api.getXML(label);
            //console.log(xml);

            var definition = this.api.getCommandString(label);
            console.log(definition);
            if (definition) {
                console.log("full "+this.api.getAlgorithmXML(label) );
                this.sendEvent("evalXML", this.api.getAlgorithmXML(label) );
            } else {
                this.sendEvent("evalXML", xml);
            }
            window.setTimeout(function(){
                that.initEmbed(label);
            },500); //TODO avoid timeout

        }).bind(this);

        // *** REMOVE LISTENERS ***
        var removeListener = (function(label) {
            console.log(label + " is removed");
            this.sendEvent("deleteObject", label);
        }).bind(this);

        // *** CLIENT LISTENERS ***
        var clientListener = (function(event){
            switch (event[0]) {

                case "updateStyle":
                    var label = event[1];
                    console.log(label + " has changed style");

                    var xml = this.api.getXML(label);
                    //console.log(xml);

                    this.sendEvent("evalXML", xml);
                    break;

                case "setMode":
                    console.log("setMode(" + event[2] + ")");
                    break;

                case "editorKeyTyped":
                    var state = this.api.getEditorState();
                    console.log(state);
                    this.sendEvent("setEditorState", state, event[1]);
                    break;
                case "editorStop":
                    this.sendEvent("setEditorState", "{content:\"\"}");
                    break;

                case "deselect":
                    console.log("deselect", event);
                    this.sendEvent("evalCommand", "SelectObjects[]");
                    break;
                case "select":
                    console.log("select", event);
                    this.sendEvent("evalCommand", "SelectObjects[" + event[1] + "]");
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
                        xml += this.api.getXML(event[i]);
                    }

                    //console.log("batch send", xml);
                    this.sendEvent("evalXML", xml);

                    // reenable update events
                    updatingOn = true;
                    break;


                case "undo":
                case "redo":
                case "addPolygonComplete":
                    console.log(event[0], "sending whole xml");

                    var xml = this.api.getXML();
                    //console.log(xml);
                    this.sendEvent("setXML", xml);
                    break;
                default:
                    console.log("unhandled event ", event[0], event);

            }

        }).bind(this);

        this.registerListeners = function() {
            this.api.registerUpdateListener(updateListener);
            this.api.registerRemoveListener(removeListener);
            this.api.registerAddListener(addListener);
            this.api.registerClientListener(clientListener);
        };

        this.unregisterListeners = function() {
            this.api.unregisterUpdateListener(updateListener);
            this.api.unregisterRemoveListener(removeListener);
            this.api.unregisterAddListener(addListener);
            this.api.unregisterClientListener(clientListener);
        };

        this.dispatch = function(last) {
            if (last && last.client != this.clientId) {
                target = last.embedLabel ? this.embeds[last.embedLabel] : this;
                if (last.type == "evalXML") {
                    target.evalXML(last.content);
                } else if (last.type == "setXML") {
                    target.setXML(last.content);
                } else if (last.type == "evalCommand") {
                    target.evalCommand(last.content);
                } else if (last.type == "deleteObject") {
                    target.api.deleteObject(last.content);
                } else if (last.type == "setEditorState") {
                    target.unregisterListeners();
                    target.api.setEditorState(last.content, last.label);
                    target.registerListeners();
                } else  if (last.type == "addImage") {
                    var file = JSON.parse(last.content);
                    target.api.addImage(file.fileName, file.fileContent);
                }
            }
        };
   }

    var mainSession = new LiveApp();

    window.GeoGebraLive = {
        dispatch: function(last) {
            mainSession.dispatch(last);
        },
        start: function(api, callback) {
           mainSession.api = api;
           mainSession.eventCallback = callback;
           mainSession.registerListeners();
       }
    }
})();