/*
  @author: GeoGebra - Dynamic Mathematics for Everyone, http://www.geogebra.org
  @license: This file is subject to the GeoGebra Non-Commercial License Agreement, see http://www.geogebra.org/license. For questions please write us at office@geogebra.org.
*/

/*global renderGGBElement, XDomainRequest, ggbApplets, console */

var latestVersion="5.0.458.0";
var isRenderGGBElementEnabled = false;
var scriptLoadStarted = false;
var html5AppletsToProcess = null;
var ggbHTML5LoadedCodebaseIsWebSimple = false;
var ggbHTML5LoadedCodebaseVersion = null;
var ggbHTML5LoadedScript = null;

var ggbCompiledResourcesLoadFinished = false;
var ggbCompiledResourcesLoadInProgress = false;
var ggbCompiledAppletsLoaded = false;

/**
 * @param ggbVersion The version of the GeoGebraFile as string in the format x.x (e.g. '4.4'). If the version is not specified, the latest stable GeoGebraVersion is used (currently 4.4).
 * @param parameters An object containing parameters that are passed to the applet.
 * @param views An object containing information about which views are used in the GeoGebra worksheet. Each variable is boolean.
 *              E.g.: {"is3D":false,"AV":false,"SV":false,"CV":false,"EV2":false,"CP":false,"PC":false,"DA":false,"FI":false,"PV":false,"macro":false};
 * @param html5NoWebSimple Set to true to avoid using web Simple for simple html5 applets. In this case the full version is used always.
 */
