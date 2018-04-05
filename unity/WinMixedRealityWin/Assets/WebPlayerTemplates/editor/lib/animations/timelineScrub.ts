/**
 * Created by Deian on 12/7/2016.
 */

import helpers from '../function_helpers';
import unitsConvertor from 'scripts/helpers/units_conversion';
import Enums from '../enums';
import couiEditor from '../../scripts/main';

export class TimelineScrub {

    private runtimeEditor;
    private currentKeyframeStack = {};
    private previousKeyframeStack = {};

    constructor(runtimeEditor: any) {
        this.runtimeEditor = runtimeEditor;
    }

    /**
     * @function Function returning the keyframe values from
     * the animations regardless of the nesting dept.
     * @memberOf module:lib/animations/timelineScrub
     * @param {any} obj - string style value of the lower keyframe
     * @returns any
     */
    returnKeyframeValues(obj: any): any {
        let returnedObj = $.extend(true, {}, obj);
        let firstKey = parseFloat(Object.keys(returnedObj)[0]);
        if (typeof firstKey === 'number' && !isNaN(firstKey)) {
            return returnedObj;
        } else {
            returnedObj = returnedObj[Object.keys(returnedObj)[0]];
            return this.returnKeyframeValues(returnedObj);
        }
    }

    /**
     * @function Function getting all the keyframes in between the timeframe
     * from all the keyframes in the global animations.
     * @param {number} timeBefore - the start time of the timeframe.
     * @param {number} timeAfter - the end time of the timeframe.
     * @param {IKeyframeData[]} keyframes - the keyframes for for the current class and group.
     * @memberOf module:lib/animations/timelineScrub
     * @returns IKeyframeData[]
     */
    findKeyFramesBetweenTime(timeBefore: number, timeAfter: number, keyframes: IKeyframeData[]): number[] {
        let k: number[] = [];
        let keyframeTime: any;
        for (keyframeTime in keyframes) {
            keyframeTime = parseFloat(keyframeTime);
            if (keyframeTime >= timeBefore && keyframeTime <= timeAfter ||
                keyframeTime <= timeBefore && keyframeTime >= timeAfter) {
                if (keyframeTime !== timeBefore) {
                    k.push(keyframeTime);
                }
            }
        }
        if (timeAfter < timeBefore) {
            k.reverse();
        }
        return k;
    }

    /**
     * @function Function rendering the Keyframes in the timeframe.
     * @param {number} timeBefore - the start time of the timeframe.
     * @param {number} timeAfter - the end time of the timeframe.
     * @memberOf module:lib/animations/timelineScrub
     * @returns void
     */
    renderKeyframesInTime(timeBefore: number, timeAfter: number): void {
        if (timeBefore === timeAfter) {
            return;
        }

        let foundKeyFramesBetweenTimes: number[] = [];
        let keyframeGroup = '';
        if (!this.runtimeEditor.iframe) {
            let animationWidgets = this.runtimeEditor.scene.animations;
            for (let widget in animationWidgets) {
                let id = animationWidgets[widget].id;
                for (let keyframeGroups in animationWidgets[widget]) {
                    for (let group in animationWidgets[widget][keyframeGroups].keyframes) {
                        keyframeGroup = group;
                        for (let property in animationWidgets[widget][keyframeGroups].keyframes[group]) {
                            foundKeyFramesBetweenTimes = foundKeyFramesBetweenTimes
                                .concat(this.findKeyFramesBetweenTime(timeBefore, timeAfter,
                                    animationWidgets[widget][keyframeGroups].keyframes[group][property]));
                        }
                    }
                }

                foundKeyFramesBetweenTimes.push(timeAfter);
                foundKeyFramesBetweenTimes = helpers.removeDuplicates(foundKeyFramesBetweenTimes);
                if (timeBefore > timeAfter) {
                    foundKeyFramesBetweenTimes.sort((a, b) => {
                        return b - a;
                    });
                } else {
                    foundKeyFramesBetweenTimes.sort((a, b) => {
                        return a - b;
                    });
                }

                for (var i = 0; i < foundKeyFramesBetweenTimes.length; i++) {
                    this.currentKeyframeStack[id] = {};
                    this.performTimelineScrub(id, foundKeyFramesBetweenTimes[i]);
                    this.executeStackedStyleChanges(keyframeGroup);
                }
            }

        }
    }

    /**
     * @function Function executing the style changes and clearing
     * the current stack and replacing the previews for filtering.
     * @memberOf module:lib/animations/timelineScrub
     * @returns void
     */
    executeStackedStyleChanges(group?: string): void {
        for (let id in this.currentKeyframeStack) {
            let domElement = document.getElementById(id);
            for (let styleType in this.currentKeyframeStack[id]) {
                domElement.style[styleType] = this.currentKeyframeStack[id][styleType];
            }
        }
        this.previousKeyframeStack = $.extend(true, {}, this.currentKeyframeStack);
        this.currentKeyframeStack = {};
    }

