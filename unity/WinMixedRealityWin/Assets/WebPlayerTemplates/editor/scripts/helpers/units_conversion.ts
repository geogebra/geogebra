/**
 *  @module scripts/helpers/units_conversion
 *  @requires module:lib/function_helpers
 */

'use strict';
import helpers from 'lib/function_helpers';

export default {
    /**
     *
     * @param value - number + units
     * @example `getPixelsWidgetGeometry('10vh')`
     * @returns {{originalUnit: *, pixels: *}}
     */
    getPixelsWidgetGeometry: function (id, property, value) {

        var originalUnit = helpers.getUnitStyle(value);
        var pixels = 0;
        var newValue = parseFloat(value);

        if (originalUnit === 'px') {
            pixels = newValue;
        } else {
            pixels = parseFloat(this.convertUnitsToPixel(id, value, property));
        }

        return {
            originalUnit: originalUnit,
            pixels: pixels
        };
    },
    /**
     * Function converting pixel values to unit values (all editor units).
     *
     * @function convertPixelToUnit
     * @param id of the element
     * @param value to be converted
     * @param unit to be converted to
     * @param property edited(defines parent references)
     * @return {String} converted number value for pixels
     */
    convertPixelToUnit: function (id, value, unit, property) {
        var _tempValue;
        if (property === 'fontSize' && !isNaN(value)) {
            _tempValue = helpers.getSceneFontSize();
        } else {
            if (value !== 'auto') {
                _tempValue = parseFloat(value);
            } else {
                _tempValue = 0;
            }
        }
        switch (unit) {
            case 'vw':
                return this.convertPixelToVw(_tempValue) + unit;
            case 'vh':
                return this.convertPixelToVh(_tempValue) + unit;
            case 'vmin':
                return this.convertPixelToVmin(_tempValue) + unit;
            case 'vmax':
                return this.convertPixelToVmax(_tempValue) + unit;
            case 'pt':
                return this.convertPixelToPt(_tempValue) + unit;
            case 'rem':
                return this.convertPixelToRem(_tempValue) + unit;
            case '%':
                return this.pixelToPercentHandling(id, _tempValue, property) + unit;
            case 'auto':
                return unit;
            default:
                return _tempValue + 'px';
        }
    },
    /**
     * Function converting unit values (all editor units) to pixel values.
     *
     * @function convertUnitsToPixel
     * @param id of the element
     * @param value to be converted
     * @param unit to be converted to
     * @param property edited(defines parent references)
     * @return {String} converted number value for the selected units
     */
    convertUnitsToPixel: function (id, value, property = null) {
        var _tempValue;
        var unit = helpers.getUnitStyle(value);
        if (property === 'fontSize' && value === 'auto') {
            return '16px';
        } else {
            _tempValue = parseFloat(value);
        }
        switch (unit) {
            case 'vw':
                return this.convertVwToPixel(_tempValue) + 'px';
            case 'vh':
                return this.convertVhToPixel(_tempValue) + 'px';
            case 'vmin':
                return this.convertVminToPixel(_tempValue) + 'px';
            case 'vmax':
                return this.convertVmaxToPixel(_tempValue) + 'px';
            case 'pt':
                return this.convertPtToPixel(_tempValue) + 'px';
            case 'rem':
                return this.convertRemToPixel(_tempValue) + 'px';
            case '%':
                return this.percentToPixelHandling(id, _tempValue, property) + 'px';
            case 'auto':
                return unit;
            default:
                return _tempValue + 'px';
        }
    },
    /**
     * Handles percent to pixel handling based on the parent reference.
     *
     * @function percentToPixelHandling
     * @param id of the element
     * @param percent value to be converted
     * @param property edited(defines parent references)
     * @return number value
     */
    percentToPixelHandling: function (id, value, property) {
        var parentPositionValue;
        var element = document.getElementById(id);

        if (property === 'top' || property === 'height') {
            parentPositionValue = parseFloat(this.convertUnitsToPixel(element.parentElement.id, element.parentElement.style.height, property));
        } else if (property === 'left' || property === 'width') {
            parentPositionValue = parseFloat(this.convertUnitsToPixel(element.parentElement.id, element.parentElement.style.width, property));
        } else if (property === 'backgroundPositionX' ||
            property === '-webkit-mask-position-x' ||
            property === 'backgroundSizeWidth' ||
            property === 'transform-origin-x' ||
            property === 'perspective-origin-x' ||
            property === 'translateX' ||
            property === '-webkit-mask-sizeWidth') {
            let width = element.style.width;
            const unit = helpers.getUnitStyle(width);
            if (unit === '%') {
                width = this.convertUnitsToPixel(element.id, element.style.width, 'width');
            }
            parentPositionValue = parseFloat(width);
        } else if (property === 'backgroundPositionY' ||
            property === '-webkit-mask-position-y' ||
            property === 'backgroundSizeHeight' ||
            property === 'transform-origin-y' ||
            property === 'perspective-origin-y' ||
            property === 'translateY' ||
            property === '-webkit-mask-sizeHeight') {
            let height = element.style.height;
            const unit = helpers.getUnitStyle(height);
            if (unit === '%') {
                height = this.convertUnitsToPixel(element.id, element.style.height, 'height');
            }
            parentPositionValue = parseFloat(height);
        }

        return this.convertPercentToPixel(value, parentPositionValue);
    },
    /**
     * Handles pixel to percent handling based on the parent reference.
     *
     * @function pixelToPercentHandling
     * @param id of the element
     * @param pixel value to be converted
     * @param property edited(defines parent references)
     * @return number value
     */
    pixelToPercentHandling: function (id, value, property) {
        var parentPositionValue;
        var $element = document.getElementById(id);
        var offsets = helpers.getAbsolutePosition($element, $element.parentElement.id);
        if (property === 'top') {
            parentPositionValue = parseFloat(getComputedStyle($element.parentElement, null).getPropertyValue('height'));
            return this.convertPixelToPercent(offsets.top, parentPositionValue);
        } else if (property === 'left') {
            parentPositionValue = parseFloat(getComputedStyle($element.parentElement, null).getPropertyValue('width'));
            return this.convertPixelToPercent(offsets.left, parentPositionValue);
        } else if (property === 'width') {
            var parentWidth = parseFloat(getComputedStyle($element.parentElement, null).getPropertyValue('width'));
            var elWidth = getComputedStyle($element, null).getPropertyValue('width');

            // WHAT IS THIS USED FOR //
            if (helpers.getUnitStyle(elWidth) === '%') {
                elWidth = this.convertPercentToPixel(parseFloat(elWidth), parentWidth);
            }
            // --------------------- //

            return this.convertPixelToPercent(parseFloat(elWidth), parentWidth);
        } else if (property === 'height') {
            var parentHeight = parseFloat(getComputedStyle($element.parentElement, null).getPropertyValue('height'));
            var elHeight = getComputedStyle($element, null).getPropertyValue('height');

            // WHAT IS THIS USED FOR //
            if (helpers.getUnitStyle(elHeight) === '%') {
                elHeight = this.convertPercentToPixel(parseFloat(elHeight), parentHeight);
            }
            // --------------------- //

            return this.convertPixelToPercent(parseFloat(elHeight), parentHeight);

        } else if (property === 'backgroundSizeWidth' ||
            property === 'backgroundPositionX' ||
            property === 'transform-origin-x' ||
            property === 'perspective-origin-x' ||
            property === 'translateX' ||
            property === '-webkit-mask-sizeWidth') {
            var width = getComputedStyle($element, null).getPropertyValue('width');
            return this.convertPixelToPercent(value, parseFloat(width));
        } else if (property === 'backgroundSizeHeight' ||
            property === 'backgroundPositionY' ||
            property === 'transform-origin-y' ||
            property === 'perspective-origin-y' ||
            property === 'translateY' ||
            property === '-webkit-mask-sizeHeight') {
            var height = getComputedStyle($element, null).getPropertyValue('height');
            return this.convertPixelToPercent(value, parseFloat(height));
        }
    },
    convertVwToPixel: function (num) {
        var sceneSize = helpers.getSceneSize();

        return (parseFloat(num) * sceneSize.width) / 100;
    },
    convertVhToPixel: function (num) {
        var sceneSize = helpers.getSceneSize();

        return (parseFloat(num) * sceneSize.height) / 100;
    },
    convertVmaxToPixel: function (num) {
        var sceneSize = helpers.getSceneSize();

        var sceneH = sceneSize.height;
        var sceneW = sceneSize.width;

        var largerSize = sceneH > sceneW ? sceneH : sceneW;

        return (parseFloat(num) * largerSize) / 100;
    },
    convertVminToPixel: function (num) {
        var sceneSize = helpers.getSceneSize();

        var sceneH = sceneSize.height;
        var sceneW = sceneSize.width;

        var smallerSize = sceneH > sceneW ? sceneW : sceneH;

        return (parseFloat(num) * smallerSize) / 100;
    },
    convertPtToPixel: function (num) {
        return (parseFloat(num) * 4) / 3;
    },
    convertPixelToRem: function (num) {
        var sceneFontSize = helpers.getSceneFontSize();
        return parseFloat(num) / sceneFontSize;
    },
    convertRemToPixel: function (num) {
        var sceneFontSize = helpers.getSceneFontSize();
        return parseFloat(num) * sceneFontSize;
    },
    convertPixelToVh: function (num) {
        var sceneSize = helpers.getSceneSize();

        return (100 / sceneSize.height) * num;
    },
    convertPixelToVw: function (num) {
        var sceneSize = helpers.getSceneSize();

        return (100 / sceneSize.width) * num;
    },
    convertPixelToVmin: function (num) {
        var sceneSize = helpers.getSceneSize();

        var sceneH = sceneSize.height;
        var sceneW = sceneSize.width;

        var smallerSize = sceneH > sceneW ? sceneW : sceneH;

        return (100 / smallerSize) * num;
    },
    convertPixelToVmax: function (num) {
        var sceneSize = helpers.getSceneSize();

        var sceneH = sceneSize.height;
        var sceneW = sceneSize.width;

        var largerSize = sceneH > sceneW ? sceneH : sceneW;

        return (100 / largerSize) * num;
    },
    convertPixelToPt: function (num) {
        return (parseFloat(num) / 4) * 3;
    },
    convertPixelToPercent: function (number, parentSize) {
        return (parseFloat(number) / parseFloat(parentSize)) * 100;
    },
    convertPercentToPixel: function (number, parentSize) {
        return (parseFloat(parentSize) * parseFloat(number)) / 100;
    },
    toDegrees: function (angle) {
        return parseFloat(angle) * (180 / Math.PI);
    },
    toRadians: function (angle) {
        return parseFloat(angle) * (Math.PI / 180);
    }
};
