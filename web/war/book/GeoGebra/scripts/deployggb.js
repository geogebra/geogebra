/*global renderGGBElement, deployJava, XDomainRequest, ggbApplets, __ggb__giac, console */
/**
 * (c) International GeoGebra Institute 2013
 * Licence for use: http://creativecommons.org/licenses/by-nc-nd/3.0/
 * For commercial use please see: http://www.geogebra.org/license
 *
 */

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
    var javaCodebase = "";
    var isOverriddenJavaCodebase = false;
    var isHTML5Offline = false;
    var isJavaOffline = false;
    var loadedAppletType = null;
    var javaCodebaseVersion = null;
    var html5CodebaseVersion = null;
    var html5CodebaseScript = null;
    var html5CodebaseIsWebSimple = false;
    var previewImagePath = null;
    var previewLoadingPath = null;
    var fonts_css_url = null;
    var giac_js_url = null;
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
        javaCodebaseVersion = version;
        setDefaultJavaCodebaseForVersion(version);
    };

    /**
     * Overrides the codebase version for HTML5.
     * If another codebase than the default codebase should be used, this method has to be called before setHTML5Codebase.
     * @param version The version of the codebase that should be used for HTML5 applets.
     */
    applet.setHTML5CodebaseVersion = function(version, offline) {
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
        isOverriddenJavaCodebase = true;

        if (codebase.slice(-1) === '/') {
            javaCodebaseVersion = codebase.slice(-4,-1);
        } else {
            javaCodebaseVersion = codebase.slice(-3);
        }

        if (offline === null) {
            offline = (codebase.indexOf("http") === -1);
        }
        if (offline && jnlpBaseDir !== null) {
            jnlpBaseDir = null;
        }

        doSetJavaCodebase(codebase, offline);
    };

    applet.setFontsCSSURL = function(url) {
        fonts_css_url = url;
    };

    applet.setGiacJSURL = function(url) {
        giac_js_url = url;
    };


    applet.toggleAppletTypeControls = function(parentSelector) {
        var currentAppletType = applet.getLoadedAppletType();
        var displayJava = "none",
            displayHTML5 = "none";
        if (currentAppletType === "java" && applet.isHTML5Installed()) {
            displayHTML5 = "inline";
        } else if (currentAppletType === "html5" && applet.isJavaInstalled()) {
            displayJava = "inline";
        }

        var elem = document.querySelector(parentSelector + ' .view_as_Java');
        if (elem !== null)  {
            elem.style.display = displayJava;
        }
        elem = document.querySelector(parentSelector + ' .view_as_separator_Java');
        if (elem !== null) {
            elem.style.display = displayJava;
        }
        elem = document.querySelector(parentSelector + ' .view_as_HTML5');
        if (elem !== null) {
            elem.style.display = displayHTML5;
        }
        elem = document.querySelector(parentSelector + ' .view_as_separator_HTML5');
        if (elem !== null) {
            elem.style.display = displayHTML5;
        }

    };

    var doSetJavaCodebase = function(codebase, offline) {
        javaCodebase = codebase;

        // Check if the codebase is online or local
        isJavaOffline = offline;

        // Set the name of the JNLP file to the codebase directory
        if (jnlpBaseDir === null) {
            var dir='';
            if (isJavaOffline) {
                var loc = window.location.pathname;
                dir = loc.substring(0, loc.lastIndexOf('/'))+'/';
            }
            applet.setJNLPFile(dir+codebase+'/'+buildJNLPFileName(isJavaOffline));
        } else {
            applet.setJNLPFile(jnlpBaseDir+javaCodebaseVersion+'/'+buildJNLPFileName(isJavaOffline));
        }
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
        jnlpBaseDir = baseDir;
        applet.setJNLPFile(jnlpBaseDir+javaCodebaseVersion+'/'+buildJNLPFileName(isJavaOffline));
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
            if (type === "java") {
                injectJavaApplet(appletElem, parameters);
            } else if (type === "compiled") {
                injectCompiledApplet(appletElem, parameters, true);
            } else if (type === "screenshot") {
                injectScreenshot(appletElem, parameters);
            } else {
                injectHTML5Applet(appletElem, parameters, noPreview);
            }
        }

        return;
    };

    function getWidthHeight(appletElem) {
        var myWidth = 0, myHeight = 0;
        if (typeof( window.innerWidth ) === 'number') {
            //Non-IE
            myWidth = window.innerWidth;
            myHeight = window.innerHeight;
        } else if (document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight )) {
            //IE 6+ in 'standards compliant mode'
            myWidth = document.documentElement.clientWidth;
            myHeight = document.documentElement.clientHeight;
        } else if (document.body && ( document.body.clientWidth || document.body.clientHeight )) {
            //IE 4 compatible
            myWidth = document.body.clientWidth;
            myHeight = document.body.clientHeight;
        }

        if (appletElem) {
            if (appletElem.clientWidth > 0 && appletElem.clientWidth < myWidth) {
                myWidth = appletElem.clientWidth;
            } else {
                var rect = appletElem.getBoundingClientRect();
                if (rect.left>0) {
                    myWidth -= rect.left*2;
                }
            }
        }

