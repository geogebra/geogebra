import {selectors} from '@geogebra/web-test-harness/selectors'

describe('Properties View', () => {
    beforeEach(() => {
        cy.visit('notes.html');
        selectors.graphicsViewContextMenu.get().click();
        cy.get('.gwt-MenuItem').contains('Settings').click();
        cy.get('.gwt-TabBarItem').contains('Grid').click();
    });

    it("should display the ruling dropdown button", () => {
        selectors.rulingDropdown.get().should('be.visible')
    });
    it("the ruling dropdown button should open the dropdown", () => {
        selectors.rulingDropdown.get().click();
        cy.get('.gwt-PopupPanel .grid').should('be.visible')
    });
});
