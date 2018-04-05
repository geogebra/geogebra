/**
 * Drag elements
 * @module lib/drag_element
 * @requires module:scripts/main
 * @requires module:lib/function_helpers
 */

'use strict';
import helpers from './function_helpers';
import couiEditor from '../scripts/main';
import Transform from './transform';

declare let $;

module DragElements {

    var matrixEl;
    var curTransform;
    var scale;
    var startMouseX;
    var startMouseY;
    var startElTop = [];
    var startElLeft = [];
    var startRealElLeft = [];
    var startRealElTop = [];
    var realElementIds = [];
    var realElement = [];
    var masks;
    var masksLength;
    var parentIds = [];

    var snapBounds = [];
    var selectedElBounds = [];

    var hasSnappedHorizontal = false;
    var hasSnappedVertical = false;
    let transform = new Transform();

    function hookEvent(element, eventName, callback) {
        if (typeof(element) === 'string') {
            element = document.getElementById(element);
        }
        if (element === null) {
            return;
        }
        element.addEventListener(eventName, callback, false);
    }

    function unhookEvent(element, eventName, callback) {
        if (typeof(element) === 'string') {
            element = document.getElementById(element);
        }
        if (element === null) {
            return;
        }
        element.removeEventListener(eventName, callback, false);
    }

    function cancelEvent(e) {
        e = e ? e : window.event;
        if (e.stopPropagation) {
            e.stopPropagation();
        }
        if (e.preventDefault) {
            e.preventDefault();
        }
        e.cancelBubble = true;
        e.cancel = true;
        e.returnValue = false;
        return false;
    }

    function applyDrag() {
        hasSnappedHorizontal = false;
        hasSnappedVertical = false;

        var newPos = [];
        var diffSX;
        var diffSY;

        for (var i = 0; i < masksLength; i++) {

            var computedElTop;
            var computedElLeft;

            if (parentIds[i] !== null) {
                computedElTop = masks[i].offsetTop;
                computedElLeft = masks[i].offsetLeft;
            }

            diffSX = (window.mouseCoordsX - startMouseX) / scale;
            diffSY = (window.mouseCoordsY - startMouseY) / scale;

            if (parentIds[i] !== null) {

                const absTranformM = helpers.getElementAbsoluteTransformV2(realElement[i]);
                const coords = helpers.getTransformedCoordsGlobal(diffSX, diffSY, startMouseX, startMouseY, absTranformM);
                newPos.push(coords);
            } else {
                newPos.push(null);
            }
        }

        for (var j = 0; j < masksLength; j++) {

            var parentElementId = realElement[j].id;
            var allowLeftMovement = true;
            var allowTopMovement = true;

            while (parentElementId !== 'scene' && realElementIds.indexOf(parentElementId) !== -1) {
                var $parentElement = $('#' + parentElementId);

                allowLeftMovement = (realElement[j].style.left !== 'auto' && realElement[j].style.left !== '');
                allowTopMovement = (realElement[j].style.top !== 'auto' && realElement[j].style.top !== '');

                parentElementId = $parentElement[0].parentElement.id;
            }

            //update left
            if (allowLeftMovement && !hasSnappedHorizontal) {

                if (parentIds[j] !== null && newPos[j] !== null) {
                    realElement[j].style.left = (startRealElLeft[j] + newPos[j].x) + 'px';
                }
            }
            //update top
            if (allowTopMovement && !hasSnappedVertical) {

                if (parentIds[j] !== null && newPos[j] !== null) {
                    realElement[j].style.top = (startRealElTop[j] + newPos[j].y) + 'px';
                }
            }


        }

        for (var h = 0; h < masksLength; h++) {
            let elId = masks[h].attributes['data-widget-id'].nodeValue;
            let editorId = couiEditor.selectedEditor;
            let runtimeEditor = couiEditor.openFiles[editorId].runtimeEditor;
            let element = runtimeEditor.mappedWidgets[elId].widget;

            if (element.transformed_position) {
                transform.transform(element);
                let maskPosition = helpers.getTransformedElMaskPos(element);
                masks[h].style.width = maskPosition.width + 'px';
                masks[h].style.height = maskPosition.height + 'px';
                masks[h].style.top = maskPosition.top + 'px';
                masks[h].style.left = maskPosition.left + 'px';
            }
        }

        if (couiEditor.openFiles[couiEditor.selectedEditor].tab.snapOn && snapBounds.length !== 0) {

            var elementBounds = getBoundingBoxes(realElement, true)[0];

            for (var m = 0; m < snapBounds.length; m++) {

                var snapBox = snapBounds[m];

                // // TOP - TOP
                var options = {
                    dragValue: elementBounds.top,
                    stationaryValue: snapBox.top,
                    styleValueDrag: 'top',
                    elementBounds: elementBounds,
                    snapBox: snapBox
                };

                adjustSnap(options);

                // // TOP - BOTTOM
                options.stationaryValue = snapBox.top + snapBox.height;
                adjustSnap(options);

                // // BOTTOM - TOP
                options.styleValueDrag = 'bottom';
                options.stationaryValue = snapBox.top - elementBounds.height;
                adjustSnap(options);

                // // BOTTOM - BOTTOM
                options.stationaryValue = snapBox.top + snapBox.height - elementBounds.height;
                adjustSnap(options);

                // // LEFT - LEFT
                options.dragValue = elementBounds.left;
                options.stationaryValue = snapBox.left;
                options.styleValueDrag = 'left';
                adjustSnap(options);

                // // LEFT - RIGHT
                options.stationaryValue = snapBox.left + snapBox.width;
                adjustSnap(options);

                // // RIGHT - LEFT
                options.styleValueDrag = 'right';
                options.stationaryValue = snapBox.left - elementBounds.width;
                adjustSnap(options);

                // // RIGHT - RIGHT
                options.stationaryValue = snapBox.left + snapBox.width - elementBounds.width;
                adjustSnap(options);
            }

            if (!hasSnappedHorizontal) {
                $('#snap-line-horizontal').css({'display': 'none'});
            }

            if (!hasSnappedVertical) {
                $('#snap-line-vertical').css({'display': 'none'});
            }
        }
    }

