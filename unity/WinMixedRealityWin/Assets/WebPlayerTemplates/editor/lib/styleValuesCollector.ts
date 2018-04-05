'use strict';

import Enums from './enums';
import helpers from './function_helpers';
import couiEditor from '../scripts/main';

module styleValuesCollector {
    var widgetObj: IWidget;

    /**
     * Get complete string style value from widget
     * @param group
     * @param property
     * @param widget
     * @returns {string}
     */
    export function getValue(group: string, property: string, widget: IWidget): string {

        let styleType: ISaveType = Enums.StyleTypes;
        widgetObj = widget;

        switch (group) {
            case styleType.geometry:
                return getGeometryValue(group, property);
            case styleType.transform:
                return getTansformValue(group, property);
            case styleType['-webkit-filter']:
                return getFilterValue(group, property);
            case styleType.boxShadow:
                return getBoxShadowValue(group, property);
            case styleType.backgroundColor:
                return getBackgroundValue(group, property);
            case styleType.styles:
                if (property === 'background') {
                    return getBackgroundValue(group, property);
                }
                return getStyleValue(group, property);
            case styleType.font:
                return getFontValue(group, property);
            case styleType.units:
                if (property === 'fontSize') {
                    return getFontValue(group, property);
                }
        }
    }

    function getGeometryValue(group: string, property: string): string {
        return widgetObj[group][property];
    }

    function getTansformValue(group: string, property: string): string {
        return widgetObj[group][property];
    }

    function getFilterValue(group: string, property: string): string {
        let value = widgetObj[group][property] || '0px';
        let element = document.getElementById(widgetObj.id);
        let elStyle = element.style['-webkit-filter'];

        return (
            helpers.splitCssStringProperties(widgetObj.id, elStyle, 'webkitFilter', property, value, function (val) {
                return val;
            })
        );
    }

    function getBoxShadowValue(group: string, property: string): string {
        var runtimeEditor = couiEditor.openFiles[couiEditor.selectedEditor].runtimeEditor;
        return runtimeEditor.buildBoxShadowProperty(widgetObj);
    }

    function getBackgroundValue(group: string, property: string): string {
        return widgetObj[property];
    }

    function getStyleValue(group: string, property: string): string {
        if (property === 'backgroundSizeWidth' || property === 'backgroundSizeHeight') {
            let bgWidth = widgetObj[group]['backgroundSizeWidth'] || '';
            let bgHeight = widgetObj[group]['backgroundSizeHeight'] || '';
            let bgSize = bgWidth + ' ' + bgHeight;
            return bgSize.trim();
        }

        if (property === '-webkit-mask-sizeWidth' || property === '-webkit-mask-sizeHeight') {
            let maskWidth = widgetObj[group]['-webkit-mask-sizeWidth'] || '';
            let maskHeight = widgetObj[group]['-webkit-mask-sizeHeight'] || '';
            let maskSize = maskWidth + ' ' + maskHeight;
            return maskSize.trim();
        }
        return widgetObj[group][property];
    }

    function getFontValue(group: string, property: string): string {
        return widgetObj[property];
    }
}

export default styleValuesCollector;

