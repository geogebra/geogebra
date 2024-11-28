import {selectors} from '@geogebra/web-test-harness/selectors'
/*global cy*/

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
        cy.get('[data-title="Shape"]').click();

        selectors.dynamicStyleBar.get().should('not.exist');

        selectors.euclidianView.get()
            .mouseEvent('down', 200, 200)
            .mouseEvent('move', 250, 250)
            .mouseEvent('up');

        selectors.dynamicStyleBar.get().should('be.visible');
    });

    it("Quick stylebar should show after creating text", () => {
        cy.get('[data-title="Text"]').click();

        selectors.dynamicStyleBar.get().should('not.exist');

        selectors.euclidianView.get()
            .mouseEvent('down', 200, 200)
            .mouseEvent('up');

        selectors.dynamicStyleBar.get().should('be.visible');
    });

    it("Quick stylebar should show after inserting graphing calculator", () => {
        cy.viewport(900, 900);
        cy.window().then((win) => {win.mainApplet = win.ggbApplet;});
        cy.get('[data-title="More"]').click();
        selectors.dynamicStyleBar.get().should('not.exist');
        cy.get('li').contains("GeoGebra").click();
        cy.get('.contextSubMenu li').contains("Graphing").click();
        // cypress view too small to se the whole stylebar here
        cy.get(".matDynStyleContextButton").should('be.visible');
    });
});
