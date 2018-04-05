/**
 *  @module lib/build_widget_settings.js
 *  @requires module:scripts/main
 *  @requires module:lib/function_helpers
 *  @requires module:lib/editor_settings
 */
'use strict';

declare let $;

import couiEditor from '../scripts/main';
import helpers from './function_helpers';
import editorSettings from './editor_settings';

module BuildWidgetHandler {

    var widget;
    var elementNode;

    export function buildWidget(type, currentWidget?, node?) {
        widget = currentWidget || $.extend(true, {}, couiEditor.environmentProperties.DefaultWidget);
        var defaultElementNode = {
            style: {},
            dataset: {},
            attributes: {},
            childNodes: {}
        };

        elementNode = node || defaultElementNode;
        widget.type = type;

        var nodeStyle = elementNode.style;
        // Set common properties
        widget.styles.display = nodeStyle.display || widget.styles.display;
        widget.styles.zIndex = nodeStyle.zIndex || widget.styles.zIndex;
        widget.styles.overflow = nodeStyle.overflow || widget.styles.overflow;
        widget.styles.opacity = nodeStyle.opacity || widget.styles.opacity;
        widget.styles.fontWeight = nodeStyle.fontWeight || widget.styles.fontWeight;
        widget.styles.fontStyle = nodeStyle.fontStyle || widget.styles.fontStyle;
        widget.styles.fontFamily = nodeStyle.fontFamily || widget.styles.fontFamily || 'initial';
        widget.fontSize = nodeStyle.fontSize || widget.styles.fontSize || 'auto';
        widget.color = nodeStyle.color || widget.color;

        let top = nodeStyle.top || widget.geometry.top;
        let left = nodeStyle.left || widget.geometry.left;
        let width = nodeStyle.width || widget.geometry.width;
        let height = nodeStyle.height || widget.geometry.height;

        if (nodeStyle.top) {
            var unitTop = helpers.getUnitStyle(top);
            top = parseFloat(top).toFixed(2) + unitTop;
        }

        if (nodeStyle.left) {
            var unitLeft = helpers.getUnitStyle(left);
            left = parseFloat(left).toFixed(2) + unitLeft;
        }

        if (nodeStyle.width) {
            if (nodeStyle.width === 'auto') {
                helpers.disableKendoInput('width');
            } else {
                const unitWidth = helpers.getUnitStyle(width);
                width = parseFloat(width).toFixed(2) + unitWidth;
            }
        }

        if (nodeStyle.height) {
            if (nodeStyle.width === 'auto') {
                helpers.disableKendoInput('height');
            } else {
                const unitHeight = helpers.getUnitStyle(height);
                height = parseFloat(height).toFixed(2) + unitHeight;
            }
        }

        widget.geometry = {
            'position': nodeStyle.position || widget.geometry.position,
            'top': top,
            'left': left,
            'width': width,
            'height': height
        };

        if (nodeStyle.transform) {
            widget.transform = helpers.createLongPropertyGroup(nodeStyle.transform);
        }

        if (nodeStyle.perspective) {
            widget.styles.perspective = nodeStyle.perspective;
        }

        if (nodeStyle.transformStyle) {
            widget.styles.transformStyle = nodeStyle.transformStyle;
        }

        if (nodeStyle.transformOrigin) {
            var transformOrigin = nodeStyle.transformOrigin.split(' ');
            widget['transform-origin'] = {};
            widget['transform-origin']['transform-origin-x'] = helpers.getStringPropertyPercent(transformOrigin[0]) || '50%';
            widget['transform-origin']['transform-origin-y'] = helpers.getStringPropertyPercent(transformOrigin[1]) || '50%';
        }

        if (nodeStyle.perspectiveOrigin) {
            var perspectiveOrigin = nodeStyle.perspectiveOrigin.split(' ');
            widget['perspective-origin']['perspective-origin-x'] = perspectiveOrigin[0] || '50%';
            widget['perspective-origin']['perspective-origin-y'] = perspectiveOrigin[1] || '50%';
        }

        if (elementNode.className) {
            widget.className = elementNode.className;
        }

        if (nodeStyle.boxShadow) {
            var temp = nodeStyle.boxShadow.split(') ');
            widget.boxShadow = helpers.createBoxShadowPropertyGroup(temp[0], temp[1]);
        }

        if (editorSettings.environment.GT === couiEditor.preferences.couiEnvironment) {
            // set our special layer clip property
            widget['-coherent-layer-clip-aa'] = nodeStyle['-coherent-layer-clip-aa'] || 'off';

            // // set localization attr
            if (elementNode.attributes['data-l10n-id']) {
                widget.attrs['data-l10n-id'] = elementNode.attributes['data-l10n-id'].value;
            } else {
                widget.attrs['data-l10n-id'] = '';
            }

            if (nodeStyle.webkitFilter) {
                widget['-webkit-filter'] = helpers.createFilterPropertyGroup(nodeStyle.webkitFilter);
            }

            // TODO backgroundBlendMode is disabled for the current editor version
            //widget.styles.backgroundBlendMode = nodeStyle.backgroundBlendMode || widget.styles.backgroundBlendMode;
            widget.styles.mixBlendMode = nodeStyle.mixBlendMode || 'normal';
        }

        build[type]();

        return widget;
    }

