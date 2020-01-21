import {selectors} from '@geogebra/web-test-harness/selectors'

describe('Properties View', () => {
    beforeEach(() => {
        cy.visit('graphing.html');
        cy.get("body.application");
    });

    it("should not display the image fill type", () => {
        cy.writeInAVInput("x^2{rightarrow}+y^2{rightarrow}=1{enter}");
        selectors.avMoreButton.get().eq(0).click();
        selectors.avContextMenuSettings.get().click();
        cy.get('.gwt-TabBarItem').contains('Style').click();
        selectors.fillType.get().should('not.contain', 'Image');
    });
});
