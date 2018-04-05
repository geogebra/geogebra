/**
 *  @module lib/widget_selection
 *  @export lib/widget_selection.WidgetSelection
 *  @requires module:lib/function_helpers
 */

'use strict';
declare let $;

import couiEditor from '../scripts/main';
import helpers from './function_helpers';
import Transform from './transform';

namespace WidgetSelection {

    var offsetTop;
    var offsetLeft;
    var initialWidth;
    var initialHeight;
    var $centerPane;
    var $scene;
    var $selectionDrag;
    var widgetsSelected = false;
    var isSelectorOpen = false;

    /**
     * @memberOf lib/widget_selection
     * @class
     * @constructor
     */
    export class WidgetSelection {

        init() {
            $centerPane = $('#top-scene-holder');
            $scene = $('#scene');
        }

        getSelectedState() {
            return widgetsSelected;
        }

        setSelectedState(state) {
            widgetsSelected = state;
        }

        detachHandlers() {
            $centerPane[0].removeEventListener('mousedown', attachEvent, false);
            $centerPane[0].removeEventListener('mousedown', selectElement, false);
        }

        attachHandlers() {
            this.detachHandlers();

            $centerPane[0].addEventListener('mousedown', attachEvent, false);
            $centerPane[0].addEventListener('mousedown', selectElement, false);
        }
    }

    function attachEvent(e) {
        widgetsSelected = false;
        $selectionDrag = $('.selection-drag');
        $selectionDrag.addClass('active');

        offsetTop = $centerPane.offset().top;
        offsetLeft = $centerPane.offset().left;
        initialWidth = e.pageX;
        initialHeight = e.pageY;

        $selectionDrag.css({
            'left': e.pageX - offsetLeft,
            'top': e.pageY - offsetTop
        });

        $centerPane.bind('mouseup', selectElements);
        $centerPane.bind('mousemove', openSelector);
    }

    var selectElement = function (e) {
        var target = e.target;
        var dataset = target.dataset;
        var editorId = couiEditor.selectedEditor;
        var runtimeEditor = couiEditor.openFiles[editorId].runtimeEditor;
        if (dataset.elementSelectable &&
            dataset.elementSelectable === 'true') {
            runtimeEditor.selectJstreeItem(target.id);
        } else if (dataset.parentWidgetId) {
            runtimeEditor.selectJstreeItem(dataset.parentWidgetId);
        } else if (target.id === 'scene' || target.id === 'scene-wrapper') {
            runtimeEditor.clearSelectedElements();
        }
    };

    var selectElements = function (e) {

        $centerPane.unbind('mousemove', openSelector);
        $centerPane.unbind('mouseup', selectElements);

        if (isSelectorOpen) {
            var elementArr = [];
            $('[data-element-selectable="true"][data-element-type="widget"]')
                .each(function () {
                    var $thisElement = $(this);
                    var result = doObjectsCollide($selectionDrag, $thisElement);

                    if (result) {
                        elementArr.push($thisElement[0].id);
                    }
                });

            var editorId = couiEditor.selectedEditor;
            var runtimeEditor = couiEditor.openFiles[editorId].runtimeEditor;
            runtimeEditor.selectMultiJstreeItems(elementArr);
        }

        $selectionDrag.removeClass('active');
        $selectionDrag.width(0).height(0);
        isSelectorOpen = false;
    };

    function openSelector(e) {

        isSelectorOpen = true;
        var selectionWidth = Math.abs(initialWidth - e.pageX);
        var selectionHeight = Math.abs(initialHeight - e.pageY);

        $selectionDrag.css({
            'width': selectionWidth,
            'height': selectionHeight
        });
        if (e.pageX <= initialWidth && e.pageY >= initialHeight) {
            $selectionDrag.css({
                'left': e.pageX - offsetLeft
            });
        } else if (e.pageY <= initialHeight && e.pageX >= initialWidth) {
            $selectionDrag.css({
                'top': e.pageY - offsetTop
            });
        } else if (e.pageY < initialHeight && e.pageX < initialWidth) {
            $selectionDrag.css({
                'left': e.pageX - offsetLeft,
                'top': e.pageY - offsetTop
            });
        }
    }

