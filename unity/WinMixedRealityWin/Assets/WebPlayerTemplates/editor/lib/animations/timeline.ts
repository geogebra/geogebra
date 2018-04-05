/**
 *  @module lib/animations/timeline
 *  @requires module:lib/function_helpers
 *  @requires module:lib/animations/keyframes
 *  @requires hbs:lib/animations/hbs/tooltip_options.hbs
 *  @requires module:lib/handlebars_helpers
 */

'use strict';

import Enums from '../enums';
import helpers from '../function_helpers';
import couiEditor from '../../scripts/main';

var tooltipHbs = require('lib/animations/hbs/tooltip_options.hbs!text');

const MAX_ZOOM = 4;
const MIN_ZOOM = 1;
const DEFAULT_ZOOM = 2.5;
const ZOOM_STEP: any = 0.1;
const PIN_WIDTH = 10;
const LEFT_PANEL_SIZE = 270;
const ADD_TIME_OFFSET = 50;
const APPEND_MORE_TIME = 10;
const TIMELINE_LEFT_OFFSET = 0;
const GT_PINOFFSET_FIX = 0.5;

declare let $: any;

let refreshInterval = null;
let documentWidth;
let leftBarOffset;
let draggedElement;

interface Element {
    scrollIntoView(arg?: boolean | Object): void;
}

/**
 * Timeline constructor
 * @memberOf module:lib/animations/timeline
 * @class
 * @constructor
 */
export default class Timeline {
    $timelineEl: any;
    $timelineWrap: any;
    $animTimeEl: any;
    $pin: any;
    $ruler: any;
    $timelineInput: any;
    $infoWidgetsSection: any;
    timelineWidth: number;
    timelineZoom: number;
    originalRulerWidth: number;
    pinCurrentSeconds: number;
    currentSeconds: number;
    oldPinheadSeconds: number;
    pinHalfWidth: number;
    runtimeEditor: any;
    templateWidgetLine: HTMLElement;
    templateTimelineInfo: HTMLElement;
    dragFlag: boolean;
    animationSearchValue: string;

    constructor(runtimeEditor: any) {
        this.$timelineEl = $('.timeline');
        this.$timelineWrap = $('.timeline-wrap');
        this.$animTimeEl = $('.animation-timeline');
        this.$pin = $('#timeline-pin');
        this.$ruler = $('#timeline-ruler');
        this.$timelineInput = $('#timeline-input');
        this.$infoWidgetsSection = $('.info-stack');
        this.timelineWidth = 2000;
        this.timelineZoom = DEFAULT_ZOOM;
        this.originalRulerWidth = 2000;
        this.pinCurrentSeconds = 0;
        this.currentSeconds = 0;
        this.oldPinheadSeconds = 0;
        this.pinHalfWidth = 4;
        this.runtimeEditor = runtimeEditor;
        this.templateWidgetLine = document.getElementById('widget-line-html');
        this.templateTimelineInfo = document.getElementById('timeline-info');
        this.dragFlag = false;
        this.animationSearchValue = '';
        this.timelineInputHandler();
    }

    create() {
        this.$timelineEl.off('mousewheel');
        this.selectKeyframeHandlers();
        this.draggingPointsHandlers();
        this.createTimeline();
        this.zoomHandler();
        this.timelineScrollHandler();
    }

    getPinTime() {
        var offsetX = this.$pin[0].offsetLeft;
        var seconds = this.pinCurrentSeconds;

        return {
            offset: offsetX,
            seconds: seconds
        };
    }

    zoomHandler() {
        var _this = this;

        this.$timelineEl.off('mousewheel');
        this.$timelineEl.on('mousewheel', function (e) {
            e.preventDefault();
            var delta = e.delta || e.originalEvent.wheelDelta;
            var zoomOut: boolean = delta ? delta < 0 : e.originalEvent.deltaY > 0;
            var previosPinOffset = _this.$pin[0].offsetLeft;

            if ((!zoomOut && _this.timelineZoom >= MAX_ZOOM) ||
                (zoomOut && _this.timelineZoom <= MIN_ZOOM)) {
                return;
            }

            _this.zoom(zoomOut);
            _this.createTimeline();

            // move the ruler
            var pinDiff = (_this.$pin[0].offsetLeft - previosPinOffset);
            if (_this.$pin[0].offsetLeft > 0) {
                _this.rulerScrollRight(pinDiff);
            }
        });
    }

    zoom(zoomOut: boolean) {
        var _this = this;
        var zoomFactor: number = (zoomOut) ? 1 : 0;

        var step: number = ZOOM_STEP * +!zoomFactor - ZOOM_STEP * zoomFactor;
        var deltaZoom = (this.timelineZoom + step) / this.timelineZoom;
        this.timelineZoom += helpers.toFixed(step, 2);

        $('.dragging-point').each(function () {
            var offsetX = $(this)[0].offsetLeft * deltaZoom;

            // fix offset if is GT environment
            if (couiEditor.globalEditorInfo.backend !== Enums.Backends.Debug &&
                couiEditor.globalEditorInfo.backend !== Enums.Backends.Website &&
                couiEditor.globalEditorInfo.backend !== Enums.Backends['Standalone-2']) {
                offsetX += GT_PINOFFSET_FIX;
            }

            _this.renderPoint(offsetX, $(this));
        });
    }

    extendTimeline() {

        this.timelineWidth = (this.originalRulerWidth * this.timelineZoom);

        this.setTimelineWidth(this.timelineWidth);
    }

    setTimelineWidth(width) {
        this.$ruler.attr('width', width);
        this.$timelineEl[0].style.width = width + 'px';
    }

    createTimeline() {

        this.extendTimeline();

        var context = this.$ruler[0].getContext('2d');

        this.timelinePositions({
            render: context
        });
    }

    createWidgetLines() {
        var context = helpers.getFromTimeline('.keyframes-line')[0].getContext('2d');
        context.beginPath();

        this.timelinePositions({
            render: context
        });
    }

    timelinePositions(options, $element?) {

        var seconds = 0;
        var secondsStep = 100;
        var step = 10;
        var timelineLen = helpers.toFixed(this.timelineWidth, 2);

        for (var i = 0; i < timelineLen; i += (step * this.timelineZoom), seconds += secondsStep) {

            if (options.render) {
                this.renderTimeline(options.render, i, seconds);
            }

            if (options.calcTimePosition) {
                this.calcTimePosition(options.calcTimePosition, i, step,
                    seconds, secondsStep, $element);
            }
        }

        return seconds;
    }

    renderTimeline(context, i, seconds) {
        context.font = '10px Helvetica, sans-serif';
        context.fillStyle = '#fff';

        if ((seconds % 1000) === 0) {
            context.beginPath();
            context.moveTo(i, 0);
            context.lineTo(i, 16);
            context.strokeStyle = '#fff';
            context.stroke();
            context.fillText(seconds, i + 3, 10);
        } else if (this.timelineZoom > 1 && (seconds % 500) === 0) {
            context.beginPath();
            context.moveTo(i, 0);
            context.lineTo(i, 8);
            context.strokeStyle = '#fff';
            context.stroke();
            context.fillText(seconds, i + 3, 10);
        } else if (this.timelineZoom > 2 && (seconds % 100) === 0) {
            context.beginPath();
            context.moveTo(i, 0);
            context.lineTo(i, 5);
            context.strokeStyle = '#fff';
            context.stroke();
        } else if (this.timelineZoom > 3 && (seconds % 50) === 0) {
            context.beginPath();
            context.moveTo(i, 0);
            context.lineTo(i, 5);
            context.strokeStyle = '#fff';
            context.stroke();
        }
    }


    renderPoint(xPos, $element) {
        if (xPos < 0) {
            xPos = -4;
        }
        $element.css('left', xPos);
    }

    getKeyframeWidgetData($keyframe) {
        var className = $keyframe[0].attributes['data-animation-class'].value;
        var time = $keyframe[0].attributes['data-current-time'].value;
        var id = $keyframe[0].attributes['data-keyframe-widget-id'].value;
        var group = $keyframe[0].attributes['data-keyframe-group'].value;
        var prop = $keyframe[0].attributes['data-keyframe-property'].value;
        var clasess = this.runtimeEditor.scene.animationClasses;
        var value = clasess[className].keyframes[group][prop][time].values[0];

        return {
            className: className,
            time: time,
            id: id,
            group: group,
            prop: prop,
            value: value
        };
    }

