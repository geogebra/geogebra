/**
 *  @module lib/handlebars_helpers
 *  @requires module:scripts/main
 *  @requires module:bower_components/handlebars/handlebars
 */
'use strict';
declare let $;

import couiEditor from '../scripts/main';
import helpers from  'lib/function_helpers';

var Handlebars = require('../bower_components/handlebars/handlebars');

Handlebars.registerHelper('debug', function (optionalValue) {
    console.log('Current Context');
    console.log('====================');
    console.log(this);

    if (optionalValue) {
        console.log('Value');
        console.log('====================');
        console.log(optionalValue);
    }
});

Handlebars.registerHelper('is_in_scene', function (url, type, options) {
    let fullUrl = url.replace('uiresources/', '');
    const scene = couiEditor.openFiles[couiEditor.selectedEditor].runtimeEditor.scene;

    if (fullUrl.startsWith('../')) {
        fullUrl = fullUrl.replace(/\.\.\//g, '');
    }

    const fnFalse = options.inverse;
    const fnTrue: any = options.fn;

    if (type === 'style') {
        type = 'styles';
    } else {
        type = 'scripts';
    }

    for (let i = 0; i < scene[type].length; i++) {
        const externalPathFile = scene[type][i].replace(/\?.*/, '');

        if (externalPathFile === fullUrl) {
            return fnTrue();
        }
    }

    return fnFalse();
});

Handlebars.registerHelper('font-enabled', function (url, type, options) {
    const fontUrl = url.replace('uiresources/', '').replace(/\\/g, '/');
    const scene = couiEditor.openFiles[couiEditor.selectedEditor].runtimeEditor.scene;

    for (let i = 0; i < scene['fonts'].length; i++) {
        if (fontUrl === scene['fonts'][i]) {
            return options.fn();
        }
    }

    return options.inverse();
});

Handlebars.registerHelper('each_hash', function (context, options) {

    var fn: any = options.fn,
        inverse: any = options.inverse;
    var result = '';

    if (typeof context === 'object') {
        for (var key in context) {
            if (context.hasOwnProperty(key)) {
                // clone the context so it's not
                // modified by the template-engine when
                // setting "_key"
                var ctx = $.extend({
                        '_key': key
                    },
                    context[key]);

                result = result + fn(ctx);
            }
        }
    } else {
        result = inverse(this);
    }
    return result;
});

Handlebars.registerHelper('select', function (value, options) {
    var $el = $('<select />').html(options.fn(this));
    $el.find('[value="' + value + '"]').attr({
        'selected': 'selected'
    });
    return $el.html();
});

Handlebars.registerHelper('radioOption', function (value, property, options) {
    if (options === undefined) {
        options = property;
    }
    var $el = $('<radioOption />').html(options.fn(this));
    if (property === 'boxShadow' && value === undefined) {
        $el.find('[value="remove"]').attr({
            'checked': 'checked'
        });
    } else {
        $el.find('[value="' + value + '"]').attr({
            'checked': 'checked'
        });
    }
    return $el.html();
});

Handlebars.registerHelper('select_units', function (value, options) {
    var $el = $('<select />').html(options.fn(this));
    if (value !== undefined) {
        var regex = /\D+$/;
        var result = value.match(regex);

        $el.find('[value="' + result + '"]').attr({
            'selected': 'selected'
        });
    }
    return $el.html();

});

Handlebars.registerHelper('ifvalue', function (conditional, options) {
    if (options.hash.value === conditional) {
        return options.fn(this);
    } else {
        return options.inverse(this);
    }
});

Handlebars.registerHelper('ifvalueNot', function (conditional, options) {
    if (options.hash.value !== conditional) {
        return options.fn(this);
    } else {
        return options.inverse(this);
    }
});

/**
 * fallow the format
 *  {{#ifvalues widget.styles.webkitMaskImage value='{items": [" ", "initial"]}'}}
 *  {{#ifvalues widget.styles.webkitMaskImage value='{"notEqual": "true","items": [" ", "initial"]}'}}
 */
Handlebars.registerHelper('ifvalues', function (conditional, options) {
    var obj = JSON.parse(options.hash.value);
    var len = obj.items.length;

    if (obj.notEqual) {
        for (var i = 0; i < len; i++) {
            if (obj.items[i] === conditional) {
                if (obj.showHide) {
                    let $html = $(options.fn(this));
                    $html.addClass('hidden');
                    return $html.prop('outerHTML');
                } else {
                    return options.inverse(this);
                }
            }
        }
        return options.fn(this);
    } else {
        for (var j = 0; j < len; j++) {
            if (obj.items[j] === conditional) {
                return options.fn(this);
            }
        }
        return options.inverse(this);
    }
});

Handlebars.registerHelper('option', function (value, label, selectedValue) {
    var selectedProperty = value === selectedValue ? 'selected="selected"' : '';
    return new Handlebars.SafeString('<option value="' + value + '"' + selectedProperty + '>' + label + '</option>');
});

Handlebars.registerHelper('format_value', function (value, options) {
    if (value) {
        var regex = /[^a-zA-Z%]+/;
        var unit = value.split(regex);
        if (unit) {
            if (unit[1] === 'px' || unit[1] === 'deg' || unit[1] === 'rad' || unit[1] === '%') {
                return 1;
            } else {
                var formatTo = couiEditor.openFiles[couiEditor.selectedEditor].runtimeEditor.getFormat();

                return (1 / (Math.pow(10, formatTo)));
            }
        }
    }
});

Handlebars.registerHelper('format_name', function (value, options) {
    // shortening longer than 20 char strings //
    if (value.length > 20) {
        var strLength = value.length;
        value = value.substring(strLength - 20, strLength);
        return '...' + value;
    }
    return value;
});

Handlebars.registerHelper('is_component', function (value, options) {
    if (value.name.endsWith('.component')) {
        return '<span class="fa fa-remove fa-right-small delete-component" ' +
            'data-url="' + value.url + '" data-link="' + value.name + '"></span>';
    }
    return '';
});

Handlebars.registerHelper('is_widget', function (value, options) {
    if (!value.name.endsWith('.component')) {
        return '<span class="fa fa-sign-out fa-right-small convert-widget" ' +
            'data-url="' + value.url + '" data-link="' + value.name + '"></span>';
    }
    return '';
});

Handlebars.registerHelper('format_name_background_url', function (value, options) {
    return helpers.cleanBackgroundUrls(value);
});

Handlebars.registerHelper('number_only', function (value, options) {
    var formatTo = couiEditor.openFiles[couiEditor.selectedEditor].runtimeEditor.getFormat();
    value = value.toString();
    // FILTERS NO NUMBER VALUES FROM THE NUMBER ONLY INPUT //
    if (value !== undefined) {
        if (value === 'auto') {
            return '0';
        }
        var regex = /[^a-zA-Z%]+/;
        var result = value.match(regex);
        if (result !== null) {
            var newResult = parseFloat(result[0]);
            var unit = value.split(regex)[1];
            if (unit === 'px' || unit === 'deg' || unit === 'rad') {
                return parseInt(newResult.toString());
            } else {
                return newResult;
            }
        } else {
            return result;
        }
    } else {
        return;
    }
});

Handlebars.registerHelper('rad_to_deg', function (value, options) {
    if (value !== undefined) {
        var deg = 0;
        if (value !== undefined) {
            var regex = /[^a-zA-Z%]+/;
            var number = value.match(regex);
            var unit = value.split(regex)[1];
            if (unit !== 'deg') {
                number = number * (180 / Math.PI);
            }
            return parseInt(number);
        }
    }
    return;
});

Handlebars.registerHelper('ifCond', function (v1, operator, v2, opts) {

    var isTrue = false;
    switch (operator) {
        case '===':
            isTrue = v1 === v2;
            break;
        case '!==':
            isTrue = v1 !== v2;
            break;
        case '<':
            isTrue = v1 < v2;
            break;
        case '<=':
            isTrue = v1 <= v2;
            break;
        case '>':
            isTrue = v1 > v2;
            break;
        case '>=':
            isTrue = v1 >= v2;
            break;
        case '||':
            isTrue = v1 || v2;
            break;
        case '&&':
            isTrue = v1 && v2;
            break;
    }
    return isTrue ? opts.fn(this) : opts.inverse(this);
});

Handlebars.registerHelper('CompileBackground', function (widget, opts) {
    var isTrue = false;
    if (widget.type === 'image' ||
        widget.type === 'responsiveImage' ||
        widget.styles.webkitMaskImage !== undefined ||
        widget.background !== undefined ||
        widget.styles.backgroundImage !== undefined) {
        isTrue = true;
    }
    return isTrue ? opts.fn(this) : opts.inverse(this);
});

Handlebars.registerHelper('hideHummingbird', function (value, options) {
    if (value !== 'Hummingbird') {
        return options.fn(this);
    }
});

export default Handlebars;

