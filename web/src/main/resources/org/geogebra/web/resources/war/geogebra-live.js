(function() {
    function LiveApp(parentClientId, embedLabel, users, delay) {
        this.api = null;
        this.users = users || {};
      	this.delay = delay || 200;
        this.clientId = parentClientId;
        this.currentAnimations = [];
        this.embeds = {};
        this.createEvent = function(type, content, label) {
            var event = {
                "type": type,
                "content": content,
                "embedLabel": embedLabel,
                "clientId": this.clientId
            }
            if (label) {
                event.label = label;
            }
            event.fire = function(callbacks) {
                callbacks.forEach(function(callback) {
                    callback(event);
                });
            }
            return event;
        };
        this.sendEvent = function(type, content, label) {
            var event = this.createEvent(type, content, label);
            event.fire(this.eventCallbacks['construction']);
        }
        this.evalCommand = function(command) {
            this.unregisterListeners();
            this.api.evalCommand(command);
            this.registerListeners();
        };

        this.evalXML = function(xml) {
            this.unregisterListeners();
            this.api.evalXML(xml);
            this.api.updateConstruction();
            this.registerListeners();
            var that = this;
            setTimeout(function() {
                that.api.getAllObjectNames("embed").forEach(that.initEmbed.bind(that));
            }, 500);//TODO no timeout
        };

        this.setXML = function(xml) {
            this.unregisterListeners();
            this.api.setXML(xml);
            this.api.updateConstruction();
            this.registerListeners();
        };

        this.initEmbed = function(label) {
            if (this.embeds[label]) {
                return;
            }
            var calc = (this.api.getEmbeddedCalculators() || {})[label];
            if (calc && calc.registerClientListener) {
                var calcLive = new LiveApp(this.clientId, label, this.users, this.delay);
                calcLive.api = calc;
                calcLive.eventCallbacks = this.eventCallbacks;
                calcLive.registerListeners();
                this.embeds[label] = calcLive;
            }
        }

        let objectsInWaiting = [];
        let updateCallback;

        let dispatchUpdates = (function() {
            if (!updateCallback) {
                const that = this;
                updateCallback = setTimeout(function() {
                    const tempObjects = objectsInWaiting.slice();
                    objectsInWaiting = [];

                    for (let i = 0; i < tempObjects.length; i++) {
                        const label = tempObjects[i];
                        const calculators = that.api.getEmbeddedCalculators(true);
                        const embed = calculators && calculators[label];

                        if (embed && embed.controller) {
                            that.sendEvent("evalGMContent", embed.toJSON(), label);
                        }

                        let commandString = that.api.getCommandString(label, false);
                        // send command for dependent objects
                        if (commandString) {
                            that.sendEvent("evalCommand", label + " := " + commandString, label);
                            var group = that.api.getObjectsOfItsGroup(label);
                            if (group != null) {
                                that.sendEvent("addToGroup", label, group);
                            }
                        }
                        // send XML for free and moveable objects (point on line)
                        if (!commandString || that.api.isMoveable(label)) {
                            let xml = that.api.getXML(label);
                            that.sendEvent("evalXML", xml, label);
                        }
                        that.sendEvent("select", label, "true");
                    }

                    updateCallback = null;
                }, that.delay);
            }
        }).bind(this);

        // *** UPDATE LISTENERS ***
        let updateListener = (function(label) {
            if ((this.api.hasUnlabeledPredecessors(label) || this.api.isMoveable(label)) && !(this.currentAnimations.includes(label))) {
                console.log("update event for " + label);
                if (!objectsInWaiting.includes(label)) {
                    objectsInWaiting.push(label);
                    dispatchUpdates();
                }
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
                    if (item.fileName.indexOf(image) > -1) {
                        item.fileName = image;
                        that.sendEvent("addImage", JSON.stringify(item));
                    }
                })
            }
            var xml = this.api.getXML(label);

            var definition = this.api.getCommandString(label);
            if (definition) {
                this.sendEvent("evalXML", this.api.getAlgorithmXML(label), label);
            } else {
                this.sendEvent("evalXML", xml, label);
            }
            this.sendEvent("deselect");
            this.sendEvent("select", label, "true");
            window.setTimeout(function(){
                that.initEmbed(label);
            },500); //TODO avoid timeout

        }).bind(this);

        // *** REMOVE LISTENERS ***
        var removeListener = (function(label) {
            console.log(label + " is removed");
            this.sendEvent("deleteObject", label);
            delete(this.embeds[label]);
        }).bind(this);

        var renameListener = (function(oldName, newName) {
            this.sendEvent("renameObject", oldName, newName);
        }).bind(this);

        // *** CLIENT LISTENERS ***
        var clientListener = (function(event) {
            var editorEventBus = this.eventCallbacks["editor"];
            switch (event[0]) {
                case "updateStyle":
                    var label = event[1];
                    console.log(label + " has changed style");

                    var xml = this.api.getXML(label);
                    this.sendEvent("evalXML", xml);
                    break;

                case "editorKeyTyped":
                    if (editorEventBus) {
                        var state = this.api.getEditorState();
                        this.createEvent("setEditorState", state, event[1]).fire(editorEventBus);
                    }
                    break;

                case "editorStop":
                    if (editorEventBus) {
                          this.createEvent("setEditorState", "{content:\"\"}").fire(editorEventBus);
                    }
                    break;

                case "deselect":
                    this.sendEvent(event[0]);
                    break;

                case "select":
                    this.sendEvent(event[0], event[1], event[2]);
                    break;
                case "embeddedContentChanged":
                    this.sendEvent(event[0], event[2], event[1]);
                    break;
                case "undo":
                case "redo":
                case "addPolygonComplete":
                    console.log(event[0], "sending whole xml");

                    var xml = this.api.getXML();
                    //console.log(xml);
                    this.sendEvent("setXML", xml);
                    break;

                case "addSlide":
                    this.sendEvent(event[0]);
                    break;

                case "removeSlide":
                case "moveSlide":
                case "selectSlide":
                case "clearSlide":
                case "orderingChange":
                    this.sendEvent(event[0], event[2]);
                    break;

                case "pasteSlide":
                    this.sendEvent(event[0], event.cardIdx, event.ggbFile);
                    break;

                case "startAnimation":
                    var label = event[1];
                    console.log("animation started for " + label);
                    this.currentAnimations.push(label);
                    this.sendEvent(event[0], label, label);
                    break;

                case "stopAnimation":
                    var label = event[1];
                    console.log("animation stopped for " + label);
                    this.currentAnimations.splice(this.currentAnimations.indexOf(label), 1);
                    this.sendEvent(event[0], label, label);
                    break;

                case "groupObjects":
                    this.sendEvent(event[0], event.targets);
                    break;

                case "ungroupObjects":
                    this.sendEvent(event[0], event.targets);
                    break;

                case "pasteElmsComplete":
                    let pastedGeos = "";
                    for (const geo of event.targets) {
                        pastedGeos += this.api.getXML(geo);
                    }
                    this.sendEvent("evalXML", pastedGeos);
                    break;

                case "addGeoToTV":
                case "removeGeoFromTV":
                	this.sendEvent(event[0], event[1]);
                	break;

                case "setValuesOfTV":
                	this.sendEvent(event[0], event[2]);
                	break;

                case "showPointsTV":
                	this.sendEvent(event[0], event.column, event.show);
                	break;

                default:
                    // console.log("unhandled event ", event[0], event);
            }

        }).bind(this);

        this.registerListeners = function() {
            this.api.registerUpdateListener(updateListener);
            this.api.registerRemoveListener(removeListener);
            this.api.registerAddListener(addListener);
            this.api.registerClientListener(clientListener);
            this.api.registerRenameListener(renameListener);
        };

        this.unregisterListeners = function() {
            this.api.unregisterUpdateListener(updateListener);
            this.api.unregisterRemoveListener(removeListener);
            this.api.unregisterAddListener(addListener);
            this.api.unregisterClientListener(clientListener);
            this.api.unregisterRenameListener(renameListener);
        };

        this.dispatch = function(last) {
            if (last && last.clientId != this.clientId) {
                target = last.embedLabel ? this.embeds[last.embedLabel] : this;
                if (last.type == "evalXML") {
                    target.evalXML(last.content);
                    target.api.previewRefresh();
                } else if (last.type == "setXML") {
                    target.setXML(last.content);
                } else if (last.type == "evalCommand") {
                    target.evalCommand(last.content);
                    target.api.previewRefresh();
                } else if (last.type == "deleteObject") {
                    target.unregisterListeners();
                    if (target === this) {
                        delete(this.embeds[last.content]);
                    }
                    target.api.deleteObject(last.content);
                    target.registerListeners();
                } else if (last.type == "setEditorState") {
                    target.unregisterListeners();
                    target.api.setEditorState(last.content, last.label);
                    target.registerListeners();
                } else if (last.type == "addImage") {
                    var file = JSON.parse(last.content);
                    target.api.addImage(file.fileName, file.fileContent);
                } else if (last.type == "addSlide"
                    || last.type == "removeSlide"
                    || last.type == "moveSlide"
                    || last.type == "clearSlide") {
                    target.api.handleSlideAction(last.type, last.content);
                } else if (last.type == "selectSlide") {
                    target.unregisterListeners();
                    target.api.selectSlide(last.content);
                    target.registerListeners();
                } else if (last.type == "renameObject") {
                    target.unregisterListeners();
                    target.api.renameObject(last.content, last.label);
                    target.registerListeners();
                } else if (last.type == "pasteSlide") {
                    target.api.handleSlideAction(last.type, last.content, last.label);
                } else if (last.type == "evalGMContent") {
                    var gmApi = target.api.getEmbeddedCalculators(true)[last.label];
                    if (gmApi) {
                        gmApi.loadFromJSON(last.content);
                    }
                } else if (last.type == "startAnimation") {
                    target.api.setAnimating(last.label, true);
                    target.api.startAnimation();
                } else if (last.type == "stopAnimation") {
                    target.api.setAnimating(last.label, false);
                } else if (last.type == "select") {
                    let user = this.users[last.clientId];
                    if (user && last.content) {
                    	// user name, user color, label of geo selected, 'true' if the geo was just added
                        target.api.addMultiuserSelection(user.name, user.color, last.content, !!last.label);
                    }
                } else if (last.type == "deselect") {
                    let user = this.users[last.clientId];
                    if (user) {
                        target.api.removeMultiuserSelections(user.name);
                    }
                } else if (last.type == "orderingChange") {
                    target.api.updateOrdering(last.content);
                } else if (last.type == "groupObjects") {
                    target.api.groupObjects(last.content);
                } else if (last.type == "ungroupObjects") {
                    target.api.ungroupObjects(last.content);
                } else if (last.type == "addToGroup") {
                    target.api.addToGroup(last.content, last.label);
                } else if (last.type == "embeddedContentChanged") {
                    target.api.setEmbedContent(last.label, last.content);
                } else if (last.type == "addGeoToTV") {
                	target.api.addGeoToTV(last.content);
                } else if (last.type == "setValuesOfTV") {
                	target.api.setValuesOfTV(last.content);
                } else if (last.type == "removeGeoFromTV") {
                	target.api.removeGeoFromTV(last.content);
                } else if (last.type == "showPointsTV") {
                	target.api.showPointsTV(last.content, last.label);
                }
            }
        };
   }

    window.GeoGebraLive = function(api, id, delay) {
        var mainSession = new LiveApp(id);
        mainSession.api = api;
        mainSession.eventCallbacks = {"construction": []}
        mainSession.registerListeners();
        mainSession.delay = delay;

        this.dispatch = function(last) {
            mainSession.dispatch(last);
        },
        this.addUser = function(user) {
            mainSession.users[user.id] = user;
        }
        this.addEventListener = function(eventCategory, callback) {
           var eventCategories = typeof eventCategory == "string" ? [eventCategory] : eventCategory;
           eventCategories.forEach(function(category) {
               mainSession.eventCallbacks[category] = mainSession.eventCallbacks[category] || [];
               mainSession.eventCallbacks[category].push(callback);
           });
       }
    }
})();