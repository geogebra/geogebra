/* global ace */
/* global w2ui */
'use strict';

declare let $;

// Importing Editor //
import {coui} from '../scripts/editor';

// Importing Settings and Properties //
import helpers from 'lib/function_helpers';
import Enums from 'lib/enums';
import EditorSettings from 'lib/editor_settings';
import EditorProperties from 'lib/editor_properties';
import PrototypeMethods from  '../scripts/globals/prototype-methods';

PrototypeMethods();

// Importing External Handling //
import keyhandlers from 'lib/key_handlers';

let couiEditor = new coui.Editor();
export default couiEditor;

engine.on('LoadFile', function (filename, fileContents) {
    console.log('Loading: ' + filename);

    if (filename !== 'EmptyUI.html') {
        window['CURRENT_FILE'] = filename;
        document.body.dispatchEvent(new Event('turnOnLoadingScreen'));
    }

    setTimeout(function () {
        if (couiEditor.isItPublishPage(fileContents)) {
            const cont = couiEditor.getOriginSceneUrl(fileContents);
            const path = helpers.getFileAndPath(filename).path;
            engine.call('SetCurrentSceneURL', path);

            couiEditor.PENDING_PUBLISH_PAGE_LOAD = true;
            couiEditor.openAsset({path: cont});
            return;
        } else {
            couiEditor.PENDING_SCENE_LOAD = false;
            couiEditor.PENDING_PUBLISH_PAGE_LOAD = false;
        }

        try {
            if (couiEditor.sceneExist(filename)) {
                couiEditor.focusFile(filename);
                console.log('File found. Focusing tab.');
                return;
            }
        } catch (e) {
            window['err'] = e;
            console.error('Could not open file: ' + e);
        }
        try {
            var EMPTY_SCENE_NAME = 'EmptyUI.html';
            // The empty scene is used as a placeholder in UE4
            // create a new scene instead

            if (filename !== EMPTY_SCENE_NAME) {
                if (couiEditor.PENDING_PUBLISH_PAGE_LOAD && fileContents === '') {
                    $('#coui-editor').trigger('vexFlashMessage', [Enums.Messages.cannotFindPublishPage]);
                    couiEditor.PENDING_PUBLISH_PAGE_LOAD = false;
                    return;
                }

                couiEditor.openFile(fileContents, filename);
                console.log('File loaded.');

                if (couiEditor.PENDING_WIDGET_LOAD) {
                    couiEditor.openFiles[couiEditor.selectedEditor].tab.tabWidgetState.editWidget = true;
                    couiEditor.PENDING_WIDGET_LOAD = false;
                }

                if (filename.endsWith('.component')) {
                    document.body.dispatchEvent(new Event('__openComponentComplete'));
                }
            } else {
                document.body.dispatchEvent(new Event('couiEditorIsReadyForUse'));
            }
        } catch (e) {
            console.error('Could not open file: ' + e);
        }
    }, 100);
});

engine.on('AboutToClose', function () {
    couiEditor.closeAllTabs();
});

engine.on('FileSaved', couiEditor.onSaveCompleted.bind(couiEditor));

