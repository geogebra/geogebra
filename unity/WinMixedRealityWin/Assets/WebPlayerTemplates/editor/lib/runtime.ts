/**
 *  @module lib/runtime
 *  @requires module:when
 *  @requires module:lib/function_helpers
 *  @requires module:scripts/globals/globals
 *  @requires module:lib/editor_settings
 *  @requires module:lib/enums
 */

'use strict';
declare let $;

let when = require('when');

import Enums from './enums';
import couiEditor from '../scripts/main';
import helpers from './function_helpers';
import enums from './enums';
import globals from '../scripts/globals/globals';
import editorSettings from './editor_settings';

export namespace runtimeCore {

    var factories = {};
    const nonDisplayInheritEls = enums.nonDisplayInheritElements;

    var isInEditor = function () {
        return window.globalEditorInfo !== undefined;
    };

    window.engine = window.engine || undefined;

    var doesEngineExist = function () {
        return window.engine && window.engine.on !== undefined;
    };

    var styleTag = document.createElement('style');
    styleTag.id = 'style-runtime';

    if (isInEditor()) {
        vex.defaultOptions.className = 'vex-theme-flat-attack';
    }

    var loadFiles = function (files) {

        if (files === undefined) {
            return when();
        }
        var n = files.length,
            promises = [];

        var catchErrorHandler = function (e) {
            console.error.bind(console);
            if (isInEditor()) {
                vex.dialog.alert(e);
            }
        };

        for (var i = 0; i < n; ++i) {
            promises.push(System.import('runtime_editor/' + files[i])
                .catch(catchErrorHandler));
        }
        return when.all(promises);
    };

    var buildEventsObj = function (events) {
        var eventsObj = {};

        for (var i = 0; i < events.length; i++) {
            for (var p in events[i]) {
                eventsObj[p] = events[i][p];
            }
        }
        return eventsObj;
    };

    var loadSceneObj = function (scene, target, callback?) {

        return loadFiles(scene.deps).then(function (data) {

            var len = scene.styles.length;

            if (len !== 0) {
                for (var i = 0; i < len; i++) {
                    var link: any = document.createElement('link');
                    link.rel = 'stylesheet';
                    link.property = 'stylesheet';
                    link.type = 'text/css';
                    link.className = 'styles';
                    link.href = scene.styles[i];

                    target.appendChild(link);
                }
            }

            return create(scene, target, true, callback);

        }).then(function () {

            var len = scene.scripts.length;
            var script;

            if (len !== 0) {

                for (var i = 0; i < len; i++) {
                    script = document.createElement('script');
                    script.className = 'scripts';
                    script.src = scene.scripts[i];

                    target.appendChild(script);
                }
            }
        }).then(function () {
            return attachEvents(scene.widgets, target, scene, callback);
        }).then(function () {
            return loadAnimations(scene.animationClasses, target,
                callback);
        }).then(function (animationsCss) {
            $(target).find('*').css('cursor', '');
            return buildHTMLPage(target, animationsCss, scene, callback);
        });
    };

    var loadAnimations = function (animObj, target, callback) {
        var animationClasses = Object.keys(animObj);
        for (var i = 0; i < animationClasses.length; i++) {
            if ($.isEmptyObject(animObj[animationClasses[i]].keyframes)) {
                delete animObj[animationClasses[i]];
            }
        }
        return callback.runtimeEditor.Animations.exportToCss(animObj);
    };

    var writeEvents = function (widget, text) {
        var temp;
        var domElement;

        // call the function again untill there are no more children
        if (widget.children.length) {
            for (var child in widget.children) {
                if (widget.children.hasOwnProperty(child)) {
                    text = writeEvents(widget.children[child], text);
                }
            }
        }

        // loop the events and set them
        if (widget.events) {
            temp = widget.events;

            domElement = 'document.getElementById("' + widget.id + '")';

            for (var prop in temp) {
                var obj = temp[prop];

                for (var innerProp in obj) {
                    switch (innerProp) {
                        case 'javascriptFunction':
                            text += domElement + '.addEventListener("' + prop + '", function() { ' +
                                obj[innerProp] + ' });\n';
                            break;
                        case 'engineCallArguments':
                            text += domElement + '.addEventListener("' + prop +
                                '", function() { engine.call.apply(engine, [' +
                                eventsArrayStringConcatenation(obj[innerProp]) +
                                ']); });\n';
                            break;
                        case 'engineTriggerArguments':
                            text += domElement + '.addEventListener("' + prop +
                                '", function() { engine.trigger.apply(engine, [' +
                                eventsArrayStringConcatenation(obj[innerProp]) + ']); });\n';
                            break;
                        case 'blueprintFunction':
                            text += domElement + '.addEventListener("' + prop +
                                '", function() { engine.trigger(\'' + obj[innerProp] + '\'); });\n';
                            break;
                    }
                }
            }
        }

        return text;
    };