    selectKeyframe($keyframe) {
        var keyframeData = this.getKeyframeWidgetData($keyframe);
        var selectedWidgets = this.runtimeEditor.currentElementsSelection;
        if (selectedWidgets.length === 0 ||
            selectedWidgets[0] !== keyframeData.id) {
            this.runtimeEditor.selectJstreeItem(keyframeData.id);
        }

        helpers.getFromTimeline('div[data-keyframe-widget-id]').removeClass('keyframe-pressed');
        $keyframe.addClass('keyframe-pressed');
        var selector = $keyframe.attr('data-keyframe-widget-id');
        this.toggleElementTimeline($('#' + selector), true);

        $('.breadcrumb').html('<span class="keyframe-info-text"><b>Animated element: </b></span>' + keyframeData.id +
            '<span class="keyframe-info-text">&nbsp&nbsp<b> property: </b></span> ' + keyframeData.prop +
            '<span class="keyframe-info-text">&nbsp&nbsp<b> value: </b></span>' + keyframeData.time);
    }

    unselectAllKeyfames() {
        helpers.getFromTimeline('div[data-keyframe-widget-id]').removeClass('keyframe-pressed');
        this.runtimeEditor.selectedKeyframes.length = 0;
    }

    unhighlightAllTracks() {

        // deselect element tracks
        helpers.getFromTimeline('div[data-timeline-info-widget-id] > div')
            .removeClass('element-track-selected');

        helpers.getFromTimeline('div[data-timeline-info-widget-id] > div > .info-widget-name')
            .removeClass('element-track-selected');

        helpers.getFromTimeline('div[data-widget-id] > .keyframe-line')
            .removeClass('element-track-selected');

        // deselect animation class tracks
        helpers.getFromTimeline('div[data-timeline-info-widget-id] > .info-class-name')
            .removeClass('class-track-selected');

        helpers.getFromTimeline('div[data-widget-id] > .keyframe-line-class-name')
            .removeClass('class-track-selected');

        // deselect properties tracks
        helpers.getFromTimeline('div[data-timeline-info-widget-id] > .info-property-name')
            .removeClass('track-selected');

        helpers.getFromTimeline('div[data-widget-id] > div > .selectable-track')
            .removeClass('track-selected');

        // deselect input fields for class
        helpers.getFromTimeline('div[data-timeline-info-widget-id] > div > .animation-class-name-input')
            .removeClass('input-track-selected');

    }

    selectKeyframeHandlers() {
        var _this = this;

        helpers.getFromTimeline('.dragging-point').off('mousedown');
        helpers.getFromTimeline('.dragging-point').on('mousedown', function (event) {
            _this.runtimeEditor.blurOutTextInputs();
            if ($(this)[0].id !== 'timeline-pin') {
                _this.selectKeyframe($(this));
                _this.runtimeEditor.selectedKeyframes[0] = $(this);
            }
        });

        $('html').off('click');
        $('html').on('click', function (e) {
            if (_this.dragFlag) {
                _this.dragFlag = false;
            }
        });
    }

    toggleElementTimeline(element, shouldHighlight) {
        var elementId = element.attr('id');
        var elementLine: Element = document.getElementById('anim-option-' + elementId);

        if (shouldHighlight) {
            // select element tracks
            helpers.getFromTimeline('div[data-timeline-info-widget-id="' + elementId + '"] > div')
                .addClass('element-track-selected');

            helpers.getFromTimeline('div[data-timeline-info-widget-id="' + elementId + '"] > div > .info-widget-name')
                .addClass('element-track-selected');

            helpers.getFromTimeline('div[data-widget-id="' + elementId + '"] > .keyframe-line')
                .addClass('element-track-selected');

            // select animation class tracks
            helpers.getFromTimeline('div[data-timeline-info-widget-id="' + elementId + '"] > .info-class-name')
                .addClass('class-track-selected');

            helpers.getFromTimeline('div[data-widget-id="' + elementId + '"] > .keyframe-line-class-name')
                .addClass('class-track-selected');

            // select properties tracks
            helpers.getFromTimeline('div[data-timeline-info-widget-id="' + elementId + '"] > .info-property-name')
                .addClass('track-selected');

            helpers.getFromTimeline('div[data-widget-id="' + elementId + '"] > div > .selectable-track')
                .addClass('track-selected');

            // select input fields for class
            helpers.getFromTimeline('div[data-timeline-info-widget-id="' + elementId + '"] ' +
                '> div > .animation-class-name-input')
                .addClass('input-track-selected');

            // if lines exists on the timeline scroll to them
            if (elementLine !== null) {
                elementLine.scrollIntoView({block: 'start', behavior: 'smooth'});
            }

        } else {
            // deselect element tracks
            helpers.getFromTimeline('div[data-timeline-info-widget-id="' + elementId + '"] > div')
                .removeClass('element-track-selected');

            helpers.getFromTimeline('div[data-timeline-info-widget-id="' + elementId + '"] > div > .info-widget-name')
                .removeClass('element-track-selected');

            helpers.getFromTimeline('div[data-widget-id="' + elementId + '"] > .keyframe-line')
                .removeClass('element-track-selected');

            // deselect animation class tracks
            helpers.getFromTimeline('div[data-timeline-info-widget-id="' + elementId + '"] > .info-class-name')
                .removeClass('class-track-selected');

            helpers.getFromTimeline('div[data-widget-id="' + elementId + '"] > .keyframe-line-class-name')
                .removeClass('class-track-selected');

            // deselect properties tracks
            helpers.getFromTimeline('div[data-timeline-info-widget-id="' + elementId + '"] > .info-property-name')
                .removeClass('track-selected');

            helpers.getFromTimeline('div[data-widget-id="' + elementId + '"] > div > .selectable-track')
                .removeClass('track-selected');

            // deselect input fields for class
            helpers.getFromTimeline('div[data-timeline-info-widget-id="' + elementId + '"] ' +
                '> div > .animation-class-name-input').removeClass('input-track-selected');
        }
    }

    draggingPointsHandlers() {
        var _this = this;
        var draggedKeyframesByClass = [];
        var selectedElements;

        helpers.getFromTimeline('.dragging-point').kendoDraggable({
            group: 'keyframesGroup',
            hint: function (element) {
                draggedElement = element;
                var time = draggedElement.attr('data-current-time');
                var group = draggedElement.attr('data-keyframe-group');
                var property = draggedElement.attr('data-keyframe-property');
                var className = draggedElement.attr('data-animation-class');

                draggedKeyframesByClass = $('[data-current-time="' + time + '"]' +
                    '[data-keyframe-group="' + group + '"]' +
                    '[data-keyframe-property="' + property + '"]' +
                    '[data-animation-class="' + className + '"]');

                return element.clone().addClass('ob-clone');
            },
            drag: function (e) {
                var dragOffsetX = e.clientX;

                if (draggedElement[0].id !== 'timeline-pin') {
                    _this.runtimeEditor.selectedKeyframes[0] = draggedElement;
                    _this.dragFlag = true;
                }

                if (dragOffsetX > documentWidth - 60) {
                    if (refreshInterval === null) {

                        refreshInterval = setInterval(_this.rulerScrollRight.bind(_this), 5);
                    }
                } else if (dragOffsetX < leftBarOffset.left) {
                    if (refreshInterval === null) {

                        refreshInterval = setInterval(_this.rulerScrollLeft.bind(_this), 5);
                    }
                } else {
                    clearInterval(refreshInterval);
                    refreshInterval = null;
                }

                var scrollLeft = _this.$timelineWrap.scrollLeft();
                var pinOffsetX = parseFloat($('.ob-clone')[0].style.left) - leftBarOffset.left + scrollLeft;

                if (draggedElement[0].id === 'timeline-pin') {
                    _this.moveTimePoint(draggedElement, pinOffsetX);
                    _this.setTimelineInput(_this.currentSeconds);
                }
            },
            dragstart: function (e) {
                _this.runtimeEditor._sceneActionState.primaryAction = 'new action';
                documentWidth = $(document).width();
                leftBarOffset = $('#horizontal-timeline-splitter .k-splitbar').offset();
                draggedElement.addClass('ob-hide');
                $('mask.select-corners').hide();
                $('.transform-origin-point').hide();
                selectedElements = _this.runtimeEditor.currentElementsSelection;
                _this.runtimeEditor._sceneActionState.draggingTimelinePin = true;
            },
            axis: 'x',
            container: _this.$timelineWrap,
            dragend: function (e) {
                var scrollLeft = _this.$timelineWrap.scrollLeft();
                var pinOffsetX = parseFloat($('.ob-clone')[0].style.left) - leftBarOffset.left + scrollLeft;

                clearInterval(refreshInterval);
                refreshInterval = null;
                _this.runtimeEditor._sceneActionState.draggingTimelinePin = false;

                if (draggedElement[0].id !== 'timeline-pin') {
                    _this.runtimeEditor._undoCreationStepsLength = draggedKeyframesByClass.length;
                    for (var i = 0; i < draggedKeyframesByClass.length; i++) {
                        _this.moveTimePoint($(draggedKeyframesByClass[i]), pinOffsetX);
                    }
                    _this.selectKeyframe(draggedElement);
                } else {
                    _this.moveTimePoint(draggedElement, pinOffsetX);
                    _this.setTimelineInput(_this.currentSeconds);
                }

                $('mask.select-corners').show();
                $('.transform-origin-point').show();
                if (selectedElements.length > 0) {
                    _this.runtimeEditor.highlightSelectedEl(null, selectedElements);
                }

                _this.runtimeEditor.exportScene();
            }
        });

        $('body').kendoDropTarget({
            group: 'keyframesGroup',
            drop: function (e) {
                var $cloneObj = $('.ob-clone');
                $cloneObj.css('visibility', 'hidden');
                var pos = $cloneObj.offset();
                $(e.draggable.currentTarget)
                    .removeClass('ob-hide').offset(pos);
            }
        });

        this.$animTimeEl.off('click');
        this.$animTimeEl.on('click', function (e) {
            _this.runtimeEditor.blurOutTextInputs();
            if (e.target.id === 'timeline-ruler') {
                _this.setPinheadPosition(e.offsetX);
                _this.setTimelineInput(_this.currentSeconds);
            }
        });
    }

