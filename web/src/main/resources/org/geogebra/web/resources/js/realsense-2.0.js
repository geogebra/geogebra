/*******************************************************************************

INTEL CORPORATION PROPRIETARY INFORMATION
This software is supplied under the terms of a license agreement or nondisclosure
agreement with Intel Corporation and may not be copied or disclosed except in
accordance with the terms of that agreement
Copyright(c) 2014 Intel Corporation. All Rights Reserved.

*******************************************************************************/

var RealSense = { connection: null };
var RealSenseVersion = '2.0.1';

/** Create an instance of the PXCMSenseManager .
    @return Promise object with PXCMSenseManager object in success callback.
*/
PXCMSenseManager_CreateInstance = function () {
    if (RealSense.connection == null) RealSense.connection = new RealSenseConnection();
    return RealSense.connection.call(0, 'PXCMSenseManager_CreateInstance', { 'js_version': RealSenseVersion }).then(function (result) {
        return new PXCMSenseManager(result.instance.value);
    })
};

/** Create an instance of the PXCMSession.
    @return Promise object with PXCMSePXCMSessionnseManager object in success callback.
*/
PXCMSession_CreateInstance = function () {
    if (RealSense.connection == null) RealSense.connection = new RealSenseConnection();
    return RealSense.connection.call(0, 'PXCMSession_CreateInstance', { 'js_version': RealSenseVersion }).then(function (result) {
        return new PXCMSession(result.instance.value);
    })
};

