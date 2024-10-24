import {selectors} from '@geogebra/web-test-harness/selectors'
/*global cy*/

describe('Properties View', () => {
    beforeEach(() => {
        cy.visit('notes.html');
        cy.get('[data-title="Settings"]').click();
        cy.get('.gwt-MenuItem').contains('Ruling').click();
    });

    it("should display the ruling dialog", () => {
        cy.get("button").contains("Save").should('be.visible')
    });
    it("the ruling dialog should close on save", () => {
        cy.get("div").contains("Lined").click();
        cy.get("button").contains("Save").click();
        cy.get('.gwt-PopupPanel').should('not.exist')
    });
});