    /**
     * Get all keyframe offsets
     * @function
     * @memberOf module:lib/animations/timeline.Timeline
     * @param sorted {Boolean} - optional parameter, if set to true, the function return a sorted array
     * @returns {Array}
     * @private
     */
    _getKeyframeOffsets() {
        var widgetLine = helpers.getFromTimeline('.keyframe');
        var keyframeOffsets = [];

        for (var i = 0; i < widgetLine.length; i++) {
            keyframeOffsets.push(widgetLine[i].offsetLeft);
        }

        keyframeOffsets = keyframeOffsets.sort(function (a, b) {
            return a - b;
        });

        return keyframeOffsets;
    }

    /**
     * Get the positions of the timeline pin and keyframe offsets
     * @function
     * @memberOf module:lib/animations/timeline.Timeline
     * @returns {{pin: (Number), keyframes: (Array)}}
     * @private
     */
    _getTimelinePositions() {
        var currentPosition = helpers.getFromTimeline('.dragging-point')[0].offsetLeft;
        var keyframeOffsets = this._getKeyframeOffsets();
        var uniqueKeyframeOffsets = helpers.removeDuplicates(keyframeOffsets);

        return {
            pin: currentPosition,
            keyframes: uniqueKeyframeOffsets
        };
    }

    setToNextKeyframe() {
        var timelinePositions = this._getTimelinePositions();
        if (timelinePositions.keyframes.length > 0) {
            var nextKeyframe = helpers.biggerClosest(timelinePositions.keyframes, timelinePositions.pin);
            var miliseconds = this.convertOffsetXToTime(nextKeyframe);
            this.setTimelineInput(miliseconds);
            this.setPinheadPosition(nextKeyframe);
        }
    }

    setToPreviousKeyframe() {
        var timelinePositions = this._getTimelinePositions();
        if (timelinePositions.keyframes.length > 0) {
            var previousKeyframe = helpers.smallerClosest(timelinePositions.keyframes, timelinePositions.pin);
            var miliseconds = this.convertOffsetXToTime(previousKeyframe);
            this.setTimelineInput(miliseconds);
            this.setPinheadPosition(previousKeyframe);
        }
    }

    setToFirstKeyframe() {
        var sortedKeyframeOffsets = this._getKeyframeOffsets();
        var firstKeyframe = sortedKeyframeOffsets[0];
        if (firstKeyframe) {
            var miliseconds = this.convertOffsetXToTime(firstKeyframe);
            this.setTimelineInput(miliseconds);
            this.setPinheadPosition(firstKeyframe);
        }
    }

    setToLastKeyframe() {
        var sortedKeyframeOffsets = this._getKeyframeOffsets();
        var lastKeyframe = sortedKeyframeOffsets[sortedKeyframeOffsets.length - 1];
        if (lastKeyframe) {
            var miliseconds = this.convertOffsetXToTime(lastKeyframe);
            this.setTimelineInput(miliseconds);
            this.setPinheadPosition(lastKeyframe);
        }
    }

    revertPinheadPosition() {
        this.setTimelineInput(this.oldPinheadSeconds);
        this.setPinheadPosition(0, this.oldPinheadSeconds);
    }

    convertTimeToOffsetX(seconds) {
        return (seconds / 10) * this.timelineZoom - this.pinHalfWidth;
    }

    convertOffsetXToTime(offset) {
        var time = (offset * 10) / this.timelineZoom - this.pinHalfWidth;
        return this.formatTimelineInputValue(time);
    }

    /**
     * Function returning the maximum keyframe time.
     * @function getLastKeyframeTime
     * @memberOf module:lib/animations/timeline.Timeline
     * @return {number} max - maximum keyframe time
     */
    getLastKeyframeTime() {
        var $keyFrames = helpers.getFromTimeline('.keyframe');
        var max = null;

        $keyFrames.each(function () {
            var currentValue = parseInt($(this).attr('data-current-time'));
            if ((max === null) || (currentValue > max)) {
                max = currentValue;
            }
        });
        return max || false;
    }

    doTimerTimeout(length, resolution, onInstance, onComplete, type) {
        var _this = this;

        var steps = (length / 100) * (resolution / 10),
            speed = length / steps,
            count = 0,
            start = new Date().getTime();

        function instance() {
            if (count++ === steps && type === Enums.animationPreviewType.endlessPreview) {
                onComplete(steps, count);
            } else {
                onInstance(steps, count);

                var diff = (new Date().getTime() - start) - (count * speed);
                if (_this.runtimeEditor.iframe) {
                    window.setTimeout(instance, (speed - diff));
                } else {
                    onComplete(steps, count);
                }
            }
        }

        window.setTimeout(instance, speed);
    }

    doTimerAnimationFrame(totalTime, onInstance, onComplete, type) {
        var _this = this;
        var zoomFactor = _this.timelineZoom;
        var start = null;

        function step(timestamp) {
            if (!start) {
                start = timestamp;
            }
            var progress = timestamp - start;
            var shift = (totalTime / progress) * (zoomFactor / _this.timelineZoom);

            onInstance(shift);

            if ((progress < totalTime || type === Enums.animationPreviewType.endlessPreview) &&
                _this.runtimeEditor.iframe) {
                requestAnimationFrame(step);
                _this.setTimelineInput(progress);
            } else {
                onComplete();
            }
        }

        requestAnimationFrame(step);
    }

    animatePinhead(totalTime, type) {
        var _this = this;

        var lastKeyframePosition = this.convertTimeToOffsetX(totalTime);


        var position = 0;
        var $timeLinePin = helpers.getFromTimeline('#timeline-pin');

        this.doTimerAnimationFrame(totalTime, function (steps) {
                position = lastKeyframePosition / steps;
                $timeLinePin.css('left', position);
            },
            function () {
                _this.revertPinheadPosition();
            }, type);
    }

    setPinheadPosition(offsetX, seconds?) {
        if (seconds !== undefined) {
            this.oldPinheadSeconds = this.currentSeconds;
            this.currentSeconds = seconds;

            offsetX = this.convertTimeToOffsetX(seconds);
        }

        this.runtimeEditor._sceneActionState.draggingTimelinePin = true;
        this.moveTimePoint(this.$pin, offsetX);
        this.runtimeEditor._sceneActionState.draggingTimelinePin = false;

        let selectedElements: string[] = this.runtimeEditor.currentElementsSelection;
        if (selectedElements.length > 0 &&
            (!couiEditor.EXPORTING_COMPONENT && !couiEditor.EXPORTING_WIDGET)) {
            this.runtimeEditor.highlightSelectedEl(null, this.runtimeEditor.currentElementsSelection);
        }
    }