/**
    This is the main object for the Intel® RealSense™ SDK pipeline.
    Control the pipeline execution with this interface.
*/
function PXCMSenseManager(instance) {
    var instance = instance;
    var self = this;
    this.CUID_PXCMHandModule = 1313751368;
    this.CUID_PXCMFaceModule = 1144209734;
    this.mid_callbacks = {};

    /** Enable the hand module in the SenseManager pipeline.
        @param {function} onData    Callback function to receive per-frame recognition results
        @return Promise object
    */
    this.EnableHand = function (onData) {
        return this.EnableModule(this.CUID_PXCMHandModule, 0, onData);
    }

    /** Enable the face module in the SenseManager pipeline.
        @param {function} onData    Callback function to receive per-frame recognition results
        @return Promise object
    */
    this.EnableFace = function (onData) {
        return this.EnableModule(this.CUID_PXCMFaceModule, 0, onData);
    }

    /** Create PXCMHandConfiguration object for changing hand module configuration
        @return Promise object with PXCMHandConfiguration object in success callback
    */
    this.CreateHandConfiguration = function () {
        return RealSense.connection.call(self.mid_callbacks[this.CUID_PXCMHandModule].module_instance, 'PXCMHandModule_CreateActiveConfiguration').then(function (result) {
            return new PXCMHandConfiguration(result.instance.value);
        });
    }

    /** Create PXCMFaceConfiguration object for changing face module configuration
        @return Promise object with PXCMFaceConfiguration object in success callback
    */
    this.CreateFaceConfiguration = function () {
        var config;
        return RealSense.connection.call(self.mid_callbacks[this.CUID_PXCMFaceModule].module_instance, 'PXCMFaceModule_CreateActiveConfiguration').then(function (result) {
            config = new PXCMFaceConfiguration(result.instance.value);
            return RealSense.connection.call(result.instance.value, 'PXCMFaceConfiguration_GetConfigurations');
        }).then(function (result) {
            var c = result.configs;
            //for (var attrname in c) { config[attrname] = c[attrname]; }
            config.configs = result.configs;
            return config;
        });
    }

    /** Query the PXCMCaptureManager object for changing capture configuration
        @return Promise object with PXCMCaptureManager object in success callback
    */
    this.QueryCaptureManager = function () {
        return RealSense.connection.call(instance, 'PXCMSenseManager_QueryCaptureManager').then(function (result) {
            return new PXCMCaptureManager(result.instance.value);
        });
    }

    /** Initialize the SenseManager pipeline for streaming with callbacks. The application must 
        enable raw streams or algorithm modules before this function.
        @param {function} onConnect     Optional callback when there is a device connection or disconnection
        @return Promise object
    */
    this.Init = function (onConnect) {
        if (onConnect !== undefined) {
            RealSense.connection.subscribe_callback("PXCMSenseManager_OnConnect", this, onConnect);
        }
        return RealSense.connection.call(instance, 'PXCMSenseManager_Init', { 'handler': true, 'onModuleProcessedFrame': true, 'onConnect': onConnect !== undefined, 'attachDataToCallbacks': true /*, 'onImageSamples': true, 'addRefImages': true*/ }, 20000); // Connection to camera may take long time
    }

    /** Start streaming with reporting per-frame recognition results to callbacks specified in Enable* functions.
        The application must initialize the pipeline before calling this function.
        @return Promise object
    */
    this.StreamFrames = function () {
        return RealSense.connection.call(instance, 'PXCMSenseManager_StreamFrames', { blocking: false });
    };

    /** Pause/Resume the execution of the hand module.
        @param {Boolean} pause        If true, pause the module. Otherwise, resume the module.
        @return Promise object
    */
    this.PauseHand = function (pause) {
        return this.PauseModule(this.CUID_PXCMHandModule, pause);
    }

    /** Pause/Resume the execution of the face module.
        @param {Boolean} pause        If true, pause the module. Otherwise, resume the module.
        @return Promise object
    */
    this.PauseFace = function (pause) {
        return this.PauseModule(this.CUID_PXCMFaceModule, pause);
    }

    /** Close the execution pipeline.
        @return Promise object
    */
    this.Close = function () {
        return RealSense.connection.call(instance, 'PXCMSenseManager_Close');
    }

    ///////////////////////////////////////////////////////////////
    // Internal functions

    this.EnableModule = function (mid, mdesc, onData) {
        this.mid_callbacks[mid] = { callback: onData };
        var res;
        return RealSense.connection.call(instance, 'PXCMSenseManager_EnableModule', { mid: mid, mdesc: mdesc }).then(function (result) {
            res = result;
            return RealSense.connection.call(instance, 'PXCMSenseManager_QueryModule', { mid: mid });
        }).then(function (result2) {
            res.instance = result2.instance.value;
            self.mid_callbacks[mid].instance = result2.instance.value;
            var module = null;
            //if (mid == this.CUID_PXCMFaceModule) module = new PXCMFaceModule(result2.instance.value);
            //if (mid == this.CUID_PXCMHandModule) module = new PXCMHandModule(result2.instance.value);
            res.module = module;
            self.mid_callbacks[mid].module_instance = result2.instance.value;
            self.mid_callbacks[mid].module = module;
            return res;
        });
    }

    this.PauseModule = function (mid, pause) {
        return RealSense.connection.call(instance, 'PXCMSenseManager_PauseModule', { 'mid': mid, 'pause': pause });
    }

    this.EnableStreams = function (sdesc, onData) {
        this.mid_callbacks[mid] = { callback: onData };
        return RealSense.connection.call(instance, 'PXCMSenseManager_EnableStreams', { 'sdesc': sdesc });
    }

    this.OnModuleProcessedFrame = function (self, response) {
        if (self.mid_callbacks[response.mid]) {
            var callback = self.mid_callbacks[response.mid].callback;
            var module = self.mid_callbacks[response.mid].module;
            //var result = response.result;
            //result.instance = self.mid_callbacks[response.mid].instance;
            callback(response.mid, module, response);
            return;
        }
    };

    RealSense.connection.subscribe_callback("PXCMSenseManager_OnModuleProcessedFrame", this, this.OnModuleProcessedFrame);
}

function PXCMCaptureManager(instance) {
    var instance = instance;
    var self = this;
    this.STREAM_TYPE_COLOR = 0x0001;
    this.STREAM_TYPE_DEPTH = 0x0002;

    /**
        @brief    Return the stream resolution of the specified stream type.
        @param {Number} type    The stream type, COLOR=1, DEPTH=2
        @return Promise object with property 'size' : { 'width' : Number, 'height' : Number }
    */
    this.QueryImageSize = function (type) {
        return RealSense.connection.call(instance, 'PXCMCaptureManager_QueryImageSize', { 'type': type });
    }
}