    function eventsArrayStringConcatenation(props) {
        var text = '';
        for (var i = 0; i < props.length; i++) {
            text += '\'' + props[i] + '\',';
        }

        return text.substring(0, text.length - 1);
    }

    var attachEvents = function (el, target, scene, callback) {
        var scriptTag = document.createElement('script');
        var globalScriptTag = document.createElement('script');
        var globalSceneEvents = scene.sceneEvents.sceneLoad;
        var innerText = '';

        scriptTag.className = 'local-events';
        globalScriptTag.className = 'global-events';

        globalScriptTag.textContent = globalSceneEvents;

        for (var key in el) {
            innerText = writeEvents(el[key], innerText);
        }

        scriptTag.textContent = '(function(){\n' + innerText + '\n})();\n';

        target.appendChild(globalScriptTag);
        target.appendChild(scriptTag);
    };

    var buildHTMLPage = function (html, animationsCss, scene, callback?) {

        let extraCss: string = '';
        let bodyHTML: string = '';
        let fontFaces: string = '';
        let originalFileName: string = '';
        let editor: any = couiEditor.openFiles[couiEditor.selectedEditor];

        const currentEnvironment = couiEditor.preferences.couiEnvironment;
        let currentEnvironmentProp = couiEditor.environmentProperties;

        if (scene.fonts.length !== 0) {
            scene.fonts.map((font) => {
                fontFaces += helpers.buildFontCss(font);
            });
        }

        if (callback.options && callback.options.publishScene) {
            let inlineToCssClasses: IPublishPage = helpers.buildPublishPage(html, currentEnvironment);
            originalFileName = `\n${currentEnvironmentProp.ORIGINAL_SOURCE_SCENE_PATH}\n
<!-- ${callback.options.originalSceneName} -->
\n${currentEnvironmentProp.ORIGINAL_SOURCE_SCENE_PATH_END}`;

            extraCss = fontFaces + '\n' + inlineToCssClasses.css;
            bodyHTML = inlineToCssClasses.cleanedHTML;

            // overwrite fontFaces to prevent it being applied in the head tag
            fontFaces = '';

        } else {
            fontFaces = `<style id="coui_font_faces">\n${fontFaces}</style>`;
            bodyHTML = html.innerHTML;
        }

        var sceneType = 'scene';

        if (editor.runtimeEditor) {
            var isEditWidget = editor.tab.tabWidgetState.editWidget;
            var isNewWidget = editor.tab.tabWidgetState.createNewWidget;

            if ((isEditWidget || isNewWidget) &&
                (scene.widgets[0] && scene.widgets[0].widgetkit)
                && !scene.widgets[0].widgetkit.endsWith('.component')) {
                sceneType = scene.widgets[0].widgetkit;
            }
            editor.tab.tabWidgetState.createNewWidget = false;
        }

        var aspectRatio = '\'{' +
            '"width": "' + scene.sceneSize.width + '", ' +
            '"height": "' + scene.sceneSize.height + '", ' +
            '"type": "' + scene.sceneSize.type + '"' +
            '}\'';

        var sceneProperties = {
            style: scene.style,
            sceneType: sceneType
        };

        bodyHTML = beautifyHtml(bodyHTML);

        var sceneStyles = 'body {' +
            'background-color:' + scene.style.backgroundColor + ';' +
            '}';

        var editorStyleProperties = '';
        if (editorSettings.environment.GT === couiEditor.preferences.couiEnvironment) {
            editorStyleProperties = ' * {' + '\n' +
                '\tmargin: 0;' + '\n' +
                '\tpadding: 0;' + '\n' +
                '\tbox-sizing: border-box;\n' +
                '\tfont-family:"Helvetica Neue",Helvetica, Arial,sans-serif;\n' +
                '}' + '\n';
        } else if (editorSettings.environment.Hummingbird === couiEditor.preferences.couiEnvironment) {
            editorStyleProperties = ' * {' + '\n' +
                '\tbox-sizing: border-box;\n' +
                '\tfont-family:"Helvetica Neue",Helvetica, Arial,sans-serif;\n' +
                '}' + '\n';
        }

        var hummingbirdStyleProperties = '';
        if (editorSettings.environment.Hummingbird === couiEditor.preferences.couiEnvironment) {
            hummingbirdStyleProperties = 'body {\n' +
                '\twidth: 100vw;\n' +
                '\theight: 100vh;\n' +
                '}\n';

            var regex = /(?:flex-flow:|-webkit-flex-flow:)(.*?)(?:;)/gm;

            bodyHTML = bodyHTML.split(regex).map(function (result) {
                if (result.indexOf('>') === -1 && result.indexOf('<') === -1) {
                    var newResult = result.trim().split(' ');
                    var flexDirection = 'flex-direction: ' + newResult[0] + ';';
                    var flewWrap = 'flex-wrap: ' + newResult[1] + ';';
                    return flexDirection + ' ' + flewWrap;
                    //return newResult;
                } else if (result.indexOf('-webkit-') !== -1) {
                    return result.replace(/-webkit-/gm, '');
                } else {
                    return result;
                }
            }).join('');

            bodyHTML = bodyHTML.replace(/.mask:.*?;/gm, (regSelection) => {
                let newString: string = ' mask-image: ';

                newString += regSelection.match(/url\(.*?\)/)[0] + '; mask-position: ';
                newString += regSelection.match(/\).*?\//)[0].replace(') ', '').replace(' /', '') + '; mask-repeat: ';
                newString += regSelection.match(/(repeat|no-repeat).*?;/)[0];

                return newString;
            });
        } else if (editorSettings.environment.GT === couiEditor.preferences.couiEnvironment) {
            bodyHTML = bodyHTML.replace(/-webkit-mask:.*?;/gm, (regSelection) => {
                return regSelection.replace('/ inherit ', '');
            });
        }

        const metadata =
            '<script>' + '\n' +
            currentEnvironmentProp.EDITOR_VERSION_MARK_START + '\n' +
            'var version = "' + couiEditor.EDITOR_VERSION.join('.') + '"; \n' +
            currentEnvironmentProp.EDITOR_VERSION_MARK_END + '\n' +
            currentEnvironmentProp.EDITOR_ENV_MARK_START + '\n' +
            'var environment = "' + couiEditor.preferences.couiEnvironment + '"; \n' +
            currentEnvironmentProp.EDITOR_ENV_MARK_END + '\n' +
            currentEnvironmentProp.ASPECT_RATIO_MARK_START + '\n' +
            'var sceneAspectRatio = ' + aspectRatio + '\n' +
            currentEnvironmentProp.ASPECT_RATIO_MARK_END + '\n' +

            globals.marker.scenePropertiesStart +
            '\n var sceneProperties = ' + JSON.stringify(sceneProperties) + '\n' +
            globals.marker.scenePropertiesEnd +
            '\n</script>\n';

        var coherentHTML =
            originalFileName +
            '\n' + currentEnvironmentProp.COMMENT_MARK_START + '\n' +
            animationsCss +
            '\n' +
            (callback.options && callback.options.publishScene ? '' : metadata) +
            // TODO is this necessary?
            //'<script src="runtime/modules.js"></script>\n' +
            fontFaces +
            '<style>' + '\n' +
            editorStyleProperties +
            hummingbirdStyleProperties +
            sceneStyles + '\n' +
            extraCss +
            '</style>' + '\n' +
            '</head>' + '\n' +
            '<body>' + '\n' +
            bodyHTML + '\n' +
            currentEnvironmentProp.COMMENT_MARK_END + '\n';

        return coherentHTML;
    };

