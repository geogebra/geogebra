/**
 *  @module lib/animations/exportCss
 *  @requires module:lib/function_helpers
 */

'use strict';
import couiEditor from '../../scripts/main';
import helpers from '../function_helpers';

module exportCssModule {

    var runtimeEditor;

    export function exportCss(animObj: any, editorId: string): string {
        runtimeEditor = couiEditor.openFiles[editorId].runtimeEditor;
        runtimeEditor.totalAnimationTime = 0;
        return buildCss(animObj);
    }

    let getCustomMetadataFilters = function (customMeta, className): string {
        if (customMeta) {
            return 'custom-metadata: ' + '"{"' +
                className + '":' + JSON.stringify(customMeta.keyframes['-webkit-filter']) + '}";\n' +
                '}';
        }
    };

    const getCustomMetadataTransforms = function (animObj: IAnimationClass, className: string): string {
        return 'transforms-metadata: ' + '"{"' +
            className + '":' + JSON.stringify(animObj.keyframes['transform']) + '}";\n';
    };

    var buildCss = function (animObj: any): string {
        let style: string = '';
        const animObjOrigin = runtimeEditor.scene.animationClasses;
        const isPublishing: boolean = couiEditor.onSelectedFileType.publishPage;
        for (var className in animObj) {
            let customMetadataFilters: string = '}\n';
            let customMetadataTransforms: string = '';
            let animWidget = animObj[className];

            let animations;
            if (animWidget.keyframes['-webkit-filter']) {
                const customMetaAnim = animObjOrigin[className];
                customMetadataFilters = isPublishing ? '}\n' : getCustomMetadataFilters(customMetaAnim, className);
                animWidget.keyframes['-webkit-filter'] = combineFilterAnims(animWidget.keyframes['-webkit-filter']);

                //gets the animationData from the first keyframes and uses it for the combined filters animation
                let firstKeyframe = Object.keys(animWidget.animationsData['-webkit-filter'])[0];
                let currentAnimData = animWidget.animationsData['-webkit-filter'][firstKeyframe];
                animWidget.animationsData['-webkit-filter'] = {
                    filterProperties: currentAnimData
                };
            } else {
                // in case the animation object doesn't have filter animation
                // but some of previous had, reset the value
                customMetadataFilters = '}\n';
            }

            if (animWidget.keyframes['transform']) {
                const originalTransforms = runtimeEditor.scene.animationClasses[className];
                customMetadataTransforms = isPublishing ? '' : getCustomMetadataTransforms(originalTransforms, className);
            }

            animations = fillAnimData(animWidget);

            if (animWidget.className) {
                style += '.' + animWidget.className + ' {\n' +
                    'animation-fill-mode: forwards;\n' +
                    'animation-name: ' + animations.names + ';\n' +
                    'animation-duration: ' + animations.end + ';\n' +
                    'animation-timing-function: ' + animations.timing + ';\n' +
                    'animation-delay: ' + animations.start + ';\n' +
                    'animation-iteration-count: ' + animations.iteration + ';\n' +
                    'animation-direction: ' + animations.direction + ';\n' +
                    customMetadataTransforms +
                    customMetadataFilters;

                for (var groupName in animWidget.keyframes) {
                    var animKeyframeGroup = animWidget.keyframes[groupName];
                    var animDataGroup = animWidget.animationsData[groupName];
                    for (var propName in animKeyframeGroup) {
                        var propertyObj = animKeyframeGroup[propName];
                        var animationData = animDataGroup[propName];
                        var duration = findTimePositions(propertyObj);
                        var keyframes = loopKeyframes(propertyObj, groupName,
                            duration);
                        var animationName;

                        if (animationData.name && animationData.name !== '') {
                            animationName = animationData.name;
                        } else {
                            animationName = groupName + '_' +
                                propName + '_' + animWidget.id;
                        }

                        style += '@keyframes ' + animationName +
                            '{\n' + keyframes + '}\n ';
                    }
                }
            }
        }
        if (style !== '') {
            return '<style id="coui_animations_block">\n' +
                couiEditor.environmentProperties.CSS_ANIMATIONS_MARK_START + '\n' +
                style +
                couiEditor.environmentProperties.CSS_ANIMATIONS_MARK_END + '\n' +
                '</style>';
        }

        return '';
    };

    /**
     * Combines all filter animations into one animation
     * @param filterAnims {Object} - separated filter animatios
     * @returns filters {Object}
     * */
    function combineFilterAnims(filterAnims: Object) {
        let filters = {filterProperties: {}};

        for (let filter in filterAnims) {
            for (let time in filterAnims[filter]) {
                if (!filters.filterProperties[time]) {
                    filters.filterProperties[time] = filterAnims[filter][time];
                }
            }
        }

        return filters;
    }

