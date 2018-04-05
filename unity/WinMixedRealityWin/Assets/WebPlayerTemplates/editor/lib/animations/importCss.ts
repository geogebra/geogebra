/**
 *  @module lib/animations/importCss
 *  @requires module:lib/animations/types
 *  @requires module:lib/enums
 *  @requires module:lib/function_helpers
 */
'use strict';
declare let $;

import types from './types';
import Enums from '../enums';
import helpers from '../function_helpers';
import couiEditor from '../../scripts/main';

module importCssHandler {
    /**
     * @param animObj
     * @returns {{}}
     */
    export function importCss(animObj) {
        var result = parser.parse(animObj);
        result = JSON.parse(JSON.stringify(result, null, '\t'));

        return buildAnimObject(result.rulelist);
    }

    /**
     * Applies the animations metadata from the work file to the animObj
     *
     * @param animationsMetadata  {Object} - parsed metadata from the work file
     * @param animObj {Object} - the result from buildAnimObject
     *
     * @returns animObj {Object}
     * */
    function applyAnimsMetadata(animationsMetadata: Object, animObj: Object): Object {
        for (let i in animationsMetadata) {
            if (!animObj[i]) {
                continue;
            }

            let properties = Object.keys(animationsMetadata[i]);
            properties.filter(function (prop) {
                if (!animObj[i].animationsData['-webkit-filter'][prop]) {
                    let currentAnimData = animObj[i].animationsData['-webkit-filter'].filterProperties;
                    let propertiesLen = properties.length;

                    animObj[i].animationsData['-webkit-filter'][properties[0]] = currentAnimData;
                    let firstProp = animObj[i].animationsData['-webkit-filter'][properties[0]];

                    for (let k = 1; k < propertiesLen; k++) {
                        animObj[i].animationsData['-webkit-filter'][properties[k]] = firstProp;
                    }

                    animObj[i].keyframes['-webkit-filter'] = animationsMetadata[i];
                    delete animObj[i].animationsData['-webkit-filter'].filterProperties;
                }
            });
        }

        return animObj;
    }

    /**
     * Applies the animations transforms metadata from the work file to the animObj
     *
     * @param animationsMetadata  {Object} - parsed metadata from the work file
     * @param animObj {Object} - the result from buildAnimObject
     *
     * @returns animObj {Object}
     * */
    function applyTransformsMetadata(animationsMetadata: Object, animObj: Object): Object {
        for (let animation in animationsMetadata) {
            if (!animObj[animation]) {
                continue;
            }

            animObj[animation].keyframes['transform'] = animationsMetadata[animation];
        }

        return animObj;
    }

    /**
     *
     * @param cssObj
     * @returns {{}}
     */
    var buildAnimObject = function (cssObj) {
        var animObj = {};
        let cssCustomMeta;
        let cssTransformsMeta;
        let animationsMetadata;
        let transformAnimationsMetadata;

        for (var i = 0; i < cssObj.length; i++) {

            var type = cssObj[i].type;
            if (type === 'style') {
                cssCustomMeta = cssObj[i].declarations['custom-metadata'];
                cssTransformsMeta = cssObj[i].declarations['transforms-metadata'];
                if (cssCustomMeta) {
                    let cssAnimationsStr = cssCustomMeta.substr(1, cssCustomMeta.length - 2).replace(/\s(?!(\d+(?=px)))/g, '');
                    animationsMetadata = JSON.parse(cssAnimationsStr);
                }

                if (cssTransformsMeta) {
                    // transform meta data comes in format { " key " : " value " }
                    // remove unnecessary spaces between key and value of JSON string
                    let cssTransformStr = cssTransformsMeta.substr(1, cssTransformsMeta.length - 2).replace(/\s(?!(\d+(=px)))/g, '');
                    transformAnimationsMetadata = JSON.parse(cssTransformStr);
                }

                var originSelector: any = cssObj[i].selector;
                var selector = cssObj[i].selector.substr(1).trim();
                if (originSelector.startsWith('#')) {
                    couiEditor.animationsBackwardsCompatibility.idsToClasses.push(selector);
                }

                var declarations = cssObj[i].declarations;
                animObj[selector] = {};
                animObj[selector].keyframes = {};
                animObj[selector].className = selector;
                animObj[selector].animationsData = {};
                animObj[selector].labels = [];
                animObj[selector].events = {};
                animObj[selector].belongTo = couiEditor.animationBelongsTo;

                var splitsDelay = declarations['animation-delay']
                    .split(/\,/);
                var splitsDirection = declarations['animation-direction']
                    .split(/\,/);
                var splitsDuration = declarations['animation-duration']
                    .split(/\,/);
                var splitsIteration = declarations['animation-iteration-count']
                    .split(/\,/);
                var splitsTiming = declarations['animation-timing-function']
                    .split(/\,/);
                var splitsName = declarations['animation-name']
                    .split(/\,/);

                var keyframesLen = splitsDelay.length;

                for (var j = 0; j < keyframesLen; j++) {
                    var delay = parseFloat(splitsDelay[j]);
                    var duration = parseFloat(splitsDuration[j]);
                    var totalTime = duration;

                    // keyframe declarartions
                    var keyframes = cssObj[i + j + 1].keyframes;

                    if (splitsName[j].indexOf('transform_') !== -1) {
                        keyframes = reconstructTransformsKeyframes(keyframes);
                    }

                    for (var k = 0; k < keyframes.length; k++) {
                        var property = types(keyframes[k].declarations);
                        if (property === 'rotate') {
                            property = 'rotateZ';
                        }
                        var group = Enums.WidgetGroups[property];
                        var percentOffset = parseFloat(keyframes[k].offset);
                        var keyframeTime = convertPtcToMs(percentOffset, totalTime) + delay;
                        var keyframeValue = getPropertyValue(keyframes[k].declarations, property, group);

                        // time key
                        animObj[selector].keyframes[group] = animObj[selector].keyframes[group] || {};
                        animObj[selector].keyframes[group][property] =
                            animObj[selector].keyframes[group][property] || {};
                        animObj[selector].keyframes[group][property][keyframeTime] =
                            animObj[selector].keyframes[group][property][keyframeTime] || {};

                        var timeKey = animObj[selector].keyframes[group][property][keyframeTime];

                        timeKey.values = [keyframeValue];
                        timeKey.property = property;
                        timeKey.group = group;
                        timeKey.time = {
                            seconds: keyframeTime
                        };

                        let animationName = splitsName[j];

                        if (group === 'transform') {
                            animationName = splitsName[j].replace('combined', property);
                        }

                        animObj[selector].animationsData[group] = animObj[selector].animationsData[group] || {};
                        animObj[selector].animationsData[group][property] = {
                                direction: splitsDirection[j],
                                iteration: splitsIteration[j],
                                timing: splitsTiming[j],
                                name: animationName
                            } || {};
                    }
                }
            }

            if (animationsMetadata) {
                animObj = applyAnimsMetadata(animationsMetadata, animObj);
            }

            if (transformAnimationsMetadata) {
                animObj = applyTransformsMetadata(transformAnimationsMetadata, animObj);
            }
        }

        return animObj;
    };