    var build: any = {};
    build.video = function () {
        setUrl(' ');
    };

    build.liveview = function () {
        setAttribute.src('liveviewName');
    };

    build.responsiveImage = function () {
        setProperty.width('auto');
        setProperty.height('auto');

        setUrl(' ');

        setProperty.responsiveBackgroundImage('');
        setProperty.backgroundRepeat('no-repeat');
        setProperty.backgroundPositionX('0px');
        setProperty.backgroundPositionY('0px');
        setProperty.backgroundSize('contain');

        setProperty.maskImageFullProps();
    };

    build.inputText = function () {
        setProperty.width('140px');
        setProperty.height('26px');
        setProperty.padding('2px');

        setAttribute.value('');
        setAttribute.placeHolder('placeHolder');
    };

    build.range = function () {
        setProperty.width('100px');
        setProperty.height('50px');
        setProperty.background('rgba(255, 255, 255, 1)');

        setAttribute.min('0');
        setAttribute.max('100');
        setAttribute.value('0');
        setAttribute.step('1');
    };

    build.number = function () {
        setProperty.width('60px');
        setProperty.height('20px');

        setAttribute.min('0');
        setAttribute.max('100');
        setAttribute.value('0');
        setAttribute.step('1');
    };

    build.flexbox = function () {
        setProperty.className('flexbox');
        setProperty.alignContent('flex-start');
        setProperty.alignItems('flex-start');
        setProperty.flexDirection('row');
        setProperty.flexWrap('nowrap');
        setProperty.justifyContent('flex-start');

        setProperty.color('rgba(0, 0, 0, 1)');
        setProperty.display('flex');
        setProperty.backgroundImage('rgba(255, 255, 255, 1)');
        setProperty.maskImageFullProps();
    };

    build.flexboxChild = function () {
        setProperty.flex('0 1 auto');
        setProperty.order('0');
        setProperty.alignSelf('auto');
        setProperty.color('rgba(0, 0, 0, 1)');
        setProperty.backgroundImage('rgba(255, 255, 255, 1)');
        setProperty.maskImageFullProps();
    };

    build.option = function () {
        setProperty.width('60px');
        setProperty.height('20px');

        setAttribute.value('option');
        setText(widget.type);

        setAttribute.select(false);
        setProperty.color('rgba(0, 0, 0, 1)');
    };

    build.checkbox = function () {
        setProperty.width('16px');
        setProperty.height('16px');

        setAttribute.checked(false);
        setProperty.background('rgba(255, 255, 255, 1)');
    };

    build.radio = function () {
        setProperty.width('16px');
        setProperty.height('16px');
        setProperty.borderTopLeftRadius('100rem');
        setProperty.borderTopRightRadius('100rem');
        setProperty.borderBottomLeftRadius('100rem');
        setProperty.borderBottomRightRadius('100rem');
        setProperty.background('rgba(255, 255, 255, 1)');
        setAttribute.checked(false);
    };