function PXCMHandConfiguration(instance) {
    var instance = instance;
    var self = this;

    /** Enable all gestures
		@param {Boolean} continuousGesture  Set to "true" to get an event at every frame, or "false" to get only start and end states of the gesture
		@return Promise object
    */
    this.EnableAllGestures = function (continuousGesture) {
        return RealSense.connection.call(instance, 'PXCMHandConfiguration_EnableAllGestures', { 'continuousGesture': continuousGesture });
    }

    /** Enable all alert messages.
		@return Promise object
	*/
    this.EnableAllAlerts = function () {
        return RealSense.connection.call(instance, 'PXCMHandConfiguration_EnableAllAlerts');
    }

    /** Disable all gestures
		@param {Boolean} continuousGesture  Set to "true" to get an event at every frame, or "false" to get only start and end states of the gesture
		@return Promise object
    */
    this.DisableAllGestures = function () {
        return RealSense.connection.call(instance, 'PXCMHandConfiguration_DisableAllGestures');
    }

    /** Disable all alert messages.
		@return Promise object
	*/
    this.DisableAllAlerts = function () {
        return RealSense.connection.call(instance, 'PXCMHandConfiguration_DisableAllAlerts');
    }

    /** Commit the configuration changes to the module
		This method must be called in order for any configuration changes to actually apply
		@return Promise object
	*/
    this.ApplyChanges = function () {
        return RealSense.connection.call(instance, 'PXCMHandConfiguration_ApplyChanges');
    }
}

function PXCMFaceConfiguration(instance) {
    var instance = instance;
    var self = this;
    this.FACE_MODE_COLOR = 0;
    this.FACE_MODE_COLOR_PLUS_DEPTH = 1;

    /** Set tracking mode. 
		@param {Number} FACE_MODE_COLOR (0) or FACE_MODE_COLOR_PLUS_DEPTH (1)
		@return Promise object
	*/
    this.SetTrackingMode = function (trackingMode) {
        return RealSense.connection.call(instance, 'PXCMFaceConfiguration_SetTrackingMode', { 'trackingMode': trackingMode });
    }

    /** Commit the configuration changes to the module
		This method must be called in order for any configuration changes to actually apply
		@return Promise object
	*/
    this.ApplyChanges = function () {
        return RealSense.connection.call(instance, 'PXCMFaceConfiguration_ApplyChanges', { 'configs': this.configs });
    }
}

function PXCMSession(instance) {
    var instance = instance;
    //this.CUID = PXC_UID('S', 'E', 'S', ' ');

    //ImplGroup
    this.IMPL_GROUP_ANY = 0;
    this.IMPL_GROUP_OBJECT_RECOGNITION = 0x00000001;
    this.IMPL_GROUP_SPEECH_RECOGNITION = 0x00000002;
    this.IMPL_GROUP_SENSOR = 0x00000004;
    this.IMPL_GROUP_CORE = 0x80000000;
    this.IMPL_GROUP_USER = 0x40000000;

    //ImplSubgroup
    this.IMPL_SUBGROUP_ANY = 0;
    this.IMPL_SUBGROUP_FACE_ANALYSIS = 0x00000001;
    this.IMPL_SUBGROUP_GESTURE_RECOGNITION = 0x00000010;
    this.IMPL_SUBGROUP_SEGMENTATION = 0x00000020;
    this.IMPL_SUBGROUP_VISUALVITALS = 0x00000040;
    this.IMPL_SUBGROUP_EMOTION_RECOGNITION = 0x00000080;
    this.IMPL_SUBGROUP_AUDIO_CAPTURE = 0x00000001;
    this.IMPL_SUBGROUP_VIDEO_CAPTURE = 0x00000002;
    this.IMPL_SUBGROUP_VOICE_RECOGNITION = 0x00000001;
    this.IMPL_SUBGROUP_VOICE_SYNTHESIS = 0x00000002;

    this.QueryVersion = function () {
        return RealSense.connection.call(instance, 'PXCMSession_QueryVersion');
    }

    this.QueryImpl = function (templat, idx) {
        return RealSense.connection.call(instance, 'PXCMSession_QueryImpl', { templat: templat, idx: idx });
    }

    this.CreateImpl = function (desc, iuid, cuid) {
        return RealSense.connection.call(instance, 'PXCMSession_CreateImpl', { 'desc': desc, 'iuid': iuid, 'cuid': cuid }).then(function (result) {
            var capture = new PXCMCapture(result.instance.value);
            return capture;
        })
    }

    ///** 
    //    @brief Create an instance of the PXCCaptureManager interface.
    //    @return The PXCCaptureManager instance.
    //*/
    //__inline PXCCaptureManager* CreateCaptureManager(void) {
    //    PXCCaptureManager *cm=0;
    //    CreateImpl(0, PXC_UID('C','P','U','T'), 0, (void**)&cm);
    //    return cm;
    //}
    //
    ///** 
	//	@brief Create an instance of the PXCAudioSource interface.
	//	@return The PXCAudioSource instance.
    //*/
    //__inline PXCAudioSource *CreateAudioSource(void) {
    //    PXCAudioSource *am=0;
    //    CreateImpl(0, PXC_UID('A','D','S','R'), 0, (void**)&am);
    //    return am;
    //}

    /** 
        @brief Return the module descriptor
        @param[in]  module          The module instance
		@return Promise object with module descriptor
	*/
    this.QueryModuleDesc = function (module) {
        return RealSense.connection.call(instance, 'PXCMSpeechRecognition_QueryModuleDesc', { 'module': module.instance });
    }

    this.CreateSpeechRecognition = function () {
        return RealSense.connection.call(instance, 'PXCMSession_CreateImpl', { 'cuid': -2146187993 }).then(function (result) {
            var capture = new PXCMSpeechRecognition(result.instance.value);
            return capture;
        })
    }
}

