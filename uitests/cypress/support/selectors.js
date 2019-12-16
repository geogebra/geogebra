const selectorMap = {
    avInput: "avInputTextArea",
    avMoreButton: "avItemMoreButton",
    avContextMenuDelete: "menuDelete",
    euclidianView: "euclidianView",
    panViewTool: "panViewTool",
    mathField: "mathFieldTextArea",
    editor: "mathFieldEditor",
    keyboard: "tabbedKeyboard",
    toolsPanelButton: "toolsPanelButton",
    mediaPanelButton: "mediaPanelButton",
    dynamicStyleBar: "dynamicStyleBar",
    insertGraphingCalculatorButton: "selectModeButton117",
    maskToolButton: "selectModeButton122",
    rulingDropdown: "rulingDropdown",
    graphicsViewContextMenu: "graphicsViewContextMenu",
};

export const selectors = {};

for (let key in selectorMap) {
    const selector = {
        get: () => cy.get("[data-test=" + selectorMap[key] +"]"),
        click: () => selector.get().click({force: true}),
    };

    selectors[key] = selector;
}