    var load = function (scene, target) {
        return when(System.import('runtime_editor/' + scene + '!text')).then(function (scene) {
            scene = JSON.parse(scene);

            return loadSceneObj(scene, target);
        });
    };

    var factory = function (widget, result, callback) {
        var ctor = factories[widget.type];
        if (ctor === undefined) {
            ctor = factories[widget.type] = function () {
                var x = document.createTextNode(JSON.stringify(widget));
                result.appendChild(x);
            };
        }
        return ctor(widget, result, callback);
    };

    var setAttributes = function (element, attrs) {
        for (var prop in attrs) {
            if (attrs[prop] !== '') {
                element.setAttribute(prop, attrs[prop]);
            }
        }
    };

    var setStyles = function (element, styles, type) {
        for (var prop in styles) {
            if (canApplyProperty(prop, styles[prop], type)) {
                if (prop === 'backgroundSize' && styles[prop] === 'auto') {
                    var backgroundSizeWidth = styles.backgroundSizeWidth || 'auto';
                    var backgroundSizeHeight = styles.backgroundSizeHeight || 'auto';
                    element.style[prop] = backgroundSizeWidth +
                        ' ' + backgroundSizeHeight;
                } else if (prop === '-webkit-mask-size' &&
                    styles[prop] === 'auto') {
                    var webkitMaskSizeWidth = styles['-webkit-mask-sizeWidth'] || 'auto';
                    var webkitMaskSizeHeight = styles['-webkit-mask-sizeHeight'] || 'auto';
                    element.style[prop] = webkitMaskSizeWidth +
                        ' ' + webkitMaskSizeHeight;
                } else {
                    element.style[prop] = styles[prop];
                }
            }
        }
    };