var GGBApplet = function() {
    "use strict";
    var applet = {};

    // Define the parameters
    var ggbVersion = '5.0';
    var parameters = {};
    var views = null;
    var html5NoWebSimple = false;
    var html5NoWebSimpleParamExists = false;
    var appletID = null;
    var initComplete = false;
    var html5OverwrittenCodebaseVersion = null;
    var html5OverwrittenCodebase = null;

    for(var i=0; i<arguments.length; i++) {
        var p = arguments[i];
        if (p !== null) {
            switch(typeof(p)) {
                case 'number':
                    ggbVersion = p.toFixed(1);
                    break;
                case 'string':
                    // Check for a version number
                    if (p.match(new RegExp("^[0-9]\\.[0-9]+$"))) {
                        ggbVersion = p;
                    } else {
                        appletID = p;
                    }
                    break;
                case 'object':
                    if (typeof p.is3D !== "undefined") {
                        views = p;
                    } else {
                        parameters = p;
                    }
                    break;
                case 'boolean':
                    html5NoWebSimple = p;
                    html5NoWebSimpleParamExists = true;
                    break;
            }
        }
    }

    if (views === null) {
        views = {"is3D":false,"AV":false,"SV":false,"CV":false,"EV2":false,"CP":false,"PC":false,"DA":false,"FI":false,"PV":false,"macro":false};

        // don't use web simple when material is loaded from tube, because we don't know which views are used.
        if (parameters.material_id !== undefined && !html5NoWebSimpleParamExists) {
            html5NoWebSimple = true;
        }
    }

    if (appletID !== null && parameters.id === undefined) {
        parameters.id = appletID;
    }

    // Private members
    var jnlpFilePath = "";
    var html5Codebase = "";
    var isHTML5Offline = false;
    var loadedAppletType = null;
    var html5CodebaseVersion = null;
    var html5CodebaseScript = null;
    var html5CodebaseIsWebSimple = false;
    var previewImagePath = null;
    var previewLoadingPath = null;
    var previewPlayPath = null;
    var fonts_css_url = null;
    var jnlpBaseDir = null;
    var preCompiledScriptPath = null;
    var preCompiledResourcePath = null;
    var preCompiledScriptVersion = null;

    if (parameters.height !== undefined) {
        parameters.height = Math.round(parameters.height);
    }
    if (parameters.width !== undefined) {
        parameters.width = Math.round(parameters.width);
    }
    var parseVersion =function(d){
        return parseFloat(d)>4.0 ? parseFloat(d) : 5; 
    };
    /**
     * Overrides the codebase for HTML5.
     * @param codebase Can be an URL or a local file path.
     * @param offline Set to true, if the codebase is a local URL and no web URL
     */
    applet.setHTML5Codebase = function(codebase, offline) {
        html5OverwrittenCodebase = codebase;
        setHTML5CodebaseInternal(codebase, offline);
    };

    /**
     * Overrides the codebase version for Java.
     * @param version The version of the codebase that shoudl be used for java applets.
     */
    applet.setJavaCodebaseVersion = function(version) {
        // for compatibility only
    };

    /**
     * Overrides the codebase version for HTML5.
     * If another codebase than the default codebase should be used, this method has to be called before setHTML5Codebase.
     * @param version The version of the codebase that should be used for HTML5 applets.
     */
    applet.setHTML5CodebaseVersion = function(version, offline) {
        var numVersion = parseFloat(version);
        if (numVersion !== NaN && numVersion < 5.0) {
            console.log("The GeoGebra HTML5 codebase version "+numVersion+" is deprecated. Using version "+latestVersion+" instead.");
            return;
        } // Version 4.2 is not working properly
        html5OverwrittenCodebaseVersion = version;
        setDefaultHTML5CodebaseForVersion(version, offline);
    };

    applet.getHTML5CodebaseVersion = function() {
        return html5CodebaseVersion;
    };

    applet.getParameters = function() {
        return parameters;
    };


    /**
     * Overrides the codebase for Java.
     * @param codebase Can be an URL or a local file path.
     * @param offline Set to true, if the codebase is a local URL and no web URL
     */
    applet.setJavaCodebase = function(codebase, offline) {
        //not needed, for API compatibility only
    };

    applet.setFontsCSSURL = function(url) {
        fonts_css_url = url;
    };

    /**
      * This function is not needed anymore. Keep it for downward compatibility of the API.
      */
    applet.setGiacJSURL = function(url) {
    };

    /**
     * Overrides the JNLP file to use.
     * By default (if this method is not called), the jnlp file in the codebase directory is used.
     * Cannot be used in combination with setJNLPBaseDir
     * @param newJnlpFilePath The absolute path to the JNLP file.
     */
    applet.setJNLPFile = function(newJnlpFilePath) {
        jnlpFilePath = newJnlpFilePath;
    };

    /**
     * Sets an alternative base directory for the JNLP File. The path must not include the version number.
     * @param baseDir
     */
    applet.setJNLPBaseDir = function(baseDir) {
        //not needed, for comaptibility only
    };

    /**
     * Injects the applet;
     * @param containerID The id of the HTML element that is the parent of the new applet.
     * All other content (innerText) of the container will be overwritten with the new applet.
     * @param type Can be 'preferJava', 'preferHTML5', 'java', 'html5', 'auto' or 'screenshot'. Default='auto';
     * @param boolean noPreview. Set to true if no preview image should be shown
     * @return The type of the applet that was injected or null if the applet could not be injected.
     */
    applet.inject = function() {
        function isOwnIFrame() {
            return window.frameElement && window.frameElement.getAttribute("data-singleton");
        }

        var type = 'auto';
        var container_ID = parameters.id;
        var container;
        var noPreview = false;
        for(var i=0; i<arguments.length; i++) {
            var p = arguments[i];
            if (typeof(p) === "string") {
                p = p.toLowerCase();
                if (p === 'preferjava' || p === 'preferhtml5' || p === 'java' || p === 'html5' || p === 'auto' || p === 'screenshot' || p === 'prefercompiled' || p === 'compiled') {
                    type = p;
                } else {
                    container_ID = arguments[i];
                }
            } else if (typeof(p) === "boolean") {
                noPreview = p;
            } else if (p instanceof HTMLElement) {
                container = p;
            }
        }

        continueInject();

        function continueInject() {
            // Check if the initialization is complete
            if (! initComplete) {
                // Try again in 200 ms.
                setTimeout(continueInject, 200);
                return;
            }

            // Use the container id as appletid, if it was not defined.
            type = detectAppletType(type); // Sets the type to either 'java' or 'html5'

            var appletElem = container || document.getElementById(container_ID);

            if (!appletElem) {
                console.log("possibly bug on ajax loading? ");
                return;
            }

            // Remove an existing applet
            applet.removeExistingApplet(appletElem, false);

            // Read the applet dimensions from the container, if they were not defined in the params
            //it is okay, but sadly no height of the container, so we must take care of this too - geogebraweb won't wet widht and height if one if it 0
            if (parameters.width === undefined && appletElem.clientWidth) {
                parameters.width = appletElem.clientWidth;
            }
            if (parameters.height === undefined && appletElem.clientHeight) {
                parameters.height = appletElem.clientHeight;
            }

            if (!(parameters.width && parameters.height) && type === "html5") {
                delete parameters.width;
                delete parameters.height;
            }

            // Inject the new applet
            loadedAppletType = type;
            if (type === "screenshot") {
                injectScreenshot(appletElem, parameters);
            } else if (type === "compiled") {
                injectCompiledApplet(appletElem, parameters, true);
            } else {
                // Check if applets should be loaded instantly or with a play button
                var playButton = false;
                if (parameters.hasOwnProperty("playButton") && parameters.playButton || parameters.hasOwnProperty("clickToLoad") && parameters.clickToLoad) {
                    playButton = true;
                } else if (parameters.hasOwnProperty("playButtonAutoDecide") && parameters.playButtonAutoDecide) {
                    playButton = (!isInIframe() || isOwnIFrame())  && isMobileDevice();
                }

                if (playButton) {
                    loadedAppletType = "screenshot";
                    injectPlayButton(appletElem, parameters, noPreview, type);
                } else {
                    injectHTML5Applet(appletElem, parameters, noPreview);
                }
            }
        }

        return;
    };

    function isInIframe () {
        try {
            return window.self !== window.top;
        } catch (e) {
            return true;
        }
    }

    function isMobileDevice() {
        if (parameters.hasOwnProperty("screenshotGenerator") && parameters.screenshotGenerator) {
            return false;
        }
        return (Math.max(screen.width,screen.height) < 800);
    }

    applet.getViews = function() {
        return views;
    };

    /**
     * @returns boolean Whether the system is capable of showing the GeoGebra Java applet
     */
    applet.isJavaInstalled = function() {
        return false;
    };

    function pluginEnabled(name) {
        var plugins = navigator.plugins,
            i = plugins.length,
            regExp = new RegExp(name, 'i');
        while (i--) {
            if (regExp.test(plugins[i].name)) {
                return true;
            }
        }
        return false;
    }

    var getTubeURL = function() {
        var tubeurl, protocol;
        // Determine the url for the tube API
        if (parameters.tubeurl !== undefined) {

            // Url was specified in parameters
            tubeurl = parameters.tubeurl;
        } else if (
            window.location.host.indexOf("www.geogebra.org") > -1 ||
            window.location.host.indexOf("www-beta.geogebra.org") > -1 ||
            window.location.host.indexOf("www-test.geogebra.org") > -1 ||
            window.location.host.indexOf("alpha.geogebra.org") > -1 ||
            window.location.host.indexOf("groot.geogebra.org") > -1 ||
            window.location.host.indexOf("pool.geogebra.org") > -1 ||
            window.location.host.indexOf("strange.geogebra.org") > -1 ||
            window.location.host.indexOf("marvl.geogebra.org") > -1 ||
            window.location.host.indexOf("beta.geogebra.org") > -1 ||
            window.location.host.indexOf("tube.geogebra.org") > -1 ||
            window.location.host.indexOf("tube-beta.geogebra.org") > -1 ||
            window.location.host.indexOf("cloud.geogebra.org") > -1 ||
            window.location.host.indexOf("cloud-beta.geogebra.org") > -1 ||
            window.location.host.indexOf("cloud-stage.geogebra.org") > -1 ||
            window.location.host.indexOf("stage.geogebra.org") > -1 ||
            window.location.host.indexOf("tube-test.geogebra.org") > -1) {

            // if the script is used on a tube site, use this site for the api url.
            tubeurl = window.location.protocol + "//" + window.location.host;
        } else {
            // Use main tube url
            if (window.location.protocol.substr(0,4) === 'http') {
                protocol = window.location.protocol;
            } else {
                protocol = 'http:';
            }
            tubeurl = protocol+"//www.geogebra.org";
        }
        return tubeurl;
    };

    var fetchParametersFromTube = function(successCallback, materialsApiURL) {
        var tubeurl = materialsApiURL ?  materialsApiURL.substring(0, materialsApiURL.indexOf("/", 8)) 
            : getTubeURL();

        // load ggbbase64 string and settings from API
        var api_request = {
            "request": {
                "-api": "1.0.0",
                "login": {
                    "-type":"cookie",
                    "-getuserinfo":"false"
                },
                "task": {
                    "-type": "fetch",
                    "fields": {
                        "field": [
                            { "-name": "id" },
                            { "-name": "geogebra_format" },
//                            { "-name": "prefapplettype" },
                            { "-name": "width" },
                            { "-name": "height" },
                            { "-name": "toolbar" },
                            { "-name": "menubar" },
                            { "-name": "inputbar" },
                            { "-name": "stylebar" },
                            { "-name": "reseticon" },
                            { "-name": "labeldrags" },
                            { "-name": "shiftdragzoom" },
                            { "-name": "rightclick" },
                            { "-name": "ggbbase64" },
                            { "-name": "preview_url" },
                            { "-name": "appname" }
                        ]
                    },
                    "filters" : {
                        "field": [{
                                "-name":"id", "#text": ""+parameters.material_id+""
                        }]
                    },
                    "order": {
                        "-by": "id",
                        "-type": "asc"
                    },
                    "limit": { "-num": "1" }
                }
            }
        },

        // TODO: add prefapplet type (params:'type' API:'prefapplettype')
        // TODO: Read view settings from database

        success = function() {
            var text = xhr.responseText;
            var jsondata= JSON.parse(text); //retrieve result as an JSON object
            var item = null;
            for (i=0; jsondata.responses && i<jsondata.responses.response.length; i++) {
                if (jsondata.responses.response[i].item !== undefined) {
                    item = jsondata.responses.response[i].item;
                }
            }
            if (item === null) {
                onError();
                return;
            }

            if (item.geogebra_format !== "") {
                ggbVersion = item.geogebra_format;
            }
            if (parameters.ggbBase64 === undefined) {
                parameters.ggbBase64 = item.ggbBase64;
            }
            if (parameters.width === undefined) {
                parameters.width = item.width;
            }
            if (parameters.height === undefined) {
                parameters.height = item.height;
            }
            if (parameters.showToolBar === undefined) {
                parameters.showToolBar = item.toolbar === "true";
            }
            if (parameters.showMenuBar === undefined) {
                parameters.showMenuBar = item.menubar === "true";
            }
            if (parameters.showAlgebraInput === undefined) {
                parameters.showAlgebraInput = item.inputbar === "true";
            }
            if (parameters.allowStyleBar === undefined) {
                parameters.allowStyleBar = item.stylebar === "true";
            }
            if (parameters.showResetIcon === undefined) {
                parameters.showResetIcon = item.reseticon === "true";
            }
            if (parameters.enableLabelDrags === undefined) {
                parameters.enableLabelDrags = item.labeldrags === "true";
            }
            if (parameters.enableShiftDragZoom === undefined) {
                parameters.enableShiftDragZoom = item.shiftdragzoom === "true";
            }
            if (parameters.enableRightClick === undefined) {
                parameters.enableRightClick = item.rightclick === "true";
            }
            if (parameters.showToolBarHelp === undefined) {
                parameters.showToolBarHelp =  parameters.showToolBar;
            }
            if (parameters.appname === undefined) {
                parameters.appname =  item.appname;
            }

            if (parseFloat(item.geogebra_format) >= 5.0) {
                views.is3D = true;
            }

//            var views = {"is3D":false,"AV":false,"SV":false,"CV":false,"EV2":false,"CP":false,"PC":false,"DA":false,"FI":false,"PV":false,"macro":false};

            var previewUrl = (item.previewUrl === undefined) ? tubeurl+"/files/material-"+item.id+".png" : item.previewUrl;
            applet.setPreviewImage(previewUrl, tubeurl+"/images/GeoGebra_loading.png", tubeurl+"/images/applet_play.png");

            successCallback();
        };

        var url = tubeurl+"/api/json.php";
        var xhr = createCORSRequest('POST', url);

        var onError = function() {
            log("Error: The request for fetching material_id " + parameters.material_id + " from tube was not successful.");
        };

        if (!xhr) {
            onError();
            return;
        }

        // Response handlers.
        xhr.onload = success;
        xhr.onerror = onError;
        xhr.onprogress = function(){}; // IE9 will abort the xhr.send without this

        // Send request
        if ( xhr.setRequestHeader ) { // IE9's XDomainRequest does not support this method
            xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        }
        xhr.send(JSON.stringify(api_request));
    };

    // Create the XHR object.
    function createCORSRequest(method, url) {
        var xhr = new XMLHttpRequest();
        if ("withCredentials" in xhr) {
            // XHR for Chrome/Firefox/Opera/Safari.
            xhr.open(method, url, true);
        } else if (typeof XDomainRequest !== "undefined") {
            // XDomainRequest for IE.
            xhr = new XDomainRequest();
            xhr.open(method, url);
        } else {
            // CORS not supported.
            xhr = null;
        }
        return xhr;
    }

    /**
     * @returns boolean Whether the system is capable of showing the GeoGebra HTML5 applet
     */
    applet.isHTML5Installed = function() {
        if (isInternetExplorer()) {
            if ((views.is3D || html5CodebaseScript === "web3d.nocache.js") && getIEVersion() < 11) { // WebGL is supported since IE 11
                return false;
            } else if (getIEVersion() < 10) {
                return false;
            }
        }
        return true;
    };

    /**
     * @returns boolean Whether the system is capable of showing precompiled HTML5 applets
     */
    applet.isCompiledInstalled = function() {
        if (isInternetExplorer()) {
            if (views.is3D && getIEVersion() < 11) { // WebGL is supported since IE 11
                return false;
            } else if (getIEVersion() < 9) {
                return false;
            }
        }
        return true;
    };

    /**
     * @returns The type of the loaded applet or null if no applet was loaded yet.
     */
    applet.getLoadedAppletType = function() {
        return loadedAppletType;
    };

    applet.setPreviewImage = function(previewFilePath, loadingFilePath, playFilePath) {
        previewImagePath = previewFilePath;
        previewLoadingPath = loadingFilePath;
        previewPlayPath = playFilePath;
    };

    applet.removeExistingApplet = function(appletParent, showScreenshot) {
        var i;
        if (typeof appletParent === 'string') {
            appletParent = document.getElementById(appletParent);
        }
        if (loadedAppletType === 'compiled' && window[parameters.id] !== undefined) {
            // Stop/remove the applet
            if (typeof window[parameters.id].stopAnimation === "function") {
                window[parameters.id].stopAnimation();
            }
            if (typeof window[parameters.id].remove === "function") {
                window[parameters.id].remove();
            }

            // Set the applet objects to undefined
            if (ggbApplets !== undefined) {
                for (i=0; i<ggbApplets.length;i++) {
                    if (ggbApplets[i] === window[parameters.id]) {
                        ggbApplets.splice(i, 1);
                    }
                }
            }
            window[parameters.id] = undefined;
        }

        loadedAppletType = null;
        for (i=0; i<appletParent.childNodes.length;i++) {
            var tag = appletParent.childNodes[i].tagName;
            var className = appletParent.childNodes[i].className;
            if (appletParent.childNodes[i].className === "applet_screenshot") {
                if (showScreenshot) {
                    // Show the screenshot instead of the removed applet
                    appletParent.childNodes[i].style.display = "block";
                    loadedAppletType = "screenshot";
                } else {
                    // Hide the screenshot
                    appletParent.childNodes[i].style.display = "none";
                }
            } else if ((tag === "APPLET" || tag === "ARTICLE" || tag === "DIV" || (loadedAppletType === 'compiled' && (tag === "SCRIPT" || tag === "STYLE"))
              ) && className !== "applet_scaler prerender") {
                // Remove the applet
                appletParent.removeChild(appletParent.childNodes[i]);
                i--;
            }
        }

        var appName = (parameters.id !== undefined ? parameters.id : "ggbApplet");
        var app = window[appName];
        if (app) {
            if (typeof app === "object" && typeof app.getBase64 === "function") { // Check if the variable is a GeoGebra Applet and remove it
                app.remove();
                window[appName] = null;
            }
        }
    };

    applet.refreshHitPoints = function() {
        if (parseVersion(ggbHTML5LoadedCodebaseVersion) >= 5.0) {
            return true; // Not necessary anymore in 5.0
        }
        var app = applet.getAppletObject();
        if (app) {
            if (typeof app.recalculateEnvironments === "function") {
                app.recalculateEnvironments();
                return true;
            }
        }
        return false;
    };

    applet.startAnimation = function() {
        var app = applet.getAppletObject();
        if (app) {
            if (typeof app.startAnimation === "function") {
                app.startAnimation();
                return true;
            }
        }
        return false;
    };

    applet.stopAnimation = function() {
        var app = applet.getAppletObject();
        if (app) {
            if (typeof app.stopAnimation === "function") {
                app.stopAnimation();
                return true;
            }
        }
        return false;
    };

    applet.setPreCompiledScriptPath = function(path, version) {
        preCompiledScriptPath = path;
        if (preCompiledResourcePath === null) {
            preCompiledResourcePath = preCompiledScriptPath;
        }
        preCompiledScriptVersion = version;
    };

    applet.setPreCompiledResourcePath = function(path) {
        preCompiledResourcePath = path;
    };

    applet.getAppletObject = function() {
        var appName = (parameters.id !== undefined ? parameters.id : "ggbApplet");
        return window[appName];
    };

    applet.resize = function() {};

    var appendParam = function(applet, name, value) {
        var param = document.createElement("param");
        param.setAttribute("name", name);
        param.setAttribute("value", value);
        applet.appendChild(param);
    };

    var valBoolean = function(value) {
        return (value && value !== "false");
    };

    var injectHTML5Applet = function(appletElem, parameters, noPreview) {
        if (parseVersion(html5CodebaseVersion) <= 4.2) {
            noPreview = true;
        }
        // Decide if the script has to be (re)loaded or renderGGBElement can be used to load the applet
        var loadScript = !isRenderGGBElementEnabled && !scriptLoadStarted;
        // Reload the script when not loaded yet, or  currently the wrong version is loaded
        if ((!isRenderGGBElementEnabled && !scriptLoadStarted) || (ggbHTML5LoadedCodebaseVersion !== html5CodebaseVersion || (ggbHTML5LoadedCodebaseIsWebSimple && !html5CodebaseIsWebSimple))) {
            loadScript = true ;
            isRenderGGBElementEnabled = false;
            scriptLoadStarted = false;
        }

        var article = document.createElement("article");
        var oriWidth = parameters.width;
        var oriHeight = parameters.height;
        parameters.disableAutoScale = parameters.disableAutoScale === undefined ? GGBAppletUtils.isFlexibleWorksheetEditor() : parameters.disableAutoScale;

        // The HTML5 version 4.4 changes the height depending on which bars are shown. So we have to correct it here.
        if (parameters.width !== undefined) {
            if (parseVersion(html5CodebaseVersion) <= 4.4) {
                if (valBoolean(parameters.showToolBar)) {
                    parameters.height -= 7;
                }
                if (valBoolean(parameters.showAlgebraInput)) {
                    parameters.height -= 37;
                }
                if (parameters.width < 605 && valBoolean(parameters.showToolBar)) {
                    parameters.width = 605;
                    oriWidth = 605;
                }
            } else {
                // calculate the minWidth
                var minWidth = 100;
                if (valBoolean(parameters.showToolBar) || valBoolean(parameters.showMenuBar)) {
                    if (parameters.hasOwnProperty("customToolBar")) {
                        parameters.customToolbar = parameters.customToolBar;
                    }
                    minWidth = (valBoolean(parameters.showMenuBar) ? 245 : 155);
                }

                if (oriWidth < minWidth) {
                    parameters.width = minWidth;
                    oriWidth = minWidth;
                }
            }
        }
        article.className = "notranslate"; //we remove geogebraweb here, as we don't want to parse it out of the box.
        article.style.border = 'none';
        article.style.display = 'inline-block';

        for (var key in parameters) {
            if (parameters.hasOwnProperty(key) && key !== "appletOnLoad" && key !== 'scale') {
                article.setAttribute("data-param-"+key, parameters[key]);
            }
        }
        if(fonts_css_url){
            article.setAttribute("data-param-fontscssurl",fonts_css_url);
        }

        // Resize the applet when the window is resized
        applet.resize = function() {
            GGBAppletUtils.responsiveResize(appletElem, parameters);
        };

        if (typeof jQuery === "function") {
            jQuery(window).resize(function() {
                applet.resize();
            });
        } else {
            var oldOnResize = null;
            if (window.onresize !== undefined && typeof window.onresize === "function") {
                oldOnResize = window.onresize;
            }
            window.onresize = function() {
                applet.resize();
                if (typeof oldOnResize === "function") {
                    oldOnResize();
                }
            };
        }

	var oriAppletOnload = (typeof parameters.appletOnLoad === "function") ?
                      parameters.appletOnLoad : function(){};

        // Add the tag for the preview image
        if (!noPreview && parameters.width !== undefined) {

            // Prevent GeoGebraWeb from showing the splash
            if (!parameters.hasOwnProperty('showSplash')) {
                article.setAttribute("data-param-showSplash", 'false');
            }
            // Check if the screenshot is already there
            var previewPositioner = appletElem.querySelector(".applet_scaler.prerender");
            var preRendered = (previewPositioner !== null);

            if (!preRendered) {
                var previewContainer = createScreenShotDiv(oriWidth, oriHeight, parameters.borderColor, false);

                // This div is needed to have an element with position relative as origin for the absolute positioned image
                previewPositioner = document.createElement("div");
                previewPositioner.className = "applet_scaler";
                previewPositioner.style.position = "relative";
                previewPositioner.style.display = 'block';
                previewPositioner.style.width = oriWidth+'px';
                previewPositioner.style.height = oriHeight+'px';
            } else {
                var previewContainer = previewPositioner.querySelector(".ggb_preview");
            }

            if (window.GGBT_spinner) {
                window.GGBT_spinner.attachSpinner(previewPositioner, '66%');
            }

            if (parseVersion(html5CodebaseVersion)>=5.0) {

                // Workaround: Remove the preview image when the applet is fully loaded
                
                parameters.appletOnLoad = function(api) {
                    var preview = appletElem.querySelector(".ggb_preview");
                    if (preview) {
                        preview.parentNode.removeChild(preview);
                    }
                    if (window.GGBT_spinner) {
                        window.GGBT_spinner.removeSpinner(previewPositioner);
                    }
                    if (window.GGBT_wsf_view) {
                        $(window).trigger("resize");
                    } else {
                        window.onresize();
                    }

                    oriAppletOnload(api);
                    
                };

                if (!preRendered) {
                    previewPositioner.appendChild(previewContainer);
                }
            } else {
                article.appendChild(previewContainer);
            }

            previewPositioner.appendChild(article);
            if (!preRendered) {
                appletElem.appendChild(previewPositioner);
            }

            // Redo resizing when screenshot is loaded to recalculate it after scrollbars are gone
            setTimeout(function() {
                applet.resize();
            }, 1);
        } else {
            var appletScaler = document.createElement("div");
            appletScaler.className = "applet_scaler";
            appletScaler.style.position = "relative";
            appletScaler.style.display = 'block';

            appletScaler.appendChild(article);
            appletElem.appendChild(appletScaler);

            // Apply scaling
	    parameters.appletOnLoad = function(api){
		applet.resize();
		oriAppletOnload(api);
	    }
        }


        function renderGGBElementWithParams(article, parameters) {
            if (parameters && typeof parameters.appletOnLoad === "function" && typeof renderGGBElement === "function") {
                renderGGBElement(article, parameters.appletOnLoad);
            } else {
                renderGGBElement(article);
            }

            log("GeoGebra HTML5 applet injected and rendered with previously loaded codebase.", parameters);
        }


        function renderGGBElementOnTube(a, parameters) {
            if (typeof renderGGBElement === "undefined") {
                //it is possible, that we get here many times, before script are loaded. So best here to save the article element for later - otherwise only last article processed :-)
                if (html5AppletsToProcess === null) {
                    html5AppletsToProcess = [];
                }
                html5AppletsToProcess.push({
                    article : a,
                    params : parameters
                });
                window.renderGGBElementReady = function() {
                    isRenderGGBElementEnabled = true;
                    if (html5AppletsToProcess !== null && html5AppletsToProcess.length) {
                        html5AppletsToProcess.forEach(function(obj) {
                            renderGGBElementWithParams(obj.article, obj.params);
                        });
                        html5AppletsToProcess = null;
                    }

                };

                //TODO: remove this hack, because it is a hack!
                if (parseVersion(html5CodebaseVersion) < 5.0) {
                        a.className += " geogebraweb";
                }

            } else {
                renderGGBElementWithParams(a, parameters);
            }
        }

        // Load the web script
        if (loadScript) {
            scriptLoadStarted = true;
            if (parseVersion(html5CodebaseVersion)>=4.4) {
                if(!html5Codebase.requirejs){
                    var fontscript2 = document.createElement("script");
                    fontscript2.type = 'text/javascript';
                    fontscript2.src = html5Codebase+'js/webfont.js';
                    appletElem.appendChild(fontscript2);
                }
            }

            // Remove all table tags within an article tag if there are any
            for (var i=0; i<article.childNodes.length;i++) {
                var tag = article.childNodes[i].tagName;
                if (tag === "TABLE") {
                    article.removeChild(article.childNodes[i]);
                    i--;
                }
            }

            // Remove old script tags
            if (ggbHTML5LoadedScript !== null) {
                var el = document.querySelector('script[src="'+ggbHTML5LoadedScript+'"]');
                if (el !== undefined && el !== null) {
                    el.parentNode.removeChild(el);
                }
            }

            var script = document.createElement("script");

            var scriptLoaded = function() {
                renderGGBElementOnTube(article, parameters);
            };

            script.src=html5Codebase + html5CodebaseScript;
            
            ggbHTML5LoadedCodebaseIsWebSimple = html5CodebaseIsWebSimple;
            ggbHTML5LoadedCodebaseVersion = html5CodebaseVersion;
            ggbHTML5LoadedScript = script.src;
            log("GeoGebra HTML5 codebase loaded: '"+html5Codebase+"'.", parameters);
            if (!html5OverwrittenCodebase) {
                web3d.succeeded=web3d();
                scriptLoaded();
            } else if (html5Codebase.requirejs) {
                require(['geogebra/runtime/js/web3d/web3d.nocache'], scriptLoaded);
            } else {
                script.onload = scriptLoaded;
                appletElem.appendChild(script);
            }
        } else {
            renderGGBElementOnTube(article, parameters);
        }

        parameters.height = oriHeight;
        parameters.width = oriWidth;
    };

    var injectCompiledApplet = function(appletElem, parameters, noPreview) {
        var appletObjectName = parameters.id;
        //if (scale !== 1) {
        //    parameters.scale = scale;
        //    appletElem.style.minWidth = parameters.width * scale+"px";
        //    appletElem.style.minHeight = parameters.height * scale+"px";
        //}

        var viewContainer = document.createElement("div");
        viewContainer.id = "view-container-"+appletObjectName;
        viewContainer.setAttribute("width", parameters.width);
        viewContainer.setAttribute("height", parameters.height);
        viewContainer.style.width = parameters.width+"px";
        viewContainer.style.height = parameters.height+"px";
//        viewContainer.style.border = "1px solid black";

        if (parameters.showSplash === undefined) {
            parameters.showSplash = true;
        }

        // Resize the applet when the window is resized
        var oldOnResize = null;
        if (window.onresize !== undefined && typeof window.onresize === "function") {
            oldOnResize = window.onresize;
        }
        window.onresize = function() {
            var scale = GGBAppletUtils.getScale(parameters, appletElem);
            var scaleElem = null;
            for (var i=0; i<appletElem.childNodes.length;i++) {
                if (appletElem.childNodes[i].className.match(/^applet_scaler/)) {
                    scaleElem = appletElem.childNodes[i];
                    break;
                }
            }

            if (scaleElem !== null) {                
                scaleElem.parentNode.style.transform = "";
                if (!isNaN(scale) && scale !== 1) {
                    // Set the scale factor for the applet
                    GGBAppletUtils.scaleElement(scaleElem, scale);
                    scaleElem.parentNode.style.width = ((parameters.width+2)*scale)+'px';
                    scaleElem.parentNode.style.height = ((parameters.height+2)*scale)+'px';

                } else {
                    // Remove scaling
                    GGBAppletUtils.scaleElement(scaleElem, 1);
                    scaleElem.parentNode.style.width = (parameters.width+2)+'px';
                    scaleElem.parentNode.style.height = (parameters.height+2)+'px';
                }
            }

            var appName = (parameters.id !== undefined ? parameters.id : "ggbApplet");
            var app = window[appName];
            if (app !== undefined && app !== null && typeof app.recalculateEnvironments === "function") {
                app.recalculateEnvironments();
            }

            if (oldOnResize !== null) {
                oldOnResize();
            }
        };


        var viewImages = document.createElement("div");
        viewImages.id = '__ggb__images';

        // Add the tag for the preview image
        var appletScaler;
        if (!noPreview && previewImagePath !== null && parseVersion(html5CodebaseVersion)>=4.4 && parameters.width !== undefined) {
            var previewContainer = createScreenShotDiv(parameters.width, parameters.height, parameters.borderColor, false);

            // This div is needed to have an element with position relative as origin for the absolute positioned image
            var previewPositioner = document.createElement("div");
            previewPositioner.style.position = "relative";
            previewPositioner.className = "applet_scaler";
            previewPositioner.style.display = 'block';
            previewPositioner.style.width = parameters.width+'px';
            previewPositioner.style.height = parameters.height+'px';
            previewPositioner.appendChild(previewContainer);
            appletElem.appendChild(previewPositioner);
            appletScaler = previewPositioner;

            // Redo resizing when screenshot is loaded to recalculate it after scrollbars are gone
            setTimeout(function() {
                window.onresize();
            }, 1);

            if (typeof window.GGBT_ws_header_footer === "object") {
                window.GGBT_ws_header_footer.setWsScrollerHeight();
            }
        } else {
            appletScaler = document.createElement("div");
            appletScaler.className = "applet_scaler";
            appletScaler.style.position = "relative";
            appletScaler.style.display = 'block';

            appletElem.appendChild(appletScaler);
            window.onresize();
        }

        // Load the web fonts
        if (!ggbCompiledResourcesLoadFinished && !ggbCompiledResourcesLoadInProgress) {
//            var resource1 = document.createElement("link");
//            resource1.type = 'text/css';
//            resource1.rel = 'stylesheet';
//            resource1.href = preCompiledResourcePath+'/mathquillggb.css';
//
//            var resource2 = document.createElement("script");
//            resource2.type = 'text/javascript';
//            resource2.src = preCompiledResourcePath+'/jquery-1.7.2.min.js';
//
//            var resource3 = document.createElement("script");
//            resource3.type = 'text/javascript';
//            resource3.src = preCompiledResourcePath+'/mathquillggb.js';

            var resource4 = document.createElement("script");
            resource4.type = 'text/javascript';
            resource4.innerHTML = '\n' +
                'WebFontConfig = {\n' +
                '   loading: function() {},\n' +
                '   active: function() {},\n' +
                '   inactive: function() {},\n' +
                '   fontloading: function(familyName, fvd) {},\n' +
                '   fontactive: function(familyName, fvd) {' +
                '       if (!ggbCompiledAppletsLoaded) {' +
                '           ggbCompiledAppletsLoaded = true;' +
                '           ' +
                '           setTimeout(function() {' +
                '               ggbCompiledResourcesLoadFinished = true;' +
                '               ggbCompiledResourcesLoadInProgress = false;' +
                '               if (window.ggbApplets != undefined) {' +
                '                   for (var i = 0 ; i < window.ggbApplets.length ; i++) {' +
                '                       window.ggbApplets[i].init({scale:window.ggbApplets[i].scaleParameter, url:window.ggbApplets[i].preCompiledScriptPath+"/", ss:'+(parameters.showSplash?'true':'false')+', sdz:'+(parameters.enableShiftDragZoom?'true':'false')+', rc:'+(parameters.enableRightClick?'true':'false')+', sri:'+(parameters.showResetIcon?'true':'false')+'});' +
                '                   }' +
                '               }' +
                '               if (typeof window.ggbCompiledAppletsOnLoad == "function") {' +
                '                   window.ggbCompiledAppletsOnLoad();' +
                '               }' +
                '           },1);' +
                '       }' +
                '   },\n' +
                '   fontinactive: function(familyName, fvd) {},\n' +
                '   custom: {\n' +
                '       families: ["geogebra-sans-serif", "geogebra-serif"],\n' +
                '           urls: [ "'+preCompiledResourcePath+"/fonts/fonts.css"+'" ]\n' +
                '   }\n' +
                '};\n' +
                '\n';

            var resource5 = document.createElement("script");
            resource5.type = 'text/javascript';
            resource5.src = preCompiledResourcePath+'/fonts/webfont.js';

            ggbCompiledResourcesLoadInProgress = true;
//            appletScaler.appendChild(resource1);
//            appletScaler.appendChild(resource2);
//            appletScaler.appendChild(resource3);
            appletScaler.appendChild(resource4);
            appletScaler.appendChild(resource5);
        }

        // Load the applet script
        var appletStyle = document.createElement("style");
        appletStyle.innerHTML = '\n' +
            '.view-frame {\n' +
            '    border: 1px solid black;\n' +
            '    display: inline-block;\n' +
            '}\n' +
            '#tip {\n' +
            '    background-color: yellow;\n' +
            '    border: 1px solid blue;\n' +
            '    position: absolute;\n' +
            '    left: -200px;\n' +
            '    top: 100px;\n' +
            '};\n';

        appletScaler.appendChild(appletStyle);

        var script = document.createElement("script");

        var scriptLoaded = function() {
            window[appletObjectName].preCompiledScriptPath = preCompiledScriptPath;
            window[appletObjectName].scaleParameter = parameters.scale;

            if (!noPreview) {
                appletScaler.querySelector(".ggb_preview").remove();
            }
            appletScaler.appendChild(viewContainer);
            appletScaler.appendChild(viewImages);

            if (ggbCompiledResourcesLoadFinished) {
                window[appletObjectName].init({scale:parameters.scale, url:preCompiledScriptPath+'/', ss:parameters.showSplash, sdz:parameters.enableShiftDragZoom, rc:parameters.enableRightClick, sri:parameters.showResetIcon});
                if (typeof window.ggbAppletOnLoad === 'function') {
                    window.ggbAppletOnLoad(appletElem.id);
                }
                if (typeof parameters.appletOnLoad === 'function') {
                    parameters.appletOnLoad(appletElem.id);
                }

            }
        };

        var scriptFile = preCompiledScriptPath + "/applet.js" + (preCompiledScriptVersion !== null && preCompiledScriptVersion !== null ? "?v="+preCompiledScriptVersion : "");
        script.src=scriptFile;
        script.onload = scriptLoaded;

        log("GeoGebra precompiled applet injected. Script="+scriptFile+".");
        appletScaler.appendChild(script);
    };

    var injectScreenshot = function(appletElem, parameters, showPlayButton) {
        // Add the tag for the preview image
        var previewContainer = createScreenShotDiv(parameters.width, parameters.height, parameters.borderColor, showPlayButton);

        // This div is needed to have an element with position relative as origin for the absolute positioned image
        var previewPositioner = document.createElement("div");
        previewPositioner.style.position = "relative";
        previewPositioner.style.display = 'block';
        previewPositioner.style.width = parameters.width+'px';
        previewPositioner.style.height = parameters.height+'px';
        previewPositioner.className = "applet_screenshot applet_scaler" + (showPlayButton ? " applet_screenshot_play" : "");
        previewPositioner.appendChild(previewContainer);

        var scale = GGBAppletUtils.getScale(parameters, appletElem, showPlayButton);


        if(showPlayButton) {
            appletElem.appendChild(getPlayButton());
            if (!window.GGBT_wsf_view) {
                appletElem.style.position = "relative";
            }
        } else if (window.GGBT_spinner) {
            window.GGBT_spinner.attachSpinner(previewPositioner, '66%');
        }

        appletElem.appendChild(previewPositioner);

        // Set the scale for the preview image
        if (scale !== 1 && !isNaN(scale)) {
            // Set the scale factor for the preview image
            GGBAppletUtils.scaleElement(previewPositioner, scale);
            previewPositioner.style.width = (parameters.width)+'px';
            previewPositioner.style.height = (parameters.height)+'px';
            previewPositioner.parentNode.style.width = (parameters.width*scale)+'px';
            previewPositioner.parentNode.style.height = (parameters.height*scale)+'px';
        }

        applet.resize = function() {
            resizeScreenshot(appletElem, previewContainer, previewPositioner, showPlayButton);
        };

        if (typeof jQuery === "function") {
            jQuery(window).resize(function() {
                applet.resize();
            });
        } else {
            var oldOnResize = null;
            // Resize the preview when the window is resized
            if (window.onresize !== undefined && typeof window.onresize === "function") {
                oldOnResize = window.onresize;
            }
            window.onresize = function() {
                applet.resize();
                if (typeof oldOnResize === "function") {
                    oldOnResize();
                }
            };
        }
        applet.resize();
    };

    function resizeScreenshot(appletElem, previewContainer, previewPositioner, showPlayButton, oldOnResize) {
        if (!appletElem.contains(previewContainer)) { // Don't resize the screenshot if it is not visible (anymore)
            return;
        }

        if (typeof window.GGBT_wsf_view === "object" && window.GGBT_wsf_view.isFullscreen()) {
            if (appletElem.id !== "fullscreencontent") {
                return;
            }
            window.GGBT_wsf_view.setCloseBtnPosition(appletElem);
        }

        var scale = GGBAppletUtils.getScale(parameters, appletElem, showPlayButton);

        if (previewPositioner.parentNode !== null) {
            if (!isNaN(scale) && scale !== 1) {
                GGBAppletUtils.scaleElement(previewPositioner, scale);
                previewPositioner.parentNode.style.width = (parameters.width * scale) + 'px';
                previewPositioner.parentNode.style.height = (parameters.height * scale) + 'px';
            } else {
                GGBAppletUtils.scaleElement(previewPositioner, 1);
                previewPositioner.parentNode.style.width = (parameters.width) + 'px';
                previewPositioner.parentNode.style.height = (parameters.height) + 'px';
            }
        }

        // positions the applet in the center of the popup
        if(typeof window.GGBT_wsf_view === 'object' && window.GGBT_wsf_view.isFullscreen()) {
            GGBAppletUtils.positionCenter(appletElem);
        }

        if (typeof window.GGBT_ws_header_footer === "object") {
            window.GGBT_ws_header_footer.setWsScrollerHeight();
        }

        if (typeof oldOnResize === "function") {
            oldOnResize();
        }

    }

    applet.onExitFullscreen = function(fullscreenContainer, appletElem) {
        appletElem.appendChild(fullscreenContainer);
    };

    var injectPlayButton = function(appletElem, parameters, noPreview, type) {
        injectScreenshot (appletElem, parameters, true);

        // Load applet on play button click
        var play = function() {
            // Remove the screenshot after the applet is injected
            var elems = [];
            for (i=0; i<appletElem.childNodes.length;i++) {
                elems.push(appletElem.childNodes[i]);
            }
           
            if (window.GGBT_wsf_view) {
                var content = window.GGBT_wsf_view.renderFullScreen(appletElem, parameters.id);
                var container = document.getElementById("fullscreencontainer");
                var oldcontent = jQuery(appletElem).find('.fullscreencontent');
                if (oldcontent.length > 0) {
                    // Reuse the previously rendered applet
                    content.remove();
                    oldcontent.attr("id", "fullscreencontent").show();
                    jQuery(container).append(oldcontent);
                    window.onresize();
                } else {
                    // Render a new applet
                    injectHTML5Applet(content, parameters, false);
                }
                window.GGBT_wsf_view.launchFullScreen(container);
            } else {
                loadedAppletType = type;
                injectHTML5Applet(appletElem, parameters, false);
            }

            if (!window.GGBT_wsf_view) {
                for (i = 0; i < elems.length; i++) {
                    appletElem.removeChild(elems[i]);
                }
            }
        };

        // Find the play button and add the click handler
        var imgs = appletElem.getElementsByClassName("ggb_preview_play");
        for (var i = 0; i < imgs.length; i++) {
            imgs[i].addEventListener('click', play, false);
            imgs[i].addEventListener('ontouchstart', play, false);
        }

        // Call onload
        if (typeof window.ggbAppletPlayerOnload === 'function') {
            window.ggbAppletPlayerOnload(appletElem);
        }

        //remove fullscreen button if not needed
        if (isMobileDevice() && window.GGBT_wsf_view) {
            $(".wsf-element-fullscreen-button").remove();
        }
    };

    var getPlayButton = function() {
        var playButtonContainer = document.createElement("div");
        playButtonContainer.className = 'ggb_preview_play icon-applet-play';
        if (!window.GGBT_wsf_view) { // on tube, the play button image is defined in a css file
            var css = '' +
                '.icon-applet-play {' +
                '   width: 100%;' +
                '   height: 100%;box-sizing: border-box;position: absolute;z-index: 1001;cursor: pointer;border-width: 0px;' +
                '   background-color: transparent;background-repeat: no-repeat;left: 0;top: 0;background-position: center center;' +
                '   background-image: url("'+getTubeURL()+'/images/worksheet/icon-start-applet.png");' +
                '}' +
                '.icon-applet-play:hover {' +
                        'background-image: url("'+getTubeURL()+'/images/worksheet/icon-start-applet-hover.png");' +
                '}';
            var style = document.createElement('style');

            if (style.styleSheet) {
                style.styleSheet.cssText = css;
            } else {
                style.appendChild(document.createTextNode(css));
            }

            document.getElementsByTagName('head')[0].appendChild(style);
        }
        return playButtonContainer;
    };

    var createScreenShotDiv = function(oriWidth, oriHeight, borderColor, showPlayButton) {
        var previewContainer = document.createElement("div");
        previewContainer.className = "ggb_preview";
        previewContainer.style.position = "absolute";
        //previewContainer.style.zIndex = "1000001";
        // too high z-index causes various problems
        // overlaps fixed header
        // overlaps popups
        previewContainer.style.zIndex = "90";
        previewContainer.style.width = oriWidth-2+'px'; // Remove 2 pixel for the border
        previewContainer.style.height = oriHeight-2+'px'; // Remove 2 pixel for the border
        previewContainer.style.top = "0px";
        previewContainer.style.left = "0px";
        previewContainer.style.overflow = "hidden";
        previewContainer.style.backgroundColor = "white";
        var bc = 'lightgrey';
        if (borderColor !== undefined) {
            if (borderColor === "none") {
                bc = "transparent";
            } else {
                bc = borderColor;
            }
        }
        previewContainer.style.border = "1px solid " + bc;

        var preview = document.createElement("img");
        preview.style.position = "relative";
        preview.style.zIndex = "1000";
        preview.style.top = "-1px"; // Move up/left to hide the border on the image
        preview.style.left = "-1px";
        if (previewImagePath !== null) {
            preview.setAttribute("src", previewImagePath);
        }
        preview.style.opacity = 0.7;

        if (previewLoadingPath !== null) {

            var previewOverlay;

            var pWidth, pHeight;
            if (!showPlayButton) {
                previewOverlay = document.createElement("img");
                previewOverlay.style.position = "absolute";
                previewOverlay.style.zIndex = "1001";
                previewOverlay.style.opacity = 1.0;

                preview.style.opacity = 0.3;

                pWidth = 360;
                if (pWidth > (oriWidth/4*3)) {
                    pWidth = oriWidth/4*3;
                }
                pHeight = pWidth/5.8;
                previewOverlay.setAttribute("src", previewLoadingPath);

                previewOverlay.setAttribute("width", pWidth);
                previewOverlay.setAttribute("height", pHeight);
                var pX = (oriWidth - pWidth) / 2;
                var pY = (oriHeight - pHeight) / 2;
                previewOverlay.style.left = pX + "px";
                previewOverlay.style.top = pY + "px";

                previewContainer.appendChild(previewOverlay);
            }
        }

        previewContainer.appendChild(preview);
        return previewContainer;
    };


    /**
     * Detects the type of the applet (java or html5).
     * If a fixed type is passed in preferredType (java or html5), this type is forced.
     * Otherwise the method tries to find out which types are supported by the system.
     * If a preferredType is passed, this type is used if it is supported.
     * If auto is passed, the preferred type is html5 for versions >= 4.4 and java for all versions < 4.4.
     * @param preferredType can be 'preferJava', 'preferHTML5', 'java', 'html5', 'auto' or 'screenshot'. Default='auto'
     */
    var detectAppletType = function(preferredType) {
        preferredType = preferredType.toLowerCase();
        if ((preferredType === "html5") || (preferredType === "screenshot") || (preferredType === "compiled")) {
            return preferredType;
        }

        if ((preferredType === "prefercompiled") && (preCompiledScriptPath !== null)) {
            if (applet.isCompiledInstalled()) {
                return "compiled";
            } 
        }
        
        return "html5";
    };

    var getIEVersion = function() {
        var a=navigator.appVersion;
        if (a.indexOf("Trident/7.0") > 0) {
            return 11;
        } else {
            return a.indexOf('MSIE')+1?parseFloat(a.split('MSIE')[1]):999;
        }
    };

    var isInternetExplorer = function() {
        return (getIEVersion() !== 999);
    };


    var modules = ["web", "webSimple", "web3d", "tablet", "tablet3d", "phone"];
    /**
     * @param version Can be: 3.2, 4.0, 4.2, 4.4, 5.0, test, test42, test44, test50
     */
    var setDefaultHTML5CodebaseForVersion = function(version, offline) {
        html5CodebaseVersion = version;
        if (offline) {
            setHTML5CodebaseInternal(html5CodebaseVersion, true);
            return;
        }

        // Set the codebase URL for the version
        var hasWebSimple = ! html5NoWebSimple;
        if (hasWebSimple) {
            var v = parseVersion(html5CodebaseVersion);
            if ((!isNaN(v) && v < 4.4)) {
                hasWebSimple = false;
            }
        }

        var protocol,
            codebase;
        if (window.location.protocol.substr(0,4) === 'http') {
            protocol = window.location.protocol;
        } else {
            protocol = 'http:';
        }
        var index = html5CodebaseVersion.indexOf("//");
        if (index > 0) {
            codebase = html5CodebaseVersion;            
        } else if(index === 0) {
            codebase = protocol + html5CodebaseVersion;
        } else {
            codebase = "https://cdn.geogebra.org/apps/"+latestVersion+"/";
        }
        
        for(var key in modules){
            if (html5CodebaseVersion.slice(modules[key].length*-1) === modules[key] ||
                html5CodebaseVersion.slice((modules[key].length+1)*-1) === modules[key]+"/") {
                setHTML5CodebaseInternal(codebase, false);
                return;
            }
        }

        // Decide if web, websimple or web3d should be used
        if (!GGBAppletUtils.isFlexibleWorksheetEditor() && hasWebSimple && !views.is3D && !views.AV && !views.SV && !views.CV && !views.EV2 && !views.CP && !views.PC && !views.DA && !views.FI && !views.PV &&
            !valBoolean(parameters.showToolBar) && !valBoolean(parameters.showMenuBar) && !valBoolean(parameters.showAlgebraInput) && !valBoolean(parameters.enableRightClick)) {
            codebase += 'webSimple/';
        } else {
            codebase += 'web3d/';
        }

        setHTML5CodebaseInternal(codebase, false);
    };

    var setHTML5CodebaseInternal = function(codebase, offline) {
        if(codebase.requirejs){
             html5Codebase = codebase;
             return;
        }
        if (codebase.slice(-1) !== '/') {
            codebase += '/';
        }
        html5Codebase = codebase;

        if (offline === null) {
            offline = (codebase.indexOf("http") === -1);
        }
        isHTML5Offline = offline;

        // Set the scriptname (web or webSimple)
        html5CodebaseScript = "web.nocache.js";
        html5CodebaseIsWebSimple = false;
        var folders = html5Codebase.split("/");
        if (folders.length > 1) {
            if (! offline && folders[folders.length-2] === 'webSimple') {  // Currently we don't use webSimple for offline worksheets
                html5CodebaseScript = "webSimple.nocache.js";
                html5CodebaseIsWebSimple = true;
            } else if (modules.indexOf(folders[folders.length-2]) >= 0) {
                html5CodebaseScript = folders[folders.length-2] + ".nocache.js";
            }
        }

        // Extract the version from the codebase folder
        folders = codebase.split('/');
        html5CodebaseVersion = folders[folders.length-3];
        if (html5CodebaseVersion.substr(0,4) === 'test') {
            html5CodebaseVersion = html5CodebaseVersion.substr(4,1) + '.' + html5CodebaseVersion.substr(5,1);
        } else if (html5CodebaseVersion.substr(0,3) === 'war' || html5CodebaseVersion.substr(0,4) === 'beta') {
            html5CodebaseVersion = '5.0';
        }

        // Check if the codebase version is deprecated
        var numVersion = parseFloat(html5CodebaseVersion);
        if (numVersion !== NaN && numVersion < 5.0) {
            console.log("The GeoGebra HTML5 codebase version "+numVersion+" is deprecated. Using version "+latestVersion+" instead.");
            setDefaultHTML5CodebaseForVersion("5.0", offline);
        }
    };

    var log = function(text, parameters) {
        if ( window.console && window.console.log ) {
            if(!parameters || (typeof parameters.showLogging === 'undefined') ||
                (parameters.showLogging && parameters.showLogging !== "false")) {
                    console.log(text);
            }
        }
    };

    // Read the material parameters from the tube API, if a material_id was passed
    if (parameters.material_id !== undefined) {
        fetchParametersFromTube(continueInit, parameters.materialsApi);
    } else {
        continueInit();
    }

    function continueInit() {
        var html5Version = ggbVersion;
        if (html5OverwrittenCodebaseVersion !== null) {
            html5Version = html5OverwrittenCodebaseVersion;
        } else {
            if (parseFloat(html5Version) < 5.0) { // Use 5.0 as default for html5. Change the version number here, when a new stable version is released.
                html5Version = "5.0";
            }
        }

        // Initialize the codebase with the default URLs
        setDefaultHTML5CodebaseForVersion(html5Version, false);

        if (html5OverwrittenCodebase !== null) {
            setHTML5CodebaseInternal(html5OverwrittenCodebase, isHTML5Offline);
        }
        initComplete = true;
    }

    return applet;
};

