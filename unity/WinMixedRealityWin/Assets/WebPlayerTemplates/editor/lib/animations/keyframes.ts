/**
 *  @module lib/animations/keyframes
 *  @requires module:lib/enums
 *  @requires module:lib/function_helpers
 *  @requires module:scripts/helpers/units_conversion
 */

'use strict';

declare let $;

import Enums from '../enums';
import helpers from '../function_helpers';
import couiEditor from '../../scripts/main';
import {TimelineScrub} from './timelineScrub';

/**
 * Keyframes constructor
 * @memberOf module:lib/animations/keyframes
 * @class
 * @constructor
 */
export default class Keyframes {
    runtimeEditor: any;
    templateKeyframeLine: any;

    currentKeyframeStack: any = {};
    previousKeyframeStack: any = {};

    templateKeyframePropName: any;
    TimelineScrub: TimelineScrub;
    templateKeyframe: any;
    Timeline: any;

    constructor(runtimeEditor: any) {
        this.runtimeEditor = runtimeEditor;
        this.templateKeyframeLine = document.getElementById('keyframe-line');
        this.templateKeyframePropName = document.getElementById('keyframe-property-name');
        this.templateKeyframe = document.getElementById('keyframe-html');
    }

    init() {
        this.Timeline = this.runtimeEditor.Timeline;
        this.TimelineScrub = new TimelineScrub(this.runtimeEditor);
    }

    widgetKeyframesHandlers(selector) {

        var _this = this;
        $(selector).find('.add-keyframe').off('click');
        $(selector).find('.add-keyframe').on('click', function () {
            _this.runtimeEditor._sceneActionState.primaryAction = 'new action';
            _this.collectKeyframeData($(this));
        });
    }

    /**
     * @function
     * @memberOf module:lib/animations/keyframes.Keyframes
     * @param $this
     * @param oldTime
     * @param currentTime
     */
    onKeyframeChange($this, oldTime,
                     currentTime) {

        var widgetId = $this[0].attributes['data-keyframe-widget-id'].value;
        var className = $this[0].attributes['data-animation-class'].value;
        var property = $this[0].attributes['data-keyframe-property'].value;
        var group = $this[0].attributes['data-keyframe-group'].value;
        var keyframe = this.runtimeEditor.scene.animationClasses[className]
            .keyframes[group][property];
        var currentKeyframeTime = parseFloat($this[0].attributes['data-current-time'].value);
        var $elementInTime = helpers.getFromTimeline('[data-widget-id="' + widgetId + '"] ' +
            '[data-property-type="' + property + '"]' + '[data-keyframe-group="' + group + '"] ' +
            ' [data-current-time="' + currentTime + '"]');

        if (currentKeyframeTime !== currentTime) {

            keyframe.renameProperty(oldTime, currentTime);
            keyframe[currentTime].time.seconds = currentTime;
            if ($elementInTime.length > 0) {
                $elementInTime.remove();
            }
        }

        // undo-redo
        var actionState = this.runtimeEditor.getRedoUndoPrimaryState();
        if (actionState !== 'new action') {
            var keyframeOffsetX = this.Timeline.convertTimeToOffsetX(currentTime);
            $this[0].style.left = keyframeOffsetX + 'px';
            $this[0].attributes['data-current-time'].value = currentTime;
        }

        var keyframeParams = {
            oldTime: oldTime,
            currentTime: currentTime
        };
        this.runtimeEditor._sceneActionState.moveKeyframe = true;
        this.runtimeEditor.createUndoRedoCommand(actionState, widgetId, group, group, property, keyframeParams);
    }

    getValue($this, group, property) {
        var $parentEl = $this.parents('.wrap-property');
        var id = this.runtimeEditor.getSelectedWidgetId();
        var widget = this.runtimeEditor.mappedWidgets[id].widget;

        switch (group) {
            case 'geometry':
                return this.runtimeEditor
                    .getGeometryValue($parentEl, group, property);
            case 'transform-origin':
                return this.runtimeEditor
                    .getTransformOriginValue($parentEl, group, property);
            case 'transform':
                return this.runtimeEditor
                    .getTransformValue($parentEl, group, property);
            case '-webkit-filter':
                return this.runtimeEditor
                    .getFilterValues(widget, group, property);
            case 'boxShadow':
                return this.runtimeEditor.buildBoxShadowProperty(widget);
            case 'backgroundColor':
                return this.runtimeEditor.getBackgroundColorValue();
            case 'styles':
                return this.runtimeEditor.getStyleValue($parentEl, group,
                    property);
            case 'font':
                return this.runtimeEditor.getFontValue($parentEl, group,
                    property);
        }
    }