function PXCMSpeechRecognition(instance) {
    var instance = instance;
    var self = this;
    this.FACE_MODE_COLOR = 0;
    this.FACE_MODE_COLOR_PLUS_DEPTH = 1;

    /**
        @brief The function returns the available algorithm configurations.
        @param[in]  idx         The zero-based index to retrieve all algorithm configurations.
		@return Promise object
	*/
    this.QueryProfile = function (idx) {
        return RealSense.connection.call(instance, 'PXCMSpeechRecognition_QueryProfile', { 'idx': idx });
    }

    /**
        @brief The function returns the working algorithm configurations.
        @param[out] pinfo       The algorithm configuration, to be returned.
		@return Promise object
	*/
    this.QueryProfile = function () {
        return RealSense.connection.call(instance, 'PXCMSpeechRecognition_QueryProfile', { 'idx': -1 });
    }

    /**
        @brief The function sets the working algorithm configurations. 
        @param[in] config       The algorithm configuration.
		@return Promise object
	*/
    this.SetProfile = function (config) {
        return RealSense.connection.call(instance, 'PXCMSpeechRecognition_SetProfile', { 'config': config });
    }

    /** 
        @brief The function builds the recognition grammar from the list of strings. 
        @param[in] gid          The grammar identifier. Can be any non-zero number.
        @param[in] cmds         The string list.
        @param[in] labels       Optional list of labels. If not provided, the labels are 1...ncmds.
		@return Promise object
	*/
    this.BuildGrammarFromStringList = function (gid, cmds, labels) {
        return RealSense.connection.call(instance, 'PXCMSpeechRecognition_BuildGrammarFromStringList', { 'gid': gid, 'cmds': cmds, 'labels': labels });
    }

    /** 
        @brief The function deletes the specified grammar and releases any resources allocated.
        @param[in] gid          The grammar identifier.
		@return Promise object
	*/
    this.ReleaseGrammar = function (gid) {
        return RealSense.connection.call(instance, 'PXCMSpeechRecognition_ReleaseGrammar', { 'gid': gid });
    }

    /** 
        @brief The function sets the active grammar for recognition.
        @param[in] gid          The grammar identifier.
		@return Promise object
	*/
    this.SetGrammar = function (gid) {
        return RealSense.connection.call(instance, 'PXCMSpeechRecognition_SetGrammar', { 'gid': gid }, 30000); // Loading language model may take long time
    }

    /** 
        @brief The function sets the dictation recognition mode. 
        The function may take some time to initialize.
		@return Promise object
	*/
    this.SetDictation = function () {
        return RealSense.connection.call(instance, 'PXCMSpeechRecognition_SetGrammar', { 'gid': 0 }, 30000); // Loading language model may take long time
    }

    /** 
        @brief The function starts voice recognition.
        @param[in] OnRecognition    The callback function is invoked when there is some speech recognized.
        @param[in] handler          The callback function is triggered by any alert event.
		@return Promise object
	*/
    this.StartRec = function (OnRecognition, OnAlert) {
        RealSense.connection.subscribe_callback("PXCMSpeechRecognition_OnRecognition", this, OnRecognition);
        RealSense.connection.subscribe_callback("PXCMSpeechRecognition_OnAlert", this, OnAlert);
        return RealSense.connection.call(instance, 'PXCMSpeechRecognition_StartRec', { 'handler': true, 'onRecognition': true, 'onAlert': true }, 20000); // Loading language model may take several seconds
    }

    /** 
        @brief The function stops voice recognition immediately.
		@return Promise object
	*/
    this.StopRec = function () {
        return RealSense.connection.call(instance, 'PXCMSpeechRecognition_StopRec', { });
    }
}