var GGBAppletUtils = (function() {
    "use strict";

    function isFlexibleWorksheetEditor() {
        return (window.GGBT_wsf_edit !== undefined);
    }
    
    function scaleElement(el, scale){
        if (scale != 1) {
            el.style.transformOrigin = "0% 0% 0px";
            el.style.webkitTransformOrigin = "0% 0% 0px";
            el.style.transform = "scale(" + scale + "," + scale + ")";
            el.style.webkitTransform = "scale(" + scale + "," + scale + ")";
            el.style.maxWidth = "initial";
            // Remove the max width from the image and the div
            if (el.querySelector(".ggb_preview") !== null) {
                el.querySelector(".ggb_preview").style.maxWidth = "initial";
            }
            if (el.querySelectorAll('.ggb_preview img')[0] !== undefined) {
              el.querySelectorAll('.ggb_preview img')[0].style.maxWidth = "initial";
            }
            if (el.querySelectorAll('.ggb_preview img')[1] !== undefined) {
              el.querySelectorAll('.ggb_preview img')[1].style.maxWidth = "initial"
            }


        } else {
            el.style.transform = "none";
            el.style.webkitTransform = "none";
        }
    }

    function getWidthHeight(appletElem, appletWidth, allowUpscale, noBorder, scaleContainerClass) {
        // Find the container class
        var container = null;
        if (scaleContainerClass != undefined && scaleContainerClass != '') {
            var parent = appletElem.parentNode;
            while(parent != null) {
                if ((' ' + parent.className + ' ').indexOf(' ' + scaleContainerClass + ' ') > -1) {
                    container = parent;
                    break;
                } else {
                    parent = parent.parentNode;
                }
            }
        }

        var myWidth = 0, myHeight = 0, windowWidth = 0, border = 0, borderRight = 0, borderLeft = 0, borderTop = 0;

        if (container) {
            myWidth = container.offsetWidth;
            myHeight = container.offsetHeight;
        } else {
            if (window.innerWidth && document.documentElement.clientWidth) {
                myWidth = Math.min(window.innerWidth, document.documentElement.clientWidth);
                myHeight = Math.min(window.innerHeight, document.documentElement.clientHeight);
                // Using mywith instead of innerWidth because after rotating a mobile device the innerWidth is sometimes wrong (e.g. on Galaxy Note III)
                // windowWidth = window.innerWidth
                windowWidth = myWidth;
            } else if (typeof( window.innerWidth ) === 'number') {
                //Non-IE
                myWidth = window.innerWidth;
                myHeight = window.innerHeight;
                windowWidth = window.innerWidth;
            } else if (document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight )) {
                //IE 6+ in 'standards compliant mode'
                myWidth = document.documentElement.clientWidth;
                myHeight = document.documentElement.clientHeight;
                windowWidth = document.documentElement.clientWidth;
            } else if (document.body && ( document.body.clientWidth || document.body.clientHeight )) {
                //IE 4 compatible
                myWidth = document.body.clientWidth;
                myHeight = document.body.clientHeight;
                windowWidth = document.documentElement.clientWidth;
            }

            if (appletElem) {
                var rect = appletElem.getBoundingClientRect();
                if (rect.left > 0) {
                    if (rect.left <= myWidth && (noBorder === undefined || !noBorder)) {
                        if (document.dir === 'rtl') {
                            borderRight = myWidth - rect.width - rect.left;
                            borderLeft = (windowWidth <= 480 ? 10 : 30);
                        } else {
                            borderLeft = rect.left;
                            borderRight = (windowWidth <= 480 ? 10 : 30);
                        }
                        border = borderLeft + borderRight;
                    }
                }
            }

            // overwrite borders with other numbers if it is in fullscreen mode
            // make sure X is visible all the time
            if(appletElem && typeof window.GGBT_wsf_view === "object" && window.GGBT_wsf_view.isFullscreen()) {
                // APPLET IS DISPLAYED IN FULLSCREEN
                var appletRect = appletElem.getBoundingClientRect();

                // X is positioned to the right/left
                // set a border so it is visible
                if(window.GGBT_wsf_view.getCloseBtnPosition() === 'closePositionRight') {
                    // X is positioned to the right/left
                    // 40 is the width of the X close button
                    border = 40;
                    borderTop = 0;
                } else if(window.GGBT_wsf_view.getCloseBtnPosition() === 'closePositionTop') {
                    // X is positioned on top
                    border = 0;
                    borderTop = 40;
                }
            }
        }

        //console.log('myWidth: '+ myWidth);
        //console.log('myHeight: ' + myHeight);
        //console.log('border: ' + border);
        //console.log('borderTop: '+ borderTop);

        if (appletElem) {
            if ((allowUpscale === undefined || !allowUpscale) && appletWidth > 0 && appletWidth + border < myWidth) {
                myWidth = appletWidth;
            } else {
                myWidth -= border;
            }

            if(typeof window.GGBT_wsf_view === "object" && window.GGBT_wsf_view.isFullscreen() && (allowUpscale === undefined || !allowUpscale)) {
                // applet is displayed in fullscreen
                myHeight -= borderTop;
            }
        }

        //console.log('myWidth: ' + myWidth + ', myHeight: ' + myHeight);

        return {width: myWidth, height: myHeight};
    }

    function calcScale(parameters, appletElem, allowUpscale, showPlayButton, scaleContainerClass){
        if (parameters.isScreenshoGenerator) {
            return 1;
        }
        var ignoreHeight = (showPlayButton !== undefined && showPlayButton);
        var noScaleMargin = parameters.noScaleMargin != undefined && parameters.noScaleMargin;
        var windowSize = getWidthHeight(appletElem, parameters.width, allowUpscale, (ignoreHeight && window.GGBT_wsf_view) || noScaleMargin, scaleContainerClass);
        var windowWidth = parseInt(windowSize.width);

        var appletWidth = parameters.width;
        var appletHeight = parameters.height;
        if (appletWidth === undefined) {
            var articles = appletElem.getElementsByTagName('article');
            if (articles.length === 1) {
                appletWidth = articles[0].offsetWidth;
                appletHeight = articles[0].offsetHeight;
            }
        }

        var xscale = windowWidth / appletWidth;
        var yscale = (ignoreHeight ? 1 : windowSize.height / appletHeight);
        if (allowUpscale !== undefined && !allowUpscale) {
            xscale = Math.min(1, xscale);
            yscale = Math.min(1, yscale);
        }

        return Math.min(xscale, yscale);
    }

    function getScale(parameters, appletElem, showPlayButton) {
        var scale = 1,
            autoScale,
            allowUpscale = false;

        if (parameters.hasOwnProperty('allowUpscale')) {
            allowUpscale = parameters.allowUpscale;
        }

        if (parameters.hasOwnProperty('scale')) {
            scale = parseFloat(parameters.scale);
            if (isNaN(scale) || scale === null || scale === 0) {
                scale = 1;
            }
            if (scale > 1) {
                allowUpscale = true;
            }
        }

        if(appletElem && typeof window.GGBT_wsf_view === "object" && window.GGBT_wsf_view.isFullscreen()) {
            allowUpscale = true;
        }

        if (!(parameters.hasOwnProperty('disableAutoScale') && parameters.disableAutoScale)) {
            autoScale = calcScale(parameters, appletElem, allowUpscale, showPlayButton, parameters.scaleContainerClass);
        } else {
            return scale;
        }

        if (allowUpscale && (!parameters.hasOwnProperty('scale') || scale === 1)) {
            return autoScale;
        } else {
            return Math.min(scale, autoScale);
        }
    }

    /**
     * Positiones the applet in the center of the screen
     * Used for fullscreen popups
     * @param appletElem
     */
    function positionCenter(appletElem) {
        var windowWidth = Math.min(window.innerWidth, document.documentElement.clientWidth);
        var windowHeight = Math.min(window.innerHeight, document.documentElement.clientHeight);
        var appletRect = appletElem.getBoundingClientRect();

        var calcHorizontalBorder = (windowWidth - appletRect.width) / 2;
        var calcVerticalBorder = (windowHeight - appletRect.height) / 2;

        if(calcVerticalBorder < 0) {
            calcVerticalBorder = 0;
        }

        appletElem.style.position = "relative";

        if(window.GGBT_wsf_view.getCloseBtnPosition() === 'closePositionRight') {
            // X is positioned to the right/left

            if(calcHorizontalBorder < 40) {
                // if there is not enough space left for the X, don't position it in the center
                appletElem.style.left = '40px';
            } else {
                appletElem.style.left = calcHorizontalBorder + 'px';
            }
            appletElem.style.top = calcVerticalBorder + 'px';

        } else if(window.GGBT_wsf_view.getCloseBtnPosition() === 'closePositionTop') {
            // X is positioned on top

            if(calcVerticalBorder < 40) {
                // if there is not enough space left for the X, don't position it in the center
                appletElem.style.top = '40px';
            } else {
                appletElem.style.top = calcVerticalBorder + 'px';
            }

            appletElem.style.left = calcHorizontalBorder + 'px';
        }
    }

    function responsiveResize(appletElem, parameters) {
        var article = appletElem.getElementsByTagName("article")[0];

        if (article) {
            if (typeof window.GGBT_wsf_view === "object" && window.GGBT_wsf_view.isFullscreen()) {
                var articles = appletElem.getElementsByTagName("article");
                if (articles.length > 0 && parameters.id !== articles[0].getAttribute("data-param-id")) {
                    return;
                }

                window.GGBT_wsf_view.setCloseBtnPosition(appletElem);
            }

            if(article.parentElement && /fullscreen/.test(article.parentElement.className)){
                return; //fullscreen button inside applet pressed
            }

            var scale = getScale(parameters, appletElem);

            article.removeAttribute("data-param-scale");
            if (isFlexibleWorksheetEditor()) {
                article.setAttribute("data-param-scale", scale);
            }

            var scaleElem = null;
            for (var i = 0; i < appletElem.childNodes.length; i++) {
                if (appletElem.childNodes[i].className !== undefined && appletElem.childNodes[i].className.match(/^applet_scaler/)) {
                    scaleElem = appletElem.childNodes[i];
                    break;
                }
            }

            if (scaleElem !== null && scaleElem.querySelector(".noscale") !== null) {
                return;
            }
            
            var appName = (parameters.id !== undefined ? parameters.id : "ggbApplet");
            var app = window[appName];
            
            if ((app == null || !app.recalculateEnvironments) && scaleElem !== null && !scaleElem.className.match(/fullscreen/)) {
                scaleElem.parentNode.style.transform = "";
                if (!isNaN(scale) && scale !== 1) {
                    // Set the scale factor for the applet
                    scaleElem.parentNode.style.width = (parameters.width * scale) + 'px';
                    scaleElem.parentNode.style.height = (parameters.height * scale) + 'px';
                    scaleElement(scaleElem, scale);

                } else {
                    // Remove scaling
                    scaleElement(scaleElem, 1);
                    scaleElem.parentNode.style.width = (parameters.width) + 'px';
                    scaleElem.parentNode.style.height = (parameters.height) + 'px';
                }
            }
            // positions the applet in the center of the popup
            if (typeof window.GGBT_wsf_view === 'object' && window.GGBT_wsf_view.isFullscreen()) {
                positionCenter(appletElem);
            }

            if (window.GGBT_wsf_view && !window.GGBT_wsf_view.isFullscreen()) {
                window.GGBT_wsf_general.adjustContentToResize($(article).parents('.content-added-content'));
            }
        }
    }

    return {
        responsiveResize: responsiveResize,
        isFlexibleWorksheetEditor: isFlexibleWorksheetEditor,
        positionCenter: positionCenter,
        getScale: getScale,
        scaleElement: scaleElement
    };
})();