    collectKeyframeData($this) {
        var selectedWidgetId = this.runtimeEditor.getSelectedWidgetId();
        var currentPinTime = this.Timeline.getPinTime();

        var property = $this.attr('data-property-key');
        var group = $this.attr('data-property-set');
        var value;

        if (property === 'transform-origin') {
            var $transformXInput = helpers.getFromTimeline('[data-property-set="transform-origin"]' +
                '[data-property-key="transform-origin-x"]');
            var $transformYInput = helpers.getFromTimeline('[data-property-set="transform-origin"]' +
                '[data-property-key="transform-origin-y"]');

            var transFormX = this.getValue($transformXInput, group, 'transform-origin-x');
            var transFormY = this.getValue($transformYInput, group, 'transform-origin-y');

            value = transFormX + ' ' + transFormY;
        } else if (property === 'perspective-origin') {
            const $perspectiveXInput = helpers.getFromTimeline('[data-property-set="styles"]' +
                '[data-property-key="perspective-origin-x"]');
            const $perspectiveYInput = helpers.getFromTimeline('[data-property-set="styles"]' +
                '[data-property-key="perspective-origin-y"]');

            const perspectiveFormX = this.getValue($perspectiveXInput, group, 'perspective-origin-x');
            const perspectiveFormY = this.getValue($perspectiveYInput, group, 'perspective-origin-y');

            value = perspectiveFormX + ' ' + perspectiveFormY;
        } else {
            value = this.getValue($this, group, property);
        }

        if (group === '-webkit-filter') {
            property = Enums.StylePropToKeyframeProp[group][property];
        }
        // TODO: Precedent value not rendering from the toolbars,handle better on recurrence;
        if (helpers.notAllowedForAnimation(property, value)) {
            return;
        }

        this.runtimeEditor.Animations.addKeyframe(selectedWidgetId, group, property, value, currentPinTime);

        if (group === 'transform') {
            // add keyframes for the missing transform properties applied to the widget
            helpers.syncTransformAnimations(selectedWidgetId, property);
        }
        if (group === '-webkit-filter') {
            // add keyframes for the missing filters properties applied to the widget
            helpers.syncFiltersAnimations(selectedWidgetId, property);
        }
    }

