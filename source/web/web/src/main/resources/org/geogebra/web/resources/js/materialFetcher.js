    var previewImagePath = null;
    var previewLoadingPath = null;
    var previewPlayPath = null;

    var setPreviewImage = function(previewFilePath, loadingFilePath, playFilePath) {
                previewImagePath = previewFilePath;
                previewLoadingPath = loadingFilePath;
                previewPlayPath = playFilePath;
    };

    var createScreenShotDiv = function(oriWidth, oriHeight, borderColor, showPlayButton) {
        var previewContainer = document.createElement("div");
        previewContainer.className = "ggb_preview";
        previewContainer.style.position = "absolute";
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

    var fetchParametersFromApi = function() {
        var onSuccess = function(text) {
            var jsonData= JSON.parse(text);
            // handle either worksheet or single element format
            var isGeoGebra = function(element) {return element.type == 'G' || element.type == 'E'};
            var item = jsonData.elements ? jsonData.elements.filter(isGeoGebra)[0] : jsonData;
            if (!item || !item.url) {
                onError();
                return;
            }

            options.filename = item.url;
            updateAppletSettings(item.settings || {});
            // user setting of preview URL has precedence
            var imageDir = 'https://www.geogebra.org/images/';
            setPreviewImage(previewImagePath || item.previewUrl,
            imageDir + 'GeoGebra_loading.png', imageDir + 'applet_play.png');
            buildPreview();
            resolve(options);
        };
        var onError = function() {
            options.onError && options.onError();
            log('Error: Fetching material (id ' + options.material_id + ') failed.', parameters);
        };
    
        sendCorsRequest(
            'https://api.geogebra.org/v1.0/materials/'  + options.material_id + '?scope=basic',
            onSuccess,
            onError
        );
    };

    function buildPreview() {
        var oriWidth=options.width;
        var oriHeight=options.height;
        var previewContainer = createScreenShotDiv(oriWidth, oriHeight, options.borderColor, false);
        // This div is needed to have an element with position relative as origin for the absolute positioned image
        var previewPositioner = document.createElement("div");
        previewPositioner.className = "applet_scaler";
        previewPositioner.style.position = "relative";
        previewPositioner.style.display = 'block';
        previewPositioner.style.width = oriWidth+'px';
        previewPositioner.style.height = oriHeight+'px';
        previewPositioner.appendChild(previewContainer);
        var parentElement = options.element.parentElement;
        previewPositioner.appendChild(options.element);
        parentElement.appendChild(previewPositioner);
        options.removePreview = function() {
            var preview = document.querySelector(".ggb_preview");
            if (preview) {
                preview.parentNode.removeChild(preview);
            }
        }
        GGBAppletUtils.responsiveResize(parentElement, options);
    }

    function updateAppletSettings(settings) {
        var optionNames = ['width', 'height', 'showToolBar', 'showMenuBar',
            'showAlgebraInput', 'allowStyleBar', 'showResetIcon', 'enableLabelDrags',
            'enableShiftDragZoom', 'enableRightClick', 'appName'];
        // different defaults in API and web3d
        ['enableLabelDrags', 'enableShiftDragZoom', 'enableRightClick'].forEach(function(name) {
            settings[name] = !!settings[name];
        });
        optionNames.forEach(function(name) {
             if (options[name] === undefined && settings[name] !== undefined) {
                options[name] = settings[name];
             }
        });
        if (options.showToolBarHelp === undefined) {
            options.showToolBarHelp = options.showToolBar;
        }
    }

    // Create the XHR object.
    function sendCorsRequest(url, onSuccess, onError) {
        var xhr = new XMLHttpRequest();
        xhr.open('GET', url);
        // Response handlers.
        xhr.onload = function() {
            onSuccess(xhr.responseText);
        }
        xhr.onerror = onError;
        xhr.send();
    }

    if (options.material_id) {
        fetchParametersFromApi();
    } else {
        resolve(options);
    }

    var GGBAppletUtils = (function() {
        "use strict";

        function scaleElement(el, scale) {
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

        function getWidthHeight(appletElem, appletWidth, allowUpscale, autoHeight, noBorder, scaleContainerClass) {
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
                myHeight = Math.max(autoHeight ? container.offsetWidth : 0, container.offsetHeight);
            } else {
                if (window.innerWidth && document.documentElement.clientWidth) {
                    myWidth = Math.min(window.innerWidth, document.documentElement.clientWidth);
                    myHeight = Math.min(window.innerHeight, document.documentElement.clientHeight);
                    // Using mywith instead of innerWidth because after rotating a mobile device the innerWidth is sometimes wrong (e.g. on Galaxy Note III)
                    // windowWidth = window.innerWidth
                    windowWidth = myWidth;
                } else {
                    //Non-IE
                    myWidth = window.innerWidth;
                    myHeight = window.innerHeight;
                    windowWidth = window.innerWidth;
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
            }

            if (appletElem) {
                if ((allowUpscale === undefined || !allowUpscale) && appletWidth > 0 && appletWidth + border < myWidth) {
                    myWidth = appletWidth;
                } else {
                    myWidth -= border;
                }
            }

            return {width: myWidth, height: myHeight};
        }

        function calcScale(parameters, appletElem, allowUpscale, showPlayButton, scaleContainerClass){
            if (parameters.isScreenshoGenerator) {
                return 1;
            }
            var ignoreHeight = (showPlayButton !== undefined && showPlayButton);
            var noScaleMargin = parameters.noScaleMargin != undefined && parameters.noScaleMargin;
            var valBoolean = function(value) {
                return (value && value !== "false");
            };
            var autoHeight = valBoolean(parameters.autoHeight);
            var windowSize = getWidthHeight(appletElem, parameters.width, allowUpscale, autoHeight, noScaleMargin, scaleContainerClass);
            var windowWidth = parseInt(windowSize.width);

            var appletWidth = parameters.width;
            var appletHeight = parameters.height;
            if (appletWidth === undefined) {
                var article = appletElem.querySelector('.appletParameters');
                if (article) {
                    appletWidth = article.offsetWidth;
                    appletHeight = article.offsetHeight;
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

        function responsiveResize(appletElem, parameters) {
            var article = appletElem.querySelector(".ggb_preview");

            if (article) {

                if(article.parentElement && (/fullscreen/).test(article.parentElement.className)){
                    return; //fullscreen button inside applet pressed
                }

                var scale = getScale(parameters, appletElem);

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
            }
        }

        return {
            responsiveResize: responsiveResize,
            getScale: getScale,
            scaleElement: scaleElement
        };
    })();