    moveTimePoint(draggedElement, offsetX) {

        var oldTime = parseFloat(draggedElement.attr('data-current-time')) || 0;

        this.timelinePositions({
            calcTimePosition: offsetX
        }, draggedElement);

        if (draggedElement[0].id !== 'timeline-pin') {
            this.runtimeEditor.Keyframes.onKeyframeChange(draggedElement, oldTime, this.currentSeconds);
        } else {
            this.runtimeEditor.Keyframes.TimelineScrub.renderKeyframesInTime(oldTime, this.currentSeconds);
        }

        draggedElement.attr('data-current-time', this.currentSeconds);
    }

    timelineScrollHandler() {
        var _this = this;

        this.$timelineWrap.scroll(function () {
            var scrollPercentage = 100 * this.scrollLeft / this.scrollWidth / (1 - this.clientWidth / this.scrollWidth);

            if (+scrollPercentage > 90) {
                _this.addMoreTime();
            }
        });
    }

    /**
     * Function formating and applying milisecond values to the timeline input
     * @function setTimelineInput
     * @memberOf module:lib/animations/timeline.Timeline
     * @param {number} miliseconds - the time to apply to the timeline input in miliseconds
     */
    setTimelineInput(miliseconds) {
        function addZ(n) {
            return (n < 10 ? '0' : '') + n;
        }

        var ms = miliseconds % 1000;
        miliseconds = (miliseconds - ms) / 1000;
        var secs = miliseconds % 60;
        miliseconds = (miliseconds - secs) / 60;
        var mins = miliseconds % 60;

        var newValue = addZ(mins) + ':' + addZ(secs) + ':' + ('000' + ms).substr(-3);
        this.$timelineInput.val(newValue);
    }

    /**
     * Function formating the timeline input value to the closest 10-th of a milisecond
     * @function formatTimelineInputValue
     * @memberOf module:lib/animations/timeline.Timeline
     * @param {number} miliseconds - the time to format in miliseconds
     * @returns {number} - formated value in miliseconds
     */
    formatTimelineInputValue(miliseconds) {
        miliseconds = miliseconds < 0 ? 0 : miliseconds;
        return (miliseconds % 100) > 50 ? Math.ceil(miliseconds / 100) * 100 : Math.floor(miliseconds / 100) * 100;
    }

    /**
     * Function initializing the timeline input handling
     * @function timelineInputHandler
     * @memberOf module:lib/animations/timeline.Timeline
     */
    timelineInputHandler() {
        var _this = this;

        this.$timelineInput = helpers.getFromTimeline('#timeline-input');
        this.$timelineInput.val('00:00:000');

        this.$timelineInput.off('blur keyup');
        this.$timelineInput.on('blur keyup', function (event) {
            event.preventDefault();

            if (event.keyCode !== Enums.Keys.left &&
                event.keyCode !== Enums.Keys.right &&
                event.keyCode !== Enums.Keys.ctrl &&
                event.keyCode !== Enums.Keys.shift &&
                event.keyCode !== Enums.Keys.up &&
                event.keyCode !== Enums.Keys.down &&
                event.keyCode === Enums.Keys.enter) {

                var input = $(this);
                var timeValue = input.val().split(':');

                var mins;
                var secs;
                var ms;

                if (timeValue.length === 1) {
                    mins = 0;
                    secs = 0;
                    timeValue[0] = timeValue[0].trim() === '' ? 0 : timeValue[0];
                    ms = parseInt(timeValue[0]);
                } else if (timeValue.length === 2) {
                    mins = 0;
                    secs = parseInt(timeValue[0]);
                    ms = parseInt(timeValue[1]);
                } else {
                    mins = parseInt(timeValue[0]);
                    secs = parseInt(timeValue[1]);
                    ms = parseInt(timeValue[2]);
                }

                var currentTime = ms + secs * 1000 + mins * 60000;
                currentTime = currentTime > 3599999 ? 3599999 : currentTime;
                currentTime = _this.formatTimelineInputValue(currentTime);

                _this.timelineWidth = _this.timelineWidth < currentTime ? currentTime : _this.timelineWidth;
                _this.setPinheadPosition(null, currentTime);
                _this.setTimelineInput(currentTime);

                input.blur();
            }
        });
    }

    rulerScrollLeft() {
        var left = this.$timelineWrap.scrollLeft();
        this.$timelineWrap.scrollLeft(left - 2);
    }

    rulerScrollRight(num) {
        var step = num || 2;
        var left = this.$timelineWrap.scrollLeft();
        this.$timelineWrap.scrollLeft(left + step);
    }

    addMoreTime() {
        this.$timelineEl.width('+=' + APPEND_MORE_TIME);
        this.originalRulerWidth = this.$timelineEl.width();
        this.createTimeline();
    }

    calcTimePosition(pinOffsetX, i, step,
                     seconds, secondsStep, $element) {

        pinOffsetX -= TIMELINE_LEFT_OFFSET;

        var stepDiff = (step * this.timelineZoom);
        var halfDiff = stepDiff;
        var totalHalfStepDiff = i - halfDiff;
        var totalSeconds = seconds - secondsStep;

        if (pinOffsetX >= totalHalfStepDiff &&
            pinOffsetX <= totalHalfStepDiff + (halfDiff) ||
            pinOffsetX >= totalHalfStepDiff - (halfDiff / 2) &&
            pinOffsetX <= totalHalfStepDiff) {
            this.setPointSeconds(totalSeconds, totalHalfStepDiff, $element);
            return;
        }
    }

    setPointSeconds(seconds, positionX,
                    $element) {

        this.currentSeconds = seconds;
        if ($element[0].id === 'timeline-pin') {
            this.pinCurrentSeconds = this.currentSeconds;
        }
        this.renderPoint(positionX - this.pinHalfWidth, $element);
    }

    /**
     * @function
     * @memberOf module:lib/animations/timeline.Timeline
     * @param {object} keyframeData
     */
    deleteKeyframe(keyframeData) {
        var $propertyEl = helpers.getFromTimeline('.widget-line.' + keyframeData.className +
            '[data-property-type="' + keyframeData.prop + '"]');

        var scene = this.runtimeEditor.scene;
        var sceneAnim = scene.animations[keyframeData.id][keyframeData.className];
        var sceneKeyframe = sceneAnim.keyframes[keyframeData.group];

        // check if the keyframe
        if (sceneKeyframe) {
            var widgetProperty = sceneKeyframe[keyframeData.prop];

            delete widgetProperty[keyframeData.time];

            $propertyEl.find('[data-current-time="' + keyframeData.time + '"]')
                .remove();

            if ($.isEmptyObject(widgetProperty)) {
                delete sceneAnim.animationsData[keyframeData.group][keyframeData.prop];
                delete sceneAnim.keyframes[keyframeData.group][keyframeData.prop];
                widgetProperty = null;

                $propertyEl.remove();

                var $infoPropertyEl = helpers.getFromTimeline('.info-property-name.' +
                    keyframeData.className + '[data-timeline-info-property-name="' +
                    keyframeData.prop + '"]');
                $infoPropertyEl.remove();
            }

            if ($.isEmptyObject(sceneAnim.keyframes[keyframeData.group])) {
                delete sceneAnim.keyframes[keyframeData.group];
                delete sceneAnim.animationsData[keyframeData.group];
            }

            if ($.isEmptyObject(sceneAnim.keyframes)) {
                for (var widgetId in scene.animations) {
                    if (scene.animations[widgetId][keyframeData.className]) {
                        delete scene.animations[widgetId][keyframeData.className];
                    }
                }
            }
        }

        // undo-redo
        var actionState = this.runtimeEditor.getRedoUndoPrimaryState();
        var keyframeParams = {
            seconds: keyframeData.time,
            value: keyframeData.value
        };
        this.runtimeEditor._sceneActionState.deleteKeyframe = true;
        this.runtimeEditor.createUndoRedoCommand(
            actionState, keyframeData.id, keyframeData.group, keyframeData.group, keyframeData.prop, keyframeParams
        );

        this.runtimeEditor.exportScene();
        helpers.getFromTimeline('.info-widgets').getNiceScroll().resize();
    }

