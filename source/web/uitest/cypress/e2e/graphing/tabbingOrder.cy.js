import {selectors} from '@geogebra/web-test-harness/selectors'

describe('Accessibility test', () => {
    beforeEach(() => {
        cy.visit('graphing.html');
        cy.window().then(win => win.localStorage.setItem("keyboardwanted", "false"));
        cy.injectAxe();
        cy.get("body.application");
    });

    afterEach(cy.setSaved);

    function pressTab() {
        cy.press(Cypress.Keyboard.Keys.TAB)
    }

    it("Graphing tabbing order by default", () => {
        pressTab();
        cy.get("[data-test='mainMenu']").should("be.focused");
        pressTab();
        cy.get("#logoID").should("be.focused");
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
    });
});
