import {selectors} from '@geogebra/web-test-harness/selectors'
/*global cy*/
describe('Context menu test', () => {
    beforeEach(() => {
        cy.visit('graphing.html');
        cy.get("body.application");
    });

    afterEach(cy.setSaved);

    it("Context menu should be shown", () => {
        // Enter x^2-2 in the input line
        cy.writeInAVInput("x^2{rightarrow}-2{enter}");
        console.log(selectors);
        // Context menu button
        selectors.algebraItemMore.at(1).eq(0).click();
        selectors.avContextMenuDelete.get().should('be.visible');
    });
});