    /**
     * @function
     * @memberOf module:lib/animations/timeline.Timeline
     */
    deleteSelectedKeyframes() {
        var len = this.runtimeEditor.selectedKeyframes.length;
        for (var i = 0; i < len; i++) {
            var selectedKeyframe = this.runtimeEditor.selectedKeyframes[i];
            var keyframeData = this.getKeyframeWidgetData(selectedKeyframe);
            var keyframeCount = helpers.getFromTimeline('.keyframe[data-animation-class="' +
                keyframeData.className + '"][data-keyframe-property="' + keyframeData.prop + '"][data-current-time="' +
                keyframeData.time + '"]').length;
            this.runtimeEditor._undoCreationStepsLength = keyframeCount;

            for (var widgetAnimId in this.runtimeEditor.scene.animations) {
                if (this.runtimeEditor.scene.animations[widgetAnimId][keyframeData.className]) {

                    keyframeData.id = widgetAnimId;
                    this.deleteKeyframe(keyframeData);
                }
            }

        }

        this.runtimeEditor.selectedKeyframes.length = 0;
    }

    deleteWidget(id) {
        delete this.runtimeEditor.scene.animations[id];
        var $oldInfoEl = helpers.getFromTimeline('[data-timeline-info-widget-id="' + id + '"]');
        var $oldWidgetEl = helpers.getFromTimeline('.widgets-keyframes [data-widget-id="' + id + '"]');

        $('.k-animation-container').has('[kendotooltipid="' + id + '"]').remove();

        $oldInfoEl.remove();
        $oldWidgetEl.remove();
    }

    /**
     * Set html input value
     * @function
     * @memberOf module:lib/animations/timeline.Timeline
     * @param {string} widgetId
     * @param {string} newClassName
     * @param {string} oldClassName
     */
    setInputClassName(widgetId, newClassName, oldClassName?) {
        var switchClassName = false;
        if (!widgetId && helpers.getFromTimeline('.animation-class-name-input' +
                '[data-old-animation-class="' + oldClassName + '"]').length > 0) {
            helpers.getFromTimeline('.animation-class-name-input' +
                '[data-old-animation-class="' + oldClassName + '"]').val(newClassName);
        } else {
            var $inputClassName = helpers.getFromTimeline('[data-timeline-info-widget-id="' + widgetId + '"] ' +
                '.animation-class-name-input');
            $inputClassName.val(newClassName);
            switchClassName = true;
        }

        this.setOldAnimationClass(widgetId, newClassName, oldClassName, switchClassName);
    }

    /**
     * Removes class name animation elements from DOM
     * @function
     * @memberOf module:lib/animations/timeline.Timeline
     * @param {string} widgetId
     * @param {string} className
     */
    removeClassNameFromDOM(widgetId, className) {

        if (className && className !== '') {
            helpers.getFromTimeline('[data-timeline-info-widget-id="' + widgetId + '"] ' +
                '.info-property-name.' + className).remove();
            helpers.getFromTimeline('[data-widget-id="' + widgetId + '"] .widget-line.' + className).remove();
        }

        if ((!this.runtimeEditor.scene.animations[widgetId] ||
                this.runtimeEditor.getRedoUndoPrimaryState() !== 'new action') &&
            couiEditor.preferences.timeline.filterTimelineWidgets) {
            helpers.getFromTimeline('[data-timeline-info-widget-id="' + widgetId + '"]').remove();
            helpers.getFromTimeline('.widget-line.widget-name-line[data-widget-id="' + widgetId + '"]').remove();
        }
    }

    /**
     * @function
     * @memberOf module:lib/animations/timeline.Timeline
     * @param {string} widgetId
     * @param {string} className
     */
    setOldAnimationClass(widgetId, newClassName, oldClassName, switchClassName) {
        if (!oldClassName || switchClassName) {
            helpers.getFromTimeline('[data-timeline-info-widget-id="' + widgetId + '"] [data-old-animation-class]')
                .attr('data-old-animation-class', newClassName);
            helpers.getFromTimeline('[data-tooltip-change-class-id="' + widgetId + '"] [data-old-animation-class]')
                .attr('data-old-animation-class', newClassName);
            helpers.getFromTimeline('.k-animation-container')
                .has('[kendotooltipid="' + widgetId + '"]:not([data-tooltip-change-class-id])').remove();
        } else {
            helpers.getFromTimeline('[data-old-animation-class="' + oldClassName + '"]')
                .attr('data-old-animation-class', newClassName);
        }

        this.runtimeEditor.exportScene();
    }

    /**
     * @function
     * @memberOf module:lib/animations/timeline.Timeline
     * @param {string} widgetId
     * @param {string} oldClassName
     * @param {string} newClassName
     */
    setNewAnimationClass(widgetId, newClassName, oldClassName) {
        helpers.getFromTimeline('[data-timeline-info-widget-id] .' + oldClassName).addClass(newClassName)
            .removeClass(oldClassName);
        helpers.getFromTimeline('[data-widget-id] .' + oldClassName).addClass(newClassName)
            .removeClass(oldClassName);
        helpers.getFromTimeline('[data-animation-class="' + oldClassName + '"]')
            .attr('data-animation-class', newClassName);
        helpers.getFromTimeline('.animation-class-name-input[data-old-animation-class="' + oldClassName + '"]')
            .val(newClassName);
    }

    /**
     * Attach input handler of animation class
     * @function
     * @memberOf module:lib/animations/timeline.Timeline
     * @param {string} widgetId - widget id
     */
    editAnimationClassNameHandler(widgetId) {
        var $classNameInput = helpers.getFromTimeline('[data-timeline-info-widget-id="' + widgetId + '"] ' +
            '.animation-class-name-input');
        var _this = this;
        $classNameInput.off('keypress click focusout');
        $classNameInput.click(function () {
            $(this).addClass('input-tracked-focused');
        }).keypress(function (event) {
            if (event.which === Enums.Keys.enter) {
                $(this).blur();
            } else if (event.which === Enums.Keys.space) {
                return false;
            }
        }).focusout(function () {
            saveClassName($(this));
        });

        let kendoElement = $classNameInput.data('kendoTooltip');
        if (kendoElement) {
            kendoElement.destroy();
        }

        $classNameInput.kendoTooltip({
            autoHide: true,
            showOn: 'animationClassDuplicate',
            content: kendo.template('<div kendoTooltipID="' + widgetId + '" class="tooltip-options' +
                ' tooltip-animation-class">' +
                '<div class="animation-class-dublicate">Class name already exist. </br> ' +
                'Please choose another one.</div>' +
                '</div>'),
            width: 280,
            height: 75,
            position: 'top'
        });

        function saveClassName($this) {
            var oldClassName = $this.attr('data-old-animation-class');
            var newClassName = $this.val();

            $this.removeClass('input-tracked-focused');

            _this.runtimeEditor._sceneActionState.primaryAction = 'new action';
            _this.editAnimationClassName(widgetId, oldClassName, newClassName);
        }
    }

    /**
     * Attach input handler of widget id
     * @function
     * @memberOf module:lib/animations/timeline.Timeline
     * @param {string} widgetId - widget id
     */
    editWidgetIdHandler(widgetId: string): void {
        const $idInput = helpers.getFromTimeline('[data-timeline-info-widget-id="' + widgetId + '"] ' +
            '.info-widget-name');
        const _this = this;
        $idInput.off('keypress click focusout');
        $idInput.click(function () {
            $(this).addClass('input-tracked-focused');
        }).keypress(function (event) {
            if (event.which === Enums.Keys.enter) {
                $(this).blur();
            } else if (event.which === Enums.Keys.space) {
                return false;
            }
        }).focusout(function () {
            saveWidgetId($(this));
        });

        function saveWidgetId($this) {
            const newId = $this.val();
            const oldId = $this.attr('value');
            const widget = _this.runtimeEditor.mappedWidgets[oldId].widget;
            $this.removeClass('input-tracked-focused');
            if (newId !== widget.id) {

                _this.runtimeEditor._sceneActionState.primaryAction = 'new action';
                _this.runtimeEditor._setProperties(widget, $this, 'element-id', 'id', newId);
            }
        }
    }

