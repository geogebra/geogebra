/// <reference path="../../typings/coui-editor/common/types.d.ts"/>
/// <reference path="../../typings/vex-js/index.d.ts"/>
/// <reference path="../../typings/coui-editor/runtime_editor.d.ts"/>
// global define, System, jQuery, kendoPanelBar()
// Important methods
// CreateElementOnTheScene - create single widget
// CreateElementsOnTheScene - create widget with children
// removeWidget - delete widget, delete element
// initElementOnScene = create a widget by dragging on the scene
// saveScene - save scene and reload if a file is added or deleted
// exportScene - save current scene state without saving in file
// selectJstreeItem - this method selects and highlights an element on the
// scene and calls renderProperties
// jsTreeAddNode - add node in hieratchy
// clearSelectedElements = removes current widget selection on the scene
// _sceneJsTree - attach jstree handlers
// switchAnimationClassName

/**
 *  @module lib/runtime_editor
 *  @requires module:lib/runtime
 *  @requires module:lib/flexbox
 *  @requires module:lib/handlebars_helpers
 *  @requires module:lib/drag_element
 *  @requires module:lib/interact
 *  @requires module:lib/widget_selection
 *  @requires module:lib/enums
 *  @requires module:lib/components/components
 *  @requires module:lib/animations/runtime
 *  @requires module:lib/function_helpers
 *  @requires module:scripts/helpers/units_conversion
 *  @requires module:lib/styleValuesGetter
 *  @requires module:lib/allowedKeyframeProperies
 *  @requires module:lib/helpers/vexConfirm
 *  @requires hbs:lib/hbs/widget_list.hbs
 *  @requires hbs:lib/hbs/files_list.hbs
 *  @requires hbs:lib/hbs/editing_menu.hbs
 *  @requires hbs:lib/hbs/widget_properties.hbs
 *  @requires hbs:lib/hbs/text_properties.hbs
 *  @requires hbs:lib/hbs/background_properties.hbs
 *  @requires hbs:lib/hbs/events_properties.hbs
 *  @requires hbs:lib/hbs/geometry_properties.hbs
 *  @requires hbs:lib/hbs/transform_properties.hbs
 *  @requires hbs:lib/hbs/border_style_properties.hbs
 *  @requires hbs:templates/editor/scene_properties.hbs
 *  @exports class:RuntimeEditor
 */

'use strict';

declare let $;


import filtersConfig from './common/filters_config';
import Enums from './enums';
import flexbox from './flexbox';
import runtime from './runtime';
import Drag from './drag_element';
import couiEditor from '../scripts/main';
import helpers from './function_helpers';
import Scene from '../scripts/scene/scene';
import Animations from './animations/runtime';
import editorSettings from './editor_settings';
import WidgetSelection from './widget_selection';
import VexConfirm from './common/vexConfirm';
import editorProperties from './editor_properties';
import buildWidgetHandler from './build_widget_settings';
import styleValuesCollector from './styleValuesCollector';
import unitsConvertor from '../scripts/helpers/units_conversion';
import AllowedKeyframeProperties from './allowedKeyframeProperies';
import SceneInteract from './interact';
import Transform from './transform';

import VirtualList from './modules/virtual_list/virtual-list';

import {configuration as inputTypeConfig, nonValidatedElements} from './configs/input_type_validations';

import InputValidator from './modules/validation/validator';
import {createVDOM} from './modules/virtual_list/helpers';

import InputSearch from './modules/search/search';
import * as search_helpers from './modules/search/helpers';

import Sorter from './modules/asset_sorter/sorter';
import * as sort_helpers from './modules/asset_sorter/helpers';

import {dispatch} from './common/utils';

// HANDLEBAR TEMPLATES //

let widgetHbs = require('lib/hbs/widget_list.hbs!text');
let AssetsHbs = require('lib/hbs/assets_list.hbs!text');
let editingMenuHbs = require('lib/hbs/editing_menu.hbs!text');
let propertiesHbs = require('lib/hbs/widget_properties.hbs!text');
let textPropertiesHbs = require('lib/hbs/text_properties.hbs!text');
let backgroundPropertiesHbs = require('lib/hbs/background_properties.hbs!text');
let dataBindingPropertiesHbs = require('lib/hbs/data_binding_properties.hbs!text');
let eventsPropertiesHbs = require('lib/hbs/events_properties.hbs!text');
let geometryPropertiesHbs = require('lib/hbs/geometry_properties.hbs!text');
let transformPropertiesHbs = require('lib/hbs/transform_properties.hbs!text');
let borderStylePropertiesHbs = require('lib/hbs/border_style_properties.hbs!text');
let blendModesHbs = require('lib/hbs/blend_modes.hbs!text');
let scenePropertiesHbs = require('templates/editor/scene_properties.hbs!text');

const fontFamilyTemplate = require('lib/hbs/templates/fontFamilyOption.hbs!text');
const selectedFontFamily = require('lib/hbs/templates/selectedFontFamily.hbs!text');

let redoLenCommands = 0;
let undoLenCommands = 0;
let commandUndo = [];
let commandRedo = [];
let redoLenCount = 1;
let undoLenCount = 1;

const Interact = SceneInteract.Interact;

/**
 * This is a description of the RuntimeEditor
 * @memberOf module:lib/runtime_editor
 * @class
 * @constructor
 */
export default class RuntimeEditor {
    private forceAutoKeyframes: boolean;
    private runtimeBuildDropShodowFilter: IDropShodow;
    widgets: any;
    equalProportion: boolean;
    scenePreviewEl: any;
    sceneBorderSize: number;
    styleValuesCollector: any;
    currentElementsSelection: any;
    isInPanningMode: boolean;
    currentParentElementsSelection: any;
    selectedKeyframes: any;
    _undoCreationStepsLength: number;
    _lastDeletedAnimationWidgets: any;
    currentWrappedSelectionSize: any;
    sceneAssetSelected: boolean;
    scene: IScene;
    elementReposition: boolean;
    _skipUndoRedoSteps: number;
    totalAnimationTime: number;
    WidgetSelection: any;
    mappedWidgets: any;
    sceneAssetPreviewImage: any;
    sceneAssetPreviewVideo: any;
    sceneAssetPreviewWidget: any;
    sceneAssetPreviewStandin: any;
    sceneAssetPreviewWindow: any;
    widgetsEl: any;
    propertiesElBar: any;
    scenePropertiesElBar: any;
    contextMenuEl: any;
    assetsBarContent: any;

    assetsLibraryHolder: any;
    assetsHierarchyHolder: any;
    scenePropertiesHolder: any;
    rightPanel: any;

    sceneWrapper: any;
    _isElementRotated: boolean;
    _sceneClickingState: any;
    editorTabName: string;
    _openedFile: any;
    _pickedAsset: any;
    autoKeyframe: boolean;
    repeatAnimation: boolean;
    _sceneActionState: ISceneActions;
    tab: any;
    interactElementsState: string;
    _dragFlag: boolean;
    timeline: any;
    formatTo: number;
    templateProperties: any;
    templateTextProperties: any;
    templateBackgroundProperties: any;
    templateDataBindingEvents: any;
    templateEventsProperties: any;
    templateGeometryProperties: any;
    templateTransformProperties: any;
    templateBorderStyleProperties: any;
    templateBlendModes: any;
    Animations: any;
    createElementEvent: any;
    iframe: boolean;
    Timeline: any;
    flexBoxHolder: any;
    TEMPLATE_CACHE: any;
    draggedElement: any;
    shouldEnableFont: boolean;
    private transform: Transform;
    private virtualList: VirtualList;
    private totalAssetTypes: number;
    private inputValidator: InputValidator;
    public inputSearch: InputSearch;
    private assetsDOM: HTMLElement[];
    private filteredAssetDOM: HTMLElement[];
    private pendingTransformOriginPoint;
    public assetSorter: Sorter;
    private activeFilterButtons: string[];

    constructor() {
        this.transform = new Transform();
        this.forceAutoKeyframes = false;
        this.runtimeBuildDropShodowFilter = helpers.getBoxShodowDefaults();
        this.equalProportion = false;

        this.TEMPLATE_CACHE = {
            get: function (selector) {
                if (!this.templates) {
                    this.templates = {};
                }
                let template = this.templates[selector];
                if (!template) {
                    template = document.getElementById(selector);
                    this.templates[selector] = template;
                }
                return template;
            },
            set: function (oldID, newID) {
                if (!this.templates) {
                    this.templates = {};
                }
                let template = this.templates[oldID];
                if (template) {
                    this.templates[newID] = this.templates[oldID];
                    delete this.templates[oldID];
                }
            }
        };

        this.flexBoxHolder = flexbox;
        this.widgets = runtime.factories;
        this.scenePreviewEl = document.getElementById('scene');
        this.sceneBorderSize = 3;
        this.styleValuesCollector = styleValuesCollector;
        /**
         * Current selected widgets on the scene
         * @property {array} currentElementsSelection
         * @example `['coui_rectangle_1238','coui_circle_34242']`
         */
        this.currentElementsSelection = [];

        /**
         * Track current panning state, used in initScenePanZoom key events
         * @type {boolean}
         */
        this.isInPanningMode = false;

        /**
         * Only top parent widgets ids starting from current selected widgets
         * @property {array} currentElementsSelection
         * @example `['coui_rectangle_1238','coui_circle_34242', null]`
         */
        this.currentParentElementsSelection = [];

        this.selectedKeyframes = [];
        /**
         * @property {number} _undoCreationStepsLength
         */
        this._undoCreationStepsLength = 1;
        this._lastDeletedAnimationWidgets = {};
        this.currentWrappedSelectionSize = {
            top: 0,
            left: 0,
            right: 0,
            bottom: 0
        };
        this.sceneAssetSelected = false;
        /**
         * @instaceOf module:scripts/scene/scene.Scene
         * @property {scene} scene
         * @see module:scripts/scene/scene.Scene
         */
        this.scene = new Scene();

        this.virtualList = new VirtualList();

        this.inputValidator = new InputValidator(inputTypeConfig, nonValidatedElements);

        this.inputSearch = new InputSearch();

        this.assetSorter = new Sorter();
        this.activeFilterButtons = [];

        this.totalAssetTypes = 6;

        this.elementReposition = false;
        /**
         * @property {number} _skipUndoRedoSteps
         */
        this._skipUndoRedoSteps = 0;
        /**
         * @property {number} totalAnimationTime
         */
        this.totalAnimationTime = 0;
        /**
         * @instaceOf module:lib/widget_selection.WidgetSelection
         * @property {WidgetSelection} WidgetSelection
         * @see module:lib/widget_selection.WidgetSelection
         */
        this.WidgetSelection = new WidgetSelection.WidgetSelection();
        this.mappedWidgets = {};
        this.sceneAssetPreviewImage = $('#assets-preview-image');
        this.sceneAssetPreviewVideo = $('#assets-preview-video');
        this.sceneAssetPreviewWidget = $('#assets-preview-widget');
        this.sceneAssetPreviewStandin = $('#assets-preview-standin');
        this.sceneAssetPreviewWindow = $('#assets-preview-window');

        this.widgetsEl = $('#widgets-list');
        this.propertiesElBar = $('#properties-bar');
        this.scenePropertiesElBar = $('#scene-properties');
        this.contextMenuEl = $('#context-menu');
        this.assetsBarContent = $('#assets-bar-content');

        this.assetsLibraryHolder = $('.scene-library-holder');
        this.assetsHierarchyHolder = $('.scene-hierarchy-holder');
        this.scenePropertiesHolder = $('.scene-properties-holder');
        this.rightPanel = $('#right-pane');

        /**
         * DOM Element used for scene panning
         * @type {jQuery|Mixed|JQuery|HTMLElement}
         */
        this.sceneWrapper = $('#scene-wrapper');
        this._isElementRotated = false;
        this.scene.widgets.map(function (widget) {
            this.mappedWidgets[widget.id] = {
                widget: widget
            };
        });
        this._sceneClickingState = {
            draggingCreation: false
        };
        this.editorTabName = '';
        this.draggedElement = '';
        this._openedFile = null;
        this._pickedAsset = null;
        this.autoKeyframe = false;
        this.repeatAnimation = false;
        /**
         * @private
         */
        this._sceneActionState = {
            initialLoad: true,
            initialCreationOfWidgetByExternalFile: false,
            primaryAction: 'new action',
            createElement: false,
            deleteElement: false,
            createComponent: false,
            deleteComponent: false,
            widgetOption: false,
            addFile: false,
            deleteFile: false,
            draggingTimelinePin: false,
            elementWasInserted: false,
            addKeyframe: false,
            deleteKeyframe: false,
            moveKeyframe: false,
            keyframeInitial: false,
            setAnimationOptions: false,
            createNewWidget: false,
            pasteWidget: false,
            hierarchyElementMove: false,
            switchAnimationClass: false,
            deleteAnimationClassGlobal: false,
            addAnimationClassGlobal: false,
            editAnimationClass: null,
            insertMaskImage: false,
            moveElement: false,
            initialComponent: false
        };
        this.tab = couiEditor.openFiles[couiEditor.selectedEditor].tab;
        this.tab.snapOn = false;
        this.interactElementsState = 'resize';
        this.pendingTransformOriginPoint = false;
        this._dragFlag = false;
        this.timeline = {};
        this.formatTo = 1;

        this.shouldEnableFont = false;
    }


    /**
     * switch auto keyframe state
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     */
    switchAutoKeyframe() {
        if (this.autoKeyframe) {
            this.autoKeyframe = false;
        } else {
            this.autoKeyframe = true;
        }
    }

    /**
     * switch auto repeat animation state
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     */
    switchRepeatAnimation() {
        if (this.repeatAnimation) {
            this.repeatAnimation = false;
        } else {
            this.repeatAnimation = true;
        }
    }

    /**
     * Removes parent ids which are not belong to current selected widget
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {array} ids - widget ids
     * @return {array} parentIds
     */
    findParentElementsSelection(ids) {
        var parentIds = ids;

        for (var i = 0; i < ids.length; i++) {
            var element = document.getElementById(ids[i]);
            if (element !== null) {
                while (element.parentElement.id !== 'scene') {
                    for (var y = 0; y < ids.length; y++) {
                        if (element.parentElement.id === ids[y]) {
                            parentIds[i] = null;
                        }
                    }
                    element = element.parentElement;
                }
            }
        }
        return parentIds;
    }

    /**
     * Attach `engine.on('AssetChosen')` handler
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @private
     */
    _assetChosenHandler() {
        var _this = this;
        engine.off('AssetChosen');
        engine.on('AssetChosen', function (url, id, type) {
            // TODO: Rework handling once the backend
            // responds to modal window cancel;
            // THE conditional handles all cancel actions
            // but the ones for images where InsertImage
            // deletes an already created empty element;
            if (url !== '' || type === 'image' || type === 'video') {
                _this._sceneActionState.primaryAction = 'new action';
                if (_this.sceneAssetSelected) {
                    _this.sceneAssetChosen(url, type);
                } else {
                    _this.assetChosen(url, id, type);
                    _this.sceneAssetChosen(url, type);
                }
            }

            // reset actions
            _this._sceneActionState.initialCreationOfWidgetByExternalFile = false;
            _this._sceneActionState.insertMaskImage = false;
        });
    }

    /**
     * Insert normal image, webkitMask image or resposive image on the scene
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {string} id - widget id
     * @param {string} url - image url path
     */
    insertImage(id, url) {
        var scenePath = couiEditor.openFiles[couiEditor.selectedEditor].tab.filePath.replace(/\\/g, '/');
        var $element = $('#' + id);
        var elementType = $element.attr('data-type');
        var cuttedUrl = url.replace('uiresources/', '').replace(/ /g, '%20');
        cuttedUrl = cuttedUrl.replace(/url\((.*?)\)/gi, function (RegSelection, RegGroup) {
            return RegGroup;
        });

        if (couiEditor.globalEditorInfo.backend === Enums.Backends.Debug ||
            couiEditor.globalEditorInfo.backend === Enums.Backends.Website) {
            cuttedUrl = 'uiresources/' + cuttedUrl;
        }

        let propValue;

        if (this._sceneActionState.insertMaskImage) {
            propValue = (url !== '') ? 'url(' + cuttedUrl + ')' : '';
            $element.css('webkit-mask-image', propValue);
            this.applyUploadedImage($element, id, cuttedUrl, elementType);
        } else if (elementType === 'image') {
            $element.attr('src', cuttedUrl);
            this.applyUploadedImage($element, id, cuttedUrl, elementType);
        } else if (elementType === 'responsiveImage') {
            propValue = 'url(' + cuttedUrl + ')';
            $element.css('background-image', propValue);
            this.applyUploadedImage($element, id, cuttedUrl, elementType);
        } else {
            propValue = 'url(' + cuttedUrl + ')';
            $element.css('background', propValue);
            this.saveProperties(id, 'css property', 'background', propValue);
            this.renderProperties(this.mappedWidgets[id].widget);
        }
    }

    /**
     * Applies selected image to it's belonging widget
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {jquery_object} $element - jquery element selector
     * @param {string} id - widget id
     * @param {string} url - asset url
     * @param {string} type - widget type
     * @example `[ 'jquerySelector','responsiveImage1','images/mobaSliced2_17.png', 'responsiveImage']`
     */
    applyUploadedImage($element, id, url, type) {
        var _this = this;

        var _tempImg = new Image();
        _tempImg.src = url;

        var widget = this.mappedWidgets[id].widget;

        var firstUpload = helpers.isfirstUpload(type, widget);

        if (firstUpload) {
            if (this._sceneActionState.insertMaskImage) {
                this.saveProperties(id, 'styles', 'webkitMaskImage', ('url(' + url + ')'));
                widget.styles.webkitMaskImage = 'url(' + url + ')';
            } else if (type === 'responsiveImage') {
                this._skipUndoRedoSteps = 3;
                this.saveProperties(id, 'image', 'backgroundImage', ('url(' + url + ')'));
                widget.styles.backgroundImage = 'url(' + url + ')';
            } else if (type === 'image') {
                this._skipUndoRedoSteps = 3;
                this.saveProperties(id, 'image', 'url', url);
                widget.url = url;
            }
        } else {
            if (this._sceneActionState.insertMaskImage) {
                let propValue = (url !== '') ? 'url(' + url + ')' : ' ';
                this.saveProperties(id, 'styles', 'webkitMaskImage', propValue);
                widget.styles.webkitMaskImage = propValue;
                if (propValue === ' ') {
                    this.updateBackgroundFilePathInfo('hide');
                } else {
                    this.updateBackgroundFilePathInfo('show', propValue);
                }
            } else if (type === 'responsiveImage') {
                this.saveProperties(id, 'image', 'backgroundImage', widget.styles.backgroundImage);
                widget.styles.backgroundImage = 'url(' + url + ')';
                this._skipUndoRedoSteps = 2;
            } else if (type === 'image') {
                this.saveProperties(id, 'image', 'url', widget.url);
                widget.url = url;
                this._skipUndoRedoSteps = 2;
            }
        }

        if (!_this._sceneActionState.insertMaskImage) {
            // highlight element after append
            // recalculate image sizes and save width and hight
            _tempImg.onload = function () {
                var pixelWidth = _tempImg.naturalWidth;
                var pixelHeight = _tempImg.naturalHeight;

                var width;
                var height;

                if (firstUpload) {

                    width = unitsConvertor.convertPixelToVw(pixelWidth) + 'vw';
                    height = unitsConvertor.convertPixelToVh(pixelHeight) + 'vh';

                } else {

                    var widthUnit = helpers.getUnitStyle(widget.geometry.width);
                    var heightUnit = helpers.getUnitStyle(widget.geometry.height);

                    if (widthUnit !== 'auto') {
                        width = unitsConvertor.convertPixelToUnit(id, pixelWidth, widthUnit, 'width');
                    } else {
                        width = unitsConvertor.convertPixelToVw(pixelWidth) + 'vw';
                        _this._setProperties(widget, null, 'units', 'width', 'vw');
                    }

                    if (heightUnit !== 'auto') {
                        height = unitsConvertor.convertPixelToUnit(id, pixelHeight, heightUnit, 'width');
                    } else {
                        height = unitsConvertor.convertPixelToVh(pixelHeight) + 'vh';
                        _this._setProperties(widget, null, 'units', 'height', 'vh');
                    }
                }

                _this._setGeometry($element, widget, id, 'geometry',
                    'width', width);
                _this._setGeometry($element, widget, id, 'geometry',
                    'height', height);

                _this.clearSelectedElements();
                _this.selectJstreeItem(id);

                _tempImg = null;
            };
        }

        this._sceneActionState.insertMaskImage = false;
    }

    /**
     * Applies selected video to it's belonging widget
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {string} id - widget id
     * @param {string} url - asset url
     * @example `[ 'video1','images/bigBuckBunny.webm',]`
     */
    insertVideo(id, url) {
        var $element = $('#' + id);
        var _this = this;

        var widget = _this.mappedWidgets[id].widget;

        $element.attr('src', url);
        this.displayVideo($element);

        $element.on('loadedmetadata', function () {

            var width = this.videoWidth + 'px';
            var height = this.videoHeight + 'px';

            _this.saveProperties(id, 'css property', 'url',
                url);

            // skip geometry manipulation record in undo stack
            _this._skipUndoRedoSteps = 2;
            _this._setGeometry($element, widget, id, 'geometry',
                'width', width);
            _this._setGeometry($element, widget, id, 'geometry',
                'height', height);

            _this.clearSelectedElements();
            _this.selectJstreeItem(id);
        });
    }

    /**
     * Adds assets to the runtime scene
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {string} url - asset url
     * @param {string} type - asset type
     * @param {boolean} insert - add or remove handler
     * @example `[ 'video1','images/bigBuckBunny.webm',]`
     */
    _toScene(url, type, insert) {
        let fullUrl = url.replace('uiresources/', '');
        let rebuild;
        const date = new Date();

        if (type === 'style') {
            fullUrl = fullUrl + '?' + date.getTime();
            rebuild = true;
            type = 'styles';
        } else if (type === 'font') {
            type = 'fonts';
            rebuild = true;
        } else {
            type = 'scripts';
        }

        let selectedEditor = couiEditor.selectedEditor;
        const sceneType = this.scene[type];

        if (insert) {
            sceneType.push(fullUrl);

            if (rebuild) {
                couiEditor.rebuildRuntimeEditor(this.scene, this.scene.animations, this.tab.filename, selectedEditor);
            }

            if (type === 'scripts') {
                this.exportScene('file');
            }
        } else {
            for (let i = 0; i < this.scene[type].length; i++) {
                const clearedPath = sceneType[i].replace(/\?.*/, '');

                if (type === 'scripts' && sceneType[i] === fullUrl) {
                    sceneType.splice(i, 1);
                    this.exportScene('file');
                } else if (type !== 'scripts' && clearedPath === fullUrl.replace(/\?.*/, '')) {
                    sceneType.splice(i, 1);
                    if (rebuild) {
                        couiEditor.rebuildRuntimeEditor(this.scene, this.scene.animations, this.tab.filename, selectedEditor);
                    }

                    if (type === 'fonts') {
                        const font = url.replace(/\.[^/.]+$/, '').replace(/^.*[\\\/]/, '');
                        const matchedElements = this._getElementsWithFont(this.mappedWidgets, font);

                        this._resetFontFamily(matchedElements);
                    }

                    return;
                }
            }
        }
    }

