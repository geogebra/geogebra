/**
 *  @module lib/flexbox
 *  @requires module:lib/runtime
 *  @requires css:lib/lib/flexbox-style.css
 */
'use strict';

import runtime from './runtime';

module FlexBox {
    let flexbox = function (widget, result, callback) {
        var element: any = document.createElement('div'),
            elStyle = element.style;

        elStyle.width = widget.width;
        elStyle.height = widget.height;

        if (widget.flexDirection !== undefined) {
            elStyle.WebkitFlexDirection = widget.style.flexDirection;
        }

        if (widget.flexWrap !== undefined) {
            elStyle.WebkitFlexWrap = widget.style.flexWrap;
        }

        if (widget.justifyContent !== undefined) {
            elStyle.WebkitJustifyContent = widget.style.justifyContent;
        }

        if (widget.alignContent !== undefined) {
            elStyle.WebkitAlignContent = widget.style.alignContent;
        }

        if (widget.alignItems !== undefined) {
            elStyle.WebkitAlignItems = widget.style.alignItems;
        }

        runtime.setupProperties(widget, element, result, callback);
    };

    let flexboxChild = function (widget, result, callback) {
        var element: any = document.createElement('div'),
            elStyle = element.style;
        if (widget.order !== undefined) {
            elStyle.WebkitOrder = widget.style.order;
        }

        if (widget.flex !== undefined) {
            elStyle.WebkitFlex = widget.style.flex;
        }

        if (widget.alignSelf !== undefined) {
            elStyle.WebkitAlignSelf = widget.style.alignSelf;
        }

        runtime.setupProperties(widget, element, result, callback);
    };

    runtime.addFactory('flexboxChild', flexboxChild);
    runtime.addFactory('flexbox', flexbox);
}

export default FlexBox;