    /**
     * @function
     * @memberOf module:lib/animations/timeline.Timeline
     * @param widgetId
     * @param oldClassName
     * @param newClassName
     */
    editAnimationClassName(widgetId, oldClassName, newClassName) {
        let editor = couiEditor.openFiles[couiEditor.selectedEditor];
        const filename = editor.tab.filename;
        const isComponent: boolean = filename.endsWith('.component');

        for (let i = 0; i < editor.components.state.opened.length; i++) {
            let openedComponentId = editor.components.state.opened[i];
            let sceneComponent = couiEditor.openFiles[openedComponentId].runtimeEditor.scene;
            if (sceneComponent.animationClasses[newClassName]) {
                if (newClassName !== oldClassName) {
                    this.triggerDuplicateClassError(oldClassName, widgetId);
                }
                return;
            }
        }

        if (isComponent) {
            let editorId = editor.tab.tabWidgetState.instanceOf;
            let linkedEditor = couiEditor.openFiles[editorId];
            let scene = JSON.parse(linkedEditor.file);
            if (scene.animationClasses[newClassName]) {
                if (newClassName !== oldClassName) {
                    this.triggerDuplicateClassError(oldClassName, widgetId);
                }
                return;
            }
        }

        if (!this.runtimeEditor.scene.animationClasses[newClassName] &&
            newClassName !== oldClassName) {
            this.runtimeEditor.editAnimationClassName(widgetId, oldClassName, newClassName);
        } else {
            if (newClassName !== oldClassName) {
                this.triggerDuplicateClassError(oldClassName, widgetId);
            }
        }
    }

    /**
     * Trigger message
     * @function
     * @memberOf module:lib/animations/timeline.Timeline
     * @param widgetId
     * @param oldClassName
     */
    triggerDuplicateClassError(oldClassName: string, widgetId: string) {
        var $classNameInput = helpers.getFromTimeline('[data-timeline-info-widget-id="' + widgetId + '"] ' +
            '.animation-class-name-input');
        $classNameInput.trigger('animationClassDuplicate');
        $classNameInput.val(oldClassName);
    }

    /**
     * Add widget info in timeline
     * @function
     * @memberOf module:lib/animations/timeline.Timeline
     * @param {object} widget - widget object properties
     * @param {object} options - extra data *optional
     */
    addWidget(widget, options) {

        // TODO: Validate if functionality is not needed //
        var _this = this;
        var compareCssClasses = function (className) {
            if (_this.runtimeEditor.scene.animationClasses[className]) {
                _this.initAnimationClassName(widget.id, '', className, true);
            }
        };

        var cloneTemplateTimelineInfo = couiEditor.importHtmlTemplate(this.templateTimelineInfo);
        cloneTemplateTimelineInfo.setAttribute('data-timeline-info-widget-id', widget.id);

        cloneTemplateTimelineInfo.firstElementChild.firstElementChild.id = 'anim-option-' + widget.id;
        cloneTemplateTimelineInfo.firstElementChild.firstElementChild.value = helpers.shortenString(widget.id);
        cloneTemplateTimelineInfo.firstElementChild.firstElementChild.setAttribute('value', widget.id);
        this.$infoWidgetsSection.append(cloneTemplateTimelineInfo);

        var cloneTemplateWidgetLine = couiEditor.importHtmlTemplate(this.templateWidgetLine);
        cloneTemplateWidgetLine.setAttribute('data-widget-id', widget.id);
        cloneTemplateWidgetLine.setAttribute('value', widget.id);
        helpers.getFromTimeline('.widgets-keyframes-stack').append(cloneTemplateWidgetLine);

        // TODO: Validate if functionality is not needed //
        if (options && options.initial && widget.className) {
            var widgetCssClasses = widget.className.split(' ');
            widgetCssClasses.filter(compareCssClasses);
        }

        this.createToolTipClassName(widget.id);
        this.addAnimationClassHandler(widget);
        this.removeAnimationClassHandler(widget);
        this.create();
        this.editWidgetIdHandler(widget.id);
        helpers.getFromTimeline('.info-widgets').getNiceScroll().resize();
    }

    /**
     * Initialize animation options when the widget doesn't have animation object with keyframes
     * Set className for widget
     * Set first keyframe
     * Attach input field handler
     * @function
     * @memberOf module:lib/animations/timeline.Timeline
     * @param {string} widgetId
     * @param {string} oldClassName
     * @param {string} className
     */
    initAnimationClassName(widgetId, oldClassName, className, initial = false, undoRedoGlobalClass = false) {
        this.runtimeEditor.scene.animationClasses[className] = this.runtimeEditor.scene.animationClasses[className] ||
            $.extend(true, {}, Enums.newAnimation);
        this.runtimeEditor.scene.animationClasses[className].className = className;
        this.runtimeEditor.scene.animations[widgetId] = this.runtimeEditor.scene.animations[widgetId] || {};

        if (!undoRedoGlobalClass) {
            this.runtimeEditor.switchAnimationClassName(widgetId, oldClassName, className, undefined, initial);
        }

        // set class name of input field
        this.setInputClassName(widgetId, className);

        var $widgetElement = helpers.getFromTimeline('[data-timeline-info-widget-id="' + widgetId + '"]');
        $widgetElement.find('.info-class-name').show();

        helpers.getFromTimeline('.widget-name-line[data-widget-id="' + widgetId + '"] ' +
            '.keyframe-line-class-name').show();

        // attach input handler
        this.editAnimationClassNameHandler(widgetId);
        this.editWidgetIdHandler(widgetId);
        $widgetElement.find('animation-class-name-input').show();
    }

    /**
     * @function
     * @memberOf module:lib/animations/timeline.Timeline
     * @param {object} widget
     */
    createToolTipClassName(widgetId: string) {
        var _this = this;
        this.animationSearchValue = '';
        var template = document.querySelector('#animation-class-switch-dropdown');

        var $iconChangeClassName = helpers.getFromTimeline('[data-timeline-info-widget-id="' + widgetId + '"]' +
            ' .ic-change-animation-class');

        let kendoElement = $iconChangeClassName.data('kendoTooltip');
        if (kendoElement) {
            kendoElement.destroy();
        }
        $iconChangeClassName.kendoTooltip({
            autoHide: false,
            showOn: 'click',
            content: kendo.template('<div kendoTooltipID="' + widgetId + '" class="tooltip-options' +
                ' tooltip-animation-class" ' +
                'data-tooltip-change-class-id="' + widgetId + '"></div>'),
            width: 280,
            height: 75,
            position: 'top',
            show: function () {
                var currentClassName = $('[data-timeline-info-widget-id="' + widgetId + '"] ' +
                    '.info-class-name [data-old-animation-class]').val();
                var $tooltip = $('[data-tooltip-change-class-id="' + widgetId + '"]');
                var clone = couiEditor.importHtmlTemplate(template);
                var $select = $(clone);
                $select.attr('data-old-animation-class', currentClassName);
                buildClassNameSelectDate(currentClassName, $tooltip, $select);
                _this.attachTooltipChangeClassNameHandlers($select, widgetId);
            },
            hide: function () {
                var $tooltip = $('[data-tooltip-change-class-id="' + widgetId + '"]');
                _this.resetTooltipHtml($tooltip);
                _this.animationSearchValue = '';
            }
        });

        function buildClassNameSelectDate(currentClassName, $tooltip, $select) {
            var animationClasses = _this.runtimeEditor.scene.animationClasses;
            for (var name in animationClasses) {
                var opt = document.createElement('option');
                opt.value = name;
                opt.innerHTML = name;
                if (name === currentClassName) {
                    opt.setAttribute('selected', 'selected');
                }
                $select[0].appendChild(opt);
            }
            $tooltip.append('<span class="animation-library-title">Animations Library</span>');
            $tooltip.append('<label class="options-name">Animation:</label>');
            $tooltip.append($select);

            $select.kendoDropDownList({
                height: 'auto',
                autoBind: true,
                animation: false,
                filter: 'contains',
                filtering: function (e) {
                    _this.animationSearchValue = e.filter.value;
                    attachDeleteHandler(currentClassName, $tooltip, $select);
                },
                dataTextField: 'animationClass',
                dataValueField: 'value',
                headerTemplate: '',
                valueTemplate: '',
                template: '<span>#: data.animationClass #</span> ' +
                '<a class="ic-remove-animation-class-global fa fa-remove" ' +
                'data-delete-animation-class="#: data.animationClass #"></a>',
                open: function () {
                    var $input = $('.k-list-filter > input');
                    $input.val(_this.animationSearchValue);
                    $input.trigger('keydown');

                    attachDeleteHandler(currentClassName, $tooltip, $select);
                }
            });
        }

        function attachDeleteHandler(currentClassName, $tooltip, $select) {
            window.requestAnimationFrame(function () {
                helpers.getFromTimeline('[data-delete-animation-class="select animation"]').remove();

                helpers.getFromTimeline('.ic-remove-animation-class-global').off('click');
                helpers.getFromTimeline('.ic-remove-animation-class-global').on('click', function () {
                    var $this = $(this);
                    var className = $this.attr('data-delete-animation-class');
                    if ($select.data('kendoDropDownList')) {
                        $select.data('kendoDropDownList').close();
                        $select.data('kendoDropDownList').destroy();
                    }

                    $tooltip.empty();
                    _this.runtimeEditor._sceneActionState.primaryAction = 'new action';
                    _this.removeAnimationClassName(className);
                    var clone = couiEditor.importHtmlTemplate(template);
                    var $selectClone = $(clone);
                    $selectClone.attr('data-old-animation-class', currentClassName);
                    buildClassNameSelectDate(currentClassName, $tooltip, $selectClone);

                    $selectClone.data('kendoDropDownList').open();
                    _this.runtimeEditor.exportScene();
                });
            });
        }
    }

