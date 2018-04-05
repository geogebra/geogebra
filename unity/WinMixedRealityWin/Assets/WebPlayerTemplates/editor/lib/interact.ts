/**
 *  @module lib/interact
 *  @requires module:lib/function_helpers
 *  @requires module:scripts/main
 *  @requires scripts/helpers/units_conversion
 */

'use strict';
declare let $;

import helpers from './function_helpers';
import couiEditor from '../scripts/main';
import unitsConvertor from '../scripts/helpers/units_conversion';

namespace SceneInteract {

// Minimum resizable area
    var minWidth = 0;
    var minHeight = 0;

// Thresholds

    var clicked = null;
    var onRightEdge,
        onBottomEdge,
        onLeftEdge,
        onTopEdge;

    var lastStateResize;

    var interactMask;
    var matrixEl;
    var sceneMatrix;
    var scale;
    var borderLeft;
    var borderRight;
    var borderTop;
    var borderBottom;
    var marginLeft;
    var marginRight;
    var marginTop;
    var marginBottom;
    var paddingLeft;
    var paddingRight;
    var paddingTop;
    var paddingBottom;

    var borderMarginPaddingOffsetX = 0;
    var borderMarginPaddingOffsetY = 0;

    var elementId;
    var elementToInteract;
    var tabName;

    var topLeftCorner;
    var topRightCorner;
    var bottomLeftCorner;
    var bottomRightCorner;
    var leftResizePoint;
    var rightResizePoint;
    var topResizePoint;
    var bottomResizePoint;
    var currentDragX;
    var currentDragY;
    var currentElWidth;
    var currentElHeight;
    var currentMaskWidth;
    var currentMaskHeight;
    var deltaX;
    var deltaY;
    var isAutoWidth;
    var isAutoHeight;
    var createElementEvent;
    var manipulationState;
    let Transform;

    export function Interact(element, editorTabName, state, createElEvent) {
        elementId = element.id;
        elementToInteract = element;
        tabName = editorTabName;
        createElementEvent = createElEvent;
        manipulationState = state;
        Transform = couiEditor.openFiles[couiEditor.selectedEditor].runtimeEditor.transform;

        interactMask = document.getElementsByClassName(state + '-corners')[0];

        if (state === 'rotate') {
            document.removeEventListener('mousemove', onDocumentResizeMove);
            document.removeEventListener('mouseup', onDocumentResizeUp);

            rotateElement(element);
        } else if (state === 'create') {
            document.getElementById('center-pane').removeEventListener('mouseup', onDocumentRotateMouseUp);
            document.getElementById('center-pane').removeEventListener('mousemove', onDocumentRotateMouseMove);

            createElement(element);
        } else if (state === 'resize') {
            document.getElementById('center-pane').removeEventListener('mouseup', onDocumentRotateMouseUp);
            document.getElementById('center-pane').removeEventListener('mousemove', onDocumentRotateMouseMove);

            resizeElement(element);
        }

    }

    function createElement(el) {

        attachDocumentResizeHandlers();

        // Clear all selected jstree elements, preventing wrong element resize
        $('#sceneTree').jstree('deselect_all');

        var directionX = '',
            directionY = '',
            oldx = 0,
            oldy = 0,
            checkMouseDirection = function (e) {
                var changeDirection = false;

                if (e.pageX < oldx) {
                    directionX = 'left';
                } else if (e.pageX > oldx) {
                    directionX = 'right';
                }

                if (e.pageY < oldy) {
                    directionY = 'top';
                } else if (e.pageY > oldy) {
                    directionY = 'bottom';
                }

                if (directionX === 'left' &&
                    parseFloat(interactMask.style.width) <= 1) {
                    onRightEdge = false;
                    onLeftEdge = true;
                    changeDirection = true;
                }

                if (directionX === 'right' &&
                    parseFloat(interactMask.style.width) <= 1) {
                    onRightEdge = true;
                    onLeftEdge = false;
                    changeDirection = true;
                }

                if (directionY === 'top' &&
                    parseFloat(interactMask.style.height) <= 1) {
                    onTopEdge = true;
                    onBottomEdge = false;
                    changeDirection = true;
                }

                if (directionY === 'bottom' &&
                    parseFloat(interactMask.style.height) <= 1) {
                    onTopEdge = false;
                    onBottomEdge = true;
                    changeDirection = true;
                }

                if (changeDirection) {
                    onDocumentResizeDown(e);
                }

                oldx = e.pageX;
                oldy = e.pageY;
            },
            onDocumentMouseUp = function () {
                resetResizeDirection();
                document.removeEventListener('mousemove', checkMouseDirection);
                document.removeEventListener('mouseup', onDocumentMouseUp);
            };

        document.addEventListener('mousemove', checkMouseDirection);
        document.addEventListener('mouseup', onDocumentMouseUp);

        onDocumentResizeDown(createElementEvent);
    }