    const canApplyProperty = function (prop, value, type) {
        const deniedProperties = ['textTransform', 'textDecoration',
            'backgroundSizeWidth, backgroundSizeHeight',
            '-webkit-mask-sizeWidth', '-webkit-mask-sizeHeight'];

        if (deniedProperties.indexOf(prop) !== -1 && value === 'none') {
            return false;
        }

        if (prop === 'display' && value === 'inherit' && !nonDisplayInheritEls.includes(type)) {
            return false;
        }

        return true;
    };

    var setLongStyles = function (element, styles, prop) {
        var styleString = '';
        for (var a in styles) {
            var value = styles[a];

            // convert rad to deg
            if (a === 'rotate') {
                value = helpers.toDegrees(parseFloat(value)) + 'deg';
            }

            if (a !== 'dropShadowColor' && a !== 'dropShadowX' &&
                a !== 'dropShadowY' && a !== 'dropShadowBlur') {
                styleString += a + '(' + value + ') ';
            }
        }

        styleString += helpers.buildDropShadowProperty(styles);
        element.style[prop] = styleString;
    };

    var setEvents = function (element, events) {
        if (!doesEngineExist()) {

            System.config({
                paths: {
                    coherent: 'lib/coherent.js'
                }
            });

            System.import('runtime_editor/coherent').then(function (engine) {
                window.engine = engine;

                addEventsOnWidget(element, events);
            });
        } else {
            addEventsOnWidget(element, events);
        }
    };

    var addEventsOnWidget = function (element, events) {
        var eventType;
        var eventName;

        function attachHandler(eventType) {
            if (typeof eventType === 'object') {

                if (eventType.engineCallArguments) {
                    element.addEventListener(eventName, function () {
                        engine.call.apply(engine,
                            eventType.engineCallArguments);
                    }, false);
                } else if (eventType.engineTriggerArguments) {
                    element.addEventListener(eventName, function () {
                        engine.trigger
                            .apply(engine, eventType.engineTriggerArguments);
                    }, false);
                } else if (eventType.blueprintFunction) {

                    element.addEventListener(eventName, function () {
                        engine.trigger(eventType.blueprintFunction);
                    }, false);
                } else if (eventType.javascriptFunction) {
                    /*jshint -W054 */
                    element.addEventListener(eventName,
                        new Function(eventType.javascriptFunction), false);
                    /*jshint +W054 */
                }
            } else {
                element.addEventListener(eventName, window.editorEvents[eventType], false);
            }
        }

        for (eventName in events) {
            eventType = events[eventName];
            attachHandler(eventType);
        }
    };

    var addChildren = function (element, children, callback) {
        var n = children.length;
        for (var i = 0; i < n; ++i) {
            factory(children[i], element, callback);
        }
    };