    build.select = function () {
        setProperty.width('60px');
        setProperty.height('20px');
        setAttribute.multiple(false);

        setAttribute.size('1');
        setProperty.background('rgba(255, 255, 255, 1)');
    };

    build.image = function () {
        setProperty.width('auto');
        setProperty.height('auto');
        setUrl(' ');
        setProperty.maskImageFullProps();
    };

    build.textarea = function () {
        setText(widget.type);
        setProperty.background('rgba(255, 255, 255, 1)');
        setProperty.color('rgba(0, 0, 0, 1)');
        setProperty.borderTopLeftRadius('0rem');
        setProperty.borderTopRightRadius('0rem');
        setProperty.borderBottomLeftRadius('0rem');
        setProperty.borderBottomRightRadius('0rem');
        setProperty.borderWidth('0px');
        setProperty.borderColor('rgba(0, 0, 0, 1)');
        setProperty.borderStyle('solid');
        setProperty.display('block');
        setProperty.resize('none');
        setProperty.userSelect('none');
    };

    build.label = function () {

        setProperty.width('60px');
        setProperty.height('20px');
        setText(widget.type);
        setProperty.background('rgba(255, 255, 255, 0)');
        setProperty.color('rgba(0, 0, 0, 1)');

        setProperty.textAlign('left');
    };

    build.text = function () {
        setText(widget.type);
        setProperty.background('rgba(255, 255, 255, 0)');
        setProperty.color('rgba(0, 0, 0, 1)');

        setProperty.textAlign('left');
        setProperty.textDecoration('node');
        setProperty.textTransform('node');
        setProperty.maskImageFullProps();
    };

    build.ol = function () {
        setProperty.background('rgba(255, 255, 255, 0)');
        setProperty.color('rgba(0, 0, 0, 1)');

        setProperty.textAlign('left');
        setProperty.textDecoration('node');
        setProperty.textTransform('node');
    };

    build.li = function () {

        setText(widget.type);
        setProperty.background('rgba(255, 255, 255, 0)');
        setProperty.color('rgba(0, 0, 0, 1)');

        setProperty.textAlign('left');
        setProperty.textDecoration('node');
        setProperty.textTransform('node');
    };

    build.ul = function () {
        setProperty.background('rgba(255, 255, 255, 0)');
        setProperty.color('rgba(0, 0, 0, 1)');
    };

    build.div = function () {
        setUrl(' ');
        setProperty.borderTopLeftRadius('0rem');
        setProperty.borderTopRightRadius('0rem');
        setProperty.borderBottomLeftRadius('0rem');
        setProperty.borderBottomRightRadius('0rem');
        setProperty.borderWidth('0px');
        setProperty.borderColor('rgba(0, 0, 0, 1)');
        setProperty.borderStyle('solid');
        setProperty.display('block');
        setProperty.backgroundImage('rgba(221, 221, 221, 1)');

        setProperty.maskImageFullProps();
    };

    build.widget = function () {
    }; // tslint:disable-line

    build.button = function () {

        setText(widget.type);
        setProperty.color('rgba(0, 0, 0, 1)');
        setProperty.padding('0px');
        setProperty.borderTopLeftRadius('3px');
        setProperty.borderTopRightRadius('3px');
        setProperty.borderBottomLeftRadius('3px');
        setProperty.borderBottomRightRadius('3px');
        setProperty.borderWidth('0px');
        setProperty.borderColor('rgba(150, 150, 150, 1)');
        setProperty.borderStyle('solid');
        setProperty.display('block');

        setUrl(' ');

        setProperty.backgroundImage('rgba(221, 221, 221, 1)');
        setProperty.maskImageFullProps();
    };

    build.roundedRect = function () {

        setProperty.color('rgba(0, 0, 0, 1)');
        setProperty.borderTopLeftRadius('1rem');
        setProperty.borderTopRightRadius('1rem');
        setProperty.borderBottomLeftRadius('1rem');
        setProperty.borderBottomRightRadius('1rem');
        setProperty.borderWidth('0px');
        setProperty.borderColor('rgba(0, 0, 0, 1)');
        setProperty.borderStyle('solid');
        setProperty.display('block');
        setProperty.backgroundImage('rgba(255, 255, 255, 1)');
        setProperty.maskImageFullProps();
    };