    function resetResizeDirection() {
        onRightEdge = false;
        onLeftEdge = false;
        onTopEdge = false;
        onBottomEdge = false;
    }

    function resizeElement(el) {
        resetResizeDirection();
        attachResizePointsHandlers();
        attachDocumentResizeHandlers();
    }

    function attachDocumentResizeHandlers() {
        // Mouse events
        document.removeEventListener('mousemove', onDocumentResizeMove);
        document.removeEventListener('mouseup', onDocumentResizeUp);
        document.addEventListener('mousemove', onDocumentResizeMove);
        document.addEventListener('mouseup', onDocumentResizeUp);
    }

    function resizePointsHandlers(point, top, left, right, bottom) {
        var mouseDownHandler = function (e) {
            e.preventDefault();
            e.stopPropagation();
            onDocumentResizeDown(e);
        };

        var mouseOverHandler = function () {
            onTopEdge = top;
            onLeftEdge = left;
            onRightEdge = right;
            onBottomEdge = bottom;
        };

        var mouseOutHandler = function () {
            onTopEdge = false;
            onLeftEdge = false;
            onRightEdge = false;
            onBottomEdge = false;
        };

        for (var i = 0; i < point.length; i++) {
            point[i].addEventListener('mousedown', mouseDownHandler);
            point[i].addEventListener('mouseover', mouseOverHandler);
            point[i].addEventListener('mouseout', mouseOutHandler);
        }
    }

    function attachResizePointsHandlers() {
        topLeftCorner = interactMask.getElementsByClassName('top-left-corner');
        topRightCorner = interactMask.getElementsByClassName('top-right-corner');
        bottomLeftCorner = interactMask.getElementsByClassName('bottom-left-corner');
        bottomRightCorner = interactMask.getElementsByClassName('bottom-right-corner');
        leftResizePoint = interactMask.getElementsByClassName('left-center');
        rightResizePoint = interactMask.getElementsByClassName('right-center');
        topResizePoint = interactMask.getElementsByClassName('top-center');
        bottomResizePoint = interactMask.getElementsByClassName('bottom-center');

        resizePointsHandlers(topLeftCorner, true, true, false, false);
        resizePointsHandlers(topRightCorner, true, false, true, false);
        resizePointsHandlers(bottomLeftCorner, false, true, false, true);
        resizePointsHandlers(bottomRightCorner, false, false, true, true);
        resizePointsHandlers(leftResizePoint, false, true, false, false);
        resizePointsHandlers(rightResizePoint, false, false, true, false);
        resizePointsHandlers(topResizePoint, true, false, false, false);
        resizePointsHandlers(bottomResizePoint, false, false, false, true);
    }

    function recalculatePercents() {

        if (manipulationState === 'create') {
            return;
        }

        var widget = couiEditor.openFiles[tabName].runtimeEditor
            .mappedWidgets[elementToInteract.id].widget.geometry;

        var unitsWidth = helpers.getUnitStyle(widget.width);
        var unitsHeight = helpers.getUnitStyle(widget.height);
        var unitsTop = helpers.getUnitStyle(widget.top);
        var unitsLeft = helpers.getUnitStyle(widget.left);

        var computedStyle = getComputedStyle(elementToInteract.parentElement, null);

        var parentWidth = parseFloat(computedStyle.getPropertyValue('width'));
        var parentHeight = parseFloat(computedStyle.getPropertyValue('height'));

        var elementStyle = elementToInteract.style;

        if (unitsTop === '%') {
            var top = unitsConvertor.convertPercentToPixel(parseFloat(widget.top), parentHeight);
            elementStyle.top = top + 'px';
        }

        if (unitsLeft === '%') {
            var left = unitsConvertor.convertPercentToPixel(parseFloat(widget.left), parentWidth);
            elementStyle.left = left + 'px';
        }

        if (unitsWidth === '%') {
            var width = unitsConvertor.convertPercentToPixel(parseFloat(widget.width), parentWidth);
            elementStyle.width = width + 'px';
        }

        if (unitsHeight === '%') {
            var height = unitsConvertor.convertPercentToPixel(parseFloat(widget.height), parentHeight);
            elementStyle.height = height + 'px';
        }
    }

