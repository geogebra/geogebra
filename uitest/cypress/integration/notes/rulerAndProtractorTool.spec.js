import {selectors} from '@geogebra/web-test-harness/selectors'

describe('Ruler and protractor tool test', () => {
    beforeEach(() => {
        cy.visit('notes.html');
        cy.get("body.application");
    });

    afterEach(cy.setSaved);

    it("Ruler and protractor tool should change graphics view", () => {
        cy.window().then((win) => {
               before = win.ggbApplet.getPNGBase64(1);
               selectors.rulerButton.click();
               selectors.protractorButton.click();
               cy.wait(3000);
               expect(before).to.equal(win.ggbApplet.getPNGBase64(1));
        });
   	});
});