/// <reference path="../../../typings/coui-editor/editor.d.ts"/>
/**
 *  @module lib/components/components
 *  @export lib/components/components.Components
 *  @require module:lib/enums
 */
'use strict';
declare let $;
import Enums from 'lib/enums';
import unitsConvertor from '../../scripts/helpers/units_conversion';
import couiEditor from '../../scripts/main';

export default class Component implements IWidgetComponents {
    public components: IComponent;
    public state: IComponentState;

    constructor() {
        this.components = {};
        this.state = {
            opened: []
        };
    }

    create(name, component) {
        this.components[name] = component;
        let runtimeEditor = couiEditor.openFiles[couiEditor.selectedEditor].runtimeEditor;
        runtimeEditor._sceneActionState.createComponent = true;
        runtimeEditor.saveUndoRedo(null, null, null, {componentName: name}, null);
        runtimeEditor.exportScene();
        return this;
    }

    isComponentOpened(name) {
        for (let i = 0; i < this.state.opened.length; i++) {
            let openedId = this.state.opened[i];
            if (couiEditor.openFiles[openedId].tab.filename === name) {
                return this.state.opened[i];
            }
        }
        return false;
    }

    remove(name) {
        let isOpened = this.isComponentOpened(name);
        if (isOpened) {
            couiEditor.forceClose(isOpened);
        }
        let runtimeEditor = couiEditor.openFiles[couiEditor.selectedEditor].runtimeEditor;
        runtimeEditor._sceneActionState.deleteComponent = true;
        let undoRedoState = runtimeEditor.getRedoUndoPrimaryState();
        let undoRedoExportData = {
            name: name,
            json: this.components[name]
        };
        let $elComponents = $(`[data-widgetkit="${name}"]`);
        let elementLen = $elComponents.length;
        if (undoRedoState === 'new action') {
            runtimeEditor._undoCreationStepsLength = elementLen + 1;
        }
        runtimeEditor.saveUndoRedo(null, null, null, undoRedoExportData, null);
        delete this.components[name];
        let componentsIds: string[] = [];
        for (let i = 0; i < elementLen; i++) {
            componentsIds.push($elComponents[i].getAttribute('id'));
        }
        runtimeEditor.removeWidgets(componentsIds);
        return this;
    }

    registerComponents() {
        let currentEnvironmentProp = couiEditor.environmentProperties;
        let finalString = '\n' + currentEnvironmentProp.REGISTER_COMPONENTS_MARK_START;
        finalString += '\n<script>';
        let i = 0;
        for (let component in this.components) {
            finalString += '\nvar component' + i + ' = getComponentData(\'' + component + '\');\n';
            finalString += 'engine.registerComponent(\'' + component + '\', {\n' +
                '   template: component' + i + '.template,\n' +
                '   create: function () {\n' +
                '      return this.appendChild(component' + i + '.script);\n' +
                '   }\n' +
                '})\n';
            i++;
        }

        finalString += 'function getComponentData(componentName) {\n' +
            '   var el = document.querySelectorAll(\'[data-load-component-id="\' + componentName + \'"]\');\n' +
            '   var html = document.createElement(\'html\');' +
            '   html.innerHTML = el[0].innerHTML;\n' +
            '   var localEvents = html.getElementsByClassName(\'local-events\')[0];\n' +
            '   var script = document.createElement(\'script\');\n' +
            '   script.innerHTML = localEvents.innerHTML;\n' +
            '   var component = html.querySelectorAll(\'[data-widgetkit="\' + componentName + \'"]\')[0];\n' +
            '   return {\n' +
            '       template: component,\n' +
            '       script: script\n' +
            '   };\n' +
            ' }\n';
        finalString += '\n</script>\n';
        finalString += currentEnvironmentProp.REGISTER_COMPONENTS_MARK_END + '\n';
        return finalString;
    }

    replaceScriptTags(content, reverse = false) {
        let replacedTags = '';
        if (!reverse) {
            replacedTags = content.replace(/<script/g, '<x-script')
                .replace(/<\/script/g, '</x-script');
        } else {
            replacedTags = content.replace(/<x-script/g, '<script')
                .replace(/<\/x-script/g, '</script');
        }

        return replacedTags;
    }

    async buildComponentExportHTML() {
        const dfd = $.Deferred();
        const currentEnvironmentProp = couiEditor.environmentProperties;
        const editor = couiEditor.openFiles[couiEditor.selectedEditor];
        let componentsHTML = '';

        for (let componentData in this.components) {
            componentsHTML += '\n<script type="text/html" data-load-component-id="' + componentData + '">\n' +
                currentEnvironmentProp.COMPONENTS_MARK_START + '\n';
            componentsHTML += await editor.runtimeEditor.runtimeLoad(this.components[componentData], {
                widgetSave: true
            }).then((data) => {
                let mergedHTML = couiEditor.rebuildUserHTML(data, '', true);
                let newData = couiEditor.cleanupHtmlExport(mergedHTML);
                newData = this.replaceScriptTags(newData);

                return newData + '\n' +
                    currentEnvironmentProp.COMPONENTS_MARK_END + '\n' +
                    '\n</script>\n';
            }).catch((err) => {
                console.log(err.message);
            });
        }
        return dfd.resolve(componentsHTML);
    }