if(typeof define === "function" && define.amd){
    define([], function(){ return GGBApplet; })
}

function web3d(){
	  var $intern_0 = 'bootstrap', $intern_1 = 'begin', $intern_2 = 'gwt.codesvr.web3d=', $intern_3 = 'gwt.codesvr=', $intern_4 = 'web3d', $intern_5 = 'startup', $intern_6 = 'DUMMY', $intern_7 = 0, $intern_8 = 1, $intern_9 = 'iframe', $intern_10 = 'position:absolute; width:0; height:0; border:none; left: -1000px;', $intern_11 = ' top: -1000px;', $intern_12 = 'CSS1Compat', $intern_13 = '<!doctype html>', $intern_14 = '', $intern_15 = '<html><head><\/head><body><\/body><\/html>', $intern_16 = 'undefined', $intern_17 = 'readystatechange', $intern_18 = 10, $intern_19 = 'script', $intern_20 = 'javascript', $intern_21 = 'Failed to load ', $intern_22 = 'moduleStartup', $intern_23 = 'scriptTagAdded', $intern_24 = 'moduleRequested', $intern_25 = 'meta', $intern_26 = 'name', $intern_27 = 'web3d::', $intern_28 = '::', $intern_29 = 'gwt:property',
	  $intern_30 = 'content', $intern_31 = '=', $intern_32 = 'gwt:onPropertyErrorFn', $intern_33 = 'Bad handler "', $intern_34 = '" for "gwt:onPropertyErrorFn"', $intern_35 = 'gwt:onLoadErrorFn', $intern_36 = '" for "gwt:onLoadErrorFn"', $intern_37 = '#', $intern_38 = '?', $intern_39 = '/', $intern_40 = 'img', $intern_41 = 'clear.cache.gif', $intern_42 = 'baseUrl', $intern_43 = 'web3d.nocache.js', $intern_44 = 'base', $intern_45 = '//', $intern_46 = 'user.agent', $intern_47 = 'webkit', $intern_48 = 'safari', $intern_49 = 'msie', $intern_50 = 11, $intern_51 = 'ie10', $intern_52 = 9, $intern_53 = 'ie9', $intern_54 = 8, $intern_55 = 'ie8', $intern_56 = 'gecko', $intern_57 = 'gecko1_8', $intern_58 = 2, $intern_59 = 3, $intern_60 = 4, $intern_61 = 'selectingPermutation', $intern_62 = 'web3d.devmode.js',
	  $intern_63 = '%WEB3D_PERMUTATION%', $intern_64 = ':1', $intern_65 = ':2', $intern_66 = ':3', $intern_67 = ':', $intern_68 = '.cache.js', $intern_69 = 'loadExternalRefs', $intern_70 = 'end';
	  var $wnd = window;
	  var $doc = document;
	  sendStats($intern_0, $intern_1);
	  function isHostedMode(){
	    var query = $wnd.location.search;
	    return query.indexOf($intern_2) != -1 || query.indexOf($intern_3) != -1;
	  }

	  function sendStats(evtGroupString, typeString){
	    if ($wnd.__gwtStatsEvent) {
	      $wnd.__gwtStatsEvent({moduleName:$intern_4, sessionId:$wnd.__gwtStatsSessionId, subSystem:$intern_5, evtGroup:evtGroupString, millis:(new Date).getTime(), type:typeString});
	    }
	  }

	  web3d.__sendStats = sendStats;
	  web3d.__moduleName = $intern_4;
	  web3d.__errFn = null;
	  web3d.__moduleBase = $intern_6;
	  web3d.__softPermutationId = $intern_7;
	  web3d.__computePropValue = null;
	  web3d.__getPropMap = null;
	  web3d.__installRunAsyncCode = function(){
	  }
	  ;
	  web3d.__gwtStartLoadingFragment = function(){
	    return null;
	  }
	  ;
	  web3d.__gwt_isKnownPropertyValue = function(){
	    return false;
	  }
	  ;
	  web3d.__gwt_getMetaProperty = function(){
	    return null;
	  }
	  ;
	  var __propertyErrorFunction = null;
	  var activeModules = $wnd.__gwt_activeModules = $wnd.__gwt_activeModules || {};
	  activeModules[$intern_4] = {moduleName:$intern_4};
	  web3d.__moduleStartupDone = function(permProps){
	    var oldBindings = activeModules[$intern_4].bindings;
	    activeModules[$intern_4].bindings = function(){
	      var props = oldBindings?oldBindings():{};
	      var embeddedProps = permProps[web3d.__softPermutationId];
	      for (var i = $intern_7; i < embeddedProps.length; i++) {
	        var pair = embeddedProps[i];
	        props[pair[$intern_7]] = pair[$intern_8];
	      }
	      return props;
	    }
	    ;
	  }
	  ;
	  var frameDoc;
	  function getInstallLocationDoc(){
	    setupInstallLocation();
	    return frameDoc;
	  }

	  function setupInstallLocation(){
	    if (frameDoc) {
	      return;
	    }
	    var scriptFrame = $doc.createElement($intern_9);
	    scriptFrame.id = $intern_4;
	    scriptFrame.style.cssText = $intern_10 + $intern_11;
	    scriptFrame.tabIndex = -1;
	    $doc.body.appendChild(scriptFrame);
	    frameDoc = scriptFrame.contentWindow.document;
	    frameDoc.open();
	    var doctype = document.compatMode == $intern_12?$intern_13:$intern_14;
	    frameDoc.write(doctype + $intern_15);
	    frameDoc.close();
	  }

	  function installScript(filename){
	    function setupWaitForBodyLoad(callback){
	      function isBodyLoaded(){
	        if (typeof $doc.readyState == $intern_16) {
	          return typeof $doc.body != $intern_16 && $doc.body != null;
	        }
	        return /loaded|complete/.test($doc.readyState);
	      }

	      var bodyDone = isBodyLoaded();
	      if (bodyDone) {
	        callback();
	        return;
	      }
	      function checkBodyDone(){
	        if (!bodyDone) {
	          if (!isBodyLoaded()) {
	            return;
	          }
	          bodyDone = true;
	          callback();
	          if ($doc.removeEventListener) {
	            $doc.removeEventListener($intern_17, checkBodyDone, false);
	          }
	          if (onBodyDoneTimerId) {
	            clearInterval(onBodyDoneTimerId);
	          }
	        }
	      }

	      if ($doc.addEventListener) {
	        $doc.addEventListener($intern_17, checkBodyDone, false);
	      }
	      var onBodyDoneTimerId = setInterval(function(){
	        checkBodyDone();
	      }
	      , $intern_18);
	    }

	    function installCode(code_0){
	      var doc = getInstallLocationDoc();
	      var docbody = doc.body;
	      var script = doc.createElement($intern_19);
	      script.language = $intern_20;
	      script.src = code_0;
	      if (web3d.__errFn) {
	        script.onerror = function(){
	          web3d.__errFn($intern_4, new Error($intern_21 + code_0));
	        }
	        ;
	      }
	      docbody.appendChild(script);
	      sendStats($intern_22, $intern_23);
	    }

	    sendStats($intern_22, $intern_24);
	    setupWaitForBodyLoad(function(){
	      installCode(filename);
	    }
	    );
	  }

	  web3d.__startLoadingFragment = function(fragmentFile){
	    return computeUrlForResource(fragmentFile);
	  }
	  ;
	  web3d.__installRunAsyncCode = function(code_0){
	    var doc = getInstallLocationDoc();
	    var docbody = doc.body;
	    var script = doc.createElement($intern_19);
	    script.language = $intern_20;
	    script.text = code_0;
	    docbody.appendChild(script);
	  }
	  ;
	  function processMetas(){
	    var metaProps = {};
	    var propertyErrorFunc;
	    var onLoadErrorFunc;
	    var metas = $doc.getElementsByTagName($intern_25);
	    for (var i = $intern_7, n = metas.length; i < n; ++i) {
	      var meta = metas[i], name_0 = meta.getAttribute($intern_26), content;
	      if (name_0) {
	        name_0 = name_0.replace($intern_27, $intern_14);
	        if (name_0.indexOf($intern_28) >= $intern_7) {
	          continue;
	        }
	        if (name_0 == $intern_29) {
	          content = meta.getAttribute($intern_30);
	          if (content) {
	            var value_0, eq = content.indexOf($intern_31);
	            if (eq >= $intern_7) {
	              name_0 = content.substring($intern_7, eq);
	              value_0 = content.substring(eq + $intern_8);
	            }
	             else {
	              name_0 = content;
	              value_0 = $intern_14;
	            }
	            metaProps[name_0] = value_0;
	          }
	        }
	         else if (name_0 == $intern_32) {
	          content = meta.getAttribute($intern_30);
	          if (content) {
	            try {
	              propertyErrorFunc = eval(content);
	            }
	             catch (e) {
	              alert($intern_33 + content + $intern_34);
	            }
	          }
	        }
	         else if (name_0 == $intern_35) {
	          content = meta.getAttribute($intern_30);
	          if (content) {
	            try {
	              onLoadErrorFunc = eval(content);
	            }
	             catch (e) {
	              alert($intern_33 + content + $intern_36);
	            }
	          }
	        }
	      }
	    }
	    __gwt_getMetaProperty = function(name_0){
	      var value_0 = metaProps[name_0];
	      return value_0 == null?null:value_0;
	    }
	    ;
	    __propertyErrorFunction = propertyErrorFunc;
	    web3d.__errFn = onLoadErrorFunc;
	  }

	  function computeScriptBase(){
	    function getDirectoryOfFile(path){
	      var hashIndex = path.lastIndexOf($intern_37);
	      if (hashIndex == -1) {
	        hashIndex = path.length;
	      }
	      var queryIndex = path.indexOf($intern_38);
	      if (queryIndex == -1) {
	        queryIndex = path.length;
	      }
	      var slashIndex = path.lastIndexOf($intern_39, Math.min(queryIndex, hashIndex));
	      return slashIndex >= $intern_7?path.substring($intern_7, slashIndex + $intern_8):$intern_14;
	    }

	    function ensureAbsoluteUrl(url_0){
	      if (url_0.match(/^\w+:\/\//)) {
	      }
	       else {
	        var img = $doc.createElement($intern_40);
	        img.src = url_0 + $intern_41;
	        url_0 = getDirectoryOfFile(img.src);
	      }
	      return url_0;
	    }

	    function tryMetaTag(){
	      var metaVal = __gwt_getMetaProperty($intern_42);
	      if (metaVal != null) {
	        return metaVal;
	      }
	      return $intern_14;
	    }

	    function tryNocacheJsTag(){
	      var scriptTags = $doc.getElementsByTagName($intern_19);
	      for (var i = $intern_7; i < scriptTags.length; ++i) {
	        if (scriptTags[i].src.indexOf($intern_43) != -1) {
	          return getDirectoryOfFile(scriptTags[i].src);
	        }
	      }
	      return $intern_14;
	    }

	    function tryBaseTag(){
	      var baseElements = $doc.getElementsByTagName($intern_44);
	      if (baseElements.length > $intern_7) {
	        return baseElements[baseElements.length - $intern_8].href;
	      }
	      return $intern_14;
	    }

	    function isLocationOk(){
	      var loc = $doc.location;
	      return loc.href == loc.protocol + $intern_45 + loc.host + loc.pathname + loc.search + loc.hash;
	    }

	    var tempBase = tryMetaTag();
	    if (tempBase == $intern_14) {
	      tempBase = tryNocacheJsTag();
	    }
	    if (tempBase == $intern_14) {
	      tempBase = tryBaseTag();
	    }
	    if (tempBase == $intern_14 && isLocationOk()) {
	      tempBase = getDirectoryOfFile($doc.location.href);
	    }
	    tempBase = ensureAbsoluteUrl(tempBase);
	    return tempBase;
	  }

	  function computeUrlForResource(resource){
	    if (resource.match(/^\//)) {
	      return resource;
	    }
	    if (resource.match(/^[a-zA-Z]+:\/\//)) {
	      return resource;
	    }
	    return web3d.__moduleBase + resource;
	  }

	  function getCompiledCodeFilename(){
	    var answers = [];
	    var softPermutationId = $intern_7;
	    function unflattenKeylistIntoAnswers(propValArray, value_0){
	      var answer = answers;
	      for (var i = $intern_7, n = propValArray.length - $intern_8; i < n; ++i) {
	        answer = answer[propValArray[i]] || (answer[propValArray[i]] = []);
	      }
	      answer[propValArray[n]] = value_0;
	    }

	    var values = [];
	    var providers = [];
	    function computePropValue(propName){
	      var value_0 = providers[propName](), allowedValuesMap = values[propName];
	      if (value_0 in allowedValuesMap) {
	        return value_0;
	      }
	      var allowedValuesList = [];
	      for (var k in allowedValuesMap) {
	        allowedValuesList[allowedValuesMap[k]] = k;
	      }
	      if (__propertyErrorFunction) {
	        __propertyErrorFunction(propName, allowedValuesList, value_0);
	      }
	      throw null;
	    }

	    providers[$intern_46] = function(){
	      var ua = navigator.userAgent.toLowerCase();
	      var docMode = $doc.documentMode;
	      if (function(){
	        return ua.indexOf($intern_47) != -1;
	      }
	      ())
	        return $intern_48;
	      if (function(){
	        return ua.indexOf($intern_49) != -1 && (docMode >= $intern_18 && docMode < $intern_50);
	      }
	      ())
	        return $intern_51;
	      if (function(){
	        return ua.indexOf($intern_49) != -1 && (docMode >= $intern_52 && docMode < $intern_50);
	      }
	      ())
	        return $intern_53;
	      if (function(){
	        return ua.indexOf($intern_49) != -1 && (docMode >= $intern_54 && docMode < $intern_50);
	      }
	      ())
	        return $intern_55;
	      if (function(){
	        return ua.indexOf($intern_56) != -1 || docMode >= $intern_50;
	      }
	      ())
	        return $intern_57;
	      return $intern_48;
	    }
	    ;
	    values[$intern_46] = {'gecko1_8':$intern_7, 'ie10':$intern_8, 'ie8':$intern_58, 'ie9':$intern_59, 'safari':$intern_60};
	    __gwt_isKnownPropertyValue = function(propName, propValue){
	      return propValue in values[propName];
	    }
	    ;
	    web3d.__getPropMap = function(){
	      var result = {};
	      for (var key in values) {
	        if (values.hasOwnProperty(key)) {
	          result[key] = computePropValue(key);
	        }
	      }
	      return result;
	    }
	    ;
	    web3d.__computePropValue = computePropValue;
	    $wnd.__gwt_activeModules[$intern_4].bindings = web3d.__getPropMap;
	    sendStats($intern_0, $intern_61);
	    if (isHostedMode()) {
	      return computeUrlForResource($intern_62);
	    }
	    var strongName;
	    try {
	      unflattenKeylistIntoAnswers([$intern_57], $intern_63);
	      unflattenKeylistIntoAnswers([$intern_51], $intern_63 + $intern_64);
	      unflattenKeylistIntoAnswers([$intern_53], $intern_63 + $intern_65);
	      unflattenKeylistIntoAnswers([$intern_48], $intern_63 + $intern_66);
	      strongName = answers[computePropValue($intern_46)];
	      var idx = strongName.indexOf($intern_67);
	      if (idx != -1) {
	        softPermutationId = parseInt(strongName.substring(idx + $intern_8), $intern_18);
	        strongName = strongName.substring($intern_7, idx);
	      }
	    }
	     catch (e) {
	    }
	    web3d.__softPermutationId = softPermutationId;
	    return computeUrlForResource(strongName + $intern_68);
	  }

	  function loadExternalStylesheets(){
	    if (!$wnd.__gwt_stylesLoaded) {
	      $wnd.__gwt_stylesLoaded = {};
	    }
	    sendStats($intern_69, $intern_1);
	    sendStats($intern_69, $intern_70);
	  }

	  processMetas();
	  web3d.__moduleBase = "%MODULE_BASE%";
	  activeModules[$intern_4].moduleBase = web3d.__moduleBase;
	  var filename = getCompiledCodeFilename();
	  loadExternalStylesheets();
	  sendStats($intern_0, $intern_70);
	  installScript(filename);
	  return true;
	}