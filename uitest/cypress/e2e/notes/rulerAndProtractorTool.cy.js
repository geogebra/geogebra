import {selectors} from '@geogebra/web-test-harness/selectors'
/*global cy,expect*/

describe('Ruler and protractor tool test', () => {
    beforeEach(() => {
        cy.visit('notes.html');
        cy.get("body.application");
    });

    afterEach(cy.setSaved);

    it("Ruler and protractor tool should change graphics view", () => {
        let before;
        cy.document().then((doc) => {
            before = doc.querySelector("[data-test=euclidianView]").toDataURL();
            selectors.rulerButton.click();
            cy.get("li").contains("Protractor").click();
        });

        cy.wait(3000);

        cy.document().then((doc) => {
            const after =  doc.querySelector("[data-test=euclidianView]").toDataURL();
            expect(before).not.to.equal(after);
        });
       });
});