    function onStartResizeBottomRight() {
        return this;
    }

    function onStartResizeTopLeft() {
        return this;
    }

    function onStartResizeBottomLeft() {
        return this;
    }

    function onStartResizeTopRight() {
        return this;
    }

    function onEndResizeBottomRight() {
        return this;
    }

    function onEndResizeTopLeft() {
        return this;
    }

    function onEndResizeBottomLeft() {
        return this;
    }

    function onEndResizeTopRight() {
        return this;
    }

    function getMatrix(el) {
        return new WebKitCSSMatrix(window.getComputedStyle(el).webkitTransform);
    }

    function onDocumentResizeDown(e) {
        isAutoWidth = elementToInteract.style.width;
        isAutoHeight = elementToInteract.style.height;

        matrixEl = $('#scene')[0];

        couiEditor.openFiles[tabName].runtimeEditor.onResizeElStart();

        currentDragX = e.clientX;
        currentDragY = e.clientY;

        currentElWidth = parseFloat(unitsConvertor.convertUnitsToPixel(elementToInteract.id, elementToInteract.style.width, 'width'));
        currentElHeight = parseFloat(unitsConvertor.convertUnitsToPixel(elementToInteract.id, elementToInteract.style.height, 'height'));

        resizeCheck(e);

        var isResizing = onRightEdge || onBottomEdge ||
            onTopEdge || onLeftEdge;

        clicked = {
            x: currentMaskWidth,
            y: currentMaskHeight,
            cx: e.clientX,
            cy: e.clientY,
            isResizing: isResizing,
            onTopEdge: onTopEdge,
            onLeftEdge: onLeftEdge,
            onRightEdge: onRightEdge,
            onBottomEdge: onBottomEdge
        };

        borderLeft = parseFloat(getComputedStyle(elementToInteract, null).getPropertyValue('border-left-width'));
        borderRight = parseFloat(getComputedStyle(elementToInteract, null).getPropertyValue('border-right-width'));
        borderTop = parseFloat(getComputedStyle(elementToInteract, null).getPropertyValue('border-top-width'));
        borderBottom = parseFloat(getComputedStyle(elementToInteract, null).getPropertyValue('border-bottom-width'));
        marginLeft = parseFloat(getComputedStyle(elementToInteract, null).getPropertyValue('margin-left'));
        marginRight = parseFloat(getComputedStyle(elementToInteract, null).getPropertyValue('margin-right'));
        marginTop = parseFloat(getComputedStyle(elementToInteract, null).getPropertyValue('margin-top'));
        marginBottom = parseFloat(getComputedStyle(elementToInteract, null).getPropertyValue('margin-bottom'));
        paddingLeft = parseFloat(getComputedStyle(elementToInteract, null).getPropertyValue('padding-left'));
        paddingRight = parseFloat(getComputedStyle(elementToInteract, null).getPropertyValue('padding-right'));
        paddingTop = parseFloat(getComputedStyle(elementToInteract, null).getPropertyValue('padding-top'));
        paddingBottom = parseFloat(getComputedStyle(elementToInteract, null).getPropertyValue('padding-bottom'));

        borderMarginPaddingOffsetX = (borderRight + borderLeft + marginLeft + marginRight + paddingLeft + paddingRight);
        borderMarginPaddingOffsetY = (borderBottom + borderTop + marginTop + marginBottom + paddingTop + paddingTop);

        if (clicked.onBottomEdge && clicked.onLeftEdge) {
            recalculatePercents();
            onStartResizeBottomLeft();
        } else if (clicked.onTopEdge && clicked.onRightEdge) {
            recalculatePercents();
            onStartResizeTopRight();
        } else if (clicked.onBottomEdge || clicked.onRightEdge) {
            recalculatePercents();
            onStartResizeBottomRight();
        } else if (clicked.onTopEdge || clicked.onLeftEdge) {
            recalculatePercents();
            onStartResizeTopLeft();
        }
    }