engine.on('SelectedFiles', function (array) {
    // Deny save/ publish the scene
    if (array.length === 0) {
        return;
    }

    const fileWithPath = array[0].split(/uiresources[\/\\]/i)[1];
    const filename = fileWithPath.replace(/.*[\\\/]/, '');
    const regexDirectory = /.+\\/;

    let directory = '';
    if (new RegExp(regexDirectory).test(fileWithPath)) {
        directory = fileWithPath.match(regexDirectory)[0];
    }
    let unifiedDirectoryPath = directory.replace(/\\/g, '/') || '';
    let editor = couiEditor.openFiles[couiEditor.selectedEditor];

    // publish mode option
    if (couiEditor.onSelectedFileType.publishPage) {
        const originSceneFilePath = `${editor.tab.filePath}${editor.tab.filename}`;
        const unifiedFilePath = editor.tab.filePath.replace(/\\/g, '/');

        // prevent save in different directory
        if (unifiedFilePath !== unifiedDirectoryPath) {
            $('#coui-editor').trigger('vexFlashMessage', [Enums.Messages.cannotSavePublishPage]);
            return;
        }
        // prevent replacement of the original scene
        const cleanStrings = helpers.removeSlashes([originSceneFilePath, fileWithPath]);
        if (cleanStrings[0] === cleanStrings[1]) {
            $('#coui-editor').trigger('vexFlashMessage', [Enums.Messages.duplicationFileName]);
            return;
        }

        var runtimeEditor = editor.runtimeEditor;
        var scene = couiEditor.adjustSavedContent(JSON.stringify(runtimeEditor.scene), {
            boxShadowAndClasses: true,
            textarea: true
        });

        runtimeEditor.runtimeLoad(scene, {
            publishScene: true,
            originalSceneName: editor.tab.filename
        }).then((data) => {
            var mergedHTML = couiEditor.rebuildUserHTML(data);
            var newData = couiEditor.cleanupHtmlExport(mergedHTML);

            couiEditor.saveFile(fileWithPath, newData, couiEditor.selectedEditor);
            couiEditor.onSelectedFileType.publishPage = false;
            editor.tab.pendingSave = false;
        });
    } else {
        let settings;

        if (couiEditor.sceneSettings) {
            settings = couiEditor.sceneSettings;
        } else {
            console.warn(Enums.warnMessages.sceneSettings);

            // default values to prevent editor crashing if "settings" cannot be set
            settings = {
                backgroundColor: 'rgba(255, 255, 255, 0)',
                width: '1920',
                height: '1080',
                type: 'aspectRatio16_9_full_hd'
            };
        }

        const {backgroundColor, width, height, type} = settings;
        const sceneSize = helpers.getSceneSizeByType(type, width, height);

        couiEditor.createNewScene({
            'style': {
                'backgroundColor': backgroundColor
            },
            'sceneSize': sceneSize
        }, fileWithPath);
    }

    // Clear current scene settings after creation
    couiEditor.sceneSettings = {};
});

engine.on('Ready', function () {
    couiEditor.init();

    let preferences = engine.call('prefs.get', 'preferences');
    if (preferences.result !== null) {
        couiEditor.preferences = preferences.result;
        if (couiEditor.preferences.timeline) {
            couiEditor.preferences.timeline.filterTimelineWidgets =
                (String(couiEditor.preferences.timeline.filterTimelineWidgets) === 'true');
        } else {
            couiEditor.preferences.timeline = EditorSettings.defaultPreferences.timeline;
        }
    } else {
        couiEditor.preferences.couiEnvironment = 'GT';
        var selectedEnvironment = EditorSettings.defaultPreferences;
        engine.call('prefs.set', 'preferences', selectedEnvironment);
        engine.call('prefs.save');
    }

    engine.call('RequestBackendInformation').then(function (info) {
        // init VEX dialog style template
        vex.defaultOptions.className = 'vex-theme-flat-attack';
        localStorage.clear();
        couiEditor.globalEditorInfo = info;

        Promise.all([

            System.import('lib/declarations'),
            System.import('lib/handlebars_helpers'),

        ]).then(function (result) {

            couiEditor.EDITOR_VERSION = result[0].default.EDITOR_VERSION;
            couiEditor.Handlebars = result[1].default;

            couiEditor.attachEditorHandlers();

            keyhandlers.attachKeyHandlersGlobal();

            if (info.backend === Enums.Backends.Debug ||
                info.backend === Enums.Backends.Website) {
                couiEditor.attachBrowserHandlers();
            }

            let envProperties = EditorProperties[EditorSettings.environment[couiEditor.preferences.couiEnvironment]];

            couiEditor.environmentProperties = envProperties;

            // PIPEING ALL EXTENSIONS TO THE EDITOR ASSETS //
            let supportedExtensions = envProperties.DefaultExtensions;

            supportedExtensions = $.map(supportedExtensions, function (value) {
                return [value].map(function (result) {
                    return result.join('|.');
                });
            }).join('|.');

            supportedExtensions = '(.' + supportedExtensions + ')';

            couiEditor.listDirectory('', supportedExtensions, true).then(function (data) {
                couiEditor.updateSceneAssets(data);
            });

            if (info.backend === Enums.Backends.Unreal) {
                $('.btn-pref-file').parent().remove();
            }
            couiEditor.initSceneConfigVex();
            engine.trigger('FrontendReady');
        });
    });
});