    /**
     * Function comparing drag values and stationary values, setting the group snap and drawing the lines.
     *
     * @function adjustSnap
     * @param {object} options - settings for the comparing values, setting the group snap and drawing the lines
     */
    function adjustSnap(options) {
        var stationaryValue = options.stationaryValue;
        var dragValue = options.dragValue;
        var styleValue = options.styleValueDrag;

        if (dragValue > (stationaryValue - 5) &&
            dragValue < (stationaryValue + 5)) {

            if (styleValue === 'left' || styleValue === 'right') {
                if (hasSnappedVertical) {
                    return;
                }
            } else {
                if (hasSnappedHorizontal) {
                    return;
                }
            }

            positionGroupSnap(options);
            drawSnapLine(options);
        }
    }

    /**
     * Function drawing the snap helper lines.
     *
     * @function drawSnapLine
     * @param {object} options - settings for the property style and delta values
     */
    function drawSnapLine(options) {
        var styleValue = options.styleValueDrag;
        var snapBox = options.snapBox;
        var elementBounds = options.elementBounds;
        var stationaryValue = options.stationaryValue;

        if (styleValue === 'top' || styleValue === 'bottom') {
            stationaryValue = styleValue === 'top' ? stationaryValue : stationaryValue + elementBounds.height;

            var adjustedLeft = Math.min((snapBox.left + snapBox.width / 2),
                (elementBounds.left + elementBounds.width / 2));
            var adjustedWidth = Math.abs(adjustedLeft - Math.max((snapBox.left + snapBox.width / 2),
                (elementBounds.left + elementBounds.width / 2)));

            $('#snap-line-horizontal').css({
                'display': 'block',
                'left': adjustedLeft + 'px',
                'top': stationaryValue + 'px',
                'width': adjustedWidth + 'px'
            });

            hasSnappedHorizontal = true;
        } else {
            stationaryValue = styleValue === 'left' ? stationaryValue : stationaryValue + elementBounds.width;
            var adjustedTop = Math.min((snapBox.top + snapBox.height / 2),
                (elementBounds.top + elementBounds.height / 2));
            var adjustedHeight = Math.abs(adjustedTop - Math.max((snapBox.top + snapBox.height / 2),
                (elementBounds.top + elementBounds.height / 2)));

            $('#snap-line-vertical').css({
                'display': 'block',
                'left': stationaryValue + 'px',
                'top': adjustedTop + 'px',
                'height': adjustedHeight + 'px'
            });

            hasSnappedVertical = true;
        }
    }