    function onDocumentResizeMove(e) {
        resizeCheck(e);
    }

    function applyMaskSizes() {
        Transform.transform(elementToInteract);
        let maskPosition = helpers.getTransformedElMaskPos(elementToInteract);

        interactMask.style.width = maskPosition.width + 'px';
        interactMask.style.height = maskPosition.height + 'px';
        interactMask.style.top = maskPosition.top + 'px';
        interactMask.style.left = maskPosition.left + 'px';
    }

    function resizeCheck(e) {
        var width;
        var height;
        var elToInteractComputedStyle;

        if (clicked && clicked.isResizing) {
            const runtimeEditor = couiEditor.openFiles[tabName].runtimeEditor;
            var hDiff;
            var diffTopElement;

            window.requestAnimationFrame(function () {
                applyMaskSizes();
            });

            deltaX = (e.clientX - currentDragX);
            deltaY = (e.clientY - currentDragY);

            // Normalize delta resizing x and y on create element proporcional
            if ((runtimeEditor.interactElementsState === 'create' ||
                    (currentElHeight === currentElWidth)) &&
                e.shiftKey &&
                ((clicked.onLeftEdge && clicked.onTopEdge) ||
                    (clicked.onLeftEdge && clicked.onBottomEdge) ||
                    (clicked.onRightEdge && clicked.onTopEdge) ||
                    (clicked.onRightEdge && clicked.onBottomEdge))) {
                runtimeEditor.equalProportion = true;
                deltaY = deltaX;
                if (clicked.onRightEdge && clicked.onTopEdge) {
                    deltaY = -deltaX;
                }
            }

            scale = couiEditor.openFiles[tabName].runtimeEditor.tab.sceneTransformMatrix[0];

            if (clicked.onRightEdge && isAutoWidth !== 'auto') {
                if (e.shiftKey && (clicked.onBottomEdge || clicked.onTopEdge)) {
                    let ratio = 1;
                    let deltaCorrection = deltaX;

                    if (currentElHeight > currentElWidth) {
                        deltaCorrection = deltaY;
                        ratio = currentElWidth / currentElHeight;

                        if (clicked.onTopEdge) {
                            deltaCorrection = -deltaCorrection;
                        }
                    }

                    width = (currentElWidth + (deltaCorrection * ratio) / scale) - borderMarginPaddingOffsetX;
                } else {
                    width = (currentElWidth + deltaX / scale) - borderMarginPaddingOffsetX;
                }

                if (width < 0) {
                    width = 0;
                }

                elementToInteract.style.width = width + 'px';
            }

            if (clicked.onBottomEdge && isAutoHeight !== 'auto') {
                if (e.shiftKey && (clicked.onRightEdge)) {
                    let ratio = 1;
                    let deltaCorrection = deltaY;
                    if (currentElHeight < currentElWidth) {
                        deltaCorrection = deltaX;
                        ratio = currentElHeight / currentElWidth;
                    }

                    height = (currentElHeight + (deltaCorrection * ratio) / scale) - borderMarginPaddingOffsetY;
                } else {
                    height = (currentElHeight + deltaY / scale) - borderMarginPaddingOffsetY;
                }

                if (height < 0) {
                    height = 0;
                }

                elementToInteract.style.height = height + 'px';
            }

            if (clicked.onLeftEdge && isAutoWidth !== 'auto') {
                let deltaCorrection = deltaX;
                let ratio = 1;

                if (e.shiftKey && clicked.onBottomEdge || clicked.onTopEdge) {
                    if (clicked.onBottomEdge) {
                        deltaCorrection = -deltaCorrection;
                    }

                    if (currentElWidth < currentElHeight) {
                        deltaCorrection = deltaY;
                        ratio = currentElWidth / currentElHeight;
                    }

                    let _tmpWidth = currentElWidth + (deltaCorrection * ratio) / scale;

                    if (clicked.onTopEdge) {
                        _tmpWidth = currentElWidth - (deltaCorrection * ratio) / scale;
                    }

                    width = Math.max(Math.floor(_tmpWidth), minWidth);
                } else {
                    width = Math.max(Math.floor((currentElWidth - deltaX / scale)), minWidth);
                }

                if (e.shiftKey && clicked.onBottomEdge) {
                    let _tempHeight;
                    if (currentElWidth > currentElHeight) {
                        ratio = currentElHeight / currentElWidth;

                        _tempHeight = currentElHeight + (deltaCorrection * ratio) / scale;
                    } else {
                        _tempHeight = currentElHeight + (deltaCorrection) / scale;
                    }

                    height = Math.max(Math.floor(_tempHeight), minHeight);
                    elToInteractComputedStyle = getComputedStyle(elementToInteract, null);
                    hDiff = parseFloat(elToInteractComputedStyle.getPropertyValue('height')) - height;
                    diffTopElement = parseFloat(elToInteractComputedStyle.getPropertyValue('top')) + hDiff;

                    elementToInteract.style.height = height - borderMarginPaddingOffsetY + 'px';
                }

                if (width >= minWidth + borderMarginPaddingOffsetX) {
                    elToInteractComputedStyle =
                        getComputedStyle(elementToInteract, null);
                    var wDiff = parseFloat(elToInteractComputedStyle
                        .getPropertyValue('width')) - width;
                    var diffLeftElement = parseFloat(elToInteractComputedStyle
                        .getPropertyValue('left')) + wDiff;

                    elementToInteract.style.width =
                        width - borderMarginPaddingOffsetX + 'px';
                    elementToInteract.style.left =
                        diffLeftElement + borderMarginPaddingOffsetX + 'px';

                    // normalize width and height when create new element in top right direction
                    if (runtimeEditor.interactElementsState === 'create' &&
                        elementToInteract.style.width !== elementToInteract.style.height &&
                        e.shiftKey &&
                        clicked.onBottomEdge &&
                        clicked.onLeftEdge) {
                        elementToInteract.style.height = parseFloat(width) + 'px';
                    }
                }
            }

            if (clicked.onTopEdge && isAutoHeight !== 'auto') {
                let ratio = 1;
                let deltaCorrection = deltaY;
                if (currentElWidth > currentElHeight) {
                    deltaCorrection = deltaX;
                    ratio = currentElHeight / currentElWidth;

                } else {
                    if (clicked.onRightEdge) {
                        deltaCorrection = -deltaCorrection;
                    }
                }

                if (e.shiftKey && (clicked.onLeftEdge || clicked.onRightEdge)) {
                    if (clicked.onLeftEdge) {
                        deltaCorrection = -deltaCorrection;
                    }
                    height = Math.max(Math.floor(currentElHeight + (deltaCorrection * ratio) / scale), minHeight);
                } else {
                    height = Math.max(Math.floor(currentElHeight - deltaY / scale), minHeight);
                }

                if (height >= minHeight + borderMarginPaddingOffsetY) {

                    elToInteractComputedStyle =
                        getComputedStyle(elementToInteract, null);
                    hDiff = parseFloat(elToInteractComputedStyle
                        .getPropertyValue('height')) - height;
                    diffTopElement = parseFloat(elToInteractComputedStyle
                        .getPropertyValue('top')) + hDiff;
                    elementToInteract.style.height =
                        height - borderMarginPaddingOffsetY + 'px';
                    elementToInteract.style.top =
                        diffTopElement + borderMarginPaddingOffsetY + 'px';

                    // normalize width and height when create new element in top right direction
                    if (runtimeEditor.interactElementsState === 'create' &&
                        elementToInteract.style.width !== elementToInteract.style.height &&
                        e.shiftKey &&
                        clicked.onTopEdge &&
                        clicked.onRightEdge) {
                        const diffWidthHeight = parseFloat(elementToInteract.style.width) - parseFloat(elementToInteract.style.height);
                        elementToInteract.style.height = parseFloat(width) + 'px';
                        elementToInteract.style.top = parseFloat(elementToInteract.style.top) - diffWidthHeight + 'px';
                    }
                }
            }

            lastStateResize = true;
            couiEditor.openFiles[tabName].runtimeEditor.onResizeElMove(elementToInteract);
            return;

        } else {
            lastStateResize = false;
        }

        // style cursor
        if (onRightEdge && onBottomEdge || onLeftEdge && onTopEdge) {
            interactMask.style.cursor = 'nwse-resize';
        } else if (onRightEdge && onTopEdge || onBottomEdge && onLeftEdge) {
            interactMask.style.cursor = 'nesw-resize';
        } else if (onRightEdge || onLeftEdge) {
            interactMask.style.cursor = 'ew-resize';
        } else if (onBottomEdge || onTopEdge) {
            interactMask.style.cursor = 'ns-resize';
        } else {
            interactMask.style.cursor = 'default';
        }

    }