    var simple = function (widget, result, callback) {
        var element;

        if (widget.type === 'ellipse' || widget.type === 'rectangle' ||
            widget.type === 'roundedRect' || widget.type === 'circle' ||
            widget.type === 'responsiveImage' || widget.type === 'widget') {
            element = document.createElement('div');
        } else {
            element = document.createElement(widget.type);
        }

        setupProperties(widget, element, result, callback);
    };

    var inputFactory = function (widget, result, callback) {
        var element = document.createElement('input');

        if (widget.type === 'inputText') {
            element.setAttribute('type', 'text');
        } else {
            element.setAttribute('type', widget.type);
        }

        if (widget.type === 'range') {
            $(element).val(0).trigger('change');
        }

        setupProperties(widget, element, result, callback);
    };

    var textFactory = function (widget, result, callback) {
        var element = document.createElement('div');
        element.textContent = widget.text;

        setupProperties(widget, element, result, callback);
    };

    var imageFactory = function (widget, result, callback) {
        var image = new Image();
        image.src = widget.url;

        if (widget.width !== undefined) {
            image.width = widget.width;
        }

        if (widget.height !== undefined) {
            image.height = widget.height;
        }

        setupProperties(widget, image, result, callback);
    };

    var videoFactory = function (widget, result, callback) {
        var video = document.createElement('video');
        video.src = widget.url;

        setupProperties(widget, video, result, callback);
    };

    factories = {
        widget: simple,
        div: simple,
        responsiveImage: simple,
        image: imageFactory,
        liveview: imageFactory,
        video: videoFactory,
        text: textFactory,
        label: simple,
        button: simple,
        inputText: inputFactory,
        select: simple,
        range: inputFactory,
        number: inputFactory,
        option: simple,
        checkbox: inputFactory,
        radio: inputFactory,
        textarea: simple,
        ul: simple,
        ol: simple,
        li: simple,
        rectangle: simple,
        ellipse: simple,
        circle: simple,
        roundedRect: simple
    };

    var lockNewWidgetChildNodes = function (widgetChildNode, widget) {
        for (var i = 0; i < widgetChildNode.length; i++) {
            if (widgetChildNode[i].attributes) {
                if (widgetChildNode[i].hasAttribute('data-element-selectable')) {
                    widgetChildNode[i].removeAttribute('data-element-selectable');
                }
                widgetChildNode[i].setAttribute('data-parent-widget-id', widget.id);
            }

            if (widgetChildNode[i].childNodes.length > 0) {
                lockNewWidgetChildNodes(widgetChildNode[i].childNodes, widget);
            }
        }
    };

    var widgetsChildLocking = function (result) {
        var widget = result.querySelectorAll('[data-type="widget"]');
        var widgetLength = widget.length;
        if (widgetLength > 0) {
            for (var i = 0; i < widgetLength; i++) {
                var widgetChildNode = widget[i].childNodes;
                let widgetState = couiEditor.openFiles[couiEditor.selectedEditor].tab.tabWidgetState;
                if (widgetState.importedWidget && !widgetState.editWidget) {
                    lockNewWidgetChildNodes(widgetChildNode, widget[i]);
                }
            }
        }
    };

    function beautifyHtml(str) {

        var div = document.createElement('div');
        div.innerHTML = str.trim();

        return formatHtml(div, 0).innerHTML;
    }

    function formatHtml(node, level) {

        var indentBefore = new Array(level++ + 1).join('  '),
            indentAfter = new Array(level - 1).join('  '),
            textNode;

        for (var i = 0; i < node.children.length; i++) {

            textNode = document.createTextNode('\n' + indentBefore);
            node.insertBefore(textNode, node.children[i]);

            formatHtml(node.children[i], level);

            if (node.lastElementChild === node.children[i]) {
                textNode = document.createTextNode('\n' + indentAfter);
                node.appendChild(textNode);
            }
        }

        return node;
    }

