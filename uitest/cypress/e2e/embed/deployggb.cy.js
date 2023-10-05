import '../../support/embed/commands.js'
/*global cy*/

describe('deployggb test', () => {
    beforeEach(() => {
        cy.visit('full.html');
    })

   it("Embedded apps should load", () => {
        cy.get("#appLoaded1").should("be.visible");
        cy.get("#appLoaded2").should("be.visible");
   })
})