    function onDocumentResizeUp(e) {
        if (clicked) {
            if (clicked.onBottomEdge && clicked.onLeftEdge) {
                onEndResizeBottomLeft();
            } else if (clicked.onTopEdge && clicked.onRightEdge) {
                onEndResizeTopRight();
            } else if (clicked.onTopEdge || clicked.onLeftEdge) {
                onEndResizeTopLeft();
            } else if (clicked.onBottomEdge || clicked.onRightEdge) {
                onEndResizeBottomRight();
            }
        }

        clicked = null;

        if (lastStateResize === true) {
            couiEditor.openFiles[tabName].runtimeEditor.onResizeElEnd(elementToInteract, e);
            lastStateResize = false;
        }
    }

// Rotate variables
    var drag = false;
    var pos = [];
    var size = [];
    var axis = [];
    var cursor = [];
    var rad = 0;
    var lastRadInteractMask = 0;
    var lastRadEl = 0;
//var lastPer=0;
    var lastFullRad = 0;
    var maxRad = 2 * Math.PI;
    var maxDeg = 360;
    var maxPer = 100;
    var Dx = [];
    var Dy = [];
    var dummy;
    var rotateOutput = {};

    function rotatePointsHandlers(point) {
        var points = document.getElementsByClassName(point);
        var mouseDownHandler = function (e) {
            e.stopPropagation();
            onInteractMaskDown(e);
        };

        for (var i = 0; i < points.length; i++) {
            points[i].addEventListener('mousedown', mouseDownHandler);
        }
    }

// Rotate
    function rotateElement(elem) {

        rotatePointsHandlers('bottom-left-rotate');
        rotatePointsHandlers('top-left-rotate');
        rotatePointsHandlers('bottom-right-rotate');
        rotatePointsHandlers('top-right-rotate');

        document.getElementById('center-pane').addEventListener('mouseup', onDocumentRotateMouseUp);
        document.getElementById('center-pane').addEventListener('mousemove', onDocumentRotateMouseMove);
    }

