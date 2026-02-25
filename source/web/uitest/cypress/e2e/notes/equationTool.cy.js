import {selectors} from '@geogebra/web-test-harness/selectors'
import {parseString} from 'xml2js'
/*global cy,expect*/

describe('Equation tool test', () => {
    beforeEach(() => {
        cy.visit('notes.html');
        cy.get("body.application");
    });

    afterEach(cy.setSaved);

    it("Color button should change equation format", () => {
            // text tool selected as first on media panel
            cy.get('[data-title="Text"]').click();
            cy.get('li').contains('Equation').click();
            // use mouse down+up to avoid "scroll to view" behavior of cy.click
            selectors.euclidianView.get()
                .mouseEvent('down', 100, 200)
                .mouseEvent('up', 100, 200);
            cy.get("#hiddenCopyPasteLatexArea0").type("x^2", {"force": true});
            cy.wait(500);
            cy.get("[data-title=\"Color\"] img").click();
            cy.get(".colorButton:nth-child(3)").click();

            cy.window().then((win) => {
                parseString(win.ggbApplet.getXML("a"), (err, data) => {
                    expect(data.element.content[0]["$"].val).to.equal("x^(2)");
                    expect(data.element.objColor[0]["$"].g).to.equal("179");
                });
            })
    });
});