    /**
     * @function
     * @memberOf module:lib/animations/keyframes.Keyframes
     * @param selectedWidgetId
     * @param group
     * @param property
     * @param value
     * @param positions
     */
    addKeyframe(selectedWidgetId, group,
                property, value, positions) {

        var runtimeEditor = this.runtimeEditor;
        const widget = runtimeEditor.mappedWidgets[selectedWidgetId].widget;
        var className = runtimeEditor.mappedWidgets[selectedWidgetId].widget.className || '';

        if (className !== '') {
            className = helpers.getAnimationClassNames(className);
        }

        var $elementInTime = helpers.getFromTimeline('[data-keyframe-widget-id="' + selectedWidgetId + '"]' +
            '[data-keyframe-property="' + property + '"]' + '[data-keyframe-group="' + group + '"]' +
            '[data-current-time="' + positions.seconds + '"]');

        // Make sure the keyframe is not allready on the widget //
        if ($elementInTime.length === 1) {
            var keyframeData = this.Timeline.getKeyframeWidgetData($elementInTime);
            if (keyframeData.className === className &&
                keyframeData.prop === property &&
                keyframeData.value === value
            ) {
                return;
            }
        }

        if (!runtimeEditor._sceneActionState.keyframeInitial) {
            // undo-redo
            var $elementInfo = helpers.getFromTimeline('[data-timeline-info-widget-id="' + selectedWidgetId + '"] ' +
                '.info-property-name');

            var keyframeParams: any = {
                positions: $.extend(true, {}, positions),
                value: value
            };
            var actionState = runtimeEditor.getRedoUndoPrimaryState();

            if ($elementInTime.length > 0) {
                keyframeParams.oldKeyframe = runtimeEditor.scene.animations[selectedWidgetId][className]
                    .keyframes[group][property][positions.seconds];
            }

            // Init new keyframe entry
            if ($elementInfo.length === 0 && actionState === 'new action') {
                var doUndo = false;
                if (className === '') {
                    if (couiEditor.preferences.timeline.filterTimelineWidgets) {
                        runtimeEditor.Timeline.addWidget(widget, {initial: false});
                    }

                    className = couiEditor.generateClassName();
                    doUndo = true;
                }

                keyframeParams.className = className;
                var oldClassName = $(this).parents('.info-class-name')
                    .find('.animation-class-name-input').val() || '';

                runtimeEditor._sceneActionState.switchAnimationClass = true;

                this.Timeline.initAnimationClassName(selectedWidgetId, oldClassName, className);
                this.setKeyframe(selectedWidgetId, group, property,
                    value, positions, className);

                let keyframeLength = 0;
                const widgetIds = [];
                for (let widgetAnimId in runtimeEditor.scene.animations) {
                    if (widgetAnimId !== selectedWidgetId &&
                        runtimeEditor.scene.animations[widgetAnimId].className === className) {

                        keyframeLength++;
                        this.setKeyframe(widgetAnimId, group, property,
                            value, positions, className);
                        widgetIds.push(widgetAnimId);
                    }
                }

                // undo - redo
                for (let j = 0; j < keyframeLength; j++) {
                    runtimeEditor._sceneActionState.addKeyframe = true;
                    runtimeEditor.createUndoRedoCommand(
                        actionState, widgetIds[j], group, group, property, keyframeParams
                    );
                }

                // force addkeyframe command only after the new class name and auto keyframe
                if (doUndo && runtimeEditor.autoKeyframe &&
                    runtimeEditor._sceneActionState.primaryAction === 'new action') {
                    runtimeEditor._sceneActionState.addKeyframe = true;
                    runtimeEditor._undoCreationStepsLength += 1;
                    runtimeEditor.createUndoRedoCommand(
                        actionState, selectedWidgetId, group, group, property, keyframeParams
                    );
                }
            } else if (actionState !== 'new action') {
                if ($elementInfo.length === 0) {
                    runtimeEditor.Animations.createAnimationWidget(widget, {initial: false});
                    this.Timeline.initAnimationClassName(selectedWidgetId, '', className, false, true);
                    helpers.getFromTimeline('[data-timeline-info-widget-id="' + selectedWidgetId + '"] ' +
                        '.animation-class-name-input').show();
                }

                this.setKeyframe(selectedWidgetId, group, property,
                    value, positions, className);

                for (let widgetAnimId in runtimeEditor.scene.animations) {
                    if (widgetAnimId !== selectedWidgetId &&
                        runtimeEditor.scene.animations[widgetAnimId].className === className) {
                        this.setKeyframe(widgetAnimId, group, property,
                            value, positions, className);
                    }
                }

                keyframeParams.className = className;
                runtimeEditor._sceneActionState.addKeyframe = true;
                runtimeEditor.createUndoRedoCommand(
                    actionState, selectedWidgetId, group, group, property, keyframeParams
                );
            } else {
                // Loop all keyframes with this class, property and position
                let keyframeLength = 0;
                const widgetIds = [];
                for (let widgetAnimId in runtimeEditor.scene.animations) {
                    if (runtimeEditor.scene.animations[widgetAnimId][className]) {
                        keyframeLength++;
                        if (!runtimeEditor.autoKeyframe && !runtimeEditor.forceAutoKeyframes) {
                            runtimeEditor._undoCreationStepsLength = keyframeLength;
                        }
                        this.setKeyframe(widgetAnimId, group, property,
                            value, positions, className);
                        widgetIds.push(widgetAnimId);
                    }
                }

                keyframeParams.className = className;

                // undo - redo
                for (var j = 0; j < keyframeLength; j++) {
                    runtimeEditor._sceneActionState.addKeyframe = true;
                    runtimeEditor.createUndoRedoCommand(
                        actionState, widgetIds[j], group, group, property, keyframeParams
                    );
                }
            }
        } else {
            // Case we are opening file
            this.setKeyframe(selectedWidgetId, group, property,
                value, positions, className);
        }

        helpers.getFromTimeline('.info-widgets').getNiceScroll().resize();
    }