    build.circle = function () {

        setProperty.color('rgba(0, 0, 0, 1)');
        setProperty.borderTopLeftRadius('100rem');
        setProperty.borderTopRightRadius('100rem');
        setProperty.borderBottomLeftRadius('100rem');
        setProperty.borderBottomRightRadius('100rem');
        setProperty.borderWidth('0px');
        setProperty.borderColor('rgba(0, 0, 0, 1)');
        setProperty.borderStyle('solid');
        setProperty.display('block');
        setProperty.backgroundImage('rgba(255, 255, 255, 1)');
        setProperty.maskImageFullProps();
    };

    build.ellipse = function () {

        setProperty.color('rgba(0, 0, 0, 1)');
        setProperty.borderRadius('50%');
        setProperty.borderWidth('0px');
        setProperty.borderColor('rgba(0, 0, 0, 1)');
        setProperty.borderStyle('solid');
        setProperty.display('block');

        setProperty.backgroundImage('rgba(255, 255, 255, 1)');
        setProperty.maskImageFullProps();
    };

    build.rectangle = function () {

        setProperty.color('rgba(0, 0, 0, 1)');
        setProperty.borderWidth('0px');
        setProperty.borderColor('rgba(0, 0, 0, 1)');
        setProperty.borderStyle('solid');
        setProperty.display('block');
        setProperty.backgroundImage('rgba(255, 255, 255, 1)');

        setProperty.maskImageFullProps();
    };

    var setText = function (defaultValue) {
        var text;
        if (elementNode.childNodes[0]) {
            text = elementNode.childNodes[0].nodeValue;
        } else {
            text = defaultValue;
        }

        widget.text = text;
    };

    var setUrl = function (defaultValue) {
        widget.url = elementNode.src || defaultValue;
    };

    var setAttribute: any = {};

    setAttribute.placeHolder = function (defaultValue) {
        widget.attrs.placeHolder = elementNode.placeholder || defaultValue;
    };

    setAttribute.src = function (defaultValue) {
        var src = elementNode.attributes.src;
        if (src) {
            if (widget.type === 'liveview') {
                widget.attrs.src = src.value.replace('liveview://', '');
            } else {
                widget.attrs.src = src.value;
            }
        } else {
            widget.attrs.src = defaultValue;
        }
    };

    setAttribute.size = function (defaultValue) {
        widget.attrs.size = elementNode.size || defaultValue;
    };

    setAttribute.min = function (defaultValue) {
        widget.attrs.min = elementNode.min || defaultValue;
    };

    setAttribute.max = function (defaultValue) {
        widget.attrs.max = elementNode.max || defaultValue;
    };

    setAttribute.value = function (defaultValue) {
        widget.attrs.value = elementNode.value || defaultValue;
    };

    setAttribute.step = function (defaultValue) {
        widget.attrs.step = elementNode.step || defaultValue;
    };

    setAttribute.select = function (defaultValue) {
        widget.select = elementNode.select || defaultValue;
    };

    setAttribute.checked = function (defaultValue) {
        widget.checked = elementNode.checked || defaultValue;
    };

    setAttribute.multiple = function (defaultValue) {
        widget.multiple = elementNode.multiple || defaultValue;
    };

    var setProperty: any = {};

    setProperty.className = function (defaultValue) {
        widget.className = elementNode.className || defaultValue;
    };

    setProperty.alignContent = function (defaultValue) {
        widget.styles.alignContent = elementNode.style.alignContent || defaultValue;
    };

    setProperty.alignItems = function (defaultValue) {
        widget.styles.alignItems = elementNode.style.alignItems || defaultValue;
    };

    setProperty.flexDirection = function (defaultValue) {
        widget.styles.flexDirection = elementNode.style.flexDirection || defaultValue;
    };

    setProperty.flexWrap = function (defaultValue) {
        widget.styles.flexWrap = elementNode.style.flexWrap || defaultValue;
    };

    setProperty.justifyContent = function (defaultValue) {
        widget.styles.justifyContent = elementNode.style.justifyContent || defaultValue;
    };

