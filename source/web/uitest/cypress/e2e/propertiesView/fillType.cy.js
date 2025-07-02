import {selectors} from '@geogebra/web-test-harness/selectors'
/*global cy*/

describe('Properties View', () => {
    beforeEach(() => {
        cy.visit('graphing-offline.html');
        cy.get("body.application");
    });

    it("should not display the image fill type", () => {
        cy.writeInAVInput("x^2{rightarrow}+y^2{rightarrow}=1{enter}");
        selectors.algebraItemMore.at(1).eq(0).click();
        selectors.avContextMenuSettings.get().click();
        cy.get('.gwt-TabBarItem').contains('Style').click();
        cy.get('.propertiesTab .gwt-ListBox:visible').should('not.contain', 'Image');
        cy.get('.propertiesTab .gwt-ListBox:visible').should('contain', 'Standard');
    });
});
