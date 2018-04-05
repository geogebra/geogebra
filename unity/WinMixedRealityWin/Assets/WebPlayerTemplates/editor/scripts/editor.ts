/* globals importCss */

'use strict';

declare let $;
declare let ace;
declare let vex;
declare let System;

declare let w2ui;
declare let document;

import Enums from 'lib/enums';
import keyHandlers from 'lib/key_handlers';
import helpers from 'lib/function_helpers';
import importCssModule from 'lib/animations/importCss';
import buildWidgetHandler from 'lib/build_widget_settings';
import editorSettings from 'lib/editor_settings';
import Components from 'lib/components/components';

/**
 * newFileCounter
 * @type {number}
 */
let newFileCounter = 0;

export namespace coui {
    let MINIMAL_SELECTED_ELEMENTS_FOR_CREATING_A_WIDGET = 1;

    let TabStates = {
        open: 0,
        pendingClose: 1,
        safelyClosable: 2
    };

    let animationWidgetsIdsToUpdate = {};
    let appliedWidgetIds = {};

    /**
     * Track if panning state should stay active or not
     * @type {boolean}
     */
    let keepPanningMode = false;

    /**
     * This is a description of the
     *
     * @memberof coui
     * @constructor Editor
     * @constructs
     */
    export class Editor {

        public Handlebars: any;
        public EDITOR_VERSION: any[];
        public globalEditorInfo: any;
        public environmentProperties: any;

        public EXPORTING_WIDGET: boolean;
        public EXPORTING_COMPONENT: boolean;
        public PENDING_WIDGET_EXPORT: boolean;
        public PENDING_WIDGET_LOAD: boolean;
        public PENDING_SCENE_LOAD: boolean;
        public PENDING_PUBLISH_PAGE_LOAD: boolean;
        public openedTabsTypes: IOpenedTabsTypes;
        public preferences: IPreferences;

        _isClosingInOrder: boolean;
        openFiles: any;
        selectedEditor: any;
        autoReload: boolean;
        autoReloadCallback: any;
        $wrapperFilesEl: any;
        $wrapperCodeEditor: any;
        $wrapperRuntimeEditor: any;
        widgetCount: number;
        _isClosingAllTabs: boolean;
        _isExitingEditor: boolean;
        fileId: number;
        reloadedRuntimeEditor: number;
        assets: any;
        copiedWidgets: ICopiedWidgets;
        animationsBackwardsCompatibility: any;
        animationBelongsTo: string;
        currentOpenedTabs: any;
        isBasicPanelOpen: boolean;
        onclose: any;
        onExportWidget: any;
        onsave: any;
        onreload: any;
        oncloseEditor: any;
        onopen: any;
        onLaunchUrl: any;
        detachedSceneHTML: any;
        detachedAmnimationsHTML: any;
        listDirectory: any;
        openAsset: any;
        pickAsset: any;
        onSelectedFileType: IOnSelectedFile;
        sceneSettings: any;
        __mainSceneIdToBeClosed: any;
        classNamesCount: number;
        missingWidgets: Array<string>;

        constructor() {
            window['LOADING_SCREEN_ON'] = false;

            this.missingWidgets = [];
            this.__mainSceneIdToBeClosed = null;
            this.PENDING_PUBLISH_PAGE_LOAD = false;
            this.PENDING_WIDGET_EXPORT = false;
            this.PENDING_WIDGET_LOAD = false;
            this.EXPORTING_WIDGET = false;
            this.PENDING_SCENE_LOAD = false;
            this.EXPORTING_COMPONENT = false;
            this._isClosingInOrder = false;
            this.openFiles = {};
            this.openedTabsTypes = {
                scenes: [],
                components: []
            };
            this.selectedEditor = null;
            this.autoReload = true;
            this.autoReloadCallback = function () {
                console.log('auto 1');
            };
            // used to check what we are going to do after select the file from backend modal window
            // this logic is used in engine.on('selectedFile')
            this.onSelectedFileType = {
                createNewScene: false,
                publishPage: false
            };
            this.$wrapperFilesEl = $('#wrapper-files');
            this.$wrapperCodeEditor = $('#wrapper-editor');
            this.$wrapperRuntimeEditor = $('#wrapper-runtime-editor');
            this.widgetCount = 0;
            // Whether we are currently in the process of closing all tabs
            this._isClosingAllTabs = false;
            this._isExitingEditor = false;
            this.fileId = 1;
            this.reloadedRuntimeEditor = 0;
            this.assets = {
                image: [],
                video: [],
                sound: [],
                widget: [],
                style: [],
                script: [],
                font: [],
            };
            /**
             * Holds arrays widgets, elements and animations
             * @property {object} widgets
             * @property {object} animationsToPaste
             * @property {object} animations
             */
            this.copiedWidgets = {
                widgets: {},
                animationsToPaste: {},
                animations: {},
                tabName: ''
            };
            /**
             *
             * @type {{idsToClasses: Array}}
             */
            this.animationsBackwardsCompatibility = {
                idsToClasses: []
            };

            /**
             *
             * @type {string}
             */
            this.animationBelongsTo = 'scene';

            /**
             * Keep track of the open kendoPanel panels
             * @type {Array}
             */
            this.currentOpenedTabs = [];

            /**
             * Current classNames counter for the tab
             * @property {number} classNamesCount
             */
            this.classNamesCount = 0;

            /**
             * Toggle the open state of the basic properties kendo panel
             * This panel is opened by default
             * @type {boolean}
             */
            this.isBasicPanelOpen = true;

            this.detachedAmnimationsHTML = null;
            this.detachedSceneHTML = null;

            /**
             * Store new scene settings - background color, width, height and type
             * @type {Object}
             */
            this.sceneSettings = {};
        }

        init() {
            this.preferences = editorSettings.defaultPreferences;
            this._displayToolbar();
            this._displayEditMenu();
            this._displayMenu();
            this._tabsHandlers();
            this._attachMouseCoordsHandlers();
            this.initVexFlashMessages();
        }

        attachEditorHandlers() {
            var _this = this;

            this.onsave = function (filename, fileContents) {
                // console.log('Saving: ' + fileContents);
                if (this.openFiles[this.selectedEditor].tab.originalFullPath === 'widgets/') {
                    filename = 'widgets/' + filename;
                }
                engine.call('Save', filename, fileContents);
            };

            this.onExportWidget = function (filename, fileContents) {
                this.EXPORTING_WIDGET = false;
                return engine.call('ExportWidget', filename, fileContents);
            };

            this.onreload = function (filename) {
                // console.log('Reloading: ' + filename);
                engine.call('Reload', filename);
            };

            this.onclose = function (filename) {
                this.syncTabs();
                // console.log('Closing: ', filename);
                engine.call('Close', filename);
            };

            this.onLaunchUrl = function (url) {
                engine.call('LaunchURL', url);
            };

            this.oncloseEditor = function () {
                // console.log('Closing editor');
                engine.call('CloseEditor');
            };

            this.onopen = function () {
                engine.call('OpenFile');
            };

            this.listDirectory = function (directory, regex, recursive) {
                return engine.call('ListDirectory', directory, regex, recursive);
            };

            this.openAsset = function ({path, name, content}: IOpenAsset) {
                if (content) {
                    return engine.trigger('LoadFile', name, content);
                } else {
                    return engine.call('OpenAsset', path);
                }
            };

            this.pickAsset = function (widgetId, type) {
                var envExtensions = _this.environmentProperties.DefaultExtensions[type];

                var _tempString = envExtensions.map(function (result) {
                    return '*.' + result;
                }).join(';');
                _tempString = 'Coherent files (' + _tempString + ';):' + _tempString + ';';

                return engine.call('PickAssetWithExtensions', widgetId, type, _tempString);
            };
        }

        attachBrowserHandlers() {
            let _this = this;
            System.import('uiresources/one.html!text').then(function (data) {
                _this.openFile(data, 'MainUI.html');
                // Inform the the editor is ready for use
                //document.body.dispatchEvent(new Event('couiEditorIsReadyForUse'));
            });
        }

        buildTestScene(commands, callback) {
            var _this = this;
            var dfd = $.Deferred();
            var timeout;

            this.openFiles[this.selectedEditor].redo = commands;

            function doRedo() {
                timeout = setTimeout(function () {
                    _this.openFiles[_this.selectedEditor].runtimeEditor.undoRedoScene('redo');

                    if (_this.openFiles[_this.selectedEditor].redo.length === 0) {
                        return dfd.resolve();
                    } else {
                        return doRedo();
                    }
                }, 50);
            }

            doRedo();

            return dfd.promise();
        }

        runCommands(commands, options) {
            var _this = this;
            var dfd = $.Deferred();
            var timeout;

            this.openFiles[_this.selectedEditor].redo = commands;

            var logDiff = function (originalHtml, newHtml) {
                System.import('bower_components/jsdiff/diff.min').then(function (JsDiff) {
                    var diff = JsDiff.diffWordsWithSpace(originalHtml, newHtml);
                    var colorParams = [];
                    var result = '';

                    for (var i = 0; i < diff.length; i++) {
                        result += '%c' + diff[i].value;
                        if (diff[i].removed) {
                            colorParams.push('color: red');
                        } else if (diff[i].added) {
                            colorParams.push('color: green');
                        } else {
                            colorParams.push('color: grey');
                        }
                    }

                    console.log.apply(console, [].concat(result, colorParams));
                });
            };

            function doRedo() {
                timeout = setTimeout(function () {

                    _this.openFiles[_this.selectedEditor].runtimeEditor.undoRedoScene('redo');

                    if (_this.openFiles[_this.selectedEditor].redo.length === 0) {
                        _this.openFiles[_this.selectedEditor].runtimeEditor.exportScene();
                        var jsonData = _this.openFiles[_this.selectedEditor].file;
                        return _this.openFiles[_this.selectedEditor]
                            .runtimeEditor.runtimeLoad(jsonData, {
                                sceneSave: true
                            }).then(function (data) {
                                var mergedHTML = _this.rebuildUserHTML(data);
                                var newHtml = _this.cleanupHtmlExport(mergedHTML).replace(/\r/g, '').trim();
                                var originalFile = options.originalFile;
                                return System.import(originalFile + '!text').then(function (result) {
                                    var originalHtml = result.replace(/\r/g, '').trim();
                                    clearTimeout(timeout);

                                    if (originalHtml !== newHtml) {
                                        console.error('functional test failed!');
                                    } else {
                                        console.log('functional test passed!');
                                    }

                                    dfd.resolve(originalHtml === newHtml);
                                    logDiff(originalHtml, newHtml);
                                });
                            });

                    } else {
                        return doRedo();
                    }

                }, 50);
            }

            doRedo();

            return dfd.promise();
        }

        /**
         * Attach x and y mouseCoords handlers
         * @private
         */
        _attachMouseCoordsHandlers() {
            document.addEventListener('mousemove', function (e) {
                window.mouseCoordsX = e.pageX;
                window.mouseCoordsY = e.pageY;
            }, false);
        }

        /**
         * Toogle widget editing buttons
         * @public
         */
        widgetEditingHandlers() {
            var widgetOn = this.openFiles[this.selectedEditor].tab.tabWidgetState.editWidget;
            $('.btn-export-widget').toggle(!widgetOn);
            $('.btn-createWidget-scene').toggle(!widgetOn);
            $('.widgets-content').toggle(!widgetOn);
            return this;
        }

        adjustPreferences() {
            var _this = this;

            if (this.globalEditorInfo.backend !== Enums.Backends.Debug &&
                this.globalEditorInfo.backend !== Enums.Backends.Website) {

                var hasPassed;
                var clonedDropDown;

                var preferences = engine.call('prefs.get', 'preferences');
                var currentEnvironment = preferences.result.couiEnvironment;

                var $vex = vex.dialog.open({
                    closeOnOverlayClick: true,
                    contentClassName: 'modal-about',
                    message: 'Environment is ' + currentEnvironment + '. If changed the editor will be reset!',
                    buttons: [
                        $.extend({}, vex.dialog.buttons.NO, {
                            text: 'Ok',
                            click: function () {
                                hasPassed = true;
                                $vex.data().vex.value = false;
                                vex.close($vex.data().vex.id);
                            }
                        }),
                        $.extend({}, vex.dialog.buttons.NO, {
                            text: 'Cancel',
                            click: function () {
                                hasPassed = false;
                                $vex.data().vex.value = false;
                                vex.close($vex.data().vex.id);
                            }
                        })
                    ],
                    afterOpen: function ($vexContent) {
                        hasPassed = false;
                        var template = document.querySelector('#vex-preferences-set');

                        var clone = _this.importHtmlTemplate(template);

                        //TODO: Add when functionality is available.//
                        // var folderSelector = clone.querySelector('#ui-folder-selector');
                        // folderSelector.id = 'new-ui-folder-selector';
                        // var folderButton = clone.querySelector('#ui-folder-browse');
                        // folderButton.id = 'new-ui-folder-browse';

                        var currentKendoDropdown = clone.querySelector('#environment-dropdownlist');

                        var supportedEnvironments = ['GT', 'Hummingbird'];
                        supportedEnvironments.splice(supportedEnvironments.indexOf(currentEnvironment), 1);
                        supportedEnvironments.unshift(currentEnvironment);

                        $(currentKendoDropdown).kendoDropDownList({
                            dataSource: supportedEnvironments,
                            animation: false
                        });

                        clonedDropDown = clone.querySelector('[aria-activedescendant]');
                        clonedDropDown.id = 'new-editor-environment-set';

                        //TODO: Add when functionality is available.
                        // $(folderButton).on('click', function(event) {
                        //     var folderWasSelected = engine.call('SetUserResourcesFolder');
                        // });

                        $vexContent.append(clone);
                    },
                    callback: function (data) {
                        if (hasPassed) {
                            var preferencesDropdown = $('#' + clonedDropDown.id).find('#environment-dropdownlist')
                                .data('kendoDropDownList');
                            var environment = preferencesDropdown.span[0].textContent;

                            if (currentEnvironment !== environment) {
                                _this.changeEngineEnv(environment);
                            }
                        }
                    }
                });
            } else {
                vex.dialog.alert({
                    closeOnOverlayClick: true,
                    contentClassName: 'modal-about',
                    message: 'Environment preference settings are not available in the Web!'
                });
            }
        }

        tabEdited(edited, editorId?) {
            var tabs = w2ui.tabs.tabs;
            var id = editorId || this.selectedEditor;
            id = id.replace('editor', '');
            for (var i = 0; i < tabs.length; i++) {
                var currentTab = tabs[i];
                if (currentTab.id.replace('tab', '') === id) {
                    var text = currentTab.text.replace(/()\*+/g, '');
                    if (edited) {
                        currentTab.caption = currentTab.text = '*' + text;
                    } else {
                        currentTab.caption = currentTab.text = text;
                    }
                    w2ui.tabs.refresh('tab' + id);
                    break;
                }
            }
        }

        refreshTab() {
            let editorId = this.selectedEditor;
            let currentEditor = this.openFiles[editorId];
            let runtimeEditor = currentEditor.runtimeEditor;
            this.handleFileContent(JSON.stringify(runtimeEditor.scene),
                currentEditor.tab.filename, editorId, false, false);
        }

        wasTabEdited(editorId) {
            var tabs = w2ui.tabs.tabs;
            var id = editorId.replace('editor', '');
            for (var i = 0; i < tabs.length; i++) {
                var currentTab = tabs[i];
                if (currentTab.id.replace('tab', '') === id) {
                    return currentTab.text.indexOf('*') !== -1;
                }
            }
        }

        _tabsHandlers() {
            let _this = this;
            $('#tabs').w2tabs({
                name: 'tabs',
                tabs: [],
                onClose: function (event) {

                    let idOfClickedTab = event.object['data-id'];
                    let editorId = 'editor' + idOfClickedTab;
                    let editor = _this.openFiles[editorId];
                    let filename = editor.tab.filename;
                    let state = editor.tab.state;
                    const isComponent: boolean = filename.endsWith('.component');

                    if (!isComponent && editor.components && editor.components.state.opened.length !== 0) {
                        _this.closeTabsInOrder(event.target);
                        event.preventDefault();
                        return;
                    }

                    let allreadySaved = true;

                    if (state !== TabStates.safelyClosable && _this.wasTabEdited(editorId)) {

                        let vexButtons = [
                            $.extend({}, vex.dialog.buttons.NO, {
                                text: 'Save',
                                click: function () {
                                    $vex.data().vex.value = true;
                                    vex.close($vex.data().vex.id);
                                }
                            }),
                            $.extend({}, vex.dialog.buttons.NO, {
                                text: 'Discard',
                                click: function () {
                                    $vex.data().vex.value = false;
                                    vex.close($vex.data().vex.id);
                                }
                            }),
                            $.extend({}, vex.dialog.buttons.NO, {
                                text: 'Cancel',
                                click: function () {
                                    $vex.data().vex.value = 'cancel';
                                    vex.close($vex.data().vex.id);
                                    _this._isClosingAllTabs = false;
                                }
                            })
                        ];

                        if (_this._isExitingEditor === true) {
                            vexButtons.splice(2, 1);
                        }

                        let $vex = vex.dialog.open({
                            contentClassName: 'modal-about save-modal',
                            message: 'Do you want to save ' + filename + ' ?',
                            buttons: vexButtons,
                            afterOpen: function () {
                                allreadySaved = false;
                            },
                            callback: function (data) {
                                if (data !== 'cancel') {
                                    if (data) {
                                        _this.openFiles[editorId].tab.state = TabStates.pendingClose;

                                        _this.save(filename, _this.openFiles[editorId].file.valueOf(), null, editorId);
                                        event.preventDefault();
                                        return;
                                    }
                                    _this.clearTab(editorId, event);
                                }
                            }
                        });
                    }

                    if (allreadySaved) {
                        setTimeout(() => {
                            _this.clearTab(editorId, event);
                        }, 100);
                    } else {
                        event.preventDefault();
                        return;
                    }
                },
                onClick: function (event) {
                    const id = event.object['data-id'];
                    const editorId = 'editor' + id;
                    const currentTab = _this.openFiles[editorId];

                    if (editorId !== _this.selectedEditor || !_this.PENDING_SCENE_LOAD) {
                        _this.PENDING_SCENE_LOAD = true;
                        engine.call('SetCurrentSceneURL', _this.openFiles[editorId].tab.filePath);

                        if (!_this._isClosingAllTabs) {
                            window['CURRENT_FILE'] = currentTab.tab.filename;
                            document.body.dispatchEvent(new Event('turnOnLoadingScreen'));
                        }

                        window.requestAnimationFrame(function () {
                            $('.editor').hide();
                            $('#' + editorId).show();

                            var editorContent = currentTab.file.valueOf();

                            document.body.dispatchEvent(new CustomEvent('coui.tab.change'));
                            _this.selectedEditor = editorId;

                            if (currentTab.tab.fileExtension === 'html' &&
                                (_this.usesSceneEditor(editorContent) || _this.photoshopExport(editorContent)) ||
                                currentTab.tab.fileExtension === 'component') {
                                _this.handleFileContent(editorContent,
                                    currentTab.tab.filename, editorId, false, false);
                                _this._initTabsTooltip(id, currentTab.tab.filename);
                            } else {
                                _this.$wrapperRuntimeEditor.hide();
                                _this.$wrapperCodeEditor.show();
                                document.body.dispatchEvent(new Event('couiEditorIsReadyForUse'));
                            }

                        });
                    }
                }
            });
        }