    cleanupCommentMarks(content) {
        let currentEnvironmentProp = couiEditor.environmentProperties;
        return content
            .replace(currentEnvironmentProp.COMPONENTS_MARK_START, '')
            .replace(currentEnvironmentProp.COMPONENTS_MARK_END, '');
    }

    load(content) {
        var regexComponents = /\/\* Editor Components Start \*\/([\s\S]*?)\/\* Editor Components End \*\//gm;

        if (content.match(regexComponents)) {
            let componentsHTML: string[] = content.match(regexComponents);
            if (componentsHTML.length > 0) {
                let components = {};
                for (let i = 0; i < componentsHTML.length; i++) {
                    componentsHTML[i] = this.replaceScriptTags(componentsHTML[i], true);
                    componentsHTML[i] = this.cleanupCommentMarks(componentsHTML[i]);
                    let componentScene = couiEditor.buildWidgetSceneObjFromHtml(componentsHTML[i]);
                    let componentName = componentScene.widgets[0].widgetkit;
                    components[componentName] = JSON.stringify(componentScene);
                }
                this.components = components;
            }
        }
    }

    open(content, name) {
        let sceneEditorId = couiEditor.selectedEditor;

        couiEditor.openAsset({
            name,
            content
        });

        $('body').off('__openComponentComplete');
        $('body').on('__openComponentComplete', () => {
            let componentEditorId = couiEditor.selectedEditor;
            var index = this.state.opened.indexOf(componentEditorId);

            if (index === -1) {
                couiEditor.openedTabsTypes.components.push(componentEditorId);
                this.state.opened.push(componentEditorId);
            }
            couiEditor.openFiles[couiEditor.selectedEditor].tab.tabWidgetState.instanceOf = sceneEditorId;
            document.body.dispatchEvent(new Event('__componentInstanceSet'));
        });

        return this;
    }

    clearOpenedData(editorId) {
        var index = this.state.opened.indexOf(editorId);
        if (index > -1) {
            couiEditor.openedTabsTypes.components.splice(index, 1);
            this.state.opened.splice(index, 1);
        }
        return this;
    }

    save(name, content) {
        this.components[name] = content;
        if (couiEditor.EXPORTING_COMPONENT) {
            couiEditor.EXPORTING_COMPONENT = false;
        }
        couiEditor.tabEdited(false);
    }

    _insertToTheScene(draggedElement, event) {
        let importDataUrl = draggedElement.getAttribute('data-url');
        let name = draggedElement.getAttribute('data-widget-name');

        if (importDataUrl !== '') {
            this._fetchWidgetFromFileAndInsertToTheScene(importDataUrl, event);
        } else {
            this._insertWidgetComponentToTheScene(name, event);
        }
    }

    _fetchWidgetFromFileAndInsertToTheScene(importDataUrl, event) {
        let runtimeEditor = couiEditor.openFiles[couiEditor.selectedEditor].runtimeEditor;
        if (couiEditor.globalEditorInfo.backend === Enums.Backends.Debug ||
            couiEditor.globalEditorInfo.backend === Enums.Backends.Website) {
            importDataUrl = importDataUrl + '!text';
        } else {
            importDataUrl = runtimeEditor.buildWidgetUrl(importDataUrl);
        }

        System.import(importDataUrl).then((data) => {
            let offsets = this._getDroppedOffset(event);
            let left = `left: ${offsets.x}vw; `;
            let top = ` top: ${offsets.y}vh; `;
            let toTheBodyRegex = /<([\s\S]*)<\/head>/;
            let bodyRegex = /<body>([\s\S]*)<\/body>/;
            let innerHTML = bodyRegex.exec(data.valueOf())[0];
            let headHtml = toTheBodyRegex.exec(data.valueOf())[0];
            let regexTop = /(top:).*?;/;
            let regexLeft = /(left:).*?;/;

            innerHTML = innerHTML.replace(innerHTML.match(regexTop)[0], top);
            innerHTML = innerHTML.replace(innerHTML.match(regexLeft)[0], left);

            let fullHtml = `${headHtml}${innerHTML}</html>`;

            let currentEditor = couiEditor.selectedEditor;
            let filename = couiEditor.openFiles[couiEditor.selectedEditor].tab.filename;
            couiEditor.openFiles[couiEditor.selectedEditor].tab.tabWidgetState.importedWidget = true;
            couiEditor.handleFileContent(fullHtml, filename, currentEditor, false, true);
        });
    }

    _getDroppedOffset(event: Event) {
        let runtimeEditor = couiEditor.openFiles[couiEditor.selectedEditor].runtimeEditor;
        let offsets = runtimeEditor._getSceneOffset(event);

        let x = unitsConvertor.convertPixelToVw(offsets.x);
        let y = unitsConvertor.convertPixelToVh(offsets.y);

        return {
            x: x,
            y: y
        };
    }

    _insertWidgetComponentToTheScene(name, event) {
        let editor = couiEditor.openFiles[couiEditor.selectedEditor];
        let offsets = this._getDroppedOffset(event);
        let componentData = JSON.parse(editor.components.components[name]);
        componentData.widgets[0].geometry.top = offsets.y + 'vh';
        componentData.widgets[0].geometry.left = offsets.x + 'vw';
        let currentEditor = couiEditor.selectedEditor;
        let filename = editor.tab.filename;
        editor.tab.tabWidgetState.importedWidget = true;
        couiEditor.handleFileContent(JSON.stringify(componentData), filename, currentEditor, false, true);
    }
}