    function fillAnimData(propertyObj) {
        var names = '',
            end = '',
            start = '',
            timing = '',
            iteration = '',
            direction = '',
            concat = '';

        for (var groupName in propertyObj.keyframes) {
            var keyframesGroup = propertyObj.keyframes[groupName];
            var animDataGroup = propertyObj.animationsData[groupName];
            for (var propName in keyframesGroup) {
                var keyframesProp = keyframesGroup[propName];
                var animDataProp = animDataGroup[propName];
                var times = findTimePositions(keyframesProp);

                var doesNameExist = animDataProp.name;
                if (doesNameExist && doesNameExist !== '') {
                    names += concat + animDataProp.name;
                } else {
                    names += concat + groupName + '_' + propName;
                }

                end += concat + times.totalTimeToPlay + 'ms';
                start += concat + times.start + 'ms';
                timing += concat + animDataProp.timing;
                iteration += concat + animDataProp.iteration;
                direction += concat + animDataProp.direction;

                concat = ', ';
                /*jslint bitwise: true */
                if (~~times.end > ~~runtimeEditor.totalAnimationTime) {
                    runtimeEditor.totalAnimationTime = ~~times.end;
                }
                /*jslint bitwise: false */
            }
        }

        return {
            names: names,
            end: end,
            start: start,
            timing: timing,
            iteration: iteration,
            direction: direction
        };
    }

    function findTimePositions(propertyObj) {
        var keys: any = Object.keys(propertyObj);

        var totalTimeToPlay = keys[keys.length - 1] - keys[0];

        return {
            end: keys[keys.length - 1],
            start: keys[0],
            totalTimeToPlay: totalTimeToPlay
        };
    }

    function loopKeyframes(propertyObj, groupName, duration) {
        var keyframes = '';
        let endDuration: number = duration.end - duration.start;

        for (var time in propertyObj) {
            let keyPos: number = parseFloat(time) - duration.start;
            var percent = convertMsToPercent(keyPos, endDuration);
            var data;

            switch (groupName) {
                case 'geometry':
                    data = buildGeometryData(propertyObj[time]);
                    break;
                case 'transform':
                    data = buildTransformData(propertyObj[time]);
                    break;
                case 'transform-origin':
                    data = buildTransformOriginData(propertyObj[time]);
                    break;
                case '-webkit-filter':
                    data = buildFilterData(groupName, propertyObj[time]);
                    break;
                case 'boxShadow':
                    data = buildBoxShadowData(groupName, propertyObj[time]);
                    break;
                case 'backgroundColor':
                    data = buildBackgroundColorData(groupName, propertyObj[time]);
                    break;
                case 'styles':
                    if (propertyObj[time].property === 'color') {
                        data = buildColorData(groupName, propertyObj[time]);
                    } else {
                        data = buildStyleData(propertyObj[time]);
                    }
                    break;
                case 'font':
                    data = buildFontData(propertyObj[time]);
                    break;
                default:
                    console.error('Loop keyframes error!');
                    break;
            }

            keyframes += percent + '%   { ' + data.property + ': ' +
                data.value + '; }\n';
        }
        return keyframes;
    }

    function buildGeometryData(obj) {
        return {
            property: obj.property,
            value: obj.values[0]
        };
    }

    function buildStyleData(obj) {
        return {
            property: helpers.convertJsToCssProperty(obj.property),
            value: obj.values[0]
        };
    }

    function buildFontData(obj) {
        return {
            property: helpers.convertJsToCssProperty(obj.property),
            value: obj.values[0]
        };
    }

    function buildBackgroundColorData(groupName, obj) {
        return {
            property: 'background-color',
            value: obj.values[0]
        };
    }

    function buildColorData(groupName, obj) {
        return {
            property: 'color',
            value: obj.values[0]
        };
    }

    function buildTransformData(obj) {
        return {
            property: obj.property,
            value: obj.values[0]
        };
    }

    function buildTransformOriginData(obj) {
        return {
            property: obj.property,
            value: obj.values[0]
        };
    }

    function buildFilterData(groupName, obj) {

        return {
            property: groupName,
            value: helpers.buildFilterProps(obj, true)
        };
    }

    function buildBoxShadowData(groupName, obj) {
        return {
            property: 'box-shadow',
            value: obj.values[0]
        };
    }

    function getNumberOnly(value) {
        var regex = /[^a-zA-Z%]+/;
        var result = value.match(regex);
        return result;
    }


    function convertMsToPercent(num, totalTime) {
        if (num === 0 && totalTime === 0) {
            return 100;
        }
        let percent: string = ((num / totalTime) * 100).toFixed(0);
        return percent;
    }

    function convertPercentToMs(num, totalTime) {
        let percent: string = (totalTime * (num / 100)).toFixed(0);
        return percent;
    }
}

export default exportCssModule;