    function rotate(event) {
        if (drag) {

            couiEditor.openFiles[tabName].runtimeEditor.onRotateElMove();

            var cursorRad;
            var relativeRad;

            cursorRad = getAngle(event);
            relativeRad = cursorRad - rad;

            var rotationRadInteractMask = lastRadInteractMask + relativeRad;
            if (isNaN(rotationRadInteractMask)) {
                rotationRadInteractMask = lastRadInteractMask;
            }
            var rotationRadEl = lastRadEl + relativeRad;
            if (isNaN(rotationRadEl)) {
                rotationRadEl = lastRadEl;
            }

            rad = cursorRad;

            //applying rotation to element
            const newRotation = 'rotateZ(' + rotationRadEl + 'rad)';
            const rotationRegex = /rotateZ\((.*?)\)/;
            let currentTransform = elementToInteract.style.WebkitTransform;
            if (!currentTransform.match(rotationRegex)) {
                const runtimeEditor = couiEditor.openFiles[tabName].runtimeEditor;
                runtimeEditor._skipUndoRedoSteps = 1;
                const widget = runtimeEditor.mappedWidgets[elementId].widget;
                runtimeEditor._setTransform(null, widget, elementId, 'transform', 'rotateZ', '0deg');
                return;
            }
            currentTransform = currentTransform.replace(rotationRegex, newRotation);
            const newTransform = currentTransform;

            interactMask.style.WebkitTransform = 'rotateZ(' + rotationRadInteractMask + 'rad)';
            elementToInteract.style.WebkitTransform = newTransform;

            lastRadInteractMask = rotationRadInteractMask;
            lastRadEl = rotationRadEl;

        }
    }

