describe('Reset test', () => {
    beforeEach(() => {
        cy.viewport("ipad-2");
        cy.visit("resetTest.html");
        cy.get("#screenReader1");
    })

   it("Dialog on reset", () => {
        cy.get("#reset").click();
        cy.get("#reset-alert").should("be.visible")
    })
})