    /**
     * Function getting all bounding boxes of nested and none nested group or single elements.
     *
     * @function getBoundingBoxes
     * @param {object} selectedEl - widget element selected
     * @param {boolean} merge - values specifying if a single el is expected
     */
    function getBoundingBoxes(selectedEl, merge?) {
        var boxes = [];

        var runtimeEditor = couiEditor.openFiles[couiEditor.selectedEditor].runtimeEditor;

        for (var i = 0; i < selectedEl.length; i++) {
            var $element = $('#' + selectedEl[i].id);
            var tempElement = $element[0];
            var currentScene = $('#scene');

            var absolutePos = runtimeEditor.getWidgetBoundingRect(tempElement);
            var absolutePosFromScene = runtimeEditor.getWidgetBoundingRect(currentScene[0]);

            var absoluteRotation = helpers.rotationInfo(tempElement);

            absolutePosFromScene.left = absolutePosFromScene.left + parseFloat(currentScene.css('border-left-width'));
            absolutePosFromScene.top = absolutePosFromScene.top + parseFloat(currentScene.css('border-top-width'));

            var boundingBox: any = {
                width: absolutePos.right - absolutePos.left,
                height: absolutePos.bottom - absolutePos.top,
                left: absolutePos.left - absolutePosFromScene.left,
                top: absolutePos.top - absolutePosFromScene.top
            };

            var computedEl = getComputedStyle(tempElement, null);

            var elProps = {
                left: parseFloat(computedEl.getPropertyValue('border-left-width')) +
                parseFloat(computedEl.getPropertyValue('margin-left')),
                right: parseFloat(computedEl.getPropertyValue('border-right-width')) +
                parseFloat(computedEl.getPropertyValue('margin-right')),
                top: parseFloat(computedEl.getPropertyValue('border-top-width')) +
                parseFloat(computedEl.getPropertyValue('margin-top')),
                bottom: parseFloat(computedEl.getPropertyValue('border-bottom-width')) +
                parseFloat(computedEl.getPropertyValue('margin-bottom'))
            };

            var extraOffsetLeft = elProps.left + elProps.right;
            var extraOffsetTop = elProps.top + elProps.bottom;

            var originalWidth = $element.width() + extraOffsetLeft +
                parseFloat(computedEl.getPropertyValue('padding-right')) +
                parseFloat(computedEl.getPropertyValue('padding-left'));

            var originalHeight = $element.height() + extraOffsetTop +
                parseFloat(computedEl.getPropertyValue('padding-top')) +
                parseFloat(computedEl.getPropertyValue('padding-bottom'));

            var angle = absoluteRotation.deg * Math.PI / 180,
                sin = Math.sin(angle),
                cos = Math.cos(angle);

            var x1 = cos * originalWidth,
                y1 = sin * originalWidth;

            var x2 = -sin * originalHeight,
                y2 = cos * originalHeight;

            var x3 = cos * originalWidth - sin * originalHeight,
                y3 = sin * originalWidth + cos * originalHeight;

            var minX = Math.min(0, x1, x2, x3),
                maxX = Math.max(0, x1, x2, x3),
                minY = Math.min(0, y1, y2, y3),
                maxY = Math.max(0, y1, y2, y3);

            boundingBox.deltaWidth = (maxX - minX - originalWidth) / 2;
            boundingBox.deltaHeight = (maxY - minY - originalHeight) / 2;

            // ADJUST TO RECURSE INSIDE WIDGETS POSITION //

            if (tempElement.parentElement.id !== 'scene') {
                boundingBox.isChild = true;
            } else {
                boundingBox.isChild = false;
            }

            boxes.push(boundingBox);
        }

        selectedElBounds = boxes;

        if (merge && selectedElBounds.length > 1) {
            var _tempBounds: any = {
                'left': [],
                'top': [],
                'right': [],
                'bottom': [],
                'deltaWidth': [],
                'deltaHeight': []
            };

            for (var m = 0; m < boxes.length; m++) {
                _tempBounds.left.push(boxes[m].left);
                _tempBounds.top.push(boxes[m].top);
                _tempBounds.right.push(boxes[m].left + boxes[m].width);
                _tempBounds.bottom.push(boxes[m].top + boxes[m].height);
                _tempBounds.deltaWidth.push(boxes[m].deltaWidth);
                _tempBounds.deltaHeight.push(boxes[m].deltaHeight);
            }

            _tempBounds.left = Math.min.apply(Math, _tempBounds.left);
            _tempBounds.top = Math.min.apply(Math, _tempBounds.top);
            _tempBounds.width = Math.max.apply(Math, _tempBounds.right) - _tempBounds.left;
            _tempBounds.height = Math.max.apply(Math, _tempBounds.bottom) - _tempBounds.top;

            _tempBounds.deltaWidth = Math.max.apply(Math, _tempBounds.deltaWidth);
            _tempBounds.deltaHeight = Math.max.apply(Math, _tempBounds.deltaHeight);

            delete _tempBounds.right;
            delete _tempBounds.bottom;

            return [_tempBounds];
        }
        return boxes;
    }

