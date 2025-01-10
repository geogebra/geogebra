(function() {
    function LiveApp(session, embedLabel) {
        this.api = null;
        this.session = session;
        this.currentAnimations = [];
        this.embeds = {};
        this.createEvent = function(type, content, label) {
            var event = {
                "type": type,
                "content": content,
                "embedLabel": embedLabel,
                "clientId": session.clientId
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
            event.time = this.session.time++;
            event.fire(this.session.eventCallbacks['construction']);
        }

        this.evalXML = function(xml) {
            this.api.evalXML(xml);
            this.api.updateConstruction();
            var that = this;
            setTimeout(function() {
                that.api.getAllObjectNames("embed").forEach(that.initEmbed.bind(that));
            }, 500);//TODO no timeout
        };

        this.setXML = function(xml) {
            this.api.setXML(xml);
            this.api.updateConstruction();
        };

        this.initEmbed = function(label) {
            if (this.embeds[label]) {
                return;
            }
            var calc = (this.api.getEmbeddedCalculators() || {})[label];
            if (calc && calc.registerClientListener) {
                var calcLive = new LiveApp(this.session, label);
                calcLive.api = calc;
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
                        }
                        // send XML for free and moveable objects (point on line)
                        if (!commandString || that.api.isMoveable(label)) {
                            let xml = that.api.getXML(label);
                            that.sendEvent("evalXML", xml, label);
                        }
                        that.sendEvent("select", label, "true");
                    }

                    updateCallback = null;
                }, that.session.delay);
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

            if (!this.api.isIndependent(label)) {
                this.sendEvent("addObject", this.api.getAlgorithmXML(label), label);
            } else {
                this.sendEvent("addObject", xml, label);
            }
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
            var editorEventBus = this.session.eventCallbacks["editor"];
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
                case "switchCalculator":
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

                case "lockTextElement":
                case "unlockTextElement":
                	this.sendEvent(event[0], event[1]);

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

        const conflictedObjects = [];

        this.hasConflict = function(event, oldEvents) {
            for (let i = 0; i < oldEvents.length; i++) {
                const oldEvent = oldEvents[oldEvents.length - 1 - i];
                if (oldEvent.time < event.time) {
                    return false;
                }
                if (oldEvent.label && oldEvent.label == event.label) {
                    return true;
                }
            }
            return false;
        }

        this.dispatch = function(last) {
            // reject events coming from conflicted objects
            if (conflictedObjects.includes(last.label)
                && "conflictResolution" != last.type) {
                return;
            }

            if (last.time < this.session.time) {
                 if (this.hasConflict(last, session.receivedEvents) || this.hasConflict(last, session.sentEvents)) {
                     this.sendErrorEvent();
                 }
            }

            const target = last.embedLabel ? this.embeds[last.embedLabel] : this;
            target.unregisterListeners();
            if (last.type == "addObject") {
                if (target.api.exists(last.label)) {
                    if (this.session.clientId == Math.min(...this.session.users.filter(Boolean).map(u => u.id))) {
                        let counter = 1;
                        let newLabel;
                        do {
                            newLabel = last.label + "_" + counter;
                            counter++;
                        } while (target.api.exists(newLabel));

                        target.api.renameObject(last.label, newLabel);

                        target.evalXML(last.content);
                        target.api.previewRefresh();

                        const getXML = (label) =>
                            target.api.isIndependent(label) ? target.api.getXML(label) : target.api.getAlgorithmXML(label);

                        this.sendEvent("conflictResolution", getXML(newLabel) + getXML(last.label), last.label);
                        this.sendEvent("orderingChange", target.api.getOrdering(), null)
                    } else {
                        conflictedObjects.push(last.label);
                    }
                } else {
                    target.evalXML(last.content);
                    target.api.previewRefresh();
                }
                let user = this.session.users[last.clientId];
                if (user && last.content) {
                    target.api.removeMultiuserSelections(user.name);
                    // user name, user color, label of geo selected, 'true' if the geo was just added
                    target.api.addMultiuserSelection(user.name, user.color, last.content, true);
                }
            } else if (last.type == "conflictResolution") {
                conflictedObjects.splice(conflictedObjects.indexOf(last.label), 1);
                target.api.deleteObject(last.label);
                target.evalXML(last.content);
                target.api.previewRefresh();
            } else if (last.type == "evalXML") {
                if (target.checkExists(last.label)) {
                    target.evalXML(last.content);
                    target.api.previewRefresh();
                }
            } else if (last.type == "setXML") {
                target.setXML(last.content);
            } else if (last.type == "evalCommand") {
                if (target.checkExists(last.label)) {
                    target.api.evalCommand(last.content);
                    target.api.previewRefresh();
                }
            } else if (last.type == "deleteObject") {
                if (target === this) {
                    delete(this.embeds[last.content]);
                }
                target.api.deleteObject(last.content);
            } else if (last.type == "setEditorState") {
                target.api.setEditorState(last.content, last.label);
            } else if (last.type == "addImage") {
                var file = JSON.parse(last.content);
                target.api.addImage(file.fileName, file.fileContent);
            } else if (last.type == "addSlide"
                || last.type == "removeSlide"
                || last.type == "moveSlide"
                || last.type == "clearSlide") {
                try {
                    target.api.handleSlideAction(last.type, last.content);
                } catch (ex) {
                    this.sendErrorEvent();
                }
            } else if (last.type == "selectSlide") {
                target.api.selectSlide(last.content);
            } else if (last.type == "renameObject") {
                target.api.renameObject(last.content, last.label);
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
                let user = this.session.users[last.clientId];
                if (user && last.content) {
                    // user name, user color, label of geo selected, 'true' if the geo was just added
                    target.api.addMultiuserSelection(user.name, user.color, last.content, false);
                }
            } else if (last.type == "deselect") {
                let user = this.session.users[last.clientId];
                if (user) {
                    target.api.removeMultiuserSelections(user.name);
                }
            } else if (last.type == "orderingChange") {
                target.api.updateOrdering(last.content);
            } else if (last.type == "groupObjects") {
                target.api.groupObjects(last.content);
            } else if (last.type == "ungroupObjects") {
                target.api.ungroupObjects(last.content);
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
            } else if (last.type == "lockTextElement") {
                if (target.checkInline(last.content)) {
                    target.api.lockTextElement(last.content);
                }
            } else if (last.type == "unlockTextElement") {
                if (target.checkInline(last.content)) {
                    target.api.unlockTextElement(last.content);
                }
            } else if (last.type == "switchCalculator") {
                 target.api.switchCalculator(last.content);
            }
            target.registerListeners();
        };

        this.sendErrorEvent = function(label) {
            this.createEvent("error", label).fire(this.session.eventCallbacks['error']);
        };

        this.checkInline = function(label) {
            const inlineTypes = ['inlinetext', 'table', 'formula', 'mindmap'];
            if (label && !inlineTypes.includes(this.api.getObjectType(label))) {
                 this.sendErrorEvent()
                 return false;
            }
            return true;
        };

        this.checkExists = function(label) {
            if (label && !this.api.exists(label)) {
                 this.sendErrorEvent(label);
                 return false;
            }
            return true;
        }
   }

    window.GeoGebraLive = function(api, id, delay) {
        var session = {
            clientId: id,
            delay: delay || 200,
            time: 0,
            users: [],
            sentEvents: [],
            receivedEvents: []
        };
        var mainApp = new LiveApp(session);
        mainApp.api = api;
        session.eventCallbacks = {"construction": [], "error": []}
        mainApp.registerListeners();

        this.dispatch = function(event) {
            if (event && event.clientId !== id) {
                mainApp.dispatch(event);
                session.time = Math.max(session.time, event.time) + 1;
                session.receivedEvents.push({
                    ...event,
                    timestamp: new Date().getTime()
                });
            }
        }

        this.addUser = function(user) {
            session.users[user.id] = user;
        }

        this.addEventListener = function(eventCategory, callback) {
            let eventCategories = typeof eventCategory == "string" ? [eventCategory] : eventCategory;
            eventCategories.forEach(function(category) {
                session.eventCallbacks[category] = session.eventCallbacks[category] || [];
                session.eventCallbacks[category].push(callback);
            });
        }

        this.addEventListener(["construction", "editor"], (event) => {
            session.sentEvents.push({
                ...event,
                timestamp: new Date().getTime()
            });
        });

        this.startLogging = () => {
            session.sentEvents = [];
            session.receivedEvents = [];
        }

        function download(object, fileName) {
            const a = document.createElement("a");
            a.href = "data:text/json;charset=utf-8,"
                + encodeURIComponent(JSON.stringify(object, null, 4));
            a.setAttribute("download", fileName);
            a.click();
        }

        this.getSent = () => {
            download(session.sentEvents, "events-sent.json");
        }

        this.getReceived = () => {
            download(session.receivedEvents, "events-received.json");
        }
    }
})();