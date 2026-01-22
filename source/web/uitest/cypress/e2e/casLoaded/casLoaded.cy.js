/*global cy*/
describe('CASLoaded test', () => {
    beforeEach(() => {
        cy.visit('classic.html');
        cy.get("body.application");
    })

    afterEach(cy.setSaved);

    const solve = "sln=Solve[{{}x^3{rightarrow}+y=8{enter}";
    const length = "Length[Join[sln{enter}";

    it("CAS loaded after command", () => {
        cy.window().then((win) => {
            win.ggbApplet.asyncEvalCommand("CASLoaded[]");
            // cas loading ...
            cy.get(".avValue").should("contain", "false");
            // cas loaded
            cy.get(".avValue").should("contain", "true");
            cy.writeInAVInput(solve);
            cy.writeInAVInput(length);
            // verify that CAS works
            cy.get(".avValue").should("contain", "2");
        });
    })

    it("CAS loaded on base64 load", () => {
        cy.fixture("casLoadingTest").then(cy.setBase64);
        cy.expectGgbString("m1", "");
        cy.get(".avPlainText").should("contain", "CAS Loaded");
        cy.expectGgbString("m1", "m1 = {{x, -1 / 2 x³ + 75 / 2}}");
    })
})