    /**
     * @function Function updating the current style stack.
     * @param {string} id - string id info of the widget.
     * @param {string} styleType - the styleType of the value.
     * @param {string} value - the style value.
     * @memberOf module:lib/animations/timelineScrub
     * @returns void
     */
    updateStackedStyleChanges(options: IScrubOptions, property?: string): void {
        let appliedStyle = options.styleType === 'rotate' ? 'transform' : options.styleType;

        let previousExists: boolean = (this.previousKeyframeStack[options.id] !== undefined &&
            this.previousKeyframeStack[options.id][appliedStyle] === options.newValue
        );

        if (!previousExists) {
            if (this.currentKeyframeStack[options.id] === undefined) {
                this.currentKeyframeStack[options.id] = {};
            }
            if (!this.currentKeyframeStack[options.id][appliedStyle]) {
                this.currentKeyframeStack[options.id][appliedStyle] = '';
            }

            // concatenate filter properties without dropShodow
            if (options.propGroup === '-webkit-filter') {
                if (!(property === 'dropShadowBlur' ||
                        property === 'dropShadowX' ||
                        property === 'dropShadowY' ||
                        property === 'dropShadowColor'
                    )) {
                    this.currentKeyframeStack[options.id][appliedStyle] += property + '(' + options.newValue.trim() + ')';
                }
            } else {
                this.currentKeyframeStack[options.id][appliedStyle] = options.newValue;
            }

        }
    }

    /**
     * @function Function adjusting the inset style value of boxshadow properties in the box shadow style
     * @memberOf module:lib/animations/timelineScrub
     * @param {string} lowerKeyframeValue - string style value of the lower keyframe
     * @param {string} higherKeyframeValue - string style value of the higher keyframe
     * @param {number} percentageShift - percentage value for the current time in between the frames
     * @param {string} id - string id info of the widget
     * @returns {string} tempObject - adjusted backGround size styles
     **/
    adjustBackgroundSize(lowerKeyframeValue: string, higherKeyframeValue: string,
                         percentageShift: number, id: string): { 'appliedValue': string, displayedValue: string } {

        let tempValueLow = lowerKeyframeValue.split(' ');
        let tempValueHigh = higherKeyframeValue.split(' ');

        let tempValueLow1 = parseFloat(unitsConvertor.convertUnitsToPixel(id, tempValueLow[0], 'backgroundSize'));
        let tempValueHigh1 = parseFloat(unitsConvertor.convertUnitsToPixel(id, tempValueLow[1], 'backgroundSize'));

        let tempValueLow2 = parseFloat(unitsConvertor.convertUnitsToPixel(id, tempValueHigh[0], 'backgroundSize'));
        let tempValueHigh2 = parseFloat(unitsConvertor.convertUnitsToPixel(id, tempValueHigh[1], 'backgroundSize'));

        let applied1 = tempValueLow1 + (tempValueLow2 - tempValueLow1) / 100 * percentageShift;
        let applied2 = tempValueHigh1 + (tempValueHigh2 - tempValueHigh1) / 100 * percentageShift;

        let lowerStyle;
        let higherStyle;

        if (percentageShift < 50) {
            lowerStyle = helpers.getUnitStyle(tempValueLow[0]);
            higherStyle = helpers.getUnitStyle(tempValueLow[1]);
        } else {
            lowerStyle = helpers.getUnitStyle(tempValueHigh[0]);
            higherStyle = helpers.getUnitStyle(tempValueHigh[1]);
        }

        let displayed1 = unitsConvertor.convertPixelToUnit(id, applied1, lowerStyle, 'backgroundSize');
        let displayed2 = unitsConvertor.convertPixelToUnit(id, applied2, higherStyle, 'backgroundSize');

        let tempObject = {
            'appliedValue': applied1 + 'px ' + applied2 + 'px',
            'displayedValue': displayed1 + ' ' + displayed2
        };
        return tempObject;
    }