//        window.alert( 'Width = ' + myWidth );
//        window.alert( 'Height = ' + myHeight );
        return {width: myWidth, height: myHeight};
    }
    
    function getScale(isScreenshoGenerator, appletElem){
        if (isScreenshoGenerator) {
            return 1;
        }
        var windowSize = getWidthHeight(appletElem);
        var windowWidth = parseInt(windowSize.width);

        var xscale = Math.min(1, windowWidth / parameters.width);
        var yscale = Math.min(1, windowSize.height / parameters.height);

        return Math.min(xscale, yscale);
    }

    applet.getViews = function() {
        return views;
    };

    /**
     * @returns boolean Whether the system is capable of showing the GeoGebra Java applet
     */
    applet.isJavaInstalled = function() {
        if (typeof deployJava === 'undefined') {
            // incase deployJava.js not available
            if (navigator.javaEnabled()) {
                // Check if IE is in metro mode
                if (isInternetExplorer() && getIEVersion() >= 10) {
                    if(window.innerWidth === screen.width && window.innerHeight === screen.height) {
                        return false;
                    }
                }

                // Check if on Android device
                if (navigator.userAgent.indexOf('Android ') > -1) {
                    return false;
                }

                // Check if the java plugin is installed
                if (!isInternetExplorer() && !pluginEnabled('java')) {
                    return false;
                }

                return true;
            }
        } else {
            return (deployJava.versionCheck("1.6.0+") || deployJava.versionCheck("1.4") || deployJava.versionCheck("1.5.0*"));
        }
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

    var fetchParametersFromTube = function(successCallback) {
        var tubeurl,
            protocol;
        // Determine the url for the tube API
        if (parameters.tubeurl !== undefined) {

            // Url was specified in parameters
                tubeurl = parameters.tubeurl;
        } else if (window.location.host.indexOf("geogebratube.org") > -1 || window.location.host.indexOf("tube.geogebra.org") > -1 ||
            window.location.host.indexOf("tube-test.geogebra.org") > -1 || window.location.host.indexOf("tube-beta.geogebra.org") > -1 || window.location.host.indexOf("geomatech-test.geogebra.org") > -1) {

            // if the script is used on a tube site, use this site for the api url.
            tubeurl = window.location.protocol + "//" + window.location.host;
        } else {
            // Use main tube url
            if (window.location.protocol.substr(0,4) === 'http') {
                protocol = window.location.protocol;
            } else {
                protocol = 'http:';
            }
            tubeurl = protocol+"//tube.geogebra.org";
        }

        // load ggbbase64 string and settings from API
        var api_request = {
            "request": {
                "-api": "1.0.0",
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
                            { "-name": "reseticon" },
                            { "-name": "labeldrags" },
                            { "-name": "shiftdragzoom" },
                            { "-name": "rightclick" },
                            { "-name": "ggbbase64" }
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

        success = function() {
            var text = xhr.responseText;
            var jsondata= JSON.parse(text); //retrieve result as an JSON object
            var item = jsondata.responses.response.item;
            if (item === undefined) {
                onError();
                return;
            }

            ggbVersion = item.geogebra_format;
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

            if (parseFloat(item.geogebra_format) >= 5.0) {
                views.is3D = true;
            }

//            var views = {"is3D":false,"AV":false,"SV":false,"CV":false,"EV2":false,"CP":false,"PC":false,"DA":false,"FI":false,"PV":false,"macro":false};

            applet.setPreviewImage(tubeurl+"/files/material-"+item.id+".png", tubeurl+"/images/GeoGebra_loading.png");

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
     * @return NULL if no version found. Else return some things like: '1.6.0_31'
     */
    var JavaVersion = function() {
        var resutl = null;
        // Walk through the full list of mime types.
        for( var i=0,size=navigator.mimeTypes.length; i<size; i++ )
        {
            // The jpi-version is the plug-in version.  This is the best
            // version to use.
            if( (resutl = navigator.mimeTypes[i].type.match(/^application\/x-java-applet;jpi-version=(.*)$/)) !== null ) {
                return resutl[1];
            }
        }
        return null;
    };

    /**
     * @returns boolean Whether the system is capable of showing the GeoGebra HTML5 applet
     */
    applet.isHTML5Installed = function() {
        if (isInternetExplorer()) {
            if ((views.is3D || html5CodebaseScript === "web3d.nocache.js") && getIEVersion() < 11) { // WebGL is supported since IE 11
                return false;
            } else if (views.CV && getIEVersion() < 10) {
                return false;
            } else if (getIEVersion() < 9) {
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

    applet.setPreviewImage = function(previewFilePath, loadingFilePath) {
        previewImagePath = previewFilePath;
        previewLoadingPath = loadingFilePath;
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
            if (appletParent.childNodes[i].className === "applet_screenshot") {
                if (showScreenshot) {
                    // Show the screenshot instead of the removed applet
                    appletParent.childNodes[i].style.display = "block";
                    loadedAppletType = "screenshot";
                } else {
                    // Hide the screenshot
                    appletParent.childNodes[i].style.display = "none";
                }
            } else if (tag === "APPLET" || tag === "ARTICLE" || tag === "DIV" || (loadedAppletType === 'compiled' && (tag === "SCRIPT" || tag === "STYLE"))) {
                // Remove the applet
                appletParent.removeChild(appletParent.childNodes[i]);
                i--;
            }
        }

        var appName = (parameters.id !== undefined ? parameters.id : "ggbApplet");
        var app = window[appName];
        if (app !== undefined) {
        //???????    eval(appName + "=null;");
            window[appName] = null;
        }
    };

    applet.refreshHitPoints = function() {
        if (parseFloat(ggbHTML5LoadedCodebaseVersion) >= 5.0) {
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

    //var getAppletObject = function() {
    //    var appName = (parameters.id !== undefined ? parameters.id : "ggbApplet");
    //    return window[appName];
    //};

//    var validateJavaApplet = function(appletElem, container_ID) {
//        if ((typeof appletElem.isAnimationRunning) === 'undefined') {
//            log("Error: The GeoGebra Java applet could not be started. Used JNLP file = '"+jnlpFilePath+"'. Switching to HTML5 instead.");
//
//            // Try html5 instead
//            applet.inject(container_ID, 'html5');
//        }
//    }

    var injectJavaApplet = function(appletElem, parameters) {
        if (views.CV) {
            var giac_url;
//            if (views.CV && (navigator.appVersion.indexOf("Mac")!=-1 || navigator.appVersion.indexOf("Linux")!=-1 || navigator.appVersion.indexOf("X11")!=-1)) {
            // Load the javascript version of giac
            if (giac_js_url !== null) {
                giac_url = giac_js_url;
            } else {
                giac_url = javaCodebase+'/giac.js';
            }
            var script = document.createElement("script");
            script.setAttribute("src", giac_url);

            script.onload = function() {
                window._GIAC_caseval = __ggb__giac.cwrap('_ZN4giac7casevalEPKc', 'string', ['string']);
            };
            appletElem.appendChild(script);

            script = document.createElement("script");
            script.innerHTML = "" +
                "       var _GIAC_caseval = 'nD';" +
                "       function _ggbCallGiac(exp) {" +
                "           var ret = _GIAC_caseval(exp);" +
                "           return ret;" +
                "       }";
            appletElem.appendChild(script);
        }

        var applet = document.createElement("applet");
        applet.setAttribute("name", (parameters.id !== undefined ? parameters.id : "ggbApplet"));
        if (parameters.height !== undefined && parameters.height > 0) {
            applet.setAttribute("height", parameters.height);
        }
        if (parameters.width !== undefined && parameters.width > 0) {
            applet.setAttribute("width", parameters.width);
        }
        applet.setAttribute("code", "dummy");

        appendParam(applet, "jnlp_href", jnlpFilePath);
        if (isOverriddenJavaCodebase) {
            appendParam(applet, "codebase", javaCodebase);
        }

        appendParam(applet, "boxborder", "false");
        appendParam(applet, "centerimage", "true");

        if(ggbVersion === "5.0") {
            appendParam(applet, "java_arguments", "-Xmx1024m -Djnlp.packEnabled=false");
        } else {
            appendParam(applet, "java_arguments", "-Xmx1024m -Djnlp.packEnabled=true");
        }

        // Add dynamic parameters
        for (var key in parameters) {
            if (key !== 'width' && key !== 'height') {
                appendParam(applet, key, parameters[key]);
            }
        }

        appendParam(applet, "framePossible", "false");
        if (! isJavaOffline) {
            appendParam(applet, "image", "http://tube.geogebra.org/images/java_loading.gif");
        }
        appendParam(applet, "codebase_lookup", "false");

        if (navigator.appName !== 'Microsoft Internet Explorer' || getIEVersion() > 9) {
            applet.appendChild(document.createTextNode("This is a Java Applet created using GeoGebra from www.geogebra.org - it looks like you don't have Java installed, please go to www.java.com"));
        }

        applet.style.display = "block";
        appletElem.appendChild(applet);

//        setTimeout(validateJavaApplet(appletElem, container_ID),5000);

        log("GeoGebra Java applet injected. Used JNLP file = '"+jnlpFilePath+"'"+(isOverriddenJavaCodebase?" with overridden codebase '"+javaCodebase+"'." : "."), parameters);
    };

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

        // The HTML5 version 4.4 changes the height depending on which bars are shown. So we have to correct it here.
        if (parameters.width !== undefined) {
            if (parseFloat(html5CodebaseVersion) <= 4.4) {
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
                if (parameters.width < 560 && valBoolean(parameters.showToolBar)) {
                    parameters.width = 560;
                    oriWidth = 560;
                }
                if (parseFloat(html5CodebaseVersion) >= 5.0 && parameters.width < 745 && valBoolean(parameters.showMenuBar)) {
                    parameters.width = 745;
                    oriWidth = 745;
                }
            }
        }
        article.className = "notranslate"; //we remove geogebraweb here, as we don't want to parse it out of the box.
        article.style.border = 'none';
        article.style.display = 'inline-block';


        var scale = 1;
        if (parameters.hasOwnProperty('scale')) {
            scale = parameters.scale;
        } else if (!isFlexibleWorksheetEditor()) {
            scale = getScale(parameters.screenshotGenerator, appletElem);
            if(!isNaN(scale) && scale!==1) {
                article.setAttribute("data-param-scale", scale);
            }
        }

        for (var key in parameters) {
            if (parameters.hasOwnProperty(key) && key !== "appletOnLoad") {
                article.setAttribute("data-param-"+key, parameters[key]);
            }
        }

        // Add the tag for the preview image
        if (!noPreview && previewImagePath !== null && parseFloat(html5CodebaseVersion)>=4.4 && parameters.width !== undefined) {
            var previewContainer = createScreenShotDiv(oriWidth, oriHeight, parameters.borderColor, scale);

            // This div is needed to have an element with position relative as origin for the absolute positioned image
            var previewPositioner = document.createElement("div");
            previewPositioner.style.position = "relative";
            previewPositioner.style.display = 'block';
            previewPositioner.style.width = oriWidth+'px';
            previewPositioner.style.height = oriHeight+'px';

            // Prevent GeoGebraWeb from showing the splash
            if (!parameters.hasOwnProperty('showSplash')) {
                article.setAttribute("data-param-showSplash", 'false');
            }

            if (parseFloat(html5CodebaseVersion)>=5.0) {

                // Workaround: Remove the preview image when the applet is fully loaded
                if (typeof parameters.appletOnLoad === "function") {
                    var oriAppletOnload = parameters.appletOnLoad;
                }
                parameters.appletOnLoad = function() {
                    var preview = appletElem.querySelector(".ggb_preview");
                    if (preview) {
                        preview.parentNode.removeChild(preview);
                    }
                    if (typeof oriAppletOnload === "function") {
                        oriAppletOnload();
                    }
                };
                previewPositioner.appendChild(previewContainer);

                // Set the scale for the preview image
                if (scale && scale !== 1 && !isNaN(scale) && !isFlexibleWorksheetEditor()) {
                    var scaleElem = previewPositioner;
                    if (appletElem.id === 'applet_container') {
                        scaleElem = appletElem;
                    }
                    // Set the scale factor for the preview image
                    scaleElem.style.transform = "scale(" + scale + "," + scale + ")";
                    scaleElem.style.transformOrigin = "0% 0% 0px";
                }
            } else {
                article.appendChild(previewContainer);
            }

            previewPositioner.appendChild(article);
            appletElem.appendChild(previewPositioner);
        } else {
            appletElem.appendChild(article);
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
                if (parseFloat(html5CodebaseVersion) < 5.0) {
                        a.className += " geogebraweb";
                }

            } else {
                renderGGBElementWithParams(a, parameters);
            }
        }

        // Load the web script
        if (loadScript) {
            scriptLoadStarted = true;
            if (parseFloat(html5CodebaseVersion)>=4.4) {
                var f_c_u;
                if (fonts_css_url === null) {
                    f_c_u = html5Codebase+"css/fonts.css";
                } else {
                    f_c_u = fonts_css_url;
                }

                var fontscript1 = document.createElement("script");
                fontscript1.type = 'text/javascript';
                fontscript1.innerHTML = '\n' +
                    '//<![CDATA[\n' +
                    'WebFontConfig = {\n' +
                    '   loading: function() {},\n' +
                    '   active: function() {},\n' +
                    '   inactive: function() {},\n' +
                    '   fontloading: function(familyName, fvd) {},\n' +
                    '   fontactive: function(familyName, fvd) {},\n' +
                    '   fontinactive: function(familyName, fvd) {},\n' +
                    '   custom: {\n' +
                    '       families: ["geogebra-sans-serif", "geogebra-serif"],\n' +
                    '           urls: [ "'+f_c_u+'" ]\n' +
                    '   }\n' +
                    '};\n' +
                    '//]]>\n' +
                    '\n';

                var fontscript2 = document.createElement("script");
                fontscript2.type = 'text/javascript';
                fontscript2.src = html5Codebase+'js/webfont.js';

                appletElem.appendChild(fontscript1);
                appletElem.appendChild(fontscript2);
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
                if (el !== undefined) {
                    el.parentNode.removeChild(el);
                }
            }

            var script = document.createElement("script");

            var scriptLoaded = function() {
                renderGGBElementOnTube(article, parameters);
            };

            log(html5Codebase);

            script.src=html5Codebase + html5CodebaseScript;
            script.onload = scriptLoaded;
            ggbHTML5LoadedCodebaseIsWebSimple = html5CodebaseIsWebSimple;
            ggbHTML5LoadedCodebaseVersion = html5CodebaseVersion;
            ggbHTML5LoadedScript = script.src;
            log("GeoGebra HTML5 applet injected. Codebase = '"+html5Codebase+"'.", parameters);
            appletElem.appendChild(script);
        } else {
            renderGGBElementOnTube(article, parameters);
        }

        parameters.height = oriHeight;
        parameters.width = oriWidth;
    };

    var injectCompiledApplet = function(appletElem, parameters, noPreview) {
        var appletObjectName = parameters.id;

        var scale = getScale(parameters.screenshotGenerator, appletElem);

        if (scale !== 1) {
            parameters.scale = scale;
            appletElem.style.minWidth = parameters.width * scale+"px";
            appletElem.style.minHeight = parameters.height * scale+"px";
        }

        var viewContainer = document.createElement("div");
        viewContainer.id = "view-container-"+appletObjectName;
        viewContainer.setAttribute("width", parameters.width);
        viewContainer.setAttribute("height", parameters.height);
        viewContainer.style.width = parameters.width*scale+"px";
        viewContainer.style.height = parameters.height*scale+"px";
//        viewContainer.style.border = "1px solid black";

        if (parameters.showSplash === undefined) {
            parameters.showSplash = true;
        }


        var viewImages = document.createElement("div");
        viewImages.id = '__ggb__images';

        // Add the tag for the preview image
        if (!noPreview && previewImagePath !== null && parseFloat(html5CodebaseVersion)>=4.4 && parameters.width !== undefined) {
            var previewContainer = createScreenShotDiv(parameters.width, parameters.height, parameters.borderColor);

            // This div is needed to have an element with position relative as origin for the absolute positioned image
            var previewPositioner = document.createElement("div");
            previewPositioner.style.position = "relative";
            previewPositioner.className = "ggb_preview_container";
            previewPositioner.style.display = 'block';
            previewPositioner.style.width = parameters.width*scale+'px';
            previewPositioner.style.height = parameters.height*scale+'px';
            previewPositioner.appendChild(previewContainer);
            appletElem.appendChild(previewPositioner);
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
//            appletElem.appendChild(resource1);
//            appletElem.appendChild(resource2);
//            appletElem.appendChild(resource3);
            appletElem.appendChild(resource4);
            appletElem.appendChild(resource5);
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

        appletElem.appendChild(appletStyle);

        var script = document.createElement("script");

        var scriptLoaded = function() {
            window[appletObjectName].preCompiledScriptPath = preCompiledScriptPath;
            window[appletObjectName].scaleParameter = parameters.scale;

            if (!noPreview) {
                appletElem.querySelector(".ggb_preview_container").remove();
            }
            appletElem.appendChild(viewContainer);
            appletElem.appendChild(viewImages);

            if (ggbCompiledResourcesLoadFinished) {
                window[appletObjectName].init({scale:parameters.scale, url:preCompiledScriptPath+'/', ss:parameters.showSplash, sdz:parameters.enableShiftDragZoom, rc:parameters.enableRightClick, sri:parameters.showResetIcon});
                if (typeof window.ggbAppletOnLoad === 'function') {
                    window.ggbAppletOnLoad(appletElem.id);
                }
            }
        };

        var scriptFile = preCompiledScriptPath + "/applet.js" + (preCompiledScriptVersion != null && preCompiledScriptVersion != undefined ? "?v="+preCompiledScriptVersion : "");
        script.src=scriptFile;
        script.onload = scriptLoaded;

        log("GeoGebra precompiled applet injected. Script="+scriptFile+".");
        appletElem.appendChild(script);
    };

    var injectScreenshot = function(appletElem, parameters) {

        // Add the tag for the preview image
        if (previewImagePath !== null && parseFloat(html5CodebaseVersion)>=4.4 && parameters.width !== undefined) {
            var previewContainer = createScreenShotDiv(parameters.width, parameters.height, parameters.borderColor);

            // This div is needed to have an element with position relative as origin for the absolute positioned image
            var previewPositioner = document.createElement("div");
            previewPositioner.style.position = "relative";
            previewPositioner.style.display = 'block';
            previewPositioner.style.width = parameters.width+'px';
            previewPositioner.style.height = parameters.height+'px';
            previewPositioner.className = "applet_screenshot";
            previewPositioner.appendChild(previewContainer);

            //var scale = 1;
            //if (parameters.hasOwnProperty('scale')) {
            //    scale = parameters.scale;
            //} else {
            //    scale = getScale(parameters.screenshotGenerator, appletElem);
            //}
            //if(!isNaN(scale) && scale < 1 && scale > 0){
            //    previewPositioner.style.transform = "scale(" + scale + "," + scale + ")";
            //    previewPositioner.style.transformOrigin = "0% 0% 0px";
            //}

            appletElem.appendChild(previewPositioner);
        }
    };

    var createScreenShotDiv = function(oriWidth, oriHeight, borderColor) {
        var previewContainer = document.createElement("div");
        previewContainer.className = "ggb_preview";
        previewContainer.style.position = "absolute";
        previewContainer.style.zIndex = "1000001";
        previewContainer.style.width = oriWidth-2+'px'; // Remove 2 pixel for the border
        previewContainer.style.height = oriHeight-2+'px'; // Remove 2 pixel for the border
        previewContainer.style.top = "0px";
        previewContainer.style.left = "0px";
        previewContainer.style.overflow = "hidden";
        previewContainer.style.backgroundColor = "white";
        var bc = 'black';
        if (borderColor !== undefined) {
            if (borderColor === "none") {
                bc = "transparent";
            }
        }
        previewContainer.style.border = "1px solid " + bc;

        var preview = document.createElement("img");
        preview.style.position = "relative";
        preview.style.zIndex = "1000";
        preview.style.top = "-1px"; // Move up/left to hide the border on the image
        preview.style.left = "-1px";
        preview.setAttribute("src", previewImagePath);
        preview.style.opacity = 0.3;

        if (previewLoadingPath !== null) {
            var previewLoading = document.createElement("img");
            previewLoading.style.position = "absolute";
            previewLoading.style.zIndex = "1001";
            previewLoading.style.opacity = 1.0;
            previewLoading.setAttribute("src", previewLoadingPath);
            var pWidth = 360;
            if (pWidth > (oriWidth/4*3)) {
                pWidth = oriWidth/4*3;
            }
            var pHeight = pWidth/5.8;
            var pX = (oriWidth - pWidth) / 2;
            var pY = (oriHeight - pHeight) / 2;
            previewLoading.style.left = pX + "px";
            previewLoading.style.top = pY + "px";
            previewLoading.setAttribute("width", pWidth-4);
            previewLoading.setAttribute("height", pHeight-4);
            previewContainer.appendChild(previewLoading);
        }

        previewContainer.appendChild(preview);
        return previewContainer;
    };


    var buildJNLPFileName = function(isOffline) {
        var version = parseFloat(javaCodebaseVersion);
        var filename = "applet" + version*10 + "_";
        if (isOffline) {
            filename += "local";
        } else {
            filename += "web";
        }
        if (views.is3D) {
            filename += "_3D";
        }
        filename += ".jnlp";
        return filename;
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
        if ((preferredType === "java") || (preferredType === "html5") || (preferredType === "screenshot") || (preferredType === "compiled")) {
            return preferredType;
        }

        if (preferredType === "preferjava") {
            if (applet.isJavaInstalled()) {
                return "java";
            } else {
                return "html5";
            }
        } else if (preferredType === "preferhtml5") {
            if (applet.isHTML5Installed()) {
                return "html5";
            } else {
                return "java";
            }
        } else if ((preferredType === "prefercompiled") && (preCompiledScriptPath !== null)) {
            if (applet.isCompiledInstalled()) {
                return "compiled";
            } else {
                return "java";
            }
        } else {
            // type=auto
            if ((applet.isJavaInstalled()) &&
                (!applet.isHTML5Installed())) {
                return "java";
            } else {
                return "html5";
            }
        }
    };

    var getIEVersion = function() {
        var a=navigator.appVersion;
        if (a.indexOf("Trident/7.0") > 0)
            return 11;
        else {
            return a.indexOf('MSIE')+1?parseFloat(a.split('MSIE')[1]):999;
        }
    };

    var isInternetExplorer = function() {
        return (getIEVersion() != 999);
    }


    function isFlexibleWorksheet() {
        return (window.GGBT_wsf_view !== undefined || window.GGBT_wsf_edit !== undefined);
    }

    function isFlexibleWorksheetEditor() {
        return (window.GGBT_wsf_edit !== undefined);
    }

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
            var v = parseFloat(html5CodebaseVersion);
            if ((isNaN(v) || v < 4.4)) {
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
        } else if(index === 0){
            codebase = protocol + html5CodebaseVersion;
        } else {
            codebase = protocol+"//web.geogebra.org/" + html5CodebaseVersion + "/";
        }
        var modules = ["web", "webSimple", "web3d", "tablet", "phone"];
        for(var key in modules){
            if (html5CodebaseVersion.slice(modules[key].length*-1) === modules[key] ||
                html5CodebaseVersion.slice((modules[key].length+1)*-1) === modules[key]+"/") {
                setHTML5CodebaseInternal(codebase, false);
                return;
            }
        }

        // Decide if web, websimple or web3d should be used
        if (!isFlexibleWorksheet() && hasWebSimple && !views.is3D && !views.AV && !views.SV && !views.CV && !views.EV2 && !views.CP && !views.PC && !views.DA && !views.FI && !views.PV &&
            !valBoolean(parameters.showToolBar) && !valBoolean(parameters.showMenuBar) && !valBoolean(parameters.showAlgebraInput) && !valBoolean(parameters.enableRightClick)) {
            codebase += 'webSimple/';
        } else {
            var v = parseFloat(html5CodebaseVersion);
            if (views.is3D && (isNaN(v) || v >= 5.0)) {
                codebase += 'web3d/';
            } else {
                codebase += 'web/';
            }
        }

        setHTML5CodebaseInternal(codebase, false);
    };

    var setHTML5CodebaseInternal = function(codebase, offline) {
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
            } else if (folders[folders.length-2] === 'web3d' || folders[folders.length-2] === 'tablet' || folders[folders.length-2] === 'phone') {
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
    };

    var setDefaultJavaCodebaseForVersion = function(version) {

        // There are no test versions for java. So when test is passed, it will be converted to the normal codebase
        if (version === "test32") {
            javaCodebaseVersion = "3.2";
        } else if (version === "test40") {
            javaCodebaseVersion = "4.0";
        } else if (version === "test42") {
            javaCodebaseVersion = "4.2";
        } else if (version === "test50") {
            javaCodebaseVersion = "5.0";
        } else if (version === "test") {
            javaCodebaseVersion = ggbVersion;
        } else {
            javaCodebaseVersion = version;
        }

        // For versions below 4.0 the java codebase of version 4.0 is used.
        if (parseFloat(javaCodebaseVersion)<4.0) {
            javaCodebaseVersion = "4.0";
        }

        var protocol;
        if (window.location.protocol.substr(0,4) === 'http') {
            protocol = window.location.protocol;
        } else {
            protocol = 'http:';
        }
        var codebase = protocol+"//jars.geogebra.org/webstart/" + javaCodebaseVersion + '/';
        if (javaCodebaseVersion === '4.0' || javaCodebaseVersion === '4.2') {
            codebase += 'jnlp/';
        }

        applet.setJNLPBaseDir('http://tube.geogebra.org/webstart/');

        doSetJavaCodebase(codebase, false);
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
        fetchParametersFromTube(continueInit);
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
        setDefaultJavaCodebaseForVersion(ggbVersion); // For java we always use the version of the file per default.

        if (html5OverwrittenCodebase !== null) {
            setHTML5CodebaseInternal(html5OverwrittenCodebase, isHTML5Offline);
        }
        initComplete = true;
    }

    return applet;
};