        /**
         *
         */
        initSceneConfigVex() {
            var _this = this;
            var sceneTemplateHbs = '';
            System.import('templates/editor/scene_create_new.hbs!text').then(function (data) {
                sceneTemplateHbs = data;
            });

            $(window).on('createNewScene', function () {
                var vexButtons = [
                    $.extend({}, vex.dialog.buttons.NO, {
                        text: 'OK',
                        click: function () {
                            _this.PENDING_SCENE_LOAD = false;
                            _this.sceneSettings = {
                                backgroundColor: $('.init-scene-background-picker').data('kendoColorPicker').value(),
                                width: $('.init-aspect-ratio-width-custom').val(),
                                height: $('.init-aspect-ratio-height-custom').val(),
                                type: $('select.init-scene-aspect-ratio').val()
                            };

                            const {type, width, height} = _this.sceneSettings;
                            const sceneSize = helpers.getSceneSizeByType(type, width, height);

                            if (_this.globalEditorInfo.backend === Enums.Backends.Debug ||
                                _this.globalEditorInfo.backend === Enums.Backends.Website) {

                                _this.createNewTestScene(false, {
                                    'style': {
                                        'backgroundColor': _this.sceneSettings.backgroundColor
                                    },
                                    'sceneSize': sceneSize
                                });
                            } else {
                                engine.call('ShowFileDialog', {
                                    __Type: 'FileDialogConfig',
                                    extensions: ['.html'],
                                    dialogName: 'Save New Scene As',
                                    initDirectory: '',
                                    fileMustExist: false,
                                    isOpenFile: false,
                                    allowMultiselect: false
                                });
                            }

                            $vex.data().vex.value = true;
                            vex.close($vex.data().vex.id);
                        }
                    }),
                    $.extend({}, vex.dialog.buttons.NO, {
                        text: 'Cancel',
                        click: function () {
                            _this.PENDING_SCENE_LOAD = true;
                            $vex.data().vex.value = false;
                            vex.close($vex.data().vex.id);
                        }
                    })
                ];

                var $vex = vex.dialog.open({
                    contentClassName: 'modal-about',
                    message: 'New file',
                    buttons: vexButtons,
                    afterOpen: function ($vexContent) {

                        var scenePropertiesTemplate = _this.Handlebars.compile(sceneTemplateHbs);
                        var sceneProperties = scenePropertiesTemplate(Enums.newScene);
                        $vexContent.append(sceneProperties);
                        var $aspectRatio = $vexContent.find('.init-scene-aspect-ratio');
                        $aspectRatio.kendoDropDownList({});

                        var $sceneBackgroundColorPicker = $vexContent.find('.init-scene-background-picker');
                        var $aspectRatioCustom = $vexContent.find('.init-aspect-ratio-custom');

                        $sceneBackgroundColorPicker.kendoColorPicker({
                            buttons: false,
                            value: 'rgba(255, 255, 255, 0)',
                            opacity: true
                        });

                        $aspectRatio.on('change', function () {
                            if ($(this).val() === 'aspectRatio_custom') {
                                $aspectRatioCustom.show();
                            } else {
                                $aspectRatioCustom.hide();
                            }
                        });
                    }
                });
            });
        }

        syncTabs(): void {
            const standingTabs = w2ui.tabs.tabs;
            const editorsArray = Object.keys(this.openFiles);

            if (standingTabs.length !== editorsArray.length) {
                const tabFiles = standingTabs.map((obj) => obj.path + obj.text.replace('*', ''));

                const editorFiles = editorsArray.map((value) => {
                    const path = this.openFiles[value].tab.filePath;
                    const filename = this.openFiles[value].tab.filename.replace('*', '');

                    return path + filename;
                });

                for (let i = 0; i < tabFiles.length; i++) {
                    if (editorFiles.indexOf(tabFiles[i]) === -1) {
                        w2ui.tabs.remove(standingTabs[i].id);
                    }
                }
            }
        }

        clearTab(editorId, event) {
            const _this = this;
            const $editor = $('#' + editorId);
            let editor = this.openFiles[editorId];
            const filename = editor.tab.filename;
            const tabs = w2ui.tabs.tabs;
            const isComponent: boolean = filename.endsWith('.component');

            if (isComponent) {
                const linkedEditor = this.openFiles[this.selectedEditor].tab.tabWidgetState.instanceOf ||
                    editor.tab.tabWidgetState.instanceOf;
                this.openFiles[linkedEditor].components.clearOpenedData(editorId);
            }

            delete appliedWidgetIds[editorId];
            localStorage.removeItem(editorId);

            if (w2ui.tabs.active === event.target && tabs.length > 1) {
                // The tab is not the last one and it's the active one
                var activeTabIndex = _this._getActiveTabIndex();
                var nextTabToFocus = (activeTabIndex + 1) % (tabs.length);
                if (this._isClosingInOrder && this.__mainSceneIdToBeClosed) {
                    let nextIdToClose = this.__mainSceneIdToBeClosed;
                    if (this.openFiles[this.__mainSceneIdToBeClosed].components.state.opened.length !== 0) {
                        nextIdToClose = this.openFiles[this.__mainSceneIdToBeClosed].components.state.opened[0];
                    }

                    let tabIdInOrder = nextIdToClose.replace('editor', 'tab');
                    this.focusFileOnClose($editor, tabIdInOrder);
                } else {
                    this.focusFileOnClose($editor, tabs[nextTabToFocus].id);
                }
            } else {
                $editor.remove();
            }

            delete this.openFiles[editorId];

            if (this.onclose !== undefined) {
                this.onclose(filename);
            }
            if (this._isClosingAllTabs) {
                setTimeout(this.closeAllTabs.bind(_this), 100);
            }
            if (this._isClosingInOrder) {
                setTimeout(this.closeTabsInOrder.bind(_this), 100);
            }
            if (tabs.length === 0) {
                this.$wrapperCodeEditor.hide();
            }
        }

        /**
         *
         * @param innerHTML
         * @param fontFaces
         * @returns {any}
         */
        buildHTML(innerHTML, fontFaces?: string) {

            var tempDiv = document.createElement('div');
            tempDiv.className = 'temp-holder';

            tempDiv.innerHTML = innerHTML;
            return this.buildJSON(tempDiv, fontFaces);
        }

        _getActiveTabIndex() {
            var tabs = w2ui.tabs.tabs;
            for (var i = 0; i < tabs.length; i++) {
                if (tabs[i].id === w2ui.tabs.active) {
                    return i;
                }
            }
            return tabs.length - 1;
        }

        focusFileOnClose($editor, tabId) {
            $editor.hide();
            $editor.remove();
            w2ui.tabs.click(tabId);
        }

