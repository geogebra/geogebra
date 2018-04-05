/**
 *  @module lib/animations/types
 *  @requires module:lib/function_helpers
 */
'use strict';

import helpers from '../function_helpers';

export default function (obj) {

    for (var property in obj) {

        switch (property) {
            case '-webkit-filter':
                return 'filterProperties';
            case 'box-shadow':
                return 'boxShadowProperties';
            case 'background-color':
                return 'backgroundColor';
            case 'color':
                return 'color';
            case 'border-color':
            case 'z-index':
            case 'border-width':
            case 'border-top-left-radius':
            case 'border-top-right-radius':
            case 'border-bottom-left-radius':
            case 'border-bottom-right-radius':
            case 'background-position-x':
            case 'background-position-y':
            case 'background-size':
            case 'font-size':
            case 'font-style':
            case 'font-weight':
                return helpers.convertCssToJsProperty(property);
        }
        return property;
    }
}

