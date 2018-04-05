/**
 *  @module lib/function_helpers
 *  @requires module:lib/enums
 *  @requires module:scripts/main
 *  @requires module:lib/editor_settings
 *  @requires module:lib/editor_properties
 */
'use strict';

declare var $;

import Enums from './enums';
import couiEditor from '../scripts/main';
import editorProperties from './editor_properties';
import editorSettings from './editor_settings';
import filtersConfig from './common/filters_config';
import unitsCovertor from  '../scripts/helpers/units_conversion';

export default {
    getTransformedElMaskPos (el: IWidget) {
        var minY, minX, maxY, maxX;

        for (let i = 0; i < el.transformed_position.length; i++) {
            let x = el.transformed_position[i][0];
            let y = el.transformed_position[i][1];

            minY = (minY === undefined) ? y : minY;
            minX = (minX === undefined) ? x : minX;
            maxY = (maxY === undefined) ? y : maxY;
            maxX = (maxX === undefined) ? x : maxX;

            minY = y < minY ? y : minY;
            minX = x < minX ? x : minX;
            maxY = y > maxY ? y : maxY;
            maxX = x > maxX ? x : maxX;
        }

        return {top: minY, left: minX, height: maxY - minY, width: maxX - minX};
    },

    /**
     * Gets the transform origin from the style of the element
     * @param elGeometry {IWidgetGeometry}
     * @param element {IWidget}
     * @returns {IPosition}
     * */
        getTransformOrigin(elGeometry: IWidgetGeometry, element: IWidget): IPosition {
        let style = getComputedStyle(document.getElementById(element.id));
        let tOrigin = style.transformOrigin.split(' ');

        //adds the offset from the scene
        return {
            x: Number(tOrigin[0].match(/(-|[0-9\.0-9])+/g)[0]) + Number(elGeometry.left),
            y: Number(tOrigin[1].match(/(-|[0-9\.0-9])+/g)[0]) + Number(elGeometry.top)
        };
    },

    /**
     * Gets the geometry property of a widget, but without the units
     * @param element {IWidget}
     * @returns {IWidgetGeometry}
     * */
        getElementVertices (widget: IWidget): IWidgetGeometry {
        if ($.isEmptyObject(widget)) {
            return {top: '0', left: '0', width: '0', height: '0', position: ''};
        }

        let element = document.getElementById(widget.id);
        let width = window.getComputedStyle(element, null).getPropertyValue('width');
        let height = window.getComputedStyle(element, null).getPropertyValue('height');
        let top = element.offsetTop + 'px';
        let left = element.offsetLeft + 'px';

        if (this.getUnitStyle(top) === '%') {
            top = unitsCovertor.convertUnitsToPixel(widget.id, top, 'top');
        }
        if (this.getUnitStyle(left) === '%') {
            left = unitsCovertor.convertUnitsToPixel(widget.id, left, 'left');
        }
        if (this.getUnitStyle(width) === '%') {
            width = unitsCovertor.convertUnitsToPixel(widget.id, width, 'width');
        }
        if (this.getUnitStyle(height) === '%') {
            height = unitsCovertor.convertUnitsToPixel(widget.id, height, 'height');
        }
        return {
            top: this.getUnitValue(top),
            left: this.getUnitValue(left),
            width: this.getUnitValue(width),
            height: this.getUnitValue(height),
            position: element.style.position
        };
    },

    /**
     * Builds the transformation styles from the transfrom property of a widget
     * @param currentWidget{IWidget}
     * @returns transformsCss {string}
     * */
        buildTransformStyles(currentWidget: IWidget): string {
        let transformsCss = '';
        for (let i in Enums.TransformsOrder) {
            let prop = Enums.TransformsOrder[i];
            if (currentWidget.transform[prop]) {
                transformsCss += prop + '(' + currentWidget.transform[prop] + ') ';
            }
        }

        if (couiEditor.preferences.couiEnvironment === editorSettings.environment.Hummingbird) {
            transformsCss = this.rebuildTransformForHb(transformsCss);
        }

        return transformsCss;
    },
    rebuildTransformForHb(transformCssStr) {
        const scaleXRegex = /scaleX\((.*?)\)/;
        const scaleYRegex = /scaleY\((.*?)\)/;
        const resultScaleX = transformCssStr.match(scaleXRegex);
        const resultScaleY = transformCssStr.match(scaleYRegex);
        if (resultScaleX !== null || resultScaleY !== null) {
            let mergedScale = 'scale';
            if (resultScaleX !== null && resultScaleY !== null) {
                mergedScale += `(${resultScaleX[1]}, ${resultScaleY[1]})`;
                const propsToReplace = `${resultScaleX[0]} ${resultScaleY[0]}`;
                transformCssStr = transformCssStr.replace(propsToReplace, mergedScale);
            } else if (resultScaleX !== null && resultScaleY === null) {
                mergedScale += `(${resultScaleX[1]}, 1)`;
                const propsToReplace = `${resultScaleX[0]}`;
                transformCssStr = transformCssStr.replace(propsToReplace, mergedScale);
            } else if (resultScaleX === null && resultScaleY !== null) {
                mergedScale += `(1, ${resultScaleY[1]})`;
                const propsToReplace = `${resultScaleY[0]}`;
                transformCssStr = transformCssStr.replace(propsToReplace, mergedScale);
            }
        }

        const rotationZRegex = /rotateZ/;
        const resultRotationZ = transformCssStr.match(rotationZRegex);
        if (resultRotationZ !== null) {
            transformCssStr = transformCssStr.replace(rotationZRegex, 'rotate');
        }

        return transformCssStr;
    },
    buildPerspectiveOriginStyles(currentWidget: IWidget): string {
        let transformsCss = '';
        for (let i in currentWidget.transform) {
            if (i === 'transform-origin-x' || i === 'transform-origin-y') {
                continue;
            }
            transformsCss += i + '(' + currentWidget.transform[i] + ') ';
        }

        return transformsCss;
    },
    /**
     * Creates the transpose matrix of a column-major ordered matrix
     * @param matrix {Array}
     * @returns parsedM {Array}
     * */
        parseMatrix (matrix: Array<string> | Object, isWebKit?: boolean) {
        let parsedM = [[], [], [], []];
        let row = 0;
        let column = 0;

        let matrixLength = (matrix instanceof Array) ? matrix.length : 16;

        for (let m = 0; m < matrixLength; m++) {
            if (m !== 0 && m % Math.sqrt(matrixLength) === 0) {
                row = 0;
                column++;
            }
            let idx = isWebKit ? 'm' + (column + 1) + (row + 1) : m;
            parsedM[row][column] = Number(matrix[idx]);
            row++;
        }

        return parsedM;
    },

    /**
     * Multiplies two matrices
     * @param matrix {Array}
     * @param other {Array}
     * @returns result {Array}
     * */
        multiply (matrix: Array<Array<number>>, other: Array<number>) {
        let result = [];
        let sum = 0;
        for (let a = 0; a < matrix.length; a++) {
            for (let b = 0; b < matrix[a].length; b++) {
                sum += matrix[a][b] * other[b];
                result[a] = sum;
            }
            sum = 0;
        }
        return result;
    },
    /**
     * Checks if two arrays contain the same elements in the same order
     * @param a {Array} - the first array
     * @param b {Array} - the second array
     * @returns {boolean}
     * */
        areEqual (a: Array<number>, b: Array<number>): boolean {
        let match = 0;
        let length = a.length;
        if (length !== b.length) {
            return false;
        }

        for (let i = 0; i < length; i++) {
            if (a[i] !== b[i]) {
                return false;
            }
            match++;
        }

        return (match === a.length) ? true : false;
    },

    /**
     *Builds a string from an array of string values
     * @param values {Array}
     * @param sort {boolean} - by default the values will be sorted in ascending
     * order, if sort is passed, they will be sorted in descending order
     * @returns filterString {string}
     * */
        buildStringProps (values: Array<string>, sort?: boolean): string {
        values = sort ? this.sortArr(values) : values;
        let filterString = '';

        for (let i = 0; i < values.length; i++) {
            filterString += values[i];
        }

        return filterString;
    },

    /**
     * Checks if an array contains a value that matches a string
     * @param stringProps {Array<string>}
     * @param prop {string} - the string that has to be matched in the ith element of the stringProps array
     * @returns match {Array<number>} - Array, the first element of which is the index of the matched element
     * */
        hasValue (stringProps: Array<string>, prop: string): number {
        let match = -1;

        for (let i = 0; i < stringProps.length; i++) {
            if (stringProps[i].match(prop)) {
                match = i;
                break;
            }
        }
        return match;
    },

    /**
     * Sorts an array of string or number values in ascending order by default
     * @param arr {Array} - the array to be sorted
     * @param descending {boolean} - if given, the array will be sorted in descending order
     * @returns {Array} - the sorted array
     * */
        sortArr (arr: Array<string | number>, descending?: boolean): Array<string | number> {
        let sorted = arr.sort(function (el, otherEl) {
            let element = (el > otherEl) ? 1 : -1;
            return (descending) ? element * -1 : element;
        });

        return sorted;
    },
    getBoxShodowDefaults(): IDropShodow {
        return {
            dropShadowBlur: filtersConfig['-webkit-filter'].dropShadowBlur.default,
            dropShadowX: filtersConfig['-webkit-filter'].dropShadowX.default,
            dropShadowY: filtersConfig['-webkit-filter'].dropShadowY.default,
            dropShadowColor: filtersConfig['-webkit-filter'].dropShadowColor.default,
        };
    },
    buildDropShadowFiltersProps(keyframe: IKeyframeData, value: string): void {
        const property = keyframe.property;
        const runtimeEditor = couiEditor.openFiles[couiEditor.selectedEditor].runtimeEditor;
        if (property === 'dropShadowBlur' ||
            property === 'dropShadowX' ||
            property === 'dropShadowY' ||
            property === 'dropShadowColor') {
            let splitVal = value.replace(/\s/g, '').match(/([^\]+)( [^\)]+)/g);
            let dropShadowValue = (property === 'dropShadowColor') ? splitVal[0] + '(' + splitVal[1] + ')' : splitVal[0];
            runtimeEditor.runtimeBuildDropShodowFilter[property] = dropShadowValue;
        }
    },
    /**
     * Builds all filter properties as string from an array
     *
     * @param values {Array} - the separated filter values
     * @param sort {boolean} - if given, the values will be sorted in an ascending order
     * @returns stringValue {string}
     * */
        buildFilterProps (keyframe: IKeyframeData, sort?: boolean): string {
        let stringValue = '';
        let values = keyframe.values;

        values = sort ? this.sortArr(values) : values;

        for (let i = 0; i < values.length; i++) {
            stringValue += values[i];
        }
        return stringValue;
    },

    /**
     * Checks if an DOM element is of an empty type (type void) and has no closing tag
     * @param obj {HTMLElement} - the element to be checked
     * @returns {boolean]
     * */
        isVoidElement(obj: HTMLElement): boolean {
        let tag = obj.tagName;
        return (< string[]>Enums.voidElements).indexOf(tag) !== -1;
    },
    /**
     * Checks if a property is not allowed for animation
     *
     * @param prop {string} - propertyType;
     * @param value {string} - propertyValue;
     * @returns {boolean}
     * */
        notAllowedForAnimation(prop: string, value: string): boolean {
        if (Enums.notAllowedForAnimation[prop] === value) {
            return true;
        }
        return false;
    },
    /**
     * Checks if an ID contains only numbers and if this ID is already used
     *
     * @param id {string}
     * @param mappedWidgetsId {string} - current id in the runtime_editor's mappedWidgets
     * @returns isValid {boolean}
     * */
        validateId(id: string): boolean {
        let isValid = false;

        if (!id.match(/[^a-zA-Z0-9_-]+/g)) {
            isValid = true;
        }

        return isValid;
    },
    /**
     * Checks if an "Animation Name" start with a dash or a letter and is followed by only letters
     *
     * @param animationName {string}
     * @returns isValid {boolean}
     * */
        validateAnimationName(animationName: string): boolean {
        let isValid = false;

        if (animationName.match(/^([a-zA-Z]|^[-](?=[a-zA-Z]))[a-zA-Z0-9_-]*/g)) {
            isValid = true;
        }

        return isValid;
    },
    /**
     * Searches for a property in the objects of an array
     *
     * @param objs {Array} - array of objects in which to search for a prop
     * @param prop {string}
     * @param val {string | number}
     * @returns hasInChildren {boolean}
     * */
        hasInChildren (objs: Array<Object>, prop: string, val: string | number) {
        let hasInChildren = false;

        for (let i = 0; i < objs.length; i++) {
            if (objs[i][prop] === val) {
                hasInChildren = true;
                break;
            }
        }
        return hasInChildren;
    },
    /**
     * Rekicks the dom HTML after previews so that the webkitMask will keep their properties
     * Using it in @editor
     * @method rekickDomWebkitMasks
     * @returns {void}
     */
        rekickDomWebkitMasks(): void {
        let widgets: IWidget = couiEditor.openFiles[couiEditor.selectedEditor].runtimeEditor.mappedWidgets;
        for (let key of Object.keys(widgets)) {
            if (widgets[key].widget.styles.webkitMaskImage) {
                let elementStyle: HTMLElement = document.getElementById(key);
                elementStyle.style['maskImage'] = widgets[key].widget.styles.webkitMaskImage;
                elementStyle.style['maskRepeat'] = widgets[key].widget.styles.webkitMaskRepeat;
            }
        }
    },
    /**
     * Function getFromTimeline
     *
     * @param {string} selector - the jquery selector to execute on the detachedHTML
     * @returns {object}
     */
    getFromTimeline: function (selector: string) {
        let $element: any;
        if (!couiEditor.detachedAmnimationsHTML) {
            $element = $(selector);
        } else {
            $element = $(couiEditor.detachedAmnimationsHTML).find(selector);
        }
        return $element;
    },
    /**
     * Function shortening longer unit value.
     * @param {string} unitString - the (presumably) long unit value.
     * @returns {string}
     */
    shortenLongValue: function (unitString: string): string {
        let unitStyle = this.getUnitStyle(unitString);
        let newUnitValue: string;
        let unitValue: string;
        switch (unitStyle) {
            case 'deg':
            case 'px':
            case '%':
                unitValue = this.getUnitValue(unitString);
                newUnitValue = parseInt(unitValue) + unitStyle;
                break;
            case 'vw':
            case 'vh':
            case 'vmax':
            case 'vmin':
            case 'rem':
            case 'pt':
                unitValue = this.getUnitValue(unitString);
                newUnitValue = parseFloat(unitValue).toFixed(1) + unitStyle;
                break;
            default:
                newUnitValue = unitString;
        }
        return newUnitValue;
    },
    /**
     *
     * @param min
     * @param max
     * @returns {*}
     */
    getRandomNumber: function (min, max) {
        return Math.random() * (max - min) + min;
    },
    /**
     * Function translating the names of the dataBinds
     *
     * @function translateDataBindNames
     * @param {string} value - name of the dataBinding
     * @param {boolean} toScene - handler of whether to translate to scene or to editor
     * @returns {string}
     */
    translateDataBindNames: function (value, toScene) {
        if (toScene) {
            let translatedNames = Enums.DataBindingGroups[value].replace('data-', '');

            if (value === 'bindClassToggle') {
                translatedNames = translatedNames.replace('bind-', '');
            } else if (value === 'bindStyleBackgroundColor') {
                translatedNames = translatedNames.replace('bind-style-', '');
            } else if (value === 'bindBackgroundImageUrl') {
                translatedNames = translatedNames.replace('bind-background-image-url', 'background-image');
            }

            return translatedNames;
        } else {
            if (Enums.DataBindingGroups[value]) {
                return value;
            }

            let newValue = value.split('-')
                .map(element => this.capitalizeFirstLetter(element))
                .join('')
                .replace('Bind', 'bind');

            if (value === 'class-toggle') {
                newValue = 'bind' + newValue;
            } else if (value === 'background-color') {
                newValue = 'bindStyle' + newValue;
            } else if (value === 'background-image') {
                newValue = 'bind' + newValue + 'Url';
            }

            return newValue;
        }
    },
    /**
     * Function normalizing style values so that they can be applied in the editor.
     *
     * @function stylesValueNormalizer
     * @param {stringStyle} string to normalize the values in
     * @return normalized string style
     */
    stylesValueNormalizer: function (stringStyle) {
        var rgbMatch = /rgba\((\d.*?),\s*(\d.*?),\s*(\d.*?),\s*(\d.*?)\)/g;
        var rgbValue = stringStyle.match(rgbMatch);

        if (rgbValue !== null) {
            var newValue = rgbValue[0].split(rgbMatch);
            newValue = 'rgba(' + parseInt(newValue[1]) + ',' + parseInt(newValue[2]) + ',' +
                parseInt(newValue[3]) + ',' + parseFloat(newValue[4]).toFixed(1) + ')';
            stringStyle = stringStyle.replace(rgbValue[0], newValue);
        }

        return stringStyle;
    },
    /**
     * Function converting string to a normalized RGBA values string.
     *
     * @function convertColorValues
     * @param {stringStyle} string to convert color values in
     * @return converted string style
     */
    convertColorValues: function (stringStyle) {
        var rgbMatch = /rgb\((\d+),\s*(\d+),\s*(\d+)\)/g;
        var nexMatch = /#\b[0-9A-Fa-f]+\b/g;

        var rgbValue = stringStyle.match(rgbMatch);

        if (rgbValue !== null) {
            var tempRGB = rgbValue[0].replace(')', ',1)');
            tempRGB = tempRGB.replace('rgb(', 'rgba(');
            stringStyle = stringStyle.replace(rgbValue[0], tempRGB);
        }

        var nexValue = stringStyle.match(nexMatch);

        if (nexValue !== null) {

            var hex = nexValue[0].replace('#', '');

            if (hex.length < 6) {
                hex = hex[0] + hex[0] + hex[1] + hex[1] + hex[2] + hex[2];
            }

            var r = parseInt((hex).substring(0, 2), 16);
            var g = parseInt((hex).substring(2, 4), 16);
            var b = parseInt((hex).substring(4, 6), 16);

            var result = 'rgba(' + r + ',' + g + ',' + b + ',1)';

            stringStyle = stringStyle.replace(nexValue[0], result);
        }

        return stringStyle;
    },
    /**
     * Function using regular expression to to get the lowest decimal between two values.
     *
     * @function getHighestDecimal
     * @param {num1} first decimal number
     * @param {num2} second decimal number
     * @return the highest decimal number
     */
    getHighestDecimal: function (num1, num2) {
        var match1 = ('' + num1).match(/(?:\.(\d+))?(?:[eE]([+-]?\d+))?$/);
        var match2 = ('' + num2).match(/(?:\.(\d+))?(?:[eE]([+-]?\d+))?$/);
        if (!match1 || !match2) {
            return 0;
        }
        var highestFirst = Math.max(0, (match1[1] ? match1[1].length : 0) - (match1[2] ? +match1[2] : 0));
        var highestSecond = Math.max(0, (match2[1] ? match2[1].length : 0) - (match2[2] ? +match2[2] : 0));

        return Math.max(highestFirst, highestSecond);
    },
    /**
     * Function using regular expression to omit the text content of the
     * user css styles in order to close the scope to the shown viewport.
     *
     * @function applyStylesToScene
     * @param {cssContent} an array of css styles strings
     * @return the merged content of all the css style strings changed to apply only to the scope of the editor viewport
     */
    applyStylesToScene: function (cssContent) {
        var comittedContent;
        /* tslint:disable */
        // The regex bellow checks the css strings and does the following:
        // THE PURPOSE: Find all style mark begginings to add a another tag.
        // 1. "(?:#|\.|@)" - look after for a class a @(id, media type, webkit, animation) and pass the group you to
        // 2. "[\sa-zA-Z+~*$>:=^#\"\'\.\[\]\[0-9\]-]+\s*(?:,\s*(?:#|\.)?[\sa-zA-Z+~*$>:=^#\"\'\.\[\]\[0-9\]-]" -
        // this in practice jumps all the inside classes, tags, etc.. which can also include empty spaces quotes digits
        // 3. "+)*\s*(?={)" - break the above cycle when you hit a group which (after a number of empty spaces contains a '{';
        // 4. "()" - the quotes on over all specify the last group to be returned and the inner ones are used as marks
        // of to where to insert extra tags (#scene in this case to augment the output string);
        var regex = /^((?:#|\.|@)?[\sa-zA-Z+~*$>:=^#\"\'\.\[\]\[0-9\]-]+\s*(?:,\s*(?:#|\.)?[\sa-zA-Z+~*$>:=^#\"\'\.\[\]\[0-9\]-]+)*\s*(?={))/gm;
        /* tslint:enable */
        function isObject(value) {
            return (typeof value === 'string');
        }

        var filteredCssCont = cssContent.filter(isObject);
        comittedContent = filteredCssCont.join('\n');

        comittedContent = comittedContent.split(regex).map(function (result) {
            if (result !== '' && result[0] !== '{') {
                var selectorList = result.split(',').map(function (element) {
                    let cssSelectot = element.trim();
                    if (cssSelectot === '@font-face') {
                        return cssSelectot + ' ';
                    } else if (cssSelectot === 'body' ||
                        cssSelectot === 'html' ||
                        cssSelectot === 'body, html') {
                        return '#scene ';
                    } else {
                        return '#scene ' + cssSelectot;
                    }
                }).join(',');
                return selectorList;
            } else {
                return result;
            }
        }).join('');

        return comittedContent + '\n' +
            '#scene * {' + '\n' +
            '/*CSS transitions*/' + '\n' +
            '-o-transition-property: none !important;' + '\n' +
            '-moz-transition-property: none !important;' + '\n' +
            '-ms-transition-property: none !important;' + '\n' +
            '-webkit-transition-property: none !important;' + '\n' +
            'transition-property: none !important;' + '\n' +
            '/*CSS animations*/' + '\n' +
            '-webkit-animation: none !important;' + '\n' +
            '-moz-animation: none !important;' + '\n' +
            '-o-animation: none !important;' + '\n' +
            '-ms-animation: none !important;' + '\n' +
            'animation: none !important;' + '\n' +
            '}' + '\n' +
            '#scene point, #scene mask, #scene div[data-type="widget"]{' + '\n' +
            'background-color: transparent !important;' + '\n' +
            '}' + '\n' +
            '#scene div{' + '\n' +
            '-o-transform: none;' + '\n' +
            '-moz-transform: none;' + '\n' +
            '-ms-transform: none;' + '\n' +
            '-webkit-transform: none;' + '\n' +
            'transform: none;' + '\n' +
            '}';
    },
    /**
     * Function returning the background image size in pixel of any element.
     *
     * @function getBackgroundSize
     * @param {elem} an element to return the background size of
     * @return width and height of the background image of the selected element
     */
    getBackgroundSize: function (elem) {
        // This:
        //       * Gets elem computed styles:
        //             - CSS background-size
        //             - element's width and height
        //       * Extracts background URL
        var computedStyle = getComputedStyle(elem),
            image = new Image(),
            src = computedStyle.backgroundImage.replace(/url\((['"])?(.*?)\1\)/gi, '$2'),
            cssSize: any = computedStyle.backgroundSize,
            elemW = parseInt(computedStyle.width.replace('px', ''), 10),
            elemH = parseInt(computedStyle.height.replace('px', ''), 10),
            elemDim = [elemW, elemH],
            computedDim = [],
            ratio;
        // Load the image with the extracted URL.
        // Should be in cache already.
        image.src = src;
        // Determine the 'ratio'
        ratio = image.width > image.height ? image.width / image.height : image.height / image.width;
        // Split background-size properties into array
        cssSize = cssSize.split(' ');
        // First property is width. It is always set to something.
        computedDim[0] = cssSize[0];
        // If height not set, set it to auto
        computedDim[1] = cssSize.length > 1 ? cssSize[1] : 'auto';
        if (cssSize[0] === 'cover') {
            // Width is greater than height
            if (elemDim[0] > elemDim[1]) {
                // Elem's ratio greater than or equal to img ratio
                if (elemDim[0] / elemDim[1] >= ratio) {
                    computedDim[0] = elemDim[0];
                    computedDim[1] = 'auto';
                } else {
                    computedDim[0] = 'auto';
                    computedDim[1] = elemDim[1];
                }
            } else {
                computedDim[0] = 'auto';
                computedDim[1] = elemDim[1];
            }
        } else if (cssSize[0] === 'contain') {
            // Width is less than height
            if (elemDim[0] < elemDim[1]) {
                computedDim[0] = elemDim[0];
                computedDim[1] = 'auto';
            } else {
                // elem's ratio is greater than or equal to img ratio
                if (elemDim[0] / elemDim[1] >= ratio) {
                    computedDim[0] = 'auto';
                    computedDim[1] = elemDim[1];
                } else {
                    computedDim[1] = 'auto';
                    computedDim[0] = elemDim[0];
                }
            }
        } else {
            // If not 'cover' or 'contain', loop through the values
            for (var i = cssSize.length; i--;) { // tslint:disable-line
                // Check if values are in pixels or in percentage
                if (cssSize[i].indexOf('px') > -1) {
                    // If in pixels, just remove the 'px' to get the value
                    computedDim[i] = cssSize[i].replace('px', '');
                } else if (cssSize[i].indexOf('%') > -1) {
                    // If percentage, get percentage of elem's dimension
                    // and assign it to the computed dimension
                    computedDim[i] = elemDim[i] * (cssSize[i].replace('%', '') / 100);
                }
            }
        }
        // If both values are set to auto, return image's
        // original width and height
        if (computedDim[0] === 'auto' && computedDim[1] === 'auto') {
            computedDim[0] = image.width;
            computedDim[1] = image.height;
        } else {
            // Depending on whether width or height is auto,
            // calculate the value in pixels of auto.
            // ratio in here is just getting proportions.
            ratio = computedDim[0] === 'auto' ? image.height / computedDim[1] : image.width / computedDim[0];
            computedDim[0] = computedDim[0] === 'auto' ? image.width / ratio : computedDim[0];
            computedDim[1] = computedDim[1] === 'auto' ? image.height / ratio : computedDim[1];
        }
        // Finally, return an object with the width and height of the
        // background image.
        return {
            width: computedDim[0],
            height: computedDim[1]
        };
    },
    /**
     *
     * @param {string} type
     * @param {number} customWidth
     * @param {number} customHeight
     * @returns {{type: string, width: number, height: number}}
     */
    getSceneSizeByType: function (type, customWidth, customHeight) {
        var width: string = '';
        var height: string = '';

        switch (type) {
            case 'aspectRatio5_4':
                width = '1000px';
                height = '800px';
                break;
            case 'aspectRatio4_3':
                width = '800px';
                height = '600px';
                break;
            case 'aspectRatio16_10':
                width = '1440px';
                height = '900px';
                break;
            case 'aspectRatio16_9':
                width = '1600px';
                height = '900px';
                break;
            case 'aspectRatio16_9_full_hd':
                width = '1920px';
                height = '1080px';
                break;
            case 'aspectRatio_custom':
                width = customWidth + 'px';
                height = customHeight + 'px';
                break;
        }
        return {
            'type': type,
            'width': width,
            'height': height
        };
    },
    numKeys: function (o) {
        return Object.keys(o).length;
    },
    getObjects: function (obj, key) {
        var objects = [];
        for (var i in obj) {
            if (!obj.hasOwnProperty(i)) {
                continue;
            }
            if (typeof obj[i] === 'object' && i !== key) {
                objects = objects.concat(this.getObjects(obj[i], key));
            } else if (i === key) {
                objects.push(obj[key]);
            }
        }
        return objects;
    },
    getSceneSize: function () {
        var scene = couiEditor.openFiles[couiEditor.selectedEditor].runtimeEditor.scene;

        var sceneH = parseInt(scene.sceneSize.height);
        var sceneW = parseInt(scene.sceneSize.width);

        return {
            'width': sceneW,
            'height': sceneH
        };
    },
    getSceneFontSize: function () {
        return parseFloat(getComputedStyle($('#scene')[0], null)['font-size']);
    },
    isfirstUpload: function (type, widget) {
        if (type === 'image') {
            return widget.url.trim() === '';
        } else if (type === 'responsiveImage') {
            return widget.styles.backgroundImage.trim() === '';
        }
    },
    capitalizeFirstLetter: function (str) {
        return str.charAt(0).toUpperCase() + str.slice(1);
    },
    toFixed: function (num, digits) {
        var pow = Math.pow(10, digits);

        return Math.floor((num * 100 / 100) * pow) / pow;
    },
    isUndefined: function (a) {
        return a === undefined;
    },
    isEventName: function (name) {
        return (<string[]>Enums.EventTypes).indexOf(name) !== -1;
    },
    isDataBindingTypes: function (name) {
        return (<string[]>Enums.DataBindingTypes).indexOf(name) !== -1;
    },
    /**
     * Function checking whether the value is a transform property.
     *
     * @function isTransformProperty
     * @param {string} property - the chosen transform property
     * @return {boolean}
     */
    isTransformProperty: function (property) {
        return (< string[]>Enums.TransformTypes).indexOf(property) !== -1;
    },
    /**
     * Returning the newly created transform value.
     *
     * @function getTransformProperty
     * @param {string} inputValue - the input value from the transform toolbar
     * @param {string} propKey - the input class transform property edited
     * @return {string} adjusted input value to apply to the widget
     */
    getTransformProperty: function (inputValue, propKey) {
        if (propKey === 'rotateX' || propKey === 'rotateY' || propKey === 'rotateZ'
            || propKey === 'skewX' || propKey === 'skewY') {
            inputValue = inputValue !== undefined ? inputValue : '0deg';
        } else if (propKey === 'scaleX' || propKey === 'scaleY' || propKey === 'scaleZ') {
            inputValue = inputValue !== undefined ? inputValue : '1';
        } else if (propKey === 'transform-origin-x' || propKey === 'transform-origin-y') {
            inputValue = inputValue !== undefined ? inputValue : '50%';
        } else {
            // translateX,Y,Z cases //
            inputValue = inputValue !== undefined ? inputValue : '0px';
        }
        return inputValue;
    },
    /**
     * Function returning the shortened widget name.
     * @function shortenString
     * @param {string} value - the unmuted string value
     * @return {string} value - the returned string value shortened to 20 units
     */
    shortenString: function (value) {
        if (value.length > 20) {
            value = value.substring(0, 20);
            return value + '...';
        }
        return value;
    },

    /**
     * Shorten a string and display only a portion of it backwards
     * @param value {string} - the value to be shorten
     * @param substring - substring index
     * @returns {string}
     */
    backwardsShortenString: function (value: string, substring: number = 20): string {
        const valueLength = value.length;
        let result = value;

        if (valueLength > substring) {
            result = result.substring(valueLength - substring, valueLength);
            return '...' + result;
        }

        return value;
    },

    /**
     *
     * Helper function to determine whether there is an intersection between the two polygons described
     * by the lists of vertices. Uses the Separating Axis Theorem
     *
     * @function doPolygonsIntersect
     * @param {object} a an array of connected points [{x:, y:}, {x:, y:},...] that form a closed polygon
     * @param {object} b an array of connected points [{x:, y:}, {x:, y:},...] that form a closed polygon
     * @return true if there is any intersection between the 2 polygons, false otherwise
     */
    doPolygonsIntersect: function (a, b) {

        var polygons = [a, b];
        var minA, maxA, projected, i, i1, j, minB, maxB;

        for (i = 0; i < polygons.length; i++) {

            // for each polygon, look at each edge of the polygon, and determine if it separates
            // the two shapes
            var polygon = polygons[i];
            for (i1 = 0; i1 < polygon.length; i1++) {

                // grab 2 vertices to create an edge
                var i2 = (i1 + 1) % polygon.length;
                var p1 = polygon[i1];
                var p2 = polygon[i2];

                // find the line perpendicular to this edge
                var normal = {
                    x: p2.y - p1.y,
                    y: p1.x - p2.x
                };

                minA = maxA = undefined;
                // for each vertex in the first shape, project it onto the line perpendicular to the edge
                // and keep track of the min and max of these values
                for (j = 0; j < a.length; j++) {
                    projected = normal.x * a[j].x + normal.y * a[j].y;
                    if (this.isUndefined(minA) || projected < minA) {
                        minA = projected;
                    }
                    if (this.isUndefined(maxA) || projected > maxA) {
                        maxA = projected;
                    }
                }

                // for each vertex in the second shape, project it onto the line perpendicular to the edge
                // and keep track of the min and max of these values
                minB = maxB = undefined;
                for (j = 0; j < b.length; j++) {
                    projected = normal.x * b[j].x + normal.y * b[j].y;
                    if (this.isUndefined(minB) || projected < minB) {
                        minB = projected;
                    }
                    if (this.isUndefined(maxB) || projected > maxB) {
                        maxB = projected;
                    }
                }

                // if there is no overlap between the projects, the edge we are looking at separates the two
                // polygons, and we know there is no overlap
                if (maxA < minB || maxB < minA) {
                    return false;
                }
            }
        }
        return true;
    },
    calcElementTransformedMatrixCoords: function (el, parentId) {
        var coords = {
            x: 0,
            y: 0,
        };

        while (el.parentElement.id !== parentId) {
            let absoluteTransformMatrix = this.getElementAbsoluteTransform(el.parentElement, parentId);
            var parentCoords = this.calcMatrixPoints(el, absoluteTransformMatrix);

            coords.x += parentCoords.x;
            coords.y += parentCoords.y;
            el = el.parentElement;
        }

        return coords;
    },
    getElementAbsoluteTransform: function (el, parentId) {
        var matrix = new WebKitCSSMatrix(window.getComputedStyle(el).webkitTransform);

        if (el.id === 'scene') {
            matrix.m41 = 0;
            matrix.m42 = 0;
        }
        while (el.parentElement.id !== parentId) {
            var parentM = new WebKitCSSMatrix(window.getComputedStyle(el.parentElement).webkitTransform);
            //skip the translation of the scene
            if (el.parentElement.id === 'scene' || el.id === 'scene') {
                parentM.m41 = 0;
                parentM.m42 = 0;
            }

            matrix = matrix.multiply(parentM);
            el = el.parentElement;
        }
        return matrix;
    },
    calcMatrixPoints: function (el, matrix) {
        var m = matrix;
        var borderLeft = parseFloat(getComputedStyle(el, null).getPropertyValue('border-left-width')) || 0;
        var borderTop = parseFloat(getComputedStyle(el, null).getPropertyValue('border-top-width')) || 0;
        var l = el.offsetLeft + borderLeft, //position from the parent
            t = el.offsetTop + borderTop; //position from the parent

        var left = (!isNaN(l)) ? l : 0;
        var top = (!isNaN(t)) ? t : 0;

        let pointX = left - parseFloat(getComputedStyle(el.parentElement, null).getPropertyValue('width')) / 2 +
            parseFloat(getComputedStyle(el, null).getPropertyValue('width')) / 2;
        let pointY = top - parseFloat(getComputedStyle(el.parentElement, null).getPropertyValue('height')) / 2 +
            parseFloat(getComputedStyle(el, null).getPropertyValue('height')) / 2;

        let testMatrix = this.multiply(this.parseMatrix(m, true), [pointX, pointY, 0, 1]);

        var x = testMatrix[0];
        var y = testMatrix[1];

        return {
            x: x - pointX,
            y: y - pointY
        };
    },
    firstToUpperCase: function (str) {
        return str.substr(0, 1).toUpperCase() + str.substr(1);
    },
    getFileType: function (url) {
        var extension = url.split('.').pop();
        var type;

        var extensions = editorProperties[editorSettings.environment[couiEditor.preferences.couiEnvironment]].DefaultExtensions;

        if (extensions.image.indexOf(extension) !== -1) {
            type = 'image';
        } else if (extensions.video.indexOf(extension) !== -1) {
            type = 'video';
        } else if (extensions.html.indexOf(extension) !== -1) {
            if (url.indexOf('widgets') !== -1) {
                type = 'widget';
            } else {
                type = false;
            }
        } else if (extensions.style.indexOf(extension) !== -1) {
            type = 'style';
        } else if (extensions.script.indexOf(extension) !== -1) {
            type = 'script';
        } else if (extensions.font.indexOf(extension) !== -1) {
            type = 'font';
        } else {
            type = false;
        }
        return type;
    },
    getFilename: function (url) {
        var regex = /[^\\|/|]+$/;
        var name = url.match(regex)[0];
        return name;
    },
    getTransformedCoordsGlobal: function (deltaX, deltaY, clickX, clickY, m3d) {
        var matrixCorrect = this.parseMatrix(m3d, true);

        var scene = document.getElementById('scene');
        var sceneOffsets = this.getAbsolutePosition(scene, 'coui-editor');

        var realClickX = clickX - sceneOffsets.left;
        var realClickY = clickY - sceneOffsets.top;

        var clickPoint = {
            x: realClickX,
            y: realClickY,
            z: 0,
            w: 1
        };

        var initialPoint = {
            x: 0,
            y: 0,
            z: 0
        };

        // first point on the plane
        var elemVectorX0 = ((-10) * matrixCorrect[0][0]) + ((-10) * matrixCorrect[0][1]) +
            (0 * matrixCorrect[0][2]) + (1 * matrixCorrect[0][3]);
        var elemVectorY0 = ((-10) * matrixCorrect[1][0]) + ((-10) * matrixCorrect[1][1]) +
            (0 * matrixCorrect[1][2]) + (1 * matrixCorrect[1][3]);
        var elemVectorZ0 = ((-10) * matrixCorrect[2][0]) + ((-10) * matrixCorrect[2][1]) +
            (0 * matrixCorrect[2][2]) + (1 * matrixCorrect[2][3]);
        var elemVectorW0 = (-10 * matrixCorrect[3][0]) + (-10 * matrixCorrect[3][1]) +
            (0 * matrixCorrect[3][2]) + (1 * matrixCorrect[3][3]);

        var pointOnElement0 = {
            x: elemVectorX0,
            y: elemVectorY0,
            z: elemVectorZ0,
            w: elemVectorW0
        };

        var A = (0 * matrixCorrect[0][0]) + (0 * matrixCorrect[0][1]) +
            (1 * matrixCorrect[0][2]) + (1 * matrixCorrect[0][3]);
        var B = (0 * matrixCorrect[1][0]) + (0 * matrixCorrect[1][1]) +
            (1 * matrixCorrect[1][2]) + (1 * matrixCorrect[1][3]);
        var C = (0 * matrixCorrect[2][0]) + (0 * matrixCorrect[2][1]) +
            (1 * matrixCorrect[2][2]) + (1 * matrixCorrect[2][3]);

        var division = Math.sqrt(A * A + B * B + C * C);

        A = A / division;
        B = B / division;
        C = C / division;

        var D = A * pointOnElement0.x + B * pointOnElement0.y + C * pointOnElement0.z;
        var d = (((A * initialPoint.x) + (B * initialPoint.y) + (C * initialPoint.z)) - D) /
            Math.sqrt((A * A) + (B * B) + (C * C));

        var t = -(((clickPoint.x) * A) + ((clickPoint.y) * B) + (0 * C) + d) / ((0 * A) + (0 * B) + (-1 * C));

        var mouseX = window.mouseCoordsX - sceneOffsets.left;
        var mouseY = window.mouseCoordsY - sceneOffsets.top;
        var t2 = -(((mouseX) * A) + ((mouseY) * B) + (0 * C) + d) / ((0 * A) + (0 * B) + (-1 * C));

        var cx1 = (clickPoint.x * matrixCorrect[0][0]) + (clickPoint.y * matrixCorrect[0][1]) +
            (t * matrixCorrect[0][2]) + (1 * matrixCorrect[0][3]);
        var cy1 = (clickPoint.x * matrixCorrect[1][0]) + (clickPoint.y * matrixCorrect[1][1]) +
            (t * matrixCorrect[1][2]) + (1 * matrixCorrect[1][3]);

        var cx2 = (mouseX * matrixCorrect[0][0]) + (mouseY * matrixCorrect[0][1]) +
            (t2 * matrixCorrect[0][2]) + (1 * matrixCorrect[0][3]);
        var cy2 = (mouseX * matrixCorrect[1][0]) + (mouseY * matrixCorrect[1][1]) +
            (t2 * matrixCorrect[1][2]) + (1 * matrixCorrect[1][3]);

        var deltaX1 = (cx2 - cx1);
        var deltaY1 = (cy2 - cy1);

        return {
            x: deltaX1,
            y: deltaY1
        };
    },
    getElementAbsoluteTransformV2: function (el) {

        let matrices = [new WebKitCSSMatrix(window.getComputedStyle(el).webkitTransform)];
        let scene = document.getElementById('scene');
        let finalMatrix = new WebKitCSSMatrix(window.getComputedStyle(scene).webkitTransform);
        while (true) {
            if (el.parentElement.id === 'scene') {
                break;
            }
            matrices.push(new WebKitCSSMatrix(window.getComputedStyle(el.parentElement).webkitTransform));
            el = el.parentElement;
        }

        for (let i = matrices.length - 1; i > 0; i--) {
            finalMatrix = finalMatrix.multiply(matrices[i]);
        }
        return finalMatrix.inverse();
    },
    getTransformedCoordsSkew: function (x, y, skewX, skewY) {
        var x1 = (x - (skewX));
        var y1 = (y - (skewY));

        return {
            x: x1,
            y: y1
        };
    },
    getTransformedCoordsRotation: function (x, y, rot) {
        var x1 = (x * Math.cos(-rot)) - (y * Math.sin(-rot));
        var y1 = (x * Math.sin(-rot)) + (y * Math.cos(-rot));

        return {
            x: x1,
            y: y1
        };
    },
    getAbsolutePosition: function (element, parentId) {
        var xPosition = 0,
            yPosition = 0,
            paddingLeft,
            paddingTop,
            borderLeft,
            borderTop,
            sceneBorderWidth = 3;

        if (parentId !== 'scene') {
            sceneBorderWidth = 0;
        }

        while (element && element.id !== parentId) {

            paddingLeft = parseFloat(getComputedStyle(element.parentElement, null)
                    .getPropertyValue('padding-left')) || 0;
            paddingTop = parseFloat(getComputedStyle(element.parentElement, null)
                    .getPropertyValue('padding-top')) || 0;

            borderLeft = parseFloat(getComputedStyle(element.parentElement, null)
                    .getPropertyValue('border-left-width')) || 0;
            borderTop = parseFloat(getComputedStyle(element.parentElement, null)
                    .getPropertyValue('border-top-width')) || 0;

            xPosition += element.offsetLeft - paddingLeft + borderLeft;
            yPosition += element.offsetTop - paddingTop + borderTop;
            element = element.offsetParent;
        }

        return {
            left: xPosition - sceneBorderWidth,
            top: yPosition - sceneBorderWidth
        };
    },
    rotationInfo: function (el, options?) {
        var info: any = {
            rad: 0,
            deg: 0
        };
        while (el.id !== 'scene') {

            var tr: any = window.getComputedStyle(el, null)
                .getPropertyValue('-webkit-transform');

            tr = tr.match('matrix\\((.*)\\)');
            if (tr) {
                tr = tr[1].split(',');
                if (typeof tr[0] !== 'undefined' && typeof tr[1] !== 'undefined') {
                    info.rad += Math.atan2(tr[1], tr[0]);
                    info.deg += info.rad * 180 / Math.PI;
                }
            }
            if (options === 'single') {
                break;
            }
            el = el.parentElement;
        }
        return info;
    },
    findParents: function (el) {
        while (el.parentNode) {
            var topEl = el;
            el = el.parentNode;
            if (el.id === 'scene') {
                return topEl;
            }
        }
        return null;
    },
    timeFormatAnimations: function (milli) {
        var milliseconds = milli % 1000;
        var seconds = Math.floor((milli / 1000) % 60);
        var minutes = Math.floor((milli / (60 * 1000)) % 60);

        return minutes + ':' + seconds + '.' + milliseconds;
    },
    /**
     *
     * @param {object} styles
     * @returns {string}
     */
    buildDropShadowProperty: function (styles) {
        if (styles.dropShadowColor) {
            return 'drop-shadow(' + styles.dropShadowColor + ' ' +
                parseFloat(styles.dropShadowX) + 'px ' +
                parseFloat(styles.dropShadowY) + 'px ' +
                parseFloat(styles.dropShadowBlur) + 'px)';
        }
        return '';
    },
    /**
     *
     * @param boxShadowString
     * @returns {*}
     */
    dropShadowPropsFix: function (boxShadowString: string): any {

        var dropShadowRegex = /(\(.*\))(.*?\))/;
        var dropShadowRegexReverse = /(.*)(rgb.+\))/;
        //var dropShadowString = this.buildDropShadowProperty(boxShadowString);
        var splitDropShadowString = boxShadowString.match(dropShadowRegex);
        var boxShadowColor;
        var cleanBoxshadowProps;
        if (splitDropShadowString === null) {
            splitDropShadowString = boxShadowString.match(dropShadowRegexReverse);
            boxShadowColor = splitDropShadowString[2];
            cleanBoxshadowProps = splitDropShadowString[1]
                .substring(1)
                .trim()
                .split(' ');
            return [cleanBoxshadowProps[0], cleanBoxshadowProps[1],
                cleanBoxshadowProps[2], cleanBoxshadowProps[3]];
        } else {
            boxShadowColor = splitDropShadowString[1];
            cleanBoxshadowProps = splitDropShadowString[2]
                .substring(0, splitDropShadowString[2].length - 1)
                .trim()
                .split(' ');
            return {
                boxShadowX: cleanBoxshadowProps[0],
                boxShadowY: cleanBoxshadowProps[1],
                boxShadowColor: boxShadowColor,
                boxShadowBlur: cleanBoxshadowProps[2]
            };
        }
    },
    /**
     * TODO parse both string drop-shadow(rgba(255,255,255) 30px 40px 10px) &&
     * drop-shadow(30px 40px 10px rgba(255,255,255));
     * @param values
     * @returns {Array|{index: number, input: string}}
     */
    getValuesFromFilterCssProperties: function (values) {
        var regex = /([^\]+)( [^\)]+)/g;
        var cleanValues = this.cleanFilterString(values);
        var result = cleanValues.clenedString.match(regex) || [];

        for (var prop in cleanValues.dropShadowProps) {
            result.push(prop);
            result.push(cleanValues.dropShadowProps[prop]);
        }

        return result;
    },
    getValuesBoxshadow: function (values) {
        values = values.trim();
        var regex = /((?:rgb|hsl)(?:a)?\(.+\))$|(#[0-9a-fA-F]{3,6})$|\b(\w+)$|([^ ]+)/gm;
        var props = values.match(regex);

        if (props.length === 5) {
            props.unshift('none');
        }

        return ['insetOutset', props[0],
            'horizontalLength', props[1],
            'verticalLength', props[2],
            'blurRadius', props[3],
            'spreadRadius', props[4],
            'color', props[5]
        ];
    },
    splitCssStringProperties: function (widgetId, stringProps, propGroup, propKey, val, callback) {
        var regex = /(\D+)\((.*?)\)/;
        var filterVals = stringProps.split(' ').map(function (str) {
            return str.match(regex);
        }).filter(function (value) {
            return value !== null;
        }).reduce(function (filterVals, currentValue) {
            // return array with property name and value ['blur', '1px']
            filterVals[currentValue[1]] = currentValue[2];
            return filterVals;
        }, {});

        // Setting the property value
        filterVals[propKey] = typeof val === 'number' ? Math.round(val * 10) / 10 : val;

        // Render
        var vals = [];
        Object.keys(filterVals).sort().forEach(function (key, i) {
            if (key !== 'dropShadowBlur' && key !== 'dropShadowColor' &&
                key !== 'dropShadowX' && key !== 'dropShadowY') {
                vals.push(key + '(' + filterVals[key] + ')');
            }
        });

        var allProperties = vals.join(' ');
        if (propGroup === 'webkitFilter') {
            var widgetStylesObj = couiEditor.openFiles[couiEditor.selectedEditor]
                .runtimeEditor.mappedWidgets[widgetId].widget['-webkit-filter'];
            allProperties += ' ' + this.buildDropShadowProperty(widgetStylesObj);
        }

        return callback(allProperties.trim());
    },
    /**
     * Function returning the box shadow object from the longhand boxShadow style.
     * @param {string} stringProps - longhand boxShadow style.
     * @returns {any}
     */
    getBoxshadowObject: function (stringProps: string): any {
        let boxShadowObject = {};
        let boxShadowValues = this.getValuesBoxshadow(stringProps);
        for (let j = 0; j < boxShadowValues.length - 1; j += 2) {
            boxShadowObject[boxShadowValues[j]] = boxShadowValues[j + 1];
        }
        return boxShadowObject;
    },
    /**
     * Function returning the webkit filter object from the longhand webkit filter style.
     * @param {string} stringProps - longhand webkit filter style.
     * @returns {any}
     */
    getWebkitFilterObject: function (stringProps: string): any {
        let filterVals = {};
        let filterProp = this.getValuesFromFilterCssProperties(stringProps);
        for (let j = 0; j < filterProp.length - 1; j += 2) {
            filterVals[filterProp[j]] = filterProp[j + 1];
        }

        if (filterVals['rgba'] || filterVals['rgb']) {
            filterVals['dropShadowColor'] = filterProp[0] + '(' + filterProp[1] + ')';
            delete filterVals['rgba'];
            delete filterVals['rgb'];
        } else if (filterVals['dropShadowColor']) {
            filterVals['dropShadowColor'] = filterProp[1] + '(' + filterProp[2] + ')';
        }
        return filterVals;
    },
    /**
     * Method adjusting the widget(in editor) transform styles to combine to a single transform style string
     *
     * This method:
     *
     * 1. Merges the all transfrom properties to a single transform value.
     * 2. Converts Transfrom Rotation from Radian to Degree.
     * 3. Moves the outliers (transform-origin-y, transform-origin-x) outside of the transform prop. so that
     * they can be handled with the standart handling;
     *
     * @function mergeTransfromStyleProperties
     * @param {object} transformObject - an object containing all of the transforms of the widget
     * @return {object} newTransformObject - object containing the transform styles as well as the transform origins
     */
    mergeTransfromStyleProperties: function (transformObject) {
        var keys = Object.keys(transformObject);

        var transformString = '';
        var originProp = {};

        for (var i = 0; i < keys.length; i++) {
            const propKey = keys[i];

            if (transformObject[propKey] !== Enums.TransformDefaultValue[propKey]) {
                if (propKey === 'rotateX' || propKey === 'rotateY' || propKey === 'rotateZ'
                    || propKey === 'skewX' || propKey === 'skewY') {
                    transformString = transformString + propKey + '(' + transformObject[propKey] + ') ';
                } else if (propKey === 'transform-origin-x' || propKey === 'transform-origin-y') {
                    originProp[propKey] = transformObject[propKey];
                } else if (propKey === 'rotate') {
                    var rotationValue = transformObject[propKey];
                    if (this.getUnitStyle(rotationValue) === 'rad') {
                        rotationValue = this.toDegrees(parseFloat(rotationValue)) + 'deg';
                    }
                    transformString = transformString + propKey + '(' + rotationValue + ') ';
                } else if ((propKey === 'scaleX' || propKey === 'scaleY') &&
                    couiEditor.preferences.couiEnvironment === 'Hummingbird') {

                    const scaleX = transformObject.scaleX || 1;
                    const scaleY = transformObject.scaleY || 1;

                    if (!transformString.match(/scale/)) {
                        transformString = transformString + `scale(${scaleX}, ${scaleY}) `;
                    }
                } else {
                    transformString = transformString + propKey + '(' + transformObject[propKey] + ') ';
                }
            }
        }

        var newTransformObject = {
            'basic': transformString.trim(),
            'origin': ''
        };

        if (originProp['transform-origin-x'] || originProp['transform-origin-y']) {
            newTransformObject.origin = (originProp['transform-origin-x'] || '50%') + ' ' +
                (originProp['transform-origin-y'] || '50%');
        }

        return newTransformObject;
    },
    isBackgroundPositionString: function (value) {
        value = value + '';
        return (['center', 'top', 'bottom', 'left', 'right'].indexOf(value) !== -1);
    },
    convertJsToCssProperty: function (property) {
        // Split at each capital letter, convert to lower case and join with dashes (e.g. borderColor -> border-color)
        return property.split(/(?=[A-Z])/).map(function (s) {
            if (s === 'webkit') {
                s = '-' + s;
            }
            return s.toLowerCase();
        }).join('-');
    },
    convertCssToJsProperty: function (cssProperty) {
        var _this = this;
        var jsProperty = cssProperty.split(/[-]/).map(function (s) {
            return _this.capitalizeFirstLetter(s);
        });

        jsProperty[0] = jsProperty[0].toLowerCase();
        return jsProperty.join('');
    },
    _mapWidgets(widgets, mappedWidgets) {
        var _this = this;
        mappedWidgets = mappedWidgets ? mappedWidgets : {};

        widgets.map(function (widget) {
            var sceneLen = 0;

            if (widget.children !== undefined &&
                widget.children.length > 0) {
                sceneLen = widget.children.length;
            }

            if (widget.id === undefined) {
                widget.id = couiEditor.generateRandomId(widget.type);
                couiEditor.widgetCount++;
            }

            mappedWidgets[widget.id] = {
                widget: widget
            };

            if (sceneLen > 0) {
                return _this._mapWidgets(widget.children, mappedWidgets);
            }
        });

        return mappedWidgets;
    },
    getStringPropertyPercent: function (backgroundProperty) {
        var returnValue;
        switch (backgroundProperty) {
            case 'left':
                returnValue = '0%';
                break;
            case 'top':
                returnValue = '0%';
                break;
            case 'center':
                returnValue = '50%';
                break;
            case 'right':
                returnValue = '100%';
                break;
            case 'bottom':
                returnValue = '100%';
                break;
            default:
                returnValue = backgroundProperty;
        }
        return returnValue;
    },
    /**
     *
     * @param value
     * @example '139.0%' matches '%' - return '%'
     * @returns {any}
     */
    getUnitStyle: function (value: string) {
        //Matches the unit out of any string (ex. from '139.0%' matches '%', from '100deg' matches 'deg');
        var regex = /([a-zA-Z+%]+)/g;
        value = '' + value;
        var result = value.match(regex);
        if (!result) {
            return '';
        }
        if (result.length > 1) {
            return result[result.length - 1];
        } else {
            if (result[0] === 'top' ||
                result[0] === 'left' ||
                result[0] === 'right' ||
                result[0] === 'bottom' ||
                result[0] === 'center' ||
                result[0] === 'initial' ||
                result[0] === 'inherit' ||
                result[0] === 'rgba' ||
                result[0] === 'blur' ||
                result[0] === 'invert') {
                return '';
            } else if (this.isBackgroundPositionString(result[0])) {
                return '%';
            } else {
                return result[0];
            }
        }
    },
    /**
     *
     * @param value
     * @example `getColorValue('(rgb(0,0,0)')`, `getColorValue('(#000)')` - return clear value `rgb(0,0,0)`, `#000`
     * @returns {*}
     */
    getColorValue: function (value) {
        var colorRegex = /rgb.+\)|#.+/;
        if (value instanceof Array) {
            return value[0].match(colorRegex);
        } else {
            return value.match(colorRegex)[0];
        }
    },
    getUnitValue: function (prop) {
        if (prop === 'center' ||
            prop === 'bottom' ||
            prop === 'left' ||
            prop === 'right' ||
            prop.toString() === '0'
        ) {
            return prop;
        }

        if (prop !== undefined &&
            prop.indexOf('rgb') === -1 &&
            prop.indexOf('rgba') === -1 &&
            prop.indexOf('blur') === -1 &&
            prop.indexOf('invert') === -1
        ) {
            prop = '' + prop;
            if (!isNaN(parseFloat(prop)) || prop.indexOf('rotate') === 0 || prop.indexOf('blur') === 0) {
                //Matches the number out of any string (ex. from 'rotate(100deg)' takes out '100');
                var regex = /[^a-zA-Z%-()]+/;
                var result = prop.match(regex);
                if (result) {
                    return prop.match(regex)[0];
                }
            }
        }
        return '';
    },
    /**
     * Checks whether the input is a valid number
     * @function isInputValidNumber
     * @param {string} input - the string tested whether it is a 'number' or not
     * @returns {boolean}
     */
    isInputValidNumber: function (input) {
        if (!input) {
            return;
        }

        var regex = /^-?\d*\.?\d*$/;
        return regex.test(input);
    },
    /**
     * This function patch the behavior of '<input type="number">' in GT.
     * As soon as type="number" is working correctly it should be deleted and
     * replaced with the value of the input.
     *
     * @returns {*}
     */
    patchInputNumberBehavior: function (object) {
        var $this = object.$this,
            currentWidget = object.currentWidget,
            propGroup = object.propGroup,
            propKey = object.propKey,
            valueAsNumber;

        if (this.isInputValidNumber($this.val()) === true) {
            valueAsNumber = $this.val();
        } else {
            if (!$this.val()) {
                valueAsNumber = parseFloat(currentWidget[propGroup][propKey]).toFixed(1);
            } else if ($this.hasClass('units-number') && $this.val()) {
                valueAsNumber = parseFloat(currentWidget[propGroup][propKey]).toFixed(1);
                valueAsNumber = valueAsNumber ? valueAsNumber : '';

                // The value is overwrite here, because in the future when we
                // no longer need the function, we will replace it with "$this.val()"
                $this.val(valueAsNumber);
            } else {
                return $this.val();
            }
        }

        return valueAsNumber;
    },
    cleanUrls: function (url) {
        const regex = /^coui:\/\/(?:uiresources\/)|(!?editor\/)|^coui:\/\/(?:editor\/)/;
        let path = url.replace(regex, '').replace(/editor\//, '');
        // remove http url from path, when the scene is opened in web
        path = path.replace(/^[^#]*?:\/\/.*?\/(.*)$/, '$1');
        return this.externalFilePathHandler(path, couiEditor.openFiles[couiEditor.selectedEditor].tab.filePath);
    },
    /**
     * return object properties for an widget
     * @param {string} string - css property in format `contrast(1) saturate(1) opacity(1) brightness(1)`
     * @returns {object | string}
     * @example return object in format `{contrast: '1',  brightness: '1'}`
     */
    createLongPropertyGroup: function (string) {
        if (string.trim() !== '') {
            var regExp = /\(([^)]+)\)/;
            // negative lookup to match shorthand properties
            var temp = string.split(/ (?!\d)/);
            var propertyObj: any = {};
            var state;
            var property;

            for (var prop in temp) {
                state = regExp.exec(temp[prop]);
                if (state !== null) {

                    property = temp[prop].split('(');

                    var value = state[1];
                    // convert deg to rad
                    if (property[0] === 'rotate' ||
                        property[0] === 'rotateX' ||
                        property[0] === 'rotateY' ||
                        property[0] === 'rotateZ' ||
                        property[0] === 'skewX' ||
                        property[0] === 'skewY') {
                        var unit = this.getUnitStyle(value);

                        if (unit === 'rad') {
                            value = this.toDegrees(parseFloat(value)) + 'deg';
                        }

                        if (property[0] === 'rotate') {
                            propertyObj['rotateZ'] = value;
                        } else {
                            propertyObj[property[0]] = value;
                        }

                    } else if (property[0] === 'scale' && couiEditor.preferences.couiEnvironment === 'Hummingbird') {
                        const splitValue = value.split(', ');
                        propertyObj.scaleX = splitValue[0];
                        propertyObj.scaleY = splitValue[1];
                    } else {
                        propertyObj[property[0]] = value;
                    }
                }
            }

            return propertyObj;
        }
        return {};
    },
    syncFiltersAnimations(selectedWidgetId: string, property: string): void {
        const runtimeEditor = couiEditor.openFiles[couiEditor.selectedEditor].runtimeEditor;
        const missingTransformKeyframes = this.findNotAnimatedFiltersProperties(selectedWidgetId, property);
        if (missingTransformKeyframes.length > 0) {
            runtimeEditor.forceAutoKeyframes = true;
            runtimeEditor._undoCreationStepsLength = missingTransformKeyframes.length;
            missingTransformKeyframes.map((propertyName) => {
                const transformValue = runtimeEditor.mappedWidgets[selectedWidgetId].widget['-webkit-filter'][propertyName];
                runtimeEditor.Animations.addKeyframe(selectedWidgetId, '-webkit-filter', propertyName, transformValue, {
                    offset: -4,
                    seconds: 0
                });
            });
            runtimeEditor.forceAutoKeyframes = false;
        }
    },
    findNotAnimatedFiltersProperties(widgetId: string, defaultProperty: string): Array<string> {
        const animClass = couiEditor.openFiles[couiEditor.selectedEditor].runtimeEditor.scene.animations[widgetId];
        if (!animClass) {
            return [];
        }
        const className = animClass.className;
        const filterProperties = animClass[className].keyframes['-webkit-filter'];
        if (!filterProperties) {
            return [];
        }
        const animatedProperties = [defaultProperty];
        for (let key in filterProperties) {
            if (key !== defaultProperty) {
                animatedProperties.push(key);
            }
        }

        const widget = couiEditor.openFiles[couiEditor.selectedEditor].runtimeEditor.mappedWidgets[widgetId].widget;
        const widgetFilterProperties = [];
        for (let key in widget['-webkit-filter']) {
            widgetFilterProperties.push(key);
        }

        const missingFilters = widgetFilterProperties.filter(val => !animatedProperties.includes(val));

        return missingFilters;
    },
    syncTransformAnimations(selectedWidgetId: string, property: string): void {
        const runtimeEditor = couiEditor.openFiles[couiEditor.selectedEditor].runtimeEditor;
        const missingTransformKeyframes = this.findNotAnimatedTransformProperties(selectedWidgetId, property);
        if (missingTransformKeyframes.length > 0) {
            runtimeEditor.forceAutoKeyframes = true;
            runtimeEditor._undoCreationStepsLength = missingTransformKeyframes.length;
            missingTransformKeyframes.map((propertyName) => {
                const transformValue = runtimeEditor.mappedWidgets[selectedWidgetId].widget.transform[propertyName];
                runtimeEditor.Animations.addKeyframe(selectedWidgetId, 'transform', propertyName, transformValue, {
                    offset: -4,
                    seconds: 0
                });
            });
            runtimeEditor.forceAutoKeyframes = false;
        }
    },
    findNotAnimatedTransformProperties(widgetId: string, defaultProperty: string): Array<string> {
        const animClass = couiEditor.openFiles[couiEditor.selectedEditor].runtimeEditor.scene.animations[widgetId];
        if (!animClass) {
            return [];
        }
        const className = animClass.className;
        const transformProperties = animClass[className].keyframes.transform;
        if (!transformProperties) {
            return [];
        }
        const animatedProperties = [defaultProperty];
        for (let key in transformProperties) {
            if (key !== defaultProperty) {
                animatedProperties.push(key);
            }
        }

        const widget = couiEditor.openFiles[couiEditor.selectedEditor].runtimeEditor.mappedWidgets[widgetId].widget;
        const widgetTransformProperties = [];
        for (let key in widget.transform) {
            widgetTransformProperties.push(key);
        }

        const missingTransforms = widgetTransformProperties.filter(val => !animatedProperties.includes(val));

        return missingTransforms;
    },
    reorderTransform: function (keyframes: Object): Object {
        let ordered = [];
        for (let time in keyframes) {
            ordered[time] = [];
            for (let transfromProp in Enums.TransformsOrder) {
                for (let j = 0; j < keyframes[time].length; j++) {
                    if (Enums.TransformsOrder[transfromProp] === keyframes[time][j].property) {
                        ordered[time].push(keyframes[time][j]);
                    }
                }
            }
        }

        return ordered;
    },
    /**
     * return filter object for an widget
     * @param {string} string - filter properties in format `contrast(1) saturate(1) drop-shadow(10px 20px 30px #fff)`
     * @returns {object}
     */
    createFilterPropertyGroup: function (string) {
        var propertyObj = this.cleanFilterString(string);

        var longStyleProperties = this.createLongPropertyGroup(propertyObj.clenedString);
        $.extend(true, longStyleProperties, propertyObj.dropShadowProps);
        return longStyleProperties;
    },
    /**
     * return modulated value(rising from 0 to 0.5 and getting lower from 0.5 to 1)
     * @param {value} number - value to be modulated
     * @param {modulator} number - float between 0 and 1
     * @returns {number}
     */
    modulateValue: function (value, modulator) {
        return parseFloat(Math.cos(Math.PI * modulator).toFixed(2)) * (value);
    },
    hasDropShadowProperty(obj: any): boolean {
        if (obj.dropShadowBlur ||
            obj.dropShadowColor ||
            obj.dropShadowX ||
            obj.dropShadowY) {
            return true;
        }
        return false;
    },
    /**
     *
     * @param string
     * @returns {{clenedString: *, dropShadowProps: {}}}
     */
    cleanFilterString: function (string) {
        var dropShadowRegExp = /(drop-shadow)(.*?\))(.*?\))/;
        var clenedString = string;
        var dropShadowProps = dropShadowRegExp.exec(string);
        var propertyObj: any = {};
        var tempProps = [];

        if (dropShadowProps !== null) {
            clenedString = string.replace(dropShadowProps[0], '').trim(); //remove drop-shadow property from string

            var len = dropShadowProps[3].length;
            if (dropShadowProps[3] !== ')') {
                dropShadowProps[3] = dropShadowProps[3].substr(0, len - 1).trim();
                tempProps = dropShadowProps[3].split(' ');
            } else {
                tempProps = this.dropShadowPropsFix(dropShadowProps[2]);
            }

            propertyObj.dropShadowX = tempProps[0];
            propertyObj.dropShadowY = tempProps[1];
            propertyObj.dropShadowBlur = tempProps[2];

            var colorValue: any = this.getColorValue(dropShadowProps[2]);
            propertyObj.dropShadowColor = colorValue;
        }

        return {
            clenedString: clenedString,
            dropShadowProps: propertyObj
        };
    },
    createBoxShadowPropertyGroup: function (elColor, elProps) {
        var shadowObj: any = {};
        var props = ['horizontalLength', 'verticalLength', 'blurRadius', 'spreadRadius', 'insetOutset'];
        var shadowProps = elProps.split(' ');

        shadowObj.color = elColor + ')';

        if (props.length !== shadowProps.length) {
            for (var i = 0; i < props.length; i++) {
                if (props[i] === 'insetOutset') {
                    shadowObj[props[i]] = 'none';
                } else {
                    shadowObj[props[i]] = shadowProps[i];
                }
            }
        } else {
            for (var j = 0; j < props.length; j++) {
                shadowObj[props[j]] = shadowProps[j];
            }
        }

        return shadowObj;
    },
    toDegrees: function (angle) {
        return angle * (180 / Math.PI);
    },
    toRadians: function (angle) {
        return angle * (Math.PI / 180);
    },
    /**
     *
     * @param value
     * @returns {*|string}
     */
    getAnimationClassNames: function (value) {
        //return value;
        var classes = value.split(' ');
        var runtimeAnimationClassNames =
            couiEditor.openFiles[couiEditor.selectedEditor].runtimeEditor.scene.animationClasses;
        var animationClasses = [];
        for (var i = 0; i < classes.length; i++) {
            if (runtimeAnimationClassNames[classes[i]]) {
                animationClasses.push(classes[i]);
            }
        }
        // return the first match
        return animationClasses[0] || '';
    },
    /**
     *
     * @param classNames
     * @param className
     */
    doesClassNameExist: function (classNames, className) {
        var classes = classNames.split(' ');

        for (var i = 0; i < classes.length; i++) {
            if (classes[i] === className) {
                return true;
            }
        }

        return false;
    },
    /**
     *
     * @param classNames
     * @returns {Array}
     */
    getAnimationClassesFromClassNames: function (classNames) {
        var animationClassNames = [];
        var classes = classNames.split(' ');
        var runtimeAnimationClassNames =
            couiEditor.openFiles[couiEditor.selectedEditor].runtimeEditor.scene.animationClasses;

        for (var i = 0; i < classes.length; i++) {
            if (runtimeAnimationClassNames[classes[i]]) {
                animationClassNames.push(classes[i]);
            }
        }

        return animationClassNames;
    },
    /**
     *
     * @param classNames
     * @param oldClassName
     * @param newClassName
     * @returns {*}
     */
    replaceAnimationName: function (classNames, oldClassName, newClassName) {
        if (!classNames) {
            return newClassName;
        }

        var classes = classNames.split(' ');

        if (oldClassName !== '') {
            for (var i = 0; i < classes.length; i++) {
                if (classes[i] === oldClassName) {
                    classes[i] = newClassName;
                }
            }
        } else {
            if (!this.doesClassNameExist(classNames, newClassName)) {
                classes.push(newClassName);
            }
        }

        return classes.join(' ');
    },
    /**
     *
     * @param {array} animationClassesNames
     * @param {string} classNames
     */
    removeMoreThanOneAnimationClasses: function (animationClassesNames, classNames) {

        var allClasses = classNames.split(' ');
        var cleanClassNames = allClasses.filter(function (index) {
            return animationClassesNames.indexOf(index) < 0;
        });

        console.log(cleanClassNames);
        cleanClassNames.unshift(animationClassesNames[0]);
        return cleanClassNames.join(' ');
    },
    /**
     * Extract and save the current scene objects in a HTML file
     * Currently exporting: redo and animationClasses
     */
    exportJSON: function () {
        couiEditor.openFiles[couiEditor.selectedEditor].runtimeEditor.undoRedoScene('undo');
        if (couiEditor.openFiles[couiEditor.selectedEditor].undo.length === 0) {
            var redo = couiEditor.openFiles[couiEditor.selectedEditor].redo;
            var animationClasses =
                couiEditor.openFiles[couiEditor.selectedEditor].runtimeEditor.scene.animationClasses;

            var sceneObj = {
                redo: redo,
                animationClasses: animationClasses
            };
            var str = JSON.stringify(sceneObj);
            couiEditor.onsave('*new0.html', str);
        }
    },
    /**
     * @typedef {Object} moveWidgetOptions
     * @property {string} elementId - Widget id.
     * @property {string} moveDirection - The direction in which thewidget will be moved.
     * @property {number} step - Number that represent the increment of the position.
     * @property {boolean} preserveUnits - If true the current widget units will be preserved,
     * else the units will be convert to "px".
     */

    /**
     * Change widget position.
     * @param {moveWidgetOptions} options
     */
    moveWidget: function (options) {
        if (!options || !options.elementId || !options.moveDirection || !options.step ||
            options.preserveUnits === undefined) {
            return;
        }
        var elementId = options.elementId;
        var step = options.step;
        var preserveUnits = options.preserveUnits;

        var currentRuntimeEditor = couiEditor.openFiles[couiEditor.selectedEditor].runtimeEditor;
        var mappedWidget = currentRuntimeEditor.mappedWidgets[elementId].widget;
        var direction;
        var elementUnitValue;
        var elementUnitStyle;
        var newElementValue;

        if (options.moveDirection === 'up' || options.moveDirection === 'down') {
            direction = 'top';
        } else {
            direction = 'left';
        }

        if (options.moveDirection === 'down' || options.moveDirection === 'right') {
            step = step * -1;
        }

        if (preserveUnits) {
            elementUnitValue = parseFloat(mappedWidget.geometry[direction]);
            elementUnitStyle = this.getUnitStyle(mappedWidget.geometry[direction]);
            if (elementUnitStyle === 'px') {
                step *= 10;
            }

            newElementValue = (elementUnitValue - step).toFixed(1) + elementUnitStyle;
            currentRuntimeEditor._setGeometry(elementId, mappedWidget, elementId,
                'geometry', direction, newElementValue);
        } else {
            let element = document.getElementById(elementId);
            let elementMask: any = document.body.querySelector('[data-widget-id="' + elementId + '"]');

            let offsetTop = element.offsetTop;
            let offsetLeft = element.offsetLeft;

            if (direction === 'top') {
                element.style[direction] = (offsetTop - step) + 'px';
                elementMask.style[direction] = (offsetTop - step) + 'px';
            } else {
                element.style[direction] = (offsetLeft - step) + 'px';
                elementMask.style[direction] = (offsetLeft - step) + 'px';
            }
            currentRuntimeEditor.onInteractEnd(document.getElementById(elementId));
        }
    },
    /**
     * Get the closest bigger number in array of numbers
     * @param array {Array} - array of numbers to check in
     * @param closestTo {Number} - the initial number
     * @returns {number} - the closest to the closestTo number
     */
    biggerClosest: function biggerClosest(array, closestTo) {
        // Get the highest number in array in case it match nothing.
        // If the array is empty, it will be set to 0
        var closest = array.sort(function (a, b) {
                return a - b;
            })[array.length - 1] || 0;

        for (var i = 0; i < array.length; i++) {
            if (array[i] > closestTo && array[i] < closest) {
                closest = array[i];
            }
        }

        return closest;
    },
    /**
     * Get the closest smaller number in array of numbers
     * @param array {Array} - array of numbers to check in
     * @param closestTo {Number} - the initial number
     * @returns {number} - the closest to the closestTo number
     */
    smallerClosest: function smallerClosest(array, closestTo) {
        // Get the smallest number in array in case it match nothing.
        // If the array is empty, it will be set to 0
        var closest = array.sort(function (a, b) {
                return a - b;
            })[0] || 0;

        for (var i = 0; i < array.length; i++) {
            if (array[i] < closestTo && array[i] > closest) {
                closest = array[i];
            }
        }

        return closest;
    },
    /**
     * Remove duplicated elements from an array
     * @param array {Array}
     * @returns {Array}
     */
    removeDuplicates: function removeDuplicates(array) {
        var uniqueArray = [];

        $.each(array, function (i, el) {
            if ($.inArray(el, uniqueArray) === -1) {
                uniqueArray.push(el);
            }
        });

        return uniqueArray;
    },
    /**
     * Count the number of decimals in a number
     * @param value {string}
     * @returns {number} - the total count of decimals
     */
    countDecimals: function (value) {
        var decimals = value.split('.')[1];

        return decimals ? decimals.length : 0;
    },

    getFileAndPath(url: string) {
        const path = url.replace(/[^\/\\]*$/, '').replace(/'|"/gi, '');
        const filename = url.replace(/^.*[\\\/]/, '').replace(/'|"/gi, '');

        return {
            path,
            filename
        };
    },

    /**
     * Checks and convert image paths to handle different file levels
     * @param imagePath - Image path, relative from uiresources
     * @param scenePath - Current scene root level
     * @param fullURL - Image path with filename
     * @returns {String} - converted image path with filename, relative to root scene level
     */
        pathHandler(imagePath: string, scenePath: string, fullURL: string): string {
        // if the image has absolute path convert to relative
        let path = fullURL.replace('coui://uiresources/', '').replace(/(\/\/+)/g, '/');
        const image = imagePath.replace('coui://uiresources/', '');

        const scene = scenePath.replace(/\\/g, '/');
        const regExp = new RegExp(`^${scene}`);

        // check if the image is in the root folder
        if (image.match(regExp)) {
            // remove root scene path and filename
            path = path.replace(scene, '').replace(/[^\/\\]*$/, '');
        } else {
            let sceneFolders = scene.split('/');
            let imageFolders = image.split('/');
            const imageFolderLen = imageFolders.length;
            let upFolders = 0;

            for (let i = 0; i < imageFolderLen; i++) {
                if (sceneFolders[0] !== imageFolders[0]) {
                    path = '../'.repeat(upFolders || sceneFolders.length - 1) + imageFolders.join('/');
                    break;
                } else if (sceneFolders[0] === imageFolders[0]) {
                    imageFolders = imageFolders.slice(i + 1);
                    sceneFolders = sceneFolders.slice(i + 1);
                    upFolders++;
                }
            }
        }

        return path;
    },

    /**
     *  Handle and convert different asset paths
     *  Using it in @openPathHandler
     * @param options
     * @property options.regGroup - path and filename of an asset
     * @property options.path - root scene path
     * @property options.isResponsive - optional parameter used for handling responsive images
     * @property options.isFont - optional parameter used for handling font faces
     * @returns {string}
     */
        pathChanger(options: PathChanger): string {
        let convertedPath = options.regGroup;

        // check if the path string has "../" in it.
        // if it doesn't the image is within the scope of the root folder
        if (!options.regGroup.match(/\.\.\//)) {
            options.regGroup = options.regGroup.replace(/'/g, '');
            convertedPath = `${options.path}${options.regGroup}`;
        } else {
            const upLevel = options.regGroup.split('../').length - 1;
            const imagePath = options.path.split('/');
            const tempPath = options.path.split('/').slice(0, upLevel).join('/');

            options.regGroup = options.regGroup.split('../').join('').replace(/'/g, '');

            if (imagePath.length - 1 === upLevel) {
                convertedPath = options.regGroup;
            } else {
                convertedPath = `${tempPath}/${options.regGroup}`;
            }
        }

        // serve responsive image
        if (options.isResponsive) {
            if (couiEditor.globalEditorInfo.backend === Enums.Backends.Debug ||
                couiEditor.globalEditorInfo.backend === Enums.Backends.Website) {
                return `url('uiresources/${convertedPath}')`;
            } else {
                return `url('coui://uiresources/${convertedPath}')`;
            }
            // serve font face src paths
        } else if (options.isFont) {
            return `'${convertedPath}'`;
            // serve normal non responsive image
        } else {
            return `src="${convertedPath}"`;
        }
    },

    /**
     * Executed on scene open, checks and converts asset paths to handle different root folder levels
     * @param content - HTML content of the scene
     * @param path - scene root path
     * @returns {string}
     */
        openPathHandler(content: string, path: string): string {
        let properPathContent = content;
        const scriptSplitTag = '<script class="global-events">';
        let splitContentByJsScript = properPathContent.split(scriptSplitTag);

        let cleanedContent = splitContentByJsScript[0]

        // change responsive image and font face paths
            .replace(/url\((.*?)\)/gi, (regSelect, regGroup) => {
                // cases where the url comes from data-<event> attribute
                // on the scene element
                // TODO: think of a better implementation
                if (regSelect.match(/&quot;/)) {
                    return regSelect;
                }

                const url = regGroup.replace(/'/g, '');
                const extension = url.split('.').pop();

                // fonts
                if (extension === Enums.extensions.ttf || extension === Enums.extensions.ttf) {
                    return this.pathChanger({regGroup: url, path, isFont: true});

                    // responsive images
                } else {
                    return this.pathChanger({regGroup: url, path, isResponsive: true});
                }
            })
            // change image and external javascript paths
            .replace(/src="(.*?)"/gi, (regSelect, regGroup) => {
                const extension = regGroup.split('.').pop();
                // javascript
                if (extension === Enums.extensions.js) {
                    return this.jsFileHandler(regGroup, path);
                    // non-responsive images
                } else {
                    return this.pathChanger({regGroup, path});
                }
            })
            // change external css files
            .replace(/<link.*href="([coui:\/\/]|.*?)">/gi, (regSelection, regGroup) => {
                const convertedPath = this.assetPathHandler(regGroup, path);
                return `<link rel="stylesheet" type="text/css" class="styles" href="${convertedPath}">`;
            });

        if (splitContentByJsScript[1]) {
            return cleanedContent + scriptSplitTag + splitContentByJsScript[1];
        } else {
            return cleanedContent;
        }
    },
    /**
     * Return the top root folder of a path
     * @param path
     * @returns {string}
     */
        rootFolder(path: string): string {
        return path.split(/[\/\\]/)[0];
    },

    externalFilePathHandler(fullCSSPath: string, scenePath: string): string {
        let path = fullCSSPath.replace('!css', '');
        let convertedScenePath = scenePath.replace(/\\/g, '/');

        const cssPath = this.getFileAndPath(fullCSSPath).path;
        const cssFilename = this.getFileAndPath(fullCSSPath).filename;
        const regExp = new RegExp(`^${convertedScenePath}`);

        let fileFolders = cssPath.split('/');
        let sceneFolders = convertedScenePath.split('/');
        const filePathLength = sceneFolders.length;

        let upFolders = 0;

        if (cssPath.match(regExp)) {
            path = path.replace(scenePath, '');
        } else {
            for (let i = 0; i < filePathLength; i++) {
                if (sceneFolders[i] !== fileFolders[i]) {
                    path = '../'.repeat(upFolders || sceneFolders.length - 1) + fileFolders.join('/') + cssFilename;
                    break;
                } else if (sceneFolders[i] === fileFolders[i]) {
                    fileFolders = fileFolders.slice(i + 1);
                    sceneFolders = sceneFolders.slice(i + 1);
                    upFolders++;
                }
            }
        }

        return path;
    },
    checkFilePaths(styleURL: string, fullURL: string, scenePath: string): boolean {
        const clearedStyleURL = styleURL.replace(/\?+.*/, '');
        const clearedFullURL = fullURL.replace(/\?+.*/, '');
        const upFolders = styleURL.replace(/[^\/\\]*$/, '').split('../').length - 1;
        const pathFolders = scenePath.split('/').slice(0, -1);
        let tempFolders = pathFolders;

        for (let i = 0; i < upFolders; i++) {
            tempFolders = tempFolders.slice(0, -1);
        }

        if (tempFolders.join('/') === '') {
            return (clearedStyleURL.replace(/\.\.\//g, '')) === clearedFullURL;
        } else {
            return ((tempFolders.join('/') + '/' + clearedStyleURL.replace(/\.\.\//g, '')) === clearedFullURL);
        }
    },

    buildPublishPage(sceneDom: any, currentEnvironment: string): IPublishPage {
        var cssDeclarations = '';
        var counter = 0;

        function loopChildren(nodes) {
            $(nodes).each((index, element) => {
                if (element.attributes && element.hasAttribute('style')) {
                    const style = element.getAttribute('style');
                    const className = 'coui-style-' + counter;

                    element.removeAttribute('style');
                    element.removeAttribute('data-type');
                    element.removeAttribute('data-element-type');
                    element.removeAttribute('data-element-selectable');

                    counter++;
                    element.classList.add(className);
                    let props = style;
                    // Coupling all Hummingbird Handling //
                    if (currentEnvironment === 'Hummingbird') {
                        props = props.replace(/(?:flex-flow:|-webkit-flex-flow:)(.*?);/gm, (result1, result2) => {
                            let newResult = result2.trim().split(' ');
                            let flexDirection = 'flex-direction: ' + newResult[0] + ';';
                            let flewWrap = 'flex-wrap: ' + newResult[1] + ';';

                            return flexDirection + ' ' + flewWrap;
                        });
                        props = props.replaceAll('inline-block', 'block');
                        props = props.replaceAll(' -webkit-user-select: none;', '');
                        props = props.replaceAll('-webkit-', '');
                        props = props.replace(/.mask:.*?;/gm, (regSelection) => {
                            let newString: string = ' mask-image: ';

                            newString += regSelection.match(/url\(.*?\)/)[0] + '; mask-position: ';
                            newString += regSelection.match(/\).*?\//)[0]
                                    .replace(') ', '').replace(' /', '') + '; mask-repeat: ';
                            newString += regSelection.match(/(repeat|no-repeat).*?;/)[0];

                            return newString;
                        });
                    }

                    props = props.replaceAll(';', ';\n');
                    cssDeclarations += `.${className} {\n ${props} }\n`;
                }
                if (element.childNodes.length > 0) {
                    loopChildren(element.childNodes);
                }
            });
        }

        loopChildren(sceneDom);

        return {
            cleanedHTML: sceneDom.innerHTML,
            css: cssDeclarations
        };
    },
    jsFileHandler(filePath: string, scenePath: string): string {
        const fullPath = this.assetPathHandler(filePath, scenePath);

        return `src="${fullPath}"`;
    },

    assetPathHandler(fullAssetPath: string, scenePath: string): string {
        const assetPath = this.getFileAndPath(fullAssetPath).path.match(/(\.\.\/)/g);
        const assetFolders = assetPath ? assetPath.length : 0;
        const sceneFolders = scenePath.split('/').filter(Boolean);
        let fullPath = sceneFolders;

        for (let i = 0; i < assetFolders; i++) {
            if (fullPath.length > 0) {
                fullPath = fullPath.slice(0, -1);
            }
        }

        if (fullPath.length > 0) {
            return `${fullPath.join('/')}/${fullAssetPath.replace(/\.\.\//g, '')}`;
        }

        return fullAssetPath.replace(/\.\.\//g, '');
    },
    /**
     * @param strings
     * @returns {Array}
     */
        removeSlashes(strings: string[]): string[] {
        let replaced = [];
        replaced[0] = strings[0].split('/').join('');
        replaced[1] = strings[1].split('\\').join('');
        return replaced;
    },

    /**
     * Get the cursor position of the element
     * @param element {Node}
     * @returns {{start: number, end: number}}
     */
        getTextSelection(element: HTMLInputElement): IGetTextSelection {
        return {
            start: element.selectionStart,
            end: element.selectionEnd,
        };
    },
    /**
     * @param value {string} - url('pesho.png')
     * @returns {string} - pesho.png
     */
        cleanBackgroundUrls(value: string): string {
        let regExp = /\(([\s\S]*)\)/;
        try {
            let clenPath = value.match(regExp)[1].replace('\'', '');
            // shortening longer than 20 char strings //
            if (clenPath.length > 20) {
                var strLength = clenPath.length;
                clenPath = clenPath.substring(strLength - 20, strLength);
                return '...' + clenPath;
            }
            return clenPath;
        } catch (e) {
            return value;
        }
    },

    /**
     * Disable a KendoUI input and reset its value to 0
     * @param property - the "data-property-key" from which the element will be selected
     * @example disableKendoInput('width')
     */
        disableKendoInput(property: string): void {
        $(`input[data-property-key="${property}"]`).attr('disabled', true);
        $(`input[data-property-key="${property}"]`).val(0);
    },

    /**
     * Enable KendoUI input
     * @param property {string} - the "data-property-key" from which the element will be selected
     * @example enableKendoInput('width');
     */
        enableKendoInput(property: string): void {
        $(`input[data-property-key="${property}"]`).attr('disabled', false);
    },

    /**
     * Build a font-face rule from a font, located under uiresources
     * @param fontPath {string} - the path to the font, based on scene location
     * @returns {string} - the CSS font-face rule
     */
        buildFontCss(fontPath: string) {
        const fileName = fontPath.replace(/\.[^/.]+$/, '').replace(/^.*[\\\/]/, '');

        return '@font-face {\n' +
            `\tfont-family: ${fileName};\n` +
            `\tsrc: url(${fontPath});\n` +
            `}\n`;
    },

    fontIsPresent(fontName, enabledFonts: string[]) {
        return enabledFonts
        // remove file extension and path from enabled fonts
            .map((font) => font.replace(/\.[^/.]+$/, '').replace(/^.*[\\\/]/, ''))
            // add inherit to the fonts array
            .concat(['inherit'])
            .includes(fontName);
    },

    transformMaskSize(maskSize) {
        let property = maskSize.split(': ')[0];
        let value = maskSize.split(': ')[1].replace(';', '');

        if (value !== 'auto') {
            value = this.maskSizeFormat(value);
        }

        return `${property}: ${value};`;
    },

    maskSizeFormat(value) {
        const hasMultipleValues = value.match(/\s/g);

        if (!hasMultipleValues && value !== 'contain' &&
            value !== 'cover' && value !== 'inherit') {
            return value + ' ' + value;
        }

        return value;
    },

    getHBScaleProperties(widget) {
        let scaleX = widget.transform.scaleX || 1;
        let scaleY = widget.transform.scaleY || 1;

        return `${scaleX}, ${scaleY}`;
    },

    /**
     * Checks and returns the value from a mappedWidget object.
     * In case the property doesn't exist in the widget,
     * the function returns the default value
     * Used for: -webkit-filter, transform, transform-origin
     * User in: getWidgetState
     * @param widget
     * @param prop {string} - the property looked for
     * @param propGroup {string} - the parent object of prop
     * @param defaultValue {string} - the default property value
     * @returns {string}
     */
        getWidgetComplexProp(widget: IWidget, prop: string, propGroup: string, defaultValue: string): string {
        if (widget[propGroup]) {
            return widget[propGroup][prop] || defaultValue;
        }

        return defaultValue;
    },

    /**
     * Return widget's property value
     * @param widget
     * @param prop
     * @param propGroup
     */
        getWidgetState(widget: IWidget, prop: string, propGroup: string) {
        let value;
        const defaultPropGroups = ['styles', 'geometry'];

        if (defaultPropGroups.includes(propGroup)) {
            return widget[propGroup][prop];
        } else if (propGroup === '-webkit-filter') {
            return this.getWidgetComplexProp(widget, prop, propGroup, Enums['-webkit-filterDefaultValues'][prop]);
        } else if (propGroup === 'transform') {
            return this.getWidgetComplexProp(widget, prop, propGroup, Enums.TransformDefaultValue[prop]);
        } else if (propGroup === 'transform-origin') {
            return this.getWidgetComplexProp(widget, prop, propGroup, 0);
        } else if (propGroup === 'dataBindings') {
            prop = this.translateDataBindNames(prop, false);
            return this.getWidgetComplexProp(widget, prop, propGroup, '');
        }

        switch (prop) {
            case 'id':
                value = widget.id;
                break;
            case 'className':
                value = widget.className;
                break;
            case 'data-l10n-id':
                value = widget.attrs['data-l10n-id'];
                break;
            case 'src':
                if (propGroup === 'attrs') {
                    value = widget.attrs.src;
                }
                break;
            case 'min':
            case 'max':
            case 'value':
            case 'step':
                if (propGroup === 'attrs') {
                    value = widget.attrs[prop];
                }
                break;
            case 'placeHolder':
                value = widget.attrs.placeHolder;
                break;
            default:
                value = '';
        }

        return value;
    },

    /**
     * Checks if the widgetkit extension is .component
     * @param {IWidget} widget
     * @returns {boolean}
     *
     * @example widget.html -> false
     * @example widget.component -> true
     */
    isComponent(widget: IWidget): boolean {
        if (!widget.widgetkit) {
            return false;
        }

        return /(?:\.([^.]+))?$/.exec(widget.widgetkit)[1] === 'component';
    }
};