    function getPropertyValue(obj, prop, group) {
        switch (group) {
            case 'geometry':
            case 'transform':
                if (prop === 'rotateZ') {
                    if (!obj[prop]) {
                        return obj['rotate'];
                    }
                }
                return obj[prop];
            case 'transform-origin':
                return getTransformOriginValue(obj[prop]);
            case '-webkit-filter':
                return getFilterValue(obj[group]);
            case 'boxShadow':
                return getBoxShadowValue(obj['box-shadow']);
            case 'backgroundColor':
                return getBackgroundColorValue(obj['background-color']);
            case 'font':
                return getFontValue(obj, prop);
            case 'styles':
                if (prop === 'color') {
                    return getColorValue(obj.color);
                } else if (prop === 'borderColor') {
                    return getBorderColorValue(obj['border-color']);
                } else {
                    return getStyleValue(obj, prop);
                }
            default:
                return false;
        }
    }

    /**
     * Function reconstructing the transfrom keyframes on load.
     * @function reconstructTransformsKeyframes
     * @param {object} keyframes - all of the keyframes in the animation which are transforms.
     */
    function reconstructTransformsKeyframes(keyframes) {
        var newKeyframes = [];
        for (var i = 0; i < keyframes.length; i++) {
            var keyframeValueCurrent = keyframes[i].declarations.transform;
            var keyframePropertiesCurrent = helpers.createFilterPropertyGroup(keyframeValueCurrent);

            var keyframeNames = Object.keys(keyframePropertiesCurrent);

            /* jshint ignore:start */
            keyframeValueCurrent = keyframeNames.map(function (propName) {
                if (keyframePropertiesCurrent[propName]) {
                    var keyframeCopy = $.extend(true, {}, keyframes[i]);
                    delete keyframeCopy.declarations.transform;
                    keyframeCopy.declarations[propName] = keyframePropertiesCurrent[propName];
                    newKeyframes.push(keyframeCopy);
                }
            });
            /* jshint ignore:end */
        }
        return newKeyframes;
    }

    function getBackgroundColorValue(string) {
        return string;
    }

    function getColorValue(string) {
        return string;
    }

    function getBorderColorValue(string) {
        return string;
    }

    function getStyleValue(obj, prop) {

        var cssProperty = helpers.convertJsToCssProperty(prop);

        return obj[cssProperty];
    }

    function getFontValue(obj, prop) {

        var cssProperty = helpers.convertJsToCssProperty(prop);

        return obj[cssProperty];
    }

    function getTransformOriginValue(string) {
        var regExp = /\(([^)]+)\)/;
        var value = regExp.exec(string);
        if (value) {
            return value[1];
        }
        return string;
    }

    function getFilterValue(string) {
        return string;
    }

    function getBoxShadowValue(string) {
        return string;
    }

    function convertPtcToMs(num, totalTime) {
        var percent = totalTime * (num / 100);
        return parseInt(percent.toFixed());
    }
}

export default importCssHandler;