    /**
     * @function
     * @memberOf module:lib/runtime
     * @param widget
     * @param element
     * @param result
     * @param callbacksObj
     */
    export function setupProperties(widget, element, result, callbacksObj) {
        const runtimeEditor = couiEditor.openFiles[couiEditor.selectedEditor].runtimeEditor;

        // if build of widget is from paste and is nested widget generate new id
        if (runtimeEditor && runtimeEditor._sceneActionState.pasteWidget &&
            couiEditor.copiedWidgets.widgets[widget.id] &&
            couiEditor.copiedWidgets.widgets[widget.id].nestedElementId) {
            var oldId = widget.id;
            var newId = couiEditor.generateRandomId(widget.type);
            widget.id = newId;
            element.id = newId;
            runtimeEditor.cloneAnimations(newId, oldId);
        } else if (widget.id !== undefined) {
            element.id = widget.id;
        }

        if (widget.className !== undefined) {
            if (element.className !== '') {
                var classes = widget.className.split(' ');
                var len = classes.length;
                for (var i = 0; i < len; i++) {
                    element.classList.add(classes[i]);
                }
            } else {
                element.className = widget.className;
            }
        }

        if (widget.background !== undefined) {
            element.style.background = widget.background;
            element.style.backgroundRepeat = 'repeat';
        }

        if (widget.styles.mixBlendMode && widget.styles.mixBlendMode === 'normal') {
            element.style.mixBlendMode = widget.styles.mixBlendMode;
        }

        if (widget.styles.backgroundBlendMode && widget.styles.backgroundBlendMode === 'normal') {
            element.style.backgroundBlendMode = widget.styles.backgroundBlendMode;
        }

        if (widget.selected) {
            element.setAttribute('selected', 'selected');
        }
        if (widget.multiple) {
            element.setAttribute('multiple', 'multiple');
        }
        if (widget.checked) {
            element.setAttribute('checked', 'checked');
        }

        if (widget.attrs !== undefined) {
            setAttributes(element, widget.attrs);
        }

        if (widget.dataBindings !== undefined) {
            var dataBindings = widget.dataBindings;
            for (var prop in dataBindings) {
                if (dataBindings[prop] !== '') {
                    element.setAttribute(Enums.DataBindingGroups[prop], dataBindings[prop]);
                }
            }
        }

        if (widget.styles !== undefined) {
            setStyles(element, widget.styles, widget.type);
        }

        if (widget['-webkit-filter'] !== undefined) {
            setLongStyles(element, widget['-webkit-filter'], 'webkitFilter');
        }

        if (widget.geometry !== undefined) {
            setStyles(element, widget.geometry, widget.type);
        }

        if (couiEditor.preferences.couiEnvironment === 'Hummingbird' &&
            (element.style.maskImage === undefined || element.style.maskImage === '')
            && callbacksObj !== undefined
        ) {
            element.style.mask = '';
            element.style.maskSize = '';
            element.style.maskRepeat = '';
            element.style.maskPosition = '';
            element.style.maskImage = '';
        }

        if (widget.transform !== undefined && widget.transform.rotate !== undefined) {
            element.style.transform = 'rotate(' + widget.transform.rotate + ')';
        } else if (widget.transform !== undefined) {
            element.style.transform = helpers.buildTransformStyles(widget);
        }
        if (widget['transform-origin']) {
            const originX = widget['transform-origin']['transform-origin-x'] || '50%';
            const originY = widget['transform-origin']['transform-origin-y'] || '50%';
            element.style.transformOrigin = `${originX}  ${originY}`;
        }

        if (widget['perspective-origin']) {
            const perspectiveX = widget['perspective-origin']['perspective-origin-x'] || '50%';
            const perspectiveY = widget['perspective-origin']['perspective-origin-y'] || '50%';
            element.style.perspectiveOrigin = `${perspectiveX}  ${perspectiveY}`;
        }

        if (widget.boxShadow !== undefined) {
            var boxShadowInstance = widget.boxShadow;

            if (boxShadowInstance.insetOutset === 'none') {
                boxShadowInstance.insetOutset = '';
            } else {
                boxShadowInstance.insetOutset = boxShadowInstance.insetOutset + ' ';
            }

            element.style.boxShadow = boxShadowInstance.insetOutset +
                boxShadowInstance.horizontalLength + ' ' +
                boxShadowInstance.verticalLength + ' ' +
                boxShadowInstance.blurRadius + ' ' +
                boxShadowInstance.spreadRadius + ' ' +
                boxShadowInstance.color;
        }

        if (widget.text !== undefined) {
            element.textContent = widget.text;
        }

        if (widget['-coherent-layer-clip-aa'] !== undefined) {
            element.style['-coherent-layer-clip-aa'] = widget['-coherent-layer-clip-aa'];
        }

        if (widget.color !== undefined) {
            element.style.color = widget.color;
        }

        if (widget.fontSize !== undefined) {
            if (widget.fontSize !== '') {
                element.style.fontSize = widget.fontSize;
            }
        }

        if (widget.fontFamily !== undefined) {
            element.style.fontFamily = widget.fontFamily;
        }

        if (widget.type === 'liveview') {
            element.setAttribute('src', 'liveview://' + widget.attrs.src.trim());
        }

        if (widget.children !== undefined &&
            widget.children.length > 0) {
            addChildren(element, widget.children, callbacksObj);
        }

        // needed to generate the correct element on load
        if (widget.type) {
            if (widget.widgetkit) {
                element.setAttribute('data-type', 'widget');
            } else {
                element.setAttribute('data-type', widget.type);
            }
        }

        // needed to separate kit widget form other elements
        if (widget.widgetkit) {
            element.setAttribute('data-widgetkit', widget.widgetkit);
        }

        // set the widget events as data attr
        if (widget.events && callbacksObj && callbacksObj.options) {

            for (var eventName in widget.events) {

                if (widget.events.hasOwnProperty(eventName)) {
                    var eventObject = widget.events[eventName];

                    for (var property in eventObject) {
                        if (eventObject.hasOwnProperty(property)) {

                            if (property === 'javascriptFunction') {
                                element.setAttribute('data-' + eventName, eventObject[property]);
                            } else {
                                element.setAttribute('data-' + eventName, property + '(' + eventObject[property] + ')');
                            }
                        }
                    }
                }
            }
        }

        element.setAttribute('data-element-type', 'widget');
        element.setAttribute('data-element-selectable', 'true');

        // Remove mousedown
        $(element).on('mousedown', function (e) {
            runtimeEditor.inputSearch.focusout();
            e.preventDefault();
        });

        widget.selectable = true;

        result.appendChild(element);

        // Check environment
        if (callbacksObj !== undefined) {

            if (!callbacksObj.options) {
                callbacksObj.runtimeEditor
                    .recalculateUnits(element, widget.id);
            }

        } else {
            // Attach events the environment is runtime only
            if (widget.events !== undefined) {
                setEvents(element, widget.events);
            }
        }
    }