    /**
     * Function setting the dragged values to the snap values.
     *
     * @function positionGroupSnap
     * @param {object} options - settings for the property style and delta values.
     * @param {float} movementAdjustment - value expressing the snapped and grad differences
     */
    function positionGroupSnap(options) {
        var styleValue = options.styleValueDrag;
        var stationaryValue = options.stationaryValue;

        var rotAdjustment, elementVal;

        if (styleValue === 'left' || styleValue === 'right') {
            styleValue = 'left';
            rotAdjustment = 'deltaWidth';
        } else {
            styleValue = 'top';
            rotAdjustment = 'deltaHeight';
        }

        var parentAdjustment = options.elementBounds[styleValue];

        for (var i = 0; i < realElement.length; i++) {
            elementVal = stationaryValue + selectedElBounds[i][rotAdjustment] +
                selectedElBounds[i][styleValue] - parentAdjustment;

            if (selectedElBounds[i].isChild && realElementIds.indexOf(realElement[i].parentElement.id) === -1) {

                var originalTop = parseFloat(realElement[i].style.top);
                var originalLeft = parseFloat(realElement[i].style.left);

                var absoluteRotation = helpers.rotationInfo(realElement[i].parentElement, 'single');
                var absolutePos = helpers.getAbsolutePosition(realElement[i], 'scene');

                var newTransform1 =
                    helpers.getTransformedCoordsRotation(absolutePos.left, absolutePos.top, absoluteRotation.rad);

                absolutePos[styleValue] = absolutePos[styleValue] - (parentAdjustment - stationaryValue);

                var newTransform2 =
                    helpers.getTransformedCoordsRotation(absolutePos.left, absolutePos.top, absoluteRotation.rad);

                realElement[i].style.top = originalTop + newTransform2.y - newTransform1.y + 'px';
                realElement[i].style.left = originalLeft + newTransform2.x - newTransform1.x + 'px';

                masks[i].style[styleValue] =
                    parseFloat(masks[i].style[styleValue]) - (parentAdjustment - stationaryValue) + 'px';

            } else if (!selectedElBounds[i].isChild) {

                var originalVal = parseFloat(realElement[i].style[styleValue]);

                realElement[i].style[styleValue] = elementVal + 'px';
                masks[i].style[styleValue] =
                    parseFloat(masks[i].style[styleValue]) - (originalVal - elementVal) + 'px';
            }
        }
    }