    /**
     * @function Function adjusting the inset style value of boxshadow properties in the box shadow style
     * @memberOf module:lib/animations/timelineScrub
     * @param {string} lowerKeyframeValue - string style value of the lower keyframe
     * @param {string} higherKeyframeValue - string style value of the higher keyframe
     * @param {string} newValue - string style value for the current time
     * @param {number} percentageShift - percentage value for the current time in between the frames
     * @returns {string} adjusted boxShadow styles
     */
    adjustBoxShadowInset(lowerKeyframeValue: string, higherKeyframeValue: string,
                         newValue: string, percentageShift: number): string {
        let lowKeyframeArray = lowerKeyframeValue.split(' ');
        let highKeyframeArray = higherKeyframeValue.split(' ');
        let newArray = newValue.split(' ');

        if (percentageShift < 50) {
            newArray[0] = lowKeyframeArray[0] !== 'remove' ? lowKeyframeArray[0] : highKeyframeArray[0];
        } else {
            newArray[0] = highKeyframeArray[0] !== 'remove' ? highKeyframeArray[0] : lowKeyframeArray[0];
        }

        return newArray.join(' ').replace('remove', '').trim();
    }

    /**
     * @function Function creating the skrub value for the current anymation=
     * @memberOf module:lib/animations/timelineScrub
     * @param {array} mixStringArray - array containing the split value for the hey keyframe
     * @param {string} lowerKeyframeValue - string style value of the lower keyframe
     * @param {string} higherKeyframeValue - string style value of the higher keyframe
     * @param {number} percentageShift - percentage value for the current time in between the frames
     * @param {string} styleType - string style value for the current property
     * @returns {string} newValue - mixed string style
     */
    modulateStyleValue(mixStringArray: string[], lowerKeyframeSplitters: string[],
                       higherKeyframeSplitters: string[], percentageShift: number, styleType: string): string {

        let newNumberArray = [];

        for (let i = 0; i < lowerKeyframeSplitters.length; i++) {
            let lowerValue = parseFloat(lowerKeyframeSplitters[i]);
            let higherValue = parseFloat(higherKeyframeSplitters[i]);
            newNumberArray[i] = lowerValue + (higherValue - lowerValue) / 100 * percentageShift;
        }
        let counter = 0;
        let newValue = mixStringArray.map(function (element) {
            if (!isNaN(parseInt(element)) || element.indexOf('#') === 0) {
                switch (styleType) {
                    case 'backgroundSize':
                        return newNumberArray[counter++].toFixed(2);
                    case '-webkit-mask-size':
                        return newNumberArray[counter++].toFixed(2);
                    case 'boxShadowProperties':
                        return newNumberArray[counter++].toFixed(2);
                    case 'filterProperties':
                        return newNumberArray[counter++].toFixed(2);
                    case 'borderColor':
                        return newNumberArray[counter++].toFixed(2);
                    case 'backgroundColor':
                        return newNumberArray[counter++].toFixed(2);
                    case 'transform':
                        return newNumberArray[counter++].toFixed(1);
                    case 'transform-origin':
                        return newNumberArray[counter++].toFixed(1);
                    case 'perspective-origin':
                        return newNumberArray[counter++].toFixed(1);
                    case 'color':
                        if (counter === 3) {
                            return newNumberArray[counter++].toFixed(2);
                        }
                        return newNumberArray[counter++].toFixed(0);
                    case '-webkit-mask-position-x':
                        return newNumberArray[0].toFixed(2);
                    case '-webkit-mask-position-y':
                        return newNumberArray[0].toFixed(2);
                    case 'borderWidth':
                        return newNumberArray[0].toFixed(2);
                    case 'borderTopLeftRadius':
                        return newNumberArray[0].toFixed(2);
                    case 'borderTopRightRadius':
                        return newNumberArray[0].toFixed(2);
                    case 'borderBottomLeftRadius':
                        return newNumberArray[0].toFixed(2);
                    case 'borderBottomRightRadius':
                        return newNumberArray[0].toFixed(2);
                    case 'backgroundPositionX':
                        return newNumberArray[0].toFixed(2);
                    case 'backgroundPositionY':
                        return newNumberArray[0].toFixed(2);
                    case 'opacity':
                        return newNumberArray[0].toFixed(2);
                    case 'left':
                        return newNumberArray[0].toFixed(2);
                    case 'top':
                        return newNumberArray[0].toFixed(2);
                    case 'width':
                        return newNumberArray[0].toFixed(2);
                    case 'height':
                        return newNumberArray[0].toFixed(2);
                    case 'scaleX':
                        return newNumberArray[0].toFixed(2);
                    case 'scaleY':
                        return newNumberArray[0].toFixed(2);
                    case 'scaleZ':
                        return newNumberArray[0].toFixed(2);
                    case 'rotate':
                        return newNumberArray[0].toFixed(1);
                    case 'fontSize':
                        return newNumberArray[0].toFixed(1);
                    case 'rotateX':
                        return newNumberArray[0].toFixed(1);
                    case 'rotateY':
                        return newNumberArray[0].toFixed(1);
                    case 'rotateZ':
                        return newNumberArray[0].toFixed(1);
                    case 'translateX':
                        return newNumberArray[0].toFixed(1);
                    case 'translateY':
                        return newNumberArray[0].toFixed(1);
                    case 'translateZ':
                        return newNumberArray[0].toFixed(1);
                    case 'skewX':
                        return newNumberArray[0].toFixed(1);
                    case 'skewY':
                        return newNumberArray[0].toFixed(1);
                    case 'perspective':
                        return newNumberArray[0].toFixed(1);
                    case 'zIndex':
                        return newNumberArray[0].toFixed(0);
                }
            } else {
                return element;
            }
        }).join('');

        return newValue;
    }