engine.mock('RequestBackendInformation', function () {
    if (document.location.hostname === 'localhost') {
        return {
            backend: Enums.Backends.Debug
        };
    } else {
        return {
            backend: Enums.Backends.Website
        };
    }
});

engine.mock('LaunchURL', function (url) {
    return window.open(url, '_blank');
});

engine.mock('Save', function (filename, fileContents) {
    engine.trigger('FileSaved', filename, filename);
});

engine.mock('ListDirectory', function (directory, regex, recursive) {
    if (directory !== 'widgets') {
        return [
            {'__Type': 'FileEntry', 'isFile': true, 'url': 'videos/big-buck-bunny.webm'},
            {'__Type': 'FileEntry', 'isFile': true, 'url': 'images/mobaSliced2_04.png'},
            {'__Type': 'FileEntry', 'isFile': true, 'url': 'mobaSliced2_03.png'},
            {'__Type': 'FileEntry', 'isFile': true, 'url': 'images/mobaSliced2_07.png'},
            {'__Type': 'FileEntry', 'isFile': true, 'url': 'testStyle.css'},
            {'__Type': 'FileEntry', 'isFile': true, 'url': 'css/testStyle.css'},
            {'__Type': 'FileEntry', 'isFile': true, 'url': 'css/style.css'},
            {'__Type': 'FileEntry', 'isFile': true, 'url': 'hello.js'},
            {'__Type': 'FileEntry', 'isFile': false, 'url': 'videos'},
            {'__Type': 'FileEntry', 'isFile': false, 'url': 'images'},
            {'__Type': 'FileEntry', 'isFile': true, 'url': 'images/ammo.png'},
            {'__Type': 'FileEntry', 'isFile': true, 'url': 'images/blueBar.png'},
            {'__Type': 'FileEntry', 'isFile': true, 'url': 'images/bulletsG.png'},
            {'__Type': 'FileEntry', 'isFile': true, 'url': 'images/bulletsW.png'},
            {'__Type': 'FileEntry', 'isFile': true, 'url': 'images/corner.png'},
            {'__Type': 'FileEntry', 'isFile': true, 'url': 'images/crosshair.png'},
            {'__Type': 'FileEntry', 'isFile': true, 'url': 'images/gun.png'},
            {'__Type': 'FileEntry', 'isFile': true, 'url': 'images/hb.png'},
            {'__Type': 'FileEntry', 'isFile': true, 'url': 'images/hbar.png'},
            {'__Type': 'FileEntry', 'isFile': true, 'url': 'images/health.png'},
            {'__Type': 'FileEntry', 'isFile': true, 'url': 'images/hTitles.png'},
            {'__Type': 'FileEntry', 'isFile': true, 'url': 'images/leftHover.png'},
            {'__Type': 'FileEntry', 'isFile': true, 'url': 'images/mask.png'},
            {'__Type': 'FileEntry', 'isFile': true, 'url': 'images/minimap.png'},
            {'__Type': 'FileEntry', 'isFile': true, 'url': 'images/minimapB.png'},
            {'__Type': 'FileEntry', 'isFile': true, 'url': 'images/minimapR.png'},
            {'__Type': 'FileEntry', 'isFile': true, 'url': 'images/redCorner.png'},
            {'__Type': 'FileEntry', 'isFile': true, 'url': 'images/rightHover.png'},
            {'__Type': 'FileEntry', 'isFile': true, 'url': 'images/target.png'},
            {'__Type': 'FileEntry', 'isFile': true, 'url': 'images/titles.png'},
            {'__Type': 'FileEntry', 'isFile': true, 'url': 'images/tittle1.png'},
            {'__Type': 'FileEntry', 'isFile': true, 'url': 'images/whiteHover.png'},
            {'__Type': 'FileEntry', 'isFile': true, 'url': 'fonts/Exo2-SemiBoldItalic.ttf'},
            {'__Type': 'FileEntry', 'isFile': true, 'url': 'fonts/ArimaMadurai-Black.ttf'}
        ];
    } else {
        return [
            {'__Type': 'FileEntry', 'isFile': true, 'url': 'widgets/minimap.html'},
            {'__Type': 'FileEntry', 'isFile': false, 'url': 'widgets'}
        ];
    }
});

engine.trigger('Ready');


