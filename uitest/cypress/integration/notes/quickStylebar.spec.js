import {selectors} from '@geogebra/web-test-harness/selectors'

describe('Quick stylebar test', () => {
    beforeEach(() => {
        cy.visit('notes.html');
        cy.get("body.application");
    });

    afterEach(cy.setSaved);

    it("Quick stylebar should not be visible after pen draw", () => {
        selectors.dynamicStyleBar.get().should('not.exist');

        selectors.euclidianView.get()
            .mouseEvent('down', 200, 200)
            .mouseEvent('move', 250, 250)
            .mouseEvent('move', 250, 300)
            .mouseEvent('up');

        selectors.dynamicStyleBar.get().should('not.exist');
    });

    it("Quick stylebar should show after creating rectangle", () => {
        selectors.toolsPanelButton.click();

        selectors.dynamicStyleBar.get().should('not.exist');

        selectors.euclidianView.get()
            .mouseEvent('down', 200, 200)
            .mouseEvent('move', 250, 250)
            .mouseEvent('up');

        selectors.dynamicStyleBar.get().should('be.visible');
    });

    it("Quick stylebar should show after creating text", () => {
        selectors.mediaPanelButton.click();

        selectors.dynamicStyleBar.get().should('not.exist');

        selectors.euclidianView.get()
            .mouseEvent('down', 200, 200)
            .mouseEvent('up');

        selectors.dynamicStyleBar.get().should('be.visible');
    });

    it("Quick stylebar should show after inserting graphing calculator", () => {
        cy.window().then((win) => {win.mainApplet = win.ggbApplet;});
        selectors.mediaPanelButton.click();

        selectors.dynamicStyleBar.get().should('not.exist');
        selectors.insertGraphingCalculatorButton.click();
        selectors.dynamicStyleBar.get().should('be.visible');
    });
});