    function doObjectsCollide(a, b) {
        var pointsA = findSelectionPoints(a[0]);

        //TODO: remove from here
        var editorId = couiEditor.selectedEditor;
        var runtimeEditor = couiEditor.openFiles[editorId].runtimeEditor;

        let transform = new Transform();
        let widget = runtimeEditor.mappedWidgets[b[0].id].widget;
        let transformedPos = transform.getViewportPosition(widget);

        var pointsB = transformedPos.length ? transformedPos : findPoints(b[0]);
        //TODO: remove from here

        var hits = helpers.doPolygonsIntersect(pointsB, pointsA);

        return hits;
    }

    function calcPoints(left, top, elementMatrix, centerPointLeft, centerPointTop) {

        var x = elementMatrix.m11 * left + -elementMatrix.m12 * top;
        var y = -elementMatrix.m21 * left + elementMatrix.m22 * top;

        x = centerPointLeft + x;
        y = centerPointTop + y;

        return {
            x: x,
            y: y
        };
    }

    function findSelectionPoints(element) {
        var elementWidth = parseFloat(window.getComputedStyle(element, null).getPropertyValue('width'));
        var elementHeight = parseFloat(window.getComputedStyle(element, null).getPropertyValue('height'));

        var selectionTop = element.offsetTop;
        var selectionLeft = element.offsetLeft;
        var selectionRight = selectionLeft + elementWidth;
        var selectionBottom = selectionTop + elementHeight;

        var topLeft = {
            x: selectionLeft,
            y: selectionTop
        };

        var topRight = {
            x: selectionRight,
            y: selectionTop
        };

        var bottomRight = {
            x: selectionRight,
            y: selectionBottom
        };

        var bottomLeft = {
            x: selectionLeft,
            y: selectionBottom
        };

        return [topLeft, topRight,
            bottomRight, bottomLeft
        ];
    }

    function findPoints(element) {
        var sceneEl = document.getElementById('scene');
        var sceneMatrix = new WebKitCSSMatrix(window.getComputedStyle(sceneEl).webkitTransform);

        var elementWidth = parseFloat(window.getComputedStyle(element, null).getPropertyValue('width'));
        var elementHeight = parseFloat(window.getComputedStyle(element, null).getPropertyValue('height'));

        var elementMatrix = new WebKitCSSMatrix(window.getComputedStyle(element).webkitTransform);

        elementMatrix = elementMatrix.multiply(sceneMatrix);

        var position = helpers.getAbsolutePosition(element, 'scene-wrapper');
        var elementLeft = position.left;
        var elemrntTop = position.top;

        var coordsDiff = helpers.calcElementTransformedMatrixCoords(element, 'scene-wrapper');

        elementLeft += (coordsDiff.x + sceneMatrix.e);
        elemrntTop += (coordsDiff.y + sceneMatrix.f);

        var halfWidth = elementWidth / 2;
        var halfHeight = elementHeight / 2;

        var centerPointLeft = elementLeft + halfWidth;
        var centerPointTop = elemrntTop + halfHeight;

        var top = -halfHeight;
        var left = -halfWidth;
        var right = halfWidth;
        var bottom = halfHeight;

        var result = {
            topLeft: calcPoints(left, top, elementMatrix,
                centerPointLeft, centerPointTop),
            topRight: calcPoints(right, top, elementMatrix,
                centerPointLeft, centerPointTop),
            bottomLeft: calcPoints(left, bottom, elementMatrix,
                centerPointLeft, centerPointTop),
            bottomRight: calcPoints(right, bottom, elementMatrix,
                centerPointLeft, centerPointTop)
        };

        return [result.topLeft, result.topRight,
            result.bottomRight, result.bottomLeft
        ];
    }
}


export default WidgetSelection;