        /**
         * Places selected assets inside the working Editor
         * @function
         * @memberOf coui
         * @param {array} data - object array
         */
        updateSceneAssets(data) {

            // TODO: ADD FILTERS FOR THE NEW BACKEND STRUCTURE //

            var addURLprefix = false;
            if (this.globalEditorInfo.backend === Enums.Backends.Debug ||
                this.globalEditorInfo.backend === Enums.Backends.Website) {
                addURLprefix = true;
            }

            for (var i = 0; i < data.length; i++) {

                if (!!data[i].isFile && data[i].url.split('.').pop() !== 'meta') {

                    // FOR WIDGETS ONLY - THEY ARE ONLY LOADED ON RUNTIME //
                    if (data[i].__Type === 'FileEntry' || data[i].__Type === 'FEditorFileEntry') {
                        data[i].__Type = helpers.getFileType(data[i].url);
                    }

                    if (data[i].__Type) {
                        if (!data[i].name) {
                            data[i].name = data[i].url.replace(/\//gm, '\\');
                        }

                        var url = data[i].url.split('\\').join('/');
                        data[i].url = addURLprefix ? 'uiresources/' + url : url;

                        this.assets[data[i].__Type].push(data[i]);
                    }
                }
            }

            // Check if there are any open scenes.
            // Prevent error at first time editor open
            if (this.selectedEditor) {
                let components = this.openFiles[this.selectedEditor].components.components;

                // update components
                for (let component in components) {
                    this.assets.widget.push({
                        __Type: 'widget',
                        isFile: false,
                        name: component,
                        url: ''
                    });
                }
            }
        }

        rebuildRuntimeEditor(file, animationObj, fileName, tabId) {
            var _this = this;

            document.body.dispatchEvent(new CustomEvent('coui.editor.rebuild'));

            $('#wrapper-runtime-editor *').each(function () {
                $(this).unbind();
                $(this).off('blur')
                    .off('focus')
                    .off('focusin')
                    .off('focusout')
                    .off('resize')
                    .off('scroll')
                    .off('click')
                    .off('dbclick')
                    .off('mousedown')
                    .off('mouseup')
                    .off('mousemove')
                    .off('mouseover')
                    .off('mouseout')
                    .off('mouseenter')
                    .off('mouseleave')
                    .off('change');
            });

            this.$wrapperCodeEditor.hide();
            this.$wrapperRuntimeEditor.show();

            this.initRuntimeEditorHtml(tabId).then(function () {
                _this.createRuntimeEditor(file, animationObj, tabId);
            });
        }

        cleanCoherentTags(content) {
            var newContent;

            /* tslint:disable */
            var couiAnimationStyle = /((<style[^<]*.CSS Animations Start([\s\S]*?)CSS Animations End*[^>]*<\/style>)[\s\S]*?)/g;
            var regexAspectRatio = /((<script[^<]*.Aspect Ratio Start([\s\S]*?)Aspect Ratio End*[^>]*<\/script>)[\s\S]*?)/g;
            /* tslint:enable */

            if (content) {
                newContent = content.replace(couiAnimationStyle, '').replace(regexAspectRatio, '').trim();
            }

            return newContent;
        }

        /**
         * Change the HTML content of the scene to a ready for export HTML
         * - Change the code outside the coherent comment properly
         * - Add the component wrapper and register code
         * - Add scene's components, if any
         * @param coherentHTML {string} - the current HTML representation of the exported scene
         * @param componentsHtml {string} - the component code
         * @param isExternal {boolean} - is the code coming from widget/component creation function or from file save
         * @returns {string} - the refactored scene HTML representation as string
         */
        rebuildUserHTML(coherentHTML: string, componentsHtml: string = '', isExternal: boolean = false): string {
            const coherentRegex = /<!-- Coherent Editor Start -->([\s\S]*)<!-- Coherent Editor End -->/;
            const editor = this.openFiles[this.selectedEditor];
            const rebuiltHTML = [];
            let mergedHTML = [];
            let registerComponents = '';
            let splitContent;
            let originalHTML;

            // originalHTML exist only if the scene is reopened.
            // Check if the scene is initially created or reopened.
            if (editor.tab.originalHTML) {
                splitContent = editor.tab.originalHTML.split(coherentRegex);
            }

            if (componentsHtml !== '' && Object.keys(editor.components.components).length > 0) {
                registerComponents = editor.components.registerComponents();
                originalHTML = editor.tab.originalHTML;
                // there are no components on the scene, but there is code outside the coherent comments
            } else if (componentsHtml === '' && !isExternal && splitContent &&
                (splitContent[0] !== Enums.defaultHTML.top || splitContent[3] !== Enums.defaultHTML.bottom)) {

                // Remove component wrapper and register code if the scene has no components
                originalHTML = editor.tab.originalHTML
                    .replace(Enums.componentWrapper, '')
                    .replace(Enums.componentRegister, '');
            }

            if (originalHTML && originalHTML !== '') {
                mergedHTML = originalHTML.split(coherentRegex);
            } else {
                mergedHTML[0] = Enums.defaultHTML.top;
                mergedHTML[2] = Enums.defaultHTML.bottom;
            }

            rebuiltHTML.push(
                this.cleanCoherentTags(isExternal ? Enums.defaultHTML.top : mergedHTML[0]),
                coherentHTML,
                componentsHtml,
                registerComponents,
                this.cleanCoherentTags(isExternal ? Enums.defaultHTML.bottom : mergedHTML[2])
            );

            return rebuiltHTML.join('');
        }

        /**
         * Clears empty BoxShadow properties from the scene.
         * @function adjustSavedContent
         * @memberOf coui
         * @param {string} fileContents - string containing the JSON scene data
         * @param {object} styles
         * @return {string} tempObject - string containing the scene data excluding empty
         * boxShadow properties
         */
        adjustSavedContent(fileContents: string, styles: any): string {
            var tempSceneObject = JSON.parse(fileContents);
            var animationsHolder = tempSceneObject.animationClasses;
            var animationKeys = Object.keys(animationsHolder);
            for (var i = 0; i < animationKeys.length; i++) {
                var animationKey = animationKeys[i];

                if (animationsHolder[animationKey].animationsData['-webkit-filter']) {
                    this.prepareFilterAnimationToExport(animationsHolder, animationKey);
                }

                if (animationsHolder[animationKey].animationsData.transform) {
                    this.prepareTransformAnimationToExport(animationsHolder, animationKey);
                }
            }

            var clearWidgetJunk = function (widget) {
                // Adjust animation on scene //
                if (tempSceneObject.animations[widget.id]) {
                    var firstAnimationKey = tempSceneObject.animations[widget.id].className;
                    tempSceneObject.animations[widget.id][firstAnimationKey] =
                        tempSceneObject.animationClasses[firstAnimationKey];
                }

                // Clearing empty boxShadow
                var boxShadow = widget.boxShadow;
                if (boxShadow &&
                    boxShadow.blurRadius === '0px' &&
                    (boxShadow.color === 'rgb(0, 0, 0)' || boxShadow.color === '#000') &&
                    boxShadow.horizontalLength === '0px' &&
                    boxShadow.insetOutset === '' &&
                    boxShadow.spreadRadius === '0px' &&
                    boxShadow.verticalLength === '0px') {
                    delete widget.boxShadow;
                }
                // Clearing empty classNames
                if (widget.className === '') {
                    delete widget.className;
                }
                // Merging transform properties and creating a transformOrigin
                // widget.transform = helpers.mergeTransfromStyleProperties(widget.transform);
            };

            for (var h = 0; h < tempSceneObject.widgets.length; h++) {
                var parentWidget = tempSceneObject.widgets[h];
                if (styles.boxShadowAndClasses) {
                    clearWidgetJunk(parentWidget);

                    for (var m = 0; m < parentWidget.children.length; m++) {
                        var childWidget = parentWidget.children[m];
                        clearWidgetJunk(childWidget);
                    }
                }

                if (styles.textarea && tempSceneObject.widgets.type === 'textarea') {
                    delete tempSceneObject.widgets[i].styles.webkitUserSelect;
                }
            }

            return JSON.stringify(tempSceneObject);
        }

        prepareFilterAnimationToExport(animationsHolder, animationKey) {
            // Adjusting animationData for filters //
            var filters = animationsHolder[animationKey].animationsData['-webkit-filter'];

            animationsHolder[animationKey].animationsData['-webkit-filter'] = {};

            var firstKey = Object.keys(filters)[0];

            if (firstKey) {
                filters[firstKey].name = filters[firstKey].name.split('_');
                filters[firstKey].name[2] = 'combined';
                filters[firstKey].name = filters[firstKey].name.join('_');
                animationsHolder[animationKey].animationsData['-webkit-filter'].combined = filters[firstKey];

                // Adjusting keyframeValues for filters //
                var filterKeyframes = animationsHolder[animationKey].keyframes['-webkit-filter'];
                var filterKeyframesKeys = Object.keys(filterKeyframes).filter(function (element) {
                    return element;
                });

                var timeValues = {};

                for (let j = 0; j < filterKeyframesKeys.length; j++) {
                    var keyframeTimes = Object.keys(filterKeyframes[filterKeyframesKeys[j]]);
                    for (let k = 0; k < keyframeTimes.length; k++) {
                        if (timeValues[keyframeTimes[k]]) {
                            timeValues[keyframeTimes[k]].push(
                                filterKeyframes[filterKeyframesKeys[j]][keyframeTimes[k]]
                            );
                        } else {
                            timeValues[keyframeTimes[k]] =
                                [filterKeyframes[filterKeyframesKeys[j]][keyframeTimes[k]]];
                        }
                    }
                }

                var timeValuesTimes = Object.keys(timeValues);

                let filtersToExport = [];

                // Loop in time and build all necessary filters  keyframes
                // Get get correct value for each keyframe using the interpolator functionality
                for (let i = 0; i < timeValuesTimes.length; i++) {
                    filtersToExport
                        .push(this.openFiles[this.selectedEditor].runtimeEditor.Keyframes.TimelineScrub.performTimelineScrub(null, Number(timeValuesTimes[i]), animationKey, true, '-webkit-filter'));
                }

                // rebuild filters keyframe data
                for (let i = 0; i < filtersToExport.length; i++) {
                    timeValues[timeValuesTimes[i]] = [];
                    for (let h = 0; h < filtersToExport[i].length; h++) {
                        timeValues[timeValuesTimes[i]].push({
                            group: '-webkit-filter',
                            property: filtersToExport[i][h].key,
                            time: {
                                seconds: timeValuesTimes[i]
                            },
                            values: [filtersToExport[i][h].value]
                        });
                    }
                }

                var keyframeTemplate = $.extend(true, {}, timeValues[timeValuesTimes[0]][0]);

                keyframeTemplate.values[0] = '';
                keyframeTemplate.time.seconds = '';

                var newTransform;
                /* tslint:disable */
                newTransform = timeValuesTimes.reduce(function (o, v) {
                    o[v] = $.extend(true, {}, keyframeTemplate);
                    o[v].time.seconds = parseInt(v);
                    return o;
                }, {});
                /* tslint:enable */

                for (var n = 0; n < timeValuesTimes.length; n++) {
                    var value = timeValues[timeValuesTimes[n]].map(function (element) {
                        if (element.property === 'dropShadow') {
                            return element.values[0].trim();
                        } else {
                            return element.property + '(' + element.values[0].trim() + ')';
                        }
                    }).join(' ');

                    newTransform[timeValuesTimes[n]].property = '-webkit-filter';
                    newTransform[timeValuesTimes[n]].values[0] =
                        newTransform[timeValuesTimes[n]].values[0] + value;
                }
                animationsHolder[animationKey].keyframes['-webkit-filter'] = {};
                animationsHolder[animationKey].keyframes['-webkit-filter'].combined = newTransform;
            }
        }

        prepareTransformAnimationToExport(animationsHolder, animationKey) {
            // Adjusting animationData for transforms //
            var transforms = animationsHolder[animationKey].animationsData.transform;

            animationsHolder[animationKey].animationsData.transform = {};

            var transformProp = false;
            if (transforms['transform-origin']) {
                transformProp = true;
                animationsHolder[animationKey].animationsData.transform['transform-origin'] =
                    transforms['transform-origin'];
                delete transforms['transform-origin'];
            }

            var firstKey = Object.keys(transforms)[0];

            if (firstKey) {
                transforms[firstKey].name = transforms[firstKey].name.split('_');
                transforms[firstKey].name[2] = 'combined';
                transforms[firstKey].name = transforms[firstKey].name.join('_');
                animationsHolder[animationKey].animationsData.transform.combined = transforms[firstKey];

                // Adjusting keyframeValues for transforms //
                var transformKeyframes = animationsHolder[animationKey].keyframes.transform;
                var transformKeyframesKeys = Object.keys(transformKeyframes).filter(function (element) {
                    return element !== 'transform-origin';
                });
                var timeValues = {};

                for (let j = 0; j < transformKeyframesKeys.length; j++) {
                    var keyframeTimes = Object.keys(transformKeyframes[transformKeyframesKeys[j]]);
                    for (let k = 0; k < keyframeTimes.length; k++) {
                        if (timeValues[keyframeTimes[k]]) {
                            timeValues[keyframeTimes[k]].push(
                                transformKeyframes[transformKeyframesKeys[j]][keyframeTimes[k]]
                            );
                        } else {
                            timeValues[keyframeTimes[k]] =
                                [transformKeyframes[transformKeyframesKeys[j]][keyframeTimes[k]]];
                        }
                    }
                }

                var timeValuesTimes = Object.keys(timeValues);

                let transformsToExport = [];

                // Loop in time and build all necessary transform keyframes
                // Get get correct value for each keyframe using the interpolator functionality
                for (let i = 0; i < timeValuesTimes.length; i++) {
                    transformsToExport
                        .push(this.openFiles[this.selectedEditor].runtimeEditor.Keyframes.TimelineScrub.performTimelineScrub(null, Number(timeValuesTimes[i]), animationKey, true, 'transform'));
                }

                // rebuild transform keyframe data
                for (let i = 0; i < transformsToExport.length; i++) {
                    timeValues[timeValuesTimes[i]] = [];
                    for (let h = 0; h < transformsToExport[i].length; h++) {
                        timeValues[timeValuesTimes[i]].push({
                            group: 'transform',
                            property: transformsToExport[i][h].key,
                            time: {
                                seconds: timeValuesTimes[i]
                            },
                            values: [transformsToExport[i][h].value]
                        });
                    }
                }

                var keyframeTemplate = $.extend(true, {}, timeValues[timeValuesTimes[0]][0]);

                keyframeTemplate.values[0] = '';
                keyframeTemplate.time.seconds = '';

                var newTransform;
                /* tslint:disable */
                newTransform = timeValuesTimes.reduce(function (o, v) {
                    o[v] = $.extend(true, {}, keyframeTemplate);
                    o[v].time.seconds = parseInt(v);
                    return o;
                }, {});
                /* tslint:enable */

                timeValues = helpers.reorderTransform(timeValues);

                for (var n = 0; n < timeValuesTimes.length; n++) {
                    var value = timeValues[timeValuesTimes[n]].map(function (element) {
                        return element.property + '(' + element.values[0].trim() + ')';
                    }).join(' ');

                    if (this.preferences.couiEnvironment === editorSettings.environment.Hummingbird) {
                        value = helpers.rebuildTransformForHb(value);
                    }

                    newTransform[timeValuesTimes[n]].property = 'transform';
                    newTransform[timeValuesTimes[n]].values[0] =
                        newTransform[timeValuesTimes[n]].values[0] + value;
                }

                if (transformProp) {
                    transformProp = animationsHolder[animationKey].keyframes.transform['transform-origin'];
                    animationsHolder[animationKey].keyframes.transform = {};
                    animationsHolder[animationKey].keyframes.transform['transform-origin'] = transformProp;
                } else {
                    animationsHolder[animationKey].keyframes.transform = {};
                }

                animationsHolder[animationKey].keyframes.transform.combined = newTransform;
            }
        }

        getExtension(filename: string, editorId: string): string {
            const isComponent: boolean = filename.endsWith('.component');
            if (isComponent) {
                return Enums.extensions.component;
            }
            return this.openFiles[editorId].tab.fileExtension;
        }

        /**
         *
         * @param {string} filename
         * @param {object|string} fileContents
         * @param {bool} [newScene=undifined] - optional
         */
        async save(filename, fileContents, newScene?, componentLinkedEditor?) {
            let editorId = componentLinkedEditor || this.selectedEditor;
            let editor = this.openFiles[editorId];
            let extension = this.getExtension(filename, editorId);
            let path = editor.tab.filePath;
            let file = filename.replace(/.*[\\\/]/, '');
            let timeline = null;
            let currentPinPosition = null;

            if (editor.runtimeEditor) {
                timeline = editor.runtimeEditor.Timeline;
                currentPinPosition = timeline.pinCurrentSeconds;
                if (currentPinPosition !== 0) {
                    timeline.setPinheadPosition(null, 0);
                }

                if (!this.EXPORTING_WIDGET) {
                    fileContents = JSON.stringify(editor.runtimeEditor.scene);
                }
            }

            if (extension === Enums.extensions.html && editor.runtimeEditor) {
                fileContents = this.adjustSavedContent(fileContents, {
                    boxShadowAndClasses: true,
                    textarea: true
                });
            }

            if (extension === Enums.extensions.component) {
                let linkedEditor = editor.tab.tabWidgetState.instanceOf || editorId;
                this.openFiles[linkedEditor].components.save(file, fileContents);
                this.onSaveCompleted(file, file);
                this.tabEdited(true, linkedEditor);
            } else if (!this.EXPORTING_WIDGET) {
                if (editorId === undefined) {
                    return;
                }

                if (newScene) {
                    this.saveFile(path + file, fileContents, editorId);
                    this.setPinheadPosition(currentPinPosition);
                    return;
                }
                if (this.isJson(editor.file)) {
                    let currentEnvironmentProp = this.environmentProperties;
                    let componentsToExport = await editor.components.buildComponentExportHTML();
                    if (Object.keys(editor.components.components).length > 0) {
                        componentsToExport = currentEnvironmentProp.WRAPPER_COMPONENTS_MARK_START +
                            componentsToExport + currentEnvironmentProp.WRAPPER_COMPONENTS_MARK_END;
                    }
                    editor.runtimeEditor.runtimeLoad(fileContents, {
                        sceneSave: true
                    }).then((data) => {
                        let mergedHTML = this.rebuildUserHTML(data, componentsToExport);
                        let newData = this.cleanupHtmlExport(mergedHTML);
                        this.saveFile(path + file, newData, editorId);
                        this.setPinheadPosition(currentPinPosition);
                    }).catch((err) => {
                        console.log(err.message);
                    });
                } else {
                    this.saveFile(path + file, fileContents, editorId);
                    this.setPinheadPosition(currentPinPosition);
                }
            } else {
                editor.runtimeEditor.runtimeLoad(fileContents, {
                    widgetSave: true
                }).then((data) => {
                    let mergedHTML = this.rebuildUserHTML(data, '', this.EXPORTING_WIDGET);
                    let newData = this.cleanupHtmlExport(mergedHTML);
                    this.saveFile(path + file, newData, editorId);
                    this.setPinheadPosition(currentPinPosition);
                }).catch((err) => {
                    console.log(err.message);
                });
            }
        }

        public setPinheadPosition(position: number): void {
            let hasSelectedEditor = this.openFiles[this.selectedEditor];

            if (position !== null && hasSelectedEditor && hasSelectedEditor.runtimeEditor) {
                const runtimeEditor = this.openFiles[this.selectedEditor].runtimeEditor;
                const timeline = runtimeEditor.Timeline;
                timeline.setPinheadPosition(null, position);
            }
        }

        /**
         *
         * @param editor
         * @param sceneBackgroundColor
         */
        previewStop(runtimeEditor, sceneBackgroundColorValue?: string) {
            let $scene = $('#scene');
            clearTimeout(runtimeEditor.playAnimation);
            runtimeEditor.iframe.remove();
            runtimeEditor.iframe = undefined;
            if (sceneBackgroundColorValue) {
                $scene[0].style.backgroundColor = sceneBackgroundColorValue;
            }

            $scene.append(this.detachedSceneHTML);
            helpers.rekickDomWebkitMasks();
            this.detachedSceneHTML = null;
        }

        spriteChangeState(element, start) {
            var stateOne, stateTwo;

            if (start) {
                stateOne = 'play';
                stateTwo = 'stop';
            } else {
                stateOne = 'stop';
                stateTwo = 'play';
            }

            element.addClass('btn-' + stateOne);
            element.removeClass('btn-' + stateTwo);
        }

        toggleVideoPlayback(videos, play) {
            var videoElementsLength = videos.length;
            for (var i = 0; i < videoElementsLength; i++) {
                if (play) {
                    videos[i].play();
                } else {
                    videos[i].pause();
                }
            }
        }

        getMaxVideoLength(videos) {
            var longestVideoLength = 0;
            var videoElementsLength = videos.length;
            for (var i = 0; i < videoElementsLength; i++) {
                if (videos[i].duration * 1000 > longestVideoLength) {
                    longestVideoLength = videos[i].duration * 1000;
                }
            }
            // in miliseconds
            return longestVideoLength;
        }

        preview(filename, fileContents, type) {

            var _this = this;
            var editorId = this.selectedEditor;

            if (editorId === undefined) {
                console.error('File id for ' + filename + ' cannot be found!');
                return;
            }

            var scenePlaySprite = $('#preview-scene-button > .fa');
            var animationPlaySprite = $('#preview-animation-button > .fa');

            var runtimeEditor = this.openFiles[editorId].runtimeEditor;


            if (!runtimeEditor.iframe) {
                this.setPinheadPosition(0);
            }

            fileContents = this.adjustSavedContent(JSON.stringify(runtimeEditor.scene), {
                boxShadowAndClasses: true,
                textarea: true
            });

            var newSceneObj = JSON.parse(fileContents);
            var $iframeScripts = newSceneObj.scripts;
            newSceneObj.scripts = [];
            let $scene = $('#scene');
            let sceneColorValue: string = runtimeEditor.scene.style.backgroundColor;

            if (!runtimeEditor.iframe) {
                runtimeEditor.runtimeLoad(JSON.stringify(newSceneObj))
                    .then(function (data) {

                        // ----------------------------------------- //
                        // DECOMMENT ON VIDEO PLAYBACK REINTEGRATION //
                        // ----------------------------------------- //

                        // var maxVideoLength = _this.getMaxVideoLength($('#scene video'));
                        // totalTime = totalTime > maxVideoLength ? totalTime : maxVideoLength;

                        // ----------------------------------------- //

                        var animations = runtimeEditor.scene.animations;

                        var animationTimes = helpers.getObjects(animations, 'seconds').map(function (element) {
                            return parseFloat(element);
                        });
                        var totalTime = Math.max.apply(Math, animationTimes);

                        if (totalTime > 0) {
                            var width = runtimeEditor.scene.sceneSize.width;
                            var height = runtimeEditor.scene.sceneSize.height;

                            var scriptsRegex = /((<script[^<]([\s\S]*?)[^>]*<\/script>)[\s\S]*?)/g;

                            var newData;
                            var uiRegex;
                            if (_this.globalEditorInfo.backend === Enums.Backends.Debug ||
                                _this.globalEditorInfo.backend === Enums.Backends.Website) {
                                uiRegex = /&quot;/gi;
                                var newURLStart = 'url(' + window.location.origin + '/';
                                const linkTagRegex = /(<link\s+(?:[^>]*?\s+)?href=")([^"]*)"/g;
                                newData = data.replace(uiRegex, '');

                                newData = newData.replace(/url\(/gm, newURLStart)
                                // include uiresources to all external css paths in link tags
                                    .replace(linkTagRegex, '$1' + window.location.origin + '/uiresources/$2"');
                            } else {
                                uiRegex = /coui:\/\/uiresources\/editor\//gi;
                                newData = data.replace(uiRegex, '');
                            }

                            var m = scriptsRegex.exec(newData);
                            while (m) {
                                $iframeScripts.push(m[0]);
                                m = scriptsRegex.exec(newData);
                            }

                            newData = newData.replace(scriptsRegex, '');

                            var $iframe = $('<iframe id="preview" ' +
                                'sandbox="allow-same-origin allow-scripts allow-forms" width="' +
                                width + '" height="' + height + '"></iframe>');

                            runtimeEditor.clearSelectedElements();
                            let $style = $('#scene style').detach();
                            _this.detachedSceneHTML = $('#scene').html();
                            $scene[0].style.backgroundColor = null;
                            $('#scene *').detach();
                            $style.prependTo($scene);
                            $scene.append($iframe);

                            _this.spriteChangeState(animationPlaySprite, false);
                            _this.spriteChangeState(scenePlaySprite, false);

                            $iframe.ready(function () {
                                var $iframeScope: any = document.getElementById('preview').contentWindow;

                                var $iframeContents = $iframe.contents();

                                $iframeContents.find('html')
                                    .css('overflow', 'hidden').append(newData);

                                var $iframeVideos = $iframeContents.find('video');

                                runtimeEditor.evalScripts($iframeScope, $iframeScripts);

                                runtimeEditor.displayVideos($iframeVideos);
                                runtimeEditor.iframe = $iframe;
                            });

                            // ----------------------------------------- //
                            // DECOMMENT ON VIDEO PLAYBACK REINTEGRATION //
                            // ----------------------------------------- //
                            // var maxVideoLength = _this.getMaxVideoLength($('#scene video'));
                            // totalTime = totalTime > maxVideoLength ? totalTime : maxVideoLength;

                            // ----------------------------------------- //
                            runtimeEditor.Timeline.animatePinhead(totalTime, type);
                            // stop after preview
                            if (type === Enums.animationPreviewType.preview) {
                                runtimeEditor.playAnimation = setTimeout(function () {
                                    _this.previewStop(runtimeEditor, sceneColorValue);

                                    _this.spriteChangeState(animationPlaySprite, true);
                                    _this.spriteChangeState(scenePlaySprite, true);

                                    runtimeEditor.highlightSelectedEl(null, runtimeEditor.currentElementsSelection);
                                }, totalTime);
                            }
                        }
                    });
            } else {

                this.spriteChangeState(animationPlaySprite, true);
                this.spriteChangeState(scenePlaySprite, true);

                _this.previewStop(runtimeEditor, sceneColorValue);

                runtimeEditor.highlightSelectedEl(null, runtimeEditor.currentElementsSelection);
            }
        }

        saveFile(filename, fileContents, editorId, extension?) {
            if (this.EXPORTING_WIDGET) {
                const file = helpers.getFileAndPath(filename).filename;

                this.onExportWidget(file, fileContents)
                    .then(() => this.openFiles[this.selectedEditor].runtimeEditor._initAssetsKendoToolbar());
            } else {
                if (this.openFiles[editorId].tab.pendingSave) {
                    return;
                }

                this.openFiles[editorId].tab.pendingSave = true;

                if (this.autoReload === true) {
                    this._reload(this.autoReloadCallback);
                }

                if (this.onsave && extension !== Enums.extensions.component) {
                    this.onsave(filename, fileContents);
                }
            }
        }

        onSaveCompleted(oldFilename, newFilename) {
            let id = this.getIdForFile(oldFilename);
            if (id === undefined) {
                return;
            }
            let editorId = 'editor' + id;

            this.openFiles[editorId].tab.pendingSave = false;
            this.tabEdited(false, editorId);

            if (oldFilename !== newFilename) {
                this.renameTab(oldFilename, newFilename);
            }
            if (this.openFiles[editorId].tab.state === TabStates.pendingClose) {
                this.openFiles[editorId].tab.state = TabStates.safelyClosable;
                let tabId = 'tab' + id;
                w2ui.tabs.animateClose(tabId);
            }
        }

        _reload(filename) {
            if (this.onreload) {
                this.onreload(filename);
            }
        }

        isItPublishPage(htmlContent: string): boolean {
            const doesCommentMarkExist = htmlContent.indexOf(this.environmentProperties.ORIGINAL_SOURCE_SCENE_PATH) > -1
                && htmlContent.indexOf(this.environmentProperties.ORIGINAL_SOURCE_SCENE_PATH_END) > -1;
            return doesCommentMarkExist;
        }

        getOriginSceneUrl(htmlContent: string): any {
            const regexPublishPage = /<!-- Original scene path -->([\s\S]*)<!-- Original scene path end -->/;
            if (regexPublishPage.exec(htmlContent)) {
                return regexPublishPage.exec(htmlContent)[1].replace(/<!--([\s\S]*?)-->/ig, '$1').trim();
            } else {
                console.error('there is no comment marks');
            }
        }


        getFileEditorProps(regExp, content: string) {
            let fileEditorPropVal = regExp.exec(content);
            let fileEditorProp = '';

            if (fileEditorPropVal) {
                fileEditorProp = fileEditorPropVal[0].match(/"(.*)"/)[1];
            }

            return fileEditorProp;
        }

        changeEngineEnv(environment) {
            this.preferences.couiEnvironment = environment;
            this.savePreferences();

            this._isExitingEditor = true;

            engine.trigger('AboutToClose');
        }

        savePreferences() {
            engine.call('prefs.set', 'preferences', this.preferences);
            engine.call('prefs.save');
        }

        showEnvironmentWarning(fileSetting: string, currentSetting: string) {
            vex.dialog.confirm({
                message: 'You are running the Coherent Editor in a ' + currentSetting +
                ' environment, but the file that you are trying to open has been created in '
                + fileSetting + ' environment. Would you like to start the editor in '
                + fileSetting + ' environment? ',
                contentClassName: 'modal-about',
                callback: (value) => {
                    delete this.openFiles[this.selectedEditor];

                    if (!value) {
                        return;
                    }

                    this.changeEngineEnv(fileSetting);
                }
            });
        }

        compareSettings(settingName: string, content: string) {
            let regExps = {
                'EDITOR_VERSION': Enums.EDITOR_VERSION,
                'couiEnvironment': Enums.couiEnvironment
            };

            let fileSetting = this.getFileEditorProps(regExps[settingName], content);

            if (fileSetting && fileSetting !== this.preferences[settingName]) {
                this.showEnvironmentWarning(fileSetting, this.preferences[settingName]);
            }
        }

        openFile(content, fileName) {
            const regexp: any = /\/*([^/]*\.([^.]*))$/;

            const splitFileName: string[] = regexp.exec(fileName);
            let fileExtension: string = splitFileName[splitFileName.length - 1];
            let path = fileName.replace(/[^\/\\]*$/, '');
            const rootFolder = helpers.rootFolder(path);
            const originalFullPath = path;

            // properly handle widget image sources by removing the folder from the path.
            // the "widgets" folder should only have the html files, with no assets in it.
            if (rootFolder === 'widgets') {
                path = path.split(/[\/\\]/).slice(1).join('/');
            }

            switch (fileExtension) {
                case 'js':
                    fileExtension = Enums.extensions.js;
                    break;
                case 'css':
                    fileExtension = Enums.extensions.css;
                    break;
                case 'html':
                    fileExtension = Enums.extensions.html;
                    break;
                case 'component':
                    fileExtension = Enums.extensions.component;
                    break;
            }

            const newFileName: string = fileName.replace(/^.*[\\\/]/, '');
            // replace relative file paths with full paths from uiresources
            const convertedContent = helpers.openPathHandler(content, path);

            if (fileExtension === Enums.extensions.html || fileExtension === Enums.extensions.component) {
                if (this.usesSceneEditor(content) || fileExtension === Enums.extensions.component) {

                    const sceneEnv = this.getFileEditorProps(Enums.couiEnvironment, content) || null;

                    this._addRuntimeEditor(convertedContent, newFileName, fileExtension, path);
                    this.compareSettings('couiEnvironment', content);

                    this.$wrapperCodeEditor.hide();

                    // Check for scene environment differences. If found, prevent any loading.
                    // Ignore components and new scenes.
                    if (sceneEnv && sceneEnv !== this.preferences.couiEnvironment && fileExtension !== Enums.extensions.component) {
                        document.body.dispatchEvent(new Event('editorLoaded'));
                        return;
                    }
                } else {
                    this._addFileContent(content, newFileName, fileExtension);
                }
            } else {
                this._addFileContent(content, newFileName, fileExtension);
            }

            document.body.dispatchEvent(new CustomEvent('coui.tab.change'));
            this.selectedEditor = 'editor' + this.fileId;
            this.openFiles[this.selectedEditor].tab.originalFullPath = originalFullPath;

            this._addTab(newFileName, path);
            this.fileId++;

            if (this.PENDING_WIDGET_LOAD) {
                this.openFiles[this.selectedEditor].tab.tabWidgetState.editWidget = true;
                this.PENDING_WIDGET_LOAD = false;
            }
        }

        /**
         * force close tab without save
         * @param editorId {string}
         */
        forceClose(editorId) {
            this.tabEdited(false, editorId);
            let componentTabId = editorId.replace('editor', 'tab');
            w2ui.tabs.animateClose(componentTabId);
        }

        closeTabsInOrder(eventId?: string) {
            let editorId = this.selectedEditor;

            if (eventId) {
                editorId = eventId.replace('tab', 'editor');
            }

            this._isClosingInOrder = true;

            if (this.__mainSceneIdToBeClosed !== null) {
                editorId = this.__mainSceneIdToBeClosed;
            } else {
                // first component tab
                this.__mainSceneIdToBeClosed = editorId;
                let componentTabs = this.openFiles[editorId].components.state.opened[0];
                let $editor = $('#' + editorId);
                let componentTabId = componentTabs.replace('editor', 'tab');
                this.focusFileOnClose($editor, componentTabId);
                w2ui.tabs.animateClose(componentTabId);
                return;
            }

            let tabs = this.openFiles[editorId].components.state.opened;

            if (tabs.length === 0) {
                // last closing tab - the scene
                this._isClosingInOrder = false;
                this.__mainSceneIdToBeClosed = null;
                let sceneTabId = editorId.replace('editor', 'tab');
                let filename = this.openFiles[editorId].tab.filename;
                let content = this.openFiles[editorId].file;
                window.requestAnimationFrame(() => {
                    w2ui.tabs.animateClose(sceneTabId);
                });
                return;
            }

            // Request closing the first tab.
            // When that's done, _isClosingAllTabs will cause the next one to close as well
            var nextTab = w2ui.tabs.tabs[this._getActiveTabIndex()];
            w2ui.tabs.animateClose(nextTab.id);
        }

        closeAllTabs() {
            if (w2ui.tabs.tabs.length === 0) {
                this.oncloseEditor();
                return;
            }
            this._isClosingAllTabs = true;
            // Request closing the first tab.
            // When that's done, _isClosingAllTabs will cause the next one to close as well
            var nextTab = w2ui.tabs.tabs[this._getActiveTabIndex()];
            w2ui.tabs.animateClose(nextTab.id);
        }

        focusFile(filename) {
            // Much magic, but this simulates clicks
            w2ui.tabs.click('tab' + this.getIdForFile(filename));
        }

        focusCUIFile(filename) {
            var id = filename.replace('editor', '');
            w2ui.tabs.click('tab' + id);
        }

        private getSceneFileAndPath(sceneId: string) {
            let path: string = this.openFiles[sceneId].tab.originalFullPath;
            const filename: string = this.openFiles[sceneId].tab.filename;

            return {
                path,
                filename
            };
        }

        /**
         * Check if the scene is already opened.
         * Used in engine.on('LoadFile').
         * @param fullPath
         * @returns {boolean}
         */
        sceneExist(fullPath: string): boolean {
            for (let sceneId in this.openFiles) {
                const path = this.openFiles[sceneId].tab.originalFullPath;
                const filename = this.openFiles[sceneId].tab.filename;

                const forwardSlashPath = path ? path.replace(/\\/g, '/') : '';

                if (filename.endsWith('.component')) {
                    let openedComponents = this.openFiles[this.selectedEditor].components.state.opened;
                    for (let i = 0; i < openedComponents.length; i++) {
                        if (this.openFiles[openedComponents[i]].tab.filename === fullPath) {
                            return true;
                        }
                    }
                } else if (forwardSlashPath + filename === fullPath) {
                    return true;
                }
            }

            return false;
        }

        getIdForFile(sceneName: string) {
            var _this = this;
            for (var editorId in _this.openFiles) {
                const path = this.getSceneFileAndPath(editorId).path.replace(/\\/g, '/');
                const filename = this.getSceneFileAndPath(editorId).filename.replace(/\\/g, '/');
                const scene = sceneName.replace(/\\/g, '/');

                // if the tab is old widget compare only by name
                if ((path === 'widgets/' && filename === scene) || (path + filename === scene)) {
                    /*jslint bitwise: true */
                    return ~~editorId.replace('editor', '');
                    /*jslint bitwise: false */
                }
            }
        }

        renameTab(oldFilename, newFilename) {
            var id = this.getIdForFile(oldFilename);
            if (id === undefined) {
                return;
            }
            var editorId = 'editor' + id;
            this.openFiles[editorId].tab.filename = newFilename;
            var runtimeEditor = this.openFiles[editorId].runtimeEditor;
            if (runtimeEditor) {
                runtimeEditor.setOpenedFile(newFilename);
            }
            var tabs = w2ui.tabs.tabs;
            for (var i = 0; i < tabs.length; i++) {
                if (tabs[i]['data-id'] === id) {
                    tabs[i].caption = tabs[i].text = newFilename;
                    w2ui.tabs.refresh('tab' + id);
                    break;
                }
            }
        }

        _createTabData(filename, fileExtension) {
            return {
                'filePath': '',
                'filename': filename,
                'fileExtension': fileExtension,
                'state': TabStates.open,
                'pendingSave': false, // true if there's a pending request to save this file
                'tabWidgetState': {
                    'importedWidget': false,
                    'editWidget': false,
                    'instanceOf': null,
                    'createNewWidget': false
                },
                'scrollIndex': {
                    'rightPanel': 0,
                    'assetLibrary': 0
                },
                'assetsLibraryStates': {
                    'image': {
                        expanded: true
                    },
                    'video': {
                        expanded: true
                    },
                    'widget': {
                        expanded: true
                    },
                    'script': {
                        expanded: true
                    },
                    'style': {
                        expanded: true
                    },
                    'font': {
                        expanded: true
                    }
                },
                'snapOn': false
            };
        }


        /**
         * Initialize the ACE text editor.
         * @param content
         * @param filename
         * @param fileExtension
         * @private
         */
        _addFileContent(content, filename, fileExtension) {
            var _this = this;
            $('.editor').hide();

            if (typeof content !== 'string') {
                content = content.toString();
            }

            var fileid = 'editor' + this.fileId;
            var editorEl: any = document.createElement('pre');
            editorEl.id = fileid;
            editorEl.className = 'editor';
            editorEl.dataset.id = this.fileId;

            this.$wrapperFilesEl.append(editorEl);
            this.openFiles[fileid] = {};
            this.openFiles[fileid].file = ace.edit(editorEl);
            this.openFiles[fileid].file.setTheme('ace/theme/twilight');
            this.openFiles[fileid].file.getSession().setMode('ace/mode/' + fileExtension);
            this.openFiles[fileid].file.insert(content);

            var aceSession = this.openFiles[fileid].file.getSession();
            aceSession.getUndoManager().reset();
            aceSession.on('change', function () {
                _this.tabEdited(true);
            });

            // The file property is either a string or an ace editor instance
            // so overwrite valueOf to always return the file contents
            this.openFiles[fileid].file.valueOf = function () {
                let fileContents: any = this;
                return fileContents.getValue();
            }.bind(this.openFiles[fileid].file);

            this.openFiles[fileid].tab = this._createTabData(filename, fileExtension);
            editorEl.style.display = 'block';
        }

        _addRuntimeEditor(content, filename, fileExtension, path) {
            let contentObj = this.isJson(content) ? JSON.parse(content) : {};
            var fileid = 'editor' + this.fileId;

            this.openFiles[fileid] = {};
            this.openFiles[fileid].createdNow = contentObj.createdNow || false;
            this.openFiles[fileid].file = content;
            this.openFiles[fileid].tab = this._createTabData(filename, fileExtension);
            this.openFiles[fileid].tab.filePath = path;
            this.openFiles[fileid].pendingRebuild = false;
            this.openFiles[fileid].widgetTypesCounter = {};
            this.openFiles[fileid].widgetClassCounter = {};
            this.openFiles[fileid].components = new Components();
            this.selectedEditor = fileid;
            if (!this.isJson(content)) {
                this.openFiles[fileid].components.load(content);
            }
        }

        _addTab(fileName, path?) {
            w2ui.tabs.add({
                id: 'tab' + this.fileId,
                'data-id': this.fileId,
                'path': path || '',
                caption: fileName,
                closable: true
            });

            w2ui.tabs.click('tab' + this.fileId);

            this._initTabsTooltip(this.fileId, fileName);
        }

        _initTabsTooltip(tabId, filename) {
            let isComponent = filename.endsWith('.component');
            let _this = this;
            if (!isComponent) {
                return;
            }

            if (this.openFiles['editor' + tabId].tab.tabWidgetState.instanceOf !== null) {
                _setTooltipHover();
            } else {
                $('body').off('__componentInstanceSet');
                $('body').on('__componentInstanceSet', () => {
                    _setTooltipHover();
                });
            }

            function _setTooltipHover() {
                let linkedEditor = _this.openFiles['editor' + tabId].tab.tabWidgetState.instanceOf;
                let scenePath = _this.openFiles[linkedEditor].tab.filePath || '';
                let sceneName = _this.openFiles[linkedEditor].tab.filename;

                $('#tabs_tabs_tab_tab' + tabId).append('<span class="tab-tooltip">belongs&nbsp;to:&nbsp;' +
                    scenePath +
                    sceneName +
                    '</span>');
                $('#tabs_tabs_tab_tab' + tabId).hover(function () {
                    $(this).find('.tab-tooltip').show();
                }, function () {
                    $(this).find('.tab-tooltip').hide();
                });
            }
        }

        _getCurrentFileId() {
            // This is kind of unstable code but gets the job done reaaaally quickly
            return document.querySelector('.editor[style*="display: block"]').id;
        }

        /**
         * On editor initialization, creates the top edit menu elements
         * @private
         */
        _displayEditMenu() {
            System.import('lib/hbs/editing_menu.hbs!text').then(function (data) {
                $('#edit-menu').append(data);
            });
        }

        _displayMenu() {
            var _this = this;

            $('.btn-new-file').off('click');
            $('.btn-new-file').on('click', function () {
                $(window).trigger('createNewScene');
            });

            $('.btn-open-file').off('click');
            $('.btn-open-file').on('click', function () {
                _this.onopen();
            });

            $('.btn-publish-file').off('click');
            $('.btn-publish-file').on('click', function () {
                var currentTab = _this.openFiles[_this.selectedEditor];
                if (currentTab) {
                    currentTab.runtimeEditor.publishScene();
                }
            });

            $('.btn-save-file').off('click');
            $('.btn-save-file').on('click', function () {
                var currentTab = _this.openFiles[_this.selectedEditor];
                if (currentTab) {
                    var filename = currentTab.tab.filename;
                    var content = currentTab.file.valueOf();

                    _this.save(filename, content);
                }
            });

            $('.btn-quit-file').off('click');
            $('.btn-quit-file').on('click', function () {
                engine.trigger('AboutToClose');
            });

            $('.btn-pref-file').off('click');
            $('.btn-pref-file').on('click', function () {
                _this.adjustPreferences();
            });

            $('.btn-documentation').on('click', function (event) {
                event.preventDefault();
                _this.onLaunchUrl(Enums.Links.documentation);
            });
            $('.btn-tutorials').on('click', function (event) {
                event.preventDefault();
                _this.onLaunchUrl(Enums.Links.tutorials);
            });
            $('.btn-community-forums').on('click', function (event) {
                event.preventDefault();
                _this.onLaunchUrl(Enums.Links.communityForum);
            });
            $('.btn-editor-roadmap').on('click', function (event) {
                event.preventDefault();
                _this.onLaunchUrl(Enums.Links.roadmap);
            });
            $('.bnt-shortcut').on('click', _this.onshortcut);
            $('.btn-about').on('click', _this.onabout.bind(_this));
        }

        onshortcut() {
            System.import('lib/hbs/shortcuts.hbs!text').then(function (content) {
                vex.dialog.open({
                    contentClassName: 'modal-shortcuts',
                    message: 'Keyboard Shortcuts',
                    buttons: [
                        $.extend({}, vex.dialog.buttons.NO, {
                            text: 'Close'
                        })
                    ],
                    afterOpen: function ($vexContent) {
                        $vexContent.append(content);
                    }
                });
            });
        }

        onabout() {
            let _this = this;
            vex.dialog.open({
                contentClassName: 'modal-about version',
                message: 'About',
                buttons: [
                    $.extend({}, vex.dialog.buttons.NO, {
                        text: 'Ok'
                    })
                ],
                afterOpen: ($vexContent) => {
                    var version = _this.EDITOR_VERSION.join('.');
                    $vexContent.append('Coherent Editor version: ' + version);
                }
            });
        }

        onundo() {
            var editor = this.openFiles[this.selectedEditor];
            if (editor) {
                if (editor.runtimeEditor) {
                    editor.runtimeEditor.undoRedoScene('undo');
                } else {
                    editor.file.undo();
                }
            }
        }

        onredo() {
            var editor = this.openFiles[this.selectedEditor];
            if (editor) {
                if (editor.runtimeEditor) {
                    editor.runtimeEditor.undoRedoScene('redo');
                } else {
                    editor.file.redo();
                }
            }
        }

        delete() {
            var editor = this.openFiles[this.selectedEditor];
            if (editor) {
                var runtimeEditor = editor.runtimeEditor;
                runtimeEditor.removeMultipleWidgets();
            }
        }

        _displayToolbar() {
            var _this = this;

            $('#toolbar-code-editor').w2toolbar({
                name: 'toolbar',
                items: [{
                    type: 'button',
                    id: 'save-file',
                    caption: 'Save',
                    icon: 'fa fa-save'
                }, {
                    type: 'break',
                    id: 'break1'
                }, {
                    type: 'button',
                    id: 'undo',
                    caption: 'Undo',
                    icon: 'fa fa-undo'
                }],
                onClick: function (event) {
                    var id;

                    switch (event.target) {
                        case 'open-file':
                            var cont = 'Pehso';
                            var name = 'coherent_view_1.css';
                            _this.openFile(cont, name);
                            break;
                        case 'save-file':
                            id = _this._getCurrentFileId();
                            _this.save(_this.openFiles[id].tab.filename, _this.openFiles[id].file.valueOf());
                            break;
                        case 'reload-file':
                            _this._reload(_this.openFiles[id].tab.filename);
                            break;
                        case 'auto-reload':
                            if (event.item.checked !== true) {
                                _this.autoReload = true;
                            } else {
                                _this.autoReload = false;
                            }
                            break;
                        case 'undo':
                            id = _this._getCurrentFileId();
                            _this.openFiles[id].file.undo();
                            break;
                    }
                }
            });
        }

        kendoDestroyEvents() {
            var $horizontalTimelineSplitter = $('#horizontal-timeline-splitter');
            var $horizontalInner = $('#horizontal-inner');
            var $vertical = $('#vertical');
            var $horizontal = $('#horizontal');

            // Destroy order is important!
            var $animationToolbar = $('#animation-toolbar');

            if ($animationToolbar.length > 0 && $animationToolbar.data('kendoToolBar')) {
                $animationToolbar.data('kendoToolBar').destroy();
            }

            // kendoSplitter
            // Destroy order is important!
            if ($horizontalTimelineSplitter.length > 0 && $horizontalTimelineSplitter.data('kendoSplitter')) {
                $horizontalTimelineSplitter.data('kendoSplitter').destroy();
            }

            if ($horizontalInner.length > 0 && $horizontalInner.data('kendoSplitter')) {
                $horizontalInner.data('kendoSplitter').destroy();
            }

            if ($vertical.length > 0 && $vertical.data('kendoSplitter')) {
                $vertical.data('kendoSplitter').destroy();
            }

            if ($horizontal.length > 0 && $horizontal.data('kendoSplitter')) {
                $horizontal.data('kendoSplitter').destroy();
            }

            // Toolbars
            // TODO this lines crashing the editor in GT!
            // if ($('#toolbar').length > 0) {
            //     $('#toolbar').data('kendoToolBar').destroy();
            // }

            var $borderColorPicker = $('#borderColor-picker');
            var $backgroundPicker = $('#background-picker');
            var $boxShadowPicker = $('#boxShadow-picker');
            var $colorPicker = $('#color-picker');

            // Color pickers
            if ($borderColorPicker.length > 0) {
                $borderColorPicker.remove();
            }

            if ($backgroundPicker.length > 0) {
                $backgroundPicker.remove();
            }

            if ($boxShadowPicker.length > 0) {
                $boxShadowPicker.remove();
            }

            if ($colorPicker.length > 0) {
                $colorPicker.remove();
            }

            var $selectDropdown = $('#left-pane select');

            // Dropdown lists
            if ($selectDropdown.length > 0) {
                $selectDropdown.remove();
            }

            var $tabs = $('#create-element-tabs');

            // KendoTabStrip
            if ($tabs.length > 0) {
                $tabs.remove();
            }

            var $dragPoints = $('.dragging-point');

            // Kendo draggable
            if ($dragPoints.length > 0) {
                $dragPoints.removeData('kendoDraggable');
                $dragPoints.removeData('role');
                $dragPoints.unbind('mousedown');
                $dragPoints.unbind('selectstart');
                $dragPoints.remove();
            }
        }

        niceScrollDestroyEvents() {
            if ($('.info-widgets').length > 0) {
                $('.info-widgets').remove();
            }
        }

        initRuntimeEditorHtml(editorId) {
            var _this = this;

            this.kendoDestroyEvents();
            this.niceScrollDestroyEvents();

            return Promise.all([
                System.import('lib/hbs/runtime_editor_wrapper.hbs!text'),
                System.import('lib/animations/hbs/timeline.hbs!text')
            ]).then(function (modules) {
                var htmlRuntimeEditor = modules[0];
                var htmlTimeline = modules[1];

                var templateRuntimeEditor = _this.Handlebars.compile(htmlRuntimeEditor);
                var templateTimeline = _this.Handlebars.compile(htmlTimeline);
                _this.$wrapperRuntimeEditor.html(templateRuntimeEditor);
                $('.animations-panel').html(templateTimeline);
                _this.$wrapperRuntimeEditor.find('.runtime-editor').attr('id', editorId);

                $('#horizontal-timeline-splitter').kendoSplitter({
                    panes: [{
                        collapsible: false,
                        min: '380px',
                        max: '500px',
                        size: '400px'
                    }, {
                        collapsible: false
                    }],
                    resize: function () {
                        var $element = $(this.element[0]);

                        var height = $('#middle-pane').height();
                        $element.height(height);
                        $element.find('.k-splitbar').height(height);
                        $element.find('.info-section').height(height);

                        $('#middle-pane').hide().show(0);
                        _this.resizeTimelineVertical();
                    }
                });
            });
        }

        createRuntimeEditor(content, animationObj, tabId) {
            var _this = this;
            var runtimeEditor;

            System.config({
                paths: {
                    when: 'vendor/when/when.js'
                }
            });
            System.import('./lib/runtime_editor')
                .then(function (RuntimeEditor) {
                    var data = false;
                    runtimeEditor = new RuntimeEditor.default();

                    var currentEditor = _this.openFiles[tabId];

                    currentEditor.runtimeEditor = runtimeEditor;

                    var editorUndoRedo = JSON.parse(localStorage.getItem(tabId));

                    if (editorUndoRedo !== null) {
                        currentEditor.undo = editorUndoRedo[tabId].undo;
                        currentEditor.redo = editorUndoRedo[tabId].redo;
                    } else {
                        currentEditor.undo = [];
                        currentEditor.redo = [];
                    }

                    if (_this.globalEditorInfo.backend !== Enums.Backends.Debug ||
                        _this.globalEditorInfo.backend !== Enums.Backends.Website) {
                        runtimeEditor.init(content, animationObj, tabId);
                        runtimeEditor.setOpenedFile(currentEditor.tab.filename);
                    } else {
                        runtimeEditor.init(content, animationObj, tabId);
                    }
                    // order of importance
                    _this.initScenePanZoom(runtimeEditor);
                    _this.initKendoSplitter();
                    _this.initTimelineScroll();
                    _this.initKendoToolbar(runtimeEditor);
                    _this.initRightPanelItem(runtimeEditor);
                    _this.initEditingEventHandlers(runtimeEditor);
                    _this.initSceneEventHandlers(runtimeEditor);
                    _this.widgetEditingHandlers();
                    keyHandlers.attachkeyHandlersRuntimeEditor(runtimeEditor);

                    // HACK!!!
                    // Fix animation panel layer rendering problem
                    $('#horizontal-timeline-splitter').css('transform', 'rotate(360deg)');

                    // Inform the the editor is ready for use
                    runtimeEditor._sceneActionState.initialLoad = false;
                    runtimeEditor.exportScene();

                    _this.assets.widget = [];
                    _this.listDirectory('widgets', '(.html)', false).then(function (data) {
                        _this.updateSceneAssets(data);
                        runtimeEditor._initAssetsKendoToolbar();
                    });

                    var path = _this.openFiles[_this.selectedEditor].tab.filePath;
                    engine.call('SetCurrentSceneURL', path);

                    _this.classNamesCount +=
                        helpers.numKeys(_this.openFiles[_this.selectedEditor].runtimeEditor.scene.animationClasses);

                    if (_this.missingWidgets.length > 0 && !_this.PENDING_WIDGET_EXPORT) {
                        let message = 'Could not load: <br />';
                        _this.missingWidgets.map((link) => {
                            message += `${link} <br />`;
                        });

                        $('#coui-editor').trigger('vexFlashMessage', [message]);
                        _this.missingWidgets = [];
                        _this.PENDING_WIDGET_EXPORT = false;
                    }
                    window.requestAnimationFrame(function () {
                        document.body.dispatchEvent(new Event('editorLoaded'));
                    });
                });
        }

        /**
         * Vex flash messages
         */
        initVexFlashMessages() {
            $('#coui-editor').on('vexFlashMessage', function (e, customMessages, autoClose = false) {
                let message = customMessages || Enums.Messages.duplicateAnimationsTabToTab;

                var $vex = vex.dialog.open({
                    closeOnOverlayClick: true,
                    contentClassName: 'modal-about',
                    message: message,
                    unsafeContent: 'html-escape',
                    buttons: [
                        $.extend({}, vex.dialog.buttons.NO, {
                            text: 'Ok',
                            click: function () {
                                $vex.data().vex.value = false;
                                vex.close($vex.data().vex.id);
                            }
                        })
                    ],
                    afterOpen: function () {
                        if (autoClose) {
                            setTimeout(function () {
                                if ($vex.data().vex) {
                                    vex.close($vex.data().vex.id);
                                }
                            }, 3000);
                        }
                    }
                });
            });
        }

        importHtmlTemplate(template) {
            var cloneTemplate;

            if (template.content === undefined &&
                this.globalEditorInfo.backend === Enums.Backends.Standalone ||
                this.globalEditorInfo.backend === Enums.Backends.Unreal) {
                cloneTemplate = template.cloneNode(true);
            } else {
                cloneTemplate = document.importNode(template.content, true);
            }

            return cloneTemplate.firstElementChild;
        }

        /**
         * @function
         * Check for name duplication in components
         * @memberOf coui.Editor
         * @param  {string} name
         * @returns {boolean}
         */
        checkComponentNameDuplication(name) {
            let components = this.openFiles[this.selectedEditor].components.components;
            for (let component in components) {
                let fileName = component;
                console.log(name, fileName);
                if (fileName === name) {
                    return true;
                }
            }
            return false;
        }

        /**
         * @function
         * Check for name duplication in widget folder
         * @memberOf coui.Editor
         * @param  {string} name
         * @returns {boolean}
         */
        checkWidgetNameDuplication(name) {

            return this.listDirectory('widgets', '(.html)', false).then(function (widgetList) {
                for (let i = 0; i < widgetList.length; i++) {
                    let fileName = widgetList[i].url.split('\\')[1];
                    console.log(name, fileName);
                    if (fileName === name) {
                        return true;
                    }
                }

                return false;
            });
        }

        initExportWidgetHandler() {
            var editorId = this.selectedEditor;
            var editor = this.openFiles[editorId];
            if (!editor) {
                return;
            }

            var _this = this;

            var runtimeEditor = editor.runtimeEditor;
            var notAllowed = false;

            var selectedWidgetIds = runtimeEditor.currentParentElementsSelection;
            selectedWidgetIds = selectedWidgetIds.filter(function (obj) {
                return obj !== null;
            });

            for (var i = 0; i < selectedWidgetIds.length; i++) {
                if (runtimeEditor.isWidget(selectedWidgetIds[i]) || !runtimeEditor.isTopLevel(selectedWidgetIds[i])) {
                    notAllowed = true;
                    break;
                }
            }

            if (selectedWidgetIds.length >= MINIMAL_SELECTED_ELEMENTS_FOR_CREATING_A_WIDGET && !notAllowed) {
                var $vex = vex.dialog.open({
                    closeOnOverlayClick: true,
                    contentClassName: 'modal-about',
                    message: '',
                    buttons: [
                        $.extend({}, vex.dialog.buttons.NO, {
                            text: 'Ok',
                            click: function () {
                                notAllowed = false;
                                var inputField: any = document.getElementById('give-widget-name');
                                /* tslint:disable */
                                var unAllowedChar = /(\/|\$|\%|\^|\;|\=|\'|\|\,|\.|\&|\!|\\n|\\r|\\t|\@|\#|\(|\)|\{|\}|\[|\]|\`|<|>|:|"|\||\\|\?|\~|\*)/g;
                                /* tslint:enable */
                                var _widgetName: any = inputField.value;
                                let exportAsComponent = $('#export-widget-type').is(':checked') || false;

                                if (!exportAsComponent) {
                                    _this.checkWidgetNameDuplication(_widgetName + '.html')
                                        .then(function (hasWidgetDuplicationName) {
                                            verifyWidgetName(hasWidgetDuplicationName);
                                        });
                                } else {
                                    let hasComponentNameDuplication =
                                        _this.checkComponentNameDuplication(_widgetName + '.component');
                                    verifyWidgetName(hasComponentNameDuplication);
                                }

                                function verifyWidgetName(hasWidgetNameDuplication) {
                                    let pendingReplace = $('.vex-dialog-button.vex-first')
                                        .hasClass('pending-widget-name-replace');
                                    if (hasWidgetNameDuplication && !pendingReplace) {
                                        $($vex).find('.vex-dialog-message')
                                            .text(Enums.warnMessages.nameDuplication);
                                        $('.vex-dialog-button.vex-first')
                                            .text('replace')
                                            .addClass('pending-widget-name-replace');

                                        $(inputField).one('input', function () {
                                            $('.vex-dialog-button.vex-first')
                                                .text('OK')
                                                .removeClass('pending-widget-name-replace');
                                            $($vex).find('.vex-dialog-message').text('');
                                        });
                                        event.preventDefault();
                                    } else if (_widgetName.search(unAllowedChar) !== -1) {
                                        $($vex).find('.vex-dialog-message').text(
                                            'Invalid widget name! The following characters :' +
                                            ' \\"\' ,/.:|&!\\n\\r\\t@#(){}[]=;^%$`~ ' +
                                            'are not supported in widget names!'
                                        );
                                        inputField.value = '';
                                        event.preventDefault();
                                    } else if (_widgetName.trim().length === 0) {
                                        $($vex).find('.vex-dialog-message').text('Invalid widget name.');
                                        inputField.value = '';
                                        event.preventDefault();
                                    } else if (!helpers.validateId(_widgetName)) {
                                        $($vex).find('.vex-dialog-message')
                                            .text(Enums.warnMessages.idValidationWarning('widget name'));
                                        inputField.value = '';
                                        event.preventDefault();
                                    } else {
                                        $vex.data().vex.value = false;
                                        vex.close($vex.data().vex.id);
                                    }
                                }

                            }
                        }),
                        $.extend({}, vex.dialog.buttons.NO, {
                            text: 'Cancel',
                            click: function () {
                                notAllowed = true;
                                vex.close($vex.data().vex.id);
                            }
                        })
                    ],
                    afterOpen: function ($vexContent) {
                        notAllowed = true;
                        let _that = this;
                        let template = document.querySelector('#vex-create-widget');

                        let clonedElement = _this.importHtmlTemplate(template);

                        // in Development or Hummingbird
                        if (_this.globalEditorInfo.backend === Enums.Backends.Hummingbird ||
                            _this.preferences.couiEnvironment === 'Hummingbird') {
                            clonedElement.querySelector('input[type="checkbox"].convert-units-to-percent')
                                .parentNode.remove();
                            clonedElement.querySelector('input[type="checkbox"].export-widget-type')
                                .parentNode.remove();
                        } else {
                            let inputCheckboxPercent =
                                clonedElement.querySelector('input[type="checkbox"].convert-units-to-percent');
                            inputCheckboxPercent.id = 'convert-units-to-percent';
                            let inputCheckboxWidgetType =
                                clonedElement.querySelector('input[type="checkbox"].export-widget-type');
                            inputCheckboxWidgetType.id = 'export-widget-type';
                        }

                        let inputName = clonedElement.querySelector('input[type="text"]');
                        inputName.id = 'give-widget-name';

                        $(inputName).on('keydown', function (event) {
                            if (event.keyCode === Enums.Keys.enter) {
                                _that.buttons[0].click();
                            }
                        });

                        $vexContent.append(clonedElement);
                    },
                    callback: function (data) {
                        if (!notAllowed) {

                            let convertToPercents = $('#convert-units-to-percent').is(':checked') || false;
                            let exportAsComponent = $('#export-widget-type').is(':checked') || false;
                            let widgetInputName: any = document.getElementById('give-widget-name').value;
                            let _widgetName = widgetInputName;

                            if (exportAsComponent) {
                                _widgetName += '.component';
                                _this.EXPORTING_COMPONENT = true;
                            } else {
                                _widgetName += '.html';
                                _this.EXPORTING_WIDGET = true;
                                _this.PENDING_WIDGET_EXPORT = true;
                            }

                            // reset pinhead position before saving
                            runtimeEditor.Timeline.setPinheadPosition(0, 0);

                            let cleanWidgetId = _this.cleanWidgetName(widgetInputName);

                            let _widgetScene = runtimeEditor.scene;

                            let _editedScene = $.extend(true, {}, _widgetScene);
                            let _newWidgetObject = $.extend(true, {}, _this.environmentProperties.DefaultWidget);

                            let widgets = _editedScene.widgets;

                            _newWidgetObject.id = _this.generateRandomId(cleanWidgetId);

                            _newWidgetObject.type = 'widget';

                            let selectedWidgets = [];
                            let unselectedWidgets = [];

                            // we must separate the selected and unselected widgets.
                            // the selected widgets are going to be children of new
                            // widget
                            for (let i = 0; i < widgets.length; i++) {
                                let widgetId = widgets[i].id;
                                if (selectedWidgetIds.indexOf(widgetId) >= 0) {
                                    selectedWidgets.push(widgets[i]);
                                } else {
                                    unselectedWidgets.push(widgets[i]);
                                }
                            }

                            // set widget geometry and make reposition;
                            // of the top level widgets
                            _newWidgetObject.geometry = runtimeEditor
                                .getWidgetElementSelectionSize(selectedWidgets, convertToPercents);

                            if (selectedWidgets.length >= MINIMAL_SELECTED_ELEMENTS_FOR_CREATING_A_WIDGET) {
                                _newWidgetObject.children = selectedWidgets;
                                _widgetScene.widgets = [_newWidgetObject];
                            } else {
                                _widgetScene.widgets = selectedWidgets;
                            }

                            runtimeEditor.createUndoRedoNewWidget(_newWidgetObject, exportAsComponent);

                            unselectedWidgets.push(_widgetScene.widgets[0]);

                            _editedScene.widgets = unselectedWidgets;

                            _this.openFiles[editorId].tab.tabWidgetState.importedWidget = true;

                            _widgetScene.widgets[0].widgetkit = _widgetName;
                            _widgetScene.animationClasses =
                                runtimeEditor.transferAnimations(_editedScene, selectedWidgets, _widgetName);
                            _this.openFiles[editorId].tab.tabWidgetState.createNewWidget = true;

                            let cleanWidgetScene = JSON.parse(JSON.stringify(_widgetScene));
                            // reset external files(styles and script) before export;
                            cleanWidgetScene.styles = [];
                            cleanWidgetScene.scripts = [];
                            cleanWidgetScene.widgets = [cleanWidgetScene.widgets[0]];
                            cleanWidgetScene.sceneEvents.sceneLoad = '';
                            cleanWidgetScene.style.backgroundColor = 'rgba(255, 255, 255, 0)';

                            if (exportAsComponent) {
                                _this.openFiles[editorId]
                                    .components.create(_widgetName, JSON.stringify(cleanWidgetScene));
                            } else {
                                _this.save(_widgetName, JSON.stringify(cleanWidgetScene));
                            }

                            if (exportAsComponent) {
                                _this.replaceContentOfOpenedComponent(_widgetName, cleanWidgetScene, editorId);
                            }

                            _this.handleFileContent(JSON.stringify(_editedScene),
                                _this.openFiles[editorId].tab.filename, editorId, false, false);
                            _this.EXPORTING_COMPONENT = false;
                        }
                    }
                });
            } else {
                vex.dialog.alert({
                    contentClassName: 'modal-about',
                    message: 'Invalid selection! Please select at least two top level non-widget UI elements.'
                });
            }
        }

        replaceContentOfOpenedComponent(componentName, sceneObj, editorId) {
            for (let i = 0; i < this.openFiles[editorId].components.state.opened.length; i++) {
                let openedEditorId = this.openFiles[editorId].components.state.opened[i];
                if (this.openFiles[openedEditorId].tab.filename === componentName) {
                    this.openFiles[openedEditorId].file = JSON.stringify(sceneObj);
                    return;
                }
            }
        }

        initSceneEventHandlers(runtimeEditor) {
            var _this = this;

            $('#right-pane').off('scroll');
            $('#right-pane').on('scroll', function (event) {
                runtimeEditor.tab.scrollIndex.rightPanel = $(event.target).scrollTop();
            });

            $('#assets-bar-holder').off('scroll');
            $('#assets-bar-holder').on('scroll', function (event) {
                runtimeEditor.tab.scrollIndex.assetLibrary = $(event.target).scrollTop();
            });

            $('#save-scene').off('click');
            $('#save-scene').on('click', function () {
                var currentTab = _this.openFiles[_this.selectedEditor];
                var filename = currentTab.tab.filename;

                _this.save(filename, currentTab.file.valueOf());
            });

            $('#preview-animation-button').off('click');
            $('#preview-animation-button').on('click', function () {
                var previewType = '';

                if (_this.openFiles[_this.selectedEditor].runtimeEditor.repeatAnimation) {
                    previewType = Enums.animationPreviewType.endlessPreview;
                } else {
                    previewType = Enums.animationPreviewType.preview;
                }

                runtimeEditor.preview(previewType);
            });

            $('#prev-animation-button').off('click');
            $('#prev-animation-button').on('click', function () {
                runtimeEditor.Timeline.setToPreviousKeyframe();
            });

            $('#next-animation-button').off('click');
            $('#next-animation-button').on('click', function () {
                runtimeEditor.Timeline.setToNextKeyframe();
            });

            $('#first-animation-button').off('click');
            $('#first-animation-button').on('click', function () {
                runtimeEditor.Timeline.setToFirstKeyframe();
            });

            $('#last-animation-button').off('click');
            $('#last-animation-button').on('click', function () {
                runtimeEditor.Timeline.setToLastKeyframe();
            });

            $('#auto-keyframe-button').off('click');
            $('#auto-keyframe-button').on('click', function () {
                $(this).toggleClass('active');
                runtimeEditor.switchAutoKeyframe();
            });

            $('#repeat-animation-button').off('click');
            $('#repeat-animation-button').on('click', function () {
                $(this).toggleClass('active');
                runtimeEditor.switchRepeatAnimation();
            });

            $('#filter-animation-button').off('click');
            $('#filter-animation-button').on('click', function () {
                runtimeEditor._sceneActionState.primaryAction = 'new action';
                if (_this.preferences.timeline.filterTimelineWidgets) {
                    _this.preferences.timeline.filterTimelineWidgets = false;
                } else {
                    _this.preferences.timeline.filterTimelineWidgets = true;
                }

                _this.savePreferences();

                runtimeEditor.Timeline.resetAllTimeline();
                runtimeEditor.Animations.loadTimeline(runtimeEditor.scene.animationClasses);
            });

            $('.animations-panel a').on('click', function () {
                runtimeEditor.blurOutTextInputs();
            });

            // TODO separate handlers for easier unit testing
            $('#scene-aspect-ratio').off('change');
            $('#scene-aspect-ratio').on('change', function (event) {
                var value = event.target.value;

                runtimeEditor._setUndoRedoCommandsFill({
                    aspectRatio: {
                        type: runtimeEditor.scene.sceneSize.type
                    }
                }, 'new action');

                runtimeEditor.setAspectRatio(value);

                if (value !== 'aspectRatio_custom') {
                    window.requestAnimationFrame(function () {
                        _this.focusCUIFile(_this.selectedEditor);
                    });
                } else {
                    document.querySelector('#aspect-ratio-custom').classList.remove('is-hidden');
                }
            });

            $('#resetZoom').off('click');
            $('#resetZoom').on('click', function () {
                $('#scene').panzoom('setMatrix', 'matrix(1, 0, 0, 1, 0, 0)');
                runtimeEditor.tab.sceneTransformMatrix = [1, 0, 0, 1, 0, 0];
                runtimeEditor.changeSceneScale(1);
            });
        }

        /**
         * Initialize the Edit Menu and Context Menu event handlers
         * @param runtimeEditor
         */
        initEditingEventHandlers(runtimeEditor) {
            var _this = this;

            $('.btn-undo-scene').off('click');
            $('.btn-undo-scene').on('click', function (e) {
                e.preventDefault();
                _this.onundo();
            });

            $('.btn-redo-scene').off('click');
            $('.btn-redo-scene').on('click', function (e) {
                e.preventDefault();
                _this.onredo();
            });

            $('.btn-copy-widgets').off('click');
            $('.btn-copy-widgets').on('click', function (e) {
                e.preventDefault();
                if ($(this).hasClass('disabled')) {
                    return;
                }
                runtimeEditor._sceneActionState.primaryAction = 'new action';
                runtimeEditor.copyWidgets();
            });

            $('.btn-paste-widgets').off('click');
            $('.btn-paste-widgets').on('click', function (e) {
                e.preventDefault();
                runtimeEditor.cloneWidget();
            });

            $('.btn-createWidget-scene').off('click');
            $('.btn-createWidget-scene').on('click', function (e) {
                e.preventDefault();
                if ($(this).hasClass('disabled')) {
                    return;
                }

                _this.initExportWidgetHandler();
            });

            $('.btn-delete-widget').off('click');
            $('.btn-delete-widget').on('click', function (e) {
                e.preventDefault();
                if ($(this).hasClass('disabled')) {
                    return;
                }

                _this.delete();
            });
        }

        initScenePanZoom(runtimeEditor) {
            var _this = this;
            var sceneTransform;
            var tab = runtimeEditor.tab;
            var $sceneScaleSlider = $('#sceneScale');
            var $sceneZoomButtons = $('.sceneZoomButton');
            var $sceneCenter = $('#top-scene-holder');
            var $sceneZoomInput = $('#slider-zoom-percent');

            if (tab.sceneTransformMatrix) {
                sceneTransform = 'matrix(' + tab.sceneTransformMatrix.join(' ,') + ')';
            } else {
                sceneTransform = 'matrix(1, 0, 0, 1, 0, 0)';
                tab.sceneTransformMatrix = [1, 0, 0, 1, 0, 0];
            }

            /**
             * Toggle active state for kendo buttons
             * @param active {String} - active element's id
             * @param inactive {String} - inactive element's id
             */
            var toggleActive = function (activeElementId, inactiveElementId) {
                $(activeElementId).addClass('k-state-active');
                $(inactiveElementId).removeClass('k-state-active');
            };

            runtimeEditor.sceneWrapper.panzoom('destroy');
            $('#scene').panzoom('destroy');

            var $panzoom = $('#scene-wrapper').panzoom({
                disablePan: true,
                startTransform: sceneTransform,
                minScale: 0.3,
                maxScale: 1.7,
                cursor: 'default',
                $set: $('#scene')
            });

            $('#scene').panzoom({disablePan: true});

            var newScaleValue = tab.sceneTransformMatrix[0];
            runtimeEditor.changeSceneScale(newScaleValue);

            // ALT KEY PRESSED //
            $('html').off('keydown');
            $('html').on('keydown', function (event) {
                if (event.which === Enums.Keys.alt) {
                    event.preventDefault();

                    runtimeEditor.isInPanningMode = true;
                    toggleActive('#pan-tool', '#select-tool');
                    $panzoom.panzoom('option', {disablePan: false});

                    $('.onoff').css('z-index', -1);
                    _this.panningCursor(true);
                }
            });

            // ALT KEY RELEASED //
            $('html').off('keyup');
            $('html').on('keyup', function (event) {
                if (event.which === Enums.Keys.alt && !keepPanningMode) {
                    event.preventDefault();

                    runtimeEditor.isInPanningMode = false;
                    toggleActive('#select-tool', '#pan-tool');
                    $panzoom.panzoom('option', {disablePan: true});

                    _this.panningCursor(false);
                    $('.onoff').css('z-index', 999);

                    tab.sceneTransformMatrix = $panzoom.panzoom('getMatrix').map(function (result) {
                        return parseFloat(result);
                    });
                }
            });

            // Prevent continuous Alt key push after Alt+Tab
            // by disabling panning when windows loses focus
            $(window).off('blur');
            $(window).on('blur', function () {
                if (!keepPanningMode) {

                    runtimeEditor.isInPanningMode = false;
                    toggleActive('#select-tool', '#pan-tool');
                    _this.panningCursor(false);

                    $panzoom.panzoom('option', {disablePan: true});
                }
            });

            // MOUSE WHEEL CHANGE //
            $panzoom.parent().on('mousewheel.focal', function (e) {
                e.preventDefault();
                var delta = e.delta || e.originalEvent.wheelDelta;
                var zoomOut = delta ? delta < 0 : e.originalEvent.deltaY > 0;

                $panzoom.panzoom('zoom', zoomOut, {
                    animate: false,
                    increment: 0.1,
                    focal: e
                });

                tab.sceneTransformMatrix = $panzoom.panzoom('getMatrix').map(function (result) {
                    return parseFloat(result);
                });

                var newScaleValue = tab.sceneTransformMatrix[0];

                runtimeEditor.changeSceneScale(newScaleValue);
            });

            var $scenePropertySlider = $('#scene-property');

            // RANGE SLIDER CHANGE //
            $scenePropertySlider.off('mousedown');
            $scenePropertySlider.on('mousedown', function () {
                runtimeEditor.WidgetSelection.detachHandlers();
            });

            $scenePropertySlider.off('mouseup mouseleave');
            $scenePropertySlider.on('mouseup mouseleave', function () {
                runtimeEditor.WidgetSelection.attachHandlers();
            });

            $sceneScaleSlider.off('input change');
            $sceneScaleSlider.on('input change', function (event) {
                event.preventDefault();

                runtimeEditor._dragFlag = false;

                var focalHolder = {
                    'clientX': $sceneCenter.offset().left + $sceneCenter.width() / 2,
                    'clientY': $sceneCenter.offset().top + $sceneCenter.height() / 2
                };

                var newScaleValue = event.target.value;

                //The top value for the input range is 170 the bottom is 30.
                //The top value for the panzoom matrix scale is 1.7 the bottom 0.3.
                //The above returnes the ne value by leveling the input and multipling with the leveled scale.

                newScaleValue = (((newScaleValue - 30) / 140) * 1.4 + 0.3);

                $panzoom.panzoom('zoom', newScaleValue, {
                    disablePan: false,
                    focal: focalHolder
                });

                runtimeEditor.changeSceneScale(newScaleValue);
                tab.sceneTransformMatrix = $panzoom.panzoom('getMatrix').map(function (result) {
                    return parseFloat(result);
                });
            });

            // ZOOM BUTTONS PRESSED //
            $sceneZoomButtons.off('mousedown');
            $sceneZoomButtons.on('mousedown', function (event) {
                event.preventDefault();

                var zoomOut = this.id === 'incrementSceneZoom' ? false : true;

                var focalHolder = {
                    'clientX': $sceneCenter.offset().left + $sceneCenter.width() / 2,
                    'clientY': $sceneCenter.offset().top + $sceneCenter.height() / 2
                };

                $panzoom.panzoom('zoom', zoomOut, {
                    disablePan: false,
                    increment: 0.025 + Math.random() * 0.0001,
                    focal: focalHolder
                });

                tab.sceneTransformMatrix = $panzoom.panzoom('getMatrix').map(function (result) {
                    return parseFloat(result);
                });

                var newScaleValue = tab.sceneTransformMatrix[0];
                runtimeEditor.changeSceneScale(newScaleValue);
            });

            $sceneZoomInput.off('mousedown');
            $sceneZoomInput.on('mousedown', function () {
                var slider = $(this);
                var currentVal = slider.val();
                if (currentVal[currentVal.length - 1] === '%') {
                    slider.val(currentVal.slice(0, -1));
                }
            });

            $sceneZoomInput.off('blur keyup');
            $sceneZoomInput.on('blur keyup', function (e) {
                event.preventDefault();
                var slider = $(this);
                var newScaleValue = parseFloat(slider.val());

                if (e.type === 'blur' || e.keyCode === Enums.Keys.enter) {

                    var focalHolder = {
                        'clientX': $sceneCenter.offset().left + $sceneCenter.width() / 2,
                        'clientY': $sceneCenter.offset().top + $sceneCenter.height() / 2
                    };

                    if (newScaleValue > 170) {
                        newScaleValue = 170;
                    } else if (newScaleValue < 30) {
                        newScaleValue = 30;
                    }

                    newScaleValue = (((newScaleValue - 30) / 140) * 1.4 + 0.3);

                    $panzoom.panzoom('zoom', newScaleValue, {
                        disablePan: false,
                        focal: focalHolder
                    });

                    tab.sceneTransformMatrix = $panzoom.panzoom('getMatrix').map(function (result) {
                        return parseFloat(result);
                    });

                    if (e.keyCode === Enums.Keys.enter) {
                        slider.blur();
                    }

                    runtimeEditor.changeSceneScale(newScaleValue);
                }
            });

            tab.sceneTransformMatrix = $panzoom.panzoom('getMatrix').map(function (result) {
                return parseFloat(result);
            });
        }

        initKendoSplitter() {
            var _this = this;

            $('#horizontal').kendoSplitter({
                orientation: 'horizontal',
                panes: [{
                    resizable: false,
                    collapsible: true,
                    size: '280px'
                }],
                resize: function () {
                    $('#left-pane').height($('#left-pane').height() + 15);
                }
            });

            $('#vertical').kendoSplitter({
                orientation: 'vertical',
                panes: [{
                    collapsible: true
                }, {
                    collapsible: true,
                    min: '100px',
                    size: '35%'
                }],
                collapse: function (e) {
                    if (e.pane.id === 'middle-pane') {
                        $('.info-widgets').getNiceScroll().hide();
                        _this.detachedAmnimationsHTML = $('.animations-panel').detach();
                    }
                },
                expand: function (e) {
                    if (_this.detachedAmnimationsHTML !== null) {
                        $('#middle-pane').append(_this.detachedAmnimationsHTML);
                        _this.detachedAmnimationsHTML = null;
                        _this.resizeTimelineVertical();
                        $('.info-widgets').getNiceScroll().show();
                    }
                },
                resize: function () {
                    $('#middle-pane').hide().show(0);
                    $('#assets-bar .files-cont').hide().show(0);
                    if ($('#horizontal-timeline-splitter').data('kendoSplitter')) {
                        $('#horizontal-timeline-splitter').data('kendoSplitter').trigger('resize');
                    }
                    _this.resizeTimelineVertical();
                }
            });

            $('#horizontal-inner').kendoSplitter({
                orientation: 'horizontal',
                panes: [{
                    collapsible: true
                }, {
                    collapsible: true,
                    min: '270px',
                    size: '270px'
                }]
            });

            this.resizeTimelineVertical();
        }

        resizeTimelineVertical() {
            const runtimeEditor = this.openFiles[this.selectedEditor].runtimeEditor;
            var timelineHeight = $('.timeline-wrap').height();

            $('.widgets-keyframes').height(timelineHeight - 62);
            $('.info-widgets').height(timelineHeight - 62);
            $('.pin-height').height(timelineHeight - 42);

            $('#assets-bar .files-cont').hide().show(0);
            $('.info-widgets').getNiceScroll().resize();

            let scenePropertiesHeight = $('.scene-properties-holder').height();
            let sceneHierarchyHeight = $('.scene-hierarchy-holder').height();
            let holderHeight = $('#right-pane').height();

            let newAssetsHeight = holderHeight - (scenePropertiesHeight + sceneHierarchyHeight);

            if (!$('#assets-bar').is(':visible')) {
                $('.scene-library-holder').css('height', Enums.assetsLibraryLabelSize);
            } else {
                newAssetsHeight =
                    (newAssetsHeight < Enums.assetLabelsEntryHeight) ? Enums.assetLabelsEntryHeight : newAssetsHeight;
                $('.scene-library-holder').css('height', newAssetsHeight);
                $('#assets-bar').css('height', newAssetsHeight - Enums.assetsLibraryLabelSize);
                $('#assets-bar-holder').css('height', newAssetsHeight - Enums.assetLibraryPreviewSize);

                // resizeTimelineVertical is triggered before runtimeEditor is created
                // and before the initialization of the virtual list
                if (runtimeEditor && runtimeEditor.virtualList.isInstantiated()) {
                    runtimeEditor.virtualList.refresh();
                }
            }
        }

        initTimelineScroll() {

            $('.info-widgets').niceScroll({
                cursoropacitymin: 1,
                cursorwidth: 10,
                cursorborder: '1px solid #262626',
                cursorborderradius: 0,
                cursorcolor: '#1C1C1C'
            });

            $('.info-widgets').scroll(function (e) {
                var t = $('.info-widgets').scrollTop();
                $('.widgets-keyframes').scrollTop(t);
            });
        }

        createModalSceneEditor(runtimeEditor,
                               eventName) {

            if (runtimeEditor.scene.sceneEvents === undefined) {
                runtimeEditor.scene.sceneEvents = {
                    sceneLoad: ''
                };
            }

            var sceneJsCode = runtimeEditor.scene.sceneEvents[eventName];

            runtimeEditor.createModalJsEditor(eventName, 'sceneEvents',
                sceneJsCode);
        }

        initKendoToolbar(runtimeEditor) {
            var _this = this;

            function showHideExtraToolbar() {
                $('#extra-toolbar').hide();
            }

            /**
             * Toggle scene panning based on button clicked
             * @param event {MouseEvent} - click event returned from kendoToolBar button group
             */
            var handleScenePanning = function (event) {
                if (event.id === 'pan-tool') {
                    runtimeEditor.isInPanningMode = true;
                } else if (event.id === 'select-tool') {
                    runtimeEditor.isInPanningMode = false;
                }

                keepPanningMode = runtimeEditor.isInPanningMode;
                _this.panningCursor(runtimeEditor.isInPanningMode);

                runtimeEditor.scenePanning();
            };

            var handleSceneSnapping = function (event) {
                var state = _this.openFiles[_this.selectedEditor].tab.snapOn;
                _this.openFiles[_this.selectedEditor].tab.snapOn = !state;
                $(event.target).toggleClass('k-state-active', !state);
            };

            // remove previous init
            $('.k-list-container').remove();

            $('#animation-toolbar').kendoToolBar({
                items: [{
                    type: 'button',
                    spriteCssClass: 'btn-first-keyframe',
                    id: 'first-animation-button',
                }, {
                    type: 'button',
                    spriteCssClass: 'btn-previous-keyframe',
                    id: 'prev-animation-button'
                }, {
                    type: 'button',
                    spriteCssClass: 'fa btn-play',
                    id: 'preview-animation-button'
                }, {
                    type: 'button',
                    spriteCssClass: 'btn-next-keyframe',
                    id: 'next-animation-button'
                }, {
                    type: 'button',
                    spriteCssClass: 'btn-last-keyframe',
                    id: 'last-animation-button'
                }, {
                    type: 'button',
                    spriteCssClass: 'ic-autokeyframe',
                    id: 'auto-keyframe-button',
                    class: 'btn-autokeyframe',
                    togglable: true
                }, {
                    type: 'button',
                    spriteCssClass: 'ic-repeat-animation',
                    id: 'repeat-animation-button',
                    class: 'btn-repeat-animation',
                    togglable: true
                }, {
                    type: 'button',
                    spriteCssClass: 'fa fa-filter btn-filter-animation',
                    id: 'filter-animation-button',
                    class: 'btn-filter-animation',
                    togglable: true,
                    selected: _this.preferences.timeline.filterTimelineWidgets
                }]
            });

            $('#toolbar').kendoToolBar({
                items: [{
                    type: 'button',
                    spriteCssClass: 'fa fa-file',
                    id: 'new-file',
                    click: function () {
                        $(window).trigger('createNewScene');
                    }
                }, {}, {
                    type: 'button',
                    spriteCssClass: 'fa fa-folder-open',
                    id: 'open-file',
                    click: function () {
                        _this.onopen();
                    }
                }, {}, {
                    type: 'button',
                    spriteCssClass: 'fa fa-save',
                    id: 'save-scene'
                }, {
                    type: 'button',
                    spriteCssClass: 'fa fa-external-link',
                    id: 'publish-scene',
                    click: () => {
                        runtimeEditor.publishScene();
                    }
                }, {
                    type: 'button',
                    spriteCssClass: 'fa fa-undo',
                    id: 'undo-scene',
                    click: function () {
                        runtimeEditor.undoRedoScene('undo');
                        _this.autoTriggerUndoRedo('undo');
                    }
                }, {}, {
                    type: 'button',
                    spriteCssClass: 'fa fa-repeat',
                    id: 'redo-scene',
                    click: function () {
                        runtimeEditor.undoRedoScene('redo');
                        _this.autoTriggerUndoRedo('redo');
                    }
                }, {
                    type: 'button',
                    spriteCssClass: 'fa fa-magnet',
                    id: 'snap-scene',
                    togglable: true,
                    selected: _this.openFiles[_this.selectedEditor].tab.snapOn,
                    toggle: handleSceneSnapping
                }, {
                    type: 'button',
                    spriteCssClass: 'fa fa-mouse-pointer',
                    id: 'select-tool',
                    togglable: true,
                    group: 'scene-buttons',
                    selected: true,
                    toggle: handleScenePanning
                }, {
                    type: 'button',
                    spriteCssClass: 'fa fa-hand-paper-o',
                    id: 'pan-tool',
                    togglable: true,
                    group: 'scene-buttons',
                    toggle: handleScenePanning
                }, {
                    type: 'button',
                    imageUrl: './img/resetZoom.png',
                    id: 'resetZoom'
                }, {
                    type: 'button',
                    imageUrl: './img/help.png',
                    id: 'help',
                    click: function () {
                        _this.onLaunchUrl(Enums.Links.documentation);
                    }
                }]
            });

            _this.setToolbarTitles();
        }

        initRightPanelItem(runtimeEditor) {
            this.initKendoDropDownsInRightPane();
            this.initKendoToolbarsInRightPane(runtimeEditor);
            this.initInputSupportForGTInRightPane(runtimeEditor);
            this.initSceneBackgroundColorPicker(runtimeEditor);
        }

        initKendoDropDownsInRightPane() {
            $('#right-pane select').kendoDropDownList({
                animation: false
            });

            return this;
        }

        initKendoToolbarsInRightPane(runtimeEditor) {
            var _this = this;

            $('#scene-events').kendoToolBar({
                items: [{
                    type: 'splitButton',
                    overflow: 'never',
                    text: 'Events',
                    id: 'scene-events-split-button',
                    menuButtons: [{
                        text: 'on scene load',
                        id: 'sceneLoad',
                        click: function (data) {
                            _this.createModalSceneEditor(runtimeEditor, data.id);
                        }
                    }]
                }]
            });
        }

        initInputSupportForGTInRightPane(runtimeEditor) {
            runtimeEditor.inputNumbersFocusInOutHandler('right-pane');
        }

        initSceneBackgroundColorPicker(runtimeEditor) {
            var $sceneBackgroundColorPicker = $('#scene-background-picker');

            $sceneBackgroundColorPicker.kendoColorPicker({
                buttons: false,
                value: runtimeEditor.scene.getStyleProperty('backgroundColor'),
                opacity: true,

                close: function (event) {
                    runtimeEditor.setSceneStyleProperty('backgroundColor', event.sender.element.val());
                },
                select: function (event) {
                    runtimeEditor.scene.applyStyleProperty('backgroundColor', event.sender.element.val());
                }
            });
        }

        autoTriggerUndoRedo(type) {
            const editor = this.openFiles[this.selectedEditor];
            const runtimeEditor = editor.runtimeEditor;
            const redo = editor.redo[0];
            const isPrevRedoComponentDel = editor.undo[0] && editor.undo[0][0].deleteComponent;

            if (redo &&
                ((redo[0].deleteElement && redo[0].deleteElement.createWidget.isNewWidget) ||
                    redo[0].deleteComponent)) {
                runtimeEditor.undoRedoScene(type);
            }

            // case where the component is deleted with previous undo steps
            if (redo && redo[0].createElement && isPrevRedoComponentDel) {
                const widget = redo[0].createElement.deleteWidget;

                if (widget.widgetkit && widget.widgetkit.endsWith('.component')) {
                    runtimeEditor.undoRedoScene(type);
                }
            }
        }

        /**
         * @function
         * @memberOf coui.Editor
         * Create new empty scene + auto trigger save after
         */
        createNewScene(options, fileWithPath): void {
            const path = helpers.getFileAndPath(fileWithPath).path;
            engine.call('SetCurrentSceneURL', path);

            let newFilename = fileWithPath;
            const sceneCount: number = Object.keys(this.openFiles).length;
            const newScene = $.extend(true, {}, Enums.newScene);

            options.createdNow = true;
            if (options) {
                $.extend(true, newScene, options);
            }

            if (sceneCount > 0) {
                for (let scene in this.openFiles) {
                    let {path, filename} = this.getSceneFileAndPath(scene);
                    const forwardSlashPath = path.replace(/\//g, '\\');

                    if (forwardSlashPath + filename === fileWithPath) {
                        this.clearTab(scene, event);
                    }
                }
            }

            this.openFile(JSON.stringify(newScene), newFilename);
            $('#loader-wrapper').show();

            this.save(newFilename, JSON.stringify(newScene), true);
            newFileCounter++;

            this.openFiles[this.selectedEditor].tab.sceneID = this.fileId;
            this.openFiles[this.selectedEditor].tab.pendingSave = false;
        }

        createNewTestScene(fileName, options): void {
            let newFilename = '';
            if (fileName) {
                newFilename = fileName + '.html';
            } else {
                newFilename = '*new' + newFileCounter + '.html';
            }

            let newScene = $.extend(true, {}, Enums.newScene);
            if (options) {
                $.extend(true, newScene, options);
            }

            this.openFile(JSON.stringify(newScene), newFilename);
            newFileCounter++;
        }

        /**
         * @function
         * @memberOf coui.Editor
         * @param type - widget type
         * @returns {*} - returns the type + widget type number `rectangle1`
         */
        generateRandomId(type) {
            this.incrementWidgetTypesCounter(type);
            var widgetTypeAddedCounter = this.getWidgetCounterForType(type);
            return type + widgetTypeAddedCounter;
        }

        /**
         * @function
         * @memberOf coui.Editor
         * return string
         */
        public generateClassName(): string {
            let editor = this.openFiles[this.selectedEditor];
            let newClassName = Enums.animationClassPrefix + 'animation' +
                this.classNamesCount++;
            const filename = editor.tab.filename;
            const isComponent: boolean = filename.endsWith('.component');

            if (editor.runtimeEditor.scene.animationClasses[newClassName]) {
                return this.generateClassName();
            }

            for (let i = 0; i < editor.components.state.opened.length; i++) {
                let openedComponentId = editor.components.state.opened[i];
                let sceneComponent = this.openFiles[openedComponentId].runtimeEditor.scene;
                if (sceneComponent.animationClasses[newClassName]) {
                    return this.generateClassName();
                }
            }

            if (isComponent) {
                let editorId = editor.tab.tabWidgetState.instanceOf;
                let linkedEditor = this.openFiles[editorId];
                let scene = JSON.parse(linkedEditor.file);
                if (scene.animationClasses[newClassName]) {
                    return this.generateClassName();
                }
            }

            return newClassName;
        }

        incrementWidgetTypesCounter(type) {
            var editorTab = this.openFiles[this.selectedEditor];
            editorTab.widgetTypesCounter = editorTab.widgetTypesCounter || {};

            if (!editorTab.widgetTypesCounter[type]) {
                editorTab.widgetTypesCounter[type] = 0;
            }

            var currentTypeCount = editorTab.widgetTypesCounter[type] + 1;
            var runtimeEditor = editorTab.runtimeEditor;

            if (runtimeEditor) {

                while (runtimeEditor.mappedWidgets[type + currentTypeCount] !== undefined) {
                    editorTab.widgetTypesCounter[type]++;
                    currentTypeCount = editorTab.widgetTypesCounter[type] + 1;
                }
            }

            editorTab.widgetTypesCounter[type]++;
        }

        getWidgetCounterForType(type) {
            return this.openFiles[this.selectedEditor].widgetTypesCounter[type];
        }

        cleanupImportedHTML(content: string) {
            // remove register component script
            let regexRegisterComponent =
                /<!-- Register Components Start -->([\s\S]*?)<!-- Register Components End -->/;
            let resultRegister = regexRegisterComponent.exec(content);

            if (resultRegister !== null) {
                content = content.replace(resultRegister[0], '');
            }

            // remove components from scene html
            let regexWrapperComponent = /<!-- Wrapper Components Start -->([\s\S]*?)<!-- Wrapper Components End -->/;
            let resultWrapper = regexWrapperComponent.exec(content);

            if (resultWrapper !== null) {
                content = content.replace(resultWrapper[0], '');
            }

            return content;
        }

        handleFileContent(content, filename, editorID, initialImport, runtimeImport) {
            let isComponent = filename.endsWith('.component');
            let dfd = $.Deferred();

            // INITIALISE CUSTOM USER CONTENT IN TAB //
            if (this.openFiles[this.selectedEditor].tab.originalHTML === undefined
                && !this.isJson(content)
                && !isComponent) {
                this.openFiles[this.selectedEditor].tab.originalHTML = this.cleanupImportedHTML(content.valueOf());
            }

            initialImport = initialImport ? initialImport : false;
            runtimeImport = runtimeImport ? runtimeImport : false;

            var isPhotoshop = this.photoshopExport(content);
            var innerHTML,
                innerStyles,
                animationCSS;

            var regexCssAnim = /\/\* CSS Animations Start \*\/([\s\S]*?)\/\* CSS Animations End \*\//;
            var regexAspectRatio = /\/\* Aspect Ratio Start \*\/([\s\S]*)\/\* Aspect Ratio End \*\//;

            // TODO when this file become a module - use globals.marker
            var regexSceneProperties = /\/\* Scene Properties Start \*\/([\s\S]*)\/\* Scene Properties End \*\//;
            var bodyRegex = /<body>([\s\S]*)<\/body>/;

            if (!isPhotoshop) {
                if (bodyRegex.exec(content.valueOf())) {
                    innerHTML = bodyRegex.exec(content.valueOf())[0];
                }
            } else {
                /* tslint:disable */
                var regexPhotoshopContent = /<!-- Save for Web Slices \([\s\S]+\) -->([\s\S]+)<!-- End Save for Web Slices -->/;
                var regexPhotoshopStyles = /<!-- Save for Web Styles \([\s\S]+\) -->([\s\S]+)<!-- End Save for Web Styles -->/;
                /* tslint:enable */

                if (regexPhotoshopContent.exec(content.valueOf()) && regexPhotoshopStyles.exec(content.valueOf())) {
                    innerHTML = regexPhotoshopContent.exec(content.valueOf())[1];
                    innerStyles = regexPhotoshopStyles.exec(content.valueOf())[1];

                    this.addTempStyleHolder(innerStyles);
                }
            }

            var sceneObj: any = {};
            // On the initial load we expect HTML otherwise object
            if (innerHTML === undefined) {
                sceneObj = JSON.parse(content.valueOf());
                if (this.openFiles[this.selectedEditor].tab.tabWidgetState.importedWidget && runtimeImport) {
                    let regExp = /.html/;
                    let widgetName = sceneObj.widgets[0].widgetkit.replace(regExp, '');
                    widgetName = this.cleanWidgetName(widgetName);
                    let newId = this.generateRandomId(widgetName);
                    sceneObj.widgets[0].id = newId;
                }
            } else if (isPhotoshop) {
                sceneObj = this.buildHTML(innerHTML);
            } else {
                var coherentRegex = /<!-- Coherent Editor Start -->([\s\S]*)<!-- Coherent Editor End -->/;
                var coherentContent = coherentRegex.exec(content.valueOf())[1];
                const fontsTag = /<style id="coui_font_faces">([\s\S]*?)<\/style>/.exec(content.valueOf());
                let fontsContent;

                if (fontsTag) {
                    fontsContent = fontsTag[1];
                }

                var coherentBody = coherentContent.split('<body>')[1];
                sceneObj = this.buildHTML(coherentBody, fontsContent);
            }

            // Set Scene Properties
            if (regexSceneProperties.exec(content)) {
                var SceneProperties;
                SceneProperties = regexSceneProperties.exec(content)[1];
                SceneProperties = SceneProperties.match(/{.+}/);
                SceneProperties = JSON.parse(SceneProperties[0]);

                // backwards compatibility
                if (SceneProperties.style) {
                    // new
                    sceneObj.style = SceneProperties.style;
                    this.animationBelongsTo = SceneProperties.sceneType;
                } else {
                    // old
                    sceneObj.style = SceneProperties;
                }
            }

            if (regexCssAnim.exec(content)) {
                animationCSS = regexCssAnim.exec(content.valueOf())[1];
            }

            // Set Aspect Ratio
            if (regexAspectRatio.exec(content)) {
                var aspectRatio;
                aspectRatio = regexAspectRatio.exec(content)[1];
                aspectRatio = aspectRatio.match(/{.+}/);
                aspectRatio = JSON.parse(aspectRatio[0]);
                sceneObj.sceneSize = aspectRatio;
            }

            // Set animations object
            if (animationCSS && animationCSS !== '') {
                sceneObj.animationClasses = importCssModule.importCss(animationCSS);
            }
            var newImportedWidget = sceneObj.widgets[0];

            if (this.openFiles[this.selectedEditor].tab.tabWidgetState.importedWidget && runtimeImport) {
                sceneObj = this.extendObj(this.openFiles[this.selectedEditor].runtimeEditor.scene, sceneObj);
            }

            const editor = this.openFiles[this.selectedEditor];

            if (sceneObj.widgets && this.checkWidget(sceneObj.widgets) && !initialImport && !isComponent) {
                sceneObj = this.updateWidget(sceneObj);
                editor.tab.tabWidgetState.importedWidget = true;

                if (sceneObj instanceof Promise) {
                    sceneObj.then((data) => {

                        this.animationBackwardsCompatibility(data.widgets);
                        this.loopAnimationsIds(data.animationClasses).then((finalAnimations) => {
                            // if (!finalAnimations) {
                            //     return;
                            // }
                            data.animationClasses = finalAnimations;
                            if (runtimeImport) {
                                editor.runtimeEditor.saveUndoRedoAfterCreateElement(
                                    newImportedWidget.id, newImportedWidget);
                            }

                            data.animations = {};

                            this.rebuildRuntimeEditor(data, finalAnimations, filename, editorID);
                            dfd.resolve();
                        });
                    });
                } else {
                    this.animationBackwardsCompatibility(sceneObj.widgets);
                    sceneObj.animations = {};
                    this.rebuildRuntimeEditor(sceneObj, sceneObj.animationClasses, filename, editorID);
                    dfd.resolve();
                }
            } else {
                if (initialImport && !runtimeImport) {
                    return sceneObj;
                }

                this.animationBackwardsCompatibility(sceneObj.widgets);
                sceneObj.animations = {};
                this.rebuildRuntimeEditor(sceneObj, sceneObj.animationClasses, filename, editorID);
                dfd.resolve();
            }
            return dfd.promise();
        }

        /**
         * @function
         * Generate random ids for all child widgets
         * @memberOf coui.Editor
         * @param {widget}
         * @returns {widget}
         */
        loopWidgetChildAndGenerateIds(widget) {
            let _this = this;
            let newWidget = $.extend(true, {}, widget);

            function loopChild(children) {
                for (let i = 0; i < children.length; i++) {
                    children[i].id = _this.generateRandomId(children[i].type);
                    if (children[i].children.length > 0) {
                        loopChild(children[i].children);
                    }
                }
            }

            loopChild(newWidget.children);
            return newWidget;
        }

        animationBackwardsCompatibility(widgets) {

            // Backwards compatibility
            var animationIds = this.animationsBackwardsCompatibility.idsToClasses;
            var len = animationIds.length;

            for (var i = 0; i < len; i++) {
                for (var j = 0; j < widgets.length; j++) {
                    if (widgets[j].id === animationIds[i]) {
                        if (!helpers.doesClassNameExist(widgets[j].className, animationIds[i])) {
                            widgets[j].className = animationIds[i];
                        }
                    }
                    if (widgets[j].children.length > 0) {
                        this.animationBackwardsCompatibility(widgets[j].children);
                    }
                }
            }
            // end of backwards compatibility
        }

        loopAnimationsIds(newClasses) {
            var runtimeEditor = this.openFiles[this.selectedEditor].runtimeEditor;
            if (!runtimeEditor) {
                return new Promise(function (resolve) {
                    var finalAnimations = newClasses || {};
                    return resolve(finalAnimations);
                });
            }

            var originClasses = runtimeEditor.scene.animationClasses;
            var promisses = [];

            var vexConfirm = function () {
                var p = new Promise(function (resolve) {
                    var $vex = vex.dialog.confirm({
                        closeOnOverlayClick: true,
                        contentClassName: 'modal-about',
                        message: Enums.Messages.overwriteWidgetAnimations,
                        buttons: [
                            $.extend({}, vex.dialog.buttons.NO, {
                                text: 'Yes',
                                click: function () {
                                    vex.close($vex.data().vex.id);
                                    resolve('yes');

                                }
                            }), $.extend({}, vex.dialog.buttons.NO, {
                                text: 'No',
                                click: function () {
                                    vex.close($vex.data().vex.id);
                                    resolve('no');
                                }
                            })
                        ]
                    });
                });

                promisses.push(p);
            };

            if (this.openFiles[this.selectedEditor].runtimeEditor) {

                for (var originClassName in originClasses) {
                    var newAnimationClassName = newClasses[originClassName];
                    if (newAnimationClassName &&
                        newAnimationClassName.belongTo !== originClasses[originClassName].belongTo) {
                        vexConfirm();
                        break;
                    }
                }

                promisses.push(new Promise(function (resolve) {
                    return resolve('yes');
                }));
            }

            return Promise.all(promisses).then(function (data) {
                if (data[0] === 'yes') {
                    return $.extend(true, originClasses, newClasses);
                } else {
                    return originClasses;
                }
            });
        }

        /*jshint latedef: false */
        /**
         * - Remove the absolute editor paths and convert them to relative
         * based on root level folder, ready to be exported.
         * - Changing webkit-transform's, image and asset (css and js files) paths.
         * - Clean font-family: initial css properties from elements.
         * @param htmlContents - the raw HTML markup coming from the editor
         * @returns {string}
         */
        cleanupHtmlExport(htmlContents: string): string {
            let regexUrl;
            const regexWebkit = /-webkit-transform/g;

            // get the global and local events code as string
            const eventsCode = htmlContents.match(/<script class="global-events">[\s\S]*\/script>/)[0];

            // split the content
            // html[0] - the editable HTML string
            // html[1] - the ending coherent comment with the closing body and html code
            const html = htmlContents.split(eventsCode);

            if (this.globalEditorInfo.backend === Enums.Backends.Unreal) {
                regexUrl = /coui:\/\/editor\//gi;
            } else {
                regexUrl = /coui:\/\/uiresources\/editor\//gi;
            }

            let cleanedHtml = html[0].replace(regexWebkit, 'transform').replace(regexUrl, '');
            const scenePath = this.openFiles[this.selectedEditor].tab.filePath;

            // change relative responsive image and font face paths
            cleanedHtml = cleanedHtml.replace(/url\((.*?)\)/gi, (RegSelection, RegGroup) => {
                // cases where the url comes from data-<event> attribute
                // on the scene element
                if (RegSelection.match(/&quot;/)) {
                    return RegSelection;
                }

                const quotes = RegGroup[0] === '"' ? `"` : `'`;
                const imageURL = this.handleRelativePath(RegGroup);

                return `url(${quotes}${imageURL}${quotes})`;
                // change relative and absolute (coui://) image src paths and js asset files
            }).replace(/src="([coui:\/\/]|.*?)"/gi, (regSelection, regGroup) => {
                const extension = regGroup.split('.').pop();
                let convertedPath = regGroup;
                if (extension !== 'js') {
                    convertedPath = this.handleImageCouiPath(regGroup);
                } else {
                    const scriptPath = helpers.getFileAndPath(regGroup).path;
                    const filename = helpers.getFileAndPath(regGroup).filename;
                    convertedPath = helpers.externalFilePathHandler(scriptPath, scenePath) + filename;
                }

                return `src="${convertedPath}"`;
                // change the CSS paths based on the root folder
            }).replace(/<link.*href="([coui:\/\/]|.*?)">/gi, function (regSelection, regGroup) {
                const scriptPath = helpers.getFileAndPath(regGroup).path;
                const filename = helpers.getFileAndPath(regGroup).filename;
                const convertedPath = helpers.externalFilePathHandler(scriptPath, scenePath) + filename;

                return `<link rel="stylesheet" type="text/css" class="styles" href="${convertedPath}">`;
            }).replace(/background-position: initial initial/gi, function (regSelection, regGroup) {
                return `background-position: initial`;
                // TODO: remove this when is fixed in the backend
                // front end workaround!!!
                // Currently the backend exports wrong property name -webkit-blend-mode
            }).replace(/-webkit-blend-mode/gi, function (regSelection, regGroup) {
                return `mix-blend-mode`;
                // clean font-family: initial css properties
            }).replace(/font-family: initial;/g, '')
            // workaround for webkit-mask-size's second missing value.
            // if the two values are the same, the backend merges them, which by standard
            // is not the same behavior as including two values for width and height
            // TODO: remove when fixed
                .replace(/(-webkit-)?mask-size: (.*?);/gi, (regSelection) => {
                    return helpers.transformMaskSize(regSelection);
                });

            return [
                cleanedHtml,
                eventsCode,
                html[1]
            ].join('');
        }

        private handleImageCouiPath(url) {
            const scenePath = this.openFiles[this.selectedEditor].tab.filePath.replace(/\\/g, '/');
            const isCouiPath = url.match(/coui:\/\//);
            let {path, filename} = helpers.getFileAndPath(url);

            const regExp = new RegExp(`^${scenePath}`);
            let convertedPath = '';

            // if the url is an absolute "coui://" path
            if (isCouiPath) {
                path = path.replace(/coui:\/\/uiresources\//, '');
            }

            if (url.match(regExp)) {
                // remove root scene path and filename
                convertedPath = path.replace(scenePath, '').replace(/[^\/\\]*$/, '');
            } else {
                let sceneFolders = scenePath.split('/');
                let imageFolders = path.split('/');
                const imageFolderLen = imageFolders.length;

                for (let i = 0; i < imageFolderLen; i++) {
                    let upFolders = 0;

                    if (sceneFolders[i] !== imageFolders[i]) {
                        convertedPath = '../'.repeat(upFolders || sceneFolders.length - 1) + imageFolders.join('/');
                        break;
                    } else if (sceneFolders[i] === imageFolders[i]) {
                        imageFolders = imageFolders.slice(i + 1);
                        sceneFolders = sceneFolders.slice(i + 1);
                        upFolders++;
                    }
                }
            }

            return convertedPath + filename;
        }

        handleRelativePath(url: string): string {
            const cleanUrl = url.replace(/'|"/g, '');
            const scenePath = this.openFiles[this.selectedEditor].tab.filePath;
            const {path, filename} = helpers.getFileAndPath(cleanUrl);
            let convertedPath = helpers.pathHandler(path, scenePath, cleanUrl);

            return convertedPath + filename;
        }

        /**
         * Change the cursor of the scene based on panning state
         * @param isPanning {Boolean}
         * @private
         */
        panningCursor(isPanning) {
            var $elements = $('.panning');

            if (isPanning) {
                $elements.addClass('is-panning');
            } else {
                $elements.removeClass('is-panning');
            }
        }

        buildJSON(holder, fontFaces = '') {
            const temp = [];
            const sceneDOMObj = holder.childNodes;
            const newSceneObj = $.extend(true, {}, Enums.newScene);
            const scenePath = this.openFiles[this.selectedEditor].tab.filePath;

            this.renderStyles('imported-styles', holder);

            // add the main dependencies
            for (let prop in sceneDOMObj) {
                const sceneProp = sceneDOMObj[prop];

                if (sceneProp.localName !== undefined &&
                    sceneProp.localName !== null &&
                    sceneProp.nodeName !== '#text' &&
                    sceneProp.nodeName !== 'SCRIPT' &&
                    sceneProp.nodeName !== 'LINK') {
                    temp.push(sceneProp);
                }

                switch (sceneProp.className) {
                    case 'global-events':
                        newSceneObj.sceneEvents.sceneLoad = sceneProp.innerText;
                        break;
                    case 'scripts':
                        const clearedJSPath = helpers.cleanUrls(sceneProp.src);
                        newSceneObj.scripts.push(helpers.assetPathHandler(clearedJSPath, scenePath));
                        break;
                    case 'styles':
                        const clearedCSSPath = helpers.cleanUrls(sceneProp.href);
                        newSceneObj.styles.push(helpers.assetPathHandler(clearedCSSPath, scenePath));
                        break;
                }
            }

            if (fontFaces) {
                newSceneObj.fonts = (fontFaces.match(/'.*?'/gi) || []).map(font => font.replace(/'/g, ''));
            }

            if (temp.length) {
                this.buildChildNodes(temp, newSceneObj);
            }

            return newSceneObj;
        }

        renderStyles(styleTag, nodes) {
            var styleHolder = document.getElementById(styleTag);

            if (styleHolder === null) {
                return;
            }

            var rules = styleHolder.querySelector('style').sheet.cssRules;

            for (var idx = 0, len = rules.length; idx < len; idx++) {
                let selector: any = rules[idx];
                var collection = nodes.querySelector(selector.selectorText);

                collection.style.cssText += selector.style.cssText;
            }

            styleHolder.parentNode.removeChild(styleHolder);
        }

        buildChildNodes(obj, data) {
            var el;

            for (var child in obj) {
                el = this.childNode(obj[child]);
                data.widgets.push(el);
            }
        }

        loopChildNodes(node) {
            if (node.length === 1) {
                var nodeEl = this.childNode(node[0]);

                return nodeEl;
            } else {
                var multipleEl = [];

                for (var i = 0; i <= node.length - 1; i++) {
                    multipleEl.push(this.childNode(node[i]));
                }

                return multipleEl;
            }
        }

        async updateWidget(sceneObj) {
            let _tmp: any = '';
            let promiseArr = [];
            let indexes = [];
            let _this = this;

            for (var i in sceneObj.widgets) {
                if (sceneObj.widgets[i].children.length && !sceneObj.widgets[i].widgetkit) {
                    await this.updateWidget(sceneObj.widgets[i].children);
                } else if (sceneObj.widgets[i].widgetkit) {
                    var widgetUrl = '';

                    if (sceneObj.widgets[i].widgetkit !== ' ') {
                        widgetUrl = sceneObj.widgets[i].widgetkit;
                    } else {
                        widgetUrl = 'test';
                    }

                    _tmp = await this.fetchWidget(widgetUrl);
                    if (_tmp !== 'missing widget') {
                        promiseArr.push(_tmp);
                        indexes.push(i);
                    }
                }
            }

            if (promiseArr.length !== 0) {
                return Promise.all(promiseArr).then(function (data) {
                    for (var j = 0; j < data.length; j++) {
                        if (typeof data[j] === 'string') {
                            data[j] = JSON.parse(data[j]);
                        }
                        sceneObj.animationClasses = $.extend({}, sceneObj.animationClasses, data[j]['animationClasses']);
                        sceneObj.widgets[indexes[j]].children = data[j]['widgets'][0].children;
                        if (sceneObj.widgets[indexes[j]].widgetkit.endsWith('.component')) {
                            sceneObj.widgets[indexes[j]] =
                                _this.loopWidgetChildAndGenerateIds(sceneObj.widgets[indexes[j]]);
                        }
                    }
                    return sceneObj;
                });
            } else {
                return sceneObj;
            }
        }

        buildWidgetSceneObjFromHtml(content) {
            let scene = this.handleFileContent(content, 'load.component', this.selectedEditor, true, false);
            return scene;
        }

        async fetchWidget(url) {
            let _this = this;
            let _tmp;
            let date = new Date();
            let widgetUrl;
            let type = 'component';
            const sceneName = this.openFiles[this.selectedEditor].tab.filename;
            const widgetPath = this.openFiles[this.selectedEditor].tab.filePath
                .replace('widgets/', '')
                // for testing purposes
                .replace('editor/selenium/scenes/GT/', '')
                .replace('editor/selenium/scenes/General/', '');

            if (url.endsWith('.html')) {
                type = 'widget';
            }

            const originalWidgetUrl = `${widgetPath}widgets/${url}`;

            if (type === 'widget') {
                if (this.globalEditorInfo.backend === Enums.Backends.Standalone ||
                    this.globalEditorInfo.backend === Enums.Backends.Unreal) {
                    widgetUrl = `${originalWidgetUrl}?${date.getTime()}!text`;
                } else {
                    widgetUrl = `uiresources/${originalWidgetUrl}?${date.getTime()}!text`;
                }

                const exist = await this.linkCheck(originalWidgetUrl.replace(/\//g, '\\'));

                if (exist || sceneName === url) {
                    return System.import(widgetUrl).then(function (data) {
                        _tmp = _this.handleFileContent(data,
                            _this.openFiles[_this.selectedEditor].tab.filename,
                            _this.selectedEditor, true, false);

                        return _tmp;
                    });
                } else {
                    this.missingWidgets.push(originalWidgetUrl);
                    return 'missing widget';
                }
            } else if (type === 'component') {
                let editor = this.openFiles[this.selectedEditor];
                let component = editor.components.components[url];
                if (!component) {
                    component = editor.file;
                }

                let _tmp = _this.handleFileContent(component, _this.openFiles[_this.selectedEditor].tab.filename,
                    _this.selectedEditor, true, false);
                return Promise.resolve(_tmp);
            }
        }

        async linkCheck(url) {
            const widgets = await this.listDirectory('widgets', '(.html)', false);
            return widgets.some(widget => widget.url === url);
        }

        checkWidget(widgetObj) {
            for (var i in widgetObj) {
                if (widgetObj[i].children.length && !widgetObj[i].widgetkit) {
                    this.checkWidget(widgetObj[i].children);
                } else if (widgetObj[i].widgetkit) {
                    return true;
                }
            }

            return false;
        }

        /**
         * Determines if an element with a given id is present in the scene,
         * depending on the type of the file property of the editor
         *
         * @param editor {Object}
         * @param id {string}
         * @returns hasId {boolean}
         *
         * */
        hasId(editor, id: string): boolean {
            let hasId = false;

            if (this.isJson(editor.file)) {
                hasId = helpers.hasInChildren(JSON.parse(editor.file).widgets, 'id', id);
            } else {
                //TODO: this might be redundant since the editor.file
                //TODO: property is HTML only if the file has just been imported
                hasId = (editor.file.indexOf('id="' + id + '"') !== -1) ? true : false;
            }

            return hasId;
        }

        /**
         * Determines if an element's is should be changed
         *
         * @param editors {Object} - currently opened editors
         * @param id {string}
         * @param currentEditor {string}
         *
         * @returns {boolean} - whether the id should be changed or not
         *
         * */
        toChangeId(editors: Object, id: string, currentEditor: string): boolean {
            let isInOtherEditor = false;
            let isInCurrentEditor = false;

            for (let i in editors) {
                if (this.isJson(editors[i].file)) {
                    isInOtherEditor = this.hasId(editors[i], id);

                    if (!editors[i].createdNow) {
                        continue;
                    }
                    if (currentEditor === i) {
                        isInCurrentEditor = this.hasId(editors[i], id);

                    }
                }
            }

            let toChangeId = (isInOtherEditor && !isInCurrentEditor) ? false : true;

            return toChangeId;
        }

        getChangeIdConditions(id, appliedWidgetIds) {
            let toChangeId = false;
            if (id) {
                toChangeId = this.toChangeId(this.openFiles, id, this.selectedEditor);
            }

            let isInDOM = document.getElementById(id);

            let conditions = {
                allowAdding: (!isInDOM || (isInDOM && !toChangeId)),
                applied: appliedWidgetIds[this.selectedEditor][id],
                editing: this.openFiles[this.selectedEditor].tab.tabWidgetState.editWidget
            };

            return conditions;
        }

        // TODO: when adding new properties list them here //
        childNode(obj) {
            this.widgetCount++;

            var dataSet;
            var eventContent;
            var temp;
            var innerEl = [];

            var widget = $.extend(true, {}, this.environmentProperties.DefaultWidget);

            if (obj.children.length) {
                temp = this.loopChildNodes(obj.children);

                if (temp instanceof Array) {
                    innerEl = temp;
                } else {
                    innerEl.push(temp);
                }
            }

            if (widget.type !== ' ') {
                var nodeType = '';

                if (obj.nodeName === 'IMG') {
                    nodeType = 'image';
                } else {
                    nodeType = obj.nodeName.toLowerCase();
                }

                if (widget.widgetkit) {
                    widget.type = 'widget';
                } else {
                    if (obj.hasAttribute('data-type')) {
                        widget.type = obj.getAttribute('data-type');
                    } else {
                        widget.type = nodeType;
                    }
                }
            }

            appliedWidgetIds[this.selectedEditor] = appliedWidgetIds[this.selectedEditor] || {};

            let changeIdConditions = this.getChangeIdConditions(obj.id, appliedWidgetIds);


            if (obj.id && changeIdConditions.allowAdding && !changeIdConditions.applied || changeIdConditions.editing) {
                widget.id = obj.id;
                appliedWidgetIds[this.selectedEditor][obj.id] = obj.id;
                this.incrementWidgetTypesCounter(widget.type);
            } else {
                var newId;
                if (widget.type === 'widget') {
                    var regExp = /.html/;
                    var widgetName = obj.dataset.widgetkit.replace(regExp, '');
                    widgetName = this.cleanWidgetName(widgetName);
                    newId = this.generateRandomId(widgetName);
                } else {
                    newId = this.generateRandomId(widget.type);
                }
                widget.id = newId;
                animationWidgetsIdsToUpdate[newId] = obj.id;
                appliedWidgetIds[this.selectedEditor][newId] = newId;
            }

            widget = buildWidgetHandler.buildWidget(widget.type, widget, obj);

            dataSet = obj.dataset;

            for (var prop in dataSet) {
                if (prop === 'type' || prop === 'widgetkit') {
                    widget[prop] = dataSet[prop] || true;
                } else if (helpers.isDataBindingTypes(prop)) {
                    this.setWidgetDataBinds(widget.dataBindings, prop, dataSet[prop]);
                } else if (helpers.isEventName(prop)) {
                    eventContent = dataSet[prop].split('(');

                    if (eventContent[0] === 'engineTriggerArguments' ||
                        eventContent[0] === 'engineCallArguments' ||
                        eventContent[0] === 'blueprintFunction') {

                        var propArray = eventContent[1].split(')')[0].split(',');
                        this.setWidgetEvents(widget.events, prop, eventContent, propArray);
                    } else {
                        widget.events[prop] = {
                            'javascriptFunction': dataSet[prop]
                        };
                    }
                }
            }

            if (innerEl.length) {
                widget.children = innerEl;
            }

            return widget;
        }

        cleanWidgetName(widgetName) {
            let isComponent = widgetName.endsWith('.component');
            if (isComponent) {
                widgetName = widgetName.replace('.component', '');
            }
            let regExpChars = /[,._ ()+\-*&^!?=]/g;
            return widgetName.replace(regExpChars, '_');
        }

        setWidgetDataBinds(obj, propKey, propValue) {
            obj[propKey] = {};
            obj[propKey] = propValue;
        }

        setWidgetEvents(obj, prop, innerProp, propArray) {
            var eventFnName = innerProp[0];
            obj[prop] = {};
            obj[prop][eventFnName] = propArray;
        }

        isJson(str) {
            try {
                JSON.parse(str);
            } catch (e) {
                return false;
            }
            return true;
        }

        usesSceneEditor(content) {
            return this.isJson(content) || (content.indexOf(this.environmentProperties.COMMENT_MARK_START) > -1 &&
                content.indexOf(this.environmentProperties.COMMENT_MARK_END) > -1);
        }

        photoshopExport(content) {
            return this.isJson(content) ||
                (content.indexOf(this.environmentProperties.PHOTOSHOP_STYLE_COMMENT_END) > -1 &&
                    content.indexOf(this.environmentProperties.PHOTOSHOP_CONTENT_COMMENT_END) > -1);
        }

        addTempStyleHolder(style) {
            var styleTag = document.createElement('div');

            styleTag.id = 'imported-styles';
            styleTag.innerHTML = style;

            document.body.appendChild(styleTag);
        }

        setToolbarTitles() {
            document.getElementById('new-file').title = 'New file';
            document.getElementById('open-file').title = 'Open file';
            document.getElementById('save-scene').title = 'Save file';
            document.getElementById('undo-scene').title = 'Undo';
            document.getElementById('redo-scene').title = 'Redo';
            document.getElementById('select-tool').title = 'Select Tool';
            document.getElementById('pan-tool').title = 'Pan Scene';
            document.getElementById('resetZoom').title = 'Reset zoom';
            document.getElementById('help').title = 'Help';
        }

        extendObj(obj1, obj2) {
            var _extendObj = function (a, b) {
                var _tmp = {};
                var _tmpArr = [];

                for (var prop in b) {
                    var _aObj = a[prop];
                    var _bObj = b[prop];
                    if (prop === 'animations') {
                        _tmp[prop] = $.extend(true, _aObj, _bObj);
                    } else {
                        if (_aObj) {
                            if (typeof _aObj === 'object' && !(_bObj instanceof Array)) {
                                _tmp[prop] = _extendObj(_aObj, _bObj);
                            } else {
                                if (_bObj instanceof Array) {
                                    _tmpArr = [];
                                    _tmp[prop] = _aObj.concat(_bObj);
                                } else {
                                    _tmp[prop] = _aObj;
                                }
                            }
                        } else {
                            _tmp[prop] = _bObj;
                        }
                    }
                }

                return _tmp;
            };
            return _extendObj(obj1, obj2);
        }
    }
}