    /**
     * @function
     * @memberOf module:lib/animations/keyframes.Keyframes
     * @param selectedWidgetId
     * @param group
     * @param property
     * @param value
     * @param positions
     * @returns {Enums.newScene.animations|{}|coui.Editor.copiedWidgets.animations|array|Object|*}
     */
    buildKeyframeObject(selectedWidgetId, group,
                        property, value, positions) {
        var classNames = this.runtimeEditor.mappedWidgets[selectedWidgetId].widget.className;
        var widgetClasses = classNames.split(' ');
        var animationsContainer = this.runtimeEditor.scene.animations;
        var animationClasses: IAnimationClasses = this.runtimeEditor.scene.animationClasses;

        widgetClasses.filter(function (className) {

            // prevent merging data in classes
            if (!animationClasses[className]) {
                return;
            }

            animationClasses[className] = animationClasses[className] || $.extend(true, {}, Enums.newAnimation);
            animationClasses[className].className = className;
            animationClasses[className].keyframes[group] = animationClasses[className].keyframes[group] || {};
            animationClasses[className].animationsData[group] = animationClasses[className].animationsData[group] || {};
            animationClasses[className].animationsData[group][property] =
                animationClasses[className].animationsData[group][property] || {
                    timing: 'linear',
                    direction: 'normal',
                    iteration: 1,
                    name: className + '_' + group + '_' + property
                };

            animationClasses[className].keyframes[group][property] =
                animationClasses[className].keyframes[group][property] || {};
            animationClasses[className].keyframes[group][property][positions.seconds] = {
                property: property,
                group: group,
                values: [value],
                time: positions
            };

            animationsContainer[selectedWidgetId] = animationsContainer[selectedWidgetId] || {};

            if (!animationsContainer[selectedWidgetId][className]) {
                animationsContainer[selectedWidgetId][className] = animationClasses[className];
            }

            animationsContainer[selectedWidgetId].id = selectedWidgetId;
            animationsContainer[selectedWidgetId].className = className;
        });

        return animationsContainer;
    }

    setKeyframe(selectedWidgetId, group,
                property, value, positions, className) {

        var animationsObj = this.buildKeyframeObject(selectedWidgetId, group,
            property, value, positions);

        var $elementInTime = helpers.getFromTimeline('[data-keyframe-widget-id="' + selectedWidgetId + '"]' +
            '[data-keyframe-property="' + property + '"]' + '[data-keyframe-group="' + group + '"]' +
            '[data-current-time="' + positions.seconds + '"]');

        if ($elementInTime.length > 0) {
            $elementInTime.remove();
        }

        var cloneTemplateKeyframeLine = couiEditor.importHtmlTemplate(this.templateKeyframeLine);
        cloneTemplateKeyframeLine.setAttribute('data-property-type', property);

        let $animationGroup = helpers.getFromTimeline('[data-widget-id="' + selectedWidgetId + '"]' +
            ' [data-property-type="' + property + '"]');

        // TODO: ANIMATION GROUP CORRECT !! BUT < 0 //
        if ($animationGroup.length <= 0) {

            var $info = helpers.getFromTimeline('[data-timeline-info-widget-id="' + selectedWidgetId + '"]');

            var cloneKeyframePropNameHtml = couiEditor.importHtmlTemplate(this.templateKeyframePropName);
            cloneKeyframePropNameHtml.setAttribute('data-timeline-info-property-name', property);
            $(cloneKeyframePropNameHtml).addClass(className);

            $(cloneKeyframePropNameHtml).find('.property-name-text').text(property);
            var iconAnimationOptions = cloneKeyframePropNameHtml.querySelectorAll('.ic-properties');

            $info.append(cloneKeyframePropNameHtml);

            helpers.getFromTimeline('.widgets-keyframes [data-widget-id="' + selectedWidgetId + '"]')
                .append(cloneTemplateKeyframeLine);

            this.Timeline.createTooltip(helpers.getFromTimeline(iconAnimationOptions),
                animationsObj[selectedWidgetId], group, property);
        }

        var cloneTemplateKeyframe = couiEditor.importHtmlTemplate(this.templateKeyframe);
        cloneTemplateKeyframe.classList.add('keyframe', 'dragging-point');
        cloneTemplateKeyframe.setAttribute('data-current-time', positions.seconds);
        cloneTemplateKeyframe.setAttribute('data-keyframe-group', group);
        cloneTemplateKeyframe.setAttribute('data-keyframe-property', property);
        cloneTemplateKeyframe.setAttribute('data-keyframe-widget-id', selectedWidgetId);
        cloneTemplateKeyframe.setAttribute('data-animation-class', className);

        var keyframeOffsetX = this.Timeline.convertTimeToOffsetX(positions.seconds);

        cloneTemplateKeyframe.style.left = keyframeOffsetX + 'px';
        var $widgetLine = helpers.getFromTimeline('[data-widget-id="' + selectedWidgetId + '"] ' +
            '[data-property-type="' + property + '"]');
        $widgetLine.append(cloneTemplateKeyframe);
        $widgetLine.addClass(className);

        if (cloneTemplateKeyframe.offsetLeft > $('#timeline-ruler').width()) {
            this.Timeline.originalRulerWidth = cloneTemplateKeyframe.offsetLeft + 200;
        }
    }
}