    function getPos(element) {
        //get the position [left,top] relative to whole document
        var tmpElement = element;
        var left = tmpElement.offsetLeft;
        var top = tmpElement.offsetTop;

        /* jshint -W084 */
        while (tmpElement = tmpElement.offsetParent) {
            left += tmpElement.offsetLeft;
        }

        var tmpInteractMask = interactMask;

        while (tmpInteractMask = tmpInteractMask.offsetParent) {
            top += tmpInteractMask.offsetTop;
        }
        /* jshint +W084 */

        return [left, top];
    }

    function getSize(elem) {
        //return the size [width,height] of the element

        return [elem.offsetWidth, elem.offsetHeight];
    }

    function getAxis(elem) {
        //return the center point [left,top] of the element
        return [getPos(elem)[0] + getSize(elem)[0] / 2, getPos(elem)[1] + getSize(elem)[1] / 2];
    }

    function getCursorPos(event) {
        //return the cursor's position [x,y]
        return [event.pageX, event.pageY];
    }

    function getAngle(event) {
        //getting rotation angle by Arc Tangent 2
        var rad;
        pos = getPos(elementToInteract);
        size = getSize(elementToInteract);
        axis = getAxis(elementToInteract);
        cursor = getCursorPos(event);
        try {
            rad = Math.atan2(cursor[1] - axis[1], cursor[0] - axis[0]);
        } catch (err) {
            console.error(err);
        }

        //correct the 90° of difference starting from the Y axis of the element
        rad += maxRad / 4;
        //transform opposite angle negative value, to possitive
        if (rad < 0) {
            rad += maxRad;
        }
        return rad;
    }

    function setRotateDrag(event, bool) {
        //set or unset the drag flag
        if (bool) {
            event.preventDefault();
            rad = getAngle(event);
            drag = true;
        } else {
            drag = false;
        }
    }

    function onInteractMaskDown(event) {
        couiEditor.openFiles[tabName].runtimeEditor.onRotateElStart();

        var currRadInteractMask = rotationInfo(interactMask);
        var currRadEl = rotationInfo(elementToInteract);

        lastRadInteractMask = currRadInteractMask.rad;
        lastRadEl = currRadEl.rad;
        matrixEl = $('#scene')[0];
        sceneMatrix = getMatrix(matrixEl);
        setRotateDrag(event, true);
    }

    function onDocumentRotateMouseUp(event) {
        const info = {
            rad: lastRadEl,
            deg: helpers.toDegrees(lastRadEl)
        };
        couiEditor.openFiles[tabName].runtimeEditor.onRotateElEnd(elementToInteract, info);
        setRotateDrag(event, false);
    }

    function onDocumentRotateMouseMove(event) {
        rotate(event);
    }

    function rotationInfo(el, options?) {
        var info = {
            rad: 0,
            deg: 0
        };

        var transform: any = window.getComputedStyle(el, null).getPropertyValue('-webkit-transform');
        transform = transform.match('matrix\\((.*)\\)');

        if (transform) {
            transform = transform[1].split(',');
            if (typeof transform[0] !== 'undefined' && typeof transform[1] !== 'undefined') {
                info.rad += Math.atan2(transform[1], transform[0]);
                info.deg += parseFloat((info.rad * 180 / Math.PI).toFixed(1));
            }
        } else {
            if (el.id !== null &&
                el.id !== '' &&
                couiEditor.openFiles[couiEditor.selectedEditor].runtimeEditor.mappedWidgets[el.id].widget.transform) {
                info.deg = parseFloat(couiEditor.openFiles[couiEditor.selectedEditor].runtimeEditor.mappedWidgets[el.id].widget.transform.rotateZ) || 0;
                info.rad = helpers.toRadians(info.deg);
            }
        }

        return info;
    }
}

export default SceneInteract;