    setProperty.padding = function (defaultValue) {
        if (editorSettings.environment.GT === couiEditor.preferences.couiEnvironment) {
            widget.styles.padding = elementNode.style.padding || defaultValue;
        }
    };

    setProperty.borderTopLeftRadius = function (defaultValue) {
        widget.styles.borderTopLeftRadius = elementNode.style.borderTopLeftRadius || defaultValue;

    };

    setProperty.borderTopRightRadius = function (defaultValue) {
        widget.styles.borderTopRightRadius = elementNode.style.borderTopRightRadius || defaultValue;
    };

    setProperty.borderBottomLeftRadius = function (defaultValue) {
        widget.styles.borderBottomLeftRadius = elementNode.style.borderBottomLeftRadius || defaultValue;
    };

    setProperty.borderBottomRightRadius = function (defaultValue) {
        widget.styles.borderBottomRightRadius = elementNode.style.borderBottomRightRadius || defaultValue;
    };

    setProperty.borderWidth = function (defaultValue) {
        widget.styles.borderWidth = elementNode.style.borderWidth || defaultValue;
    };

    setProperty.borderRadius = function (defaultValue) {
        widget.styles.borderRadius = elementNode.style.borderRadius || defaultValue;
    };

    setProperty.borderColor = function (defaultValue) {
        widget.styles.borderColor = elementNode.style.borderColor || defaultValue;
    };

    setProperty.borderStyle = function (defaultValue) {
        widget.styles.borderStyle = elementNode.style.borderStyle || defaultValue;
    };

    setProperty.width = function (defaultValue) {
        widget.geometry.width = elementNode.style.width || defaultValue;
    };

    setProperty.height = function (defaultValue) {
        widget.geometry.height = elementNode.style.height || defaultValue;
    };

    setProperty.display = function (defaultValue) {
        widget.styles.display = elementNode.style.display || defaultValue;
    };

    setProperty.color = function (defaultValue) {
        widget.color = elementNode.style.color || defaultValue;
    };

    setProperty.background = function (defaultValue) {
        widget.background = elementNode.style.backgroundColor || defaultValue;
    };

    setProperty.flex = function (defaultValue) {
        widget.styles.flex = elementNode.style.flex || defaultValue;
    };

    setProperty.order = function (defaultValue) {
        widget.styles.order = elementNode.style.order || defaultValue;
    };

    setProperty.alignSelf = function (defaultValue) {
        widget.styles.alignSelf = elementNode.style.alignSelf || defaultValue;
    };

    setProperty.backgroundRepeat = function (defaultValue) {
        widget.styles.backgroundRepeat = elementNode.style.backgroundRepeat || defaultValue;
    };

    setProperty.backgroundPositionX = function (defaultValue) {
        widget.styles.backgroundPositionX = elementNode.style.backgroundPositionX || defaultValue;
    };

    setProperty.backgroundPositionY = function (defaultValue) {
        widget.styles.backgroundPositionY = elementNode.style.backgroundPositionY || defaultValue;
    };

    setProperty.backgroundSize = function (defaultValue) {
        var nodeStyle = elementNode.style;
        var widgetStyles = widget.styles;

        if (!widgetStyles.backgroundSizeWidth) {
            widgetStyles.backgroundSizeWidth = 'auto';
        }

        if (!widgetStyles.backgroundSizeHeight) {
            widgetStyles.backgroundSizeHeight = 'auto';
        }

        if (!nodeStyle.backgroundSize) {
            widgetStyles.backgroundSize = 'contain';
            return;
        }

        var sizeValue = nodeStyle.backgroundSize;

        if (sizeValue !== 'inherit' && sizeValue !== 'cover' &&
            sizeValue !== 'contain') {
            var splitedVal = sizeValue.split(' ');
            widgetStyles.backgroundSize = 'auto';
            widgetStyles.backgroundSizeWidth = splitedVal[0];

            if (splitedVal.length === 1) {
                widgetStyles.backgroundSizeHeight = 'auto';
            } else {
                widgetStyles.backgroundSizeHeight = splitedVal[1];
            }
        } else {
            widgetStyles.backgroundSize = sizeValue;
        }
    };

    setProperty.responsiveBackgroundImage = function (defaultValue) {
        widget.styles.backgroundImage = elementNode.style.backgroundImage || defaultValue;
    };