    export function dragObject(element, realEl, attachElement, lowerBound, upperBound,
                               startCallback, moveCallback, endCallback, attachLater) {
        realElement = new Array(realEl);

        if (lowerBound !== null && upperBound !== null) {
            var temp = lowerBound.Min(upperBound);
            upperBound = lowerBound.Max(upperBound);
            lowerBound = temp;
        }

        var cursorStartPos = null;
        var dragging: boolean = false;
        var listening: boolean = false;
        var disposed: boolean = false;

        function dragStart(eventObj) {
            var currentSelectedEditor = couiEditor.openFiles[couiEditor.selectedEditor];

            var scenePanning = currentSelectedEditor.runtimeEditor.isInPanningMode;
            currentSelectedEditor.runtimeEditor.inputSearch.focusout();

            if (dragging || !listening || disposed) {
                return;
                // prevent elements from dragging when scene is in panning mode
            } else if (scenePanning) {
                dragStop();
                return;
            }

            dragging = true;

            if (startCallback !== null) {
                startCallback();
            }

            matrixEl = $('#scene')[0];
            curTransform = new WebKitCSSMatrix(window.getComputedStyle(matrixEl).webkitTransform);
            scale = curTransform.a;

            startMouseX = window.mouseCoordsX;
            startMouseY = window.mouseCoordsY;
            masks = document.getElementsByTagName('mask');
            masksLength = masks.length;

            parentIds = currentSelectedEditor.runtimeEditor.currentParentElementsSelection;

            realElementIds = currentSelectedEditor.runtimeEditor.currentElementsSelection;

            if (couiEditor.openFiles[couiEditor.selectedEditor].tab.snapOn) {

                // FILTERING ELEMENTS TO SNAP TO (UNSELECTED) //
                var currentMappedWidgets = currentSelectedEditor.runtimeEditor.mappedWidgets;

                var snapBoundsElement = $.map(currentMappedWidgets, function (value) {
                    if (realElementIds.indexOf(value.widget.id) === -1) {
                        return value.widget;
                    }
                });

                // FILTERING ELEMENTS NESTED INSIDE THE SELECTED ELEMENTS //
                var realElementChildrenIds = realElementIds.map(function (value) {
                    if (currentMappedWidgets[value].widget.children.length > 0) {
                        return currentMappedWidgets[value].widget.children.map(function (result) {
                            return result.id;
                        });
                    }
                });

                realElementChildrenIds = [].concat.apply([], realElementChildrenIds);

                snapBoundsElement = snapBoundsElement.filter(function (value) {
                    if (realElementChildrenIds.indexOf(value.id) === -1) {
                        return true;
                    }
                    return false;
                });

                // CREATING THE SNAPBOUND //

                snapBounds = getBoundingBoxes(snapBoundsElement);

                $('<div/>', {id: 'snap-line-horizontal'}).css({
                    'background-color': '#d973d9',
                    'border-radius': '10px',
                    'position': 'absolute',
                    'height': 1 / scale + 'px',
                    'display': 'none'
                }).appendTo('#scene');

                $('<div/>', {id: 'snap-line-vertical'}).css({
                    'background-color': '#d973d9',
                    'border-radius': '10px',
                    'position': 'absolute',
                    'width': 1 / scale + 'px',
                    'display': 'none'
                }).appendTo('#scene');
            }

            for (var i = 0; i < masksLength; i++) {
                realElement[i] = document.getElementById(realElementIds[i]);

                /* tslint:disable */
                startElTop[i] = masks[i]['offsetTop'];
                startElLeft[i] = masks[i]['offsetLeft'];
                /* tslint:enable */

                if (parentIds[i] !== null) {
                    startRealElTop[i] = realElement[i]['offsetTop'];
                    startRealElLeft[i] = realElement[i]['offsetLeft'];
                }
            }

            applyDrag();

            hookEvent(document, 'mousemove', dragGo);
            hookEvent(document, 'mouseup', dragStopHook);

            return cancelEvent(eventObj);
        }

        function dragGo(eventObj) {
            // disable dragging when panning is activated
            if (disposed || eventObj.altKey) {
                return;
            }

            applyDrag();

            if (moveCallback !== null) {
                moveCallback();
            }

            return cancelEvent(event);
        }

        function dragStopHook(eventObj) {
            dragStop();
            return cancelEvent(eventObj);
        }

        function dragStop() {
            if (!dragging || disposed) {
                return;
            }

            $('#snap-line-horizontal').remove();
            $('#snap-line-vertical').remove();

            selectedElBounds = [];
            hasSnappedHorizontal = false;
            hasSnappedVertical = false;

            unhookEvent(document, 'mousemove', dragGo);
            unhookEvent(document, 'mouseup', dragStopHook);

            cursorStartPos = null;
            if (endCallback !== null) {
                endCallback(element);
            }
            dragging = false;
        }

        window.dispose = function () {
            if (disposed) {
                return;
            }
            window.stopListening(true);
            element = null;
            attachElement = null;
            lowerBound = null;
            upperBound = null;
            startCallback = null;
            moveCallback = null;
            endCallback = null;
            disposed = true;
        };

        window.startListening = function () {
            if (listening || disposed) {
                return;
            }
            listening = true;
            hookEvent(attachElement, 'mousedown', dragStart);
        };

        window.stopListening = function (stopCurrentDragging) {
            if (!listening || disposed) {
                return;
            }
            unhookEvent(attachElement, 'mousedown', dragStart);
            listening = false;

            if (stopCurrentDragging && dragging) {
                dragStop();
            }
        };

        window.isDragging = function () {
            return dragging;
        };
        window.isListening = function () {
            return listening;
        };
        window.isDisposed = function () {
            return disposed;
        };

        if (typeof(attachElement) === 'string') {
            attachElement = document.getElementById(attachElement);
        }
        if (attachElement === null) {
            attachElement = element;
        }

        if (!attachLater) {
            window.startListening();
        }
    }
}

export default DragElements;