    /**
     * Applies selected asset to the scene library
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {string} url - asset url
     * @param {string} type - asset type (images, video, sound, widget)
     * @example `['images/ducky.png', 'image']`
     */
    sceneAssetChosen(url, type) {
        var assetName = url.replace('uiresources/', '').replace(/\//gm, '\\');

        var assetsList = couiEditor.assets[type];
        var newUrl = url.replace('uiresources/', '');

        var inStore = false;
        for (var i = 0; i < assetsList.length; i++) {
            if (assetsList[i].url === newUrl) {
                inStore = true;
            }
        }

        if (!inStore && url !== '') {
            var importedAsset = [{
                __Type: type,
                isFile: true,
                name: assetName,
                url: newUrl
            }];

            couiEditor.updateSceneAssets(importedAsset);
            this.assetsBarContent[0].style.height = '150px';
            document.body.dispatchEvent(new CustomEvent('coui.editor.rebuild'));
            this._initAssetsKendoToolbar();
            this._switchPreviewWindow(url, type);
        } else if (!this.sceneAssetSelected && url !== '') {
            this.assetsBarContent[0].style.height = '150px';
            document.body.dispatchEvent(new CustomEvent('coui.editor.rebuild'));
            this._initAssetsKendoToolbar();
            this._switchPreviewWindow(url, type);
        } else if (url !== '') {
            vex.dialog.alert({
                closeOnOverlayClick: true,
                contentClassName: 'modal-about',
                message: 'Selected asset is already in the assets panel!'
            });
        }

        if (type === 'font' && this.shouldEnableFont) {
            this._toScene(url, type, true);
        }

        this.sceneAssetSelected = false;
        this.shouldEnableFont = false;
    }

    private createAssetDOM(assets) {
        const isWidget = couiEditor.openFiles[this.editorTabName].tab.tabWidgetState.editWidget;

        const assetsList = {
            isWidget: isWidget,
            assets: []
        };

        assetsList.assets = assetsList.assets.concat(
            assets.image,
            assets.video
        );

        if (!isWidget) {
            assetsList.assets = assetsList.assets.concat(
                assets.widget,
                assets.sound,
                assets.script,
                assets.style,
                assets.font);
        }

        const templateFilesList = couiEditor.Handlebars.compile(AssetsHbs);
        return {
            template: templateFilesList(assetsList),
            total: assetsList.assets.length
        };
    }

    /**
     * Renders all scene asset in the scene library toolbar
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     */
    _initAssetsKendoToolbar() {
        const _self = this;

        if (this.assetsBarContent.data('kendoPanelBar')) {
            this.assetsBarContent.data('kendoPanelBar').destroy();
        }

        this.assetsBarContent.empty();

        const assetPanel = this.createAssetDOM(couiEditor.assets);
        this.assetsDOM = createVDOM(assetPanel.template);
        this.filteredAssetDOM = this.assetsDOM;

        const opts = {
            scroller: $('#assets-bar-holder'),
            total: assetPanel.total + this.totalAssetTypes,
            buffer: 4,
            itemHeight: 34
        };

        this._attachLibraryClickEvent();

        const updateVDOM = (e: CustomEvent) => {
            this.virtualList.updateDOM(e.detail);
        };

        const updateFilteredVDOM = (e: CustomEvent) => {
            this.activeFilterButtons = sort_helpers.getActiveSortButtons();
            this.filteredAssetDOM = this.activeFilterButtons.length ? e.detail : this.assetsDOM;

            if (this.inputSearch.getInput()) {
                this.inputSearch.trigger('input');
                return;
            }

            this.virtualList.updateDOM(e.detail);
        };

        const clearSearch = (e) => {
            this.inputSearch.reset();
            this.inputSearch.trigger('input');
        };

        const removeListeners = (e) => {
            document.body.removeEventListener('coui.assets.search', updateVDOM);
            document.body.removeEventListener('coui.assets.filter', updateFilteredVDOM);
            document.body.removeEventListener('coui.editor.rebuild', saveAssetInfo);
            document.body.removeEventListener('coui.editor.rebuild', removeListeners);

            _self.assetSorter.stopListening('click', filterClick);
            _self.inputSearch.stopListening('input', filterSearch);
        };

        const filterClick = (e) => {
            dispatch('coui.assets.filter', sort_helpers.onClickFilter(e, this.assetsDOM));
        };

        const filterSearch = (e) => {
            dispatch('coui.assets.search', search_helpers.assetSearch(e, this.filteredAssetDOM));
        };

        const saveAssetInfo = () => {
            const tabStorage = JSON.parse(localStorage.getItem(_self.editorTabName));

            // In case of tab close, the current localStorage is empty
            if (tabStorage) {
                const extended = JSON.stringify(Object.assign(tabStorage,
                    {
                        assetSearchValue: _self.inputSearch.getInput(),
                        activeFilters: _self.activeFilterButtons
                    }
                ));
                localStorage.setItem(_self.editorTabName, extended);
            }
        };

        this.inputSearch
            .init(document.getElementById('asset-search'))
            .listen('input', filterSearch)
            .listen('click', clearSearch, document.getElementById('asset-search-clear'))
            .listen('coui.editor.rebuild', saveAssetInfo, document.body)
            .focusout(document.getElementById('assets-bar-holder'));

        this.virtualList
            .init(this.assetsBarContent, opts, this.assetsDOM)
            .listen('coui.assets.search', updateVDOM)
            .listen('coui.assets.filter', updateFilteredVDOM)
            .listen('coui.editor.rebuild', removeListeners);

        this.assetSorter
            .init()
            .listen('click', filterClick);

        const searchedAsset = JSON.parse(localStorage.getItem(_self.editorTabName)).assetSearchValue;
        const activeFilters = JSON.parse(localStorage.getItem(_self.editorTabName)).activeFilters;

        if (searchedAsset) {
            (this.inputSearch.input as HTMLInputElement).value = searchedAsset;
            this.inputSearch.trigger('input');
        }

        if (activeFilters) {
            this.activeFilterButtons = activeFilters;
            sort_helpers.applyActive(this.activeFilterButtons);
            dispatch('coui.assets.filter', sort_helpers.filterVDOM(this.activeFilterButtons, this.assetsDOM));
        }
    }

    /**
     * Applies selected asset to the scene
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {string} url - asset url
     * @param {string} id - asset id
     * @param {string} type - asset type (images, video, style)
     * @param {string} pickedAsset - the typeof picked asset (responsiveImage, Image, Video)
     */
    assetChosen(url, id, type, pickedAsset?) {
        if (url.length === 0) {

            if (this._sceneActionState.initialCreationOfWidgetByExternalFile) {
                // The user canceled the operation
                this.removeWidget(id);
            }

            return;
        }

        if (couiEditor.globalEditorInfo.backend === Enums.Backends.Standalone ||
            couiEditor.globalEditorInfo.backend === Enums.Backends.Unreal) {
            url = url.replace('uiresources/', '');
        }

        var correctAssetPicked = pickedAsset || this._pickedAsset;
        couiEditor.reloadedRuntimeEditor++;

        if (type === 'image') {
            this._sceneActionState.elementWasInserted = true;
            this.insertImage(id, url);
        } else if (type === 'video') {
            this._sceneActionState.elementWasInserted = true;
            this.insertVideo(id, url);
        } else {
            // undo-redo options
            var params: any = {};
            var actionState = this.getRedoUndoPrimaryState();
            this._sceneActionState.addFile = true;

            if (type === 'style') {
                this._toScene(url, 'style', true);
                params.index = this.scene.styles.length - 1;
            }

            if (correctAssetPicked === 'script' ||
                correctAssetPicked === 'scripts' ||
                correctAssetPicked === 'events') {
                this._toScene(url, 'script', true);
                params.index = this.scene[correctAssetPicked].length - 1;
            }

            params.url = url;
            params.type = correctAssetPicked;
            this.createUndoRedoCommand(actionState, id, null, null, null, params);
            this.exportScene('file');
            return;
        }

        // edit widget state functionality
        var isWidget = couiEditor.openFiles[this.editorTabName].tab.tabWidgetState.editWidget;
        if (isWidget) {
            this.placeWidgetOnScene(id);
        }
    }

    setOpenedFile(file) {
        this._openedFile = file;
    }

    jstreeDNDHandler() {
        var _this = this;

        $(document).on('dnd_start.vakata', function () {
            _this._sceneActionState.primaryAction = 'new action';
        });
    }

    getOpenedFile() {
        return this._openedFile;
    }

    calcWrappedSelection(widgets) {
        for (var i = 0; i < widgets.length; i++) {
            var widget = widgets[i];
            var element = document.getElementById(widget.id);
            var positions = this.getWidgetBoundingRect(element);

            var selectionPos = this.getSelectionSize();

            if (positions.top < selectionPos.top) {
                this.currentWrappedSelectionSize.top = positions.top;
            }

            if (positions.left < selectionPos.left) {
                this.currentWrappedSelectionSize.left = positions.left;
            }

            if (positions.right > selectionPos.right) {
                this.currentWrappedSelectionSize.right = positions.right;
            }

            if (positions.bottom > selectionPos.bottom) {
                this.currentWrappedSelectionSize.bottom = positions.bottom;
            }

            if (widget.children.length !== 0) {
                this.calcWrappedSelection(widget.children);
            }
        }
    }

    getWidgetBoundingRect(element) {
        var pos = element.getBoundingClientRect();
        var scale = this.tab.sceneTransformMatrix[0];

        return {
            top: pos.top / scale,
            left: pos.left / scale,
            right: pos.right / scale,
            bottom: pos.bottom / scale
        };
    }

    transferAnimations(editedScene, selectedWidgets, widgetLinkName) {
        var _newSceneAnimations = {};
        for (var i = 0; i < selectedWidgets.length; i++) {
            var widgetAnimation = editedScene.animations[selectedWidgets[i].id];
            if (widgetAnimation) {
                var className = editedScene.animations[selectedWidgets[i].id].className;
                _newSceneAnimations[className] = editedScene.animationClasses[className];
                _newSceneAnimations[className].belongTo = widgetLinkName;
            }
        }
        return $.extend(true, {}, _newSceneAnimations);
    }

    getWidgetElementSelectionSize(widgets, convertAllToPercents) {
        var element = document.getElementById(widgets[0].id);
        var posObj = this.getWidgetBoundingRect(element);

        this.currentWrappedSelectionSize = posObj;
        this.calcWrappedSelection(widgets);
        var sceneOffset = $('#scene').offset();

        var scale = this.tab.sceneTransformMatrix[0];

        var topOffset = this.currentWrappedSelectionSize.top - sceneOffset.top / scale - this.sceneBorderSize;
        var leftOffset = this.currentWrappedSelectionSize.left - sceneOffset.left / scale - this.sceneBorderSize;

        var widgetWidth = this.currentWrappedSelectionSize.right - this.currentWrappedSelectionSize.left;
        var widgetHeight = this.currentWrappedSelectionSize.bottom - this.currentWrappedSelectionSize.top;
        var widgetLen = widgets.length;

        for (var i = 0; i < widgetLen; i++) {

            var widget = widgets[i];
            var widgetLeft = widget.geometry.left;
            var widgetTop = widget.geometry.top;

            var unitsTop = helpers.getUnitStyle(widgetTop);
            var unitsLeft = helpers.getUnitStyle(widgetLeft);

            var unitsWidth = helpers.getUnitStyle(widget.geometry.width);
            var unitsHeight = helpers.getUnitStyle(widget.geometry.height);

            var _tempTopOffset = unitsConvertor.convertPixelToUnit(widget.id, topOffset, unitsTop, 'top');
            var _tempLeftOffset = unitsConvertor.convertPixelToUnit(widget.id, leftOffset, unitsLeft, 'left');

            _tempTopOffset = parseFloat(_tempTopOffset);
            _tempLeftOffset = parseFloat(_tempLeftOffset);

            var left = parseFloat(widgetLeft);
            var top = parseFloat(widgetTop);
            var widgetElement = document.getElementById(widget.id);

            if (convertAllToPercents) {
                this.convertAllWidgetsGeometryToPercent(widget, widgetWidth, widgetHeight, topOffset, leftOffset);
            } else {
                // top
                if (unitsTop === '%') {
                    widget.geometry.top = this.convertWidgetPositionTopToPercent(widgetElement, widgetHeight, topOffset);
                } else {
                    widget.geometry.top = (top - _tempTopOffset) + unitsTop;
                }
                // left
                if (unitsLeft === '%') {
                    widget.geometry.left = this.convertWidgetPositionLeftToPercent(widgetElement, widgetWidth, leftOffset);
                } else {
                    widget.geometry.left = (left - _tempLeftOffset) + unitsLeft;
                }
                // width
                if (unitsWidth === '%') {
                    widget.geometry.width = this.convertWidgetWidthToPercent(widgetElement, widgetWidth);
                }
                // height
                if (unitsHeight === '%') {
                    widget.geometry.height = this.convertWidgetHeightToPercent(widgetElement, widgetHeight);
                }
            }
        }

        return {
            position: 'absolute',
            top: topOffset.toFixed() + 'px',
            left: leftOffset.toFixed() + 'px',
            width: widgetWidth.toFixed() + 'px',
            height: widgetHeight.toFixed() + 'px'
        };
    }

    convertWidgetPositionLeftToPercent(element, parentWidth, leftOffset) {
        var position = helpers.getAbsolutePosition(element, $(element).parent().attr('id'));

        var elementLeft = position.left - leftOffset;

        var _percentTempLeftOffset = unitsConvertor.convertPixelToPercent(elementLeft, parentWidth);

        return _percentTempLeftOffset + '%';
    }

    convertWidgetPositionTopToPercent(element, parentHeight, topOffset) {
        var position = helpers.getAbsolutePosition(element, $(element).parent().attr('id'));

        var elementTop = position.top - topOffset;

        var _percentTempTopOffset = unitsConvertor.convertPixelToPercent(elementTop, parentHeight);

        return _percentTempTopOffset + '%';
    }

    convertWidgetWidthToPercent(element, parentWidth) {
        var elementWidth = parseFloat(getComputedStyle(element, null).getPropertyValue('width'));
        var elementWidthPercent = unitsConvertor.convertPixelToPercent(elementWidth, parentWidth);

        return elementWidthPercent + '%';
    }

    convertWidgetHeightToPercent(element, parentHeight) {
        var elementHeigth = parseFloat(getComputedStyle(element, null).getPropertyValue('height'));
        var elementHeightPercent = unitsConvertor.convertPixelToPercent(elementHeigth, parentHeight);

        return elementHeightPercent + '%';
    }

    convertAllWidgetsGeometryToPercent(widget, parentWidth, parentHeight, topOffset, leftOffset): void {
        var element = document.getElementById(widget.id);
        var elementWidth = parseFloat(getComputedStyle(element, null).getPropertyValue('width'));
        var elementHeight = parseFloat(getComputedStyle(element, null).getPropertyValue('height'));
        var elementWidthPercent = unitsConvertor.convertPixelToPercent(elementWidth, parentWidth);
        var elementHeightPercent = unitsConvertor.convertPixelToPercent(elementHeight, parentHeight);

        var position = helpers.getAbsolutePosition(element, $(element).parent().attr('id'));

        var elementTop = position.top - topOffset;
        var elementLeft = position.left - leftOffset;

        var _percentTempTopOffset = unitsConvertor.convertPixelToPercent(elementTop, parentHeight);
        var _percentTempLeftOffset = unitsConvertor.convertPixelToPercent(elementLeft, parentWidth);

        widget.geometry.width = elementWidthPercent + '%';
        widget.geometry.height = elementHeightPercent + '%';
        widget.geometry.top = _percentTempTopOffset + '%';
        widget.geometry.left = _percentTempLeftOffset + '%';

        var scale = this.tab.sceneTransformMatrix[0];

        var parentOffsetTop = position.top - element.offsetTop / scale - this.sceneBorderSize;
        var parentOffsetLeft = position.left - element.offsetLeft / scale - this.sceneBorderSize;

        for (var i = 0; i < widget.children.length; i++) {
            this.convertAllWidgetsGeometryToPercent(widget.children[i], elementWidth, elementHeight, parentOffsetTop, parentOffsetLeft);
        }
    }

    getSelectionSize() {
        return this.currentWrappedSelectionSize;
    }

    init(scene, animationsCSS, tabName) {
        let that = this;
        this.editorTabName = tabName;
        this.widgetsEl.empty();
        var templateWidgets = couiEditor.Handlebars.compile(widgetHbs);
        var htmlWidgets = templateWidgets(editorProperties[couiEditor.preferences.couiEnvironment].Elements);
        this.templateProperties = couiEditor.Handlebars.compile(propertiesHbs);
        this.templateTextProperties = couiEditor.Handlebars.compile(textPropertiesHbs);
        this.templateBackgroundProperties = couiEditor.Handlebars.compile(backgroundPropertiesHbs);
        this.templateDataBindingEvents = couiEditor.Handlebars.compile(dataBindingPropertiesHbs);
        this.templateEventsProperties = couiEditor.Handlebars.compile(eventsPropertiesHbs);
        this.templateGeometryProperties = couiEditor.Handlebars.compile(geometryPropertiesHbs);
        this.templateTransformProperties = couiEditor.Handlebars.compile(transformPropertiesHbs);
        this.templateBorderStyleProperties = couiEditor.Handlebars.compile(borderStylePropertiesHbs);
        this.templateBlendModes = couiEditor.Handlebars.compile(blendModesHbs);

        // to work properly kendo panel bar for widgets list
        // 1. first append the html
        this.widgetsEl.append(htmlWidgets);

        // 2. then init the kendo tabs
        $('#create-element-tabs').kendoTabStrip({
            animation: {
                open: {
                    effects: 'none'
                }
            }
        });

        // 3. then init the panel bar
        this.widgetsEl.kendoPanelBar({
            animation: 'none'
        });

        // init Animation class before load the scene
        this.Animations = new Animations();
        this.Animations.init(this);

        let $rightPanelContent = $('#right-pane-content');
        $rightPanelContent.kendoPanelBar({
            animation: 'none',
            collapse: (event) => {
                this.recalculateAssetsLibraryHeight(event);
            },
            activate: (event) => {
                this.recalculateAssetsLibraryHeight(event, true);
            }
        });

        $rightPanelContent.off('mousewheel');
        $rightPanelContent.on('mousewheel', function () {
            that.sceneAssetPreviewStandin.css('transform', 'rotate(360deg)');
        });

        if (scene !== undefined) {
            var sceneObj;

            if (typeof scene === 'string') {
                sceneObj = JSON.parse(scene);
            } else {

                if (animationsCSS && typeof animationsCSS === 'string') {
                    scene.animations = this.Animations.importCSS(animationsCSS) || {};
                }

                sceneObj = scene;
            }

            this.loadScene(sceneObj);
        } else {
            this._sceneJsTree([]);
        }

        this.scenePropertiesElBar.empty();
        var scenePropertiesTemplate = couiEditor.Handlebars.compile(scenePropertiesHbs);
        var sceneProperties = scenePropertiesTemplate(this.scene);
        this.scenePropertiesElBar.append(sceneProperties).kendoPanelBar({
            animation: 'none'
        });
        this.contextMenuEl.empty();
        var templateContextMenu = couiEditor.Handlebars.compile(editingMenuHbs);
        var htmlEditOptions = templateContextMenu(this.scene);

        this.contextMenuEl.append(htmlEditOptions);

        this._setRightClickHandling(true);
        this._addWidgetHandler();
        this._removeFilesHandler();
        this._openFileToEditHandler();
        this._assetChosenHandler();
        this._disableMaskEvents();
        this._customAspectRationHandler();
        this.WidgetSelection.init();
        this.WidgetSelection.attachHandlers();
        this.importExternalCSS(this.scene.styles);
        this.importFontFaces(this.scene.fonts);

        // Used to properly dispay enabled edit menu elements on new scene creation
        this.clearSelectedElements();
        this.recalculateAssetsLibraryHeight(null, true);
    }

    /**
     * Recalculates the assets library height
     * @function recalculateAssetsLibraryHeight
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {event} event - event that triggered the recalculate
     * @param {boolean} expanding - whether the event is expanding (no events case)
     * @returns {void}
     */
    private recalculateAssetsLibraryHeight(event, expanding = false): void {
        let scenePropertiesHeight = this.scenePropertiesHolder.height();
        let sceneHierarchyHeight = this.assetsHierarchyHolder.height();
        let holderHeight = this.rightPanel.height();
        let newAssetsHeight;

        if (expanding) {
            // CASE EXPANDING //
            newAssetsHeight = holderHeight - (scenePropertiesHeight + sceneHierarchyHeight);
        } else {
            // CASE COLLAPSING //
            if (event !== null && event.item.classList.contains('scene-properties-holder')) {
                scenePropertiesHeight = Enums.assetsLibraryLabelSize;
            } else {
                sceneHierarchyHeight = Enums.assetsLibraryLabelSize;
            }
            newAssetsHeight = holderHeight - (scenePropertiesHeight + sceneHierarchyHeight);
        }

        if (!$('#assets-bar').is(':visible') || (!expanding && event !== null && event.item.classList.contains('scene-library-holder'))) {
            // CASE ASSET LIBRARY IS COLLAPSE AND EVENT WAS TRIGGERED FROM LIBRARY HOLDER (OTHER KENDO COLLAPSE)//
            this.assetsLibraryHolder.css('height', Enums.assetsLibraryLabelSize);
        } else {
            // REGULAR HEIGHT HANDLING //
            newAssetsHeight = (newAssetsHeight < Enums.assetLabelsEntryHeight) ? Enums.assetLabelsEntryHeight : newAssetsHeight;
            this.assetsLibraryHolder.css('height', newAssetsHeight);
            $('#assets-bar').css('height', newAssetsHeight - Enums.assetsLibraryLabelSize);
            let assetsBarHeight = newAssetsHeight - Enums.assetLibraryPreviewSize;
            $('#assets-bar-holder').css('height', assetsBarHeight);
        }

        this.scenePropertiesHolder.css('transform', 'rotate(360deg)');
        this.assetsHierarchyHolder.css('transform', 'rotate(360deg)');
        this.assetsLibraryHolder.css('transform', 'rotate(360deg)');
        this.sceneAssetPreviewStandin.css('transform', 'rotate(360deg)');
    }

    importExternalCSS(cssArray) {
        var prefix = '';

        if (couiEditor.globalEditorInfo.backend === Enums.Backends.Debug ||
            couiEditor.globalEditorInfo.backend === Enums.Backends.Website) {
            prefix = 'uiresources/';
        }

        var importCSS = function (string) {
            let filteredStr = string.replace('!css', '');
            const scenePath = couiEditor.openFiles[couiEditor.selectedEditor].tab.filePath;
            if (string.startsWith('../')) {
                filteredStr = filteredStr.replace(/\.\.\//g, '');
            }

            return System.import(prefix + filteredStr + '!text');
        };

        Promise.all(cssArray.map(importCSS)).then(
            function (value) {
                var scopedStyles = helpers.applyStylesToScene(value);
                $('<style>' + scopedStyles + '</style>').prependTo($('#scene'));
            });
    }

    importFontFaces(fontArray: Array<string>): void {
        if (fontArray.length === 0) {
            return;
        }

        const scenePath = couiEditor.openFiles[couiEditor.selectedEditor].tab.filePath;
        let fontCSS = '';

        fontArray
            .map(fontPath => helpers.externalFilePathHandler(fontPath, scenePath))
            .map(relativeFontPath => {
                fontCSS += `${helpers.buildFontCss(relativeFontPath)}`;
            });

        $(`<style id='coui_font_faces'>${fontCSS}</style>`).prependTo($('#scene'));
    }

    _setRightClickHandling(active) {
        if (active) {
            this.sceneWrapper.off('contextmenu mouseup');
            this.sceneWrapper.on('contextmenu mouseup', this._rightClickContextMenuHandler.bind(this));
        }
    }

    getFormat() {
        return this.formatTo;
    }

    setFormat(value) {
        if (value !== undefined) {
            if ((typeof value) === 'number' && Number.isInteger(value) && value >= 0) {
                this.formatTo = value;
            } else {
                console.error('Format Value is not a valid.');
            }
        }
    }

    _rightClickContextMenuHandler(e) {
        var contextWrapper = $('#context-menu');
        var contentMenuElements = contextWrapper.find('.btn-dynamic');

        if (e.which === Enums.Keys.rightMouseClick) {

            e.preventDefault();

            if (!this.WidgetSelection.getSelectedState()) {
                contentMenuElements.addClass('disabled');
            } else {
                contentMenuElements.removeClass('disabled');
            }

            var windowHeight = $(window).height();
            var windowWidth = $(window).width();
            var windowHalfHeight = windowHeight / 2;
            var windowHalfWidth = windowWidth / 2;

            //When user click on bottom-left part of window
            if (e.clientY > windowHalfHeight && e.clientX <= windowHalfWidth) {
                contextWrapper.css('left', e.clientX);
                contextWrapper.css('bottom', windowHeight - e.clientY);
                contextWrapper.css('right', 'auto');
                contextWrapper.css('top', 'auto');
            } else if (e.clientY > windowHalfHeight && e.clientX > windowHalfWidth) {
                //When user click on bottom-right part of window
                contextWrapper.css('right', windowWidth - e.clientX);
                contextWrapper.css('bottom', windowHeight - e.clientY);
                contextWrapper.css('left', 'auto');
                contextWrapper.css('top', 'auto');
            } else if (e.clientY <= windowHalfHeight && e.clientX <= windowHalfWidth) {
                //When user click on top-left part of window
                contextWrapper.css('left', e.clientX);
                contextWrapper.css('top', e.clientY);
                contextWrapper.css('right', 'auto');
                contextWrapper.css('bottom', 'auto');
            } else {
                //When user click on top-right part of window
                contextWrapper.css('right', windowWidth - e.clientX);
                contextWrapper.css('top', e.clientY);
                contextWrapper.css('left', 'auto');
                contextWrapper.css('bottom', 'auto');
            }

            contextWrapper.fadeIn(50, function () {
                $('#coui-editor').off('click');
                $('#coui-editor').on('click', function () {
                    contextWrapper.fadeOut(50);
                });
            });
        } else {
            if (contextWrapper.is(':visible')) {
                contextWrapper.fadeOut(50);
            }
        }
    }

    /**
     * Remove an asset from a clone of couiEditor.assets and return the new array
     * @param assetObj {Array} - the specific asset group of assets (font, image, widget, etc.)
     * @param asset {string} - the asset to be removed
     * @returns {any}
     * @private
     */
    _filterAssets(assetObj, asset: string) {
        // create a clone of the array by value
        let assets = assetObj.slice();

        for (var i = 0; i < assets.length; i++) {
            if (assets[i].url === asset) {
                assets.splice(i, 1);
            }
        }

        return assets;
    }

    _filterComponents(assetObj, asset: string) {
        // create a clone of the array by value
        let assets = assetObj.slice();

        for (var i = 0; i < assets.length; i++) {
            if (assets[i].name === asset) {
                assets.splice(i, 1);
            }
        }

        return assets;
    }

    /**
     * Reset the selected elements font-family property to initial
     * @param matchedElements {Array} - array of all elements with matched font-family style by ID
     * @private
     */
    _resetFontFamily(matchedElements: Array<string>) {
        matchedElements.forEach(id => {
            this.mappedWidgets[id].widget.styles.fontFamily = 'initial';
            document.getElementById(id).style.fontFamily = 'initial';
        });

        this.clearSelectedElements();
    }

    /**
     * Get all elements with a specific font
     * @param elements {Array} - all elements on the scene
     * @param font - the font to check for
     * @returns {Array}
     * @private
     */
    _getElementsWithFont(elements, font: string): Array<string> {
        const matchedElements = [];

        for (let id of Object.keys(elements)) {
            if (elements[id].widget.styles.fontFamily === font) {
                matchedElements.push(elements[id].widget.id);
            }
        }
        return matchedElements;
    }

    /**
     * Font family removal error message
     * @param fontName - The font name to be deleted
     * @param matchedElements - matched elements using the font
     * @returns {{header: string, body: string, footer: string}}
     * @private
     */
    private _fontFamilyWarning(fontName, matchedElements) {
        const plural = matchedElements.length > 1 ? 'elements' : 'element';

        return {
            header: `Removing ${fontName} will affect the following ${plural} with ID:`,
            body: `<div class="vex-font-elements">${matchedElements.join(',')}</div>`,
            footer: `<div class="vex-dialog-message">The font-family will be reset to default. Are you sure you want to delete it?</div>`
        };
    }

    /**
     * Attaches event handlers to the elements inside the аssets library
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     */
    _attachLibraryClickEvent() {
        var _this = this;

        this.assetsBarContent.find('.k-icon').remove();
        this.sceneAssetPreviewImage.css('display', 'none');
        this.sceneAssetPreviewVideo.css('display', 'none');
        this.sceneAssetPreviewWidget.css('display', 'none');
        this.sceneAssetPreviewStandin.css('display', 'block');

        $(document).off('click', '.edit-widget');
        $(document).on('click', '.edit-widget', function (event) {
            _this.exportScene();
            let type = 'component';
            let editDataUrl = this.getAttribute('data-url');
            let editDataLink = this.getAttribute('data-link');

            if (editDataLink.endsWith('.html')) {
                type = 'widget';
            }

            couiEditor.PENDING_WIDGET_LOAD = true;

            if (type === 'widget') {
                // TODO: FIX THE PATH
                if (couiEditor.globalEditorInfo.backend === Enums.Backends.Unreal && editDataUrl.indexOf('uiresources') < 0) {
                    editDataUrl = 'uiresources/' + editDataUrl;
                }
                couiEditor.openAsset({path: editDataUrl});
            } else if (type === 'component') {
                let component = couiEditor.openFiles[couiEditor.selectedEditor].components;
                let content = component.components[editDataLink];
                component.open(content, editDataLink);
            }

            event.stopImmediatePropagation();
        });

        $(document).off('click', '.convert-widget');
        $(document).on('click', '.convert-widget', function () {
            let editDataUrl = this.getAttribute('data-url');
            let editDataLink = this.getAttribute('data-link');
            let date = new Date();
            System.import(editDataUrl + '?' + date.getTime() + '!text').then(function (data) {
                let componentName = editDataLink.match(/[^\/\\]*$/);
                let currentEditor = couiEditor.openFiles[couiEditor.selectedEditor];
                componentName = componentName[0].replace('.html', '.component');
                let isOpened = currentEditor.components.isComponentOpened(componentName);
                if (isOpened) {
                    $('#coui-editor').trigger('vexFlashMessage', [Enums.warnMessages.closeTabToConvertTheWidget]);
                    return;
                }
                let newScene = couiEditor.handleFileContent(data, componentName, _this.editorTabName, true, false);
                newScene.widgets[0].widgetkit = componentName;
                let jsonScene: string = JSON.stringify(newScene);
                jsonScene = jsonScene.replaceAll('coui://uiresources/', '');
                currentEditor.components.create(componentName, jsonScene);
                couiEditor.refreshTab();
                $('#coui-editor').trigger('vexFlashMessage', [Enums.Messages.widgetConvertSuccess, true]);
            });
        });

        this.sceneAssetPreviewStandin.css('background-color', 'white');

        $(document).off('click', '#assets-bar .fa-plus');
        $(document).on('click', '#assets-bar .fa-plus', function (event) {
            var assetType = this.getAttribute('data-type');
            _this.sceneAssetSelected = true;

            if (assetType === 'events') {
                this._pickedAsset = 'events';
                assetType = 'script';
            } else if (assetType === 'script') {
                this._pickedAsset = 'scripts';
            } else {
                this._pickedAsset = assetType;
            }

            couiEditor.pickAsset('', assetType).otherwise(function (t) {
                console.log('error = ', t);
            });

            event.stopImmediatePropagation();
        });

        $(document).off('click', '#assets-bar .fa-remove');
        $(document).on('click', '#assets-bar .fa-remove', function (event) {
            const $removeButton = this;
            let url = this.getAttribute('data-url');
            const $holderGroup = this.parentElement;
            const deleteType = $holderGroup.getAttribute('data-type');
            const vListIndex = $holderGroup.getAttribute('vList-index');
            const filteredAssets = _this._filterAssets(couiEditor.assets[deleteType], url);
            const isActive = $(this.parentElement).find('.fa-dot-circle-o').length > 0;

            if (isActive) {
                couiEditor.tabEdited(true);
            }

            if (deleteType === 'widget') {
                let confirmDelete = new VexConfirm();
                const name = $removeButton.getAttribute('data-link');
                confirmDelete.create(Enums.warnMessages.deleteComponents(name)).then(function (result) {
                    if (result === 'yes') {
                        _this.removeWidgetAsset(name, 'widget');
                        _this.assetsDOM = createVDOM(_this.createAssetDOM(couiEditor.assets).template);
                    }
                });
            } else if (deleteType === 'font') {
                const fontName = url.replace(/\.[^/.]+$/, '').replace(/^.*[\\\/]/, '');
                const matchedElements = _this._getElementsWithFont(_this.mappedWidgets, fontName);

                if (!isActive && !matchedElements.length) {
                    removeAsset(vListIndex);
                } else {
                    const warning = _this._fontFamilyWarning(fontName, matchedElements);

                    vex.dialog.open({
                        contentClassName: 'modal-about modal-font-confirm',
                        message: warning.header,
                        buttons: [
                            $.extend({}, vex.dialog.buttons.YES, {text: 'Yes'}),
                            $.extend({}, vex.dialog.buttons.NO, {text: 'Cancel'})
                        ],

                        afterOpen($vexContent) {
                            $vexContent.append(warning.body, warning.footer);
                        },

                        callback(value) {
                            // on confirm
                            if (value) {
                                _this._resetFontFamily(matchedElements);
                                removeAsset(vListIndex);
                            } else {
                                return;
                            }
                        }
                    });
                }
            } else {
                if (deleteType !== 'style' &&
                    deleteType !== 'script' &&
                    deleteType !== 'font') {
                    var sceneWidgetSelectType = 'sceneAssetPreview' + helpers.capitalizeFirstLetter(deleteType);

                    if (_this[sceneWidgetSelectType].attr('src') === url) {
                        _this[sceneWidgetSelectType].fadeOut(100, function () {
                            _this[sceneWidgetSelectType].attr('src', '');
                            _this[sceneWidgetSelectType].css('display', 'none');

                            _this.sceneAssetPreviewStandin.css('display', 'block');
                            _this.sceneAssetPreviewStandin.fadeIn(100);
                        });
                    }
                } else {
                    // converting style to styles or script to scripts to refer to the scene //
                    url = url.replace('uiresources/', '');
                    _this._toScene(url, deleteType, false);
                }

                removeAsset(vListIndex);
            }

            function removeAsset(index) {
                if (filteredAssets.length === 0) {
                    $($holderGroup).find('.fa-play').css('transform', 'rotate(0deg)');
                }

                couiEditor.assets[deleteType] = filteredAssets;

                _this.virtualList.removeAsset(index);
                _this.assetsDOM = createVDOM(_this.createAssetDOM(couiEditor.assets).template);
            }

            event.stopImmediatePropagation();
        });

        $(document).off('click', '.script-or-style');
        $(document).on('click', '.script-or-style', function (event) {

            const $this = $(this).find('span[data-type="radio-span"]');
            let dataURL;
            let dataType;

            if ($this.hasClass('fa-circle-o')) {
                $this.removeClass('fa-circle-o');
                $this.addClass('fa-dot-circle-o');

                dataURL = $(this).find('span[data-link]').attr('data-url');
                dataType = $(this).find('span[data-type]').attr('data-type');

                _this._toScene(dataURL, dataType, true);
            } else {
                $this.removeClass('fa-dot-circle-o');
                $this.addClass('fa-circle-o');

                dataURL = $(this).find('span[data-link]').attr('data-url');
                dataType = $(this).find('span[data-type]').attr('data-type');

                _this._toScene(dataURL, dataType, false);
            }
            couiEditor.tabEdited(true);
            event.stopImmediatePropagation();
        });

        $(document).off('click', '.asset-file-name');
        $(document).on('click', '.asset-file-name', function (event) {

            $('.asset-file-name').removeClass('selected');

            var url = $(this).find('span[data-link]').attr('data-url');

            var type = $(this).find('span[data-type]').attr('data-type');

            _this._switchPreviewWindow(url, type);

            $(this).addClass('selected');
            event.stopImmediatePropagation();
        });

        $('#assets-bar').kendoDraggable({
            filter: '.asset-file-name',
            group: 'assetsGroup',
            distance: 1,
            hint(element) {
                const type = element.data('type');
                const name = element.find('span[data-link]').attr('data-link');
                let url = element.find('span[data-link]').attr('data-url');

                if (couiEditor.globalEditorInfo.backend === Enums.Backends.Standalone ||
                    couiEditor.globalEditorInfo.backend === Enums.Backends.Unreal) {
                    url = url.replace('uiresources/', '');
                }

                var newElement;
                if (type === 'image') {
                    newElement = $('<img id="dragged-kendo-element" src="' + url + '">');
                } else if (type === 'video') {
                    newElement = $('<video autoplay="false" id="dragged-kendo-element" src="' + url + '">');
                } else if (type === 'widget') {
                    newElement = $('<div data-url="' +
                        url + '" data-widget-name="' +
                        name + '" id="dragged-kendo-element" class="widget-asset-preview"></div>');
                }

                _this.draggedElement = $(newElement[0]);
                _this.draggedElement.css('z-index', '999999');
                return newElement;
            },
            dragstart(event) {

                var scale = _this.tab.sceneTransformMatrix[0];

                var elementLeft = parseFloat(_this.draggedElement.css('left'));
                var elementTop = parseFloat(_this.draggedElement.css('top'));

                elementLeft = (event.clientX - elementLeft) / scale;
                elementTop = (event.clientY - elementTop) / scale;

                _this.draggedElement.css({
                    'transform-origin': '0px 0px',
                    'transform': `scale(${scale},${scale}) translate(${elementLeft}px ,${elementTop}px)`,
                });
            }
        });

        _this.sceneWrapper.kendoDropTarget({
            group: 'assetsGroup',
            dragenter(e) {
                e.draggable.hint.css('border', 'solid 1px black');
            },
            dragleave(e) {
                e.draggable.hint.css('border', '');
            },
            drop(e) {
                const draggedElement = e.draggable.hint[0];
                $('.select-corners').addClass('disable-click');

                // off mousedown if previous element is init by dragging
                $('body').off('mousedown');

                _this._sceneActionState.primaryAction = 'new action';

                let type;
                switch (draggedElement.tagName) {
                    case 'IMG':
                        type = 'responsiveImage';
                        break;
                    case 'DIV':
                        couiEditor.openFiles[_this.editorTabName].components._insertToTheScene(draggedElement, e);
                        return;
                    case 'VIDEO':
                        type = 'video';
                        break;
                }

                const currentWidget = _this._prepareObjEl(type);
                const elementId = currentWidget.id;
                const offsets = _this._getSceneOffset(e);

                currentWidget.geometry.width = unitsConvertor.convertPixelToVw(draggedElement.width) + 'vw';
                currentWidget.geometry.height = unitsConvertor.convertPixelToVh(draggedElement.height) + 'vh';

                _this.createElementOnTheScene(elementId, type, currentWidget);

                _this.applyProperties(elementId, 'css property', 'top', offsets.y);
                _this.applyProperties(elementId, 'css property', 'left', offsets.x);
                _this.applyProperties(elementId, 'css property', 'width', draggedElement.width);
                _this.applyProperties(elementId, 'css property', 'height', draggedElement.height);

                _this.highlightSelectedEl($(document.getElementById(elementId)));

                const x = unitsConvertor.convertPixelToVw(offsets.x);
                const y = unitsConvertor.convertPixelToVh(offsets.y);

                currentWidget.geometry.top = y + 'vh';
                currentWidget.geometry.left = x + 'vw';

                _this._sceneActionState.elementWasInserted = true;

                type = type === 'responsiveImage' ? 'image' : 'video';

                const url = draggedElement.getAttribute('src');
                _this.assetChosen(url, elementId, type);
            }
        });
    }

    removeWidgetAsset(name, deleteType, url = undefined) {
        couiEditor.openFiles[couiEditor.selectedEditor].components.remove(name);

        const filteredAssets = this._filterComponents(couiEditor.assets[deleteType], name);
        const widgetIndex = this.virtualList.findIndex(name);

        couiEditor.assets[deleteType] = filteredAssets;

        if (widgetIndex !== null) {
            this.virtualList.removeAsset(widgetIndex);
        }
    }

    _getSceneOffset(e) {
        var sceneOffset = $(this.scenePreviewEl).offset();
        var scale = this.tab.sceneTransformMatrix[0];

        var x = ((e.originalEvent.pageX - sceneOffset.left) / scale);
        var y = ((e.originalEvent.pageY - sceneOffset.top) / scale);

        return {'x': x, 'y': y};
    }

    /**
     * Switches the assets inside the preview window
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {string} type - asset type
     * @param {string} url - asset url
     * @example `[ 'image','images/mobaSliced2_17.png']`
     */
    _switchPreviewWindow(url, type) {
        if (type !== 'style' && type !== 'script' && type !== 'font') {
            var _this = this;

            if (couiEditor.globalEditorInfo.backend === Enums.Backends.Standalone ||
                couiEditor.globalEditorInfo.backend === Enums.Backends.Unreal) {
                url = url.replace('uiresources/', '');
            }

            var selectedElement = this.sceneAssetPreviewWindow.children().filter(function () {
                return this.style.display === 'block';
            });

            var sceneWidgetSelectType = 'sceneAssetPreview' + helpers.capitalizeFirstLetter(type);

            if (_this[sceneWidgetSelectType].attr('src') === url) {
                return;
            }

            var sameType = selectedElement[0].tagName === _this[sceneWidgetSelectType][0].tagName;

            $(selectedElement).fadeOut(50, function () {
                if (!sameType) {
                    selectedElement.attr('src', '');
                    _this[sceneWidgetSelectType].attr('src', url);

                    selectedElement.css('display', 'none');
                    selectedElement.css('opacity', '1');

                    _this[sceneWidgetSelectType].css('display', 'block');
                } else {
                    _this[sceneWidgetSelectType].attr('src', url);
                }
                _this[sceneWidgetSelectType].fadeIn(50);
            });
        }
    }

    buildWidgetUrl(dataUrl) {
        var date = new Date();

        if (couiEditor.globalEditorInfo.backend === Enums.Backends.Standalone ||
            couiEditor.globalEditorInfo.backend === Enums.Backends.Unreal) {
            return dataUrl + '?' + date.getTime() + '!text';
        } else {
            return 'mocks/widget.html!text';
        }
    }

    _customAspectRationHandler() {
        var customAspectRatioWrapper = document.getElementById('aspect-ratio-custom');
        customAspectRatioWrapper.addEventListener('change',
            this._AspectRationonChangeHandler.bind(this), false);
    }

    _AspectRationonChangeHandler(event) {
        var sceneSize = this.scene.sceneSize;
        var width = $('#aspect-ratio-width-custom').val();
        var height = $('#aspect-ratio-height-custom').val();

        this._setUndoRedoCommandsFill({
            aspectRatio: {
                type: this.scene.sceneSize.type,
                width: this.scenePreviewEl.style.width,
                height: this.scenePreviewEl.style.height
            }
        });

        sceneSize.width = width + 'px';
        sceneSize.height = height + 'px';
        this.scenePreviewEl.style.width = sceneSize.width;
        this.scenePreviewEl.style.height = sceneSize.height;
        this.exportScene();

        window.requestAnimationFrame(couiEditor.focusCUIFile.bind(this, this.editorTabName));

        // The returned values should be used only for unit testing
        return {
            width: sceneSize.width,
            height: sceneSize.height
        };
    }

    _disableMaskEvents() {
        window.removeEventListener('keydown', this.disableMask, false);
        window.addEventListener('keydown', this.disableMask, false);

        window.removeEventListener('keyup', this.enableMask, false);
        window.addEventListener('keyup', this.enableMask, false);
    }

    disableMask(event) {
        if (event.keyCode === Enums.Keys.ctrl) {
            $('.select-corners').addClass('disable-click');
        }
    }

    enableMask(event) {
        if (event.keyCode === Enums.Keys.ctrl) {
            $('.select-corners').removeClass('disable-click');
        }
    }

    _addWidgetHandler() {
        var _this = this;

        $('.widget-name').off();
        $('.widget-name').on('click', function () {
            $('.select-corners').addClass('disable-click');

            // off mousedown if previous element is init by dragging
            $('body').off('mousedown');

            $('.widget-name').removeClass('selected');
            $(this).addClass('selected');

            var elementType = $(this).attr('data-type');

            // MAKES BACKEND RELATED WIDGET CREATION SILENT IN THE BACKGROUND //
            if (couiEditor.globalEditorInfo.backend === Enums.Backends.Debug ||
                couiEditor.globalEditorInfo.backend === Enums.Backends.Website) {
                if (elementType === 'image' || elementType === 'video' || elementType === 'responsiveImage') {
                    return;
                }
            }

            _this._sceneActionState.primaryAction = 'new action';

            var currentWidget = _this._prepareObjEl(elementType);
            var elementId = currentWidget.id;

            _this._sceneActionState.elementWasInserted = false;

            if (elementType === 'image' ||
                elementType === 'responsiveImage' ||
                elementType === 'video') {
                _this.interactElementsState = 'resize';
                _this._sceneClickingState.draggingCreation = false;
                _this._sceneActionState.initialCreationOfWidgetByExternalFile = true;
                _this.createElementOnTheScene(elementId, elementType,
                    currentWidget);

                if (elementType === 'responsiveImage') {
                    $('#properties').data('kendoPanelBar').expand($('#background-properties'));
                }

                if (elementType === 'video') {
                    $('#upload-video').click();
                } else {
                    $('#upload-image').click();
                }

            } else {
                _this._sceneClickingState.draggingCreation = true;
                _this.initElementOnScene(elementId, elementType,
                    currentWidget);
            }

        });
    }

    /**
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {array} copiedWidgets - array with ids
     */
    setCopiedWidgets(copiedWidgets, children?) {

        for (var i = 0; i < copiedWidgets.length; i++) {
            if (copiedWidgets[i] !== null) {

                var newWidget = this.initialWidgetCopy(copiedWidgets[i], children);

                if (newWidget.widget.children.length > 0) {
                    for (var j = 0; j < newWidget.widget.children.length; j++) {
                        this.setCopiedWidgets([newWidget.widget.children[j].id], true);
                    }
                }
            }
        }
    }

    /**
     * Get and copy all selected widgets
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     */
    copyWidgets() {
        var selectedWidgets = $.extend(true, [], this.currentParentElementsSelection);
        couiEditor.copiedWidgets.widgets = {};
        couiEditor.copiedWidgets.animations = {};
        couiEditor.copiedWidgets.animationsToPaste = {};

        this.setCopiedWidgets(selectedWidgets);
    }

    /**
     * Generate initial copy data
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param id
     * @param isChildren
     * @returns {object} extend new copy of widget
     */
    initialWidgetCopy(id, isChildren) {
        if (!isChildren) {
            couiEditor.copiedWidgets.widgets[id] = $.extend(true, {}, this.mappedWidgets[id]);
        } else {
            couiEditor.copiedWidgets.widgets[id] = {
                nestedElementId: id
            };
        }

        if (this.scene.animations[id]) {
            couiEditor.copiedWidgets.animations[id] = $.extend(true, {}, this.scene.animations[id]);

            // Empty class animation fix
            if (!couiEditor.copiedWidgets.animations[id][this.scene.animations[id].className]) {
                couiEditor.copiedWidgets.animations[id][this.scene.animations[id].className] = {};
                $.extend(true, couiEditor.copiedWidgets.animations[id][this.scene.animations[id].className], Enums.newAnimation);
            }
        }

        couiEditor.copiedWidgets.tabName = this.editorTabName;

        return $.extend(true, {}, this.mappedWidgets[id]);
    }


    /**
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param offset
     * @param id
     * @returns {{widget: *, newId: *}}
     */
    buildCopyWidgetData(currentOffset: number, id: string) {

        let oldWidget: any = couiEditor['copiedWidgets'].widgets[id].widget;
        let isComponent = (oldWidget.widgetkit) ? oldWidget.widgetkit.endsWith('.component') : false;
        let widgetType = (!isComponent) ? oldWidget.type : oldWidget.id.replace('.component', '');
        let newElementId: string = couiEditor.generateRandomId(widgetType);

        let newWidget = $.extend(true, {}, oldWidget);
        newWidget.id = newElementId;

        let $element: HTMLElement = document.getElementById(id);

        if ($element !== null) {
            let topValue = unitsConvertor.getPixelsWidgetGeometry(id, 'top', newWidget.geometry.top);
            let leftValue = unitsConvertor.getPixelsWidgetGeometry(id, 'left', newWidget.geometry.left);

            let originalTopUnit: string = topValue.originalUnit;
            let originalLeftUnit: string = leftValue.originalUnit;

            let newTopValue = topValue.pixels + currentOffset;
            let newLeftValue = leftValue.pixels + currentOffset;

            if (originalTopUnit !== '%') {
                newWidget.geometry.top = unitsConvertor.convertPixelToUnit(id, newTopValue, originalTopUnit, 'top');
            } else {
                let parentPositionValue = parseFloat(getComputedStyle($element.parentElement, null).getPropertyValue('height'));
                newWidget.geometry.top = unitsConvertor.convertPixelToPercent(newTopValue, parentPositionValue).toFixed(1) + '%';
            }

            if (originalLeftUnit !== '%') {
                newWidget.geometry.left = unitsConvertor.convertPixelToUnit(id, newLeftValue, originalLeftUnit, 'left');
            } else {
                let parentPositionValue = parseFloat(getComputedStyle($element.parentElement, null).getPropertyValue('width'));
                newWidget.geometry.left = unitsConvertor.convertPixelToPercent(newLeftValue, parentPositionValue).toFixed(1) + '%';
            }
        }

        return {
            widget: newWidget,
            newId: newElementId
        };
    }

    /**
     *
     * @returns {boolean}
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     */
    doesWidgetsClassNamesExistInAnimationClasses() {
        var animationClasses = this.scene.animationClasses;
        var animationWidgets = couiEditor.copiedWidgets.animations;

        for (var widgetId in animationWidgets) {
            if (animationClasses[animationWidgets[widgetId].className] &&
                this.editorTabName !== couiEditor.copiedWidgets.tabName) {
                return true;
            }
        }

        return false;
    }

    /**
     *
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     */
    compareWidgetAnimationClassesWithAnimationClasses() {
        var animationClasses = this.scene.animationClasses;
        var animationWidgets = couiEditor.copiedWidgets.animations;

        for (var widgetId in animationWidgets) {
            if (!animationClasses[animationWidgets[widgetId].className] &&
                this.editorTabName !== couiEditor.copiedWidgets.tabName) {
                if (animationWidgets[widgetId].className) {
                    animationClasses[animationWidgets[widgetId].className] = animationWidgets[widgetId][animationWidgets[widgetId].className];
                }
            }
        }
    }

    /**
     * generate new ids for top level and nested widgets
     * get original top level widget from `couiEditor.copiedWidgets.widgets`
     * get animations for all copied widgets with their ids from `couiEditor.copiedWidgets.animations`
     * build animation object to paste
     * `couiEditor.copiedWidgets.animationsToPaste` contains new generated ids
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     */
    cloneWidget() {
        if (this.doesWidgetsClassNamesExistInAnimationClasses()) {
            $('#coui-editor').trigger('vexFlashMessage', []);
            return;
        }

        this.compareWidgetAnimationClassesWithAnimationClasses();

        var countParents = 0;
        var currentOffset = Math.random() * 15 + 10;
        var parentId;
        var parentJstreeId;
        var widgetIds = [];

        for (var id in couiEditor.copiedWidgets.widgets) {
            if (!couiEditor.copiedWidgets.widgets[id].nestedElementId) {
                countParents++;
            }
        }

        this._undoCreationStepsLength = countParents;
        this._sceneActionState.pasteWidget = true;

        const inWidgetEditing = couiEditor.openFiles[couiEditor.selectedEditor].tab.tabWidgetState.editWidget;

        let checkConditional = function (currentWidget: any, inWidgetEditing: boolean): boolean {
            let conditional: boolean;
            if (inWidgetEditing) {
                conditional = (!currentWidget.nestedElementId && (currentWidget.widget.type !== 'widget' && inWidgetEditing));
            } else {
                conditional = !currentWidget.nestedElementId;
            }
            return conditional;
        };

        for (var widgetId in couiEditor.copiedWidgets.widgets) {
            let currentWidget = couiEditor.copiedWidgets.widgets[widgetId];

            if (checkConditional(currentWidget, inWidgetEditing)) {
                var newWidget = this.buildCopyWidgetData(currentOffset, widgetId);
                if (!this.isTopLevel(widgetId)) {
                    if (document.getElementById(widgetId)) {
                        parentId = document.getElementById(widgetId).parentElement.id;
                        parentJstreeId = $('#sceneTree').jstree().get_node($('li[data-id="' + parentId + '"]')).id;
                    }
                }

                let jstreeNodeIds = this.getJstreeAfterNodeIds(parentId);

                widgetIds.push(newWidget.widget.id);

                this.createElementsOnTheScene({
                    currentWidget: newWidget.widget,
                    parentWidgetId: parentId,
                    parentJstreeNode: parentJstreeId,
                    jstreePosition: 'last',
                    jstreeAfterNodeId: jstreeNodeIds.lastChildId,
                    jstreeNodeAppendId: jstreeNodeIds.parentId
                });

                if (this.scene.animations[widgetId]) {
                    couiEditor.copiedWidgets.animationsToPaste[newWidget.newId] = $.extend(true, {}, couiEditor.copiedWidgets.animations[widgetId]);
                    couiEditor.copiedWidgets.animationsToPaste[newWidget.newId].id = newWidget.newId;
                }

                parentId = undefined;
                parentJstreeId = undefined;
            }
        }

        this.selectMultiJstreeItems(widgetIds);
        $.extend(this.scene.animations, couiEditor.copiedWidgets.animationsToPaste);

        // map animation classes to copied widgets
        for (var animWidgetId in couiEditor.copiedWidgets.animationsToPaste) {
            for (var animationClass in this.scene.animationClasses) {
                if (couiEditor.copiedWidgets.animationsToPaste[animWidgetId][animationClass]) {
                    this.scene.animations[animWidgetId][animationClass] = this.scene.animationClasses[animationClass];
                }
            }
        }

        this.Animations.loadKeyframes(this.scene.animationClasses, true);

        // reset
        this._sceneActionState.pasteWidget = false;
        this._skipUndoRedoSteps = 0;
        couiEditor.copiedWidgets.animationsToPaste = {};
    }

    getJstreeAfterNodeIds(parentId) {
        let jstreeNodeIds = {
            lastChildId: '',
            parentId: parentId
        };

        if (parentId) {
            let widgetChildrenLength = this.mappedWidgets[parentId].widget.children.length;
            jstreeNodeIds.lastChildId = this.mappedWidgets[parentId].widget.children[widgetChildrenLength - 1].id;
            jstreeNodeIds.parentId = jstreeNodeIds.lastChildId ? '' : jstreeNodeIds.parentId;
        }

        return jstreeNodeIds;
    }

    /**
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param oldId
     * @param newId
     */
    cloneAnimations(id, oldId) {
        var oldAnimData,
            newAnimationObj;

        if (oldId) {
            oldAnimData = couiEditor.copiedWidgets.animations[oldId];
            newAnimationObj = $.extend(true, {}, oldAnimData);
            couiEditor.copiedWidgets.animationsToPaste[id] = newAnimationObj;
        } else {
            oldAnimData = this.scene.animations[id];
            newAnimationObj = $.extend(true, {}, oldAnimData);
            couiEditor.copiedWidgets.animations[id] = newAnimationObj;
        }
    }

    /**
     * Initialize element before start creation of new widget by dragging on the scene
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {string} elementId
     * @param {string} elementType
     * @param {object} currentWidget
     */
    initElementOnScene(elementId,
                       elementType, currentWidget) {
        var _this = this;
        var body = document.body;
        var isOnStage = false;

        this.WidgetSelection.detachHandlers();
        this.currentElementsSelection = [elementId];
        this.currentParentElementsSelection = [elementId];
        this.interactElementsState = 'create';

        body.addEventListener('mouseup', applyVisualWidget, false);
        body.addEventListener('mousedown', initWidget, false);

        function initWidget(event) {
            const trigger = event.srcElement.id;

            // Filtering for widget creation only in these cases:
            // 1. Clicked on the scene
            // 2. Clicked out of the scene (scene-wrapper)
            // 3. Clicked on one of the widgets

            if (trigger === 'scene' ||
                trigger === 'scene-wrapper' ||
                _this.mappedWidgets[trigger] !== undefined) {

                _this.scene.widgets.push(currentWidget);

                $('.select-corners').removeClass('disable-click');
                var targetId = event.target.id;

                // Check starting point by dragging of the element.
                // If is on the scene, create a widget
                var isParentCenterPane = targetId !== '' &&
                    $('#center-pane').find('#' + targetId).length === 1;

                if (isParentCenterPane ||
                    targetId === 'center-pane') {
                    var sceneOffset = $(_this.scenePreviewEl).offset();
                    var scale = _this.tab.sceneTransformMatrix[0];

                    var x = (event.pageX - sceneOffset.left) / scale;
                    var y = (event.pageY - sceneOffset.top) / scale;

                    _this.createElementSimple(currentWidget);

                    // Map objects
                    _this.mappedWidgets[elementId] = {
                        widget: currentWidget
                    };

                    _this.createElementEvent = event;

                    let $widgetEl: any = $('#' + elementId);

                    _this.applyProperties(elementId, 'css property',
                        'top', y);
                    _this.applyProperties(elementId, 'css property',
                        'left', x);

                    _this.highlightSelectedEl($widgetEl);

                    x = unitsConvertor.convertPixelToVw(x);
                    y = unitsConvertor.convertPixelToVh(y);

                    currentWidget.geometry.top = y.toFixed(_this.formatTo + 1) + 'vh';
                    currentWidget.geometry.left = x.toFixed(_this.formatTo + 1) + 'vw';

                    isOnStage = true;
                }
            }
            _this._sceneClickingState.draggingCreation = false;
            _this.WidgetSelection.attachHandlers();
            body.removeEventListener('mousedown', initWidget, false);
        }

        function applyVisualWidget() {

            if (isOnStage) {
                _this.interactElementsState = 'resize';
                _this.jsTreeAddNode(elementType, elementId, '#', 'last', true);

                var isWidget = _this.tab.tabWidgetState.editWidget;
                if (isWidget) {
                    _this.placeWidgetOnScene(elementId);
                }

                // timeline creation
                _this.Animations.createAnimationWidget(currentWidget);

                _this.saveUndoRedoAfterCreateElement(elementId, currentWidget);
            }
            _this._sceneClickingState.draggingCreation = false;

            body.removeEventListener('mouseup', applyVisualWidget, false);
        }
    }

    placeWidgetOnScene(elementId) {
        this.elementReposition = true;
        this.appendElementInWidgets(elementId);
    }

    appendElementInWidgets(elementId) {
        var jstreeCurrNode = $('#sceneTree').jstree().get_node($('li[data-id="' + elementId + '"]'), false);
        var widgetTopLevelId = this.scene.widgets[0].id;

        var jstreeWidgetTop = $('#sceneTree').jstree().get_node($('li[data-id="' + widgetTopLevelId + '"]'), false);

        $('#sceneTree').jstree().move_node(jstreeCurrNode, jstreeWidgetTop, 'last');
    }

    getRedoUndoPrimaryState() {
        var actionState;
        var currentTab = couiEditor.openFiles[this.editorTabName];

        if (this._sceneActionState.primaryAction === 'new action') {
            return this._sceneActionState.primaryAction;
        }

        // If comes from undo command, save to redo
        if (this._sceneActionState.primaryAction === 'undo') {
            actionState = 'redo';
            // set global redo commands to execute
            if (redoLenCommands === 0) {
                redoLenCommands = currentTab.undo[0] ? currentTab.undo[0].length : 1;
            }
        } else {
            actionState = 'undo';
            // set global undo commands to execute
            if (undoLenCommands === 0) {
                undoLenCommands = currentTab.redo[0] ? currentTab.redo[0].length : 1;
            }
        }

        return actionState;
    }

    getSceneScale() {
        var curTransform = new WebKitCSSMatrix(window.getComputedStyle(this.scenePreviewEl).webkitTransform);
        return curTransform.a;
    }

    undoRedoMoveWidget(jstreeNodeId, currentParentJstreeId,
                       oldParentJstreeId, oldPos, pos) {
        var parentId;
        if (oldParentJstreeId === 'top') {
            parentId = '#';
        } else {
            // get parent jstreeNode id
            parentId = $('#sceneTree').jstree().get_node($('li[data-id="' + oldParentJstreeId + '"]'), false).id;
        }

        var jstreeNode = $('#sceneTree').jstree().get_node($('li[data-id="' + jstreeNodeId + '"]'), false);

        // IMPORTANT fix jstree position
        if (currentParentJstreeId === oldParentJstreeId && oldPos >= pos) {
            oldPos++;
        }

        $('#sceneTree').jstree().move_node(jstreeNode, parentId, oldPos);
    }

    createUndoRedoNewWidget(newWidget, isComponent?) {
        var newWidgetId = newWidget.id;
        this._sceneActionState.primaryAction = 'new action';
        this._sceneActionState.initialComponent = true;
        // records two commands

        // first delete all elements
        var ids = this.currentParentElementsSelection.filter(Boolean);
        this._undoCreationStepsLength = ids.length;

        this.removeWidgets(ids);

        // second create the new widget on the scene
        if (isComponent) {
            this._undoCreationStepsLength = 2;
        }

        this.createElementOnTheScene(newWidgetId, 'widget', newWidget, true);
        this._sceneActionState.initialComponent = false;
    }

    /**
     * Create multiple elements from children of widget object
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {string} elementId
     * @param {string} elementType
     * @param {object} currentWidget
     * @param {bool} isNewWidget
     */
    createElementOnTheScene(elementId,
                            elementType, currentWidget, isNewWidget?) {

        this.scene.widgets.push(currentWidget);

        var x = unitsConvertor.convertPixelToVw(parseFloat(currentWidget.geometry.left));
        var y = unitsConvertor.convertPixelToVh(parseFloat(currentWidget.geometry.top));

        var width = unitsConvertor.convertPixelToVw(parseFloat(currentWidget.geometry.width));
        var height = unitsConvertor.convertPixelToVh(parseFloat(currentWidget.geometry.height));

        currentWidget.geometry.top = y.toFixed(this.formatTo + 1) + 'vh';
        currentWidget.geometry.left = x.toFixed(this.formatTo + 1) + 'vw';
        currentWidget.geometry.width = width.toFixed(this.formatTo + 1) + 'vw';
        currentWidget.geometry.height = height.toFixed(this.formatTo + 1) + 'vh';

        // Map objects
        this.mappedWidgets[elementId] = {
            widget: currentWidget
        };

        this.createElement(currentWidget);

        this.recalculateUnits($('#' + elementId)[0]);

        this.jsTreeAddNode(elementType, elementId, '#', 'last', true);

        // undo/ redo
        this.saveUndoRedoAfterCreateElement(elementId, currentWidget, isNewWidget);
    }

    saveUndoRedoAfterCreateElement(elementId, currentWidget, isNewWidget?) {

        // undo/ redo
        if (currentWidget.type === 'widget' && isNewWidget) {
            this._sceneActionState.createNewWidget = true;
        } else {
            this._sceneActionState.createElement = true;
        }

        if (!this._sceneActionState.pasteWidget && !this._sceneActionState.initialComponent) {
            this._undoCreationStepsLength = 1;
        }

        var actionState = this.getRedoUndoPrimaryState();

        // skip 4 property saves if element is init on the
        // scene by dragging
        if (actionState === 'new action' && this._dragFlag) {
            this._skipUndoRedoSteps = 4;
        }

        this.createUndoRedoCommand(actionState, elementId, null,
            null,
            null, currentWidget);
        this.exportScene();
    }

    /**
     * Create multiple elements from children of widget object
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {string} elementId - widget id
     * @param {string} elementType - widget id
     * @param {object} currentWidget - widget id
     * @param {string} parentWidgetId - widget id
     * @param {string} parentJstreeNode - widget id
     * @param {int} jstreePosition - widget id
     * @param {string} jstreeAfterNodeId - widget id
     * @param {string} jstreeNodeAppendId - widget id
     * @param {bool} isNewWidget - image url path
     */
    createElementsOnTheScene(options) {
        var parentJstreeId = '#';

        // Map objects
        this.mappedWidgets[options.currentWidget.id] = {
            widget: options.currentWidget
        };

        if (options.parentWidgetId !== undefined) {
            // push widget to its parent widget
            this.mappedWidgets[options.parentWidgetId].widget.children.push(options.currentWidget);
            // get parent current parent jstree id if the hierarcy is rebuild
            parentJstreeId = $('#sceneTree').jstree().get_node($('li[data-id="' + options.parentWidgetId + '"]')).id;
        } else {
            // if widget is on top level then push it on scene.widgets
            this.scene.widgets.push(options.currentWidget);
        }

        this.createElement(options.currentWidget);
        this.recalculateUnits($('#' + options.currentWidget.id)[0]);
        const type = helpers.isComponent(options.currentWidget) ? 'component' : options.currentWidget.type;

        this.jsTreeAddNode(type, options.currentWidget.id, parentJstreeId, options.jstreePosition, true);
        var isWidget = couiEditor.openFiles[this.editorTabName].tab.tabWidgetState.editWidget;
        if (isWidget) {
            this.placeWidgetOnScene(options.currentWidget.id);
        }

        // if widget has children add all jstree nodes
        if (options.currentWidget.children.length > 0) {
            this.rebuildJstreeChildren(options.currentWidget.children, options.currentWidget.id);
        }

        // save undo or redo command
        this.saveUndoRedoAfterCreateElement(options.currentWidget.id, options.currentWidget, options.isNewWidget);

        // reorder element in the DOM
        if (options.jstreeAfterNodeId) {
            this.reorderElementOnTheScene('after node', options.currentWidget.id, options.jstreeAfterNodeId);
        }

        if (options.jstreeNodeAppendId) {
            this.reorderElementOnTheScene('prepend node', options.currentWidget.id, options.jstreeNodeAppendId);
        }
        this.exportScene();
    }

    /**
     *
     * @param childrenWidgets
     * @param parentId
     */
    rebuildJstreeChildren(childrenWidgets, parentId) {
        for (var i = 0; i < childrenWidgets.length; i++) {
            let widget = childrenWidgets[i];

            // Map objects
            this.mappedWidgets[widget.id] = {
                widget: widget
            };

            var jstreeNode = $('#sceneTree').jstree().get_node($('li[data-id="' + parentId + '"]'), false);
            this.jsTreeAddNode(widget.type, widget.id, jstreeNode, 'last', false);
            this.recalculateUnits($('#' + widget.id)[0]);

            if (childrenWidgets[i].children.length > 0) {
                this.rebuildJstreeChildren(widget.children, widget.id);
            }

            // timeline widget creation
            var $element = $('#' + widget.id);
            if ($element.length > 0 &&
                $element[0].hasAttribute('data-element-selectable')) {
                this.Animations.createAnimationWidget(widget, {initial: true});
            }
        }
    }

    reorderElementOnTheScene(type, elementId, targetNodeToAppendId) {

        if (type === 'after node') {
            $('#' + elementId).insertAfter($('#' + targetNodeToAppendId));
        }

        if (type === 'prepend node') {
            $('#' + targetNodeToAppendId).prepend($('#' + elementId));
        }
        let $widgetEl: any = $('#' + elementId);
        this.highlightSelectedEl($widgetEl);
    }

    addFile(type) {
        if (type === 'events') {
            this._pickedAsset = 'events';
            type = 'script';
        } else if (type === 'script') {
            this._pickedAsset = 'scripts';
        } else {
            this._pickedAsset = type;
        }

        couiEditor.pickAsset('', type).otherwise(function (t) {
            console.log('error = ', t);
        });
    }

    _openFileToEditHandler() {
        $('.file-link').off();
        $('.file-link').on('dblclick', function () {

            var path = $(this).val();
            console.log(path);
            engine.call('OpenAsset', 'uiresources/' + path)
                .then(function (r) {
                    console.log('success' + r);
                }, function (e) {
                    console.log('error' + e);
                });
        });
    }

    _removeFilesHandler() {
        var _this = this;
        $('.remove-files').off();
        $('.remove-files').on('click', function () {
            var index = $(this).attr('data-index');
            var type = $(this).parents('.files-cont').attr('data-type');
            var originLink = $(this).attr('data-link');
            _this._sceneActionState.primaryAction = 'new action';
            _this.deleteFile(index, type, originLink);
        });
    }

    deleteFile(index, type,
               originLink) {

        var link = '';
        var fullLink = '';
        var location = window.location.href;
        var substrLocation = location.substr(0, location.length - 10);
        var sceneTypes = {
            script: 'scripts',
            scripts: 'scripts',
            style: 'styles',
            styles: 'styles',
            events: 'events'
        };

        this._sceneActionState.deleteFile = true;

        // Remove link tag from head
        if (type === 'styles') {
            link = originLink.substring(0, originLink.length - 4);
            fullLink = substrLocation + 'runtime_editor/' + link;
            $('link[href="' + fullLink + '"]').remove();
        }

        this.scene[sceneTypes[type]].splice(index, 1);

        var params = {
            type: type,
            index: index,
            url: originLink
        };

        var actionState = this.getRedoUndoPrimaryState();
        this.createUndoRedoCommand(actionState, null, null, null, null, params);

        this.exportScene('file');
    }

    runtimeLoad(data, options) {
        var _this = this;
        var wrapperDiv = document.createElement('div');
        wrapperDiv.id = 'wrapper-runtime';

        var callbackObj = {
            runtimeEditor: _this,
            options: options
        };

        return runtime.loadSceneObj(JSON.parse(data), wrapperDiv,
            callbackObj);
    }

    /**
     * Save all data to file
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {string} type - optional
     */
    saveScene(type?) {
        var data = JSON.stringify(this.scene);

        // save in global editor instance
        couiEditor.openFiles[this.editorTabName].file = data;

        couiEditor.save(this._openedFile, data);

        this.exportUndoRedo();

        if (type === 'file') {
            couiEditor.focusCUIFile(this.editorTabName);
        }
    }

    /**
     *
     */
    publishScene() {
        couiEditor.setPinheadPosition(0);
        couiEditor.onSelectedFileType.publishPage = true;
        engine.call('ShowFileDialog', {
            __Type: 'FileDialogConfig',
            extensions: ['.html'],
            dialogName: 'Publish scene',
            initDirectory: '',
            fileMustExist: false,
            isOpenFile: false,
            allowMultiselect: false
        });
        this.Timeline.revertPinheadPosition();
    }

    preview(type) {
        var data = JSON.stringify(this.scene);
        // save in global editor instance
        couiEditor.openFiles[this.editorTabName].file = data;
        couiEditor.preview(this._openedFile, data, type);
    }

    /**
     * Export `this.scene` object to json sting and store it in `couiEditor.openFiles[this.editorTabName].file`
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {string|null} type - optional
     */
    exportScene(type?) {
        if (this._sceneActionState.initialLoad) {
            return;
        }

        var data = JSON.stringify(this.scene);

        // save in global editor instance
        couiEditor.openFiles[this.editorTabName].file = data;

        this.exportUndoRedo();

        if (type === 'file') {
            couiEditor.focusCUIFile(this.editorTabName);
        }
    }

    exportUndoRedo() {
        var undoRedoExport = JSON.parse(localStorage.getItem(this.editorTabName)) || {};
        undoRedoExport[this.editorTabName] = {
            undo: couiEditor.openFiles[this.editorTabName].undo,
            redo: couiEditor.openFiles[this.editorTabName].redo
        };

        localStorage.setItem(this.editorTabName, JSON.stringify(undoRedoExport));
    }

    _prepareObjEl(type: string) {
        var elementObj: any = buildWidgetHandler.buildWidget(type);

        elementObj.id = couiEditor.generateRandomId(type);

        couiEditor.widgetCount++;

        return elementObj;
    }

    setAspectRatio(type) {
        var _this = this;
        var sceneSize = this.scene.sceneSize;

        // TODO remove sceneSize.id when is fixed
        if (this.scene.sceneSize.type !== undefined) {
            this.scene.sceneSize.type = type;
        } else {
            this.scene.sceneSize.id = type;
        }

        switch (type) {
            case 'aspectRatio5_4':
                _this.scene.sceneSize.width = '1000px';
                _this.scene.sceneSize.height = '800px';
                break;
            case 'aspectRatio4_3':
                _this.scene.sceneSize.width = '800px';
                _this.scene.sceneSize.height = '600px';
                break;
            case 'aspectRatio16_10':
                _this.scene.sceneSize.width = '1440px';
                _this.scene.sceneSize.height = '900px';
                break;
            case 'aspectRatio16_9':
                _this.scene.sceneSize.width = '1600px';
                _this.scene.sceneSize.height = '900px';
                break;
            case 'aspectRatio16_9_full_hd':
                _this.scene.sceneSize.width = '1920px';
                _this.scene.sceneSize.height = '1080px';
                break;
            case 'aspectRatio_custom':
                _this.scene.sceneSize.width = sceneSize.width;
                _this.scene.sceneSize.height = sceneSize.height;
                break;
        }

        _this.scenePreviewEl.style.width = sceneSize.width;
        _this.scenePreviewEl.style.height = sceneSize.height;

        _this.exportScene();
    }

    highlightSelectedEl(element, elementsIds?) {
        var _this = this;
        var currState = this.interactElementsState + '-corners';
        var editMenuButtons = $('#edit-menu').find('.btn-dynamic');
        var isButtonDisabled = editMenuButtons.hasClass('disabled');
        var isLocked;

        if (element) {
            const unitX = helpers.getUnitStyle(element[0].style.left);
            const unitY = helpers.getUnitStyle(element[0].style.top);

            if (unitX === '%') {
                const elLeft = element[0].offsetLeft;
                element[0].style.left = `${elLeft}px`;
            }

            if (unitY === '%') {
                const elTop = element[0].offsetTop;
                element[0].style.top = `${elTop}px`;
            }
        }

        _this.WidgetSelection.setSelectedState(true);
        $('.select-corners').remove();

        if (isButtonDisabled) {
            editMenuButtons.removeClass('disabled');
        }

        if (!_this.iframe) {
            if (elementsIds) {
                for (var i = 0; i < elementsIds.length; i++) {
                    var elem = $('#' + elementsIds[i]);
                    isLocked = (elem.attr('data-lock') === 'true');
                    _this.appendManipulationMasks(currState, isLocked, elem);
                }
            } else {
                isLocked = (element.attr('data-lock') === 'true');
                _this.appendManipulationMasks(currState, isLocked, element);
            }
        }
    }

    /**
     * Scales the manipulation masks (points, rotator widgets) of widgets in the scene
     * @function scaleManipulationMasks
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {number} sceneScale - the scale to adjust the masks to
     */
    scaleManipulationMasks(sceneScale) {
        if (sceneScale >= 1) {
            $('.select-corners').find('point').css({'transform': 'scale(' + sceneScale + ')'});

            $('.transform-origin-point').css({
                width: 10 * sceneScale + 'px',
                height: 10 * sceneScale + 'px',
                borderWidth: 2 * sceneScale + 'px'
            });

            $('mask').css('border-width', sceneScale + 'px');

            if (this.getSelectedWidgetId() && !this._sceneClickingState.draggingCreation) {
                const $el = $(`#${this.getSelectedWidgetId()}`);
                this.computeSelectCorners($el);
            }
        } else {
            $('.select-corners').find('point').css({'transform': 'scale(1)'});
        }
    }

    /**
     * Scales the manipulation masks (points, rotator widgets) of widgets in the scene
     * @function scaleManipulationMasks
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {number} sceneScale - the scale to adjust the masks to
     */
    changeSceneScale(sceneScale) {
        const $sceneScaleSlider = $('#sceneScale');
        const modulator = ((sceneScale - 0.3) / 1.4) * 140 + 30;

        $('#slider-zoom-percent').val(Math.round(modulator) + '%');
        $sceneScaleSlider.val(Math.round(modulator));
        this.scaleManipulationMasks(1 / sceneScale);
    }

    appendManipulationMasks(currState, isLocked, element) {

        if (!element[0]) {
            return;
        }

        var _this = this;
        var cornerPoints = '';

        if (currState === 'resize-corners') {
            _this._setRightClickHandling(true);
        } else {
            _this._setRightClickHandling(false);
        }

        // ESCAPE WHEN OFFSETS ARE NOT AVAILABLE //
        // CASE: OPTION in OPTION or SELECT //
        if (element[0].tagName === 'OPTION' &&
            (element[0].parentElement.tagName === 'SELECT' ||
                element[0].parentElement.tagName === 'OPTION')) {
            this.highlightSelectedEl($(element[0].parentElement));
            return;
        }

        if (this.currentElementsSelection.length === 1) {
            if (this.interactElementsState === 'rotate') {
                cornerPoints =
                    '<point class="rotateLeftTop top-left-rotate"></point>' +
                    '<point class="rotateRightTop top-right-rotate"></point>' +
                    '<point class="rotateLeftBottom bottom-left-rotate"></point>' +
                    '<point class="rotateRightBottom bottom-right-rotate"></point>';
            } else if ((this.interactElementsState === 'resize' || this.interactElementsState === 'create')) {
                cornerPoints =
                    '<point class="topLeftCorner top-left-corner"></point>' +
                    '<point class="leftCenter left-center"></point>' +
                    '<point class="bottomCenter bottom-center"></point>' +
                    '<point class="rightCenter right-center"></point>' +
                    '<point class="topCenter top-center"></point>' +
                    '<point class="topRightCorner top-right-corner"></point>' +
                    '<point class="bottomRightCorner bottom-right-corner"></point>' +
                    '<point class="bottomLeftCorner bottom-left-corner"></point>';
            }
        }

        var $selectCorners = $('<mask class="' + currState +
            ' select-corners panning" data-widget-id="' + element[0].id + '">' +
            '</mask>');

        $selectCorners.append(cornerPoints);

        $(this.scenePreviewEl).append($selectCorners);

        this.computeSelectCorners(element, $selectCorners);

        var scale = this.tab.sceneTransformMatrix[0];

        this.scaleManipulationMasks(1 / scale);

        $selectCorners[0].addEventListener('click', clickSelect, false);

        function clickSelect(e) {
            // TODO test and check if something is crashing
            // e.stopPropagation();

            if (!_this._dragFlag && !_this.isInPanningMode) {
                _this.blurOutTextInputs();
                _this._elementSelectionHelper(element[0].id);

                if (_this.currentElementsSelection.length === 1) {
                    if (_this.propertiesElBar.is(':empty')) {
                        _this.selectJstreeItem(element[0].id);
                    }
                }

                if (_this.interactElementsState === 'resize') {
                    _this.interactElementsState = 'rotate';
                } else if (_this.interactElementsState === 'rotate') {
                    _this.interactElementsState = 'resize';
                }
                _this.highlightSelectedEl(element);
            }

            _this._dragFlag = false;
        }

        // attach interaction on the mask if the element isn't locked
        // or scene isn't in panning mode
        if (!isLocked && !_this.isInPanningMode) {
            Drag.dragObject($selectCorners[0], element[0], null,
                null, null,
                function () {
                    _this.onDragElStart();
                },
                function () {
                    _this.onDragElMove();
                },
                function () {
                    _this.onDragElEnd();
                },
                false);

            // attach rotate and resize handler only if has one selected
            // element
            if (this.currentElementsSelection.length === 1) {
                this.interactHandler(element[0].id);
            }
        }
    }

    private getTransformOriginPositionPoint(id: string): { transformOriginX, transformOriginY } {
        let transformOriginX: any = Enums.TransformDefaultValue['transform-origin-x'];
        let transformOriginY: any = Enums.TransformDefaultValue['transform-origin-y'];
        const $element = $(`#${id}`);
        const transformOriginFromWidget = this.mappedWidgets[id].widget['transform-origin'];
        if (transformOriginFromWidget) {
            transformOriginX = transformOriginFromWidget['transform-origin-x'] || transformOriginX;
            transformOriginY = transformOriginFromWidget['transform-origin-y'] || transformOriginY;
        }
        const border = parseFloat($element.css('border-width'));

        transformOriginX = unitsConvertor.convertUnitsToPixel(id, transformOriginX, 'transform-origin-x');
        transformOriginY = unitsConvertor.convertUnitsToPixel(id, transformOriginY, 'transform-origin-y');

        const halfPixelsOfPoint = 5;
        const sceneOffset = $(this.scenePreviewEl).offset();

        transformOriginX = parseFloat(transformOriginX) - (border) - halfPixelsOfPoint;
        transformOriginY = parseFloat(transformOriginY) - (border) - halfPixelsOfPoint;

        const widget = this.mappedWidgets[id].widget;
        const $transformOriginPoint =
            $(`<point class="transform-origin-point-temp" style="left: ${transformOriginX}px; top: ${transformOriginY}px"></point>`);

        let $tempElement = $('<div></div>');
        if (widget.type === 'image' ||
            widget.type === 'video' ||
            widget.type === 'inputText' ||
            widget.type === 'select' ||
            widget.type === 'li' ||
            widget.type === 'ul' ||
            widget.type === 'ol' ||
            widget.type === 'range' ||
            widget.type === 'number' ||
            widget.type === 'radio' ||
            widget.type === 'checkbox') {
            const absoluteMatrix = new WebKitCSSMatrix(window.getComputedStyle($element[0]).webkitTransform);
            const origin = $element.css('transform-origin');
            const elMatrix = `matrix3D(${absoluteMatrix.m11}, ${absoluteMatrix.m12}, ${absoluteMatrix.m13}, ${absoluteMatrix.m14},
                            ${absoluteMatrix.m21}, ${absoluteMatrix.m22}, ${absoluteMatrix.m23}, ${absoluteMatrix.m24},
                            ${absoluteMatrix.m31}, ${absoluteMatrix.m32}, ${absoluteMatrix.m33}, ${absoluteMatrix.m34},
                            ${absoluteMatrix.m41}, ${absoluteMatrix.m42}, ${absoluteMatrix.m43}, ${absoluteMatrix.m44})`;
            const elementWidth = $element.css('width');
            const elementHeight = $element.css('height');
            const elementTop = $element.css('top');
            const elementLeft = $element.css('left');
            const elementBorder = $element.css('border');

            $tempElement = $(`<div style="opacity: 0.5;background: red; position: absolute; border: ${elementBorder}; top: ${elementTop}; left: ${elementLeft};
                                    width: ${elementWidth}; height: ${elementHeight}; transform: ${elMatrix}; transform-origin: ${origin}"></div>`);

            $tempElement.append($transformOriginPoint);
            $($element[0].parentElement).append($tempElement);
        } else {
            $($element).append($transformOriginPoint);
        }

        const pointBoundingClientRect = $transformOriginPoint[0].getBoundingClientRect();

        const centerPoint = {
            x: (pointBoundingClientRect.right - pointBoundingClientRect.left) / 2,
            y: (pointBoundingClientRect.bottom - pointBoundingClientRect.top) / 2
        };

        const finalCoords = {
            transformOriginX: ((pointBoundingClientRect.left + centerPoint.x) - sceneOffset.left - halfPixelsOfPoint) / this.getSceneScale(),
            transformOriginY: ((pointBoundingClientRect.top + centerPoint.y) - sceneOffset.top - halfPixelsOfPoint) / this.getSceneScale()
        };

        $tempElement.remove();
        $transformOriginPoint.remove();
        return finalCoords;
    }

    _elementSelectionHelper(widgetId) {
        this.currentElementsSelection = [];
        this.currentElementsSelection[0] = widgetId;
        this.currentParentElementsSelection = [];
        this.currentParentElementsSelection[0] = widgetId;
    }

    computeSelectCorners(el, mask?) {
        var computedEl = getComputedStyle(el[0], null);
        var $selectCorners;
        if (mask) {
            $selectCorners = $(mask);
        } else {
            $selectCorners = $('.select-corners');
        }

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

        var absolutePosFromScene = helpers.getAbsolutePosition(el[0], 'scene');
        var afterRotationPos = {
            x: 0,
            y: 0
        };

        if (el[0].parentElement.id !== 'scene') {
            afterRotationPos = helpers.calcElementTransformedMatrixCoords(el[0], 'scene');
        }

        var extraOffsetLeft = elProps.left + elProps.right;
        var extraOffsetTop = elProps.top + elProps.bottom;

        let maskPosition = {
            width: $(el).width() + extraOffsetLeft +
            parseFloat(computedEl.getPropertyValue('padding-right')) +
            parseFloat(computedEl.getPropertyValue('padding-left')),
            height: $(el).height() + extraOffsetTop +
            parseFloat(computedEl.getPropertyValue('padding-top')) +
            parseFloat(computedEl.getPropertyValue('padding-bottom')),
            top: (absolutePosFromScene.top + afterRotationPos.y),
            left: (absolutePosFromScene.left + afterRotationPos.x),
        };

        const widget = this.mappedWidgets[el[0].id].widget;

        this.transformChildren(widget);
        if (widget.transformed_position && (
                !couiEditor.EXPORTING_WIDGET && !couiEditor.EXPORTING_COMPONENT
            )) {
            this.transform.transform(widget);
            maskPosition = helpers.getTransformedElMaskPos(widget);
        } else {
            delete widget.transformed_position;
        }

        $selectCorners.css({
            width: maskPosition.width,
            height: maskPosition.height,
            position: 'absolute',
            top: maskPosition.top,
            left: maskPosition.left
        });

        if (this.currentElementsSelection.length === 1 && !this._isElementRotated) {
            this.updateTransformOriginPoint(widget, maskPosition, $selectCorners);
        }
    }

    /**
     * Update the coordinates of transform origin point
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {IWidget} widget
     * @param {object} maskPosition
     * @param {object} $selectCorners
     */
    private updateTransformOriginPoint(widget: IWidget, positions?: { top, left }, $selectCorners?: JQuery): void {
        const $mask = $selectCorners || $('.select-corners');
        let maskPosition = {
            top: 0,
            left: 0
        };

        if (positions) {
            maskPosition = positions;
        } else {
            maskPosition.top = parseFloat($mask.css('top'));
            maskPosition.left = parseFloat($mask.css('left'));
        }

        let $transformOriginPoint;

        if ($('.transform-origin-point').length === 0) {
            $transformOriginPoint = $(`<point class="transform-origin-point" data-origin-id="${widget.id}"></point>`);
        } else {
            $transformOriginPoint = $('.transform-origin-point');
        }

        let {transformOriginX, transformOriginY} = this.getTransformOriginPositionPoint(widget.id);
        const GTzoomInFix = 0.1;
        transformOriginX = (transformOriginX - this.sceneBorderSize * (this.getSceneScale() - GTzoomInFix));
        transformOriginY = (transformOriginY - this.sceneBorderSize * (this.getSceneScale() - GTzoomInFix));

        if ($(`#scene > .transform-origin-point[data-origin-id="${widget.id}"]`).length === 0) {
            $(this.scenePreviewEl).append($transformOriginPoint);
        }

        $transformOriginPoint.css({'left': transformOriginX, 'top': transformOriginY});
    }

    /**
     * Remove multiple widgets
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {array} selection - array with ids
     * @param {bool} isNewWidget
     */
    removeWidgets(selection: string[], isNewWidget?) {
        var $jstree = $('#sceneTree').jstree();
        var jsTreeDeepClone = $.extend(true, {}, $jstree);
        for (var i = 0; i < selection.length; i++) {
            this.removeWidget(selection[i], isNewWidget, jsTreeDeepClone);
        }
    }

    /**
     * Remove widget
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {string} id
     * @param {bool} isNewWidget
     * @param {jquery_object} $jstree - optional
     */
    removeWidget(id, isNewWidget?, $jstree?) {

        if ($jstree) {
            $jstree.deselect_all(true); // jshint ignore:line
            $jstree.search(id);
        } else {
            var $jstree = $('#sceneTree').jstree();
        }

        var jstreeNode = $jstree.get_node($('li[data-id="' + id + '"]'));
        this._lastDeletedAnimationWidgets = {};

        if (jstreeNode) {
            var jstreeId = jstreeNode.id;
            var widgetId = jstreeNode['li_attr']['data-id'];
            var parentWidgetIds = [];
            var jstreeParentId = jstreeNode.parent;

            var editMenuButtons = $('#edit-menu').find('.btn-dynamic');
            var isButtonDisabled = editMenuButtons.hasClass('disabled');

            if (!isButtonDisabled) {
                editMenuButtons.addClass('disabled');
            }

            var jstreeParentChildren = $jstree.get_node(jstreeParentId).children;
            var jstreePosition = -1;
            var jstreeNodeBeforeId = false;
            var jstreeNodeAppendId = false;

            var widget = this.mappedWidgets[widgetId];

            for (var i = 0; i < jstreeNode.parents.length - 1; i++) {
                var getNode = $jstree.get_node(jstreeNode.parents[i]);
                parentWidgetIds.push(getNode['li_attr']['data-id']);
            }

            while (jstreePosition < jstreeParentChildren.length) {
                jstreePosition++;
                if (jstreeParentChildren[jstreePosition] === jstreeId) {
                    var nodeBeforeJstreeId = $jstree.get_node(jstreeParentId).children[jstreePosition - 1];
                    if (nodeBeforeJstreeId !== undefined) {
                        jstreeNodeBeforeId = $jstree.get_node(nodeBeforeJstreeId).li_attr['data-id'];
                    } else {
                        jstreeNodeAppendId = parentWidgetIds[0];
                    }
                    break;
                }
            }

            // undo/ redo info
            var deleteInfo: any = {
                widget: widget,
                widgetParentIds: parentWidgetIds,
                parentElementId: parentWidgetIds[0],
                jstreeParent: jstreeParentId,
                jstreePosition: jstreePosition,
                jstreeNodeBeforeId: jstreeNodeBeforeId,
                jstreeNodeAppendId: jstreeNodeAppendId,
                isNewWidget: isNewWidget
            };

            this._deleteSceneWidgetObj(parentWidgetIds, widgetId);
            this._deleteWidgetsAndAnimations(widgetId);

            deleteInfo.animationData = {};
            deleteInfo.animationData = this._lastDeletedAnimationWidgets;

            var actionState = this.getRedoUndoPrimaryState();
            this._sceneActionState.deleteElement = true;
            this.createUndoRedoCommand(actionState, widgetId, null,
                null,
                null, deleteInfo);

            $('#sceneTree').jstree().delete_node(jstreeId);
            $('#' + id).remove();

            this.propertiesElBar.empty();
            $('.select-corners').remove();
            $('.transform-origin-point').remove();
            this.exportScene();
            $('.info-widgets').getNiceScroll().resize();
        }
    }

    _deleteSceneWidgetObj(parents,
                          currentId) {

        var parentsLen = parents.length;
        var sceneWidgets = this.scene.widgets;
        var widgetsLen = sceneWidgets.length;

        if (parentsLen === 0) {
            for (let i = 0; i < widgetsLen; i++) {
                if (currentId === sceneWidgets[i].id) {
                    sceneWidgets.splice(i, 1);
                    return;
                }
            }
        } else {
            var found,
                count = 0;
            parents.reverse();

            // search for parent in top level
            for (let i = 0; i < widgetsLen; i++) {
                if (parents[0] === sceneWidgets[i].id) {
                    found = sceneWidgets[i];
                    count++;
                }
            }

            var deleteWidget = function (parentId) {

                // next search in children
                for (var x = 0; x < found.children.length; x++) {

                    if (currentId === found.children[x].id) {
                        found.children.splice(x, 1);
                        return;
                    }

                    if (parentId === found.children[x].id) {
                        count++;
                        found = found.children[x];
                        deleteWidget(parents[count]);
                    }
                }
            };

            deleteWidget(parents[count]);
        }
    }

    _deleteWidgetsAndAnimations(id) {
        var _this = this;

        function deleteWiget(widgets) {
            var len = widgets.length;

            for (var i = 0; i < len; i++) {
                var widget = widgets[i];
                _this._lastDeletedAnimationWidgets[widget.id] = $.extend(true, {}, _this.scene.animations[widget.id]);
                _this.Animations.deleteWidget(widget.id);
                delete _this.mappedWidgets[widget.id];
                if (widget.children.length > 0) {
                    deleteWiget(widget.children);
                }
            }
        }

        this._lastDeletedAnimationWidgets[id] = $.extend(true, {}, this.scene.animations[id]);

        deleteWiget(this.mappedWidgets[id].widget.children);
        this.Animations.deleteWidget(id);
        delete this.mappedWidgets[id];
    }

    saveUndoRedo(widgetId, propGroup, prop, value, originalPropGroup) {
        var _this = this;
        var sceneActionState = this._sceneActionState;
        var currentTab = couiEditor.openFiles[this.editorTabName];

        // if action come from user scene interaction or redo button record undo command
        if (sceneActionState.primaryAction === 'new action' || sceneActionState.primaryAction === 'redo') {
            if (currentTab.redo[0] !== undefined) {
                undoLenCommands = currentTab.redo[0].length;
            }
            createUndoRedo('undo');
        }

        if (sceneActionState.primaryAction === 'undo') {
            if (currentTab.undo[0] !== undefined) {
                redoLenCommands = currentTab.undo[0].length;
            }
            createUndoRedo('redo');
        }

        // Undo-redo functionality
        function createUndoRedo(type) {
            var undoGroup = originalPropGroup || propGroup;
            _this.createUndoRedoCommand(type, widgetId, undoGroup, propGroup, prop, value);
        }
    }

    /**
     * Stuff that we need to allow auto keyframe creation
     * @param {string} prop
     * @param {boolean} skipAutoKeyframes
     * @returns {boolean}
     */
    allowAutoKeyframe(prop, skipAutoKeyframes) {
        return this.autoKeyframe && !this._sceneActionState.draggingTimelinePin &&
            AllowedKeyframeProperties[prop] && !skipAutoKeyframes &&
            this._sceneActionState.primaryAction === 'new action';
    }

    setDropShadowProps(widgetId) {
        const widget = this.mappedWidgets[widgetId].widget;
        if (!widget['-webkit-filter']) {
            widget['-webkit-filter'] = {};
        }
        if (!widget['-webkit-filter'].dropShadowY) {
            widget['-webkit-filter'].dropShadowY = '0px';
        }
        if (!widget['-webkit-filter'].dropShadowX) {
            widget['-webkit-filter'].dropShadowX = '0px';
        }
        if (!widget['-webkit-filter'].dropShadowBlur) {
            widget['-webkit-filter'].dropShadowBlur = '0px';
        }
        if (!widget['-webkit-filter'].dropShadowColor) {
            widget['-webkit-filter'].dropShadowColor = 'rgb(0, 0, 0)';
        }
    }

    saveProperties(widgetId, propGroup, prop, value, originalPropGroup?) {
        var SaveGroups: any = Enums.SavePropertiesTypes;

        // skip auto keyframes generation if we have skip of undo redo steps
        var skipAutoKeyframes = false;
        if (this._skipUndoRedoSteps > 0 || helpers.notAllowedForAnimation(prop, value)) {
            skipAutoKeyframes = true;
        }

        // special case for transforms
        // force autokeyframes to apply missing transforms from widget to timeline animations(add keyframes for each missing properties)
        const animation = this.scene.animations[widgetId];
        if ((propGroup === 'transform' || propGroup === '-webkit-filter') &&
            !this._sceneActionState.initialLoad &&
            animation &&
            animation[animation.className] &&
            !this._sceneActionState.draggingTimelinePin &&
            this._sceneActionState.primaryAction === 'new action' &&
            !this.autoKeyframe) {
            const transformKeyframe = animation[animation.className].keyframes.transform;
            if (transformKeyframe) {
                if (!transformKeyframe[prop]) {
                    this.forceAutoKeyframes = true;
                } else if (transformKeyframe[prop]) {
                    const firstKeyframe = transformKeyframe[prop]['0'];

                    if (firstKeyframe && firstKeyframe.time.seconds === 0) {
                        this.forceAutoKeyframes = true;
                    }
                }
            }

            const filterKeyframe = animation[animation.className].keyframes['-webkit-filter'];
            if (filterKeyframe) {
                if (!filterKeyframe[prop]) {
                    this.forceAutoKeyframes = true;
                } else if (filterKeyframe[prop]) {
                    const firstKeyframe = filterKeyframe[prop]['0'];

                    if (firstKeyframe && firstKeyframe.time.seconds === 0) {
                        this.forceAutoKeyframes = true;
                    }
                }
            }
        }

        // Auto keyframes
        if ((this.allowAutoKeyframe(prop, skipAutoKeyframes) || this.forceAutoKeyframes) && !skipAutoKeyframes) {
            this._undoCreationStepsLength += 1;
        }

        if (propGroup === 'id property') {
            this.mappedWidgets[value] = {
                widget: this.mappedWidgets[widgetId].widget
            };

            this.mappedWidgets[widgetId].widget[prop] = value;
            if (widgetId !== value) {
                delete this.mappedWidgets[widgetId];
            }
        }

        // save undo/ redo
        if (this._skipUndoRedoSteps === 0) {
            let elementOnTheScene = $(`#${widgetId}`).length;
            if (originalPropGroup !== 'events' && !this._sceneActionState.draggingTimelinePin
                && !this._sceneActionState.pasteWidget) {
                // prevent unnecessary class change save
                if (!(prop === 'className' && elementOnTheScene === 0 &&
                        this._sceneActionState.primaryAction !== 'new action')) {
                    this.saveUndoRedo(widgetId, propGroup, prop, value, originalPropGroup);
                }
            }
        } else {
            this._skipUndoRedoSteps--;
        }

        if (propGroup === 'image') {
            this.mappedWidgets[widgetId].widget[prop] = value;
        }

        if (propGroup === 'boxShadow') {

            if (prop !== 'insetOutset' && prop !== 'color') {
                value = parseFloat(value) + 'px';
            }

            this.mappedWidgets[widgetId].widget['boxShadow'][prop] = value;
        }

        if (propGroup === '-webkit-filter') {
            if (prop === 'dropShadowColor' ||
                prop === 'dropShadowBlur' ||
                prop === 'dropShadowY' ||
                prop === 'dropShadowX'
            ) {
                this.setDropShadowProps(widgetId);
            }

            // TODO FIX THIS
            this.mappedWidgets[widgetId]
                .widget['-webkit-filter'] = this.mappedWidgets[widgetId].widget['-webkit-filter'] || {};

            this.mappedWidgets[widgetId]
                .widget['-webkit-filter'][prop] = this.mappedWidgets[widgetId].widget['-webkit-filter'][prop] || {};

            if ((value === 0 || value === '0deg' || value === '0px') &&
                prop !== 'dropShadowX' && prop !== 'dropShadowY' &&
                prop !== 'dropShadowColor' && prop !== 'dropShadowBlur') {
                delete this.mappedWidgets[widgetId]
                    .widget['-webkit-filter'][prop];
            } else {
                this.mappedWidgets[widgetId].widget['-webkit-filter'][prop] = value;
            }
        }

        if (propGroup === 'transform') {
            this.mappedWidgets[widgetId].widget['transform'][prop] = value;
        }

        if (propGroup === 'transform-origin') {
            this.mappedWidgets[widgetId].widget['transform-origin'][prop] = value;
        }

        if (propGroup === 'perspective-origin') {
            this.mappedWidgets[widgetId].widget['perspective-origin'][prop] = value;
        }

        if (propGroup === 'css property' || propGroup === 'set text' ||
            propGroup === 'class property' || propGroup === 'checked' ||
            propGroup === 'font') {
            this.mappedWidgets[widgetId].widget[prop] = value;
        }

        if (propGroup === 'geometry' ||
            propGroup === SaveGroups.Events.Local.modular ||
            propGroup === 'attrs' || propGroup === 'styles' ||
            propGroup === 'dataBindings') {
            this.mappedWidgets[widgetId].widget[propGroup][prop] = value;
        }

        if (propGroup === SaveGroups.Events.Engine.call ||
            propGroup === SaveGroups.Events.Engine.trigger ||
            propGroup === SaveGroups.Events.Local.javascript ||
            propGroup === SaveGroups.Events.Engine.blueprint) {
            this.mappedWidgets[widgetId].widget['events'][prop] = {};
            this.mappedWidgets[widgetId]
                .widget['events'][prop][propGroup] = value;
        }

        if (propGroup === 'remove property') {
            if (originalPropGroup === 'dataBindings' || 'styles') {
                delete this.mappedWidgets[widgetId].widget[originalPropGroup][prop];
            } else {
                delete this.mappedWidgets[widgetId].widget[prop];
            }
        }

        if (propGroup === SaveGroups.Events.Local.empty) {
            delete this.mappedWidgets[widgetId].widget.events[prop];
        }

        // Auto keyframes
        if (this.allowAutoKeyframe(prop, skipAutoKeyframes) || this.forceAutoKeyframes) {
            let currentPinTime = this.Timeline.getPinTime();
            (propGroup === 'css property') ? propGroup = 'styles' : '';
            let finalValue = this.styleValuesCollector.getValue(propGroup, prop, this.mappedWidgets[widgetId].widget);
            const keyframeProperty = Enums.StylePropToKeyframeProp[propGroup][prop];

            if (prop === 'transform-origin-x' || prop === 'transform-origin-y') {
                let transformX = '50%';
                let transformY = '50%';
                if (this.mappedWidgets[widgetId].widget['transform-origin']) {
                    transformX = this.mappedWidgets[widgetId].widget['transform-origin']['transform-origin-x'] || '50%';
                    transformY = this.mappedWidgets[widgetId].widget['transform-origin']['transform-origin-y'] || '50%';
                }
                finalValue = transformX + ' ' + transformY;
            } else if (prop === 'perspective-origin-x' || prop === 'perspective-origin-y') {
                let perspectiveX = '50%';
                let perspectiveY = '50%';
                if (this.mappedWidgets[widgetId].widget['perspective-origin']) {
                    perspectiveX = this.mappedWidgets[widgetId].widget['perspective-origin']['perspective-origin-x'] || '50%';
                    perspectiveY = this.mappedWidgets[widgetId].widget['perspective-origin']['perspective-origin-y'] || '50%';
                }
                finalValue = perspectiveX + ' ' + perspectiveY;
            } else if (prop === 'background') {
                propGroup = 'backgroundColor';
                finalValue = helpers.convertColorValues(finalValue);
            } else if (propGroup === '-webkit-filter') {
                finalValue = this.getFilterValues(this.mappedWidgets[widgetId].widget, '-webkit-filter', keyframeProperty);
            }

            const animation = this.scene.animations[widgetId];

            // save the keyframe at the beginning of the animation if we having only one keyframe for this transform property
            if (animation &&
                animation[animation.className].keyframes.transform &&
                this.forceAutoKeyframes) {
                if (!animation[animation.className].keyframes.transform[prop] ||
                    Object.keys(animation[animation.className].keyframes.transform[prop]).length < 2) {
                    currentPinTime = {
                        offset: -4,
                        seconds: 0
                    };
                }
            }

            // save the keyframe at the beginning of the animation if we having only one keyframe for this transform property
            if (animation &&
                animation[animation.className].keyframes['-webkit-filter'] &&
                this.forceAutoKeyframes) {
                if (!animation[animation.className].keyframes['-webkit-filter'][prop] ||
                    Object.keys(animation[animation.className].keyframes['-webkit-filter'][prop]).length < 2) {
                    currentPinTime = {
                        offset: -4,
                        seconds: 0
                    };
                }
            }

            this.Animations.addKeyframe(widgetId, propGroup, keyframeProperty, finalValue, currentPinTime);
        }

        if (!this._sceneActionState.draggingTimelinePin &&
            this._undoCreationStepsLength === 1 &&
            this._skipUndoRedoSteps === 0) {
            this.exportScene();

            if (skipAutoKeyframes) {
                skipAutoKeyframes = false;
            }
        }

        if (propGroup === 'transform') {
            // add keyframes for the missing transform properties applied to the widget
            helpers.syncTransformAnimations(widgetId, prop);
        }
        if (propGroup === '-webkit-filter') {
            // add keyframes for the missing filters properties applied to the widget
            helpers.syncFiltersAnimations(widgetId, prop);
        }
        this.forceAutoKeyframes = false;
    }

    _setUndoRedoType(originalPropGroup, prop) {
        var type;

        if (this._sceneActionState.createElement === true) {
            type = 'createElement';

            // reset create element state
            this._sceneActionState.createElement = false;

        } else if (this._sceneActionState.createNewWidget === true) {
            type = 'createElement';

            // reset create element state
            this._sceneActionState.createNewWidget = false;

        } else if (this._sceneActionState.deleteElement === true) {
            type = 'deleteElement';

            // reset delete element state
            this._sceneActionState.deleteElement = false;

        } else if (this._sceneActionState.widgetOption === true) {
            type = 'widgetOption';

            // reset options element state
            this._sceneActionState.widgetOption = false;

        } else if (this._sceneActionState.moveElement === true) {
            type = 'moveElement';

            // reset move element state
            this._sceneActionState.moveElement = false;

        } else if (this._sceneActionState.addFile === true) {
            type = 'deleteFile';

            // reset move element state
            this._sceneActionState.addFile = false;
        } else if (this._sceneActionState.deleteFile === true) {
            type = 'addFile';

            // reset move element state
            this._sceneActionState.deleteFile = false;
        } else if (this._sceneActionState.addKeyframe === true) {

            type = 'addKeyframe';

            this._sceneActionState.addKeyframe = false;
        } else if (this._sceneActionState.deleteKeyframe === true) {

            type = 'deleteKeyframe';

            this._sceneActionState.deleteKeyframe = false;
        } else if (this._sceneActionState.moveKeyframe === true) {

            type = 'moveKeyframe';

            this._sceneActionState.moveKeyframe = false;
        } else if (this._sceneActionState.deleteAnimationClassGlobal === true) {

            type = 'deleteAnimationClassGlobal';

            this._sceneActionState.deleteAnimationClassGlobal = false;
        } else if (this._sceneActionState.addAnimationClassGlobal === true) {

            type = 'addAnimationClassGlobal';

            this._sceneActionState.addAnimationClassGlobal = false;
        } else if (this._sceneActionState.createComponent === true) {

            type = 'createComponent';

            this._sceneActionState.createComponent = false;
        } else if (this._sceneActionState.deleteComponent === true) {

            type = 'deleteComponent';

            this._sceneActionState.deleteComponent = false;
        } else if (this._sceneActionState.setAnimationOptions === true) {

            type = 'setAnimationOptions';

            this._sceneActionState.setAnimationOptions = false;
        } else if (prop === 'background' || prop === 'color' ||
            prop === 'borderColor' || prop === 'dropShadowColor' ||
            (originalPropGroup === 'boxShadow' && prop === 'color')) {
            type = 'color';

        } else {
            // default command type
            type = 'property';
        }

        return type;
    }

    createUndoRedoCommand(sceneActionType, widgetId, originalPropGroup, propGroup, prop, value: any) {
        var _this = this;
        var isNewWidget = this._sceneActionState.createNewWidget;

        var type = this._setUndoRedoType(originalPropGroup, prop);

        var action = {};
        action[type] = {
            widgetId: widgetId,
            propGroup: originalPropGroup,
            propKey: prop
        };

        if (typeof value === 'object') {
            switch (type) {
                case 'deleteComponent':
                    action[type].createComponent = value;
                    break;
                case 'createComponent':
                    action[type].deleteComponent = value;
                    break;
                case 'deleteElement':
                    action[type].createWidget = value;
                    break;
                case 'createElement':
                    action[type].deleteWidget = value;
                    action[type].isNewWidget = isNewWidget;
                    break;
                case 'moveElement':
                    action[type].moveWidget = value;
                    break;
                case 'widgetOption':
                    action[type].options = value;
                    break;
                case 'addFile':
                    action[type].params = value;
                    break;
                case 'deleteFile':
                    action[type].params = value;
                    break;
                case 'addKeyframe':
                    action[type].deleteKeyframe = value;
                    break;
                case 'deleteKeyframe':
                    action[type].addKeyframe = value;
                    break;
                case 'moveKeyframe':
                    action[type].params = value;
                    break;
                case 'setAnimationOptions':
                    action[type].params = value;
                    break;
                case 'deleteAnimationClassGlobal':
                    action[type].params = value;
                    break;
                case 'addAnimationClassGlobal':
                    action[type].params = value;
                    break;
            }
        }

        switch (originalPropGroup) {
            case 'geometry':
                action[type].value = _this.mappedWidgets[widgetId].widget[propGroup][prop] || '0';
                break;
            case 'image':
                action[type].value = value;
                break;
            case 'units':
                if (prop === 'fontSize') {
                    action[type].value = _this.mappedWidgets[widgetId].widget[prop] || 'auto';
                } else if (helpers.isTransformProperty(prop)) {
                    action[type].value = _this.mappedWidgets[widgetId].widget[propGroup][prop] || Enums.TransformDefaultValue[prop];
                } else if (_this.mappedWidgets[widgetId].widget[propGroup] !== undefined) {
                    action[type].value = _this.mappedWidgets[widgetId].widget[propGroup][prop] || 'auto';
                } else {
                    action[type].value = 'auto';
                }
                break;
            case 'css property':
                action[type].value = _this.mappedWidgets[widgetId]
                    .widget[prop];
                break;
            case 'element-text':
            case 'element-class':
                // special property
                if (this._sceneActionState.editAnimationClass) {
                    action[type].editAnimationClass = this._sceneActionState.editAnimationClass;
                }
                action[type].value = _this.mappedWidgets[widgetId]
                    .widget[prop] || '';
                break;
            case 'checked':
                action[type].value = _this.mappedWidgets[widgetId]
                    .widget[prop];
                break;
            case 'dataBindings':
                propGroup = propGroup === 'remove property' ? originalPropGroup : propGroup;
                action[type].value = _this.mappedWidgets[widgetId].widget[propGroup][prop] || '';

                break;
            case 'element-id':
                action[type].widgetId = value;
                action[type].value = widgetId;
                break;
            case 'transform':
            case '-webkit-filter':
            case 'transform-origin':
            case 'perspective-origin':
            case 'boxShadow':
                if (helpers.isTransformProperty(prop)) {
                    if (type !== 'deleteKeyframe') {
                        var widgetValue;
                        if (prop === 'perspective-origin-x' || prop === 'perspective-origin-y') {
                            widgetValue = _this.mappedWidgets[widgetId].widget[originalPropGroup][prop] || Enums.TransformDefaultValue[prop];
                        } else if (prop !== 'transform-origin') {
                            widgetValue = _this.mappedWidgets[widgetId].widget[originalPropGroup][prop] || Enums.TransformDefaultValue[prop];
                        } else {
                            var transforms = _this.mappedWidgets[widgetId].widget[originalPropGroup];
                            widgetValue = (transforms['transform-origin-x'] || Enums.TransformDefaultValue[prop]) + ' ' + (transforms['transform-origin-y'] || Enums.TransformDefaultValue[prop]);
                        }
                        action[type].value = helpers.getTransformProperty(widgetValue, prop);
                    } else {
                        action[type].value = value.value;
                    }
                } else if (prop === 'rotate') {
                    action[type].value = _this.mappedWidgets[widgetId]
                        .widget[propGroup][prop] || '0deg';
                } else if (prop !== 'color' && prop !== 'insetOutset') {
                    action[type].value = _this.mappedWidgets[widgetId]
                            .widget[propGroup][prop] ||
                        Enums[propGroup + 'DefaultValues'][prop];
                } else if (prop === 'color') {
                    action[type].value = _this.mappedWidgets[widgetId]
                        .widget[propGroup][prop] || '#000';
                } else if (prop === 'insetOutset') {
                    action[type].value = _this.mappedWidgets[widgetId]
                        .widget[propGroup][prop] || 'remove';
                }
                break;
            case 'attrs':
            case 'styles':
                var defaultValue = '';
                if (prop === 'backgroundSizeWidth' ||
                    prop === 'backgroundSizeHeight') {
                    defaultValue = 'auto';
                } else if (prop === 'transformStyle') {
                    defaultValue = 'flat';
                } else if (prop === 'perspective') {
                    defaultValue = '0px';
                }

                action[type].value = _this.mappedWidgets[widgetId]
                    .widget[propGroup][prop] || defaultValue;
                break;
            case 'font':
                action[type].value = _this.mappedWidgets[widgetId]
                    .widget[prop] || 'auto';
                break;
        }

        this._setUndoRedoCommandsFill(action);
    }

    _setUndoRedoCommandsFill(action) {
        couiEditor.tabEdited(true);

        var undoRedoForAction = Object.keys(action)[0];
        var sceneActionType = this.getRedoUndoPrimaryState();

        if (sceneActionType === 'undo' || sceneActionType === 'new action') {
            this._undoCommandsFill(action, undoRedoForAction);
        }

        if (sceneActionType === 'redo') {
            this._redoCommandsFill(action);
        }
    }

    _undoCommandsFill(action, type) {

        var sceneActionState = this._sceneActionState;

        // Check undo command if come from user action on scene
        if (sceneActionState.primaryAction === 'new action') {

            // drag element on the scene and push multiple commands
            if (this._undoCreationStepsLength > 1) {
                commandUndo.push(action);
                this._undoCreationStepsLength--;
                return;
            }

            this.clearRedoCommands();
        }

        // Check undo command if come from redo button
        if (sceneActionState.primaryAction === 'redo') {

            // push number of commands from redo
            if (undoLenCount !== undoLenCommands) {
                commandUndo.push(action);
                undoLenCount++;
                return;
            }

        }

        // default pushing 1 command
        commandUndo.push(action);

        couiEditor.openFiles[this.editorTabName].undo.unshift(commandUndo);

        // reset
        undoLenCount = 1;
        undoLenCommands = 0;
        this._undoCreationStepsLength = 1;
        commandUndo = [];
    }

    clearRedoCommands() {
        couiEditor.openFiles[this.editorTabName].redo = [];
    }

    _redoCommandsFill(action) {
        // push number of commands from undo
        if (redoLenCount !== redoLenCommands) {
            commandRedo.push(action);
            redoLenCount++;
            return;
        }

        // default pushing 1 command
        commandRedo.push(action);

        couiEditor.openFiles[this.editorTabName].redo.unshift(commandRedo);

        // reset
        redoLenCount = 1;
        redoLenCommands = 0;
        commandRedo = [];
    }

    addEvent(el, eventName, fn) {
        document.getElementById(el).addEventListener(eventName, window.editorEvents[fn], false);
    }

    removeEvent(el, eventName, fn) {
        document.getElementById(el).removeEventListener(eventName, window.editorEvents[fn], false);
    }

    inputNumbersFocusInOutHandler(id) {
        $('#' + id + ' input[type="number"]').focusin(function () {
            $(this).addClass('dark-arrows-numbers');
        });

        $('#' + id + ' input[type="number"]').focusout(function () {
            $(this).removeClass('dark-arrows-numbers');
        });
    }

    initKendoProperties(widget) {

        // Initialize the whole property bar
        $('#properties-wrap').kendoPanelBar({
            animation: 'none'
        });

        // Initialize the individual property bars wrapper
        $('#properties').kendoPanelBar({
            animation: 'none'
        });

        $('.k-list-container:not(#scene-aspect-ratio-list, #scene-events-split-button_optionlist)').remove();

        $('#left-pane select').kendoDropDownList({
            animation: false
        });

        if (widget.boxShadow !== undefined) {
            this.initBoxShadowColorPicker(widget);
        }

        if (widget['-webkit-filter'] !== undefined) {
            this.initFilterColorPicker(widget);
        }

        if (!couiEditor.isBasicPanelOpen) {
            $('#properties').data('kendoPanelBar').collapse('#basic-properties');
        }
    }

    /**
     * Initiallize main properties event handlers
     * This way is a bit faster than initialization events
     * @param widget {Object} - currently selected widget
     */
    kendoPropertiesHandler() {
        var propertiesPanel = $('#properties').data('kendoPanelBar');

        var onExpand = function (e) {
            if (e.item.id === 'basic-properties') {
                couiEditor.isBasicPanelOpen = true;
            }

            if (couiEditor.currentOpenedTabs.indexOf(e.item.id) === -1) {
                couiEditor.currentOpenedTabs.push(e.item.id);
            }
        };

        var onCollapse = function (e) {
            if (e.item.id === 'basic-properties') {
                couiEditor.isBasicPanelOpen = false;

                if (couiEditor.currentOpenedTabs.indexOf('basic-properties') === -1) {
                    return;
                }
            }

            var index = couiEditor.currentOpenedTabs.indexOf(e.item.id);
            couiEditor.currentOpenedTabs.splice(index, 1);
        };

        propertiesPanel.unbind('expand', onExpand);
        propertiesPanel.bind('expand', onExpand);

        propertiesPanel.unbind('collapse', onCollapse);
        propertiesPanel.bind('collapse', onCollapse);
    }

    /**
     * Expand the currently opened panels
     * @param widget {Object} - currently selected element
     * @param panelPropertyIds {Array} - array of all opened panels
     */
    expandKendoPanels(widget, panelPropertyIds) {
        var _this = this;
        var openedTabs = panelPropertyIds;

        // Check if the element has border property panel
        var haveBorderProperties = function (widgetType) {
            var types = Enums.noBorderPropertyElements;

            return types.indexOf(widgetType) === -1;
        };

        // Handle the different property render functions
        // Using object literal is faster than a switch statement
        var panelRenderer = {
            'geometry-properties': function () {
                $('#geometry-properties').off();
                _this._renderGeometryPropertiesPanel(widget);
            },
            'text-properties': function () {
                $('#text-properties').off();
                _this._renderTextPropertiesPanel(widget);
            },
            'border-style-properties': function () {
                $('#border-style-properties').off();
                if (haveBorderProperties(widget.type)) {
                    _this._renderBorderPropertiesPanel(widget);
                }
            },
            'events-properties': function () {
                $('#events-propertie').off();
                _this._renderEventPropertiesBar(widget);
            },
            'transform-properties': function () {
                $('#transform-properties').off();
                _this._renderTransformPropertiesBar(widget);
            },
            'data-binding-properties': function () {
                $('#data-binding-properties').off();
                _this._renderDataBindingPropertiesBar(widget);
            },
            'blend-modes-properties': function () {
                $('#blend-modes-properties').off();
                _this._renderBlendModesPanel(widget);
            }
        };

        if (openedTabs.length > 0) {
            for (var i = 0; i < openedTabs.length; i++) {
                var property = openedTabs[i];

                if (typeof panelRenderer[openedTabs[i]] === 'function') {
                    panelRenderer[property]();
                }

                $('#properties').data('kendoPanelBar').expand('#' + panelPropertyIds[i]);
            }
        }
    }

    initBoxShadowColorPicker(widget) {
        var _this = this;
        var $boxShadowColorPicker = $('#boxShadow-picker');

        /**
         * Set the box-shadow color and apply it.
         * Keep the method invocations and variable assignment
         * in the same sequence. saveProperties() needs to run before
         * boxShadowProps and applyProperties().
         *
         * @param color {String}
         * @private
         */
        var setBoxShadowColor = function (color) {
            _this.saveProperties(widget.id, 'boxShadow', 'color', color);

            var boxShadowProps = _this.buildBoxShadowProperty(widget);
            _this.applyProperties(widget.id, 'css property', 'boxShadow', boxShadowProps);
        };

        $boxShadowColorPicker.kendoColorPicker({
            buttons: false,
            open: function () {
                // TODO: Ugly - info on first comment
                // change it if is possible
                _this.colorPickerLeftReposition();
            },
            close: function () {
                // TODO: Ugly - info on first comment
                _this.colorPickerRemoveLeftReposition();
                var color = $boxShadowColorPicker.data('kendoColorPicker').value();
                _this._sceneActionState.primaryAction = 'new action';
                setBoxShadowColor(color);

            },
            select: function (event) {
                $boxShadowColorPicker.data('kendoColorPicker').value(event.value);
                var boxShadowProps = _this.buildBoxShadowProperty(widget);
                _this.applyProperties(widget.id, 'css property', 'boxShadow', boxShadowProps);
            },
            value: widget.boxShadow.color,
            opacity: true
        });
    }

    initFilterColorPicker(widget) {
        var _this = this;
        var $dropShadowColorPicker = $('#dropShadow-picker');

        /**
         * Set and apply filters color
         * @private
         */
        var setFilterColor = function () {
            var color = $dropShadowColorPicker.data('kendoColorPicker').value();
            var el = document.getElementById(widget.id);

            _this.saveProperties(widget.id, '-webkit-filter', 'dropShadowColor', color);
            helpers.splitCssStringProperties(widget.id, el.style['webkitFilter'], 'webkitFilter', 'dropShadowColor', color, function (val) {
                _this.applyProperties(widget.id, 'css property', '-webkit-filter', val);
            });
        };

        var widgetDropShadowColor = widget['-webkit-filter']['dropShadowColor'];
        var dropShadowColorPickerValue = '#000';

        if (widgetDropShadowColor) {
            if (widgetDropShadowColor instanceof Array) {
                dropShadowColorPickerValue = widgetDropShadowColor[0];
            } else {
                dropShadowColorPickerValue = widgetDropShadowColor;
            }
        }

        $dropShadowColorPicker.kendoColorPicker({
            buttons: false,
            open: function () {
                // TODO: Ugly - info on first comment
                // change it if is possible
                _this.colorPickerLeftReposition();

                $('body').on('mouseup', function () {
                    _this._sceneActionState.primaryAction = 'new action';
                    setFilterColor();
                });
            },
            close: function () {
                // TODO: Ugly - info on first comment
                _this.colorPickerRemoveLeftReposition();

                $('body').off('mouseup');
            },
            select: function (event) {
                $dropShadowColorPicker.data('kendoColorPicker').value(event.value);
                setFilterColor();
            },
            value: dropShadowColorPickerValue,
            opacity: true
        });
    }

    /**
     * Applies the selected to a complex toolbar consisting of units and values
     * @function setUnitNumberToolbar
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {string} propKey - current property key
     * @param {string} value - value to set
     * @param {boolean} interact - comes from scene interaction(drag, resize)
     */
    setUnitNumberToolbar(propKey, value): void {
        var filteredValue = value.replace(/[^\d.-]+/, '');
        var unit = helpers.getUnitStyle(value);

        const numberInput = $('input[data-property-key="' + propKey + '"]');

        numberInput.attr('disabled', false);

        if (unit === 'px' || unit === 'deg') {
            numberInput.attr('step', 1);
            numberInput.val(Math.round(filteredValue));
        } else if (unit === 'auto' || helpers.isBackgroundPositionString(value)) {
            helpers.disableKendoInput(propKey);
        } else {
            numberInput.attr('step', 0.1);
            numberInput.val(filteredValue);
        }
    }

    /**
     * Applies the selected value to the widget and sets the undo redo
     * @function _setUnits
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {jquery_object} $this - the clicked number/slider/value
     * @param {object} widget - the widget owning the property
     * @param {string} currentWidgetId - the current id of the widget
     * @param {string} propGroup - current property group
     * @param {string} propKey - current property key
     * @param {string} value - value (coming only if from undo redo)
     * @example `[ 'jquerySelector','<Object>','responsiveImage1', 'units', 'backgroundPositionX', 'center']`
     */
    _setUnits($this, widget, currentWidgetId, propGroup, propKey, value) {
        var newValue;           // the new value of the property;
        var currentValue;       // currentlyValue;
        var selectedUnit;       // currently selected units;

        var saveType = 'css property';

        var originalPropGroup = propGroup;
        var localPropGroup = this._getPropertyGroup(propKey);

        if (value !== undefined) {

            // value came from an undo redo command

            if (helpers.isBackgroundPositionString(value)) {
                // case: redoing a background position string
                selectedUnit = value;
                newValue = helpers.getStringPropertyPercent(value);
            } else {
                // case: redoing regular value
                selectedUnit = helpers.getUnitStyle(value);
                newValue = unitsConvertor.convertUnitsToPixel(currentWidgetId, value, propKey);
            }

            this.selectKendoDropdownItem(originalPropGroup, propKey, selectedUnit);
            this.setUnitNumberToolbar(propKey, value);

            this.saveProperties(currentWidgetId, localPropGroup, propKey, value, originalPropGroup);
            this.applyProperties(currentWidgetId, saveType, propKey, newValue);

            return;
        } else {
            // value came from an click on the panel
            selectedUnit = $this.find(':selected').val();
        }

        // Selected value from a backgroundPositionString
        if (helpers.isBackgroundPositionString(selectedUnit)) {
            currentValue = helpers.getStringPropertyPercent(selectedUnit);
            this.setUnitNumberToolbar(propKey, selectedUnit);
            this.saveProperties(currentWidgetId, localPropGroup, propKey, selectedUnit, originalPropGroup);
            this.applyProperties(currentWidgetId, saveType, propKey, currentValue);

            return;
        } else if (widget[localPropGroup]) {
            currentValue = widget[localPropGroup][propKey];
        } else {
            currentValue = widget[propKey];
        }

        if (helpers.isTransformProperty(propKey) && currentValue === undefined) {
            currentValue = Enums.TransformDefaultValue[propKey];
        }

        // Selected a backgroundPositionString //
        if (helpers.isBackgroundPositionString(currentValue)) {
            if (currentValue === 'center' || currentValue === 'bottom' || currentValue === 'right') {
                currentValue = this._fixBackgroundPercentage(currentValue, currentWidgetId, propKey);
            } else {
                currentValue = helpers.getStringPropertyPercent(currentValue);
            }
        }

        if (selectedUnit !== 'auto') {
            newValue = unitsConvertor.convertUnitsToPixel(currentWidgetId, currentValue, propKey);
        } else {
            if (localPropGroup !== 'geometry' && !(propKey === '-webkit-mask-sizeHeight' || propKey === '-webkit-mask-sizeWidth')) {

                saveType = 'remove css property';
                localPropGroup = 'remove property';
            }
            newValue = selectedUnit;
        }

        var newUnitValue = unitsConvertor.convertPixelToUnit(currentWidgetId, newValue, selectedUnit, propKey);

        if (newUnitValue !== 'auto') {
            let unitStyle = helpers.getUnitStyle(newUnitValue);

            newUnitValue = parseFloat(newUnitValue).toFixed(this.formatTo) + unitStyle;
        }

        if (helpers.isTransformProperty(propKey)) {
            saveType = 'transform';
        }

        if (propKey === 'width' || propKey === 'height') {
            newValue = newUnitValue;
        }

        this.setUnitNumberToolbar(propKey, newUnitValue);
        this.saveProperties(currentWidgetId, localPropGroup, propKey, newUnitValue, originalPropGroup);
        this.applyProperties(currentWidgetId, saveType, propKey, newValue);
        if (newValue === 'auto' && (propKey === 'top' || propKey === 'left')) {
            this.clearSelectedElements();
            this.selectJstreeItem(widget.id);
        }
    }

    /**
     * Adjusts the value comming in from a background string property ('center, left') to the appropriate pixel value
     * @function _fixBackgroundPercentage
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {string} currentValue - the current value selected
     * @param {string} currentWidgetId - the current selected widget Id
     * @param {string} propKey - the current selected property Key
     */
    _fixBackgroundPercentage(currentValue, currentWidgetId, propKey) {

        var newValue = helpers.getStringPropertyPercent(currentValue);
        var backgroundSize = helpers.getBackgroundSize($('#' + currentWidgetId)[0]);
        var pixelValue = parseFloat(unitsConvertor.convertUnitsToPixel(currentWidgetId, newValue, propKey));

        if (currentValue === 'center') {
            if (propKey === 'backgroundPositionX') {
                pixelValue = pixelValue - backgroundSize.width / 2;
            } else {
                pixelValue = pixelValue - backgroundSize.height / 2;
            }
        } else if (currentValue === 'bottom' || currentValue === 'right') {
            if (propKey === 'backgroundPositionX') {
                pixelValue = pixelValue - backgroundSize.width;
            } else {
                pixelValue = pixelValue - backgroundSize.height;
            }
        }

        return unitsConvertor.convertPixelToUnit(currentWidgetId, pixelValue, '%', propKey);
    }

    _getPropertyGroup(propKey) {
        // Returning property group for saving and applying properties
        if (propKey === 'fontSize') {
            return 'font';
        } else if (
            propKey === 'borderWidth' ||
            propKey === 'borderTopLeftRadius' ||
            propKey === 'borderTopRightRadius' ||
            propKey === 'borderBottomLeftRadius' ||
            propKey === 'borderBottomRightRadius' ||
            propKey === 'backgroundSizeWidth' ||
            propKey === 'backgroundSizeHeight' ||
            propKey === 'backgroundPositionX' ||
            propKey === 'backgroundPositionY' ||
            propKey === '-webkit-mask-position-x' ||
            propKey === '-webkit-mask-position-y' ||
            propKey === '-webkit-mask-sizeWidth' ||
            propKey === '-webkit-mask-sizeHeight' ||
            propKey === 'padding') {
            return 'styles';
        } else if (
            helpers.isTransformProperty(propKey)
        ) {
            return 'transform';
        } else {
            return 'geometry';
        }
    }

    _setBackgroundColor($this, widget,
                        id, group, key, value) {

        this.setPropertiesBarValue(id, group, key, value);
        this.saveProperties(id, 'css property', 'background', value);
        this.applyProperties(id, 'css property', 'background', value);
    }

    _setBorderColor($this, widget,
                    id, group, key, value) {

        this.setPropertiesBarValue(id, group, key, value);
        this.saveProperties(id, 'css property', 'borderColor', value);
        this.applyProperties(id, 'css property', 'borderColor', value);
    }

    _setColor($this, widget,
              id, group, key, value) {

        this.setPropertiesBarValue(id, group, key, value);
        this.saveProperties(id, 'css property', 'color', value);
        this.applyProperties(id, 'css property', 'color', value);
    }

    _setPositionType(widget, value) {
        var $kendoSelectTop = $('select[data-property-set="units"][data-property-key="top"]').data('kendoDropDownList');

        var $kendoSelectLeft = $('select[data-property-set="units"][data-property-key="left"]').data('kendoDropDownList');

        if (value === 'relative') {


            $('input[data-property-key="left"]').prop('disabled', true);
            $('input[data-property-key="top"]').prop('disabled', true);

            this.setPropertiesBarValue(widget.id, 'geometry', 'left', '0');
            this.setPropertiesBarValue(widget.id, 'geometry', 'top', '0');

            if (this._sceneActionState.primaryAction === 'new action') {
                if (this._sceneActionState.hierarchyElementMove) {
                    this._undoCreationStepsLength = 4;
                } else {
                    this._undoCreationStepsLength = 3;
                }

                this._setProperties(widget, null, 'geometry', 'left', 'auto');
                this._setProperties(widget, null, 'geometry', 'top', 'auto');
            }

            if ($kendoSelectTop) {

                $kendoSelectTop.enable(false);
                $kendoSelectLeft.enable(false);
            }

            this.applyProperties(widget.id, 'css property', 'position', 'relative');
            this.clearSelectedElements();
            this.selectJstreeItem(widget.id);
        } else if (value === 'absolute') {

            if (!this._sceneActionState.hierarchyElementMove) {
                $('input[data-property-key="left"]').prop('disabled', false);
            }

            if (!this._sceneActionState.hierarchyElementMove) {
                $('input[data-property-key="top"]').prop('disabled', false);
            }

            if ($kendoSelectTop) {

                $kendoSelectTop.enable(true);
                $kendoSelectLeft.enable(true);
            }

            this.applyProperties(widget.id, 'css property', 'position', 'absolute');
        }
    }

    _setGeometry($this, currentWidget, currentWidgetId, propGroup, propKey, value) {
        var newVal;
        var oldVal;
        var originalPropGroup = propGroup;
        var saveType = 'css property';
        var units;
        var valueAsNumber;

        if (value === undefined) {

            if (propKey === 'position') {

                newVal = $this.find(':selected').val();
                oldVal = currentWidget.geometry.position;
                this._setPositionType(currentWidget, newVal);

            } else {

                valueAsNumber = helpers.patchInputNumberBehavior({
                    $this: $this,
                    currentWidget: currentWidget,
                    propGroup: propGroup,
                    propKey: propKey
                });

                units = $('select[data-property-key="' + propKey + '"]').find(':selected').val();

                if (propKey === 'rotation') {
                    var rad = unitsConvertor.toRadians(valueAsNumber);
                    newVal = rad + 'rad';
                    // handle height and width's negative input values
                } else if ((propKey === 'width' || propKey === 'height') && parseFloat(valueAsNumber) < 0) {
                    valueAsNumber = 0;
                    newVal = 0 + units;
                    $this.val(0);
                } else {
                    // return current units for saving
                    newVal = valueAsNumber + units;
                }

                // return pixels to scene live view
                if ((propKey === 'width' || propKey === 'height') && units === '%') {
                    oldVal = newVal;
                } else {
                    oldVal = unitsConvertor.convertUnitsToPixel(currentWidgetId, $this.val() + units, propKey);
                }
            }

        } else {
            // Undo/ redo action
            newVal = value;
            units = helpers.getUnitStyle(newVal);

            // apply visual on the scene percent
            if (units === '%') {
                oldVal = newVal;
            } else {
                // else apply visual on the scene pixels
                oldVal = unitsConvertor.convertUnitsToPixel(currentWidgetId, value, propKey);
            }

            if (propKey === 'position') {
                // set dropdown unit by kendo dropdown api
                this._setPositionType(currentWidget, newVal);
                this.selectKendoDropdownItem(propGroup, propKey, newVal);

            } else {
                this.setPropertiesBarValue(currentWidgetId, originalPropGroup, propKey, newVal);
            }
        }

        if (valueAsNumber !== '' && valueAsNumber !== null) {
            this.saveProperties(currentWidgetId, propGroup, propKey, newVal, originalPropGroup);
            this.applyProperties(currentWidgetId, saveType, propKey, oldVal);
            this.setUnitNumberToolbar(propKey, newVal);
        }
    }

    /**
     * Sets the data binding properties of the elements
     * @function _setUnits
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {jquery_object} $this - the clicked number/slider/value
     * @param {object} currentWidget - the widget owning the property
     * @param {string} currentWidgetId - the current id of the widget
     * @param {string} propGroup - current property group
     * @param {string} propKey - current property key
     * @param {string} value - value (coming only if from undo redo)
     * @example `[ 'jquerySelector','<Object>','responsiveImage1', 'units', 'backgroundPositionX', 'center']`
     */
    _setBindings($this, currentWidget, currentWidgetId, propGroup, propKey, value) {
        var val;
        var oldVal;

        var originalPropGroup = propGroup;
        var applyType = 'dataBindings';

        if (!value && value !== '') {
            val = $this.val();
        } else {
            val = value;
        }

        if (val.trim() === '') {
            propGroup = 'remove property';
        }

        propKey = helpers.translateDataBindNames(propKey, false);
        oldVal = currentWidget.dataBindings[propKey];

        this.saveProperties(currentWidgetId, propGroup, propKey, val, originalPropGroup);
        this.applyProperties(currentWidgetId, applyType, propKey, val);

        this.setDataBindingInput(originalPropGroup, propKey, val);
    }

    _setEvents($this, currentWidget, currentWidgetId, propGroup, propKey, value) {
        var val;
        var oldVal;
        // Global events numerations
        var Events = Enums.SavePropertiesTypes.Events;
        var originalPropGroup = propGroup;
        var applyType = 'events';
        var eventType = $this.attr('data-event-type');

        oldVal = currentWidget.events[propKey];

        // text input field changes
        if (eventType === Events.Engine.blueprint) {
            // changing the value in blueprint input field
            val = $this.val();
            propGroup = Events.Engine.blueprint;
        } else if (eventType === Events.Engine.call) {
            // changing input value in engineCall input field
            val = this.buildEngineEventArray($this, 'engine-call');
            propGroup = Events.Engine.call;
        } else if (eventType === Events.Engine.trigger) {
            // changing input value in engineTrigger input field
            val = this.buildEngineEventArray($this, 'engine-trigger');
            propGroup = Events.Engine.trigger;
        } else if (eventType === Events.Local.javascript) {
            /// changing the value in a javascript input field
            val = $this.val();
            propGroup = Events.Local.javascript;
        } else {
            // Switching select drop down event types
            var selectedDropdown = this._setEventsByDropDown($this,
                propGroup, propKey, originalPropGroup);
            val = selectedDropdown.val;
            propGroup = selectedDropdown.propGroup;
        }

        if (val === Events.Local.empty) {
            propGroup = Events.Local.empty;
        }

        this.saveProperties(currentWidgetId, propGroup, propKey, val, originalPropGroup);
        this.applyProperties(currentWidgetId, applyType, propKey, val);
    }

    _setEventsByDropDown($this,
                         propGroup, propKey, originalPropGroup) {
        // Global events numerations
        var Events = Enums.SavePropertiesTypes.Events;
        var _this = this;
        var selectedDropdown = $this.find(':selected').val();
        var val: any = '';

        if (selectedDropdown === Events.Engine.blueprint) {

            this.hideShowEventParams($this, 'blueprintFunction');
            propGroup = Events.Engine.blueprint;
            val = $this.parents('.events-wrap')
                .find('.set-blueprint-function').val();
        } else if (selectedDropdown === Events.Local.javascript) {

            this.hideShowEventParams($this, 'javascriptFunction');
            propGroup = Events.Local.javascript;

            var widgetId = $('#selected-element').attr('data-element-id');
            var widgetEvents = this.mappedWidgets[widgetId].widget.events;

            if (!widgetEvents[propKey]) {
                widgetEvents[propKey] = {
                    javascriptFunction: ''
                };
            }

            val = widgetEvents[propKey].javascriptFunction;

            _this.createModalJsEditor(propKey,
                Events.Local.javascript, val, originalPropGroup);
        } else if (selectedDropdown === Events.Engine.call) {

            this.hideShowEventParams($this, 'engineCall');
            propGroup = Events.Engine.call;
            val = this.buildEngineEventArray($this, 'engine-call');
        } else if (selectedDropdown === Events.Engine.trigger) {

            this.hideShowEventParams($this, 'engineTrigger');
            propGroup = Events.Engine.trigger;
            val = this.buildEngineEventArray($this, 'engine-trigger');
        } else {
            // Standard javascript name of function
            // from drop down menu
            this.hideShowEventParams($this);
            val = $this.val();
        }

        return {
            propGroup: propGroup,
            val: val
        };
    }

    _setFonts($this, currentWidget, currentWidgetId, propGroup, propKey, value) {

        var newVal;
        if (value !== undefined) {
            newVal = value;
        } else {
            let numberValue = $this.val();
            if (propKey === 'fontSize' && numberValue < 0) {
                numberValue = 0;
            }
            newVal = numberValue + helpers.getUnitStyle(currentWidget[propKey]);
        }

        if (currentWidget['fontSize'] === undefined) {
            currentWidget['fontSize'] = '';
        }

        // return pixels to scene live view //
        var pixelValue = unitsConvertor.convertUnitsToPixel(currentWidgetId, newVal, propKey);

        this.saveProperties(currentWidgetId, propGroup, propKey, newVal, propGroup);
        this.applyProperties(currentWidgetId, 'css property', propKey, pixelValue);
        this.setPropertiesBarValue(currentWidgetId, propGroup, propKey, newVal);
    }

    _setAttrs($this, currentWidget, currentWidgetId, propGroup, propKey, value) {
        var val;
        var originalPropGroup = propGroup;
        var saveType = 'attrs';

        if (value !== undefined) {
            // undo - redo
            val = value;
            $('[data-property-set="' + propGroup + '"][data-property-key="' + propKey + '"]').val(val);
        } else {
            val = $this.val();
        }

        this.saveProperties(currentWidgetId, saveType, propKey, val, originalPropGroup);
        this.applyProperties(currentWidgetId, saveType, propKey, val);
    }

    _setStyles($this, currentWidget, currentWidgetId, propGroup, propKey, value) {
        var val;
        var originalPropGroup = propGroup;
        var saveType = 'css property';
        var oldVal;

        if (propKey === 'borderWidth' ||
            propKey === 'borderTopLeftRadius' ||
            propKey === 'borderTopRightRadius' ||
            propKey === 'borderBottomLeftRadius' ||
            propKey === 'borderBottomRightRadius' ||
            propKey === 'backgroundPositionX' ||
            propKey === 'backgroundPositionY' ||
            propKey === '-webkit-mask-position-x' ||
            propKey === '-webkit-mask-position-y' ||
            propKey === 'padding' ||
            propKey === 'backgroundSizeWidth' ||
            propKey === 'backgroundSizeHeight' ||
            propKey === '-webkit-mask-sizeWidth' ||
            propKey === '-webkit-mask-sizeHeight' ||
            propKey === 'perspective') {

            // undo - redo mode
            if (value !== undefined) {
                val = value;
                this.setPropertiesBarValue(currentWidgetId, propGroup, propKey, val);

                oldVal = unitsConvertor.convertUnitsToPixel(currentWidgetId, val, propKey);
            } else {
                var units = $('select[data-property-key="' + propKey + '"]').find(':selected').val() || '';
                // return current units for saving
                val = helpers.patchInputNumberBehavior({
                    $this: $this,
                    currentWidget: currentWidget,
                    propGroup: propGroup,
                    propKey: propKey
                }) + units;

                // return pixels to scene live view
                oldVal = unitsConvertor.convertUnitsToPixel(currentWidgetId, val, propKey);
            }

            if (propKey === 'backgroundSizeWidth' ||
                propKey === 'backgroundSizeHeight') {

                this.setBackgroundSizeWidthHeight(currentWidgetId, propKey, val);
                return;
            } else if (propKey === '-webkit-mask-sizeWidth' ||
                propKey === '-webkit-mask-sizeHeight') {
                this.setWebkitMaskSizeWidthHeight(currentWidgetId, propKey, val);
                return;
            } else if (propKey === 'perspective') {
                if (parseFloat(val) === 0) {
                    oldVal = 'none';
                }
            }

        } else if (propKey === 'perspective-origin-x' ||
            propKey === 'perspective-origin-y') {

            if (propKey === 'perspective-origin-x') {
                if (!currentWidget[propGroup]['perspective-origin-y']) {
                    let perspectiveY = $('[data-property-set="styles"][data-property-key="perspective-origin-y"]').val();
                    currentWidget[propGroup]['perspective-origin-y'] = perspectiveY;
                }
            }

            if (propKey === 'perspective-origin-y') {
                if (!currentWidget[propGroup]['perspective-origin-x']) {
                    let perspectiveX = $('[data-property-set="styles"][data-property-key="perspective-origin-x"]').val();
                    currentWidget[propGroup]['perspective-origin-x'] = perspectiveX;
                }
            }

            // undo - redo mode
            if (value !== undefined) {
                val = value;
            } else {
                val = $this.val();
            }
            this.setPropertiesBarValue(currentWidgetId, propGroup, propKey, val);
        } else if (propKey === 'backgroundSize' || propKey === 'backgroundRepeat') {
            // undo - redo mode
            if (value !== undefined) {
                val = oldVal = value;
            } else {
                val = oldVal = $this.val();
            }
            this._setBackgroundRepeatOrSize($this, currentWidget, propKey, propGroup, val);
        } else if (propKey === '-webkit-mask-size' || propKey === 'webkitMaskRepeat') {
            // undo - redo mode
            if (value !== undefined) {
                val = oldVal = value;
            } else {
                val = oldVal = $this.val();
            }

            oldVal = this._setWebkitMaskRepeatOrSize($this, currentWidget, propKey, propGroup, val);
        } else if (propKey === 'fontFamily') {
            val = oldVal = value;
        } else {
            // undo - redo mode
            if (value !== undefined) {
                val = oldVal = value;

                if (propKey === 'textAlign' ||
                    propKey === 'textTransform' ||
                    propKey === 'textDecoration') {

                    $('[data-property-set="' + propGroup + '"][value="' + val + '"]').prop('checked', true);
                } else if (propKey === 'zIndex' ||
                    propKey === 'flex' ||
                    propKey === 'order' ||
                    propKey === 'opacity') {

                    $('[data-property-set="' + propGroup + '"][data-property-key="' + propKey + '"]').val(val);
                } else if (propKey === 'webkitMaskImage') {
                    if (val === ' ') {
                        this.updateBackgroundFilePathInfo('hide');
                    } else {
                        this.updateBackgroundFilePathInfo('show', val);
                    }

                } else if (propKey === 'fontFamily') {
                    var $kendoSelect = $('select[data-property-set="' + propGroup + '"][data-property-key="' + propKey + '"]');
                    $kendoSelect.data('kendoDropDownList')
                        .select(function (dataItem) {
                            return dataItem.style === val;
                        });
                } else {

                    // set dropdown unit by kendo dropdown api
                    this.selectKendoDropdownItem(propGroup, propKey, val);
                }
            } else {
                val = oldVal = helpers.patchInputNumberBehavior({
                    $this: $this,
                    currentWidget: currentWidget,
                    propGroup: propGroup,
                    propKey: propKey
                });
            }
        }

        this.saveProperties(currentWidgetId, propGroup, propKey, val, originalPropGroup);
        this.applyProperties(currentWidgetId, saveType, propKey, oldVal);
    }

    _setBackgroundRepeatOrSize($this, currentWidget, propKey, propGroup, val) {

        if (val === 'auto' || propKey === 'backgroundRepeat') {

            var width = currentWidget.styles.backgroundSizeWidth;
            var height = currentWidget.styles.backgroundSizeHeight;

            var widthInput = $('input[data-property-key="backgroundSizeWidth"]');
            var heightInput = $('input[data-property-key="backgroundSizeHeight"]');

            var widthValue;
            var heightValue;

            var heightType;
            var widthType;

            if (width === 'auto' || height === 'auto') {

                widthType = 'px';
                heightType = 'px';

                var unitGeoWidth = currentWidget.geometry.width;
                var unitGeoHeight = currentWidget.geometry.height;

                widthValue = unitsConvertor.convertUnitsToPixel(currentWidget.id, unitGeoWidth, propKey);
                heightValue = unitsConvertor.convertUnitsToPixel(currentWidget.id, unitGeoHeight, propKey);

                widthInput.attr('step', 1);
                heightInput.attr('step', 1);

                widthValue = parseInt(widthValue);
                heightValue = parseInt(heightValue);

            } else {

                widthValue = helpers.getUnitValue(width);
                heightValue = helpers.getUnitValue(height);

                widthType = helpers.getUnitStyle(width);
                heightType = helpers.getUnitStyle(height);

                if (widthType === 'pt' || widthType === 'px') {
                    widthInput.attr('step', 1);
                } else {
                    widthInput.attr('step', 0.1);
                }

                if (heightType === 'pt' || heightType === 'px') {
                    heightInput.attr('step', 1);
                } else {
                    heightInput.attr('step', 0.1);
                }

                widthValue = parseFloat(widthValue).toFixed(this.formatTo);
                heightValue = parseFloat(heightValue).toFixed(this.formatTo);
            }

            widthInput.val(widthValue);
            heightInput.val(heightValue);

            widthInput.attr('value', widthValue);
            heightInput.attr('value', heightValue);

            this.selectKendoDropdownItem('units', 'backgroundSizeWidth', widthType);
            this.selectKendoDropdownItem('units', 'backgroundSizeHeight', heightType);

            currentWidget[propGroup].backgroundSizeWidth = widthValue + widthType;
            currentWidget[propGroup].backgroundSizeHeight = heightValue + heightType;
        }

        this.selectKendoDropdownItem(propGroup, propKey, val);
    }

    _setMaskSizeKendoInputs(widget) {
        if (widget.styles['-webkit-mask-sizeHeight'] === 'auto') {
            helpers.disableKendoInput('-webkit-mask-sizeHeight');
        } else {
            helpers.enableKendoInput('-webkit-mask-sizeHeight');
        }

        if (widget.styles['-webkit-mask-sizeWidth'] === 'auto') {
            helpers.disableKendoInput('-webkit-mask-sizeWidth');
        } else {
            helpers.enableKendoInput('-webkit-mask-sizeWidth');
        }
    }

    _setWebkitMaskRepeatOrSize($this, currentWidget, propKey, propGroup, val) {
        if (val === 'auto' || propKey === 'backgroundRepeat') {

            var width = currentWidget.styles['-webkit-mask-sizeWidth'];
            var height = currentWidget.styles['-webkit-mask-sizeHeight'];

            var widthInput = $('input[data-property-key="-webkit-mask-sizeWidth"]');
            var heightInput = $('input[data-property-key="-webkit-mask-sizeHeight"]');

            var widthValue;
            var heightValue;

            var heightType;
            var widthType;

            if (width === 'auto' || height === 'auto') {

                widthType = 'px';
                heightType = 'px';

                var unitGeoWidth = currentWidget.geometry.width;
                var unitGeoHeight = currentWidget.geometry.height;

                widthValue = unitsConvertor.convertUnitsToPixel(currentWidget.id, unitGeoWidth, propKey);
                heightValue = unitsConvertor.convertUnitsToPixel(currentWidget.id, unitGeoHeight, propKey);

                widthInput.attr('step', 1);
                heightInput.attr('step', 1);

                widthValue = parseInt(widthValue);
                heightValue = parseInt(heightValue);

            } else {

                widthValue = helpers.getUnitValue(width);
                heightValue = helpers.getUnitValue(height);

                widthType = helpers.getUnitStyle(width);
                heightType = helpers.getUnitStyle(height);

                if (widthType === 'pt' || widthType === 'px') {
                    widthInput.attr('step', 1);
                } else {
                    widthInput.attr('step', 0.1);
                }

                if (heightType === 'pt' || heightType === 'px') {
                    heightInput.attr('step', 1);
                } else {
                    heightInput.attr('step', 0.1);
                }

                widthValue = parseFloat(widthValue).toFixed(this.formatTo);
                heightValue = parseFloat(heightValue).toFixed(this.formatTo);
            }

            widthInput.val(widthValue);
            heightInput.val(heightValue);

            widthInput.attr('value', widthValue);
            heightInput.attr('value', heightValue);

            this.selectKendoDropdownItem('units', '-webkit-mask-sizeWidth', widthType);
            this.selectKendoDropdownItem('units', '-webkit-mask-sizeHeight', heightType);

            currentWidget[propGroup]['-webkit-mask-sizeWidth'] = widthValue + widthType;
            currentWidget[propGroup]['-webkit-mask-sizeHeight'] = heightValue + heightType;

            var finalWebkitMaskSize = widthValue + widthType + ' ' + heightValue + heightType;

            return finalWebkitMaskSize;
        }

        this.selectKendoDropdownItem(propGroup, propKey, val);
        return val;
    }

    setWebkitMaskSizeWidthHeight(widgetId,
                                 propKey, value) {
        var bgSizeWidthInput = $('input[data-property-key="-webkit-mask-sizeWidth"]');
        var bgSizeHeightInput = $('input[data-property-key="-webkit-mask-sizeHeight"]');

        var bgSizeWidth = bgSizeWidthInput.val();
        var bgSizeHeight = bgSizeHeightInput.val();

        var bgSizeWidthUnits = $('select[data-property-key="-webkit-mask-sizeWidth"]').find(':selected').val();
        var bgSizeHeightUnits = $('select[data-property-key="-webkit-mask-sizeHeight"]').find(':selected').val();

        // check for undo redo
        if (value !== undefined) {
            var unitType = helpers.getUnitStyle(value);
            var unitValue = helpers.getUnitValue(value);

            if (unitType === 'px') {
                unitValue = parseInt(unitValue);
            } else {
                unitValue = parseFloat(unitValue).toFixed(this.formatTo);
            }
        }

        var width = unitsConvertor.convertUnitsToPixel(widgetId, bgSizeWidth + bgSizeWidthUnits, '-webkit-mask-sizeWidth');
        var height = unitsConvertor.convertUnitsToPixel(widgetId, bgSizeHeight + bgSizeHeightUnits, '-webkit-mask-sizeHeight');

        // concatenate webkitMaskSizeHeight and
        // backgroundSizeWidth to build valid webkitMaskSize
        // property
        var val = width + ' ' + height;

        // save original properties group and value
        // needed for undo redo
        this.saveProperties(widgetId, 'styles', propKey, value);
        // apply visual webkitMaskSize property

        this.applyProperties(widgetId, 'css property', '-webkit-mask-size', val);
    }

    setBackgroundSizeWidthHeight(widgetId,
                                 propKey, value) {
        var bgSizeWidthInput = $('input[data-property-key="backgroundSizeWidth"]');
        var bgSizeHeightInput = $('input[data-property-key="backgroundSizeHeight"]');

        var bgSizeWidth = bgSizeWidthInput.val();
        var bgSizeHeight = bgSizeHeightInput.val();

        var bgSizeWidthUnits = $('select[data-property-key="backgroundSizeWidth"]').find(':selected').val();
        var bgSizeHeightUnits = $('select[data-property-key="backgroundSizeHeight"]').find(':selected').val();

        // check for undo redo
        if (value !== undefined) {
            var unitType = helpers.getUnitStyle(value);
            var unitValue = helpers.getUnitValue(value);

            if (unitType === 'px') {
                unitValue = parseInt(unitValue);
            } else {
                unitValue = parseFloat(unitValue).toFixed(this.formatTo);
            }
        }

        var width = unitsConvertor.convertUnitsToPixel(widgetId, bgSizeWidth + bgSizeWidthUnits, 'backgroundSizeWidth');
        var height = unitsConvertor.convertUnitsToPixel(widgetId, bgSizeHeight + bgSizeHeightUnits, 'backgroundSizeHeight');

        // concatenate backgroundSizeHeight and
        // backgroundSizeWidth to build valid backgroundSize
        // property
        var val = width + ' ' + height;

        // save original properties group and value
        // needed for undo redo
        this.saveProperties(widgetId, 'styles', propKey, value);
        // apply visual backgroundSize property

        this.applyProperties(widgetId, 'css property', 'backgroundSize', val);
    }

    updateSelection(oldId, newId) {
        this.TEMPLATE_CACHE.set(oldId, newId);

        for (let i = 0; i < this.currentElementsSelection.length; i++) {
            if (this.currentElementsSelection[i] === oldId) {
                this.currentElementsSelection[i] = newId;
            }
        }

        for (let i = 0; i < this.currentParentElementsSelection.length; i++) {
            if (this.currentParentElementsSelection[i] === oldId) {
                this.currentParentElementsSelection[i] = newId;
            }
        }
    }

    _showValidationWarning(message, callback: Function) {
        //prevents the editor from dragging
        $('#top-scene-holder').trigger('mouseup');
        //show warning and reset the current value
        vex.dialog.open({
            message: message,
            contentClassName: 'modal-about',
            buttons: [$.extend({}, vex.dialog.buttons.YES, {text: 'OK'})],
            callback: callback
        });
    }

    _setId($this, currentWidget, currentWidgetId, propGroup, propKey, value) {
        let val;
        const originalPropGroup = propGroup;
        const saveType = 'id property';

        if (value !== undefined) {
            // undo - redo
            val = value;
            if (currentWidgetId === this.getSelectedWidgetId()) {
                $('[data-property-set="' + propGroup + '"][data-property-key="' + propKey + '"]').val(val);
            }
        } else {
            val = $this.val();
        }

        let isValid = helpers.validateId(val);
        let notOnScene = this.mappedWidgets[val] === undefined;
        if (val === '' || val === ' ') {
            this._showValidationWarning(Enums.warnMessages.idValidationWarning('ID'), () => {
                resetId();
            });
        } else if (isValid && notOnScene) {
            this.updateSelection(currentWidgetId, val);
            this.saveProperties(currentWidgetId, saveType, propKey, val, originalPropGroup);
            this.applyProperties(currentWidgetId, saveType, propKey, val);
        } else if (isValid && !notOnScene) {
            this._showValidationWarning(Enums.warnMessages.idNamesCollisionWarning, () => {
                resetId();
            });
        } else if (!isValid) {
            this._showValidationWarning(Enums.warnMessages.invalidCharacter, () => {
                resetId();
            });
        }

        function resetId() {
            const idToRevert = currentWidget.id;
            $(`[data-property-set="${propGroup}"][data-property-key="${propKey}"]`).val(idToRevert);
            $(`[data-timeline-info-widget-id="${currentWidgetId}"] .info-widget-name`).val(idToRevert);
        }
    }

    _setFilter($this, currentWidget, currentWidgetId, propGroup, propKey, value) {
        var sliderVal;
        var _this = this;

        if (value !== undefined) {
            // undo - redo
            if (propKey !== 'dropShadowColor') {
                sliderVal = parseFloat(value);
            } else {
                sliderVal = value;
            }
        } else {
            sliderVal = $this.val();
        }

        this.setPropertiesBarValue(currentWidgetId, propGroup, propKey, sliderVal);

        var originalPropGroup = propGroup;
        var el = $('#' + currentWidgetId);
        var saveType = '-webkit-filter';

        if (propKey === 'blur') {
            sliderVal += 'px';
        }

        if (propKey === 'hue-rotate') {
            sliderVal += 'deg';
        }

        if (propKey === 'dropShadowBlur' || propKey === 'dropShadowColor' ||
            propKey === 'dropShadowX' || propKey === 'dropShadowY') {
            this.saveProperties(currentWidgetId, saveType, propKey, sliderVal, originalPropGroup);
        }

        helpers.splitCssStringProperties(currentWidgetId, el[0].style[propGroup], 'webkitFilter', propKey, sliderVal, function (val) {
            if (propKey !== 'dropShadowBlur' && propKey !== 'dropShadowColor' &&
                propKey !== 'dropShadowX' && propKey !== 'dropShadowY') {
                _this.saveProperties(currentWidgetId, saveType, propKey, sliderVal, originalPropGroup);
            }
            _this.applyProperties(currentWidgetId, 'css property', '-webkit-filter', val);
        });
    }

    /**
     * Set boxshadow property
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {object} $this
     * @param {object} currentWidget
     * @param {string} currentWidgetId
     * @param {string} propGroup
     * @param {string} propKey
     * @param {string|undefined} value - *optional
     * @private
     */
    _setBoxShadow($this, currentWidget, currentWidgetId, propGroup, propKey, value) {
        var valueToSave;
        if (value !== undefined) {
            // undo-redo
            valueToSave = value;

            this.setPropertiesBarValue(currentWidgetId, propGroup,
                propKey, value);
        } else {
            valueToSave = $this.val();
        }

        var saveType = 'boxShadow';
        var originalPropGroup = propGroup;
        var widget = this.mappedWidgets[currentWidgetId].widget;

        if (valueToSave === 'remove' && !value) {
            this._undoCreationStepsLength = 6;
            var $horizontalLength = $('[data-property-set="' + propGroup +
                '"][data-property-key="horizontalLength"]');
            var $verticalLength = $('[data-property-set="' + propGroup +
                '"][data-property-key="verticalLength"]');
            var $blurRadius = $('[data-property-set="' + propGroup +
                '"][data-property-key="blurRadius"]');
            var $spreadRadius = $('[data-property-set="' + propGroup +
                '"][data-property-key="spreadRadius"]');

            this.saveProperties(currentWidgetId, saveType, 'horizontalLength', '0', originalPropGroup);
            this.saveProperties(currentWidgetId, saveType, 'verticalLength', '0', originalPropGroup);
            this.saveProperties(currentWidgetId, saveType, 'blurRadius', '0', originalPropGroup);
            this.saveProperties(currentWidgetId, saveType, 'spreadRadius', '0', originalPropGroup);
            this.saveProperties(currentWidgetId, saveType, 'color', '#000', originalPropGroup);
            this.saveProperties(currentWidgetId, saveType, propKey, 'remove', originalPropGroup);

            this.applyProperties(currentWidgetId, 'css property', 'boxShadow', '');

            // reset sliders
            $horizontalLength.val(0);
            $verticalLength.val(0);
            $blurRadius.val(0);
            $spreadRadius.val(0);
        } else {

            this.saveProperties(currentWidgetId, saveType, propKey, valueToSave, originalPropGroup);
            var boxShadow = this.buildBoxShadowProperty(widget);
            this.applyProperties(currentWidgetId, 'css property', 'boxShadow', boxShadow);
        }
    }

    _setPerspectiveOrigin($this, currentWidget: IWidget, currentWidgetId, propGroup, propKey, value) {
        let unitStyle: string;
        let inputValue: string;

        const saveType = 'perspective-origin';
        let remoteTriggerd: boolean;

        if (value !== undefined) {
            // Comming from undo/redo or from setVisual settings //
            remoteTriggerd = true;

            if (value.split(' ').length > 1) {
                let properties;
                let keys: string[];

                var propValues = value.split(' ');
                keys = ['perspective-origin-x', 'perspective-origin-y'];
                properties = {
                    'perspective-origin-x': propValues[0],
                    'perspective-origin-y': propValues[1]
                };

                for (var i = 0; i < keys.length; i++) {
                    propKey = keys[i];
                    value = properties[propKey];
                    this.setPropertiesBarValue(currentWidgetId, propGroup, propKey, value);
                    currentWidget[propGroup][propKey] = value;
                }

                this.exportScene();
                return;
            }
            inputValue = value;
        } else {
            remoteTriggerd = false;
            inputValue = helpers.patchInputNumberBehavior({
                $this: $this,
                currentWidget: currentWidget,
                propGroup: propGroup,
                propKey: propKey
            });
            unitStyle = $('select[data-property-set="units"][data-property-key="' + propKey + '"]').find(':selected').val() || '';
            inputValue += unitStyle;
        }

        this.saveProperties(currentWidgetId, saveType, propKey, inputValue, propGroup);
        const transformStyles = inputValue;
        propGroup = propKey;

        this.applyProperties(currentWidgetId, 'css property', propGroup, transformStyles);
        if (remoteTriggerd) {
            this.setPropertiesBarValue(currentWidgetId, saveType, propKey, inputValue);
        }
    }

    _setTransformOrigin($this, currentWidget: IWidget, currentWidgetId, propGroup, propKey, value) {
        let unitStyle: string;
        let inputValue: string;

        const saveType = 'transform-origin';
        let remoteTriggerd: boolean;

        if (value !== undefined) {
            // Comming from undo/redo or from setVisual settings //
            remoteTriggerd = true;

            if (value.split(' ').length > 1) {
                let properties;
                let keys: string[];

                var propValues = value.split(' ');
                keys = ['transform-origin-x', 'transform-origin-y'];
                properties = {
                    'transform-origin-x': propValues[0],
                    'transform-origin-y': propValues[1]
                };

                for (var i = 0; i < keys.length; i++) {
                    propKey = keys[i];
                    const valueOrigin = properties[propKey];
                    this.setPropertiesBarValue(currentWidgetId, propGroup, propKey, valueOrigin);
                    if (!currentWidget[propGroup]) {
                        currentWidget[propGroup] = {};
                    }
                    currentWidget[propGroup][propKey] = valueOrigin;
                    this.applyProperties(currentWidgetId, 'css property', propKey, valueOrigin);
                }


                this.computeSelectCorners($(`#${currentWidgetId}`));
                this.exportScene();

                return;
            }
            inputValue = value;
        } else {
            remoteTriggerd = false;
            inputValue = helpers.patchInputNumberBehavior({
                $this: $this,
                currentWidget: currentWidget,
                propGroup: propGroup,
                propKey: propKey
            });
            unitStyle = $('select[data-property-set="units"][data-property-key="' + propKey + '"]').find(':selected').val() || '';
            inputValue += unitStyle;
        }

        this.saveProperties(currentWidgetId, saveType, propKey, inputValue, propGroup);
        const transformStyles = inputValue;
        propGroup = propKey;

        this.applyProperties(currentWidgetId, 'css property', propGroup, transformStyles);
        if (remoteTriggerd) {
            this.setPropertiesBarValue(currentWidgetId, saveType, propKey, inputValue);
        }
        this.computeSelectCorners($(`#${currentWidgetId}`));
    }

    _setTransform($this, currentWidget: IWidget, currentWidgetId, propGroup, propKey, value) {
        let unitStyle: string;
        let inputValue: string;

        const saveType = 'transform';
        let remoteTriggerd: boolean;

        if (value !== undefined) {
            // Comming from undo/redo or from setVisual settings //
            remoteTriggerd = true;

            if (value.split(' ').length > 1) {
                let properties;
                let keys: string[];

                properties = helpers.createFilterPropertyGroup(value);
                keys = Object.keys(properties);

                for (var i = 0; i < keys.length; i++) {
                    propKey = keys[i];
                    value = properties[propKey];
                    this.setPropertiesBarValue(currentWidgetId, propGroup, propKey, value);
                    currentWidget[propGroup][propKey] = value;
                }

                this.exportScene();
                return;
            }
            inputValue = value;
        } else {
            remoteTriggerd = false;
            inputValue = helpers.patchInputNumberBehavior({
                $this: $this,
                currentWidget: currentWidget,
                propGroup: propGroup,
                propKey: propKey
            });
            unitStyle = $('select[data-property-set="units"][data-property-key="' + propKey + '"]').find(':selected').val() || '';
            inputValue += unitStyle;
        }

        this.saveProperties(currentWidgetId, saveType, propKey, inputValue, propGroup);
        const transformStyles = helpers.buildTransformStyles(currentWidget);

        this.applyProperties(currentWidgetId, 'css property', propGroup, transformStyles);
        if (remoteTriggerd) {
            this.setPropertiesBarValue(currentWidgetId, saveType, propKey, inputValue);
        }

        this.transformChildren(currentWidget);
    }

    transformChildren(currentWidget) {
        if ((couiEditor.EXPORTING_WIDGET ||
                couiEditor.EXPORTING_COMPONENT) ||
            (this.getRedoUndoPrimaryState() === 'undo' ||
                this.getRedoUndoPrimaryState() === 'redo') ||
            this._sceneActionState.pasteWidget
        ) {
            return;
        }
        let children = currentWidget.children;
        let childrenLength = children.length;

        this.transform.transform(currentWidget);

        if (childrenLength) {
            for (let i = 0; i < childrenLength; i++) {
                if (!this.mappedWidgets[children[i].id]) {
                    return;
                }
                this.transform.transform(children[i]);
            }
        }
    }

    /**
     * Edit animation class name property
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {string} widgetId
     * @param {string} oldClassName
     * @param {string} newClassName
     */
    editAnimationClassName(widgetId, oldClassName, newClassName) {
        if (newClassName.trim() === '') {
            this.Timeline.removeAnimationClassFromWidget(widgetId, oldClassName, true);
            return;
        } else if (!helpers.validateAnimationName(newClassName)) {
            let _this = this;
            this._showValidationWarning(Enums.warnMessages.animationNameNotAllowed, () => {
                let oldName = _this.mappedWidgets[widgetId].widget.className;
                let widgetTrack = $(`.widget-timeline-holder[data-timeline-info-widget-id="${widgetId}"]`);
                widgetTrack.find('.animation-class-name-input').val(oldName);
            });
            return;
        }

        var animationClassNames = this.scene.animationClasses;
        var widgetsAnimations = this.scene.animations;
        animationClassNames.renameProperty(oldClassName, newClassName);
        animationClassNames[newClassName].className = newClassName;
        this._sceneActionState.editAnimationClass = {
            oldClassName: oldClassName,
            newClassName: newClassName
        };

        var timelineClassNameEditLen = helpers.getFromTimeline('.info-class-name [data-old-animation-class="' + oldClassName + '"]').length;
        this._undoCreationStepsLength = timelineClassNameEditLen;

        for (var widgetAnimId in widgetsAnimations) {
            if (widgetsAnimations[widgetAnimId].className === oldClassName) {
                widgetsAnimations[widgetAnimId][newClassName] = animationClassNames[newClassName];
                widgetsAnimations[widgetAnimId].className = newClassName;

                var widget = this.mappedWidgets[widgetAnimId].widget;
                var fullClassNames = widget.className;
                var newClassNames = helpers.replaceAnimationName(fullClassNames, oldClassName, newClassName);

                delete widgetsAnimations[widgetAnimId][oldClassName];
                this.setClassName(null, widgetAnimId, newClassNames, false);

                this.Timeline.setNewAnimationClass(widgetAnimId, newClassName, oldClassName);
                this.Timeline.setOldAnimationClass(widgetAnimId, newClassName, oldClassName);
            }
        }

        this._sceneActionState.editAnimationClass = null;
    }

    /**
     * Switch animation class name property
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {string} widgetId
     * @param {string} oldClassName
     * @param {string} newClassName
     * @param {string} fullClasssValue
     */
    switchAnimationClassName(widgetId, oldClassName, newClassName, fullClassValue, initial = false) {

        var animationClassNames = this.scene.animationClasses;
        var widgetAnimations = this.scene.animations;
        var fullClassNames = fullClassValue;
        widgetAnimations[widgetId] = widgetAnimations[widgetId] || {};
        widgetAnimations[widgetId].id = widgetId;
        widgetAnimations[widgetId].className = newClassName;
        widgetAnimations[widgetId][newClassName] = animationClassNames[newClassName] || {};

        // comes from timeline dropdown menu
        if (!fullClassNames) {
            fullClassNames = this.mappedWidgets[widgetId].widget.className;
            var newClassNames = helpers.replaceAnimationName(fullClassNames, oldClassName, newClassName);

            if (!this._sceneActionState.switchAnimationClass) {
                this._skipUndoRedoSteps = 1;
            }

            this.setClassName(null, widgetId, newClassNames, true);
        } else {
            // comes from properties bar className input field
            this.saveProperties(widgetId, 'class property', 'className', fullClassNames, 'element-class');
        }

        if (oldClassName !== '' && oldClassName !== newClassName) {
            this.Timeline.removeClassNameFromDOM(widgetId, oldClassName);
            if (widgetAnimations[widgetId].className) {
                delete widgetAnimations[widgetId][oldClassName];
            } else {
                delete widgetAnimations[widgetId];
            }
        }

        if (!initial && oldClassName !== newClassName) {
            this.Animations.loadKeyframes(animationClassNames);
        }

        this.Timeline.setInputClassName(widgetId, newClassName, oldClassName);

        var $widgetInputLine = helpers.getFromTimeline('[data-timeline-info-widget-id="' + widgetId + '"] .info-class-name');
        if (!$widgetInputLine.is(':visible')) {
            $widgetInputLine.show();
            helpers.getFromTimeline('.widget-name-line[data-widget-id="' + widgetId + '"] .keyframe-line-class-name').show();
            helpers.getFromTimeline('[data-timeline-info-widget-id="' + widgetId + '"] .animation-class-name-input').show();
        } else if (newClassName === undefined) {
            $widgetInputLine.hide();
            helpers.getFromTimeline('.widget-name-line[data-widget-id="' + widgetId + '"] .keyframe-line-class-name').hide();
            helpers.getFromTimeline('[data-timeline-info-widget-id="' + widgetId + '"] .animation-class-name-input').hide();
        }

        this.exportScene();
    }

    /**
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {object} $this
     * @param {object} widget
     * @param {string} value
     * @param {bool} loopAnimations
     */
    setClassName($this, widgetId, value, loopAnimations) {
        var val;
        var propGroup = 'element-class';
        var propKey = 'className';
        var saveType = 'class property';
        var originalPropGroup = 'element-class';

        if (value !== undefined) {
            // undo - redo
            val = value;
            var $classInputField = $('[data-property-set="' + propGroup + '"][data-property-key="' + propKey + '"]');
            if (this.getSelectedWidgetId() === widgetId &&
                $classInputField.length > 0) {
                $classInputField.val(val);
            }
        } else {
            val = $this.val();
        }

        if (loopAnimations) {
            val = this.loopAnimationClasses(widgetId, val);
        } else {
            this.saveProperties(widgetId, saveType, propKey, val, originalPropGroup);
        }

        this.applyProperties(widgetId, saveType, propKey, val);
    }

    /**
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {string} classNames - example `pesho class1 class3`
     * @param {string} widgetId
     * @return {string} classNames - cleaned class names
     */
    loopAnimationClasses(widgetId, classNames) {
        var appliedClassName = '';
        var animationWidgets = this.scene.animations;
        if (animationWidgets[widgetId]) {
            for (var className in animationWidgets[widgetId]) {
                if (animationWidgets[widgetId][className]) {
                    if (animationWidgets[widgetId][className].className) {
                        appliedClassName = className;
                    }
                }
            }
        }

        if (classNames === '' || !classNames) {
            this.Timeline.removeAnimationClassFromWidget(widgetId, appliedClassName, false);
        } else {
            var classNameExist = helpers.doesClassNameExist(classNames, appliedClassName);
            var newClassNames = helpers.getAnimationClassesFromClassNames(classNames);
            if (!classNameExist && appliedClassName !== '') {
                this.switchAnimationClassName(widgetId, appliedClassName, newClassNames[0], classNames);
                return;
            }

            var len = newClassNames.length;
            if (len > 0) {
                if (len !== 1) {
                    // check if the class names string contains more than one animation class and if it has, removes the others
                    classNames = helpers.removeMoreThanOneAnimationClasses(newClassNames, classNames);
                }

                this.switchAnimationClassName(widgetId, appliedClassName, newClassNames[0], classNames);
            } else {
                this.saveProperties(widgetId, 'class property', 'className', classNames, 'element-class');
            }
        }

        return classNames;
    }

    _setText($this, currentWidget, currentWidgetId, propGroup, propKey, value) {
        let val;
        const textInput = $('[data-property-set="' + propGroup + '"][data-property-key="' + propKey + '"]');
        const saveType = 'set text';
        const originalPropGroup = propGroup;

        if (value !== undefined) {
            // undo - redo
            const {start, end} = helpers.getTextSelection(textInput[0]);

            val = value;
            textInput.val(val);
            textInput[0].setSelectionRange(start, end);
        } else {
            val = $this.val();
        }

        this.saveProperties(currentWidgetId, saveType, propKey, val, originalPropGroup);
        this.applyProperties(currentWidgetId, saveType, propKey, val);
    }

    _setChecked($this, currentWidget, currentWidgetId, propGroup, propKey, value) {
        var saveType = 'checked';
        var originalPropGroup = propGroup;
        var val;
        if (value !== undefined) {
            // undo - redo
            val = value;

            // val can be on or off if propKey is -coherent-layer-clip-aa // to preset the checkbox we need to change it to true or false
            if (val === 'on') {
                val = true;
            } else if (val === 'off') {
                val = false;
            }

            $('[data-property-set="' + propGroup + '"][data-property-key="' + propKey + '"]').prop('checked', val);
        } else {
            val = $this.is(':checked');
        }

        if (propKey === '-coherent-layer-clip-aa') {
            if (val) {
                val = 'on';
            } else {
                val = 'off';
            }
        }

        this.saveProperties(currentWidgetId, saveType, propKey, val, originalPropGroup);
        this.applyProperties(currentWidgetId, saveType, propKey, val);
    }

    setPropertiesBarValue(currentWidgetId, propGroup, propKey, value) {
        var units = helpers.getUnitStyle(value);
        var newValue;
        var unitNumber;

        if (units === 'auto' || units === 'relative') {
            newValue = units;
        } else if (propKey === 'rotate' || helpers.isTransformProperty(propKey)) {
            if (units === 'rad') {
                unitNumber = helpers.getUnitValue(value.toString());
                newValue = (unitNumber).toFixed(1);
            } else {
                newValue = helpers.getUnitValue(value.toString());
            }
        } else if (propKey === 'boxShadowColor' || propKey === 'dropShadowColor') {
            newValue = helpers.getColorValue(value);
        } else if (units === 'px' || units === '' || propGroup === 'boxShadow' || propKey === 'fontStyle') {
            newValue = value;
        } else {
            unitNumber = helpers.getUnitValue(value);
            newValue = parseFloat(unitNumber).toFixed(this.formatTo) + units;
        }

        var len = this.currentElementsSelection.length;
        for (var i = 0; i < len; i++) {
            if (this.currentElementsSelection[i] === currentWidgetId) {
                switch (propGroup) {
                    case 'font':
                        this.setFontSizeInput(value);
                        break;
                    case '-webkit-filter':
                        this.setFilterInput(propGroup, propKey, newValue);
                        break;
                    case 'boxShadow':
                        this.setBoxShadowInput(propGroup, propKey, newValue);
                        break;
                    case 'backgroundColor':
                        this.setBackgroundInput(propGroup, propKey, newValue);
                        break;
                    case 'styles':
                        if (propKey === 'color') {
                            this.setColorInput(propGroup, propKey, newValue);
                        } else if (propKey === 'borderColor') {
                            this.setBorderColorInput(propGroup, propKey, newValue);
                        } else if (propKey === 'backgroundPositionX' || propKey === 'backgroundPositionY') {
                            if (helpers.isBackgroundPositionString(value)) {
                                $('select[data-property-set="units"][data-property-key="' + propKey + '"]').data('kendoDropDownList').value(value);
                                this.setUnitNumberToolbar(propKey, value);
                            } else {
                                this.setStyleInput(propGroup, propKey, newValue, units);
                                this.setUnitNumberToolbar(propKey, value);
                            }
                        } else {
                            this.setStyleInput(propGroup, propKey, newValue, units);
                        }
                        break;
                    case 'geometry':
                        this.setGeometryInput(propGroup, propKey, newValue);
                        break;
                    case 'transform':

                        this.setTransformInput(propGroup, propKey, newValue);
                        break;
                    case 'transform-origin':

                        this.setTransformOriginInput(propGroup, propKey, newValue);
                        break;
                    case 'perspective-origin':

                        this.setPerspectiveOriginInput(propGroup, propKey, newValue);
                        break;
                }
            }
        }
    }

    setFontSizeInput(value) {
        var $fontSize = $('select[data-property-set="units"][data-property-key="fontSize"]').data('kendoDropDownList');

        if ($fontSize) {
            this.setUnitNumberToolbar('fontSize', value);
            var units = helpers.getUnitStyle(value);
            $fontSize.value(units);
        }
    }

    setBackgroundInput(group, key, value) {
        var $colorPicker = $('#background-picker');
        if ($colorPicker.length > 0) {
            $colorPicker.data('kendoColorPicker').value(value);
        }
    }

    setBorderColorInput(group, key, value) {
        var $colorPicker = $('#borderColor-picker');
        if ($colorPicker.length > 0) {
            $colorPicker.data('kendoColorPicker').value(value);
        }
    }

    setColorInput(group, key, value) {
        var $colorPicker = $('#color-picker');
        if ($colorPicker.length > 0) {
            $colorPicker.data('kendoColorPicker').value(value);
        }
    }

    setBoxShadowInput(group, key, value) {
        if (key === 'insetOutset') {
            // set checkbox
            $('[data-property-set="' + group + '"][value="' + value + '"]').prop('checked', true);
        } else if (key === 'color') {
            var $colorPicker = $('#' + group + '-picker');
            if ($colorPicker.length > 0) {
                $colorPicker.data('kendoColorPicker').value(value);
            }
        } else {
            // set slider
            value = parseFloat(value);
            $('[data-property-set="' + group + '"][data-property-key="' + key + '"]').val(value);
        }
    }

    setTransformInput(group, key, value) {
        $('input[data-property-set="' + group + '"][data-property-key="' +
            key + '"]').val(value);
    }

    setTransformOriginInput(group, key, value) {
        $('input[data-property-set="' + group + '"][data-property-key="' +
            key + '"]').val(value);
    }

    setPerspectiveOriginInput(group, key, value) {
        $('input[data-property-set="' + group + '"][data-property-key="' +
            key + '"]').val(value);
    }

    setFilterInput(group, key, value) {
        // when resetting the filter, value is passed as number
        let stringValue = value.toString();

        if (key === 'dropShadowColor') {
            $('#dropShadow-picker[data-property-set="' + group + '"][data-property-key="' +
                key + '"]').data('kendoColorPicker').value(stringValue);
        } else {
            let formatedValue: any = isNaN(Number(stringValue)) ? stringValue.match(/([-|+|#]?\d*\.?\d+)/g)[0] : stringValue;
            const pointEnd = formatedValue.charAt(stringValue.length - 1) === '.';

            if (!pointEnd) {
                formatedValue = parseFloat(formatedValue);
            }

            $('[data-property-set="' + group + '"][data-property-key="' + key + '"]').val(formatedValue);
            $('[data-property-set="' + group + '"][data-property-key="' + key + '"]').attr('value', formatedValue);
        }
    }

    /**
     * Method setting the data binding input of the kendoElements.
     * @function setDataBindingInput
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {string} group - property group to set in the kendo input
     * @param {string} key - property key of the group to set in the kendo input
     * @param {string} value - value to set the input to
     */
    setDataBindingInput(group, key, value) {
        key = helpers.translateDataBindNames(key, true);
        $('[data-property-set="' + group + '"][data-property-key="' +
            key + '"]').val(value);
    }

    setGeometryInput(group, key, value) {
        var $kendoSelect = $('select[data-property-set="units"][data-property-key="' + key + '"]').data('kendoDropDownList');

        if (value !== 'auto') {

            $('[data-property-set="' + group + '"][data-property-key="' + key + '"]').val(parseFloat(value));
            var units = helpers.getUnitStyle(value);

            if ($kendoSelect) {
                $kendoSelect.value(units);
            }

        } else {

            $('[data-property-set="' + group + '"][data-property-key="' +
                key + '"]').val(0);

            if ($kendoSelect) {
                $kendoSelect.value('auto');
            }
        }
    }

    setStyleInput(group, key, value, units) {
        let $kendoSelect;

        if (key === 'fontWeight' || key === 'fontStyle') {
            $kendoSelect = $('select[data-property-set="styles"][data-property-key="' + key + '"]').data('kendoDropDownList');
            if ($kendoSelect) {
                if (units.trim() !== '') {
                    $kendoSelect.value(units);
                } else {
                    $kendoSelect.value(value);
                }
            }
        } else if (key === 'perspective-origin-x' || key === 'perspective-origin-y') {
            $('input[data-property-set="' + group + '"][data-property-key="' +
                key + '"]').val(value);
        } else {
            $kendoSelect = $('select[data-property-set="units"][data-property-key="' + key + '"]').data('kendoDropDownList');
            if ($kendoSelect) {
                $kendoSelect.value(units);
            }

            $('input[data-property-set="' + group + '"][data-property-key="' +
                key + '"]').val(parseFloat(value));
        }
    }

    buildEngineEventArray($this, formClass) {

        var formdata = $this.parents('.events-wrap')
                .find('.form-' + formClass)
                .serializeArray(),
            formdataLen = formdata.length,
            paramData = [];

        for (var i = 0; i < formdataLen; i++) {
            paramData.push(formdata[i].value);
        }

        return paramData;
    }

    /**
     * Attaches on click handlers to the 'reset' filters buttons
     *
     * @param $removeButtons {JQuery} - elements to which to attach the event listeners
     * @param widget {Object}
     *
     * */
    initRemoveFilterPropsHandlers($removeButtons: JQuery, widget: Object) {
        $removeButtons.off('click');
        $removeButtons.on('click', {widget: widget}, (event) => {
            let key = $(event.currentTarget).attr('data-type');
            this.setFiltersDefaults(event, key);
        });
    }

    initPropertiesBarHandlers(widget, propertiesBarId) {
        const _this = this;
        const $propertiesType = $('#' + propertiesBarId);
        const $propertyInputs = $propertiesType.find('input[type="number"], input[type="text"]');

        //TODO: enable this for all remove buttons, not only for .remove-filter-property
        let $removeButtons = $propertiesType.find('span.remove-filter-property, .dismiss-filters');
        this.initRemoveFilterPropsHandlers($removeButtons, widget);

        _this.addFontClickHandler();

        $propertyInputs.off('input paste');
        $propertyInputs.on('input paste', action);

        // In case of scene save when a property key is active,
        // validate the input value and apply if valid
        $propertyInputs.off('keyup');
        $propertyInputs.on('keyup', function (event) {
            if (event.keyCode === Enums.Keys.esc || event.keyCode === Enums.Keys.enter) {
                $(event.target).blur();
            }
        });

        $propertyInputs.off('focusout');
        $propertyInputs.on('focusout', function (event) {
            if (!event.originalEvent) {
                return;
            }

            const value = this.value;
            const propGroup: string = $(this).attr('data-property-set');
            const propKey: string = $(this).attr('data-property-key');
            let oldValue = helpers.getWidgetState(widget, propKey, propGroup);

            if (oldValue === value) {
                return;
            }

            _this._sceneActionState.primaryAction = 'new action';
            _this._setProperties(widget, $(this));
        });

        $propertiesType.find('[type="radio"], [type="checkbox"]').bind('change', directApplyAction);
        $propertiesType.find('select').bind('change', directApplyAction);
        $propertiesType.find('input[type="range"]').bind('mouseup', directApplyAction);
        $propertiesType.find('input[type="range"]').bind('input', directApplyAction);

        // attach animation keyframe handlers
        this.Animations.attachKeyframeHandlers('#' + propertiesBarId);

        $propertiesType.find('.add-js-code').on('click', function () {
            const propGroup = $(this).attr('data-property-set');
            const propKey = $(this).attr('data-property-key');
            const val = _this.mappedWidgets[widget.id].widget.events[propKey].javascriptFunction;
            const Events = Enums.SavePropertiesTypes.Events;

            _this.createModalJsEditor(propKey, Events.Local.javascript, val, propGroup);
        });

        // switch dark and white arrows on focus input numbers
        this.inputNumbersFocusInOutHandler(propertiesBarId);

        function action(e: KeyboardEvent) {
            const value: string = this.value;
            const propKey = this.getAttribute('data-property-key');
            const propGroup = this.getAttribute('data-property-set');
            let oldValue = helpers.getWidgetState(widget, propKey, propGroup);

            if (inputTypeConfig[propKey].parse) {
                oldValue = String(parseFloat(oldValue));
            }

            if (oldValue === value || inputTypeConfig[propKey].validateOnBlur || value === '' || value.substr(-1) === '.') {
                return;
            }

            if (!_this.inputValidator.validate(value, propKey)) {
                this.value = oldValue;
                return;
            }

            _this._sceneActionState.primaryAction = 'new action';
            _this._setProperties(widget, $(this));
        }

        function directApplyAction(e) {
            _this._sceneActionState.primaryAction = 'new action';
            _this._setProperties(widget, $(this));
        }
    }

    /**
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     */
    blurOutTextInputs() {
        this.propertiesElBar.find('input[type="text"]').blur();
    }

    /**
     * Set kendo dropdown item in properties bar
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param propGroup
     * @param {string} propKey - special cases for backgrounsSize and webkitMaskSize
     * @param val
     */
    selectKendoDropdownItem(propGroup, propKey, val) {

        if (propKey === 'backgroundSize' || propKey === '-webkit-mask-size') {
            var splitVal = val.split(' ');
            if (splitVal.length > 1) {
                $('#background-properties-content .input-' + propKey + '.wrap-property.clearfix').css('display', 'block');
                this.setUnitNumberToolbar(propKey + 'Width', splitVal[0]);
                this.setUnitNumberToolbar(propKey + 'Height', splitVal[1]);
                this.selectKendoDropdownItem('units', propKey + 'Width', helpers.getUnitStyle(splitVal[0]));
                this.selectKendoDropdownItem('units', propKey + 'Height', helpers.getUnitStyle(splitVal[1]));
                val = 'auto';
            } else if (splitVal[0] !== 'auto') {
                $('#background-properties-content .input-' + propKey + '.wrap-property.clearfix').css('display', 'none');
            }
        }

        var $kendoSelect = $('select[data-property-set="' + propGroup + '"][data-property-key="' + propKey + '"]');

        if ($kendoSelect.length > 0) {
            // set dropdown unit by kendo dropdown api
            $kendoSelect.data('kendoDropDownList')
                .select(function (dataItem) {
                    return dataItem.value === val;
                });
        }
    }

    _setProperties(widget, $this, group?, key?, value?) {
        var _this = this;

        var propGroup = group || $this.attr('data-property-set');
        var propKey = key || $this.attr('data-property-key');

        // coming from undo-redo $this will be null
        if ($this) {
            const _value = value || $this.context.value;
            let oldValue = helpers.getWidgetState(widget, propKey, propGroup);
            const shouldValidate: boolean = this.inputValidator.shouldValidate($this);

            if (shouldValidate) {
                if (inputTypeConfig[propKey].parse) {
                    oldValue = String(parseFloat(oldValue));
                }

                if (!_this.inputValidator.validate(_value, propKey)) {
                    $this.val(oldValue);
                    return;
                }
            }
        }

        if (propGroup !== undefined) {
            var currentWidget = _this.mappedWidgets[widget.id].widget;
            var currentWidgetId = currentWidget.id;

            if (currentWidget[propGroup] === undefined &&
                propGroup !== 'units' &&
                propGroup !== 'font' &&
                propKey !== '-coherent-layer-clip-aa') {
                currentWidget[propGroup] = {};
            }

            if (propGroup === 'geometry') {
                _this._setGeometry($this, currentWidget, currentWidgetId, propGroup, propKey, value);
                _this.computeSelectCorners($('#' + currentWidgetId));
            }

            if (propGroup === 'image') {
                _this.insertImage(currentWidgetId, value);
            }

            if (propGroup === 'transform') {
                _this._setTransform($this, currentWidget, currentWidgetId, propGroup, propKey, value);
                _this.computeSelectCorners($('#' + currentWidgetId));
            }

            if (propGroup === 'perspective-origin') {
                _this._setPerspectiveOrigin($this, currentWidget, currentWidgetId, propGroup, propKey, value);
            }

            if (propGroup === 'transform-origin') {
                _this._setTransformOrigin($this, currentWidget, currentWidgetId, propGroup, propKey, value);
            }

            if (propGroup === '-webkit-filter') {
                _this._setFilter($this, currentWidget, currentWidgetId, propGroup, propKey, value);
            }

            if (propGroup === 'boxShadow') {
                _this._setBoxShadow($this, currentWidget, currentWidgetId, propGroup, propKey, value);
            }

            if (propGroup === 'events') {
                _this._setEvents($this, currentWidget, currentWidgetId, propGroup, propKey, value);
            }

            if (propGroup === 'dataBindings') {
                _this._setBindings($this, currentWidget, currentWidgetId, propGroup, propKey, value);
            }

            if (propGroup === 'units') {
                // workaround
                // if width or height is set to auto force auto for the both properties
                if ($this && (propKey === '-webkit-mask-sizeWidth' || propKey === '-webkit-mask-sizeHeight')) {
                    this.forceMaskSizeUnits($this, propKey, currentWidget);
                }
                this._setUnits($this, currentWidget, currentWidgetId, propGroup, propKey, value);
            }

            if (propGroup === 'font') {
                _this._setFonts($this, currentWidget, currentWidgetId, propGroup, propKey, value);
            }

            if (propGroup === 'element-id') {
                _this._setId($this, currentWidget, currentWidgetId, propGroup, propKey, value);
            }

            if (propGroup === 'element-text') {
                _this._setText($this, currentWidget, currentWidgetId, propGroup, propKey, value);
            }

            if (propGroup === 'element-class') {
                _this.setClassName($this, currentWidget.id, value, true);
            }

            if (propGroup === 'checked') {
                _this._setChecked($this, currentWidget, currentWidgetId, propGroup, propKey, value);
            }

            if (propGroup === 'attrs') {
                _this._setAttrs($this, currentWidget, currentWidgetId, propGroup, propKey, value);
            }

            if (propGroup === 'styles') {

                if (propKey === 'borderWidth' ||
                    propKey === 'padding') {
                    _this.computeSelectCorners($('#' + currentWidgetId));
                }

                if (propKey === 'backgroundSize' ||
                    propKey === 'backgroundSizeWidth' ||
                    propKey === 'backgroundSizeHeight') {
                    var incomingValue = value || $this[0].value;
                    var $inputBgSize = $('.input-' + propKey);

                    if (incomingValue === 'cover' ||
                        incomingValue === 'contain' ||
                        incomingValue === 'inherit') {
                        if ($inputBgSize.is(':visible')) {
                            $inputBgSize.hide();
                        }
                    } else {
                        $inputBgSize.show();
                    }
                }

                if (propKey === '-webkit-mask-size' ||
                    propKey === '-webkit-mask-sizeWidth' ||
                    propKey === '-webkit-mask-sizeHeight') {
                    var incomingMaskValue = value || $this[0].value;
                    var $inputMaskSize = $('.input-' + propKey);

                    if (incomingMaskValue === 'cover' ||
                        incomingMaskValue === 'contain' ||
                        incomingMaskValue === 'inherit') {
                        if ($inputMaskSize.is(':visible')) {
                            $inputMaskSize.hide();
                        }
                    } else {
                        $inputMaskSize.show();
                    }
                }

                _this._setStyles($this, currentWidget, currentWidgetId, propGroup, propKey, value);

                this._setMaskSizeKendoInputs(currentWidget);
            }
        }
    }

    /**
     * if width or height is set to auto force auto for the both properties
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {object} $this dropdown select element
     * @param {string} propKey
     * @param {IWidget} currentWidget
     * @return {void}
     */
    private forceMaskSizeUnits($this, propKey, currentWidget) {
        const oldUnitsWidth = currentWidget.styles['-webkit-mask-sizeWidth'];
        const oldUnitsHeight = currentWidget.styles['-webkit-mask-sizeHeight'];
        const selectedUnit = $this.find(':selected').val();
        if (selectedUnit === 'auto') {
            if (propKey === '-webkit-mask-sizeWidth') {
                this._undoCreationStepsLength += 1;
                let $dropDownHeight = $('[data-property-set="units"][data-property-key="-webkit-mask-sizeHeight"]');
                this._setProperties(currentWidget, $dropDownHeight, 'units', '-webkit-mask-sizeHeight', 'auto');
            } else if (propKey === '-webkit-mask-sizeHeight') {
                this._undoCreationStepsLength += 1;
                let $dropDownWidth = $('[data-property-set="units"][data-property-key="-webkit-mask-sizeWidth"]');
                this._setProperties(currentWidget, $dropDownWidth, 'units', '-webkit-mask-sizeWidth', 'auto');
            }
        } else if (oldUnitsWidth === 'auto' || oldUnitsHeight === 'auto') {
            this._undoCreationStepsLength += 2;
            let wSize = '100';
            let hSize = '100';
            let currentWSize = '0';
            let currentHSize = '0';
            if (selectedUnit === '%') {
                wSize = wSize + '%';
                hSize = hSize + '%';
            } else {
                currentWSize = $(`#${currentWidget.id}`)[0].style.width;
                currentHSize = $(`#${currentWidget.id}`)[0].style.height;
                if (selectedUnit === 'px') {
                    wSize = currentWSize;
                    hSize = currentHSize;
                } else {
                    wSize = unitsConvertor.convertPixelToUnit(currentWidget.id, currentWSize, selectedUnit, propKey);
                    hSize = unitsConvertor.convertPixelToUnit(currentWidget.id, currentHSize, selectedUnit, propKey);
                }
            }
            this._setProperties(currentWidget, null, 'styles', '-webkit-mask-sizeWidth', wSize);
            this._setProperties(currentWidget, null, 'styles', '-webkit-mask-sizeHeight', hSize);
        }
    }

    getStyleValue($wrapper, group, property) {
        if (property === 'color') {
            return this.getTextColorValue();
        } else if (property === 'borderColor') {
            return this.getBorderColorValue();
        } else if (property === 'borderWidth' ||
            property === 'borderTopLeftRadius' ||
            property === 'borderTopRightRadius' ||
            property === 'borderBottomLeftRadius' ||
            property === 'borderBottomRightRadius' ||
            property === 'perspective') {

            var numberValue = $wrapper.find('input[data-property-set="' + group + '"][data-property-key="' + property + '"]').val();
            var units = $wrapper.find('[data-property-set="units"][data-property-key="' + property + '"]').find(':selected').val();

            return numberValue + units;
        } else if (property === 'zIndex' ||
            property === 'opacity' ||
            property === 'perspective-origin-x' ||
            property === 'perspective-origin-y') {
            return $wrapper.find('input[data-property-set="' + group + '"][data-property-key="' + property + '"]').val();
        } else if (property === 'backgroundPositionX' ||
            property === 'backgroundPositionY' ||
            property === '-webkit-mask-position-x' ||
            property === '-webkit-mask-position-y') {
            return this.getBackgroundPosition($wrapper, property);
        } else if (property === 'backgroundSize' ||
            property === '-webkit-mask-size') {
            return this.getBackgroundSizeValue($wrapper, property);
        } else if (property === 'fontWeight' || property === 'fontStyle') {
            return this.getFontValue(null, null, property);
        } else {
            console.error('Attempt to get an unsupported style');
            return '';
        }
    }

    getFontValue($wrapper, group,
                 property) {
        switch (property) {
            case 'fontSize':
                let units = $wrapper.find('[data-property-set="units"][data-property-key="' + property + '"]').find(':selected').val();
                if (units !== 'auto') {
                    return $wrapper.find('input[data-property-set="' + group + '"][data-property-key="' + property + '"]').val() + units;
                }
                return units;
            case 'fontWeight':
                return $('select[data-property-set="styles"][data-property-key="' + property + '"]').data('kendoDropDownList').value();
            case 'fontStyle':
                return $('select[data-property-set="styles"][data-property-key="' + property + '"]').data('kendoDropDownList').value();
        }
    }

    /**
     * Returning backgroundSize values from the kendo toolbar input
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {object} $wrapper - dom element containing the values of the input
     * @param {string} property - css property typr - backgroundSize or webkitMaskSize
     * @return {string} backgroundSize values
     */
    getBackgroundSizeValue($wrapper, property) {
        var selectedVal = $wrapper.find('select[data-property-set="styles"][data-property-key="' + property + '"]').val();
        if (selectedVal !== 'auto') {
            return selectedVal;
        }

        var selectedElement = this.currentElementsSelection[0];
        var widgetStyle = this.mappedWidgets[selectedElement].widget.styles;

        return widgetStyle[property + 'Width'] + ' ' + widgetStyle[property + 'Height'];
    }

    /**
     * Returning the backgroundPosition values from the kendo toolbar input
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {object} $wrapper - dom element containing the values of the input
     * @return {string} backgroundPosition values
     */
    getBackgroundPosition($wrapper, property) {
        var units = $wrapper.find('[data-property-set="units"][data-property-key="' + property + '"]').find(':selected').val();
        if (helpers.isBackgroundPositionString(units)) {
            return units;
        }
        var numberValue = $wrapper.find('input[data-property-set="styles"][data-property-key="' + property + '"]').val();
        return numberValue + units;
    }

    getTextColorValue() {
        return $('#color-picker').data('kendoColorPicker').value();
    }

    getBorderColorValue() {
        return $('#borderColor-picker').data('kendoColorPicker').value();
    }

    getBackgroundColorValue() {
        return $('#background-picker').data('kendoColorPicker').value();
    }

    getTransformValue($wrapper, group, property) {
        var returnValue = '';
        if (property === 'transform') {
            var transformTypes = (<string[]>Enums.TransformTypes).filter(function (element) {
                return (element !== 'transform-origin-x' && element !== 'transform-origin-y');
            });

            for (var i = 0; i < transformTypes.length; i++) {
                var propKey = transformTypes[i];

                var propertyValue = $('input[data-property-set="' + group + '"][data-property-key="' + propKey + '"]').val() +
                    ($('[data-property-set="units"][data-property-key="' + propKey + '"]').find(':selected').val() || '');
                returnValue = returnValue + (propKey + '(' + propertyValue + ')') + ' ';
            }
        } else {
            returnValue = $wrapper.find('input[data-property-set="' + group + '"][data-property-key="' + property + '"]').val() +
                ($wrapper.find('[data-property-set="units"][data-property-key="' + property + '"]').find(':selected').val() || '');
        }
        return returnValue.trim();
    }

    getTransformOriginValue($wrapper, group, property) {
        return $wrapper.find('input[data-property-set="' + group + '"][data-property-key="' + property + '"]').val() +
            ($wrapper.find('[data-property-set="units"][data-property-key="' + property + '"]').find(':selected').val() || '').trim();
    }

    getGeometryValue($wrapper, group,
                     property) {
        return $wrapper.find('input[data-property-set="' + group + '"][data-property-key="' + property + '"]').val() +
            $wrapper.find('[data-property-set="units"][data-property-key="' + property + '"]').find(':selected').val();
    }

    getFilterValues(widget, group, property) {
        let filterString = '';
        let propSetKeySelector = 'input[data-property-set="' + group + '"][data-property-key="' + property + '"]';
        let dropShadowProps = ['dropShadowColor', 'dropShadowY', 'dropShadowX', 'dropShadowBlur', 'drop-shadow'];
        let elementVal = $('.wrap-property ' + propSetKeySelector + '').val();

        if (dropShadowProps.indexOf(property) === -1) {
            let elUnit = filtersConfig[group][property].unit;
            if (property === 'hue-rotate' && widget['-webkit-filter']['hue-rotate']) {
                elementVal = widget['-webkit-filter']['hue-rotate'];
                elUnit = '';
            }

            filterString = elementVal + elUnit;
        } else {
            this.setDropShadowProps(widget.id);
            let dropShadowObj = widget['-webkit-filter'];
            filterString += dropShadowObj[property];
        }
        return filterString;
    }

    undoRedoScene(type) {
        var commands = couiEditor.openFiles[this.editorTabName][type][0];
        var elementsId = [];

        if (commands !== undefined) {
            var commandsLength = commands.length;
            var lastCommandName = Object.keys(commands[commandsLength - 1])[0];

            for (var i = commandsLength - 1; i >= 0; i--) {
                var commandName = Object.keys(commands[i])[0];

                // using presets value of undo command for element id //
                if (commands[i].property && commands[i].property.propGroup === 'element-id') {
                    elementsId.push(commands[i][commandName].value);
                } else {
                    elementsId.push(commands[i][commandName].widgetId);
                }

                this.executeCommand(commands[i], type);
            }

            couiEditor.openFiles[this.editorTabName][type].splice(0, 1);

            var localStoreItem = JSON.parse(localStorage.getItem(this.editorTabName));
            localStoreItem[this.editorTabName][type] = couiEditor.openFiles[this.editorTabName][type];
            localStorage.setItem(this.editorTabName, JSON.stringify(localStoreItem));

            elementsId = helpers.removeDuplicates(elementsId);
            if (lastCommandName !== 'createElement') {
                this.selectMultiJstreeItems(elementsId);
            }

            this.inputSearch.trigger('input');
        }

        // reset
        redoLenCount = 1;
        redoLenCommands = 0;
        undoLenCount = 1;
        undoLenCommands = 0;
    }

    executeCommand(command, type) {
        var key = Object.keys(command)[0];

        // set scene state to undo to prevent saveProperties()
        this._sceneActionState.primaryAction = type;
        this['_undoRedo_' + key](command, type);
    }

    _undoRedo_setAnimationOptions(command) {
        var widgetData = command.setAnimationOptions;
        this.Timeline['setAnimation' + widgetData.params.type](widgetData.widgetId, widgetData.propGroup, widgetData.propKey, widgetData.params.value, widgetData.params.className);
    }

    _undoRedo_deleteComponent(command) {
        let createData = command.deleteComponent;
        let componentData = createData.createComponent;
        let componentJSON = componentData.json;
        let componentName = componentData.name;
        couiEditor.openFiles[couiEditor.selectedEditor].components.create(componentName, componentJSON);
        document.body.dispatchEvent(new CustomEvent('coui.editor.rebuild'));
        this._initAssetsKendoToolbar();

        // load component animations
        this.Animations.loadKeyframes(this.scene.animationClasses);
    }

    _undoRedo_createComponent(command) {
        let componentData = command.createComponent;
        let componentName = componentData.deleteComponent.componentName;
        this.removeWidgetAsset(componentName, 'widget');
        this.assetsDOM = createVDOM(this.createAssetDOM(couiEditor.assets).template);

        if (this.activeFilterButtons) {
            dispatch('coui.assets.filter', sort_helpers.filterVDOM(this.activeFilterButtons, this.assetsDOM));
        }
    }

    _undoRedo_deleteAnimationClassGlobal(command) {
        this.Timeline._addGlobalAnimationClassName(command.deleteAnimationClassGlobal.params);
    }

    _undoRedo_addAnimationClassGlobal(command) {
        this.Timeline._deleteGlobalAnimationClassName(command.addAnimationClassGlobal.params.className);
    }

    _undoRedo_addKeyframe(command) {
        var widgetData = command.addKeyframe;
        var oldKeyframe = widgetData.deleteKeyframe.oldKeyframe;

        if (oldKeyframe) {

            this.Animations.addKeyframe(widgetData.widgetId,
                oldKeyframe.group, oldKeyframe.property,
                oldKeyframe.values[0], oldKeyframe.time);
        } else {
            var keyframeData = {
                time: widgetData.deleteKeyframe.positions.seconds,
                id: widgetData.widgetId,
                group: widgetData.propGroup,
                prop: widgetData.propKey,
                value: widgetData.deleteKeyframe.value,
                className: widgetData.deleteKeyframe.className
            };

            this.Animations.deleteKeyframe(keyframeData);
        }
    }

    _undoRedo_deleteKeyframe(command) {
        var widgetData = command.deleteKeyframe;

        this.Animations.addKeyframe(widgetData.widgetId, widgetData.propGroup, widgetData.propKey, widgetData.addKeyframe.value, widgetData.addKeyframe);
    }

    _undoRedo_moveKeyframe(command) {
        var widgetData = command.moveKeyframe;
        var $elementInTime = helpers.getFromTimeline('[data-widget-id="' + widgetData.widgetId + '"] [data-property-type="' + widgetData.propKey + '"] [data-current-time="' + widgetData.params.currentTime + '"]');
        this.Animations.onKeyframeChange($elementInTime, widgetData.params.currentTime, widgetData.params.oldTime);
    }

    _undoRedo_property(command) {
        var props = command.property;
        var widget = this.mappedWidgets[props.widgetId];
        var selectedWidget = this.getSelectedWidgetId();

        this.clearSelectedElements();

        if (selectedWidget !== props.widgetId) {
            this.selectJstreeItem(props.widgetId);
        }

        var prop = props.propKey;
        var group = props.propGroup;

        if (group === 'units') {
            if (widget.widget.geometry[prop] === 'auto') {
                this._setProperties(widget.widget, null, 'geometry', props.propKey, props.value);
                this._skipUndoRedoSteps = 1;
            }
        }
        if (props.editAnimationClass) {
            this.editAnimationClassName(props.widgetId, props.editAnimationClass.newClassName, props.editAnimationClass.oldClassName);
        } else {
            this._setProperties(widget.widget, null, props.propGroup, props.propKey, props.value);
        }
    }

    _undoRedo_color(command) {
        var _this = this;
        var props = command.color;
        var widget = this.mappedWidgets[props.widgetId].widget;
        var key = props.propKey;
        var group = props.propGroup;
        var selector = key;
        var applyValue = props.value;
        var selectedWidget = this.getSelectedWidgetId();
        var $colorPickerElement;

        if (selectedWidget !== props.widgetId) {
            this.selectJstreeItem(props.widgetId);
        }

        var isUrl = false;

        if (props.value instanceof Array) {
            isUrl = props.value[0].startsWith('url');
        } else if (props.value instanceof String) {
            isUrl = props.value.startsWith('url');
        }

        if (!isUrl) {

            if (group === 'boxShadow') {
                $colorPickerElement = $('#boxShadow-picker');
            } else if (key === 'dropShadowColor') {
                $colorPickerElement = $('#dropShadow-picker');
            } else {
                $colorPickerElement = $('#' + selector + '-picker');
            }
            if ($colorPickerElement.length > 0) {
                var colorValue = helpers.getColorValue(props.value)[0];
                $colorPickerElement.data('kendoColorPicker').value(colorValue);
            }
        }

        this.saveProperties(widget.id, group, key, applyValue);

        if (group === 'boxShadow') {
            var boxShadow = this.buildBoxShadowProperty(widget);
            this.applyProperties(widget.id, 'css property',
                '-webkit-box-shadow', boxShadow);
        } else if (key === 'dropShadowColor') {
            var el = document.getElementById(widget.id);
            helpers.splitCssStringProperties(widget.id, el.style['webkitFilter'], 'webkitFilter', 'dropShadowColor', null, function (val) {
                _this.applyProperties(widget.id, 'css property', '-webkit-filter', val);
            });
        } else {
            if (key === 'borderColor') {
                group = 'css property';
            }

            this.applyProperties(widget.id, group, selector, applyValue);
        }
    }

    _undoRedo_createElement(command) {
        var widgetId = command.createElement.widgetId;
        var isNewWidget = command.createElement.isNewWidget;
        this.removeWidget(widgetId, isNewWidget);
    }

    _undoRedo_widgetOption(command) {
        var fn = command.widgetOption.options.fn;
        this[fn](command.widgetOption.widgetId);
    }

    _undoRedo_addFile(command) {
        var addCommand = command.addFile.params;
        this.assetChosen(addCommand.url, null, addCommand.type,
            addCommand.type);
    }

    _undoRedo_deleteFile(command) {
        var deleteCommand = command.deleteFile.params;
        this.deleteFile(deleteCommand.index, deleteCommand.type, deleteCommand.url);
    }

    _undoRedo_deleteElement(command) {
        var deletedWidget = command.deleteElement.createWidget;
        var parents = deletedWidget.widgetParentIds.reverse();
        var widget = deletedWidget.widget.widget;

        this.createElementsOnTheScene({
            currentWidget: widget,
            parentWidgetId: parents[0],
            parentJstreeNode: deletedWidget.jstreeParent,
            jstreePosition: deletedWidget.jstreePosition,
            jstreeAfterNodeId: deletedWidget.jstreeNodeBeforeId,
            jstreeNodeAppendId: deletedWidget.jstreeNodeAppendId,
            isNewWidget: deletedWidget.isNewWidget
        });

        if (helpers.isComponent(widget)) {
            const component = {
                __Type: 'widget',
                isFile: true,
                name: widget.widgetkit,
                url: ''
            };

            // check if component is already in assets in cases where the component was deleted
            // and undo-redo action was performed.
            if (!couiEditor.assets.widget.some(widget => widget.name === component.name)) {
                couiEditor.assets.widget.push(component);
            }

            this.assetsDOM = createVDOM(this.createAssetDOM(couiEditor.assets).template);
            this.virtualList.updateDOM(this.assetsDOM);
        }

        this.Animations.loadKeyframes(deletedWidget.animationData[widget.id]);
    }

    _undoRedo_moveElement(command) {
        var widgetId = command.moveElement.widgetId;
        var oldParentId = command.moveElement.moveWidget.oldParentJstreeId;
        var currentPerentId = command.moveElement.moveWidget.currentParentJstreeId;
        var position = command.moveElement.moveWidget.position;
        var oldPosition = command.moveElement.moveWidget.oldPosition;
        this.undoRedoMoveWidget(widgetId, currentPerentId, oldParentId, oldPosition, position);
    }

    _undoRedo_sceneProperty(command, type) {
        this.setSceneStyleProperty('backgroundColor', command.sceneProperty.style.backgroundColor);
        $('#scene-background-picker').data('kendoColorPicker').value(command.sceneProperty.style.backgroundColor);
    }

    _undoRedo_aspectRatio(command, type) {
        var sceneType = command.aspectRatio.type;

        if (type !== 'new action') {
            this._setUndoRedoCommandsFill({
                aspectRatio: {
                    type: this.scene.sceneSize.type,
                    width: this.scene.sceneSize.width,
                    height: this.scene.sceneSize.height
                }
            });
        }

        if (sceneType === 'aspectRatio_custom') {
            this.scene.sceneSize.width = command.aspectRatio.width;
            this.scene.sceneSize.height = command.aspectRatio.height;
        }

        this.setAspectRatio(sceneType);

        window.requestAnimationFrame(function () {
            let selectedEditor = couiEditor.selectedEditor;
            couiEditor.focusCUIFile(selectedEditor);
        });
    }

    buildBoxShadowProperty(widget) {

        if (Object.keys(widget.boxShadow).length === 0 || !widget.boxShadow) {
            return '0px 0px 0px 0px #000;';
        }

        var boxShadowProps = widget.boxShadow;
        var outsetInset = boxShadowProps.insetOutset;

        if (outsetInset === 'none') {
            outsetInset = '';
        }

        return outsetInset + ' ' + boxShadowProps.horizontalLength + ' ' +
            boxShadowProps.verticalLength + ' ' +
            boxShadowProps.blurRadius + ' ' + boxShadowProps.spreadRadius + ' ' + boxShadowProps.color;
    }

    evalScripts(scriptScope, scripts) {

        var importScripts = function (string) {
            if ((/(.js)$/g).exec(string) === null) {
                return ((function (string) {
                    return $(string).html();
                })(string));
            } else {
                return new Promise(function (resolve) {
                    $.ajax({
                        type: 'GET',
                        url: 'coui://uiresources/editor/' + string,
                        dataType: 'text',
                        success: function (text) {
                            resolve(text);
                        }
                    });
                });
            }
        };

        var evalAll = function (scripts) {
            for (var i = 0; i < scripts.length; i++) {
                if (scriptScope.self === scriptScope) {
                    scriptScope.eval(scripts[i]);
                }
            }
        };

        Promise.all(
            scripts.map(importScripts)
        ).then(evalAll);
    }

    removeMultipleWidgets() {
        this._sceneActionState.primaryAction = 'new action';
        var ids = this.currentParentElementsSelection.filter(Boolean);
        this._undoCreationStepsLength = ids.length;
        this.removeWidgets(ids);
    }

    /**
     *
     * @param elementID
     * @returns {boolean}
     */
    isTopLevel(elementID) {
        var element = document.getElementById(elementID);
        return element && element.parentElement.id === 'scene' ? true : false;
    }

    isWidget(elementID) {
        return this.mappedWidgets[elementID].widget.type === 'widget' ? true : false;
    }

    /**
     *
     * @param widget
     */
    renderProperties(widget) {
        var _this = this;
        var handlebarObj: any = {};
        handlebarObj.widget = widget;
        handlebarObj.env = couiEditor.preferences.couiEnvironment;
        var htmlProperties = this.templateProperties(handlebarObj);

        this.propertiesElBar[0].innerHTML = htmlProperties;

        this.initKendoProperties(widget);
        this.kendoPropertiesHandler();
        this.propertiesElBar.find('#remove-widget').off();
        this.propertiesElBar.find('#remove-widget').on('click', function () {
            _this.removeMultipleWidgets();
        });

        // Initializing all text and number fields on the tabs //
        this.initPropertiesBarHandlers(widget, 'basic-properties');
        this.initPropertiesBarHandlers(widget, 'filter-properties');
        this.initPropertiesBarHandlers(widget, 'transform-properties');
        this.initPropertiesBarHandlers(widget, 'boxshadow-properties');
        this.initPropertiesBarHandlers(widget, 'blend-modes-properties');

        // Initializing all text and number fields on the tabs //
        this.textPropertiesBarHandler(widget);
        this.blendModesBarHandler(widget);
        this.eventsPropertiesBarHandler(widget);
        this.filtersPropertiesBarHandler(widget);
        this.geometryPropertiesBarHandler(widget);
        this.transformPropertiesBarHandler(widget);
        this.backgroundPropertiesBarHandler(widget);
        this.borderStylePropertiesBarHandLer(widget);
        this.dataBindingPropertiesBarHandler(widget);

        this.expandKendoPanels(widget, couiEditor.currentOpenedTabs);

        // attach image upload event
        if (widget.url !== undefined) {
            this.uploadVideoHandlers(widget);
        }

        if (this.interactElementsState !== 'create') {
            this.interactElementsState = 'resize';
        }
    }

    uploadImageHandlers(widget) {
        $('#upload-image').on('click', function (e) {

            couiEditor.pickAsset(widget.id, 'image').otherwise(function (t) {
                console.log('error = ', t);
            });
        });
    }

    uploadMaskImageHandlers(widget) {
        $('#upload-mask-image').on('click', () => {
            this._sceneActionState.insertMaskImage = true;
            couiEditor.pickAsset(widget.id, 'image').otherwise(function (t) {
                console.log('error = ', t);
            });
        });
        $('#remove-mask-image').on('click', () => {
            let imageMaskValue = widget.styles.webkitMaskImage;
            if (imageMaskValue && imageMaskValue !== ' ') {
                this._sceneActionState.insertMaskImage = true;
                this.insertImage(widget.id, '');
            }
        });
    }

    uploadVideoHandlers(widget) {
        $('#upload-video').on('click', function (e) {

            couiEditor.pickAsset(widget.id, 'video').otherwise(function (t) {
                console.log('error = ', t);
            });
        });
    }

    backgroundPropertiesBarHandler(widget) {

        if ($('#background-properties').length === 0) {
            return;
        }

        var _this = this;

        var handlebarObj: any = {};
        handlebarObj.widget = widget;
        handlebarObj.env = couiEditor.preferences.couiEnvironment;
        var htmlProperties =
            _this.templateBackgroundProperties(handlebarObj);
        document.getElementById('background-properties-content').innerHTML = htmlProperties;

        $('#background-properties-content select').kendoDropDownList({
            animation: false
        });

        var $backgroundColorPicker = $('#background-picker');

        $backgroundColorPicker.kendoColorPicker({
            buttons: false,
            open: function () {
                // TODO: Ugly
                // 2 nested kendoPanelBars crash kendo color picker z-order
                // and we need to reposition left side
                // change it if is possible
                _this.colorPickerLeftReposition();

                $backgroundColorPicker.on('mouseup', function () {
                    var background = $backgroundColorPicker.data('kendoColorPicker').value();

                    _this._sceneActionState.primaryAction = 'new action';
                    _this.saveProperties(widget.id, 'css property', 'background', background);
                });
            },
            close: function () {
                // TODO: Ugly
                _this.colorPickerRemoveLeftReposition();

                $backgroundColorPicker.off('mouseup');
            },
            select: function (event) {
                $backgroundColorPicker.data('kendoColorPicker').value(event.value);

                _this.applyProperties(widget.id, 'css property', 'background', event.value);
                _this.saveProperties(widget.id, 'css property', 'background', event.value);
            },
            value: _this.isColorbackground(widget.background),
            opacity: true
        });

        // check background-size value to show extra input field
        if (widget.styles.backgroundSize === 'auto') {
            $('.input-backgroundSize').show();
        }

        // check -webkit-mask-size value to show extra input field
        if (widget.styles['-webkit-mask-size'] === 'auto') {
            $('.input--webkit-mask-size').show();
        }

        this._setMaskSizeKendoInputs(widget);

        // attach image upload event
        if (widget.url !== undefined ||
            widget.type === 'responsiveImage') {
            this.uploadImageHandlers(widget);
        }

        // attach image upload event
        if (widget.styles.webkitMaskImage !== undefined) {
            this.uploadMaskImageHandlers(widget);
        }

        this.initPropertiesBarHandlers(widget, 'background-properties');
    }

    _renderBlendModesPanel(widget) {
        const blendModeContent: HTMLElement = document.getElementById('blend-modes-content');
        const handlebarObj: any = {};
        handlebarObj.widget = widget;

        const htmlProperties = this.templateBlendModes(handlebarObj);

        // Check if the blend-modes-content element exist
        // in case of widget or component creation
        if (blendModeContent) {
            blendModeContent.innerHTML = htmlProperties;

            $('#blend-modes-content select').kendoDropDownList({
                animation: false
            });

            this.initPropertiesBarHandlers(widget, 'blend-modes-properties');
        }
    }

    _renderTextPropertiesPanel(widget) {
        var _this = this;
        var handlebarObj: any = {};
        handlebarObj.widget = widget;
        var htmlProperties = this.templateTextProperties(handlebarObj);
        document.getElementById('text-properties-content').innerHTML = htmlProperties;

        $('#text-properties-content select').kendoDropDownList({
            animation: false
        });

        var $textColorPicker = $('#color-picker');
        $textColorPicker.kendoColorPicker({
            buttons: false,
            open: function () {
                // TODO: Ugly - info on first comment
                // change it if is possible
                _this.colorPickerLeftReposition();

                $('body').on('mouseup', function () {
                    var color = $textColorPicker.data('kendoColorPicker').value();

                    _this.saveProperties(widget.id, 'css property', 'color', color);
                });
            },
            close: function () {
                // TODO: Ugly - info on first comment
                _this.colorPickerRemoveLeftReposition();

                $('body').off('mouseup');
            },
            select: function (event) {
                $textColorPicker.data('kendoColorPicker').value(event.value);

                _this.applyProperties(widget.id, 'css property', 'color', event.value);
                _this.saveProperties(widget.id, 'css property', 'color', event.value);
            },
            value: widget.color,
            opacity: true
        });

        // create an array of objects with all possible font-family options
        const fontStyles = _this.scene.fonts.map(font => {
            const fontPath = font.replace(/[^\/\\]*$/, '');
            const shortenedFont = helpers.backwardsShortenString(fontPath, 18);

            return {
                name: font.replace(/\.[^/.]+$/, '').replace(/^.*[\\\/]/, ''),
                style: font.replace(/\.[^/.]+$/, '').replace(/^.*[\\\/]/, ''),
                location: `Located under: ${shortenedFont}`
            };
        }) || [];

        $('.font-family').kendoDropDownList({
            height: 'auto',
            animation: false,
            dataSource: [
                {name: 'Default', style: 'initial', location: 'Reset to default'},
                {
                    name: 'Inherit',
                    style: 'inherit',
                    location: 'Inherits from parent element',
                    customClass: 'last-default'
                }
            ].concat(fontStyles),
            filter: 'contains',
            dataTextField: 'name',
            dataValueField: 'style',
            template: fontFamilyTemplate,
            valueTemplate: selectedFontFamily,
            noDataTemplate: 'No fonts found',
            index: -1,

            change() {
                _this._sceneActionState.primaryAction = 'new action';
                _this._setProperties(widget, null, 'styles', 'fontFamily', this.value());
            },

            open() {
                // prevent input search box selection on open
                // which causes the dropdown to not hide on second click
                $($('.k-list-filter > input').get(0)).trigger('mousedown');
            },

            dataBound() {
                const kendoDropdown = $('select.font-family').data('kendoDropDownList');

                if (helpers.fontIsPresent(widget.styles.fontFamily, _this.scene.fonts)) {
                    kendoDropdown.value(widget.styles.fontFamily);
                } else {
                    kendoDropdown.value('Default');
                }
            }
        });

        this.initPropertiesBarHandlers(widget, 'text-properties');
    }

    addFontClickHandler() {
        const addFontButton = $('#add-font');

        addFontButton.on('click', () => {
            this.shouldEnableFont = true;
            couiEditor.pickAsset('', 'font').otherwise(t => {
                console.log('error = ', t);
            });
        });
    }

    blendModesBarHandler(widget) {
        var _this = this;

        $('#blend-modes-properties').on('click', function () {
            _this._renderBlendModesPanel(widget);
            $(this).off('click');
        });
    }

    textPropertiesBarHandler(widget) {
        var _this = this;

        $('#text-properties').on('click', function () {
            _this._renderTextPropertiesPanel(widget);
            $(this).off('click');
        });
    }

    _renderBorderPropertiesPanel(widget) {
        var _this = this;
        var handlebarObj: any = {};
        handlebarObj.widget = widget;
        handlebarObj.env = couiEditor.preferences.couiEnvironment;
        var htmlProperties = this.templateBorderStyleProperties(handlebarObj);

        document.getElementById('border-style-properties-content').innerHTML = htmlProperties;

        var $borderColorPicker = $('#borderColor-picker');
        if (widget.styles !== undefined) {
            if (widget.styles.borderColor !== undefined) {
                $borderColorPicker.kendoColorPicker({
                    buttons: false,
                    open: function () {
                        // TODO: Ugly - info on first comment
                        // change it if is possible
                        _this.colorPickerLeftReposition();

                        $('body').on('mouseup', function () {
                            var color = $borderColorPicker.data('kendoColorPicker').value();

                            _this.saveProperties(widget.id, 'styles', 'borderColor', color);
                        });
                    },
                    close: function () {
                        // TODO: Ugly - info on first comment
                        _this.colorPickerRemoveLeftReposition();

                        $('body').off('mouseup');
                    },
                    select: function (event) {
                        $borderColorPicker.data('kendoColorPicker').value(event.value);

                        _this.applyProperties(widget.id, 'css property', 'borderColor', event.value);
                        _this.saveProperties(widget.id, 'styles', 'borderColor', event.value);
                    },
                    value: widget.styles.borderColor,
                    opacity: true
                });
            }
        }

        $('#border-style-properties-content select').kendoDropDownList({
            animation: false
        });

        this.initPropertiesBarHandlers(widget, 'border-style-properties');
    }

    borderStylePropertiesBarHandLer(widget) {
        var _this = this;

        $('#border-style-properties').on('click', function () {
            _this._renderBorderPropertiesPanel(widget);
            $(this).off('click');
        });
    }

    _addRemoveEngineCallEngineTriggerParamsHandler(widget) {
        var _this = this;

        $('.add-engine-event-param').on('click', function () {
            widgetEventToChage($(this)).push('');
            _this._renderEventPropertiesBar(widget);
        });

        $('.remove-engine-event-param').on('click', function () {
            var index = $(this).attr('data-param-index');

            widgetEventToChage($(this)).splice(index, 1);

            _this._renderEventPropertiesBar(widget);
        });

        function widgetEventToChage($this) {
            var eventType = $this
                .closest('.events-wrap')
                .find('select')
                .attr('data-property-key');

            var eventEngineFn = $this.attr('data-engine-fn');
            return widget.events[eventType][eventEngineFn];
        }
    }

    _renderEventPropertiesBar(widget) {
        var handlebarObj: any = {};
        var eventTypes = <string[]> Enums.EventTypes;
        handlebarObj.allEvents = window.editorEvents;
        handlebarObj.eventTypes = {};

        for (var i = 0; i <= eventTypes.length - 1; i++) {
            var event = eventTypes[i];
            if (widget.events) {
                handlebarObj.eventTypes[event] = widget.events[event];
            } else {
                handlebarObj.eventTypes[event] = '';
            }
        }

        var htmlProperties = this.templateEventsProperties(handlebarObj);
        document.getElementById('events-properties-content').innerHTML = htmlProperties;

        this._addRemoveEngineCallEngineTriggerParamsHandler(widget);

        if (couiEditor.globalEditorInfo.backend === Enums.Backends.Standalone) {
            $('.blueprint-option').remove();
        }

        this.initPropertiesBarHandlers(widget, 'events-properties');

        $('#events-properties-content select').kendoDropDownList({
            animation: false
        });
    }

    /**
     * show - hide remove maskImage bar
     * @param type
     * @param value
     */
    updateBackgroundFilePathInfo(type: string, value ?: string) {
        if (type === 'show') {
            $('.mask-image-remove-area').parent().fadeIn();
            let cleanedUrl = helpers.cleanBackgroundUrls(value);
            $('.mask-image-remove-area .url-text').text(cleanedUrl);
        } else {
            $('.mask-image-remove-area').parent().fadeOut();
        }
    }

    /**
     * Reset drop shadow filter
     * @param widget
     */
    filtersPropertiesBarHandler(widget) {
        var _this = this;

        // filters dismiss button
        $('.dismiss-filter-drop-shadow').on('click', function () {
            _this._undoCreationStepsLength = 4;
            _this._setFilter(null, widget, widget.id, '-webkit-filter', 'dropShadowX', '0px');
            _this._setFilter(null, widget, widget.id, '-webkit-filter', 'dropShadowY', '0px');
            _this._setFilter(null, widget, widget.id, '-webkit-filter', 'dropShadowBlur', '0px');
            _this._setFilter(null, widget, widget.id, '-webkit-filter', 'dropShadowColor', 'rgb(0, 0, 0)');

            $('#dropShadow-picker').data('kendoColorPicker').value('rgb(0, 0, 0)');
        });
    }

    eventsPropertiesBarHandler(widget) {
        var _this = this;

        $('#events-properties').on('click', function () {
            _this._renderEventPropertiesBar(widget);
            $(this).off('click');
        });
    }

    dataBindingPropertiesBarHandler(widget) {
        var _this = this;

        $('#data-binding-properties').on('click', function () {
            _this._renderDataBindingPropertiesBar(widget);
            $(this).off('click');
        });
    }

    _renderDataBindingPropertiesBar(widget) {
        var handlebarObj: any = {};
        var dataBindingTypes: string[] = <string[]> Enums.DataBindingTypes;

        handlebarObj.dataBindingTypes = {};

        for (var i = 0; i <= dataBindingTypes.length - 1; i++) {
            var dataBindingEvent = dataBindingTypes[i];
            var translatedNames = helpers.translateDataBindNames(dataBindingEvent, true);
            if (widget.dataBindings[dataBindingEvent]) {
                handlebarObj.dataBindingTypes[translatedNames] = widget.dataBindings[dataBindingEvent];
            } else {
                handlebarObj.dataBindingTypes[translatedNames] = '';
            }
        }

        var htmlProperties = this.templateDataBindingEvents(handlebarObj);
        document.getElementById('data-binding-properties-content').innerHTML = htmlProperties;

        this.initPropertiesBarHandlers(widget, 'data-binding-properties');
    }

    _renderGeometryPropertiesPanel(widget) {
        var handlebarObj: any = {};
        handlebarObj.widget = widget;
        var htmlProperties = this.templateGeometryProperties(handlebarObj);

        document.getElementById('geometry-properties-content').innerHTML = htmlProperties;
        this.initPropertiesBarHandlers(widget, 'geometry-properties');

        this.couiEnvironmentManagement();
        $('#geometry-properties-content select').kendoDropDownList({
            animation: false
        });

        if (widget.geometry.position === 'relative') {
            $('#geometry-properties-content select[data-property-key="top"] , select[data-property-key="left"]')
                .kendoDropDownList({
                    enable: false
                });
        }
    }

    geometryPropertiesBarHandler(widget) {
        var _this = this;

        $('#geometry-properties').on('click', function () {
            _this._renderGeometryPropertiesPanel(widget);
            $(this).off('click');
        });
    }

    /**
     * Method forcing a rerender of the transformProperties bar.
     * @function transformPropertiesBarHandler
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {object} widget - widget that is currently selected AKA to get the transform properties from.
     */
    transformPropertiesBarHandler(widget) {
        var _this = this;

        $('#transform-properties').on('click', function () {
            _this._renderTransformPropertiesBar(widget);
            $(this).off('click');
        });
    }

    /**
     * Method rendering the transformProperties bar.
     * @function _renderTransformPropertiesBar
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {object} widget - widget that is currently selected AKA to get the transform properties from.
     */
    _renderTransformPropertiesBar(widget) {
        var handlebarObj: any = {};
        handlebarObj.widget = $.extend(true, {}, widget);
        handlebarObj.env = couiEditor.preferences.couiEnvironment;

        var htmlProperties = this.templateTransformProperties(handlebarObj);

        document.getElementById('transform-properties-content').innerHTML = htmlProperties;
        this.initPropertiesBarHandlers(widget, 'transform-properties');
        this._initTransformHandlers(widget);

        $('#transform-properties-content select').kendoDropDownList({
            animation: false
        });
    }

    /**
     * Sets the default properties to the filters
     *
     * @param event {JQueryEventObject}
     * @param key {string} - the key of the property that has to be reset; if it's 'all' - resets all
     *
     * */
    setFiltersDefaults(event: JQueryEventObject, key: string) {
        if (key !== 'all') {
            this.resetFilterProps(event, key, filtersConfig['-webkit-filter'][key].default);
            return;
        }

        for (let i in filtersConfig['-webkit-filter']) {
            this.resetFilterProps(event, i, filtersConfig['-webkit-filter'][i].default);
        }
    }

    /**
     * Sets and saves a property to a filter and re-renders the filters panel
     *
     * @param event {JQueryEventObject}
     * @param key {string} - key of the property
     * @param defaultValue {string | number}
     *
     * */
    resetFilterProps(event: JQueryEventObject, key: string, defaultValue: string | number) {
        this._setProperties(event.data.widget, null, '-webkit-filter', key, defaultValue);
    }

    /**
     * Method creating the event system of the transforms bar.
     * @function _initTransformHandlers
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {object} widget - widget that is currently selected AKA to get the transform properties from.
     */
    _initTransformHandlers(widget) {
        var _this = this;

        $('.remove-property').off('click');
        $('.remove-property').on('click', function (event) {
            if (widget.transform) {
                var key = $(this).attr('data-type');

                var defaultValue = Enums.TransformDefaultValue[key];
                var units = helpers.getUnitStyle(defaultValue);

                _this.selectKendoDropdownItem('units', key, units);
                _this.setUnitNumberToolbar(key, defaultValue);

                _this._setTransform(null, widget, widget.id, 'transform', key, defaultValue);
                _this.computeSelectCorners($(`#${widget.id}`));
                delete widget.transform[key];

                _this.exportScene();
            }
        });

        $('.dismiss-transform').off('click');
        $('.dismiss-transform').on('click', function (event) {
            if (widget.transform) {
                var keys = <string[]> Enums.TransformTypes;

                keys = keys.filter(function (element) {
                    if (couiEditor.preferences.couiEnvironment === 'GT') {
                        return element !== 'transform-origin-x' &&
                            element !== 'transform-origin-y';
                    } else if (couiEditor.preferences.couiEnvironment === 'Hummingbird') {
                        return element === 'translateX' ||
                            element === 'translateY' ||
                            element === 'scaleX' ||
                            element === 'scaleY' ||
                            element === 'scaleZ';
                    }
                });
                _this._undoCreationStepsLength = keys.length;
                for (var i = 0; i < keys.length; i++) {
                    var defaultValue = Enums.TransformDefaultValue[keys[i]];
                    var units = helpers.getUnitStyle(defaultValue);

                    _this.selectKendoDropdownItem('units', keys[i], units);
                    _this.setUnitNumberToolbar(keys[i], defaultValue);

                    _this._sceneActionState.primaryAction = 'new action';
                    _this._setTransform(null, widget, widget.id, 'transform', keys[i], defaultValue);
                    _this.computeSelectCorners($(`#${widget.id}`));
                    delete widget.transform[keys[i]];
                }
                _this.exportScene();
            }
        });

        $('.dismiss-transform-origin').off('click');
        $('.dismiss-transform-origin').on('click', function () {
            if (widget.transform) {
                var keys = ['transform-origin-x', 'transform-origin-y'];
                _this._undoCreationStepsLength = 2;
                for (var i = 0; i < keys.length; i++) {
                    var defaultValue = Enums.TransformDefaultValue[keys[i]];
                    var units = helpers.getUnitStyle(defaultValue);

                    _this.selectKendoDropdownItem('units', keys[i], units);
                    _this.setUnitNumberToolbar(keys[i], defaultValue);

                    _this._sceneActionState.primaryAction = 'new action';
                    _this._setTransformOrigin(null, widget, widget.id, 'transform-origin', keys[i], defaultValue);
                    _this.computeSelectCorners($(`#${widget.id}`));
                    delete widget.transform[keys[i]];
                }
                _this.exportScene();
            }
        });

        $('.dismiss-perspective-origin').off('click');
        $('.dismiss-perspective-origin').on('click', function () {
            if (widget.transform) {
                var keys = ['perspective-origin-x', 'perspective-origin-y'];
                _this._undoCreationStepsLength = 2;
                for (var i = 0; i < keys.length; i++) {
                    var defaultValue = Enums.TransformDefaultValue[keys[i]];
                    var units = helpers.getUnitStyle(defaultValue);

                    _this.selectKendoDropdownItem('units', keys[i], units);
                    _this.setUnitNumberToolbar(keys[i], defaultValue);

                    _this._sceneActionState.primaryAction = 'new action';
                    _this._setPerspectiveOrigin(null, widget, widget.id, 'perspective-origin', keys[i], defaultValue);
                    _this.computeSelectCorners($(`#${widget.id}`));
                    delete widget.transform[keys[i]];
                }
                _this.exportScene();
            }
        });
    }

    couiEnvironmentManagement() {
        if (editorSettings.environment.Hummingbird === couiEditor.preferences.couiEnvironment) {
            var $dropdownPercents = $('.units-string option[value="%"]');
            $dropdownPercents.remove();
        }
    }

    applyProperties(elId, saveType, prop, val) {
        var $element = $('#' + elId);

        if (saveType === 'css property') {
            if (prop === 'backgroundSize') {
                if (val === 'auto') {
                    var widget = couiEditor.openFiles[couiEditor.selectedEditor].runtimeEditor.mappedWidgets[elId].widget;
                    var backgroundSize = widget.styles.backgroundSizeWidth + ' ' + widget.styles.backgroundSizeHeight;
                    $element.css(prop, backgroundSize);
                } else {
                    val = val.split(' ');
                    val[0] = unitsConvertor.convertUnitsToPixel(elId, val[0], 'backgroundSize');
                    val[1] = unitsConvertor.convertUnitsToPixel(elId, val[1], 'backgroundSize');
                    val = val.join(' ');
                    $element.css(prop, val);
                }
            } else if (prop === 'webkitMaskImage' && val === ' ') {
                val = val.trim();
                $element.css(prop, val);
            } else if (prop === '-webkit-mask-sizeWidth' || prop === '-webkit-mask-sizeHeight') {
                const width = this.mappedWidgets[elId].widget.styles['-webkit-mask-sizeWidth'];
                const height = this.mappedWidgets[elId].widget.styles['-webkit-mask-sizeHeight'];

                val = `${width} ${height}`;
                $element.css('webkit-maskSize', val);
            } else if (prop === 'perspective-origin-x' || prop === 'perspective-origin-y') {
                if (this.mappedWidgets[elId].widget['perspective-origin']) {
                    const perspectiveX = this.mappedWidgets[elId].widget['perspective-origin']['perspective-origin-x'] || '50%';
                    const perspectiveY = this.mappedWidgets[elId].widget['perspective-origin']['perspective-origin-y'] || '50%';

                    val = `${perspectiveX} ${perspectiveY}`;
                    $element.css('perspective-origin', val);
                }
            } else {
                $element.css(prop, val);
            }

        } else if (saveType === 'id property') {

            $element.attr('id', val);
            var currentNode = $('#sceneTree').jstree().get_node($('li[data-id="' + elId + '"]'), false);
            currentNode['li_attr']['data-id'] = val;

            $('li[data-id="' + elId + '"]').attr('data-id', val);

            if (elId === this.getSelectedWidgetId()) {
                $('#selected-element').attr('data-element-id', val);
                $('[data-property-key="id"]').attr('value', val);
            }

            $('[data-widget-id="' + elId + '"]')
                .attr('data-widget-id', val);

            var jstreeLink = $('li[data-id="' + val + '"] > a');
            jstreeLink.find('.jstree-id-item').text(val);
            var jstreeHtml = jstreeLink.find('.jstree-extra-html')[0].outerHTML;

            // rename timeline settings
            this.Animations.editId(elId, val);

            // replace html in current jstree node
            currentNode.text = jstreeHtml;

        } else if (saveType === 'class property') {
            $element.attr('class', val);
        } else if (saveType === 'checked') {
            $element.prop(prop, val);
        } else if (saveType === 'attrs') {
            $element.attr(prop, val);
            if ($element[0].type === 'range') {
                $element.val(val).trigger('change');
            }
        } else if (saveType === 'remove css property') {
            $element.css(prop, 'auto');
            $element.css(prop, '');
        } else if (saveType === 'set text') {
            $element[0].childNodes[0].nodeValue = val;
            let $widgetEl: any = $('#' + elId);
            this.highlightSelectedEl($widgetEl);
        } else if (saveType === 'image') {
            $element.attr(prop, val);
            let $widgetEl: any = $('#' + elId);
            this.highlightSelectedEl($widgetEl);
        } else if (saveType === 'dataBindings') {
            var actualStyle = Enums.DataBindingGroups[prop];
            if (val.trim() === '') {
                $element.removeAttr(actualStyle);
            } else {
                $element.attr(actualStyle, val);
            }
        }
    }

    _childElMoveCheck(currentId, parentId, oldparentId) {
        if (this._sceneActionState.primaryAction !== 'new action') {
            return;
        }

        var $element = $('#' + currentId);
        var currentWidget = this.mappedWidgets[currentId].widget;
        var oldParent = this.mappedWidgets[oldparentId];

        this._sceneActionState.hierarchyElementMove = true;

        if (parentId !== 'top') {
            var parentWidget = this.mappedWidgets[parentId].widget;

            if (parentWidget.type === 'flexbox' || parentWidget.type === 'select' ||
                parentWidget.type === 'ul' || parentWidget.type === 'ol') {

                if (currentWidget.geometry !== undefined && currentWidget.geometry.position !== 'relative') {
                    this._undoCreationStepsLength = 4;

                    this._setGeometry($element, currentWidget, currentId, 'geometry', 'position', 'relative');
                    this.clearSelectedElements();
                    this.selectJstreeItem(currentId);
                }
            }
        } else if (oldparentId !== 'top' &&
            (oldParent.widget.type === 'flexbox' || oldParent.widget.type === 'select' ||
                oldParent.widget.type === 'ul' || oldParent.widget.type === 'ol')) {

            this._undoCreationStepsLength = 4;

            this._setGeometry($element, currentWidget, currentId, 'geometry', 'position', 'absolute');
            this._setGeometry($element, currentWidget, currentId, 'geometry', 'left', '0vw');
            this._setGeometry($element, currentWidget, currentId, 'geometry', 'top', '0vh');

            $('input[data-property-key="top"]').prop('disabled', false);
            $('input[data-property-key="left"]').prop('disabled', false);
        }

        this._sceneActionState.hierarchyElementMove = false;
    }

    createComponent(widget, callback?) {
        runtime.createComponent(widget, callback);
    }

    createElementSimple(widget) {
        runtime.createAnElement(widget, this.scenePreviewEl);
    }

    createElement(widget) {
        // timeline creation
        this.Animations.createAnimationWidget(widget, {initial: true});

        runtime.createAnElement(widget, this.scenePreviewEl);
    }

    getPreviewStageSize() {
        return {
            width: $(this.scenePreviewEl).width(),
            height: $(this.scenePreviewEl).height()
        };
    }

    getElementSize(el) {
        return {
            width: el.width(),
            height: el.height()
        };
    }

    getSelectedWidgetId() {
        var jstreeSelected = $('#sceneTree').jstree(true)
            .get_selected(true);

        if (jstreeSelected.length > 0) {
            return jstreeSelected[0].li_attr['data-id'];
        }

        return false;
    }

    /**
     * Add new widget node in jstree hierarchy
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     * @param {string} type - widget type
     * @param {string} id - id of widget
     * @param {string} parent - id of parent node
     * @param {number} index - position of hierarchy
     * @param {bool} selectAfter - true if we want to auto select the node
     * @return {*}
     */
    jsTreeAddNode(type, id, parent, index, selectAfter) {
        var _this = this;
        var position = 'last';
        var extraJstreeItemHtml = this.jsTreeExtraItemHtml(type, id);

        if (index !== undefined) {
            if (index === 0) {
                position = 'first';
            } else {
                position = index;
            }
        }

        $('#sceneTree').jstree().create_node(parent, {
            'text': extraJstreeItemHtml,
            'icon': 'fa fa-circle',
            'li_attr': {
                'data-id': id
            }
        }, position, function () {
            if (selectAfter) {
                _this.selectJstreeItem(id);
            } else {
                $('#sceneTree').jstree().open_node(parent);
            }
        }, true);

        $('#sceneTree').jstree().is_loaded(parent);
    }

    createModalJsEditor(propKey,
                        propGroup, val, originalPropGroup) {

        var _this = this;
        var modalEditor: any;
        var $modalWrapper = $('<div id="modal-edit-js-code"></div>');
        var $errorReportDiv = $('<div id="error-report-div"></div>');
        var content = $modalWrapper.text(val);

        $('body').append($modalWrapper);

        modalEditor = ace.edit('modal-edit-js-code');
        modalEditor.setTheme('ace/theme/twilight');
        modalEditor.getSession().setMode('ace/mode/javascript');
        modalEditor.getSession().on('changeAnnotation', function () {
            var $errorDiv = $('#error-report-div');
            var annotations = modalEditor.getSession().getAnnotations();
            $errorDiv.empty();

            for (var key in annotations) {
                if (annotations.hasOwnProperty(key)) {
                    $errorDiv.append(`<div>${annotations[key].text}on line  ${annotations[key].row}</div>`);
                    _this.scrollDown($errorDiv);
                }
            }
        });

        vex.dialog.open({
            contentClassName: 'modal-code',
            closeClassName: 'modal-close',
            message: 'Code',
            buttons: [
                $.extend({}, vex.dialog.buttons.YES, {
                    text: 'Save'
                }),
                $.extend({}, vex.dialog.buttons.NO, {
                    text: 'Cancel'
                })
            ],
            afterOpen: function ($vexContent) {
                $vexContent.find('.vex-dialog-input')
                    .append(content)
                    .append($errorReportDiv);
            },
            callback: function (data) {
                if (data) {
                    var val = modalEditor.getValue();

                    if (propGroup === 'sceneEvents') {

                        _this.scene.sceneEvents[propKey] = val;
                    } else {
                        var currentWidgetId = $('#selected-element')
                            .attr('data-element-id');

                        _this.saveProperties(currentWidgetId, propGroup, propKey, val, originalPropGroup);
                    }

                    _this.saveScene();
                }
            }
        });
    }

    visibilityWidgetHandler() {
        var $v = $('.visibility-widget');
        var _this = this;
        $v.off('click');
        $v.on('click', function () {
            var id = $(this).parents('.jstree-node').attr('data-id');
            _this._sceneActionState.primaryAction = 'new action';
            _this.setVisibility(id);
        });
    }

    lockWidgetHandler() {
        var $l = $('.lock-widget');
        var _this = this;
        $l.off('click');
        $l.on('click', function () {
            var id = $(this).parents('.jstree-node').attr('data-id');
            _this._sceneActionState.primaryAction = 'new action';
            _this.setLocking(id);
        });
    }

    setVisibility(id) {
        this._sceneActionState.widgetOption = true;

        // Finds the JSTree parent node;
        var $jsTreeParents = $('[data-id="' + id + '"]').parents('.jstree-node');

        // Finds the actual parent node in the DOM and returns if invisible;
        var $jsTreeFirstParent = $jsTreeParents.first('[role=tree-item]').parents('.jstree-node').first('[role=tree-item]');

        if ($('#' + $jsTreeFirstParent.attr('data-id')).attr('data-visibility') === 'false') {
            return;
        }

        // Finds the selected node in the DOM and it's children;
        let $widgetEl: any = $('#' + id);
        var $childElems = $widgetEl.find('[data-element-type=widget]');
        var active = !(eval($widgetEl.attr('data-visibility')));

        this.setElementVisible(id, active);
        this.highlightSelectedEl($widgetEl);

        var loopLength = $childElems.length;
        var elementIDs = [];
        elementIDs.push(id);

        for (let i = 0; i < loopLength; i++) {
            var currentId = $($childElems[i]).attr('id');
            elementIDs.push(currentId);
            this.setElementVisible(currentId, active);
        }

        var actionScene = this.getRedoUndoPrimaryState();
        var undoRedoOptions: any;

        // INVISIBLE ELEMENTS ARE SET TO LOCKED //
        if (!active && actionScene === 'new action') {

            var elementsLength = elementIDs.length;
            this._undoCreationStepsLength = elementsLength + 1;

            for (let i = 0; i < elementsLength; i++) {

                this.setElementLock(elementIDs[i], !active);

                undoRedoOptions = {
                    'fn': 'setLocking',
                    'data-lock': !active
                };

                this.createUndoRedoCommand(actionScene, elementIDs[i], null, null, null, undoRedoOptions);
                this._sceneActionState.widgetOption = true;
            }
        }

        undoRedoOptions = {
            'fn': 'setVisibility',
            'data-visibility': active
        };

        this.createUndoRedoCommand(actionScene, id, null, null, null, undoRedoOptions);
    }

    setLocking(id) {
        this._sceneActionState.widgetOption = true;

        // Finds the selected node in the DOM and it's children;
        let $widgetEl: any = $('#' + id);
        var active = !(eval($widgetEl.attr('data-lock')));

        this.highlightSelectedEl($widgetEl);
        this.setElementLock(id, active);

        var undoRedoOptions = {
            fn: 'setLocking',
            'data-lock': !active
        };

        var actionScene = this.getRedoUndoPrimaryState();
        this.createUndoRedoCommand(actionScene, id, null, null, null, undoRedoOptions);
    }

    setElementLock(id, active) {
        var $widgetEl = $('#' + id);
        $widgetEl.attr('data-lock', active);
        $widgetEl.attr('data-element-selectable', (!active));

        var $jstreeOptionType = $('li[data-id="' + id + '"] > a').find('.lock-widget');

        if (active) {
            $jstreeOptionType.addClass('active');
        } else {
            $jstreeOptionType.removeClass('active');
        }
    }

    setElementVisible(id, active) {
        var $widgetEl = $('#' + id);
        $widgetEl.attr('data-visibility', active);
        $widgetEl.css({
            'visibility': (active ? 'visible' : 'hidden')
        });

        var $jstreeOptionType = $('li[data-id="' + id + '"] > a').find('.visibility-widget');

        if (active) {
            $jstreeOptionType.removeClass('active');
        } else {
            $jstreeOptionType.addClass('active');
        }
    }

    loadScene(data) {
        var _this = this;

        this.scene = $.extend(true, this.scene, data);

        this.mapLoadedScene(this.scene.widgets);
        this.loadHierarchy(this.scene.widgets);

        this.scene.setStyleProperty('backgroundColor', this.scene.getStyleProperty('backgroundColor'));

        var callbackObj = {
            runtimeEditor: _this,
            endAllCallback: function () {
                $('#resetZoom').click();
            },
        };

        // TODO remove sceneSize.id when is fixed
        var sceneAspectRatioType = this.scene.sceneSize.type ||
            this.scene.sceneSize.id;

        this.setAspectRatio(sceneAspectRatioType);

        runtime.create(this.scene, this.scenePreviewEl, false, callbackObj);
        this.Animations.loadTimeline(this.scene.animationClasses);
    }

    mapLoadedScene(widgets) {
        var _this = this;

        widgets.map(function (widget) {
            var sceneLen = 0;

            if (widget.children !== undefined &&
                widget.children.length > 0) {
                sceneLen = widget.children.length;
            }

            if (widget.id === undefined) {
                widget.id = couiEditor.generateRandomId(widget.type);
                couiEditor.widgetCount++;
            }
            _this.mappedWidgets[widget.id] = {
                widget: widget
            };

            if (sceneLen > 0) {
                return _this.mapLoadedScene(widget.children);
            }
        });


    }

    loadHierarchy(data) {
        var jstreeData = this.buildJsonInitJstree(data);
        this._sceneJsTree(jstreeData);
    }

    buildJsonInitJstree(widgets) {
        var jsonreeData = [];

        var len = widgets.length;
        var _this = this;

        for (var i = 0; i < len; i++) {
            jsonreeData.push(jsonTreeBuild(widgets[i]));
        }

        function jsonTreeBuild(widget) {
            const node: any = {};
            const type = helpers.isComponent(widget) ? 'component' : widget.type;

            node.text = _this.jsTreeExtraItemHtml(type, widget.id);

            node.icon = 'fa fa-circle';
            node['li_attr'] = {
                'data-id': widget.id
            };
            var children = widget.children;
            if (children !== undefined && children.length > 0) {
                node.children = [];
                children.forEach(function (subwidget) {
                    node.children.push(jsonTreeBuild(subwidget));
                });
            }
            return node;
        }

        return jsonreeData;
    }

    jsTreeExtraItemHtml(type, id) {
        return '<div class="jstree-extra-html"><span class="visibility-widget" data-id="' + id + '"></span>' +
            '<span class="lock-widget" data-id="' + id + '"></span>' +
            '<span class="jstree-id-item">' + id + '</span>' +
            '<span class="jstree-type-item"> (' + type + ')</span></div>';
    }

    moveWidgetScenePreview($currentEl, parentElNew, position, oldParentPos) {
        parentElNew.append($currentEl);

        if (position < oldParentPos) {
            parentElNew.children().eq(position).before($currentEl);
        }

        if (position > oldParentPos) {
            parentElNew.children().eq(position - 1).after($currentEl);
        }

        if (this.elementReposition) {
            this.repositionElementAgainstParent($currentEl[0]);
        }
    }

    moveWidget(widgetId, oldParentId,
               newParentId, newPosition,
               oldPosition) {

        var widgetToMove;
        var widgetToRemove;
        var widgetNewParent;
        var widgetOldParent;

        if (newParentId === 'top' && oldParentId === 'top') { // reorder top level
            widgetToMove = this.scene.widgets[oldPosition];

            // switch elements in top position
            this.scene.widgets.splice(oldPosition, 1);
            this.scene.widgets.splice(newPosition, 0, widgetToMove);

        } else if (newParentId === 'top') { // Coming element from nested to top level

            widgetToMove = this.mappedWidgets[widgetId].widget;

            // remove the element from children position
            widgetToRemove = this.mappedWidgets[oldParentId].widget;

            // remove the element from nested parent
            widgetToRemove.children.splice(oldPosition, 1);

            // add element on top level
            this.scene.widgets.splice(newPosition, 0, widgetToMove);

        } else if (oldParentId === 'top') { // coming element from top level to nested children
            widgetToMove = this.scene.widgets[oldPosition];

            widgetNewParent = this.mappedWidgets[newParentId].widget;

            // add the element from children position
            widgetNewParent.children.splice(newPosition, 0, widgetToMove);

            // remove element from top position
            this.scene.widgets.splice(oldPosition, 1);
        } else { // reorder in nested parents
            widgetToMove = this.mappedWidgets[widgetId].widget;

            widgetNewParent = this.mappedWidgets[newParentId].widget;
            widgetOldParent = this.mappedWidgets[oldParentId].widget;

            if (widgetNewParent === widgetOldParent) {
                // remove the widget from old parent
                widgetOldParent.children.splice(oldPosition, 1);
                // add widget to children parent
                widgetNewParent.children.splice(newPosition, 0, widgetToMove);
            } else {
                // add widget to children parent
                widgetNewParent.children.splice(newPosition, 0, widgetToMove);
                // remove the widget from old parent
                widgetOldParent.children.splice(oldPosition, 1);
            }
        }
        //calculates the transformed position
        if (widgetNewParent && widgetNewParent.transformed_position) {
            this.transformChildren(widgetNewParent);
        }
    }

    selectJstreeItem(id) {
        this.currentElementsSelection[0] = id;
        var currenetRenderedPropertiesId = $('#selected-element').attr('data-element-id');

        if (currenetRenderedPropertiesId !== id &&
            id !== undefined) {

            this.clearSelectedElements();

            var jsTree = $('#sceneTree').jstree();

            jsTree.deselect_all();
            jsTree.search(id);
            jsTree.select_node($('li[data-id="' + id + '"]'));
        }

        this.Timeline.unselectAllKeyfames();
        this.Timeline.toggleElementTimeline($('#' + id), true);
    }

    /**
     *
     * @param ids
     */
    selectMultiJstreeItems(ids) {
        let selectedIds = ids.filter((id) => id !== null);
        let cloneIds = selectedIds.slice(0);
        this.currentElementsSelection = [];

        this.currentElementsSelection = selectedIds;
        this.currentParentElementsSelection = this.findParentElementsSelection(cloneIds);
        this.interactElementsState = 'resize';


        if (selectedIds.length === 0) {
            this.clearSelectedElements();
        } else if (selectedIds.length === 1) {
            this.selectJstreeItem(selectedIds[0]);
        } else {
            this.highlightSelectedEl(null, selectedIds);
        }
    }

    /**
     * Clear selection of all elements on the scene
     * @function
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     */
    clearSelectedElements() {
        this.closeColorPickers();
        this.blurOutTextInputs();

        $('#edit-menu').find('.btn-dynamic').addClass('disabled');

        var jsTree = $('#sceneTree').jstree();
        jsTree.deselect_all(true);
        jsTree.redraw(true);

        $('mask').remove();
        $('.transform-origin-point').remove();
        $('#selected-element').remove();
        this.propertiesElBar.empty();
        this.currentElementsSelection.length = 0;
        this.currentParentElementsSelection.length = 0;
        this.Timeline.unhighlightAllTracks();
        this.Timeline.unselectAllKeyfames();
        this.WidgetSelection.setSelectedState(false);
        this._setRightClickHandling(true);
    }

    _sceneJsTree(data) {
        var _this = this;
        var inWidgetEditor = couiEditor.openFiles[_this.editorTabName].tab.tabWidgetState.editWidget;
        $('#sceneTree').jstree({
            core: {
                rules: {
                    creatable: true
                },
                data: data,
                multiple: true,
                html_titles: true,
                load_open: inWidgetEditor,
                check_callback: function (operation, node, node_parent, node_position, more) {
                    if (node_parent.id !== '#') {
                        const widget = document.getElementById(node_parent.id).innerText.split(' ')[0];
                        const preventNesting = _this.mappedWidgets[widget].widget.type === 'widget';

                        if (preventNesting && !couiEditor.openFiles[_this.editorTabName].tab.tabWidgetState.editWidget) {
                            return false;
                        }
                    }
                }
            },
            dnd: {
                check_while_dragging: true,
                inside_pos: 'last'
            },
            plugins: ['dnd', 'search'],

        }).on('ready.jstree', function (e, data) {
            var jsTree = $('#sceneTree').jstree();
            var allNodes = jsTree.get_node('#');

            _this.widgetVisualSceneState(allNodes);
            _this.jstreeDNDHandler();

            if (inWidgetEditor) {
                jsTree.open_all();
            }
            _this.rightPanel.scrollTop(_this.tab.scrollIndex.rightPanel);
        }).on('create_node.jstree', function (e, data) {
            var allNodes = $('#sceneTree').jstree().get_node('#');
            _this.widgetVisualSceneState(allNodes);
        }).on('move_node.jstree', function (e, data) {
            let $jsTree = $(this);
            let newParentDataId = 'top';
            let oldParentDataId = 'top';
            let oldParentId = data.old_parent;
            let $parentElNew = $(_this.scenePreviewEl);
            let currentNodeId = data.node['li_attr']['data-id'];
            let oldParentPos = data['old_position'];
            let position = data.position;
            let $currentEl = $('#' + currentNodeId);
            let currentParentId = data.parent;
            let elementAbsolutePosition = helpers.getAbsolutePosition($currentEl[0], 'scene');

            if (currentParentId !== '#') {
                newParentDataId = $jsTree.find('#' + currentParentId).attr('data-id');
                // more flexible way of selecting an element by id
                // handling cases where the id have whitespace in it
                $parentElNew = $(`[id='${newParentDataId}']`);
                if (helpers.isVoidElement($parentElNew[0])) {
                    vex.dialog.alert({
                        closeOnOverlayClick: true,
                        contentClassName: 'modal-about',
                        message: 'Void dom elements cannot be nested into!'
                    });
                    $jsTree.jstree().move_node(data.node, oldParentId, oldParentPos);
                    $jsTree.jstree().redraw(true);
                    return;
                }
            }

            if (oldParentId !== '#') {
                oldParentDataId = $jsTree.find('#' + oldParentId).attr('data-id');
            }

            _this.selectJstreeItem(currentNodeId);
            _this.moveWidget(currentNodeId, oldParentDataId, newParentDataId, position, oldParentPos);
            _this.moveWidgetScenePreview($currentEl, $parentElNew, position, oldParentPos);

            $jsTree.jstree().open_node(currentParentId);

            _this.highlightSelectedEl($currentEl);
            _this._childElMoveCheck(currentNodeId, newParentDataId, oldParentDataId);

            // undo/ redo
            var actionState = _this.getRedoUndoPrimaryState();

            var moveInfo = {
                currentParentJstreeId: currentParentId,
                oldParentJstreeId: oldParentDataId,
                position: position,
                oldPosition: oldParentPos
            };

            if (_this._sceneActionState.primaryAction === 'new action') {
                _this._undoCreationStepsLength = 3;
                _this.repositionElementAfterReorder($currentEl[0], $parentElNew[0], elementAbsolutePosition);
            }

            _this._sceneActionState.moveElement = true;
            _this.createUndoRedoCommand(actionState, currentNodeId, null,
                null,
                null, moveInfo);

            $jsTree.jstree().redraw(true);

        }).on('select_node.jstree', function (e, data) {
            _this.Timeline.unhighlightAllTracks();
            _this.Timeline.unselectAllKeyfames();

            var widgetId = data.node['li_attr']['data-id'];
            var currenetRenderedPropertiesId = $('#selected-element').attr('data-element-id');
            if (currenetRenderedPropertiesId !== widgetId) {
                // set current selection
                _this.currentElementsSelection[0] = widgetId;
                _this.currentParentElementsSelection[0] = widgetId;
                let $element: any = $('#' + widgetId);
                _this.highlightSelectedEl($element);
                _this.Timeline.toggleElementTimeline($element, true);

                // check creation method of the element
                // if is by dragging create properties bar after drag end
                if (!_this._sceneClickingState.draggingCreation) {
                    _this.renderProperties(_this.mappedWidgets[widgetId].widget);
                }
            }
        }).on('hover_node.jstree', function (e, data) {
            if (!_this._dragFlag) {
                var widgetId = data.node['li_attr']['data-id'];
                _this.interactElementsState = 'resize';
                let $element: any = $('#' + widgetId);
                _this.highlightSelectedEl($element);
            }
            _this._dragFlag = false;
        }).on('dehover_node.jstree', function (e, data) {
            var selectedNode = $('#sceneTree').jstree('get_selected');
            if (selectedNode.length > 0) {
                var id = $('#' + selectedNode[0]).attr('data-id');
                let $element: any = $('#' + id);
                _this.highlightSelectedEl($element);
            } else {
                $('.element-blink').removeClass('element-blink');
            }
        }).on('open_node.jstree', function (e, data) {
            var $currentNode = $('#' + data.node.li_attr['data-id']);
            var widget = ($currentNode.attr('data-type') === 'widget');
            if (widget && !inWidgetEditor) {
                $('#sceneTree').jstree().close_node(data.node.id);
            }
            _this.widgetVisualSceneState(data.node);
        }).on('redraw.jstree', function () {
            var allNodes = $('#sceneTree').jstree().get_node('#');
            _this.widgetVisualSceneState(allNodes);
        });
    }

    widgetVisualSceneState(node) {
        var childrenNode = node.children_d;
        var len = childrenNode.length;
        var ids = [];

        for (var i = 0; i < len; i++) {
            var jstreeNode = $('#' + childrenNode[i]);
            var id = jstreeNode.attr('data-id');
            var el = $('#' + id);
            var lock = el.attr('data-lock');
            var visibility = el.attr('data-visibility');
            if (lock === 'true') {
                $('#' + childrenNode[i] + ' > a .lock-widget')
                    .addClass('active');
            } else {
                el.attr('data-lock', 'false');
            }
            if (visibility === 'false') {
                $('#' + childrenNode[i] + ' > a .visibility-widget')
                    .addClass('active');
            } else {
                el.attr('data-visibility', 'true');
            }
        }
        this.visibilityWidgetHandler();
        this.lockWidgetHandler();
    }

    onRotateElStart() {
        this.closeColorPickers();
        this._undoCreationStepsLength = 1;
        $('.rotate-corners').addClass('disable-click');
        this._dragFlag = false;

    }

    onRotateElMove() {
        const element = document.getElementById(this.getSelectedWidgetId());
        this._dragFlag = true;
        this._isElementRotated = true;
        this._sceneActionState.primaryAction = 'new action';

        this.computeSelectCorners($(element));
    }

    onRotateElEnd(element, rotationInfo) {
        if (this._isElementRotated) {
            $('.rotate-corners').removeClass('disable-click');
            this.saveProperties(element.id, 'transform', 'rotateZ', rotationInfo.deg + 'deg');
            var numInputRot = $('input[data-property-key="rotateZ"]');
            numInputRot.val(parseInt(rotationInfo.deg));
            this._isElementRotated = false;

        }
    }

    onDragElStart() {
        this.closeColorPickers();
        this._dragFlag = false;
        this._sceneActionState.primaryAction = 'start dragging';
        this.pendingTransformOriginPoint = true;

        // TODO remove this when mousemove is fixed!!!
        window.startMouseX = mouseCoordsX;
        window.startMouseY = mouseCoordsY;
    }

    onDragElMove() {
        this.prepareTransformOriginPosition();
        this.hasElementBeenDragged();
    }

    onDragElEnd() {
        if (this._dragFlag) {

            this.setUndoRedoShift(2);

            var len = this.currentParentElementsSelection.length;
            for (var i = 0; i < len; i++) {
                var element = document.getElementById(this.currentElementsSelection[i]);
                if (this.currentParentElementsSelection[i] !== null) {
                    this.onInteractEnd(element);
                }
            }
        }
        this.pendingTransformOriginPoint = false;
    }

    onResizeElStart(element) {
        this.closeColorPickers();
        this._dragFlag = false;
        this._undoCreationStepsLength = 4;
    }

    onResizeElMove(element) {
        const widget = this.mappedWidgets[element.id].widget;
        this.sceneWrapper.panzoom('disable');
        this._dragFlag = true;
        this.updateTransformOriginPoint(widget);
        this._sceneActionState.primaryAction = 'new action';
    }

    onResizeElEnd(element, event) {
        this.sceneWrapper.panzoom('enable');
        this.onInteractEnd(element);

        var id = element.id;
        var widget = this.mappedWidgets[id].widget;
        var unitW = helpers.getUnitStyle(widget.geometry.width);
        var unitH = helpers.getUnitStyle(widget.geometry.height);

        var elWidth = parseFloat(element.style.width);
        var elHeight;
        if (event.shiftKey && this.equalProportion) {
            elHeight = elWidth;
            this.equalProportion = false;
        } else {
            elHeight = parseFloat(element.style.height);
        }

        var width = unitsConvertor.convertPixelToUnit(id, elWidth, unitW, 'width');
        var height = unitsConvertor.convertPixelToUnit(id, elHeight, unitH, 'height');

        if (unitH === 'auto') {
            this._undoCreationStepsLength = this._undoCreationStepsLength - 1;
        }

        if (unitW === 'auto') {
            this._undoCreationStepsLength = this._undoCreationStepsLength - 1;
        }

        if (unitH !== 'auto') {
            var currentHeight = widget.geometry.height;
            if (currentHeight !== height) {
                var heightToSave = parseFloat(height);
                var heightToApply = unitsConvertor.convertUnitsToPixel(id, heightToSave + unitH, 'height');
                this.saveProperties(id, 'geometry', 'height', heightToSave.toFixed(this.formatTo + 1) + unitH);
                this.applyProperties(id, 'css property', 'height', heightToApply);
                this.setUnitNumberToolbar('height', heightToSave.toFixed(this.formatTo + 1) + unitH);
            } else {
                this.adjustUndoRedoStepsLen();
            }
        }

        if (unitW !== 'auto') {
            var currentWidth = widget.geometry.width;
            if (currentWidth !== width) {
                var widthToSave = parseFloat(width);
                var widthToApply = unitsConvertor.convertUnitsToPixel(id, widthToSave + unitW, 'width');
                this.saveProperties(id, 'geometry', 'width', widthToSave.toFixed(this.formatTo + 1) + unitW);
                this.applyProperties(id, 'css property', 'width', widthToApply);
                this.setUnitNumberToolbar('width', widthToSave.toFixed(this.formatTo + 1) + unitW);
            } else {
                this.adjustUndoRedoStepsLen();
            }
        }

        if (unitH === '%') {
            element.style.height = height;
        }

        if (unitW === '%') {
            element.style.width = width;
        }

        let $element: any = $('#' + id);
        this.highlightSelectedEl($element);
    }

    prepareTransformOriginPosition() {
        if (this.currentElementsSelection.length === 1) {
            const id = this.getSelectedWidgetId();
            const widget = this.mappedWidgets[id].widget;
            this.updateTransformOriginPoint(widget);
        }
    }

    setUndoRedoShift(undoMultiplier) {
        this._undoCreationStepsLength = this.currentParentElementsSelection.filter(function (value) {
            return value !== null;
        }).length * undoMultiplier;
    }

    repositionElementAgainstParent(element) {
        var elStyle = element.style;
        var left = parseFloat(elStyle.left);
        var top = parseFloat(elStyle.top);
        var elLeft = left - element.parentElement.offsetLeft;
        var elTop = top - element.parentElement.offsetTop;

        elStyle.left = elLeft + 'px';
        elStyle.top = elTop + 'px';

        this.elementReposition = false;

        this.selectJstreeItem(element.id);
    }

    /**
     *
     * @param element
     * @param parentElement
     * @param {object} elementAbsolutePosition
     */
    repositionElementAfterReorder(element: HTMLElement, parentElement: HTMLElement, elementAbsolutePosition: any): void {
        interface IParentPos {
            left: number;
            top: number;
        }

        let parentAbsolutePosition: IParentPos = {
            left: 0,
            top: 0
        };

        if (parentElement && parentElement.id !== 'scene') {
            parentAbsolutePosition = helpers.getAbsolutePosition(parentElement, 'scene');
        }

        let diffX = elementAbsolutePosition.left - parentAbsolutePosition.left;
        let diffY = elementAbsolutePosition.top - parentAbsolutePosition.top;
        var elStyle = element.style;

        elStyle.left = diffX + 'px';
        elStyle.top = diffY + 'px';

        this.recalculatePixelToUnits(element);
        this.selectJstreeItem(element.id);
    }

    onInteractEnd(element) {
        this.Timeline.unselectAllKeyfames();
        this.pendingTransformOriginPoint = false;

        if (element.style.position === 'absolute') {
            var id = element.id;
            var widget = this.mappedWidgets[id].widget;
            var unitX = helpers.getUnitStyle(widget.geometry.left);
            var unitY = helpers.getUnitStyle(widget.geometry.top);

            var elLeft = element.offsetLeft;
            var elTop = element.offsetTop;

            var left = unitsConvertor.convertPixelToUnit(id, elLeft, unitX, 'left');
            var top = unitsConvertor.convertPixelToUnit(id, elTop, unitY, 'top');

            if (unitY === 'auto') {
                this._undoCreationStepsLength = this._undoCreationStepsLength - 1;
            }

            if (unitX === 'auto') {
                this._undoCreationStepsLength = this._undoCreationStepsLength - 1;
            }

            if (unitY !== 'auto') {
                var currentTop = widget.geometry.top;
                if (currentTop !== top) {
                    var topToSave = parseFloat(top);
                    var topToApply = unitsConvertor.convertUnitsToPixel(id, topToSave + unitY, 'top');
                    this.saveProperties(id, 'geometry', 'top', topToSave.toFixed(this.formatTo + 1) + unitY);
                    this.applyProperties(id, 'css property', 'top', topToApply);
                    this.setUnitNumberToolbar('top', topToSave.toFixed(this.formatTo + 1) + unitY);
                } else {
                    this.adjustUndoRedoStepsLen();
                }
            }

            if (unitX !== 'auto') {
                var currentLeft = widget.geometry.left;
                if (currentLeft !== left) {
                    var leftToSave = parseFloat(left);
                    var leftToApply = unitsConvertor.convertUnitsToPixel(id, leftToSave + unitX, 'left');
                    this.saveProperties(id, 'geometry', 'left', leftToSave.toFixed(this.formatTo + 1) + unitX);
                    this.applyProperties(id, 'css property', 'left', leftToApply);
                    this.setUnitNumberToolbar('left', leftToSave.toFixed(this.formatTo + 1) + unitX);
                } else {
                    this.adjustUndoRedoStepsLen();
                }
            }

            if (unitX === '%') {
                // element.style.left = left;
            }

            if (unitY === '%') {
                // element.style.top = top;
            }
        }
    }

    adjustUndoRedoStepsLen() {
        if (this._undoCreationStepsLength > 1) {
            this._undoCreationStepsLength--;
        }
        if (this._skipUndoRedoSteps > 0) {
            this._skipUndoRedoSteps--;
        }
    }

    closeColorPickers() {
        if ($('.color-picker').data('kendoColorPicker') !== undefined) {
            $('body').off('mouseup');
            $('.color-picker').data('kendoColorPicker').close();
        }
    }

    interactHandler(elementId) {
        var element = document.getElementById(elementId);

        Interact(element, this.editorTabName, this.interactElementsState, this.createElementEvent);
    }

    hasElementBeenDragged() {
        // TODO 'if' remove this when mousemove is fixed!!!
        if (startMouseX !== mouseCoordsX && startMouseY !== mouseCoordsY) {
            this._dragFlag = true;
            this._sceneActionState.primaryAction = 'new action';
        }
    }

    displayVideos(videoElements) {
        for (var i = 0; i < videoElements.length; i++) {
            this.displayVideo(videoElements[i]);
        }
    }

    displayVideo(element) {
        var _this = this;
        $(element).one('canplay', function () {
            this.volume = 0;
            this.play();
            $(this).one('timeupdate', function () {
                _this.stopVideo(this);
            });
        });
    }

    stopVideo(element) {
        element.pause();
        element.currentTime = 0;
    }

    /**
     * Set scene panning
     * @memberOf module:lib/runtime_editor.RuntimeEditor
     */
    scenePanning() {
        var _this = this;

        this.sceneWrapper.panzoom('option', {
            // reversed for better readability
            disablePan: !_this.isInPanningMode
        });
    }

    setSceneStyleProperty(key, value) {
        var action = {
            sceneProperty: {
                style: {
                    backgroundColor: this.scene.style[key]
                }
            }
        };

        this._setUndoRedoCommandsFill(action);

        this.scene.setStyleProperty(key, value);

        this.exportScene();

        return this;
    }

    recalculatePixelToUnits(element: HTMLElement): void {
        let widget = this.mappedWidgets[element.id].widget;
        let elStyle = element.style;
        let topUnit = helpers.getUnitStyle(widget.geometry.top);
        let leftUnit = helpers.getUnitStyle(widget.geometry.left);

        let elTop = parseFloat(elStyle.top);
        let elLeft = parseFloat(elStyle.left);

        let newValueTop = unitsConvertor.convertPixelToUnit(element.id, elTop, topUnit, 'top');
        let newValueLeft = unitsConvertor.convertPixelToUnit(element.id, elLeft, leftUnit, 'left');

        this._setGeometry(null, widget, widget.id, 'geometry', 'top', newValueTop);
        this._setGeometry(null, widget, widget.id, 'geometry', 'left', newValueLeft);
    }

    recalculateUnits(element) {
        var id = element ? element.id : '';

        if (!id || this.mappedWidgets[id] === undefined) {
            return;
        }

        var widget = this.mappedWidgets[id].widget;

        var styleProps: any = {};
        if (widget.geometry !== undefined) {

            if (widget.geometry.top) {
                styleProps.top = widget.geometry.top;
            }
            if (widget.geometry.left) {
                styleProps.left = widget.geometry.left;
            }
            if (widget.geometry.width) {
                styleProps.width = widget.geometry.width;
            }
            if (widget.geometry.height) {
                styleProps.height = widget.geometry.height;
            }
            if (widget.fontSize) {
                styleProps.fontSize = widget.fontSize;
            }
            if (widget.styles.borderWidth) {
                styleProps.borderWidth = widget.styles.borderWidth;
            }
            if (widget.styles.backgroundPositionX) {
                styleProps.backgroundPositionX = widget.styles.backgroundPositionX;
            }
            if (widget.styles.backgroundPositionY) {
                styleProps.backgroundPositionY = widget.styles.backgroundPositionY;
            }
            if (widget.styles.backgroundSize) {
                if (widget.styles.backgroundSize === 'auto') {
                    styleProps.backgroundSize = widget.styles.backgroundSize;
                }
            }

            for (var prop in styleProps) {
                if (prop === 'backgroundSize') {
                    if (styleProps[prop] === 'auto') {
                        var values = element.style[prop].split(' ');

                        var width;
                        var height;

                        if (values.length === 1) {
                            width = height = values[0].trim();
                        } else {
                            width = values[0].trim();
                            height = values[1].trim();
                        }

                        var widthUnit = helpers.getUnitStyle(width);
                        var heightUnit = helpers.getUnitStyle(height);

                        var widthVal;
                        var heightVal;

                        if (widthUnit !== '%') {
                            widthVal = unitsConvertor.convertUnitsToPixel(id, width, 'backgroundSizeWidth');
                        }

                        if (heightUnit !== '%') {
                            heightVal = unitsConvertor.convertUnitsToPixel(id, height, 'backgroundSizeHeight');
                        }

                        widget.styles.backgroundSizeWidth = width;
                        widget.styles.backgroundSizeHeight = height;
                        element.style[prop] = widthVal + ' ' + heightVal;
                    }
                } else {
                    var unit = helpers.getUnitStyle(styleProps[prop]);
                    if (unit !== '%') {
                        element.style[prop] = unitsConvertor.convertUnitsToPixel(id, element.style[prop], prop);
                    }
                }
            }
        }
    }


    colorPickerLeftReposition() {
        setTimeout(function () {
            $('.k-animation-container').css('left', '6px');
        }, 50);
    }

    colorPickerRemoveLeftReposition() {
        $('.k-animation-container').css('left', 'auto');
    }

    scrollDown(div) {
        if (div.scrollTop < div.scrollHeight - div.clientHeight) {
            div.scrollTop += 20; // move down
        }
    }

    isColorbackground(backgroundValue) {
        if (backgroundValue !== undefined) {
            // check background value string starts with
            if (backgroundValue.startsWith('url')) {
                return 'rgba(255,255,255,1);';
            } else {
                return backgroundValue;
            }
        }
    }

    hideShowEventParams($this, elementToShow?) {
        var $eventWrapper = $this.parents('.events-wrap');

        var elements = {
            blueprintFunction: $eventWrapper.find('.blueprint-events'),
            engineCall: $eventWrapper.find('.enginecall-events'),
            engineTrigger: $eventWrapper.find('.enginetrigger-events'),
            javascriptFunction: $eventWrapper.find('.javascript-events'),
        };

        for (var element in elements) {
            elements[element].hide();
        }

        if (elements[elementToShow]) {
            elements[elementToShow].show();
        }
    }
}