    /**
     * remove all timeline DOM elements
     * clear all info, tooltiips, keyframes etx.
     */
    resetAllTimeline() {
        $('.info-stack').empty();
        $('.widgets-keyframes-stack').empty();
    }

    /**
     * @function
     * @memberOf module:lib/animations/timeline.Timeline
     * @param {object} classNameData
     * @private
     */
    _addGlobalAnimationClassName(classNameData) {
        this.runtimeEditor.scene.animationClasses[classNameData.className] = classNameData;

        this.runtimeEditor._sceneActionState.addAnimationClassGlobal = true;
        this.runtimeEditor.saveUndoRedo(null, null, null, {className: classNameData.className}, null);
        this.runtimeEditor.Animations.loadKeyframes(this.runtimeEditor.scene.animationClasses, false, true);
    }

    /**
     * @function
     * @memberOf module:lib/animations/timeline.Timeline
     * @param {string} className
     * @private
     */
    _deleteGlobalAnimationClassName(className) {
        var oldClassObject = this.runtimeEditor.scene.animationClasses[className];
        delete this.runtimeEditor.scene.animationClasses[className];

        this.runtimeEditor._sceneActionState.deleteAnimationClassGlobal = true;
        this.runtimeEditor.saveUndoRedo(null, null, null, oldClassObject, null);
    }

    /**
     * Remove selected animation class globally
     * Loop and remove the selected class from all affected widgets
     * save to undo - redo
     * @function
     * @memberOf module:lib/animations/timeline.Timeline
     * @param className
     */
    removeAnimationClassName(className) {
        var oldClassObject = this.runtimeEditor.scene.animationClasses[className];
        delete this.runtimeEditor.scene.animationClasses[className];
        var animations = this.runtimeEditor.scene.animations;

        var undoLen = 1;
        var widget;

        for (widget in animations) {
            if (animations[widget].className === className) {
                undoLen++;
            }
        }

        this.runtimeEditor._undoCreationStepsLength = undoLen;

        this.runtimeEditor._sceneActionState.deleteAnimationClassGlobal = true;
        this.runtimeEditor.saveUndoRedo(null, null, null, oldClassObject, null);

        for (widget in animations) {
            if (animations[widget].className === className) {
                this.removeAnimationClassFromWidget(widget, className, true);
            }
        }
    }

    /**
     * @function
     * @memberOf module:lib/animations/timeline.Timeline
     * @param {object} $select
     * @param {string} widgetId
     */
    attachTooltipChangeClassNameHandlers($select, widgetId) {
        var _this = this;

        $select.on('change', function () {
            _this.runtimeEditor._sceneActionState.primaryAction = 'new action';
            _this.runtimeEditor._sceneActionState.switchAnimationClass = true;
            var $select = $(this).parents('.tooltip-options').find('select.select-animation-class');
            var oldClassName = $select.attr('data-old-animation-class');
            var newClassName = $select.val();
            if (newClassName !== 'remove-animation-class') {
                if (!_this.runtimeEditor.scene.animations[widgetId]) {
                    _this.editAnimationClassNameHandler(widgetId);
                }
                _this.runtimeEditor.switchAnimationClassName(widgetId, oldClassName, newClassName);

            } else {
                _this.removeAnimationClassFromWidget(widgetId, oldClassName, true);
            }
            _this.runtimeEditor._sceneActionState.switchAnimationClass = false;
        });
    }

    /**
     * @function
     * @memberOf module:lib/animations/timeline.Timeline
     * @param {object} widget
     */
    addAnimationClassHandler(widget) {
        var _this = this;
        var $iconAddClassName = helpers.getFromTimeline('[data-timeline-info-widget-id="' + widget.id + '"] ' +
            '.ic-add-animation-class');

        $iconAddClassName.off('click');
        $iconAddClassName.on('click', function () {
            _this.runtimeEditor._sceneActionState.addNewAnimationClassName = true;
            var oldClassName = $(this).parents('.widget-timeline-holder').find('.animation-class-name-input').val();
            var newClassName = couiEditor.generateClassName();
            _this.runtimeEditor._sceneActionState.primaryAction = 'new action';
            _this.runtimeEditor._sceneActionState.switchAnimationClass = true;
            _this.initAnimationClassName(widget.id, oldClassName, newClassName);
        });
    }

    /**
     * @function
     * @memberOf module:lib/animations/timeline.Timeline
     * @param {object} widgets
     */
    removeAnimationClassHandler(widget) {
        var _this = this;
        var $iconDeleteClassName = helpers.getFromTimeline('[data-timeline-info-widget-id="' + widget.id + '"]' +
            ' .ic-remove-animation-class');

        $iconDeleteClassName.off('click');
        $iconDeleteClassName.on('click', function () {
            var className = $(this).parents('.info-class-name')
                .find('.animation-class-name-input').val();
            _this.runtimeEditor._sceneActionState.primaryAction = 'new action';
            _this.removeAnimationClassFromWidget(widget.id, className, true);
        });
    }

    /**
     * @function
     * @memberOf module:lib/animations/timeline.Timeline
     * @param {string} widgeId
     * @param {string} className
     */
    removeAnimationClassFromWidget(widgetId, oldClassName, getClassesFromWidget) {
        var newStringClasses = '';
        delete this.runtimeEditor.scene.animations[widgetId];

        if (getClassesFromWidget) {
            var widgetClasses = this.runtimeEditor.mappedWidgets[widgetId].widget.className;
            if (newStringClasses === '') {
                newStringClasses = widgetClasses.replace(oldClassName, '').trim();
            }
        }

        this.removeClassNameFromDOM(widgetId, oldClassName);
        this.setInputClassName(widgetId, '', oldClassName);
        this.runtimeEditor.setClassName(null, widgetId, newStringClasses, false);

        helpers.getFromTimeline('[data-timeline-info-widget-id="' + widgetId + '"] .info-class-name').hide();
        helpers.getFromTimeline('.widget-name-line[data-widget-id="' + widgetId + '"] ' +
            '.keyframe-line-class-name').hide();
    }

    getTooltipHtml(widget) {
        return couiEditor.Handlebars.compile(tooltipHbs)(widget);
    }

    resetTooltipHtml(tooltip: JQuery): void {
        tooltip.empty();
        tooltip.parent().parent().find('.k-list-container.k-popup.k-group.k-reset').remove();
    }

    createTooltip($element, widget, group, property) {
        var _this = this;

        let kendoElement = $element.data('kendoTooltip');
        if (kendoElement) {
            kendoElement.destroy();
        }

        $element.kendoTooltip({
            autoHide: false,
            showOn: 'click',
            content: kendo.template('<div class="tooltip-options" kendoTooltipID="' + widget.id + '" ' +
                'data-tooltip-id="' + widget.id +
                '" data-tooltip-property-name="' + property +
                '" data-tooltip-group-name="' + group + '"></div>'),
            width: 280,
            height: 135,
            position: 'top',
            show: function () {
                var currentClassName = $element
                    .parents('[data-timeline-info-widget-id="' + widget.id + '"]')
                    .find('.animation-class-name-input')
                    .val();
                var animData = widget[currentClassName].animationsData[group][property];
                var tootipHtml = _this.getTooltipHtml(animData);
                var $tooltip = $('[data-tooltip-id="' + widget.id + '"]');
                $tooltip.attr('data-animation-class', currentClassName);
                $tooltip.append(tootipHtml);
                $tooltip.find('select').kendoDropDownList({
                    animation: false
                });

                _this.attachTooltipHandlers(widget.id);
            },
            hide: function () {
                var $tooltip = $('[data-tooltip-id="' + widget.id + '"]');
                _this.resetTooltipHtml($tooltip);
            }
        });
    }

