/*global cy*/

describe.skip('Whats new dialog', () => {
    beforeEach(() => {
        cy.clearCookies();
        cy.visit("graphing.html");
        cy.get("body.application");
    })

   it("should display the whats new dialog", () => {
        cy.get(".whatsNewDialog").should("be.visible");
        cy.get(".whatsNewDialog .title").should("be.visible");
        cy.get(".whatsNewDialog .message").should("be.visible");
        cy.get(".whatsNewDialog .link").should("be.visible");
    })
})