    setProperty.backgroundImage = function (defaultValue) {
        var nodeStyle = elementNode.style;
        if (nodeStyle.backgroundImage &&
            nodeStyle.backgroundImage !== 'initial') {
            widget.background = nodeStyle.backgroundImage || defaultValue;
        } else {
            widget.background = nodeStyle.backgroundColor || defaultValue;
        }
    };

    setProperty.textAlign = function (defaultValue) {
        if (editorSettings.environment.GT === couiEditor.preferences.couiEnvironment) {
            widget.styles.textAlign = elementNode.style.textAlign || defaultValue;
        }
    };

    setProperty.textTransform = function (defaultValue) {
        if (editorSettings.environment.GT === couiEditor.preferences.couiEnvironment) {
            widget.styles.textTransform = elementNode.style.textTransform || defaultValue;
        }
    };

    setProperty.textDecoration = function (defaultValue) {
        if (editorSettings.environment.GT === couiEditor.preferences.couiEnvironment) {
            widget.styles.textDecoration = elementNode.style.textDecoration || defaultValue;
        }
    };

    setProperty.maskImageFullProps = function () {

        // Handling in the case that the redundant
        // 1.5.1 Editor Version (-webkit-mask: no-repeat)
        // is still present in the scene/sample

        if (elementNode.style.webkitMask !== 'no-repeat') {
            this.maskPositionX('0px');
            this.maskPositionY('0px');
            this.maskSize('contain');
            this.maskRepeat('no-repeat');
            this.maskImage(' ');
        } else {
            elementNode.style.webkitMask = '';
        }
    };

    setProperty.maskImage = function (defaultValue) {
        if (elementNode.style.webkitMaskImage && elementNode.style.webkitMaskImage !== 'initial') {
            widget.styles.webkitMaskImage = elementNode.style.webkitMaskImage;
        } else {
            widget.styles.webkitMaskImage = defaultValue;
        }
    };

    setProperty.maskPositionX = function (defaultValue) {
        widget.styles['-webkit-mask-position-x'] = elementNode.style.webkitMaskPositionX || defaultValue;
    };

    setProperty.maskPositionY = function (defaultValue) {
        widget.styles['-webkit-mask-position-y'] = elementNode.style.webkitMaskPositionY || defaultValue;
    };

    setProperty.maskSize = function (defaultValue) {
        var nodeStyle = elementNode.style;
        var widgetStyles = widget.styles;

        if (!widgetStyles['-webkit-mask-sizeWidth']) {
            widgetStyles['-webkit-mask-sizeWidth'] = 'auto';
        }

        if (!widgetStyles['-webkit-mask-sizeHeight']) {
            widgetStyles['-webkit-mask-sizeHeight'] = 'auto';
        }

        if (!nodeStyle['-webkit-mask-size']) {
            widgetStyles['-webkit-mask-size'] = 'contain';
            return;
        }

        var sizeValue = helpers.maskSizeFormat(nodeStyle['-webkit-mask-size']);

        if (sizeValue !== 'inherit' && sizeValue !== 'cover' &&
            sizeValue !== 'contain') {
            var splitedVal = sizeValue.split(' ');
            widgetStyles['-webkit-mask-size'] = 'auto';
            widgetStyles['-webkit-mask-sizeWidth'] = splitedVal[0];

            if (splitedVal.length === 1) {
                widgetStyles['-webkit-mask-sizeHeight'] = 'auto';
            } else {
                widgetStyles['-webkit-mask-sizeHeight'] = splitedVal[1];
            }
        } else {
            widgetStyles['-webkit-mask-size'] = sizeValue;
        }
    };

    setProperty.maskRepeat = function (defaultValue) {
        widget.styles.webkitMaskRepeat = elementNode.style.webkitMaskRepeat || defaultValue;
    };

    setProperty.resize = function (defaultValue) {
        widget.styles.resize = elementNode.style.resize || defaultValue;
    };

    setProperty.userSelect = function (defaultValue) {
        widget.styles.webkitUserSelect = elementNode.style.webkitUserSelect || defaultValue;
    };
}

export default BuildWidgetHandler;