// layout of object received in face callback (callback specified in EnableFaceModule)
var PXCMFaceData = {
    frameTimestamp: Number,
    faces: [{
        userID: Number,
        detection: {
            faceAverageDepth: Number,
            faceBoundingRect: {
                x: Number,
                y: Number,
                w: Number,
                h: Number,
            }
        },
        landmarks: {
            landmarksNumPoints: Number,
            landmarksPoints: [{
                label: Number,
                confidenceImage: Number,
                confidenceWorld: Number,
                world: {
                    x: Number,
                    y: Number,
                    z: Number,
                },
                image: {
                    x: Number,
                    y: Number,
                }
            }]
        },
        pose: {
            headPosition: {
                x: Number,
                y: Number,
                z: Number,
            },
            poseEulerAngles: {
                yaw: Number,
                pitch: Number,
                roll: Number,
            },
            poseQuaternion: {
                x: Number,
                y: Number,
                z: Number,
                w: Number,
            }
        },
        expressions: {
            browRaiserLeft: Number,
            browRaiserRight: Number,
            browLowererLeft: Number,
            browLowererRight: Number,
            smile: Number,
            mouthOpen: Number,
            eyesClosedLeft: Number,
            eyesClosedRight: Number,
            headTurnLeft: Number,
            headTurnRight: Number,
            headUp: Number,
            headDown: Number,
            headTiltLedt: Number,
            headTiltRight: Number,
            eyesTurnLeft: Number,
            eyesTurnRight: Number,
            eyesUp: Number,
            eyesDown: Number,
        }
    }],
    alerts: [{
        name: String,
        timeStamp: Number,
        faceId: Number,
    }]
}

// layout of object received in hand callback (callback specified in EnableHandModule)
var PXCMHandData = {
    hands: [{
        uniqueId: Number,
        userId: Number,
        timeStamp: Number,
        isCalibrated: Boolean,
        bodySide: Number,
        boundingBoxImage: {
            x: Number,
            y: Number,
            w: Number,
            h: Number,
        },
        massCenterImage: {
            x: Number,
            y: Number,
        },
        massCenterWorld: {
            x: Number,
            y: Number,
            z: Number,
        },
        palmOrientation: {
            x: Number,
            y: Number,
            z: Number,
            w: Number,
        },
        extremityPoints: [{
            name: String,
            label: Number,
            pointWorld: {
                x: Number,
                y: Number,
                z: Number,
            },
            pointImage: {
                x: Number,
                y: Number,
                z: Number,
            }
        }],
        fingerData: [{
            name: String,
            label: Number,
            foldedness: Number,
            radius: Number,
        }],
        trackedJoint: [{
            name: String,
            label: Number,
            confidence: Number,
            positionWorld: {
                x: Number,
                y: Number,
                z: Number,
            },
            positionImage: {
                x: Number,
                y: Number,
                z: Number,
            },
            localRotation: {
                x: Number,
                y: Number,
                z: Number,
                w: Number,
            },
            globalOrientation: {
                x: Number,
                y: Number,
                z: Number,
                w: Number,
            },
            speed: {
                x: Number,
                y: Number,
                z: Number,
            }
        }],
        normalizedJoint: [{
            name: String,
            label: Number,
            confidence: Number,
            positionWorld: {
                x: Number,
                y: Number,
                z: Number,
            },
            positionImage: {
                x: Number,
                y: Number,
                z: Number,
            },
            localRotation: {
                x: Number,
                y: Number,
                z: Number,
                w: Number,
            },
            globalOrientation: {
                x: Number,
                y: Number,
                z: Number,
                w: Number,
            },
            speed: {
                x: Number,
                y: Number,
                z: Number,
            },
        }],
        hasNormalizedJoints: Boolean,
        hasSegmentationImage: Boolean,
        hasTrackedJoints: Boolean,
    }],
    alerts: [{
        name: String,
        label: Number,
        handId: Number,
        timeStamp: Number,
        frameNumber: Number,
    }],
    gestures: [{
        timeStamp: Number,
        handId: Number,
        state: Number,
        frameNumber: Number,
        name: String,
    }]
}

