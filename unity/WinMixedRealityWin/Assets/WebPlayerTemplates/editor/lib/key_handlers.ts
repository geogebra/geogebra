/**
 *  @module lib/key_handlers
 *  @requires module:lib/enums
 *  @requires module:lib/functions_helpers
 *  @requires module:scripts/main
 */

'use strict';
declare let $;
declare let document;

import Enums from './enums';
import helpers from './function_helpers';
import couiEditor from '../scripts/main';

namespace KeyhandlersFunction {

    /**
     *
     * @param moveDirection
     */
    export function arrowKeysHandler(moveDirection) {
        var currentRuntimeEditor = couiEditor.openFiles[couiEditor.selectedEditor].runtimeEditor;
        var currentSelectedElements = currentRuntimeEditor.currentParentElementsSelection;
        var currentSelectedWidgetGeometry;
        var step = event.shiftKey ? 5 : 1;

        if (currentSelectedElements.length === 1) {
            currentSelectedWidgetGeometry = currentRuntimeEditor.mappedWidgets[currentSelectedElements].widget.geometry;
            var topUnitStyle = helpers.getUnitStyle(currentSelectedWidgetGeometry.top);
            var leftUnitStyle = helpers.getUnitStyle(currentSelectedWidgetGeometry.left);

            if ((moveDirection === 'left' || moveDirection === 'right') && leftUnitStyle !== 'px') {
                step = step * 0.1;
            }

            if ((moveDirection === 'up' || moveDirection === 'down') && leftUnitStyle !== 'px') {
                step = step * 0.1;
            }
        }

        if (currentSelectedElements.length > 1) {
            currentSelectedElements.forEach(function (elementId) {
                helpers.moveWidget({
                    elementId: elementId,
                    moveDirection: moveDirection,
                    step: step,
                    preserveUnits: false
                });
            });
            currentRuntimeEditor.selectMultiJstreeItems(currentSelectedElements);
        } else {
            if (currentRuntimeEditor.currentElementsSelection.length === 1) {
                helpers.moveWidget({
                    elementId: currentSelectedElements[0],
                    moveDirection: moveDirection,
                    step: step,
                    preserveUnits: true
                });
                currentRuntimeEditor.computeSelectCorners($(`#${currentSelectedElements[0]}`));
            }
        }
    }

    /**
     * Select next/prev widget
     * @param {boolean} direction - true for next / false for previous
     * @param {object} runtimeEditor
     */
    export function selectElement(direction, runtimeEditor) {
        var currentSelectedElementId = runtimeEditor.currentParentElementsSelection;
        var hierarchyTree = $('#sceneTree').jstree();
        /*jshint camelcase: false */
        var selectedElementIdInHierarchyTree = hierarchyTree.get_selected()[0];
        /*jshint camelcase: true */
        var currentElementInHierarchyTree = $('#' + selectedElementIdInHierarchyTree);
        var firstElementInHierarchyTree = currentElementInHierarchyTree.siblings().first().attr('data-id');
        var lastElementInHierarchyTree = currentElementInHierarchyTree.siblings().last().attr('data-id');

        var selectableElementID;

        if (currentSelectedElementId.length !== 1) {
            return;
        }

        if (direction) {
            var nextElementInHierarchyTree = currentElementInHierarchyTree.next('li').attr('data-id');
            selectableElementID = nextElementInHierarchyTree || firstElementInHierarchyTree;
        } else {
            var prevElementInHierarchyTree = currentElementInHierarchyTree.prev('li').attr('data-id');
            selectableElementID = prevElementInHierarchyTree || lastElementInHierarchyTree;
        }

        if (selectableElementID) {
            runtimeEditor.selectJstreeItem(selectableElementID);
        }
    }

    export function switchElement(event, runtimeEditor) {
        event.preventDefault();

        var isDirectionForward = event.shiftKey ? false : true;

        selectElement(isDirectionForward, runtimeEditor);
    }

