/*global cy*/
describe('Sliders test', () => {
    beforeEach(() => {
        cy.visit('graphing.html');
        cy.get("body.application");
    });

    afterEach(cy.setSaved);

    it("sliders should be editable", () => {
        cy.writeInAVInput("5{enter}");
        cy.wait(200);
        cy.get(".avPlainText").click();
        cy.focused().type("00{enter}", {"force": true});
        cy.get(".avPlainText").contains("a = 500").should('exist');
    });
});
