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
//            applet.setPreviewImage(previewImagePath || item.previewUrl,
//                imageDir + 'GeoGebra_loading.png', imageDir + 'applet_play.png');

//            successCallback();
			resolve(options);
        };
        var onError = function() {
            options.onError && options.onError();
            log('Error: Fetching material (id ' + options.material_id + ') failed.', parameters);
        };

        var host = location.host.match(/(www|stage|beta|groot|alpha).geogebra.(org|net)/) ? location.host
        	: 'www.geogebra.org';
        var path = '/materials/' + options.material_id + '?scope=basic';
        sendCorsRequest(
            'https://' + host + '/api/proxy.php?path=' + encodeURIComponent(path),
            onSuccess,
            onError
        );
    };

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

    fetchParametersFromApi();