//////////////////////////////////////////////////////////////////////////////////
// Internal object for websocket communication

function RealSenseConnection() {
    this.socketUrl = 'ws://localhost:4181';

    var self = this;
    var noop = function () { };
    this.onmessage = noop;
    this.onopen = noop;
    this.onclose = noop;
    this.onerror = noop;

    this.queue = [];        // queue before websocket is open
    this.websocket = null;  // WebSocket object
    this.request_array = {};// Requests by request id
    this.request_id = 0;    // Increment on every message
    this.callbacks = {};    // Callbacks from server
    this.binary_data = null;// Data received in last binary message

    this.call = function (instance, method, params, timeout) {
        params = params || {};      // Empty params by default
        timeout = timeout || 10000; // Default timeout in ms for response from server

        if (!("WebSocket" in window)) throw "WebSocket not available";

        if (this.websocket === null || this.websocket.readyState > 1) { // Create WebSocket if not created or closed
            this.websocket = new WebSocket(this.socketUrl);
            this.websocket.binaryType = "arraybuffer"; // Receive binary messages as ArrayBuffer
            this.websocket.onopen = function (event) { self._onopen(event); };
            this.websocket.onmessage = function (event) { self._onmessage(event); };
            this.websocket.onerror = this.onerror;
            this.websocket.onclose = this.onclose;
        }

        // Construct request as id+instance+method+params
        var request = params;
        request.id = ++this.request_id;
        request.instance = { value: instance };
        request.method = method;

        // Convert request to JSON string
        var request_text = JSON.stringify(request);

        // Send request or put request into queue (if socket still in CONNECTING state)
        if (this.websocket.readyState == 0) {
            this.queue.push(request_text);
        } else if (this.websocket.readyState == 1) {
            this.websocket.send(request_text);
        }

        // Create promise object
        var promise = new Promise(function (resolve, reject) {
            request.resolve = resolve;
            request.reject = reject;
        });

        // Add timeout handler
        request.timeoutHandler = function () {
            if (RealSense.connection.websocket.readyState > 1) {
                this.reject({ 'error': 'error opening websocket' });
            } else {
                this.reject({ 'error': 'request timeout on method ' + request.method });
            }
        }
        if (this.websocket.readyState > 1) {
            request.reject({ 'error': 'error opening websocket' });
        } else {
            request.timeout_id = setTimeout(function () { request.timeoutHandler() }, timeout)
        }

        // Store request by id
        this.request_array[request.id] = request;

        return promise;
    };

    // Send queued messages when socket is open
    this._onopen = function (event) {
        self.onopen(event);
        for (var i = 0; i < self.queue.length; i++) {
            self.websocket.send(self.queue[i]);
        }
        self.queue = [];
    }

    // Message handler
    this._onmessage = function (event) {
        if (event.data instanceof ArrayBuffer) {
            this.binary_data = new Uint8Array(event.data);
            //this.onmessage(event.data);
            return;
        }

        // Parse JSON
        var response;
        try {
            var t0 = performance.now();
            response = JSON.parse(event.data);
            var t1 = performance.now();
            response.parse_time = t1 - t0;
        } catch (err) {
            this.onmessage(event.data, null);
            return;
        }
        if (typeof response !== 'object') return; // error parsing JSON

        if (response.method !== 'undefined' && this.callbacks[response.method]) { // callback from server
            var callback = this.callbacks[response.method].callback;
            var obj = this.callbacks[response.method].obj;
            callback(obj, response);
            return;
        } else if (response.id !== 'undefined' && this.request_array[response.id]) { // result from server
            // Attach request to response object and remove from array
            response.request = this.request_array[response.id];
            delete this.request_array[response.id];

            clearTimeout(response.request.timeout_id);

            if (this.binary_data != null) {
                response.binary_data = this.binary_data;
            }

            // if error or status<0
            if ('error' in response || ('status' in response && response.status < 0)) {
                response.request.reject(response);
            } else {
                response.request.resolve(response);
            }
            //return;
        }

        // Unknown message from server, pass it to onmessage handler
        this.onmessage(event.data, response);
    };

    // Subscribe to callback from server
    this.subscribe_callback = function (method, obj_ptr, callback) {
        this.callbacks[method] = { obj: obj_ptr, callback: callback };
    }
}