    export function attachKeyHandlersGlobal() {
        $('body').off('keydown');
        $('body').off('keyup');
        $('body').on('keydown', function (event) {
            switch (event.which) {
                case Enums.Keys.s:
                    if (event.ctrlKey) {
                        event.preventDefault();

                        // In case of scene save when a property input is active,
                        // validate the input value and apply if valid
                        const element = document.activeElement;
                        if (element.tagName === 'INPUT' && element.getAttribute('property') !== null) {
                            $(document.activeElement).blur();
                        }

                        var currentTab = couiEditor.openFiles[couiEditor.selectedEditor];
                        var filename = currentTab.tab.filePath + currentTab.tab.filename;

                        couiEditor.save(filename, currentTab.file.valueOf());
                    }
                    break;
                default:
                    break;
            }
        });
    }

    export function attachkeyHandlersRuntimeEditor(runtimeEditor) {
        let _this = this;
        this.attachKeyHandlersGlobal();
        $('body').on('keydown', function (event) {
            var currentElementTag = document.activeElement.tagName;
            if (currentElementTag !== 'INPUT' && currentElementTag !== 'TEXTAREA') {
                switch (event.which) {
                    case Enums.Keys.p:
                        event.preventDefault();
                        runtimeEditor.Timeline.setPinheadPosition(0, 0);
                        runtimeEditor.preview('scene');
                        break;
                    case Enums.Keys.c:
                        if (event.ctrlKey) {
                            runtimeEditor.copyWidgets();
                        }
                        break;
                    case Enums.Keys.v:
                        if (event.ctrlKey) {
                            runtimeEditor._sceneActionState.primaryAction = 'new action';
                            runtimeEditor.cloneWidget();
                        }
                        break;
                    default:
                        break;
                }

                if (currentElementTag) {
                    let sceneId: any = Enums.newScene;
                    const scene = document.getElementById(sceneId._DOMId);

                    if (!scene) {
                        return;
                    }

                    const isTargetChildOfScene = scene.contains(event.target);

                    if (isTargetChildOfScene === false) {
                        event.preventDefault();
                        document.activeElement.blur();
                    }

                    switch (event.keyCode) {
                        case Enums.Keys.left:
                        case 16777234:
                            _this.arrowKeysHandler('left');
                            break;
                        case Enums.Keys.up:
                        case 16777235:
                            _this.arrowKeysHandler('up');
                            break;
                        case Enums.Keys.right:
                        case 16777236:
                            _this.arrowKeysHandler('right');
                            break;
                        case Enums.Keys.down:
                        case 16777237:
                            _this.arrowKeysHandler('down');
                            break;
                        case Enums.Keys.tab:
                            event.preventDefault();

                            var isDirectionForward = event.shiftKey ? false : true;

                            _this.selectElement(isDirectionForward, runtimeEditor);
                    }
                }
            }

            // ctrl + z
            if (event.ctrlKey && event.shiftKey === false && event.keyCode === Enums.Keys.z) {
                runtimeEditor.undoRedoScene('undo');
                couiEditor.autoTriggerUndoRedo('undo');
            }

            // ctrl + shift + z
            if (event.ctrlKey && event.shiftKey && event.keyCode === Enums.Keys.z) {
                runtimeEditor.undoRedoScene('redo');
                couiEditor.autoTriggerUndoRedo('redo');
            }
        }).keyup(function (event) {
            var currentElementTag = document.activeElement.tagName;
            if (currentElementTag !== 'INPUT' && currentElementTag !== 'TEXTAREA') {
                switch (event.which) {
                    case Enums.Keys.delete:
                    case 16777223: //Selenium webdriver.sendKeys('DELETE') sends keycode = 16777223
                        event.preventDefault();
                        runtimeEditor._sceneActionState.primaryAction = 'new action';
                        if (runtimeEditor.selectedKeyframes.length > 0) {
                            runtimeEditor.Animations.deleteKeyframe();
                        } else {
                            runtimeEditor.removeMultipleWidgets();
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        $('#sceneTree').off('keydown');
        $('#sceneTree').on('keydown', function (event) {
            if (event.keyCode === Enums.Keys.tab) {
                _this.switchElement(event, runtimeEditor);
            }
        });
    }
}

export default KeyhandlersFunction;
