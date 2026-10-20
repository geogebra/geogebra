import {selectors} from '@geogebra/web-test-harness/selectors'

function terminalLog(violations) {
    console.log(violations);
    // pluck specific keys to keep the table readable
    const violationData = violations.map(
        ({ id, impact, description, nodes }) => ({
            id,
            impact,
            description,
            nodes: nodes.length
        })
    )

    cy.task('table', violationData)
}

describe('Accessibility test', () => {
    beforeEach(() => {
        cy.visit('calculator.html');
        cy.window().then(win => win.localStorage.setItem("keyboardwanted", "false"));
        cy.injectAxe();
        cy.get("body.application");
    });

    afterEach(cy.setSaved);

    function pressTab() {
        cy.press(Cypress.Keyboard.Keys.TAB)
    }

    it("Tabbing order by default", () => {
        pressTab();
        cy.get("[data-test='mainMenu']").should("be.focused");
        pressTab();
        cy.get("#logoID").should("be.focused");
        pressTab();
        cy.get("[data-test='appPickerButton']").should("be.focused");
        pressTab();
        cy.get("#shareButton").should("be.focused");
        pressTab();
        cy.get("#assignButton").should("be.focused");
        pressTab();
        cy.get("#signInTextID").should("be.focused");
        pressTab();
        selectors.graphicsViewContextMenu.get().should("be.focused");
        pressTab();
        cy.get("[data-test='zoomToFitButton']").should("be.focused");
        pressTab();
        cy.get("[data-test='zoomInButton']").should("be.focused");
        pressTab();
        cy.get("[data-test='zoomOutButton']").should("be.focused");
        pressTab();
        cy.get("[data-test='fullscreenButton']").should("be.focused");
        pressTab();
        cy.wait(1000);
        cy.get("#hiddenCopyPasteLatexArea0").should("be.focused");
        cy.checkA11y(null, {includedImpacts: ["critical"]}, terminalLog);
    });

    it("Linear notation", () => {
        cy.window().then(win => {
            win.ggbApplet.setXML('<geogebra format="5.0"><kernel><algebraStyle val="4"/></kernel></geogebra>');
            win.ggbApplet.evalCommand('a=1+2');
        })
        selectors.euclidianView.get()
            .mouseEvent('down', 100, 40)
        pressTab();
        cy.focused().type("{ctrl}{rightArrow}");
        cy.get(".AutoCompleteTextFieldW input").first().type("{leftArrow}3");
        cy.get(".AutoCompleteTextFieldW input").first().should('have.value', 'a = 1 + 32');
    });
});
