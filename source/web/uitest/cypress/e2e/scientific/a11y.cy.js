describe('Accessibility test', () => {
    beforeEach(() => {
        cy.visit('scientific.html');
        cy.window().then(win => win.localStorage.setItem("keyboardwanted", "false"));
        cy.injectAxe();
        cy.get("body.application");
    });

    it("Linear notation", () => {
        cy.window().then(win => {
            win.ggbApplet.setXML('<geogebra format="5.0"><kernel><algebraStyle val="4"/></kernel></geogebra>');
            win.ggbApplet.evalCommand('a=1+2');
        })
        for (let k = 0; k < 5; k++) {
            cy.press(Cypress.Keyboard.Keys.TAB);
        }
        cy.press('\n');
        cy.get(".AutoCompleteTextFieldW input").first().should("be.focused");
    });
});