    /**
     * @function adsjustForStringStyleValues
     * Function adjusting the string style values
     * @memberOf module:lib/animations/timelineScrub
     * @param {IScrubOptions} options - options for the scrub functionality
     * @returns {IScrubOptions} options - options for the scrub functionality
     **/
    adjustForStringStylesValues(options: IScrubOptions): IScrubOptions {
        let stringMatch = /([-|+|#]?\d*\.?\d+)/g;
        let pastMiddle = options.percentageShift > 50;

        let lowerKeyframeSplitters = options.lowerKeyframeValue.match(stringMatch);
        let higherKeyframeSplitters = options.higherKeyframeValue.match(stringMatch);

        if (lowerKeyframeSplitters === null ||
            higherKeyframeSplitters === null ||
            options.styleType === 'fontWeight' ||
            options.styleType === 'fontStyle') {
            options.newValue = pastMiddle ? options.higherKeyframeValue : options.lowerKeyframeValue;
            options.originalValue = options.newValue;
        } else if (options.propGroup === '-webkit-filter') {
            const regexFilter = /\((.*?)\)/;
            let result: any = options.lowerKeyframeValue.match(regexFilter);
            if (result !== null) {
                options.originalValue = result[1];
            }

            let mixString = options.lowerKeyframeValue.split(stringMatch);
            options.newValue = this.modulateStyleValue(mixString, lowerKeyframeSplitters,
                higherKeyframeSplitters, options.percentageShift, options.styleType);
        } else {
            let mixString = options.lowerKeyframeValue.split(stringMatch);
            options.newValue = this.modulateStyleValue(mixString, lowerKeyframeSplitters,
                higherKeyframeSplitters, options.percentageShift, options.styleType);
            if (options.lowerKeyframeStyle.trim() !== '' &&
                options.higherKeyframeStyle.trim() !== '' &&
                options.propGroup !== 'transform' &&
                options.propGroup !== 'transform-origin' &&
                options.styleType !== 'perspective-origin') {
                if (pastMiddle && !helpers.isBackgroundPositionString(options.originalValue)) {
                    options.originalValue = unitsConvertor.convertPixelToUnit(options.id, options.newValue,
                        options.higherKeyframeStyle, options.styleType);
                } else if (!helpers.isBackgroundPositionString(options.originalValue)) {
                    options.originalValue = unitsConvertor.convertPixelToUnit(options.id, options.newValue,
                        options.lowerKeyframeStyle, options.styleType);
                }
            } else {
                options.originalValue = options.newValue;
            }
        }

        return options;
    }

    /**
     * @function adjustForBackgroundPosition
     * Function adjusting styles to be used in scrubing.
     * @memberOf module:lib/animations/timelineScrub
     * @param {IScrubOptions} options - options for the scrub functionality
     * @returns {IScrubOptions} options - options for the scrub functionality
     **/
    adjustForBackgroundPosition(options: IScrubOptions): IScrubOptions {
        let pastMiddle = options.percentageShift > 50;

        if (options.styleType === 'backgroundPositionX' || options.styleType === 'backgroundPositionY' ||
            options.styleType === '-webkit-mask-position-y' || options.styleType === '-webkit-mask-position-x') {

            options.originalValue = pastMiddle ? options.higherKeyframeValue : options.lowerKeyframeValue;

            if (options.lowerKeyframeValue === 'center' ||
                options.lowerKeyframeValue === 'bottom' ||
                options.lowerKeyframeValue === 'right') {
                options.lowerKeyframeValue =
                    this.runtimeEditor._fixBackgroundPercentage(options.lowerKeyframeValue,
                        options.id, options.styleType);
            } else {
                options.lowerKeyframeValue = helpers.getStringPropertyPercent(options.lowerKeyframeValue);
            }

            if (options.higherKeyframeValue === 'center' ||
                options.higherKeyframeValue === 'bottom' ||
                options.higherKeyframeValue === 'right') {
                options.higherKeyframeValue =
                    this.runtimeEditor._fixBackgroundPercentage(options.higherKeyframeValue,
                        options.id, options.styleType);
            } else {
                options.higherKeyframeValue = helpers.getStringPropertyPercent(options.higherKeyframeValue);
            }
        }

        return options;
    }

    /**
     * @function adjustGroupedStyles
     * Function adjusting aditional grouped styles {transforms, geometry, etc...} to be used in scrubing.
     * @memberOf module:lib/animations/timelineScrub
     * @param {IScrubOptions} options - options for the scrub functionality
     * @returns {IScrubOptions} options - options for the scrub functionality
     **/
    adjustGroupedStyles(options: IScrubOptions, originalProperty: string): IScrubOptions {
        let tabBelongsToWidget = false;

        if (this.runtimeEditor.currentElementsSelection.length === 1 &&
            this.runtimeEditor.currentElementsSelection[0] === options.id) {
            tabBelongsToWidget = true;
        }

        if (tabBelongsToWidget &&
            options.styleType !== 'transform-origin' &&
            options.styleType !== 'perspective-origin' &&
            !helpers.isTransformProperty(options.styleType) &&
            options.styleType !== '-webkit-mask-size' &&
            options.styleType !== 'filterProperties'
        ) {
            let newUnitValue: string = helpers.shortenLongValue(options.originalValue);
            this.runtimeEditor.setPropertiesBarValue(options.id, options.propGroup,
                options.styleType, newUnitValue);
        }

        if (options.styleType === 'left' || options.styleType === 'top' || options.styleType === 'width' ||
            options.styleType === 'height' || options.styleType === 'fontSize') {
            let currentUnits = helpers.getUnitStyle(options.newValue);
            if (currentUnits !== 'px' && currentUnits !== '%') {
                options.newValue = unitsConvertor.convertUnitsToPixel(options.id, options.newValue);
            }
        } else if (options.styleType === 'filterProperties') {
            options.styleType = '-webkit-filter';
            if (tabBelongsToWidget) {
                this.runtimeEditor.setFilterInput(options.styleType, originalProperty,
                    options.newValue);
            }
        } else if (options.styleType === 'boxShadowProperties') {
            options.styleType = 'boxShadow';
            options.newValue = this.adjustBoxShadowInset(options.lowerKeyframeValue, options.higherKeyframeValue,
                options.newValue, options.percentageShift);
            if (tabBelongsToWidget) {
                let boxShadowProps = helpers.getValuesBoxshadow(options.newValue);
                for (let h = 0; h < boxShadowProps.length - 1; h += 2) {
                    this.runtimeEditor.setBoxShadowInput(
                        options.styleType, boxShadowProps[h], boxShadowProps[h + 1]
                    );
                }
            }
        } else if (options.styleType === 'rotate') {
            options.originalValue = options.newValue;
            options.newValue = 'rotate(' + options.newValue + ')';
        } else if (options.styleType === 'backgroundSize') {
            let tempValue = this.adjustBackgroundSize(
                options.lowerKeyframeValue, options.higherKeyframeValue, options.percentageShift, options.id
            );
            options.newValue = tempValue.appliedValue;
            if (tabBelongsToWidget) {
                this.runtimeEditor.selectKendoDropdownItem(
                    'styles', 'backgroundSize', tempValue.displayedValue
                );
            }
        } else if (options.styleType === '-webkit-mask-size') {
            let newSplitValue = options.newValue.split(' ');
            if (newSplitValue.length > 1) {
                this.runtimeEditor.setPropertiesBarValue(options.id, options.propGroup,
                    '-webkit-mask-sizeWidth', newSplitValue[0]);
                this.runtimeEditor.setPropertiesBarValue(options.id, options.propGroup,
                    '-webkit-mask-sizeHeight', newSplitValue[1]);
            } else {
                this.runtimeEditor.selectKendoDropdownItem('styles', options.styleType, options.newValue);
            }
        } else if (helpers.isTransformProperty(options.styleType) ||
            options.styleType === 'transform-origin' ||
            options.styleType === 'perspective-origin') {
            options = this.adjustTransformStyles(options);
        }

        return options;
    }

    /**
     * @function adjustTransformStyles
     * Function adjusting the transform styles to be used in scrubing.
     * @memberOf module:lib/animations/timelineScrub
     * @param {IScrubOptions} options - options for the scrub functionality
     * @returns {IScrubOptions} options - options for the scrub functionality
     **/
    adjustTransformStyles(options: IScrubOptions): IScrubOptions {
        options.originalValue = 'edited_transform';

        let properties = {};
        let keys;
        let propValues;

        if (helpers.isTransformProperty(options.styleType)) {
            keys = [options.styleType];
            properties[options.styleType] = [options.newValue];
        } else if (options.styleType === 'perspective-origin') {
            propValues = options.newValue.split(' ');
            keys = ['perspective-origin-x', 'perspective-origin-y'];
            properties = {
                'perspective-origin-x': propValues[0],
                'perspective-origin-y': propValues[1]
            };
        } else {
            propValues = options.newValue.split(' ');
            keys = ['transform-origin-x', 'transform-origin-y'];
            properties = {
                'transform-origin-x': propValues[0],
                'transform-origin-y': propValues[1]
            };
        }

        for (let i = 0; i < keys.length; i++) {
            const propKey = keys[i];
            const value = properties[propKey];
            const widget = this.runtimeEditor.mappedWidgets[options.id].widget;
            if (options.propGroup === 'transform-origin') {
                this.runtimeEditor._setTransformOrigin(null, widget, widget.id, options.propGroup,
                    propKey, value[0]);
            } else if (options.styleType === 'perspective-origin') {
                this.runtimeEditor._setStyles(null, widget, widget.id, options.propGroup,
                    propKey, value[0]);
            } else {
                this.runtimeEditor._setTransform(null, widget, widget.id, options.propGroup,
                    options.styleType, value[0]);
            }

        }

        return options;
    }

    /**
     * @function adjustTransformStyles
     * Function adjusting the unit styles to be used in scrubing (to pixel conversion).
     * @memberOf module:lib/animations/timelineScrub
     * @param {IScrubOptions} options - options for the scrub functionality
     * @returns {IScrubOptions} options - options for the scrub functionality
     **/
    adjustForUnitConversion(options: IScrubOptions): IScrubOptions {
        let valueTypes = ['pt', 'rem', 'vh', 'vw', 'vmax', 'vmin', '%'];

        if (valueTypes.indexOf(options.lowerKeyframeStyle) !== -1 &&
            options.styleType !== 'backgroundSize' &&
            options.styleType !== '-webkit-mask-size' &&
            options.styleType !== 'transform-origin' &&
            options.styleType !== 'perspective-origin') {
            options.lowerKeyframeValue =
                unitsConvertor.convertUnitsToPixel(options.id, options.lowerKeyframeValue, options.styleType);
        }

        if (valueTypes.indexOf(options.higherKeyframeStyle) !== -1 &&
            options.styleType !== 'backgroundSize' &&
            options.styleType !== '-webkit-mask-size' &&
            options.styleType !== 'transform-origin' &&
            options.styleType !== 'perspective-origin') {
            options.higherKeyframeValue =
                unitsConvertor.convertUnitsToPixel(options.id, options.higherKeyframeValue, options.styleType);
        }

        return options;
    }

    /**
     * @function Function that sets higher and lower keyframes filter values to a string
     * @memberOf module:lib/animations/timelineScrub
     * @param options {IScrubOptions}
     * @param keyframeProperties {Object}
     * @param time {Object} - lower and higher keyframe time
     * @returns options {IScrubOptions}
     * */
    adjustForFilterValues(options: IScrubOptions, keyframeProperties, time): IScrubOptions {
        options.lowerKeyframeValue =
            helpers.buildFilterProps(keyframeProperties[time.lower], false);
        options.higherKeyframeValue =
            helpers.buildFilterProps(keyframeProperties[time.higher], false);

        return options;
    }

    /**
     * @function Function handling the timeline scrub.
     * @memberOf module:lib/animations/timelineScrub
     * @param {string} id - id of the widget to prepare for style changes.
     * @param {number} currentTime - the current time of the pinhead in milliseconds
     * @param {string} className(optional) - animation class name
     * @param {boolean} saveExport(optional) - if its true, return array of reordered interpolated keyframes of transforms or filters animations
     * @param {string} propertyType(optional) - needed if saveExport its true - can be transforms or filters
     * @returns {void} | {Array<Object}
     */
    performTimelineScrub(id: string, currentTime: number, className?: string, saveExport = false, propertyType?: string): void | Array<Object> {
        let currentAnimation;
        let exportedValues = [];

        if (className) {
            currentAnimation = this.runtimeEditor.scene.animationClasses[className];
        } else {
            let animationsName = this.runtimeEditor.scene.animations[id];
            if (!animationsName) {
                return;
            }
            currentAnimation = this.runtimeEditor.scene.animations[id][animationsName.className];
            if (!currentAnimation) {
                return;
            }
        }

        let keyframes = currentAnimation.keyframes;
        let keyframesNames = Object.keys(keyframes);

        // if doesn't have id comes from export of the scene and we need to calculate only transforms
        if (id === null) {
            keyframesNames = [propertyType];
        }

        // loop all keyframe property names
        for (let m = 0; m < keyframesNames.length; m++) {
            const keyframeProperty = keyframes[keyframesNames[m]];
            const keyframesLen = Object.keys(keyframeProperty).length;
            let hasDropShadowToExport: boolean = false;
            let originalProperty;
            let options: IScrubOptions = {
                id: id,
                styleType: '',
                newValue: '',
                propGroup: '',
                originalValue: '',
                lowerKeyframeValue: '',
                lowerKeyframeStyle: '',
                higherKeyframeValue: '',
                higherKeyframeStyle: '',
                percentageShift: 0
            };

            // prepare to concatenate dropShadow properties at the and of the string if it has
            if (keyframeProperty['dropShadowX'] ||
                keyframeProperty['dropShadowY'] ||
                keyframeProperty['dropShadowColor'] ||
                keyframeProperty['dropShadowBlur']) {
                hasDropShadowToExport = true;
            }

            // loop all keyframes in time for each keyframe properties
            for (let n = 0; n < keyframesLen; n++) {

                let newInstanceOfKeyframeProperty = {};
                newInstanceOfKeyframeProperty[Object.keys(keyframeProperty)[n]] = keyframeProperty[Object.keys(keyframeProperty)[n]];

                let keyframeProperties = this.returnKeyframeValues(newInstanceOfKeyframeProperty);
                let keyframeTimes: number[] = Object.keys(keyframeProperties).map(function (element) {
                    return parseFloat(element);
                });

                let higherKeyframeTime;
                let lowerKeyframeTime;

                if (keyframeTimes.indexOf(currentTime) !== -1) {
                    higherKeyframeTime = lowerKeyframeTime = currentTime;
                } else {
                    higherKeyframeTime = helpers.biggerClosest(keyframeTimes, currentTime);
                    lowerKeyframeTime = helpers.smallerClosest(keyframeTimes, currentTime);
                }

                if (saveExport ||
                    (lowerKeyframeTime <= currentTime && higherKeyframeTime >= currentTime) ||
                    keyframeProperties[lowerKeyframeTime].group === '-webkit-filter') {

                    options.percentageShift =
                        ((currentTime - lowerKeyframeTime) / (higherKeyframeTime - lowerKeyframeTime)) * 100;

                    options.percentageShift = isFinite(options.percentageShift) ? options.percentageShift : 100;

                    options.propGroup = keyframeProperties[lowerKeyframeTime].group;
                    options.styleType = keyframeProperties[lowerKeyframeTime].property;
                    originalProperty = options.styleType;

                    options.lowerKeyframeValue = keyframeProperties[lowerKeyframeTime].values[0];
                    options.higherKeyframeValue = keyframeProperties[higherKeyframeTime].values[0];

                    let filterProperty = Enums['-webkit-filterDefaultValues'][options.styleType];
                    if (options.propGroup === '-webkit-filter' && filterProperty !== 'undefined') {
                        options.styleType = 'filterProperties';
                        options = this.adjustForFilterValues(options, keyframeProperties,
                            {lower: lowerKeyframeTime, higher: higherKeyframeTime});
                    }

                    options.lowerKeyframeValue = helpers.convertColorValues(options.lowerKeyframeValue);
                    options.higherKeyframeValue = helpers.convertColorValues(options.higherKeyframeValue);

                    options = this.adjustForBackgroundPosition(options);

                    options.lowerKeyframeStyle = helpers.getUnitStyle(options.lowerKeyframeValue);
                    options.higherKeyframeStyle = helpers.getUnitStyle(options.higherKeyframeValue);

                    options = this.adjustForUnitConversion(options);

                    options = this.adjustForStringStylesValues(options);

                    options.newValue = helpers.stylesValueNormalizer(options.newValue);
                    options.originalValue = helpers.stylesValueNormalizer(options.originalValue);

                    // in case we are scrubbing filter group
                    // special case to build filter dropShadow property and apply it at the end of whole filter string
                    if (options.propGroup === '-webkit-filter') {
                        helpers.buildDropShadowFiltersProps(keyframeProperties[lowerKeyframeTime], options.newValue);
                    }

                    if (id !== null) {
                        options = this.adjustGroupedStyles(options, originalProperty);
                    }

                    if (options.originalValue !== 'edited_transform' &&
                        id !== null) {
                        this.updateStackedStyleChanges(options, keyframeProperties[higherKeyframeTime].property);
                    }
                    if (saveExport) {
                        // skip dropshadow properties when build the properties runtime
                        if (originalProperty !== 'dropShadowX' &&
                            originalProperty !== 'dropShadowY' &&
                            originalProperty !== 'dropShadowColor' &&
                            originalProperty !== 'dropShadowBlur') {
                            exportedValues.push({
                                key: originalProperty,
                                value: options.newValue
                            });
                        }

                        // concatenate at the end of builded properties dropShadow properties while exporting
                        if ((n + 1) === keyframesLen && hasDropShadowToExport) {
                            exportedValues.push({
                                key: 'dropShadow',
                                value: helpers.buildDropShadowProperty(this.runtimeEditor.runtimeBuildDropShodowFilter)
                            });
                            this.runtimeEditor.runtimeBuildDropShodowFilter = helpers.getBoxShodowDefaults();
                        }
                    } else {
                        this.applyStyleToWidget(options, originalProperty);
                    }
                }
            }
            // concatenate at the end of builded properties dropShadow properties while scrubbing
            if (options.styleType === '-webkit-filter' && !saveExport && hasDropShadowToExport) {
                const runtimeEditor = couiEditor.openFiles[couiEditor.selectedEditor].runtimeEditor;
                const dropShadow = helpers.buildDropShadowProperty(runtimeEditor.runtimeBuildDropShodowFilter);
                this.currentKeyframeStack[id][options.styleType] += dropShadow;
                this.runtimeEditor.runtimeBuildDropShodowFilter = helpers.getBoxShodowDefaults();
            }
        }
        if (saveExport) {
            return exportedValues;
        }
    }

    /**
     * @function Function applying the update scrub properties to the widget.
     * @memberOf module:lib/animations/timelineScrub
     * @param {IScrubOptions} options - options for the scrub functionality
     * @returns {void}
     */
    applyStyleToWidget(options: IScrubOptions, originalProperty: string) {
        if (options.id === null) {
            return;
        }

        const newUnitValue: string = helpers.shortenLongValue(options.originalValue);

        if (options.propGroup === 'backgroundColor') {
            this.runtimeEditor.mappedWidgets[options.id]
                .widget['background'] = newUnitValue;
        } else if (options.styleType === '-webkit-mask-size') {
            let newWebkitProps: string[] = options.newValue.split(' ');
            if (newWebkitProps.length > 1) {
                this.runtimeEditor.mappedWidgets[options.id]
                    .widget[options.propGroup]['-webkit-mask-size'] = 'auto';
                this.runtimeEditor.mappedWidgets[options.id]
                    .widget[options.propGroup]['-webkit-mask-sizeWidth'] = newWebkitProps[0];
                this.runtimeEditor.mappedWidgets[options.id]
                    .widget[options.propGroup]['-webkit-mask-sizeHeight'] = newWebkitProps[1];
            } else {
                this.runtimeEditor.mappedWidgets[options.id]
                    .widget[options.propGroup][options.styleType] = newUnitValue;
            }
        } else if (options.styleType === 'fontSize') {
            this.runtimeEditor.mappedWidgets[options.id]
                .widget['fontSize'] = newUnitValue;
        } else if (options.styleType === 'color') {
            this.runtimeEditor.mappedWidgets[options.id]
                .widget['color'] = newUnitValue;
        } else if (options.styleType === 'transform-origin') {
            let newTransformOrigin: string[] = options.newValue.split(' ');

            this.runtimeEditor.mappedWidgets[options.id]
                .widget['transform-origin']['transform-origin-x'] = newTransformOrigin[0];
            this.runtimeEditor.mappedWidgets[options.id]
                .widget['transform-origin']['transform-origin-y'] = newTransformOrigin[1];

        } else if (options.styleType === 'perspective-origin') {

            let newPerspectiveOrigin: string[] = options.newValue.split(' ');
            this.runtimeEditor.mappedWidgets[options.id]
                .widget['perspective-origin']['perspective-origin-x'] = newPerspectiveOrigin[0];
            this.runtimeEditor.mappedWidgets[options.id]
                .widget['perspective-origin']['perspective-origin-y'] = newPerspectiveOrigin[1];

        } else if (helpers.isTransformProperty(options.styleType)) {

            this.runtimeEditor.mappedWidgets[options.id]
                .widget['transform'][options.styleType] = options.newValue;

        } else if (options.styleType === '-webkit-filter') {
            let filterValues = helpers.getWebkitFilterObject(options.newValue);
            if (Object.keys(filterValues).length !== 0) {
                Object.keys(filterValues).map((objectKey) => {
                    this.runtimeEditor.mappedWidgets[options.id]
                        .widget[options.propGroup][objectKey] = filterValues[objectKey];
                });
            } else {
                this.runtimeEditor.mappedWidgets[options.id]
                    .widget[options.propGroup][originalProperty] = options.newValue;
            }
        } else if (options.styleType === 'boxShadow') {
            let boxShadowValues = helpers.getBoxshadowObject(options.newValue);
            Object.keys(boxShadowValues).map((objectKey) => {
                this.runtimeEditor.mappedWidgets[options.id]
                    .widget[options.propGroup][objectKey] = boxShadowValues[objectKey];
            });
        } else {
            this.runtimeEditor.mappedWidgets[options.id]
                .widget[options.propGroup][options.styleType] = newUnitValue;
        }
    }
}