    export function createComponent(component: IWidget, callback?) {
        let fragment = document.createDocumentFragment(),
            widget = component;

        factory(widget, fragment, callback);
        let $div = $('<div></div>');
        $div[0].appendChild(fragment);
        return $div.html();
    }

    export function loadAndCreateObj(scene, target, callback) {
        return loadSceneObj(scene, target, callback);
    }

    export function loadAndCreate(scene, target) {
        return load(scene, target);
    }

    export function create(scene, result, saving, callback) {
        var scenePreview = document.createDocumentFragment(),
            widgets = scene.widgets,
            widgetLength = widgets.length;

        for (var i = 0; i < widgetLength; ++i) {
            factory(widgets[i], scenePreview, callback);
        }

        result.appendChild(scenePreview);

        widgetsChildLocking(result);

        if (callback !== undefined) {
            var sceneVideos = $('#scene video');
            callback.runtimeEditor.displayVideos(sceneVideos);

            if (callback.endAllCallback !== undefined) {
                callback.endAllCallback();
            }
        }

        return when();
    }

    export function createAnElement(element, result, callbackObj?) {
        var fragment = document.createDocumentFragment(),
            widget = element;

        factory(widget, fragment, callbackObj);

        result.appendChild(fragment);
        widgetsChildLocking(result);
    }

    export function addFactory(type, ctor) {
        factories[type] = ctor;
    }

    export function promise(r) {
        return when(r);
    }

    export var factoriesExport = factories;

}

export default {
    loadSceneObj: runtimeCore.loadAndCreateObj,
    load: runtimeCore.loadAndCreate,
    create: runtimeCore.create,
    createAnElement: runtimeCore.createAnElement,
    addFactory: runtimeCore.addFactory,
    promise: runtimeCore.promise,
    setupProperties: runtimeCore.setupProperties,
    createComponent: runtimeCore.createComponent,
    factories: runtimeCore.factoriesExport
};

