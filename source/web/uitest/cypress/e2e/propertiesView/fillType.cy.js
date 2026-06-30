import {selectors} from '@geogebra/web-test-harness/selectors'
/*global cy*/

describe('Properties View', () => {
    beforeEach(() => {
        cy.visit('graphing-offline.html');
        cy.get("body.application");
    });

    // TODO add test for exam where this is not allowed
    it("should display the image fill type", () => {
        cy.writeInAVInput("x^2{rightarrow}+y^2{rightarrow}=1{enter}");
        selectors.algebraItemMore.at(1).eq(0).click();
        selectors.avContextMenuSettings.get().click();
        cy.get('.tabList .gwt-Label').contains('Style').click();
        cy.get('.expandableList .header').contains('Filling').click();
        cy.get('.connectedButtonGroup .gwt-Label').contains('Image').click();
        cy.get('.expandableList.extended').should('contain', 'Choose from File');
    });
});
