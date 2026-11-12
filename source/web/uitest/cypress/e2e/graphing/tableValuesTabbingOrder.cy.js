import '../../support/embed/commands.js'
import {selectors} from '@geogebra/web-test-harness/selectors'

describe('Accessibility test', () => {
    beforeEach(() => {
        cy.window().then(win => win.localStorage.removeItem("keyboardwanted"));
        cy.visit('graphing.html');
        cy.get("body.application");
    });

    afterEach(cy.setSaved);

    it("First cell focused on table of values open", () => {
    	cy.writeInAVInput("x^2{enter}");
        selectors.algebraItemMore.at(1).eq(0).click();
        cy.wait(500);
        cy.get("[data-test='menuCreateTableValues']").click();
        cy.get("button").contains("OK").click();
		cy.get("#hiddenCopyPasteLatexArea1").should("be.focused");
		cy.press(Cypress.Keyboard.Keys.ENTER);
		cy.get("button").contains("Algebra").click();
		cy.get("button").contains("Table").click();
		cy.get("#hiddenCopyPasteLatexArea1").should("be.focused");
    });
});