    attachTooltipHandlers(id) {
        var _this = this;

        helpers.getFromTimeline('[data-tooltip-id="' + id + '"] select.animation-timing')
            .on('change', function (event) {
                _this.runtimeEditor._sceneActionState.primaryAction = 'new action';
                var data = _this.collectAnimationData(event);
                _this.setAnimationTiming(data.id, data.group, data.property, data.value, data.className);
            });

        helpers.getFromTimeline('[data-tooltip-id="' + id + '"] input.animation-iteration')
            .on('input', function (event) {
                _this.runtimeEditor._sceneActionState.primaryAction = 'new action';
                var data = _this.collectAnimationData(event);
                _this.setAnimationIteration(data.id, data.group, data.property, data.value, data.className);
            });

        helpers.getFromTimeline(`[data-tooltip-id="${id}"] input.animation-iteration-loop`)
            .on('change', function (event) {
                let value = 'infinite';
                const $inputIteration = helpers.getFromTimeline(`[data-tooltip-id="${id}"] input.animation-iteration`);
                if (!this.checked) {
                    value = $inputIteration.val();
                }

                _this.runtimeEditor._sceneActionState.primaryAction = 'new action';
                var data = _this.collectAnimationData(event);
                _this.setAnimationIteration(data.id, data.group, data.property, value, data.className);
            });

        helpers.getFromTimeline('[data-tooltip-id="' + id + '"] select.animation-direction')
            .on('change', function (event) {
                _this.runtimeEditor._sceneActionState.primaryAction = 'new action';
                var data = _this.collectAnimationData(event);
                _this.setAnimationDirection(data.id, data.group, data.property, data.value, data.className);
            });

        helpers.getFromTimeline('[data-tooltip-id="' + id + '"] input.animation-name')
            .on('input', function (event) {
                _this.runtimeEditor._sceneActionState.primaryAction = 'new action';
                var data = _this.collectAnimationData(event);
                _this.setAnimationName(data.id, data.group, data.property, data.value, data.className);
            });
    }

    collectAnimationData(event) {
        var $parent = $(event.target).parents('.tooltip-options');

        return {
            className: $parent.attr('data-animation-class'),
            value: $(event.target).val(),
            id: $parent.attr('data-tooltip-id'),
            group: $parent.attr('data-tooltip-group-name'),
            property: $parent.attr('data-tooltip-property-name')
        };
    }

    setAnimationTiming(id, group,
                       property, value, className) {
        var animObj = this.runtimeEditor.scene.animationClasses[className];
        var params = {
            oldValue: animObj.animationsData[group][property].timing,
            className: className
        };

        if (group === 'transform' || group === '-webkit-filter') {
            for (let prop in animObj.animationsData[group]) {
                animObj.animationsData[group][prop].timing = value;
            }
        } else {
            animObj.animationsData[group][property].timing = value;
        }

        this.runtimeEditor.exportScene();

        this.createUndoRedoAnimationOptions(id, group,
            property, params, 'Timing');
    }

    setAnimationIteration(id, group,
                          property, value, className) {
        var animObj = this.runtimeEditor.scene.animationClasses[className];
        var params = {
            oldValue: animObj.animationsData[group][property].iteration,
            className: className
        };

        const $inputIteration = helpers.getFromTimeline('[data-tooltip-id="' + id + '"] input.animation-iteration');
        if (value === 'infinite') {
            $inputIteration.prop('disabled', true);
        } else {
            $inputIteration.prop('disabled', false);
        }

        if (group === 'transform' || group === '-webkit-filter') {
            for (let prop in animObj.animationsData[group]) {
                animObj.animationsData[group][prop].iteration = value;
            }
        } else {
            animObj.animationsData[group][property].iteration = value;
        }

        this.runtimeEditor.exportScene();

        this.createUndoRedoAnimationOptions(id, group,
            property, params, 'Iteration');
    }

    setAnimationDirection(id, group,
                          property, value, className) {
        var animObj = this.runtimeEditor.scene.animationClasses[className];
        var params = {
            oldValue: animObj.animationsData[group][property].direction,
            className: className
        };

        if (group === 'transform' || group === '-webkit-filter') {
            for (let prop in animObj.animationsData[group]) {
                animObj.animationsData[group][prop].direction = value;
            }
        } else {
            animObj.animationsData[group][property].direction = value;
        }

        this.runtimeEditor.exportScene();

        this.createUndoRedoAnimationOptions(id, group,
            property, params, 'Direction');
    }

    setAnimationName(id, group,
                     property, value, className) {
        var animObj = this.runtimeEditor.scene.animationClasses[className];
        var params = {
            oldValue: animObj.animationsData[group][property].name,
            className: className
        };

        if (group === 'transform' || group === '-webkit-filter') {
            for (let prop in animObj.animationsData[group]) {
                animObj.animationsData[group][prop].name = value;
            }
        } else {
            animObj.animationsData[group][property].name = value;
        }

        this.runtimeEditor.exportScene();

        this.createUndoRedoAnimationOptions(id, group,
            property, params, 'Name');
    }

    createUndoRedoAnimationOptions(id, group,
                                   property, value, type) {

        var actionState = this.runtimeEditor.getRedoUndoPrimaryState();
        var animationParams = {
            type: type,
            value: value.oldValue,
            className: value.className
        };

        this.runtimeEditor._sceneActionState.setAnimationOptions = true;
        this.runtimeEditor.createUndoRedoCommand(actionState, id, group, group, property, animationParams);
    }

    /**
     * @function reinitKendoTooltips
     * This method reinitializez the kendo tooltips in case of id change of the elements
     * @memberOf module:lib/animations/timeline.Timeline
     * @param {string} oldId - the old ID of the Kendo Tooltip
     * @param {string} newId - the new ID of the Kendo Tooltip
     * @param {JQuery} tooltipElements - the JQuery selector holding the tooltip elements
     * @return void
     */
    reinitKendoTooltips(oldId: string, newId: string, tooltipElements: JQuery): void {
        $('[kendoTooltipID="' + oldId + '"]').attr('kendoTooltipID', newId);
        $('[data-tooltip-id="' + oldId + '"]').attr('data-tooltip-id', newId);
        $('[data-tooltip-change-class-id="' + oldId + '"]').attr('data-tooltip-change-class-id', newId);

        this.editAnimationClassNameHandler(newId);
        this.createToolTipClassName(newId);

        if (tooltipElements.length > 0) {
            let widget = $.extend(true, {}, this.runtimeEditor.scene.animations[oldId]);
            widget.id = newId;
            for (var i = 0; i < tooltipElements.length; i++) {
                let properties = $(tooltipElements[i]).find('[data-timeline-info-property-name]');
                for (var k = 0; k < properties.length; k++) {
                    let property = $(properties[k]).find('.property-name-text').text();
                    if (property !== '') {
                        let $element = $(properties[k]).find('[data-role="tooltip"].animation-options');
                        let group = Enums.WidgetGroups[property];
                        this.createTooltip($element, widget, group, property);
                    }
                }
            }
        }
    }

    /**
     * @function
     * @memberOf module:lib/animations/timeline.Timeline
     * @param oldId
     * @param newId
     */
    editId(oldId: string, newId: string) {
        var $oldInfoEl = helpers.getFromTimeline('[data-timeline-info-widget-id="' + oldId + '"]');
        var $oldWidgetEl = helpers.getFromTimeline('[data-widget-id="' + oldId + '"]');
        var $keyframeWidgetIds = helpers.getFromTimeline('[data-keyframe-widget-id="' + oldId + '"]');
        var $oldDataTooltip = helpers.getFromTimeline('[data-tooltip-id="' + oldId + '"]');

        const $inputId = helpers.getFromTimeline('[data-timeline-info-widget-id="' + oldId + '"] .info-widget-name');
        $inputId.val(helpers.shortenString(newId));
        $inputId.attr('value', helpers.shortenString(newId));

        $oldInfoEl.attr('data-timeline-info-widget-id', newId);
        $oldWidgetEl.attr('data-widget-id', newId);
        $oldDataTooltip.attr('data-tooltip-id', newId);
        $keyframeWidgetIds.attr('data-keyframe-widget-id', newId);

        this.reinitKendoTooltips(oldId, newId, $oldInfoEl);
    }
}




