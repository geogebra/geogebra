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