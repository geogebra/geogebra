/**
 *  @module lib/animations/runtime
 *  @requires module:lib/animations/timeline
 *  @requires module:lib/animations/keyframes
 *  @requires module:lib/animations/exportCss
 *  @requires module:lib/animations/importCss
 */
'use strict';

declare let $;
import Timeline from './timeline';
import Keyframes from './keyframes';
import helpers from '../function_helpers';
import exportCssModule from './exportCss';
import importCssModule from './importCss';
import couiEditor from '../../scripts/main';

export default class Animations {
    runtimeEditor: any;

    init(runtimeEditor) {
        this.runtimeEditor = runtimeEditor;
        this.runtimeEditor.Timeline = new Timeline(runtimeEditor);
        this.runtimeEditor.Keyframes = new Keyframes(runtimeEditor);

        this.runtimeEditor.Timeline.create();
        this.runtimeEditor.Keyframes.init();
    }

    attachKeyframeHandlers(selector) {
        this.runtimeEditor.Keyframes.widgetKeyframesHandlers(selector);
    }

    createAnimationWidget(widget, options) {
        const doesTimelineWidgetExist = $(`[data-timeline-info-widget-id="${widget.id}"]`).length;
        if (couiEditor.preferences.timeline.filterTimelineWidgets && !doesTimelineWidgetExist) {
            const widgetClassNames = widget.className.split(' ');

            widgetClassNames.map((className) => {
                if (this.runtimeEditor.scene.animationClasses[className]) {
                    this.runtimeEditor.Timeline.addWidget(widget, options);
                }
            });
        } else if (!doesTimelineWidgetExist) {
            this.runtimeEditor.Timeline.addWidget(widget, options);
        }
    }

    onKeyframeChange($element, oldTime,
                     currentTime) {
        this.runtimeEditor.Keyframes.onKeyframeChange($element, oldTime,
            currentTime);
    }

    addKeyframe(widgetId, group, property,
                value, position) {
        this.runtimeEditor.Keyframes.addKeyframe(widgetId, group, property, value, position);
        this.runtimeEditor.Timeline.create();
        this.runtimeEditor.exportScene();
    }


    loadTimeline(animObj, initial = true) {
        // create timeline animations for all widget on the scene
        for (var widgetId in this.runtimeEditor.mappedWidgets) {
            var widget = this.runtimeEditor.mappedWidgets[widgetId].widget;
            var $element = $('#' + widget.id);
            if ($element.length > 0 &&
                $element[0].hasAttribute('data-element-selectable')) {

                this.createAnimationWidget(widget, {initial: initial});
            }
        }


        this.loadKeyframes(animObj, initial);
    }

    /**
     * Compare widget classes with animation object class name
     * load keyframes on all widget with equal class name
     * @param animObj
     * @param initial
     * @param undoRedoGlobalClass
     */
    loadKeyframes(animObj: IAnimationClasses, initial: boolean = false, undoRedoGlobalClass: boolean = false) {
        var _this = this;
        // create keyframes
        var compareCssClasses = function (value) {
            if (value === this.className && (!couiEditor.copiedWidgets.widgets[this.widgetId] || initial)) {
                // if is true, recreate animation class name panel
                // in the DOM for each widgets which contains this animation class
                if (undoRedoGlobalClass) {
                    _this.runtimeEditor.Timeline.initAnimationClassName(this.widgetId, '', this.className, false, true);
                }
                _this.runtimeEditor.Keyframes.addKeyframe(this.widgetId, this.group,
                    this.property, this.value, {
                        seconds: this.keyframeTime
                    });
            }
        };

        this.runtimeEditor._sceneActionState.keyframeInitial = true;
        for (var widgetClassName in animObj) {
            let currentKeyframes = animObj[widgetClassName].keyframes;
            for (var group in currentKeyframes) {
                let properties = currentKeyframes[group];
                for (var property in properties) {
                    let currentProperty = properties[property];
                    for (var keyframeTime in currentProperty) {
                        var value = currentProperty[keyframeTime].values[0];
                        var mappedWidgets = this.runtimeEditor.mappedWidgets;
                        for (var widgetId in mappedWidgets) {
                            if (mappedWidgets[widgetId].widget.className) {
                                var keyframeData = {
                                    className: widgetClassName,
                                    widgetId: widgetId,
                                    group: group,
                                    property: property,
                                    value: value,
                                    keyframeTime: keyframeTime
                                };
                                if (!$(`[data-timeline-info-widget-id="${widgetId}"]`).length &&
                                    !$(`#${widgetId}`).is('[data-parent-widget-id]')) {
                                    this.createAnimationWidget(this.runtimeEditor.mappedWidgets[widgetId].widget, {initial: false});
                                }
                                var widgetCssClasses = mappedWidgets[widgetId].widget.className.split(' ');
                                widgetCssClasses.filter(compareCssClasses.bind(keyframeData));
                            }
                        }
                    }
                }
            }
        }

        this.runtimeEditor._sceneActionState.keyframeInitial = false;
        this.runtimeEditor.Timeline.create();
        this.runtimeEditor.exportScene();

        var isWidget = couiEditor.openFiles[couiEditor.selectedEditor].tab.tabWidgetState.editWidget;
        if (isWidget) {
            // Remove
            var wrapperWidgetId = this.runtimeEditor.scene.widgets[0].id;
            helpers.getFromTimeline('[data-timeline-info-widget-id="' + wrapperWidgetId + '"]').hide();
            helpers.getFromTimeline('.widget-line[data-widget-id="' + wrapperWidgetId + '"]').hide();
        }
    }

    deleteKeyframe(keyframeData) {
        if (keyframeData) {
            this.runtimeEditor.Timeline.deleteKeyframe(keyframeData);
        } else {
            this.runtimeEditor.Timeline.deleteSelectedKeyframes();
        }

    }

    editId(oldId, newId) {
        this.runtimeEditor.Timeline.editId(oldId, newId);

        if (this.runtimeEditor.scene.animations[oldId]) {
            this.runtimeEditor.scene.animations[newId] = this.runtimeEditor.scene.animations[oldId];
            this.runtimeEditor.scene.animations[oldId].id = newId;

            if (oldId !== newId) {
                delete this.runtimeEditor.scene.animations[oldId];
            }
        }
    }

    deleteWidget(id) {
        this.runtimeEditor.Timeline.deleteWidget(id);
    }

    exportToCss(animObj) {
        return exportCssModule.exportCss(animObj, this.runtimeEditor.editorTabName);
    }

    importCSS(animObj) {
        return importCssModule.importCss(animObj);
    }
}

